package ie.gmit.sw.util;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Collection Utility class contains generic methods
 * that operate on Collections.
 *
 * @author Cian Hatton
 */
public class CollectionUtils {
    /**
     * Takes a List of collections and returns a new collection
     * which is the intersection of all provided collections.
     *
     * @param cols the List of collections.
     * @param <T>  the type of the collections in the input list.
     * @return the new collection which is the intersection of cols
     */
    public static <T> Collection<T> intersection(final List<Collection<T>> cols) {
        if (cols.isEmpty()) {
            return ImmutableSet.of();
        }
        final Collection<T> set = new HashSet<>(cols.get(0));
        for (int i = 1; i < cols.size(); i++) {
            set.retainAll(cols.get(i));
        }
        return ImmutableSet.copyOf(set);
    }
}
