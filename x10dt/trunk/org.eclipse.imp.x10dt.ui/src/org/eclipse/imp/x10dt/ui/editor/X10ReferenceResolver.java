package x10.uide.editor;


import org.eclipse.uide.editor.ReferenceResolver;
import org.eclipse.uide.parser.IParseController;

import polyglot.types.LocalInstance;
import polyglot.visit.NodeVisitor;

//import lpg.lpgjavaruntime.*;

//import x10.safari.parser.Ast.*;
import polyglot.ast.*;


public class X10ReferenceResolver extends ReferenceResolver {

	public X10ReferenceResolver () {
		super();

		// If you want to stipulate at construction time the
		// node types that are legal link source or target types,
		// you can do that here ...
		Class[] sourceTypes = new Class[4];
		sourceTypes[0] = TypeNode.class;
		sourceTypes[1] = Call.class;
		sourceTypes[2] = Field.class;
		sourceTypes[3] = Local.class;
		setSourceTypes(sourceTypes);
	}
	
	
	/**
	 * Get the target for a given source node in the AST represented by a
	 * given Parse Controller.
	 */
	public Object getLinkTarget(Object node, IParseController parseController)
	{
		if (node instanceof TypeNode) {
		    TypeNode typeNode= (TypeNode) node;
		    return typeNode.type();
		} else if (node instanceof Call) {
		    Call call= (Call) node;
		    Receiver rcvr= call.target();
		    return getLinkTarget(rcvr, parseController);
		} else if (node instanceof Field) {
		    Field field= (Field) node;
		    return field.type();
		} else if (node instanceof Local) {
		    Local local= (Local) node;
			Node varDefNode = findVarDefinition(local, (Node) parseController.getCurrentAst());
			return varDefNode;
		}
		return null;
	}
	
	
    public static Node findVarDefinition(Local local, Node ast) {
    	final LocalInstance li= local.localInstance();
    	final LocalDecl ld[]= new LocalDecl[1];

    	NodeVisitor finder= new NodeVisitor() {
    	    public NodeVisitor enter(Node n) {
    		if (n instanceof LocalDecl) {
    		    LocalDecl thisLD= (LocalDecl) n;

    		    if (thisLD.localInstance() == li)
    			ld[0]= thisLD;
    		}
    		return super.enter(n);
    	    }
    	};
    	ast.visit(finder);
    	return ld[0];
    }
    
	public boolean hasSuitableLinkSourceType(Object node) {
		if (node instanceof Ambiguous) {
			//System.out.println("hasSuitableLinkSourceType found Ambiguous node (returning false)");
			return false;
		}
		if (node instanceof TypeNode) {
			//System.out.println("hasSuitableLinkSourceType found TypeNode (returning true)");
		    return true;
		}
		if (node instanceof Call) {
			//System.out.println("hasSuitableLinkSourceType found Call (returning true)");
		    return true;
		}
		if (node instanceof Field) {
			//System.out.println("hasSuitableLinkSourceType found Field (returning true)");
		    return true;
		}
		if (node instanceof Local) {
			//System.out.println("hasSuitableLinkSourceType found Local (returning true)");
		    return true;
		}
		return false;
	}
    
		
	/**
	 * Get the text associated with a given node for use in a link
	 * from (or to) that node
	 */
	public String getLinkText(Object node) {
		// In the original, link text is determined by ISourceHyperlink,
		// and this is all that does
		return node.toString();
	}


}
