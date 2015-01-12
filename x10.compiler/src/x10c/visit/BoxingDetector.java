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
package x10c.visit;

import java.util.Stack;

import polyglot.ast.Call;
import polyglot.ast.Cast;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Return;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import x10.ast.ClosureCall;
import x10.ast.Closure_c;
import x10.ast.ParExpr;
import x10.ast.X10Call;
import x10.ast.X10MethodDecl;
import x10.ast.X10New;
import x10.types.MethodInstance;
import x10.types.checker.Converter;
import x10.visit.X10PrettyPrinterVisitor;
import x10c.ast.X10CNodeFactory_c;
import x10c.types.X10CTypeSystem_c;

/**
 * This pass adds boxing AST nodes to mark necessary boxing
 * conversion in places, where it is not easy to make a judgement
 * about boxing directly in codegen, for example, in field assignments,
 * where the codegen cannot know, whether the assignment expression
 * value is used or not.
 */
public class BoxingDetector extends NodeVisitor {
    
    private final X10CTypeSystem_c xts;
    private final X10CNodeFactory_c xnf;
    private final Stack<Type> returnType = new Stack<Type>();
    
    public BoxingDetector( TypeSystem ts, NodeFactory nf) {
        xts = (X10CTypeSystem_c) ts;
        xnf = (X10CNodeFactory_c) nf;
    }
    
    @Override
    public NodeVisitor enter(Node parent, Node n) {
        // keep track of the return type of the enclosing method
        if (n instanceof X10MethodDecl) {
            X10MethodDecl decl = (X10MethodDecl)n;
            returnType.push(decl.returnType().type());
        }
        return this;
    }
    
    @Override
    public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
        // do context bookkeeping first
        if (old instanceof X10MethodDecl) {
            assert ((X10MethodDecl)old).returnType().type() == returnType.peek();
            returnType.pop();
        }
        
        // NB: for calls and closure calls, the rules for boxing are complicated,
        // so we insert a simple Cast AST node, so that codegen makes a decision
        // whether to do boxing/unboxing. The goal is to ensure, that
        // the only places, where codegen needs to consider boxing/unboxing
        // are:
        // * casts
        // * method or closure call arguments
        
        // Generic methods (return type T) can return boxed values (e.g., x10.core.UInt)
        // but in the type instantiated [UInt] subclass it is treated as unboxed int.
        // We need to insert explicit cast to
        // an unboxed type,  so that proper conversion method call is inserted later.
        if (n instanceof X10Call && !(parent instanceof Eval)) {
            X10Call call = (X10Call)n;
            Receiver target = call.target();
            MethodInstance mi = call.methodInstance();
            Type expectedReturnType = call.type();
            // do not insert cast the expected type is already boxed or is void
            if (X10PrettyPrinterVisitor.isBoxedType(expectedReturnType) || expectedReturnType.isVoid()      // (void) never needs boxing
                    // or if the method def type is void -- it is a synthetic method without a proper definition
                    || mi.def().returnType().get().isVoid()) return n;
            if (X10PrettyPrinterVisitor.isBoxedType(mi.def().returnType().get())) {
                // only insert cast if the actual returned type is boxed
                return cast(call, expectedReturnType);
            }
        }
        
        // Closures may be implemented by methods returning boxed or unboxed values,
        // depending on some involved condition that is checked in codegen.
        // So add the dummy type cast to give the codegen
        // chance to handle unboxing in a single place
        if (n instanceof ClosureCall && !(parent instanceof Eval) && !(parent instanceof Cast)) {
            ClosureCall call = (ClosureCall)n;
            // if the return type is not primitive, then unboxing will not be needed
            if (X10PrettyPrinterVisitor.isBoxedType(call.type()) || call.type().isVoid()) return n;
            return cast(call, call.type());
        }
        
