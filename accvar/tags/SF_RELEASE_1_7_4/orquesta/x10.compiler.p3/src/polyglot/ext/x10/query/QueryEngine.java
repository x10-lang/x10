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
import polyglot.ast.Unary;
import polyglot.ext.x10.Configuration;
import polyglot.ext.x10.ExtensionInfo;
import polyglot.ext.x10.ast.SettableAssign;
import polyglot.ext.x10.types.X10ArraysMixin;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.ArrayType;
import polyglot.types.SemanticException;
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
	 * @return true iff a is a dense one-dimensional array with zero origin
	 */
	public boolean isRectangularRankOneLowZero(SettableAssign a) {
		Type t = a.array().type();
	        X10TypeSystem ts = (X10TypeSystem) t.typeSystem();
	        if (X10ArraysMixin.isX10Array(t))
	            return X10ArraysMixin.isRail(t) || (X10ArraysMixin.isZeroBased(t) && X10ArraysMixin.isRankOne(t) && X10ArraysMixin.isRect(t));
	        else
	            return false;
	}

	protected boolean needsHereCheck(Type t) {
		/* Removed by RAJ to disable compile time BAD_PLACE_CHECK option*/
		/* Reinstated by Igor because the place checks are still generated.
		   To be removed when the generation is completely disabled. */
		if (!Configuration.BAD_PLACE_RUNTIME_CHECK)
			return false;
            X10TypeSystem ts = (X10TypeSystem) t.typeSystem();
            if (ts.isPoint(t)) return true;
            if (ts.isValueType(t))  return false;
		return true;
	}

	public boolean needsHereCheck(Call c) {
		return needsHereCheck(c.target().type());
	}

	public boolean needsHereCheck(Field f) {
		return needsHereCheck(f.target().type());
	}

	public boolean needsHereCheck(SettableAssign a) {
		Type lt = a.leftType();
		X10TypeSystem ts = (X10TypeSystem) lt.typeSystem();
	        if (X10ArraysMixin.isX10Array(lt)) {
			return needsHereCheck(X10ArraysMixin.arrayBaseType(lt));
		}
		return false;
	}

	public boolean needsHereCheck(Unary a) {
	    System.out.println("TODO: needsHereCheck for " + a);
		Type lt = a.expr().type();
		if (lt instanceof ArrayType) {
			ArrayType at = (ArrayType) lt;
			return needsHereCheck(at.base());
		}
		return false;
	}
}

