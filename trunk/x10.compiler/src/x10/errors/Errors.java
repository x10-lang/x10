package x10.errors;

import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.Receiver;
import polyglot.types.FieldInstance;
import polyglot.types.MethodInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import x10.ast.SemanticError;
import x10.ast.X10Call;
import x10.ast.X10FieldAssign_c;
import x10.constraint.XTerm;
import x10.types.X10ProcedureInstance;
import x10.types.checker.Converter;
import x10.types.checker.Converter.ConversionType;
import x10.types.constraints.XConstrainedTerm;

/**
 * Start at centralizing Error messages. Goal is to support standardization of error messages for 
 * internationalization, to make unit tests more accurate, and to support better error-handling
 * inside compiler. 
 * 
 * @author vj 2010/02/06
 *
 */
public class Errors {
	
	public static interface DepTypeException  {}
	public static interface PlaceTypeException {}
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
	}
	public static class IncompatibleReturnType extends SemanticException {
		private static final long serialVersionUID = -6220163900080278288L;

		public IncompatibleReturnType(MethodInstance mi, MethodInstance mj) {
			super("Attempting to use incompatible return type."
					+ "\n\t Method: " + mi
					+ "\n\t Expected Type: " + mj.returnType()
					+ "\n\t Found Type: " + mi.returnType(), mi.position());
		}
		
	}
	
	public static class InvalidParameter extends SemanticException {
		private static final long serialVersionUID = -1351185257724314440L;
		public InvalidParameter(Type from, Type to, Position pos) {
			super("Invalid Parameter.\n\t expected type: " + to + "\n\t found: " + from, pos);
		}
	}

	
	public static class NoAssignmentInDepType extends SemanticException implements DepTypeException {
		private static final long serialVersionUID = 8343234065357158485L;
		public NoAssignmentInDepType(FieldAssign f, Position pos) {
			super("Assignment may not appear in a dependent type: \n\t Error: " + f, pos);
		}
	}

	
	public static class PlaceTypeErrorFieldShouldBeGlobal extends SemanticException implements PlaceTypeException {
		private static final long serialVersionUID = -7491337042919050786L;
		public PlaceTypeErrorFieldShouldBeGlobal(Field f, Position pos) {
			super("Place type error: Field should be global. \n\t Field: " + f, pos);
		}
	}
	public static class PlaceTypeErrorFieldShouldBeLocalOrGlobal extends SemanticException implements PlaceTypeException {
		private static final long serialVersionUID = 8839433155480902083L;
		public PlaceTypeErrorFieldShouldBeLocalOrGlobal(Field f, XTerm place, Position pos) {
			super("Place type error: either field target should be local or field should be global." 
					+ "\n\t Field target: " + f.target()
					+ "\n\t Current place: " + place
					+ "\n\t Field: " + f.name(), pos);
		}
	}
	
	public static class PlaceTypeErrorMethodShouldBeGlobal extends SemanticException implements PlaceTypeException {
		private static final long serialVersionUID = -657551989521522263L;

		public PlaceTypeErrorMethodShouldBeGlobal(Call c, Position pos) {
			super("Place type error: Method should be global. (Called within a global method.) \n\t Method: " + c.name(), pos);
		}
	}
	public static class PlaceTypeErrorMethodShouldBeLocalOrGlobal extends SemanticException implements PlaceTypeException {
		private static final long serialVersionUID = 5212483087766572622L;

		public PlaceTypeErrorMethodShouldBeLocalOrGlobal(Call c, XConstrainedTerm place, Position pos) {
			super("Place type error: either method target should be local or method should be global." 
					+ "\n\t Method target: " + c.target()
					+ "\n\t Current place: " + (place == null ? null : place.term())
					+ "\n\t Method: " + c.name(), pos);
		}
	}
	
	public static class DependentClauseErrorFieldMustBeFinal extends SemanticException implements DepTypeException {
		private static final long serialVersionUID = 8737323529719693415L;
		public DependentClauseErrorFieldMustBeFinal(Field f,Position pos) {
			super("Only final fields are permitted in dependent clauses."
					+ "\n\t Field: " + f, pos);
		}
	}
	
	public static class DependentClauseErrorSelfMayAccessOnlyProperties extends SemanticException implements DepTypeException {
		private static final long serialVersionUID = 8019315512496243771L;
		public DependentClauseErrorSelfMayAccessOnlyProperties(FieldInstance fi,Position pos) {
			super("Only properties may be prefixed with self in a dependent clause."
					+ "\n\t Field: " + fi.name()
					+ "\n\t Container: " + fi.container(), pos);
		}
	}
	
	public static class CannotAccessStaticFieldOfTypeParameter extends SemanticException {
		private static final long serialVersionUID = -8016592273145691613L;
		public CannotAccessStaticFieldOfTypeParameter(Type t,Position pos) {
			super("Cannot access static field of a type parameter" 
					+ "\n\t Type Parameter: " + t, pos);
		}
	}
	
	public static class CannotReadFieldOfProtoValue extends SemanticException implements ProtoTypeException {
		private static final long serialVersionUID = -512760271069318563L;
		public CannotReadFieldOfProtoValue(Field f,Position pos) {
			super("Cannot read field of a proto value."  
					+ "\n\t Field: " + f
					+ "\n\t Proto value:" + f.target(), pos);
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
	}
	public static class ProtoValuesAssignableOnlyUsingEquals extends SemanticException implements ProtoTypeException {
		private static final long serialVersionUID = -7997300104807372345L;
		public ProtoValuesAssignableOnlyUsingEquals(Expr e,  Position pos) {
			super("A proto value assignment to a field must use \"=\" assignment operator."
					+ "\n\t Value: " + e,
					pos);
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
	}
	public static class InconsistentReturnType extends SemanticException {
		private static final long serialVersionUID = 5928425853367539997L;

		public <PI extends X10ProcedureInstance<?>> InconsistentReturnType(Type t, PI me) {
			super("Inconsistent return type."
					+ "\n\t ReturnType: " + t 
					+ "\n\t Invocation: " + me
					+ "\n\t Position: " + me.position());
		}
	}

	
}
