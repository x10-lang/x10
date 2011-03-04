package x10.ast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassMember;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Local;
import polyglot.ast.MethodDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.PackageNode;
import polyglot.ast.SourceFile;
import polyglot.ast.Special;
import polyglot.ast.Stmt;
import polyglot.ast.TopLevelDecl;
import polyglot.ast.TypeNode;
import polyglot.frontend.FileSource;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.frontend.Source;
import polyglot.types.ClassDef;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LocalInstance;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.Package;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;
import x10.types.ParameterType;
import x10.types.X10ClassType;
import x10.types.X10Context;
import x10.types.X10Flags;
import x10.types.X10MethodInstance;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;

public class X10Boxed_c extends X10Cast_c {

    public X10Boxed_c(Position pos, TypeNode castType, Expr expr, ConversionType convert) {
        super(pos, castType, expr, convert);
    }

    // e as I
    // ->
    // ((x:V): I => {
    //     class X extends Box[V] implements I {
    //         def this(x:V) { super(x); }
    //         def m() = x.m();
    //     }
    //     return new X(x);
    //  })(e)
    public Expr wrap(ContextVisitor tc) throws SemanticException {
        Expr e = expr;
        Type toType = castType.type();

        X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
        X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
        X10Context context = (X10Context) tc.context();
        
        ClassDef currClass = context.currentClassDef();
        Package currPackage = context.package_();

        assert ts.isInterfaceType(toType);
        
        if (ts.typeEquals(toType, ts.Object(), context)) {
            Position pos = this.position();
            Type t = ts.boxOf(Types.ref(expr.type()));
            return X10Cast_c.check(nf.New(pos, nf.CanonicalTypeNode(pos, Types.ref(t)), Collections.singletonList(expr)), tc);
        }

        boolean local = false;
        Type fromType = e.type();
        Type fromBase = X10TypeMixin.baseType(fromType);

        if (fromBase instanceof X10ClassType) {
            X10ClassType ct = (X10ClassType) fromBase;
            if (ct.isAnonymous()) {
                if (ct.interfaces().size() > 0)
                    fromType = ct.interfaces().get(0);
                else
                    fromType = ct.superClass();
            }

            if (ct.isLocal() || ct.isAnonymous()) {
                local = true;
            }
            else if (ct.isGloballyAccessible()) {
                local = false;
            }
            else {
                local = true;
            }
        }
        
        local = true;
        
        assert ts.isValueType(fromType, context) || ts.isParameterType(fromType);

        Name className = Name.makeFresh("Boxed$");
        Name xname = Name.make("v");

        Type superType = ts.boxOf(Types.ref(fromType));

        List<ClassMember> members = new ArrayList<ClassMember>();

        {
            Formal xformal = nf.Formal(position(), nf.FlagsNode(position(), Flags.FINAL), nf.CanonicalTypeNode(position(), fromType), nf.Id(position(), xname));
            Local x = nf.Local(position(), nf.Id(position(), xname));

            X10ConstructorCall superCall = (X10ConstructorCall) nf.X10ConstructorCall(position(), ConstructorCall.SUPER, null, Collections.EMPTY_LIST, Collections.<Expr>singletonList(x));

            Block constructorBody = nf.Block(position(), superCall);
            X10ConstructorDecl constructor = (X10ConstructorDecl) nf.X10ConstructorDecl(position(), nf.FlagsNode(position(), Flags.NONE), nf.Id(position(), className), null, Collections.EMPTY_LIST, Collections.singletonList(xformal), null, Collections.EMPTY_LIST, constructorBody);

            members.add(constructor);
        }

        List<MethodInstance> methods = new ArrayList<MethodInstance>();
        X10ClassType toCT = (X10ClassType) X10TypeMixin.baseType(toType);
        getInheritedVirtualMethods(toCT, methods);

        // Remove abstract or overridden methods.
        for (ListIterator<MethodInstance> i = methods.listIterator(); i.hasNext(); ) {
            MethodInstance mi = i.next();           
            MethodInstance mj = ts.findImplementingMethod(toCT, mi, context);
            if (mj != null && mj.def() != mi.def())
                i.remove();
        }

        for (MethodInstance mi : methods) {
            X10MethodInstance xmi = (X10MethodInstance) mi;

            List<TypeParamNode> params = new ArrayList<TypeParamNode>();
            List<Formal> formals = new ArrayList<Formal>();
            List<TypeNode> typeArgs = new ArrayList<TypeNode>();
            List<TypeNode> throwTypes   = new ArrayList<TypeNode>();
            List<Expr> locals = new ArrayList<Expr>();

            for (Type ti : xmi.throwTypes()) {
                throwTypes.add(nf.CanonicalTypeNode(position(), ti));
            }

            for (Type ti : xmi.typeParameters()) {
                params.add(nf.TypeParamNode(position(), nf.Id(position(), ((ParameterType) ti).name())));
            }

            for (LocalInstance ld : xmi.formalNames()) {
                TypeNode xtn = null;
                if (ld.type() instanceof ParameterType) {
                    xtn = nf.AmbTypeNode(position(), nf.Id(position(), ((ParameterType) ld.type()).name()));
                }
                else {
                    // FIXME: need to subst parameters appearing in the type.
                    xtn = nf.CanonicalTypeNode(position(), ld.type());
                }
                Formal xformal = nf.Formal(position(), nf.FlagsNode(position(), Flags.FINAL), xtn, nf.Id(position(), ld.name()));
                Local x = nf.Local(position(), nf.Id(position(), ld.name()));

                formals.add(xformal);
                locals.add(x);
            }

            Call call = nf.X10Call(position(), nf.Field(position(), nf.Special(position(), Special.THIS), nf.Id(position(), Name.make("value"))),
                                   nf.Id(position(), xmi.name()), typeArgs, locals);

            Stmt eval = null;
            if (xmi.returnType().isVoid()) {
                eval = nf.Eval(position(), call);                
            }
            else {
                eval = nf.Return(position(), call);    
            }
            Block body = nf.Block(position(), eval);
            X10Flags mflags = X10Flags.toX10Flags(xmi.flags());
            mflags = mflags.clearExtern();
            mflags = X10Flags.toX10Flags(mflags.clearNative());
            mflags = X10Flags.toX10Flags(mflags.clearAbstract());
            MethodDecl method = nf.X10MethodDecl(position(), nf.FlagsNode(position(), mflags), nf.CanonicalTypeNode(position(), xmi.returnType()), nf.Id(position(), xmi.name()), params, formals, null, throwTypes, body);

            members.add(method);
        }

        ClassBody classBody = nf.ClassBody(position(), members);

        X10ClassDecl Xdecl = nf.X10ClassDecl(position(), nf.FlagsNode(position(), Flags.NONE), nf.Id(position(), className), Collections.EMPTY_LIST, Collections.EMPTY_LIST, null, nf.CanonicalTypeNode(position(), superType), Collections.<TypeNode>singletonList(nf.CanonicalTypeNode(position(), toType)), classBody);
        
        TypeBuilder tb = new TypeBuilder(tc.job(), ts, nf);
        tb = tb.pushPackage(currPackage);
        tb = tb.pushClass(currClass);
        tb = tb.pushCode(context.currentCode());
        
        TypeCheckPreparer sr = new TypeCheckPreparer(tc.job(), ts, nf, new HashMap<Node, Node>());
        sr = (TypeCheckPreparer) sr.context(context);
        
        TypeChecker tc2;
        tc2 = new TypeChecker(tc.job(), ts, nf, new HashMap<Node, Node>());
        tc2 = (TypeChecker) tc2.context(context);

        if (! local) {
            PackageNode pn = nf.PackageNode(position(), Types.ref(currPackage));
            SourceFile sf = nf.SourceFile(position(), pn, Collections.EMPTY_LIST, Collections.<TopLevelDecl>singletonList(Xdecl));
            Source s = new Source(className.toString() + ".x10", false) { };
            sf = sf.source(s);
            Scheduler scheduler = ts.extensionInfo().scheduler();
            Job job = scheduler.addJob(s, sf);
            scheduler.addDependenciesForJob(job, true);

            TypeNode tn = nf.AmbTypeNode(position(), nf.Id(position(), className));
            New n = nf.New(position(),
                           tn,
                           Collections.<Expr>singletonList(e), null);
            n = (New) this.visitChild(n, tb);
            n = (New) this.visitChild(n, sr);
            n = (New) this.visitChild(n, tc2);
            return n;
        }
        else {

            // Generate as a local or anonymous class.
            Formal xformal = nf.Formal(position(), nf.FlagsNode(position(), Flags.FINAL), nf.CanonicalTypeNode(position(), fromType), nf.Id(position(), xname));

            Local x = nf.Local(position(), nf.Id(position(), xname));

            TypeNode tn = nf.AmbTypeNode(position(), nf.Id(position(), className));
            New n = nf.New(position(),
                           tn,
                           Collections.<Expr>singletonList(x), null);

            Block body = nf.Block(position(), nf.LocalClassDecl(position(), Xdecl), nf.Return(position(), n));

            Closure closure = nf.Closure(position(), Collections.EMPTY_LIST, Collections.singletonList(xformal), null, nf.CanonicalTypeNode(position(), Types.ref(toType)), Collections.EMPTY_LIST, body);
            closure = (Closure) this.visitChild(closure, tb);
            closure = (Closure) this.visitChild(closure, sr);
            closure = (Closure) this.visitChild(closure, tc2);

            return check(nf.ClosureCall(position(), check(closure, tc), Collections.EMPTY_LIST, Collections.singletonList(e)), tc);
        }
    }

