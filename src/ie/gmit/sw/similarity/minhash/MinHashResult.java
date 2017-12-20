package ie.gmit.sw.similarity.minhash;


import ie.gmit.sw.documents.Document;
import ie.gmit.sw.similarity.results.FutureResult;

public class MinHashResult extends FutureResult<Integer> {
    public MinHashResult(final Document document, final int result) {
        super(document, result);
    }
}
