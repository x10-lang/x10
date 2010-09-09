package lpg.lpgjavaruntime;

import java.util.ArrayList;
import java.util.HashMap;

//
// PrsStream holds an arraylist of tokens "lexed" from the input stream.
//
public class PrsStream implements TokenStream, ParseErrorCodes
{
    private LexStream lexStream;
    private int kindMap[] = null;
    private ArrayList tokens = new ArrayList();
    private ArrayList adjuncts = new ArrayList();
    private int index = 0;
    private int len = 0;

    public PrsStream() { }
    
    public PrsStream(LexStream lexStream)
    {
        this.lexStream = lexStream;
        if (lexStream != null) lexStream.setPrsStream(this);
        resetTokenStream();
    }

    public void remapTerminalSymbols(String[] ordered_parser_symbols, int eof_symbol)
                throws UndefinedEofSymbolException,
                       NullExportedSymbolsException,
                       NullTerminalSymbolsException,
                       UnimplementedTerminalsException
    {
        String[] ordered_lexer_symbols = lexStream.orderedExportedSymbols();
        if (ordered_lexer_symbols == null)
            throw new NullExportedSymbolsException();
        if (ordered_parser_symbols == null)
            throw new NullTerminalSymbolsException();
        ArrayList unimplemented_symbols = new ArrayList();
        if (ordered_lexer_symbols != ordered_parser_symbols)
        {
            kindMap = new int[ordered_lexer_symbols.length];

            HashMap terminal_map = new HashMap();
            for (int i = 0; i < ordered_lexer_symbols.length; i++)
                terminal_map.put(ordered_lexer_symbols[i], new Integer(i));
            for (int i = 0; i < ordered_parser_symbols.length; i++)
            {
                Integer k = (Integer) terminal_map.get(ordered_parser_symbols[i]);
                if (k != null)
                     kindMap[k.intValue()] = i;
                else
                {
                    if (i == eof_symbol)
                        throw new UndefinedEofSymbolException();
                    unimplemented_symbols.add(new Integer(i));
                }
            }
        }

        if (unimplemented_symbols.size() > 0)
            throw new UnimplementedTerminalsException(unimplemented_symbols);
    }

    public final int mapKind(int kind) { return (kindMap == null ? kind : kindMap[kind]); }

    public String[] orderedTerminalSymbols() { return null; }

    public void resetTokenStream()
    {
        tokens = new ArrayList();
        index = 0;

        adjuncts = new ArrayList();
    }

    void setLexStream(LexStream lexStream) 
    { 
        this.lexStream = lexStream; 
        resetTokenStream();
    }
    
    /**
     * @deprecated function
     */
    public void resetLexStream(LexStream lexStream) 
    { 
        this.lexStream = lexStream; 
        if (lexStream != null) lexStream.setPrsStream(this);
    }
    
    public void makeToken(int startLoc, int endLoc, int kind)
    {
        Token token = new Token(this, startLoc, endLoc, mapKind(kind));
        token.setTokenIndex(tokens.size());
        tokens.add(token);
        token.setAdjunctIndex(adjuncts.size());
    }
    
    public int makeErrorToken(int firsttok, int lasttok, int errortok, int kind)
    {
        int index = tokens.size(); // the next index

        //
        // Note that when creating an error token, we do not remap its kind.
        // Since this is not a lexical operation, it is the responsibility of
        // the calling program (a parser driver) to pass to us the proper kind
        // that it wants for an error token.
        //
        Token token = new ErrorToken(getIToken(firsttok),
                                     getIToken(lasttok),
                                     getIToken(errortok),
                                     getStartOffset(firsttok),
                                     getEndOffset(lasttok),
                                     kind);
        token.setTokenIndex(tokens.size());
        tokens.add(token);
        token.setAdjunctIndex(adjuncts.size());

        return index;
    }
    
    public void addToken(IToken token)
    {
        token.setTokenIndex(tokens.size());
        tokens.add(token);
        token.setAdjunctIndex(adjuncts.size());
    }

