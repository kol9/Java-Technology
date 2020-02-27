import java.util.*;


/**
 * @author Nikolay Yarlychenko
 */
public class ArraySet<E> extends AbstractSet<E> implements NavigableSet<E> {

    private final Comparator<? super E> comparator;
    private final List<E> data;


    public ArraySet() {
        comparator = null;
        data = Collections.emptyList();
    }

    public ArraySet(Collection<? extends E> data) {
        this(data, null);
    }

    public ArraySet(Collection<? extends E> data, Comparator<? super E> cmp) {
        comparator = cmp;
        TreeSet<E> tmp = new TreeSet<>(cmp);
        tmp.addAll(data);
        this.data = new ArrayList<>(tmp);
    }

    private ArraySet(List<E> data, Comparator<? super E> cmp) {
        this.data = data;
        this.comparator = cmp;
    }


    E getItem(int i) {
        if (i < 0 || i >= size()) {
            return null;
        } else {
            return data.get(i);
        }
    }

    @Override
    public E lower(E e) {
        int i = getIndex(e, -1, -1);
        return getItem(i);
    }

    @Override
    public E floor(E e) {
        int i = getIndex(e, 0, -1);
        return getItem(i);
    }

    @Override
    public E ceiling(E e) {
        int i = getIndex(e, 0, 0);
        return getItem(i);
    }

    @Override
    public E higher(E e) {
        int i = getIndex(e, 1, 0);
        return getItem(i);
    }

    @Override
    public E pollFirst() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E pollLast() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<E> iterator() {
        return Collections.unmodifiableList(data).iterator();
    }

    @Override
    public NavigableSet<E> descendingSet() {
        return new ArraySet<>(new ReversedList<>(data), Collections.reverseOrder(comparator));
    }

    @Override
    public Iterator<E> descendingIterator() {
        return descendingSet().iterator();
    }

    boolean isBordersInvalid(int l, int r) {
        return (l < 0 || r < 0 || l > r || l >= size() || r >= size());
    }

    @Override
    public NavigableSet<E> subSet(E e, boolean fromInclusive, E e1, boolean toInclusive) {
        if (compare(e, e1) > 0) {
            throw new IllegalArgumentException("fromElement > toElement");
        }
        int ind1 = fromInclusive ? getIndex(e, 0, 0) : getIndex(e, 1, 0);
        int ind2 = toInclusive ? getIndex(e1, 0, -1) : getIndex(e1, -1, -1);

        if (isBordersInvalid(ind1, ind2)) {
            return new ArraySet<>(Collections.emptyList(), comparator);
        }

        return new ArraySet<>(data.subList(ind1, ind2 + 1), comparator);
    }

    @Override
    public NavigableSet<E> headSet(E e, boolean isInclusive) {
        if (size() == 0 || compare(first(), e) > 0) {
            return new ArraySet<>(Collections.emptyList(), comparator);
        }
        return subSet(first(), true, e, isInclusive);
    }

    @Override
    public NavigableSet<E> tailSet(E e, boolean isInclusive) {

        if (size() == 0 || compare(e, last()) > 0) {
            return new ArraySet<>(Collections.emptyList(), comparator);
        }
        return subSet(e, isInclusive, last(), true);
    }

    @Override
    public int size() {
        return data.size();
    }


    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        return Collections.binarySearch(data, (E) o, comparator) >= 0;
    }

    int getIndex(E e, int found, int notFound) {
        int ind = Collections.binarySearch(data, e, comparator);

        if (ind >= 0) {
            return ind + found;
        } else {
            ind *= -1;
            ind -= 1;
            ind += notFound;
        }

        if (!(ind >= 0 && ind < size())) {
            ind = -1;
        }

        return ind;
    }

    @SuppressWarnings("unchecked")
    private int compare(E e1, E e2) {
        return comparator() == null ? ((Comparable<? super E>) e1).compareTo(e2) : comparator().compare(e1, e2);
    }

    @Override
    public SortedSet<E> subSet(E e, E e1) {
        return subSet(e, true, e1, false);
    }

    @Override
    public SortedSet<E> headSet(E e) {
        return headSet(e, false);
    }

    @Override
    public SortedSet<E> tailSet(E e) {
        return tailSet(e, true);
    }

    @Override
    public E first() {
        if (size() == 0) {
            throw new NoSuchElementException();
        }
        return data.get(0);
    }

    @Override
    public E last() {
        if (size() == 0) {
            throw new NoSuchElementException();
        }
        return data.get(data.size() - 1);
    }
}


class ReversedList<E> extends AbstractList<E> {
    private final List<E> data;

    public ReversedList(List<E> other) {
        data = other;
    }

    @Override
    public int size() {
        return (data == null) ? 0 : data.size();
    }

    @Override
    public E get(int index) {
        Objects.checkIndex(index, size());
        return data.get(size() - index - 1);
    }
}