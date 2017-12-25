package ie.gmit.sw;

import ie.gmit.sw.documents.Document;
import ie.gmit.sw.documents.FileDocument;
import ie.gmit.sw.documents.StringDocument;
import ie.gmit.sw.documents.URLDocument;
import ie.gmit.sw.similarity.indexes.CachingJaacardIndex;
import ie.gmit.sw.similarity.indexes.JaacardIndex;
import ie.gmit.sw.similarity.indexes.SimilarityIndex;
import ie.gmit.sw.similarity.shingles.Shinglizer;
import ie.gmit.sw.similarity.shingles.WordShinglizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * The type Ui.
 */
class UI {

    private final static int MIN_NUMBER_DOCUMENTS = 2;
    private final Scanner scanner;
    private boolean running;

    /**
     * Instantiates a new Ui.
     *
     * @param scanner the scanner
     */
    UI(Scanner scanner) {
        this.scanner = scanner;
        running = true;
    }

    private String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private int promptForInt(String prompt) {
        System.out.print(prompt);
        final int choice = scanner.nextInt();
        scanner.nextLine(); // swallow new line character.
        return choice;
    }

    private void displayMenu() {
        System.out.println("Enter option.");
        System.out.println("1) Compare document similarity.");
        System.out.println("2) Exit.");
    }

    private int getNumberOfDocumentsToCreate() {
        int numDocs;
        do {
            numDocs = promptForInt("How many documents do you want to compare?");
            if (numDocs < MIN_NUMBER_DOCUMENTS) {
                System.out.println("You must compare at least [" + MIN_NUMBER_DOCUMENTS + "] documents.");
            }
        } while (numDocs < MIN_NUMBER_DOCUMENTS);
        return numDocs;
    }

    private void compareDocuments() {
        final int numDocs = getNumberOfDocumentsToCreate();
        final List<Document> documents = new ArrayList<>(numDocs);
        for (int i = 0; i < numDocs; i++) {
            final Document document = createDocument();
            documents.add(document);
        }

        final int shingleSize = promptForInt("Enter shingle size: ");
        final int numMinHashes = promptForInt("Enter number of min hashes: ");
        final Shinglizer wordShinglizer = new WordShinglizer(shingleSize);
        final SimilarityIndex index = new CachingJaacardIndex(wordShinglizer, numMinHashes);
        System.out.println("Jaacard Index: " + index.computeIndex(documents));
    }

    private Document createDocument() {
        Optional<Document> document = Optional.empty();
        do {
            System.out.println("1) File Document");
            System.out.println("2) Url Document");
            System.out.println("3) Plain text document.");
            final int choice = promptForInt("Which type of document do you want to make?");
            switch (choice) {
                case 1:
                    document = createFileDocument();
                    break;
                case 2:
                    document = createURLDocument();
                    break;
                case 3:
                    document = createStringDocument();
                    break;
                default:
                    System.out.println("Please enter a valid option.");
            }
        } while (!document.isPresent());

        return document.get();
    }

    private Optional<Document> createFileDocument() {
        final String path = promptForString("Enter the path of the file you want to create a document from.");
        try {
            return Optional.of(new FileDocument(path));
        } catch (IOException e) {
            System.out.println("There was an error reading the file [" + path + "]");
            System.out.println("Make sure the path is correct and that the file exists.");
            return Optional.empty();
        }
    }

    private Optional<Document> createURLDocument() {
        final String url = promptForString("Enter the url you want to use (e.g. https://www.irishtimes.com/)");
        final String tagString = promptForString("Enter the tags you want to include space separated on one line. (e.g. 'p h1 h2')");
        final String[] tags = tagString.split(" ");
        try {
            final Document doc = new URLDocument.Builder()
                    .url(url)
                    .tags(tags)
                    .build();

            return Optional.of(doc);

        } catch (IOException e) {
            System.out.println("There was an error creating a URL Document with the provided url [" + url + "]");
            System.out.println("There could be a problem with the URL itself or your internet connection.");
            return Optional.empty();
        }
    }

    private Optional<Document> createStringDocument() {
        final String text = promptForString("Enter the document text. (all on one line)");
        return Optional.of(new StringDocument(text));
    }

    /**
     * Start.
     */
    void start() {
        do {
            displayMenu();
            switch (promptForInt("")) {
                case 1:
                    compareDocuments();
                    break;
                case 2:
                    System.out.println("Goodbye!");
                    running = false;
                    break;
            }
        } while (running);
    }
}
