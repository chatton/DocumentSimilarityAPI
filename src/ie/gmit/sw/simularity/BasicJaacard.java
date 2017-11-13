package ie.gmit.sw.simularity;

import ie.gmit.sw.Document;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

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
        int s1Abs = text1.length; // |A|
        int s2Abs = text2.length; // |B|

        Set<String> set1 = new TreeSet<>(Arrays.asList(text1));
        Set<String> set2 = new TreeSet<>(Arrays.asList(text2));

        set1.retainAll(set2); // S1 intersects S2

        int intersectionAbs = set1.size();

        return intersectionAbs / (double)(s1Abs + s2Abs - intersectionAbs);
    }
}
