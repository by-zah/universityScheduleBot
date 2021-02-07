package ua.khnu.dto;

public class File {
    private final String content;
    private final String extension;

    public File(byte[] content, String extension) {
        this.content = new String(content);
        this.extension = extension;
    }

    public String getContent() {
        return content;
    }

    public String getExtension() {
        return extension;
    }
}
