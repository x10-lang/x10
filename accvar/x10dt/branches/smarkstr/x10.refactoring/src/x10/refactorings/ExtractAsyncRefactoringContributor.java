package x10.refactorings;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.services.IRefactoringContributor;
import org.eclipse.jface.action.IAction;

public class ExtractAsyncRefactoringContributor implements IRefactoringContributor {
    public ExtractAsyncRefactoringContributor() { System.err.println("ExtractAsyncRefactoringContributor");}

    public IAction[] getEditorRefactoringActions(UniversalEditor editor) {
	return new IAction[] {
		new ExtractAsyncRefactoringAction(editor)
	};
    }
}
