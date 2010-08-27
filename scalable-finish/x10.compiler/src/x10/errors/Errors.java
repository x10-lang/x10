/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.errors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.ast.Binary;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.Formal;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.FieldInstance;
import polyglot.types.MethodInstance;
import polyglot.types.ProcedureDef;
import polyglot.types.ProcedureInstance;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.types.VarInstance;
import polyglot.types.TypeSystem_c.ConstructorMatcher;
import polyglot.types.TypeSystem_c.MethodMatcher;
import polyglot.util.CodedErrorInfo;
import polyglot.util.ErrorInfo;
import polyglot.util.Position;
import x10.ExtensionInfo;
import x10.ast.DepParameterExpr;
import x10.ast.SemanticError;
import x10.ast.X10ClassDecl;
import x10.ast.X10FieldDecl;
import x10.constraint.XTerm;
import x10.types.MacroType;
import x10.types.ParameterType;
import x10.types.X10ClassDef;
import x10.types.X10FieldInstance;
import x10.types.X10ProcedureInstance;
import x10.types.checker.Converter;
import x10.types.checker.Converter.ConversionType;
import x10.types.constraints.CConstraint;

/**
 * Start at centralizing Error messages. Goal is to support standardization of error messages for
 * internationalization, to make unit tests more accurate, and to support better error-handling
 * inside compiler.
 *
 * @author vj 2010/02/06
 *
 */
public class Errors {

	public static void issue(Job job, SemanticException e) {
		issue(job, e, null);
	}
	public static void issue(Job job, SemanticException e, Node n) {
		ExtensionInfo ei = (ExtensionInfo) job.extensionInfo();
		if (e.getCause() == null && e.position() == null && n != null)
			e = new SemanticException(e.getMessage(), n.position());
		boolean newP = ei.errorSet().add(e);
		if (newP && e.getMessage() != null) {

			Position position = e.position();

			if (position == null && n != null) {
				position = n.position();
			}
			
			ErrorInfo errorInfo = new CodedErrorInfo(ErrorInfo.SEMANTIC_ERROR,
			 					e.getMessage(), position, e.attributes());
			job.compiler().errorQueue().enqueue(errorInfo);
		}
	}

	public static interface DepTypeException {}
	public static interface ProtoTypeException {}
	public static interface ConversionException {}

