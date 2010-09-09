/**
 * 
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.ClassBody;
import polyglot.ast.ConstructorCall;
import polyglot.ast.ConstructorCall_c;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.X10ConstructorDef;
import polyglot.ext.x10.types.X10ConstructorInstance;
import polyglot.ext.x10.types.X10MethodInstance;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.types.ConstructorDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.Context;
import polyglot.types.ErrorRef_c;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import x10.constraint.XConstraint;

/**
 * A call to this(...) or super(...) in the body of a constructor.
 * (The call new C(...) is represented by an X10New_c.)
 * @author vj
 *
 */
public class X10ConstructorCall_c extends ConstructorCall_c implements X10ConstructorCall {

	/**
	 * @param pos
	 * @param kind
	 * @param qualifier
	 * @param arguments
	 */
	public X10ConstructorCall_c(Position pos, Kind kind, Expr qualifier,
		List<TypeNode> typeArguments, List<Expr> arguments) {
		super(pos, kind, qualifier, arguments);
		this.typeArguments = typeArguments;
		
	}
	
	// Override to remove reference to ts.Object(), which will cause resolver loop.
	@Override
	public Node buildTypes(TypeBuilder tb) throws SemanticException {
	    TypeSystem ts = tb.typeSystem();

	    // Remove super() calls for java.lang.Object.
	    if (kind == SUPER && tb.currentClass().fullName().equals(QName.make("x10.lang.Ref"))) {
		return tb.nodeFactory().Empty(position());
	    }
	    if (kind == SUPER && tb.currentClass().fullName().equals(QName.make("x10.lang.Value"))) {
		return tb.nodeFactory().Empty(position());
	    }

	    ConstructorCall_c n = this;

	    ConstructorInstance ci = ts.createConstructorInstance(position(), new ErrorRef_c<ConstructorDef>(ts, position(), "Cannot get ConstructorDef before type-checking constructor call."));
	    return n.constructorInstance(ci);
	}
	
	@Override
	public Node visitChildren(NodeVisitor v) {
		Expr qualifier = (Expr) visitChild(this.qualifier, v);
		List<TypeNode> typeArguments = visitList(this.typeArguments, v);
		List arguments = visitList(this.arguments, v);
		X10ConstructorCall_c n = (X10ConstructorCall_c) typeArguments(typeArguments);
		return n.reconstruct(qualifier, arguments);
	}
	
	    List<TypeNode> typeArguments;
	    public List<TypeNode> typeArguments() { return typeArguments; }
	    public X10ConstructorCall typeArguments(List<TypeNode> args) {
		    X10ConstructorCall_c n = (X10ConstructorCall_c) copy();
		    n.typeArguments = new ArrayList<TypeNode>(args);
		    return n;
	    }
	    

	public Node typeCheck(ContextVisitor tc) throws SemanticException {
		X10ConstructorCall_c n = (X10ConstructorCall_c) super.typeCheck(tc);
		if (kind().equals(ConstructorCall.SUPER)) {
			Context ctx = tc.context();
			if (! (ctx.inCode()) || ! (ctx.currentCode() instanceof X10ConstructorDef))
				throw new SemanticException("A call to super must occur only in the body of a constructor.",
						position());
			
			//	The constructorinstance for this super call.
			
			X10ConstructorInstance ci = (X10ConstructorInstance) n.constructorInstance();
//			Type type = ci.returnType();
//			
//			
//			// Now construct from this generic return type the instance obtained by substituting 
//			// the actual parameters for the formals.
//			Type retType = X10New_c.instantiateType(ci, type, typeArguments, arguments);
//			XConstraint c = X10TypeMixin.realX(retType);
//			
//		    	if (ci.guard() != null) {
//		    		XConstraint guard = X10New_c.instantiateConstraint(ci, ci.guard(), null, typeArguments, arguments);
//		    		ci = (X10ConstructorInstance) ci.guard(guard);
//		    	}
			
			// The constructor *within which this super call happens*.
			X10ConstructorDef thisConstructor = (X10ConstructorDef) ctx.currentCode();
			XConstraint c = X10TypeMixin.realX(ci.returnType());
			thisConstructor.setSupClause(Types.ref(c));
		}
	
		return n;
	}
	 public String toString() {
			return (qualifier != null ? qualifier + "." : "") + kind + arguments;
		    }

}
