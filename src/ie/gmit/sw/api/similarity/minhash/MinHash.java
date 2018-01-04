package ie.gmit.sw.api.similarity.minhash;

import com.google.common.collect.ImmutableList;
import ie.gmit.sw.api.documents.Document;
import ie.gmit.sw.api.similarity.shingles.Shingle;

import java.util.Collection;

/**
 * The type Min hash. Used to calculate the min hash of a group
 * of shingles. This value is used to compute the JaacardIndex.
 *
 * @author Cian Hatton
 * @see ie.gmit.sw.api.similarity.indexes.JaacardIndex
 * @see Shingle
 */
public class MinHash {

    private final int hash;
    private final Collection<Shingle> shingles;
    private final Document document;

    /**
     * Instantiates a new Min hash.
     *
     * @param hash     the hash value
     * @param shingles the shingles
     * @param document the document
     */
    public MinHash(final int hash, final Collection<Shingle> shingles, final Document document) {
        this.hash = hash;
        this.shingles = ImmutableList.copyOf(shingles);
        this.document = document;
    }

    /**
     * Calculates the min hash result. The min hash is the lowest value
     * seen after looking at the hash of every {@link Shingle} provided
     * with the hash constructor parameter.
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
