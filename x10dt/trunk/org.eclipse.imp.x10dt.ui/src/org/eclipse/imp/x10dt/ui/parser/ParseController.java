package x10.uide.parser;

import java.io.IOException;
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

public class ParseController implements IParseController
{
    private IProject project;
    private String filePath;
    private ParserDelegate parser;
    private LexerDelegate lexer;
    private Node currentAst;

    private char keywords[][];
    private boolean isKeyword[];

    public void initialize(String filePath, IProject project, IMessageHandler handler) {
        this.project= project;
        this.filePath= filePath;
        createLexerAndParser(project.getLocation().append(filePath).toString());
    }
    public IParser getParser() { return parser; }
    public ILexer getLexer() { return lexer; }
    public Object getCurrentAst() { return currentAst; }
    public char [][] getKeywords() { return keywords; }
    public boolean isKeyword(int kind) { return isKeyword[kind]; }
    public int getTokenIndexAtCharacter(int offset)
    {
        int index = parser.getParseStream().getTokenIndexAtCharacter(offset);
        return (index < 0 ? -index : index);
    }
    public IToken getTokenAtCharacter(int offset) {
    	return parser.getParseStream().getTokenAtCharacter(offset);
    }
    public IASTNodeLocator getNodeLocator() { return new PolyglotNodeLocator(lexer.getLexStream()); }

    public boolean hasErrors() { return currentAst == null; }
    public List getErrors() { return Collections.singletonList(new ParseError("parse error", null)); }
    
    public ParseController()
    {
    }

    private void createLexerAndParser(String filePath) {
        try {
            lexer = new LexerDelegate(filePath); // Create the lexer
            parser = new ParserDelegate(lexer.getLexStream(), project);  // Create the parser
        } catch (IOException e) {
            throw new Error(e);
        }
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
    	MyMonitor my_monitor = new MyMonitor(monitor);
    	char[] contentsArray = contents.toCharArray();

        lexer.initialize(contentsArray, filePath);
        parser.getParseStream().resetTokenStream();
        lexer.lexer(my_monitor, parser.getParseStream()); // Lex the stream to produce the token stream
        if (my_monitor.isCancelled())
        	return currentAst; // TODO currentAst might (probably will) be inconsistent wrt the lex stream now

        currentAst = (Node) parser.parser(my_monitor, 0);

        cacheKeywordsOnce(); // better place/time to do this?

        return currentAst;
    }

    private void cacheKeywordsOnce() {
        if (keywords == null) {
            String tokenKindNames[] = parser.getParseStream().orderedTerminalSymbols();
            this.isKeyword = new boolean[tokenKindNames.length];
            this.keywords = new char[tokenKindNames.length][];

            int [] keywordKinds = lexer.getKeywordKinds();
            for (int i = 1; i < keywordKinds.length; i++)
            {
                int index = parser.getParseStream().mapKind(keywordKinds[i]);

                isKeyword[index] = true;
                keywords[index] = parser.getParseStream().orderedTerminalSymbols()[index].toCharArray();
            }
        }
    }
}
