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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

import polyglot.ast.Expr;
import polyglot.ast.FieldDecl_c;
import polyglot.ast.FlagsNode;
import polyglot.ast.FloatLit;
import polyglot.ast.Id;
import polyglot.ast.IntLit;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.StringLit;
import polyglot.ast.TypeNode;
import polyglot.ast.CanonicalTypeNode;
import polyglot.frontend.AbstractGoal_c;
import polyglot.frontend.Globals;
import polyglot.frontend.Goal;
import polyglot.types.ClassDef;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.InitializerDef;
import polyglot.types.LazyRef;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.ContainerType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.VarDef_c.ConstantValue;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;
import x10.constraint.XTerm;
import x10.errors.Errors;
import x10.extension.X10Del;
import x10.extension.X10Del_c;
import x10.extension.X10Ext;
import x10.types.ParameterType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import polyglot.types.Context;
import x10.types.X10Def;
import x10.types.X10FieldDef;

import x10.types.X10InitializerDef;


import x10.types.X10FieldDef_c;
import x10.types.X10ParsedClassType;
import x10.types.X10ParsedClassType_c;
import x10.types.X10ClassDef_c;
import polyglot.types.TypeSystem;
import polyglot.types.FieldInstance;
import x10.types.checker.Checker;
import x10.types.checker.Converter;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;
import x10.types.constraints.XConstrainedTerm;
import x10.visit.X10TypeChecker;

public class X10FieldDecl_c extends FieldDecl_c implements X10FieldDecl {

	TypeNode hasType;
	public Type hasType() {
		return hasType==null ? null : hasType.type();
	}
    public X10FieldDecl_c(NodeFactory nf, Position pos, FlagsNode flags, TypeNode type,
            Id name, Expr init)
    {
        super(pos, flags, 
        		type instanceof HasTypeNode_c 
        		? nf.UnknownTypeNode(type.position()) 
        				: type, name, init);
        if (type instanceof HasTypeNode_c) 
			hasType = ((HasTypeNode_c) type).typeNode();
    }
    
    @Override
    public Context enterScope(Context c) {
        c = super.enterScope(c);
        if (!c.inStaticContext() && fieldDef().thisDef() != null)
            c.addVariable(fieldDef().thisDef().asInstance());
        return c;
    }

    @Override
    public X10FieldDecl_c flags(FlagsNode flags) {
        return (X10FieldDecl_c) super.flags(flags);
    }
    @Override
    public X10FieldDecl_c type(TypeNode type) {
        return (X10FieldDecl_c) super.type(type);
    }
    @Override
    public X10FieldDecl_c name(Id name) {
        return (X10FieldDecl_c) super.name(name);
    }
    @Override
    public X10FieldDecl_c init(Expr init) {
        return (X10FieldDecl_c) super.init(init);
    }
    @Override
    public X10FieldDecl_c fieldDef(FieldDef mi) {
        return (X10FieldDecl_c) super.fieldDef(mi);
    }
    @Override
    public X10FieldDef fieldDef() {
        return (X10FieldDef) super.fieldDef();
    }

    protected X10FieldDecl_c reconstruct(TypeNode hasType) {
    	if (this.hasType != hasType)  {
    		X10FieldDecl_c n = (X10FieldDecl_c) copy();
    		n.hasType = hasType;
    		return n;
    	}
    	return this;
    }
	public Context enterChildScope(Node child, Context c) {
		Context oldC=c;
		if (child == this.type || child==this.hasType) {
		    c = c.pushBlock();
		    FieldDef fi = fieldDef();
		    c.addVariable(fi.asInstance());
		    c.setVarWhoseTypeIsBeingElaborated(fi);
		    addInClassInvariantIfNeeded(c);
		    //PlaceChecker.setHereTerm(fieldDef(), c);
		}
				
	    if (child == this.init) {
	        c = c.pushBlock();
	        addInClassInvariantIfNeeded(c);
	    	PlaceChecker.setHereTerm(fieldDef(), c);
		}
		c = super.enterChildScope(child, c);
		return c;
	}
	
