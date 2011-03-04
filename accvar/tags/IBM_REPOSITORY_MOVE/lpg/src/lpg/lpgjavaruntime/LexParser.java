package lpg.lpgjavaruntime;

public class LexParser
{
    private int START_STATE,
                LA_STATE_OFFSET,
                EOFT_SYMBOL,
                ACCEPT_ACTION,
                ERROR_ACTION;

    private TokenStream tokStream;
    private ParseTable prs;
    private RuleAction ra;
    private IntTuple action = null;

    public LexParser() {}

    public LexParser(TokenStream tokStream, ParseTable prs, RuleAction ra)
    {
        this.tokStream = tokStream;
        pushPtRa(prs, ra);
    }

    private void initialize(ParseTable prs, RuleAction ra)
    {
        this.prs = prs;
        this.ra = ra;

        START_STATE = prs.getStartState();
        LA_STATE_OFFSET = prs.getLaStateOffset();
        EOFT_SYMBOL = prs.getEoftSymbol();
        ACCEPT_ACTION = prs.getAcceptAction();
        ERROR_ACTION = prs.getErrorAction();
    }

    //
    // Stacks portion
    //
    private int STACK_INCREMENT = 1024,
                stateStackTop,
                stackLength = 0,
                stack[],
                locationStack[];
    
    private int ptraStackTop = -1;
    
    private ParseTable parseTableStack[] = new ParseTable[STACK_INCREMENT];

    private RuleAction ruleActionStack[] = new RuleAction[STACK_INCREMENT];

    public void pushPtRa (ParseTable prs, RuleAction ra)
    {
        ptraStackTop++;
        parseTableStack[ptraStackTop] = prs;
        ruleActionStack[ptraStackTop] = ra;
        initialize(prs, ra);
    }
    
    public void popPtRa()
    {
        ptraStackTop--;
        ParseTable prs = parseTableStack[ptraStackTop];
        RuleAction ra  = ruleActionStack[ptraStackTop];
        initialize(prs,ra);
    }
    
    private void reallocateStacks()
    {
        int old_stack_length = (stack == null ? 0 : stackLength);
        stackLength += STACK_INCREMENT;

        if (old_stack_length == 0)
        {
            stack = new int[stackLength];
            locationStack = new int[stackLength];
        }
        else
        {
            System.arraycopy(stack, 0, stack = new int[stackLength], 0, old_stack_length);
            System.arraycopy(locationStack, 0, locationStack = new int[stackLength], 0, old_stack_length);
        }
        return;
    }
    //
    // Given a rule of the form     A ::= x1 x2 ... xn     n > 0
    //
    // the function getToken(i) yields the symbol xi, if xi is a terminal
    // or ti, if xi is a nonterminal that produced a string of the form
    // xi => ti w. If xi is a nullable nonterminal, then ti is the first
    //  symbol that immediately follows xi in the input (the lookahead).
    //
    public final int getToken(int i)
    {
        return locationStack[stateStackTop + (i - 1)];
    }
    public final void setSym1(int i) {}
    public final int getSym(int i) { return getLastToken(i); }


    private int lastToken,
                currentAction,
                curtok,
                starttok,
                current_kind;

    public final int getCurrentRule()     { return currentAction; }
    public final int getFirstToken()      { return getToken(1); }
    public final int getFirstToken(int i) { return getToken(i); }
    public final int getLastToken()       { return lastToken; }
    public final int getLastToken(int i)  { return (i >= prs.rhs(currentAction)
                                                       ? lastToken
                                                       : tokStream.getPrevious(getToken(i + 1))); }

    public void resetTokenStream(int i)
    {
        tokStream.reset(i);
        curtok = tokStream.getToken();
        current_kind = tokStream.getKind(curtok);
        if (stack == null)
            reallocateStacks();
        if (action == null)
            action = new IntTuple(1 << 10);
        else action.reset();
    }

