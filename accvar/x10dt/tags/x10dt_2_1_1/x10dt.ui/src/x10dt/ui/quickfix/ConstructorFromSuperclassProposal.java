package x10dt.ui.quickfix;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

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
import polyglot.ast.ClassDecl;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.FlagsNode;
import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.Flags;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SimpleCodeWriter;
import polyglot.visit.PrettyPrinter;
import x10.ExtensionInfo;
import x10.ast.X10NodeFactory_c;
import x10dt.ui.X10DTUIPlugin;
import x10dt.ui.editor.X10LabelProvider;
import x10dt.ui.parser.PolyglotNodeLocator;

public class ConstructorFromSuperclassProposal extends CUCorrectionProposal {
	IQuickFixInvocationContext context;
	String name;
	String arguments;
	

	ClassDecl classDecl;
	ClassType classType;
	ClassType superClassType;
	ConstructorInstance superConstInst;

	public ConstructorFromSuperclassProposal(
			IQuickFixInvocationContext context, String name, String arguments) {
		super("Add constructor '" + name + arguments + "'", context.getModel(), 8, null);
		this.context = context;
		this.name = name;
		this.arguments = arguments;
		setImage(X10LabelProvider._DESC_OBJS_INNER_CLASS_PUBLIC);
	}

	@Override
	protected void addEdits(IDocument document, TextEdit editRoot)
			throws CoreException {
		
		Object root = getCompilationUnit().getAST(new NullMessageHandler(),
				new NullProgressMonitor());
		PolyglotNodeLocator pnl = new PolyglotNodeLocator(getCompilationUnit().getProject(), null);

		Node coveringNode = (Node) pnl.findEnclosingClassDecl(root, context
				.getOffset());

		if (!(coveringNode instanceof ClassDecl)) {
			// TODO quickfix failed should we show a popup?
			return;
		}

		classDecl = (ClassDecl) coveringNode;
		classType = classDecl.classDef().asType();
		superClassType = (ClassType)classType.superClass();
		ConstructorInstance ci = null;
		
//		for(ConstructorInstance c : superClassType.constructors())
//		{
//			if(c.signature().equals(name + arguments))
//			{
//				ci = c;
//				break;
//			}
//		}
//		
//		if(ci == null)
//		{
//			return;
//		}
		
		ci = superClassType.constructors().get(0);
		
		MultiTextEdit edit = new MultiTextEdit();
		int offset = classDecl.body().position().endOffset() - 1;
		String lineDelim = TextUtilities.getDefaultLineDelimiter(document);
		
		X10NodeFactory_c factory = new X10NodeFactory_c(new ExtensionInfo());
		FlagsNode flags = factory.FlagsNode(null, ci.flags());
		
		List<Formal> formals = new ArrayList<Formal>();
		for(Type formal : ci.formalTypes())
		{
			ClassType ct = (ClassType)formal;
			formals.add(factory.Formal(null, factory.FlagsNode(null, Flags.NONE), factory.TypeNodeFromQualifiedName(new Position("", ""), ct.fullName()), factory.Id(null, ct.name())));
		}
			
		// TODO Auto-generated constructor stub
		Block body = factory.Block(null);
		ConstructorDecl newConstructorDecl = factory.ConstructorDecl(null, flags, factory.Id(null, classType.name()), formals, body);
		
		try {
			edit.addChild(new InsertEdit(offset, lineDelim));
			
			StringWriter sw = new StringWriter();
			CodeWriter cw = new SimpleCodeWriter(sw, 1);
			newConstructorDecl.prettyPrint(cw, new PrettyPrinter());
			cw.flush();

			edit.addChild(new InsertEdit(offset, sw.toString()));
			edit.addChild(new InsertEdit(offset, lineDelim));
			edit.addChild(new InsertEdit(offset, lineDelim));
			editRoot.addChild(edit);
			FormatUtils.format(LanguageRegistry.findLanguage("X10"), document, new Region(offset, editRoot.getLength()));
		} catch (Exception e) {
			X10DTUIPlugin.log(e);
			// TODO quickfix failed should we show a popup?
		}
	}
}
