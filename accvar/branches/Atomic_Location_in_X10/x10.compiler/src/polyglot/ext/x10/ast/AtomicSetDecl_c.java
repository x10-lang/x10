package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.FieldDecl_c;
import polyglot.types.Flags;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.PrettyPrinter;

public class AtomicSetDecl_c extends FieldDecl_c implements AtomicSetDecl {

	String label;
	
	public AtomicSetDecl_c(Position pos, Flags flags, TypeNode type,
            String name, Expr init, String label){
		super(pos, flags, type, name, init);
		this.label = label;
	}
	
	public String getlabel(){
		return this.label;
	}
	
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		super.prettyPrint(w, tr);
		w.write("//atomic set - " + label);
	}
		
}
