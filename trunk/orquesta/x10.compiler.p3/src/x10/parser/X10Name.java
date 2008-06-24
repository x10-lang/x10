package x10.parser;

import polyglot.ast.Id;
import polyglot.ast.NodeFactory;
import polyglot.ast.QualifierNode;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.parse.Name;
import polyglot.types.TypeSystem;
import polyglot.util.Position;

public class X10Name extends Name {
	public X10Name(NodeFactory nf, TypeSystem ts, Position pos, Id name) {
		super(nf, ts, pos, name);
	}

	public X10Name(NodeFactory nf, TypeSystem ts, Position pos, Name prefix, Id name) {
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
