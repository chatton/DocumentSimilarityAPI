package ie.gmit.sw.documents;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileDocument implements Document {
    private final String text;
    private final int id;

    public FileDocument(final String filePath, final int id) throws FileNotFoundException {
        this.id = id;
        text = getTextFromFile(filePath);
    }

    private String getTextFromFile(String path) throws FileNotFoundException {
        final BufferedReader reader = new BufferedReader(new FileReader(path));

        final StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
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
