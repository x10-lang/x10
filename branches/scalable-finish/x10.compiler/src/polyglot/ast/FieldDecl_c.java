/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2007 Polyglot project group, Cornell University
 * Copyright (c) 2006-2007 IBM Corporation
 * 
 */

package polyglot.ast;

import java.util.*;

import polyglot.frontend.*;
import polyglot.types.*;
import polyglot.types.VarDef_c.ConstantValue;
import polyglot.util.*;
import polyglot.visit.*;

/**
 * A <code>FieldDecl</code> is an immutable representation of the declaration
 * of a field of a class.
 */
public class FieldDecl_c extends Term_c implements FieldDecl {
    protected FlagsNode flags;
    protected TypeNode type;
    protected Id name;
    protected Expr init;
    protected FieldDef fi;
    protected InitializerDef ii;

    public FieldDecl_c(Position pos, FlagsNode flags, TypeNode type,
            Id name, Expr init)
    {
        super(pos);
        assert(flags != null && type != null && name != null); // init may be null
        this.flags = flags;
        this.type = type;
        this.name = name;
        this.init = init;
    }

    public List<Def> defs() {
        if (init == null)
            return Collections.<Def>singletonList(fi);
        else {
            return CollectionUtil.<Def>list(fi, ii);
        }
    }

    public MemberDef memberDef() {
        return fi;
    }

    public VarDef varDef() {
        return fi;
    }

    public CodeDef codeDef() {
        return ii;
    }

    /** Get the initializer instance of the initializer. */
    public InitializerDef initializerDef() {
        return ii;
    }

    /** Set the initializer instance of the initializer. */
    public FieldDecl initializerDef(InitializerDef ii) {
        if (ii == this.ii) return this;
        FieldDecl_c n = (FieldDecl_c) copy();
        n.ii = ii;
        return n;
    }

    /** Get the type of the declaration. */
    public Type declType() {
        return type.type();
    }

    /** Get the flags of the declaration. */
    public FlagsNode flags() {
        return flags;
    }

    /** Set the flags of the declaration. */
    public FieldDecl flags(FlagsNode flags) {
        FieldDecl_c n = (FieldDecl_c) copy();
        n.flags = flags;
        return n;
    }

    /** Get the type node of the declaration. */
    public TypeNode type() {
        return type;
    }

    /** Set the type of the declaration. */
    public FieldDecl type(TypeNode type) {
        FieldDecl_c n = (FieldDecl_c) copy();
        n.type = type;
        return n;
    }

    /** Get the name of the declaration. */
    public Id name() {
        return name;
    }

    /** Set the name of the declaration. */
    public FieldDecl name(Id name) {
        FieldDecl_c n = (FieldDecl_c) copy();
        n.name = name;
        return n;
    }

    public Term codeBody() {
        return init;
    }

    /** Get the initializer of the declaration. */
    public Expr init() {
        return init;
    }

    /** Set the initializer of the declaration. */
    public FieldDecl init(Expr init) {
        FieldDecl_c n = (FieldDecl_c) copy();
        n.init = init;
        return n;
    }

    /** Set the field instance of the declaration. */
    public FieldDecl fieldDef(FieldDef fi) {
        if (fi == this.fi) return this;
        FieldDecl_c n = (FieldDecl_c) copy();
        n.fi = fi;
        return n;
    }

    /** Get the field instance of the declaration. */
    public FieldDef fieldDef() {
        return fi;
    }

    /** Reconstruct the declaration. */
    protected FieldDecl_c reconstruct(FlagsNode flags, TypeNode type, Id name, Expr init) {
        if (this.flags != flags || this.type != type || this.name != name || this.init != init) {
            FieldDecl_c n = (FieldDecl_c) copy();
            n.flags = flags;
            n.type = type;
            n.name = name;
            n.init = init;
            return n;
        }

        return this;
    }

