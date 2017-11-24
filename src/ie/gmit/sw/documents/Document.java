package ie.gmit.sw.documents;

import com.google.common.collect.ImmutableList;

import java.util.List;

public interface Document {

    String text();

    default List<String> words() {
        final String[] words = text().split("[ -.,;:\\-]+");
        return ImmutableList.copyOf(words);
    }
}
