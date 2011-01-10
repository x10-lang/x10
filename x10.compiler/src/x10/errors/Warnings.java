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
		return new ErrorInfo(ErrorInfo.WARNING, "Expression '" + e + "' was cast to type " + t + ".", p);
	}

	public static void issue(Job job, String message, Position pos) {
		issue(job, new ErrorInfo(ErrorInfo.WARNING, message, pos));
	}
	public static void issue(Job job, ErrorInfo e) {
		issue((ExtensionInfo) job.extensionInfo(), e);
	}
	public static void issue(ExtensionInfo extInfo, ErrorInfo e) {
		boolean newP = extInfo.warningSet().add(e);
		if (newP) {
			extInfo.compiler().errorQueue().enqueue(e);
		}
	}
}
