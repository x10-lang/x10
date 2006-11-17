package lpg.lpgjavaruntime;

public class RecoveryParser extends DiagnoseParser implements ParseErrorCodes
{
    private BacktrackingParser parser;
    private IntTuple action,
                     tokens;
    private int actionStack[];
    private PrimaryRepairInfo scope_repair = new PrimaryRepairInfo();

    //
    //
    //
    public RecoveryParser(BacktrackingParser parser, IntTuple action, IntTuple tokens, TokenStream tokStream, ParseTable prs)
    {
        this(parser, null, action, tokens, tokStream, prs);
    }

    public RecoveryParser(BacktrackingParser parser, Monitor monitor, IntTuple action, IntTuple tokens, TokenStream tokStream, ParseTable prs)
    {
        this(parser, monitor, action, tokens, tokStream, prs, 0, 0);
    }
    
    public RecoveryParser(BacktrackingParser parser, IntTuple action, IntTuple tokens, TokenStream tokStream, ParseTable prs, int maxErrors, long maxTime)
    {
        this(parser, null, action, tokens, tokStream, prs, maxErrors, maxTime);
    }

    //
    // maxErrors is the maximum number of errors to be repaired
    // maxTime is the maximum amount of time allowed for diagnosing
    // but at least one error must be diagnosed 
    //
    public RecoveryParser(BacktrackingParser parser, Monitor monitor, IntTuple action, IntTuple tokens, TokenStream tokStream, ParseTable prs, int maxErrors, long maxTime)
    {
        super(monitor, tokStream, prs, maxErrors, maxTime);
        this.parser = parser;
        this.action = action;
        this.tokens = tokens;
    }

    protected void reallocateStacks()
    {
        super.reallocateStacks();
        if (actionStack == null)
            actionStack = new int[stateStack.length];
        else
        {
            int old_stack_length = actionStack.length;
            System.arraycopy(actionStack, 0, actionStack = new int[stateStack.length], 0, old_stack_length);
        }

        return;
    }

    public void reportError(int scope_index, int error_token)
    {
        String text = "\"";
        for (int i = scopeSuffix(scope_index); scopeRhs(i) != 0; i++)
        {
            if (! isNullable(scopeRhs(i)))
            {
                int symbol_index = (scopeRhs(i) > NT_OFFSET
                                        ? nonterminalIndex(scopeRhs(i) - NT_OFFSET)
                                        : terminalIndex(scopeRhs(i)));
                if (name(symbol_index).length() > 0)
                {
                    if (text.length() > 1) // Not just starting quote?
                        text += " "; // add a space separator
                    text += name(symbol_index);
                }
            }
        }
        text += "\"";

        String location = tokStream.getFileName()
                              + ':' + tokStream.getLine(error_token)
                              + ':' + tokStream.getColumn(error_token)
                              + ':' + tokStream.getEndLine(error_token)
                              + ':' + tokStream.getEndColumn(error_token)
                              + ": ";

        tokStream.reportError(SCOPE_CODE,
                              location,
                              error_token,
                              error_token,
                              text);
        return;        
    }
    
    public int recover(int error_token) throws BadParseException
    {
        if (stateStack == null)
            reallocateStacks();

        //
        //
        //
        tokStream.reset();
        tokens.reset();
        int restart_token = tokStream.peek(),
            old_action_size = 0;
        tokens.add(tokStream.getPrevious(restart_token));
        stateStackTop = 0;
        stateStack[stateStackTop] = START_STATE;

        do
        {
            tokStream.reset(restart_token);
            action.reset(old_action_size);
            if (! fixError(error_token))
                throw new BadParseException(error_token);

            //
            // if the parser needs to stop processing,
            // it may do so here.
            //
            if (monitor != null && monitor.isCancelled())
                break;

            //
            // At this stage, we have a recovery configuration. See how
            // far we can go with it.
            //
            restart_token = error_token;
            tokStream.reset(error_token);
            old_action_size = action.size(); // save the old size in case we encounter a new error
            error_token = parser.backtrackParse(stateStack, stateStackTop, action, 0);
        } while (error_token != 0); // no error found

        return restart_token;
    }

//void TemporaryErrorDump()
//{
//int prevStackTop = stateStackTop;
//System.arraycopy(stateStack, 0, prevStack, 0, stateStackTop + 1); // save StateStack
//RepairCandidate candidate = primaryDiagnosis(scope_repair);
//stateStackTop = prevStackTop;
//System.arraycopy(prevStack, 0, stateStack, 0, stateStackTop + 1); // restore StateStack
//}

