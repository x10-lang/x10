/**
 * 
 */
package x10.visit;

import java.util.Iterator;
import java.util.List;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.frontend.Job;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.AnnotationNode;
import x10.ast.Closure;
import x10.ast.X10AmbTypeNode_c;
import x10.ast.X10ConstructorDecl;
import x10.ast.X10MethodDecl;
import x10.extension.X10Ext;

/**
 * Set the flag inferGuard associated to a method, constructor and closure
 * definition to true if it is marked with the annotation @InferGuard.
 *
 * @author Louis Mandel
 * @author Olivier Tardieu
 *
 */
public class InferGuardVisitor extends ContextVisitor {

	public InferGuardVisitor(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
	}

    @Override
	public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) {
        if (! (n.ext() instanceof X10Ext)) {
            return n;
        }

        List<AnnotationNode> annotations = ((X10Ext) n.ext()).annotations();

        for (Iterator<AnnotationNode> i = annotations.iterator(); i.hasNext(); ) {
        	AnnotationNode a = i.next();
        	TypeNode tn = a.annotationType();
        	if (!(tn instanceof X10AmbTypeNode_c)) {
        		continue;
        	}
        	X10AmbTypeNode_c at = (X10AmbTypeNode_c) tn;
        	if (!"InferGuard".equals(at.name().toString())) { // TODO imprecise
        		continue;
        	}
        	if (n instanceof X10MethodDecl) {
        		X10MethodDecl mdecl = (X10MethodDecl) n;
        		mdecl.methodDef().inferGuard(true);
        	}
        	else if (n instanceof X10ConstructorDecl) {
        		X10ConstructorDecl cdecl = (X10ConstructorDecl) n;
        		cdecl.constructorDef().inferGuard(true);
        	}
        	else if (n instanceof Closure) {
        		Closure cldecl = (Closure) n;
        		cldecl.closureDef().inferGuard(true);
        	}
        }

        return n;
    }

}
