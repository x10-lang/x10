package x10.lang;

/**
 * 
 * @author donawa
 *
 * Provide a mapping from a global array index to the appropriate offset
 * for the place.  One GlobalIndexMap per place
 */
public class GlobalIndexMap{
	public int getDevirtualizedIndex(int index){ return index;} //default: do nothing
}
