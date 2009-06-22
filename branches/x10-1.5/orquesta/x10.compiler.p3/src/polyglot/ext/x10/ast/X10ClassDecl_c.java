/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.ast.AmbExpr;
import polyglot.ast.AmbTypeNode;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassDecl_c;
import polyglot.ast.ClassMember;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.FlagsNode;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.QualifierNode;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.ExtensionInfo.X10Scheduler;
import polyglot.ext.x10.extension.X10Del;
import polyglot.ext.x10.extension.X10Del_c;
import polyglot.ext.x10.types.MacroType;
import polyglot.ext.x10.types.ParameterType;
import polyglot.ext.x10.types.PathType;
import polyglot.ext.x10.types.PathType_c;
import polyglot.ext.x10.types.TypeProperty;
import polyglot.ext.x10.types.X10ClassDef;
import polyglot.ext.x10.types.X10ClassDef_c;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10FieldInstance;
import polyglot.ext.x10.types.X10Flags;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.frontend.Globals;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.LazyRef_c;
import polyglot.types.Named;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import polyglot.util.TypedList;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PruningVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
/**
 * The same as a Java class, except that it needs to handle properties.
 * Properties are converted into public final instance fields immediately.
 * TODO: Use the retType for the class during type checking.
 * @author vj
 *
 */
public class X10ClassDecl_c extends ClassDecl_c implements X10ClassDecl {
   
    public static final boolean CLASS_TYPE_PARAMETERS = true;
    
    List<PropertyDecl> properties;

    public List<PropertyDecl> properties() { return properties; }
    public X10ClassDecl properties(List<PropertyDecl> ps) {
	X10ClassDecl_c n = (X10ClassDecl_c) copy();
	n.properties = new ArrayList<PropertyDecl>(ps);
	return n;
    }
    
    List<TypeParamNode> typeParameters;
    
    public List<TypeParamNode> typeParameters() { return typeParameters; }
    public X10ClassDecl typeParameters(List<TypeParamNode> ps) {
	X10ClassDecl_c n = (X10ClassDecl_c) copy();
	n.typeParameters = new ArrayList<TypeParamNode>(ps);
	return n;
    }
    
    List<TypePropertyNode> typeProperties;

    public List<TypePropertyNode> typeProperties() { return typeProperties; }
    public X10ClassDecl typeProperties(List<TypePropertyNode> ps) {
	    X10ClassDecl_c n = (X10ClassDecl_c) copy();
    	n.typeProperties = new ArrayList<TypePropertyNode>(ps);
    	return n;
    }
	
    protected DepParameterExpr classInvariant;
    
    protected X10ClassDecl_c(Position pos, FlagsNode flags, Id name,
	            List<TypeParamNode> typeParameters,
		    List<TypePropertyNode> typeProperties,
		    List<PropertyDecl> properties,
            DepParameterExpr tci,
            TypeNode superClass, List<TypeNode> interfaces, ClassBody body) {
        super(pos, flags, name, superClass, interfaces, body);

        this.typeParameters = TypedList.copyAndCheck(typeParameters, TypeParamNode.class, true);
        this.typeProperties = TypedList.copyAndCheck(typeProperties, TypePropertyNode.class, true);
        this.properties = TypedList.copyAndCheck(properties, PropertyDecl.class, true);
        this.classInvariant = tci;
        
//        this.superClass = superClass;
//        this.interfaces = TypedList.copyAndCheck(interfaces, TypeNode.class, true);
        
        if (CLASS_TYPE_PARAMETERS) {
            this.typeProperties = Collections.EMPTY_LIST;
            this.typeParameters = new TransformingList<TypePropertyNode,TypeParamNode>(typeProperties, new Transformation<TypePropertyNode,TypeParamNode>() {
        	public TypeParamNode transform(TypePropertyNode n) {
        	    X10NodeFactory nf = (X10NodeFactory) Globals.NF();
        	    return nf.TypeParamNode(n.position(), n.name(), n.variance());
        	}
            });
        }
    }
    
    // TODO: do not strip out dependent clauses of parameter types
    
