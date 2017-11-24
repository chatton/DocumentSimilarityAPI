package ie.gmit.sw.similarity.shingles;

import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import ie.gmit.sw.documents.Document;

import java.util.List;

import static com.google.common.collect.ImmutableList.toImmutableList;

public class WordShinglizer implements Shinglizer {

    private final int numWords;
    private final String pattern;

    public WordShinglizer(final int numWords) {
        this(numWords, "[ -.,;:\\-]+");
    }

    public WordShinglizer(final int numWords, final String pattern) {
        this.numWords = numWords;
        this.pattern = pattern;
    }

    @Override
    public ShinglizeResult shinglize(final Document document) {
        final List<String> words = document.words().stream()
                .map(String::toLowerCase)
                .collect(toImmutableList());

        final List<Shingle> shingles = Streams.stream(
                Iterables.partition(words, numWords))
                .map(Shingle::new)
                .collect(toImmutableList());

        return new ShinglizeResult(document, shingles);
    }
}
