package ie.gmit.sw.similarity;


import java.util.Collections;
import java.util.List;

public class Shingle {

    private final List<String> words;
    private final int docId;

    public Shingle(final List<String> words, final int docId) {
        this.words = Collections.unmodifiableList(words);
        this.docId = docId;
    }

    public int getDocId() {
        return docId;
    }

    public List<String> getWords() {
        return words;
    }

    @Override
    public String toString(){
        return String.format("Shingle{docId=%s, words=%s}", docId, words);
    }

}
