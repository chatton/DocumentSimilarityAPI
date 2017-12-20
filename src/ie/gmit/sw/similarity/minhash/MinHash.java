package ie.gmit.sw.similarity.minhash;

import ie.gmit.sw.documents.Document;
import ie.gmit.sw.similarity.shingles.Shingle;

import java.util.List;

public class MinHash  {

    private final int hash;
    private final List<Shingle> shingles;
    private final Document document;

    public MinHash(final int hash, final List<Shingle> shingles, final Document document) {
        this.hash = hash;
        this.shingles = shingles;
        this.document = document;
    }

    public MinHashResult calculate() {
        final int minHash = shingles.stream()
                .mapToInt(shingle -> shingle.hashCode() ^ hash)
                .min()
                .orElse(Integer.MAX_VALUE);

        return new MinHashResult(document, minHash);
    }
}
