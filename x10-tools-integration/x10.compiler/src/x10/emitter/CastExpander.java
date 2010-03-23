/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */
package x10.emitter;

import polyglot.ast.Node;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.visit.Translator;

public class CastExpander extends Expander {

	private final CodeWriter w;
	private final TypeExpander typeExpander;
	private final Template template;
	private final Expander child;
	private final Node node;

	public CastExpander(CodeWriter w, Emitter er, TypeExpander typeExpander,
			Expander child) {
		super(er);
		this.w = w;
		this.typeExpander = typeExpander;
		this.child = child;
		this.template = null;
		this.node = null;
	}

	public CastExpander(CodeWriter w, Emitter er, Node node) {
		super(er);
		this.w = w;
		this.typeExpander = null;
		this.child = null;
		this.template = null;
		this.node = node;
	}

	public CastExpander(CodeWriter w, Emitter er, Expander child) {
		super(er);
		this.w = w;
		this.typeExpander = null;
		this.child = child;
		this.template = null;
		this.node = null;
	}

	public CastExpander(CodeWriter w, Emitter er, Template template) {
		super(er);
		this.w = w;
		this.typeExpander = null;
		this.child = null;
		this.template = template;
		this.node = null;
	}

	public CastExpander castTo(Type castType) {
		return new CastExpander(w, er, new TypeExpander(er, castType, 0), this);
	}

	public CastExpander castTo(Type castType, int flags) {
		return new CastExpander(w, er, new TypeExpander(er, castType, flags),
				this);
	}

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
		} else if (template != null) {
			buf.append(template);
		} else {
			buf.append(child);
		}
	}

	@Override
	public void expand(Translator tr) {
		if (typeExpander == null) {
			expandChild(tr);
		} else {
			w.write("((");
			typeExpander.expand(tr);
			w.write(")(");
			expandChild(tr);
			w.write("))");
		}
	}

	private void expandChild(Translator tr) {
		Node printExpr = node;
		Template printTemplate = template;
		Expander printExpander = child;

		while (true) {
			if (printExpr != null) {
				er.prettyPrint(printExpr, tr);
			} else if (printTemplate != null) {
				printTemplate.expand(tr);
			} else {
				if (printExpander instanceof CastExpander) {
					CastExpander castChild = (CastExpander) printExpander;
					if (sameCast(castChild)) {
						printExpr = castChild.node;
						printTemplate = castChild.template;
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