	public void addInClassInvariantIfNeeded(Context c) {
        if (!fieldDef().flags().isStatic()) {
            // this call occurs in the body of an instance method for T.
            // Pick up the real clause for T -- that information is known 
            // statically about "this"
            Ref<? extends ContainerType> container = fieldDef().container();
            if (container.known()) { 
                X10ClassType type = (X10ClassType) Types.get(container);
                Ref<CConstraint> rc = type.x10Def().realClause();
                c.addConstraint(rc);
                Ref<TypeConstraint> tc = type.x10Def().typeBounds();
                if (tc != null) {
                    c.setTypeConstraintWithContextTerms(tc);
                }
            }
        }
    }
	@Override
	public void setResolver(final Node parent, TypeCheckPreparer v) {
		final FieldDef def = fieldDef();
		Ref<ConstantValue> rx = def.constantValueRef();
		if (rx instanceof LazyRef<?>) {
		    LazyRef<ConstantValue> r = (LazyRef<ConstantValue>) rx;
		    TypeChecker tc0 = new X10TypeChecker(v.job(), v.typeSystem(), v.nodeFactory(), v.getMemo());
		    final TypeChecker tc = (TypeChecker) tc0.context(v.context().freeze());
		    final Node n = this;
		    r.setResolver(new AbstractGoal_c("ConstantValue") {
		        private static final long serialVersionUID = -4839673421806815982L;
		        { this.scheduler = tc.job().extensionInfo().scheduler(); }
		        public boolean runTask() {
		            if (state() == Goal.Status.RUNNING_RECURSIVE || state() == Goal.Status.RUNNING_WILL_FAIL) {
		                // The field is not constant if the initializer is recursive.
		                //
		                // But, we could be checking if the field is constant for another
		                // reference in the same file:
		                //
		                // m() { use x; }
		                // final int x = 1;
		                //
		                // So this is incorrect.  The goal below needs to be refined to only visit the initializer.
		                def.setNotConstant();
		            }
		            else {
		                Node m = parent.visitChild(n, tc);
		                tc.job().nodeMemo().put(n, m);
		                tc.job().nodeMemo().put(m, m);
		            }
		            return true;
		        }
		    });
		}
	}


    public Node conformanceCheck(ContextVisitor tc) {
        Node result = super.conformanceCheck(tc);

        // Any occurrence of a non-final static field in X10
        // should be reported as an error.
        if (flags().flags().isStatic() && (!flags().flags().isFinal())) {
            Errors.issue(tc.job(),
                    new Errors.CannotDeclareStaticNonFinalField(position()));
        }
        
        FieldDef fi = fieldDef();
        ContainerType ref = fi.container().get();

        TypeSystem xts = (TypeSystem) ref.typeSystem();
        Context context = (Context) tc.context();
        if (Types.isX10Struct(ref) && !isMutable(xts, ref)) {
            Flags x10flags = fi.flags();
            if (! x10flags.isFinal()) 
                Errors.issue(tc.job(),
                        new Errors.IllegalFieldDefinition(fi, position()));
        }
        
        checkVariance(tc);
        
        X10MethodDecl_c.checkVisibility(tc, this);
        
        return result;
    }
    
    protected boolean isMutable(TypeSystem xts, Type t) {
        if (!(t instanceof X10ClassType)) return false;
        X10ClassType ct = (X10ClassType) t;
        try {
            Type m = xts.systemResolver().findOne(QName.make("x10.compiler.Mutable"));
            return ct.annotations().contains(m);
        } catch (SemanticException e) {
            return false;
        }
    }
    
    protected void checkVariance(ContextVisitor tc) {
	Context c = (Context) tc.context();
	X10ClassDef cd;
	if (c.inSuperTypeDeclaration())
	    cd = c.supertypeDeclarationType();
	else
	    cd = (X10ClassDef) c.currentClassDef();
        final Map<Name,ParameterType.Variance> vars = CollectionFactory.newHashMap();
        for (int i = 0; i < cd.typeParameters().size(); i++) {
    	ParameterType pt = cd.typeParameters().get(i);
    	ParameterType.Variance v = cd.variances().get(i);
    	vars.put(pt.name(), v);
        }
        /*try {
        if (flags().flags().isFinal()) {
            Checker.checkVariancesOfType(type.position(), type.type(), ParameterType.Variance.COVARIANT, "as the type of a final field", vars, tc);
        }
        else {
        	Checker.checkVariancesOfType(type.position(), type.type(), ParameterType.Variance.INVARIANT, "as the type of a non-final field", vars, tc);
        }
        } catch (SemanticException e) {
            Errors.issue(tc.job(), e, this);
        }*/
    }

