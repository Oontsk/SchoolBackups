package cmsc420.sortedmap;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cmsc420.meeshquest.part3.City;

public class Treap<K, V> extends AbstractMap<K, V> implements SortedMap<K, V> {

	private class TreapNode extends AbstractMap.SimpleEntry<K, V> implements SortedMap.Entry<K, V> {
		private static final long serialVersionUID = 1L;
		private TreapNode left = null, right = null, parent;
		private int priority;
		
		public TreapNode(K arg0, V arg1, TreapNode parent, int priority) {
			super(arg0, arg1);
			this.parent = parent;
			this.priority = priority;
		}	
		
		private Element print(Document results) {
			Element node = results.createElement("node");
			node.setAttribute("key", getKey().toString());
			node.setAttribute("priority", String.valueOf(priority));
			
			if (getValue() instanceof City) {
				node.setAttribute("value", ((City) getValue()).toString());
			}
			
			if (left != null) {
				node.appendChild(left.print(results));
			} else {
				node.appendChild(results.createElement("emptyChild"));
			}
			
			if (right != null) {
				node.appendChild(right.print(results));
			} else {
				node.appendChild(results.createElement("emptyChild"));
			}
			return node;
		}
	}
	
	private TreapNode root = null;
	private int size = 0, modCount = 0;
	private EntrySet es = null;
	private Comparator<K> comp;
	private Random random = new Random();
	
	public Treap(Comparator<K> comp) {
		this.comp = comp;
	}
	
	public Treap() {
		this.comp = null;
	}
	
	@SuppressWarnings("unchecked")
	private int compare(K o, K t) {
		return comp != null ? comp.compare(o, t) :
			((Comparable<K>) o).compareTo(t);
	}
	
	private TreapNode firstEntry() {
		TreapNode p = root;
		if (p != null) {
			while (p.left != null) {
				p = p.left;
			}
		}
		return p;
	}
	
	private TreapNode lastEntry() {
		TreapNode p = root;
		if (p != null) {
			while (p.right != null) {
				p = p.right;
			}
		}
		return p;
	}
	
	private TreapNode ceilingEntry(K key) {
		TreapNode p = root;
		while (p != null) {
			int cmp = compare(key, p.getKey());
			if (cmp < 0) {
				if (p.left != null) {
					p = p.left;
				} else {
					return p;
				}
			} else if (cmp > 0) {
				if (p.right != null) {
					p = p.right;
				} else {
					TreapNode parent = p.parent, ch = p;
					while (parent != null && ch == parent.right) {
						ch = parent;
						parent = parent.parent;
					}
					return parent;
				}
			} else {
				return p;
			}
		}
		return null;
	}
	
	private TreapNode lowerEntry(K key) {
		TreapNode p = root;
		while (p != null) {
			int cmp = compare(key, p.getKey());
			if (cmp > 0) {
				if (p.right != null) {
					p = p.right;
				} else {
					return p;
				}
			} else {
				if (p.left != null) {
					p = p.left;
				} else {
					TreapNode parent = p.parent, ch = p;
					while (parent != null && ch == parent.left) {
						ch = parent;
						parent = parent.parent;
					}
					return parent;
				}
			}
		}
		return null;
	}
	
	private TreapNode successor(TreapNode t) {
		if (t == null) {
			return null;
		} else if (t.right != null) {
			TreapNode p = t.right;
			while (p.left != null) {
				p = p.left;
			}
			return p;
		} else {
			TreapNode p = t.parent, ch = t;
			while (p != null && ch == p.right) {
				ch = p;
				p = p.parent;
			}
			return p;
		}
	}
	
	private TreapNode getEntry(K key) {
		if (key == null) {
			throw new NullPointerException();
		}
		TreapNode p = root;
		while (p != null) {
			int c = compare(key, p.getKey());
			if (c < 0) {
				p = p.left;
			} else if (c > 0) {
				p = p.right;
			} else {
				return p;
			}
		}
		return null;
	}
	
