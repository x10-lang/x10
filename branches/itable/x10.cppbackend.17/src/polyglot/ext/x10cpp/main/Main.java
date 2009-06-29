/*
 * Created on Nov 12, 2007
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package polyglot.ext.x10cpp.main;

import polyglot.ext.x10cpp.visit.X10CPPTranslator;
import polyglot.frontend.Compiler;
import polyglot.main.Options;
import polyglot.util.ErrorQueue;

public class Main extends polyglot.main.Main {

	public Main() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		try {
			new Main().start(args);
		}
		catch (TerminationException te) {
			if (te.getMessage() != null)
				(te.exitCode==0?System.out:System.err).println(te.getMessage());
			System.exit(te.exitCode);
		}
	}
}