    /** Strip out dependent clauses. */
    public TypeNode simplify(TypeNode tn) {
	if (tn == null)
	    return null;
	return (TypeNode) tn.visit(new NodeVisitor() {
	    @Override
//	    public Node override(Node n) {
//		if (n instanceof Expr || n instanceof Stmt || n instanceof DepParameterExpr)
//		    return n;
//		return null;
////		if (n instanceof TypeNode || n instanceof QualifierNode || n instanceof Formal || n instanceof TypeParamNode)
////		    return null;
////		return n;
//	    }
	    public Node leave(Node old, Node n, NodeVisitor v) {
		if (n instanceof AmbDepTypeNode) {
		    AmbDepTypeNode adtn = (AmbDepTypeNode) n;
		    if (CLASS_TYPE_PARAMETERS) {
			if (adtn.typeArgs().size() > 0) {
			    // Just remove the value args and constraint; keep the type args.
			    return adtn.args(Collections.EMPTY_LIST).constraint(null);
			}
		    }
		    // Remove the type args, value args, and constraint.
		    X10AmbTypeNode atn = ((X10NodeFactory) Globals.NF()).X10AmbTypeNode(n.position(), adtn.prefix(), adtn.name());
		    return atn;
		}
		return n;
	    }
	});
    }
    
    public DepParameterExpr classInvariant() {
    	return classInvariant;
    }
    
    public X10ClassDecl classInvariant(DepParameterExpr tn) {
    	if (this.classInvariant == tn) {
    		return this;
    	}
    	X10ClassDecl_c n = (X10ClassDecl_c) copy();
    	n.classInvariant = tn;
    	return n;
    }
    
    @Override
    protected void setSuperClass(TypeSystem ts, ClassDef thisType) throws SemanticException {
        TypeNode superClass = this.superClass;

        final X10TypeSystem xts = (X10TypeSystem) ts;
        
        // We need to lazily set the superclass, otherwise we go into an infinite loop
        // during bootstrapping: Object, refers to Int, refers to Object, ...
        final LazyRef<Type> superRef = Types.lazyRef(null);

        if (thisType.fullName().equals(QName.make("x10.lang.Ref"))) {
            thisType.superType(null);
        }
        else if (thisType.fullName().equals(QName.make("x10.lang.Value"))) {
            thisType.superType(null);
        }
        else if (thisType.fullName().equals(QName.make("x10.lang.Object"))) {
            thisType.superType(null);
        }
        else if (flags().flags().isInterface()) {
            thisType.superType(null);
        }
        else if (superClass == null && X10Flags.toX10Flags(flags().flags()).isValue()) {
//            thisType.superType(Types.<Type>ref(xts.Value()));
            superRef.setResolver(new Runnable() {
        	public void run() {
        	    superRef.update(xts.Value());
        	}
            });
            thisType.superType(superRef);
        }
        else if (superClass == null) {
//            thisType.superType(Types.<Type>ref(xts.Ref()));
            superRef.setResolver(new Runnable() {
        	public void run() {
        	    superRef.update(xts.Ref());
        	}
            });
            thisType.superType(superRef);
        }
        else {
            super.setSuperClass(ts, thisType);
        }
    }
    
    @Override
    protected void setInterfaces(TypeSystem ts, ClassDef thisType) throws SemanticException {
        if (thisType.fullName().equals(QName.make("x10.lang.Ref"))) {
            thisType.addInterface(Types.ref(ts.Object()));
        }
        else if (thisType.fullName().equals(QName.make("x10.lang.Value"))) {
            thisType.addInterface(Types.ref(ts.Object()));
        }
        else if (thisType.fullName().equals(QName.make("x10.lang.Object"))) {
        }
        else if (interfaces.isEmpty() && flags().flags().isInterface()) {
        }
        else {
            super.setInterfaces(ts, thisType);
        }
    }

    public Node disambiguate(ContextVisitor ar) throws SemanticException {
    	ClassDecl n = (ClassDecl) super.disambiguate(ar);
    	// Now we have successfully performed the base disambiguation.
    	X10Flags xf = X10Flags.toX10Flags(n.flags().flags());
    	if (xf.isSafe()) {
    		ClassBody b = n.body();
    		List<ClassMember> m = b.members();
    		final int count = m.size();
    		List<ClassMember> newM = new ArrayList<ClassMember>(count);
    		for(int i=0; i < count; i++) {
    			ClassMember mem = m.get(i);
        		if (mem instanceof MethodDecl) {
        			MethodDecl decl = (MethodDecl) mem;
        			X10Flags mxf = X10Flags.toX10Flags(decl.flags().flags()).Safe();
        			mem = decl.flags(decl.flags().flags(mxf));
        		} 
        		newM.add(mem);
    		}
    		n = n.body(b.members(newM));
    	}
    	return n;
    }
    
