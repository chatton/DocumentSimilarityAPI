package ie.gmit.sw.similarity.indexes;

import com.google.common.collect.ImmutableList;
import ie.gmit.sw.documents.Document;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Caching jaacard index.
 * CachingSimilarityIndex caches any computed values and will
 * return the same values if the same documents are provided as
 * input parameters for the computeIndex method.
 */
public class CachingSimilarityIndex implements SimilarityIndex {

    private final SimilarityIndex index;
    private final Map<List<Document>, Double> cache;

    public CachingSimilarityIndex(final SimilarityIndex index) {
        this.index = index;
        cache = new HashMap<>();
    }

    /**
     * @param documents the documents that will be compared to each other.
     * @return a result between 0 and 1 indicating the level of similarity between documents.
     */
    @Override
    public double computeIndex(final List<Document> documents) {
        final List<Document> sortedDocuments = documents.stream()
                .sorted(Comparator.comparingInt(Document::hashCode))
                .collect(ImmutableList.toImmutableList());

        if (cache.containsKey(sortedDocuments)) {
            return cache.get(sortedDocuments);
        }

        final double result = index.computeIndex(documents);
        cache.put(sortedDocuments, result);
        return result;
    }
}
