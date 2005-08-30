package polyglot.ext.x10.visit;

import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import polyglot.ast.*;
import polyglot.util.InternalCompilerError;
import polyglot.visit.NodeVisitor;
import polyglot.ext.x10.ast.Async;

public class SyncAccess extends NodeVisitor {
	private Hashtable ht;
	private Hashtable accesses;
	
	public SyncAccess(Hashtable ht){
		this.ht = ht;
		accesses = new Hashtable();
	}
	
	public Node leave(Node old, Node n, NodeVisitor v) {
		if(n instanceof Field){
			Field fd = (Field)n;
			if(fd.target() != null && !fd.isTargetImplicit()){ 
				String tname = null;
				if(fd.target() instanceof Field){
					Field targetFd = (Field)fd.target();
					tname = targetFd.name();
				}
				//else
					//TODO: fix this case
				if(tname != null && ht.containsKey(tname)){ 
					// atomic set access
					String label = (String)ht.get(tname);
					if(!accesses.containsKey(label)){
						List list = new ArrayList();
						list.add(fd);
						accesses.put(label, list);
					}
					else {
						List list = (ArrayList)accesses.get(label);
						if(!list.contains(fd.target())) list.add(fd.target());
						accesses.put(label, list);
					}
				}
			}
			else{
				String fdname = fd.name();
				if(ht.containsKey(fdname)){ 
					// atomic set access
					String label = (String)ht.get(fdname);
					if(!accesses.containsKey(label)){
						List list = new ArrayList();
						list.add(fd);
						accesses.put(label, list);
					}
					else {
						List list = (ArrayList)accesses.get(label);
						if(!list.contains(fd)) list.add(fd);
						accesses.put(label, list);
					}
				}
			}
		}
		return n;
	}

	public Hashtable accesses(){
		return accesses;
	}
}