    public Context enterChildScope(Node child, Context c) {
    	X10Context xc = (X10Context) c;
    	
    	if (child != this.body) {
    	       X10ClassDef_c type = (X10ClassDef_c) this.type;
               xc = xc.pushSuperTypeDeclaration(type);
               
               // Add this class to the context, but don't push a class scope.
               // This allows us to detect loops in the inheritance
               // hierarchy, but avoids an infinite loop.
               xc = (X10Context) xc.pushBlock();
               xc.addNamed(type.asType());
               
               // Add type parameters
               for (ParameterType t : type.typeParameters()) {
        	   xc.addNamed(t);
               }
               
               for (ClassMember cm : body.members()) {
        	   if (cm instanceof PropertyDecl) {
        	       PropertyDecl pd = (PropertyDecl) cm;
        	       FieldDef fd = pd.fieldDef();
        	       xc.addVariable(fd.asInstance());
        	   }
               }
               
               // For X10, also add the properties.
//               for (TypeProperty t : type.typeProperties()) {
//		    PathType pt = t.asType();
//		    X10TypeSystem ts = (X10TypeSystem) xc.typeSystem();
//		    try {
//			Type pt2 = PathType_c.pathBase(pt, ts.xtypeTranslator().transThisWithoutTypeConstraint(), type.asType());
//			xc.addNamed((Named) pt2);
//		    }
//		    catch (SemanticException e) {
//		    }
//               }

//               for (FieldDef f : type.properties()) {
//                   xc.addVariable(f.asInstance());
//               }
               
               return child.del().enterScope(xc); 
           }
    	   
//    	   if (child == this.classInvariant) {
//    	       X10ClassDef_c type = (X10ClassDef_c) this.type;
//    	       xc = (X10Context) xc.pushClass(type, type.asType());
//               // Add type parameters
//               for (ParameterType t : type.typeParameters()) {
//        	   xc.addNamed(t);
//               }
//    	       return child.del().enterScope(xc); 
//    	   }
    	   
           if (child == this.body) {
    	       X10ClassDef_c type = (X10ClassDef_c) this.type;
    	       xc = (X10Context) xc.pushClass(type, type.asType());
               // Add type parameters
               for (ParameterType t : type.typeParameters()) {
        	   xc.addNamed(t);
               }
               return child.del().enterScope(xc); 
           }
    	   
           return super.enterChildScope(child, xc);
    }
    
    public Node visitSignature(NodeVisitor v) {
        X10ClassDecl_c n = (X10ClassDecl_c) super.visitSignature(v);
        List<TypePropertyNode> tps = (List<TypePropertyNode>) visitList(this.typeProperties, v);
        n = (X10ClassDecl_c) n.typeProperties(tps);
        List<PropertyDecl> ps = (List<PropertyDecl>) visitList(this.properties, v);
        n = (X10ClassDecl_c) n.properties(ps);
        DepParameterExpr ci = (DepParameterExpr) visitChild(this.classInvariant, v);
        return ci == this.classInvariant ? n : n.classInvariant(ci);
    }
    
