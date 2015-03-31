/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10cpp.visit;

import static x10cpp.visit.Emitter.mangled_field_name;
import static x10cpp.visit.SharedVarsMethods.chevrons;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Assign_c;
import polyglot.ast.Block;
import polyglot.ast.BooleanLit_c;
import polyglot.ast.Call_c;
import polyglot.ast.CharLit_c;
import polyglot.ast.Conditional_c;
import polyglot.ast.ConstructorCall_c;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl_c;
import polyglot.ast.FloatLit_c;
import polyglot.ast.Formal;
import polyglot.ast.IntLit_c;
import polyglot.ast.Lit_c;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl_c;
import polyglot.ast.New_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.NullLit;
import polyglot.ast.NullLit_c;
import polyglot.ast.Return_c;
import polyglot.frontend.Job;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.FunctionDef;
import polyglot.types.Name;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.AssignPropertyCall_c;
import x10.ast.Closure;
import x10.ast.ClosureCall_c;
import x10.ast.Closure_c;
import x10.ast.SettableAssign;
import x10.ast.Tuple_c;
import x10.types.FunctionType;
import x10.types.FunctionType_c;
import x10.types.MethodInstance;
import x10.types.X10FieldInstance;
import x10.types.checker.Converter;
import x10.util.AltSynthesizer;
import x10.util.Synthesizer;
import x10.visit.ClosureCaptureVisitor;

/**
 * A pass to run right before final C++ codegeneration
 * to insert upcasts that are required by the C++ backend,
 * but are not explicitly injected by the front-end.
 * 
 * We assume that the program is statically type correct, 
 * therefore all of these casts can be unchecked.
 * They fall into two main catagories:
 *   (a) casts injected for actual parameters of a call
 *       so that the static types match the callee method.
 *       These are required to make overloading work.
 *   (b) casts injected on other assignment-like operations
 *       whose purpose is to ensure that representation level
 *       boxing/unboxing operations are performed.
 *       
 *  TODO:  There's a static cast for the return type of Call_c in MPGC.
 *         I think that should no longer be needed, since we are injecting casts
 *         thoroughly here.  Verify this and remove cast in Call_c if true.
 */
public class CastInjector extends ContextVisitor {
    final Synthesizer synth;
    
    public CastInjector(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        synth = new Synthesizer(nf, ts);
    }
    
