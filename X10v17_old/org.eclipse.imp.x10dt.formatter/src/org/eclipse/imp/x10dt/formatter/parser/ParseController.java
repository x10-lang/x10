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

package org.eclipse.imp.x10dt.formatter.parser;


import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.language.ILanguageService;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.parser.ILexer;
import org.eclipse.imp.parser.IMessageHandler;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.IParser;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.eclipse.imp.parser.MessageHandlerAdapter;
import org.eclipse.imp.parser.SimpleLPGParseController;
import org.eclipse.imp.services.ILanguageSyntaxProperties;
import org.eclipse.imp.x10dt.core.X10Plugin;
import org.eclipse.imp.x10dt.formatter.parser.ast.ASTNode;

public class ParseController extends SimpleLPGParseController implements
		IParseController, ILanguageService {
	private PatternX10Parser parser;
	private PatternX10Lexer lexer;

	/**
	 * @param filePath
	 *            Absolute path of file
	 * @param project
	 *            Project that contains the file
	 * @param handler
	 *            A message handler to receive error messages (or any others)
	 *            from the parser
	 */
	public void initialize(IPath filePath, ISourceProject project,
			IMessageHandler handler) {
		super.initialize(filePath, project, handler);
	}

	public IParser getParser() {
		return parser;
	}

	public ILexer getLexer() {
		return lexer;
	}
	
	public ParseController() {
		super(X10Plugin.kLanguageName);
	}

	/**
	 * setFilePath() should be called before calling this method.
	 */
	public Object parse(String contents, boolean scanOnly,
			IProgressMonitor monitor) {
		PMMonitor my_monitor = new PMMonitor(monitor);
		char[] contentsArray = contents.toCharArray();


		if (lexer == null) {
			lexer = new PatternX10Lexer();
		}
		lexer.reset(contentsArray, fFilePath.toOSString());
		
		if (parser == null) {
			parser = new PatternX10Parser(lexer.getLexStream());
		}
		
		parser.reset(lexer.getLexStream());
		parser.getParseStream().setMessageHandler(new MessageHandlerAdapter(handler));

		lexer.lexer(my_monitor, parser.getParseStream()); // Lex the stream to
															// produce the token
															// stream
		if (my_monitor.isCancelled())
			return fCurrentAst; // TODO fCurrentAst might (probably will) be
								// inconsistent wrt the lex stream now

		fCurrentAst = (ASTNode) parser.parser(my_monitor, 0);

		cacheKeywordsOnce();

		Object result = fCurrentAst;
		return result;
	}

	public ISourcePositionLocator getNodeLocator() {
		// TODO Auto-generated method stub
		return null;
	}

	public ILanguageSyntaxProperties getSyntaxProperties() {
		// TODO Auto-generated method stub
		return null;
	}

}
