package lpg.lpgjavaruntime;

public abstract class AbstractToken
{
    private int kind = 0,
                startOffset = 0,
                endOffset = 0,
                tokenIndex = 0,
                adjunctIndex;
    private PrsStream prsStream;

    public AbstractToken() {}
    public AbstractToken(PrsStream prsStream, int startOffset, int endOffset, int kind)
    {
        this.prsStream = prsStream;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.kind = kind;
    }

    public int getKind() { return kind; }
    public void setKind(int kind) { this.kind = kind; }

    public int getStartOffset() { return startOffset; }
    public void setStartOffset(int startOffset)
    {
        this.startOffset = startOffset;
    }

    public int getEndOffset() { return endOffset; }
    public void setEndOffset(int endOffset)
    {
        this.endOffset = endOffset;
    }

    public int getTokenIndex() { return tokenIndex; }
    public void setTokenIndex(int tokenIndex) { this.tokenIndex = tokenIndex; }

    public void setAdjunctIndex(int adjunctIndex) { this.adjunctIndex = adjunctIndex; }
    public int getAdjunctIndex() { return adjunctIndex; }
    
    public PrsStream getPrsStream() { return prsStream; }
    public int getLine() { return (prsStream == null ? 0 : prsStream.getLexStream().getLineNumberOfCharAt(startOffset)); }
    public int getColumn() { return (prsStream == null ? 0 : prsStream.getLexStream().getColumnOfCharAt(startOffset)); }
    public int getEndLine() { return (prsStream == null ? 0 : prsStream.getLexStream().getLineNumberOfCharAt(endOffset)); }
    public int getEndColumn() { return (prsStream == null ? 0 : prsStream.getLexStream().getColumnOfCharAt(endOffset)); }

    /**
     * @deprecated replaced by {@link #toString()}
     */
    public String getValue(char[] inputChars)
    {
        if (inputChars != prsStream.getInputChars() && prsStream != null)
            throw new MismatchedInputCharsException();
        return toString();
    }

    public String toString()
    {
        if (prsStream == null)
            return "<toString>";
        if (endOffset >= prsStream.getInputChars().length)
            return "$EOF"; // String.valueOf(IToken.EOF);
        int len = endOffset - startOffset + 1;
        return (len <= 0 ? "" : new String(prsStream.getInputChars(), startOffset, len));
    }
}