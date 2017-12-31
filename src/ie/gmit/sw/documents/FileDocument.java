package ie.gmit.sw.documents;

import afu.org.checkerframework.checker.oigj.qual.O;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type FileDocument. A FileDocument is a {@link Document} implementation
 * created from a file.
 */
public class FileDocument implements Document {

    private final StringDocument stringDoc;

    /**
     * Instantiates a new File document.
     *
     * @param filePath the path to the file the document should be made from.
     * @throws IOException throws an IOException if the file is not found.
     */
    public FileDocument(final String filePath) throws IOException {
        stringDoc = new StringDocument(getTextFromFile(filePath));
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
        return stringDoc.hashCode();
    }

    @Override
    public int id() {
        return hashCode();
    }

    @Override
    public String text() {
        return stringDoc.text();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof FileDocument)) {
            return false;
        }
        return stringDoc.equals(((FileDocument) other).stringDoc);
    }
}