    public Node leaveCall(Node old, Node n, NodeVisitor v) {
        if (n instanceof Assign_c) {
            Assign_c assign = (Assign_c)n;
            Expr rhs = assign.right();
            Expr newRhs = boxingUpcastIfNeeded(assign.right(), assign.left().type());
            return rhs == newRhs ? assign : assign.right(newRhs);
        } else if (n instanceof ConstructorCall_c) {
            ConstructorCall_c call = (ConstructorCall_c)n;
            List<Expr> args = call.arguments();
            List<Type> formals = call.constructorInstance().formalTypes();
            List<Expr> newArgs = castActualsToFormals(args, formals);
            return null == newArgs ? call : call.arguments(newArgs);
        } else if (n instanceof FieldDecl_c) {
            FieldDecl_c fd = (FieldDecl_c)n;
            Expr init = fd.init();
            if (init == null) return fd;
            Expr newInit = boxingUpcastIfNeeded(init, fd.type().type());
            return init == newInit ? fd : fd.init(newInit);
        } else if (n instanceof AssignPropertyCall_c) {
            AssignPropertyCall_c call = (AssignPropertyCall_c)n;
            List<Expr> args = call.arguments();
            List<X10FieldInstance> props = call.properties();
            List<Type> ftypes = new ArrayList<Type>(props.size());
            for (X10FieldInstance fi: props) {
                ftypes.add(fi.type());
            }
            List<Expr> newArgs = castActualsToFormals(args, ftypes);
            return null == newArgs ? call : call.arguments(newArgs);
        } else if (n instanceof Return_c) {
            Return_c ret = (Return_c)n;
            Expr rhs = ret.expr();
            if (rhs == null) return ret;
            FunctionDef container = (FunctionDef) context.currentCode();
            Type rType = container.returnType().get();
            Expr newRhs = boxingUpcastIfNeeded(rhs, rType);
            return rhs == newRhs ? ret : ret.expr(newRhs);
        } else if (n instanceof LocalDecl_c) {
            LocalDecl_c ld = (LocalDecl_c)n;
            Expr init = ld.init();
            if (null == init) return ld;
            Expr newInit = boxingUpcastIfNeeded(init, ld.type().type());
            return newInit == init ? ld : ld.init(newInit);
        } else if (n instanceof Call_c) {
            Call_c call = (Call_c)n;
            MethodInstance mi = call.methodInstance();
            if (ts.isHandOptimizedInterface(mi.container())) {
                return call;
            } else {               
                List<Expr> args = call.arguments();
                List<Type> formals = call.methodInstance().formalTypes();
                List<Expr> newArgs = castActualsToFormals(args, formals);
                if (!mi.flags().isStatic() && ts.isParameterType(call.target().type())
                        && !(mi.container().isClass() && mi.container().toClass().flags().isInterface())) {
                    call = (Call_c)call.target(makeCast(call.target().position(), (Expr)call.target(), mi.container()));
                }
                return null == newArgs ? call : call.arguments(newArgs);
            }
        } else if (n instanceof New_c) {
            New_c asNew = (New_c)n;
            List<Expr> args = asNew.arguments();
            List<Type> formals = asNew.constructorInstance().formalTypes();
            List<Expr> newArgs = castActualsToFormals(args, formals);
            return null == newArgs ? asNew : asNew.arguments(newArgs);
        } else if (n instanceof Conditional_c) {
            Conditional_c cond = (Conditional_c)n;
            Expr cons = cond.consequent();
            Expr newCons = upcast(cons, cond.type());
            Expr alt = cond.alternative();
            Expr newAlt = upcast(alt, cond.type());
            return cons == newCons && alt == newAlt ? cond : cond.consequent(newCons).alternative(newAlt);
        } else if (n instanceof ClosureCall_c) {
            ClosureCall_c call = (ClosureCall_c)n;
            List<Expr> args = call.arguments();
            List<Type> formals = call.closureInstance().formalTypes();
            List<Expr> newArgs = castActualsToFormals(args, formals);
            return null == newArgs ? call : call.arguments(newArgs);            
        } else if (n instanceof Tuple_c) {
            Tuple_c tuple = (Tuple_c)n;
            List<Expr> inits = tuple.arguments();
            List<Expr> newInits = null;
            Type T = Types.getParameterType(tuple.type(), 0);       
            for (int i=0; i<inits.size(); i++) {
                Expr e = inits.get(i);
                Expr e2 = boxingUpcastIfNeeded(e, T);
                if (e != e2) {
                    if (newInits == null) {
                        newInits = new ArrayList<Expr>(inits);
                    }
                    newInits.set(i, e2);
                }
            }
            return null == newInits ? tuple : tuple.arguments(newInits);
        } else if (n instanceof BooleanLit_c || n instanceof IntLit_c || n instanceof FloatLit_c || n instanceof CharLit_c) {
            Lit_c lit = (Lit_c) n;
            if (Types.baseType(lit.type()).isAny()) {
                return makeCast(n.position(), lit.type(lit.constantValue().getLitType(ts)), lit.type());
            }
        }
        
        return n;
    }
    
    /**
     * Inject any needed upcasts to ensure that the actual and formal types
     * match at a call site.  An exact deep base type equality is needed
     * to ensure that overloading at the C++ level works as expected.
     */
    private List<Expr> castActualsToFormals(List<Expr> args, List<Type> formals) {
        List<Expr> newArgs = null;
        for (int i=0; i<args.size(); i++) {
            Expr e = args.get(i);
            Type fType = formals.get(i);
            Expr e2 = null;
            
            if (Types.baseType(fType) instanceof FunctionType) {
                e2 = upcastToFunctionType(e, (FunctionType)Types.baseType(fType)); 
            } else if (!ts.typeDeepBaseEquals(fType, e.type(), context)) {
                e2 =  makeCast(e.position(), e, fType);
            }
            
            if (e2 != null) {
                if (newArgs == null) {
                    newArgs = new ArrayList<Expr>(args);
                }
                newArgs.set(i, e2);
            }
            
        }
        return newArgs == null ? args : newArgs;
    }
    
    /**
     * Like castActualsToFormals, but for a single Expr.
     * Used in a few additional cases where an exact deep base type equality
     * is required for C++ level compilation to work as expected.
     */
    private Expr upcast(Expr a, Type fType) {
        if (Types.baseType(fType) instanceof FunctionType) {
            return upcastToFunctionType(a, (FunctionType)Types.baseType(fType));
        } else if (!ts.typeDeepBaseEquals(fType, a.type(), context)) {
            Position pos = a.position();
            return makeCast(a.position(), a, fType);
        } else {
            return a;
        }
    }
    
