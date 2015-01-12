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

package x10.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.CanonicalTypeNode_c;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.frontend.Globals;
import polyglot.frontend.SetResolverGoal;
import polyglot.types.ClassDef;
import polyglot.types.CodeDef;
import polyglot.types.ConstructorDef;
import polyglot.types.Context;
import polyglot.types.Def;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.constraint.XConstraint;
import x10.errors.Errors;
import x10.extension.X10Del;
import x10.types.ClosureDef;
import x10.types.FunctionType_c;
import x10.types.ConstrainedType;

import x10.types.ParameterType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import polyglot.types.Context;

import x10.types.X10ParsedClassType;
import x10.types.XTypeTranslator;

import polyglot.types.TypeSystem;
import x10.types.constraints.CConstraint;
import x10.visit.X10TypeChecker;

/**
 * The X10 version of a CanonicalTypeNode. 
 * Has an associated DepParameterExpr.
 * @author vj
 *
 */
public class X10CanonicalTypeNode_c extends CanonicalTypeNode_c implements X10CanonicalTypeNode, AddFlags {
	
    public X10CanonicalTypeNode_c(Position pos, Type type) {
	this(pos, Types.<Type>ref(type));
    }
    public X10CanonicalTypeNode_c(Position pos, Ref<? extends Type> type) {
	super(pos, type);
    }
    
    Flags flags;
    public void addFlags(Flags f) {
    	flags = f;
    }
  
    @Override
    public Node typeCheck(ContextVisitor tc) {
	Context c = (Context) tc.context();
	TypeSystem ts = (TypeSystem) tc.typeSystem();

	// Expand, and transfer flags from the type node to the type.
	Type t = Types.get(type);
	
	t = ts.expandMacros(t);
	Type xt =  t;
	if (flags != null) {
		xt = Types.processFlags(flags, xt);
		flags = null;
	}
	((Ref<Type>) type).update(xt);

	if (t instanceof ParameterType) {
	    ParameterType pt = (ParameterType) t;
	    Def def = Types.get(pt.def());
	    boolean inConstructor = false;
	    Context p = c;
	    // Pop back to the right context before proceeding
	    while (p.pop() != null && (p.currentClassDef() != def || p.currentCode() instanceof ClosureDef))
	        p = p.pop();
	    if (p.currentCode() instanceof ConstructorDef) {
	        ConstructorDef td = (ConstructorDef) p.currentCode();
	        Type container = Types.get(td.container());
	        if (container instanceof X10ClassType) {
	            X10ClassType ct = (X10ClassType) container;
	            if (ct.def() == def) {
	                inConstructor = true;
	            }
	        }
	    }
	    if (p.inStaticContext() && def instanceof ClassDef && ! inConstructor) {
	        Errors.issue(tc.job(),
	                new Errors.CannotReferToTypeParameterFromStaticContext(pt, def, position()));
	    }
	    if (flags != null && ! flags.equals(Flags.NONE)) {
	    	Errors.issue(tc.job(),
	    	        new Errors.CannotQualifyTypeParameter(pt, def, flags, position()));
	    }
	}

	try {
	    checkType(tc.context(), t, position());
	} catch (SemanticException e) {
	    Errors.issue(tc.job(), e, this);
	}

	List<AnnotationNode> as = ((X10Del) this.del()).annotations();
	if (as != null && !as.isEmpty()) {
	    // Eh.  Why not?
//	    if (c.inAnnotation()) {
//		throw new SemanticException("Annotations not permitted within annotations.", position());
//	    }
	
	    List<Type> annotationTypes = new ArrayList<Type>();
	    for (AnnotationNode an : as) {
	    Type at = an.annotationInterface();
		annotationTypes.add(at);
	    }
	
	    Type newType = ts.AnnotatedType(position(), t, annotationTypes);
	    Ref<Type> tref = (Ref<Type>) type;
	    tref.update(newType);
	}

	X10CanonicalTypeNode n = (X10CanonicalTypeNode) super.typeCheck(tc);
	Type nt = n.type();
	if (nt.isClass()) {
	    X10ClassType ct = (X10ClassType) nt.toClass();
	    if (ct.error() != null && !position().isCompilerGenerated() && ct.error().position() != null) {
	        // The error will have been reported.  Say something sensible instead.
	        String file = position().file();
	        String dfile = ct.error().position().file();
	        if (!file.equals(dfile)) {
	            SemanticException error =
	                new SemanticException(file+" depends on "+dfile+", which has compilation errors");
	            Errors.issue(tc.job(), error, this);
	        }
	    }
	}
	return n;
    }

    // todo: C:\cygwin\home\Yoav\intellij\sourceforge\x10.tests\examples\Benchmarks\SeqArray1.x10
    // C:\cygwin\home\Yoav\intellij\sourceforge\x10.tests\examples\Benchmarks\SeqArray2b.x10
    // C:\cygwin\home\Yoav\intellij\sourceforge\x10.tests\examples\Constructs\Array\Array1.x10
    @Override
    public void setResolver(Node parent, final TypeCheckPreparer v) {
    	if (typeRef() instanceof LazyRef<?>) {
    		LazyRef<Type> r = (LazyRef<Type>) typeRef();
    		if (!r.isResolverSet()) {
    		    TypeChecker tc = new X10TypeChecker(v.job(), v.typeSystem(), v.nodeFactory(), v.getMemo());
                // similarly to TypeNode_c, freezing is not needed.
    		    tc = (TypeChecker) tc.context(v.context().freeze());
    		    r.setResolver(new X10TypeCheckTypeGoal(parent, this, tc, r));
    		}
    	}
    }
    @Override
    public Node conformanceCheck(ContextVisitor tc) {
        Type t = type();
        
        XConstraint c = Types.realX(t);
        
        if (! c.consistent()) {
            Errors.issue(tc.job(),
                    new Errors.InvalidType(t, position()));
        }
        
        TypeSystem ts = (TypeSystem) t.typeSystem();
        
        if (! ts.consistent(t, tc.context())) {
            Errors.issue(tc.job(), new Errors.TypeInconsistent(t,position()));
        }
        
        return this;
    }
    
