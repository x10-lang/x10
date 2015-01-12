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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.HashSet;

import polyglot.ast.Block;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl_c;
import polyglot.ast.ClassMember;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Expr;
import polyglot.ast.FlagsNode;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.ast.Special;
import polyglot.ast.Field;
import polyglot.ast.Call;
import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.main.Reporter;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.ContainerType;
import polyglot.types.Context;
import polyglot.types.Def;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.LazyRef_c;
import polyglot.types.LocalDef;
import polyglot.types.MemberDef;
import polyglot.types.TypeSystem_c;

import polyglot.types.Name;
import polyglot.types.ObjectType;
import polyglot.types.QName;
import polyglot.types.Ref;

import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.PruningVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

import x10.errors.Errors;
import x10.extension.X10Del;
import x10.extension.X10Del_c;
import x10.extension.X10Ext;
import x10.types.MacroType;
import x10.types.ParameterType;
import x10.types.TypeDef;
import x10.types.TypeParamSubst;
import x10.types.X10ClassDef;
import x10.types.X10ClassDef_c;
import x10.types.X10ClassType;
import x10.types.X10ParsedClassType_c;
import x10.types.X10FieldInstance;

import x10.types.X10LocalDef;
import x10.types.X10MethodDef;
import x10.types.MethodInstance;
import x10.types.X10ParsedClassType;
import x10.types.X10TypeEnv_c;

import x10.types.constraints.CConstraint;
import x10.types.constraints.ConstraintManager;
import x10.types.constraints.TypeConstraint;
import x10.util.Synthesizer;
import x10.visit.ChangePositionVisitor;

/**
 * The same as a Java class, except that it needs to handle properties.
 * Properties are converted into public final instance fields immediately.
 * TODO: Use the retType for the class during type checking.
 * @author vj
 *
 */
public class X10ClassDecl_c extends ClassDecl_c implements X10ClassDecl {

    protected FlagsNode flags;
    protected Id name;
    protected TypeNode superClass;
    protected List<TypeNode> interfaces;
    protected ClassBody body;
    protected ConstructorDef defaultCI;
    protected X10ClassDef type;
    
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
	
    protected DepParameterExpr classInvariant;
    
    protected X10ClassDecl_c(Position pos, FlagsNode flags, Id name,
	            List<TypeParamNode> typeParameters,
		    List<PropertyDecl> properties,
		    DepParameterExpr tci,
            TypeNode superClass,
            List<TypeNode> interfaces, ClassBody body) {
        super(pos, flags, name, superClass, interfaces, body);

        this.typeParameters = TypedList.copyAndCheck(typeParameters, TypeParamNode.class, true);
        this.properties = TypedList.copyAndCheck(properties, PropertyDecl.class, true);
        this.classInvariant = tci;
        
        assert(flags != null && name != null && interfaces != null && body != null); 
        this.flags = flags;
        this.name = name;
        this.superClass = superClass;
        this.interfaces = TypedList.copyAndCheck(interfaces, TypeNode.class, true);
        this.body = body;
        
//        this.superClass = superClass;
//        this.interfaces = TypedList.copyAndCheck(interfaces, TypeNode.class, true);
    }
    
    // TODO: do not strip out dependent clauses of parameter types
    
    /** Strip out dependent clauses. */
  /*  public TypeNode simplify(TypeNode tn) {
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
		    return adtn.constraint(null);
		}
		if (n instanceof AmbMacroTypeNode) {
		    AmbMacroTypeNode adtn = (AmbMacroTypeNode) n;
		    if (adtn.typeArgs().size() > 0) {
		        // Just remove the value args and constraint; keep the type args.
		        return adtn.args(Collections.EMPTY_LIST);
		    }
		    // Remove the type args, value args, and constraint.
		    X10AmbTypeNode atn = ((X10NodeFactory) Globals.NF()).X10AmbTypeNode(n.position(), adtn.prefix(), adtn.name());
		    return atn;
		}
		return n;
	    }
	});
    }*/
    
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
    protected void setSuperClass(TypeSystem ts, ClassDef thisType) {
        TypeNode superClass = this.superClass;

        final TypeSystem xts = (TypeSystem) ts;
        
        // We need to lazily set the superclass, otherwise we go into an infinite loop
        // during bootstrapping: Object, refers to Int, refers to Object, ...
        final LazyRef<Type> superRef = Types.lazyRef(null);

        if (flags().flags().isInterface()) {
        	thisType.superType(null);
        }
        else if (superClass == null) {
        	// [DC] no root for classes anymore
        	thisType.superType(null);
        }
        else {
        	superSetSuperClass(ts, thisType);
        }
    }
    
    private void superSetSuperClass(TypeSystem ts, ClassDef thisType) {
        TypeNode superClass = this.superClass;
        Reporter reporter = ts.extensionInfo().getOptions().reporter;
        
        if (superClass != null) {
            Ref<? extends Type> t = superClass.typeRef();
            if (reporter.should_report(Reporter.types, 3))
                reporter.report(3, "setting superclass of " + this.type + " to " + t);
            thisType.superType(t);
        }
        else {
            // the superclass was not specified, and the type is not the same
            // as ts.Object() (which is typically java.lang.Object)
            // As such, the default superclass is ts.Object().
            if (reporter.should_report(Reporter.types, 3))
                reporter.report(3, "setting superclass of " + this.type + " to " + null);
            thisType.superType(null);
        }
    }
    
    @Override
    protected void setInterfaces(TypeSystem ts, ClassDef thisType) {
    	final TypeSystem xts = (TypeSystem) ts;
    	Reporter reporter = xts.extensionInfo().getOptions().reporter;

    	// For every struct and interface, add the implicit Any interface.
    	Flags flags =  flags().flags();
    	if (flags.isStruct()
    			|| (flags.isInterface() && ! name.toString().equals("Any"))
    			|| xts.isParameterType(thisType.asType())) {
    		thisType.addInterface(xts.lazyAny());
    	}
    	
    	List<TypeNode> interfaces = this.interfaces;
        for (TypeNode tn : interfaces) {
            Ref<? extends Type> t = tn.typeRef();

            if (reporter.should_report(Reporter.types, 3))
                reporter.report(3, "adding interface of " + thisType + " to " + t);

            thisType.addInterface(t);
        }
    }

    public X10ClassDecl flags(FlagsNode flags) {
    	X10ClassDecl_c n = (X10ClassDecl_c) copy();
        n.flags = flags;
        return n;
    }
    public X10ClassDecl name(Id name) {
    	X10ClassDecl_c n = (X10ClassDecl_c) copy();
        n.name = name;
        return n;
    }
    public X10ClassDecl superClass(TypeNode superClass) {
    	X10ClassDecl_c n = (X10ClassDecl_c) copy();
        n.superClass = superClass;
        return n;
    }
    public X10ClassDecl body(ClassBody body) {
    	X10ClassDecl_c n = (X10ClassDecl_c) copy();
        n.body = body;
        return n;
    }
    public X10ClassDecl interfaces(List<TypeNode> interfaces) {
    	X10ClassDecl_c n = (X10ClassDecl_c) copy();
        n.interfaces = TypedList.copyAndCheck(interfaces, TypeNode.class, true);
        return n;
    }
    public X10ClassDecl classDef(X10ClassDef type) {
    	if (type == this.type) return this;
        X10ClassDecl_c n = (X10ClassDecl_c) copy();
        n.type = type;
        return n;
    }
    public X10ClassDef classDef() {
        return  type;
    }

    @Override
    public Context enterScope(Context c) {
    	c = c.pushCode(classDef());
    	return c.pushBlock();
    }

