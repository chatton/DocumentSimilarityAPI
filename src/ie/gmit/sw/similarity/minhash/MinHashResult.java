package ie.gmit.sw.similarity.minhash;


import ie.gmit.sw.documents.Document;

public class MinHashResult {
    private final int result;
    private final Document document;

    public MinHashResult(final int result, final Document document) {
        this.result = result;
        this.document = document;
    }

    public int get() {
        return result;
    }

    public Document getDocument() {
        return document;
    }
}