        if (n instanceof Expr) {    // boxing may be needed only for expressions
            Expr expr = (Expr)n;
            
            // parent node still has "old" as its child
            if (isBoxed(expr) && expectsUnboxed(parent, old)) {
                return unbox(expr);
            } else if (isUnboxed(expr) && expectsBoxed(parent, old)) {
                return box(expr);
            }
        }
        return n;
    }

    
    private boolean isUnboxed(Expr n) {
        return !isBoxed(n);
    }
    
    protected boolean isBoxed(Expr n) {
        if (n instanceof Field) {
            return isBoxed((Field)n);
        }
        else if (n instanceof X10Call) {
            return isBoxed((X10Call)n);
        }
        else if (n instanceof ClosureCall) {
            return isBoxed((ClosureCall)n);
        }
        else if (n instanceof ParExpr) {
            return isBoxed(((ParExpr)n).expr());
        }
        return isBoxedType(((Expr) n).type());
    }
    
    private boolean isBoxed(X10Call call) {
        Type type = call.methodInstance().def().returnType().get();
        // FIXME: inline & dispatch method conditions
        if (isBoxedType(type))
            return true;
        return false;
    }
    
    private boolean isBoxed(ClosureCall call) {
        // FIXME: these conditions are copied over from Emitter.printApplyMethodName() and X10PrettyPrinterVisitor.visit(ClosureCall)
        Expr target = call.target();
        if (target instanceof ParExpr) { target = ((ParExpr) target).expr(); }
        boolean newClosure = target instanceof Closure_c;
        MethodInstance mi = call.closureInstance();
        if ((!newClosure && !mi.returnType().isVoid() && mi.formalTypes().size() == 0)
                || !(mi.returnType().isVoid() || (newClosure && !mi.returnType().isParameterType()))) {
            // in this case generic apply$G is used
            return true;
        }
        Type type = call.closureInstance().def().returnType().get();
        if (isBoxedType(type))
            return true;
        return false;
    }
    
    private boolean isBoxed(Field field) {
        if (isBoxedType(field.fieldInstance().def().type().get()))
            return true;
        return false;
    }
    
    private boolean expectsUnboxed(Node parent, Node n) {
        // the majority of boxing decisions are made in codegen directly
        /*
        if (n instanceof Call && child instanceof Expr) {
            return !expectsBoxed((Call) n, child);
        }
        else */
        if (parent instanceof FieldAssign) {
            // check type for the assignment rhs
            if (n == ((FieldAssign)parent).right()) return !expectsBoxed((FieldAssign) parent);
            // and return false for target object
            return false;
        }
        else if (parent instanceof LocalAssign) {
            if (n == ((LocalAssign)parent).right()) return !expectsBoxed((LocalAssign) parent);
            return false;
        }
        else if (parent instanceof LocalDecl) {
            if (n == ((LocalDecl)parent).init()) return !expectsBoxed((LocalDecl) parent);
            return false;
        }
        else if (parent instanceof Eval) {
            return false;
        }
        else if (parent instanceof Return) {
            return !isBoxedType(returnType.peek());
        }
        else if (n instanceof Field) {
            return !isBoxedType(((Field) n).type());
        }
        /*
        // assume default rule: expect primitive types as unboxed, leave everything else as is
        else if (n instanceof Expr) {
            return !isBoxedType(((Expr) n).type());
        }
        */
        return false;
    }
    
    private boolean expectsBoxed(Node parent, Node n) {
        // the majority of boxing is currently added by codegen
        /*
        if (n instanceof X10Call && child instanceof Expr) {
            return expectsBoxed((X10Call) n, child);
        }
        else if (n instanceof X10New && child instanceof Expr) {
            return expectsBoxed((X10New) n, child);
        }
        else if (n instanceof LocalDecl) {
            return expectsBoxed((LocalDecl) n);
        }
        else */
        if (parent instanceof FieldAssign) {
            if (n == ((FieldAssign)parent).right()) return expectsBoxed((FieldAssign) parent);
            return false;
        }
        else if (parent instanceof LocalAssign) {
            if (parent == ((LocalAssign)parent).right()) return expectsBoxed((LocalAssign) parent);
            return false;
        }
        else if (parent instanceof LocalDecl) {
            if (n == ((LocalDecl)parent).init()) return expectsBoxed((LocalDecl) parent);
            return false;
        }
        else if (parent instanceof Return) {
            return isBoxedType(returnType.peek());
        }
        return false;
    }
    
    private boolean expectsBoxed(Call call, Node arg) {
        // implicit this argument also is present
        if (call.target() == arg)
            return isBoxedType(call.target().type());   // targets of primitive types are unboxed
        int i = call.arguments().indexOf(arg);
        if (i < 0) throw new InternalCompilerError("BoxingPropagator: cannot find argument in call");
        Type type = call.methodInstance().def().formalTypes().get(i).get();
        return isBoxedType(type);
    }
    
    private boolean expectsBoxed(X10New call, Node arg) {
        int i = call.arguments().indexOf(arg);
        if (i < 0) throw new InternalCompilerError("BoxingPropagator: cannot find argument in new()");
        if (i >= call.constructorInstance().def().formalTypes().size()) {
            // a case when constructor's def() does not match the contructor call
            // fall back to default rule -- a bug in earlier AST passes?
            return isBoxedType(call.arguments().get(i).type());
        }
        Type type = call.constructorInstance().def().formalTypes().get(i).get();
        return isBoxedType(type);
    }

    private boolean expectsBoxed(FieldAssign assign) {
        return isBoxedType(assign.fieldInstance().def().type().get());
    }
    
    private boolean expectsBoxed(LocalAssign assign) {
        return isBoxedType(assign.type());
    }
    
    private boolean expectsBoxed(LocalDecl decl) {
        return isBoxedType(decl.declType());
    }
    
    private Node box(Expr expr) {
        Position pos = Position.compilerGenerated(null);
        return xnf.X10Cast(pos, xnf.CanonicalTypeNode(pos, expr.type()), expr, Converter.ConversionType.BOXING).type(expr.type());
    }
    
    private Node unbox(Expr expr) {
        Position pos = Position.compilerGenerated(null);
        return xnf.X10Cast(pos, xnf.CanonicalTypeNode(pos, expr.type()), expr, Converter.ConversionType.UNBOXING).type(expr.type());
    }
    
    private Node cast(Expr expr, Type type) {
        Position pos = Position.COMPILER_GENERATED;
        return xnf.X10Cast(pos, xnf.CanonicalTypeNode(pos, type), expr, Converter.ConversionType.PRIMITIVE).type(type);
    }
    
    private boolean isBoxedType(Type type) {
        if (type == null) return false;
        return X10PrettyPrinterVisitor.isBoxedType(type);
    }
}
