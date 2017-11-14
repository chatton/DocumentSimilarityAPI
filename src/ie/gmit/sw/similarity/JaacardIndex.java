package ie.gmit.sw.similarity;

import ie.gmit.sw.documents.Document;
import ie.gmit.sw.MinHash;

import java.util.*;

public class JaacardIndex implements SimilarityIndex {

    private final Document doc1;
    private final Document doc2;

    public JaacardIndex(final Document doc1, final Document doc2) {
        this.doc1 = doc1;
        this.doc2 = doc2;
    }

    @Override
    public double computeIndex() {

        Random rnd = new Random();
        Set<Integer> hashes = new TreeSet<>();

        int k = 2000;
        for (int i = 0; i < k; i++) {
            hashes.add(rnd.nextInt());
        }

        List<Integer> shingles = new MinHash(doc1, hashes).calculateShingles();
        List<Integer> shingles2 = new MinHash(doc2, hashes).calculateShingles();

        Set<Integer> s1 = new HashSet<>(shingles);
        Set<Integer> s2 = new HashSet<>(shingles2);

        s1.retainAll(s2);

        return (double)s1.size() / k;
    }
}
