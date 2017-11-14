package ie.gmit.sw;

import ie.gmit.sw.documents.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MinHash {
    private final Document doc;
    private final Set<Integer> hashes;

    public MinHash(Document doc, Set<Integer> hashes) {
        this.doc = doc;
        this.hashes = new HashSet<>(hashes);
    }

    public List<Integer> calculateShingles() {
        String text = doc.getText();
        String[] words = text.split(" ");
        List<Integer> shingles = new ArrayList<>();
        for (Integer hash : hashes) {
            int min = Integer.MAX_VALUE;
            for (String word : words) {
                int minHash = word.hashCode() ^ hash;
                if (minHash < min) {
                    min = minHash;
                }
            }
            shingles.add(min);
        }
        return shingles;
    }
}
