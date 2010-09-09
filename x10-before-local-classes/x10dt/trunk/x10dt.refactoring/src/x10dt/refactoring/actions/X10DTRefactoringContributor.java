package x10dt.refactoring.actions;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.services.IRefactoringContributor;
import org.eclipse.jface.action.IAction;

public class X10DTRefactoringContributor implements IRefactoringContributor {
    public X10DTRefactoringContributor() {}

    public IAction[] getEditorRefactoringActions(UniversalEditor editor) {
        return new IAction[] {
//              new MoveRefactoringAction(editor),
                new RenameRefactoringAction(editor),
                new MarkContextAction(editor),
                new ExtractAsyncRefactoringAction(editor),
                new ExtractAsyncInFinishRefactoringAction(editor),
                new LoopFlatParallelizationRefactoringAction(editor) };
    }
}