    //
    // Given the configuration consisting of the states in stateStack
    // and the sequence of tokens (current_kind, followed by the tokens
    // in tokStream), fixError parses up to error_token in the tokStream
    // recovers, if possible, from that error and returns the result.
    // While doing this, it also computes the location_stack information
    // and the sequence of actions that matches up with the result that
    // it returns.
    //
    private boolean fixError(int error_token)
    {
//System.err.println("fixError entered on error token " + error_token + " ==> " + tokStream.getName(error_token) +
//                   " in state " + originalState(stateStack[stateStackTop]) +
//                   " to restart on token " + tokStream.peek() + " ==> " + tokStream.getName(tokStream.peek()));
        //
        // Save information about the current configuration.
        //
        int start_token = tokStream.getToken(), 
            curtok = start_token, 
            current_kind = tokStream.getKind(curtok);

        buffer[1] = error_token;
        buffer[0] = tokStream.getPrevious(buffer[1]);
        for (int k = 2; k < BUFF_SIZE; k++)
            buffer[k] = tokStream.getNext(buffer[k - 1]);

        scope_repair.distance = 0;
        scope_repair.misspellIndex = 0;
        scope_repair.bufferPosition = 1;

        //
        // Clear the configuration stack.
        //
        main_configuration_stack = new ConfigurationStack(prs);

        //
        // Keep parsing until we reach the end of file and succeed or
        // an error is encountered. The list of actions executed will
        // be store in the "action" tuple.
        //
        locationStack[stateStackTop] = curtok;
        actionStack[stateStackTop] = action.size();
        int act = tAction(stateStack[stateStackTop], current_kind);
        for (;;)
        {
            //
            // if the parser needs to stop processing,
            // it may do so here.
            //
            if (monitor != null && monitor.isCancelled())
                return true;

            if (act <= NUM_RULES)
            {
                action.add(act); // save this reduce action
                stateStackTop--;

                do
                {
                    stateStackTop -= (rhs(act) - 1);
                    act = ntAction(stateStack[stateStackTop], lhs(act));
                } while(act <= NUM_RULES);

                try
                {
                    stateStack[++stateStackTop] = act;
                }
                catch(IndexOutOfBoundsException e)
                {
                    reallocateStacks();
                    stateStack[stateStackTop] = act;
                }
                locationStack[stateStackTop] = curtok;
                actionStack[stateStackTop] = action.size();
                act = tAction(act, current_kind);
                continue;
            }
            else if (act == ERROR_ACTION)
            {
                if (curtok != error_token || main_configuration_stack.size() > 0)
                {
                    ConfigurationElement configuration = main_configuration_stack.pop();
                    if (configuration == null)
                        act = ERROR_ACTION;
                    else
                    {
                        stateStackTop = configuration.stack_top;
                        configuration.retrieveStack(stateStack);
                        act = configuration.act;
                        curtok = configuration.curtok;
                        action.reset(configuration.action_length);
                        current_kind = tokStream.getKind(curtok);
                        tokStream.reset(tokStream.getNext(curtok));
                        continue;
                    }
                }

                break;
            }
            else if (act > ACCEPT_ACTION && act < ERROR_ACTION)
            {
                if (main_configuration_stack.findConfiguration(stateStack, stateStackTop, curtok))
                    act = ERROR_ACTION;
                else
                {
                    main_configuration_stack.push(stateStack, stateStackTop, act + 1, curtok, action.size());
                    act = baseAction(act);
                }
                continue;
            }
            else 
            {
                if (act < ACCEPT_ACTION)
                {
                    action.add(act); // save this shift action
                    curtok = tokStream.getToken();
                    current_kind = tokStream.getKind(curtok);
                }
                else if (act > ERROR_ACTION)
                {
                    action.add(act); // save this shift-reduce action
                    curtok = tokStream.getToken();
                    current_kind = tokStream.getKind(curtok);
                    act -= ERROR_ACTION;
                    do
                    {
                        stateStackTop -= (rhs(act) - 1);
                        act = ntAction(stateStack[stateStackTop], lhs(act));
                    } while(act <= NUM_RULES);
                }
                else break; // assert(act == ACCEPT_ACTION);  THIS IS NOT SUPPOSED TO HAPPEN!!!

                try
                {
                    stateStack[++stateStackTop] = act;
                }
                catch(IndexOutOfBoundsException e)
                {
                    reallocateStacks();
                    stateStack[stateStackTop] = act;
                }

                if (curtok == error_token)
                {
                    scopeTrial(scope_repair, stateStack, stateStackTop);
                    if (scope_repair.distance >= MIN_DISTANCE)
                    {
//TemporaryErrorDump();
                        for (int token = start_token; token != error_token; token = tokStream.getNext(token))
                            tokens.add(token);
                        acceptRecovery(error_token);
                        break; // equivalent to: return true;
                    }
                }

                locationStack[stateStackTop] = curtok;
                actionStack[stateStackTop] = action.size();
                act = tAction(act, current_kind);
            }
        }

//if (act != ERROR_ACTION)
//System.err.println("fixError exiting in state " + originalState(stateStack[stateStackTop]) +
//                   " on symbol " + curtok + " ==> " + tokStream.getName(curtok));
        return (act != ERROR_ACTION);
    }

