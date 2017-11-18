package ie.gmit.sw.similarity.indexes;


import ie.gmit.sw.documents.Document;
import ie.gmit.sw.similarity.minhash.MinHash;
import ie.gmit.sw.similarity.minhash.MinHashResult;
import ie.gmit.sw.similarity.shingles.ShinglizeResult;
import ie.gmit.sw.similarity.shingles.Shinglizer;

import java.util.ArrayList;
import java.util.Collections;
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

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class JaacardIndex implements SimilarityIndex {

    private final List<Document> documents;
    private final Shinglizer shinglizer;
    private final int numHashes;
    private ExecutorService executor;

    public JaacardIndex(final List<Document> documents, final Shinglizer shinglizer, final int numHashes) {
        if (documents.size() < 2) {
            throw new IllegalArgumentException("You must provide at least 2 documents to compare.");
        }
        this.documents = Collections.unmodifiableList(documents);
        this.numHashes = numHashes;
        this.executor = Executors.newFixedThreadPool(10);
        this.shinglizer = shinglizer;
    }

    private Set<Integer> generateHashes() {
        return new Random().ints()
                .limit(numHashes)
                .boxed()
                .collect(toSet());
    }


    private Object getResultFromFuture(final Future future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ShinglizeResult getShinglizeResult(final Future<ShinglizeResult> future) {
        return (ShinglizeResult) getResultFromFuture(future);
    }

    private MinHashResult getMinHashResult(final Future<MinHashResult> future) {
        return (MinHashResult) getResultFromFuture(future);
    }

    private List<ShinglizeResult> shinglize() {
        final List<Future<ShinglizeResult>> futures = documents.stream()
                .map(doc -> executor.submit(() -> shinglizer.shinglize(doc)))
                .collect(toList());

        return futures.stream()
                .map(this::getShinglizeResult)
                .collect(toList());
    }

    private Map<Document, Set<Integer>> calculateMinHashResults(final List<ShinglizeResult> shinglizeResults) {

        final List<Future<MinHashResult>> minHashFutures = new ArrayList<>();
        final Set<Integer> hashes = generateHashes();

        for (ShinglizeResult shinglizeResult : shinglizeResults) {
            for (Integer hash : hashes) {
                minHashFutures.add(executor.submit(() -> new MinHash(
                        hash, shinglizeResult.getResult(), shinglizeResult.getDocument()).calculate())
                );
            }
        }

        final Map<Document, Set<Integer>> minHashResults = new HashMap<>();
        for (Document doc : documents) {
            minHashResults.put(doc, new HashSet<>());
        }

        minHashFutures.stream()
                .map(this::getMinHashResult)
                .forEach(result -> {
                    Set<Integer> results = minHashResults.get(result.getDocument());
                    results.add(result.getResult());
                });

        return minHashResults;
    }

    private Set<Integer> computeSetIntersections(final List<Set<Integer>> sets) {

        if (sets.isEmpty()) {
            return new HashSet<>();
        }

        final Set<Integer> intersection = new HashSet<>(sets.get(0));
        for (int i = 1; i < sets.size(); i++) {
            intersection.retainAll(sets.get(i));
        }
        return intersection;
    }

    @Override
    public double computeIndex() {
        executor = Executors.newFixedThreadPool(10);
        final List<ShinglizeResult> shingleResults = shinglize();
        final Map<Document, Set<Integer>> minHashResults = calculateMinHashResults(shingleResults);
        final Set<Integer> finalResults = computeSetIntersections(new ArrayList<>(minHashResults.values()));
        executor.shutdown();
        return (double) finalResults.size() / numHashes;
    }
}
