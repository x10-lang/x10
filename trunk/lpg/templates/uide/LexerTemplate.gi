--
-- An LPG Lexer Template Using lpg.jar
--
-- An instance of this template must have a $Export section and the export_terminals option
--
-- Macros that may be redefined in an instance of this template
--
--     $eof_token
--     $additional_interfaces
--     $super_stream_class -- subclass com.ibm.lpg.LpgLexStream for getKind
--     $prs_stream_class -- use /.PrsStream./ if not subclassing
--
-- B E G I N N I N G   O F   T E M P L A T E   LexerTemplateD
--
%Options programming_language=java,margin=4
%Options table
%options action=("*.java", "/.", "./")
%options ParseTable=lpg.lpgjavaruntime.ParseTable

$Define
    --
    -- Macros that are be needed in an instance of this template
    --
    $eof_token /.$_EOF_TOKEN./
    
    $additional_interfaces /../
    $super_stream_class /.LpgLexStream./
    $prs_stream_class /.PrsStream./

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

    $BeginJava
    /.$BeginAction
                    $symbol_declarations./

    $EndJava /.$EndAction./
$End

$Globals
    /.import lpg.lpgjavaruntime.*;
    ./
$End

$Headers
    /.
    public class $action_type extends $super_stream_class implements $exp_type, $sym_type, RuleAction$additional_interfaces
    {
        private static ParseTable prs = new $prs_type();
        private $prs_stream_class prsStream;
        private LexParser lexParser = new LexParser(this, prs, this);

        public $prs_stream_class getPrsStream() { return prsStream; }
        public int getToken(int i) { return lexParser.getToken(i); }
        public int getRhsFirstTokenIndex(int i) { return lexParser.getFirstToken(i); }
        public int getRhsLastTokenIndex(int i) { return lexParser.getLastToken(i); }

        public int getLeftSpan() { return lexParser.getFirstToken(); }
        public int getRightSpan() { return lexParser.getLastToken(); }
  
        public $action_type(String filename, int tab) throws java.io.IOException 
        {
            super(filename, tab);
        }

        public $action_type(char[] input_chars, String filename, int tab)
        {
            super(input_chars, filename, tab);
        }

        public $action_type(char[] input_chars, String filename)
        {
            this(input_chars, filename, 1);
        }

        public $action_type() {}

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
