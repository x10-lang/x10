/*
 * Created on Jul 20, 2006
 */
package org.eclipse.imp.x10dt.ui.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.imp.editor.OutlineInformationControl;
import org.eclipse.imp.editor.OutlineInformationControl.OutlineContentProviderBase;
import org.eclipse.imp.language.ILanguageService;

import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.Node;
import polyglot.ast.SourceFile;
import polyglot.util.Position;
import polyglot.visit.HaltingVisitor;
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

class OutlineVisitor extends HaltingVisitor {
    private final List<Node> fChildren= new ArrayList<Node>();

    public List<Node> getChildren() { return fChildren; }

    public NodeVisitor enter(Node n) {
	if (n instanceof SourceFile) {
	    SourceFile file= (SourceFile) n;

	    fChildren.addAll(file.decls());
	    return bypassChildren(n);
	} else if (n instanceof ClassDecl) {
	    ClassDecl cd= (ClassDecl) n;

	    for(Iterator iter= cd.body().members().iterator(); iter.hasNext(); ) {
		ClassMember member= (ClassMember) iter.next();
		Position p= member.position();
		if (p.offset() != p.endOffset()) // crude way of detecting compiler-generated members
		    fChildren.add(member);
	    }
	    return bypassChildren(n);
	}
	return this;
    }
}
