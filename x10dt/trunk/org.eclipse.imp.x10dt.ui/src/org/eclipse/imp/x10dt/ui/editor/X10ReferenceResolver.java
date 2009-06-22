package x10.uide.editor;

import java.util.List;
import org.eclipse.uide.core.ILanguageService;
import org.eclipse.uide.editor.IReferenceResolver;
import org.eclipse.uide.editor.ReferenceResolver;
import org.eclipse.uide.parser.IParseController;
import polyglot.ast.Ambiguous;
import polyglot.ast.Call;
import polyglot.ast.Field;
import polyglot.ast.Id;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ast.Special;
import polyglot.ast.TypeNode;
import polyglot.types.Declaration;
import polyglot.types.FieldInstance;
import polyglot.types.LocalInstance;
import polyglot.types.MethodInstance;
import polyglot.types.ReferenceType;
import polyglot.visit.NodeVisitor;
import x10.uide.parser.PolyglotNodeLocator;

public class X10ReferenceResolver implements IReferenceResolver, ILanguageService {
    /**
     * Get the target for a given referencing source node in the AST represented
     * by a given ParseController.
     */
    public Object getLinkTarget(Object node, IParseController parseController) {
	if (node instanceof Ambiguous) {
	    return null;
	}
	if (node instanceof Id) {
	    Id id= (Id) node;
	    node= findParent(id, parseController);
	}
	if (node instanceof TypeNode) {
	    TypeNode typeNode= (TypeNode) node;
	    return typeNode.type();
	} else if (node instanceof Call) {
	    Call call= (Call) node;
	    MethodInstance mi= call.methodInstance();
	    if (mi != null)
		return mi.declaration();
	} else if (node instanceof Field) {
	    Field field= (Field) node;
	    FieldInstance fi= field.fieldInstance();
	    if (fi != null)
		return fi.declaration();
	} else if (node instanceof Local) {
	    Local local= (Local) node;
	    LocalInstance li= local.localInstance();
	    if (li != null)
		return li.declaration();
	}
	return node; // If it's not something we know how to resolve, just return the node itself
    }

    private Object findParent(Id id, IParseController parseController) {
	PolyglotNodeLocator locator= (PolyglotNodeLocator) parseController.getNodeLocator();

	return locator.findParentNode(parseController.getCurrentAst(), id.position().offset(), id.position().endOffset());
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
