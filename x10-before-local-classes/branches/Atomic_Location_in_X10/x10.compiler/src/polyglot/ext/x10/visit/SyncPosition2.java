package polyglot.ext.x10.visit;

import java.util.List;
import java.util.Hashtable;
import java.util.ArrayList;
import polyglot.ast.*;
import polyglot.ext.x10.ast.Async;
import polyglot.util.InternalCompilerError;
import polyglot.visit.NodeVisitor;

public class SyncPosition2 extends NodeVisitor {
	private List accesses;
	private Stmt position;
	private Hashtable below; 
	
	public SyncPosition2(List accesses){
		this.accesses = accesses;
		position = null;
		below = new Hashtable();
	}
	
	public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
		if(n instanceof Field){
			Field fd = (Field)n;
			if(accesses.contains(fd)){ // atomic set access
				if(below.containsKey(parent)){
					List list = (ArrayList)below.get(parent);
					list.add(fd);
					below.put(parent, list);
				}
				else{
					List list = new ArrayList();
					list.add(fd);
					below.put(parent, list);
				}
			}
			else if(fd.target() != null && !fd.isTargetImplicit()){
				if(fd.target() instanceof Field){
					if(accesses.contains(fd.target())){
						if(below.containsKey(parent)){
							List list = (ArrayList)below.get(parent);
							list.add(fd.target());
							below.put(parent, list);
						}
						else{
							List list = new ArrayList();
							list.add(fd.target());
							below.put(parent, list);
						}
					}
				}
			}
		}
		else if(below.containsKey(n)){ // atomic set access in sibling subtree
			List list = (ArrayList)below.get(n);
			if(list.size() == accesses.size()){
				if(n instanceof Stmt)
					position = (Stmt)n;
				else if(!below.containsKey(parent)){
					List plist = new ArrayList();
					plist.addAll(list);
					below.put(parent, plist);
				}
				else{
					List plist = (ArrayList)below.get(parent);
					plist.add(list);
					below.put(parent, plist);
				}		
			}
			else if(!below.containsKey(parent)){
				List plist = new ArrayList();
				plist.add(below.get(n));
				below.put(parent, plist);
			}
			else{
				List plist = (ArrayList)below.get(parent);
				plist.add(below.get(n));
				below.put(parent, plist);
			}
		}
		return n;
	}
		
	public Stmt position(){
		return position;
	}
}

