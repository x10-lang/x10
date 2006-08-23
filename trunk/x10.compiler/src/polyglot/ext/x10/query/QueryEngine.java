/*
 * Created on Feb 15, 2006
 */
package polyglot.ext.x10.query;

import polyglot.ext.x10.Configuration;
import polyglot.ext.x10.ExtensionInfo;
import polyglot.ast.Call;
import polyglot.ast.Field;
import polyglot.types.Type;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.ast.X10ArrayAccess1;
import polyglot.ext.x10.ast.X10ArrayAccess;
import polyglot.ext.x10.ast.X10ArrayAccess1Assign;
import polyglot.ext.x10.ast.X10ArrayAccessAssign;
import polyglot.ext.x10.ast.X10ArrayAccess1Unary;
import polyglot.ext.x10.ast.X10ArrayAccessUnary;

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

	protected boolean needsHereCheck(Type t) {
		if (!Configuration.BAD_PLACE_RUNTIME_CHECK)
			return false;
		if (t instanceof X10Type && ((X10Type)t).isPoint())
			return true;
		if (t instanceof X10Type && ((X10Type)t).isValueType())
			return false;
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

