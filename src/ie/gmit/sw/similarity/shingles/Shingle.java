package ie.gmit.sw.similarity.shingles;

import java.util.Collection;

/**
 * The type Shingle. Represents a group of words.
 * Only the hashCode is stored.
 */
public class Shingle {

    private final int hashCode;
    private final String text;

    /**
     * Instantiates a new Shingle.
     *
     * @param words the words
     * @throws IllegalArgumentException if an empty collection or null is provided
     */
    public Shingle(final Collection<String> words) {
        if (words == null || words.isEmpty()) {
            throw new IllegalArgumentException("Must provide a Collection with at least 1 word.");
        }

        text = String.join(" ", words)
                .replace("\n", "")
                .replace("\r", "");

        hashCode = text.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof Shingle)) {
            return false;
        }
        return hashCode == ((Shingle) other).hashCode;
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
