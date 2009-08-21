package x10.emitter;

import polyglot.visit.Translator;
import x10.visit.X10PrettyPrinterVisitor;

/**
 * An abstract class for sub-template expansion.
 */
public class Inline extends Expander {
	/**
	 * 
	 */

	private final String str;
	public Inline(Emitter er, String str) {
		super(er);
		this.str = str;
	}
	public void expand(Translator tr) {
		er.prettyPrint(str, tr);
	}
}