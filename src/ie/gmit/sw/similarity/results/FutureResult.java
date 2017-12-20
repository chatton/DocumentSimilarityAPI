package ie.gmit.sw.similarity.results;

import ie.gmit.sw.documents.Document;

public class FutureResult<T> {

    private final Document document;
    private final T result;

    public FutureResult(Document document, T result) {
        this.result = result;
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }

    public T get() {
        return result;
    }
}