    /** Visit the children of the declaration. */
    public Node visitChildren(NodeVisitor v) {
        FieldDecl_c n = (FieldDecl_c) visitSignature(v);
        Expr init = (Expr) n.visitChild(n.init, v);
        return init == n.init ? n : n.init(init);
    }

    public Node buildTypesOverride(TypeBuilder tb) throws SemanticException {
        TypeSystem ts = tb.typeSystem();

        ClassDef ct = tb.currentClass();
        assert ct != null;

        Flags flags = this.flags.flags();

        if (ct.flags().isInterface()) {
            flags = flags.Public().Static().Final();
        }

        FieldDef fi = createFieldDef(ts, ct, flags);
        ct.addField(fi);

        TypeBuilder tbChk = tb.pushDef(fi);
        
        InitializerDef ii = null;

        if (init != null) {
            Flags iflags = flags.isStatic() ? Flags.STATIC : Flags.NONE;
            ii = createInitializerDef(ts, ct, iflags);
            fi.setInitializer(ii);
            tbChk = tbChk.pushCode(ii);
        }

        final TypeBuilder tbx = tb;
        final FieldDef mix = fi;
        
        FieldDecl_c n = (FieldDecl_c) this.visitSignature(new NodeVisitor() {
            public Node override(Node n) {
                return FieldDecl_c.this.visitChild(n, tbx.pushDef(mix));
            }
        });
        
        fi.setType(n.type().typeRef());

        Expr init = (Expr) n.visitChild(n.init, tbChk);
        n = (FieldDecl_c) n.init(init);

        n = (FieldDecl_c) n.fieldDef(fi);
        
        if (ii != null) {
            n = (FieldDecl_c) n.initializerDef(ii);
        }

        n = (FieldDecl_c) n.flags(n.flags.flags(flags));

        return n;
    }

    protected InitializerDef createInitializerDef(TypeSystem ts, ClassDef ct, Flags iflags) {
	InitializerDef ii;
	ii = ts.initializerDef(init.position(), Types.<ClassType>ref(ct.asType()), iflags);
	return ii;
    }

    protected FieldDef createFieldDef(TypeSystem ts, ClassDef ct, Flags flags) {
	FieldDef fi = ts.fieldDef(position(), Types.ref(ct.asType()), flags, type.typeRef(), name.id());
	return fi;
    }

    public Context enterScope(Context c) {
        if (ii != null) {
            return c.pushCode(ii);
        }
        return c;
    }
    