    @Override
    public Context enterChildScope(Node child, Context c) {
        X10ClassDef_c type = (X10ClassDef_c) this.type;
    	if (child != this.body ) {
    		if (child == this.classInvariant) {
        		c = c.pushClass(type, type.asType());
    		} else {
    			c = c.pushSuperTypeDeclaration(type);

    			// Add this class to the context, but don't push a class scope.
    			// This allows us to detect loops in the inheritance
    			// hierarchy, but avoids an infinite loop.
    			c = c.pushBlock();
    			c.addNamed(type.asType());
    			
    			// add class invariant, XTENLANG-3125
    			if (classInvariant != null) {
					c.addConstraint(type.classInvariant()); // is this needed too?
					c.setCurrentTypeConstraint(classInvariant.typeConstraint());
    			}
    		}

    		// Add type parameters
    		for (ParameterType t : type.typeParameters()) {
    			c.addNamed(t);
    		}

    		for (PropertyDecl pd : properties) {
    			FieldDef fd = pd.fieldDef();
    			c.addVariable(fd.asInstance());
    			if (pd==child)
    				break; // do not add downstream properties
    		}
    	} else {
    	    c = c.pushClass(type, type.asType());
    	    // Add type parameters
    	    for (ParameterType t : type.typeParameters()) {
    	        c.addNamed(t);
    	    }
    	    // The class invariant should be added only when entering 
    	    // children (field, method) that are not static. 
    	    /* DepParameterExpr v = classInvariant();
    	    
    	    if (v != null) {
    	        Ref<TypeConstraint> tc = v.typeConstraint();
    	        if (tc != null) {
    	            c.setCurrentTypeConstraint(tc); // todo: what about setCurrentConstraint ?
    	        }
    	    }*/
    	}
        return super.enterChildScope(child, c); 
    	
    }
    
    public Node visitSignature(NodeVisitor v) {
    	FlagsNode flags = (FlagsNode) visitChild(this.flags, v);
        Id name = (Id) visitChild(this.name, v);
        List<TypeParamNode> tps = visitList(this.typeParameters, v);
        TypeNode superClass = (TypeNode) visitChild(this.superClass, v);
        List<TypeNode> interfaces = visitList(this.interfaces, v);
        DepParameterExpr ci = (DepParameterExpr) visitChild(this.classInvariant, v);
        List<PropertyDecl> ps = visitList(this.properties, v);
        ClassBody body = this.body;
        X10ClassDecl_c n = (X10ClassDecl_c) reconstruct(flags, name, superClass, interfaces, body);
        if (!CollectionUtil.allEqual(tps, this.typeParameters) || !CollectionUtil.allEqual(ps, this.properties) || ci != this.classInvariant) {
            if (n == this) n = (X10ClassDecl_c) n.copy();
            n.typeParameters = new ArrayList<TypeParamNode>(tps);
            n.properties = new ArrayList<PropertyDecl>(ps);
            n.classInvariant = ci;
        }
        return n;
    }
    
    @Override
    public X10ClassDecl_c preBuildTypes(TypeBuilder tb) {
        X10ClassDecl_c n = (X10ClassDecl_c) superPreBuildTypes(tb);
        
        final X10ClassDef def = (X10ClassDef) n.type;

        def.setThisDef(tb.typeSystem().thisDef(n.position(), Types.ref(def.asType())));
        
        TypeBuilder childTb = tb.pushClass(def);
        
        List<TypeParamNode> pas = n.visitList(n.typeParameters, childTb);
        // [IP] Don't do this here.  It's the job of X10InnerClassRemover
//        pas = new LinkedList<TypeParamNode>(pas);
//        
//        if (def.isMember() && ! def.flags().isStatic()) {
//            X10ClassDef outer = (X10ClassDef) Types.get(def.outer());
//            while (outer != null) {
//                X10NodeFactory nf = (X10NodeFactory) tb.nodeFactory();
//                for (int i = 0; i < outer.typeParameters().size(); i++) {
//                    ParameterType pt = outer.typeParameters().get(i);
//                    TypeParamNode tpn = nf.TypeParamNode(pt.position(), nf.Id(pt.position(), pt.name()));
//                    tpn = tpn.variance(outer.variances().get(i));
//                    tpn = (TypeParamNode) n.visitChild(tpn, childTb);
//                    pas.add(tpn);
//                }
//                
//                if (outer.isMember())
//                    outer = (X10ClassDef) Types.get(outer.outer());
//                else
//                    outer = null;
//            }
//        }
        
        n = (X10ClassDecl_c) n.typeParameters(pas);
        
        for (TypeParamNode tpn : n.typeParameters()) {
            def.addTypeParameter(tpn.type(), tpn.variance());
        }

        ///* [DC] going to hack the typesystem to allow calls to these special Any methods even if they don't exist.
        // [DC] actually, no.  going to do that for objects but not structs, and do something else for structs later
        if (flags().flags().isStruct())
            n = x10.util.Struct.addStructMethods(tb,n);
		//*/


        //I need a way to access the outer instance when generating dynamic_checks.
        //For example:
        //class A(a:Any) {
        //  class B(b:Any) {
        //    def m() {a!=null} {...}
        //  }
        //}
        //class Test {
        //  def test(b:B) {
        //    b.m(); // with DYNAMIC_CHECKS it will check that "a!=null", by dynamically inserting this code:  "if (!(b.OUTER.a!=null)) throw new FailedDynamicCheckException(); b.m()"
        //  }
        //}
        //However, we don't have a way to access the outer instance!
        //So we generate methods that return it.
        //
        // adding methods to access the outer instances (used in Desugarer.desugarCall)
        // The method name includes both the container name and the qualifier name to handle this nasty case:
        //class A[T] {
        //  public final def A$$A$this() = A.this;
        //  class Inner extends A[Int] {
        //    public final def Inner$$A$this() = A.this; // the return type is different!
        //    public final def Inner$$Inner$this() = Inner.this;
        //  }
        //}
        // Another simpler example:
        // class A {
        //  public final def A$$A$this() = A.this;
        //  class B {
        //   public final def B$$B$this() = B.this;
        //   public final def B$$A$this() = A.this;
        //  }
        //  class D extends B {
        //   public final def D$$D$this() = D.this;
        //   public final def D$$A$this() = A.this;
        //  }
        //  static class C {
        //   public final def C$$C$this() = C.this;
        //  }
        // }
        {
            final Position pos = n.position().markCompilerGenerated();
            final Flags flags = Flags.PUBLIC.Final();
            final NodeFactory nf = tb.nodeFactory();

            final QName containerName = def.fullName();
            ClassType curr = def.asType();
            while (curr!=null) {
                if (curr.flags().isInterface())
                    break;
                final UnknownTypeNode returnType = nf.UnknownTypeNode(pos);
                final QName fullName = curr.fullName();
                MethodDecl md = nf.MethodDecl(pos,nf.FlagsNode(pos,flags),returnType,nf.Id(pos,getThisMethod(containerName,fullName)),Collections.<Formal>emptyList(),
                        nf.Block(pos,nf.Return(pos,nf.Special(pos, Special.Kind.THIS, nf.CanonicalTypeNode(pos, curr)))));
                ArrayList<AnnotationNode> ans = new ArrayList<AnnotationNode>(1);
                AnnotationNode synthetic = nf.AnnotationNode(pos, nf.AmbMacroTypeNode(pos, nf.PrefixFromQualifiedName(pos,QName.make("x10.compiler")), nf.Id(pos, "Synthetic"), Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList()));
                ans.add(synthetic);
                md = (X10MethodDecl) ((X10Ext) md.ext()).annotations(ans);
                n = (X10ClassDecl_c) n.body(n.body().addMember(md));

                if (curr.flags().isStatic() || !curr.isMember()) break; // a non-member class doesn't have an outer instance
                curr = curr.outer();
            }
        }


        return n;
    }
    public static Name getThisMethod(QName containerName, QName n) {
        return Name.make(
            containerName.toString().replace('.','$')+ "$"+ "$this"
                +"$"+ n.toString().replace('.','$')
            );
    }

