package x10dt.ui.typeHierarchy;
/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/


import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.model.ICompilationUnit;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import polyglot.ast.Call_c;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.types.ClassType;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import x10.types.MethodInstance;
import x10dt.search.core.elements.IMemberInfo;
import x10dt.search.core.elements.IMethodInfo;
import x10dt.search.core.elements.ITypeInfo;
import x10dt.ui.X10DTUIPlugin;
import x10dt.ui.parser.PolyglotNodeLocator;


public class SelectionConverter {

	private static final ITypeInfo[] EMPTY_RESULT= new ITypeInfo[0];

	private SelectionConverter() {
		// no instance
	}

	/**
	 * Converts the selection provided by the given part into a structured selection. The following
	 * conversion rules are used:
	 * <ul>
	 * <li><code>part instanceof UniversalEditor</code>: returns a structured selection using code
	 * resolve to convert the editor's text selection.</li>
	 * <li><code>part instanceof IWorkbenchPart</code>: returns the part's selection if it is a
	 * structured selection.</li>
	 * <li><code>default</code>: returns an empty structured selection.</li>
	 * </ul>
	 * 
	 * @param part the part
	 * @return the selection
	 * @throws Exception thrown when the type root can not be accessed
	 */
	public static IStructuredSelection getStructuredSelection(IWorkbenchPart part) throws Exception {
		if (part instanceof UniversalEditor)
			return new StructuredSelection(codeResolve((UniversalEditor)part));
		ISelectionProvider provider= part.getSite().getSelectionProvider();
		if (provider != null) {
			ISelection selection= provider.getSelection();
			if (selection instanceof IStructuredSelection)
				return (IStructuredSelection)selection;
		}
		return StructuredSelection.EMPTY;
	}


	/**
	 * Converts the given structured selection into an array of Java elements.
	 * An empty array is returned if one of the elements stored in the structured
	 * selection is not of type <code>ITypeInfo</code>
	 * @param selection the selection
	 * @return the Java element contained in the selection
	 */
	public static ITypeInfo[] getElements(IStructuredSelection selection) {
		if (!selection.isEmpty()) {
			ITypeInfo[] result= new ITypeInfo[selection.size()];
			int i= 0;
			for (Iterator iter= selection.iterator(); iter.hasNext(); i++) {
				Object element= iter.next();
				if (!(element instanceof ITypeInfo))
					return EMPTY_RESULT;
				result[i]= (ITypeInfo)element;
			}
			return result;
		}
		return EMPTY_RESULT;
	}

	public static boolean canOperateOn(UniversalEditor editor) {
		if (editor == null)
			return false;
		return getInput(editor) != null;

	}

	public static IMemberInfo[] codeResolveOrInputForked(UniversalEditor editor) throws InvocationTargetException, InterruptedException {
		IMemberInfo input= getInput(editor);
		if (input == null)
			return EMPTY_RESULT;

		ITextSelection selection= (ITextSelection)editor.getSelectionProvider().getSelection();
		IMemberInfo[] result= performForkedCodeResolve(input, selection);
		if (result.length == 0) {
			result= new IMemberInfo[] {input};
		}
		return result;
	}

	public static IMemberInfo[] codeResolve(UniversalEditor editor) throws Exception {
		return codeResolve(editor, true);
	}

	/**
	 * Perform a code resolve at the current selection of an editor
	 * 
	 * @param editor the editor
	 * @param primaryOnly if <code>true</code> only primary working copies will be returned
	 * @return the resolved elements
	 * @throws Exception thrown when the type root can not be accessed
	 * @since 3.2
	 */
	public static IMemberInfo[] codeResolve(UniversalEditor editor, boolean primaryOnly) throws Exception {
		return new IMemberInfo[] {getInput(editor, primaryOnly)};
//		if (input != null)
//			return codeResolve(input, (ITextSelection) editor.getSelectionProvider().getSelection());
//		return EMPTY_RESULT;
	}

