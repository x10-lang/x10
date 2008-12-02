/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Call;
import polyglot.ast.Call_c;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.X10ConstructorInstance;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10Flags;
import polyglot.ext.x10.types.X10MethodInstance;
import polyglot.ext.x10.types.X10MethodInstance_c;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.constr.C_Field_c;
import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.BindingConstraintSystem;
import polyglot.types.Context;
import polyglot.types.ErrorRef_c;
import polyglot.types.FieldInstance;
import polyglot.types.MethodInstance;
import polyglot.types.NoMemberException;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.TypeChecker;

/**
 * A method call wrapper to rewrite getLocation() calls on primitives
 * and array operator calls. And perform other dep type processing on some selected method calls.
 * @author Igor
 */
public class X10Call_c extends Call_c {
    public X10Call_c(Position pos, Receiver target, Id name,
                     List arguments) {
        super(pos, target, name, arguments);
    }
    

    /**
     * Rewrite getLocation() to Here for value types and operator calls for
     * array types, otherwise leave alone.
     */
    public Node typeCheck(TypeChecker tc) throws SemanticException {
    	
        X10NodeFactory xnf = (X10NodeFactory) tc.nodeFactory();
        X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
        if (this.target != null && this.target.type().isPrimitive() &&
                name().equals("getLocation") && arguments().isEmpty())
        {
            return xnf.Here(position()).del().typeCheck(tc);
        }
     
        try {
            Context c = tc.context();
            X10Call_c result = (X10Call_c) super.typeCheck(tc);
    		result = (X10Call_c) result.methodInstance((X10MethodInstance) result.methodInstance().copy());
            // If we found a method, the call must type check, so no need to check
            // the arguments here.
            result.checkConsistency(c);
        	result = result.adjustMI(tc);
        	result.checkWhereClause(tc);
        	result.checkAnnotations(tc);
        	Expr r = result.setDeptypeForBuiltInCalls(xts);
        	
        	return r;
        } catch (NoMemberException e) {
            if (e.getKind() != NoMemberException.METHOD || this.target == null)
                throw e;
            Type type = target.type();
            String name = name();
            List arguments = arguments();
            ReferenceType java_io_PrintStream = (ReferenceType) xts.forName("java.io.PrintStream");
            if (xts.isX10Array(type)) {
            // Special methods on arrays
            Type elem = xts.baseType(type);
            //reduce(), scan(), restriction(), union(), overlay(), update(), and lift()
            if ((name.equals("reduce") && arguments.size() == 2 &&
                    xts.isSubtype(((Expr)arguments.get(0)).type(), xts.OperatorBinary()) &&
                    xts.isSubtype(((Expr)arguments.get(1)).type(), elem)) ||
                (name.equals("scan") && arguments.size() == 2 &&
                    xts.isSubtype(((Expr)arguments.get(0)).type(), xts.OperatorBinary()) &&
                    xts.isSubtype(((Expr)arguments.get(1)).type(), elem)) ||
                (name.equals("restriction") && arguments.size() == 1 &&
                    isRestrictionArgType(((Expr)arguments.get(0)).type(), xts)) ||
                (name.equals("union") && arguments.size() == 1 &&
                    xts.isSubtype(type, ((Expr)arguments.get(0)).type())) ||
                (name.equals("overlay") && arguments.size() == 1 &&
                    xts.isSubtype(type, ((Expr)arguments.get(0)).type())) ||
                (name.equals("view")) || /* (VIVEK) Prototype extension to support array views in the future */
                (name.equals("update") && arguments.size() == 1 &&
                    xts.isSubtype(type, ((Expr)arguments.get(0)).type())) ||
                (name.equals("lift") &&
                    ((arguments.size() == 2 &&
                      xts.isSubtype(((Expr)arguments.get(0)).type(), xts.OperatorBinary()) &&
                      xts.isSubtype(type, ((Expr)arguments.get(1)).type())) ||
                     (arguments.size() == 1 &&
                      xts.isSubtype(((Expr)arguments.get(0)).type(), xts.OperatorUnary()))))
               )
            {
                TypeNode t = xnf.CanonicalTypeNode(position(), xts.ArrayOperations());
                List newargs = TypedList.copy(arguments, Expr.class, false);
                newargs.add(0, this.target);
                return ((X10Call_c)this.target(t).arguments(newargs)).superTypeCheck(tc);
            }
            } else
            // FIXME: [IP] HACK: do not typecheck printf beyond the first argument
            if (xts.typeEquals(type, java_io_PrintStream)) {
                if (name.equals("printf") && arguments.size() >= 1 &&
                        xts.isSubtype(((Expr) arguments.get(0)).type(), xts.String()))
                {
                    try {
                        MethodInstance new_mi = xts.findMethod(java_io_PrintStream, "printf",
                                Arrays.asList(new Type[] { xts.String(), xts.arrayOf(xts.Object()) }),
                                tc.context().currentClassScope());
                        return (X10Call_c)this.methodInstance(new_mi).type(new_mi.returnType());
                    } catch (NoMemberException f) {
                        // For Java 1.4, we need to emulate this method
                        // TODO: generate a call to x10.lang.Runtime.printf(PrintStream, String, Object[]) instead
                    }
                }
            }
            throw e;
        } finally {
        
        }
    }

