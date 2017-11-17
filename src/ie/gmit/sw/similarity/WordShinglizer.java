package ie.gmit.sw.similarity;

import ie.gmit.sw.documents.Document;

import java.util.ArrayList;
import java.util.List;

public class WordShinglizer implements Shinglizer {

    private final Document document;
    private final int numWords;

    public WordShinglizer(final Document document, final int numWords) {
        this.document = document;
        this.numWords = numWords;
    }

    @Override
    public List<Shingle> shinglize() {
        String text = document.getText().toLowerCase();
        StringBuilder sb = new StringBuilder();
        String[] words = text.split("[ -.,;:\\-]+");
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
        System.out.println(shingles.size());
        return shingles;
    }

    @Override
    public List<Shingle> call() throws Exception {
        return shinglize();
    }
}
