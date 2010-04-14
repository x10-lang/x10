package x10.errors;

import polyglot.ast.Expr;
import polyglot.frontend.Job;
import polyglot.types.Type;
import polyglot.util.ErrorInfo;
import polyglot.util.Position;
import x10.Configuration;
import x10.ExtensionInfo;

public class Warnings {
	
	public static ErrorInfo CastingExprToType(Expr e, Type t, Position p) {
		return new ErrorInfo(ErrorInfo.WARNING, "Expression " + e + " cast to type " + t + ".", p);
	}

	public static void issue(Job job, ErrorInfo e) {
		ExtensionInfo ei = (ExtensionInfo) job.extensionInfo();
		boolean newP = ei.warningSet().add(e);
		if (newP) {
			if (Configuration.VERBOSE_CALLS ) {
			  job.compiler().errorQueue().enqueue(e);
			}
			else {
				if (! Configuration.STATIC_CALLS)
					ei.incrWeakCallsCount();
			}
		}
	}
}
