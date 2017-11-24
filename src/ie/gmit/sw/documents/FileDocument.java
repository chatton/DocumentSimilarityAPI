package ie.gmit.sw.documents;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileDocument implements Document {
    private final String text;
    private final int id;

    public FileDocument(final String filePath) throws IOException {
        text = getTextFromFile(filePath);
        id = text.hashCode();
    }

    private String getTextFromFile(String path) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(path))) {
            return stream.collect(Collectors.joining(" "));
        }
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String text() {
        return text;
    }
}