    @Override
    public Node buildTypesOverride(TypeBuilder tb) throws SemanticException {
        X10ClassDecl_c n = (X10ClassDecl_c) super.buildTypesOverride(tb);
        
        X10ClassDef def = (X10ClassDef) n.type;
        
        TypeBuilder childTb = tb.pushClass(def);
        
        n = (X10ClassDecl_c) X10Del_c.visitAnnotations(n, childTb);
        
        List<AnnotationNode> as = ((X10Del) n.del()).annotations();
        if (as != null) {
            List<Ref<? extends Type>> ats = new ArrayList<Ref<? extends Type>>(as.size());
            for (AnnotationNode an : as) {
        	ats.add(an.annotationType().typeRef());
            }
            ((X10ClassDef) n.type).setDefAnnotations(ats);
        }
        
	List<TypeParamNode> pas = (List<TypeParamNode>) n.visitList(n.typeParameters, childTb);
        n = (X10ClassDecl_c) n.typeParameters(pas);
        
        for (TypeParamNode tpn : n.typeParameters()) {
            def.addTypeParameter(tpn.type(), tpn.variance());
        }
        
        List<TypePropertyNode> tps = (List<TypePropertyNode>) n.visitList(n.typeProperties, childTb);
        n = (X10ClassDecl_c) n.typeProperties(tps);
        
        List<PropertyDecl> ps = (List<PropertyDecl>) visitList(n.properties, childTb);
        n = (X10ClassDecl_c) n.properties(ps);

        final DepParameterExpr ci = (DepParameterExpr) n.visitChild(n.classInvariant, childTb);
        n = (X10ClassDecl_c) n.classInvariant(ci);

        final LazyRef<XConstraint> c = new LazyRef_c<XConstraint>(new XConstraint_c());

        final X10ClassDecl_c nn = n;
        
        // Add all the constraints on the supertypes into the invariant.
        c.setResolver(new Runnable() {
            public void run() {
        	XConstraint x = new XConstraint_c();
        	try {
        	    if (ci != null) {
        		XConstraint xi = ci.xconstraint().get();
        		x.addIn(xi);
        	    }
        	    if (nn.superClass != null) {
        		Type t = nn.superClass.type();
        		XConstraint tc = X10TypeMixin.xclause(t);
        		if (tc != null)
        		    x.addIn(tc);
        	    }
        	    for (TypeNode tn : nn.interfaces) {
        		Type t = tn.type();
        		XConstraint tc = X10TypeMixin.xclause(t);
        		if (tc != null)
        		    x.addIn(tc);
        	    }
        	}
        	catch (XFailure e) {
        	    x.setInconsistent();
        	}
        	c.update(x);
            }
        });
        
        def.setXClassInvariant(c);

        return n;
    }
    
//    private X10ClassDecl_c disambiguateHeader(TypeBuilder tb) {
//	X10TypeSystem ts = (X10TypeSystem) tb.typeSystem();
//	X10ClassDef_c type = (X10ClassDef_c) this.type;
//	return (X10ClassDecl_c) this.visitChildren(new NodeVisitor() {
//	    public Node override(Node n) {
//		if (n == body)
//		    return n;
//		if (n instanceof AmbExpr) {
//		    AmbExpr e = (AmbExpr) n;
//		    if (e.name)
//		}
//		return null;
//	    }
//	});
//    }

    public Node typeCheckClassInvariant(Node parent, ContextVisitor tc, TypeChecker childtc) throws SemanticException {
	X10ClassDecl_c n = this;
	DepParameterExpr classInvariant = (DepParameterExpr) n.visitChild(n.classInvariant, childtc);
	n = (X10ClassDecl_c) n.classInvariant(classInvariant);
	return n;
    }
    
    public Node typeCheckProperties(Node parent, ContextVisitor tc, TypeChecker childtc) throws SemanticException {
        X10ClassDecl_c n = this;
        List<TypeParamNode> typeParameters = (List<TypeParamNode>) n.visitList(n.typeParameters, childtc);
        n = (X10ClassDecl_c) n.typeParameters(typeParameters);
        List<TypePropertyNode> typeProperties = (List<TypePropertyNode>) n.visitList(n.typeProperties, childtc);
        n = (X10ClassDecl_c) n.typeProperties(typeProperties);
        List<PropertyDecl> properties = (List<PropertyDecl>) n.visitList(n.properties, childtc);
        n = (X10ClassDecl_c) n.properties(properties);
        return n;
    }
    
    public Node typeCheckOverride(Node parent, ContextVisitor tc) throws SemanticException {
    	X10ClassDecl_c n = this;
    	
    	NodeVisitor v = tc.enter(parent, n);
    	
    	if (v instanceof PruningVisitor) {
    		return this;
    	}
    	
    	TypeChecker childtc = (TypeChecker) v;
    	n = (X10ClassDecl_c) n.typeCheckSupers(tc, childtc);
    	n = (X10ClassDecl_c) n.typeCheckProperties(parent, tc, childtc);
    	n = (X10ClassDecl_c) n.typeCheckClassInvariant(parent, tc, childtc);
    	n = (X10ClassDecl_c) n.typeCheckBody(parent, tc, childtc);
    	
    	n = (X10ClassDecl_c) X10Del_c.visitAnnotations(n, childtc);

    	// Make sure the node and type are consistent WRT super types.
        NodeFactory nf = tc.nodeFactory();
        
        if (n.superClass == null && type.superType() != null)
            n = (X10ClassDecl_c) n.superClass(nf.CanonicalTypeNode(position(), type.superType()));
        
        List<TypeNode> newInterfaces = new ArrayList<TypeNode>();
        for (Ref<? extends Type> t : type.interfaces()) {
            boolean added = false;
            for (int i = 0; i < n.interfaces.size(); i++) {
                TypeNode tn = n.interfaces.get(i);
                if (tn.typeRef() == t) {
                    newInterfaces.add(tn);
                    added = true;
                    continue;
                }
            }
            if (! added) {
                TypeNode tn = nf.CanonicalTypeNode(position(), t);
                newInterfaces.add(tn);
            }
        }
        n = (X10ClassDecl_c) n.interfaces(newInterfaces);
        
    	return n;
    }

