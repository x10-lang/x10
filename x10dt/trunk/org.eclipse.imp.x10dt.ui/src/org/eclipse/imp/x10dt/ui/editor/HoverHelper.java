/*
 * Created on Feb 9, 2006
 */
package x10.uide.editor;

import java.util.List;

import lpg.runtime.LexStream;

import org.eclipse.jdt.internal.ui.text.HTMLPrinter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.uide.editor.IHoverHelper;
import org.eclipse.uide.editor.IReferenceResolver;
import org.eclipse.uide.parser.IASTNodeLocator;
import org.eclipse.uide.parser.IParseController;
import polyglot.ast.Node;

public class HoverHelper implements IHoverHelper {
    public String getHoverHelpAt(IParseController parseController, ISourceViewer srcViewer, int offset) {
	try {
	    List/*<Annotation>*/ annotations= AnnotationHover.getJavaAnnotationsForLine(srcViewer, srcViewer.getDocument().getLineOfOffset(offset));

	    if (annotations != null && annotations.size() > 0)
		return AnnotationHover.formatAnnotationList(annotations);
	} catch (BadLocationException e) {
	    return "???";
	}

    	IReferenceResolver linkMapper = new X10ReferenceResolver();
    	
    	// Get stuff for getting link source node
        Object root= parseController.getCurrentAst();
        IASTNodeLocator nodeLocator = parseController.getNodeLocator();

        if (root == null) return null;

        Object selNode = nodeLocator.findNode(root, offset);

        if (selNode == null) return null;
//      System.out.println("Selected node: " + selNode);

       	final Object target = linkMapper.getLinkTarget(selNode, parseController);

       	if (target == null) return null;

	StringBuffer buffer= new StringBuffer();

	// This is what the JDT hover helper does, essentially; looks nice
	HTMLPrinter.addSmallHeader(buffer, target.toString());
       	return buffer.toString();
    }
}