    @Override
    public void setResolver(final Node parent, TypeCheckPreparer v) {
    	final FieldDef def = fieldDef();
    	Ref<ConstantValue> rx = def.constantValueRef();
    	if (rx instanceof LazyRef) {
    		LazyRef<ConstantValue> r = (LazyRef<ConstantValue>) rx;
    		  TypeChecker tc0 = new TypeChecker(v.job(), v.typeSystem(), v.nodeFactory(), v.getMemo());
    		  final TypeChecker tc = (TypeChecker) tc0.context(v.context().freeze());
    		  final Node n = this;
    		  r.setResolver(new AbstractGoal_c("ConstantValue") {
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

    public Node checkConstants(ContextVisitor tc) throws SemanticException {
        if (init == null || ! init.isConstant() || ! fi.flags().isFinal()) {
            fi.setNotConstant();
        }
        else {
            fi.setConstantValue(init.constantValue());
        }

        return this;
    }

    public Node visitSignature(NodeVisitor v) {
	FlagsNode flags = (FlagsNode) this.visitChild(this.flags, v);
        TypeNode type = (TypeNode) this.visitChild(this.type, v);
        Id name = (Id) this.visitChild(this.name, v);
        return reconstruct(flags, type, name, this.init);
    }

    public Node typeCheckBody(Node parent, TypeChecker tc, TypeChecker childtc) throws SemanticException {
        FieldDecl_c n = this;
        Expr init = (Expr) n.visitChild(n.init, childtc);
        n = (FieldDecl_c) n.init(init);
        return n.checkConstants(tc);
    }
    
    public Node typeCheck(ContextVisitor tc) throws SemanticException {
	TypeSystem ts = tc.typeSystem();
	
	if (init != null && ! (init.type() instanceof UnknownType)) {
	    if (init instanceof ArrayInit) {
		((ArrayInit) init).typeCheckElements(tc, type.type());
	    }
	    else {
		if (! ts.isImplicitCastValid(init.type(), type.type(), tc.context()) &&
			! ts.typeEquals(init.type(), type.type(), tc.context()) &&
			! ts.numericConversionValid(type.type(), init.constantValue(), tc.context())) {
		    
		    throw new SemanticException("The type of the variable " +
		                                "initializer \"" + init.type() +
		                                "\" does not match that of " +
		                                "the declaration \"" +
		                                type.type() + "\".",
		                                init.position());
		}
	    }
	}
	
	return this;
    }

    public Node conformanceCheck(ContextVisitor tc) throws SemanticException {
        TypeSystem ts = tc.typeSystem();

        // Get the fi flags, not the node flags since the fi flags
        // account for being nested within an interface.
        Flags flags = fi.flags();

        try {
            ts.checkFieldFlags(flags);
        }
        catch (SemanticException e) {
            throw new SemanticException(e.getMessage(), position());
        }

        Type fcontainer = Types.get(fieldDef().container());
        
        if (fcontainer.isClass()) {
            ClassType container = fcontainer.toClass();

            if (container.flags().isInterface()) {
        	if (flags.isProtected() || flags.isPrivate()) {
        	    throw new SemanticException("Interface members must be public.",
        	                                position());
        	}
            }

            // check that inner classes do not declare static fields, unless they
            // are compile-time constants
            if (flags.isStatic() &&
        	    container.isInnerClass()) {
        	// it's a static field in an inner class.
        	if (!flags.isFinal() || init == null || !init.isConstant()) {
        	    throw new SemanticException("Inner classes cannot declare " +
        	                                "static fields, unless they are compile-time " +
        	                                "constant fields.", this.position());
        	}
            }
        }

        return this;
    }

    public NodeVisitor exceptionCheckEnter(ExceptionChecker ec) throws SemanticException {
        return ec.push(new ExceptionChecker.CodeTypeReporter("A field initializer"));
    }

    public Type childExpectedType(Expr child, AscriptionVisitor av) {
        if (child == init) {
            TypeSystem ts = av.typeSystem();

            // If the RHS is an integral constant, we can relax the expected
            // type to the type of the constant.
            if (ts.numericConversionValid(type.type(), child.constantValue(), av.context())) {
                return child.type();
            }
            else {
                return type.type();
            }
        }

        return child.type();
    }

    public Term firstChild() {
        return type;
    }

    public List<Term> acceptCFG(CFGBuilder v, List<Term> succs) {
        if (init != null) {
            v.visitCFG(type, init, ENTRY);
            v.visitCFG(init, this, EXIT);
        } else {
            v.visitCFG(type, this, EXIT);
        }

        return succs;
    }


    public String toString() {
        return flags.flags().translate() + type + " " + name +
        (init != null ? " = " + init : "");
    }

    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        boolean isInterface = fi != null && fi.container() != null &&
        fi.container().get().toClass().flags().isInterface();

        Flags f = flags.flags();

        if (isInterface) {
            f = f.clearPublic();
            f = f.clearStatic();
            f = f.clearFinal();
        }

        w.write(f.translate());
        print(type, w, tr);
        w.allowBreak(2, 2, " ", 1);
        tr.print(this, name, w);

        if (init != null) {
            w.write(" =");
            w.allowBreak(2, " ");
            print(init, w, tr);
        }

        w.write(";");
    }

    public void dump(CodeWriter w) {
        super.dump(w);

        if (fi != null) {
            w.allowBreak(4, " ");
            w.begin(0);
            w.write("(instance " + fi + ")");
            w.end();
        }

        w.allowBreak(4, " ");
        w.begin(0);
        w.write("(name " + name + ")");
        w.end();
    }
    

}