    protected FieldDef createFieldDef(TypeSystem ts, ClassDef ct, Flags xFlags) {
    	
    	X10FieldDef fi = (X10FieldDef) ts.fieldDef(position(), Types.ref(ct.asType()), xFlags, type.typeRef(), name.id());
    	fi.setThisDef(((X10ClassDef) ct).thisDef());

    	return fi;
    }
    
    protected InitializerDef createInitializerDef(TypeSystem ts, ClassDef ct, Flags iflags) {
        X10InitializerDef ii;
        ii = (X10InitializerDef) super.createInitializerDef(ts, ct , iflags);
        ii.setThisDef(((X10ClassDef) ct).thisDef());
        return ii;
    }

    @Override
    public Node buildTypesOverride(TypeBuilder tb) {
        TypeSystem ts = (TypeSystem) tb.typeSystem();

        X10FieldDecl_c n = (X10FieldDecl_c) super.buildTypesOverride(tb);
        
        X10FieldDef fi = (X10FieldDef) n.fieldDef();
        
        final ClassDef container = tb.currentClass();

        n = (X10FieldDecl_c) X10Del_c.visitAnnotations(n, tb);

        List<AnnotationNode> as = ((X10Del) n.del()).annotations();
        if (as != null) {
            List<Ref<? extends Type>> ats = new ArrayList<Ref<? extends Type>>(as.size());
            for (AnnotationNode an : as) {
                ats.add(an.annotationType().typeRef());
            }
            fi.setDefAnnotations(ats);
        }

        // Clear the static bit on properties
        if (this instanceof PropertyDecl) {
            Flags flags = flags().flags().clearStatic();
            n = (X10FieldDecl_c) n.flags(n.flags.flags(flags));
            fi.setFlags(flags);
        }

        // vj - shortcut and initialize the field instance if the decl has an initializer
        // This is the hack to permit reading the list of properties from the StringLit initializer
        // of a field, without waiting for a ConstantsChecked pass to run.
        if (init instanceof StringLit) {
            String val = ((StringLit) init).value();
            fi.setConstantValue(x10.types.constants.ConstantValue.makeString(val));
        }

        // TODO: Could infer type of final fields as LCA of types assigned in the constructor.
        if (type instanceof UnknownTypeNode && init == null)
        	Errors.issue(tb.job(), new Errors.CannotInferFieldType(position()));
        
        // Do not infer types of mutable fields, since there could be more than one assignment and the compiler might not see them all.
        if (type instanceof UnknownTypeNode && ! flags.flags().isFinal())
        	Errors.issue(tb.job(), new Errors.CannotInferNonFinalFieldType(position()));
        
        return n;
    }
    
	    public static boolean shouldInferType(Node n, TypeSystem ts) {
	        try {
	            Type at = ts.systemResolver().findOne(QName.make("x10.compiler.NoInferType"));
	            boolean res = ((X10Ext)n.ext()).annotationMatching(at).isEmpty();
	            if (res == true) return true;
	            return res;
	        } catch (SemanticException e) {
	            return false;
	        }
	    }

	    @Override
	    public Node setResolverOverride(Node parent, TypeCheckPreparer v) {
		    if (type() instanceof UnknownTypeNode && init != null) {
			    UnknownTypeNode tn = (UnknownTypeNode) type();

			    NodeVisitor childv = v.enter(parent, this);
			    childv = childv.enter(this, init);

			    if (childv instanceof TypeCheckPreparer) {
				    TypeCheckPreparer tcp = (TypeCheckPreparer) childv;
				    final LazyRef<Type> r = (LazyRef<Type>) tn.typeRef();
				    TypeChecker tc = new X10TypeChecker(v.job(), v.typeSystem(), v.nodeFactory(), v.getMemo());
				    tc = (TypeChecker) tc.context(tcp.context().freeze());
				    r.setResolver(new TypeCheckExprGoal(this, init, tc, r));
			    }
		    }
		    return super.setResolverOverride(parent, v);
	    }

