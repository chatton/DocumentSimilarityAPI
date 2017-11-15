package ie.gmit.sw.similarity;


public class Shingle {
    private final int docId;
    private final String text;

    public Shingle(final String text, final int docId) {
        this.docId = docId;
        this.text = text;
    }

    public int getDocId() {
        return docId;
    }

    @Override
    public int hashCode(){
        return text.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Shingle{docId=%s, text=%s}", docId, text);
    }

}
