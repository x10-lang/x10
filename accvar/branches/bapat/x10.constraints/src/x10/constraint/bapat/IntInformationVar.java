package x10.constraint.bapat;

/**
 * A Var that van have some sort of an integer associated
 * with it.  For now, this is either an integer constant
 * (which always have such an integer value) or a named
 * var (which may or may not have such a value, but when
 * it does uses it to represent its rank).
 */
public abstract class IntInformationVar extends Var {
	
	public abstract boolean hasIntInformation();
	
	public abstract int getIntInformation();
	
	public abstract void setIntInformation(int n);

}