	    @Override
	    public Node typeCheckOverride(Node parent, ContextVisitor tc) {
	        X10FieldDecl_c nn = this;
	        X10FieldDecl old = nn;

	        NodeVisitor childtc = tc.enter(parent, nn);
	        nn = (X10FieldDecl_c) X10Del_c.visitAnnotations(nn, childtc);

	        // Do not infer types of native fields
	        if (nn.type() instanceof UnknownTypeNode && ! shouldInferType(nn, tc.typeSystem()))
	            Errors.issue(tc.job(), new Errors.CannotInferNativeFieldType(nn.position()));

	        if (nn.hasType != null && ! nn.flags().flags().isFinal()) {
	            Errors.issue(tc.job(), new Errors.OnlyValMayHaveHasType(nn));
	        }
	        if (nn.type() instanceof UnknownTypeNode && shouldInferType(nn, tc.typeSystem())) {
	            Expr init = (Expr) nn.visitChild(nn.init(), childtc);
	            if (init != null) {
	                Type t = init.type();
	                if (t instanceof X10ClassType) {
	                    X10ClassType ct = (X10ClassType) t;
	                    if (ct.isAnonymous()) {
	                        if (ct.interfaces().size() > 0)
	                            t = ct.interfaces().get(0);
	                        else
	                            t = ct.superClass();
	                    }
	                }
	                Context xc = nn.enterChildScope(nn.type(), tc.context());
	                t = PlaceChecker.ReplaceHereByPlaceTerm(t, xc);
	                LazyRef<Type> r = (LazyRef<Type>) nn.type().typeRef();
	                r.update(t);
	                TypeNode htn = null;
	                {
	                    TypeNode tn = (TypeNode) nn.visitChild(nn.type(), childtc);
	                    if (nn.hasType != null) {
	                        htn = (TypeNode) nn.visitChild(nn.hasType, childtc);
	                        boolean checkSubType = true;
	                        try {
	                            Types.checkMissingParameters(htn);
	                        } catch (SemanticException e) {
	                            Errors.issue(tc.job(), e, htn);
	                            checkSubType = false;
	                        }
	                        if (checkSubType && ! htn.type().typeSystem().isSubtype(nn.type().type(), htn.type(),tc.context())) {
	                            xc = (Context) nn.enterChildScope(init, tc.context());
	                            Expr newInit = Converter.attemptCoercion(tc.context(xc), init, htn.type());
	                            if (newInit == null) {
	                                Errors.issue(tc.job(),
	                                             new Errors.TypeIsNotASubtypeOfTypeBound(nn.type().type(),
	                                                                                     htn.type(),
	                                                                                     nn.position()));
	                            } else {
	                                init = newInit;
	                                r.update(newInit.type()); 
	                            }
	                        }
	                    }
	                }
	                FlagsNode flags = (FlagsNode) nn.visitChild(nn.flags(), childtc);
	                Id name = (Id) nn.visitChild(nn.name(), childtc);
	                TypeNode tn = (TypeNode) nn.visitChild(nn.type(), childtc);

	                nn = nn.reconstruct(flags, tn, name, init, htn);
	                return tc.leave(parent, old, nn, childtc);
	            }
	        }
	        return null;
	    }
	     
	    /** Reconstruct the declaration. */
	    protected X10FieldDecl_c reconstruct(FlagsNode flags, TypeNode type, Id name, Expr init, TypeNode hasType) {
	        X10FieldDecl_c n = (X10FieldDecl_c) super.reconstruct(flags, type, name, init);
	        if (n.hasType != hasType) {
	            n.hasType = hasType;
	        }
	        return n;
	    }

