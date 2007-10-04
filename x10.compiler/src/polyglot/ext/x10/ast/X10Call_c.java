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
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10Flags;
import polyglot.ext.x10.types.X10MethodInstance;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.Context;
import polyglot.types.NoMemberException;
import polyglot.types.SemanticException;
import polyglot.types.Type;
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
            // If we found a method, the call must type check, so no need to check
            // the arguments here.
            result.checkConsistency(c);
        	if (! result.target().type().isCanonical()) {
        		return result;
        	}
        	result = result.adjustMI(tc);
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
            if (xts.equals(type, java_io_PrintStream)) {
                if (name.equals("printf") && arguments.size() >= 1 &&
                        xts.isSubtype(((Expr) arguments.get(0)).type(), xts.String()))
                {
                    try {
                        MethodInstance new_mi = xts.findMethod(java_io_PrintStream, "printf",
                                Arrays.asList(new Type[] { xts.String(), xts.arrayOf(xts.Object()) }),
                                tc.context().currentClass());
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
    	final int argSize = arguments.size();
    	if (name().equals("block") && (argSize <= 1) && (target instanceof Field)) {
    			// handles the method
    		    // dist(:rank==a.rank,isZeroBased=a.isZeroBased,rect==a.rect) block(final region a)
    		    // on the class x10.lang.dist.factory. (Actually, it checks that the receiver is 
    		    // a field called factory, and the methodname is block and the call has <= 1 args.)
    		String name = ((Field) target).name();
    		if (name.equals("factory")) {
    			Type type = type();
				X10ParsedClassType rType = ((X10ParsedClassType) type).makeVariant();
    			if (argSize == 0) {
    				rType.setZeroBasedRectRankOne();
    			} else{
    				Type argType =  ((Expr) arguments.get(0)).type();
    				assert xts.isRegion(argType);
    				rType.transferRegionProperties((X10ParsedClassType) argType);
    			}
    			return type(rType);
    		}
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
    	
    	X10MethodInstance xmi = (X10MethodInstance) mi;
    	if (mi == null) return this;
    	X10Type type = (X10Type) mi.returnType();
    	X10Type retType = X10New_c.instantiateType(type, target, arguments);
    	if (retType != type) {
    		mi.setReturnType(retType);
    	}
    	return (X10Call_c) this.type(retType);
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

