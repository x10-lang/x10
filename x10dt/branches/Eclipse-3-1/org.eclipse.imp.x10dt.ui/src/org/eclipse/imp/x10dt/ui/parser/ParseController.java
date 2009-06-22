package org.eclipse.imp.x10dt.ui.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.parser.IASTNodeLocator;
import org.eclipse.imp.parser.ILexer;
import org.eclipse.imp.parser.IParser;
import org.eclipse.imp.parser.SimpleLPGParseController;

import polyglot.ast.Node;
import polyglot.frontend.FileSource;

public class ParseController extends SimpleLPGParseController {
    private CompilerDelegate fCompiler;

    public IParser getParser() {
	return new ParserDelegate(fCompiler.getParser());
    }

    public ILexer getLexer() {
	return new LexerDelegate(fCompiler.getLexer());
    }

    public IASTNodeLocator getNodeLocator() {
	return new PolyglotNodeLocator(fProject, getLexer().getLexStream());
    }

    public ParseController() {
	System.out.println("creating ParseController()");
    }

    public Object parse(String contents, boolean scanOnly, IProgressMonitor monitor) {
	FileSource fileSource= null;
	try {
	    PMMonitor my_monitor= new PMMonitor(monitor);
	    fCompiler= new CompilerDelegate(my_monitor, fProject.getRawProject()); // Create the compiler
	    fileSource= new StringSource(contents, new File(fProject != null ? fProject.getRawProject().getLocation().append(fFilePath).toString()
		    : fFilePath.toOSString()), fFilePath.toOSString());
	    List/*<SourceStream>*/streams= new ArrayList();
	    streams.add(fileSource); //PC: just to test...
	    fCompiler.getFrontEnd().compile(streams);
	} catch (IOException e) {
	    throw new Error(e);
	} finally {
	    // RMF 8/2/2006 - retrieve the AST if there is one; some later phase of compilation
	    // may fail, even though the AST is well-formed enough to provide an outline.
	    if (fileSource != null)
		fCurrentAst= (Node) fCompiler.getJob(fileSource).ast();
	    // RMF 8/2/2006 - cacheKeywordsOnce() must have been run for syntax highlighting to work.
	    // Must do this after attempting parsing (even though that might fail), since it depends
	    // on the parser/lexer being set in the ExtensionInfo, which only happens as a result of
	    // ExtensionInfo.parser(). Ugghh.
	    cacheKeywordsOnce();
	}
	return fCurrentAst;
    }
}
