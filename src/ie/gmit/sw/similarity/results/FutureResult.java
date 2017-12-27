package ie.gmit.sw.similarity.results;

import ie.gmit.sw.documents.Document;

/**
 * The type Future result.
 *
 * @param <T> the type parameter
 */
public abstract class FutureResult<T> {

    private final Document document;
    private final T result;

    /**
     * Instantiates a new Future result.
     *
     * @param document the document
     * @param result   the result
     */
    public FutureResult(Document document, T result) {
        this.result = result;
        this.document = document;
    }

    /**
     * Gets document.
     *
     * @return the document
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Get t.
     *
     * @return the t
     */
    public T get() {
        return result;
    }
}
