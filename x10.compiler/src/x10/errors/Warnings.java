/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.errors;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.frontend.Job;
import polyglot.types.Name;
import polyglot.types.Type;
import polyglot.types.ProcedureInstance;
import polyglot.util.ErrorInfo;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.Configuration;
import x10.ExtensionInfo;
import x10.X10CompilerOptions;
import x10.types.X10Use;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CRequirementCollection;

public class Warnings {

	public static ErrorInfo CastingExprToType(Expr e, Type t, Position p) {
		return new ErrorInfo(ErrorInfo.WARNING, "Expression '" + e + "' was cast to type " + t + ".", p);
	}
    public static void checkErrorAndGuard(ContextVisitor tc, X10Use use, Node n) {
        if (use.error() != null) {
            Errors.issue(tc.job(), use.error(), n);
        }
    }
	public static ErrorInfo GeneratedDynamicCheck(Position p) {                       
		return new ErrorInfo(ErrorInfo.WARNING, "Generated a dynamic check for the method call.", p);
	}

	public static boolean dynamicCall(Job job, ErrorInfo e) {
        final ExtensionInfo extensionInfo = (ExtensionInfo) job.extensionInfo();
        X10CompilerOptions opts = extensionInfo.getOptions();
        if (opts.x10_config.STATIC_CHECKS) {
            return false;
        } else if (opts.x10_config.VERBOSE_CHECKS) {
            Warnings.issue(job, e);
        } else {
            extensionInfo.incrWeakCallsCount();
        }
        return true;
    }

	public static ErrorInfo GeneratedGuard(Name name, CRequirementCollection reqs, CConstraint guard, Position p) {
		return new ErrorInfo(ErrorInfo.WARNING,
				             "Generated guard for the method "+name+":\n"+
							  guard+
							  "\nfrom the collected requirements:\n"+
							  reqs.requirements(),
							 p);
	}
	public static ErrorInfo GeneratedGuardForConstructor(CRequirementCollection reqs, CConstraint guard, Position p) {
		return new ErrorInfo(ErrorInfo.WARNING,
				             "Generated guard for the constructor:\n"+
							  guard+
							  "\nfrom the collected requirements:\n"+
							  reqs.requirements(),
							 p);
	}

	public static boolean inferredGuard(Job job, ErrorInfo e) {
        final ExtensionInfo extensionInfo = (ExtensionInfo) job.extensionInfo();
        X10CompilerOptions opts = extensionInfo.getOptions();
        if (!opts.x10_config.CONSTRAINT_INFERENCE) {
            return false;
        } else if (opts.x10_config.VERBOSE_INFERENCE) {
            Warnings.issue(job, e);
        } else {
            extensionInfo.incrInferredGuardsCount();
        }
        return true;
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
