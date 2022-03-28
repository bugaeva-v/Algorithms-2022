package lesson3;

import java.util.*;

import kotlin.NotImplementedError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// attention: Comparable is supported but Comparator is not
public class BinarySearchTree<T extends Comparable<T>> extends AbstractSet<T> implements CheckableSortedSet<T> {


    private final Node<T> root;

    private T minValue = null;
    private T maxValue = null;

    BinarySearchTree() {
        root = new Node<>(null, null);
    }

    BinarySearchTree(BinarySearchTree<T> tree, T min, T max) {
        root = tree.root;
        minValue = min;
        maxValue = max;
    }

    private static class Node<T> {
        T value;
        Node<T> prev;
        Node<T> left = null;
        Node<T> right = null;

        Node(T value, Node<T> prev) {
            this.value = value;
            this.prev = prev;
        }
    }

    @Override
    public int size() {
        if (root.value == null) return 0;
        int size = 0;
        try {
            for (T ignored : this) size++;
        } catch (NullPointerException e) {
            size = 0;
        }
        return size;
    }

    private Node<T> find(T value) {
        if (root.value == null) return null;
        return find(root, value);
    }

    private Node<T> find(Node<T> start, T value) {
        int comparison = value.compareTo(start.value);
        if (comparison == 0) {
            return start;
        } else if (comparison < 0) {
            if (start.left == null)
                return start;
            return find(start.left, value);
        } else {
            if (start.right == null)
                return start;
            return find(start.right, value);
        }
    }

    public boolean outOfTheLimits(T t) {
        return (minValue != null && t.compareTo(minValue) < 0) || (maxValue != null && t.compareTo(maxValue) >= 0);
    }

    public boolean meetTheRightLimit(T t) {
        return (maxValue == null || t.compareTo(maxValue) < 0);
    }

    public boolean meetTheLeftLimit(T t) {
        return (minValue == null || t.compareTo(minValue) >= 0);
    }

    @Override
    public boolean contains(Object o) {
        @SuppressWarnings("unchecked")
        T t = (T) o;
        if (outOfTheLimits(t)) return false;
        Node<T> closest = find(t);
        return closest != null && t.compareTo(closest.value) == 0;
    }

    /**
     * Добавление элемента в дерево
     *
     * Если элемента нет в множестве, функция добавляет его в дерево и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     *
     * Спецификация: {@link Set#add(Object)} (Ctrl+Click по add)
     *
     * Пример
     */
    @Override
    public boolean add(T t) {
        if (outOfTheLimits(t)) throw new IllegalArgumentException();
        Node<T> closest = find(t);
        int comparison = closest == null ? -1 : t.compareTo(closest.value);
        if (comparison == 0) {
            return false;
        }
        Node<T> newNode = new Node<>(t, closest);
        if (closest == null) {
            root.value = t;
        } else if (comparison < 0) {
            assert closest.left == null;
            closest.left = newNode;
        } else {
            assert closest.right == null;
            closest.right = newNode;
        }
        return true;
    }

    /**
     * Удаление элемента из дерева
     *
     * Если элемент есть в множестве, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     *
     * Спецификация: {@link Set#remove(Object)} (Ctrl+Click по remove)
     *
     * Средняя
     */

