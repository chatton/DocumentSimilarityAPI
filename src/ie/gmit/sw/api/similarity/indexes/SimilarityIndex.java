package ie.gmit.sw.api.similarity.indexes;

import ie.gmit.sw.api.documents.Document;

import java.util.List;

/**
 * A Similarity Index can compute an index between 0 and 1
 * which representds the similarity between a list of {@link Document}s.
 *
 * @author Cian Hatton
 */
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
