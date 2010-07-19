/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
 *******************************************************************************/

package org.eclipse.imp.x10dt.refactoring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.imp.analysis.search.PolyglotSourceFinder;
import org.eclipse.imp.core.ErrorHandler;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.refactoring.IFileVisitor;
import org.eclipse.imp.refactoring.SourceRange;
import org.eclipse.imp.refactoring.SourceRangeGroup;
import org.eclipse.imp.x10dt.ui.parser.ParseController;
import org.eclipse.imp.x10dt.ui.parser.PolyglotNodeLocator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEditGroup;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import polyglot.ast.Ambiguous;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.Field;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Id_c;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.Stmt;
import polyglot.types.Def;
import polyglot.types.FieldDef;
import polyglot.types.MemberDef;
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;

public class RenameRefactoring extends X10RefactoringBase {
    private Node fDeclNode;
    private Node fDeclParent;
    private Def fDef;
    private String fCurName;
    private String fNewName;

    public RenameRefactoring(ITextEditor editor) {
        super(editor);
    }

    public String getName() {
        return "Rename";
    }

    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        if (fSourceAST == null) {
            return RefactoringStatus.createFatalErrorStatus("Unable to analyze source due to syntax errors");
        }
        if (fSelNodes.size() != 1) {
            return RefactoringStatus.createFatalErrorStatus("You must select a single identifier to rename.");
        }
        Def decl;
        Node node= fSelNodes.get(0);

        if (node instanceof Ambiguous) {
            return RefactoringStatus.createFatalErrorStatus("Cannot rename an ambiguous entity (failed name resolution due to compile errors).");
        }

        RefactoringStatus status= new RefactoringStatus();

        if (node instanceof Id) {
            // If the selected node is an Id, we need to look at its parent
            PolyglotNodeLocator locator= (PolyglotNodeLocator) fNodeLocator;
            Node parent= (Node) locator.getParentNodeOf(node, fSourceAST);

            node= parent;
        }

        if (node instanceof Field) {
            FieldDef fieldDef= ((Field) node).fieldInstance().def();
            status= checkPrivate(fieldDef);
            decl= fieldDef;
        } else if (node instanceof FieldDecl) {
            FieldDef fieldDef= ((FieldDecl) node).fieldDef();
            status= checkPrivate(fieldDef);
            decl = fieldDef;
        } else if (node instanceof Formal) {
            decl= ((Formal) node).localDef();
        } else if (node instanceof Local) {
            decl= ((Local) node).localInstance().def();
        } else if (node instanceof LocalDecl) {
            decl = ((LocalDecl) node).localDef();
        } else if (node instanceof MethodDecl) {
            MethodDef methodDef= ((MethodDecl) node).methodDef();
            status= checkPrivate(methodDef);
            decl = methodDef;
        } else if (node instanceof Call) {            
            MethodDef methodDef= ((Call) node).methodInstance().def();
            status= checkPrivate(methodDef);
            decl= methodDef;
        } else {
            return RefactoringStatus.createFatalErrorStatus("Renaming of entities other than local variables, fields and methods not yet implemented.");
        }

        if (!status.isOK()) {
            return status;
        }

        if (declInSameFile(decl)) {
            findDeclaration(fSourceAST, decl);
        } else {
            findExternalDecl(decl);
        }

        if (fDeclNode == null) {
            return RefactoringStatus.createFatalErrorStatus("Unable to determine the selected entity.");
        }

        if (fDeclNode instanceof LocalDecl) {
            LocalDecl localDecl= (LocalDecl) fDeclNode;
            fDef= localDecl.localDef();
            fCurName= localDecl.name().toString();
        } else if (fDeclNode instanceof FieldDecl) {
            FieldDecl fieldDecl= (FieldDecl) fDeclNode;
            fDef= fieldDecl.fieldDef();
            fCurName= fieldDecl.name().toString();
        } else if (fDeclNode instanceof Formal) {
            Formal f= (Formal) fDeclNode;
            fDef = f.localDef();
            fCurName= f.name().toString();
        } else if (fDeclNode instanceof MethodDecl) {
            MethodDecl method= (MethodDecl) fDeclNode;
            fDef= method.methodDef();
            fCurName= method.name().toString();
        } else {
            fDef= null;
            fCurName= "";
        }

