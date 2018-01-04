package ie.gmit.sw.api.util;

/**
 * The type Pair. A Grouping of 2 types together in a single object.
 *
 * @param <T1> the type parameter
 * @param <T2> the type parameter
 * @author Cian Hatton
 */
public class Pair<T1, T2> {
    private final T1 t1;
    private final T2 t2;

    /**
     * Instantiates a new Pair.
     *
     * @param t1 the first type.
     * @param t2 the second type.
     */
    public Pair(final T1 t1, final T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    /**
     * First type.
     *
     * @return the first type.
     */
    public T1 first() {
        return t1;
    }

    /**
     * Second type.
     *
     * @return the second type.
     */
    public T2 second() {
        return t2;
    }
}