    public void makeAdjunct(int startLoc, int endLoc, int kind)
    {
        int token_index = tokens.size() - 1; // index of last token processed
        Adjunct adjunct = new Adjunct(this, startLoc, endLoc, mapKind(kind));
        adjunct.setAdjunctIndex(adjuncts.size());
        adjunct.setTokenIndex(token_index);
        adjuncts.add(adjunct);
    }
    
    public void addAdjunct(IToken adjunct)
    {
        int token_index = tokens.size() - 1; // index of last token processed
        adjunct.setTokenIndex(token_index);
        adjunct.setAdjunctIndex(adjuncts.size());
        adjuncts.add(adjunct);
    }

    public String getTokenText(int i)
    {
        IToken t = (IToken)tokens.get(i);
        return t.toString();
    }

    public int getStartOffset(int i)
    {
        IToken t = (IToken)tokens.get(i);
        return t.getStartOffset();
    }

    public int getEndOffset(int i)
    {
        IToken t = (IToken)tokens.get(i);
        return t.getEndOffset();
    }

    public int getTokenLength(int i)
    {
        IToken t = (IToken)tokens.get(i);
        return t.getEndOffset() - t.getStartOffset() + 1;
    }

    public int getLineNumberOfTokenAt(int i)
    {
        IToken t = (IToken)tokens.get(i);
        return lexStream.getLineNumberOfCharAt(t.getStartOffset());
    }

    public int getEndLineNumberOfTokenAt(int i)
    {
        IToken t = (IToken)tokens.get(i);
        return lexStream.getLineNumberOfCharAt(t.getEndOffset());
    }

    public int getColumnOfTokenAt(int i)
    {
        IToken t = (IToken)tokens.get(i);
        return lexStream.getColumnOfCharAt(t.getStartOffset());
    }

    public int getEndColumnOfTokenAt(int i)
    {
        IToken t = (IToken)tokens.get(i);
        return lexStream.getColumnOfCharAt(t.getEndOffset());
    }

    /**
     * @deprecated replaced by {@link #getFirstRealToken()}
     *
     */
    public int getFirstErrorToken(int i) { return getFirstRealToken(i); }
    public int getFirstRealToken(int i)
    {
        while (i >= len)
            i = ((ErrorToken) tokens.get(i)).getFirstRealToken().getTokenIndex();
        return i;
    }

    /**
     * @deprecated replaced by {@link #getLastRealToken()}
     *
     */
    public int getLastErrorToken(int i) { return getLastRealToken(i); }
    public int getLastRealToken(int i)
    {
        while (i >= len)
            i = ((ErrorToken) tokens.get(i)).getLastRealToken().getTokenIndex();
        return i;
    }

    public char [] getInputChars() { return lexStream.getInputChars(); }

    public int getSize() { return tokens.size(); }

    /**
     * @deprecated replaced by {@link #setStreamLength()}
     *
     */
    public void setSize() { len = tokens.size(); }

    //
    // This function returns the index of the token element
    // containing the offset specified. If such a token does
    // not exist, it returns the negation of the index of the 
    // element immediately preceding the offset.
    //
    public int getTokenIndexAtCharacter(int offset)
    {
        int low = 0,
            high = tokens.size();
        while (high > low)
        {
            int mid = (high + low) / 2;
            IToken mid_element = (IToken)tokens.get(mid);
            if (offset >= mid_element.getStartOffset() &&
                offset <= mid_element.getEndOffset())
                 return mid;
            else if (offset < mid_element.getStartOffset())
                 high = mid;
            else low = mid + 1;
        }

        return -(low - 1);
    }
    
    public IToken getTokenAtCharacter(int offset)
    {
        int tokenIndex = getTokenIndexAtCharacter(offset);
        return (tokenIndex < 0) ? null : getTokenAt(tokenIndex);
    }
    
    public IToken getTokenAt(int i) { return (IToken)tokens.get(i); }

    public IToken getIToken(int i)  { return (IToken)tokens.get(i); }

