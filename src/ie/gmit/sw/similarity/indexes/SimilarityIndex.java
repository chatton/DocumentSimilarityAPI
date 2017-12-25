package ie.gmit.sw.similarity.indexes;

import ie.gmit.sw.documents.Document;

import java.util.List;

/**
 * The interface Similarity index.
 */
public interface SimilarityIndex {
    /**
     * Compute index double.
     *
     * @param documents the documents
     * @return the double
     */
    double computeIndex(final List<Document> documents);
}
