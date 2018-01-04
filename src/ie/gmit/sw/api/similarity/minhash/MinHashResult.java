package ie.gmit.sw.api.similarity.minhash;


import ie.gmit.sw.api.documents.Document;
import ie.gmit.sw.api.similarity.results.FutureResult;

/**
 * The type MinHashResult. A Subclass of FutureResult
 * that returns an Integer as the result type. Used to hold onto
 * the result of a MinHash calculation.
 *
 * @author Cian Hatton
 * @see MinHash
 * @see ie.gmit.sw.api.similarity.indexes.JaacardIndex
 */
public class MinHashResult extends FutureResult<Integer> {
    /**
     * Instantiates a new Min hash result.
     *
     * @param document the document
     * @param result   the Integer result
     */
    public MinHashResult(final Document document, final int result) {
        super(document, result);
    }
}