    public void parseCharacters(ParseTable prs, RuleAction ra)
    {
        pushPtRa(prs, ra);
        parseCharacters();
    }

    //
    // Parse the input and create a stream of tokens.
    //
    public void parseCharacters()
    {
        parseCharacters(null);
    }

    public void parseCharacters(Monitor monitor)
    {
        resetTokenStream(0);
        lastToken = tokStream.getPrevious(curtok);

        //
        // Until it reaches the end-of-file token, this outer loop
        // resets the parser and processes the next token.
        //
        ProcessTokens: while (current_kind != EOFT_SYMBOL)
        {
            //
            // if the parser needs to stop processing,
            // it may do so here.
            //
            if (monitor != null && monitor.isCancelled())
                return;

            stateStackTop = -1;
            currentAction = START_STATE;
            starttok = curtok;

            ParseTable prs1 = prs;
            RuleAction ra1  = ra;
            int start_symbol = prs1.getStartSymbol();
            int num_rules    = prs1.getNumRules();
            
            ScanToken:for (;;)
            {
                try
                {
                    stack[++stateStackTop] = currentAction;
                }
                catch(IndexOutOfBoundsException e)
                {
                    reallocateStacks();
                    stack[stateStackTop] = currentAction;
                }

                locationStack[stateStackTop] = curtok;
    
                currentAction = tAction(currentAction, current_kind);
    
                if (currentAction <= num_rules)
                {
                    stateStackTop--; // make reduction look like a shift-reduce
                    do
                    {
                        int lhs_symbol = prs1.lhs(currentAction);
                        int rule_size = prs1.rhs(currentAction);
                        stateStackTop -= (rule_size - 1);
                        if (lhs_symbol == start_symbol)
                        {
                            if (rule_size == 0) //empty reduction to START_SYMBOL is illegal
                                 break ScanToken;
                            else 
                            {
                                ra1.ruleAction(currentAction);
                                continue ProcessTokens;
                            }
                        }
                        ra1.ruleAction(currentAction);
                        currentAction = prs1.ntAction(stack[stateStackTop], lhs_symbol);
                    } while(currentAction <= num_rules);
                }
                else if (currentAction > ERROR_ACTION)
                {
                    lastToken = curtok;
                    curtok = tokStream.getToken();
                    current_kind = tokStream.getKind(curtok);
                    currentAction -= ERROR_ACTION;
                    do
                    {
                        int lhs_symbol = prs1.lhs(currentAction);
                        stateStackTop -= (prs1.rhs(currentAction) - 1);
                        if (lhs_symbol == start_symbol)
                        {
                            ra1.ruleAction(currentAction);
                            continue ProcessTokens;
                        }
                        ra1.ruleAction(currentAction);
                        currentAction = prs1.ntAction(stack[stateStackTop], lhs_symbol);
                    } while(currentAction <= num_rules);
                }
                else if (currentAction < ACCEPT_ACTION)
                {
                    lastToken = curtok;
                    curtok = tokStream.getToken();
                    current_kind = tokStream.getKind(curtok);
                }
                else break; // ERROR_ACTION only. (ACCEPT_ACTION is not possible)
            }

            //
            // Whenever we reach this point, an error has been detected.
            // Note that the parser loop above can never reach the ACCEPT
            // point as it is short-circuited each time it reduces a phrase
            // to the START_SYMBOL.
            //
            // If an error is detected on a single bad character,
            // we advance to the next character before resuming the
            // scan. However, if an error is detected after we start
            // scanning a construct, we form a bad token out of the
            // characters that have already been scanned and resume
            // scanning on the character on which the problem was
            // detected. In other words, in that case, we do not advance.
            //
            if (starttok == curtok)
            {
                if (current_kind == EOFT_SYMBOL)
                    return;
                tokStream.reportError(starttok, curtok);
                lastToken = curtok;
                curtok = tokStream.getToken();
                current_kind = tokStream.getKind(curtok);
            }
            else tokStream.makeToken(starttok, lastToken, 0);
        }

        return;
    }

