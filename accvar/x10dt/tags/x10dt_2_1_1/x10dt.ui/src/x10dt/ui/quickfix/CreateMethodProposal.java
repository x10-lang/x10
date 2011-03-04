package x10dt.ui.quickfix;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.imp.editor.quickfix.CUCorrectionProposal;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.services.IQuickFixInvocationContext;
import org.eclipse.imp.utils.FormatUtils;
import org.eclipse.imp.utils.NullMessageHandler;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;

import polyglot.ast.Block;
import polyglot.ast.FlagsNode;
import polyglot.ast.Formal;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.types.Flags;
import polyglot.types.QName;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SimpleCodeWriter;
import polyglot.visit.PrettyPrinter;
import x10.ExtensionInfo;
import x10.ast.X10NodeFactory_c;
import x10dt.ui.X10DTUIPlugin;
import x10dt.ui.editor.X10LabelProvider;
import x10dt.ui.parser.PolyglotNodeLocator;

public class CreateMethodProposal extends CUCorrectionProposal {
	IQuickFixInvocationContext context;
	Object root;
	Node coveringNode;
	PolyglotNodeLocator pnl;
	String name;
	String arguments;

	public CreateMethodProposal(IQuickFixInvocationContext context,
			String name, String signature) {
		super("Create method '" + name + "()'", context.getModel(), 8, null);
		this.context = context;
		this.name = name;
		this.arguments = signature;
		setImage(X10LabelProvider._DESC_MISC_PRIVATE);
	}

	@Override
	protected void addEdits(IDocument document, TextEdit editRoot)
			throws CoreException {
		
		root = getCompilationUnit().getAST(new NullMessageHandler(),
				new NullProgressMonitor());
		pnl = new PolyglotNodeLocator(getCompilationUnit().getProject(), null);
		
		coveringNode = (Node) pnl.findNode(root, context.getOffset());
		Object node = pnl.findEnclosingMethodDecl(root, context.getOffset());
		
		if (node instanceof MethodDecl) {
			MethodDecl methodDecl = (MethodDecl) node;
			MultiTextEdit edit = new MultiTextEdit();
			
			X10NodeFactory_c factory = new X10NodeFactory_c(new ExtensionInfo());
			FlagsNode flags = factory.FlagsNode(null, Flags.PRIVATE);
			TypeNode returnType = factory.TypeNodeFromQualifiedName(null, QName.make(methodDecl.returnType().nameString()));
			
			List<Formal> formals = new ArrayList<Formal>();
			Pattern p = Pattern.compile("\\(*\\s*,*\\s*([^\\{]+)\\{self==([^\\}]+)\\}");
			Matcher m = p.matcher(arguments);
			while(m.find())
			{
				formals.add(factory.Formal(null, factory.FlagsNode(null, Flags.NONE), factory.TypeNodeFromQualifiedName(new Position("", ""), QName.make(m.group(1))), factory.Id(null, m.group(2))));
			}
				
			// TODO Auto-generated method stub
			Block body = factory.Block(null);
			MethodDecl newMethodDecl = factory.MethodDecl(null, flags, returnType, factory.Id(null, name), formals, body);
			
			int offset = methodDecl.position().endOffset() + 1;
			StringWriter sw = new StringWriter();
			
			try {
				CodeWriter cw = new SimpleCodeWriter(sw, 1);
				newMethodDecl.prettyPrint(cw, new PrettyPrinter());
				cw.flush();
			} catch (IOException e) {
				X10DTUIPlugin.log(e);
				// TODO quickfix failed should we show a popup?
				return;
			}
			
			String lineDelim = TextUtilities.getDefaultLineDelimiter(document);
			edit.addChild(new InsertEdit(offset, lineDelim));
			edit.addChild(new InsertEdit(offset, lineDelim));
			edit.addChild(new InsertEdit(offset, sw.toString()));		
			editRoot.addChild(edit);
			FormatUtils.format(LanguageRegistry.findLanguage("X10"), document, new Region(offset, editRoot.getLength()));
		}
	}
}
