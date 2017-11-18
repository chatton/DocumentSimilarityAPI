package ie.gmit.sw.similarity;


import ie.gmit.sw.documents.Document;
import ie.gmit.sw.similarity.minhash.MinHash;
import ie.gmit.sw.similarity.minhash.MinHashResult;
import ie.gmit.sw.similarity.shingles.ShinglizeResult;
import ie.gmit.sw.similarity.shingles.Shinglizer;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class JaacardIndex implements SimilarityIndex {

    private final List<Document> documents;
    private final ExecutorService executor;
    private final int k;
    private final Shinglizer shinglizer;

    public JaacardIndex(final List<Document> documents, final Shinglizer shinglizer) {
        if (documents.size() < 2) {
            throw new IllegalArgumentException("You must provide at least 2 documents to compare.");
        }
        this.documents = Collections.unmodifiableList(documents);
        k = 150; // TODO remove hard coded value.
        executor = Executors.newFixedThreadPool(8);
        this.shinglizer = shinglizer;
    }

    private Set<Integer> generateHashes() {
        Random rnd = new Random();
        Set<Integer> hashes = new TreeSet<>();
        while (hashes.size() < k) {
            hashes.add(rnd.nextInt());
        }
        return hashes;
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
        List<Future<ShinglizeResult>> futures = documents
                .stream()
                .map(doc -> executor.submit(() -> shinglizer.shinglize(doc)))
                .collect(Collectors.toList());

        return futures.stream()
                .map(this::getShinglizeResult)
                .collect(Collectors.toList());

    }

    @Override
    public double computeIndex() {

        List<ShinglizeResult> shingles = shinglize();
        Set<Integer> hashes = generateHashes();

        List<Future<MinHashResult>> minHashes = new ArrayList<>();
        for (ShinglizeResult shinglizeResult : shingles) {
            for (Integer hash : hashes) {
                minHashes.add(executor.submit(new MinHash(
                                hash, shinglizeResult.getResult(), shinglizeResult.getDocument())
                        )
                );
            }
        }

        Map<Document, Set<Integer>> minHashResults = new HashMap<>();
        for (Document doc : documents) {
            minHashResults.put(doc, new HashSet<>());
        }

        minHashes.stream()
                .map(this::getMinHashResult)
                .forEach(result -> {
                    Set<Integer> results = minHashResults.get(result.getDocument());
                    results.add(result.getResult());
                });

        executor.shutdown();

        Set<Integer> finalResults = new HashSet<>(minHashResults.get(documents.get(0)));
        for (Set set : minHashResults.values()) {
            finalResults.retainAll(set);
        }

        return (double) finalResults.size() / k;

    }
}
