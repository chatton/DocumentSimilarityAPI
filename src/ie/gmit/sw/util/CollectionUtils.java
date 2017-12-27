package ie.gmit.sw.util;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * The type Collection utils.
 */
public class CollectionUtils {

    // prevent instantiation
    private CollectionUtils() {}

    /**
     * Takes a collection of strings and returns a new collection
     * with every string changed to lower case.
     *
     * @param words the collection of words to be changed.
     * @return the new collection with all lower case Strings.
     */
    public static Collection<String> makeLowerCase(final Collection<String> words) {
        return words.stream().map(String::toLowerCase).collect(toImmutableList());
    }

    /**
     * This method takes a list of collections, and returns a new collection
     * that contains the intersection of all the collections in the list.
     *
     * @param <T>  the type parameter
     * @param cols the List of collections.
     * @return the new collection which is the intersection of cols
     */
    public static <T> Collection<T> retainAll(final List<Collection<T>> cols) {
        if (cols.isEmpty()) {
            return ImmutableSet.of();
        }
        final Set<T> set = new HashSet<>(cols.get(0));
        for (int i = 1; i < cols.size(); i++) {
            set.retainAll(cols.get(i));
        }
        return ImmutableSet.copyOf(set);
    }

    public static <T> Collection<T> merge(final Collection<Collection<T>> cols){
        final Collection<T> col = new HashSet<>();
        cols.forEach(col::addAll);
        return ImmutableSet.copyOf(col);
    }
}
