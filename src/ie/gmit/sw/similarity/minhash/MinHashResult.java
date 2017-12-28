package ie.gmit.sw.similarity.minhash;


import ie.gmit.sw.documents.Document;
import ie.gmit.sw.similarity.results.FutureResult;

/**
 * The type MinHashResult. A Subclass of FutureResult
 * that returns an Integer as the result type.
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
