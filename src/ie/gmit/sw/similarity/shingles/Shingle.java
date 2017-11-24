package ie.gmit.sw.similarity.shingles;


import java.util.List;

public class Shingle {

    private final String text;
    private final int hashCode;

    public Shingle(final List<String> words) {
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
