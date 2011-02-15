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

package x10.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Formal;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.SystemResolver;
import polyglot.types.Type;
import polyglot.types.Types;

import polyglot.util.Position;
import x10.ast.AnnotationNode;
import x10.ast.Closure;
import x10.ast.ClosureCall;
import x10.ast.X10Local_c;
import x10.constraint.XLocal;
import x10.constraint.XFailure;
import x10.types.ClosureDef;
import x10.types.ClosureInstance;
import x10.types.ClosureType_c;
import x10.types.FunctionType;
import x10.types.ParameterType;
import x10.types.MethodInstance;
import x10.types.X10LocalDef;

import x10.types.ThisDef;
import x10.types.X10ClassDef;
import x10.types.X10ClassDef_c;
import x10.types.X10ClassType;
import polyglot.types.Context;

import x10.types.X10MethodDef;
import polyglot.types.TypeSystem;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CLocal;
import x10.types.constraints.CTerms;
import x10.extension.X10Ext;

public class ClosureSynthesizer {

	/** Return an instance of the AST node, Closure, representing a closure (parms):retType => body.
	 * This is treated as if it were defined by
	 * new Fun_n_m
	 * The type of this node is an anonymous class implementing the interface 
	 * 
	 * @param xts
	 * @param xnf
	 * @param pos
	 * @param retType
	 * @param parms
	 * @param body
	 * @param context
	 * @return
	 */
	public static Closure makeClosure(TypeSystem xts, NodeFactory xnf, Position pos, Type retType, 
			List<Formal> parms, Block body,
			 Context context, List<X10ClassType> annotations) {
	        List<Ref<? extends Type>> fTypes = new ArrayList<Ref<? extends Type>>();
	        List<LocalDef> fNames = new ArrayList<LocalDef>();
	                        for (Formal f : parms) {
	            fTypes.add(Types.ref(f.type().type()));
	            fNames.add(f.localDef());
	        }
	        ClosureDef cDef = xts.closureDef(pos, Types.ref(context.currentClass()),
	                Types.ref(context.currentCode().asInstance()),
	                Types.ref(retType), 
	                //Collections.EMPTY_LIST,
	                fTypes, 
	                null, 
	                fNames, 
	                null, 
	               // Collections.<Ref<? extends Type>>emptyList(), 
	                null);
	        if (null != annotations && !annotations.isEmpty()) {
	            List<Ref<? extends Type>> ats = new ArrayList<Ref<? extends Type>>();
	            for (Type at : annotations) {
	                ats.add(Types.ref(at));
	            }
                List<AnnotationNode> ans = new ArrayList<AnnotationNode>();
                for (Type at : annotations) {
                    ans.add(xnf.AnnotationNode(pos, xnf.CanonicalTypeNode(pos, at)));
                }
	        }
	        Closure closure = (Closure) xnf.Closure(pos, //Collections.EMPTY_LIST,
	                parms, 
	                null, 
	                xnf.CanonicalTypeNode(pos, retType),
	                 body)
	                .closureDef(cDef)
	                .type(closureAnonymousClassDef( xts, cDef).asType());
            if (null != annotations && !annotations.isEmpty()) {
                List<AnnotationNode> ans = new ArrayList<AnnotationNode>();
                for (Type at : annotations) {
                    ans.add(xnf.AnnotationNode(pos, xnf.CanonicalTypeNode(pos, at)));
                }
                closure = (Closure) ((X10Ext) closure.ext()).annotations(ans);
            }
	        return closure;
	    }
	/**
	 * Return the type of an anonymous class implementing a given closure
	 * type, def.
	 * @param xts
	 * @param def
	 * @return
	 */
	public static X10ClassDef closureAnonymousClassDef(final TypeSystem xts, final ClosureDef def) {
        
        final Position pos = def.position();

        X10ClassDef cd = new X10ClassDef_c(xts, null) { 	
            private static final long serialVersionUID = 4543620040069882230L;
            @Override
            public boolean isFunction() { 
                return true;
            }
        };

        cd.position(pos);
        cd.name(null);
        cd.setPackage(null);
        cd.kind(ClassDef.ANONYMOUS);
        cd.outer(Types.ref(def.typeContainer().get().def()));
        cd.flags(Flags.FINAL);

        int numTypeParams = def.typeParameters().size();
        int numValueParams = def.formalTypes().size();

        // Add type parameters.
        List<Ref<? extends Type>> typeParams = new ArrayList<Ref<? extends Type>>();
        List<Type> typeArgs = new ArrayList<Type>();

        ClosureInstance ci = (ClosureInstance) def.asInstance();
        typeArgs.addAll(ci.formalTypes());

        if (!ci.returnType().isVoid()) {
            typeArgs.add(ci.returnType());
        }

        // Instantiate the super type on the new parameters.
        X10ClassType sup = (X10ClassType) closureBaseInterfaceDef(xts, numTypeParams, 
        		numValueParams, 
        		ci.returnType().isVoid(),
        		def.formalNames(),
        		def.guard())
        		.asType();

        assert sup.x10Def().typeParameters().size() == typeArgs.size() : def + ", " + sup + ", " + typeArgs;
        sup = sup.typeArguments(typeArgs);

        // todo: yoav added
        // Adding the method guard
        Ref<CConstraint> guard = def.guard();
        if (guard!=null) {
            CConstraint constraint = guard.get();
            // need to rename the guard variables according to the method parameters
            List<LocalDef> fromNames = def.formalNames();
            MethodInstance instance = sup.methods().get(0);
            List<LocalDef> toNames =  ((X10MethodDef) instance.def()).formalNames();
            for (int i=0; i<fromNames.size(); i++) {
                X10LocalDef fromName = (X10LocalDef) fromNames.get(i);
                X10LocalDef toName = (X10LocalDef) toNames.get(i);
                try {
                    CLocal fromLocal = CTerms.makeLocal(fromName);
                    CLocal toLocal = CTerms.makeLocal(toName);
                    constraint = constraint.substitute(toLocal,fromLocal);
                } catch (XFailure xFailure) {
                    assert false;
                }
            }

            CConstraint result = ((ClosureType_c)sup).getXClause();
            result.addIn(constraint);
            assert result.consistent();

        }
        
        cd.addInterface(Types.ref(sup));

        return cd;
    }

