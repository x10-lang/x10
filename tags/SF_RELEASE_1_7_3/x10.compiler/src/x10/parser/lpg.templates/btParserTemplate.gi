--
-- In a parser using this template, the following macro may be redefined:
--
--     $additional_interfaces
--     $ast_class
--
-- B E G I N N I N G   O F   T E M P L A T E
--
%Options programming_language=java,margin=4,backtrack
%Options table,error_maps,scopes
%options prefix=TK_
%options action_block=("*.java", "/.", "./")
%options ParseTable=lpg.runtime.ParseTable
%options nt-check

--
-- This template requires that the name of the EOF token be set
-- to EOF_TOKEN to be consistent with LexerTemplateD and LexerTemplateE
--
%EOF
    EOF_TOKEN
%End

%ERROR
    ERROR_TOKEN
%End

%Define

    $Header
    /.
                //
                // Rule $rule_number:  $rule_text
                //./

    $BeginAction
    /. $Header
                case $rule_number: {
                    //#line $next_line "$input_file$"./

    $EndAction
    /.          break;
                }./

    $BeginJava
    /. $Header
                case $rule_number: {
                    $symbol_declarations
                    //#line $next_line "$input_file$"./

    $EndJava /.$EndAction./

    $NoAction
    /. $Header
                case $rule_number:
                    break;./

    $BadAction
    /. $Header
                case $rule_number:
                    throw new Error("No action specified for rule " + $rule_number);./

    $NullAction
    /. $Header
                case $rule_number:
                    setResult(null);
                    break;./

    $BeginActions
    /.
        public void ruleAction(int ruleNumber)
        {
            switch (ruleNumber)
            {./

    $SplitActions
    /.
	            default:
	                ruleAction$rule_number(ruleNumber);
	                break;
	        }
	        return;
	    }
	
	    public void ruleAction$rule_number(int ruleNumber)
	    {
	        switch (ruleNumber)
	        {
	            //#line $next_line "$input_file$"./

    $EndActions
    /.
                default:
                    break;
            }
            return;
        }./

    $entry_declarations
    /.
        public $ast_class parse$entry_name()
        {
            return parse$entry_name(null, Integer.MAX_VALUE);
        }
        
        public $ast_class parse$entry_name(Monitor monitor)
        {
            return parse$entry_name(monitor, Integer.MAX_VALUE);
        }
        
        public $ast_class parse$entry_name(int error_repair_count)
        {
            return parse$entry_name(null, error_repair_count);
        }

        public $ast_class parse$entry_name(Monitor monitor, int error_repair_count)
        {
            try
            {
                btParser = new BacktrackingParser(monitor, (TokenStream) this, prs, (RuleAction) this);
            }
            catch (NotBacktrackParseTableException e)
            {
                throw new Error(new NotBacktrackParseTableException
                                    ("Regenerate $prs_type.java with -BACKTRACK option"));
            }
            catch (BadParseSymFileException e)
            {
                throw new Error(new BadParseSymFileException("Bad Parser Symbol File -- $sym_type.java"));
            }

            try
            {
                return ($ast_class) btParser.fuzzyParseEntry($sym_type.$entry_marker, error_repair_count);
            }
            catch (BadParseException e)
            {
                reset(e.error_token); // point to error token
                DiagnoseParser diagnoseParser = new DiagnoseParser(this, prs);
                diagnoseParser.diagnoseEntry($sym_type.$entry_marker, e.error_token);
            }

            return null;
        }
    ./

    --
    -- Macros that may be needed in a parser using this template
    --
    $additional_interfaces /../
    $ast_class /.$ast_type./
%End

%Globals
    /.import lpg.runtime.*;
    ./
%End

%Headers
    /.
    public class $action_type extends PrsStream implements RuleAction$additional_interfaces
    {
        private static ParseTable prs = new $prs_type();
        private BacktrackingParser btParser;

        public BacktrackingParser getParser() { return btParser; }
        private void setResult(Object object) { btParser.setSym1(object); }
        public Object getRhsSym(int i) { return btParser.getSym(i); }

        public int getRhsTokenIndex(int i) { return btParser.getToken(i); }
        public IToken getRhsIToken(int i) { return super.getIToken(getRhsTokenIndex(i)); }
        
        public int getRhsFirstTokenIndex(int i) { return btParser.getFirstToken(i); }
        public IToken getRhsFirstIToken(int i) { return super.getIToken(getRhsFirstTokenIndex(i)); }

        public int getRhsLastTokenIndex(int i) { return btParser.getLastToken(i); }
        public IToken getRhsLastIToken(int i) { return super.getIToken(getRhsLastTokenIndex(i)); }

        public int getLeftSpan() { return btParser.getFirstToken(); }
        public IToken getLeftIToken()  { return super.getIToken(getLeftSpan()); }

        public int getRightSpan() { return btParser.getLastToken(); }
        public IToken getRightIToken() { return super.getIToken(getRightSpan()); }

        public int getRhsErrorTokenIndex(int i)
        {
            int index = btParser.getToken(i);
            IToken err = super.getIToken(index);
            return (err instanceof ErrorToken ? index : 0);
        }
        public ErrorToken getRhsErrorIToken(int i)
        {
            int index = btParser.getToken(i);
            IToken err = super.getIToken(index);
            return (ErrorToken) (err instanceof ErrorToken ? err : null);
        }

        public $action_type(ILexStream lexStream)
        {
            super(lexStream);

            try
            {
                super.remapTerminalSymbols(orderedTerminalSymbols(), $prs_type.EOFT_SYMBOL);
            }
            catch(NullExportedSymbolsException e) {
            }
            catch(NullTerminalSymbolsException e) {
            }
            catch(UnimplementedTerminalsException e)
            {
                java.util.ArrayList unimplemented_symbols = e.getSymbols();
                System.out.println("The Lexer will not scan the following token(s):");
                for (int i = 0; i < unimplemented_symbols.size(); i++)
                {
                    Integer id = (Integer) unimplemented_symbols.get(i);
                    System.out.println("    " + $sym_type.orderedTerminalSymbols[id.intValue()]);               
                }
                System.out.println();                        
            }
            catch(UndefinedEofSymbolException e)
            {
                throw new Error(new UndefinedEofSymbolException
                                    ("The Lexer does not implement the Eof symbol " +
                                     $sym_type.orderedTerminalSymbols[$prs_type.EOFT_SYMBOL]));
            } 
        }

        public int numTokenKinds() { return $sym_type.orderedTerminalSymbols.length; }
        public String[] orderedTerminalSymbols() { return $sym_type.orderedTerminalSymbols; }
        public String getTokenKindName(int kind) { return $sym_type.orderedTerminalSymbols[kind]; }
        public int getEOFTokenKind() { return $prs_type.EOFT_SYMBOL; }
        public PrsStream getParseStream() { return (PrsStream) this; }

        /**
         * When constructing a SAFARI parser, a handler for error messages
         * can be passed to the parser.
         */
        /*
        private IMessageHandler handler = null;
        public void setMessageHandler(IMessageHandler handler)
        {
            this.handler = handler;
        }
        
        //
        // Redirect syntax error message to proper recipient.
        //
        public void reportError(int error_code, String location_info, int left_token, int right_token, String token_text)
        {
            if (this.handler == null)
                super.reportError(error_code,
                                  location_info,
                                  left_token,
                                  right_token,
                                  token_text);
            else 
            {
                int start_offset = super.getStartOffset(left_token),
                    end_offset = (right_token > left_token 
                                              ? super.getEndOffset(right_token)
                                              : super.getEndOffset(left_token));

                String msg = ((error_code == DELETION_CODE ||
                               error_code == MISPLACED_CODE ||
                               token_text.equals(""))
                                           ? ""
                                           : (token_text + " ")) +
                             errorMsgText[error_code];

                handler.handleMessage(start_offset,
                                      end_offset - start_offset + 1,
                                      msg);
            }
        }

        //
        // Report error message for given error_token.
        //
        public final void reportErrorTokenMessage(int error_token, String msg)
        {
            if (this.handler == null)
            {
                int firsttok = super.getFirstRealToken(error_token),
                    lasttok = super.getLastRealToken(error_token);
                String location = super.getFileName() + ':' +
                                  (firsttok > lasttok
                                            ? (super.getEndLine(lasttok) + ":" + super.getEndColumn(lasttok))
                                            : (super.getLine(error_token) + ":" +
                                               super.getColumn(error_token) + ":" +
                                               super.getEndLine(error_token) + ":" +
                                               super.getEndColumn(error_token)))
                                  + ": ";
                super.reportError((firsttok > lasttok ? ParseErrorCodes.INSERTION_CODE : ParseErrorCodes.SUBSTITUTION_CODE), location, msg);
            }
            else 
            {
                handler.handleMessage(super.getStartOffset(error_token),
                                      super.getTokenLength(error_token),
                                      msg);
            }
        }
        */

        public $ast_class parser()
        {
            return parser(null, Integer.MAX_VALUE);
        }
        
        public $ast_class parser(Monitor monitor)
        {
            return parser(monitor, Integer.MAX_VALUE);
        }
        
        public $ast_class parser(int error_repair_count)
        {
            return parser(null, error_repair_count);
        }

        public $ast_class parser(Monitor monitor, int error_repair_count)
        {
            try
            {
                btParser = new BacktrackingParser(monitor, (TokenStream) this, prs, (RuleAction) this);
            }
            catch (NotBacktrackParseTableException e)
            {
                throw new Error(new NotBacktrackParseTableException
                                    ("Regenerate $prs_type.java with -BACKTRACK option"));
            }
            catch (BadParseSymFileException e)
            {
                throw new Error(new BadParseSymFileException("Bad Parser Symbol File -- $sym_type.java"));
            }

            try
            {
                return ($ast_class) btParser.fuzzyParse(error_repair_count);
            }
            catch (BadParseException e)
            {
                reset(e.error_token); // point to error token
                DiagnoseParser diagnoseParser = new DiagnoseParser(this, prs);
                diagnoseParser.diagnose(e.error_token);
            }

            return null;
        }

        //
        // Additional entry points, if any
        //
        $entry_declarations
    ./

%End

%Rules
    /.$BeginActions./
%End

%Trailers
    /.
        $EndActions
    }
    ./
%End

--
-- E N D   O F   T E M P L A T E
--