    //
    //
    //
    private void acceptRecovery(int error_token)
    {
        //
        //
        //
        int action_size = action.size();

        //
        // Simulate parsing actions required for this sequence of scope
        // recoveries.
        // TODO: need to add action and fix the location_stack?
        //
        IntTuple recovery_action = new IntTuple();
        for (int k = 0; k <= scopeStackTop; k++)
        {
            int scope_index = scopeIndex[k], la = scopeLa(scope_index);

            //
            // Compute the action (or set of actions in case of conflicts) that
            // can be executed on the scope lookahead symbol. Save the action(s)
            // in the action tuple.
            //
            recovery_action.reset();
            int act = tAction(stateStack[stateStackTop], la);
            if (act > ACCEPT_ACTION && act < ERROR_ACTION) // conflicting actions?
            {
                do
                {
                    recovery_action.add(baseAction(act++));
                } while (baseAction(act) != 0);
            }
            else recovery_action.add(act);

            //
            // For each action defined on the scope lookahead symbol,
            // try scope recovery. At least one action should succeed!
            //
            int start_action_size = action.size();
            int index;
            for (index = 0; index < recovery_action.size(); index++)
            {
                //
                // Reset the action tuple each time through this loop
                // to clear previous actions that may have been added
                // because of a failed call to completeScope.
                //
                action.reset(start_action_size);
                tokStream.reset(error_token);
                tempStackTop = stateStackTop - 1;
                int max_pos = stateStackTop;

                act = recovery_action.get(index);
                while (act <= NUM_RULES)
                {
                    action.add(act); // save this reduce action
                    //
                    // ... Process all goto-reduce actions following
                    // reduction, until a goto action is computed ...
                    //
                    do
                    {
                        int lhs_symbol = lhs(act);
                        tempStackTop -= (rhs(act) - 1);
                        act = (tempStackTop > max_pos ? tempStack[tempStackTop]
                                : stateStack[tempStackTop]);
                        act = ntAction(act, lhs_symbol);
                    } while (act <= NUM_RULES);
                    if (tempStackTop + 1 >= stateStack.length)
                        reallocateStacks();
                    max_pos = max_pos < tempStackTop ? max_pos : tempStackTop;
                    tempStack[tempStackTop + 1] = act;
                    act = tAction(act, la);
                }

                //
                // If the lookahead symbol is parsable, then we check
                // whether or not we have a match between the scope
                // prefix and the transition symbols corresponding to
                // the states on top of the stack.
                //
                if (act != ERROR_ACTION)
                {
                    nextStackTop = ++tempStackTop;
                    for (int i = 0; i <= max_pos; i++)
                        nextStack[i] = stateStack[i];

                    //
                    // NOTE that we do not need to update location_stack and
                    // actionStack here because, once the rules associated with
                    // these scopes are reduced, all these states will be popped
                    // from the stack.
                    //
                    for (int i = max_pos + 1; i <= tempStackTop; i++)
                        nextStack[i] = tempStack[i];
                    if (completeScope(action, scopeSuffix(scope_index)))
                    {
                        for (int i = scopeSuffix(scopeIndex[k]); scopeRhs(i) != 0; i++)
                        {
                            // System.err.println("(*) adding token for
                            // nonterminal at location " + tokens.size());
                            tokens.add(tokStream.makeErrorToken
                                           (error_token,
                                            tokStream.getPrevious(error_token),
                                            error_token, scopeRhs(i)));
                        }

                        reportError(scopeIndex[k], tokStream.getPrevious(error_token));

                        break;
                    }
                }
            }
            assert (index < recovery_action.size()); // sanity check!
            stateStackTop = nextStackTop;
            System.arraycopy(nextStack, 0, stateStack, 0, stateStackTop + 1);
        }
        
        return;
    }
    
