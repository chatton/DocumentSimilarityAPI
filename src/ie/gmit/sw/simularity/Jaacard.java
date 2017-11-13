package ie.gmit.sw.simularity;

import ie.gmit.sw.Document;

public class Jaacard implements SimilarityIndex {

    private final Document doc1;
    private final Document doc2;

    public Jaacard(final Document doc1, final Document doc2) {
        this.doc1 = doc1;
        this.doc2 = doc2;
    }

    @Override
    public double computeIndex() {
        return 0;
    }
}