	/**
	 * Perform a code resolve in a separate thread.
	 * 
	 * @param editor the editor
	 * @param primaryOnly if <code>true</code> only primary working copies will be returned
	 * @return the resolved elements
	 * @throws InvocationTargetException which wraps any exception or error which occurs while
	 *             running the runnable
	 * @throws InterruptedException propagated by the context if the runnable acknowledges
	 *             cancelation by throwing this exception
	 * @since 3.2
	 */
	public static IMemberInfo[] codeResolveForked(UniversalEditor editor, boolean primaryOnly) throws InvocationTargetException, InterruptedException {
		IMemberInfo input= getInput(editor, primaryOnly);
		if (input != null)
			return performForkedCodeResolve(input, (ITextSelection) editor.getSelectionProvider().getSelection());
		return EMPTY_RESULT;
	}

	public static IMemberInfo getElementAtOffset(UniversalEditor editor) throws Exception {
		return getElementAtOffset(editor, true);
	}

	/**
	 * Returns the element surrounding the selection of the given editor.
	 * 
	 * @param editor the editor
	 * @param primaryOnly if <code>true</code> only primary working copies will be returned
	 * @return the element surrounding the current selection
	 * @throws Exception if the Java type root does not exist or if an exception occurs
	 *             while accessing its corresponding resource
	 * @since 3.2
	 */
	public static IMemberInfo getElementAtOffset(UniversalEditor editor, boolean primaryOnly) throws Exception {
		return getInput(editor, primaryOnly);
//		if (input != null)
//			return getElementAtOffset(input, (ITextSelection) editor.getSelectionProvider().getSelection());
//		return null;
	}

	public static ITypeInfo getTypeAtOffset(UniversalEditor editor) throws Exception {
		IMemberInfo element= SelectionConverter.getElementAtOffset(editor);
		
		ITypeInfo type= SearchUtils.getOuterTypeInfo(element);
//		if (type == null) {
//			ICompilationUnit unit= SelectionConverter.getInputAsCompilationUnit(editor);
//			if (unit != null)
//				type= unit.findPrimaryType();
//		}
		return type;
	}

	public static IMemberInfo getInput(UniversalEditor editor) {
		return getInput(editor, true);
	}

	public static IMemberInfo getInput(IProject project, IParseController parseController, int startOffset)
	{
		IMemberInfo info = null;
		try {
			int offset = startOffset;
			PolyglotNodeLocator loc = (PolyglotNodeLocator)parseController.getSourcePositionLocator();
			Object ast = parseController.getCurrentAst();
			MethodInstance method = null;
			
			Object target = loc.findNode(ast, offset);
			List<Node> nodes = loc.getPathToRoot((Node)ast, (Node)target);
			//Collections.reverse(nodes);
			
			if(target instanceof ClassBody)
			{
				nodes.add((Node)loc.getParentNodeOf(target, ast));
			}
			
			String typeName = null;
			for(Node node : nodes)
			{
				if(node instanceof Call_c)
				{
					Call_c call = (Call_c)node;
					if(call.name().equals(target))
					{
						method = call.methodInstance();
						typeName = ((Type)method.def().container().get()).fullName().toString();
					}
				}
				
				else if(node instanceof TypeNode)
				{
					typeName = ((TypeNode)node).type().toString();
				}
				
				else if(node instanceof MethodDecl)
				{
					method = ((MethodDecl)node).methodDef().asInstance();
				}
				
				else if(node instanceof ClassDecl)
				{
					typeName = ((ClassDecl)node).classDef().fullName().toString();
				}
				
				if(typeName != null)
				{
					info = SearchUtils.getType(project, typeName);
					if(method != null)
					{
						IMethodInfo[] methods = SearchUtils.getMethods((ITypeInfo)info, method.name().toString());
						if(methods.length > 0)
						{
							for(IMethodInfo methodInfo : methods)
							{
								if(isEqual(methodInfo, method))
								{
									return methodInfo;
								}
							}
						}
					}
					
					typeName = null;
				}
			}
			
		} catch (Exception e) {
			X10DTUIPlugin.log(e);
		}

		return info;
	}
	
