package polyglot.ext.x10.visit;


import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ext.x10.extension.X10Ext;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.ast.Call;
import polyglot.ext.x10.types.X10Type;
import polyglot.types.PrimitiveType;

/**
 * Visitor that inserts boxing and unboxing code into the AST.
 */
public class X10Boxer extends AscriptionVisitor
{
    public X10Boxer(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }

    public Expr ascribe(Expr e, Type toType) {
        Type fromType = e.type();

        if (toType == null) {
            return e;
        }

        Position p = e.position();

        
        
        if (e instanceof Call) {
            // for calls to method force() on Objects of type Future, 
            // make sure that the corresponding cast occurs (from Object 
            // to the primitive type),
        
            Call call_n = (Call) e;
            String m_name = call_n.name();
        	X10Type target_t = (X10Type) call_n.target().type();
        	if (m_name.equals("force") && target_t.isFuture()) {
        	    if (fromType.isPrimitive()) {
        	        Type boxed_t = ((X10TypeSystem) ts).boxedType((PrimitiveType) fromType);
            	    call_n = (Call) call_n.type(boxed_t);
            	    return nf.Cast(p, nf.CanonicalTypeNode(p, fromType), call_n);
        	    } else {
        	       return nf.Cast(p, nf.CanonicalTypeNode(p, fromType), call_n);
        	    }
        	}
        }
        
        
        // Insert a cast.  Translation of the cast will insert the
        // correct boxing/unboxing code.
        if (fromType.isPrimitive() && toType.isReference()) 
                return nf.Cast(p, nf.CanonicalTypeNode(p, toType), e);
  
        return e;
    }

    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        n = super.leaveCall(old, n, v);

        if (n.ext() instanceof X10Ext) {
            return ((X10Ext) n.ext()).rewrite((X10TypeSystem) typeSystem(),
                                              nodeFactory());
        }

        return n;
    }
}
