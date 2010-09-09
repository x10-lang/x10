/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Feb 15, 2006
 */
package x10.query;

import polyglot.ast.Call;
import polyglot.ast.Field;
import polyglot.ast.Unary;
import polyglot.types.ArrayType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import x10.Configuration;
import x10.ExtensionInfo;
import x10.ast.SettableAssign;
import x10.types.X10ClassType;
import x10.types.X10Context;
import x10.types.X10Type;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;

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
	 * @param context TODO
	 * @return true iff a is a dense one-dimensional array with zero origin
	 */
	public boolean isRectangularRankOneLowZero(SettableAssign a, X10Context context) {
		Type t = a.array().type();
	        X10TypeSystem ts = (X10TypeSystem) t.typeSystem();
	        if (X10TypeMixin.isX10Array(t))
	            return (X10TypeMixin.isZeroBased(t, context) && X10TypeMixin.isRankOne(t, context) && X10TypeMixin.isRect(t, context));
	        else
	            return false;
	}

	public boolean needsHereCheck(Type t, X10Context context) {
		/* Removed by RAJ to disable compile time BAD_PLACE_CHECK option*/
		/* Reinstated by Igor because the place checks are still generated.
		   To be removed when the generation is completely disabled. */
		if (!Configuration.BAD_PLACE_RUNTIME_CHECK)
			return false;
            X10TypeSystem ts = (X10TypeSystem) t.typeSystem();
            if (ts.isValueType(t, context))  return false;
		return true;
	}

	public boolean needsHereCheck(Call c, X10Context context) {
		return needsHereCheck(c.target().type(), context);
	}

	public boolean needsHereCheck(Field f, X10Context context) {
		return needsHereCheck(f.target().type(), context);
	}

	public boolean needsHereCheck(SettableAssign a, X10Context context) {
		Type lt = a.leftType();
		X10TypeSystem ts = (X10TypeSystem) lt.typeSystem();
	        if (X10TypeMixin.isX10Array(lt)) {
			return needsHereCheck(X10TypeMixin.arrayBaseType(lt), context);
		}
		return false;
	}

	public boolean needsHereCheck(Unary a, X10Context context) {
	    System.out.println("TODO: needsHereCheck for " + a);
		Type lt = a.expr().type();
		if (lt instanceof ArrayType) {
			ArrayType at = (ArrayType) lt;
			return needsHereCheck(at.base(), context);
		}
		return false;
	}
}

