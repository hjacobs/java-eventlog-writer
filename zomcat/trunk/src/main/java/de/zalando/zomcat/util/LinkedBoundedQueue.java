package de.zalando.zomcat.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * basic queue for storing the last x elements. It is backed up by a <code>LinkedList</code>. If the optional capacity
 * is exceeded, the oldest element is removed. The first element is the newest element.
 *
 * <p/>pay attention that the implementation is synchronized in {@link #add(Object) add(Object)}
 *
 * @param   <E>
 *
 * @author  fbrick
 */
public class LinkedBoundedQueue<E> {
    private static final long serialVersionUID = 2890178068529481219L;

    private final LinkedList<E> elements = new LinkedList<E>();
    private Integer capacity = null;
    private final transient Object guard = new Object();

    public LinkedBoundedQueue(final Integer capacity) {
        this.capacity = capacity;
    }

    /**
     * @param  element  the element to be added at first position
     */
    public void add(final E element) {
        synchronized (guard) {
            elements.addFirst(element);

            if ((capacity != null) && (elements.size() > capacity)) {
                elements.removeLast();
            }
        }
    }

    public List<E> view() {
        final List<E> result = new LinkedList<>();

        synchronized (guard) {

            final Iterator<E> iter = elements.iterator();
            while (iter.hasNext()) {
                result.add(iter.next());
            }
        }

        return Collections.unmodifiableList(result);
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("LinkedBoundedQueue [capacity=");
        builder.append(capacity);
        builder.append(", elements=");
        builder.append(view());
        builder.append("]");
        return builder.toString();
    }
}
