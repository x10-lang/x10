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

package x10.parser;

import polyglot.parse.ParsedName;
import polyglot.util.Position;
import x10.ast.Id;
import x10.ast.NodeFactory;
import x10.ast.QualifierNode;
import x10.ast.TypeNode;
import x10.ast.NodeFactory;
import x10.types.TypeSystem;

public class X10ParsedName extends ParsedName {
	public X10ParsedName(NodeFactory nf, TypeSystem ts, Position pos, Id name) {
		super(nf, ts, pos, name);
	}

	public X10ParsedName(NodeFactory nf, TypeSystem ts, Position pos, ParsedName prefix, Id name) {
		super(nf, ts, pos, prefix, name);
	}

	@Override
	public QualifierNode toQualifier() {
		NodeFactory nf = (NodeFactory) this.nf;
		if (prefix == null) {
			return nf.AmbQualifierNode(pos, name);
		}

		return nf.AmbQualifierNode(pos, prefix.toPrefix(), name);
	}


	@Override
	public TypeNode toType() {
		NodeFactory nf = (NodeFactory) this.nf;
		if (prefix == null) {
		    return nf.AmbTypeNode(pos, name);
		}
		
		return nf.AmbTypeNode(pos, prefix.toPrefix(), name);
	}
}
