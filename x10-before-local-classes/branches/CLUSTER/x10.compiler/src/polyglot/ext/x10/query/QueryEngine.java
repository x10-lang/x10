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

	public boolean needsHereCheck(Call c) {
		if (!Configuration.BAD_PLACE_RUNTIME_CHECK)
			return false;
		Type t = c.target().type();
		if (t instanceof X10Type && ((X10Type)t).isValueType())
			return false;
		return true;
	}

	public boolean needsHereCheck(Field f) {
		if (!Configuration.BAD_PLACE_RUNTIME_CHECK)
			return false;
		Type t = f.target().type();
		if (t instanceof X10Type && ((X10Type)t).isValueType())
			return false;
		return true;
	}
}

