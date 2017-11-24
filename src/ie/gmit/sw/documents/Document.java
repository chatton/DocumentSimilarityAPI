package ie.gmit.sw.documents;

import java.util.Arrays;
import java.util.List;

public interface Document {

    String text();

    default List<String> words() {
        return Arrays.asList(text().split("[ -.,;:\\-]+"));
    }
}
