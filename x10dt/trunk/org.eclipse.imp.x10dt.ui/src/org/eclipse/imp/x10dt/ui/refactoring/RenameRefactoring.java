package safari.X10.refactoring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEditGroup;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.Field;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.Stmt;
import polyglot.types.Declaration;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;

public class RenameRefactoring extends Refactoring {
    private final IFile fSourceFile;
    private final Node fDeclNode;
    private final Node fRoot;
    private final Declaration fDecl;
    private final Node fDeclParent;
    private final String fCurName;
    private String fNewName;

    public RenameRefactoring(ITextEditor editor, Node declNode, Node declParent, Node root) {
	super();

        fDeclNode= declNode;
        fDeclParent= declParent;
        fRoot= root;

        if (fDeclNode instanceof LocalDecl) {
            LocalDecl localDecl= (LocalDecl) fDeclNode;
            fDecl= localDecl.localInstance();
            fCurName= localDecl.name();
        } else if (fDeclNode instanceof FieldDecl) {
            FieldDecl fieldDecl= (FieldDecl) fDeclNode;
            fDecl= fieldDecl.fieldInstance();
            fCurName= fieldDecl.name();
        } else if (fDeclNode instanceof Formal) {
            Formal f= (Formal) fDeclNode;
            fDecl= f.localInstance();
            fCurName= f.name();
        } else if (fDeclNode instanceof MethodDecl) {
            MethodDecl method= (MethodDecl) fDeclNode;
            fDecl= method.methodInstance();
            fCurName= method.name();
        } else {
            fDecl= null;
            fCurName= "";
        }

        IEditorInput input= editor.getEditorInput();

	if (input instanceof IFileEditorInput) {
	    IFileEditorInput fileInput= (IFileEditorInput) input;

	    fSourceFile= fileInput.getFile();
	} else {
	    fSourceFile= null;
	}
    }

