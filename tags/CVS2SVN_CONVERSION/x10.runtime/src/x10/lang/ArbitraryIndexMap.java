/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.lang;

import x10.lang.GlobalIndexMap;
import java.util.Hashtable;
import java.lang.System;
/**
 * 
 * @author donawa
 *
 * Mapping function for block allocated distribution.
 * Assume array is broken up into more or less equal sized
 * chunks, one chunk per place--will be a simple difference
 * of offsets
 */
public class ArbitraryIndexMap extends GlobalIndexMap {
	private Hashtable _indexMap;
	
	class myKey {
		int _val;
		myKey(int value){ _val = value;}
		public int hashCode() { return _val;}
		public boolean equals(java.lang.Object o){
			myKey v = (myKey)o;
			return v._val == _val;
		}
		public String toString(){
			StringBuffer s = new StringBuffer("val=");
			s.append(_val);
			return s.toString();
		}
	}
	
	ArbitraryIndexMap(){
		super();
		_indexMap = new Hashtable();
	}
	
	public void addMapping(int index, int newIndex){
		_indexMap.put(new myKey(index),new myKey(newIndex));
		if(false)System.err.println("enter "+ newIndex+" for "+index+" "+this);
	}
	public int getDevirtualizedIndex(int index){
		myKey realIndex = (myKey)_indexMap.get(new myKey(index));
		if(false)System.err.println("retrieved value "+ realIndex+" for "+index+" "+this);
		return realIndex._val;}
	
	public String toString(){
		 StringBuffer s = new StringBuffer("ArbitraryMap:");
		 s.append(_indexMap);
		 return s.toString();
	}
}