	    @Override
	    public Node typeCheck(ContextVisitor tc) {
	    	final TypeNode typeNode = this.type();
	    	Type type =  typeNode.type();
	    	Type oldType = (Type)type.copy();
	    	Context xc = (Context) enterChildScope(type(), tc.context());
	    	Flags f = flags.flags();
	    	
	    	try {
                Types.checkMissingParameters(typeNode);
	    	} catch (SemanticException e) {
	    	    Errors.issue(tc.job(), e, this);
	    	}
	    	
	    	// Need to replace here by current placeTerm in type, 
	    	// since the field of this type can be referenced across
	    	// a place shift. So here must be bound to the current placeTerm.
	    	
	    	type = PlaceChecker.ReplaceHereByPlaceTerm(type, xc);

	    	TypeSystem ts = (TypeSystem) tc.typeSystem();
	    	if (type.isVoid()) {
	    		Errors.issue(tc.job(), new Errors.FieldCannotHaveType(typeNode.type(), position()));
	    		type = ts.unknownType(position()); 
	    	}


	    	if (Types.isX10Struct(fieldDef().container().get()) &&
	    			!isMutable(ts, fieldDef().container().get()) &&
	    			! f.isFinal())
	    	{
	    		Errors.issue(tc.job(), new Errors.StructMayNotHaveVarFields(position()));
	    	}

            final boolean noInit = init() == null;
            if (f.isStatic() && noInit) {
                Errors.issue(tc.job(), new Errors.StaticFieldMustHaveInitializer(name, position()));
            } 

	    	NodeFactory nf = (NodeFactory) tc.nodeFactory();

	    	X10FieldDecl_c n = (X10FieldDecl_c) this.type((CanonicalTypeNode) nf.CanonicalTypeNode(type().position(), type).ext(type().ext().copy()));

	    	// Add an initializer to uninitialized var field unless field is annotated @Uninitialized.
            final X10FieldDef fieldDef = (X10FieldDef) n.fieldDef();
            final boolean needsInit = !f.isFinal() && noInit && !Types.isUninitializedField(fieldDef, ts);
            final boolean isTransient = f.isTransient() && !Types.isSuppressTransientErrorField(fieldDef,ts);
            if (needsInit || isTransient) {
                final boolean hasZero = Types.isHaszero(type, xc);
                // creating an init.
                ContextVisitor tcWithNewContext = tc.context(xc);
	    		Expr e = Types.getZeroVal(typeNode,position().markCompilerGenerated(),tcWithNewContext);
                if (needsInit) {
                    if (e != null) {
                        n = (X10FieldDecl_c) n.init(e);
                    }
                }
                if (isTransient) {
                    // transient fields (not annotated with @SuppressTransientError) must have a default value
                    if (!hasZero)
                        Errors.issue(tc.job(), new Errors.TransientFieldMustHaveTypeWithDefaultValue(n.name(), position()));
                }
	    	}

	    	if (n.init != null) {
	    		xc = (Context) n.enterChildScope(n.init, tc.context());
	    		ContextVisitor childtc = tc.context(xc);
	    		Expr newInit = Converter.attemptCoercion(childtc, n.init, oldType); // use the oldType. The type of n.init may have "here".
	    		if (newInit == null)
	    		    Errors.issue(tc.job(),
	    		             new Errors.FieldInitTypeWrong(n.init, type, n.init.position()),
	    		             this);
                else
                    n = n.init(newInit);
	    	}

         //   Types.checkVariance(n.type(), f.isFinal() ? ParameterType.Variance.COVARIANT : ParameterType.Variance.INVARIANT,tc.job());

            // check cycles in struct declaration that will cause a field of infinite size, e.g.,
            // struct Z(@ERR u:Z) {}
            // struct Box[T](t:T) { }
            // struct InfiniteSize(@ERR x:Box[Box[InfiniteSize]]) {}
            final ContainerType containerType = fieldDef.container().get();
            X10ClassDef_c goalDef = Types.getDef(containerType);
            if (ts.isStruct(containerType)) {
                Set<X10ClassDef_c> otherStructsUsed = CollectionFactory.newHashSet();
                ArrayList<X10ParsedClassType> toExamine = new ArrayList<X10ParsedClassType>();
                final X10ParsedClassType_c goal = Types.myBaseType(type);
                if (goal!=null) {
                    toExamine.add(goal);
                    boolean isFirstTime = true;
                    while (toExamine.size()>0) {
                        final X10ParsedClassType curr = toExamine.remove(toExamine.size() - 1);
                        if (!isFirstTime && Types.getDef(curr)==goalDef) {
                            Errors.issue(tc.job(),new Errors.StructsCircularity(position),this);
                            break;
                        }
                        isFirstTime = false;

                        if (!ts.isStruct(curr)) continue;
                        X10ClassDef_c def = Types.getDef(curr);
                        if (otherStructsUsed.contains(def)) {
                            continue;
                        }
                        otherStructsUsed.add(def);
                        toExamine.addAll(getAllTypeArgs(curr));
                        for (FieldDef fi : def.fields()) {
                            if (fi.flags().isStatic()) continue;
                            X10ParsedClassType fiType = Types.myBaseType(fi.type().get());
                            if (fiType!=null) {
                                toExamine.add(fiType);
                                toExamine.addAll(getAllTypeArgs(fiType));
                            }
                        }


                    }
                }
            }

            if (f.isProperty()) {
                // you cannot write:  class A[T](b:T) {...}
                // i.e., property base type must be a class
                Type t = Types.baseType(n.type().type());
                if (!(t instanceof X10ParsedClassType))
                    Errors.issue(tc.job(),new SemanticException("A property type cannot be a type parameter.",position),this);
            }
            
	    	return n;
	    }
        public ArrayList<X10ParsedClassType> getAllTypeArgs(X10ParsedClassType curr) {
            final List<Type> typeArgs = curr.typeArguments();
            ArrayList<X10ParsedClassType> res = new ArrayList<X10ParsedClassType>();
            if (typeArgs!=null) {
                // consider: struct InfiniteSize(x:Box[Box[InfiniteSize]]) {}
                // if I just add Box[InfiniteSize] to toExamine, then when I pop it and check "if (otherStructsUsed.contains(def))" then I'll ignore it.
                // therefore I must also add InfiniteSize (i.e., all the type args found recursively in the type.)
                ArrayList<Type> toExamineArgs = new ArrayList<Type>(typeArgs);
                while (toExamineArgs.size()>0) {
                    Type ta = toExamineArgs.remove(toExamineArgs.size()-1);
                    final X10ParsedClassType_c baseTa = Types.myBaseType(ta);
                    if (baseTa!=null) {
                        res.add(baseTa);
                        List<Type> typeArgs2 = baseTa.typeArguments();
                        if (typeArgs2!=null) toExamineArgs.addAll(typeArgs2);
                    }
                }
            }
            return res;
        }

