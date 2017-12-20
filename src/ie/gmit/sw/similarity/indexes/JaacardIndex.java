package ie.gmit.sw.similarity.indexes;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import ie.gmit.sw.documents.Document;
import ie.gmit.sw.similarity.minhash.MinHash;
import ie.gmit.sw.similarity.minhash.MinHashResult;
import ie.gmit.sw.similarity.results.FutureResult;
import ie.gmit.sw.similarity.shingles.ShinglizeResult;
import ie.gmit.sw.similarity.shingles.Shinglizer;
import ie.gmit.sw.util.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class JaacardIndex implements SimilarityIndex {

    private final Shinglizer shinglizer;
    private final int numHashes;
    private ExecutorService executor;

    public JaacardIndex(final Shinglizer shinglizer, final int numHashes) {
        this.numHashes = numHashes;
        this.shinglizer = shinglizer;
    }

    private Set<Integer> generateHashes() {
        return new Random().ints()
                .limit(numHashes)
                .boxed()
                .collect(ImmutableSet.toImmutableSet());
    }


    @SuppressWarnings("unchecked")
    private <T extends FutureResult> T getResult(Future future) {
        try {
            return (T) future.get();
        } catch (InterruptedException | ExecutionException e) {
            return null; // TODO don't return null
        }
    }

    private ShinglizeResult getShinglizeResult(final Future<ShinglizeResult> future) {
        return getResult(future);
    }

    private MinHashResult getMinHashResult(final Future<MinHashResult> future) {
        return getResult(future);
    }

    private List<ShinglizeResult> shinglize(final List<Document> documents) {
        final List<Future<ShinglizeResult>> futures = documents.stream()
                .map(doc -> executor.submit(() -> shinglizer.shinglize(doc)))
                .collect(ImmutableList.toImmutableList());

        return futures.stream()
                .map(this::getShinglizeResult)
                .collect(ImmutableList.toImmutableList());
    }

    private Set<Integer> merge(final Set<Integer> set1, final Set<Integer> set2) {
        return ImmutableSet.<Integer>builder()
                .addAll(set1).addAll(set2)
                .build();
    }

    private List<Future<MinHashResult>> getMinHashFutures(final List<ShinglizeResult> shinglizeResults, final Set<Integer> hashes) {
        return shinglizeResults.stream()
                .flatMap(shingleResult -> hashes.stream().map(hash -> new Pair<>(shingleResult, hash)))
                .map(pair -> executor.submit(() -> makeMinHash(pair).calculate()))
                .collect(ImmutableList.toImmutableList());
    }

    private MinHash makeMinHash(Pair<ShinglizeResult, Integer> pair) {
        return new MinHash(pair.second(), pair.first().get(), pair.first().getDocument());
    }

    private Map<Document, Set<Integer>> buildMinHashFutureResults(final List<Future<MinHashResult>> futures) {
        final Map<Document, Set<Integer>> map = futures.stream()
                .map(this::getMinHashResult)
                .collect(Collectors.toMap(
                        MinHashResult::getDocument, // key mapper - key will be the document itself.
                        result -> ImmutableSet.of(result.get()), // value mapper - set of single minhash result value
                        this::merge // on a collision of keys, merge the values into a single set.
                ));

        return ImmutableMap.copyOf(map);
    }

    private Map<Document, Set<Integer>> calculateMinHashResults(final List<ShinglizeResult> shinglizeResults) {
        final Set<Integer> hashes = generateHashes();
        final List<Future<MinHashResult>> minHashFutures = getMinHashFutures(shinglizeResults, hashes);
        return buildMinHashFutureResults(minHashFutures);
    }

    private Set<Integer> computeSetIntersections(final List<Set<Integer>> sets) {

        if (sets.isEmpty()) {
            return ImmutableSet.of();
        }

        final Set<Integer> intersection = new HashSet<>(sets.get(0));
        for (int i = 1; i < sets.size(); i++) {
            intersection.retainAll(sets.get(i));
        }

        return ImmutableSet.copyOf(intersection);
    }

    @Override
    public double computeIndex(final List<Document> documents) {
        if (documents.size() < 2) {
            throw new IllegalArgumentException("Must provide at least 2 documents to compare.");
        }

        executor = Executors.newCachedThreadPool();
        final List<ShinglizeResult> shingleResults = shinglize(documents);
        final Map<Document, Set<Integer>> minHashResults = calculateMinHashResults(shingleResults);
        final Set<Integer> finalResults = computeSetIntersections(ImmutableList.copyOf(minHashResults.values()));
        executor.shutdown();

        final double size = (double) finalResults.size();
        return size / ((numHashes * documents.size()) - size);
    }
}
