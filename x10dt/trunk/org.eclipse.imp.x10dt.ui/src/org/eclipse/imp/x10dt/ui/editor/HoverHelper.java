/*
 * Created on Feb 9, 2006
 */
package x10.uide.editor;

import java.util.List;

import lpg.lpgjavaruntime.LexStream;

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.uide.editor.IHoverHelper;
import org.eclipse.uide.parser.IParseController;

public class HoverHelper implements IHoverHelper {
    public String getHoverHelpAt(IParseController parseController, ISourceViewer srcViewer, int offset) {
	LexStream lexStream= parseController.getLexer().getLexStream();
	List/*<Annotation>*/ annotations= AnnotationHover.getJavaAnnotationsForLine(srcViewer, lexStream.getLineNumberOfCharAt(offset));

	return AnnotationHover.formatAnnotationList(annotations);
    }
}
