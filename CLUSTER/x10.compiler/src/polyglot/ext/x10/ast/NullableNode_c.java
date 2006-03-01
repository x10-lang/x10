/*
 * Created on Nov 26, 2004
 */
package polyglot.ext.x10.ast;

import polyglot.ext.jl.ast.TypeNode_c;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;

import polyglot.util.Position;
import polyglot.util.CodeWriter;

import polyglot.ast.TypeNode;
import polyglot.ast.Node;

import polyglot.types.SemanticException;
import polyglot.types.Type;
//import polyglot.types.ReferenceType;

import polyglot.visit.TypeChecker;
import polyglot.visit.NodeVisitor;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.TypeBuilder;
import polyglot.visit.PrettyPrinter;

import polyglot.main.Report;

/**
 * A NullableNode is an TypeNode that has been marked with a nullable
 * qualifier.  Note that the base TypeNode may be ambiguous and may need to
 * be resolved.
 * TODO: Find a better way of dealing with the construction of the Nullable
 * type. In the code below, this might be done twice. That doesnt seem right.
 *
 * @author vj
 */
public class NullableNode_c extends TypeNode_c implements NullableNode {

	// Recall the field this.type is defined at the supertype.
	// The Typenode representing the argument type X.
	protected TypeNode base;

	public NullableNode_c(Position pos, TypeNode base) {
		super(pos);
		this.base = base;
	}

	public TypeNode base() {
		return this.base;
	}

	public NullableNode base(TypeNode base) {
		NullableNode_c n = (NullableNode_c) copy();
		n.base = base;
		return n;
	}

	protected NullableNode reconstruct(TypeNode base) {
		return (base != this.base) ? base(base) : this;
	}

	public Node visitChildren(NodeVisitor v) {
		TypeNode base = (TypeNode) visitChild(this.base, v);
		return reconstruct(base);
	}

	/**
	 * Disambiguate the base node. Ensure that it is unambiguous.
	 * Create a NullableType_c and store it in this.type.
	 */
	public Node disambiguate(AmbiguityRemover sc) throws SemanticException {
		if (Report.should_report("debug", 5)) {
			Report.report(5,"[NullableNode_c] Disambiguating |" + this + "|(#"
					+ this.hashCode() +") with base=|" + base + "|:");
		}

		TypeNode newType = (TypeNode) base.disambiguate(sc);

		// RMF 11/2/2005 - Don't throw a SemanticException if all that's wrong is
		// that disambiguation still needs to be done on the type argument
		if (!newType.type().isCanonical())
			return this;

		if (Report.should_report("debug", 5)) {
			Report.report(5,"[NullableNode_c] ... yields type |" + newType + "|.");
		}

		X10Type baseType = (X10Type) newType.type();
		if (null == baseType) {
			throw new SemanticException("The type constructor nullable cannot be applied to a <null> type",
					position());
		}
		// [IP] This is incorrect -- non-reference types can also be nullable
//		if (!(baseType instanceof ReferenceType))
//			throw new SemanticException("Argument to nullable type-constructor must be a reference type",
//					position());

		X10TypeSystem ts = (X10TypeSystem) baseType.typeSystem();
		// [IP] FIXME: Why are we modifying this in-place?
		this.type = ts.createNullableType(position(), baseType);

		Node result = reconstruct(newType);

		if (Report.should_report("debug", 5)) {
			Report.report(5,"[NullableNode_c] ... returns |" + result +"|(#"
					+ result.hashCode() +").");
		}

		return result;
	}

	/**
	 * Typecheck the type-argument (in this.base). If it typechecks
	 * (e.g. passes visibility constraints), update type.this if necessary.
	 * Otherwise throw a semantic exception.  TODO: Ensure that visibility of
	 * this node is the same as that of the type argument.
	 */
	public Node typeCheck(TypeChecker tc) throws SemanticException {

		if (Report.should_report("debug", 5)) {
			Report.report(5,"[NullableNode_c] Type checking |" + this +"|:");
		}

		Node n = base.typeCheck(tc);

		if (Report.should_report("debug", 5)) {
			Report.report(5,"[NullableNode_c] ... yields node |" + n +"|.");
		}

		if (!(n instanceof TypeNode))
			throw new SemanticException("Argument to nullable type-constructor does not type-check",
					position());
		if (n == base)
			return this;
		TypeNode arg = (TypeNode) n;
		X10Type argType = (X10Type) arg.type();
		// [IP] This is incorrect -- non-reference types can also be nullable
//		if (!(argType instanceof ReferenceType))
//			throw new SemanticException("Argument to nullable type-constructor must be a reference type",
//					position());
		X10TypeSystem ts = (X10TypeSystem) argType.typeSystem();
		// [IP] FIXME: Why are we modifying this in-place?
		this.type = ts.createNullableType(position(), argType);

		if (Report.should_report("debug", 5)) {
			Report.report(5, "[NullableNode_c] ... sets type to |" + this.type + "|.");
			Report.report(5, "[NullableNode_c] ... returns |" + this + "|.");
		}

		return this;
	}

	public String toString() {
		return "nullable<" + base.toString() + ">";
	}

	/**
	 * Write out Java code for this node.
	 * Hmmm.. TODO: this will need to output the BoxedType for the primitive types,
	 * and just the reference type (unchanged) otherwise.
	 */
	public void prettyPrint(CodeWriter w, PrettyPrinter pp) {
		w.write("/*nullable*/");
		base.del().prettyPrint(w, pp);
	}

	// translate??
	// dump?
}

