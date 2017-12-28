package ie.gmit.sw.similarity.minhash;

import ie.gmit.sw.documents.Document;
import ie.gmit.sw.similarity.shingles.Shingle;

import java.util.List;

/**
 * The type Min hash. Used to calculate the min hash of a group
 * of shingles.
 */
public class MinHash  {

    private final int hash;
    private final List<Shingle> shingles;
    private final Document document;

    /**
     * Instantiates a new Min hash.
     *
     * @param hash     the hash
     * @param shingles the shingles
     * @param document the document
     */
    public MinHash(final int hash, final List<Shingle> shingles, final Document document) {
        this.hash = hash;
        this.shingles = shingles;
        this.document = document;
    }

    /**
     * Calculate min hash result.
     *
     * @return the min hash result
     */
    public MinHashResult calculate() {
        final int minHash = shingles.stream()
                .mapToInt(shingle -> shingle.hashCode() ^ hash)
                .min()
                .orElse(Integer.MAX_VALUE);

        return new MinHashResult(document, minHash);
    }
}
