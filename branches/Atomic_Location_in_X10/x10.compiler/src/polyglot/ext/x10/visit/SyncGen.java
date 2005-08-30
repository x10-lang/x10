package polyglot.ext.x10.visit;

import java.util.ArrayList;
import java.util.List;
import polyglot.ast.*;
import polyglot.util.InternalCompilerError;
import polyglot.visit.NodeVisitor;

public class SyncGen extends NodeVisitor {
	protected NodeFactory nf;
	String label;
	Stmt position;
	boolean finished;

	public SyncGen(NodeFactory nf, String label, Stmt position){
		this.nf = nf;
		this.label = label;
		this.position = position;
		finished = false;
	}

	public Node leave(Node old, Node n, NodeVisitor v) {
		if(finished) return n;
		if(n instanceof Stmt){
			Stmt stmt = (Stmt)n;
			if(stmt == position){
				Expr lock = nf.AmbExpr(stmt.position(), "lock_"+label);
				List list = new ArrayList();
				list.add(stmt);
				Block block = nf.Block(stmt.position(), list);
				Stmt syncStmt = nf.Synchronized(stmt.position(), lock, block);
				finished = true;
				
				// This is necessary if the whole method(constructor)
				// body is replaced
				List newlist = new ArrayList();
				newlist.add(syncStmt);
				Block newbody = nf.Block(stmt.position(), newlist);
				return newbody;
			}
		}
		return n;
	}
}
