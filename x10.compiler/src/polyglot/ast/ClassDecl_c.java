/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2007 Polyglot project group, Cornell University
 * Copyright (c) 2006-2007 IBM Corporation
 * 
 */

package polyglot.ast;

import java.util.*;

import polyglot.frontend.Goal;
import polyglot.main.Report;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;

/**
 * A <code>ClassDecl</code> is the definition of a class, abstract class,
 * or interface. It may be a public or other top-level class, or an inner
 * named class, or an anonymous class.
 */
public class ClassDecl_c extends Term_c implements ClassDecl
{
    protected FlagsNode flags;
    protected Id name;
    protected TypeNode superClass;
    protected List<TypeNode> interfaces;
    protected ClassBody body;
    protected ConstructorDef defaultCI;
    protected ClassDef type;

    public ClassDecl_c(Position pos, FlagsNode flags, Id name,
            TypeNode superClass, List interfaces, ClassBody body) {
        super(pos);
        // superClass may be null, interfaces may be empty
        assert(flags != null && name != null && interfaces != null && body != null); 
        this.flags = flags;
        this.name = name;
        this.superClass = superClass;
        this.interfaces = TypedList.copyAndCheck(interfaces, TypeNode.class, true);
        this.body = body;
    }
    
    public List<Def> defs() {
        return Collections.<Def>singletonList(type);
    }

    public MemberDef memberDef() {
        return type;
    }

    public ClassDef classDef() {
        return type;
    }

    public ClassDecl classDef(ClassDef type) {
        if (type == this.type) return this;
        ClassDecl_c n = (ClassDecl_c) copy();
        n.type = type;
        return n;
    }

    public FlagsNode flags() {
        return this.flags;
    }

    public ClassDecl flags(FlagsNode flags) {
        ClassDecl_c n = (ClassDecl_c) copy();
        n.flags = flags;
        return n;
    }

    public Id name() {
        return this.name;
    }

    public ClassDecl name(Id name) {
        ClassDecl_c n = (ClassDecl_c) copy();
        n.name = name;
        return n;
    }

    public TypeNode superClass() {
        return this.superClass;
    }

    public ClassDecl superClass(TypeNode superClass) {
        ClassDecl_c n = (ClassDecl_c) copy();
        n.superClass = superClass;
        return n;
    }

    public List<TypeNode> interfaces() {
        return this.interfaces;
    }

    public ClassDecl interfaces(List<TypeNode> interfaces) {
        ClassDecl_c n = (ClassDecl_c) copy();
        n.interfaces = TypedList.copyAndCheck(interfaces, TypeNode.class, true);
        return n;
    }

    public ClassBody body() {
        return this.body;
    }

    public ClassDecl body(ClassBody body) {
        ClassDecl_c n = (ClassDecl_c) copy();
        n.body = body;
        return n;
    }

    protected ClassDecl_c reconstruct(FlagsNode flags, Id name, TypeNode superClass, List<TypeNode> interfaces, ClassBody body) {
        if (flags != this.flags || name != this.name || superClass != this.superClass || ! CollectionUtil.allEqual(interfaces, this.interfaces) || body != this.body) {
            ClassDecl_c n = (ClassDecl_c) copy();
            n.flags = flags;
            n.name = name;
            n.superClass = superClass;
            n.interfaces = TypedList.copyAndCheck(interfaces, TypeNode.class, true);
            n.body = body;
            return n;
        }

        return this;
    }

    /**
     * Return the first (sub)term performed when evaluating this
     * term.
     */
    public Term firstChild() {
        return body();
    }

    /**
     * Visit this term in evaluation order.
     */
    public List<Term> acceptCFG(CFGBuilder v, List<Term> succs) {
        v.visitCFG(this.body(), this, EXIT);
        return succs;
    }
    
