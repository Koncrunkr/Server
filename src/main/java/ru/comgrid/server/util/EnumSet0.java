/*
 * Copyright (c) 2003, 2019, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package ru.comgrid.server.util;


import java.io.Serial;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;

/*
 * Copied from java.util.EnumSet
 * I had to access elements long
 */

/**
 * A specialized {@link java.util.Set} implementation for use with enum types.  All of
 * the elements in an enum set must come from a single enum type that is
 * specified, explicitly or implicitly, when the set is created.  Enum sets
 * are represented internally as bit vectors.  This representation is
 * extremely compact and efficient. The space and time performance of this
 * class should be good enough to allow its use as a high-quality, typesafe
 * alternative to traditional {@code int}-based "bit flags."  Even bulk
 * operations (such as {@code containsAll} and {@code retainAll}) should
 * run very quickly if their argument is also an enum set.
 *
 * <p>The iterator returned by the {@code iterator} method traverses the
 * elements in their <i>natural order</i> (the order in which the enum
 * constants are declared).  The returned iterator is <i>weakly
 * consistent</i>: it will never throw {@link java.util.ConcurrentModificationException}
 * and it may or may not show the effects of any modifications to the set that
 * occur while the iteration is in progress.
 *
 * <p>Null elements are not permitted.  Attempts to insert a null element
 * will throw {@link NullPointerException}.  Attempts to test for the
 * presence of a null element or to remove one will, however, function
 * properly.
 *
 * <P>Like most collection implementations, {@code EnumSet} is not
 * synchronized.  If multiple threads access an enum set concurrently, and at
 * least one of the threads modifies the set, it should be synchronized
 * externally.  This is typically accomplished by synchronizing on some
 * object that naturally encapsulates the enum set.  If no such object exists,
 * the set should be "wrapped" using the {@link java.util.Collections#synchronizedSet}
 * method.  This is best done at creation time, to prevent accidental
 * unsynchronized access:
 *
 * <pre>
 * Set&lt;MyEnum&gt; s = Collections.synchronizedSet(EnumSet.noneOf(MyEnum.class));
 * </pre>
 *
 * <p>Implementation note: All basic operations execute in constant time.
 * They are likely (though not guaranteed) to be much faster than their
 * {@link java.util.HashSet} counterparts.  Even bulk operations execute in
 * constant time if their argument is also an enum set.
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/java.base/java/util/package-summary.html#CollectionsFramework">
 * Java Collections Framework</a>.
 *
 * @author Josh Bloch
 * @since 1.5
 * @see java.util.EnumMap
 */