    private SemanticException errorInAST;

    public SemanticException errorInAST() {
        return errorInAST;
    }

    public X10ClassDecl_c errorInAST(SemanticException error) {
        X10ClassDecl_c n = (X10ClassDecl_c) copy();
        n.errorInAST = error;
        return n;
    }

    public static final QName X10_INTEROP_JAVA = QName.make("x10.interop.Java");
    public static final Name JAVA_ARRAY = Name.make("array");

    private X10ClassDecl_c superPreBuildTypes(TypeBuilder tb) {
        // XTENLANG-2118: Intercept some known Java types
        if (name.id().equals(JAVA_ARRAY) && tb.currentClass() != null && tb.currentClass().fullName().equals(X10_INTEROP_JAVA))
        	tb = tb.pushClass(tb.typeSystem().JavaArray().def());
        else
        	tb = tb.pushClass(position(), name().position(), flags.flags(), name.id(), errorInAST);

        X10ClassDef type = tb.currentClass();

        // Member classes of interfaces are implicitly public and static.
        if (type.isMember() && type.outer().get().flags().isInterface()) {
            type.flags(type.flags().Public().Static());
        }

        // Member interfaces are implicitly static. 
        if (type.isMember() && type.flags().isInterface()) {
            type.flags(type.flags().Static());
        }

        // Interfaces are implicitly abstract. 
        if (type.flags().isInterface()) {
            type.flags(type.flags().Abstract());
        }

        X10ClassDecl_c n = this;
        FlagsNode flags = (FlagsNode) n.visitChild(n.flags, tb);
        Id name = (Id) n.visitChild(n.name, tb);

        TypeNode superClass = (TypeNode) n.visitChild(n.superClass, tb);
        List<TypeNode> interfaces = n.visitList(n.interfaces, tb);

        n = n.reconstruct(flags, name, superClass, interfaces, n.body);
        
        n.setSuperClass(tb.typeSystem(), type);
        n.setInterfaces(tb.typeSystem(), type);

        n = (X10ClassDecl_c) n.classDef(type).flags(flags.flags(type.flags()));
    
        return n;
    }
    
    @Override
    public X10ClassDecl_c postBuildTypes(TypeBuilder tb) {
        
    	X10ClassDecl_c n = (X10ClassDecl_c) this.copy();
    	
        if (n.defaultConstructorNeeded()) {
            ConstructorDecl cd = n.createDefaultConstructor(type, tb.typeSystem(), tb.nodeFactory());
            TypeBuilder tb2 = tb.pushClass(n.type);
            cd = (ConstructorDecl) tb2.visitEdge(n, cd);
            n = (X10ClassDecl_c) n.body(n.body().addMember(cd));
            n.defaultCI = cd.constructorDef();
        }
                
        final X10ClassDef def = (X10ClassDef) n.type;
        
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
        
        List<PropertyDecl> ps = visitList(n.properties, childTb);
        n = (X10ClassDecl_c) n.properties(ps);

        final DepParameterExpr ci = (DepParameterExpr) n.visitChild(n.classInvariant, childTb);
        n = (X10ClassDecl_c) n.classInvariant(ci);

        final LazyRef<CConstraint> c = new LazyRef_c<CConstraint>(ConstraintManager.getConstraintSystem().makeCConstraint());

        final X10ClassDecl_c nn = n;
        c.setResolver(new Runnable() {
        	public void run() {
        	    CConstraint x = ConstraintManager.getConstraintSystem().makeCConstraint();
        	    if (ci != null) {
        	        CConstraint xi = ci.valueConstraint().get();
        	        if (xi != null && ! xi.valid())
        	            x.addIn(xi);
        	    }
        	    c.update(x);
        	}
        });
        
        def.setClassInvariant(c);
        
        final LazyRef<TypeConstraint> tc = new LazyRef_c<TypeConstraint>(new TypeConstraint());

        // Set the type bounds for the def.
        tc.setResolver(new Runnable() {
        	public void run() {	
        		if (ci != null) {
        			TypeConstraint ti = ci.typeConstraint().get();
        			tc.update(ti.copy());
        		} 	
        	}
        });

        def.setTypeBounds(tc);

        return n;
    }
    
    boolean contains(List<ClassMember> list, Id xName, List<Formal> xf) {
    	OUTER: for (ClassMember yy : list) {
    		MethodDecl y = (MethodDecl) yy;
    		List<Formal> yf = y.formals();
    		if (xName.equals(y.name()) && xf.size() == yf.size()) {
    			for (int i=0; i < xf.size(); ++i) {
    				if (! (xf.get(i).equals(yf.get(i))))
    					continue OUTER;
    			}
    			return true;
    		}
    	}
    	return false;
    }
   
    /**
     * If the class is abstract, and does not implement a method specified by an interface,
     * add an abstract declaration for this method.
     * @param parent
     * @param tc
     * @param childtc
     * @return
     */
    public Node adjustAbstractMethods( ContextVisitor tc) {
    	// [DC] it seems this function exists so that one can dispatch through an
    	// abstract method in the c++ backend using the standard vtable dispatch mechanism
    	// rather than having to go through itables.  This makes the codegen simpler and is faster at runtime.

    	X10ClassDecl_c n = this;
    	
    	if (n.flags().flags().isInterface())
    		return n;
    	if (! n.flags().flags().isAbstract())
    		return n;
    	
    	Position CG = Position.compilerGenerated(body().position());
    	TypeSystem xts =  tc.typeSystem();
    	NodeFactory xnf = (NodeFactory) tc.nodeFactory();
    	X10ClassType targetType = (X10ClassType) n.classDef().asType();
    	List<X10ClassType> interfaces = xts.allImplementedInterfaces(targetType, false);
    	LinkedList<MethodInstance> candidates = new LinkedList<MethodInstance>();

    	for (X10ClassType intface : interfaces) {
    	    List<MethodInstance> oldMethods = intface.methods();
    	    for (MethodInstance mi : oldMethods) {
    	    	
    	    	// [DC] leave these guys out of the picture -- backends handle them specially
    	    	// testing mi.container().isAny() does not work since an interface I can redeclare these methods as abstract, in which
    	    	// case their container is I, not Any
    	    	if (mi.name().toString().equals("toString") && mi.formalNames().isEmpty()) continue;
    	    	if (mi.name().toString().equals("hashCode") && mi.formalNames().isEmpty()) continue;
    	    	if (mi.name().toString().equals("typeName") && mi.formalNames().isEmpty()) continue;
    	    	if (mi.name().toString().equals("equals") && mi.formalNames().size()==1 && mi.formalTypes().get(0).isAny()) continue;
    	        
    	    	MethodInstance mj = xts.findImplementingMethod(targetType, mi, true, tc.context());

    	        if (mj == null) { // This method is not already defined for this class
    	        	candidates.add(mi);
    	        }
    	    }
    	} // interfaces
    	// Remove overridden methods -- happens with covariant return types.
    	List<MethodInstance> results = new LinkedList<MethodInstance>(); 
    	Context context = xts.createContext();
    	OUTER: while (! candidates.isEmpty()) {
    	    MethodInstance mi = candidates.removeFirst();
    	    for (MethodInstance other : candidates) {
    	        if (other.canOverride(mi, context))
    	            continue OUTER;
    	    }
    	    results.add(mi);

    	}
    	Synthesizer synth = new Synthesizer(xnf, xts);
    	for (MethodInstance mi : results) {
    	    Id name = xnf.Id(CG, mi.name());
    	    TypeParamSubst subst = ((X10ParsedClassType) mi.container()).subst();
    	    List<LocalDef> formalNames = new ArrayList<LocalDef>();
    	    for (LocalDef f : ((X10MethodDef) mi.def()).formalNames()) {
    	        X10LocalDef tf = (X10LocalDef) f.copy();
    	        tf.setType(subst.reinstantiate(f.type()));
                formalNames.add(tf);
            }
            n = synth.addSyntheticMethod(n,
    	            mi.flags().Public().Abstract(),
    	            ((X10MethodDef) mi.def()).typeParameters(),
    	            mi.name(),
    	            formalNames,
    	            mi.throwTypes(),
    	            mi.returnType(), null);
    	}
    	return n;
    }
 
