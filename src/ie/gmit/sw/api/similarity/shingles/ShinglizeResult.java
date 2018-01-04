package ie.gmit.sw.api.similarity.shingles;

import ie.gmit.sw.api.documents.Document;
import ie.gmit.sw.api.similarity.results.FutureResult;

import java.util.List;

/**
 * The type ShinglizeResult. A Subclass of FutureResult
 * that returns a List of {@link Shingle}s as the result type.
 *
 * @author Cian Hatton
 * @see Shinglizer
 */
public class ShinglizeResult extends FutureResult<List<Shingle>> {
    /**
     * Instantiates a new Shinglize result.
     *
     * @param document the document
     * @param result   the result
     */
    public ShinglizeResult(final Document document, final List<Shingle> result) {
        super(document, result);
    }
}