	private V deleteEntry(TreapNode guy) {
		--size;
		++modCount;
		V old = guy.getValue();
		
		if (size == 0) {
			root = null;
			return old;
		}
		
		rotateDown(guy);
		TreapNode parent = guy.parent;
		if (parent.left == guy) {
			parent.left = null;
		} else {
			parent.right = null;
		}
		return old;
	}
	
	private void rotate(TreapNode guy) {
		TreapNode par = guy.parent;
		if (par.left != null && par.left == guy) {
			par.left = guy.right;
			if (guy.right != null) {
				guy.right.parent = par;
			}
			guy.right = par;
		} else {
			par.right = guy.left;
			if (guy.left != null) {
				guy.left.parent = par;
			}
			guy.left = par;
		}
		
		TreapNode giisan = par.parent;
		if (giisan != null) {
			if (giisan.left != null && giisan.left == par) {
				giisan.left = guy;
			} else {
				giisan.right = guy;
			}
		}
		guy.parent = giisan;
		par.parent = guy;
		
		if (root == par) {
			root = guy;
		}
	}
	
	private void rotateUp(TreapNode guy) {
		while (guy.parent != null && guy.priority > guy.parent.priority) {
			rotate(guy);
		}
	}
	
	private void rotateDown(TreapNode guy) {
		while (!(guy.left == null && guy.right == null)) {
			if (guy.left == null) {
				rotate(guy.right);
			} else if (guy.right == null) {
				rotate(guy.left);
			} else if (guy.left.priority < guy.right.priority) {
				rotate(guy.right);
			} else {
				rotate(guy.left);
			}
		}
	}
	
	public Set<Entry<K, V>> entrySet() {
		if (es == null) {
			es = new EntrySet();
		}
		return es;
	}
	
	public void clear() {
		es = null;
		size = 0;
		++modCount;
		root = null;
	}
	
	private class EntrySet extends AbstractSet<Map.Entry<K, V>> {

		public boolean contains(Object ot) {
			if (ot == null || !(ot instanceof Map.Entry)) {
				return false;
			}
			@SuppressWarnings("unchecked")
			Map.Entry<K, V> o = (Map.Entry<K, V>) ot;
			V v = o.getValue();
			TreapNode p = getEntry(o.getKey());
			return p != null && p.getValue().equals(v);
		}
		
		public boolean remove(Object ot) {
			if (ot == null || !(ot instanceof Map.Entry)) {
				return false;
			}
			@SuppressWarnings("unchecked")
			Map.Entry<K, V> o = (Map.Entry<K, V>) ot;
			V v = o.getValue();
			TreapNode p = getEntry(o.getKey());
			if (p != null && p.getValue().equals(v)) {
				deleteEntry(p);
				return true;
			}
			return false;
		}
		
		public void clear() {
			Treap.this.clear();
		}
		
		public Iterator<Entry<K, V>> iterator() {
			return new EntrySetIterator(firstEntry());
		}
		
		private class EntrySetIterator implements Iterator<Map.Entry<K, V>> {
			TreapNode next, lastRet = null;
			int eMC = modCount;
			
			private EntrySetIterator(TreapNode f) {
				next = f;
			}
			
			public boolean hasNext() {
				return next != null;
			}
			
			public Map.Entry<K, V> next() {
				if (next == null) {
					throw new NoSuchElementException();
				}
				if (eMC != modCount) {
					throw new ConcurrentModificationException();
				}
				lastRet = next;
				next = successor(next);
				return lastRet;
			}
			
			public void remove() {
				if (lastRet == null) {
					throw new IllegalStateException();
				}
				if (eMC != modCount) {
					throw new ConcurrentModificationException();
				}
				deleteEntry(lastRet);
				eMC = modCount;
				lastRet = null;
			}
		}

		
		public int size() {
			return size;
		}
		
	}
	
