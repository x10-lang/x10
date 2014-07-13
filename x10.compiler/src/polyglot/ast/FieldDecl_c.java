/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.ast;

import java.util.*;

import polyglot.frontend.*;
import polyglot.types.*;
import polyglot.types.VarDef_c.ConstantValue;
import polyglot.util.*;
import polyglot.visit.*;
import x10.errors.Errors;

/**
 * A <code>FieldDecl</code> is an immutable representation of the declaration
 * of a field of a class.
 */
public abstract class FieldDecl_c extends Term_c implements FieldDecl {
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

    public Node buildTypesOverride(TypeBuilder tb) {
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
            @Override
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

    protected abstract FieldDef createFieldDef(TypeSystem ts, ClassDef ct, Flags flags);

    public Context enterScope(Context c) {
        if (ii != null) {
            return c.pushCode(ii);
        }
        return c;
    }
    
    @Override
    public abstract void setResolver(final Node parent, TypeCheckPreparer v);

    public Node checkConstants(ContextVisitor tc) {
        if (init == null || ! init.isConstant() || ! fi.flags().isFinal()) {
            fi.setNotConstant();
        } else {
            x10.types.constants.ConstantValue cv = init.constantValue();
            if (cv instanceof x10.types.constants.ClosureValue) {
                // Hacky workaround.  
                // The issue is that the code is structured to cache the ConstantValue
                // (which contains a Closure_c AST) in the FieldInstance.  This AST
                // is not properly visited by various Jobs, and therefore when we pull
                // the Closure_c out of the ConstantValue and attempt to inline it 
                // later in compilation, we end up with a closure body that isn't legal
                // because various transformations haven't been performaed on it.
                fi.setNotConstant();
            } else {
                fi.setConstantValue(init.constantValue());
            }
        }

        return this;
    }

    public Node visitSignature(NodeVisitor v) {
	FlagsNode flags = (FlagsNode) this.visitChild(this.flags, v);
        TypeNode type = (TypeNode) this.visitChild(this.type, v);
        Id name = (Id) this.visitChild(this.name, v);
        return reconstruct(flags, type, name, this.init);
    }

    public Node typeCheckBody(Node parent, TypeChecker tc, TypeChecker childtc) {
        FieldDecl_c n = this;
        Expr init = (Expr) n.visitChild(n.init, childtc);
        n = (FieldDecl_c) n.init(init);
        return n.checkConstants(tc);
    }
    
    public abstract Node typeCheck(ContextVisitor tc);

    public Node conformanceCheck(ContextVisitor tc) {
        TypeSystem ts = tc.typeSystem();

        // Get the fi flags, not the node flags since the fi flags
        // account for being nested within an interface.
        Flags flags = fi.flags();

        try {
            ts.checkFieldFlags(flags);
        }
        catch (SemanticException e) {
            Errors.issue(tc.job(), e, this);
        }

        Type fcontainer = Types.get(fieldDef().container());
        
        if (fcontainer.isClass()) {
            ClassType container = fcontainer.toClass();

            if (container.flags().isInterface()) {
        	if (flags.isProtected() || flags.isPrivate()) {
        	    Errors.issue(tc.job(),
        	            new Errors.InterfaceMembersMustBePublic(position()));
        	}
            }

            // check that inner classes do not declare static fields, unless they
            // are compile-time constants
            if (flags.isStatic() &&
        	    (container.isInnerClass() || container.isLocal() || container.isAnonymous())) {
        	// it's a static field in an inner class.
        	if (!flags.isFinal() || init == null || !init.isConstant()) {
        	    Errors.issue(tc.job(), 
        	    		new Errors.InnerClassCannotDeclareStaticFields(fieldDef(), container, position()));
        	}
            }
        }

        return this;
    }

    public NodeVisitor exceptionCheckEnter(ExceptionChecker ec) {
        return ec.push(new ExceptionChecker.CodeTypeReporter("A field initializer"));
    }

    public Term firstChild() {
        return type;
    }

    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
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

    public abstract void prettyPrint(CodeWriter w, PrettyPrinter tr);

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
