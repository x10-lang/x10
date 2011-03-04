package x10dt.ui.quickfix;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.imp.editor.quickfix.CUCorrectionProposal;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.ICompilationUnit;
import org.eclipse.imp.runtime.PluginImages;
import org.eclipse.imp.services.IQuickFixInvocationContext;
import org.eclipse.imp.utils.FormatUtils;
import org.eclipse.imp.utils.NullMessageHandler;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;

import polyglot.ast.Throw;
import x10dt.ui.parser.PolyglotNodeLocator;

public class SurroundThrowProposal extends CUCorrectionProposal {
	IQuickFixInvocationContext context;

	public SurroundThrowProposal(IQuickFixInvocationContext context) {
		super("Surround with try/catch", context.getModel(), 8, null);
		this.context = context;
		setImage(PluginImages.get(PluginImages.IMG_CORRECTION_ADD));
	}

	@Override
	protected void addEdits(IDocument document, TextEdit editRoot)
			throws CoreException {
		ICompilationUnit cu = this.getCompilationUnit();
		Object root = cu.getAST(new NullMessageHandler(),
				new NullProgressMonitor());

		PolyglotNodeLocator pnl = new PolyglotNodeLocator(cu.getProject(), null);

		Throw th = (Throw) pnl.findNode(root, context.getOffset());
		MultiTextEdit edit = new MultiTextEdit();

		String lineDelim = TextUtilities.getDefaultLineDelimiter(document);
		edit.addChild(new InsertEdit(th.position().offset(), "try {"
				+ lineDelim));
		edit.addChild(new InsertEdit(th.position().endOffset() + 1, lineDelim
				+ "}" + lineDelim + "catch(" + th.expr().firstChild() + ") {"
				+ lineDelim + "// TODO Auto-generated catch block" + lineDelim
				+ "}"));
		
		editRoot.addChild(edit);
		int length = th.position().endOffset() + 1 + edit.getLength();
		FormatUtils.format(LanguageRegistry.findLanguage("X10"), document, new Region(th.position().offset(), length));
	}
}
