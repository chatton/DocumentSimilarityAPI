package ie.gmit.sw.similarity;

import ie.gmit.sw.Document;

import java.util.*;

public class BasicJaacard implements SimilarityIndex {

    private final Document doc1;
    private final Document doc2;

    public BasicJaacard(final Document doc1, final Document doc2) {
        this.doc1 = doc1;
        this.doc2 = doc2;
    }

    @Override
    public double computeIndex() {
        String[] text1 = doc1.getText().split(" ");
        String[] text2 = doc2.getText().split(" ");

        Random rnd = new Random();

        Set<Integer> hashes = new TreeSet<>();

        List<Integer> shingles = new ArrayList<>();
        List<Integer> shingles2 = new ArrayList<>();
        int k = 20;
        for (int i = 0; i < k; i++) {
            hashes.add(rnd.nextInt());
        }

        for (Integer hash : hashes) {
            int min = Integer.MAX_VALUE;
            for (String word : text1) {
                int minHash = word.hashCode() ^ hash;
                if (minHash < min) {
                    min = minHash;
                }
            }
            shingles.add(min);
        }

        for (Integer hash : hashes) {
            int min = Integer.MAX_VALUE;
            for (String word : text2) {
                int minHash = word.hashCode() ^ hash;
                if (minHash < min) {
                    min = minHash;
                }
            }
            shingles2.add(min);
        }

        System.out.println(shingles);
        System.out.println(shingles2);

        Set<Integer> s1 = new HashSet<>(shingles);
        Set<Integer> s2 = new HashSet<>(shingles2);

        int abs1 = s1.size();
        int abs2 = s2.size();
        s1.retainAll(s2);

        return (double)s1.size() / k;

        /*
        int s1Abs = text1.length; // |A|
        int s2Abs = text2.length; // |B|

        Set<String> set1 = new TreeSet<>(Arrays.asList(text1));
        Set<String> set2 = new TreeSet<>(Arrays.asList(text2));

        set1.retainAll(set2); // S1 intersects S2

        int intersectionAbs = set1.size();

        return intersectionAbs / (double)(s1Abs + s2Abs - intersectionAbs);
        */
        //return 0;
    }
}
