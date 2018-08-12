/* 
 * @(#)GenericComarator.java	1.0 1/1/2004
 *
 * COPYRIGHT EVAN MACHUSAK (UNIVERSITY OF MARYLAND, COLLEGE PARK), 2003
 * ALL RIGHTS RESERVED
 */
 
 
 
 /** A comparator which imposes objects' <i>natural ordering</i>.  This comparator's {@link #compare(Object, Object)} method
   * assumes its arguments implement {@link java.lang.Comparable}.  In most data structures reliant upon <tt>Comparator</tt>
   * for data which doesn't implement <tt>Comparable</tt>, the solution is merely to keep a <tt>null</tt> comparator as a member
   * and check that reference when comparison is necessary to decide which comparison method to use.<p>
   *
   * However, this approach is costly and unnecessary.  In absence of a <tt>Comparator</tt>, these data structures default to
   * using the same ordering as imposed by this <tt>Comparator</tt> anyway.  Consider the following method used internally by
   * Java's {@link java.util.TreeMap} class:<p>
   *
   * <tt>
   * private int compare(Object k1, Object k2) {<ul>
     	return (comparator==null ? ((Comparable)k1).compareTo(k2): comparator.compare(k1, k2));
   * </ul>}
   * </tt><p>
   *
   * Each time this method is invoked, a reference comparison must be made.  However, supplying an instance of <tt>TreeMap</tt>
   * with this comparator would eliminate the need to babysit its own comparator.  A data structure which defaults to using this
   * comparator if the user does not supply its own will allow the semantics of defaulting to <i>natural ordering</i> without
   * requiring unnecessary conditionals.<p>
   *
   * Also, since this class is a singleton, no more than one of this object can be instantiated in any given VM, which causes
   * the overhad of this approach to be practically zero.  This class is provided as an implementation convenience and as a
   * substitute for objects requiring comparators but with no contingency to use natural ordering when desired.  Although all of
   * Java's sorted data structures have this contingency in place, it is easy to foresee a situation where this may not be the
   * case.
   *
   * @author Evan Machusak (<a href="mailto:emach@cs.umd.edu">emach@cs.umd.edu</a>)
   * @version 1.0
   * @see java.lang.Comparable
   */
public class ReverseGenericComparator<K extends Comparable<?>> implements java.util.Comparator<K> {
	
	protected ReverseGenericComparator() {}
	public static final ReverseGenericComparator instance = new ReverseGenericComparator();
	
	/** Compares its two arguments for order.
	  * Although the implementation of this method only requires that, minimally, the first parameter is an
	  * implementor of {@link java.lang.Comparable}, due to this comparator's usage in sorted data structures
	  * in absensce of a user-defined comparator, the limitation that both parameters must be <tt>Comparable</tt>
	  * has been established.  However, this is not directly enforced.  Users of this class are responsible for
	  * adequately restricting their keyspace as necessary.<p />
	  *
	  * This may seem like an unnecessary restriction, but consider a <tt>SortedMap</tt> using this comparator.
	  * Presume the map is populated with a keyset entirely consisting of instances of type <tt>A</tt>, which
	  * implements <tt>Comparable</tt>.  Now suppose the user attempts to put a mapping containing type <tt>B</tt>
	  * which does not implement <tt>Comparable</tt>.  Presuming that <tt>A</tt> has a <tt>compareTo()</tt> function
	  * which accepts objects of type <tt>B</tt>.  This will allow a mapping with a key of type <tt>B</tt> to be
	  * added into the map.  It is now the case that an object of type <tt>B</tt> will take the place of the first
	  * argument to this function (<tt>o1</tt>) which, as we have established, minimally requires implementation
	  * of <tt>Comparable</tt> to proceed without exception.  Thus, in practice, allowing a non-comparable key in
	  * place of <tt>o2</tt> will inevitably cause a <tt>ClassCastException</tt> in the future.
	  *
	  * @param o1 an instance of <tt>Comparable</tt>, preferably mutually comparable with <tt>o2</tt>
	  * @param o2 an instance of <tt>Comparable</tt>, preferably mutually comparable with <tt>o1</tt>
	  * @return an <tt>int x</tt> such that:<ul>
	  *    <li><tt>x &lt; 0</tt> if <tt>o1</tt>'s <i>natural order</i> is before <tt>o2</tt>;<p>
	  *    <li><tt>x == 0</tt> if <tt>o1</tt> is <i>equal</i> to <tt>o2</tt> according to <tt>o1</tt>'s implementation of <tt>compareTo()</tt>;<p>
	  *    <li><tt>x > 0</tt> if <tt>o1</tt>'s <i>natural order</i> is after <tt>o2</tt>
	  * </ul>
	  * @throws ClassCastException if <tt>o1</tt> is not an instance of <tt>java.lang.Comparable</tt>
	  * @throws NullPointerException if <tt>o1</tt> is <tt>null</tt>
	  * @since 1.0
	  */
	public int compare(K o1, K o2) { return ((java.lang.Comparable)o2).compareTo(o1); }
          
        /** Indicates whether some other object is "equal to" this Comparator.
          * Because this comparator is a singleton class, this function returns <tt>true</tt> if and only if
          * <tt>obj</tt> is a reference to {@link #instance}.
          * @param obj the object to compare against this <tt>GenericComparator</tt> for equality
          * @return <tt>true</tt> if <tt>obj</tt> is a reference to <tt>instance</tt>, <tt>false</tt> otherwise
          * @since 1.0
          */          
	public boolean equals(Object obj) { return (obj == ReverseGenericComparator.instance); }
	public int hashCode() { return super.hashCode(); }
}
		
          
