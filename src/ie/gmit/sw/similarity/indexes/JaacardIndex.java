package ie.gmit.sw.similarity.indexes;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import ie.gmit.sw.documents.Document;
import ie.gmit.sw.similarity.minhash.MinHash;
import ie.gmit.sw.similarity.minhash.MinHashResult;
import ie.gmit.sw.similarity.shingles.ShinglizeResult;
import ie.gmit.sw.similarity.shingles.Shinglizer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class JaacardIndex implements SimilarityIndex {

    private final Shinglizer shinglizer;
    private final int numHashes;
    private ExecutorService executor;

    public JaacardIndex(final Shinglizer shinglizer, final int numHashes) {
        this.numHashes = numHashes;
        this.executor = Executors.newCachedThreadPool();
        this.shinglizer = shinglizer;
    }

    private Set<Integer> generateHashes() {
        return new Random().ints()
                .limit(numHashes)
                .boxed()
                .collect(ImmutableSet.toImmutableSet());
    }


    private Object getResultFromFuture(final Future future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    private ShinglizeResult getShinglizeResult(final Future<ShinglizeResult> future) {
        return (ShinglizeResult) getResultFromFuture(future);
    }

    private MinHashResult getMinHashResult(final Future<MinHashResult> future) {
        return (MinHashResult) getResultFromFuture(future);
    }

    private List<ShinglizeResult> shinglize(final List<Document> documents) {
        final List<Future<ShinglizeResult>> futures = documents.stream()
                .map(doc -> executor.submit(() -> shinglizer.shinglize(doc)))
                .collect(ImmutableList.toImmutableList());

        return futures.stream()
                .map(this::getShinglizeResult)
                .collect(ImmutableList.toImmutableList());
    }

    private Map<Document, Set<Integer>> calculateMinHashResults(final List<ShinglizeResult> shinglizeResults) {

        final Set<Integer> hashes = generateHashes();
        final List<Future<MinHashResult>> minHashFutures = shinglizeResults.stream()
                .flatMap(shingleResult -> hashes.stream()
                        .map(hash -> executor.submit(() -> new MinHash(
                                hash, shingleResult.get(), shingleResult.getDocument()).calculate()))
                ).collect(ImmutableList.toImmutableList());

        final Map<Document, Set<Integer>> minHashResults = new HashMap<>();

        for (Future<MinHashResult> future : minHashFutures) {
            MinHashResult result = getMinHashResult(future);
            minHashResults.putIfAbsent(result.getDocument(), new HashSet<>());
            minHashResults.get(result.getDocument()).add(result.get());
        }

        return ImmutableMap.copyOf(minHashResults);
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
        executor = Executors.newCachedThreadPool();
        final List<ShinglizeResult> shingleResults = shinglize(documents);
        final Map<Document, Set<Integer>> minHashResults = calculateMinHashResults(shingleResults);
        final Set<Integer> finalResults = computeSetIntersections(ImmutableList.copyOf(minHashResults.values()));
        executor.shutdown();
        double size = (double) finalResults.size();
        return size / ((numHashes * shingleResults.size()) - size);
    }
}
