package x10.ast;

import java.io.OutputStream;
import java.io.Writer;
import java.util.List;

import polyglot.frontend.ExtensionInfo;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SubtypeSet;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.ExceptionChecker;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;
import x10.types.Context;
import x10.types.Qualifier;
import x10.types.Ref;
import x10.types.SemanticException;
import x10.types.Type;
import x10.types.TypeSystem;

public class HasTypeNode_c extends TypeNode_c implements TypeNode {

	TypeNode tn;
	public HasTypeNode_c(TypeNode tn) {
		super(tn.position());
		this.tn = tn;
		// TODO Auto-generated constructor stub
	}
	public TypeNode typeNode() { return tn;}
	public Type type() { return tn.type();}
	@Override
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
         tn.prettyPrint(w, tr);
	}
	
}
