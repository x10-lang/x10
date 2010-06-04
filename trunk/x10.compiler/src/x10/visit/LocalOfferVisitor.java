package x10.visit;

import java.util.ArrayList;
import java.util.Collections;

import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Expr;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.types.LocalDef;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import x10.ast.Offer;
import x10.ast.X10NodeFactory;
import x10.types.X10Context;
import x10.types.X10TypeSystem;
import x10.util.Synthesizer;

/**
 * @author Li Yan
 * 
 * This class is used to handle offer instance with in finish(R) body.
 *
 */
public class LocalOfferVisitor extends NodeVisitor {
    protected Expr lExpr;
    protected X10Context context;
    private final X10TypeSystem xts;
    private final X10NodeFactory xnf;
    private final Synthesizer synth;
    
    public LocalOfferVisitor(Expr lExpr, X10Context context,X10TypeSystem xts,X10NodeFactory xnf,Synthesizer synth){
        this.lExpr = lExpr;
        this.context = context;
        this.xts = xts;
        this.xnf = xnf;
        this.synth = synth;
    }
    
    public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
 
 //offer e ->
 //    finishR.offer(e);   	
    	if (n instanceof Offer)
			try {
				return visitOffer((Offer) n);
			} catch (SemanticException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}          
        return n;
    }

	private Stmt visitOffer(Offer n) throws SemanticException {
		
    	Position pos = n.position();
    	Expr offerTarget = n.expr();
    	Type reducerTarget = offerTarget.type();    	 
    	Expr instance = lExpr;
   	    	
    	Name OFFER = Name.make("offer");    	    	
    	Type coFinishT = instance.type();
    	
    	Call instanceCall = synth.makeInstanceCall(pos, instance, OFFER, Collections.EMPTY_LIST, Collections.singletonList(offerTarget), xts.Void() , Collections.singletonList(reducerTarget), context);
    	
    	Stmt offercall = xnf.Eval(pos, instanceCall);     	
    	return offercall;		 
		 
	}

	
    
}
