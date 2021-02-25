package ua.khnu.util.csv;

import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;
import ua.khnu.exception.CsvException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class Csv {
    private final Map<Class<?>, Function<String, ?>> classCast;
    private final String valueSeparator;
    private final String rowSeparator;

    public Csv(String valueSeparator, String rowSeparator) {
        this.valueSeparator = valueSeparator;
        this.rowSeparator = rowSeparator;

        classCast = ImmutableMap.<Class<?>, Function<String, ?>>builder()
                .put(int.class, Integer::parseInt)
                .put(Integer.class, Integer::parseInt)
                .put(double.class, Double::parseDouble)
                .put(Double.class, Double::parseDouble)
                .put(byte.class, Byte::parseByte)
                .put(Byte.class, Byte::parseByte)
                .put(char.class, s -> s.charAt(0))
                .put(Character.class, s -> s.charAt(0))
                .put(Short.class, Short::parseShort)
                .put(short.class, Short::parseShort)
                .put(Long.class, Long::parseLong)
                .put(long.class, Long::parseLong)
                .put(Float.class, Float::parseFloat)
                .put(float.class, Float::parseFloat)
                .put(Boolean.class, Boolean::parseBoolean)
                .put(boolean.class, Boolean::parseBoolean)
                .put(String.class, s -> s)
                .build();
    }

    public Csv() {
        this(",", "\n");
    }

    public <T> List<T> read(String csv, Class<T> targetClass) {
        if (csv.isEmpty()) {
            return new ArrayList<>();
        }
        List<T> result = new ArrayList<>();
        String[] headers = getHeaders(csv);
        List<String[]> body = getBody(csv);
        validateLengths(headers, body);
        body.forEach(row -> {
            try {
                T element = targetClass.getConstructor().newInstance();
                for (int i = 0; i < headers.length; i++) {
                    var methods = targetClass.getDeclaredMethods();
                    int finalI = i;
                    var method = Arrays.stream(methods)
                            .filter(m -> m.getName().equalsIgnoreCase("set" + headers[finalI]))
                            .findAny()
                            .orElseThrow(() -> new CsvException("Class error"));
                    if (method.getParameters().length != 1) {
                        throw new CsvException("There isn't suitable setter for field " + headers[finalI]);
                    }
                    method.invoke(element, cast(row[i], method.getParameters()[0].getType()));
                }
                result.add(element);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new CsvException("Target class not supported");
            }
        });
        return result;
    }

    public <T> String createCsvFromObject(List<T> targets, Class<T> targetClass) {
        final var getters = Arrays.stream(targetClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(CsvGetter.class))
                .sorted(Comparator.comparing(m -> m.getAnnotation(CsvGetter.class).order()))
                .collect(Collectors.toList());
        final var headers = buildHeaders(getters);
        final var body = targets.stream()
                .map(target -> toCsvLine(target, getters))
                .collect(Collectors.joining(rowSeparator));
        return headers + rowSeparator + body;
    }

    private <T> String toCsvLine(T target, List<Method> getters) {
        return getters.stream()
                .map(getter -> {
                    try {
                        return getter.invoke(target);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new CsvException("Not properly getter configured");
                    }
                })
                .map(o -> {
                    var value = o == null ? "" : o.toString();
                    if (value.contains("\n")) {
                        value = "\"" + value + "\"";
                    }
                    return value;
                }).collect(Collectors.joining(valueSeparator));
    }

    private String buildHeaders(List<Method> getters) {
        return getters.stream()
                .map(Method::getName)
                .map(name -> name.substring(3))
                .collect(Collectors.joining(valueSeparator));
    }

    private <T> T cast(String s, Class<T> originalClass) {
        if (s.isEmpty()) {
            return null;
        }
        try {
            var isEnumType = getAllSuperClasses(originalClass).stream()
                    .anyMatch(c -> c.equals(Enum.class));
            if (isEnumType) {
                return (T) originalClass.getMethod("valueOf", String.class).invoke(null, s);
            }
            var function = Optional.ofNullable(classCast.get(originalClass))
                    .orElseThrow(() -> new CsvException("Class error"));
            return (T) function.apply(s);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new CsvException("Invalid class");
        }
    }


    private List<Class<?>> getAllSuperClasses(Class<?> originalClass) {
        List<Class<?>> superClasses = new ArrayList<>();
        Class<?> c = originalClass.getSuperclass();
        while (c != null) {
            originalClass = c;
            superClasses.add(c);
            c = originalClass.getSuperclass();
        }
        return superClasses;
    }

    private String[] getHeaders(String csv) {
        String[] rows = csv.split(rowSeparator);
        if (rows[0].isEmpty()) {
            throw new CsvException("Empty rows line");
        }
        return rows[0].split(valueSeparator);
    }

    private List<String[]> getBody(String csv) {
        return Arrays.stream(csv.split(rowSeparator))
                //skip header row
                .skip(1)
                .map(row -> row.split(valueSeparator))
                .collect(Collectors.toList());
    }

    private void validateLengths(String[] headers, List<String[]> body) {
        var isValid = body.stream()
                .allMatch(row -> row.length == headers.length);
        if (!isValid) {
            throw new CsvException("headers and row length don't math");
        }
    }
}