    @Override
    protected void checkSupertypeCycles(TypeSystem ts) throws SemanticException {
        Ref<? extends Type> stref = type.superType();
        if (stref != null) {
            Type t = stref.get();
            t = followDefs(t);
            if (t instanceof UnknownType)
                throw new SemanticException(); // already reported
            if (! t.isClass() || t.toClass().flags().isInterface()) {
                throw new SemanticException("Cannot extend type " +
                        t + "; not a class.",
                        superClass != null ? superClass.position() : position());
            }
            ts.checkCycles((ReferenceType) t);
        }

        for (Ref<? extends Type> tref : type.interfaces()) {
            Type t = tref.get();
            t = followDefs(t);
            if (t instanceof UnknownType)
                throw new SemanticException(); // already reported
            if (! t.isClass() || ! t.toClass().flags().isInterface()) {
                String s = type.flags().isInterface() ? "extend" : "implement";
                throw new SemanticException("Cannot " + s + " type " + t + "; not an interface.",
                        position());
            }
            ts.checkCycles((ReferenceType) t);
        }
    }

    protected List<TypeNode> followDefs(List<TypeNode> tns) {
	List<TypeNode> newTns = new ArrayList<TypeNode>();
	for (TypeNode tn : tns) {
	    newTns.add(followDefs(tn));
	}
	return newTns;
    }
    
    protected Type followDefs(Type t) {
	if (t instanceof MacroType) {
	    MacroType mt = (MacroType) t;
	    return followDefs(mt.definedType());
	}
	return t;
    }
    
    protected TypeNode followDefs(TypeNode tn) {
	Type t = tn.type();
	Type t2 = followDefs(tn.type());
	if (t2 != t) {
	    Ref<? extends Type> r = tn.typeRef();
	    if (r instanceof LazyRef) {
		((LazyRef<Type>) r).update(t2);
		return tn;
	    }
	    return tn.typeRef(Types.ref(t2));
	}
	return tn;
    }

    public Node conformanceCheck(ContextVisitor tc) throws SemanticException {
    	X10ClassDecl_c result = (X10ClassDecl_c) super.conformanceCheck(tc);

    	X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
    	
    	Type superClass = type.asType().superClass();

    	if (! flags.flags().isInterface()) {
    	    if (X10Flags.toX10Flags(flags.flags()).isValue()) {
    		if (superClass != null && ! ts.isValueType(superClass)) {
    		    throw new SemanticException("Value class " + type + " cannot extend reference class " + superClass + ".", position());
    		}
    	    }
    	    else {
    		if (superClass != null && ts.isValueType(superClass)) {
    		    throw new SemanticException("Reference class " + type + " cannot extend value class " + superClass + ".", position());
    		}
    	    }
    	}
    	else {
            if (superClass != null) {
        	throw new SemanticException("Interface \"" + this.type + "\" cannot have a superclass.",
        	                            superClass.position());
            }
    	}
    	
    	if (superClass != null) {
    	    for (PropertyDecl pd : properties()) {
    	        SemanticException ex = null;
    	        try {
    	            FieldInstance fi = ts.findField(superClass, ts.FieldMatcher(type.asType(), pd.name().id()));
    	            if (fi instanceof X10FieldInstance) {
    	                X10FieldInstance xfi = (X10FieldInstance) fi;
    	                if (xfi.isProperty())
    	                    ex = new SemanticException("Class " + type + " cannot override property " + fi.name() + " of superclass " + Types.get(fi.def().container()) + ".");
    	            }
    	        }
    	        catch (SemanticException e) {
    	            // not found.  That's good.
    	            continue;
    	        }
    	        
    	        throw ex;
    	    }
    	}

    	((X10ClassDef) type).checkRealClause();
	    
    	return result;
    }
    
    @Override
    public Term firstChild() {
        return listChild(properties, this.body);
    }

    /**
     * Visit this term in evaluation order.
     */
    public List<Term> acceptCFG(CFGBuilder v, List<Term> succs) {
	v.visitCFGList(this.properties(), this.body(), ENTRY);
        v.visitCFG(this.body(), this, EXIT);
        return succs;
    }

} 
