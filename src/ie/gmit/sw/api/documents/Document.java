package ie.gmit.sw.api.documents;

/**
 * The interface Document.
 *
 * @author Cian Hatton
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
