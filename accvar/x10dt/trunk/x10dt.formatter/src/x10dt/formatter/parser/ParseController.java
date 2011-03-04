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

package x10dt.formatter.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.language.ILanguageService;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.eclipse.imp.parser.MessageHandlerAdapter;
import org.eclipse.imp.parser.SimpleLPGParseController;
import org.eclipse.imp.services.ILanguageSyntaxProperties;

import x10.ExtensionInfo;
import x10.ast.X10NodeFactory;
import x10.ast.X10NodeFactory_c;
import x10.types.X10TypeSystem;
import x10.types.X10TypeSystem_c;
import polyglot.frontend.FileSource;
import polyglot.frontend.Resource;
import polyglot.frontend.Source;
import polyglot.util.ErrorQueue;
import polyglot.util.SilentErrorQueue;
import x10dt.core.X10DTCorePlugin;
import x10dt.formatter.parser.ast.ASTNode;

public class ParseController extends SimpleLPGParseController implements
		IParseController, ILanguageService {
	public ParseController() {
		super(X10DTCorePlugin.kLanguageName);
	}

	/**
	 * setFilePath() should be called before calling this method.
	 */
	public Object parse(String contents, IProgressMonitor monitor) {
		PMMonitor my_monitor = new PMMonitor(monitor);
		char[] contentsArray = contents.toCharArray();

		if (fLexer == null) {
			fLexer = new X10Lexer();
		}
		fLexer.reset(contentsArray, fFilePath.toOSString());
		
		
		
		if (fParser == null) {
			X10TypeSystem ts = new X10TypeSystem_c();
			ExtensionInfo xi = new ExtensionInfo();
			X10NodeFactory nf = new X10NodeFactory_c(xi);
			ErrorQueue eq = new SilentErrorQueue(0,"SilentEQ");
			Source fs;
			fs = new Source("") {};
			fParser = new X10Parser(fLexer.getILexStream(), ts,nf, fs, eq);
			
		}
		
		fParser.reset(fLexer.getILexStream());
		fParser.getIPrsStream().setMessageHandler(new MessageHandlerAdapter(handler));

		fLexer.lexer(my_monitor, fParser.getIPrsStream()); // Lex the stream to produce the token stream
		if (my_monitor.isCancelled())
			return fCurrentAst; // TODO fCurrentAst might (probably will) be
								// inconsistent wrt the lex stream now

		fCurrentAst = fParser.parser(my_monitor, 0);

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
