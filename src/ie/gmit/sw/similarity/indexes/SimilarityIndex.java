package ie.gmit.sw.similarity.indexes;

import ie.gmit.sw.documents.Document;

import java.util.List;

public interface SimilarityIndex {
    /**
     * Computes a value between 0 and 1 indicating the similarity
     * of the documents provided as input arguments.
     *
     * @param documents the {@link Document}s to be compared.
     * @return similarity index between all the provided documents as a double between 0 and 1.
     */
    double computeIndex(List<Document> documents);
}
