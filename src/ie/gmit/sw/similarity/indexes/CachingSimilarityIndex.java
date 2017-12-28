package ie.gmit.sw.similarity.indexes;

import com.google.common.collect.ImmutableList;
import ie.gmit.sw.documents.Document;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CachingSimilarityIndex caches any computed values and will
 * return the same values if the same documents are provided as
 * input parameters for the computeIndex method (irrespective of order).
 */
public class CachingSimilarityIndex implements SimilarityIndex {

    private final SimilarityIndex index;
    private final Map<List<Integer>, Double> cache;

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
        final List<Integer> documentHashCodes = documents.stream()
                .sorted(Comparator.comparingInt(Document::hashCode))
                .map(Document::hashCode)
                .collect(ImmutableList.toImmutableList());
        /*
        by sorting the documents before checking it allows us to
        take advantage of having cached results even if the documents
        are not provided in the same order.
         */

        if (cache.containsKey(documentHashCodes)) {
            return cache.get(documentHashCodes);
        }

        final double result = index.computeIndex(documents);
        cache.put(documentHashCodes, result);
        return result;
    }
}
