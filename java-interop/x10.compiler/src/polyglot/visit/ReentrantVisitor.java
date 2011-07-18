package polyglot.visit;

import java.util.HashMap;
import java.util.Map;

import polyglot.ast.Node;
import polyglot.frontend.Job;
import polyglot.util.InternalCompilerError;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;

public class ReentrantVisitor extends NodeVisitor {
    protected Job job;
    protected Map<Node,Node> newSubst;
    
    public ReentrantVisitor(Job job) {
        this.job = job;
        this.newSubst = CollectionFactory.newHashMap();
    }

    public Job job() {
        return job;
    }
    
    
    @Override
    public Node visitEdge(Node parent, Node child) {
//        Map<Node,Node> subst = job.astMap();
//        Node n = subst.get(child);
//        
//        if (n != null) {
//            if (true)
//                return n;
//            child = n;
//        }
        
        Node n;
        
        try {
            n = override(parent, child);

            if (n == null) {
                n = visitEdgeNoOverride(parent, child);
            }

//            if (child != n)
//                installSubst(newSubst, child, n);
//
//            if (n == job.ast()) {
//                job.setAstMap(newSubst);
//            }
            
            return n;
        }
        catch (InternalCompilerError e) {
            if (e.position() == null && child != null)
                e.setPosition(child.position());
            throw e;
        }
    }

//    protected void installSubst(final Map<Node,Node> subst, Node old, Node n) {
//        // Remove the children from the substitution.
//        old.visitChildren(new NodeVisitor() {
//            public Node override(Node n) {
//                subst.remove(n);
//                return n;
//            }
//        });
//
//        subst.put(old, n);
//    }
}
