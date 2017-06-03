package com.bob.mapEditor;

import java.util.ArrayList;

public class ObjectList {
	private ArrayList<MyPair<MyPair<Integer, Integer>, String>> objects;
	
	public ObjectList() {
		this.objects = new ArrayList<MyPair<MyPair<Integer, Integer>, String>>();
	}
	
	public boolean contains(MyPair<MyPair<Integer, Integer>, String> pair) {
		if (objects.isEmpty()) {
			return false;
		}
		
		for (MyPair<MyPair<Integer, Integer>, String> object:objects) {
			if (object.equals(pair)) {
				return true;
			}
		}
		return false;
	}
	
	public void add(MyPair<MyPair<Integer, Integer>, String> pair) {
		if (pair != null && !contains(pair)) {
			objects.add(pair);
		}
	}
	
	public boolean del(MyPair<MyPair<Integer, Integer>, String> pair) {
		if (pair != null ) {
			for (MyPair<MyPair<Integer, Integer>, String> object:objects) {
				if (object.equals(pair)) {
					objects.remove(object);
					return true;
				}
			}
		}
		return false;
	}
	
	public void clear() {
		objects.clear();
	}

	public boolean isEmpty() {
		return objects.isEmpty();
	}

	public ArrayList<MyPair<MyPair<Integer, Integer>, String>> getElemts() {
		return objects;
	}

}
