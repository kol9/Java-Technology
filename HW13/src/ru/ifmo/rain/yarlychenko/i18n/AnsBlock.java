package ru.ifmo.rain.yarlychenko.i18n;

/**
 * @author Nikolay Yarlychenko
 */
public class AnsBlock<V, T> {
    private V value;
    private T item;

    public AnsBlock(V value, T item) {
        this.value = value;
        this.item = item;
    }

    public V getValue() {
        return value;
    }

    public T getItem() {
        return item;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public void setItem(T item) {
        this.item = item;
    }
}