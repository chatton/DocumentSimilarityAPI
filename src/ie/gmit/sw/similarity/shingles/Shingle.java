package ie.gmit.sw.similarity.shingles;


public class Shingle {

    private final String text;

    public Shingle(final String text) {
        this.text = text;
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Shingle{text=%s}", text);
    }
}
