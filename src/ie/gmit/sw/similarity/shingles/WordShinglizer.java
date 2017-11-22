package ie.gmit.sw.similarity.shingles;

import ie.gmit.sw.documents.Document;

import java.util.ArrayList;
import java.util.List;

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
    public ShinglizeResult shinglize(Document document) {

        final String text = document.text().toLowerCase();
        final String[] words = text.split(pattern);

        int pos = 0;
        final StringBuilder sb = new StringBuilder();
        final List<Shingle> shingles = new ArrayList<>();
        while (pos < words.length) {
            for (int i = 0; i < numWords; i++) {
                if (pos == words.length) {
                    break;
                }
                sb.append(words[pos]).append(" ");
                pos++;
            }

            final Shingle shingle = new Shingle(sb.toString());
            shingles.add(shingle);
            sb.setLength(0); // clear the string builder.
        }

        return new ShinglizeResult(document, shingles);
    }
}