    public String getName() {
	return "Rename";
    }

    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
	return new RefactoringStatus();
    }

    private RefactoringStatus checkForLocal() {
        NodePathComputer pathComp= new NodePathComputer(fRoot, fDeclNode);
        List<Node> path= pathComp.getPath();
        List<Node> revPath= new ArrayList<Node>(path.size());

        revPath.addAll(path);
        Collections.reverse(revPath);

        for(Iterator iter= revPath.iterator(); iter.hasNext(); ) {
            Node n= (Node) iter.next();
            if (definesLocal(n, fNewName))
                    return RefactoringStatus.createFatalErrorStatus("Name collision: local variable '" + fNewName + "' already exists in an enclosing scope.");
            if (n instanceof MethodDecl)
                break;
        }
        return new RefactoringStatus();
    }

    /**
     * @return true if the given node is of a type that defines a local variable of the given name
     */
    private boolean definesLocal(Node n, String name) {
        if (n instanceof Block) {
            Block b= (Block) n;
            List<Stmt> statements= b.statements();
            for(Stmt stmt : statements) {
                if (definesLocal(stmt, name))
                    return true;
            }
        } else if (n instanceof LocalDecl) {
            LocalDecl ld= (LocalDecl) n;
            if (ld.name().equals(name))
                return true;
        } else if (n instanceof MethodDecl) {
            MethodDecl md= (MethodDecl) n;
            List<Formal> formals= md.formals();
            for(Formal formal : formals) {
                if (formal.name().equals(name))
                    return true;
            }
        }
        return false;
    }

    private RefactoringStatus checkForField() {
        ClassBody body= null;

        if (fDeclParent instanceof ClassDecl)
            body= ((ClassDecl) fDeclParent).body();
        else if (fDeclParent instanceof ClassBody)
            body= (ClassBody) fDeclParent;

        List<ClassMember> members= body.members();

        for(ClassMember member : members) {
            if (member instanceof FieldDecl) {
                FieldDecl fd= (FieldDecl) member;
                if (fd.name().equals(fNewName))
                    return RefactoringStatus.createFatalErrorStatus("Name collision: field '" + fNewName + "' already exists in parent type.");
            }
        }
        return new RefactoringStatus();
    }

    private RefactoringStatus checkForMethod() {
        ClassBody body= null;

        if (fDeclParent instanceof ClassDecl)
            body= ((ClassDecl) fDeclParent).body();
        else if (fDeclParent instanceof ClassBody)
            body= (ClassBody) fDeclParent;

        List<ClassMember> members= body.members();

        for(ClassMember member : members) {
            if (member instanceof MethodDecl) {
                MethodDecl md= (MethodDecl) member;
                // TODO check method signatures as well
                if (md.name().equals(fNewName))
                    return RefactoringStatus.createFatalErrorStatus("Name collision: method '" + fNewName + "' already exists in parent type.");
            }
        }
        return new RefactoringStatus();
    }

    private RefactoringStatus checkForFormal() {
        ProcedureDecl method= (ProcedureDecl) fDeclParent;
        List<Formal> formals= method.formals();
        for(Formal formal : formals) {
            if (formal.name().equals(fNewName))
                return RefactoringStatus.createFatalErrorStatus("Name collision: formal parameter '" + fNewName + "' already exists in parent method.");
        }
        return new RefactoringStatus();
    }

    public RefactoringStatus checkFinalConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        if (fDeclNode instanceof LocalDecl)
            return checkForLocal();
        else if (fDeclNode instanceof FieldDecl)
            return checkForField();
        else if (fDeclNode instanceof MethodDecl)
            return checkForMethod();
        else if (fDeclNode instanceof Formal)
            return checkForFormal();
	return new RefactoringStatus();
    }

    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
	TextFileChange tfc= new TextFileChange("Rename", fSourceFile);
	MultiTextEdit root= new MultiTextEdit();

        tfc.setEdit(root);
        createDeclChange(tfc, root);
        createRefChanges(tfc, root);

        return tfc;
    }

    private void createRefChanges(final TextFileChange tfc, final MultiTextEdit root) {
        final TextEditGroup group= new TextEditGroup("Rename references");
        NodeVisitor v= new NodeVisitor() {
            /* (non-Javadoc)
             * @see polyglot.visit.NodeVisitor#enter(polyglot.ast.Node)
             */
            @Override
            public NodeVisitor enter(Node n) {
                if (n instanceof Local) {
                    Local l= (Local) n;
                    if (l.localInstance().equals(fDecl)) {
                        Position pos= n.position();
                        final ReplaceEdit edit= new ReplaceEdit(pos.offset(), pos.endOffset() - pos.offset() + 1, fNewName);
                        root.addChild(edit);
                        group.addTextEdit(edit);
                    }
                } else  if (n instanceof Field) {
                    Field f= (Field) n;
                    if (f.fieldInstance().equals(fDecl)) {
                        Position pos= n.position();
                        final ReplaceEdit edit= new ReplaceEdit(pos.offset(), pos.endOffset() - pos.offset() + 1, fNewName);
                        root.addChild(edit);
                        group.addTextEdit(edit);
                    }
                } else  if (n instanceof Call) {
                    Call c= (Call) n;
                    if (c.methodInstance().equals(fDecl)) {
                        Position pos= n.position();
                        Call newCall= c.name(fNewName);
                        final ReplaceEdit edit= new ReplaceEdit(pos.offset(), pos.endOffset() - pos.offset() + 1, newCall.toString());
                        root.addChild(edit);
                        group.addTextEdit(edit);
                    }
                }
                return this;
            }
        };
        fRoot.visit(v);
        tfc.addTextEditGroup(group);
    }

    private void createDeclChange(TextFileChange tfc, MultiTextEdit root) {
        Position pos= fDeclNode.position();
        String newText;

        // RMF 1/29/2007 - Apparently the position of local and field decls contains just the name...
//        if (fDeclNode instanceof LocalDecl) {
//            LocalDecl ld= (LocalDecl) fDeclNode;
//            LocalDecl newDecl= ld.name(fNewName);
//            newText= newDecl.toString();
//        } else if (fDeclNode instanceof FieldDecl) {
//            FieldDecl fd= (FieldDecl) fDeclNode;
//            FieldDecl newDecl= fd.name(fNewName);
//            newText= newDecl.toString();
//        } else
        if (fDeclNode instanceof Formal) {
            Formal f= (Formal) fDeclNode;
            Formal newFormal= f.name(fNewName);
            newText= newFormal.toString();
        } else
            newText= fNewName;

        int startOffset= pos.offset();
	int endOffset= pos.endOffset();

        TextEditGroup group= new TextEditGroup("Rename declaration");

        ReplaceEdit edit= new ReplaceEdit(startOffset, endOffset - startOffset + 1, newText);

        root.addChild(edit);
        group.addTextEdit(edit);
        tfc.addTextEditGroup(group);
    }

    public void setNewName(String newName) {
        fNewName= newName;
    }

    public String getNewName() {
        return fNewName;
    }

    /**
     * @return Returns the curName.
     */
    public String getCurName() {
        return fCurName;
    }
}

class NodePathComputer {
    private static class PathSavingVisitor extends NodeVisitor {
        private final Node fTarget;
        private final List<Node> fPath= new ArrayList<Node>();
        private boolean fHitTarget= false;

        public PathSavingVisitor(Node target) {
            fTarget= target;
        }

        @Override
        public Node leave(Node old, Node n, NodeVisitor v) {
            if (n == fTarget)
                fHitTarget= true;
            if (fHitTarget)
                fPath.add(n);
            return n;
        }

        public Node override(Node n) {
            if (fHitTarget)
                return n;
            return null;
        }

        public List<Node> getPath() {
            return fPath;
        }
    }

    private final NodePathComputer.PathSavingVisitor fVisitor;
    private List<Node> fPath;

    public NodePathComputer(Node root, Node target) {
        fVisitor= new PathSavingVisitor(target);
        root.visit(fVisitor);
        fPath= new ArrayList<Node>(fVisitor.getPath().size());
        fPath.addAll(fVisitor.getPath());
        Collections.reverse(fPath);
    }
    public List<Node> getPath() {
        return fPath;
    }
}
