/*
 * Created on Feb 8, 2006
 */
package x10.uide.editor;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.uide.core.ILanguageService;
import org.eclipse.uide.editor.ISourceHyperlinkDetector;
import org.eclipse.uide.parser.IASTNodeLocator;
import org.eclipse.uide.parser.IParseController;

import polyglot.ast.Ambiguous;
import polyglot.ast.Call;
import polyglot.ast.ClassDecl;
import polyglot.ast.Field;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ast.TypeNode;
import polyglot.types.LocalInstance;
import polyglot.types.Type;
import polyglot.visit.NodeVisitor;
import x10.uide.parser.ParseController;
import x10.uide.parser.PolyglotNodeLocator;

public class HyperlinkDetector implements ISourceHyperlinkDetector, ILanguageService {
    ParseController fParseController;

    private final static boolean sDebugLink= false;

    public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, IParseController parseController) {
	fParseController= (ParseController) parseController;
	Node ast= (Node) fParseController.getCurrentAst();
	if (ast == null) return new IHyperlink[0];
	// fParseController.getNodeLocator() should do what the following does...
	IASTNodeLocator locator= new PolyglotNodeLocator(fParseController.getLexer().getLexStream());
	final Node node= (Node) locator.findNode(ast, region.getOffset());

	if (!isBoundReference(node))
	    return sDebugLink ? dummyHyperlink(textViewer, region, node) : null;
	System.out.println("Creating hyperlink for " + node);
	return createDefHyperlink(node, region, textViewer);
    }

    private IHyperlink[] createDefHyperlink(Node node, IRegion region, ITextViewer textViewer) {
	if (node instanceof TypeNode) {
	    TypeNode typeNode= (TypeNode) node;

	    return createHyperlinkToType(typeNode.type(), region, textViewer);
	} else if (node instanceof Call) {
	    Call call= (Call) node;
	    Receiver rcvr= call.target();

	    return createDefHyperlink(rcvr, region, textViewer);
	} else if (node instanceof Field) {
	    Field field= (Field) node;

	    return createHyperlinkToType(field.type(), region, textViewer);
	} else if (node instanceof Local) {
	    Local local= (Local) node;

	    return createHyperlinkToVariable(local, region, textViewer);
	}
	return null;
    }

    private IHyperlink[] createHyperlinkToVariable(Local local, IRegion region, ITextViewer textViewer) {
	Node node= findVarDefinition(local, (Node) fParseController.getCurrentAst());

	if (node != null)
	    return new IHyperlink[] { new SourceHyperlink(textViewer, region, node) };
	else
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

    private IHyperlink[] createHyperlinkToType(Type type, IRegion region, ITextViewer textViewer) {
	Node node= findTypeDefinition(type, (Node) fParseController.getCurrentAst());

	if (node != null)
	    return new IHyperlink[] { new SourceHyperlink(textViewer, region, node) };
	else
	    return null;
    }

    public static Node findTypeDefinition(final Type type, Node ast) {
	final ClassDecl cd[]= new ClassDecl[1];

	NodeVisitor finder= new NodeVisitor() {
	    public NodeVisitor enter(Node n) {
		if (n instanceof ClassDecl) {
		    ClassDecl thisCD= (ClassDecl) n;

		    if (thisCD.type() == type)
			cd[0]= thisCD;
		}
		return super.enter(n);
	    }
	};
	ast.visit(finder);
	return cd[0];
    }

    private boolean isBoundReference(final Node node) {
	if (node instanceof Ambiguous)
	    return false;
	if (node instanceof TypeNode)
	    return true;
	if (node instanceof Call)
	    return true;
	if (node instanceof Field)
	    return true;
	if (node instanceof Local)
	    return true;
	return false;
    }

    private IHyperlink[] dummyHyperlink(final ITextViewer textViewer, final IRegion region, final Node node) {
	return new IHyperlink[] { new SourceHyperlink(textViewer, region, node) };
    }
}
