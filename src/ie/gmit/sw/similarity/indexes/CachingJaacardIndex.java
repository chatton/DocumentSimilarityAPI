package ie.gmit.sw.similarity.indexes;

import com.google.common.collect.ImmutableList;
import ie.gmit.sw.documents.Document;
import ie.gmit.sw.similarity.shingles.Shinglizer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CachingJaacardIndex implements SimilarityIndex {

    private final JaacardIndex index;
    private final Map<List<Document>, Double> cache;

    public CachingJaacardIndex(final Shinglizer shinglizer, final int numHashes) {
        index = new JaacardIndex(shinglizer, numHashes);
        cache = new HashMap<>();
    }

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
