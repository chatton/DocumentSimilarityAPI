package ie.gmit.sw.api.similarity.indexes;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import ie.gmit.sw.api.documents.Document;
import ie.gmit.sw.api.similarity.indexes.generator.HashGenerator;
import ie.gmit.sw.api.similarity.minhash.MinHash;
import ie.gmit.sw.api.similarity.minhash.MinHashResult;
import ie.gmit.sw.api.similarity.results.FutureResult;
import ie.gmit.sw.api.similarity.shingles.ShinglizeResult;
import ie.gmit.sw.api.similarity.shingles.Shinglizer;
import ie.gmit.sw.api.util.Pair;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static ie.gmit.sw.api.util.CollectionUtils.intersection;


/**
 * The type Jaacard index. Implements {@link SimilarityIndex}. Can compute the
 * Jaacard Index of 2 or more provided {@link Document}s.
 *
 * @author Cian Hatton
 */
public class JaacardIndex implements SimilarityIndex {

    private final Shinglizer shinglizer;
    private final HashGenerator generator;
    private ExecutorService executor;

    /**
     * Instantiates a new JaacardIndex.
     *
     * @param shinglizer the shinglizer which will be used to break documents into shingles.
     * @param numHashes  the number of hashes that will be used in the min hash algorithm.
     */
    public JaacardIndex(final Shinglizer shinglizer, final int numHashes) {
        this(shinglizer, new HashGenerator(numHashes));
    }

    public JaacardIndex(final Shinglizer shinglizer, final HashGenerator generator) {
        this.shinglizer = shinglizer;
        this.generator = generator;
    }


    /**
     * Helper method to extract a value from a {@link FutureResult} subclass.
     *
     * @param future the future to get the result from.
     * @param <T>    a subclass of {@link FutureResult}
     * @return the result of a call to future#get or null
     */
    @SuppressWarnings("unchecked")
    private <T extends FutureResult> T getResult(final Future future) {
        try {
            return (T) future.get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("There was an error calling Future#get. Error: " + e.getMessage());
            return null;
        }
    }

    private ShinglizeResult getShinglizeResult(final Future<ShinglizeResult> future) {
        return getResult(future);
    }

    private MinHashResult getMinHashResult(final Future<MinHashResult> future) {
        return getResult(future);
    }

    /**
     * Takes a List of documents and breaks up each document into
     * a List of shingles in a separate thread. Each is converted into
     * a {@link ShinglizeResult}
     *
     * @param documents the documents to be broken up into shingles.
     * @return a List of {@link ShinglizeResult}s
     */
    private List<ShinglizeResult> shinglize(final List<Document> documents) {
        assert !executor.isShutdown();
        final List<Future<ShinglizeResult>> futures = documents.stream()
                .map(doc -> executor.submit(() -> shinglizer.shinglize(doc))) // shinglize one document per thread
                .collect(toImmutableList());

        return futures.stream()
                .map(this::getShinglizeResult) // block here on future#get
                .filter(Objects::nonNull)
                .collect(toImmutableList()); // return the final results as a list.
    }

    /**
     * Takes a list of {@link ShinglizeResult}s and a Set of hashes and creates a list
     * of {@link MinHashResult} futures which are used to calculate the Jaacard Index.
     *
     * @param shinglizeResults the list of {@link ShinglizeResult}s that are used to compute {@link MinHashResult}s
     * @param hashes           the hashes used in the minhash algorithm.
     * @return a list of MinHashResult futures.
     */
    private List<Future<MinHashResult>> startMinHashTasks(final List<ShinglizeResult> shinglizeResults,
                                                          final Collection<Integer> hashes) {
        assert !executor.isShutdown();
        return shinglizeResults.stream()
                .flatMap(shingleResult -> hashes.stream().map(hash -> new Pair<>(shingleResult, hash)))
                .map(pair -> executor.submit(() -> makeMinHash(pair).calculate()))
                .collect(toImmutableList());
    }

    /**
     * Helper construction method which takes a Pair of {@link ShinglizeResult}
     * and Integer and creates a {@link MinHash}.
     *
     * @param pair of type {@link ShinglizeResult} and Integer
     * @return MinHash created from the pair.
     */
    private MinHash makeMinHash(final Pair<ShinglizeResult, Integer> pair) {
        return new MinHash(pair.second(), pair.first().get(), pair.first().document());
    }

    /**
     * This method takes the list of {@link MinHashResult} futures and builds up a map of the results
     * with the {@link Document} Integer ID as the key and a collection of integers as the value. The collection of
     * integers are the min hash results for that {@link Document}.
     *
     * @param futures the MinHashFutures created by {@link #startMinHashTasks(List, Collection)}
     * @return A map of Document to Collection of integers. Where the integers are the min hash results for the corresponding document.
     */
    private Map<Integer, Collection<Integer>> constructMinHashResultsMap(final List<Future<MinHashResult>> futures) {
        final Map<Integer, Collection<Integer>> results = new HashMap<>();
        for (final Future<MinHashResult> future : futures) {
            final MinHashResult result = getMinHashResult(future);
            if (result == null) {
                continue;
            }
            final int docId = result.document().id();
            results.putIfAbsent(docId, new HashSet<>());
            final Collection<Integer> col = results.get(docId);
            col.add(result.get());
        }
        return ImmutableMap.copyOf(results);
    }

    /**
     * This method takes the result of all the shinglized {@link Document}s
     * and returns a map with the results of the min hash algorithm per {@link Document}.
     *
     * @param shinglizeResults the result of a call to {@link #shinglize(List)}
     * @return The map of document to collection of min hash results.
     */
    private Map<Integer, Collection<Integer>> calculateMinHashResults(final List<ShinglizeResult> shinglizeResults) {
        final Collection<Integer> hashes = generator.generate();
        final List<Future<MinHashResult>> minHashFutures = startMinHashTasks(shinglizeResults, hashes);
        assert minHashFutures.size() == hashes.size() * shinglizeResults.size();
        return constructMinHashResultsMap(minHashFutures);
    }

    /**
     * Computes the Jaacard Index of 2 or more {@link Document}s. The Jaacard Index
     * will be between 0 and 1 and is a measure of {@link Document} similarity.
     *
     * @param documents the documents to compute the Jaacard index of.
     * @return the Jaacard index of all the provided documents.
     * @throws IllegalArgumentException if fewer than 2 documents are provided.
     */
    @Override
    public double computeIndex(final List<Document> documents) {
        if (documents.size() < 2) {
            throw new IllegalArgumentException("Must provide at least 2 documents to compare.");
        }

        executor = Executors.newCachedThreadPool();
        final List<ShinglizeResult> shingleResults = shinglize(documents);
        final Map<Integer, Collection<Integer>> minHashResults = calculateMinHashResults(shingleResults);
        final Collection<Integer> finalResults = intersection(ImmutableList.copyOf(minHashResults.values()));
        executor.shutdown();


        final double size = (double) finalResults.size();
        return size / ((generator.getNumHashes() * documents.size()) - size);
    }
}