    //
    // keep looking ahead until we compute a valid action
    //
    private int lookahead(int act, int token)
    {
        act = prs.lookAhead(act - LA_STATE_OFFSET, tokStream.getKind(token));
        return (act > LA_STATE_OFFSET
                    ? lookahead(act, tokStream.getNext(token))
                    : act);
    }

    //
    // Compute the next action defined on act and sym. If this
    // action requires more lookahead, these lookahead symbols
    // are in the token stream beginning at the next token that
    // is yielded by peek().
    //
    private int tAction(int act, int sym)
    {
        act = prs.tAction(act, sym);
        return  (act > LA_STATE_OFFSET
                     ? lookahead(act, tokStream.peek())
                     : act);
    }

    private /* for now */ void scanNextToken(int i)
    {
        // reset here !!!
    }
    
    private /* for now */ void scanNextToken()
    {
        stateStackTop = -1;
        currentAction = START_STATE;
        starttok = curtok;

        int start_symbol = prs.getStartSymbol(),
            num_rules    = prs.getNumRules();
        
        ScanToken:for (;;)
        {
            try
            {
                stack[++stateStackTop] = currentAction;
            }
            catch(IndexOutOfBoundsException e)
            {
                reallocateStacks();
                stack[stateStackTop] = currentAction;
            }

            currentAction = tAction(currentAction, current_kind);

            if (currentAction <= num_rules)
            {
                action.add(currentAction);

                stateStackTop--; // make initial reduction look like a shift-reduce
                do
                {
                    int lhs_symbol = prs.lhs(currentAction),
                        rule_size = prs.rhs(currentAction);
                    if (lhs_symbol == start_symbol)
                    {
                        if (rule_size == 0) //empty reduction to START_SYMBOL is illegal
                             break ScanToken;
                        else return;
                    }

                    stateStackTop -= (rule_size - 1);
                    currentAction = prs.ntAction(stack[stateStackTop], lhs_symbol);
                } while(currentAction <= num_rules);
            }
            else if (currentAction > ERROR_ACTION)
            {
                action.add(currentAction);

                lastToken = curtok;
                curtok = tokStream.getToken();
                current_kind = tokStream.getKind(curtok);
                currentAction -= ERROR_ACTION;
                do
                {
                    int lhs_symbol = prs.lhs(currentAction);
                    stateStackTop -= (prs.rhs(currentAction) - 1);
                    if (lhs_symbol == start_symbol)
                        return;
                    currentAction = prs.ntAction(stack[stateStackTop], lhs_symbol);
                } while(currentAction <= num_rules);
            }
            else if (currentAction < ACCEPT_ACTION)
            {
                action.add(currentAction);

                lastToken = curtok;
                curtok = tokStream.getToken();
                current_kind = tokStream.getKind(curtok);
            }
            else break; // ERROR_ACTION only. (ACCEPT_ACTION is not possible)
        }

        //
        // Whenever we reach this point, an error has been detected.
        // Note that the parser loop above can never reach the ACCEPT
        // point as it is short-circuited each time it reduces a phrase
        // to the START_SYMBOL.
        //
        // If an error is detected on a single bad character,
        // we advance to the next character before resuming the
        // scan. However, if an error is detected after we start
        // scanning a construct, we form a bad token out of the
        // characters that have already been scanned and resume
        // scanning on the character on which the problem was
        // detected. In other words, in that case, we do not advance.
        //
        if (starttok == curtok)
        {
            if (current_kind == EOFT_SYMBOL)
                return;
            tokStream.reportError(starttok, curtok);
            lastToken = curtok;
            curtok = tokStream.getToken();
            current_kind = tokStream.getKind(curtok);
        }
        else tokStream.makeToken(starttok, lastToken, 0);
    }
}
