package x10.constraint.smt;


import x10.constraint.XType;

/**
 * Interface for outputting XSmtTerms in formats suitable for various solvers. 
 * @author lshadare
 *
 * @param <T> the type parameter of the XTerm<T>
 */
public interface XPrinter<T extends XType> {
	/**
	 * Examine the term and collect necessary declaration information. 
	 * @param term
	 */
	public void out(XSmtTerm<T> term);
	/**
	 * Append the particular sequence to the output stream of this printer. 
	 * @param string
	 */
	public void append(CharSequence string);
	/**
	 * Constructs a special variable for null, one for each type. 
	 * @param type
	 * @return
	 */
	public String nullVar(T type);
	/**
	 * Removes illegal characters from the string making it a valid 
	 * variable name for the particular format being used. 
	 * @param string
	 * @return
	 */
	public String mangle(String string);
	/**
	 * Write the output to file.
	 */
	public void dump();
}
