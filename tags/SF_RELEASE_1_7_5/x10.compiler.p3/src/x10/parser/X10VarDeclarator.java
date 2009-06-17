//
// Licensed Material 
// (C) Copyright IBM Corp, 2006-2008
//

/*
 * Created by vj on Jan 23, 2005
 */
package x10.parser;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.AmbExpr;
import polyglot.ast.CanonicalTypeNode_c;
import polyglot.ast.FlagsNode;
import polyglot.ast.Formal;
import polyglot.ast.Formal_c;
import polyglot.ast.Id;
import polyglot.ast.NodeFactory;
import polyglot.parse.ParsedName;
import polyglot.parse.VarDeclarator;
import polyglot.types.Flags;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import x10.parser.X10Parser.JPGPosition;

/**
 * @author vj Jan 23, 2005
 * @author igor Jan 13, 2006
 */
public class X10VarDeclarator extends VarDeclarator {
	private final List<Formal> vars;
	public FlagsNode flags;

	public X10VarDeclarator(JPGPosition pos, Id name) {
		this(pos, name, null);
	}

	public X10VarDeclarator(JPGPosition pos, List/*<Name>*/ paramList) {
		//this(pos, polyglot.ext.x10.visit.X10PrettyPrinterVisitor.getId(), paramList);
		// TODO: use the below instead
		this(pos, null, paramList);
	}

	public X10VarDeclarator(JPGPosition pos, Id name, List/*<Name>*/ paramList) {
		super(pos, name);
		if (paramList != null) {
			this.vars = new ArrayList<Formal>(paramList.size());
			for (int i = 0; i < paramList.size(); i++) {
				ParsedName ni = (ParsedName) paramList.get(i);
				TypeSystem ts = ni.ts;
				NodeFactory nf = ni.nf;
				this.vars.add(nf.Formal(ni.pos, flags == null ? nf.FlagsNode(ni.pos, Flags.NONE) : flags, nf.CanonicalTypeNode(ni.pos, ts.Int()), ni.name));
			}
	
		} else {
			vars = null;
		}
	}

	public void setFlag(FlagsNode flags) {
		this.flags = flags;
	}

    public Position position() {
        return pos;
    }
    
    public void position(Position pos) {
        this.pos = pos;
    }
    
	public boolean hasExplodedVars() {
		return vars != null && ! vars.isEmpty();
	}

	public List<Formal> names() {
		return vars;
	}
}