    public Node visitSignature(NodeVisitor v) {
	FlagsNode flags = (FlagsNode) visitChild(this.flags, v);
        Id name = (Id) visitChild(this.name, v);
        TypeNode superClass = (TypeNode) visitChild(this.superClass, v);
        List<TypeNode> interfaces = visitList(this.interfaces, v);
        ClassBody body = this.body;
        return reconstruct(flags, name, superClass, interfaces, body);
    }

    public Node visitChildren(NodeVisitor v) {
        ClassDecl_c n = (ClassDecl_c) visitSignature(v);
        ClassBody body = (ClassBody) n.visitChild(n.body, v);
        return body == n.body ? n : n.body(body);
    }

    public Node buildTypesOverride(TypeBuilder tb) throws SemanticException {
	ClassDecl_c n = this;
	n = n.preBuildTypes(tb);
	n = n.buildTypesBody(tb);
	n = n.postBuildTypes(tb);
	return n;
    }

    private ClassDecl_c buildTypesBody(TypeBuilder tb) throws SemanticException {
	ClassDecl_c n = this;
	TypeBuilder tb2 = tb.pushClass(n.type);
	ClassBody body = (ClassBody) n.visitChild(n.body, tb2);
	n = (ClassDecl_c) n.body(body);
	return n;
    }
    
    public ClassDecl_c preBuildTypes(TypeBuilder tb) throws SemanticException {
        tb = tb.pushClass(position(), flags.flags(), name.id());

        ClassDef type = tb.currentClass();

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

        ClassDecl_c n = this;
        FlagsNode flags = (FlagsNode) n.visitChild(n.flags, tb);
        Id name = (Id) n.visitChild(n.name, tb);

        TypeNode superClass = (TypeNode) n.visitChild(n.superClass, tb);
        List<TypeNode> interfaces = n.visitList(n.interfaces, tb);

        n = n.reconstruct(flags, name, superClass, interfaces, n.body);
        
        n.setSuperClass(tb.typeSystem(), type);
        n.setInterfaces(tb.typeSystem(), type);

        n = (ClassDecl_c) n.classDef(type).flags(flags.flags(type.flags()));
    
        return n;
    }
    
    public ClassDecl_c postBuildTypes(TypeBuilder tb) throws SemanticException {
	ClassDecl_c n = (ClassDecl_c) this.copy();
	
        if (n.defaultConstructorNeeded()) {
            ConstructorDecl cd = n.createDefaultConstructor(type, tb.typeSystem(), tb.nodeFactory());
            TypeBuilder tb2 = tb.pushClass(n.type);
            cd = (ConstructorDecl) tb2.visitEdge(n, cd);
            n = (ClassDecl_c) n.body(n.body().addMember(cd));
            n.defaultCI = cd.constructorDef();
        }
        
        return n;
    }

    public Context enterChildScope(Node child, Context c) {
        if (child == this.body) {
            TypeSystem ts = c.typeSystem();
            c = c.pushClass(type, type.asType());
        }
        else if (child == this.superClass || this.interfaces.contains(child)) {
            // Add this class to the context, but don't push a class scope.
            // This allows us to detect loops in the inheritance
            // hierarchy, but avoids an infinite loop.
            c = c.pushBlock();
            c.addNamed(this.type.asType());
        }
        return super.enterChildScope(child, c);
    }

//    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
//        if (type == null) {
//            throw new InternalCompilerError("Missing type.", position());
//        }
//
//        checkSupertypeCycles(ar.typeSystem());
//
//        ClassDef type = classDef();
//
//        // Make sure that the inStaticContext flag of the class is correct.
//        Context ctxt = ar.context();
//        type.inStaticContext(ctxt.inStaticContext());
//
//        return this;
//    }

    protected void checkSupertypeCycles(TypeSystem ts) throws SemanticException {
        Ref<? extends Type> stref = type.superType();
        if (stref != null) {
            Type t = stref.get();
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
            assert ! (t instanceof UnknownType);
            if (! t.isClass() || ! t.toClass().flags().isInterface()) {
                String s = type.flags().isInterface() ? "extend" : "implement";
                throw new SemanticException("Cannot " + s + " type " + t + "; not an interface.",
                        position());
            }
            ts.checkCycles((ReferenceType) t);
        }
    }

