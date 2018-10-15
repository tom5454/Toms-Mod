package com.tom.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

public class CountingList<E> extends AbstractList<E> implements ToIntFunction<E>, Function<E, Integer> {
	private Map<E, Counter> map = new HashMap<>();
	private List<E> list = new ArrayList<>();

	@Override
	public E get(int index) {
		return list.get(index);
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public E remove(int index) {
		E e = list.get(index);
		if (e != null) {
			list.remove(e);
			map.remove(e);
		}
		return e;
	}

	@Override
	public void add(int index, E element) {
		if (map.containsKey(element)) {
			map.get(element).increaseCount();
		} else {
			list.add(index, element);
			map.put(element, new Counter());
		}
	}

	public Stream<Entry<E, Counter>> streamResult() {
		return map.entrySet().stream();
	}

	@Override
	public int applyAsInt(E element) {
		return addOrGetCount(element);
	}

	@Override
	public Integer apply(E t) {
		return addOrGetCount(t);
	}
	public int addOrGetCount(E element){
		if (map.containsKey(element)) {
			return map.get(element).increaseCount() + 1;
		} else {
			list.add(element);
			map.put(element, new Counter());
		}
		return 0;
	}
}
