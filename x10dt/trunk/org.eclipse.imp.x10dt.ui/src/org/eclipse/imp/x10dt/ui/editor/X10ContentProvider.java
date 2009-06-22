/*
 * Created on Jul 20, 2006
 */
package x10.uide.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.uide.core.ILanguageService;
import org.eclipse.uide.editor.OutlineInformationControl;
import org.eclipse.uide.editor.OutlineInformationControl.OutlineContentProviderBase;

import polyglot.ast.ClassDecl;
import polyglot.ast.Node;
import polyglot.ast.SourceFile;
import polyglot.visit.NodeVisitor;

public class X10ContentProvider extends OutlineContentProviderBase implements ILanguageService {
    public X10ContentProvider() {
	this(null, false);
    }

    public X10ContentProvider(OutlineInformationControl oic, boolean showInheritedMembers) {
	super(oic, showInheritedMembers);
    }

    public Object getParent(Object element) {
	if (element instanceof Node) {
	    Node node= (Node) element;
	    final List<Node> children= childrenOf(node);

	    return children.toArray();
	}
	return null;
    }

    public Object[] getChildren(Object element) {
	return childrenOf((Node) element).toArray();
    }

    private List<Node> childrenOf(Node node) {
	OutlineVisitor v= new OutlineVisitor();

	node.visit(v);
	return v.getChildren();
    }
}

class OutlineVisitor extends NodeVisitor {
    private final List<Node> fChildren= new ArrayList<Node>();

    public List<Node> getChildren() { return fChildren; }

    public NodeVisitor enter(Node n) {
	if (n instanceof SourceFile) {
	    SourceFile file= (SourceFile) n;

	    fChildren.addAll(file.decls());
	} else if (n instanceof ClassDecl) {
	    ClassDecl cd= (ClassDecl) n;

	    fChildren.addAll(cd.body().members());
	}
	return this;
    }
}
