/*
 * Created on Feb 8, 2006
 */
package x10.uide.editor;

import java.util.List;

import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.uide.core.ILanguageService;
import org.eclipse.uide.editor.AnnotationHoverBase;

public class AnnotationHover extends AnnotationHoverBase implements IAnnotationHover, ILanguageService {
    /**
     * @see IVerticalRulerHover#getHoverInfo(ISourceViewer, int)
     */
    public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
	List javaAnnotations= getJavaAnnotationsForLine(sourceViewer, lineNumber);

	return formatAnnotationList(javaAnnotations);
    }
}