    public void getInheritedVirtualMethods(X10ClassType ct, List<MethodInstance> methods) {
        for (MethodInstance mi : ct.methods()) {
            mi.formalTypes();
            if (! mi.flags().isStatic()) 
                methods.add(mi);
        }
        Type sup = ct.superClass();
        if (sup instanceof X10ClassType) {
            getInheritedVirtualMethods((X10ClassType) sup, methods);
        }
        for (Type t : ct.interfaces()) {
            if (t instanceof X10ClassType) {
                getInheritedVirtualMethods((X10ClassType) t, methods);
            }
        }
    }

    @Override
    public Node typeCheck(ContextVisitor tc) throws SemanticException {
        Type toType = castType.type();
        Type fromType = expr.type();
        X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
        X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
        X10Context context = (X10Context) tc.context();
        
        // v to I, where I is not a value interface (i.e., a function type)
        if (ts.isValueType(fromType, context) && ts.isInterfaceType(toType) && ! ts.isValueType(toType, context)) {
            if (ts.isSubtypeWithValueInterfaces(fromType, toType, tc.context())) {
                return this.type(toType);
            }
        }

        throw new SemanticException("Cannot convert expression of type \"" 
                                    + fromType + "\" to type \"" 
                                    + toType + "\".",
                                    position());
    }

}
