--
-- An LPG Parser Template Using lpg.jar
--
--
-- B E G I N N I N G   O F   T E M P L A T E
--
-- In a parser using this template, define the following macros:
--     $action_class
--     $ast_class
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
            if (bad_rule != 0)
                return;

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

$Headers
    /.
    import com.ibm.lpg.*;

    public class $action_class extends PrsStream implements RuleAction, Parser$additional_interfaces
    {
        LexStream lexStream;
        BacktrackingParser btParser;

        public $action_class(LexStream lexStream)
        {
            super(lexStream);
            this.lexStream = lexStream;

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
                throw new Error(e);
            } 
        }

        public String[] orderedTerminalSymbols() { return $sym_type.orderedTerminalSymbols; }
        public final static String getTokenKindName(int kind) { return $sym_type.orderedTerminalSymbols[kind]; }            
        public PrsStream getParseStream() { return (PrsStream) this; }
        public int getEOFTokenKind() { return $prs_type.EOFT_SYMBOL; }
            
        public $ast_class parser()
        {
            return parser(null);
        }

        public $ast_class parser(Monitor monitor)
        {
            ParseTable prs = new $prs_type();

            try
            {
                btParser = new BacktrackingParser(monitor, this, prs, this);
            }
            catch (NotBacktrackParseTableException e)
            {
                System.out.println("****Error: Regenerate $prs_type.java with -BACKTRACK option");
                throw new Error(e);
            }
            catch (BadParseSymFileException e)
            {
                System.out.println("****Error: Bad Parser Symbol File -- $sym_type.java");
                throw new Error(e);
            }

            try
            {
                return ($ast_class) btParser.parse();
            }
            catch (BadParseException e)
            {
                if (monitor == null || !monitor.isCancelled())
                {
                    reset(e.error_token); // point to error token
                    DiagnoseParser diagnoseParser = new DiagnoseParser(this, prs);
                    diagnoseParser.diagnose(e.error_token);
                }
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
