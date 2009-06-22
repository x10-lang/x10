package x10.refactorings;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.services.IRefactoringContributor;
import org.eclipse.jface.action.IAction;

import x10.refactorings.actions.ExtractAsyncRefactoringAction;
import x10.refactorings.actions.LoopFlatParallelizationRefactoringAction;

public class X10RefactoringContributor implements IRefactoringContributor {
    public X10RefactoringContributor() { }

    public IAction[] getEditorRefactoringActions(UniversalEditor editor) {
	return new IAction[] {
//		new ExtractAsyncRefactoringAction(editor),
		new LoopFlatParallelizationRefactoringAction(editor)
	};
    }
}
