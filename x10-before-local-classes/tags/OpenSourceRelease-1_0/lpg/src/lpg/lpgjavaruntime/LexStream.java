package lpg.lpgjavaruntime;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

//
// LexStream contains an array of characters as the input stream to be parsed.
// There are methods to retrieve and classify characters.
// The lexparser "token" is implemented simply as the index of the next character in the array.
// The user must subclass LexStreamBase and implement the abstract methods: getKind.
//
public class LexStream implements TokenStream, ParseErrorCodes
{
    final static int DEFAULT_TAB = 1;

    private int index = -1;
    private int streamLength = 0;
    private char[] inputChars;
    private String fileName;
    private IntSegmentedTuple lineOffsets;
    private int tab = DEFAULT_TAB;

    /**
     * @deprecated Use function getPrsStream()
     */
    protected /* private ... when not deprecated! */ PrsStream prsStream;

    public LexStream() // can be used with explicit initialize call
    {
        lineOffsets = new IntSegmentedTuple(12); 
        setLineOffset(-1);
    }

    public LexStream(int tab) { this(); this.tab = tab; } // can be used with explicit initialize call

    public LexStream(String fileName) throws IOException
    {
        this(fileName, DEFAULT_TAB);
    }

    public LexStream(String fileName, int tab) throws IOException
    {
        this(tab);

        try
        {
            File f = new File(fileName);
            InputStreamReader in = new InputStreamReader(new FileInputStream(f));

            char[] buffer = new char[(int) f.length()];

            in.read(buffer, 0, buffer.length);
            initialize(buffer, fileName);
        }
        catch (Exception e)
        {
            IOException io = new IOException();
            System.err.println(e.getMessage());
            e.printStackTrace();
            throw(io);
        }
    }

    public LexStream(char[] inputChars, String fileName)
    {
        this();
        initialize(inputChars, fileName);
    }

    public LexStream(IntSegmentedTuple lineOffsets, char[] inputChars, String fileName)
    {
        initialize(lineOffsets, inputChars, fileName);
    }

    public LexStream(char[] inputChars, String fileName, int tab)
    {
        this(tab);
        initialize(inputChars, fileName);
    }

    public LexStream(IntSegmentedTuple lineOffsets, char[] inputChars, String fileName, int tab)
    {
        this.tab = tab;
        initialize(lineOffsets, inputChars, fileName);
    }

    public void initialize(char[] inputChars, String fileName)
    {
        setInputChars(inputChars);
        setStreamLength(inputChars.length);
        setFileName(fileName);
        computeLineOffsets();        
    }

    public void initialize(IntSegmentedTuple lineOffsets, char[] inputChars, String fileName)
    {
        this.lineOffsets = lineOffsets;
        setInputChars(inputChars);
        setStreamLength(inputChars.length);
        setFileName(fileName);
    }

    public void computeLineOffsets()
    {
        lineOffsets.reset();
        setLineOffset(-1);
        for (int i = 0; i < inputChars.length; i++)
            if (inputChars[i] == 0x0A) setLineOffset(i);
    }

    public void setInputChars(char[] inputChars)
    {
        this.inputChars = inputChars;
        index = -1; // reset the start index to the beginning of the input
    }

    public char[] getInputChars() { return inputChars; }

    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileName() { return fileName; }

    public void setLineOffsets(IntSegmentedTuple lineOffsets) { this.lineOffsets = lineOffsets; }

    public IntSegmentedTuple getLineOffsets() { return lineOffsets; }

    public void setTab(int tab) { this.tab = tab; }

    public int getTab() { return tab; }
    
    public void setStreamIndex(int index) { this.index = index; }

    public int getStreamIndex() { return index; }

    public void setStreamLength(int streamLength) { this.streamLength = streamLength; }

    public int getStreamLength() { return streamLength; }

    public void setLineOffset(int i)
    {
        lineOffsets.add(i);
    }

    public int getLineOffset(int i) { return lineOffsets.get(i); }

    public void setPrsStream(PrsStream prsStream)
    {
        prsStream.setLexStream(this);
        this.prsStream = prsStream;
    }
    
    public PrsStream getPrsStream()
    {
        return prsStream;
    }

    public String[] orderedExportedSymbols() { return null; }

    public char getCharValue(int i)
    {
        return inputChars[i];
    }

    /**
     * @deprecated replaced by {@link #getLineCount()}
     *
     */
    public int getLine() { return getLineCount(); }
    public int getLineCount() { return lineOffsets.size() - 1; }