	private static boolean isEqual(IMethodInfo methodInfo, MethodInstance methodInst)
	{
		try {
			List<Type> formals = methodInst.formalTypes();
			ITypeInfo[] params = methodInfo.getParameters();
			
			if(formals.size() == params.length)
			{
				final TypeSystem typeSystem = (TypeSystem) methodInst.typeSystem();
				for(int i = 0; i < formals.size(); i++)
				{
					Type instType = typeSystem.forName(QName.make(null, ((ClassType)formals.get(i)).fullName().toString()));
					Type infoType = typeSystem.forName(QName.make(null, params[i].getName()));
					if(!infoType.isSubtype(instType, typeSystem.emptyContext()))
					{
						return false;
					}
				}

				return true;
			}
		} catch (SemanticException e) {
			X10DTUIPlugin.log(e);
		}
		
		return false;
		
		
		
//        if (methodDecl.name().toString().equals(MAIN_METHOD_NAME) && methodDecl.flags().flags().isPublic() &&
//            methodDecl.flags().flags().isStatic() && methodDecl.returnType().type().isVoid() &&
//            (methodDecl.formals().size() == 1) && 
//            
        	
        	
        	
	}
	
	
	/**
	 * Returns the input element of the given editor
	 *
	 * @param editor the Java editor
	 * @param primaryOnly if <code>true</code> only primary working copies will be returned
	 * @return the type root which is the editor input
	 * @since 3.2
	 */
	private static IMemberInfo getInput(UniversalEditor editor,
			boolean primaryOnly) {
		if (editor == null)
			return null;
		
		IProject proj = null;
		ISourceProject sp = editor.getParseController().getProject();
		if(sp != null)
		{
			proj = sp.getRawProject();
		}
		
		return getInput(proj, editor.getParseController(), editor.getSelectedRegion().getOffset());
	}

	public static IMemberInfo getInputAsTypeRoot(UniversalEditor editor) {
		return SelectionConverter.getInput(editor);
	}

	public static ICompilationUnit getInputAsCompilationUnit(UniversalEditor editor) {
		Object editorInput= SelectionConverter.getInput(editor);
		if (editorInput instanceof ICompilationUnit)
			return (ICompilationUnit)editorInput;
		return null;
	}

//	public static IClassFile getInputAsClassFile(UniversalEditor editor) {
//		Object editorInput= SelectionConverter.getInput(editor);
//		if (editorInput instanceof IClassFile)
//			return (IClassFile)editorInput;
//		return null;
//	}

	private static IMemberInfo[] performForkedCodeResolve(final IMemberInfo input, final ITextSelection selection) throws InvocationTargetException, InterruptedException {
		final class CodeResolveRunnable implements IRunnableWithProgress {
			ITypeInfo[] result;
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					result= codeResolve(input, selection);
				} catch (Exception e) {
					throw new InvocationTargetException(e);
				}
			}
		}
		CodeResolveRunnable runnable= new CodeResolveRunnable();
		PlatformUI.getWorkbench().getProgressService().busyCursorWhile(runnable);
		return runnable.result;
	}

	public static ITypeInfo[] codeResolve(IMemberInfo input, ITextSelection selection) throws Exception {
//			if (input instanceof ICodeAssist) {
//				if (input instanceof ICompilationUnit) {
//					JavaModelUtil.reconcile((ICompilationUnit) input);
//				}
//				ITypeInfo[] elements= ((ICodeAssist)input).codeSelect(selection.getOffset() + selection.getLength(), 0);
//				if (elements.length > 0) {
//					return elements;
//				}
//			}
			return EMPTY_RESULT;
	}

