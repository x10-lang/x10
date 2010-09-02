package x10.visit;

import polyglot.ast.*;
import polyglot.util.ErrorInfo;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import polyglot.frontend.Job;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import x10.ast.*;
import x10.types.X10TypeMixin;
import x10.types.X10Flags;
import x10.types.X10FieldDef_c;
import x10.types.X10TypeSystem;

import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

//class
public class CheckEscapingThis extends NodeVisitor
{
    public static class Main extends NodeVisitor {
        private final Job job;
        public Main(Job job) {
            this.job = job;
        }
        @Override public NodeVisitor enter(Node n) {
            if (n instanceof X10ClassDecl_c)
                new CheckEscapingThis((X10ClassDecl_c)n,job,
                        job.extensionInfo().typeSystem());
            return this;
        }
    }
    // we gather info on every private/final method
    private static class MethodInfo {
        private final Set<X10FieldDef_c> read = new HashSet<X10FieldDef_c>();
        private final Set<X10FieldDef_c> write = new HashSet<X10FieldDef_c>();
        private final Set<X10FieldDef_c> seqWrite = new HashSet<X10FieldDef_c>();

    }
    private final Job job;
    private final TypeSystem ts;
    private final X10ClassDecl_c xlass;
    private final Type xlassType;
    private final HashMap<X10MethodDecl_c,MethodInfo> allMethods = new HashMap<X10MethodDecl_c, MethodInfo>();
    private final HashSet<X10ConstructorDecl_c> allCtors = new HashSet<X10ConstructorDecl_c>();
    // the set of VAR and VAL without initializers (we need to check that VAL are read properly, and that VAR are written and read properly.
    private final HashSet<X10FieldDecl_c> VAL = new HashSet<X10FieldDecl_c>();
    private final HashSet<X10FieldDecl_c> VAR = new HashSet<X10FieldDecl_c>();

    public CheckEscapingThis(X10ClassDecl_c xlass, Job job, TypeSystem ts) {
        this.job = job;
        this.ts = ts;
        this.xlass = xlass;
        this.xlassType = X10TypeMixin.baseType(xlass.classDef().asType());
        typeCheck();
    }
    private void typeCheck() {
        // visit every ctor, and every method called from a ctor, and check that this and super do not escape
        final X10ClassBody_c body = (X10ClassBody_c)xlass.body();
        for (ClassMember classMember : body.members()) {
            if (classMember instanceof X10ConstructorDecl_c) {
                final X10ConstructorDecl_c ctor = (X10ConstructorDecl_c) classMember;
                final Block ctorBody = ctor.body();
                // for native ctors, we don't have a body
                if (ctorBody!=null) {
                    allCtors.add(ctor);
                    ctorBody.visit(this);
                }
            } else if (classMember instanceof X10FieldDecl_c) {
                X10FieldDecl_c field = (X10FieldDecl_c) classMember;
                final Flags flags = field.flags().flags();
                if (flags.isStatic()) continue;
                // if the field has an init, then we do not need to track it (it is always assigned before read)
                if (field.init()!=null) continue;
                boolean isVal = flags.isFinal();
                // if a VAR has a default value, then we already created an init() expr in X10FieldDecl_c.typeCheck
                (isVal ? VAL : VAR).add(field);

            }
        }
    }
    private X10MethodDecl_c findMethod(X10Call call) {
        MethodInstance mi2 = call.methodInstance();
        final X10ClassBody_c body = (X10ClassBody_c)xlass.body();
        for (ClassMember classMember : body.members()) {
            if (classMember instanceof X10MethodDecl_c) {
                X10MethodDecl_c md = (X10MethodDecl_c) classMember;
                final MethodInstance mi = md.methodDef().asInstance();
                if (mi2.def().equals(mi.def())) return md;
            }
        }
        throw new InternalCompilerError("Could find method "+call+" in class "+xlass,call.position());
    }

    @Override
    public Node visitEdgeNoOverride(Node parent, Node n) {
        // You can access "this" for field access and field assignment.
        // field assignment:
        if (n instanceof FieldAssign) {
            FieldAssign assign = (FieldAssign) n;
            if (assign.target() instanceof Special) {
                assign.right().visit(this);
                return n;
            }
        }
        // field access:
        if (n instanceof Field && ((Field)n).target() instanceof Special) {
            return n;
        }
        // You can also access "this" as the receiver of property calls (because they are MACROS that are expanded to field access)
        // and as the receiver of private/final calls
        if (n instanceof X10Call) {
            final X10Call call = (X10Call) n;
            final Flags flags = call.methodInstance().flags();
            if (isThis(call.target())) {
                if (flags.contains(X10Flags.PROPERTY)) {
                    // property-method calls are ok
                } else {
                    // the method must be final or private
                    X10MethodDecl_c method = findMethod(call);
                    if (allMethods.containsKey(method)) {
                        // we already analyzed this method
                    } else {
                        allMethods.put(method,new MethodInfo()); // prevent infinite recursion
                        // verify the method is indeed private/final
                        if (!flags.isFinal() && !flags.isPrivate())  {
                            report("The call "+call+" is illegal because you can only call private or final methods from a constructor or from methods called from a constructor",call.position());
                        }
                        method.body().visit(this);
                    }

                }

                // it is enough to just recurse into the arguments (because the receiver is either this or super)
                for (Expr e : call.arguments())
                    e.visit(this);
                return n;
            }                        
        }
        // You cannot use "this" for anything else!
        if (isThis(n)) {
            report("'this' and 'super' cannot escape from a constructor or from methods called from a constructor",n.position());
        }
        n.del().visitChildren(this);
        return n;
    }
    private void report(String s, Position p) {
        job.compiler().errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR,s,p);
    }
    private boolean isThis(Node n) {
        if (!(n instanceof Special)) return false;
        final Special special = (Special) n;
        return //special.kind()==Special.THIS && // both this and super cannot escape
               ts.typeEquals(X10TypeMixin.baseType(special.type()), xlassType,null);
    }
}