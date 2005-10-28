--
-- An LPG Lexer Template Using lpg.jar
--
-- An instance of this template must have a $Export section and the export_terminals option
--
-- Macros that must be defined in an instance of this template
--
--     $package_declaration
--     $import_classes
--     $action_class
--     $prs_stream_class -- use /.PrsStream./ if not subclassing
--     $eof_token
--
--
-- B E G I N N I N G   O F   T E M P L A T E
--
%Options programming_language=java,margin=4
%Options table
%options action=("*.java", "/.", "./")
%options ParseTable=com.ibm.lpg.ParseTable

$Define
    --
    -- Macros that may be needed in an instance of this template
    --
    $additional_interfaces /../
    $setSym1 /.lexParser.setSym1./
    $setResult /.lexParser.setSym1./
    $getSym /.lexParser.getSym./
    $getToken /.lexParser.getToken./
    $getLeftSpan /.lexParser.getFirstToken./
    $getRightSpan /.lexParser.getLastToken./
    $prs_stream /.prsStream./
    $super_stream /.LpgLexStream./

    --
    -- Macros useful for specifying actions
    --
    $Header
    /.
                //
                // Rule $rule_number:  $rule_text
                //./

    $DefaultAction
    /. $Header
                case $rule_number: { ./

    $BeginAction
    /.$DefaultAction./

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
                    break; ./

    $BeginActions
    /.
        public void ruleAction( int ruleNumber)
        {
            switch(ruleNumber)
            {./

    $EndActions
    /.
                default:
                    break;
            }
            return;
        }./
$End

$Headers
    /.
    import com.ibm.lpg.*;

    public class $action_class extends $super_stream implements $exp_type, $sym_type, RuleAction$additional_interfaces
    {
        private $prs_stream_class prsStream;
        private ParseTable prs = new $prs_type();
        private LexParser lexParser = new LexParser(this, prs, this);

        public $action_class(String filename, int tab) throws java.io.IOException 
        {
            super(filename, tab);
        }

        public $action_class(char[] input_chars, String filename, int tab)
        {
            super(input_chars, filename, tab);
        }

        public $action_class(char[] input_chars, String filename)
        {
            this(input_chars, filename, 1);
        }

        public $action_class() {}

        public String[] orderedExportedSymbols() { return $exp_type.orderedTerminalSymbols; }
        public LexStream getLexStream() { return (LexStream) this; }

        public void lexer($prs_stream_class prsStream)
        {
            lexer(null, prsStream);
        }

        public void lexer(Monitor monitor, $prs_stream_class prsStream)
        {
            if (getInputChars() == null)
                throw new NullPointerException("LexStream was not initialized");

            this.prsStream = prsStream;

            prsStream.makeToken(0, 0, 0); // Token list must start with a bad token
                
            lexParser.parseCharacters(monitor);  // Lex the input characters
                
            int i = getStreamIndex();
            prsStream.makeToken(i, i, $eof_token); // and end with the end of file token
            prsStream.setStreamLength(prsStream.getSize());
                
            return;
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
