package x10dt.ui.quickfix;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.imp.editor.quickfix.CUCorrectionProposal;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.runtime.PluginImages;
import org.eclipse.imp.services.IQuickFixInvocationContext;
import org.eclipse.imp.utils.FormatUtils;
import org.eclipse.imp.utils.NullMessageHandler;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;

import polyglot.ast.ClassDecl;
import polyglot.ast.FlagsNode;
import polyglot.ast.Node;
import x10dt.ui.parser.PolyglotNodeLocator;

public class MakeAbstractProposal extends CUCorrectionProposal {
	IQuickFixInvocationContext context;
	Object root;
	Node coveringNode;
	PolyglotNodeLocator pnl;

	public MakeAbstractProposal(IQuickFixInvocationContext context,
			String name) {
		super("Make type '" + name + "' abstract", context.getModel(), 8, null);
		this.context = context;
		setImage(PluginImages.get(PluginImages.IMG_CORRECTION_CHANGE));
	}

	@Override
	protected void addEdits(IDocument document, TextEdit editRoot)
			throws CoreException {
		
		root = getCompilationUnit().getAST(new NullMessageHandler(),
				new NullProgressMonitor());
		pnl = new PolyglotNodeLocator(getCompilationUnit().getProject(), null);
		coveringNode = (Node) pnl.findNode(root, context.getOffset());

		int offset = 0;
		if (coveringNode instanceof ClassDecl) {
			MultiTextEdit edit = new MultiTextEdit();
			ClassDecl classDecl = (ClassDecl) coveringNode;
			offset = classDecl.position().offset();

			edit.addChild(new InsertEdit(offset, "abstract "));
			editRoot.addChild(edit);
		}

		else if (coveringNode instanceof FlagsNode) {
			MultiTextEdit edit = new MultiTextEdit();
			offset = coveringNode.position().endOffset() + 1;

			edit.addChild(new InsertEdit(offset, " abstract"));
			editRoot.addChild(edit);
		}
		
		FormatUtils.format(LanguageRegistry.findLanguage("X10"), document, new Region(offset, editRoot.getLength()));
	}
}
