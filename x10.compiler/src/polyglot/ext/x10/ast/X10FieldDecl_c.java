package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.FieldDecl_c;
import polyglot.ext.x10.types.X10ReferenceType;
import polyglot.main.Report;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.TypeChecker;

public class X10FieldDecl_c extends FieldDecl_c {

	public X10FieldDecl_c(Position pos, Flags flags, TypeNode type,
						  String name, Expr init)
	{
		super(pos, flags, type, name, init);
	}

	public Node typeCheck(TypeChecker tc) throws SemanticException {
		Node result = super.typeCheck(tc);

		//
		// Any occurrence of a non-final static field in X10
		// should be reported as an error.
		// 
		if (flags().isStatic() && (!flags().isFinal())) {
			throw new SemanticException("Non-final static field is illegal in X10",
										this.position());
		}
		return result;
	}

	public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
		X10FieldDecl_c f = (X10FieldDecl_c) super.disambiguate(ar);
		// [IP] All fields in value types should be final
		// [IP] FIXME: this will produce an "assignment to final" message --
		//      is that good enough?
		if (f.flags().isFinal())
			return f;
		FieldInstance fi = f.fieldInstance();
        X10ReferenceType ref = (X10ReferenceType) fi.container();
       // Report.report(5, "[X10FieldDecl_c] disambiguate: " + fi + " " + ref + ref.getClass());
		if (!(ref.isValueType()))
			return f;
		fi.setFlags(fi.flags().Final());
		return f.flags(f.flags().Final());
	}
}

