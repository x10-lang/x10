/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.lang;

import x10.lang.GlobalIndexMap;

/**
 * 
 * @author donawa
 *
 * Mapping function for block allocated distribution.
 * Assume array is broken up into more or less equal sized
 * chunks, one chunk per place--will be a simple difference
 * of offsets
 */
public class BlockIndexMap extends GlobalIndexMap {
	private int _offsetAdjustment;
	public void setAdjustment(int adjust){ _offsetAdjustment = adjust;}
	public int getDevirtualizedIndex(int index){ return index-_offsetAdjustment;}
	
	public String toString(){
		 StringBuffer s = new StringBuffer("BlockMap:");
		 s.append(_offsetAdjustment);
		 return s.toString();
	}
}