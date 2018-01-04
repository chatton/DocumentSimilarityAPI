package ie.gmit.sw.api.similarity.indexes.generator;

import java.util.Collection;
import java.util.Random;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

/**
 * HashGenerator generates pseudo random numbers to be used
 * in the min hash algorithm.
 *
 * @author Cian Hatton
 * @see ie.gmit.sw.api.similarity.indexes.JaacardIndex
 * @see ie.gmit.sw.api.similarity.minhash.MinHash
 */
public class HashGenerator {

    private final int numHashes;

    public HashGenerator(final int numHashes) {
        this.numHashes = numHashes;
    }

    /**
     * Generates a collection of pseudo-random numbers.
     *
     * @return a Collection of pseudo-random unique boxed integers.
     */
    public Collection<Integer> generate() {
        return new Random().ints()
                .limit(numHashes)
                .boxed()
                .collect(toImmutableSet());
    }

    public int getNumHashes() {
        return numHashes;
    }
}
