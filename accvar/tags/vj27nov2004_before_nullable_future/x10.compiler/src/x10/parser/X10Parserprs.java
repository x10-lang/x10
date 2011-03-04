
//
// This is the grammar specification from the Final Draft of the generic spec.
////
// This is the X10 grammar specification based on the Final Draft of the Java generic spec.
//
package x10.parser;

import com.ibm.lpg.*;
import java.io.*;

class X10Parserprs implements ParseTable, X10Parsersym {
    public final static byte isKeyword[] = new byte[137];
    public final boolean isKeyword(int index) { return isKeyword[index] != 0; }
    public final static short baseCheck[] = new short[12625];
    public final int baseCheck(int index) { return baseCheck[index]; }
    public final static short rhs[] = baseCheck;
    public final int rhs(int index) { return rhs[index]; };
    public final static char baseAction[] = new char[14277];
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };
    public final static char termCheck[] = new char[8185];
    public final int termCheck(int index) { return termCheck[index]; }
    public final static char termAction[] = new char[8134];
    public final int termAction(int index) { return termAction[index]; }
    public final static char asb[] = new char[660];
    public final int asb(int index) { return asb[index]; }
    public final static char asr[] = new char[1951];
    public final int asr(int index) { return asr[index]; }
    public final static char nasb[] = new char[660];
    public final int nasb(int index) { return nasb[index]; }
    public final static char nasr[] = new char[273];
    public final int nasr(int index) { return nasr[index]; }
    public final static char terminalIndex[] = new char[137];
    public final int terminalIndex(int index) { return terminalIndex[index]; }
    public final static char nonterminalIndex[] = new char[312];
    public final int nonterminalIndex(int index) { return nonterminalIndex[index]; }
    public final static char scopePrefix[] = new char[145];
    public final int scopePrefix(int index) { return scopePrefix[index]; }
    public final static char scopeSuffix[] = new char[145];
    public final int scopeSuffix(int index) { return scopeSuffix[index]; }
    public final static char scopeLhs[] = new char[145];
    public final int scopeLhs(int index) { return scopeLhs[index]; }
    public final static char scopeLa[] = new char[145];
    public final int scopeLa(int index) { return scopeLa[index]; }
    public final static char scopeStateSet[] = new char[145];
    public final int scopeStateSet(int index) { return scopeStateSet[index]; }
    public final static char scopeRhs[] = new char[719];
    public final int scopeRhs(int index) { return scopeRhs[index]; }
    public final static char scopeState[] = new char[336];
    public final int scopeState(int index) { return scopeState[index]; }
    public final static char inSymb[] = new char[660];
    public final int inSymb(int index) { return inSymb[index]; }
    public final static String name[] = new String[265];
    public final String name(int index) { return name[index]; }


    private static int offset = 0;

    static private void read(byte buffer[], int array[]) throws IOException {
        for (int i = 0; i < array.length; i++)
            array[i] = (int) ((buffer[offset++] << 24) +
                              ((buffer[offset++] & 0xFF) << 16) +
                              ((buffer[offset++] & 0xFF) << 8) +
                               (buffer[offset++] & 0xFF));
    }

    static private void read(byte buffer[], short array[]) throws IOException {
        for (int i = 0; i < array.length; i++)
            array[i] = (short) ((buffer[offset++] << 8) + (buffer[offset++] & 0xFF));
    }

    static private void read(byte buffer[], char array[]) throws IOException {
        for (int i = 0; i < array.length; i++)
            array[i] = (char) (((buffer[offset++] & 0xFF) << 8) + (buffer[offset++] & 0xFF));
    }

    static private void read(byte buffer[], byte array[]) throws IOException {
        System.arraycopy(buffer, offset, array, 0, array.length);
        offset += array.length;
    }

    static private void read(byte buffer[], String array[]) throws IOException {
        byte string_length[] = new byte[array.length];
        read(buffer, string_length);
        for (int i = 0; i < array.length; i++) {
            array[i] = new String(buffer, offset, string_length[i]);
            offset += string_length[i];
        }
    }

    static {
        try {
            InputStream infile = (new X10Parserprs()).getClass().getClassLoader().getResourceAsStream("X10Parserdcl.data");
            final byte buffer[] = new byte[102319];

            //
            // Normally, we should be able to read the content of infile with
            // the single statement: infile.read(buffer);
            // However, there appears to be a problem with this statement
            // when it is used in an eclipse plugin - in that case, only 8192
            // bytes are read, regardless of the length of buffer. Therefore, we
            // have to replace the single statement above with the loop below...
            //
            int current_index = 0;
            do {
                int num_read = infile.read(buffer, current_index, buffer.length);
                current_index += num_read;
            } while (current_index < buffer.length);

            read(buffer, isKeyword);
            read(buffer, baseCheck);
            read(buffer, baseAction);
            read(buffer, termCheck);
            read(buffer, termAction);
            read(buffer, asb);
            read(buffer, asr);
            read(buffer, nasb);
            read(buffer, nasr);
            read(buffer, terminalIndex);
            read(buffer, nonterminalIndex);
            read(buffer, scopePrefix);
            read(buffer, scopeSuffix);
            read(buffer, scopeLhs);
            read(buffer, scopeLa);
            read(buffer, scopeStateSet);
            read(buffer, scopeRhs);
            read(buffer, scopeState);
            read(buffer, inSymb);
            read(buffer, name);
        }
        catch(IOException e) {
            System.out.println("*** Illegal or corrupted Jikespg data file");
        }
        catch(Exception e) {
            System.out.println("*** Illegal or corrupted Jikespg data file");
        }
    }

    public final static int
           ERROR_SYMBOL      = 136,
           SCOPE_UBOUND      = 144,
           SCOPE_SIZE        = 145,
           MAX_NAME_LENGTH   = 36;

    public final int getErrorSymbol() { return ERROR_SYMBOL; }
    public final int getScopeUbound() { return SCOPE_UBOUND; }
    public final int getScopeSize() { return SCOPE_SIZE; }
    public final int getMaxNameLength() { return MAX_NAME_LENGTH; }

    public final static int
           NUM_STATES        = 659,
           NT_OFFSET         = 136,
           LA_STATE_OFFSET   = 14954,
           MAX_LA            = 1,
           NUM_RULES         = 677,
           NUM_NONTERMINALS  = 311,
           NUM_SYMBOLS       = 447,
           SEGMENT_SIZE      = 8192,
           START_STATE       = 5859,
           IDENTIFIER_SYMBOL = 41,
           EOFT_SYMBOL       = 127,
           EOLT_SYMBOL       = 127,
           ACCEPT_ACTION     = 12625,
           ERROR_ACTION      = 14277;

    public final static boolean BACKTRACK = true;

    public final int getNumStates() { return NUM_STATES; }
    public final int getNtOffset() { return NT_OFFSET; }
    public final int getLaStateOffset() { return LA_STATE_OFFSET; }
    public final int getMaxLa() { return MAX_LA; }
    public final int getNumRules() { return NUM_RULES; }
    public final int getNumNonterminals() { return NUM_NONTERMINALS; }
    public final int getNumSymbols() { return NUM_SYMBOLS; }
    public final int getSegmentSize() { return SEGMENT_SIZE; }
    public final int getStartState() { return START_STATE; }
    public final int getIdentifierSymbol() { return IDENTIFIER_SYMBOL; }
    public final int getEoftSymbol() { return EOFT_SYMBOL; }
    public final int getEoltSymbol() { return EOLT_SYMBOL; }
    public final int getAcceptAction() { return ACCEPT_ACTION; }
    public final int getErrorAction() { return ERROR_ACTION; }
    public final boolean isValidForParser() { return isValidForParser; }
    public final boolean getBacktrack() { return BACKTRACK; }

    public final int originalState(int state) {
        return -baseCheck[state];
    }
    public final int asi(int state) {
        return asb[originalState(state)];
    }
    public final int nasi(int state) {
        return nasb[originalState(state)];
    }
    public final int inSymbol(int state) {
        return inSymb[originalState(state)];
    }

    public final int ntAction(int state, int sym) {
        return baseAction[state + sym];
    }

    public final int tAction(int state, int sym) {
        int i = baseAction[state],
            k = i + sym;
        return termAction[termCheck[k] == sym ? k : i];
    }
    public final int lookAhead(int la_state, int sym) {
        int k = la_state + sym;
        return termAction[termCheck[k] == sym ? k : la_state];
    }
}
