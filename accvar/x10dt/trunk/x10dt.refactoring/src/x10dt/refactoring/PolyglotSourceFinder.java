/*******************************************************************************
* Copyright (c) 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation

*******************************************************************************/

package x10dt.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.model.ICompilationUnit;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.refactoring.IFileVisitor;
import org.eclipse.imp.refactoring.SourceFileFinder;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;

import polyglot.ast.Node;
import polyglot.visit.NodeVisitor;

/**
 * 
 */
public class PolyglotSourceFinder extends SourceFileFinder {
    private final NodeVisitor fNodeVisitor;

    public abstract class CompilationUnitVisitor extends NodeVisitor {
        protected ICompilationUnit fUnit;

        public CompilationUnitVisitor() { }

        public void beginUnit(ICompilationUnit unit) {
            fUnit= unit;
        }

        public void endUnit() {
            fUnit= null;
        }
    }

    /**
     * @param provider
     * @param project
     * @param visitor
     * @param language
     */
    public PolyglotSourceFinder(TextFileDocumentProvider provider, ISourceProject project, IFileVisitor fileVisitor, NodeVisitor nodeVisitor, Language language) {
        super(provider, project, fileVisitor, language);
        fNodeVisitor= nodeVisitor;
    }

    /* (non-Javadoc)
     * @see safari.X10.refactoring.RenameRefactoring.SourceFileVisitor#doVisit(org.eclipse.core.resources.IFile, org.eclipse.jface.text.IDocument, java.lang.Object)
     */
    @Override
    public void doVisit(IFile file, IDocument doc, Object astRoot) {
        Node srcFileRoot= (Node) astRoot;
        srcFileRoot.visit(fNodeVisitor);
    }
}
