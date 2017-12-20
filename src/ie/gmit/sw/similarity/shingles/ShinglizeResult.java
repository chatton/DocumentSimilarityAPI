package ie.gmit.sw.similarity.shingles;

import ie.gmit.sw.documents.Document;
import ie.gmit.sw.similarity.results.FutureResult;

import java.util.List;

public class ShinglizeResult extends FutureResult<List<Shingle>> {
    public ShinglizeResult(final Document document, final List<Shingle> result) {
        super(document, result);
    }
}
