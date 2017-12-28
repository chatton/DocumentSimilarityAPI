package ie.gmit.sw.similarity.shingles;

import java.util.Collection;

/**
 * The type Shingle. Represents a group of words.
 */
public class Shingle {

    private final int hashCode;

    /**
     * Instantiates a new Shingle.
     *
     * @param words the words
     */
    public Shingle(final Collection<String> words) {
        final String text = String.join(" ", words);
        this.hashCode = text.hashCode();
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
