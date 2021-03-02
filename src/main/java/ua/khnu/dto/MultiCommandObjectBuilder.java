package ua.khnu.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.function.BiConsumer;

@Getter
@Builder
public class MultiCommandObjectBuilder<T> {
    private final String ownerIdentifier;
    private final T object;
    private final List<BiConsumer<T, String>> setters;
    private final long relatedChatId;

    @Setter(AccessLevel.NONE)
    private int order;

    public void setNextValue(String value) {
        if (order >= setters.size()) {
            throw new IllegalStateException();
        }
        setters.get(order).accept(object, value);
        order++;
    }

    public boolean isDone() {
        return order >= setters.size();
    }
}