    protected void setSuperClass(TypeSystem ts, ClassDef thisType) throws SemanticException {
        TypeNode superClass = this.superClass;
        
        QName objectName = ((ClassType) ts.Object()).fullName();

        if (superClass != null) {
            Ref<? extends Type> t = superClass.typeRef();
            if (Report.should_report(Report.types, 3))
                Report.report(3, "setting superclass of " + this.type + " to " + t);
            thisType.superType(t);
        }
        else if (thisType.asType().equals((Object) ts.Object()) || thisType.fullName().equals(objectName)) {
            // the type is the same as ts.Object(), so it has no superclass.
            if (Report.should_report(Report.types, 3))
                Report.report(3, "setting superclass of " + thisType + " to " + null);
            thisType.superType(null);
        }
        else {
            // the superclass was not specified, and the type is not the same
            // as ts.Object() (which is typically java.lang.Object)
            // As such, the default superclass is ts.Object().
            if (Report.should_report(Report.types, 3))
                Report.report(3, "setting superclass of " + this.type + " to " + ts.Object());
            thisType.superType(Types.<Type>ref(ts.Object()));
        }
    }

    protected void setInterfaces(TypeSystem ts, ClassDef thisType) throws SemanticException {
        List<TypeNode> interfaces = this.interfaces;
        for (TypeNode tn : interfaces) {
            Ref<? extends Type> t = tn.typeRef();

            if (Report.should_report(Report.types, 3))
                Report.report(3, "adding interface of " + thisType + " to " + t);

            thisType.addInterface(t);
        }
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

    protected ConstructorDecl createDefaultConstructor(ClassDef thisType, TypeSystem ts, NodeFactory nf)
    throws SemanticException
    {
        Position pos = body().position().startOf();

        Block block = null;

        Ref<? extends Type> superType = thisType.superType();

        if (superType != null) {
            ConstructorCall cc = nf.SuperCall(pos, Collections.EMPTY_LIST);
            block = nf.Block(pos, cc);
        }
        else {
            block = nf.Block(pos);
        }

        ConstructorDecl cd = nf.ConstructorDecl(pos,
                nf.FlagsNode(pos, Flags.PUBLIC),
                name, Collections.EMPTY_LIST,
                Collections.EMPTY_LIST,
                block);
        return cd;
    }
    
    public Node typeCheckOverride(Node parent, ContextVisitor tc) throws SemanticException {
    	ClassDecl_c n = this;
    	
    	NodeVisitor v = tc.enter(parent, n);
    	
    	if (v instanceof PruningVisitor) {
    		return this;
    	}
    	
    	TypeChecker childtc = (TypeChecker) v;
    	n = (ClassDecl_c) n.typeCheckSupers(tc, childtc);
    	n = (ClassDecl_c) n.typeCheckBody(parent, tc, childtc);
    	
    	return n;
    }
    
    public Node typeCheckSupers(ContextVisitor tc, TypeChecker childtc) throws SemanticException {
        ClassDecl_c n = this;

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
        
        if (n.superClass() != null)
            assert type.superType() == n.superClass().typeRef();
        
        n = n.reconstruct(flags, name, superClass, interfaces, body);
        n.checkSupertypeCycles(tc.typeSystem());

        return n;
    }
    
    public Node typeCheckBody(Node parent, ContextVisitor tc, TypeChecker childtc) throws SemanticException {
        ClassDecl_c old = this;

        ClassDecl_c n = this;

        FlagsNode flags = n.flags;
        Id name = n.name;
        TypeNode superClass = n.superClass;
        List<TypeNode> interfaces = n.interfaces;
        ClassBody body = n.body;

        body = (ClassBody) n.visitChild(body, childtc);

        n = n.reconstruct(flags, name, superClass, interfaces, body);
        n = (ClassDecl_c) tc.leave(parent, old, n, childtc);

        return n;
    }
    
    public Node conformanceCheck(ContextVisitor tc) throws SemanticException {
        TypeSystem ts = tc.typeSystem();

        ClassType type = this.type.asType();
        Name name = this.name.id();

        // The class cannot have the same simple name as any enclosing class.
        if (type.isNested()) {
            ClassType container = type.outer();

            while (container != null) {
                if (!container.isAnonymous()) {
                    Name cname = container.name();

                    if (cname.equals(name)) {
                        throw new SemanticException("Cannot declare member " +
                                "class \"" + type.fullName() +
                                "\" inside class with the " +
                                "same name.", position());
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
        if (type.isLocal()) {
            Context ctxt = tc.context();

            if (ctxt.isLocal(name)) {
                // Something with the same name was declared locally.
                // (but not in an enclosing class)                                    
                Named nm = ctxt.find(ts.TypeMatcher(name));
                if (nm instanceof Type) {
                    Type another = (Type) nm;
                    if (another.isClass() && another.toClass().isLocal()) {
                        throw new SemanticException("Cannot declare local " +
                                "class \"" + this.type + "\" within the same " +
                                "method, constructor or initializer as another " +
                                "local class of the same name.", position());
                    }
                }
            }                
        }

        // check that inner classes do not declare member interfaces
        if (type.isMember() && type.flags().isInterface() &&
                type.outer().isInnerClass()) {
            // it's a member interface in an inner class.
            throw new SemanticException("Inner classes cannot declare " + 
                    "member interfaces.", this.position());             
        }

        // Make sure that static members are not declared inside inner classes
        if (type.isMember() && type.flags().isStatic() 
                && type.outer().isInnerClass()) {
            throw new SemanticException("Inner classes cannot declare static " 
                    + "member classes.", position());
        }

        if (type.superClass() != null) {
            if (! type.superClass().isClass() || type.superClass().toClass().flags().isInterface()) {
                throw new SemanticException("Cannot extend non-class \"" +
                        type.superClass() + "\".",
                        position());
            }

            if (type.superClass().toClass().flags().isFinal()) {
                throw new SemanticException("Cannot extend final class \"" +
                        type.superClass() + "\".",
                        position());
            }

            if (type.typeEquals(ts.Object(), tc.context())) {
                throw new SemanticException("Class \"" + this.type + "\" cannot have a superclass.",
                        superClass.position());
            }
        }

        for (Iterator<TypeNode> i = interfaces.iterator(); i.hasNext(); ) {
            TypeNode tn = (TypeNode) i.next();
            Type t = tn.type();

            if (! t.isClass() || ! t.toClass().flags().isInterface()) {
                throw new SemanticException("Superinterface " + t + " of " +
                        type + " is not an interface.", tn.position());
            }

            if (type.typeEquals(ts.Object(), tc.context())) {
                throw new SemanticException("Class " + this.type + " cannot have a superinterface.",
                        tn.position());
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
            throw new SemanticException(e.getMessage(), position());
        }

        // Check the class implements all abstract methods that it needs to.
        ts.checkClassConformance(type, enterChildScope(body, tc.context()));

        return this;
    }

    public String toString() {
	Flags flags = this.flags.flags();
        return flags.clearInterface().translate() +
        (flags.isInterface() ? "interface " : "class ") + name + " " + body;
    }

    public void prettyPrintHeader(CodeWriter w, PrettyPrinter tr) {
        w.begin(0);
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

        tr.print(this, name, w);

        if (superClass() != null) {
            w.allowBreak(0);
            w.write("extends ");
            print(superClass(), w, tr);
        }

        if (! interfaces.isEmpty()) {
            w.allowBreak(2);
            if (flags.isInterface()) {
                w.write("extends ");
            }
            else {
                w.write("implements ");
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