	public static class CannotAssign extends SemanticException {
		private static final long serialVersionUID = -4243637083971033996L;
		public CannotAssign(Expr expr, Type targetType, Position pos) {
			super("Cannot assign expression to target."
					+ "\n\t Expression: " + expr
					+ "\n\t Type: " + expr.type()
					+ "\n\t Expected type: " + targetType, pos);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof CannotAssign) )
				return false;
			return((CannotAssign)o).position().equals(position());
		}
	}
	public static class FieldInitTypeWrong extends SemanticException {
		private static final long serialVersionUID = 4778277210134359519L;

		public FieldInitTypeWrong(Expr expr, Type targetType, Position pos) {
			super("The type of the field initializer is not a subtype of the field type."
					+ "\n\t Expression: " + expr
					+ "\n\t Type: " + expr.type()
					+ "\n\t Expected type: " + targetType, pos);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof FieldInitTypeWrong) )
				return false;
			return((FieldInitTypeWrong)o).position().equals(position());
		}
	}
	public static class IncompatibleReturnType extends SemanticException {
		private static final long serialVersionUID = -6220163900080278288L;

		public IncompatibleReturnType(MethodInstance mi, MethodInstance mj) {
			super("Attempting to use incompatible return type."
					+ "\n\t Method: " + mi
					+ "\n\t Expected Type: " + mj.returnType()
					+ "\n\t Found Type: " + mi.returnType(), mi.position());
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof IncompatibleReturnType) )
				return false;
			return((IncompatibleReturnType)o).position().equals(position());
		}
	}

	public static class InvalidParameter extends SemanticException {
		private static final long serialVersionUID = -1351185257724314440L;
		public InvalidParameter(Type from, Type to, Position pos) {
			super("Invalid Parameter.\n\t expected type: " + to + "\n\t found: " + from, pos);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof InvalidParameter) )
				return false;
			return((InvalidParameter)o).position().equals(position());
		}
	}

	public static class NoAssignmentInDepType extends SemanticException implements DepTypeException {
		private static final long serialVersionUID = 8343234065357158485L;
		public NoAssignmentInDepType(FieldAssign f, Position pos) {
			super("Assignment may not appear in a dependent type: \n\t Error: " + f, pos);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof NoAssignmentInDepType) )
				return false;
			return((NoAssignmentInDepType)o).position().equals(position());
		}
	}

	public static class PlaceTypeException extends SemanticException {
		private static final long serialVersionUID = -8998234559836889448L;

		public PlaceTypeException(String s, Position p) {
			super(s,p);
		}
	}
	public static class PlaceTypeErrorFieldShouldBeGlobal extends  PlaceTypeException {
		private static final long serialVersionUID = -7491337042919050786L;
		public PlaceTypeErrorFieldShouldBeGlobal(Field f, Position pos) {
			super("Place type error: Field should be global. \n\t Field: " + f, pos);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof PlaceTypeErrorFieldShouldBeGlobal) )
				return false;
			return((PlaceTypeErrorFieldShouldBeGlobal)o).position().equals(position());
		}
	}
	public static class PlaceTypeErrorFieldShouldBeLocalOrGlobal extends PlaceTypeException {
		private static final long serialVersionUID = 8839433155480902083L;
		public PlaceTypeErrorFieldShouldBeLocalOrGlobal(Field f, XTerm place, XTerm targetPlace, Position pos) {
			super("Place type error: either field target should be local or field should be global."
					+ "\n\t Field: " + f.name()
					+ "\n\t Field target: " + f.target()
					+ (targetPlace != null ? "\n\t Field target place: "+ targetPlace : "" )
					+ "\n\t Current place: " + place,
					pos);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof PlaceTypeErrorFieldShouldBeLocalOrGlobal) )
				return false;
			return posEquals(this.position(), ((SemanticError) o).position());
		}
	}

	public static class PlaceTypeErrorMethodShouldBeGlobal extends PlaceTypeException {
		private static final long serialVersionUID = -657551989521522263L;

		public PlaceTypeErrorMethodShouldBeGlobal(Call c, Position pos) {
			super("Place type error: Method should be global. (Called within a global method.) " +
					"\n\t Method: " + c.name(), pos);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof PlaceTypeErrorMethodShouldBeGlobal) )
				return false;
			return posEquals(this.position(), ((SemanticError) o).position());
		}
	}
	public static class PlaceTypeErrorMethodShouldBeLocalOrGlobal extends  PlaceTypeException {
		private static final long serialVersionUID = 5212483087766572622L;

		public PlaceTypeErrorMethodShouldBeLocalOrGlobal(Call c, XTerm place, XTerm targetPlace, Position pos) {
			super("Place type error: either method target should be local or method should be global."
					+ "\n\t Method target: " + c.target()
					+ "\n\t Method target place: " + targetPlace
					+ "\n\t Current place: " + place
					+ "\n\t Method: " + c.name(), pos);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof PlaceTypeErrorMethodShouldBeLocalOrGlobal) )
				return false;

			return posEquals(this.position(), ((SemanticError) o).position());
		}
	}
	static boolean posEquals(Position a, Position b) {
		return a.line()==b.line() && a.column()==b.column();
	}
	public static class DependentClauseErrorFieldMustBeFinal extends SemanticException implements DepTypeException {
		private static final long serialVersionUID = 8737323529719693415L;
		public DependentClauseErrorFieldMustBeFinal(Field f,Position pos) {
			super("Only final fields are permitted in dependent clauses."
					+ "\n\t Field: " + f, pos);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof DependentClauseErrorFieldMustBeFinal) )
				return false;
			return((DependentClauseErrorFieldMustBeFinal)o).position().equals(position());
		}
	}

	public static class DependentClauseErrorSelfMayAccessOnlyProperties extends SemanticException implements DepTypeException {
		private static final long serialVersionUID = 8019315512496243771L;
		public DependentClauseErrorSelfMayAccessOnlyProperties(FieldInstance fi,Position pos) {
			super("Only properties may be prefixed with self in a dependent clause."
					+ "\n\t Field: " + fi.name()
					+ "\n\t Container: " + fi.container(), pos);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof DependentClauseErrorSelfMayAccessOnlyProperties) )
				return false;
			return((DependentClauseErrorSelfMayAccessOnlyProperties)o).position().equals(position());
		}
	}

	public static class DependentClauseIsInconsistent extends SemanticException {
	    private static final long serialVersionUID = -737687218058693221L;
	    public DependentClauseIsInconsistent(String entity, DepParameterExpr e) {
	        super("The "+entity+"'s dependent clause is inconsistent.",
	              e == null ? null : e.position());
	    }
	    public boolean equals(Object o) {
	        if (o==null || ! (o instanceof DependentClauseIsInconsistent) )
	            return false;
	        return((DependentClauseIsInconsistent)o).position().equals(position());
	    }
	}

	public static class CannotAccessStaticFieldOfTypeParameter extends SemanticException {
		private static final long serialVersionUID = -8016592273145691613L;
		public CannotAccessStaticFieldOfTypeParameter(Type t,Position pos) {
			super("Cannot access static field of a type parameter"
					+ "\n\t Type Parameter: " + t, pos);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof CannotAccessStaticFieldOfTypeParameter) )
				return false;
			return((CannotAccessStaticFieldOfTypeParameter)o).position().equals(position());
		}
	}

	public static class CannotReadFieldOfProtoValue extends SemanticException implements ProtoTypeException {
		private static final long serialVersionUID = -512760271069318563L;
		public CannotReadFieldOfProtoValue(Field f,Position pos) {
			super("Cannot read field of a proto value."
					+ "\n\t Field: " + f
					+ "\n\t Proto value:" + f.target(), pos);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof CannotReadFieldOfProtoValue) )
				return false;
			return((CannotReadFieldOfProtoValue)o).position().equals(position());
		}
	}
	public static class ProtoValuesAssignableOnlyToProtoReceivers extends SemanticException implements ProtoTypeException {
		private static final long serialVersionUID = -6741587508354666830L;
		public ProtoValuesAssignableOnlyToProtoReceivers(Expr e, FieldAssign f, Position pos) {
			super("A proto value can be assigned to a field only if receiver type is proto."
					+ "\n\t Value: " + e
					+ "\n\t Field: " + f.name()
					+ "\n\t Target: "  + f.target()
					+ "\n\t Target type: " + f.target().type(),
					pos);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof ProtoValuesAssignableOnlyToProtoReceivers) )
				return false;
			return((ProtoValuesAssignableOnlyToProtoReceivers)o).position().equals(position());
		}
	}
	public static class ProtoValuesAssignableOnlyUsingEquals extends SemanticException implements ProtoTypeException {
		private static final long serialVersionUID = -7997300104807372345L;
		public ProtoValuesAssignableOnlyUsingEquals(Expr e,  Position pos) {
			super("A proto value assignment to a field must use \"=\" assignment operator."
					+ "\n\t Value: " + e,
					pos);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof ProtoValuesAssignableOnlyUsingEquals) )
				return false;
			return((ProtoValuesAssignableOnlyUsingEquals)o).position().equals(position());
		}
	}
	public static class CannotConvertToType extends SemanticException implements ConversionException {
		private static final long serialVersionUID = 5580836853775144578L;

		public CannotConvertToType(Type fromType,  Type toType, Position pos) {
			super("Cannot perform type conversion."
					+ "\n\t From type: "  + fromType
					+ "\n\t To type: " + toType,
					pos);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof CannotConvertToType) )
				return false;
			return((CannotConvertToType)o).position().equals(position());
		}
	}

	public static class CannotConvertExprToType extends SemanticException implements ConversionException {
		private static final long serialVersionUID = -3353656656440601443L;
		public CannotConvertExprToType(Expr expr, Converter.ConversionType conversion,  Type toType, Position pos) {
			super("Cannot "
					+ (conversion == ConversionType.UNKNOWN_CONVERSION ? "cast" : "implicitly convert")
					+ " expression to type."
					+ "\n\t Expression: "  + expr
					+ "\n\t Expression type: "  + expr.type()
					+ "\n\t To type: " + toType,
					pos);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof CannotConvertExprToType) )
				return false;
			return((CannotConvertExprToType)o).position().equals(position());
		}
	}

	public static class InconsistentReturnType extends SemanticException {
		private static final long serialVersionUID = 5928425853367539997L;

		public <PI extends X10ProcedureInstance<?>> InconsistentReturnType(Type t, PI me) {
			super("Inconsistent return type."
					+ "\n\t ReturnType: " + t
					+ "\n\t Invocation: " + me
					+ "\n\t Position: " + me.position());
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof InconsistentReturnType) )
				return false;
			return((InconsistentReturnType)o).position().equals(position());
		}
	}
	public static class GlobalFieldIsVar extends SemanticException {
		private static final long serialVersionUID = 57613769584666608L;
		public GlobalFieldIsVar(X10FieldDecl f) {
			super("Global field cannot be var."
					+ "\n\t Field: " + f.name(),
					f.position());
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof GlobalFieldIsVar) )
				return false;
			return((GlobalFieldIsVar)o).position().equals(position());
		}
	}
	public static class CannotAssignToProperty extends SemanticException {
		private static final long serialVersionUID = 3461823901187721248L;
		public CannotAssignToProperty(X10FieldInstance f, Position p) {
			super("Must use property(...) to assign to a property."
					+ "\n\t Property: " + f.name(),
					p);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof CannotAssignToProperty) )
				return false;
			return((CannotAssignToProperty)o).position().equals(position());
		}
	}
	public static class TernaryConditionalTypeUndetermined extends SemanticException {
		private static final long serialVersionUID = -3724235800269996470L;
		public TernaryConditionalTypeUndetermined(Type t1, Type t2, Position p) {
			super("Could not determine type of ternary conditional expression. "
					+ "Cannot assign expression of type T1 to T2 or vice versa."
					+ "\n\t T1: " + t1
					+ "\n\t T2: " + t2,
					p);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof TernaryConditionalTypeUndetermined) )
				return false;
			return((TernaryConditionalTypeUndetermined)o).position().equals(position());
		}
	}
	public static class TypedefMustBeStatic extends SemanticException {
	    private static final long serialVersionUID = -1088534868188898121L;
	    public TypedefMustBeStatic(MacroType mt, Position pos) {
	        super("Illegal type def " + mt + ": type-defs must be static.", pos);
	    }
	    public boolean equals(Object o) {
	        if (o==null || ! (o instanceof TypedefMustBeStatic) )
	            return false;
	        return((TypedefMustBeStatic)o).position().equals(position());
	    }
	}
	public static class StructMustBeStatic extends SemanticException {
		private static final long serialVersionUID = 1450037642852701286L;
		public StructMustBeStatic(X10ClassDecl cd) {
			super("Struct must be declared static."
					+ "\n\t Struct: " + cd.name(),
					cd.position());
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof StructMustBeStatic) )
				return false;
			return((StructMustBeStatic)o).position().equals(position());
		}
	}
	public static class NewOfStructNotPermitted extends SemanticException {
		private static final long serialVersionUID = 2484875712265904017L;
		public NewOfStructNotPermitted(New n) {
			super("Struct constructor invocations must not use \"new\"."
					+ "\n\t Struct: " + n.toString(),
					n.position());
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof NewOfStructNotPermitted) )
				return false;
			return((NewOfStructNotPermitted)o).position().equals(position());
		}
	}
	public static class InstanceofError extends SemanticException {
		private static final long serialVersionUID = -3026696944876868780L;
		public InstanceofError(Type left, Type right, Position pos) {
			super("Left operand of instanceof must be castable to right type."
					+ "\n\t Left type: " + left
					+ "\n\t Right type: " + right,
					pos);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof InstanceofError) )
				return false;
			return((InstanceofError)o).position().equals(position());
		}
	}
	public static class VarMustBeFinalInTypeDef extends SemanticException {
		private static final long serialVersionUID = -1828548933164244089L;
		public VarMustBeFinalInTypeDef(String name, Position pos) {
			super("Variable must be immutable (val) in type def."
					+ "\n\t Variable: " + name,
					pos);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof VarMustBeFinalInTypeDef) )
				return false;
			return((VarMustBeFinalInTypeDef)o).position().equals(position());
		}
	}
	public static class VarMustBeAccessibleInTypeDef extends SemanticException {
		private static final long serialVersionUID = -1984266198367743732L;
		public VarMustBeAccessibleInTypeDef(VarInstance<?> var, Position pos) {
			super("Variable must be accessible in type."
					+ "\n\t Variable: " + var,
					pos);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof VarMustBeAccessibleInTypeDef) )
				return false;
			return((VarMustBeAccessibleInTypeDef)o).position().equals(position());
		}
	}

	public static class CannotExtendTwoInstancesSameInterfaceLimitation extends SemanticException {
		private static final long serialVersionUID = -1984266198367743732L;
		public CannotExtendTwoInstancesSameInterfaceLimitation(Type t1, Type t2, Position pos) {
			super("LIMITATION: Cannot extend different instantiations of the same type."
					+ "\n\t Type 1: " + t1
					+ "\n\t Type 2: " + t2,
					pos);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof CannotExtendTwoInstancesSameInterfaceLimitation) )
				return false;
			return((CannotExtendTwoInstancesSameInterfaceLimitation)o).position().equals(position());
		}
	}

	public static class TypeIsNotASubtypeOfTypeBound extends SemanticException {
	    private static final long serialVersionUID = 5054688602611389407L;
	    public TypeIsNotASubtypeOfTypeBound(Type type, Type hasType, Position pos) {
	        super("Computed type is not a subtype of type bound." +
	              "\n\t Computed Type: " + type +
	              "\n\t Type Bound: " + hasType, pos);
	    }
	    public boolean equals(Object o) {
	        if (o==null || ! (o instanceof TypeIsNotASubtypeOfTypeBound) )
	            return false;
	        return((TypeIsNotASubtypeOfTypeBound)o).position().equals(position());
	    }
	}

	public static class TypeIsMissingParameters extends SemanticException {
		private static final long serialVersionUID = 1254563921501323608L;
		public TypeIsMissingParameters(Type t1, List<ParameterType> t2, Position pos) {
			super("Type is missing parameters."
					+ "\n\t Type: " + t1
					+ "\n\t Expected parameters: " + t2,
					pos);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof TypeIsMissingParameters) )
				return false;
			return((TypeIsMissingParameters)o).position().equals(position());
		}
	}

	public static class CannotAssignToElement extends SemanticException {
		private static final long serialVersionUID = -9118489907802078734L;
		public CannotAssignToElement(String leftString, boolean arrayP, Expr right, Type t, Position pos, SemanticException cause) {
			super(toMessage(leftString, arrayP, right, t, cause), pos);
		}
		private static String toMessage(String leftString, boolean arrayP, Expr right, Type t, SemanticException cause) {
			if (cause.getMessage() == null)
				return null;
			return "Cannot assign expression to " + (arrayP ? "array " : "rail ") + "element of given type."
					+ "\n\t Expression: " + right
					+ "\n\t Type: " + right.type()
					+ "\n\t " + (arrayP ? "Array ": "Rail ") +"element: "  + leftString
					+ "\n\t Type: " + t
					+ "\n\t Cause: " + cause.getMessage();
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof CannotAssignToElement) )
				return false;
			return((CannotAssignToElement)o).position().equals(position());
		}
	}
	public static class CannotPerformAssignmentOperation extends SemanticException {
	    private static final long serialVersionUID = 2577635917256629928L;
	    public CannotPerformAssignmentOperation(String leftString, boolean arrayP, String op, Expr right, Type t, Position pos, SemanticException error) {
	        super("Cannot perform assignment expression to " + (arrayP ? "array " : "rail ") + "element of given type."
	              + "\n\t Expression: " + right
	              + "\n\t Operation: " + op
	              + "\n\t Type: " + right.type()
	              + "\n\t " + (arrayP ? "Array ": "Rail ") +"element: "  + leftString
	              + "\n\t Type: " + t
	              + "\n\t Because of: " + error.getMessage(),
	              pos);
	    }
	    public boolean equals(Object o) {
	        if (o==null || ! (o instanceof CannotPerformAssignmentOperation) )
	            return false;
	        return((CannotPerformAssignmentOperation)o).position().equals(position());
	    }
	}
	public static class AssignSetMethodCantBeStatic extends SemanticException {
		private static final long serialVersionUID = 2179749179921672516L;
		public AssignSetMethodCantBeStatic(MethodInstance mi, Expr array,  Position pos) {
			super("The set method for array cannot be static."
					+ "\n\t Array: "  + array
					+ "\n\t Method: " + mi,
					pos);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof CannotAssignToElement) )
				return false;
			return((CannotAssignToElement)o).position().equals(position());
		}
	}

	public static class ConstructorReturnTypeNotEntailed extends SemanticException {
		private static final long serialVersionUID = -4705861378590877043L;
		public ConstructorReturnTypeNotEntailed(CConstraint known, CConstraint ret,  Position pos) {
			super("Instances created by this constructor do not satisfy return type"
					+ "\n\t Constraint satisfied: "  + known
					+ "\n\t Constraint required: " + ret,
					pos);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof ConstructorReturnTypeNotEntailed) )
				return false;
			return((ConstructorReturnTypeNotEntailed)o).position().equals(position());
		}
	}
	public static class InconsistentInvariant extends SemanticException {
	    private static final long serialVersionUID = 243905319528026232L;
	    public InconsistentInvariant(X10ClassDef cd,  Position pos) {
	        super("Class invariant is inconsistent."
	              + "\n\t Invariant: "  + cd.classInvariant()
	              + "\n\t Class: " + cd,
	              pos);
	    }
	    public boolean equals(Object o) {
	        if (o==null || ! (o instanceof InconsistentInvariant) )
	            return false;
	        return((InconsistentInvariant)o).position().equals(position());
	    }
	}
	public static class ThisNotPermittedInConstructorFormals extends SemanticException {
	    private static final long serialVersionUID = -7998660806293584830L;
	    public ThisNotPermittedInConstructorFormals(List<Formal> formals,  Position pos) {
	        super("This or super cannot be used (implicitly or explicitly) in a constructor formal type."
	              + "\n\t Formals: "  + formals,
	                pos);
	    }
	    public boolean equals(Object o) {
	        if (o==null || ! (o instanceof ThisNotPermittedInConstructorFormals ) )
	            return false;
	        return((ThisNotPermittedInConstructorFormals )o).position().equals(position());
	    }
	}
	public static class ThisNotPermittedInConstructorReturnType extends SemanticException {
		private static final long serialVersionUID = 751592738631688909L;
		public ThisNotPermittedInConstructorReturnType(TypeNode type,  Position pos) {
	        super("This or super cannot be used (implicitly or explicitly) in a constructor return type."
	              + "\n\t Type: "  + type,
	                pos);
	    }
	    public boolean equals(Object o) {
	        if (o==null || ! (o instanceof ThisNotPermittedInConstructorReturnType ) )
	            return false;
	        return((ThisNotPermittedInConstructorReturnType )o).position().equals(position());
	    }
	}
	public static class MethodOrStaticConstructorNotFound extends SemanticException {
	    private static final long serialVersionUID = -6230289868576516608L;
	    public MethodOrStaticConstructorNotFound(MethodMatcher mm,  Position pos) {
	        super("Method or static constructor not found for given matcher."
	              + "\n\t Matcher: "  + mm,
	              pos);
	        
	        Map<String, Object> map = new HashMap<String, Object>();
		    map.put("ERROR_CODE", 1002);
		    map.put("METHOD", mm.name().toString());
		    map.put("ARGUMENTS", mm.argumentString());
		    setAttributes(map);
	    }
	    public MethodOrStaticConstructorNotFound(ConstructorMatcher mm,  Position pos) {
	        super("Method or static constructor not found for given matcher."
	                + "\n\t Matcher: "  + mm,
	                pos);
	    }
	    public boolean equals(Object o) {
	        if (o==null || ! (o instanceof MethodOrStaticConstructorNotFound ) )
	            return false;
	        return((MethodOrStaticConstructorNotFound )o).position().equals(position());
	    }
	}
	public static class AmbiguousCall extends SemanticException {
	    private static final long serialVersionUID = 2449179239460432298L;
        public AmbiguousCall(ProcedureInstance<?> pi,  Expr cc, Position pos) {
            super("Ambiguous call: the given procedure and closure match."
                  + "\n\t Procedure: "  + pi
                  + "\n\t Closure: "  + cc,
                  pos);
        }
	    public AmbiguousCall(MethodInstance pi, ConstructorInstance ci, Position pos) {
	        super("Ambiguous call: cannot resolve between struct constructor and method."
	              + "\n\t Method: " + pi
	              + "\n\t Struct constructor: " + ci,
	              pos);
	    }
	    public boolean equals(Object o) {
	        if (o==null || ! (o instanceof AmbiguousCall ) )
	            return false;
	        return((AmbiguousCall)o).position().equals(position());
	    }
	}
	public static class AmbiguousOperator extends SemanticException {
	    private static final long serialVersionUID = 2747145999189438964L;
	    private static String matchingMethods(List<MethodInstance> mis) {
	        StringBuilder sb = new StringBuilder();
	        for (MethodInstance mi : mis) sb.append("\t").append(mi).append("\n");
	        return sb.toString();
	    }
	    public AmbiguousOperator(Unary.Operator op, List<MethodInstance> mis,  Position pos) {
	        super("Ambiguous operator '" + op + "': all of\n" + matchingMethods(mis) + "match.", pos);
	    }
	    public AmbiguousOperator(Binary.Operator op, List<MethodInstance> mis,  Position pos) {
	        super("Ambiguous operator '" + op + "': all of\n" + matchingMethods(mis) + "match.", pos);
	    }
	    public boolean equals(Object o) {
	        if (o==null || ! (o instanceof AmbiguousOperator ) )
	            return false;
	        return((AmbiguousOperator)o).position().equals(position());
	    }
	}
	public static class OnlyValMayHaveHasType extends SemanticException {
		private static final long serialVersionUID = -4705861378590877043L;
		public OnlyValMayHaveHasType(X10FieldDecl field) {
			super("Only val fields may have a has type."
					+ "\n\t Field: "  + field
					+ "\n\t Field has type: " + field.hasType(),
					field.position());
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof OnlyValMayHaveHasType) )
				return false;
			return((OnlyValMayHaveHasType)o).position().equals(position());
		}
	}
	public static class CannotFindIndexType extends SemanticException {
		private static final long serialVersionUID = -8300517312728182918L;
		public CannotFindIndexType(Type type, Position position) {
			super("Cannot determine index type for given type."
					+ "\n\t Type: "  + type,
					position);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof CannotFindIndexType) )
				return false;
			return((CannotFindIndexType)o).position().equals(position());
		}
	}

	public static class CannotTranslateStaticField extends SemanticException {
		private static final long serialVersionUID = -950551311327307252L;
		public CannotTranslateStaticField(Type type, Position position) {
			super("Cannot translate a static field of non-class type"
					+ "\n\t Type: "  + type,
					position);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof CannotTranslateStaticField) )
				return false;
			return((CannotTranslateStaticField)o).position().equals(position());
		}
	}

	public static class CannotDisambiguate extends SemanticException {
		private static final long serialVersionUID = -4594440281666152534L;
		public CannotDisambiguate(Node n, Position position) {
			super("Cannot disambiguate " + n, position);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof CannotDisambiguate) )
				return false;
			return((CannotDisambiguate)o).position().equals(position());
		}
	}

	public static class CannotGenerateCast extends SemanticException {
		private static final long serialVersionUID = 8124533664575933282L;
		public CannotGenerateCast(Node n, Position position) {
			super("Place type error with this expression. Cannot generate dynamic cast." +
					"\n\t Expression: " + n, position);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof CannotGenerateCast) )
				return false;
			return((CannotGenerateCast)o).position().equals(position());
		}
	}

	public static class ClassMustHaveClassSupertype extends SemanticException {
	    private static final long serialVersionUID = -7826831387240378409L;
	    public ClassMustHaveClassSupertype(Ref<? extends Type> superType, ClassDef type, Position pos) {
	        super(superType + " cannot be the superclass for " + type +
	              "; a class must subclass a class.", pos);
	    }
	    public boolean equals(Object o) {
	        if (o==null || ! (o instanceof ClassMustHaveClassSupertype) )
	            return false;
	        return((ClassMustHaveClassSupertype)o).position().equals(position());
	    }
	}
	public static class NoCollectingFinishFound extends SemanticException {
		private static final long serialVersionUID = 9107601200096892236L;
		public NoCollectingFinishFound(String offer, Position position) {
			super("Cannot find enclosing collecting finish for offer statement."
					+ "\n\t Offer: "  + offer,
					position);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof NoCollectingFinishFound) )
				return false;
			return((NoCollectingFinishFound)o).position().equals(position());
		}
	}
	public static class OfferDoesNotMatchCollectingFinishType extends SemanticException {
		private static final long serialVersionUID = -4277756733682836855L;
		public OfferDoesNotMatchCollectingFinishType(Type eType, Type fType, Position position) {
			super("The type of the offer expression does not match the type of the collecting finish."
					+ "\n\t Offer expression type: "  + eType
					+ "\n\t Collecting finish type: "  + fType,
					position);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof OfferDoesNotMatchCollectingFinishType) )
				return false;
			return((OfferDoesNotMatchCollectingFinishType)o).position().equals(position());
		}
	}
	public static class IsNotReducible extends SemanticException {
		private static final long serialVersionUID = 6604309927252841516L;
		public IsNotReducible(Expr expr,  Position position) {
			super("The reducer must be of type Reducible[T], for some type T."
					+ "\n\t Reducer: " + expr
					+ "\n\t Reducer type: "  + expr.type(),
					position);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof OfferDoesNotMatchCollectingFinishType) )
				return false;
			return((OfferDoesNotMatchCollectingFinishType)o).position().equals(position());
		}
	}
	public static class CannotCallCodeThatOffers extends SemanticException {
		private static final long serialVersionUID = 1561991534265566375L;
		public CannotCallCodeThatOffers(X10ProcedureInstance<? extends ProcedureDef> pi,  Position position) {
			super("Code that can offer values of given type is invoked in a context which does not expect offers."
					+ "\n\t Offer type: " + Types.get(pi.offerType()),
					position);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof CannotCallCodeThatOffers) )
				return false;
			return((CannotCallCodeThatOffers)o).position().equals(position());
		}
	}
	public static class OfferTypeMismatch extends SemanticException {
		private static final long serialVersionUID = 6476600577193965991L;
		public OfferTypeMismatch(Type actualType, Type expectedType,   Position position) {
			super("Offer type mismatch."
					+ "\n\t Found offer type: " + actualType
					+ "\n\t Expected offer type: " + expectedType,
					position);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof OfferTypeMismatch) )
				return false;
			return((OfferTypeMismatch)o).position().equals(position());
		}
	}
}
