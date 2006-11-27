package polyglot.ext.x10.ast;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.AmbTypeNode;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.ClassBody;
import polyglot.ast.Expr;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.New_c;
import polyglot.ext.jl.parse.Name;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.LocalInstance;
import polyglot.types.MethodInstance;
import polyglot.types.ParsedClassType;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.TypeChecker;

/**
 * An allocation wrapper to rewrite array pointwiseOp construction.
 * @author Igor
 */
public class X10New_c extends New_c {
	public X10New_c(Position pos, Expr qualifier, TypeNode tn, List arguments, ClassBody body) {
		super(pos, qualifier, tn, arguments, body);
	}

	public Node typeCheckOverride(Node parent, TypeChecker tc) throws SemanticException {
		X10New_c n = (X10New_c) super.typeCheckOverride(parent, tc);
		return n;
	}

	/**
	 * Rewrite pointwiseOp construction to use Operator.Pointwise, otherwise
	 * leave alone.
	 */
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		X10New_c n = this;
		X10NodeFactory xnf = (X10NodeFactory) tc.nodeFactory();
		X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
		TypeNode oType = n.objectType();
		String opName = null;
		if (n.body != null && n.qualifier == null && oType instanceof CanonicalTypeNode) {
			ClassType type = oType.type().toClass();
			ClassType outer = null;
			if (type.isNested() && (outer = type.container().toClass()) != null && xts.isX10Array(outer)) {
				opName = type.name();
			}
		}
		if (opName != null) {
			// Special array operations
			Position nPos = oType.position();

			if (opName.equals("pointwiseOp"))
			{
				List members = n.body.members();
				MethodDecl apply = n.findMethod1Arg("apply", members);
				MethodInstance mi = apply.methodInstance();
				TypeNode appResultType = apply.returnType();
				List formals = apply.formals();
				assert (!formals.isEmpty());
				List l1 = TypedList.copy(formals, X10Formal.class, false);
				X10Formal firstArg = (X10Formal) formals.get(0);
				Position pos = firstArg.position();
				LocalInstance li = firstArg.localInstance();
				li = li.flags(Flags.FINAL).type(mi.returnType()).name("_");
				l1.add(((X10Formal) xnf.Formal(pos, Flags.FINAL, appResultType, "_").localInstance(li)).localInstances(X10Formal_c.NO_LOCALS));
				ReferenceType tOperatorPointwise = xts.OperatorPointwise();
				List l2 = TypedList.copy(mi.formalTypes(), Type.class, false);
				l2.add(mi.returnType());
				mi = mi.container(tOperatorPointwise).formalTypes(l2);
				MethodDecl decl = apply.formals(l1).methodInstance(mi);
				//  new ClassOrInterfaceType ( ArgumentListopt ) ClassBodyopt
				List classDecl = new TypedList(new LinkedList(), MethodDecl.class, false);
				for (int i = 0; i < members.size(); i++) {
					Object m = members.get(i);
					if (m != apply)
						classDecl.add(m);
					else
						classDecl.add(decl);
				}
				CanonicalTypeNode t = xnf.CanonicalTypeNode(nPos, tOperatorPointwise);
				ParsedClassType anon = n.anonType();
				// FIXME: this is probably breaking some assumptions in the typesystem
				anon.superType(tOperatorPointwise);
				return n.objectType(t).body(n.body().members(classDecl)).typeCheckOverride(null, tc);
			}
			else if (opName.equals("binaryOp"))
			{
				TypeNode t = xnf.CanonicalTypeNode(nPos, xts.OperatorBinary());
				return n.objectType(t).typeCheckOverride(null, tc);
			}
			else if (opName.equals("unaryOp"))
			{
				TypeNode t = xnf.CanonicalTypeNode(nPos, xts.OperatorUnary());
				return n.objectType(t).typeCheckOverride(null, tc);
			}
		}

		return super.typeCheck(tc);
	}

	private MethodDecl findMethod1Arg(String name, List members) {
		for (Iterator i = members.iterator(); i.hasNext();) {
			MethodDecl m = (MethodDecl) i.next();
			if (m.name().equals(name))
				return m;
		}
		return null;
	}
}

