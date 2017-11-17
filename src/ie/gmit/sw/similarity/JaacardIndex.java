package ie.gmit.sw.similarity;


import ie.gmit.sw.documents.Document;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class JaacardIndex implements SimilarityIndex {

    private final List<Document> documents;
    private final ExecutorService executor;
    private final int k;
//    private final Shinglizer shinglizer;

    public JaacardIndex(final List<Document> documents) {
        this.documents = Collections.unmodifiableList(documents);
        k = 150; // TODO remove hard coded value.
//        shingles = new ConcurrentLinkedQueue<>();
//        shingleHashes = new ConcurrentHashMap<>();
        executor = Executors.newFixedThreadPool(8);
//        this.shinglizer = new WordShinglizer()
    }

    private Set<Integer> generateHashes() {
        Random rnd = new Random();
        Set<Integer> hashes = new TreeSet<>();
        while (hashes.size() < k) {
            hashes.add(rnd.nextInt());
        }
        return hashes;
    }


    private List<Shingle> getShingleListFromFuture(final Future<List<Shingle>> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    private MinHashResult getMinHashResultFromFuture(final Future<MinHashResult> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<List<Shingle>> shinglize() {
        return documents
                .stream()
                .map(doc -> executor.submit(new WordShinglizer(doc, 10)))
                .map(this::getShingleListFromFuture)
                .collect(Collectors.toList());
    }

    @Override
    public double computeIndex() {

        List<List<Shingle>> shingles = shinglize();

        Set<Integer> hashes = generateHashes();

        List<Future<MinHashResult>> minHashes = new ArrayList<>();
        for (List<Shingle> shinglesList : shingles) {
            for (Integer hash : hashes) {
                minHashes.add(executor.submit(new MinHash(hash, shinglesList, shinglesList.get(0).getDocId())));
            }
        }

        Map<Integer, Set<Integer>> minHashResults = new HashMap<>();
        for (Document doc : documents) {
            minHashResults.put(doc.getId(), new HashSet<>());
        }

        minHashes.stream()
                .map(this::getMinHashResultFromFuture)
                .forEach(result -> {
                    Set<Integer> results = minHashResults.get(result.getDocId());
                    results.add(result.getResult());
                });

        executor.shutdown();

        Set<Integer> finalResults = new HashSet<>(minHashResults.get(1));
        for (Set set : minHashResults.values()) {
            finalResults.retainAll(set);
            System.out.println(set.size());
        }

        System.out.println(finalResults);
        return (double) finalResults.size() / k;

    }
}
