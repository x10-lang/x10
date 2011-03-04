package lpg.lpgjavaruntime;

public class DeterministicParser extends Stacks
{
    private Monitor monitor = null;
    private int START_STATE,
                NUM_RULES,
                LA_STATE_OFFSET,
                ACCEPT_ACTION,
                ERROR_ACTION;
    
    private int lastToken,
                currentAction;
    private IntTuple action = null;

    private TokenStream tokStream;
    private ParseTable prs;
    private RuleAction ra;

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
        return (act > LA_STATE_OFFSET
                    ? lookahead(act, tokStream.peek())
                    : act);
    }

    //
    // Compute the next action defined on act and the next k tokens
    // whose types are stored in the array sym starting at location
    // index. The array sym is a circular buffer. If we reach the last
    // element of sym and we need more lookahead, we proceed to the
    // first element.
    // 
    // assert(sym.length == prs.getMaxLa());
    //
    private int tAction(int act, int sym[], int index)
    {
        act = prs.tAction(act, sym[index]);
        while(act > LA_STATE_OFFSET)
        {
            index = ((index + 1) % sym.length);
            act = prs.lookAhead(act - LA_STATE_OFFSET, sym[index]);
        }

        return act;
    }

    //
    // Process reductions and continue...
    //
    private final void processReductions()
    {
        do
        {
            stateStackTop -= (prs.rhs(currentAction) - 1);
            ra.ruleAction(currentAction);
            currentAction = prs.ntAction(stateStack[stateStackTop],
                                         prs.lhs(currentAction));
        } while(currentAction <= NUM_RULES);

        return;
    }
    
    //
    // The following functions are meant to be using with the regular
    // parser (via the entry point parse). When using the incremental
    // parser (via the entry point parse(int [], int)), they are undefined
    // and always return 0.
    //
    public final int getCurrentRule()     { return action != null
                                                          ? 0
                                                          : currentAction; }
    public final int getFirstToken()      { return action != null
                                                           ? 0
                                                           : getToken(1); }
    public final int getFirstToken(int i) { return action != null
                                                           ? 0 
                                                           : getToken(i); }
    public final int getLastToken()       { return action != null
                                                           ? 0
                                                           : lastToken; }
    public final int getLastToken(int i)
    {
        return action != null
                      ? 0
                      : (i >= prs.rhs(currentAction)
                            ? lastToken
                            : tokStream.getPrevious(getToken(i + 1)));
    }

    public DeterministicParser(TokenStream tokStream,
                               ParseTable prs,
                               RuleAction ra)
           throws BadParseSymFileException, NotDeterministicParseTableException
    {
        this.tokStream = tokStream;
        this.prs = prs;
        this.ra = ra;

        START_STATE = prs.getStartState();
        NUM_RULES = prs.getNumRules();
        LA_STATE_OFFSET = prs.getLaStateOffset();
        ACCEPT_ACTION = prs.getAcceptAction();
        ERROR_ACTION = prs.getErrorAction();

        if (! prs.isValidForParser()) throw new BadParseSymFileException();
        if (prs.getBacktrack()) throw new NotDeterministicParseTableException();
    }

    public DeterministicParser(Monitor monitor,
                               TokenStream tokStream,
                               ParseTable prs,
                               RuleAction ra)
           throws BadParseSymFileException, NotDeterministicParseTableException
    {
        this(tokStream, prs, ra);
        this.monitor = monitor;
    }
    
    //
    //
    //
    public Object parse() throws BadParseException
    {
        //
        // Indicate that we are running the regular parser and not
        // the incremental one.
        //
        action = null;
        
        //
        // Reset the token stream and get the first token.
        //
        tokStream.reset();
        int curtok = tokStream.getToken(),
            current_kind = tokStream.getKind(curtok);
        lastToken = tokStream.getPrevious(curtok);
 
        //
        // Start parsing.
        //
        reallocateStacks(); // make initial allocation
        stateStackTop = -1;
        currentAction = START_STATE;
        ProcessTerminals: for (;;)
        {
            //
            // if the parser needs to stop processing,
            // it may do so here.
            //
            if (monitor != null && monitor.isCancelled())
                return null;

            try
            {
                stateStack[++stateStackTop] = currentAction;
            }
            catch(IndexOutOfBoundsException e)
            {
                reallocateStacks();
                stateStack[stateStackTop] = currentAction;
            }

            locationStack[stateStackTop] = curtok;

            currentAction = tAction(currentAction, current_kind);
 
            if (currentAction <= NUM_RULES)
            {
                stateStackTop--; // make reduction look like a shift-reduce
                processReductions();
            }
            else if (currentAction > ERROR_ACTION)
            {
                lastToken = curtok;
                curtok = tokStream.getToken();
                current_kind = tokStream.getKind(curtok);
                currentAction -= ERROR_ACTION;
                processReductions();
            }
            else if (currentAction < ACCEPT_ACTION)
            {
                lastToken = curtok;
                curtok = tokStream.getToken();
                current_kind = tokStream.getKind(curtok);
            }
            else break ProcessTerminals;
        }

        if (currentAction == ERROR_ACTION)
            throw new BadParseException(curtok);

        return parseStack[0];
    }

    //
    // This method is invoked when using the parser in an incremental mode
    // using the entry point parse(int [], int).
    //
    public final void resetParser()
    {
        if (stateStack == null)
            reallocateStacks(); // make initial allocation
        stateStackTop = 0;
        stateStack[stateStackTop] = START_STATE;
        if (action == null)
             action = new IntTuple(1 << 20);
        else action.reset();
    }

    //
    // This is an incremental LALR(k) parser that takes as argument
    // the next k tokens in the input. If these k tokens are valid for
    // the current configuration, it advances past the first of the k
    // tokens and returns either:
    //
    //    . the last transition induced by that token 
    //    . the Accept action
    //
    // If the tokens are not valid, the initial configuration remains
    // unchanged and the Error action is returned.
    //
    // Note that it is the user's responsibility to start the parser in a
    // proper configuration by initially invoking the method resetParser
    // prior to invoking this function.
    //
    public int parse(int sym[], int index)
    {
        assert(sym.length == prs.getMaxLa());
        
   
        //
        // First, we save the current length of the action tuple, in
        // case an error is encountered and we need to restore the
        // original configuration.
        //
        // Next, we declara and initialize the variable pos which will
        // be used to indicate the highest useful position in stateStack
        // as we are simulating the actions induced by the next k input
        // terminals in sym.
        //
        // The location stack will be used here as a temporary stack
        // to simulate these actions. We initialize its first useful
        // offset here.
        //
        int save_action_length = action.size(),
            pos = stateStackTop,
            location_top = stateStackTop - 1;

        //
        // When a reduce action is encountered, we compute all REDUCE
        // and associated goto actions induced by the current token.
        // Eventually, a SHIFT, SHIFT-REDUCE, ACCEPT or ERROR action is
        // computed...
        //
        for (currentAction = tAction(stateStack[stateStackTop], sym, index);
             currentAction <= NUM_RULES;
             currentAction = tAction(currentAction, sym, index))
        {
            action.add(currentAction);
            do
            {
                location_top -= (prs.rhs(currentAction) - 1);
                int state = (location_top > pos
                                          ? locationStack[location_top]
                                          : stateStack[location_top]);
                currentAction = prs.ntAction(state, prs.lhs(currentAction));
            } while (currentAction <= NUM_RULES);
            
            //
            // ... Update the maximum useful position of the
            // stateSTACK, push goto state into stack, and
            // continue by compute next action on current symbol
            // and reentering the loop...
            //
            pos = pos < location_top ? pos : location_top;
            try
            {
                locationStack[location_top + 1] = currentAction;
            }
            catch(IndexOutOfBoundsException e)
            {
                reallocateStacks();
                locationStack[location_top + 1] = currentAction;
            }
        }

        //
        // At this point, we have a shift, shift-reduce, accept or error
        // action. stateSTACK contains the configuration of the state stack
        // prior to executing any action on the currenttoken. locationStack
        // contains the configuration of the state stack after executing all
        // reduce actions induced by the current token. The variable pos
        // indicates the highest position in the stateSTACK that is still
        // useful after the reductions are executed.
        //
        if (currentAction > ERROR_ACTION || // SHIFT-REDUCE action ?
            currentAction < ACCEPT_ACTION)  // SHIFT action ?
        {
            action.add(currentAction);

            //
            // If no error was detected, update the state stack with 
            // the info that was temporarily computed in the locationStack.
            //
            stateStackTop = location_top + 1;
            for (int i = pos + 1; i <= stateStackTop; i++)
                stateStack[i] = locationStack[i];

            //
            // If we have a shift-reduce, process it as well as
            // the goto-reduce actions that follow it.
            //
            if (currentAction > ERROR_ACTION)
            {
                currentAction -= ERROR_ACTION;
                do
                {
                    stateStackTop -= (prs.rhs(currentAction) - 1);
                    currentAction = prs.ntAction(stateStack[stateStackTop],
                                                 prs.lhs(currentAction));
                } while (currentAction <= NUM_RULES);
            }

            //
            // Process the final transition - either a shift action of
            // if we started out with a shift-reduce, the final GOTO
            // action that follows it.
            //
            try
            {
                stateStack[++stateStackTop] = currentAction;
            }
            catch(IndexOutOfBoundsException e)
            {
                reallocateStacks();
                stateStack[stateStackTop] = currentAction;
            }
        }
        else if (currentAction == ERROR_ACTION)
             action.reset(save_action_length); // restore original action state.
        
        return currentAction;
    }  

    //
    // Now do the final parse of the input based on the actions in
    // the list "action" and the sequence of tokens in the token stream.
    //
    public Object parseActions() throws BadParseException
    {
        tokStream.reset();
        int curtok = tokStream.getToken();
        lastToken = tokStream.getPrevious(curtok);

        try
        {
            //
            // Reparse the input...
            //
            stateStackTop = -1;
            currentAction = START_STATE;
            for (int i = 0; i < action.size(); i++)
            {
                //
                // if the parser needs to stop processing, it may do so here.
                //
                if (monitor != null && monitor.isCancelled())
                    return null;

                stateStack[++stateStackTop] = currentAction;
                locationStack[stateStackTop] = curtok;

                currentAction = action.get(i);
                if (currentAction <= NUM_RULES) // a reduce action?
                {
                    stateStackTop--; // turn reduction intoshift-reduction
                    processReductions();
                }
                else // a shift or shift-reduce action
                {
                    lastToken = curtok;
                    curtok = tokStream.getToken();
                    if (currentAction > ERROR_ACTION) // a shift-reduce action?
                    {
                        currentAction -= ERROR_ACTION;
                        processReductions();
                    }
                }
            }
        }
        catch (Throwable e) // if any exception is thrown, indicate BadParse
        {
            throw new BadParseException(curtok);
        }

        action = null; // turn into garbage
        return parseStack[0];
    }
}
