/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.Mark as 
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

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
import x10.types.checker.PlaceChecker;
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

	
	public static class PlaceTypeErrorFieldShouldBeGlobal extends SemanticException implements PlaceTypeException {
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
	public static class PlaceTypeErrorFieldShouldBeLocalOrGlobal extends SemanticException implements PlaceTypeException {
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
	
	public static class PlaceTypeErrorMethodShouldBeGlobal extends SemanticException implements PlaceTypeException {
		private static final long serialVersionUID = -657551989521522263L;

		public PlaceTypeErrorMethodShouldBeGlobal(Call c, Position pos) {
			super("Place type error: Method should be global. (Called within a global method.) \n\t Method: " + c.name(), pos);
		}
		public boolean equals(Object o) {
			if (o==null || ! (o instanceof PlaceTypeErrorMethodShouldBeGlobal) )
				return false;
			return posEquals(this.position(), ((SemanticError) o).position());
		}
	}
	public static class PlaceTypeErrorMethodShouldBeLocalOrGlobal extends SemanticException implements PlaceTypeException {
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

	
}
