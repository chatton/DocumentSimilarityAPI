package ie.gmit.sw.similarity.shingles;


public class Shingle {

    private final String text;
    private final int hashCode;

    public Shingle(final String text) {
        this.text = text;
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
