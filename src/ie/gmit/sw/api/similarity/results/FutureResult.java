package ie.gmit.sw.api.similarity.results;

import ie.gmit.sw.api.documents.Document;

/**
 * The type Future result is an abstract base class.
 * It holds onto a {@link Document} and a Generic result.
 *
 * @param <T> the type parameter
 * @author Cian Hatton
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
    public FutureResult(final Document document, final T result) {
        this.result = result;
        this.document = document;
    }

    /**
     * Gets the document associated with the FutureResult.
     *
     * @return the document
     */
    public Document document() {
        return document;
    }

    /**
     * Gets the result associated with the FutureResult.
     *
     * @return the result
     */
    public T get() {
        return result;
    }
}
