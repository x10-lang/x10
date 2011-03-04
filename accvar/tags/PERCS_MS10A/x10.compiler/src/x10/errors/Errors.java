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
import polyglot.types.FunctionDef;
import polyglot.types.ProcedureDef;
import polyglot.types.ProcedureInstance;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.types.VarInstance;
import polyglot.types.MemberDef;
import polyglot.types.TypeObject;
import polyglot.types.ClassType;
import polyglot.types.TypeSystem_c.ConstructorMatcher;
import polyglot.types.TypeSystem_c.MethodMatcher;
import polyglot.util.CodedErrorInfo;
import polyglot.util.ErrorInfo;
import polyglot.util.Position;
import x10.ExtensionInfo;
import x10.ExtensionInfo.X10Scheduler.X10Job;
import x10.ast.DepParameterExpr;
import x10.ast.X10ClassDecl;
import x10.ast.X10FieldDecl;
import x10.constraint.XTerm;
import x10.types.MacroType;
import x10.types.ParameterType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10FieldInstance;
import x10.types.MethodInstance;
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
		issue(ei, e, getPosition(n));
		((X10Job) job).setReportedErrors(true);
	}

	public static void issue(polyglot.frontend.ExtensionInfo extInfo, SemanticException e, Position p) {
        issue((ExtensionInfo)extInfo,e,p);
    }
	public static void issue(ExtensionInfo extInfo, SemanticException e, Position p) {
		if (e.getCause() == null && e.position() == null && p != null)
			e = new SemanticException(e.getMessage(), p);
		boolean newP = extInfo.errorSet().add(e);
		if (newP && e.getMessage() != null) {

			Position position = e.position();

			if (position == null && p != null) {
				position = p;
			}
			
			ErrorInfo errorInfo = new CodedErrorInfo(ErrorInfo.SEMANTIC_ERROR,
			 					e.getMessage(), position, e.attributes());
			extInfo.compiler().errorQueue().enqueue(errorInfo);
		}
	}

	private static Position getPosition(Node n) {
		return n == null ? null : n.position();
	}

	public static interface DepTypeException {}
	public static interface ConversionException {}
    public static class EqualByTypeAndPosException extends SemanticException {
        private static final long serialVersionUID = -5707886878408735177L;
        public EqualByTypeAndPosException(String m, Position position) {
            super(m,position);
        }
		public final boolean equals(Object o) {
			if (o==null || o.getClass()!=this.getClass() )
				return false;
			return posEquals(((EqualByTypeAndPosException)o).position(),position());
		}
        public final void issue(Job job) {
            Errors.issue(job, this);
        }
    }

    // todo Yoav added: I use serialVersionUID=1L like lpg parser. We should increment it if an class changes.
    
    public static class ClassCannotHaveSuperInterface extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1L;
		public ClassCannotHaveSuperInterface(ClassType type, Position p) {
			super("Class " + type + " cannot have a superinterface.", p);
		}
	}
    public static class SuperInterfaceNotInterface extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1L;
		public SuperInterfaceNotInterface(Type t, ClassType type, Position p) {
			super("Superinterface " + t + " of " + type + " is not an interface.", p);
		}
	}
    public static class CannotHaveSuperclass extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1L;
		public CannotHaveSuperclass(ClassType type, Position p) {
			super("Class \"" + type + "\" cannot have a superclass.", p);
		}
	}
    public static class ExtendedFinalClass extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1L;
		public ExtendedFinalClass(ClassType type, Position p) {
			super("Cannot extend final class \"" + type.superClass() + "\".", p);
		}
	}
    public static class ExtendedNonClass extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1L;
		public ExtendedNonClass(ClassType type, Position p) {
			super("Cannot extend non-class \"" + type.superClass() + "\".", p);
		}
	}
    public static class InnerDeclaredStatic extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1L;
		public InnerDeclaredStatic(ClassType type, Position p) {
			super("Inner classes cannot declare static member classes.", p);
		}
	}
    public static class InnerDeclaredInterface extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1L;
		public InnerDeclaredInterface(ClassType type, Position p) {
			super("Inner classes cannot declare member interfaces.", p);
		}
	}
    public static class SameNameLocal extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1L;
		public SameNameLocal(ClassType type, Position p) {
			super("Cannot declare local " +
                                    "class \"" + type + "\" within the same " +
                                    "method, constructor or initializer as another " +
                                    "local class of the same name.", p);
		}
	}

    public static class SameNameClass extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1L;
		public SameNameClass(ClassType type, Position p) {
			super("Cannot declare member " +
                                "class \"" + type.fullName() +
                                "\" inside class with the " +
                                "same name.", p);
		}
    }
	public static class DuplicateMember extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1L;
		public DuplicateMember(TypeObject def) {
			super("Duplicate member " + def, def.position());
		}
	}

	public static class CannotAssign extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -4243637083971033996L;
		public CannotAssign(Expr expr, Type targetType, Position pos) {
			super("Cannot assign expression to target."
					+ "\n\t Expression: " + expr
					+ "\n\t Expected type: " + targetType
					+ "\n\t Found type: " + expr.type()
					, pos);
		}
	}
	public static class FieldInitTypeWrong extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 4778277210134359519L;

		public FieldInitTypeWrong(Expr expr, Type targetType, Position pos) {
			super("The type of the field initializer is not a subtype of the field type."
					+ "\n\t Expression: " + expr
					+ "\n\t Expected type: " + targetType
					+ "\n\t Found type: " + expr.type()
					, pos);
		}
	}
	public static class IncompatibleReturnType extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -6220163900080278288L;

		public IncompatibleReturnType(MethodInstance mi, MethodInstance mj) {
			super(mi.signature() + " in " + mi.container() + " cannot override " + mj.signature() + " in " + mj.container()
			        + "; attempting to use incompatible return type."
					+ "\n\t Expected Type: " + mj.returnType()
					+ "\n\t Found Type: " + mi.returnType(), mi.position());
		}
	}

	public static class InvalidParameter extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1351185257724314440L;
		public InvalidParameter(Type from, Type to, Position pos) {
			super("   Invalid Parameter.\n\t Expected type: " + to + "\n\t Found type: " + from, pos);
		}
	}

	public static class NoAssignmentInDepType extends EqualByTypeAndPosException implements DepTypeException {
		private static final long serialVersionUID = 8343234065357158485L;
		public NoAssignmentInDepType(FieldAssign f, Position pos) {
			super("Assignment may not appear in a constrained type: \n\t Error: " + f, pos);
		}
	}

	public static class PlaceTypeException extends EqualByTypeAndPosException {
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
	}

	public static class PlaceTypeErrorMethodShouldBeGlobal extends PlaceTypeException {
		private static final long serialVersionUID = -657551989521522263L;

		public PlaceTypeErrorMethodShouldBeGlobal(Call c, Position pos) {
			super("Place type error: Method should be global. (Called within a global method.) " +
					"\n\t Method: " + c.name(), pos);
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
	}
	static boolean posEquals(Position a, Position b) {
		return a.line()==b.line() && a.column()==b.column();
	}
	public static class DependentClauseErrorFieldMustBeFinal extends EqualByTypeAndPosException implements DepTypeException {
		private static final long serialVersionUID = 8737323529719693415L;
		public DependentClauseErrorFieldMustBeFinal(X10FieldInstance fi, Position pos) {
			super("Only val fields are permitted in constraints."
					+ "\n\t Field: " + fi, pos);
		}
	}

	public static class DependentClauseErrorSelfMayAccessOnlyProperties extends EqualByTypeAndPosException implements DepTypeException {
		private static final long serialVersionUID = 8019315512496243771L;
		public DependentClauseErrorSelfMayAccessOnlyProperties(FieldInstance fi, Position pos) {
			super("Only properties may be prefixed with self in a constraint."
					+ "\n\t Field: " + fi.name()
					+ "\n\t Container: " + fi.container(), pos);
		}
	}

	public static class DependentClauseIsInconsistent extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = -737687218058693221L;
	    public DependentClauseIsInconsistent(String entity, DepParameterExpr e) {
	        super("The "+entity+"'s constraint is inconsistent.",
	              e == null ? null : e.position());
	    }
	}

	public static class CannotAccessStaticFieldOfTypeParameter extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -8016592273145691613L;
		public CannotAccessStaticFieldOfTypeParameter(Type t,Position pos) {
			super("Cannot access static field of a type parameter"
					+ "\n\t Type Parameter: " + t, pos);
		}
	}

	public static class CannotConvertToType extends EqualByTypeAndPosException implements ConversionException {
		private static final long serialVersionUID = 5580836853775144578L;

		public CannotConvertToType(Type fromType,  Type toType, Position pos) {
			super("Cannot perform type conversion."
					+ "\n\t From type: "  + fromType
					+ "\n\t To type: " + toType,
					pos);
		}
	}

	public static class CannotConvertExprToType extends EqualByTypeAndPosException implements ConversionException {
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

	public static class InconsistentReturnType extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 5928425853367539997L;

		public <PI extends X10ProcedureInstance<?>> InconsistentReturnType(Type t, PI me) {
			super("Inconsistent return type."
					+ "\n\t ReturnType: " + t
					+ "\n\t Invocation: " + me,
                    me.position());
		}
	}
	public static class GlobalFieldIsVar extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 57613769584666608L;
		public GlobalFieldIsVar(X10FieldDecl f) {
			super("Global field cannot be var."
					+ "\n\t Field: " + f.name(),
					f.position());
		}
	}
	public static class CannotAssignToProperty extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 3461823901187721248L;
		public CannotAssignToProperty(X10FieldInstance f, Position p) {
			super("Must use property(...) to assign to a property."
					+ "\n\t Property: " + f.name(),
					p);
		}
	}
	public static class TernaryConditionalTypeUndetermined extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -3724235800269996470L;
		public TernaryConditionalTypeUndetermined(Type t1, Type t2, Position p) {
			super("Could not determine type of ternary conditional expression. "
					+ "Cannot assign expression of type T1 to T2 or vice versa."
					+ "\n\t T1: " + t1
					+ "\n\t T2: " + t2,
					p);
		}
	}
	public static class TypedefMustBeStatic extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = -1088534868188898121L;
	    public TypedefMustBeStatic(MacroType mt, Position pos) {
	        super("Illegal type def " + mt + ": type-defs must be static.", pos);
	    }
	}
	public static class StructMustBeStatic extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 1450037642852701286L;
		public StructMustBeStatic(X10ClassDecl cd) {
			super("Struct must be declared static."
					+ "\n\t Struct: " + cd.name(),
					cd.position());
		}
	}
	public static class NewOfStructNotPermitted extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 2484875712265904017L;
		public NewOfStructNotPermitted(New n) {
			super("Struct constructor invocations must not use \"new\"."
					+ "\n\t Struct: " + n.toString(),
					n.position());
		}
	}
	public static class InstanceofError extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -3026696944876868780L;
		public InstanceofError(Type left, Type right, Position pos) {
			super("Left operand of instanceof must be castable to right type."
					+ "\n\t Left type: " + left
					+ "\n\t Right type: " + right,
					pos);
		}
	}
	public static class VarMustBeFinalInTypeDef extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1828548933164244089L;
		public VarMustBeFinalInTypeDef(String name, Position pos) {
			super("Variable must be immutable (val) in type def."
					+ "\n\t Variable: " + name,
					pos);
		}
	}
	public static class VarMustBeAccessibleInTypeDef extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1984266198367743732L;
		public VarMustBeAccessibleInTypeDef(VarInstance<?> var, Position pos) {
			super("Variable must be accessible in type."
					+ "\n\t Variable: " + var,
					pos);
		}
	}

	public static class CannotExtendTwoInstancesSameInterfaceLimitation extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1984266198367743732L;
		public CannotExtendTwoInstancesSameInterfaceLimitation(Type t1, Type t2, Position pos) {
			super("LIMITATION: Cannot extend different instantiations of the same type."
					+ "\n\t Type 1: " + t1
					+ "\n\t Type 2: " + t2,
					pos);
		}
	}

	public static class TypeIsNotASubtypeOfTypeBound extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = 5054688602611389407L;
	    public TypeIsNotASubtypeOfTypeBound(Type type, Type hasType, Position pos) {
	        super("Computed type is not a subtype of type bound." +
	              "\n\t Computed Type: " + type +
	              "\n\t Type Bound: " + hasType, pos);
	    }
	}

	public static class TypeIsMissingParameters extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 1254563921501323608L;
		public TypeIsMissingParameters(Type t1, List<ParameterType> t2, Position pos) {
			super("Type is missing parameters."
					+ "\n\t Type: " + t1
					+ "\n\t Expected parameters: " + t2,
					pos);
		}
	}

	public static class CannotAssignToElement extends EqualByTypeAndPosException {
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
	}
	public static class CannotPerformAssignmentOperation extends EqualByTypeAndPosException {
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
	}
	public static class AssignSetMethodCantBeStatic extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 2179749179921672516L;
		public AssignSetMethodCantBeStatic(MethodInstance mi, Expr array,  Position pos) {
			super("The set method for array cannot be static."
					+ "\n\t Array: "  + array
					+ "\n\t Method: " + mi,
					pos);
		}
	}

	public static class ConstructorReturnTypeNotSubtypeOfContainer extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = 8107418837802223220L;
	    public ConstructorReturnTypeNotSubtypeOfContainer(Type retType, X10ClassType container, Position pos) {
	        super("Constructor return type is not a subtype of the containing class"
	                + "\n\t Type: " + retType
	                + "\n\t Container: " + container,
	                pos);
	    }
	}

	public static class ConstructorReturnTypeNotEntailed extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -4705861378590877043L;
		public ConstructorReturnTypeNotEntailed(CConstraint known, CConstraint ret,  Position pos) {
			super("Instances created by this constructor do not satisfy return type"
					+ "\n\t Constraint satisfied: "  + known
					+ "\n\t Constraint required: " + ret,
					pos);
		}
	}

	public static class InconsistentInvariant extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = 243905319528026232L;
	    public InconsistentInvariant(X10ClassDef cd,  Position pos) {
	        super("Class invariant is inconsistent."
	              + "\n\t Invariant: "  + cd.classInvariant()
	              + "\n\t Class: " + cd,
	              pos);
	    }
	}
	public static class ThisNotPermittedInConstructorFormals extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = -7998660806293584830L;
	    public ThisNotPermittedInConstructorFormals(List<Formal> formals, Position pos) {
	        super("This or super cannot be used (implicitly or explicitly) in a constructor formal type."
	              + "\n\t Formals: "  + formals,
	              pos);
	    }
	}
	public static class ThisNotPermittedInConstructorReturnType extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = 751592738631688909L;
	    public ThisNotPermittedInConstructorReturnType(TypeNode type, Position pos) {
	        super("This or super cannot be used (implicitly or explicitly) in a constructor return type."
	              + "\n\t Type: "  + type,
	              pos);
	    }
	}
	public static class ThisNotPermittedInPropertyInitializer extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = -7457369484612652452L;
	    public ThisNotPermittedInPropertyInitializer(Expr init, Position pos) {
	        super("This or super cannot be used (implicitly or explicitly) in a property initializer."
	              + "\n\t Expr: "  + init,
	              pos);
	    }
	}
	public static class MethodOrStaticConstructorNotFound extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = -6230289868576516608L;
	    public MethodOrStaticConstructorNotFound(MethodMatcher mm,  Position pos) {
	        super("Method or static constructor not found for given call."
	              + "\n\t Call: "  + mm,
	              pos);
	        
	        Map<String, Object> map = new HashMap<String, Object>();
		    map.put(CodedErrorInfo.ERROR_CODE_KEY, 1002);
		    map.put("METHOD", mm.name().toString());
		    map.put("ARGUMENTS", mm.argumentString());
		    setAttributes(map);
	    }
	    public MethodOrStaticConstructorNotFound(ConstructorMatcher mm,  Position pos) {
	        super("Method or static constructor not found for given call"
	                + "\n\t Call: "  + mm,
	                pos);
	    }
	}
	public static class AmbiguousCall extends EqualByTypeAndPosException {
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
	}
	public static class AmbiguousOperator extends EqualByTypeAndPosException {
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
	}
	public static class OnlyValMayHaveHasType extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -4705861378590877043L;
		public OnlyValMayHaveHasType(X10FieldDecl field) {
			super("Only val fields may have a has type."
					+ "\n\t Field: "  + field
					+ "\n\t Field has type: " + field.hasType(),
					field.position());
		}
	}
	public static class CannotFindIndexType extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -8300517312728182918L;
		public CannotFindIndexType(Type type, Position position) {
			super("Cannot determine index type for given type."
					+ "\n\t Type: "  + type,
					position);
		}
	}

	public static class CannotTranslateStaticField extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -950551311327307252L;
		public CannotTranslateStaticField(Type type, Position position) {
			super("Cannot translate a static field of non-class type"
					+ "\n\t Type: "  + type,
					position);
		}
	}

	public static class CannotDisambiguate extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -4594440281666152534L;
		public CannotDisambiguate(Node n, Position position) {
			super("Cannot disambiguate " + n, position);
		}
	}

	public static class CannotGenerateCast extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 8124533664575933282L;
		public CannotGenerateCast(Node n, Position position) {
			super("Place type error with this expression. Cannot generate dynamic cast." +
					"\n\t Expression: " + n, position);
		}
	}

	public static class ClassMustHaveClassSupertype extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = -7826831387240378409L;
	    public ClassMustHaveClassSupertype(Ref<? extends Type> superType, ClassDef type, Position pos) {
	        super(superType + " cannot be the superclass for " + type +
	              "; a class must subclass a class.", pos);
	    }
	}
	public static class NoCollectingFinishFound extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 9107601200096892236L;
		public NoCollectingFinishFound(String offer, Position position) {
			super("Cannot find enclosing collecting finish for offer statement."
					+ "\n\t Offer: "  + offer,
					position);
		}
	}
	public static class OfferDoesNotMatchCollectingFinishType extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -4277756733682836855L;
		public OfferDoesNotMatchCollectingFinishType(Type eType, Type fType, Position position) {
			super("The type of the offer expression does not match the type of the collecting finish."
					+ "\n\t Offer expression type: "  + eType
					+ "\n\t Collecting finish type: "  + fType,
					position);
		}
	}
	public static class IsNotReducible extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 6604309927252841516L;
		public IsNotReducible(Expr expr,  Position position) {
			super("The reducer must be of type Reducible[T], for some type T."
					+ "\n\t Reducer: " + expr
					+ "\n\t Reducer type: "  + expr.type(),
					position);
		}
	}
	public static class CannotCallCodeThatOffers extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 1561991534265566375L;
		public CannotCallCodeThatOffers(X10ProcedureInstance<? extends ProcedureDef> pi,  Position position) {
			super("Code that can offer values of given type is invoked in a context which does not expect offers."
					+ "\n\t Offer type: " + Types.get(pi.offerType()),
					position);
		}
	}
	public static class OfferTypeMismatch extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 6476600577193965991L;
		public OfferTypeMismatch(Type actualType, Type expectedType,   Position position) {
			super("Offer type mismatch."
					+ "\n\t Expected offer type: " + expectedType
					+ "\n\t Found offer type: " + actualType
					,
					position);
		}
	}
	public static class StructMayNotBeGlobal extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 6179492565190231102L;
		public StructMayNotBeGlobal(Position position, X10ClassDecl cd) {
			super("global modifier cannot be used for structs."
					+ "\n\t Struct declaration: " + cd.name(), 
					position);
		}
	}
	public static class GlobalClassMustHaveGlobalClassSupertype extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 2237512073167205925L;
		public GlobalClassMustHaveGlobalClassSupertype(Ref<? extends Type> superType, ClassDef type, Position pos) {
	        super(superType + " cannot be the superclass for " + type +
	              "; a global class must subclass a global class.", pos);
	    }
	}
	public static class IllegalClockedAccess extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = -5824261892277076305L;
	    public IllegalClockedAccess(X10FieldInstance fi, Position pos) {
	        super(fi + " must be accessed in a clocked context.", pos);
	    }
	}
	public static class CannotReturnExpr extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 211999857915638603L;
		public CannotReturnExpr(Type type,  Type returnType, Position pos) {
	        super("Cannot return expression of given type."
	        		+ "\n\t type: " + type
	        		+ "\n\t desired Type:" + returnType, pos);
	    }
	}
	public static class ArrayLiteralMustBeOfArrayType extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 3059270665285777371L;
		public ArrayLiteralMustBeOfArrayType(String typeName,   Position pos) {
	        super("An array literal must start with 'new Array...'.", pos);
	    }
	}
	public static class ArrayLiteralTypeMismatch extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -8344153029213631407L;
		public ArrayLiteralTypeMismatch(Expr e, Type itype) {
	        super("The literal is not of the given type"
	        		+ "\n\t expr:" + e
	        		+ "\n\t type: " + e.type()
	        		+ "\n\t desired type: " + itype, e.position());
	    }
	}
	public static class AtArgMustBePlace extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -2736877234203434252L;
		public AtArgMustBePlace(Expr e, Type placeType, Position pos) {
	        super("The place argument to the at statement cannot be converted to type Place."
	        		+ "\n\t expr:" + e
	        		+ "\n\t type: " + e.type()
	        		+ "\n\t desired type: " + placeType, pos);
	    }
	}
}
