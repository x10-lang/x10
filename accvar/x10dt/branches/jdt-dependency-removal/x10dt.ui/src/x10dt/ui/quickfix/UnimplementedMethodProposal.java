package x10dt.ui.quickfix;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.imp.editor.quickfix.CUCorrectionProposal;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.parser.ISourcePositionLocator;
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

import polyglot.ast.Block;
import polyglot.ast.ClassDecl;
import polyglot.ast.Expr;
import polyglot.ast.FlagsNode;
import polyglot.ast.Formal;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SimpleCodeWriter;
import polyglot.visit.ContextVisitor;
import x10.ExtensionInfo;
import x10.ast.X10NodeFactory_c;
import x10.extension.X10Ext;
import x10.types.MethodInstance;
import x10.types.X10MethodDef;
import x10dt.core.utils.HierarchyUtils;
import x10dt.ui.X10DTUIPlugin;
import x10dt.ui.parser.PolyglotNodeLocator;

public class UnimplementedMethodProposal extends CUCorrectionProposal {
	private static final Position EMPTY_POS = new Position("", "");

	IQuickFixInvocationContext context;
	
	ISourcePositionLocator nodeLocator;
	
	public UnimplementedMethodProposal(IQuickFixInvocationContext context) {
		super("Add unimplemented methods", context.getModel(), 8, null);
		this.context = context;
		setImage(PluginImages.get(PluginImages.IMG_CORRECTION_CHANGE));
	}
	
	boolean contains(Collection<MethodInstance> list, MethodInstance mi)
	{
		for (MethodInstance m : list) {
			if(m.isSameMethod(mi, mi.typeSystem().emptyContext()))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	protected void addEdits(IDocument document, TextEdit editRoot)
			throws CoreException {
		Object root = getCompilationUnit().getAST(new NullMessageHandler(),
				new NullProgressMonitor());
		
		PolyglotNodeLocator pnl = new PolyglotNodeLocator(getCompilationUnit().getProject(), null);
		ClassDecl classDecl = null;
		Node coveringNode = (Node) pnl.findNode(root, context.getOffset());

		if (coveringNode instanceof ClassDecl) {
			classDecl = (ClassDecl) coveringNode;
		}

		else if (coveringNode instanceof FlagsNode) {
			classDecl = (ClassDecl) pnl.findParentNode(root, context
					.getOffset());
		}

		if (classDecl != null) {
			
			Set<ClassType> superClasses = HierarchyUtils.getSuperClasses(classDecl.classDef().asType());
			superClasses.add(classDecl.classDef().asType());
			
			Set<ClassType> interfaces = HierarchyUtils.getInterfaces(superClasses);
			Set<MethodInstance> implementedMethods = HierarchyUtils.getImplementedMethods(superClasses);
			
			Set<MethodInstance> abstractMethods = HierarchyUtils.getMethods(superClasses, Flags.ABSTRACT);
			Set<MethodInstance> interfaceMethods = HierarchyUtils.getMethods(interfaces);
			
			MultiTextEdit edit = new MultiTextEdit();
			
			String lineDelim = TextUtilities.getDefaultLineDelimiter(document);
			List<MethodInstance> unimplemented = new ArrayList<MethodInstance>();

//			LanguageServiceManager man = new LanguageServiceManager(getCompilationUnit().getParseController().getLanguage());
//			man.getFormattingStrategy().format(parseController, content, isLineStart, indentation, positions);
//			
			
			for (MethodInstance mi : abstractMethods) {
				if(!contains(implementedMethods, mi))
				{
					unimplemented.add(mi);
				}
			}
			
			for (MethodInstance mi : interfaceMethods) {
				if(!contains(implementedMethods, mi))
				{
					unimplemented.add(mi);
				}
			}

			if (!unimplemented.isEmpty()) {
				int offset = classDecl.body().position().endOffset() - 1;
				X10NodeFactory_c factory = new X10NodeFactory_c(new ExtensionInfo());
				
				for (MethodInstance mi : unimplemented) {
					
					try {
						List<Formal> formals = new ArrayList<Formal>();
						
						FlagsNode flags = factory.FlagsNode(null, mi.flags().clearAbstract());
						TypeNode returnType = factory.CanonicalTypeNode(EMPTY_POS, mi.returnType());
						
						
						List<TypeNode> typeList = new ArrayList<TypeNode>();
						List<Expr> args = new ArrayList<Expr>();
						
						for (LocalDef f : ((X10MethodDef)mi.def()).formalNames()) {
							TypeNode tn = factory.CanonicalTypeNode(EMPTY_POS, f.type());
							typeList.add(tn);
							formals.add(factory.Formal(null, factory.FlagsNode(null, Flags.NONE), tn, factory.Id(null, f.name())));
				            args.add(factory.Local(null, factory.Id(null, f.name())));
				        }
						
						Block body = factory.Block(null);
						Node ret = null;
						
						if (!mi.returnType().isVoid()) {
//							if(!mi.flags().contains(Flags.ABSTRACT))
//							{
//								Call call = factory.X10Call(null, factory.Super(null), factory.Id(null, mi.name()), typeList, args);
//								ret = factory.Eval(null, call);
//							}
							
							ContextVisitor cv = new ContextVisitor(null, mi.typeSystem(), factory);
							cv.begin();
							ret = factory.Return(null, Types.getZeroVal(returnType, null, cv));
							ret = ret.ext(((X10Ext)ret.ext()).comment("// TODO: auto-generated method stub\n"));
							cv.finish();
				        }
						
						else
						{
							ret = factory.Empty(null);
							ret = ret.ext(((X10Ext)ret.ext()).comment("// TODO: auto-generated method stub"));
						}
						
						body = body.append((Stmt)ret);
						MethodDecl newMethodDecl = factory.MethodDecl(null, flags, returnType, factory.Id(null, mi.name()), formals, body);
						
						StringWriter sw = new StringWriter();
						CodeWriter cw = new SimpleCodeWriter(sw, 100) {
							public void unifiedBreak(int n, int level, String alt, int altlen) {
								newline(n, 1);
							}
						};
						
						newMethodDecl.prettyPrint(cw, new CommentPrettyPrinter());
						cw.flush();
						edit.addChild(new InsertEdit(offset, sw.toString()));
						edit.addChild(new InsertEdit(offset, lineDelim));
						edit.addChild(new InsertEdit(offset, lineDelim));
					} catch (Exception e) {
						X10DTUIPlugin.log(e);
					}
				}
				editRoot.addChild(edit);
				FormatUtils.format(LanguageRegistry.findLanguage("X10"), document, new Region(offset, editRoot.getLength()));
			}
		}
		
		// TODO quickfix failed should we show a popup?
	}
}
