/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.imp.x10dt.refactoring.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.parser.IMessageHandler;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.utils.ConsoleUtil;
import org.eclipse.imp.utils.StreamUtils;
import org.eclipse.imp.x10dt.ui.parser.ParseController;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.SourceFile;
import polyglot.ast.TopLevelDecl;
import x10.ast.X10ProcedureDecl;
import x10.constraint.XFailure;
import x10.effects.EffectsVisitor;
import x10.effects.constraints.Effect;

/**
 * An action class that runs the memory effects analyzer on each X10 source file in the
 * currently-selected project.
 */
public class AnalyzeEffects implements IWorkbenchWindowActionDelegate {
    private class ConsoleMessageHandler implements IMessageHandler {
        public void clearMessages() {}

        public void endMessageGroup() {}

        public void handleSimpleMessage(String msg, int startOffset, int endOffset,
                int startCol, int endCol, int startLine, int endLine) {
            fConsStream.println(startLine + ":" + startCol + "," + endLine + ":" + endCol + ": " + msg);
        }

        public void startMessageGroup(String groupName) {}
    }

    private class SourceFileScanner implements IResourceVisitor {
        private final ISourceProject fSrcProject;

        private final IParseController fParseCtrlr;

        private final IProgressMonitor fMonitor;

        private final ConsoleMessageHandler fMsgHandler;

        private SourceFileScanner(ISourceProject srcProject, IParseController parseCtrlr, IProgressMonitor monitor,
                ConsoleMessageHandler msgHandler) {
            fSrcProject= srcProject;
            fParseCtrlr= parseCtrlr;
            fMonitor= monitor;
            fMsgHandler= msgHandler;
        }

        public boolean visit(IResource resource) throws CoreException {
            if (resource instanceof IFile) {
                final IFile file= (IFile) resource;
                if ("x10".equals(file.getFileExtension())) {
                    processSourceFile(file);
                }
                return false;
            } else if (resource instanceof IFolder) {
                if (resource.getName().equals("bin")) {
                    return false;
                }
                fConsStream.println("Entering folder " + resource.getFullPath().toPortableString());
            }
            return true;
        }

        private void processSourceFile(final IFile file) {
            fConsStream.println();
            fConsStream.println("Processing source file " + file.getFullPath());
            try {
                FileInputStream is= new FileInputStream(new File(file.getLocation().toOSString()));
                String contents= StreamUtils.readStreamContents(is);

                fParseCtrlr.initialize(file.getFullPath().removeFirstSegments(1), fSrcProject, fMsgHandler);
                SourceFile astRoot= (SourceFile) fParseCtrlr.parse(contents, fMonitor);

                if (astRoot == null) {
                    fConsStream.println("  *** Source file failed to parse.");
                    return;
                }

                List<TopLevelDecl> decls= astRoot.decls();

                for(TopLevelDecl decl: decls) {
                    if (decl instanceof ClassDecl) {
                        processClass((ClassDecl) decl);
                    }
                }
            } catch (FileNotFoundException e) {
                fConsStream.println("  *** File not found: " + file.getName());
            } catch (Exception e) {
                fConsStream.println("  *** Exception caught: " + e.getMessage());
                e.printStackTrace(fConsStream);
            }
        }

        private void processClass(ClassDecl decl) {
            List<ClassMember> members= decl.body().members();

            for(ClassMember member: members) {
                if (member instanceof ProcedureDecl) {
                    X10ProcedureDecl procedureDecl= (X10ProcedureDecl) member;
                    String procID= procedureDecl.name() + "(" + procedureDecl.formals() + ")";
                    Effect bodyEff;

                    fConsStream.println("  Analyzing procedure " + procID);

                    try {
                        EffectsVisitor ev= new EffectsVisitor(procedureDecl /*, null*/);

                        procedureDecl.visit(ev);
                        bodyEff= ev.getEffectFor(procedureDecl.body());

                        fConsStream.println("    Effect = " + bodyEff);
                    } catch (XFailure e) {
                        fConsStream.println("  *** Exception caught while processing procedure " + procID);
                        e.printStackTrace(fConsStream);
                    }
                }
            }
        }
    }

    private IProject fProject;

    private PrintStream fConsStream= ConsoleUtil.findConsoleStream("Effects Analyzer");

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    public void dispose() {}

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init(IWorkbenchWindow window) {}

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        try {
            final IParseController fParseCtrlr= new ParseController();
            final ISourceProject fSrcProject= ModelFactory.open(fProject);
            final ConsoleMessageHandler msgHandler= new ConsoleMessageHandler();
            final IProgressMonitor monitor= new NullProgressMonitor();

            fProject.getWorkspace().run(new IWorkspaceRunnable() {
                public void run(IProgressMonitor monitor) throws CoreException {
                    fProject.accept(new SourceFileScanner(fSrcProject, fParseCtrlr, monitor, msgHandler));
                }
            }, monitor);
        } catch (CoreException e) {
            e.printStackTrace(fConsStream);
        } catch (ModelException e) {
            e.printStackTrace(fConsStream);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     * org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection ss= (IStructuredSelection) selection;
            Object first= ss.getFirstElement();
            if (first instanceof IProject) {
                fProject= (IProject) first;
            } else if (first instanceof IJavaProject) {
                fProject= ((IJavaProject) first).getProject();
            }
        }
    }
}
