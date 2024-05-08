package data_structures;

import java.util.ArrayList;

/**
 * The <code>Sorted</code> interface should be implemented by the
 * data-structures of the assignment.
 *
 * @param <T>
 *            the type of the elements to be sorted.
 */
public interface Sorted<T extends Comparable<T>> {

    /**
     * Adds the specified element to the data structure. Duplicate elements are
     * allowed, and should be represented explicitly.
     *
     * @param t
     *            the element to be added.
     */
    public void add(T t);

    /**
     * Removes the specified element from the data structure. If the element
     * occurs more than once in the data structure, only one instance of it is
     * removed. If the element is not present, this is silently ignored.
     *
     * @param t
     *            the element to be removed.
     */
    public void remove(T t);

    /**
     * Returns an <code>ArrayList</code> containing all elements in the data
     * structure, in the order implied by the data structure. Using an
     * <code>ArrayList</code> may seem over{@literal -}specified, but an
     * <code>ArrayList</code> has a specific <code>toString()</code> method that
     * is used in testing your program, which the more general <code>List</code>
     * class does not have.
     *
     * @return an <code>ArrayList</code> containing the elements.
     */
    public ArrayList<T> toArrayList();
}
