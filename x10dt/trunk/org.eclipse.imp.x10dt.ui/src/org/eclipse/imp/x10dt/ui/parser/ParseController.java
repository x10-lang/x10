package x10.uide.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lpg.lpgjavaruntime.IToken;
import lpg.lpgjavaruntime.Monitor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.uide.editor.IMessageHandler;
import org.eclipse.uide.parser.AstLocator;
import org.eclipse.uide.parser.IASTNodeLocator;
import org.eclipse.uide.parser.ILexer;
import org.eclipse.uide.parser.IParseController;
import org.eclipse.uide.parser.IParser;
import org.eclipse.uide.parser.ParseError;

import polyglot.ast.Node;
import polyglot.frontend.FileSource;

public class ParseController implements IParseController
{
    private IProject project;
    private String filePath;
    private CompilerDelegate compiler;
    private Node currentAst;

    private char keywords[][];
    private boolean isKeyword[];

    public void initialize(String filePath, IProject project, IMessageHandler handler) {
        this.project= project;
        this.filePath= filePath;
    }
    public IParser getParser() {
        return new ParserDelegate(compiler.getParser());
    }
    public ILexer getLexer() {
        return new LexerDelegate(compiler.getLexer());
    }
    public Object getCurrentAst() { return currentAst; }
    public char [][] getKeywords() { return keywords; }
    public boolean isKeyword(int kind) { return isKeyword[kind]; }
    public int getTokenIndexAtCharacter(int offset)
    {
        int index = getParser().getParseStream().getTokenIndexAtCharacter(offset);
        return (index < 0 ? -index : index);
    }
    public IToken getTokenAtCharacter(int offset) {
    	return getParser().getParseStream().getTokenAtCharacter(offset);
    }
    public IASTNodeLocator getNodeLocator() { return new PolyglotNodeLocator(getLexer().getLexStream()); }

    public boolean hasErrors() { return currentAst == null; }
    public List getErrors() { return Collections.singletonList(new ParseError("parse error", null)); }
    
    public ParseController() {
System.out.println("creating ParseController()");
    }

    class MyMonitor implements Monitor
    {
        IProgressMonitor monitor;
        boolean wasCancelled= false;
        MyMonitor(IProgressMonitor monitor)
        {
            this.monitor = monitor;
        }
        public boolean isCancelled() {
        	if (!wasCancelled)
        		wasCancelled = monitor.isCanceled();
        	return wasCancelled;
        }
    }
    
    /**
     * setFilePath() should be called before calling this method.
     */
    public Object parse(String contents, boolean scanOnly, IProgressMonitor monitor)
    {
        FileSource fileSource;
        try
        {
            MyMonitor my_monitor = new MyMonitor(monitor);
            compiler = new CompilerDelegate(my_monitor, project);  // Create the compiler
            fileSource= new SafariFileSource(contents,
                                             new File(project.getLocation().append(filePath).toString()),
                                             filePath);
            List/*<SourceStream>*/ streams= new ArrayList();
            streams.add(fileSource); //PC: just to test...
            // TODO: call compiler... cache the Ast in the parser so that it can be retrieve.
            compiler.getFrontEnd().compile(streams);
        }
        catch (IOException e)
        {
            throw new Error(e);
        }
        currentAst = (Node) compiler.getJob(fileSource).ast();

        cacheKeywordsOnce(); // better place/time to do this?
        return currentAst;
    }

    private void cacheKeywordsOnce() {
        if (keywords == null) {
            String tokenKindNames[] = getParser().getParseStream().orderedTerminalSymbols();
            this.isKeyword = new boolean[tokenKindNames.length];
            this.keywords = new char[tokenKindNames.length][];

            int [] keywordKinds = getLexer().getKeywordKinds();
            for (int i = 1; i < keywordKinds.length; i++)
            {
                int index = getParser().getParseStream().mapKind(keywordKinds[i]);

                isKeyword[index] = true;
                keywords[index] = getParser().getParseStream().orderedTerminalSymbols()[index].toCharArray();
            }
        }
    }
}
