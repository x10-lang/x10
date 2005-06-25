package x10.runtime;

/**
 * 
 * @author donawa
 *
 * Provide a handle that can be used by remote objects to manipulate data on this
 * VM, and vice versa.
 */
public class FatPointer {
	private Object _objectHandle;
	private int _key;
	
	final public int getKey(){ return _key;}
	final public void setKey(int k){_key = k;}
	final public Object getObject(){ return _objectHandle;}
	final public int setObject(Object o){_objectHandle = o; return _key=o.hashCode();}
	
	
	public String toString(){
		StringBuffer s = new StringBuffer("FatPointer::key=");
		s.append(_key);
		s.append(" Object=");
		s.append(_objectHandle.hashCode());
		return s.toString();
	}
}