        return new RefactoringStatus();
    }

    /**
     * @return true if the given decl is in the same file as the current selection
     */
    private boolean declInSameFile(Def decl) {
//      IFileEditorInput fileInput= (IFileEditorInput) fEditor.getEditorInput();

//      return decl.position().file().equals(fileInput.getFile().getLocation().toOSString());
        return decl.position().file().equals(fSourceAST.position().file()) &&
               decl.position().path().equals(fSourceAST.position().path());
    }

    /**
     * @param decl
     * @return
     */
    private void findDeclaration(final Node root, final Def decl) {     //PORT1.7 Declaration -> Def
        NodeVisitor visitor= new NodeVisitor() {
            /* (non-Javadoc)
             * @see polyglot.visit.NodeVisitor#enter(polyglot.ast.Node)
             */
            @Override
            public NodeVisitor enter(Node parent, Node n) {
                if (n instanceof LocalDecl) {
                    if (((LocalDecl) n).localDef().equals(decl)) {    //PORT1.7 localInstance() -> localDef()
                        fDeclNode= n;
                        fDeclParent= parent;
                    }
                } else if (n instanceof FieldDecl) {
                    if (((FieldDecl) n).fieldDef().equals(decl)) {  //PORT1.7 fieldInstance()->fieldDef()
                        fDeclNode= n;
                        fDeclParent= parent;
                    }
                } else if (n instanceof Formal) {
                    if (((Formal) n).localDef() .equals(decl)) {   //PORT1.7 localInstance()->localDef()
                        fDeclNode= n;
                        fDeclParent= parent;
                    }
                } else if (n instanceof MethodDecl) {
                    if (((MethodDecl) n).methodDef().equals(decl)) {    //PORT1.7  methodInstance()->methodDef()
                        fDeclNode= n;
                        fDeclParent= parent;
                    }
                }
                return this;
            }
        };
        root.visit(visitor);
    }

    private RefactoringStatus checkPrivate(MemberDef memberDef) {
        // Short-term check, until we have the search index in place to find all refs in all files.
        if (memberDef.flags().isPrivate()) {
            return new RefactoringStatus();
        } else {
            return RefactoringStatus.createFatalErrorStatus("Renaming of non-private members not yet supported (but will be soon).");
        }
    }

    /**
     * Checks for name collision between the proposed name and any existing local variables visible in the same scope, and if so, returns a fatal
     * RefactoringStatus. Otherwise, returns an ok status.
     */
    private RefactoringStatus checkForLocalCollision() {
        NodePathComputer pathComp= new NodePathComputer(fSourceAST, fDeclNode);
        List<Node> path= pathComp.getPath();
        List<Node> revPath= new ArrayList<Node>(path.size());

        revPath.addAll(path);
        Collections.reverse(revPath);

        for(Node n: revPath) {
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
            if (ld.name().id().toString().equals(name))
                return true;
        } else if (n instanceof MethodDecl) {
            MethodDecl md= (MethodDecl) n;
            List<Formal> formals= md.formals();
            for(Formal formal : formals) {
                if (formal.name().id().toString().equals(name))
                    return true;
            }
        }
        return false;
    }

    /**
     * Checks for name collision between the proposed name and any existing fields defined in the same type, and if so, returns a fatal RefactoringStatus.
     * Otherwise, returns an ok status.
     */
    private RefactoringStatus checkForFieldCollision() {
        ClassBody body= null;

        if (fDeclParent instanceof ClassDecl)
            body= ((ClassDecl) fDeclParent).body();
        else if (fDeclParent instanceof ClassBody)
            body= (ClassBody) fDeclParent;

        List<ClassMember> members= body.members();

        for(ClassMember member : members) {
            if (member instanceof FieldDecl) {
                FieldDecl fd= (FieldDecl) member;
                if (fd.name().id().toString().equals(fNewName))
                    return RefactoringStatus.createFatalErrorStatus("Name collision: field '" + fNewName + "' already exists in parent type.");
            }
        }
        return new RefactoringStatus();
    }

    /**
     * Checks for name collision between the proposed name and any existing methods of the same signature defined in the same type, and if so, returns a fatal
     * RefactoringStatus. Otherwise, returns an ok status.
     */
    private RefactoringStatus checkForMethodCollision() {
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
                if (md.name().id().toString().equals(fNewName))
                    return RefactoringStatus.createFatalErrorStatus("Name collision: method '" + fNewName + "' already exists in parent type.");
            }
        }
        return new RefactoringStatus();
    }

    /**
     * Checks for name collision between the proposed name and any existing formal parameters defined by the same method, and if so, returns a fatal
     * RefactoringStatus. Otherwise, returns an ok status.
     */
    private RefactoringStatus checkForFormalCollision() {
        ProcedureDecl method= (ProcedureDecl) fDeclParent;
        List<Formal> formals= method.formals();
        for(Formal formal : formals) {
            if (formal.name().id().toString().equals(fNewName))
                return RefactoringStatus.createFatalErrorStatus("Name collision: formal parameter '" + fNewName + "' already exists in parent method.");
        }
        return new RefactoringStatus();
    }

    /**
     * @param decl
     */
    private void findExternalDecl(Def decl) {
        final Position position= decl.position();

//      System.out.println("declaration is located in " + position.file() + ": " + position);

        IEditorInput input= fEditor.getEditorInput();

        if (isBinary(decl)) {
            MessageDialog.openError(fEditor.getSite().getShell(), "Cannot rename", "The given entity is defined in a binary file and cannot be changed.");
            return;
        }

        if (input instanceof IFileEditorInput) {
            IFileEditorInput fileInput= (IFileEditorInput) input;
            IProject project= fileInput.getFile().getProject();
            IParseController parseCtrlr= new ParseController();
            // TODO Handle cross-project references
            IPath declFilePath= new Path(position.file()).removeFirstSegments(project.getLocation().segmentCount());
            try {
                ISourceProject srcProject= ModelFactory.open(project);

                parseCtrlr.initialize(declFilePath, srcProject, null);
                IDocument document= fEditor.getDocumentProvider().getDocument(fileInput);
                Node declRoot= (Node) parseCtrlr.parse(document.get(), null);

//              System.out.println("Root of AST containing declaration: " + declRoot);
                findDeclaration(declRoot, decl);
//              System.out.println(fDeclNode);
            } catch (ModelException e) {
                ErrorHandler.reportError(e.getMessage(), e);
            }
        }
    }

    private boolean isBinary(Def decl) {    //PORT1.7   Declaration -> Def
        final String file= decl.position().file();
        return file.endsWith(".class") || file.endsWith(".jar");
    }

    public RefactoringStatus checkFinalConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        // findAllReferences();
        if (fDeclNode instanceof LocalDecl)
            return checkForLocalCollision();
        else if (fDeclNode instanceof FieldDecl)
            return checkForFieldCollision();
        else if (fDeclNode instanceof MethodDecl)
            return checkForMethodCollision();
        else if (fDeclNode instanceof Formal)
            return checkForFormalCollision();
        return new RefactoringStatus();
    }

    /**
     * Class to collect references to a given <code>Declaration</code>. Ultimately, this really belongs in the search indexing mechanism.
     */
    private static class ReferenceVisitor extends NodeVisitor implements IFileVisitor {
        private final Def fDecl; // PORT1.7 Declaration -> Def

        private final Set<SourceRangeGroup> fMatchGroups;

        private SourceRangeGroup fMatchGroup;

        public ReferenceVisitor(Def decl, Set<SourceRangeGroup> matchGroups) { // PORT1.7 Declaration -> Def
            fDecl= decl;
            fMatchGroups= matchGroups;
        }

        public void enterFile(IFile file) {
            fMatchGroup= new SourceRangeGroup(file);
            fMatchGroups.add(fMatchGroup);
        }

        /*
         * (non-Javadoc)
         * 
         * @see safari.X10.refactoring.RenameRefactoring.FileVisitor#leaveFile(org.eclipse.core.resources.IFile)
         */
        public void leaveFile(IFile file) {
        // do nothing
        }

        /*
         * (non-Javadoc)
         * 
         * @see polyglot.visit.NodeVisitor#enter(polyglot.ast.Node)
         */
        @Override
        public NodeVisitor enter(Node n) {
            if (n instanceof Local) {
                Local l= (Local) n;
                if (l.localInstance().equals(fDecl)) {
                    Position pos= n.position();
                    fMatchGroup.addReference(new SourceRange(pos.offset(), pos.endOffset() - pos.offset() + 1));
                }
            } else if (n instanceof Field) {
                Field f= (Field) n;
                if (f.fieldInstance().equals(fDecl)) {
                    Position pos= n.position();
                    fMatchGroup.addReference(new SourceRange(pos.offset(), pos.endOffset() - pos.offset() + 1));
                }
            } else if (n instanceof Call) {
                Call c= (Call) n;
                if (c.methodInstance().equals(fDecl)) {
                    Position pos= n.position();
                    fMatchGroup.addReference(new SourceRange(pos.offset(), pos.endOffset() - pos.offset() + 1));
                }
            }
            return this;
        }
    };

    private void findAllReferences() {
//      System.out.println("Examining project " + fSourceFile.getProject().getName() + " for matches.");
        final Set<SourceRangeGroup> allMatches= new HashSet<SourceRangeGroup>();
        try {
            ReferenceVisitor refVisitor= new ReferenceVisitor(fDef, allMatches);
            ISourceProject srcProject= ModelFactory.create(fSourceFile.getProject());
            fSourceFile.getProject().accept(
                    new PolyglotSourceFinder(new TextFileDocumentProvider(), srcProject, refVisitor, refVisitor, LanguageRegistry.findLanguage("X10")));
        } catch (CoreException e) {
            e.printStackTrace();
        } catch (ModelException e) {
            e.printStackTrace();
        }
//      for(SourceRangeGroup group : allMatches) {
//          System.out.println("Matches in " + group.fFile.getName() + ": ");
//          for(SourceRange range : group.fRanges) {
//              System.out.print(range);
//          }
//          System.out.println();
//      }
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
            /*
             * (non-Javadoc)
             * 
             * @see polyglot.visit.NodeVisitor#enter(polyglot.ast.Node)
             */
            @Override
            public NodeVisitor enter(Node n) {
                if (n instanceof Local) {
                    Local l= (Local) n;
                    if (l.localInstance().def().equals(fDef)) {
                        Position pos= n.position();
                        final ReplaceEdit edit= new ReplaceEdit(pos.offset(), pos.endOffset() - pos.offset() + 1, fNewName);
                        root.addChild(edit);
                        group.addTextEdit(edit);
                    }
                } else if (n instanceof Field) {
                    Field f= (Field) n;
                    if (f.fieldInstance().def().equals(fDef)) {
                        Position pos= n.position();
                        final ReplaceEdit edit= new ReplaceEdit(pos.offset(), pos.endOffset() - pos.offset() + 1, fNewName);
                        root.addChild(edit);
                        group.addTextEdit(edit);
                    }
                } else if (n instanceof Call) {
                    Call c= (Call) n;
                    if (c.methodInstance().def().equals(fDef)) {
                        Position pos= n.position();
                        Call newCall= c.name(new Id_c(pos, Name.make(fNewName)));//PORT1.7 was c.name(fNewName);
                        final ReplaceEdit edit= new ReplaceEdit(pos.offset(), pos.endOffset() - pos.offset() + 1, newCall.toString());
                        root.addChild(edit);
                        group.addTextEdit(edit);
                    }
                }
                return this;
            }
        };
        fSourceAST.visit(v);
        tfc.addTextEditGroup(group);
    }

    private void createDeclChange(TextFileChange tfc, MultiTextEdit root) {
        Position pos= fDeclNode.position();
        String newText;

        if (fDeclNode instanceof LocalDecl) {
            // RMF 7/29/2008 - Apparently the position of a local decl contains only the name and the initializer...
            LocalDecl ld= (LocalDecl) fDeclNode;
//          LocalDecl newDecl= ld.name(fNewName);
//          newText= newDecl.toString();

            newText= fNewName;
            pos= ld.name().position();//PORT1.7 was ld.id().position()
        } else if (fDeclNode instanceof FieldDecl) {
            FieldDecl fd= (FieldDecl) fDeclNode;
//          Position pos2 = fd.position();
//          FieldDecl newDecl= fd.name(new Id_c(pos2, Name.make(fNewName)));//PORT1.7 was fd.name(fNewName);

            pos= fd.name().position();
            newText= fNewName; // newDecl.toString();
        } else if (fDeclNode instanceof Formal) {
            Formal f= (Formal) fDeclNode;
//          Position pos3=f.position();
//          Formal newFormal= f.name(new Id_c(pos3,Name.make(fNewName)));//PORT1.7 was f.name(fNewName);
            pos= f.name().position();
            newText= fNewName; // newFormal.toString();
        } else if (fDeclNode instanceof MethodDecl) {
            MethodDecl m= (MethodDecl) fDeclNode;
            Id name= m.name();

            pos= name.position();
            newText= fNewName;
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
            if (old == fTarget)
                fHitTarget= true;
            if (fHitTarget)
                fPath.add(old);
            return old;
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
