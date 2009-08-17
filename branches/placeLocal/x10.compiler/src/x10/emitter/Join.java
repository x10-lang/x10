package x10.emitter;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.visit.Translator;
import x10.visit.X10PrettyPrinterVisitor;

/**
 * Join a given list of arguments with a given delimiter.
 * Two or three arguments can also be specified separately.
 * Do not join a circular list.
 */
public class Join extends Expander {
	/**
	 * 
	 */

	private final String delimiter;
	private final List args;
	public Join(Emitter er, String delimiter, Object a) {
		this(er, delimiter, Collections.singletonList(a));
	}
	public Join(Emitter er, String delimiter, Object a, Object b) {
		this(er, delimiter, Emitter.asList(a, b));
	}
	public Join(Emitter er, String delimiter, Object a, Object b, Object c) {
		this(er, delimiter, Emitter.asList(a, b, c));
	}
	public Join(Emitter er, String delimiter, Object a, Object b, Object c, Object d) {
		this(er, delimiter, Emitter.asList(a, b, c, d));
	}
	public Join(Emitter er, String delimiter, Object a, Object b, Object c, Object d, Object e) {
	    this(er, delimiter, Emitter.asList(a, b, c, d, e));
	}
	public Join(Emitter er, String delimiter, List  args) {
		super(er);
		this.delimiter = delimiter;
		this.args = args;
	}
    public void expand(Translator tr) {
		er.w.write("/* Join: { */");
		int N = args.size();
		for (Iterator i = args.iterator(); i.hasNext(); ) {
			er.prettyPrint(i.next(), tr);
			if (i.hasNext())
				er.prettyPrint(delimiter, tr);
		}
		er.w.write("/* } */");
	}
    public String toString() {
    	return "Join " + er.convertToString(args);
    }
}