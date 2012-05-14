package de.zalando.util.web.urlmapping;

import java.lang.reflect.Array;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

import com.google.common.collect.Iterators;

/**
 * Unfortunately, there is no Collections.emptyQueue(). This is a workaround, an empty well-behaved immutable queue
 * implemented as an enum singleton.
 *
 * @author  Sean Patrick Floyd (sean.floyd@zalando.de)
 */
enum NoOpQueue implements Queue<Object> {
//J-
    INSTANCE {

        @Override public boolean add(final Object e) { return false; }

        @Override public boolean offer(final Object e) { return false; }

        @Override public Object remove() { return null; }

        @Override public Object poll() { return null; }

        @Override public Object element() { return null; }

        @Override public Object peek() { return null; }

        @Override public int size() { return 0; }

        @Override public boolean isEmpty() { return true; }

        @Override public boolean contains(final Object o) { return false; }

        @Override public Iterator<Object> iterator() { return Iterators.emptyIterator(); }

        @Override public Object[] toArray() { return EMPTY_ARRAY; }

        @Override public boolean remove(final Object o) { return false; }

        @Override public boolean containsAll(final Collection<?> c) { return false; }

        @Override public boolean addAll(final Collection<? extends Object> c) { return false; }

        @Override public boolean removeAll(final Collection<?> c) { return false; }

        @Override public boolean retainAll(final Collection<?> c) { return false; }

        @Override public void clear() { }

        @Override
        public String toString(){ return "NoOpQueue[]"; }

        @Override
        public <T> T[] toArray(final T[] a) {
            final T[] arr;
            if (a.length == 0) { arr = a; } else {
                @SuppressWarnings("unchecked") // this is actually safe according to the JLS
                final T[] newArray = (T[]) Array.newInstance(a.getClass().getComponentType(), 0);
                arr = newArray;
            }
            return arr;
        }

    };
//J+

    public static <E> Queue<E> get() {
        @SuppressWarnings({ "unchecked", "rawtypes" })
        final Queue<E> queue = (Queue) INSTANCE;
        return queue;
    }

    private static final Object[] EMPTY_ARRAY = {};

}