public abstract class EnumSet0<E extends Enum<E>> extends AbstractSet<E>
    implements Cloneable, java.io.Serializable
{
    // declare EnumSet.class serialization compatibility with JDK 8
    @Serial
    private static final long serialVersionUID = 1009687484059888093L;

    /**
     * The class of all the elements of this set.
     */
    final transient Class<E> elementType;

    /**
     * All of the values comprising E.  (Cached for performance.)
     */
    final transient Enum<?>[] universe;

    EnumSet0(Class<E>elementType, Enum<?>[] universe) {
        this.elementType = elementType;
        this.universe    = universe;
    }

    /**
     * Creates an empty enum set with the specified element type.
     *
     * @param <E> The class of the elements in the set
     * @param elementType the class object of the element type for this enum
     *     set
     * @return An empty enum set of the specified type.
     * @throws NullPointerException if {@code elementType} is null
     */
    public static <E extends Enum<E>> EnumSet0<E> noneOf(Class<E> elementType) {
        Enum<?>[] universe = getUniverse(elementType);
        if (universe == null)
            throw new ClassCastException(elementType + " not an enum");

        if (universe.length <= 64)
            return new RegularEnumSet0<>(elementType, universe);
        else
            throw new UnsupportedOperationException("Cannot create for enums larger than 64");
    }

    /**
     * Creates an empty enum set with the specified element type.
     *
     * @param <E> The class of the elements in the set
     * @param elementType the class object of the element type for this enum
     *     set
     * @return An empty enum set of the specified type.
     * @throws NullPointerException if {@code elementType} is null
     */
    public static <E extends Enum<E>> EnumSet0<E> of(Class<E> elementType, long elements) {
        Enum<?>[] universe = getUniverse(elementType);
        if (universe == null)
            throw new ClassCastException(elementType + " not an enum");

        if (universe.length <= 64)
            return new RegularEnumSet0<>(elementType, universe, elements);
        else
            throw new UnsupportedOperationException("Cannot create for enums larger than 64");
    }

    /**
     * Creates an enum set containing all of the elements in the specified
     * element type.
     *
     * @param <E> The class of the elements in the set
     * @param elementType the class object of the element type for this enum
     *     set
     * @return An enum set containing all the elements in the specified type.
     * @throws NullPointerException if {@code elementType} is null
     */
    public static <E extends Enum<E>> EnumSet0<E> allOf(Class<E> elementType) {
        EnumSet0<E> result = noneOf(elementType);
        result.addAll();
        return result;
    }

    /**
     * Adds all of the elements from the appropriate enum type to this enum
     * set, which is empty prior to the call.
     */
    abstract void addAll();

    /**
     * Creates an enum set with the same element type as the specified enum
     * set, initially containing the same elements (if any).
     *
     * @param <E> The class of the elements in the set
     * @param s the enum set from which to initialize this enum set
     * @return A copy of the specified enum set.
     * @throws NullPointerException if {@code s} is null
     */
    public static <E extends Enum<E>> EnumSet0<E> copyOf(EnumSet0<E> s) {
        return s.clone();
    }

    /**
     * Creates an enum set initialized from the specified collection.  If
     * the specified collection is an {@code EnumSet} instance, this static
     * factory method behaves identically to {@link #copyOf(EnumSet0)}.
     * Otherwise, the specified collection must contain at least one element
     * (in order to determine the new enum set's element type).
     *
     * @param <E> The class of the elements in the collection
     * @param c the collection from which to initialize this enum set
     * @return An enum set initialized from the given collection.
     * @throws IllegalArgumentException if {@code c} is not an
     *     {@code EnumSet} instance and contains no elements
     * @throws NullPointerException if {@code c} is null
     */
    public static <E extends Enum<E>> EnumSet0<E> copyOf(Collection<E> c) {
        if (c instanceof EnumSet0) {
            return ((EnumSet0<E>)c).clone();
        } else {
            if (c.isEmpty())
                throw new IllegalArgumentException("Collection is empty");
            Iterator<E> i = c.iterator();
            E first = i.next();
            EnumSet0<E> result = EnumSet0.of(first);
            while (i.hasNext())
                result.add(i.next());
            return result;
        }
    }

    /**
     * Creates an enum set with the same element type as the specified enum
     * set, initially containing all the elements of this type that are
     * <i>not</i> contained in the specified set.
     *
     * @param <E> The class of the elements in the enum set
     * @param s the enum set from whose complement to initialize this enum set
     * @return The complement of the specified set in this set
     * @throws NullPointerException if {@code s} is null
     */
    public static <E extends Enum<E>> EnumSet0<E> complementOf(EnumSet0<E> s) {
        EnumSet0<E> result = copyOf(s);
        result.complement();
        return result;
    }

    /**
     * Creates an enum set initially containing the specified element.
     *
     * Overloadings of this method exist to initialize an enum set with
     * one through five elements.  A sixth overloading is provided that
     * uses the varargs feature.  This overloading may be used to create
     * an enum set initially containing an arbitrary number of elements, but
     * is likely to run slower than the overloadings that do not use varargs.
     *
     * @param <E> The class of the specified element and of the set
     * @param e the element that this set is to contain initially
     * @throws NullPointerException if {@code e} is null
     * @return an enum set initially containing the specified element
     */
    public static <E extends Enum<E>> EnumSet0<E> of(E e) {
        EnumSet0<E> result = noneOf(e.getDeclaringClass());
        result.add(e);
        return result;
    }

    /**
     * Creates an enum set initially containing the specified elements.
     *
     * Overloadings of this method exist to initialize an enum set with
     * one through five elements.  A sixth overloading is provided that
     * uses the varargs feature.  This overloading may be used to create
     * an enum set initially containing an arbitrary number of elements, but
     * is likely to run slower than the overloadings that do not use varargs.
     *
     * @param <E> The class of the parameter elements and of the set
     * @param e1 an element that this set is to contain initially
     * @param e2 another element that this set is to contain initially
     * @throws NullPointerException if any parameters are null
     * @return an enum set initially containing the specified elements
     */
    public static <E extends Enum<E>> EnumSet0<E> of(E e1, E e2) {
        EnumSet0<E> result = noneOf(e1.getDeclaringClass());
        result.add(e1);
        result.add(e2);
        return result;
    }

    /**
     * Creates an enum set initially containing the specified elements.
     *
     * Overloadings of this method exist to initialize an enum set with
     * one through five elements.  A sixth overloading is provided that
     * uses the varargs feature.  This overloading may be used to create
     * an enum set initially containing an arbitrary number of elements, but
     * is likely to run slower than the overloadings that do not use varargs.
     *
     * @param <E> The class of the parameter elements and of the set
     * @param e1 an element that this set is to contain initially
     * @param e2 another element that this set is to contain initially
     * @param e3 another element that this set is to contain initially
     * @throws NullPointerException if any parameters are null
     * @return an enum set initially containing the specified elements
     */
    public static <E extends Enum<E>> EnumSet0<E> of(E e1, E e2, E e3) {
        EnumSet0<E> result = noneOf(e1.getDeclaringClass());
        result.add(e1);
        result.add(e2);
        result.add(e3);
        return result;
    }

    /**
     * Creates an enum set initially containing the specified elements.
     *
     * Overloadings of this method exist to initialize an enum set with
     * one through five elements.  A sixth overloading is provided that
     * uses the varargs feature.  This overloading may be used to create
     * an enum set initially containing an arbitrary number of elements, but
     * is likely to run slower than the overloadings that do not use varargs.
     *
     * @param <E> The class of the parameter elements and of the set
     * @param e1 an element that this set is to contain initially
     * @param e2 another element that this set is to contain initially
     * @param e3 another element that this set is to contain initially
     * @param e4 another element that this set is to contain initially
     * @throws NullPointerException if any parameters are null
     * @return an enum set initially containing the specified elements
     */
    public static <E extends Enum<E>> EnumSet0<E> of(E e1, E e2, E e3, E e4) {
        EnumSet0<E> result = noneOf(e1.getDeclaringClass());
        result.add(e1);
        result.add(e2);
        result.add(e3);
        result.add(e4);
        return result;
    }

    /**
     * Creates an enum set initially containing the specified elements.
     *
     * Overloadings of this method exist to initialize an enum set with
     * one through five elements.  A sixth overloading is provided that
     * uses the varargs feature.  This overloading may be used to create
     * an enum set initially containing an arbitrary number of elements, but
     * is likely to run slower than the overloadings that do not use varargs.
     *
     * @param <E> The class of the parameter elements and of the set
     * @param e1 an element that this set is to contain initially
     * @param e2 another element that this set is to contain initially
     * @param e3 another element that this set is to contain initially
     * @param e4 another element that this set is to contain initially
     * @param e5 another element that this set is to contain initially
     * @throws NullPointerException if any parameters are null
     * @return an enum set initially containing the specified elements
     */
    public static <E extends Enum<E>> EnumSet0<E> of(E e1, E e2, E e3, E e4,
                                                    E e5)
    {
        EnumSet0<E> result = noneOf(e1.getDeclaringClass());
        result.add(e1);
        result.add(e2);
        result.add(e3);
        result.add(e4);
        result.add(e5);
        return result;
    }

    /**
     * Creates an enum set initially containing the specified elements.
     * This factory, whose parameter list uses the varargs feature, may
     * be used to create an enum set initially containing an arbitrary
     * number of elements, but it is likely to run slower than the overloadings
     * that do not use varargs.
     *
     * @param <E> The class of the parameter elements and of the set
     * @param first an element that the set is to contain initially
     * @param rest the remaining elements the set is to contain initially
     * @throws NullPointerException if any of the specified elements are null,
     *     or if {@code rest} is null
     * @return an enum set initially containing the specified elements
     */
    @SafeVarargs
    public static <E extends Enum<E>> EnumSet0<E> of(E first, E... rest) {
        EnumSet0<E> result = noneOf(first.getDeclaringClass());
        result.add(first);
        for (E e : rest)
            result.add(e);
        return result;
    }

    /**
     * Creates an enum set initially containing all of the elements in the
     * range defined by the two specified endpoints.  The returned set will
     * contain the endpoints themselves, which may be identical but must not
     * be out of order.
     *
     * @param <E> The class of the parameter elements and of the set
     * @param from the first element in the range
     * @param to the last element in the range
     * @throws NullPointerException if {@code from} or {@code to} are null
     * @throws IllegalArgumentException if {@code from.compareTo(to) > 0}
     * @return an enum set initially containing all of the elements in the
     *         range defined by the two specified endpoints
     */
    public static <E extends Enum<E>> EnumSet0<E> range(E from, E to) {
        if (from.compareTo(to) > 0)
            throw new IllegalArgumentException(from + " > " + to);
        EnumSet0<E> result = noneOf(from.getDeclaringClass());
        result.addRange(from, to);
        return result;
    }

    /**
     * Adds the specified range to this enum set, which is empty prior
     * to the call.
     */
    abstract void addRange(E from, E to);

    /**
     * Returns a copy of this set.
     *
     * @return a copy of this set
     */
    @SuppressWarnings("unchecked")
    public EnumSet0<E> clone() {
        try {
            return (EnumSet0<E>) super.clone();
        } catch(CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Complements the contents of this enum set.
     */
    abstract void complement();

    /**
     * Throws an exception if e is not of the correct type for this enum set.
     */
    final void typeCheck(E e) {
        Class<?> eClass = e.getClass();
        if (eClass != elementType && eClass.getSuperclass() != elementType)
            throw new ClassCastException(eClass + " != " + elementType);
    }

    /**
     * Returns all of the values comprising E.
     * The result is uncloned, cached, and shared by all callers.
     */
    private static <E extends Enum<E>> E[] getUniverse(Class<E> elementType) {
        return elementType.getEnumConstants();
    }

}
