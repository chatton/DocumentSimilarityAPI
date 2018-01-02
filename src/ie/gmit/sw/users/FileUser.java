package ie.gmit.sw.users;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileUser implements User {

    private final BufferedReader reader;
    private final File outputFile;

    public FileUser(final File inputFile, final File outputFile) throws IOException {
        createFileIfAbsent(outputFile);
        this.reader = new BufferedReader(new FileReader(inputFile));
        this.outputFile = outputFile;

    }

    private boolean createFileIfAbsent(final File file) throws IOException {
        if (!file.exists()) {
            return file.delete() && file.createNewFile();
        }
        return true;
    }

    @Override
    public void write(String str) {
        try {
            try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile, true))) {
                writer.write(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeLine(String str) {
        write(str + System.lineSeparator());
    }

    @Override
    public String read() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            return null;
        }
    }
}