    public int getLineNumberOfCharAt(int i)
    {
        int index = lineOffsets.binarySearch(i);
        return index < 0 ? -index : index == 0 ? 1 : index;
    }

    public int getColumnOfCharAt(int i)
    {
        int lineNo = getLineNumberOfCharAt(i),
            start = lineOffsets.get(lineNo - 1);
        if (start + 1 >= streamLength) return 1;        
        for (int k = start + 1; k < i; k++)
        {
            if (inputChars[k] == '\t')
            {
                int offset = (k - start) - 1;
                start -= ((tab - 1) - offset % tab);
            }
        }
        return i - start;
    }

    void reportError(int i) { reportError(i, i); }

    //
    // Methods that implement the TokenStream Interface.
    // Note that this function updates the lineOffsets table
    // as a side-effect when the next character is a line feed.
    // If this is not the expected behavior then this function should 
    // be overridden.
    //
    public int getToken() { return index = getNext(index); }

    public int getToken(int end_token)
         { return index = (index < end_token ? getNext(index) : streamLength); }

    public int getKind(int i) { return 0; }

    /**
     * @deprecated replaced by {@link #getNext()}
     *
     */
    int next(int i) { return getNext(i); }
    public int getNext(int i) { return (++i < streamLength ? i : streamLength); }

    /**
     * @deprecated replaced by {@link #getPrevious()}
     *
     */
    int previous(int i) { return getPrevious(i); }
    public int getPrevious(int i) { return (i <= 0 ? 0 : i - 1); }

    public String getName(int i) { return i >= getStreamLength() ? "" : "" + getCharValue(i); }

    public int peek() { return getNext(index); }

    public void reset(int i) { index = i - 1; }

    public void reset() { index = -1; }

    public int badToken() { return 0; }

    public int getLine(int i) { return getLineNumberOfCharAt(i); }

    public int getColumn(int i) { return getColumnOfCharAt(i); }

    public int getEndLine(int i) { return getLine(i); }

    public int getEndColumn(int i) { return getColumnOfCharAt(i); }

    public boolean afterEol(int i) { return (i < 1 ? true : getLineNumberOfCharAt(i - 1) < getLineNumberOfCharAt(i)); }

    public void makeToken(int startLoc, int endLoc, int kind)
    {
        if (prsStream != null) // let the parser find the error
             prsStream.makeToken(startLoc, endLoc, kind);
        else this.reportError(startLoc, endLoc); // make it a lexical error
    }

    /**
     * @deprecated replaced by {@link #getFirstRealToken()}
     *
     */
    public int getFirstErrorToken(int i) { return getFirstRealToken(i); }
    public int getFirstRealToken(int i) { return i; }

    /**
     * @deprecated replaced by {@link #getLastRealToken()}
     *
     */
    public int getLastErrorToken(int i) { return getLastRealToken(i); }
    public int getLastRealToken(int i) { return i; }

    public int makeErrorToken(int firsttok, int lasttok, int errortok, int kind) { return 0; }

    public void reportError(int i, String code) { reportError(i, i); }

    public void reportError(int left_loc, int right_loc)
    {
        int errorCode = (right_loc >= streamLength
                                    ? EOF_CODE
                                    : left_loc == right_loc
                                                ? LEX_ERROR_CODE
                                                : INVALID_TOKEN_CODE),
            end_loc = (left_loc == right_loc ? right_loc : right_loc - 1);
        String tokenText = (errorCode == EOF_CODE
                                       ? "End-of-file "
                                       : errorCode == INVALID_TOKEN_CODE
                                                    ? "\"" + new String(inputChars, left_loc, right_loc - left_loc) + "\" "
                                                    : "\"" + getCharValue(left_loc) + "\" ");
        String locationInfo = getFileName() + ':' + getLineNumberOfCharAt(left_loc) + ':'
                                                  + getColumnOfCharAt(left_loc) + ':'
                                                  + getLineNumberOfCharAt(end_loc) + ':'
                                                  + getColumnOfCharAt(end_loc) + ": ";
        reportError(errorCode, locationInfo, tokenText);
    }

    public void reportError(int errorCode, String locationInfo, String tokenText)
    {
        System.out.println(locationInfo + tokenText + errorMsgText[errorCode]);
    }

    public void reportError(int errorCode, String locationInfo, int leftToken, int rightToken, String tokenText)
    {
        System.out.println(locationInfo + tokenText + errorMsgText[errorCode]);
    }
}