    //T = O(n)
    //R = O(1)
    @Override
    public boolean remove(Object o) {
        @SuppressWarnings("unchecked")
        T t = (T) o;

        if (outOfTheLimits(t))
            throw new IllegalArgumentException();
        Node<T> removed = find(t);
        int comparison = removed == null ? -1 : t.compareTo(removed.value);
        if (comparison != 0) {
            return false;
        }
        if (removed.left == null && removed.right == null) {//случай удаления узла без листьев
            if (removed.prev == null)
                root.value = null;
            else if (removed == removed.prev.left)
                removed.prev.left = null;
            else
                removed.prev.right = null;
        } else if (removed.left == null) {//случай удаления узла без левого листа
            removed.right.prev = removed.prev;
            if (removed.prev == null) {
                root.value = root.right.value;
                if (root.right.left != null) root.right.left.prev = root;
                if (root.right.right != null) root.right.right.prev = root;
                root.left = root.right.left;
                root.right = root.right.right;
            } else if (removed == removed.prev.left)
                removed.prev.left = removed.right;
            else
                removed.prev.right = removed.right;
        } else if (removed.right == null) {//случай удаления узла без правого листа
            removed.left.prev = removed.prev;
            if (removed.prev == null) {
                root.value = root.left.value;
                if (root.left.left != null) root.left.left.prev = root;
                if (root.left.right != null) root.left.right.prev = root;
                root.right = root.left.right;
                root.left = root.left.left;
            } else if (removed == removed.prev.left)
                removed.prev.left = removed.left;
            else
                removed.prev.right = removed.left;
        } else {//удаление узла, имеющего оба листа
            Node<T> replacement = find(removed.right, removed.value);
            if (removed.prev == null)// удаление корневого узла
            {
                root.value = replacement.value;
                if (replacement.prev.left == replacement) replacement.prev.left = replacement.right;
                else replacement.prev.right = replacement.right;
                if (replacement.right != null) replacement.right.prev = replacement.prev;
                replacement.prev = null;
                return true;
            }

            if (replacement.right == null) {//случай замены узла на узел без листов
                if (replacement.prev == removed) {
                    if (replacement == removed.left)
                        removed.left = null;
                    else
                        removed.right = null;
                } else replacement.prev.left = null;
            } else {//случай, когда у выбранного для замены узла есть правый лист
                if (replacement.prev == removed)//ссылке на один из листов предыдущего узла по отношению к заменяемому, присваивается ссылка на правый лист узла-замены
                    removed.right = replacement.right;
                else
                    replacement.prev.left = replacement.right;
                replacement.right.prev = replacement.prev;//ссылке на предыдущую ячейку правого листа заменяющего узла присваивается ссылка на узел предыдущий по отношению к заменяемому
            }
            replacement.right = removed.right;// замена ссылок в узле-замене
            replacement.left = removed.left;
            replacement.prev = removed.prev;

            if (replacement.right != null)// замена ссылки на предыдущий узел у листьев узла-замены
                replacement.right.prev = replacement;
            if (replacement.left != null)
                replacement.left.prev = replacement;

            if (removed == removed.prev.left)// либо замена ссылки на один из листьев у узда предыдущего перед удаляемым узлом
                removed.prev.left = replacement;
            else
                removed.prev.right = replacement;
        }
        return true;
    }

    @Nullable
    @Override
    public Comparator<? super T> comparator() {
        return null;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new BinarySearchTreeIterator();
    }

    public class BinarySearchTreeIterator implements Iterator<T> {

        private Node<T> next;
        private Node<T> lastReturned = null;

        private BinarySearchTreeIterator() {
            if (root.value == null) {
                next = null;
                return;
            } else {
                Node<T> n = root;
                while (n.left != null)
                    n = n.left;
                next = n;
            }
            if (!meetTheRightLimit(next.value)) {
                next = null;
                return;
            }
            while (next != null && !meetTheLeftLimit(next.value)) {
                next();
            }
            lastReturned = null;
        }

        /**
         * Проверка наличия следующего элемента
         *
         * Функция возвращает true, если итерация по множеству ещё не окончена (то есть, если вызов next() вернёт
         * следующий элемент множества, а не бросит исключение); иначе возвращает false.
         *
         * Спецификация: {@link Iterator#hasNext()} (Ctrl+Click по hasNext)
         *
         * Средняя
         */

        //T = O(1)
        //R = O(1)
        @Override
        public boolean hasNext() {
            return next != null;
        }

        /**
         * Получение следующего элемента
         *
         * Функция возвращает следующий элемент множества.
         * Так как BinarySearchTree реализует интерфейс SortedSet, последовательные
         * вызовы next() должны возвращать элементы в порядке возрастания.
         *
         * Бросает NoSuchElementException, если все элементы уже были возвращены.
         *
         * Спецификация: {@link Iterator#next()} (Ctrl+Click по next)
         *
         * Средняя
         */

        //T = O(n)
        //R = O(1)
        @Override
        public T next() {
            if (!hasNext())
                throw new NoSuchElementException();
            lastReturned = next;
            if (next.right != null) {
                next = find(next.right, next.value);
                if (!meetTheRightLimit(next.value)) {
                    next = null;
                }
            } else {
                while (next.prev != null) {
                    if (next.prev.left == next) {
                        if (!meetTheRightLimit(next.prev.value)) next = null;
                        else next = next.prev;
                        return lastReturned.value;
                    }
                    next = next.prev;
                }
                next = null;
            }
            return lastReturned.value;
        }

