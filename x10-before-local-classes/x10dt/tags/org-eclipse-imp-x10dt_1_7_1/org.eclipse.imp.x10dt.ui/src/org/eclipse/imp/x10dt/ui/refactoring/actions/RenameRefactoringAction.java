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
package org.eclipse.imp.x10dt.ui.refactoring.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.imp.core.ErrorHandler;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.refactoring.RefactoringStarter;
import org.eclipse.imp.x10dt.ui.parser.ParseController;
import org.eclipse.imp.x10dt.ui.parser.PolyglotNodeLocator;
import org.eclipse.imp.x10dt.ui.refactoring.RefactoringMessages;
import org.eclipse.imp.x10dt.ui.refactoring.RenameRefactoring;
import org.eclipse.imp.x10dt.ui.refactoring.RenameWizard;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

import polyglot.ast.Ambiguous;
import polyglot.ast.Call;
import polyglot.ast.Field;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.types.Def;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;

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
        ISourcePositionLocator locator= parseController.getNodeLocator();

        return (Node) locator.findNode(fRoot, sel.x);
    }

    public void run() {
        final Shell shell= getTextEditor().getSite().getShell();

        if (fNode instanceof Ambiguous) {
            MessageDialog.openError(shell, "Cannot rename", "Cannot rename ambiguous entity (failed name resolution due to compile errors).");
            return;
        }

        Def decl;//PORT1.7 Declaration -> Def
        Node node= fNode;

        if (node instanceof Id) {
            UniversalEditor editor= (UniversalEditor) getTextEditor();
            IParseController parseController= editor.getParseController();
            PolyglotNodeLocator locator= (PolyglotNodeLocator) parseController.getNodeLocator();
            Node parent= (Node) locator.getParentNodeOf(node, fRoot);

            node= parent;
        }
        if (node instanceof Local) {
			decl= ((Local) node).localInstance().def();    //PORT1.7  localInstance()->localInstance().def();
        } else if (node instanceof Field) {
			decl= ((Field) node).fieldInstance().def();    //PORT1.7  fieldInstance()->fieldInstance().def();
        } else if (node instanceof Formal) {
            Formal formal = (Formal) node;
            decl=formal.localDef();                        //PORT1.7   localInstance() returning Declaration -> localDef() returning Def
        } else if (node instanceof FieldDecl) {
            FieldDecl fieldDecl = (FieldDecl) node;
            decl = fieldDecl.fieldDef();                   //PORT1.7 fieldInstance()->fieldDef();
        } else if (node instanceof LocalDecl) {
            LocalDecl localDecl = (LocalDecl) node;
            decl = localDecl.localDef();                   //PORT1.7  localInstance()->localDef()
        } else if (node instanceof MethodDecl) {
            MethodDecl methodDecl = (MethodDecl) node;
            decl = methodDecl.methodDef();                 //PORT1.7  methodInstance()->methodDef();
        } else if (node instanceof Call) {            
			decl= ((Call) node).methodInstance().def();    //PORT methodInstance()->methodInstance().def();
//      } else if (fNode instanceof TypeNode) {
        } else {
            MessageDialog.openError(shell, "Cannot rename", "Renaming of entities other than local variables, fields and methods not yet implemented.");
            return;
        }

        IFileEditorInput fileInput= (IFileEditorInput) getTextEditor().getEditorInput();

        if (decl.position().file().equals(fileInput.getFile().getLocation().toOSString()))
            findDeclaration(fRoot, decl);
        else
            findExternalDecl(decl);

        if (fDeclaringNode == null) {
//          MessageDialog.openError(shell, "Cannot rename", "Renaming entities across source files not yet supported.");
            return;
        }

        RenameRefactoring refactoring= new RenameRefactoring(getTextEditor(), fDeclaringNode, fDeclaringParent, fRoot);

        if (refactoring != null)
		new RefactoringStarter().activate(refactoring, new RenameWizard(refactoring, "Rename"), shell, "Rename", false);
    }

    /**
     * @param decl
     */
    private void findExternalDecl(Def decl) {      //PORT1.7 Declaration -> Def
        final Position position= decl.position();

        System.out.println("declaration is located in " + position.file() + ": " + position);

        ITextEditor textEditor= getTextEditor();
        IEditorInput input= textEditor.getEditorInput();

        if (isBinary(decl)) {
            MessageDialog.openError(textEditor.getSite().getShell(), "Cannot rename", "The given entity is defined in a binary file and cannot be changed.");
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
                IDocument document= textEditor.getDocumentProvider().getDocument(fileInput);
                Node declRoot= (Node) parseCtrlr.parse(document.get(), false, null);

                System.out.println("Root of AST containing declaration: " + declRoot);
                findDeclaration(declRoot, decl);
                System.out.println(fDeclaringNode);
            } catch (ModelException e) {
                ErrorHandler.reportError(e.getMessage(), e);
            }
        }
    }

    private boolean isBinary(Def decl) {    //PORT1.7   Declaration -> Def
        final String file= decl.position().file();
        return file.endsWith(".class") || file.endsWith(".jar");
    }

    /**
     * @param decl
     * @return
     */
    private void findDeclaration(Node root, final Def decl) {     //PORT1.7 Declaration -> Def
        NodeVisitor visitor= new NodeVisitor() {
            /* (non-Javadoc)
             * @see polyglot.visit.NodeVisitor#enter(polyglot.ast.Node)
             */
            @Override
            public NodeVisitor enter(Node parent, Node n) {
                if (n instanceof LocalDecl) {
                    if (((LocalDecl) n).localDef().equals(decl)) {    //PORT1.7 localInstance() -> localDef()
                        fDeclaringNode= n;
                        fDeclaringParent= parent;
                    }
                } else if (n instanceof FieldDecl) {
                    if (((FieldDecl) n).fieldDef().equals(decl)) {  //PORT1.7 fieldInstance()->fieldDef()
                        fDeclaringNode= n;
                        fDeclaringParent= parent;
                    }
                } else if (n instanceof Formal) {
                    if (((Formal) n).localDef() .equals(decl)) {   //PORT1.7 localInstance()->localDef()
                        fDeclaringNode= n;
                        fDeclaringParent= parent;
                    }
                } else if (n instanceof MethodDecl) {
                    if (((MethodDecl) n).methodDef().equals(decl)) {    //PORT1.7  methodInstance()->methodDef()
                        fDeclaringNode= n;
                        fDeclaringParent= parent;
                    }
                }
                return this;
            }
        };
        root.visit(visitor);
    }
}
