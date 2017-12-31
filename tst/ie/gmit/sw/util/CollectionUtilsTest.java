package ie.gmit.sw.util;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

public class CollectionUtilsTest {

    @Test
    public void testIntersection() {
        final Collection<Integer> col1 = ImmutableSet.of(1, 2, 3, 4, 10, 11, 12);
        final Collection<Integer> col2 = ImmutableSet.of(3, 4, 5, 6, 7, 10, 11);
        final Collection<Integer> col3 = ImmutableSet.of(10, 11, 12, 13);
        final Collection<Integer> expected = ImmutableSet.of(10, 11);
        assertEquals(expected, CollectionUtils.intersection(Arrays.asList(col1, col2, col3)));
    }
}