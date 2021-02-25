package ua.khnu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class File {
    private final String content;
    private final String extension;
}
