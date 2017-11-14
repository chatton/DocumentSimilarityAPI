package ie.gmit.sw.documents;

public class Document {
    private static int numDocs;

    private final String text;
    private final int id;
    private final String name;

    public Document(final String text) {
        this(text, "Document:" + numDocs);
    }

    public Document(final String text, final String name) {
        this.text = text;
        this.id = numDocs++;
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("Document{name=%s, id=%s}", name, id);
    }
}