	public V put(K k, V v) {
		if (k == null || v == null) {
			throw new NullPointerException();
		}
		if (size == 0) {
			++size;
			++modCount;
			root = new TreapNode(k, v, null, random.nextInt());
			return null;
		}
		TreapNode p = root;
		while (p != null) {
			int res = compare(k, p.getKey());
			if (res > 0) {
				if (p.right != null) {
					p = p.right;
				} else {
					++size;
					++modCount;
					p.right = new TreapNode(k, v, p, random.nextInt());
					rotateUp(p.right);
					return null;
				}
			} else if (res < 0) {
				if (p.left != null) {
					p = p.left;
				} else {
					++size;
					++modCount;
					p.left = new TreapNode(k, v, p, random.nextInt());
					rotateUp(p.left);
					return null;
				}
			} else {
				return p.setValue(v);
			}
		}
		return null;
	}
	
	public V remove(Object key) {
		if (key == null) {
			throw new NullPointerException();	
		}
		@SuppressWarnings("unchecked")
		K k = (K) key;
		TreapNode cur = root;
		while (cur != null) {
			int res = compare(k, cur.getKey());
			if (res > 0) {
				cur = cur.right;
			} else if (res < 0) {
				cur = cur.left;
			} else {
				return deleteEntry(cur);
			}
		}
		return null;
	}
	
	public boolean containsKey(Object key) {
		if (key == null) {
			throw new NullPointerException();
		}
		
		@SuppressWarnings("unchecked")
		K k = (K) key;
		TreapNode cur = root;
		while (cur != null) {
			int res = compare(k, cur.getKey());
			if (res < 0) {
				cur = cur.left;
			} else if (res > 0) {
				cur = cur.right;
			} else {
				return true;
			}
		}
		return false;
	}
	
	public V get(Object key) {
		if (key == null) {
			throw new NullPointerException();
		}
		@SuppressWarnings("unchecked")
		K k = (K) key;
		TreapNode cur = root;
		while (cur != null) {
			int res = compare(k, cur.getKey());
			if (res < 0) {
				cur = cur.left;
			} else if (res > 0) {
				cur = cur.right;
			} else {
				return cur.getValue();
			}
		}
		return null;
	}

	public Comparator<? super K> comparator() {
		return comp;
	}
	public K firstKey() {
		if (size == 0) {
			throw new NoSuchElementException();
		}
		return firstEntry().getKey();
	}

	public K lastKey() {
		if (size == 0) {
			throw new NoSuchElementException();
		}
		return lastEntry().getKey();
	}

	public SortedMap<K, V> subMap(K arg0, K arg1) {
		if (arg0 == null || arg1 == null) {
			throw new NullPointerException();
		}
		if (compare(arg0, arg1) > 0) {
			throw new IllegalArgumentException();
		}
		return new SubMap(this, arg0, arg1);
	}
	
	private class SubMap extends AbstractMap<K, V> implements SortedMap<K, V> {
		Treap<K, V> me;
		K lo, hi;
		SubMapEntrySet smes = new SubMapEntrySet();
		
		SubMap(Treap<K, V> me, K lo, K hi) {
			this.me = me;
			this.lo = lo;
			this.hi = hi;
		}
		
		boolean tooLow(K k) {
			return me.compare(k, lo) < 0;
		}
		
		boolean tooHigh(K k) {
			return me.compare(k, hi) >= 0;
		}
		
		boolean inRange(K k) {
			return !tooHigh(k) && !tooLow(k);
		}
		
		public void clear() {
			me.clear();
		}
		
		public V put(K k, V v) {
			if (k == null || v == null) {
				throw new NullPointerException();
			}
			if (!inRange(k)) {
				throw new IllegalArgumentException();
			}
			return me.put(k, v);
		}
		
		public V remove(Object key) {
			if (key == null) {
				throw new NullPointerException();
			}
			@SuppressWarnings("unchecked")
			K k = (K) key;
			return inRange(k) ? me.remove(key) : null;
		}
		
		public boolean containsKey(Object key) {
			if (key == null) {
				throw new NullPointerException();
			}
			@SuppressWarnings("unchecked")
			K k = (K) key;
			return !inRange(k) ? false : me.containsKey(key);	
		}
		
