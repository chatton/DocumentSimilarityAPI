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
        String text = document.getText().toLowerCase();
        StringBuilder sb = new StringBuilder();
        String[] words = text.split(pattern);
        int pos = 0;
        List<Shingle> shingles = new ArrayList<>();
        while (pos < words.length) {
            for (int i = 0; i < numWords; i++) {
                if (pos == words.length) {
                    break;
                }
                sb.append(words[pos]).append(" ");
                pos++;
            } // for

            Shingle shingle = new Shingle(sb.toString(), document.getId());
            shingles.add(shingle);
            sb = new StringBuilder();
        } // while
        return new ShinglizeResult(document, shingles);
    }
}