    //
    //
    //
    private boolean completeScope(IntTuple action, int scope_rhs_index)
    {
        int kind = scopeRhs(scope_rhs_index);
        if (kind == 0)
            return true;

        int act = nextStack[nextStackTop];

        if (kind > NT_OFFSET)
        {
            int lhs_symbol = kind - NT_OFFSET;
            if (baseCheck(act + lhs_symbol) != lhs_symbol) // is there a valid
                                                            // action defined on
                                                            // lhs_symbol?
                return false;
            act = ntAction(act, lhs_symbol);

            //
            // if action is a goto-reduce action, save it as a shift-reduce
            // action.
            //
            action.add(act <= NUM_RULES ? act + ERROR_ACTION : act);
            while (act <= NUM_RULES)
            {
                nextStackTop -= (rhs(act) - 1);
                act = ntAction(nextStack[nextStackTop], lhs(act));
            }
            nextStackTop++;
            nextStack[nextStackTop] = act;
//System.err.println("Shifting nonterminal " +
//name(nonterminalIndex(lhs_symbol)));
            return completeScope(action, scope_rhs_index + 1);
        }

        //
        // Processing a terminal
        //
        act = tAction(act, kind);
        action.add(act); // save this terminal action
        assert(act > NUM_RULES);
        if (act < ACCEPT_ACTION)
        {
            nextStackTop++;
            nextStack[nextStackTop] = act;
//System.err.println("Shifting terminal " + name(terminalIndex(kind)));
            return completeScope(action, scope_rhs_index + 1);
        }
        else if (act > ERROR_ACTION)
        {
            act -= ERROR_ACTION;
            do
            {
                nextStackTop -= (rhs(act) - 1);
                act = ntAction(nextStack[nextStackTop], lhs(act));
            } while(act <= NUM_RULES);
            nextStackTop++;
            nextStack[nextStackTop] = act;
//System.err.println("Shift-reducing terminal " + name(terminalIndex(kind)));
//assert(scopeRhs(scope_rhs_index + 1) == 0);
            return true;
        }
        else if (act > ACCEPT_ACTION && act < ERROR_ACTION) // conflicting actions?
        {
            int save_action_size = action.size();
            for (int i = act; baseAction(i) != 0; i++) // consider only shift and shift-reduce actions
            {
                action.reset(save_action_size);
                act = baseAction(i);
                action.add(act); // save this terminal action
                if (act <= NUM_RULES) // Ignore reduce actions
                    ;
                else if (act < ACCEPT_ACTION)
                {
                    nextStackTop++;
                    nextStack[nextStackTop] = act;
//System.err.println("(2)Shifting terminal " + name(terminalIndex(kind)));
                    if (completeScope(action, scope_rhs_index + 1))
                        return true;
                }
                else if (act > ERROR_ACTION)
                {
                    act -= ERROR_ACTION;
                    do
                    {
                        nextStackTop -= (rhs(act) - 1);
                        act = ntAction(nextStack[nextStackTop], lhs(act));
                    } while(act <= NUM_RULES);
                    nextStackTop++;
                    nextStack[nextStackTop] = act;
//System.err.println("(2)Shift-reducing terminal " + name(terminalIndex(kind)));
//assert(scopeRhs(scope_rhs_index + 1) == 0);
                   return true;
                }
            }
        }

        return false;
    }
}
