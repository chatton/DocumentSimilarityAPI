package ie.gmit.sw;

import com.google.common.collect.ImmutableSet;
import ie.gmit.sw.documents.Document;
import ie.gmit.sw.documents.FileDocument;
import ie.gmit.sw.documents.StringDocument;
import ie.gmit.sw.documents.URLDocument;
import ie.gmit.sw.similarity.indexes.CachingSimilarityIndex;
import ie.gmit.sw.similarity.indexes.JaacardIndex;
import ie.gmit.sw.similarity.indexes.SimilarityIndex;
import ie.gmit.sw.similarity.shingles.Shinglizer;
import ie.gmit.sw.similarity.shingles.WordShinglizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

class UI {

    private final static Set<String> VALID_CACHING_OPTIONS = ImmutableSet.of(
            "yes", "y", "no", "n"
    );

    private final static int MIN_NUMBER_DOCUMENTS = 2;

    private final Scanner scanner;

    private boolean running;
    private boolean isConfigured;
    private SimilarityIndex index;

    UI(final Scanner scanner) {
        this.scanner = scanner;
        running = true;
    }

    private String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private int promptForInt(final String prompt) {

        boolean finished = false;
        int choice = -1;
        while (!finished) {
            try {
                choice = Integer.parseInt(promptForString(prompt));
                finished = true;
            } catch (NumberFormatException e) {
                System.out.println("Bad input - enter again.");
            }
        }
        return choice;
    }

    private void displayMenu() {
        System.out.println("1) Compare document similarity.");
        System.out.println("2) Configure Jaacard Index.");
        System.out.println("3) Exit.");
    }

    private void configureJaacardIndex() {
        final Shinglizer shinglizer = createShinglizer();
        final int numMinHashes = promptForInt("Enter number of min hashes: ");
        final SimilarityIndex jaacardIndex = new JaacardIndex(shinglizer, numMinHashes);
        final boolean wantsToCache = wantsToCache();
        if (wantsToCache) {
            index = new CachingSimilarityIndex(jaacardIndex);
        } else {
            index = jaacardIndex;
        }
        isConfigured = true;
    }

    private boolean wantsToCache() {
        String choice;
        do {
            choice = promptForString("Do you want to cache results? (Y)es / (N)o: ");
        } while (!validChoice(choice));
        return choice.toLowerCase().startsWith("y");
    }

    private Shinglizer createShinglizer() {
        final int shingleSize = promptForInt("Enter shingle size: ");
        return new WordShinglizer(shingleSize);
    }

    private int getNumberOfDocumentsToCreate() {
        int numDocs;
        do {
            numDocs = promptForInt("How many documents do you want to compare?: ");
            if (numDocs < MIN_NUMBER_DOCUMENTS) {
                System.out.println("You must compare at least [" + MIN_NUMBER_DOCUMENTS + "] documents.");
            }
        } while (numDocs < MIN_NUMBER_DOCUMENTS);
        return numDocs;
    }

    private Document createDocument() {
        Document document = null;
        do {
            System.out.println("1) File Document");
            System.out.println("2) Url Document");
            System.out.println("3) Plain text document.");
            final int choice = promptForInt("Which type of document do you want to make?: ");
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
        } while (document == null);

        return document;
    }

    private List<Document> createDocuments(final int numDocs) {
        final List<Document> documents = new ArrayList<>(numDocs);
        for (int i = 0; i < numDocs; i++) {
            documents.add(createDocument());
        }
        return documents;
    }

    private void compareDocuments() {
        assert isConfigured;
        final int numDocs = getNumberOfDocumentsToCreate();
        final List<Document> documents = createDocuments(numDocs);
        final double result = index.computeIndex(documents);
        System.out.println("The [" + documents.size() + "] documents have a Jaacard index of [" + result + "]");
    }

    private Document createFileDocument() {
        final String path = promptForString("Enter the path of the file you want to create a document from.");
        try {
            return new FileDocument(path);
        } catch (IOException e) {
            System.out.println("There was an error reading the file [" + path + "]");
            System.out.println("Make sure the path is correct and that the file exists.");
            return null;
        }
    }

    private Document createURLDocument() {
        final String url = promptForString("Enter the url you want to use (e.g. www.irishtimes.com)");
        final String tagString = promptForString("Enter the tags you want to include space separated on one line. (e.g. 'p h1 h2')");
        final String[] tags = tagString.split("\\s+");
        try {
            return new URLDocument.Builder()
                    .url(url)
                    .tags(tags)
                    .build();
        } catch (IOException e) {
            System.out.println("There was an error creating a URL Document with the provided url [" + url + "]");
            System.out.println("There could be a problem with the URL itself or your internet connection.");
            return null;
        }
    }

    private Document createStringDocument() {
        final String text = promptForString("Enter the document text. (all on one line): ");
        return new StringDocument(text);
    }

    private boolean validChoice(final String choice) {
        return VALID_CACHING_OPTIONS.contains(choice.toLowerCase());
    }


    void start() {
        do {
            displayMenu();
            switch (promptForInt("Enter option: ")) {
                case 1:
                    if (isConfigured) {
                        compareDocuments();
                    } else {
                        System.out.println("You need to configure the JaacardIndex before comparing documents.");
                    }
                    break;
                case 2:
                    configureJaacardIndex();
                    System.out.println("Jaacard Index is configured.");
                    break;
                case 3:
                    System.out.println("Goodbye!");
                    running = false;
                    break;
            }
        } while (running);
    }
}
