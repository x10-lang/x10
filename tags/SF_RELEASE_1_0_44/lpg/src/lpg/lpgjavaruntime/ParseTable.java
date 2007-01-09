package lpg.lpgjavaruntime;

public interface ParseTable
{
    public int baseCheck(int index);

    public int rhs(int index);

    public int baseAction(int index);

    public int lhs(int index);

    public int termCheck(int index);

    public int termAction(int index);

    public int asb(int index);

    public int asr(int index);

    public int nasb(int index);

    public int nasr(int index);

    public int terminalIndex(int index);

    public int nonterminalIndex(int index);

    public int scopePrefix(int index);

    public int scopeSuffix(int index);

    public int scopeLhs(int index);

    public int scopeLa(int index);

    public int scopeStateSet(int index);

    public int scopeRhs(int index);

    public int scopeState(int index);

    public int inSymb(int index);

    public String name(int index);

    public int originalState(int state);

    public int asi(int state);

    public int nasi(int state);

    public int inSymbol(int state);

    public int ntAction(int state, int sym);

    public int tAction(int act, int sym);

    public int lookAhead(int act, int sym);

    public int getErrorSymbol();

    public int getScopeUbound();

    public int getScopeSize();

    public int getMaxNameLength();

    public int getNumStates();

    public int getNtOffset();

    public int getLaStateOffset();

    public int getMaxLa();

    public int getNumRules();

    public int getNumNonterminals();

    public int getNumSymbols();

    public int getSegmentSize();

    public int getStartState();

    public int getStartSymbol();

    public int getEoftSymbol();

    public int getEoltSymbol();

    public int getAcceptAction();

    public int getErrorAction();

    public boolean isNullable(int symbol);

    public boolean isValidForParser();

    public boolean getBacktrack();
}
