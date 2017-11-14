package ie.gmit.sw.documents;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileDocumentProvider implements DocumentProvider {

    private final String pathToFile;

    public FileDocumentProvider(String pathToFile) {
        this.pathToFile = pathToFile;
    }

    @Override
    public Document get() throws DocumentRetrievalException {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(pathToFile));
        } catch (FileNotFoundException e) {
            throw new DocumentRetrievalException("Unable to get document, file [" + pathToFile + "] could not be found.");
        }

        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while((line = reader.readLine()) != null){
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Document(sb.toString());
    }
}
