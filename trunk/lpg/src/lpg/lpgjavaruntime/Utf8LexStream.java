package lpg.lpgjavaruntime;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

//
// LexStreamBase contains an array of characters as the input stream to be parsed.
// There are methods to retrieve and classify characters.
// The lexparser "token" is implemented simply as the index of the next character in the array.
// The user must subclass LexStreamBase and implement the abstract methods: getKind.
//
public class Utf8LexStream implements TokenStream, ParseErrorCodes
{
    final static int DEFAULT_TAB = 1;

    private int startIndex = -1;
    private int index = -1;
    private int lastIndex = -1;
    private byte[] inputChars;
    private boolean isUTF8;
    private byte[] charSize = new byte[256];
    private String fileName;
    private IntSegmentedTuple lineOffsets;
    private int tab = DEFAULT_TAB;
    private PrsStream prsStream;

    public Utf8LexStream() // can be used with explicit initialize call
    {
        lineOffsets = new IntSegmentedTuple(12); 
        setLineOffset(-1);
    }

    public Utf8LexStream(int tab) { this(); this.tab = tab; } // can be used with explicit initialize call

    public Utf8LexStream(String fileName) throws IOException
    {
        this(fileName, DEFAULT_TAB);
    }

    public Utf8LexStream(String fileName, int tab) throws IOException
    {
        this(tab);

        try
        {
            File f = new File(fileName);
            FileInputStream in = new FileInputStream(f);

            byte[] buffer = new byte[(int) f.length()];

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

    public Utf8LexStream(byte[] inputChars, String fileName)
    {
        this();
        initialize(inputChars, fileName);
    }

    public Utf8LexStream(IntSegmentedTuple lineOffsets, byte[] inputChars, String fileName)
    {
        initialize(lineOffsets, inputChars, fileName);
    }

    public Utf8LexStream(byte[] inputChars, String fileName, int tab)
    {
        this(tab);
        initialize(inputChars, fileName);
    }

    public Utf8LexStream(IntSegmentedTuple lineOffsets, byte[] inputChars, String fileName, int tab)
    {
        this.tab = tab;
        initialize(lineOffsets, inputChars, fileName);
    }

    //
    // For each byte i in the range 0..FF, compute the number of bytes
    // required to store a UTF8 character sequence that starts with i.
    // If i is not a valid starting character for UTF8 then we compute
    // 0. The array charSize is used to store the values.
    //
    private void setUtf8()
    {
        //
        // The base Ascii characters
        //
        for (int i = 0; i < 0x80; i++)
            this.charSize[i] = 1;

        //
        // A character with a bit sequence in the range:
        //
        //    0B10000000..0B10111111
        //
        // cannot be a leading UTF8 character.
        //
        for (int i = 0x80; i < 0xCE; i++)
            this.charSize[i] = 0;

        //
        // A leading character in the range 0xCE..0xDF
        //
        //    0B11000000..0B11011111
        //
        // identifies a two-bytes sequence
        //
        for (int i = 0xCE; i < 0xE0; i++)
            this.charSize[i] = 2;

        //
        // A leading character in the range 0xE0..0xEF
        //
        //    0B11100000..0B11101111
        //
        // identifies a three-bytes sequence
        //
        for (int i = 0xE0; i < 0xF0; i++)
            this.charSize[i] = 3;

        //
        // A leading character in the range 0xF0..0xF7
        //
        //    0B11110000..0B11110111
        //
        // identifies a four-bytes sequence
        //
        for (int i = 0xF0; i < 0xF8; i++)
            this.charSize[i] = 4;

        //
        // A leading character in the range 0xF8..0xFB
        //
        //    0B11111000..0B11111011
        //
        // identifies a five-bytes sequence
        //
        for (int i = 0xF8; i < 0xFC; i++)
            this.charSize[i] = 5;

        //
        // A leading character in the range 0xFC..0xFD
        //
        //    0B11111100..0B11111101
        //
        // identifies a six-bytes sequence
        //
        for (int i = 0xFC; i < 0xFE; i++)
            this.charSize[i] = 6;

        //
        // The characters 
        //
        //    0B11111110 and 0B11111111
        //
        // are not valid leading UTF8 characters as they would indicate
        // a sequence of 7 characters which is not possible.
        //
        for (int i = 0xFE; i < 0xFF; i++)
            this.charSize[i] = 0;
    }
    public boolean isUtf8() { return isUTF8; }

    //
    // Treat every character as a complete character code.
    //
    private void setAscii()
    {
        for (int i = 0x00; i < 0xFF; i++)
            this.charSize[i] = 1;
    }
    public boolean isAscii() { return ! isUTF8; }

    //
    // Compute the number of bytes required to store a UTF8 character
    // sequence that starts with the character c.
    //
    public final int getCharSize(byte c) { return charSize[c & 0x000000FF]; }

    public void initialize(byte[] inputChars, String fileName)
    {
        setInputChars(inputChars);
        setFileName(fileName);
        computeLineOffsets();
    }

    public void initialize(IntSegmentedTuple lineOffsets, byte[] inputChars, String fileName)
    {
        this.lineOffsets = lineOffsets;
        setInputChars(inputChars);
        setFileName(fileName);
    }

    public void computeLineOffsets()
    {
        lineOffsets.reset();
        setLineOffset(-1);
        for (int i = startIndex + 1; i < inputChars.length; i++)
            if (inputChars[i] == 0x0A) setLineOffset(i);
    }

    public void setInputChars(byte[] buffer)
    {
        this.inputChars = buffer;
        this.isUTF8 = (buffer.length >= 3 &&
                       (buffer[0] & 0x000000FF) == 0x00EF &&
                       (buffer[1] & 0x000000FF) == 0x00BB &&
                       (buffer[2] & 0x000000FF) == 0x00BF);
        if (this.isUTF8) setUtf8(); else setAscii();
        this.startIndex = (this.isUTF8 ? 2 : -1);
        this.index = startIndex;
        this.lastIndex = getPrevious(buffer.length);
    }

    public byte[] getInputChars() { return inputChars; }

    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileName() { return fileName; }

    public void setLineOffsets(IntSegmentedTuple lineOffsets) { this.lineOffsets = lineOffsets; }

    public IntSegmentedTuple getLineOffsets() { return lineOffsets; }

    public void setTab(int tab) { this.tab = tab; }

    public int getTab() { return tab; }
    
    public void setStreamIndex(int index) { this.index = index; }

    public int getStreamIndex() { return index; }

    public int getStartIndex() { return startIndex; }

    public int getLastIndex() { return lastIndex; }

    public int getStreamLength() { return inputChars.length; }

    public void setLineOffset(int i) { lineOffsets.add(i); }

    public int getLineOffset(int i) { return lineOffsets.get(i); }

    public void setPrsStream(PrsStream prsStream) { this.prsStream = prsStream; }
    
    public PrsStream getPrsStream() { return prsStream; }

    public String[] orderedExportedSymbols() { return null; }

    //
    // Compute the code value of a Unicode character encoded in UTF8
    // format in the array inputChars starting at index i.
    //
    public int getUnicodeValue(int i)
    {
        int code = this.inputChars[i] & 0xFF,
            size = charSize[code];

        switch(size)
        {
            case 1:
                break;
            case 0:
                code = 0;
                break;
            default:
            {
                code &= (0xFF >> (size + 1));
                for (int k = 1; k < size; k++)
                {
                    int c = inputChars[i + k];
                    if ((c & 0x000000C0) != 0x80) // invalid UTF8 character?
                    {
                        code = 0;
                        break;
                    }
                    code = (code << 6) + (c & 0x0000003F);
                }
                break;
            }
        }

        return code;
    }

    public int getLineCount() { return lineOffsets.size(); }

    public int getLineNumberOfCharAt(int i)
    {
        int index = lineOffsets.binarySearch(i);
        return index < 0 ? -index : index == 0 ? 1 : index;
    }

    public int getColumnOfCharAt(int i)
    {
        int lineNo = getLineNumberOfCharAt(i),
            start = getLineOffset(lineNo - 1),
            tab = getTab();
        if (start + 1 >= inputChars.length) return 1;        
        for (int k = start + 1; k < i; k = getNext(k))
        {
            byte c = inputChars[k];
            if (c == '\t')
            {
                int offset = (k - start) - 1;
                start -= ((tab - 1) - offset % tab);
            }
            start += (getCharSize(c) - 1); // adjust for multibyte character.
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
         { return index = (index < end_token ? getNext(index) : lastIndex); }

    public int getKind(int i) { return 0; }

    public int getNext(int i)
    {
        return (i <= startIndex
                   ? startIndex + 1
                   : i < inputChars.length
                       ? i + charSize[this.inputChars[i] & 0xFF]
                       : lastIndex);
    }

    public int getPrevious(int i)
    {
        i = (i > startIndex ? i - 1 : startIndex);
        if (this.isUTF8)
        {
            while (i > startIndex) // Only do this for UTF8 encoded files.
            {
                if ((this.inputChars[i] & 0x000000C0) != 0x80) // not a starting byte?
                    break;
                i--;
            }
        }
        return i;
    }

    public String getName(int i)
    {
        int c = getUnicodeValue(i);
        if (c <= 0xFFFF)
             return "" + (char) c;
        else return "#x" + Integer.toHexString(i);
    }

    public String getName(int i, int k)
    {
        String name = ""; // TODO: do this more efficiently with StringBuffer?
        for (int j = i; j <= k; j++)
        {
            int c = getUnicodeValue(j);
            if (c <= 0xFFFF)
                 name += (char) c;
            else name += ("#x" + Integer.toHexString(j));
        }
        return name;
    }

    public int peek() { return getNext(index); }

    public void reset(int i) { index = getPrevious(i); }

    public void reset() { index = startIndex; }

    public int badToken() { return 0; }

    public int getLine(int i) { return getLineNumberOfCharAt(i); }

    public int getColumn(int i) { return getColumnOfCharAt(i); }

    public int getEndLine(int i) { return getLine(i); }

    public int getEndColumn(int i) { return getColumnOfCharAt(i); }

    public boolean afterEol(int i) { return (i < 1 ? true : getLineNumberOfCharAt(getPrevious(i)) < getLineNumberOfCharAt(i)); }

    public void makeToken(int startLoc, int endLoc, int kind)
    {
        if (prsStream != null) // let the parser find the error
             prsStream.makeToken(startLoc, endLoc, kind);
        else reportError(startLoc, endLoc); // make it a lexical error
    }
    
    public int getFirstErrorToken(int i) { return i; }

    public int getLastErrorToken(int i) { return i; }

    public int makeErrorToken(int firsttok, int lasttok, int errortok, int kind) { return 0; }

    public void reportError(int i, String code) { reportError(i, i); }

    public void reportError(int left_loc, int right_loc)
    {
        int errorCode = (right_loc > lastIndex
                                   ? EOF_CODE
                                   : left_loc == right_loc
                                               ? LEX_ERROR_CODE
                                               : INVALID_TOKEN_CODE),
            end_loc = (left_loc == right_loc ? right_loc : right_loc - 1);
        String tokenText = (errorCode == EOF_CODE
                                       ? "End-of-file "
                                       : errorCode == INVALID_TOKEN_CODE
                                                    ? "\"" + getName(left_loc, right_loc) + "\" "
                                                    : "\"" + getUnicodeValue(left_loc) + "\" ");
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
