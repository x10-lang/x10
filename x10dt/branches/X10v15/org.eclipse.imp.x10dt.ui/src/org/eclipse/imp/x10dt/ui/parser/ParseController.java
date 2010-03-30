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
package org.eclipse.imp.x10dt.ui.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.imp.x10dt.core.X10Plugin;

import polyglot.ast.Node;
import polyglot.frontend.FileSource;
import polyglot.frontend.Job;

public class ParseController extends SimpleLPGParseController {
	private CompilerDelegate fCompiler;
	private PMMonitor fMonitor;

	public ParseController() {
		super(X10Plugin.kLanguageName);
	}

	public CompilerDelegate getCompilerDelegate() {
		return fCompiler;
	}

	public IParser getParser() {
		return new ParserDelegate(fCompiler.getParser());
	}

	public ILexer getLexer() {
		return new LexerDelegate(fCompiler.getLexer());
	}

	public ISourcePositionLocator getSourcePositionLocator() {
		return new PolyglotNodeLocator(fProject, null /* getLexer().getLexStream() */);
	}

	public ILanguageSyntaxProperties getSyntaxProperties() {
		return new X10SyntaxProperties();
	}

	@Override
	public void initialize(IPath filePath, ISourceProject project,
			IMessageHandler handler) {
		super.initialize(filePath, project, handler);
		fMonitor = new PMMonitor(null);
	}

	public Object parse(String contents, IProgressMonitor monitor) {
		FileSource fileSource = null;
		try {
			fMonitor.setMonitor(monitor);

			String path = fFilePath.toOSString();
			File file = new File(fProject != null ? fProject.getRawProject()
					.getLocation().append(fFilePath).toString() : path);

			fileSource = new StringSource(contents, file, path);

			List<FileSource> streams = new ArrayList<FileSource>();
			// Bug 526: NPE when opening a file outside the workspace due to
			// null fProject.
			IProject proj = (fProject != null) ? fProject.getRawProject()
					: null;

			streams.add(fileSource); // PC: just to test...
			fCompiler = new CompilerDelegate(fMonitor, handler, proj); // Create
																		// the
																		// compiler
			fCompiler.getFrontEnd().compile(streams);
		} catch (IOException e) {
			throw new Error(e);
		} finally {
			// RMF 8/2/2006 - retrieve the AST if there is one; some later phase
			// of compilation
			// may fail, even though the AST is well-formed enough to provide an
			// outline.
			if (fileSource != null) {
				Job job = fCompiler.getJob(fileSource);
				if (job != null) {
					fCurrentAst = (Node) job.ast();
				}
			}
			// RMF 8/2/2006 - cacheKeywordsOnce() must have been run for syntax
			// highlighting to work.
			// Must do this after attempting parsing (even though that might
			// fail), since it depends
			// on the parser/lexer being set in the ExtensionInfo, which only
			// happens as a result of
			// ExtensionInfo.parser(). Ugghh.
			cacheKeywordsOnce();
		}
		return fCurrentAst;
	}
}
