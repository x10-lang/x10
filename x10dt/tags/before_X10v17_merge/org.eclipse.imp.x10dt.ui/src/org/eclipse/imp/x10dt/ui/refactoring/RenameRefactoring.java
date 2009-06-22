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

/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package org.eclipse.imp.x10dt.ui.refactoring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.imp.analysis.search.PolyglotSourceFinder;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.refactoring.IFileVisitor;
import org.eclipse.imp.refactoring.SourceRange;
import org.eclipse.imp.refactoring.SourceRangeGroup;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEditGroup;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
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

    /**
     * Checks for name collision between the proposed name and any existing local variables visible in the same scope, and if so, returns a fatal
     * RefactoringStatus. Otherwise, returns an ok status.
     */
    private RefactoringStatus checkForLocal() {
        NodePathComputer pathComp= new NodePathComputer(fRoot, fDeclNode);
        List<Node> path= pathComp.getPath();
        List<Node> revPath= new ArrayList<Node>(path.size());

        revPath.addAll(path);
        Collections.reverse(revPath);

        for(Iterator iter= revPath.iterator(); iter.hasNext();) {
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

    /**
     * Checks for name collision between the proposed name and any existing fields defined in the same type, and if so, returns a fatal RefactoringStatus.
     * Otherwise, returns an ok status.
     */
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

    /**
     * Checks for name collision between the proposed name and any existing methods of the same signature defined in the same type, and if so, returns a fatal
     * RefactoringStatus. Otherwise, returns an ok status.
     */
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

    /**
     * Checks for name collision between the proposed name and any existing formal parameters defined by the same method, and if so, returns a fatal
     * RefactoringStatus. Otherwise, returns an ok status.
     */
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
        // findAllReferences();
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

    /**
     * Class to collect references to a given <code>Declaration</code>. Ultimately, this really belongs in the search indexing mechanism.
     */
    private static class ReferenceVisitor extends NodeVisitor implements IFileVisitor {
        private final Declaration fDecl;

        private final Set<SourceRangeGroup> fMatchGroups;

        private SourceRangeGroup fMatchGroup;

        public ReferenceVisitor(Declaration decl, Set<SourceRangeGroup> matchGroups) {
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
        System.out.println("Examining project " + fSourceFile.getProject().getName() + " for matches.");
        final Set<SourceRangeGroup> allMatches= new HashSet<SourceRangeGroup>();
        try {
            ReferenceVisitor refVisitor= new ReferenceVisitor(fDecl, allMatches);
            ISourceProject srcProject= ModelFactory.create(fSourceFile.getProject());
            fSourceFile.getProject().accept(
                    new PolyglotSourceFinder(new TextFileDocumentProvider(), srcProject, refVisitor, refVisitor, LanguageRegistry.findLanguage("X10")));
        } catch (CoreException e) {
            e.printStackTrace();
        } catch (ModelException e) {
            e.printStackTrace();
        }
        for(SourceRangeGroup group : allMatches) {
            System.out.println("Matches in " + group.fFile.getName() + ": ");
            for(SourceRange range : group.fRanges) {
                System.out.print(range);
            }
            System.out.println();
        }
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
                    if (l.localInstance().equals(fDecl)) {
                        Position pos= n.position();
                        final ReplaceEdit edit= new ReplaceEdit(pos.offset(), pos.endOffset() - pos.offset() + 1, fNewName);
                        root.addChild(edit);
                        group.addTextEdit(edit);
                    }
                } else if (n instanceof Field) {
                    Field f= (Field) n;
                    if (f.fieldInstance().equals(fDecl)) {
                        Position pos= n.position();
                        final ReplaceEdit edit= new ReplaceEdit(pos.offset(), pos.endOffset() - pos.offset() + 1, fNewName);
                        root.addChild(edit);
                        group.addTextEdit(edit);
                    }
                } else if (n instanceof Call) {
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

        if (fDeclNode instanceof LocalDecl) {
            // RMF 7/29/2008 - Apparently the position of a local decl contains only the name and the initializer...
            LocalDecl ld= (LocalDecl) fDeclNode;
//          LocalDecl newDecl= ld.name(fNewName);
//          newText= newDecl.toString();

            newText= fNewName;
            pos= ld.id().position();
        } else if (fDeclNode instanceof FieldDecl) {
            FieldDecl fd= (FieldDecl) fDeclNode;
            FieldDecl newDecl= fd.name(fNewName);

            newText= newDecl.toString();
        } else if (fDeclNode instanceof Formal) {
            Formal f= (Formal) fDeclNode;
            Formal newFormal= f.name(fNewName);
            newText= newFormal.toString();
        } else if (fDeclNode instanceof MethodDecl) {
            MethodDecl m= (MethodDecl) fDeclNode;
            // RMF 1/30/2007 - If we knew the position of the name itself, we'd rewrite
            // only that, but we don't (pending enhancement to polyglot AST's).
            if (false)
                ;
            // pos= m.name().position();
            else {
                // The following is *BAD* - we have no way of knowing what extra info
                // might be needed on a method signature, so we should *REALLY* stick
                // to rewriting the method name.
                // Just write out the whole signature. Oddly, modifiers aren't included
                // in the method's textual extent as given by fDeclNode.position().
                StringBuffer buff= new StringBuffer();
                buff.append(m.returnType().name()).append(' ').append(fNewName).append('(');
                List<Formal> formals= m.formals();

                for(Iterator<Formal> iter= formals.iterator(); iter.hasNext();) {
                    Formal formal= iter.next();

                    buff.append(formal.flags()).append(formal.type().name()).append(' ').append(formal.name());
                    if (iter.hasNext())
                        buff.append(", ");
                }
                buff.append(')');
                newText= buff.toString();
            }
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
