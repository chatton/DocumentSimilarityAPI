package ie.gmit.sw.similarity.shingles;

import ie.gmit.sw.documents.Document;

import java.util.Collections;
import java.util.List;

public class ShinglizeResult {
    private final Document document;
    private final List<Shingle> result;

    public ShinglizeResult(final Document document, final List<Shingle> result) {
        this.document = document;
        this.result = Collections.unmodifiableList(result);
    }

    public Document getDocument() {
        return document;
    }

    public List<Shingle> getResult() {
        return result;
    }
}
