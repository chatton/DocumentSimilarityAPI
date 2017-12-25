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
import ie.gmit.sw.util.CollectionUtils;
import ie.gmit.sw.util.Pair;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * The type Jaacard index.
 *
 * @author Cian Hatton
 */
public class JaacardIndex implements SimilarityIndex {

    private final Shinglizer shinglizer;
    private final int numHashes;
    private ExecutorService executor;

    /**
     * Instantiates a new Jaacard index.
     *
     * @param shinglizer the shinglizer which will be used to break documents into shingles.
     * @param numHashes  the number of hashes that will be used in the min hash algorithm.
     */
    public JaacardIndex(final Shinglizer shinglizer, final int numHashes) {
        this.numHashes = numHashes;
        this.shinglizer = shinglizer;
    }

    /**
     * This method generates a set of numHashes random numbers.
     *
     * @return a Set of integers to be used when calculating the min hashes.
     */
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

    /**
     * This method takes a list of documents and breaks up each document
     * into a list of shingles and creates a ShinglizeResult. One document per thread.
     *
     * @param documents the documents to be broken up into shingles.
     * @return a List of ShinglizeResults
     */
    private List<ShinglizeResult> shinglize(final List<Document> documents) {
        final List<Future<ShinglizeResult>> futures = documents.stream()
                .map(doc -> executor.submit(() -> shinglizer.shinglize(doc))) // shinglize one document per thread
                .collect(ImmutableList.toImmutableList());

        return futures.stream()
                .map(this::getShinglizeResult) // block here on future#get
                .collect(ImmutableList.toImmutableList()); // return the final results as a list.
    }

    /**
     * This method takes a list of ShinglizeResults and a Set of hashes and creates a list
     * of MinHashResult futures which will be used to calculate the JaacardIndex.
     *
     * @param shinglizeResults the list of ShinglizeResults that are used to compute MinHashResults
     * @param hashes           the hashes used in the minhash algorithm.
     * @return a list of MinHashResult futures.
     */
    private List<Future<MinHashResult>> getMinHashFutures(final List<ShinglizeResult> shinglizeResults, final Set<Integer> hashes) {
        return shinglizeResults.stream()
                .flatMap(shingleResult -> hashes.stream().map(hash -> new Pair<>(shingleResult, hash)))
                .map(pair -> executor.submit(() -> makeMinHash(pair).calculate()))
                .collect(ImmutableList.toImmutableList());
    }

    /**
     * This is a helper conversion method which takes a Pair of ShinglizeResult
     * and Integer and creates a MinHash.
     *
     * @param pair
     * @return MinHash created from the pair.
     */
    private MinHash makeMinHash(Pair<ShinglizeResult, Integer> pair) {
        return new MinHash(pair.second(), pair.first().get(), pair.first().getDocument());
    }

    /**
     * This method takes the list of MinHashResult futures and builds up a map of the results
     * with the Document as a key and a collection of integers as the value. The collection of
     * integers are the min hash results.
     *
     * @param futures the MinHashFutures created by {@link ie.gmit.sw.similarity.indexes.JaacardIndex#getMinHashFutures(List, Set)}
     * @return A map of Document to Collection of integers. Where the integers are the min hash results for the corresponding document.
     */
    private Map<Document, Collection<Integer>> buildMinHashFutureResults(final List<Future<MinHashResult>> futures) {
        final Map<Document, Collection<Integer>> map = futures.stream()
                .map(this::getMinHashResult) // blocking call to future#get
                .collect(Collectors.toMap(
                        MinHashResult::getDocument, // key mapper - key will be the document itself.
                        result -> ImmutableSet.of(result.get()), // value mapper - set of single minhash result value
                        this::retainAll // on a collision of keys, retainAll the values into a single set.
                ));

        return ImmutableMap.copyOf(map);
    }

    private <T> Collection retainAll(Collection<T> col1, Collection<T> col2) {
        return CollectionUtils.retainAll(Arrays.asList(col1, col2));
    }

    /**
     * This method takes the result of all the shinglized documents
     * and returns a map with the results of the min hash algorithm per document.
     *
     * @param shinglizeResults the result of a call to {@link ie.gmit.sw.similarity.indexes.JaacardIndex#shinglize(List)}
     * @return The map of document to collection of min hash results.
     */
    private Map<Document, Collection<Integer>> calculateMinHashResults(final List<ShinglizeResult> shinglizeResults) {
        final Set<Integer> hashes = generateHashes();
        final List<Future<MinHashResult>> minHashFutures = getMinHashFutures(shinglizeResults, hashes);
        return buildMinHashFutureResults(minHashFutures);
    }

    /**
     * @param documents the documents
     * @return
     */
    @Override
    public double computeIndex(final List<Document> documents) {
        if (documents.size() < 2) {
            throw new IllegalArgumentException("Must provide at least 2 documents to compare.");
        }

        executor = Executors.newCachedThreadPool();
        final List<ShinglizeResult> shingleResults = shinglize(documents);
        final Map<Document, Collection<Integer>> minHashResults = calculateMinHashResults(shingleResults);
        final Collection<Integer> finalResults = CollectionUtils.retainAll(ImmutableList.copyOf(minHashResults.values()));
        executor.shutdown();

        final double size = (double) finalResults.size();
        return size / ((numHashes * documents.size()) - size);
    }
}
