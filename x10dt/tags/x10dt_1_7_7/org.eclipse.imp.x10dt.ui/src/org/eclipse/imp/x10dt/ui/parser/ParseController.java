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

package org.eclipse.imp.x10dt.ui.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lpg.runtime.IToken;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.parser.ILexer;
import org.eclipse.imp.parser.IMessageHandler;
import org.eclipse.imp.parser.IParser;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.eclipse.imp.parser.SimpleLPGParseController;
import org.eclipse.imp.services.ILanguageSyntaxProperties;
import org.eclipse.imp.x10dt.core.X10DTCorePlugin;
import org.eclipse.jface.text.IRegion;

import polyglot.frontend.FileSource;
import polyglot.frontend.Globals;
import polyglot.frontend.Source;

public class ParseController extends SimpleLPGParseController {
    private CompilerDelegate fCompiler;
    private PMMonitor fMonitor;

    public ParseController() {
    	super(X10DTCorePlugin.kLanguageName);
    }

    public IParser getParser() {
    	return fParser;
    }

    public ILexer getLexer() {
        return fLexer;
    }

    public CompilerDelegate getCompiler() {
        initializeGlobalsIfNeeded();
        return fCompiler;
    }

    @Override
    public ISourcePositionLocator getSourcePositionLocator() {
    	return new PolyglotNodeLocator(fProject, getLexer() != null ? getLexer().getILexStream() : null);
    }

    public ILanguageSyntaxProperties getSyntaxProperties() {
        return new X10SyntaxProperties();
    }

    @Override
    public void initialize(IPath filePath, ISourceProject project, IMessageHandler handler) {
        super.initialize(filePath, project, handler);
        fMonitor= new PMMonitor(null);
    }

    public Object parse(String contents, IProgressMonitor monitor) {
        FileSource fileSource= null;
        try {
            fMonitor.setMonitor(monitor);
            
            String path= fFilePath.toOSString();
            File file= new File(fProject != null ? fProject.getRawProject().getLocation().append(fFilePath).toString() : path);

            fileSource= new FileSource(new StringResource(contents, file, path));

            List<Source> streams= new ArrayList<Source>();
            // Bug 526: NPE when opening a file outside the workspace due to null fProject.
            IProject proj= (fProject != null) ? fProject.getRawProject() : null;

            streams.add(fileSource);
            fCompiler= new CompilerDelegate(fMonitor, handler, proj); // Create the compiler
            // RMF 5/11/2009 - Make sure to create new parser/lexer delegates, so that no one
            // gets stale information (e.g. prev token stream) if they go through the delegates.
            fCompiler.compile(streams);
            fParser= new ParserDelegate(fCompiler.getParserFor(fileSource));
            fLexer= new LexerDelegate(fCompiler.getLexerFor(fileSource));
        } catch (IOException e) {
            throw new Error(e);
        } finally {
            // RMF 8/2/2006 - retrieve the AST if there is one; some later phase of compilation
            // may fail, even though the AST is well-formed enough to provide an outline.
            if (fileSource != null) {
                fCurrentAst= fCompiler.getASTFor(fileSource);
            }
            // RMF 8/2/2006 - cacheKeywordsOnce() must have been run for syntax highlighting to work.
            // Must do this after attempting parsing (even though that might fail), since it depends
            // on the parser/lexer being set in the ExtensionInfo, which only happens as a result of
            // ExtensionInfo.parser(). Ugghh.
            if (fParser != null) { //PORT1.7
            	cacheKeywordsOnce();
            }
        }
        return fCurrentAst;
    }

    /**
     * Polyglot has a thread-local variable that must be initialized in each thread
     * that uses the front end. The Eclipse platform creates many threads that may
     * access the compiler front end (on behalf of various services like hover help
     * or the documentation provider), and we don't have control over where/when this
     * happens. As a result, this method is used to initialize the thread-local variable
     * from within those accessor methods that give the client access to the front-end.
     */
    private void initializeGlobalsIfNeeded() {
        if (Globals.Compiler() == null) {
            Globals.initialize(fCompiler.getCompiler());
        }
    }

    @Override
    public Object getCurrentAst() {
        initializeGlobalsIfNeeded();
        return super.getCurrentAst();
    }

    @Override
    public Iterator<IToken> getTokenIterator(IRegion region) {
        initializeGlobalsIfNeeded();
        return super.getTokenIterator(region);
    }
}
