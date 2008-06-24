package polyglot.ext.x10.ast;

import polyglot.ast.Assign;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Return_c;
import polyglot.types.CodeDef;
import polyglot.types.ConstructorDef;
import polyglot.types.Context;
import polyglot.types.FunctionDef;
import polyglot.types.InitializerDef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

public class X10Return_c extends Return_c {

	protected boolean implicit;

	public X10Return_c(Position pos, Expr expr, boolean implicit) {
		super(pos, expr);
		this.implicit = implicit;
	}

	@Override
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		TypeSystem ts = tc.typeSystem();
		Context c = tc.context();
	
		CodeDef ci = c.currentCode();

		// If the return type is not yet known, set it to the type of the value being returned.
		if (ci instanceof FunctionDef) {
		    FunctionDef fi = (FunctionDef) ci;
		    
		    Ref<Type> typeRef = (Ref<Type>) fi.returnType();
		    if (! typeRef.known()) {
			    if (expr == null) {
				    throw new SemanticException("Must return a value from " +
				                                fi + ".", position());
			    }
			    typeRef.update(expr.type());
		    }
		}
		
		if (ci instanceof FunctionDef) {
			FunctionDef fi = (FunctionDef) ci;
			if (fi.returnType().get().isVoid() && expr != null && implicit) {
				NodeFactory nf = tc.nodeFactory();
				if (expr instanceof Call || expr instanceof New || expr instanceof Assign)
					return nf.Block(position(), nf.Eval(expr.position(), expr), nf.Return(position()));
			}
		}
		
		return super.typeCheck(tc);
	}
}