    /**
     * If the class does not have a super type and does not define a method from Any, add a default one.
     */
    public Node addDefaultAnyMethods( ContextVisitor tc) {
    	X10ClassDecl_c n = this;
    	
    	if (n.flags().flags().isInterface())
    		return n;
    	if (n.flags().flags().isStruct())
    		return n;
    	if (n.superClass() != null)
    		return n;
    	
    	TypeSystem xts =  tc.typeSystem();
    	NodeFactory xnf = (NodeFactory) tc.nodeFactory();
    	X10ClassType targetType = (X10ClassType) n.classDef().asType();

    	LinkedList<MethodInstance> results = new LinkedList<MethodInstance>();

    	X10ClassType intface = xts.Any();
	    for (MethodInstance mi : xts.Any().methods()) {   	
	        MethodInstance mj = xts.findImplementingMethod(targetType, mi, true, tc.context());
	        if (mj == null) { // This method is not already defined for this class
	        	results.add(mi);
	        }
    	}

    	Position CG = Position.compilerGenerated(body().position());
    	Synthesizer synth = new Synthesizer(xnf, xts);
    	for (MethodInstance mi : results) {
    	    Id name = xnf.Id(CG, mi.name());
    	    List<LocalDef> formalNames = new ArrayList<LocalDef>();
    	    for (LocalDef f : ((X10MethodDef) mi.def()).formalNames()) {
    	        X10LocalDef tf = (X10LocalDef) f.copy();
    	        tf.setType(f.type());
                formalNames.add(tf);
            }
    	    Type system = xts.System();
    	    Name system_name = null;
    	    X10Call call = null;
    	    try {
	    	    if (mi.name().toString().equals("hashCode")) {
	    	    	List<Expr> args = Collections.<Expr>singletonList(xnf.Special(CG, Special.THIS).type(targetType));
	        	    call = (X10Call) synth.makeStaticCall(CG, system, Name.make("identityHashCode"), args, mi.returnType(), tc.context());
	    	    }
	    	    //if (mi.name().toString().equals("typeName")) system_name = Name.make("identityTypeName");
	    	    //if (mi.name().toString().equals("toString")) system_name = Name.make("identityToString");
	    	    //if (mi.name().toString().equals("equals")) system_name = Name.make("identityEquals");
	    	    //call = null;
	    	    if (call != null) {
		    	    assert call != null : mi.name().toString();
		    	    System.out.println("Adding hashCode method... calling: " + call);
		            n = synth.addSyntheticMethod(n,
		            		Flags.PUBLIC,
		    	            ((X10MethodDef) mi.def()).typeParameters(),
		    	            mi.name(),
		    	            formalNames,
		    	            mi.throwTypes(),
		    	            mi.returnType(), xnf.Block(CG,xnf.Return(CG, call)));
	    	    }
    	    } catch (SemanticException e) {
    	    	Errors.issue(tc.job(), e, this);
    	    }
    	}
    	return n;
    }
 
    public Node typeCheckClassInvariant(Node parent, ContextVisitor tc, TypeChecker childtc) {
	X10ClassDecl_c n = this;
	DepParameterExpr classInvariant = (DepParameterExpr) n.visitChild(n.classInvariant, childtc);
	n = (X10ClassDecl_c) n.classInvariant(classInvariant);
	

	// TODO: Add check that the invariant established by this class is adequate
	// to entail the invariants associated with all interfaces (after applying
	// a substitution which replaces all fields specified in the interface with the fields
	// implementing them in the class).
	
	
	return n;
    }
    
    public Node typeCheckProperties(Node parent, ContextVisitor tc, TypeChecker childtc) {
        X10ClassDecl_c n = this;
        List<TypeParamNode> typeParameters = n.visitList(n.typeParameters, childtc);
        n = (X10ClassDecl_c) n.typeParameters(typeParameters);
        List<PropertyDecl> properties = n.visitList(n.properties, childtc);
        n = (X10ClassDecl_c) n.properties(properties);
        return n;
    }
    
    
    
    public Node typeCheckOverride(Node parent, ContextVisitor tc) {

        X10ClassDecl_c n = this;
    	
    	NodeVisitor v = tc.enter(parent, n);
    	
    	if (v instanceof PruningVisitor) {
    		return this;
    	}
    	
    	TypeChecker childtc = (TypeChecker) v;
    	ContextVisitor oldtc = (ContextVisitor) tc.shallowCopy();
    	
    	n = (X10ClassDecl_c) n.typeCheckSupers(tc, childtc);
    	TypeSystem xts = tc.typeSystem();
    	if (superClass != null) {
    	    Ref<? extends Type> stref = superClass.typeRef();
    	    try {
                checkSuperclass(xts, stref);
    	    } catch (SemanticException e) {
                X10ClassType uc = xts.createFakeClass(QName.make(superClass.nameString()), e);
                for (ConstructorDef cd : classDef().constructors()) {
                    ConstructorDef ucd = (ConstructorDef) cd.copy();
                    ucd.setContainer(Types.ref(uc));
                    uc.def().addConstructor(ucd);
                }
                ((Ref<Type>) stref).update(uc);
    	    }
    	}
    	for (TypeNode itn : interfaces()) {
    	    Ref<? extends Type> tref = itn.typeRef();
    	    try {
    	        checkSuperinterface(xts, tref);
    	    } catch (SemanticException e) {
                X10ClassType uc = xts.createFakeClass(QName.make(itn.nameString()), e);
                uc.def().flags(uc.def().flags().Interface());
                ((Ref<Type>) tref).update(uc);
    	    }
    	}

    	n = (X10ClassDecl_c) n.typeCheckProperties(parent, tc, childtc);
    	n = (X10ClassDecl_c) n.typeCheckClassInvariant(parent, tc, childtc);

    	// have to run this before adjustAbstractMethods so that it doesn't create an abstract method for these guys
    	//n = (X10ClassDecl_c) n.addDefaultAnyMethods(oldtc);

    	n = (X10ClassDecl_c) n.adjustAbstractMethods(oldtc);

    	n = (X10ClassDecl_c) n.typeCheckBody(parent, tc, childtc);
    	
    	n = (X10ClassDecl_c) X10Del_c.visitAnnotations(n, childtc);

    	// Make sure the node and type are consistent WRT super types.
        NodeFactory nf = tc.nodeFactory();
        
        if (type.superType() != null) {
        	if (!((X10ClassDef) type).isStruct()) {
        		if ((Types.isX10Struct(type.superType().get()))) {
        			Errors.issue(tc.job(),
        			             new Errors.ClassMustHaveClassSupertype(type.superType(),
        			                                                      type,
        			                                                      position()));
        		}
        	}
        }
        if (n.superClass == null && type.superType() != null) {
            n = (X10ClassDecl_c) n.superClass(nf.CanonicalTypeNode(
                    (body!=null ? body.position().startOf() : position()).markCompilerGenerated(), type.superType()));
        }
   
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
                TypeNode tn = nf.CanonicalTypeNode(position().markCompilerGenerated(), t);
                newInterfaces.add(tn);
            }
        }
        n = (X10ClassDecl_c) n.interfaces(newInterfaces);

        if (false) { // todo: this code is useless! it only adds to the lists, without doing any checks!
        // Check for duplicate interfaces
       /* List<X10ClassType> supers = new ArrayList<X10ClassType>();
        LinkedList<Type> worklist = new LinkedList<Type>();
        worklist.add(type.asType());
        while (! worklist.isEmpty()) {
            Type t = worklist.removeFirst();
            if (t instanceof X10ClassType) {
                supers.add((X10ClassType) t);
            }
            if (t instanceof ObjectType) {
                ObjectType ot = (ObjectType) t;
                worklist.add(ot.superClass());
                worklist.addAll(ot.interfaces());
            }
        }*/
        }
        
        // Check for instance type definitions -- these are not supported.
        for (TypeDef def : ((X10ClassDef) type).memberTypes()) {
            MacroType mt = def.asType();
            if (mt.container() != null && !mt.flags().isStatic()) {
                Errors.issue(tc.job(), new Errors.TypedefMustBeStatic(mt, def.position()), this);
            }
        }
        
        // fix for XTENLANG-978
