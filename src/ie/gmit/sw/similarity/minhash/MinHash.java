package ie.gmit.sw.similarity.minhash;

import ie.gmit.sw.documents.Document;
import ie.gmit.sw.similarity.shingles.Shingle;

import java.util.List;
import java.util.concurrent.Callable;

public class MinHash implements Callable<MinHashResult> {

    private final int hash;
    private final List<Shingle> shingles;
    private final Document document;

    public MinHash(final int hash, final List<Shingle> shingles, final Document document) {
        this.hash = hash;
        this.shingles = shingles;
        this.document = document;
    }

    @Override
    public MinHashResult call() {
        int minHash = shingles.parallelStream()
                .mapToInt(shingle -> shingle.hashCode() ^ hash)
                .min()
                .orElse(Integer.MAX_VALUE);

        return new MinHashResult(minHash, document);
    }
}
