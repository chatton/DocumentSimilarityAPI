package ie.gmit.sw.util;

public class Pair<T1, T2> {
    private final T1 t1;
    private final T2 t2;

    public Pair(final T1 t1, final T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    public T1 first() {
        return t1;
    }

    public T2 second() {
        return t2;
    }
}