    /**
     * Check to see if the upcast of the argument expression to the
     * given type requires an explicit cast operation to perform
     * a C++-level representation change (eg boxing). 
     * If the cast is needed, insert it.
     * If the cast is not needed (eg if both types are classes and is is an upcast)
     * then do nothing, even if the types are not equal.
     */
    private Expr boxingUpcastIfNeeded(Expr a, Type fType) {
        if (a instanceof NullLit_c) {
            return makeCast(a.position(), a, fType);
        }

        if (Types.baseType(fType) instanceof FunctionType) {
            return upcastToFunctionType(a, (FunctionType)Types.baseType(fType));
        }
                
        if (ts.isObjectType(a.type(), context) && ts.isObjectType(fType, context)) {
            // implicit C++ level upcast is good enough (C++ and X10 have same class hierarchy structure)
            return a;
        }
        
        if (!ts.typeDeepBaseEquals(fType, a.type(), context)) {
            Position pos = a.position();
            return makeCast(a.position(), a, fType);
        } else {
            return a;
        }
    }
    
    private Expr upcastToFunctionType(Expr e, FunctionType castFType) {
        boolean exactMatch = false;

        if (e instanceof NullLit) {
            // can force exactMatch to be true because a NPE will be raised at runtime
            // if the function is actually applied. Therefore we don't need to worry about
            // a mismatch between expected and actual argument types.
            exactMatch = true;
        } else {
            List<FunctionType> cands = Types.functionTypes(e.type());
            for (FunctionType ft : cands) {
                if (ft.argumentTypes().size() == castFType.argumentTypes().size()) {
                    exactMatch = ts.typeDeepBaseEquals(ft.returnType(), castFType.returnType(), context);
                    if (exactMatch) {
                        for (int i=0; i<ft.argumentTypes().size(); i++) {
                            Type ea = ft.argumentTypes().get(i);
                            Type ca = castFType.argumentTypes().get(i);
                            if (!ts.typeDeepBaseEquals(ea, ca, context)) {
                                exactMatch = false;
                                break;
                            }
                        }
                    }
                    if (exactMatch) break;
                }
            }
        }

        if (exactMatch) {
            if (e instanceof Closure_c) {
                // C++ code generated for the allocation of a closure literal 
                // already does an upcast to the appropriate function type        
                return e;
            }
            if (e.type() instanceof FunctionType) {
                // Already at the right C++ level interface type.
                return e;
            }

            Position pos = e.position();
            return makeCast(e.position(), e, castFType);
        } else {
            List<FunctionType> cands = new ArrayList<FunctionType>();
            for (FunctionType ft : Types.functionTypes(e.type())) {
                if (ft.argumentTypes().size() == castFType.argumentTypes().size()) {
                    cands.add(ft);
                }
            }
            if (cands.size() != 1) {
                throw new InternalCompilerError("Unable to find unique starting function type for upcast of "+e.type()+"to "+castFType);
            }
            FunctionType exprFType = cands.get(0);
            Position pos = e.position();
            List<Formal> formals = new ArrayList<Formal>();
            for (Type ft : castFType.argumentTypes()) {
                Formal f = synth.createFormal(pos, ft, Name.makeFresh("arg"), Flags.FINAL);
                formals.add(f);
            }
            List<Expr> actuals = new ArrayList<Expr>();
            for (int i=0; i<formals.size(); i++) {
                Formal f = formals.get(i);
                Type inner = exprFType.argumentTypes().get(i);
                Local ref = synth.createLocal(pos, f.localDef().asInstance());
                if (ts.typeDeepBaseEquals(ref.type(), inner, context)) {
                    actuals.add(ref);
                } else {
                    actuals.add(makeCast(pos, ref, inner));
                }
            }
            Expr call = nf.ClosureCall(pos, e, actuals).closureInstance(exprFType.applyMethod()).type(exprFType.returnType());
            if (!ts.typeDeepBaseEquals(call.type(), castFType.returnType(), context)) {
                call = makeCast(pos, call, castFType.returnType());
            }
            Block body = nf.Block(pos, castFType.returnType().isVoid() ? nf.Eval(pos, call) : nf.Return(pos, call));
            Closure c = synth.makeClosure(pos, castFType.returnType(), formals, body, context);
            c.visit(new ClosureCaptureVisitor(this.context(), c.closureDef()));
            return c;
        }
     }
    
    private Expr makeCast(Position pos, Expr e, Type t) {
        return nf.X10Cast(pos, nf.CanonicalTypeNode(pos, t), e, Converter.ConversionType.UNCHECKED).type(t);
    }
        
}
