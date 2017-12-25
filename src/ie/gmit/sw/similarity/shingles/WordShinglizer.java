package ie.gmit.sw.similarity.shingles;

import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import ie.gmit.sw.documents.Document;
import ie.gmit.sw.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * The type Word shinglizer.
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
        this(numWords, "[ -.,;:\\-]+");
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
     * @param document The document that will be broken up into shingles.
     * @return A ShinglizeResult that consists of the document and a list of shingles.
     */
    @Override
    public ShinglizeResult shinglize(final Document document) {
        final String[] words = document.text().split(pattern);

        final List<Shingle> shingles = Streams.stream(
                Iterables.partition(Arrays.asList(words), numWords)) // Stream<List<String>> of size numWords at a time.
                .map(CollectionUtils::makeLowerCase) // make every word lower case to make shingles case insensitive
                .map(Shingle::new) // construct a shingle from the lower case list.
                .collect(toImmutableList());

        return new ShinglizeResult(document, shingles);
    }

}
