package ie.gmit.sw.similarity.shingles;

import com.google.common.collect.ImmutableList;
import ie.gmit.sw.documents.Document;
import ie.gmit.sw.documents.FileDocument;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class WordShinglizerTest {


    @Test
    public void shinglesAreSuccessfullyCreated() throws IOException {

        final Document testDoc = new FileDocument("test_res/poem.txt");
        final Shinglizer shinglizer = new WordShinglizer(5);
        final ShinglizeResult result = shinglizer.shinglize(testDoc);
        final List<Shingle> shingles = result.get();

        final List<Shingle> expectedShingles = ImmutableList.of(
                new Shingle(ImmutableList.of("north", "of", "53", "a", "magic")),
                new Shingle(ImmutableList.of("phrase", "spoken", "mumbled", "or", "thought")),
                new Shingle(ImmutableList.of("inwardly", "by", "thousands", "of", "souls")),
                new Shingle(ImmutableList.of("venturing", "northward", "an", "imaginary", "line")),
                new Shingle(ImmutableList.of("shown", "only", "on", "maps", "and"))
        );

        assertEquals(expectedShingles, shingles.subList(0, 5));
    }
}