    public static void checkType(Context context, Type t, Position pos) throws SemanticException {
	if (t == null) throw new SemanticException("Invalid type.", pos);
	
	if (t instanceof ConstrainedType) {
	    ConstrainedType ct = (ConstrainedType) t;
	    Type base = Types.get(ct.baseType());
	    
//	    if (base instanceof ParameterType) {
//	        throw new SemanticException("Invalid type; cannot constrain a type parameter.", position());
//	    }
	    
	    checkType(context, base, pos);
	}
	
	if (t instanceof X10ClassType && ((X10ClassType) t).error()==null) {
	    X10ClassType ct = (X10ClassType) t;
        X10ClassDef def = ct.x10Def();
        final List<Type> typeArgs = ct.typeArguments();
        final int typeArgNum = typeArgs == null ? 0 : typeArgs.size();
        final List<ParameterType> typeParam = def.typeParameters();
        final int typeParamNum = typeParam.size();

        // I want to check that all generic classes have all the required type arguments, i.e.,  X10TypeMixin.checkMissingParameters(t, position())
        // E.g., that you always write: Array[...] and never Array.
        // But that is not true for a static method, e.g., Array.make(...)
        // so instead we do this check in all other places (e.g., field access, method definitions, new calls, etc)
        // But I can check it if there are typeArguments.
        if (typeArgNum > 0) Types.checkMissingParameters(t,pos);
        
	    for (int j = 0; j < typeArgNum; j++) {
	        Type actualType = typeArgs.get(j);
	        Types.checkMissingParameters(actualType,pos);
            
	        ParameterType correspondingParam = typeParam.get(j);
	        if (actualType.isVoid()) {
                    throw new SemanticException("Cannot instantiate invariant parameter " + correspondingParam + " of " + def + " with type " + actualType + ".", pos);
	        }
	    }
	    
	    // A invariant parameter may not be instantiated on a covariant or contravariant parameter.
	    // A contravariant parameter may not be instantiated on a covariant parameter.
	    // A covariant parameter may not be instantiated on a contravariant parameter.
	    for (int j = 0; j < typeArgNum; j++) {
		Type actualType = typeArgs.get(j);
		ParameterType correspondingParam = typeParam.get(j);
		ParameterType.Variance correspondingVariance = def.variances().get(j);
		if (actualType instanceof ParameterType) {
		    ParameterType pt = (ParameterType) actualType;
		    if (pt.def() instanceof X10ClassDef) {
			X10ClassDef actualDef = (X10ClassDef) pt.def();
			for (int i = 0; i < actualDef.typeParameters().size(); i++) {
			    ParameterType.Variance actualVariance;
			    if (i < actualDef.variances().size())
				actualVariance = actualDef.variances().get(i);
			    else
				actualVariance = ParameterType.Variance.INVARIANT;
			    if (pt.typeEquals(actualDef.typeParameters().get(i), context)) {
				switch (correspondingVariance) {
				case INVARIANT:
				    switch (actualVariance) {
				    case CONTRAVARIANT:
					throw new SemanticException("Cannot instantiate invariant parameter " + correspondingParam + " of " + def + " with contravariant parameter " + pt + " of " + actualDef + ".", pos);
				    case COVARIANT:
					throw new SemanticException("Cannot instantiate invariant parameter " + correspondingParam + " of " + def + " with covariant parameter " + pt + " of " + actualDef + ".", pos);
				    }
				    break;
				case CONTRAVARIANT:
				    switch (actualVariance) {
				    case COVARIANT:
					throw new SemanticException("Cannot instantiate contravariant parameter " + correspondingParam + " of " + def + " with covariant parameter " + pt + " of " + actualDef + ".", pos);
				    }
				    break;
				case COVARIANT:
				    switch (actualVariance) {
				    case CONTRAVARIANT:
					throw new SemanticException("Cannot instantiate covariant parameter " + correspondingParam + " of " + def + " with contravariant parameter " + pt + " of " + actualDef + ".", pos);
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
    
    public String toString() {
    	return (flags == null ? "" : flags.toString() + " ") + super.toString();
    }
    
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        prettyPrint(w, tr, true);
    }

    public void prettyPrint(CodeWriter w, PrettyPrinter tr, Boolean extras) {
        if (type == null) {
            w.write("<unknown-type>");
        } else {
            type.get().print(w);
            // vj: printing Parameters should be handled by the type itself.
            // Move this code to ClassType_c.print(CodeWriter).
            /*final X10ParsedClassType baseType = Types.myBaseType(type.get());
              if (extras && baseType!=null
                    && !(baseType instanceof FunctionType_c)) {
                List<Type> typeArguments = baseType.typeArguments();
                if (typeArguments != null && typeArguments.size() > 0) {
                    w.write("[");
                    w.allowBreak(2, 2, "", 0); // miser mode
                    w.begin(0);
                            
                    for (Iterator<Type> i = typeArguments.iterator(); i.hasNext(); ) {
                        Type t = i.next();
                        t.print(w);
                        if (i.hasNext()) {
                        w.write(",");
                        w.allowBreak(0, " ");
                        }
                    }
                    w.write("]");
                    w.end();
                }
            }*/
            if (extras && type.get() instanceof ConstrainedType) {
                ((ConstrainedType) type.get()).printConstraint(w);
            }
       }
      }
}
