/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.parser;

import polyglot.ast.Id;
import polyglot.ast.NodeFactory;
import polyglot.ast.QualifierNode;
import polyglot.ast.TypeNode;
import polyglot.parse.ParsedName;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import x10.ast.X10NodeFactory;

public class X10ParsedName extends ParsedName {
	public X10ParsedName(NodeFactory nf, TypeSystem ts, Position pos, Id name) {
		super(nf, ts, pos, name);
	}

	public X10ParsedName(NodeFactory nf, TypeSystem ts, Position pos, ParsedName prefix, Id name) {
		super(nf, ts, pos, prefix, name);
	}

	@Override
	public QualifierNode toQualifier() {
		X10NodeFactory nf = (X10NodeFactory) this.nf;
		if (prefix == null) {
			return nf.AmbQualifierNode(pos, name);
		}

		return nf.X10AmbQualifierNode(pos, prefix.toPrefix(), name);
	}


	@Override
	public TypeNode toType() {
		X10NodeFactory nf = (X10NodeFactory) this.nf;
		if (prefix == null) {
		    return nf.AmbTypeNode(pos, name);
		}
		
		return nf.X10AmbTypeNode(pos, prefix.toPrefix(), name);
	}
}