        /**
         * Удаление предыдущего элемента
         *
         * Функция удаляет из множества элемент, возвращённый крайним вызовом функции next().
         *
         * Бросает IllegalStateException, если функция была вызвана до первого вызова next() или же была вызвана
         * более одного раза после любого вызова next().
         *
         * Спецификация: {@link Iterator#remove()} (Ctrl+Click по remove)
         *
         * Сложная
         */

        //T = O(n)
        //R = O(1)
        @Override
        public void remove() {
            if (lastReturned == null)
                throw new IllegalStateException();
            Node<T> prevLastReturned = lastReturned.prev;
            BinarySearchTree.this.remove(lastReturned.value);
            if (prevLastReturned == null && next != null && next.prev == null) {// случай, когда удаляется root, а заменяющим узлом выбран next
                next = root;
            }
            lastReturned = null;
        }
    }

    /**
     * Подмножество всех элементов в диапазоне [fromElement, toElement)
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева, которые
     * больше или равны fromElement и строго меньше toElement.
     * При равенстве fromElement и toElement возвращается пустое множество.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: {@link SortedSet#subSet(Object, Object)} (Ctrl+Click по subSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Очень сложная (в том случае, если спецификация реализуется в полном объёме)
     */

    //T = O(1)
    //R = O(1)
    @NotNull
    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        if (fromElement == null || toElement == null) throw new NullPointerException();
        int comp = fromElement.compareTo(toElement);
        if (comp > 0) throw new IllegalArgumentException();
        if (outOfTheLimits(fromElement) || maxValue != null && toElement.compareTo(maxValue) > 0)
            throw new IllegalArgumentException();
        return new BinarySearchTree<>(this, fromElement, toElement);
    }

    /**
     * Подмножество всех элементов строго меньше заданного
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева строго меньше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: {@link SortedSet#headSet(Object)} (Ctrl+Click по headSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> headSet(T toElement) {
        // TODO
        throw new NotImplementedError();
    }

    /**
     * Подмножество всех элементов нестрого больше заданного
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева нестрого больше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: {@link SortedSet#tailSet(Object)} (Ctrl+Click по tailSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> tailSet(T fromElement) {
        // TODO
        throw new NotImplementedError();
    }

    @Override
    public T first() {
        if (root.value == null)
            throw new NoSuchElementException();
        Iterator<T> i = iterator();
        T t = i.next();
        if (t == null) throw new NoSuchElementException();
        return t;
    }

    @Override
    public T last() {
        if (root.value == null)
            throw new NoSuchElementException();

        Node<T> current = root;
        T res = current.value;
        while (current.left != null || current.right != null) {
            while (current.right != null && meetTheRightLimit(current.value)) {
                current = current.right;
            }
            if (current.value != res) res = current.prev.value;
            if (!meetTheRightLimit(current.value)) {
                while (current.left != null && !meetTheRightLimit(current.value)) {
                    current = current.left;
                }
                if (meetTheRightLimit(current.value)) res = current.value;
                else {
                    if (outOfTheLimits(res))
                        throw new NoSuchElementException();
                    return res;
                }
            } else {
                if (outOfTheLimits(current.value))
                    throw new NoSuchElementException();
                return current.value;
            }
        }
        if (outOfTheLimits(res))
            throw new NoSuchElementException();
        return res;
    }

    public int height() {
        return height(root);
    }

    private int height(Node<T> node) {
        if (node == null || node.value == null) return 0;
        return 1 + Math.max(height(node.left), height(node.right));
    }

    public boolean checkInvariant() {
        return root.value == null || checkInvariant(root);
    }

    private boolean checkInvariant(Node<T> node) {
        Node<T> left = node.left;
        if (left != null && (left.value.compareTo(node.value) >= 0 || !checkInvariant(left))) return false;
        Node<T> right = node.right;
        return right == null || right.value.compareTo(node.value) > 0 && checkInvariant(right);
    }

}