	    /** Visit the children of the declaration. */
	    public Node visitChildren(NodeVisitor v) {
	        X10FieldDecl_c n = (X10FieldDecl_c) super.visitChildren(v);
	        TypeNode hasType = (TypeNode) visitChild(n.hasType, v);
            return n.reconstruct(hasType);
	    }
	    
	    public Node visitSignature(NodeVisitor v) {
	    	X10FieldDecl_c n = (X10FieldDecl_c) super.visitSignature(v);
	    	  TypeNode hasType = (TypeNode) visitChild(n.hasType, v);
	    	  return n.reconstruct(hasType);
	        }


	    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	        boolean isInterface = fi != null && fi.container() != null &&
	        fi.container().get().toClass().flags().isInterface();

	        Flags fs = flags.flags();
	        Boolean f = fs.isFinal();
            if (isInterface) {
                fs = fs.clearPublic();
                fs = fs.clearStatic();
            }
            fs = fs.clearFinal();
            w.write(fs.translate());
            for (Iterator<AnnotationNode> i = (((X10Ext) this.ext()).annotations()).iterator(); i.hasNext(); ) {
                AnnotationNode an = i.next();
                an.prettyPrint(w, tr);
                w.allowBreak(0, " ");
            }
            if (f)
	            w.write("val ");
	        else
                w.write("var ");
	        tr.print(this, name, w);
            w.allowBreak(2, 2, ":", 1);
            print(type, w, tr);

	        if (init != null) {
	            w.write(" =");
	            w.allowBreak(2, " ");
	            print(init, w, tr);
	        }

	        w.write(";");
	    }

	    public Node checkConstants(ContextVisitor tc) {
	    	Type native_annotation_type = tc.typeSystem().NativeType();
			if (!((X10Ext)ext).annotationMatching(native_annotation_type).isEmpty()) {
				fi.setNotConstant();
				return this;
			} else {
				return super.checkConstants(tc);
			}
	    }
}