	public static X10ClassDef closureBaseInterfaceDef(final TypeSystem xts, final int numTypeParams,
	        final int numValueParams, final boolean isVoid)
	{
	    return ClosureSynthesizer.closureBaseInterfaceDef(xts, numTypeParams, numValueParams, isVoid, null, null);
	}

	/**
     * Synthetic generated interface for the function types. Mimics an X10 source level definition of
     * the following interface, where numTypeParams=m, and numValueParams=n.
     * 
     * 
     * package x10.lang;
     * public interface Fun_m_n extends Any (
     * 
     *    public abstract def apply[X1,..,Xm,-Z1,..,-Zn,+U](formalNames){guard}:U;
     *    or: 
     *    public abstract def apply[X1,..,Xm,-Z1,..,-Zn](formalNames){guard}:void;
     * }
     * 
     * 
     * @param numTypeParams
     * @param numValueParams
     * @param isVoid
     * @param formalNames
     * @param guard
     * @return
     */
    public static X10ClassDef closureBaseInterfaceDef(final TypeSystem xts, final int numTypeParams, 
    		final int numValueParams, 
    		final boolean isVoid, 
    		List<LocalDef> formalNames1,
            // todo: the guard should not be included in the def
    		final Ref<CConstraint> guard1)
    {
        final Position pos = Position.COMPILER_GENERATED;

        String name = "Fun_" + numTypeParams + "_" + numValueParams;

        if (isVoid) {
            name = "Void" + name;
        }

        // Check if the class has already been defined.
        QName fullName = QName.make("x10.lang", name);
        Type n = SystemResolver.first(xts.systemResolver().check(fullName));

        if (n instanceof X10ClassType) {
            X10ClassType ct = (X10ClassType) n;
            return ct.x10Def();
        }

        X10ClassDef cd = (X10ClassDef) new X10ClassDef_c(xts, null) {
            private static final long serialVersionUID = -2035251841478824351L;
            @Override
            public boolean isFunction() { 
                return true;
            }
            @Override
            public X10ClassType asType() {
                if (asType == null) {
                    X10ClassDef cd = this;
                    asType = new ClosureType_c(xts, pos, this);
                }
                return (X10ClassType) asType;
            }
        };

        cd.position(pos);
        cd.name(Name.make(name));
        try {
            cd.setPackage(Types.ref(xts.packageForName(fullName.qualifier())));
        }
        catch (SemanticException e) {
            assert false;
        }

        cd.kind(ClassDef.TOP_LEVEL);
        cd.superType(null); // interfaces have no superclass
        // Functions implement the Any interface.
        cd.setInterfaces(Collections.<Ref<? extends Type>> singletonList(Types.ref(xts.Any())));
        cd.flags(Flags.PUBLIC.Abstract().Interface());

        final List<ParameterType> typeParams = new ArrayList<ParameterType>();
        final List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>();

        for (int i = 0; i < numTypeParams; i++) {
            ParameterType t = new ParameterType(xts, pos, Name.make("X" + i), Types.ref(cd));
            typeParams.add(t);
        }

        for (int i = 0; i < numValueParams; i++) {
            ParameterType t = new ParameterType(xts, pos, Name.make("Z" + (i + 1)), Types.ref(cd));
            argTypes.add(Types.ref(t));
            cd.addTypeParameter(t, ParameterType.Variance.CONTRAVARIANT);
        }

        cd.setThisDef(xts.thisDef(pos, Types.ref(cd.asType())));

        Type rt = null;

        if (!isVoid) {
            ParameterType returnType = new ParameterType(xts, pos, Name.make("U"), Types.ref(cd));
            cd.addTypeParameter(returnType, ParameterType.Variance.COVARIANT);
            rt = returnType;
        }
        else {
            rt = xts.Void();
        }

        // NOTE: don't call cd.asType() until after the type parameters are
        // added.
        FunctionType ct = (FunctionType) cd.asType();
        xts.systemResolver().install(fullName, ct);

        ThisDef thisDef = cd.thisDef();

        List<LocalDef> formalNames = xts.dummyLocalDefs(argTypes);
        X10MethodDef mi = xts.methodDef(pos, Types.ref(ct),
        		Flags.PUBLIC.Abstract(), Types.ref(rt),
        		ClosureCall.APPLY, 
        		typeParams, 
        		argTypes, 
        		thisDef,
        		formalNames, 
        		null,//todo: it was guard1
        		null, 
        	
        		null, // offerType
        		null);
        cd.addMethod(mi);

        return cd;
    }

}