    /**
     * Check if this is a call to built in methods -- methods such as x10.lang.dist.factory.block(region r)
     * which have a dependent return type but are currently implemented as Java hence we do not
     * have the right signature for them. This is stand-in code and should go away when we find 
     * a way to express all our libraries (e.g. x10.lang.dist) in X10 instead of Java.
     * 
     * This method must be called only after base-level type-checking has been performed. Therefore
     * the type of this has already been set, e.g. to dist. This method should simply set
     * the depclause on the result type if there is information in the arguments that requires this,
     * @author vj
     * @param xts
     * @return
     */
    public Expr setDeptypeForBuiltInCalls(X10TypeSystem xts) {
    	Type type = type();
    	FieldInstance dist_factory = null;
    	try {
    		dist_factory = xts.findField(xts.distribution(), "factory");
    	} catch (NoMemberException e) {
    		if (e.getKind() != NoMemberException.FIELD)
    			throw new InternalCompilerError("Something went terribly wrong", e);
    	} catch (SemanticException e) {
			throw new InternalCompilerError("Something went terribly wrong", e);
    	}
    	final int argSize = arguments.size();
    	if (target instanceof Field && xts.equals(((Field) target).fieldInstance(), dist_factory) &&
    			name().equals("block") && argSize <= 1)
    	{
    		// handles the method
    		// dist(:rank==a.rank,isZeroBased=a.isZeroBased,rect==a.rect) block(final region a)
    		// on the class x10.lang.dist.factory. (Actually, it checks that the receiver is 
    		// a field called factory, and the methodname is block and the call has <= 1 args.)
    		X10ParsedClassType rType = ((X10ParsedClassType) type);
    		if (argSize == 0) {
    			rType = rType.setZeroBasedRectRankOne();
    		} else {
    			Type argType = ((Expr) arguments.get(0)).type();
    			assert xts.isRegion(argType);
    			rType = rType.transferRegionProperties((X10ParsedClassType) argType);
    		}
    		return type(rType);
    	}
    	else if (xts.isRegion(target.type()) && name().equals("toDistribution") && argSize == 0) {
    		X10ParsedClassType rType = ((X10ParsedClassType) type);
			rType = rType.transferRegionProperties((X10ParsedClassType) target.type());
//			try {
//				C_Term targetTerm = new TypeTranslator(xts).trans(target);
//				if (targetTerm instanceof C_Var) {
//					FieldInstance fi = xts.findField(xts.distribution(), "region");
//					rType.addBinding(new C_Field_c(fi, C_Special.Self), (C_Var) targetTerm);
//				}
//			}
//			catch (Exception e) {
//				System.out.println(e.getMessage());
//			}
			return type(rType);
    	}
    	else if (xts.isX10Array(target.type()) && name().equals("local") && argSize == 0) {
    		X10ParsedClassType rType = ((X10ParsedClassType) type);
    		rType = rType.setRect();
    		rType = rType.setZeroBased();
    		rType = rType.setRank(xts.ONE());
    		rType = rType.setRail();
    		return type(rType);
    	}
    	return this;
    }

    /**
     * Compute the new resulting type for the method call by replacing this and 
     * any argument variables that occur in the rettype depclause with new
     * variables whose types are determined by the static type of the receiver
     * and the actual arguments to the call.
     * @param tc
     * @return
     * @throws SemanticException
     */
    private X10Call_c adjustMI(TypeChecker tc) throws SemanticException {
    	if (mi == null) return this;
    	X10MethodInstance xmi = (X10MethodInstance) mi;
    	X10Type type = (X10Type) mi.returnType();
    	X10Type retType = X10New_c.instantiateType(xmi, type, target, arguments);
    	if (retType != type) {
    		xmi = (X10MethodInstance) xmi.returnType(retType);
    	}
    	if (xmi.whereClause() != null) {
    		Constraint where = X10New_c.instantiateConstraint(xmi, xmi.whereClause(), target, arguments);
    		xmi = (X10MethodInstance) xmi.whereClause(where);
    	}
    	return (X10Call_c) this.type(retType);
    }
    private void checkWhereClause(TypeChecker tc) throws SemanticException {
    	X10Context c = (X10Context) tc.context();
    	X10MethodInstance mi = (X10MethodInstance) methodInstance();
    	if (mi !=null) {
    		Constraint where = mi.whereClause();
    		if (where != null && ! where.consistent()) {
    			throw new SemanticException(mi + ": Method's dependent clause not satisfied by caller.", position());
    		}
    	}
    }
    private void checkAnnotations(TypeChecker tc) throws SemanticException {
    	X10Context c = (X10Context) tc.context();
    	X10MethodInstance mi = (X10MethodInstance) methodInstance();
    	if (mi !=null) {
    		X10Flags flags = X10Flags.toX10Flags(mi.flags());
    		if (c.inNonBlockingCode() 
    				&& ! (mi.isJavaMethod() || mi.isSafe() || flags.isNonBlocking()))
    			throw new SemanticException(mi + ": Only nonblocking methods can be called from nonblocking code.", 
    					position());
    		if (c.inSequentialCode() 
    				&& ! (mi.isJavaMethod()|| mi.isSafe() || flags.isSequential()))
    			throw new SemanticException(mi + ": Only sequential methods can be called from sequential code.", 
    					position());
    		if (c.inLocalCode() 
    				&& ! (mi.isJavaMethod() || mi.isSafe() || flags.isLocal()))
    			throw new SemanticException(mi + ": Only local methods can be called from local code.", 
    					position());
    	}
    }
	private Node superTypeCheck(TypeChecker tc) throws SemanticException {
        return super.typeCheck(tc);
    }

	private boolean isRestrictionArgType(Type type, X10TypeSystem xts) {
		return xts.isPlace(type) || xts.isDistribution(type) || xts.isRegion(type);
	}
}

