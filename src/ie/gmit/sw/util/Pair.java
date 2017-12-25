package ie.gmit.sw.util;

/**
 * The type Pair.
 *
 * @param <T1> the type parameter
 * @param <T2> the type parameter
 */
public class Pair<T1, T2> {
    private final T1 t1;
    private final T2 t2;

    /**
     * Instantiates a new Pair.
     *
     * @param t1 the t 1
     * @param t2 the t 2
     */
    public Pair(final T1 t1, final T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    /**
     * First t 1.
     *
     * @return the t 1
     */
    public T1 first() {
        return t1;
    }

    /**
     * Second t 2.
     *
     * @return the t 2
     */
    public T2 second() {
        return t2;
    }
}
