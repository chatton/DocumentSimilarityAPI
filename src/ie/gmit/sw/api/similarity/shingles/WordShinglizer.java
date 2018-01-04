package ie.gmit.sw.api.similarity.shingles;

import com.google.common.collect.Streams;
import ie.gmit.sw.api.documents.Document;

import java.util.Collection;
import java.util.List;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Iterables.partition;
import static java.util.Arrays.asList;

/**
 * The type Word shinglizer. An implementation of {@link Shinglizer} that breaks
 * up a {@link Document} into multiple {@link Shingle}s.
 *
 * @author Cian Hatton
 */
public class WordShinglizer implements Shinglizer {

    private final int numWords;
    private final String pattern;


    /**
     * Instantiates a new Word shinglizer.
     *
     * @param numWords the num words
     */
    public WordShinglizer(final int numWords) {
        this(numWords, "[ -.,;:\\-\"\\?\n]+");
    }

    /**
     * Instantiates a new Word shinglizer.
     *
     * @param numWords The number of words in one shingle.
     * @param pattern  The regex pattern that splits what a word is in the context of a shingle.
     */
    public WordShinglizer(final int numWords, final String pattern) {
        this.numWords = numWords;
        this.pattern = pattern;
    }

    /**
     * Breaks up the {@link Document} into {@link Shingle}s of size 'numWords' and
     * returns a {@link ShinglizeResult} containing these {@link Shingle}s and a reference
     * to the {@link Document}.
     *
     * @param document The {@link Document} that will be broken up into {@link Shingle}s.
     * @return A {@link ShinglizeResult} that consists of the {@link Document} and a list of {@link Shingle}s.
     */
    @Override
    public ShinglizeResult shinglize(final Document document) {
        final String[] words = document.text().split(pattern);

        final List<Shingle> shingles = Streams.stream(
                partition(asList(words), numWords)) // Stream<List<String>> of size numWords at a time.
                .map(this::makeLowerCase) // make every word lower case to make shingles case insensitive
                .map(Shingle::new) // construct a shingle from the lower case collection.
                .collect(toImmutableList());

        return new ShinglizeResult(document, shingles);
    }

    /**
     * Takes a collection of strings and returns a new collection
     * with every string changed to lower case.
     *
     * @param words the collection of words to be changed.
     * @return the new collection with lower case Strings.
     */
    private Collection<String> makeLowerCase(final Collection<String> words) {
        return words.stream().map(String::toLowerCase).collect(toImmutableList());
    }

}
