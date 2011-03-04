/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Feb 15, 2006
 */
package polyglot.ext.x10.query;

import polyglot.ast.Call;
import polyglot.ast.Field;
import polyglot.ext.x10.Configuration;
import polyglot.ext.x10.ExtensionInfo;
import polyglot.ext.x10.ast.X10ArrayAccess;
import polyglot.ext.x10.ast.X10ArrayAccess1;
import polyglot.ext.x10.ast.X10ArrayAccess1Assign;
import polyglot.ext.x10.ast.X10ArrayAccess1Unary;
import polyglot.ext.x10.ast.X10ArrayAccessAssign;
import polyglot.ext.x10.ast.X10ArrayAccessUnary;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.visit.X10PrettyPrinterVisitor.Template;
import polyglot.types.Type;

/**
 * A query engine for analysis results and other properties of various program
 * elements.
 *
 * @author Igor Peshansky
 */
public class QueryEngine {
	private static QueryEngine instance_;
	public static void init(ExtensionInfo ext) {
		instance_ = new QueryEngine(ext);
	}
	public static QueryEngine INSTANCE() {
		return instance_;
	}

	private ExtensionInfo ext;

	private QueryEngine(ExtensionInfo ext) {
		this.ext = ext;
	}
	
	/**
	 * @param a Array variable being used in one-dimensional access
	 * @return true iff a is a dense one-diemnsional array with zero origin
	 */
	public boolean isRectangularRankOneLowZero(X10ArrayAccess1 a) {
		if ( a.array().type() instanceof X10ParsedClassType ) {
			X10ParsedClassType t = (X10ParsedClassType) a.array().type();
			return (t.isRail() || (t.isZeroBased() && t.isRankOne() && t.isRect()));
		}
		else 
			// a does not represent an X10 array (it may be a distribution or region)
			return false;
	}

	protected boolean needsHereCheck(Type t) {
		/* Removed by RAJ to disable compile time BAD_PLACE_CHECK option*/
		/* Reinstated by Igor because the place checks are still generated.
		   To be removed when the generation is comletely disabled. */
		if (!Configuration.BAD_PLACE_RUNTIME_CHECK)
			return false;
        if (t instanceof X10Type) {
            X10TypeSystem ts = (X10TypeSystem) t.typeSystem();
            if (ts.isPoint(t)) return true;
            if (ts.isValueType(t))  return false;
        }
		return true;
	}

	public boolean needsHereCheck(Call c) {
		return needsHereCheck(c.target().type());
	}

	public boolean needsHereCheck(Field f) {
		return needsHereCheck(f.target().type());
	}

	// TODO: consolidate the below with one interface
	public boolean needsHereCheck(X10ArrayAccess1 a) {
		return needsHereCheck(a.array().type());
	}

	public boolean needsHereCheck(X10ArrayAccess a) {
		return needsHereCheck(a.array().type());
	}

	public boolean needsHereCheck(X10ArrayAccess1Assign a) {
		return needsHereCheck(((X10ArrayAccess1)a.left()).array().type());
	}

	public boolean needsHereCheck(X10ArrayAccessAssign a) {
		return needsHereCheck(((X10ArrayAccess)a.left()).array().type());
	}

	public boolean needsHereCheck(X10ArrayAccess1Unary a) {
		return needsHereCheck(((X10ArrayAccess1)a.expr()).array().type());
	}

	public boolean needsHereCheck(X10ArrayAccessUnary a) {
		return needsHereCheck(((X10ArrayAccess)a.expr()).array().type());
	}
}

