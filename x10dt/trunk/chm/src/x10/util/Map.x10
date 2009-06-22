/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/* An X10 implementation of Map
 * 
 * @author Shane Markstrum
 * @javaauthor Josh Bloch
 * @version 08/02/06
 * @see java.util.Map
 */
 package x10.util;

import x10.lang.Object;
import java.util.Set;
import java.util.Collection;

public interface Map {
	
	int size();
	boolean isEmpty();
	boolean containsKey(Object key);
	boolean containsValue(Object value);
	nullable<Object> get(Object key);
	nullable<Object> put(Object key, nullable<Object> value);
	nullable<Object> remove(Object key);
	void putAll(Map t);
	void clear();

	Set keySet();
	Collection valueSet();
	Set entrySet();
	
	interface Entry {
		Object getKey();
		nullable<Object> getValue();
		nullable<Object> setValue(nullable<Object> value);
		boolean equals(Object o);
		int hashCode();
	}
	
	boolean equals(Object o);
	int hashCode();
}
