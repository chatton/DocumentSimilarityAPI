package ie.gmit.sw.similarity.indexes;

import ie.gmit.sw.documents.Document;

import java.util.List;

public interface SimilarityIndex {
    double computeIndex(final List<Document> documents);
}
