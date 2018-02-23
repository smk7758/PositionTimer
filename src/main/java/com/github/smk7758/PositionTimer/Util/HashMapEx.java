package com.github.smk7758.PositionTimer.Util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import javafx.util.Pair;

public class HashMapEx<K, X, Y> {
	private HashMap<K, Pair<X, Y>> hashmap = new HashMap<>();

	public HashMapEx() {
	}

	public void put(K key, X x, Y y) {
		hashmap.put(key, new Pair<X, Y>(x, y));
	}

	public X getSecond(K key) {
		return hashmap.get(key).getKey();
	}

	public Y getThird(K key) {
		return hashmap.get(key).getValue();
	}

	public K getKeyFromSecond(X x) {
		for (K key : hashmap.keySet()) {
			Pair<X, Y> pair = hashmap.get(key);
			if (pair.getKey().equals(x)) {
				return key;
			}
		}
		return null;
	}

	public K getKeyFromThird(Y y) {
		for (K key : hashmap.keySet()) {
			Pair<X, Y> pair = hashmap.get(key);
			if (pair.getValue().equals(y)) {
				return key;
			}
		}
		return null;
	}

	public Optional<K> getFirstKey() {
		return hashmap.keySet().stream().findFirst();
	}

	public Optional<X> getFirstSecond() {
		Optional<Pair<X, Y>> value = hashmap.values().stream().findFirst();
		if (value.isPresent()) {
			return Optional.ofNullable(value.get().getKey());
		} else {
			return Optional.empty();
		}
	}

	public Optional<Y> getFirstThird() {
		Optional<Pair<X, Y>> value = hashmap.values().stream().findFirst();
		if (value.isPresent()) {
			return Optional.ofNullable(value.get().getValue());
		} else {
			return Optional.empty();
		}
	}

	public int size() {
		return hashmap.size();
	}

	public Set<HashMapEx<K, X, Y>> itemSet() {
		Set<HashMapEx<K, X, Y>> sets = new HashSet<>();
		for (Entry<K, Pair<X, Y>> entry : hashmap.entrySet()) {
			HashMapEx<K, X, Y> ex = new HashMapEx<>();
			ex.put(entry.getKey(), entry.getValue().getKey(), entry.getValue().getValue());
			sets.add(ex);
		}
		return sets;
	}
}
