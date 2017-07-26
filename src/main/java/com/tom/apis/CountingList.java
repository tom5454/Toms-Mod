package com.tom.apis;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

public class CountingList<E> extends AbstractList<E> {
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
}
