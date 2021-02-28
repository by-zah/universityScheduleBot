package ua.khnu.dto;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Component
public class MultiCommandBuildersContainer {
    private final List<MultiCommandObjectBuilder<?>> multiCommandBuilders;

    public MultiCommandBuildersContainer() {
        multiCommandBuilders = new CopyOnWriteArrayList<>();
    }
}
