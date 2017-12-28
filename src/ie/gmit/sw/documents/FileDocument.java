package ie.gmit.sw.documents;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type FileDocument. A FileDocument is a {@link Document} subclass
 * created from a file.
 */
public class FileDocument implements Document {

    private final String text;
    private final int id;

    /**
     * Instantiates a new File document.
     *
     * @param filePath the path to the file the document should be made from.
     * @throws IOException throws an IOException if the file is not found.
     */
    public FileDocument(final String filePath) throws IOException {
        text = getTextFromFile(filePath);
        id = text.hashCode();
    }

    /**
     * @param path the path to the file.
     * @return The new line separated contents of the file as a String.
     * @throws IOException if there is an error reading the file at the specified path.
     */
    private String getTextFromFile(final String path) throws IOException {
        try (final Stream<String> stream = Files.lines(Paths.get(path))) {
            return stream.collect(Collectors.joining(System.lineSeparator()));
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

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof FileDocument)) {
            return false;
        }
        return id == ((FileDocument) other).id;
    }
}
