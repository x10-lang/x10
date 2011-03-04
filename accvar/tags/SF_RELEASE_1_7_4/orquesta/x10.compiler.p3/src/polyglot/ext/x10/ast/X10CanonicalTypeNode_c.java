package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.CanonicalTypeNode_c;
import polyglot.ast.Node;
import polyglot.ext.x10.extension.X10Del;
import polyglot.ext.x10.types.ConstrainedType;
import polyglot.ext.x10.types.ParameterType;
import polyglot.ext.x10.types.TypeProperty;
import polyglot.ext.x10.types.X10ClassDef;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.ClassDef;
import polyglot.types.Context;
import polyglot.types.Def;
import polyglot.types.LazyRef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;

public class X10CanonicalTypeNode_c extends CanonicalTypeNode_c implements X10CanonicalTypeNode {
    public X10CanonicalTypeNode_c(Position pos, Ref<? extends Type> type) {
	super(pos, type);
    }
    
    DepParameterExpr expr;
    
    public DepParameterExpr constraintExpr() {
	return expr;
    }
    
    public X10CanonicalTypeNode constraintExpr(DepParameterExpr e) {
	X10CanonicalTypeNode_c n = (X10CanonicalTypeNode_c) copy();
	n.expr = e;
	return n;
    }
    
    @Override
    public Node typeCheck(ContextVisitor tc) throws SemanticException {
	X10Context c = (X10Context) tc.context();
	X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();

	Type t = Types.get(type);
	if (t instanceof ParameterType) {
	    ParameterType pt = (ParameterType) t;
	    Def def = Types.get(pt.def());
	    if (c.inStaticContext() && def instanceof ClassDef) {
		throw new SemanticException("Cannot refer to type parameter " + pt.fullName() + " of " + def + " from a static context.", position());
	    }
	}
	checkType(t);

	List<AnnotationNode> as = ((X10Del) this.del()).annotations();
	if (as == null || as.isEmpty())
	    return this;

	// Eh.  Why not?
//	if (c.inAnnotation()) {
//	    throw new SemanticException("Annotations not permitted within annotations.", position());
//	}
	
	List<Type> annotationTypes = new ArrayList<Type>();
	for (AnnotationNode an : as) {
	    Type at = an.annotationInterface();
	    annotationTypes.add(at);
	}
	
	Type newType = ts.AnnotatedType(position(), t, annotationTypes);
	Ref<Type> tref = (Ref<Type>) type;
	tref.update(newType);
	
	return this;
    }
    
    public void checkType(Type t) throws SemanticException {
	if (t == null) throw new SemanticException("Invalid type.", position());
	if (t instanceof ConstrainedType) {
	    ConstrainedType ct = (ConstrainedType) t;
	    Type base = Types.get(ct.baseType());
	    
	    if (base instanceof ParameterType) {
	        throw new SemanticException("Invalid type; cannot constrained a type parameter.", position());
	    }
	    
	    checkType(base);
	}
	
	if (t instanceof X10ClassType) {
	    X10ClassType ct = (X10ClassType) t;
	    X10ClassDef def = ct.x10Def();
	    if (ct.typeArguments().size() != def.typeParameters().size())
		throw new SemanticException("Invalid type; parameterized class " + def.fullName() + " instantiated with incorrect number of arguments.", position());
	
	    // A invariant parameter may not be instantiated on a covariant or contravariant parameter.
	    // A contravariant parameter may not be instantiated on a covariant parameter.
	    // A covariant parameter may not be instantiated on a contravariant parameter.
	    for (int j = 0; j < ct.typeArguments().size(); j++) {
		Type actualType = ct.typeArguments().get(j);
		ParameterType correspondingParam = def.typeParameters().get(j);
		TypeProperty.Variance correspondingVariance = def.variances().get(j);
		if (actualType instanceof ParameterType) {
		    ParameterType pt = (ParameterType) actualType;
		    if (pt.def() instanceof X10ClassDef) {
			X10ClassDef actualDef = (X10ClassDef) pt.def();
			for (int i = 0; i < actualDef.typeParameters().size(); i++) {
			    TypeProperty.Variance actualVariance;
			    if (i < actualDef.variances().size())
				actualVariance = actualDef.variances().get(i);
			    else
				actualVariance = TypeProperty.Variance.INVARIANT;
			    if (pt.typeEquals(actualDef.typeParameters().get(i))) {
				switch (correspondingVariance) {
				case INVARIANT:
				    switch (actualVariance) {
				    case CONTRAVARIANT:
					throw new SemanticException("Cannot instantiate invariant parameter " + correspondingParam + " of " + def + " with contravariant parameter " + pt + " of " + actualDef);
				    case COVARIANT:
					throw new SemanticException("Cannot instantiate invariant parameter " + correspondingParam + " of " + def + " with covariant parameter " + pt + " of " + actualDef);
				    }
				    break;
				case CONTRAVARIANT:
				    switch (actualVariance) {
				    case COVARIANT:
					throw new SemanticException("Cannot instantiate contravariant parameter " + correspondingParam + " of " + def + " with covariant parameter " + pt + " of " + actualDef);
				    }
				    break;
				case COVARIANT:
				    switch (actualVariance) {
				    case CONTRAVARIANT:
					throw new SemanticException("Cannot instantiate covariant parameter " + correspondingParam + " of " + def + " with contravariant parameter " + pt + " of " + actualDef);
				    }
				    break;
				}
			    }
			}
		    }
		}
	    }
	}
    }
    

}
