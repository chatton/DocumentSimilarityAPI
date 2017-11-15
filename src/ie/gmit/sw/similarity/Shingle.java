package ie.gmit.sw.similarity;


public class Shingle {

    private final int docId;
    private final String text;
    protected final boolean isPoison;
    private final int number;

    public boolean isPoison() {
        return isPoison;
    }

    public Shingle(final String text, final int docId, int number) {
        this(text, docId, number, false);
    }

    public Shingle(final String text, final int docId, int number, final boolean isPoison) {
        this.docId = docId;
        this.text = text;
        this.isPoison = isPoison;
        this.number = number;
    }


    public int getDocId() {
        return docId;
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Shingle{docId=%s, text=%s}", docId, text);
    }

    public int getNumber() {
        return number;
    }
}
