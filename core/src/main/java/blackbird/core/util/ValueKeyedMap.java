package blackbird.core.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ValueKeyedMap<K, V> implements Map<K, V> {

    private Map<K, V> implementation;

    private Function<V, K> mappingFunction;

    public ValueKeyedMap(Function<V, K> mappingFunction) {
        this.mappingFunction = mappingFunction;
        this.implementation = new HashMap<>();
    }

    public ValueKeyedMap(Map<K, V> implementation, Function<V, K> mappingFunction) {
        this.mappingFunction = mappingFunction;
        this.implementation = implementation;
    }

    @Override
    public void clear() {
        implementation.clear();
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(Object key) {
        return implementation.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return implementation.containsValue(value);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return implementation.entrySet();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        return implementation.equals(o);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        implementation.forEach(action);
    }

    @Override
    public V get(Object key) {
        return implementation.get(key);
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return implementation.getOrDefault(key, defaultValue);
    }

    @Override
    public int hashCode() {
        return implementation.hashCode();
    }

    @Override
    public boolean isEmpty() {
        return implementation.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return implementation.keySet();
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    public V put(V value) {
        return implementation.put(mappingFunction.apply(value), value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V putIfAbsent(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(Object key) {
        return implementation.remove(key);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return implementation.remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V replace(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return implementation.size();
    }

    @Override
    public Collection<V> values() {
        return implementation.values();
    }

}
