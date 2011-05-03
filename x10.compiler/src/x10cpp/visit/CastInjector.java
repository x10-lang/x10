package x10cpp.visit;

import static x10cpp.visit.Emitter.mangled_field_name;
import static x10cpp.visit.SharedVarsMethods.chevrons;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Assign_c;
import polyglot.ast.Call_c;
import polyglot.ast.Conditional_c;
import polyglot.ast.ConstructorCall_c;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl_c;
import polyglot.ast.LocalDecl_c;
import polyglot.ast.New_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.NullLit_c;
import polyglot.ast.Return_c;
import polyglot.frontend.Job;
import polyglot.types.Context;
import polyglot.types.FunctionDef;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.AssignPropertyCall_c;
import x10.ast.ClosureCall_c;
import x10.ast.Closure_c;
import x10.ast.SettableAssign;
import x10.ast.Tuple_c;
import x10.types.MethodInstance;
import x10.types.X10FieldInstance;
import x10.types.checker.Converter;

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
    
    public CastInjector(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
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
            List<Expr> args = call.arguments();
            List<Type> formals = call.methodInstance().formalTypes();
            List<Expr> newArgs = castActualsToFormals(args, formals);
            return null == newArgs ? call : call.arguments(newArgs);            
        } else if (n instanceof New_c) {
            New_c asNew = (New_c)n;
            List<Expr> args = asNew.arguments();
            List<Type> formals = asNew.constructorInstance().formalTypes();
            List<Expr> newArgs = castActualsToFormals(args, formals);
            return null == newArgs ? asNew : asNew.arguments(newArgs);
        } else if (n instanceof Conditional_c) {
            Conditional_c cond = (Conditional_c)n;
            Expr cons = cond.consequent();
            Expr newCons = cast(cons, cond.type());
            Expr alt = cond.alternative();
            Expr newAlt = cast(alt, cond.type());
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
        }
        
        return n;
    }
    
    /**
     * Inject any needed casts to ensure that the actual and formal types
     * match at a call site.  An exact deep base type equality is needed
     * to ensure that overloading at the C++ level works as expected.
     */
    private List<Expr> castActualsToFormals(List<Expr> args, List<Type> formals) {
        List<Expr> newArgs = null;
        for (int i=0; i<args.size(); i++) {
            Expr e = args.get(i);
            Type fType = formals.get(i);
            Expr e2 = null;
            
            if (ts.isFunctionType(fType)) {
                e2 = castToFunctionType(e, fType); 
            } else if (!ts.typeDeepBaseEquals(fType, e.type(), context)) {
                Position pos = e.position();
                e2 =  nf.X10Cast(pos, nf.CanonicalTypeNode(pos, fType), e,
                                 Converter.ConversionType.UNCHECKED).type(fType);
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
    private Expr cast(Expr a, Type fType) {
        if (ts.isFunctionType(fType)) {
            return castToFunctionType(a, fType);
        } else if (!ts.typeDeepBaseEquals(fType, a.type(), context)) {
            Position pos = a.position();
            return nf.X10Cast(pos, nf.CanonicalTypeNode(pos, fType), a,
                              Converter.ConversionType.UNCHECKED).type(fType);
        } else {
            return a;
        }
    }
    
    /**
     * Check to see if the upcast of the argument expression to the
     * given type requires an explicit cast operation to perform
     * a C++-level representation change (eg boxing). 
     * If the cast is needed, insert it.
     * If the cast is not needed (eg if both types are x10aux:ref<...>)
     * then do nothing, even if the types are not equal.
     */
    private Expr boxingUpcastIfNeeded(Expr a, Type fType) {
        if (a instanceof NullLit_c) {
            // X10_NULL is x10aux:ref<>; implicit C++ level ref casts are enough
            return a;
        }

        if (ts.isFunctionType(fType)) {
            return castToFunctionType(a, fType);
        }
                
        if (ts.isObjectOrInterfaceType(a.type(), context)) {
            // already a x10aux::ref; implicit C++ level ref casts are enoughT
            return a;
        }
        
        if (!ts.typeDeepBaseEquals(fType, a.type(), context)) {
            Position pos = a.position();
            return nf.X10Cast(pos, nf.CanonicalTypeNode(pos, fType), a,
                              Converter.ConversionType.UNCHECKED).type(fType);
        } else {
            return a;
        }
    }
    
    private Expr castToFunctionType(Expr e, Type type) {
        // TODO: this is where the fix for XTENLANG-920 would live.
        //       if there isn't a base-type-equals of all the arugments & the return value,
        //       between the type of e and the type of type, then generate a 
        //       wrapper closure here that contains all the necessary casts in its body
        //       in addition to the apply of the orginal function type
        if (e instanceof Closure_c) {
            // Allocation of Closure_c already upcasts them to the appropriate function type.
            return e;
        }
        
        Position pos = e.position();
        return nf.X10Cast(pos, nf.CanonicalTypeNode(pos, type), e, 
                          Converter.ConversionType.UNCHECKED).type(type);
     }
        
}
