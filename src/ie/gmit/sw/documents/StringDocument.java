package ie.gmit.sw.documents;

/**
 * The type String document. A StringDocument is a {@link Document} subclass
 * created from a String.
 */
public class StringDocument implements Document {

    private final String text;
    private final int id;

    /**
     * Instantiates a new String document.
     *
     * @param s the full text of the document.
     */
    public StringDocument(final String s) {
        text = s;
        id = text.hashCode();
    }

    @Override
    public String text() {
        return text;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof StringDocument)) {
            return false;
        }
        return id == ((StringDocument) other).id;
    }
}
