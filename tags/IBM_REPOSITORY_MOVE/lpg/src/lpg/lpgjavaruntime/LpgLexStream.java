/*
 * Created on Oct 7, 2004
 *
 *  To be used by an lpg generated lexer
*/
package lpg.lpgjavaruntime;
import java.io.IOException;

/**
 * @author fisher
 *
 */
public abstract class LpgLexStream extends LexStream
{
    /**
     * 
     */
    public LpgLexStream() {
        super();
    }

    /**
     * @param tab
     */
    public LpgLexStream(int tab) {
        super(tab);
    }

    /**
     * @param fileName
     */
    public LpgLexStream(String fileName) throws IOException
    {
    	super(fileName);
    }

    /**
     * @param fileName
     * @param tab
     */
    public LpgLexStream(String fileName, int tab) throws IOException
    {
    	super(fileName, tab);
    }

    /**
     * @param inputChars
     * @param fileName
     */
    public LpgLexStream(char[] inputChars, String fileName) {
        super(inputChars, fileName);
    }

    /**
     * @param lineOffsets
     * @param inputChars
     * @param fileName
     */
    public LpgLexStream(IntSegmentedTuple lineOffsets, char[] inputChars, String fileName) {
        super(lineOffsets, inputChars, fileName);
    }

    /**
     * @param inputChars
     * @param fileName
     * @param tab
     */
    public LpgLexStream(char[] inputChars, String fileName, int tab) {
        super(inputChars, fileName, tab);
    }

    /**
     * @param lineOffsets
     * @param inputChars
     * @param fileName
     * @param tab
     */
    public LpgLexStream(IntSegmentedTuple lineOffsets, char[] inputChars, String fileName, int tab) {
        super(lineOffsets, inputChars, fileName, tab);
    }

    /* (non-Javadoc)
     * @see lpg.javaruntime.TokenStream#getKind(int)
     */
    public abstract int getKind(int i);

    public abstract String[] orderedExportedSymbols();
}
