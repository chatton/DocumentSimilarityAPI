package ie.gmit.sw.similarity.shingles;

import java.util.Collection;

/**
 * The type Shingle.
 */
public class Shingle {

    private final String text;
    private final int hashCode;

    /**
     * Instantiates a new Shingle.
     *
     * @param words the words
     */
    public Shingle(final Collection<String> words) {
        this.text = String.join(" ", words);
        this.hashCode = text.hashCode();
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return String.format("Shingle{text=%s}", text);
    }
}
