package polyglot.ext.x10.visit;

import polyglot.visit.*;
import polyglot.ext.x10.ast.*;
import polyglot.ext.x10.types.*;
import polyglot.ast.*;
import polyglot.types.*;
import polyglot.util.*;

public class X10Transmogrifier extends NodeVisitor {
    X10TypeSystem ts;
    X10NodeFactory nf;
    
    final Position CG = Position.COMPILER_GENERATED;
    
    public X10Transmogrifier(TypeSystem ts,
    	    	    	     NodeFactory nf) {
    	this.ts=(X10TypeSystem)ts;
	this.nf=(X10NodeFactory)nf;
    }
}


