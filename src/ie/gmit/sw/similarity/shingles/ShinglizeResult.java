package ie.gmit.sw.similarity.shingles;

import ie.gmit.sw.documents.Document;
import ie.gmit.sw.similarity.results.FutureResult;

import java.util.List;

/**
 * The type Shinglize result.
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