//        Map<X10ClassDef,X10ClassType> map = CollectionFactory.newHashMap();
//        for (X10ClassType ct : supers) {
//            X10ClassType t = map.get(ct.x10Def());
//            if (t != null) {
//                if (!t.typeEquals(ct, tc.context())) {
//                    String kind = ct.flags().isInterface() ? "interface" : "class";
//                    Errors.issue(tc.job(),
//                                 new Errors.CannotExtendTwoInstancesSameInterfaceLimitation(t, ct,
//                                                                                            position()));
//                }
//            }
//            map.put(ct.x10Def(), ct);
//        }
        
    	Flags flags = n.flags().flags();
    	if (flags.isStruct()) {
    		if (n.classDef().isInnerClass() && ! flags.isStatic()) {
    			Errors.issue(tc.job(), new Errors.StructMustBeStatic(n));
    			n.classDef().setFlags(n.classDef().flags().Static());
    			n = (X10ClassDecl_c) n.flags(n.flags().flags(flags.Static()));
    		}
    	}
    	n.checkStructMethods(parent, tc);

        // a superclass/interface is a covariant position (+)
        if (n.superClass!=null) Types.checkVariance(n.superClass, ParameterType.Variance.COVARIANT,tc.job());
        for (TypeNode typeNode : n.interfaces)
            Types.checkVariance(typeNode, ParameterType.Variance.COVARIANT,tc.job());

        // check normal interfaces do not have property fields, but only annotation-interfaces
        if (flags.isInterface() && properties.size()>0) {
            Type A;
            try {
                A = xts.systemResolver().findOne(QName.make("x10.lang.annotations.Annotation"));
            } catch (SemanticException e) {
                throw new InternalCompilerError("Couldn't find x10.lang.annotations.Annotation",e);
            }
            if (!classDef().asType().isSubtype(A,tc.context())) {
                Errors.issue(tc.job(), new SemanticException("Interfaces that do not extend Annotation cannot have property fields. Use property methods instead.",name().position()));                
            }
        }

        // check properties are always simpler than their container
        if (((X10ClassDef_c)classDef()).hasCircularProperty())
            Errors.issue(tc.job(), new SemanticException("A class can only have properties of a 'simpler' type, i.e., there must be an ordering for all types such that if A has a property of type B then B is 'simpler' than A, and if A extends B then B is 'simpler' than A.",name().position()));            

    	return n;
    }
    
    // TODO
    protected void checkStructMethods(Node parent, ContextVisitor tc) {
    	
    }

    @Override
    protected void checkSupertypeCycles(TypeSystem ts) throws SemanticException {
        TypeSystem xts = (TypeSystem) ts;

        Ref<? extends Type> stref = type.superType();
        checkSuperclass(xts, stref);

        for (Ref<? extends Type> tref : type.interfaces()) {
            checkSuperinterface(xts, tref);
        }
    }

    protected void checkSuperclass(TypeSystem xts, Ref<? extends Type> stref) throws SemanticException {
        if (stref == null)
            return;
        Type t = stref.get();
        t = followDefs(t);
        if (xts.hasUnknown(t))
            return;
        if (! t.isClass() || t.toClass().flags().isInterface()) {
            throw new SemanticException("Cannot extend type " + t + "; not a class.", superClass != null ? superClass.position() : name().position());
        }
        xts.checkCycles((ObjectType) t);
    }

    protected void checkSuperinterface(TypeSystem xts, Ref<? extends Type> tref) throws SemanticException {
        if (tref == null)
            return;
        Type t = tref.get();
        t = followDefs(t);
        if (xts.hasUnknown(t))
            return;
        if (! t.isClass() || ! t.toClass().flags().isInterface()) {
            String s = type.flags().isInterface() ? "extend" : "implement";
            throw new SemanticException("Cannot " + s + " type " + t + "; not an interface.", name().position());
        }
        xts.checkCycles((ObjectType) t);
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
	    if (r instanceof LazyRef<?>) {
		((LazyRef<Type>) r).update(t2);
		return tn;
	    }
	    return tn.typeRef(Types.ref(t2));
	}
	return tn;
    }

    public Node conformanceCheck(ContextVisitor tc) {
    	X10ClassDecl_c result = (X10ClassDecl_c) superConformanceCheck(tc);
    	Context context = (Context) tc.context();
    	
    	X10ClassDef cd = classDef();
    	{ 
    		CConstraint c = cd.classInvariant().get();
    		if (c != null && ! c.consistent()) {
    			Errors.issue(tc.job(), new Errors.InconsistentInvariant(cd, position()));
    		}
    		if (c != null && ! c.valid()) {
    		    c = cd.getRealClause();
    		    if (c != null && ! c.consistent()) {
    		        Errors.issue(tc.job(), new Errors.InconsistentInvariant(cd, position()));
    		    }
    		}
    	}
        
    	// Check that we're in the right file.
    	if (flags.flags().isPublic() && type.isTopLevel()) {
    	    Job job = tc.job();
    	    if (job != null) {
    	        Source s = job.source();
    	        if (! (s.name().startsWith(type.name() + ".") ||
    	                s.name().endsWith(File.separator + type.name() + ".x10") ||
    	                s.name().endsWith("/" + type.name() + ".x10")))
    	        {
    	            Errors.issue(tc.job(),
    	                    new Errors.PublicTypeMustBeDeclaredInX10(type, result.position()));
    	        }
    	    }
    	}

    	TypeSystem ts = (TypeSystem) tc.typeSystem();
    	
    	Type superClass = type.asType().superClass();


    	if (flags.flags().isInterface() && superClass != null) {
    		Errors.issue(tc.job(),
    		        new Errors.InterfaceCannotHaveSuperclass(this.type, superClass.position()));
    	}


    	if (superClass != null) {
    	    for (PropertyDecl pd : properties()) {
    	        SemanticException ex = null;
    	        try {
    	            FieldInstance fi = ts.findField(superClass, type.asType(), pd.name().id(), tc.context());
    	            if (fi instanceof X10FieldInstance) {
    	                X10FieldInstance xfi = (X10FieldInstance) fi;
    	                if (xfi.isProperty())
    	                    ex = new Errors.ClassCannotOverridePropertyOfSuperclass(type, fi, position());
    	            }
    	        }
    	        catch (SemanticException e) {
    	            // not found.  That's good.
    	            continue;
    	        }
    	        
    	        Errors.issue(tc.job(), ex, this);
    	    }
    	}

    	final X10ClassType classType = type.asType();
    	if (ts.isThrowable(classType) && classType.hasParams()) {
    		SemanticException ex = new Errors.ParametricClassCannotExtendCheckedThrowable(type, position());
    		Errors.issue(tc.job(), ex, this);
    	}
     
    
    	try {
    	    ((X10ClassDef) type).checkRealClause();
    	} catch (SemanticException e) {
    	    Errors.issue(tc.job(), e, this);
    	}
	    
    	return result;
    }
    
    private Node superConformanceCheck(ContextVisitor tc) {
        TypeSystem ts = tc.typeSystem();

        X10ClassType type = this.type.asType();
        Name name = this.name.id();

        // The class cannot have the same simple name as any enclosing class.
        if (type.isNested()) {
            ClassType container = type.outer();

            while (container != null) {
                if (!container.isAnonymous()) {
                    Name cname = container.name();

                    if (cname.equals(name)) {
                        new Errors.SameNameClass(type,position()).issue(tc.job());
                    }
                }
                if (container.isNested()) {
                    container = container.outer();
                }
                else {
                    break;
                }
            }
        }

        // A local class name cannot be redeclared within the same
        // method, constructor or initializer, and within its scope                
        final Context context = tc.context();
        if (type.isLocal()) {
            if (context.isLocal(name)) {
                // Something with the same name was declared locally.
                // (but not in an enclosing class)
                try {
                    List<Type> nm = context.find(ts.TypeMatcher(name));
                    for (Type another : nm) {
                        if (another.isClass() && another.toClass().isLocal()) {
                            Errors.issue(tc.job(), new Errors.SameNameLocal(type, position()));
                        }
                    }
                } catch (SemanticException e) {
                    Errors.issue(tc.job(), e, this);
                }
            }                
        }

        if (type.isMember() && (type.outer().isInnerClass() || type.outer().isLocal() || type.outer().isAnonymous())) {
            if (type.flags().isInterface()) {
                // check that inner classes do not declare member interfaces
                Errors.issue(tc.job(),
                        new Errors.InnerDeclaredInterface(type, type.outer(), position()));
            } else 
            if (type.flags().isStatic()) {
                // Make sure that static members are not declared inside inner classes
                Errors.issue(tc.job(),
                        new Errors.InnerDeclaredStatic(type, type.outer(), position()));
            }
        }

        if (type.superClass() != null && isValidType(type.superClass())) {
            if (! type.superClass().isClass() || type.superClass().toClass().flags().isInterface()) {
                Errors.issue(tc.job(),
                        new Errors.ExtendedNonClass(type,
                                position()));
            }

            if (type.superClass().toClass().flags().isFinal()) {
                Errors.issue(tc.job(),
                        new Errors.ExtendedFinalClass(type,
                                position()));
            }

        }

        for (Iterator<TypeNode> i = interfaces.iterator(); i.hasNext(); ) {
            TypeNode tn = (TypeNode) i.next();
            Type t = tn.type();

            if (isValidType(t) && ! (t.isClass() && t.toClass().flags().isInterface())) {
                Errors.issue(tc.job(),
                        new Errors.SuperInterfaceNotInterface(t,type,
                                tn.position()));
            }

        }


        // check that 2 super-interfaces (or a superclass and superinterface)
        // do not cause a method collision (see InterfaceMethodCollision_MustFailCompile.x10)
        //interface A {		def m():A;	}
        //interface B {		def m():B;	}
        //abstract class B2 implements B {}
        //@ERR interface C1 extends A,B {}
        //interface C2 extends A,B { def m():C; }
        //@ERR abstract C3 extends B2 implements A {}
        // (other kinds of method collisions are checked in MethodDecl_c.overrideMethodCheck)
        // optimization: methods in Any&Object cannot cause problems (such errors will be caught in MethodDecl_c)
        X10ParsedClassType_c myClassType = (X10ParsedClassType_c) type;
        if (interfaces.size()>=1) {
            final List<X10ParsedClassType_c> directSuperTypes = new ArrayList<X10ParsedClassType_c>(myClassType.directSuperTypes());
            int len = directSuperTypes.size();
            for (int i1=0; i1<len; i1++) {
                X10ParsedClassType_c it1 = directSuperTypes.get(i1);
                // we can skip Any
                if (ts.isAny(it1)) continue;
                final List<MethodInstance> methods1 = it1.getAllMethods();
                if (methods1.size()==0) continue;
                for (int i2=i1+1; i2<len; i2++) {
                    X10ParsedClassType_c it2 = directSuperTypes.get(i2);
                    if (ts.isAny(it2)) continue;
                    for (MethodInstance mi : methods1) {
                        // We need to skip methods defined by "type" because they were already checked against all previous methods, e.g., interface C2 extends A,B { def m():C; }
                        if (!type.methods(mi.name(), mi.formalTypes(), context).isEmpty())
                            continue;
                        
                        final List<MethodInstance> implementedBy = ((TypeSystem_c) ts).implemented(mi, (ContainerType) it2, context);
                        for (MethodInstance mj : implementedBy) {
                            if (mi==mj) continue;
                            checkOverride(tc, ts, context, mi, mj);
                        }
                    }
                }
            }
        }

        {
            // methods with the same erased signature must have the exact same signature (see XTENLANG-547)
            Context context2 = context.pushClass(this.type, type);
            ArrayList<MethodInstance> allMethods = myClassType.getAllMethods();
            for (int i=0; i<allMethods.size(); i++) {
                MethodInstance mi = allMethods.get(i);
                List<Type> types1 = mi.formalTypes();
                int formalNum = types1.size();
                if (formalNum ==0) continue;
                if (! ts.isAccessible(mi, context2)) continue;
                Name name1 = mi.name();

                for (int j=i+1; j<allMethods.size(); j++) {
                    MethodInstance mj = allMethods.get(j);
                    List<Type> types2 = mj.formalTypes();
                    if (types2.size()==0) continue;
                    if (! ts.isAccessible(mj, context2)) continue;
                    Name name2 = mj.name();

                    if (name1!=name2) continue;
                    if (formalNum !=types2.size()) continue;
                    int typeParamNum = mi.typeParameters().size();
                    if (typeParamNum !=mj.typeParameters().size()) continue;
                    boolean isDifferent = false;
                    for (int p=0; p<formalNum;p++) {
                        Type t1 = Types.stripConstraints(types1.get(p));
                        Type t2 = Types.stripConstraints(types2.get(p));
                        // replace type-parameters in mi with mj, e.g.,
                        // def m[U](U)
                        // def m[H](H)
                        if (typeParamNum!=0) {
                            List<ParameterType> param1 = (List<ParameterType>) (List) mi.typeParameters();
                            List<Type> param2 = mj.typeParameters();
                            TypeParamSubst subst = new TypeParamSubst(ts, param2, param1);
                            t1 = subst.reinstantiate(t1);
                        }
                        if (!ts.typeEquals(t1,t2,context2)) {
                            isDifferent = true;
                            break;
                        }
                    }
                    if (isDifferent) continue;
                    // now we require that one overrides the other
                    // either mi can override mj, or the opposite.
                    checkOverride(tc, ts, context2, mi, mj);
                }
            }
        }

        try {
            if (type.isTopLevel()) {
                ts.checkTopLevelClassFlags(type.flags());
            }
            if (type.isMember()) {
                ts.checkMemberClassFlags(type.flags());
            }
            if (type.isLocal()) {
                ts.checkLocalClassFlags(type.flags());
            }
        }
        catch (SemanticException e) {
            Errors.issue(tc.job(), e, this);
        }

        // Check that the class implements all abstract methods that it needs to.
        try {
            ts.checkClassConformance(type, enterChildScope(body, context));
        } catch (SemanticException e) {
            Errors.issue(tc.job(), e, this);
        }

        return this;
    }

    private void checkOverride(ContextVisitor tc, TypeSystem ts, Context context, MethodInstance mi, MethodInstance mj) {
        // either mi can override mj, or the opposite.
        try {
            // due to covariant return type, the first argument should be the one with return type who is a subtype of the second
            MethodInstance mj2 = expandMacros(tc, ts, mi, mj);
            ts.checkOverride(mi, mj2, context);
        } catch (SemanticException e2) {
            try {
                MethodInstance mi2 = expandMacros(tc, ts, mj, mi);
                ts.checkOverride(mj, mi2, context);
            } catch (SemanticException e) {
                e.setPosition(this.position());
                Errors.issue(tc.job(),e, this);
            }
        }
    }

    public static MethodInstance expandMacros(ContextVisitor tc, TypeSystem ts, MethodInstance mi, MethodInstance mj) {
        return ((X10TypeEnv_c)ts.env(tc.context())).expandPropertyInMethod(mj);
    }

    protected boolean isValidType(Type type) {
        TypeSystem xts = (TypeSystem) type.typeSystem();
        return !xts.hasUnknown(type);
    }

    public Term firstChild() {
        return listChild(properties, this.body);
    }

    /**
     * Visit this term in evaluation order.
     */
    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        v.visitCFGList(this.properties(), this.body(), ENTRY);
        v.visitCFG(this.body(), this, EXIT);
        return succs;
    }

    protected ConstructorDecl createDefaultConstructor(ClassDef _thisType,
    		TypeSystem ts, final NodeFactory nf)
    {
        X10ClassDef thisType = (X10ClassDef) _thisType;
        Position pos = Position.compilerGenerated(body().position());
        NodeFactory xnf = (NodeFactory) nf;
        Block block = null;

        Ref<? extends Type> superType = thisType.superType();
        Stmt s1 = null;
        if (superType != null) {
            s1 = nf.SuperCall(pos, Collections.<Expr>emptyList());
        }

        Stmt s2 = null; 
        List<TypeParamNode> typeFormals = Collections.<TypeParamNode>emptyList();
        List<Formal> formals = Collections.<Formal>emptyList();

        if (! properties.isEmpty()) {
            // build type parameters.
            /*typeFormals = new ArrayList<TypeParamNode>(typeParameters.size());
            List<TypeNode> typeActuals = new ArrayList<TypeNode>(typeParameters.size());
            for (TypeParamNode tp : typeParameters) {
                typeFormals.add(xnf.TypeParamNode(pos, tp.name()));
                typeActuals.add(xnf.CanonicalTypeNode(pos, tp.type()));
            }*/

            formals = new ArrayList<Formal>(properties.size());
            List<Expr> actuals = new ArrayList<Expr>(properties.size());
            ChangePositionVisitor changePositionVisitor = new ChangePositionVisitor(pos);
            for (PropertyDecl pd: properties) {
                Id name = (Id) pd.name().position(pos);
                TypeNode typeNode = (TypeNode) pd.type().copy();
                Node newNode = typeNode.visit(changePositionVisitor);
                formals.add(xnf.Formal(pos, nf.FlagsNode(pos, Flags.FINAL),
                        (TypeNode) newNode, name));
                actuals.add(xnf.Local(pos, name));
            }

            s2 = xnf.AssignPropertyCall(pos, Collections.<TypeNode>emptyList(), actuals);
        }
        block = s2 == null ? (s1 == null ? nf.Block(pos) : nf.Block(pos, s1))
                : (s1 == null ? nf.Block(pos, s2) : nf.Block(pos, s1, s2));

        X10ClassType resultType = (X10ClassType) thisType.asType();
        // for Generic classes
        final List<ParameterType> typeParams = thisType.typeParameters();
        if (!typeParams.isEmpty()) {
            List<Type> typeArgs = new ArrayList<Type>(typeParams);
            resultType = (X10ClassType) resultType.typeArguments(typeArgs);
        }
        // no need to bind properties because the return type is HasType  i.e.,   <: BlaBla
        X10CanonicalTypeNode returnType = (X10CanonicalTypeNode) xnf.CanonicalTypeNode(pos, resultType);

        DepParameterExpr guard = classInvariant();
        if (guard!=null) {
            // we gather the property names so we can convert all the this.p in the invariant to locals for the default ctor
            //class A1(i:Int) {this.i==2} {
            //    def this() { property(2); }
            //}
            //class B1(b:Int) {this.i!=3} extends A1 {}
            final HashSet<Name> propNames = new HashSet<Name>();
            for (PropertyDecl p : properties)
                propNames.add(p.name().id());              

            // replace "this" in the invariant with no qualifiers for the guard
            class CannotConvertInvariantToGuard extends RuntimeException {}
            try {
                guard = (DepParameterExpr) guard.visit(new NodeVisitor() {
                    @Override
                    public Node override(Node n) {
                        if (n instanceof Call)
                            throw new CannotConvertInvariantToGuard();
                        if (!(n instanceof Field)) return null;
                        Field f = (Field) n;
                        if (!(f.target() instanceof X10Special)) return null;
                        // This is not a good test cause you can refer to a super property with "this".
                        // ((X10Special) f.target()).kind()==Special.Kind.THIS)
                        if (propNames.contains(f.name().id()))
                            return nf.Local(n.position(),f.name());
                        throw new CannotConvertInvariantToGuard();
                    }
                });
            } catch (CannotConvertInvariantToGuard e) {
                guard = null;
                Errors.issue(ts.extensionInfo(), new SemanticException("Cannot create the default constructor because the class invariant uses property methods, self, or super. Please define a constructor explicitly."), classInvariant.position());
            }
        }
        ConstructorDecl cd = xnf.X10ConstructorDecl(pos,
                nf.FlagsNode(pos, Flags.PUBLIC),
                nf.Id(pos, TypeSystem.CONSTRUCTOR_NAME), 
                nf.HasType(returnType),
                typeFormals,
                formals,
                guard,
                null, // offerType
                Collections.<TypeNode>emptyList(),
                block);
        return cd;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Flags flags = this.flags.flags();
        sb.append(flags.clearInterface().translate());
        sb.append(flags.isInterface() ? "interface " : "class ");
        sb.append(name);
        if (!typeParameters.isEmpty()) {
            sb.append("[");
            boolean first = true;
            for (TypeParamNode p : typeParameters) {
                if (!first) sb.append(",");
                first = false;
                sb.append(p);
            }
            sb.append("]");
        }
        if (!properties.isEmpty()) {
            sb.append("(");
            boolean first = true;
            for (PropertyDecl p : properties) {
                if (!first) sb.append(",");
                first = false;
                sb.append(p);
            }
            sb.append(")");
        }
        if (classInvariant != null) {
            sb.append("{");
            sb.append(classInvariant);
            sb.append("}");
        }
        sb.append(" ");
        sb.append(body);
        return sb.toString();
    }

    @Override
    public void prettyPrintHeader(CodeWriter w, PrettyPrinter tr) {
        w.begin(0);
        if (type != null) {
            Flags flags = type.flags();
        
            if (flags.isInterface()) {
                w.write(flags.clearInterface().clearAbstract().translate());
            }
            else {
                w.write(flags.translate());
            }
        
            if (flags.isInterface()) {
                w.write("interface ");
            }
            else {
                w.write("class ");
            }
        }
        
        tr.print(this, name, w);
        
        if (!typeParameters.isEmpty()) {
            w.write("[");
            w.begin(0);
            for (Iterator<TypeParamNode> pi = typeParameters.iterator(); pi.hasNext(); ) {
                TypeParamNode pn = pi.next();
                print(pn, w, tr);
                if (pi.hasNext()) {
                    w.write(",");
                    w.allowBreak(0, " ");
                }
            }
            w.end();
            w.write("]");
        }
        
        if (!properties.isEmpty()) {
            w.write("(");
            w.begin(0);
            for (Iterator<PropertyDecl> pi = properties.iterator(); pi.hasNext(); ) {
                PropertyDecl pd = pi.next();
                print(pd, w, tr);
                if (pi.hasNext()) {
                    w.write(",");
                    w.allowBreak(0, " ");
                }
            }
            w.end();
            w.write(")");
        }
        
        if (superClass() != null) {
            w.allowBreak(0);
            w.write("extends ");
            print(superClass(), w, tr);
        }
        
        if (! interfaces.isEmpty()) {
            w.allowBreak(2);
            if (type != null) {
                if (type.flags().isInterface()) {
                    w.write("extends ");
                }
                else {
                    w.write("implements ");
                }
            }
        
            w.begin(0);
            for (Iterator<TypeNode> i = interfaces().iterator(); i.hasNext(); ) {
                TypeNode tn = (TypeNode) i.next();
                print(tn, w, tr);
        
                if (i.hasNext()) {
                    w.write(",");
                    w.allowBreak(0);
                }
            }
            w.end();
        }
        w.unifiedBreak(0);
        w.end();
        w.write("{");
    }
    
    public List<Def> defs() {
        return Collections.<Def>singletonList(type);
    }

    public MemberDef memberDef() {
        return type;
    }
    
    public FlagsNode flags() {
        return this.flags;
    }
    
    public Id name() {
        return this.name;
    }
    
    public TypeNode superClass() {
        return this.superClass;
    }
    
    public List<TypeNode> interfaces() {
        return this.interfaces;
    }
    
    public ClassBody body() {
        return this.body;
    }
    
    protected X10ClassDecl_c reconstruct(FlagsNode flags, Id name, TypeNode superClass, List<TypeNode> interfaces, ClassBody body) {
        if (flags != this.flags || name != this.name || superClass != this.superClass || ! CollectionUtil.allEqual(interfaces, this.interfaces) || body != this.body) {
            X10ClassDecl_c n = (X10ClassDecl_c) copy();
            n.flags = flags;
            n.name = name;
            n.superClass = superClass;
            n.interfaces = TypedList.copyAndCheck(interfaces, TypeNode.class, true);
            n.body = body;
            return n;
        }

        return this;
    }
    
    public Node visitChildren(NodeVisitor v) {
        X10ClassDecl_c n = (X10ClassDecl_c) visitSignature(v);
        ClassBody body = (ClassBody) n.visitChild(n.body, v);
        return body == n.body ? n : n.body(body);
    }

    public Node buildTypesOverride(TypeBuilder tb) {
    	X10ClassDecl_c n = this;
    	n = n.preBuildTypes(tb);
    	n = n.buildTypesBody(tb);
    	n = n.postBuildTypes(tb);
    	return n;
    }

    private X10ClassDecl_c buildTypesBody(TypeBuilder tb) {
    	X10ClassDecl_c n = this;
    	TypeBuilder tb2 = tb.pushClass(n.type);
    	ClassBody body = (ClassBody) n.visitChild(n.body, tb2);
    	n = (X10ClassDecl_c) n.body(body);
    	return n;
    }
    
    protected boolean defaultConstructorNeeded() {
        if (flags.flags().isInterface()) {
            return false;
        }
        for (ClassMember cm : body().members()) {
            if (cm instanceof ConstructorDecl) {
                return false;
            }
        }

        return true;
    }
    
    public Node typeCheckSupers(ContextVisitor tc, TypeChecker childtc) {
        X10ClassDecl_c n = this;

        // ### This should be done somewhere else, but before entering the body.
        Context c = tc.context();
        type.inStaticContext(c.inStaticContext());

        FlagsNode flags = n.flags;
        Id name = n.name;
        TypeNode superClass = n.superClass;
        List<TypeNode> interfaces = n.interfaces;
        ClassBody body = n.body;

        flags = (FlagsNode) visitChild(n.flags, childtc);
        name = (Id) visitChild(n.name, childtc);
        superClass = (TypeNode) n.visitChild(n.superClass, childtc);
        interfaces = n.visitList(n.interfaces, childtc);
        
        if (n.superClass() != null) {
            if (type.superType() != n.superClass().typeRef())
                throw new InternalCompilerError(n.position(), "ClassDecl_c.typeCheckSupers: unexpected super type " +n.superClass().typeRef()+ " doesn't equal " +type.superType());
            assert type.superType() == n.superClass().typeRef();
        }
        n = (X10ClassDecl_c) n.reconstruct(flags, name, superClass, interfaces, body);
        try {
            n.checkSupertypeCycles(tc.typeSystem());
        } catch (SemanticException e) {
            Errors.issue(tc.job(), e, this);
        }
        if (superClass != null) {
            try {
                Types.checkMissingParameters(superClass);
            } catch (SemanticException e) {
                Errors.issue(tc.job(), e, this);
            }
        }
        for (TypeNode itn : interfaces) {
            try {
                Types.checkMissingParameters(itn);
            } catch (SemanticException e) {
                Errors.issue(tc.job(), e, this);
            }
        }

        return n;
    }
    
    public Node typeCheckBody(Node parent, ContextVisitor tc, TypeChecker childtc) {
        X10ClassDecl_c old = this;

        X10ClassDecl_c n = this;

        FlagsNode flags = n.flags;
        Id name = n.name;
        TypeNode superClass = n.superClass;
        List<TypeNode> interfaces = n.interfaces;
        ClassBody body = n.body;

        body = (ClassBody) n.visitChild(body, childtc);

        n = (X10ClassDecl_c) n.reconstruct(flags, name, superClass, interfaces, body);
        n = (X10ClassDecl_c) tc.leave(parent, old, n, childtc);

        return n;
    }
    
    public void prettyPrintFooter(CodeWriter w, PrettyPrinter tr) {
        w.write("}");
        w.newline(0);
    }
    
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        prettyPrintHeader(w, tr);
        print(body(), w, tr);
        prettyPrintFooter(w, tr);
    }

    public void dump(CodeWriter w) {
        super.dump(w);

        w.allowBreak(4, " ");
        w.begin(0);
        w.write("(name " + name + ")");
        w.end();

        if (type != null) {
            w.allowBreak(4, " ");
            w.begin(0);
            w.write("(type " + type + ")");
            w.end();
        }
    }
} 
