--
-- An LPG Parser Template Using lpg.jar
--
--
-- B E G I N N I N G   O F   T E M P L A T E
--
-- In a parser using this template, define the following macros:
--
--     $package_declaration
--     $import_classes
--     $action_type
--
%Options escape=$,table=java,margin=4,backtrack,error_maps,scopes
%options action=("*.java", "/.", "./")
%options ParseTable=com.ibm.lpg.ParseTable

$Define

    $Header
    /.
                //
                // Rule $rule_number:  $rule_text
                //./

    $BeginAction
    /. $Header
                case $rule_number: {./

    $EndAction
    /.          break;
                }./

    $BeginJava
    /.$BeginAction
                    $symbol_declarations./

    $EndJava /.$EndAction./

    $NoAction
    /. $Header
                case $rule_number:
                    break;./

    $BadAction
    /. $Header
                case $rule_number:
                    bad_rule = $rule_number;
                    break;./


    $NullAction
    /. $Header
                case $rule_number:
                    $setResult(null);
                    break;./

    $BeginActions
    /.
        public void ruleAction(int ruleNumber)
        {
            switch (ruleNumber)
            {./

    $EndActions
    /.
                default:
                    break;
            }
            return;
        }./

    --
    -- Macros that may be needed in a parser using this template
    --
    $additional_interfaces /../
    $setSym1 /.btParser.setSym1./
    $setResult /.btParser.setSym1./
    $getSym /.btParser.getSym./
    $getToken /.btParser.getToken./
    $getIToken /.getIToken./
    $getLeftSpan /.btParser.getFirstToken./
    $getRightSpan /.btParser.getLastToken./
    $prs_stream /.prsStream./
$End

$Globals
    /.
        import com.ibm.lpg.*;
     ./
$End

$Headers
    /.
    public class $action_type extends PrsStream implements RuleAction$additional_interfaces
    {
        private static ParseTable prs = new $prs_type();
        private BacktrackingParser btParser;

        public $action_type(LexStream lexStream)
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
                System.out.println("The Lexer does not implement the Eof symbol " +
                                   $sym_type.orderedTerminalSymbols[$prs_type.EOFT_SYMBOL]);
                System.exit(12);
            } 
        }

        public String[] orderedTerminalSymbols() { return $sym_type.orderedTerminalSymbols; }
        public String getTokenKindName(int kind) { return $sym_type.orderedTerminalSymbols[kind]; }            
        public int getEOFTokenKind() { return $prs_type.EOFT_SYMBOL; }
        public PrsStream getParseStream() { return (PrsStream) this; }

        //
        // Report error message for given error_token.
        //
        public final void reportErrorTokenMessage(int error_token, String msg)
        {
            int firsttok = super.getFirstErrorToken(error_token),
                lasttok = super.getLastErrorToken(error_token);
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

        //
        // TODO: add the error to a list for the functions hasErrors and getErrors.
        //
        // public void reportError(int errorCode, String locationInfo, int leftToken, int rightToken, String tokenText)
        // {
        //    if (errorCode == DELETION_CODE ||
        //        errorCode == MISPLACED_CODE) tokenText = "";
        //    if (!tokenText.equals("")) tokenText += ' ';
        //        lexStream.reportError(errorCode, locationInfo, leftToken, rightToken, tokenText);
        // }

        public Object parser()
        {
            return parser(null, 0);
        }
        
        public Object parser(Monitor monitor)
        {
            return parser(monitor, 0);
        }

        public Object parser(int error_repair_count)
        {
            return parser(null, error_repair_count);
        }

        public Object parser(Monitor monitor, int error_repair_count)
        {
            try
            {
                btParser = new BacktrackingParser(monitor, (TokenStream)this, prs, (RuleAction)this);
            }
            catch (NotBacktrackParseTableException e)
            {
                System.out.println("****Error: Regenerate $prs_type.java with -BACKTRACK option");
                System.exit(1);
            }
            catch (BadParseSymFileException e)
            {
                System.out.println("****Error: Bad Parser Symbol File -- $sym_type.java");
                System.exit(1);
            }

            try
            {
                return btParser.parse(error_repair_count);
            }
            catch (BadParseException e)
            {
                reset(e.error_token); // point to error token
                DiagnoseParser diagnoseParser = new DiagnoseParser(this, prs);
                diagnoseParser.diagnose(e.error_token);
            }

            return null;
        }

    ./

$End

$Rules
    /.$BeginActions./
$End

$Trailers
    /.
        $EndActions
    }
    ./
$End

--
-- E N D   O F   T E M P L A T E
--
