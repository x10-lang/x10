package safari.X10.refactoring.actions;

import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.internal.corext.callhierarchy.MethodCall;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.eclipse.uide.editor.UniversalEditor;
import org.eclipse.uide.parser.IASTNodeLocator;
import org.eclipse.uide.parser.IParseController;
import org.eclipse.uide.refactoring.RefactoringStarter;

import polyglot.ast.Ambiguous;
import polyglot.ast.Call;
import polyglot.ast.Field;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.types.Declaration;
import polyglot.types.LocalInstance;
import polyglot.types.Type;
import polyglot.visit.NodeVisitor;
import safari.X10.refactoring.RefactoringMessages;
import safari.X10.refactoring.RenameRefactoring;
import safari.X10.refactoring.RenameWizard;

public class RenameRefactoringAction extends TextEditorAction {
    private final Node fNode;

    private final Node fRoot;

    private Node fDeclaringNode;

    private Node fDeclaringParent;

    public RenameRefactoringAction(UniversalEditor editor) {
	super(RefactoringMessages.ResBundle, "Rename.", editor);

        IEditorInput input= editor.getEditorInput();

        if (input instanceof IFileEditorInput) {
            fRoot= findRoot(editor);
            fNode= findNode(editor);
        } else {
            fRoot= null;
            fNode= null;
        }
    }

    private Node findRoot(UniversalEditor editor) {
        IParseController parseController= editor.getParseController();

        return (Node) parseController.getCurrentAst();
    }

    private Node findNode(UniversalEditor editor) {
        Point sel= editor.getSelection();
        IParseController parseController= editor.getParseController();
        IASTNodeLocator locator= parseController.getNodeLocator();

        return (Node) locator.findNode(fRoot, sel.x);
    }

    public void run() {
        final Shell shell= getTextEditor().getSite().getShell();

        if (fNode instanceof Ambiguous) {
            MessageDialog.openError(shell, "Cannot rename", "Cannot rename ambiguous entity (failed name resolution due to compile errors).");
            return;
        }

        Declaration decl;

        if (fNode instanceof Local) {
            decl= ((Local) fNode).localInstance();
        } else if (fNode instanceof Field) {
            decl= ((Field) fNode).fieldInstance();
        } else if (fNode instanceof Formal) {
            decl= ((Formal) fNode).localInstance();
        } else if (fNode instanceof FieldDecl) {
            decl= ((FieldDecl) fNode).fieldInstance();
        } else if (fNode instanceof LocalDecl) {
            decl= ((LocalDecl) fNode).localInstance();
        } else if (fNode instanceof MethodDecl) {
            decl= ((MethodDecl) fNode).methodInstance();
        } else if (fNode instanceof Call) {
            decl= ((Call) fNode).methodInstance();
//      } else if (fNode instanceof TypeNode) {
        } else {
            MessageDialog.openError(shell, "Cannot rename", "Renaming of entities other than local variables, fields and methods not yet implemented.");
            return;
        }

        findDeclaration(decl);

        if (fDeclaringNode == null)
            findExternalDecl(decl);

        if (fDeclaringNode == null) {
            MessageDialog.openError(shell, "Cannot rename", "Renaming entities across source files not yet supported.");
            return;
        }

        RenameRefactoring refactoring= new RenameRefactoring(getTextEditor(), fDeclaringNode, fDeclaringParent, fRoot);

        if (refactoring != null)
		new RefactoringStarter().activate(refactoring, new RenameWizard(refactoring, "Rename"), shell, "Rename", false);
    }

    /**
     * @param decl
     */
    private void findExternalDecl(Declaration decl) {
        System.out.println(decl.position());
    }

    /**
     * @param decl
     * @return
     */
    private void findDeclaration(final Declaration decl) {
        NodeVisitor visitor= new NodeVisitor() {
            /* (non-Javadoc)
             * @see polyglot.visit.NodeVisitor#enter(polyglot.ast.Node)
             */
            @Override
            public NodeVisitor enter(Node parent, Node n) {
                if (n instanceof LocalDecl) {
                    if (((LocalDecl) n).localInstance().equals(decl)) {
                        fDeclaringNode= n;
                        fDeclaringParent= parent;
                    }
                } else if (n instanceof FieldDecl) {
                    if (((FieldDecl) n).fieldInstance().equals(decl)) {
                        fDeclaringNode= n;
                        fDeclaringParent= parent;
                    }
                } else if (n instanceof Formal) {
                    if (((Formal) n).localInstance().equals(decl)) {
                        fDeclaringNode= n;
                        fDeclaringParent= parent;
                    }
                } else if (n instanceof MethodDecl) {
                    if (((MethodDecl) n).methodInstance().equals(decl)) {
                        fDeclaringNode= n;
                        fDeclaringParent= parent;
                    }
                }
                return this;
            }
        };
        fRoot.visit(visitor);
    }
}