//	public static ITypeInfo getElementAtOffset(ITypeInfo input, ITextSelection selection) throws Exception {
//		if (input instanceof ICompilationUnit) {
//			JavaModelUtil.reconcile((ICompilationUnit) input);
//		}
//		
//		IDocument doc = EditorUtility.getDocument(input);
//		IRegion region = WordFinder.findWord(doc, selection.getOffset());
//		ITypeInfo ref = X10SearchEngine.getTypeInfo(SearchUtils.getResource(input).getProject(), SearchUtils.getTypeRegex(doc.get(region.getOffset(), region.getLength())), new NullProgressMonitor());
//		
//		if (ref == null)
//			return input;
//		return ref;
//	}

//	public static ITypeInfo[] resolveSelectedElements(ITypeInfo input, ITextSelection selection) throws Exception {
//		ITypeInfo enclosing= resolveEnclosingElement(input, selection);
//		if (enclosing == null)
//			return EMPTY_RESULT;
//		if (!(enclosing instanceof ISourceReference))
//			return EMPTY_RESULT;
//		IRegion sr= ((ISourceReference)enclosing).getSourceRange();
//		if (selection.getOffset() == sr.getOffset() && selection.getLength() == sr.getLength())
//			return new ITypeInfo[] {enclosing};
//	}

//	public static ITypeInfo resolveEnclosingElement(UniversalEditor editor, ITextSelection selection) throws Exception {
//		ITypeInfo input= getInput(editor);
//		if (input != null)
//			return resolveEnclosingElement(input, selection);
//		return null;
//	}

//	public static ITypeInfo resolveEnclosingElement(ITypeInfo input, ITextSelection selection) throws Exception {
//		ITypeInfo atOffset= null;
//		if (input instanceof ICompilationUnit) {
//			ICompilationUnit cunit= (ICompilationUnit)input;
////			JavaModelUtil.reconcile(cunit);
//			atOffset= getElementAtOffset(input, selection);
//		} 
////		else if (input instanceof IClassFile) {
////			IClassFile cfile= (IClassFile)input;
////			atOffset= cfile.getElementAt(selection.getOffset());
////		} 
//		else {
//			return null;
//		}
//		if (atOffset == null) {
//			return input;
//		} else {
//			int selectionEnd= selection.getOffset() + selection.getLength();
//			ITypeInfo result= atOffset;
////			if (atOffset instanceof ISourceReference) {
////				IRegion range= ((ISourceReference)atOffset).getSourceRange();
////				while (range.getOffset() + range.getLength() < selectionEnd) {
////					result= result.getParent();
////					if (! (result instanceof ISourceReference)) {
////						result= input;
////						break;
////					}
////					range= ((ISourceReference)result).getSourceRange();
////				}
////			}
//			return result;
//		}
//	}

	/**
	 * Shows a dialog for resolving an ambiguous Java element. Utility method that can be called by subclasses.
	 *
	 * @param elements the elements to select from
	 * @param shell the parent shell
	 * @param title the title of the selection dialog
	 * @param message the message of the selection dialog
	 * @return returns the selected element or <code>null</code> if the dialog has been cancelled
	 */
	public static IMemberInfo selectJavaElement(IMemberInfo[] elements, Shell shell, String title, String message) {
		int nResults= elements.length;
		if (nResults == 0)
			return null;
		if (nResults == 1)
			return elements[0];

		
//		int flags= JavaElementLabelProvider.SHOW_DEFAULT | JavaElementLabelProvider.SHOW_QUALIFIED | JavaElementLabelProvider.SHOW_ROOT;

		ElementListSelectionDialog dialog= new ElementListSelectionDialog(shell, new X10LabelProvider());
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setElements(elements);

		if (dialog.open() == Window.OK) {
			return (ITypeInfo) dialog.getFirstResult();
		}
		return null;
	}
}
