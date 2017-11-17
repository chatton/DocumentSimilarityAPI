package ie.gmit.sw.similarity;

import java.util.List;
import java.util.concurrent.Callable;

public class MinHash implements Callable<MinHashResult> {

    private final int hash;
    private final List<Shingle> shingles;
    private final int docId;

    public MinHash(final int hash, final List<Shingle> shingles, final int docId) {
        this.hash = hash;
        this.shingles = shingles;
        this.docId = docId;
    }

    @Override
    public MinHashResult call() {
        int minHash = shingles.parallelStream()
                .mapToInt(shingle -> shingle.hashCode() ^ hash)
                .min()
                .orElse(Integer.MAX_VALUE);

        return new MinHashResult(minHash, docId);
    }
}
