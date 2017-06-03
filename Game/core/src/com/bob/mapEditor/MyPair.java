package com.bob.mapEditor;

import java.util.Objects;

public class MyPair<F,S> {
	private F key;
    private S value;

    public MyPair(F first, S second)
    {
        this.key = first;
        this.value = second;
    }

	public F key()   { return key; }
    public S value() { return value; }
    public void setKey(F newKey) { key = newKey; }
    public void setValue(S newValue) { value = newValue; }
    
    /*public boolean compareTo(MyPair<F,S> p) {
    	if(p != null) {
    		if (key == p.key() && value == p.value()) {
    			return true;
    		}
    	}
    	return false;
    }*/
    
    public boolean equals(Object o) {
        if (!(o instanceof MyPair)) {
            return false;
        }
        MyPair<?, ?> p = (MyPair<?, ?>) o;
        return Objects.equals(p.key(), this.key) && Objects.equals(p.value(), this.value);
    }

}
