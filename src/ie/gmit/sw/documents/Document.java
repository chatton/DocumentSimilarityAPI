package ie.gmit.sw.documents;

/**
 * The interface Document.
 */
public interface Document {
    /**
     * @return the text of the document.
     */
    String text();

    /**
     * @return an integer ID unique to the document.
     */
    int id();
}
