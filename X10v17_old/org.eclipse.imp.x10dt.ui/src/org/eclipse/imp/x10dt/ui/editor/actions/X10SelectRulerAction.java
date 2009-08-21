package org.eclipse.imp.x10dt.ui.editor.actions;

import java.util.ResourceBundle;

import org.eclipse.imp.x10dt.core.X10EditorMessages;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.texteditor.AbstractRulerActionDelegate;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.SelectAnnotationRulerAction;

public class X10SelectRulerAction extends AbstractRulerActionDelegate {
	@Override
	protected IAction createAction(ITextEditor editor, IVerticalRulerInfo rulerInfo) {
		return new X10SelectAnnotationRulerAction(X10EditorMessages.getBundleForConstructedKeys(), "X10SelectAnnotationRulerAction", editor, rulerInfo);
	}

}
