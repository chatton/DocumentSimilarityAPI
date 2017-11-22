package ie.gmit.sw.similarity.indexes;

import ie.gmit.sw.documents.Document;
import ie.gmit.sw.similarity.shingles.Shinglizer;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CachingJaacardIndex implements SimilarityIndex {

    private final JaacardIndex index;
    private final Map<List<Document>, Double> cache;

    public CachingJaacardIndex(final Shinglizer shinglizer, final int numHashes) {
        index = new JaacardIndex(shinglizer, numHashes);
        cache = new HashMap<>();
    }

    @Override
    public double computeIndex(final List<Document> documents) {
        List<Document> sortedDocuments = documents.stream()
                .sorted(Comparator.comparingInt(Document::hashCode))
                .collect(Collectors.toList());

        if (cache.containsKey(sortedDocuments)) {
            return cache.get(sortedDocuments);
        }

        double result = index.computeIndex(documents);
        cache.put(Collections.unmodifiableList(sortedDocuments), result);
        return result;
    }
}
