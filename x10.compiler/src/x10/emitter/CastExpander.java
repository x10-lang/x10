/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */
package x10.emitter;

import polyglot.ast.Node;
import polyglot.ast.NullLit;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.visit.Translator;
import x10.X10CompilerOptions;
import x10.visit.X10PrettyPrinterVisitor;

// constants
import static x10.visit.X10PrettyPrinterVisitor.BOX_PRIMITIVES;

public class CastExpander extends Expander {

	private final CodeWriter w;
	private final TypeExpander typeExpander;
	private final Expander child;
	private final Node node;
	private boolean boxConversion;     // flag requesting explicit boxing conversion
	private boolean unboxConversion;   // flag requesting explicit unboxing conversion
	private String location;
	private boolean debug = false;

	private void setLocation(Emitter er) {
	    debug = ((X10CompilerOptions)er.tr.job().extensionInfo().getOptions()).x10_config.DEBUG_CODEGEN;
	    if (!debug) return;
	    StackTraceElement[] stackTrace = new Exception().getStackTrace();
        StringBuffer sb = new StringBuffer();
        for (int i = Math.min(3, stackTrace.length); i >= 3; i--) {
            sb.append(" ");
            sb.append(stackTrace[i].toString());
        }
        location = sb.toString();
	}
	
	public CastExpander(CodeWriter w, Emitter er, TypeExpander typeExpander, Expander child) {
		super(er);
		this.w = w;
		this.typeExpander = typeExpander;
		this.child = child;
		this.node = null;
		setLocation(er);
	}

	public CastExpander(CodeWriter w, Emitter er, Node node) {
		super(er);
		this.w = w;
		this.typeExpander = null;
		this.child = null;
		this.node = node;
		setLocation(er);
	}
	
	private CastExpander setBoxConversion(boolean b) {
	    boxConversion = b;
	    return this;
	}
	
	private CastExpander setUnboxConversion(boolean b) {
	    unboxConversion = b;
	    return this;
	}

	// not used
//	public CastExpander(CodeWriter w, Emitter er, Expander child) {
//		super(er);
//		this.w = w;
//		this.typeExpander = null;
//		this.child = child;
//		this.node = null;
//	}

	public CastExpander castTo(Type castType) {
		return new CastExpander(w, er, new TypeExpander(er, castType, 0), this);
	}

	public CastExpander castTo(Type castType, int flags) {
		return new CastExpander(w, er, new TypeExpander(er, castType, flags), this);
	}
	
	public CastExpander boxTo(Type castType) {
	    return new CastExpander(w, er, new TypeExpander(er, castType, BOX_PRIMITIVES), this)
	        .setBoxConversion(true);
	}
	
	public CastExpander unboxTo(Type castType) {
	    return new CastExpander(w, er, new TypeExpander(er, castType, 0), this)
	        .setUnboxConversion(true);
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		if (typeExpander == null) {
			buf.append("(");
			toStringChild(buf);
			buf.append(")");
		} else {
			buf.append("((");
			buf.append(typeExpander);
			buf.append(")");
			toStringChild(buf);
			w.write(")");
		}
		return "((" + typeExpander + ")" + node + ")";
	}

	private void toStringChild(StringBuffer buf) {
		if (node != null) {
			buf.append(node);
		} else {
			buf.append(child);
		}
	}

	@Override
	public void expand(Translator tr) {
		if (typeExpander == null) {
			if (node instanceof NullLit && !((NullLit) node).type().isNull()) {
		        w.write("((");
	            new TypeExpander(er, ((NullLit) node).type(), 0).expand();
		        w.write(")(");
				expandChild(tr);
				w.write("))");
			} else
			expandChild(tr);
		} else {
		    Type type = typeExpander.type();
		    if (type.isAny() || type.isParameterType() || boxConversion) {
                if (debug) 
                    w.write("/*location:" + location + "*/(");
                else
                    w.write("(");
		        er.printBoxConversion(type);
		        w.write("(");         // required by printBoxConversion
		        expandChild(tr);
		        w.write("))");
		    } else if (unboxConversion) {
		        w.write("(");
		        boolean closeParen = er.printUnboxConversion(type);
		        expandChild(tr);
		        if (closeParen) w.write(")");
		        w.write(")");
		    } else {
                if (debug)
                    w.write("/*location:" + location + "*/((");
                else
                    w.write("((");
		        typeExpander.expand(tr);
		        w.write(")(");
    			expandChild(tr);
    			w.write("))");
		    }
		}
	}

	private void expandChild(Translator tr) {
		Node printExpr = node;
		Expander printExpander = child;

		while (true) {
			if (printExpr != null) {
				er.prettyPrint(printExpr, tr);
			} else {
				if (printExpander instanceof CastExpander) {
					CastExpander castChild = (CastExpander) printExpander;
					if (sameCast(castChild)) {
						printExpr = castChild.node;
						printExpander = castChild.child;
						continue;
					}
				}
				printExpander.expand(tr);
			}
			return;
		}
	}

	private boolean sameCast(CastExpander expander) {
		if (typeExpander == null || expander.typeExpander == null) {
			return typeExpander == null && expander.typeExpander == null;
		}
		return typeExpander.toString().equals(
				expander.typeExpander.toString());

	}
}
