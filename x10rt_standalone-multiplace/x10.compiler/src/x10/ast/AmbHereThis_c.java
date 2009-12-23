package x10.ast;

import java.util.List;

import polyglot.ast.Expr_c;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ast.Term;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;

public class AmbHereThis_c extends Expr_c {

	public AmbHereThis_c(Position pos) {
		super(pos);
	}
	
	 /**
     * Visit this term in evaluation order.
     */
	@Override
    public List acceptCFG(CFGBuilder v, List succs) {
        return succs;
    }
   
	public Term firstChild() {
		return null;
	}
	
	/** Disambiguate the receiver. */
    public Node disambiguate(ContextVisitor ar) throws SemanticException {
    	X10NodeFactory nf = ((X10NodeFactory) ar.nodeFactory());
    	return (ar.context().inCode()) 
    	? nf.Here(position()) 
    	: nf.This(position());
    }

    public String toString() {
    	return "hereOrThis";
    }
}
