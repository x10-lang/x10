package x10dt.refactoring.actions;

import java.util.Iterator;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

public class MarkContextAction extends TextEditorAction {
    // N.B. Following must match the ID of the annotationType extension defined in plugin.xml
    private static final String CONTEXT_ANNOTATION_TYPE= "x10dt.refactoring.contextAnnotation";

    public MarkContextAction(ITextEditor editor) {
        super(X10RefactoringMessages.ResBundle, "markContext.", editor);
    }

    @Override
    public void run() {
        ITextEditor textEditor= getTextEditor();
        IAnnotationModel annotationModel= getAnnotationModel(textEditor);
        ITextSelection textSel= (ITextSelection) textEditor.getSelectionProvider().getSelection();

        removeCurrentAnnotation(annotationModel);
        createNewContextAnnotation(textSel, annotationModel);
    }

    private static IAnnotationModel getAnnotationModel(ITextEditor editor) {
        IAnnotationModel annotationModel= editor.getDocumentProvider().getAnnotationModel(editor.getEditorInput());
        return annotationModel;
    }

    private static void removeCurrentAnnotation(IAnnotationModel annotationModel) {
        Annotation currAnnotation= findCurrentAnnotation(annotationModel);

        if (currAnnotation != null) {
            annotationModel.removeAnnotation(currAnnotation);
        }
    }

    private static Annotation findCurrentAnnotation(IAnnotationModel annotationModel) {
        @SuppressWarnings("unchecked") Iterator annoIter= annotationModel.getAnnotationIterator();
        for(; annoIter.hasNext(); ) {
            Annotation ann= (Annotation) annoIter.next();

            if (ann.getType().equals(CONTEXT_ANNOTATION_TYPE)) {
                return ann;
            }
        }
        return null;
    }

    private void createNewContextAnnotation(ITextSelection textSel, IAnnotationModel annotationModel) {
        Annotation annotation= new Annotation(CONTEXT_ANNOTATION_TYPE, false, "Finish context for Extract Async refactoring");
        Position pos= new Position(textSel.getOffset(), textSel.getLength());

        annotationModel.addAnnotation(annotation, pos);
    }

    public static void clearCurrentContext(ITextEditor editor) {
        IAnnotationModel annotationModel= getAnnotationModel(editor);

        removeCurrentAnnotation(annotationModel);
    }

    public static Position getCurrentContext(ITextEditor editor) {
        IAnnotationModel annotationModel= getAnnotationModel(editor);
        Annotation currAnnotation= findCurrentAnnotation(annotationModel);

        if (currAnnotation != null) {
            Position pos= annotationModel.getPosition(currAnnotation);
            return pos;
        }
        return null;
    }
}
