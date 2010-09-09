package polyglot.ext.x10.visit;

import java.util.Iterator;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;

import polyglot.ast.*;
import polyglot.util.InternalCompilerError;
import polyglot.visit.NodeVisitor;
import polyglot.ext.x10.ast.AtomicSetDecl;
import polyglot.ext.x10.ast.Async;

public class SyncDrive extends NodeVisitor {
	protected NodeFactory nf;
	Hashtable ht;
	
	public SyncDrive(NodeFactory nf){
		this.nf = nf;
		ht = null;
	}
	
    public Node visitEdge(Node parent, Node child) {
		Node n = child;
		if(child instanceof ClassBody){
			ClassBody classbody = (ClassBody)child;
			ht = new Hashtable();
			
			// Collect atomic sets declared in this class
			// Note: no scope -- no atomic sets declared in a method body
			for(Iterator i = classbody.members().iterator(); i.hasNext();){
				ClassMember m = (ClassMember) i.next();
				if(m instanceof AtomicSetDecl){
					AtomicSetDecl as = (AtomicSetDecl)m;
					String label = as.getlabel();
					String val = as.name();
					if(!ht.containsKey(val))
						ht.put(val, label);
				}			
			}
			if(ht.size() != 0){
				n = child.visitChildren(this);
			}
		}
		else if(child instanceof MethodDecl){
			MethodDecl method = (MethodDecl)child;
			if(!method.name().equals("main") &&
		    (method.flags().isPublic() || method.flags().isProtected())){
				UOWnoAsync uow = new UOWnoAsync();
				n = n.visitChildren(uow);
				if(uow.isUOW()) n = SyncUOW(n, false);
				else n = n.visitChildren(this);
			}
		}
		else if(child instanceof ConstructorDecl){
			ConstructorDecl cd = (ConstructorDecl)child;
			UOWnoAsync uow = new UOWnoAsync();
			n = n.visitChildren(uow);
			if(uow.isUOW()) n = SyncUOW(n, false);
			else n = n.visitChildren(this);
		}	

		
		else if(child instanceof Async){
			Async async = (Async)child;
			Stmt body = async.body();
			n = SyncUOW(body, true);
		}
		else {
			n = n.visitChildren(this);
		}
		return n;
    }
	
	private Node SyncUOW(Node uow, boolean inAsync){
		Node n = uow;
		
		if(inAsync){
			List newlist = new ArrayList();
			newlist.add(uow);
			Block newbody = nf.Block(uow.position(), newlist);
			n = newbody;
		}
		
		// find the atomic set accesses
		// NodeVisitor SyncAccess
		SyncAccess fieldaccess = new SyncAccess(ht);
		n = n.visitChildren(fieldaccess);
		Hashtable accesses = fieldaccess.accesses();
		
		// Iterate on each atomic set, traverse the AST to
		// find the atomic set access in each unit of work,
		// figure out the position to synchronize 
		// (NodeVisitor SyncPosition), and add the synchronization 
		// (NodeVisitor SyncGen).
		for(Enumeration e = accesses.keys(); e.hasMoreElements();){
			String label = (String)e.nextElement();
			SyncPosition2 pos = new SyncPosition2((ArrayList)accesses.get(label));
			n = n.visitChildren(pos);
			Stmt position = pos.position();
			
			SyncGen gen = new SyncGen(nf, label, position);
			n = n.visitChildren(gen);
		}
		return n;
	}
}

class UOWnoAsync extends NodeVisitor{
	boolean UOW;
	
	public UOWnoAsync(){
		UOW = true;
	}
	
	public NodeVisitor enter(Node parent, Node n) {
		if(n instanceof Async) UOW = false;
		return this;
	}
	
	public boolean isUOW() {return UOW;}
}