    public ArrayList getTokens() { return tokens; }

    public int getStreamIndex() { return index; }

    public int getStreamLength() { return len; }

    public void setStreamIndex(int index) { this.index = index; }

    public void setStreamLength() { this.len = tokens.size(); }

    public void setStreamLength(int len) { this.len = len; }

    public LexStream getLexStream() { return lexStream; }

    public void dumpTokens()
    {
        if (getSize() <= 2) return;
        System.out.println(" Kind \t Offset  Line \t Col \t Len  \tText\n");
        for (int i = 1; i < getSize() - 1; i++) dumpToken(i);
    }

    public void dumpToken(int i)
    {
        System.out.print( " (" + getKind(i) + ")\t : ");
        System.out.print(getStartOffset(i));
        System.out.print(" \t " + getLineNumberOfTokenAt(i));
        System.out.print(" \t " + getColumnOfTokenAt(i));
        System.out.print(" \t " + getTokenLength(i));
        System.out.print("    \t" + getTokenText(i));
        System.out.println();
    }

    int next(int i) { return (++i < len ? i : len - 1); }

    int previous(int i) { return (i <= 0 ? 0 : i - 1); }

    private IToken[] getAdjuncts(int i)
    {
        int start_index = ((IToken)tokens.get(i)).getAdjunctIndex(),
            end_index = ((IToken)tokens.get(getNext(i))).getAdjunctIndex(),
            size = end_index - start_index;
        IToken[] slice = new IToken[size];
        for (int j = start_index, k = 0; j < end_index; j++, k++)
            slice[k] = (IToken) adjuncts.get(j);
        return slice;
    }
    
    //
    // Return an iterator for the adjuncts that follow token i.
    //
    public IToken[] getFollowingAdjuncts(int i)
    {
        return getAdjuncts(i);
    }

    public IToken[] getPrecedingAdjuncts(int i)
    {
        return getAdjuncts(getPrevious(i));
    }

    public ArrayList getAdjuncts() { return adjuncts; }

    //
    // Methods that implement the TokenStream Interface
    //
    public int getToken()
    {
        index = next(index);
        return index;
    }

    public int getToken(int end_token)
         { return index = (index < end_token ? next(index) : len - 1); }

    public int getKind(int i)
    {
        IToken t = (IToken)tokens.get(i);
        return t.getKind();
    }

    public int getNext(int i) { return (++i < len ? i : len - 1); }

    public int getPrevious(int i) { return (i <= 0 ? 0 : i - 1); }

    public String getName(int i) { return getTokenText(i); }

    public int peek() { return next(index); }

    public void reset(int i) { index = previous(i); }

    public void reset() { index = 0; }

    public int badToken() { return 0; }

    public int getLine(int i) {return getLineNumberOfTokenAt(i); }

    public int getColumn(int i) { return getColumnOfTokenAt(i); }

    public int getEndLine(int i) { return getEndLineNumberOfTokenAt(i); }

    public int getEndColumn(int i) { return getEndColumnOfTokenAt(i); }

    public boolean afterEol(int i) { return (i < 1 ? true : getEndLineNumberOfTokenAt(i - 1) < getLineNumberOfTokenAt(i)); }

    public String getFileName() { return lexStream.getFileName(); }

    public void reportError(int i, String code)
    {
        System.out.println("****" + code + " " + i);
        dumpToken(i);
    }

    public void reportError(int left_char, int right_char) {}

    public void reportError(int errorCode, String locationInfo, String tokenText)
    {
        lexStream.reportError(errorCode, locationInfo, tokenText);
    }

    public void reportError(int errorCode, String locationInfo, int leftToken, int rightToken, String tokenText)
    {
        if (errorCode == DELETION_CODE ||
            errorCode == MISPLACED_CODE) tokenText = "";
        if (!tokenText.equals("")) tokenText += ' ';
        lexStream.reportError(errorCode, locationInfo, leftToken, rightToken, tokenText);
    }
}
