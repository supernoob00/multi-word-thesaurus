package com.somerdin.thesaurus.structures;

import java.util.Iterator;
import java.util.Objects;

public class UnorderedPair<E> implements Iterable<E>{
    private final E e1;
    private final E e2;

    public UnorderedPair(E e1, E e2) {
        if (e1 == null || e2 == null) {
            throw new IllegalArgumentException("Pair element cannot be null");
        }
        this.e1 = e1;
        this.e2 = e2;
    }

    public E getFirst() {
        return e1;
    }

    public E getSecond() {
        return e2;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            private int i = 0;
            private final E[] elements = (E[]) new Object[] {e1, e2};

            @Override
            public boolean hasNext() {
                return i < 2;
            }

            @Override
            public E next() {
                return elements[i++];
            }
        };
    }

    @Override
    public int hashCode() {
        return e1.hashCode() + e2.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnorderedPair<?> that = (UnorderedPair<?>) o;
        return (Objects.equals(e1, that.e1) && Objects.equals(e2, that.e2))
                || (Objects.equals(e1, that.e2) && Objects.equals(e2, that.e1));
    }
}
