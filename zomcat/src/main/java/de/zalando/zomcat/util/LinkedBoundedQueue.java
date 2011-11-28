package de.zalando.zomcat.util;

import java.util.Iterator;
import java.util.LinkedList;

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

    private final LinkedList<E> elements = new LinkedList<E>();
    private Integer capacity = null;
    private final Object guard = new Object();

    public LinkedBoundedQueue(final Integer capacity) {
        this.capacity = capacity;
    }

    /**
     * clear queue.
     */
    public void clear() {
        elements.clear();
    }

    /**
     * @return  actual size of queue
     */
    public int size() {
        return elements.size();
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

    /**
     * @return  the first (newest) element
     */
    public E getFirst() {
        return elements.getFirst();
    }

    /**
     * @return  the last (oldest) element
     */
    public E getLast() {
        return elements.getLast();
    }

    /**
     * @return  iterator for elements. It will not be protected against modifications in order to get better
     *          performance. If some elements are removed in this iteration, the queue will be modified, too!
     */
    public Iterator<E> iterator() {
        return elements.iterator();
    }

    /**
     * @return  flag if queue is empty
     */
    public boolean isEmpty() {
        return elements.isEmpty();
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
        builder.append(elements);
        builder.append("]");
        return builder.toString();
    }
}