		public V get(Object key) {
			if (key == null) {
				throw new NullPointerException();
			}
			@SuppressWarnings("unchecked")
			K k = (K) key;
			return !inRange(k) ? null : me.get(key);
		}
		
		public Set<Map.Entry<K, V>> entrySet() {
			return smes;
		}
		
		private class SubMapEntrySet extends AbstractSet<Map.Entry<K, V>> {
			public boolean contains(Object ot) {
				if (ot == null || !(ot instanceof Map.Entry)) {
					return false;
				}
				@SuppressWarnings("unchecked")
				Map.Entry<K, V> o = (Map.Entry<K, V>) ot;
				
				V v = o.getValue();
				TreapNode p = me.getEntry(o.getKey());
				return p != null && p.getValue().equals(v) && inRange(p.getKey());
 			}
			
			public boolean remove(Object ot) {
				if (ot == null || !(ot instanceof Map.Entry)) {
					return false;
				}
				@SuppressWarnings("unchecked")
				Map.Entry<K, V> o = (Map.Entry<K, V>) ot;
				V v = o.getValue();
				TreapNode p = me.getEntry(o.getKey());
				if (p != null && p.getValue().equals(v) && inRange(p.getKey())) {
					me.deleteEntry(p);
					return true;
				}
				return false;
			}
			
			public void clear() {
				me.clear();
			}
			
			public Iterator<Map.Entry<K, V>> iterator() {
				return new SubMapEntrySetIterator(ceilingEntry(lo), hi);
			}
			
			public int size() {
				int s = 0;
				Iterator<Map.Entry<K, V>> it = iterator();
				while (it.hasNext()) {
					it.next();
					++s;
				}
				return s;
			}
			
			private class SubMapEntrySetIterator implements Iterator<Map.Entry<K, V>> {
				TreapNode next, lastRet = null;
				K fence;
				int eMC = me.modCount;
				
				SubMapEntrySetIterator(TreapNode f, K fence) {
					next = f;
					this.fence = fence;
				}
				
				public boolean hasNext() {
					return next != null && me.compare(next.getKey(), fence) < 0;
				}
				
				public Map.Entry<K, V> next() {
					if (!hasNext()) {
						throw new NoSuchElementException();
					}
					if (eMC != me.modCount) {
						throw new ConcurrentModificationException();
					}
					lastRet = next;
					next = me.successor(next);
					return lastRet;
				}
				
				public void remove() {
					if (lastRet == null) {
						throw new IllegalStateException();
					}
					if (eMC != me.modCount) {
						throw new ConcurrentModificationException();
					}
					me.deleteEntry(lastRet);
					eMC = me.modCount;
					lastRet = null;
				}
			}
		}

		public Comparator<? super K> comparator() {
			return me.comp;
		}

		public K firstKey() {
			if (size == 0) {
				throw new NoSuchElementException();
			}
			return me.ceilingEntry(lo).getKey();
		}
		
		public K lastKey() {
			if (size == 0) {
				throw new NoSuchElementException();
			}
			return me.lowerEntry(hi).getKey();
		}

		public SortedMap<K, V> subMap(K fromKey, K toKey) {
			if (fromKey == null || toKey == null) {
				throw new NullPointerException();
			}
			if (!inRange(fromKey) || !inRange(toKey) ||
					me.compare(fromKey, toKey) > 0) {
				throw new IllegalArgumentException();
			}
			return new SubMap(me, fromKey, toKey);
		}

		public SortedMap<K, V> headMap(K toKey) {
			throw new UnsupportedOperationException();
		}
		
		public SortedMap<K, V> tailMap(K fromKey) {
			throw new UnsupportedOperationException();
		}
	
	}

	public SortedMap<K, V> tailMap(K arg0) {
		throw new UnsupportedOperationException();
	}

	public SortedMap<K, V> headMap(K arg0) {
		throw new UnsupportedOperationException();
	}
	
	public Element print(Document results) {
		Element treap = results.createElement("treap");
		treap.setAttribute("cardinality", String.valueOf(size));
		treap.appendChild(root.print(results));
		return treap;
	}

}
