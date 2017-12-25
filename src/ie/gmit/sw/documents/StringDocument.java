package ie.gmit.sw.documents;

/**
 * The type String document.
 */
public class StringDocument implements Document {

    private final String text;

    /**
     * Instantiates a new String document.
     *
     * @param s the full text of the document.
     */
    public StringDocument(String s) {
        text = s;
    }

    @Override
    public String text() {
        return text;
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }
}
