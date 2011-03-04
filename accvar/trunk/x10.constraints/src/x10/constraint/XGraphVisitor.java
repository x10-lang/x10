package x10.constraint;

/**
 * An XGraphVisitor may visit the graph underlying a representation of 
 * a constraint. The visitor will see the atomic formulas a
 * and the constraints s==t and s!=t stored in the graph in
 * some order. The visitor may return false from any visit
 * method to terminate further traversal of the graph.
 
 * @author vj
 *
 */
public interface XGraphVisitor {

    /**
     * Visiting the graph encounters a formula t.  
     * Process this information.
     * Return false if the visit should be aborted.
     * @param t -- the formula encountered.
     * @return false -- the visit should be terminated.
     */
    boolean visitAtomicFormula(XTerm t);
    
    /**
     * Visiting the graph encounters t1 == t2. 
     * Process this information.
     * Return false if the visit should be aborted.
     * @param t1 --  
     * @param t2 --  
     * @return false -- the visit should be terminated.
     */
    boolean visitEquals(XTerm t1, XTerm t2);
    
    /**
     * Visiting the graph encounters t1 != t2. 
     * Process this information.
     * Return false if the visit should be aborted.
     * @param t -- the formula encountered.
     * @return false -- the visit should be terminated.
     */
    boolean visitDisEquals(XTerm t1, XTerm t2);
    
   
}
