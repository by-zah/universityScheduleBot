package ua.khnu.util;

import ua.khnu.exception.CsvException;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Csv {
    private final Map<Class<?>, Function<String, ?>> classCast;

    public Csv() {
        classCast = new HashMap<>();
        classCast.put(int.class, Integer::parseInt);
        classCast.put(String.class, s -> s);
        classCast.put(double.class, Double::parseDouble);
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
        String[] rows = csv.split("\n");
        if (rows[0].isEmpty()) {
            throw new CsvException("Empty rows line");
        }
        return rows[0].split(",");
    }

    private List<String[]> getBody(String csv) {
        final var body = Arrays.stream(csv.split("\n"))
                .map(row -> row.split(","))
                .collect(Collectors.toList());
        return body.subList(1, body.size());
    }

    private void validateLengths(String[] headers, List<String[]> body) {
        var isValid = body.stream()
                .allMatch(row -> row.length == headers.length);
        if (!isValid) {
            throw new CsvException("headers and row length don't math");
        }
    }
}
