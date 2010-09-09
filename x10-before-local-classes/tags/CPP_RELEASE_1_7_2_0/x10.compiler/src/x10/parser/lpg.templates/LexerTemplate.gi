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
%options action-block=("*.java", "/.", "./")
%options ParseTable=lpg.runtime.ParseTable
%Options prefix=Char_

--
-- This template requires that the name of the EOF token be set
-- to EOF and that the prefix be "Char_" to be consistent with
-- KeywordTemplateD.
--
%Eof
    EOF
%End

--
-- This template also requires that the name of the parser EOF
-- Token to be exported be set to EOF_TOKEN
--
%Export
    EOF_TOKEN
%End

%Define
    --
    -- Macros that are be needed in an instance of this template
    --
    $eof_token /.$_EOF_TOKEN./
    
    $additional_interfaces /../
    $super_stream_class /.LpgLexStream./
    $prs_stream_class /.IPrsStream./

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

    $BeginAction /.$DefaultAction./

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
        public void ruleAction(int ruleNumber)
        {
            switch(ruleNumber)
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
                {./

    $EndActions
    /.
                default:
                    break;
            }
            return;
        }./
%End

%Globals
    /.import lpg.runtime.*;
    ./
%End

%Headers
    /.
    public class $action_type extends $super_stream_class implements $exp_type, $sym_type, RuleAction$additional_interfaces
    {
        private static ParseTable prs = new $prs_type();
        public ParseTable getParseTable() { return prs; }

        private LexParser lexParser = new LexParser(this, prs, this);
        public LexParser getParser() { return lexParser; }

        public int getToken(int i) { return lexParser.getToken(i); }
        public int getRhsFirstTokenIndex(int i) { return lexParser.getFirstToken(i); }
        public int getRhsLastTokenIndex(int i) { return lexParser.getLastToken(i); }

        public int getLeftSpan() { return lexParser.getToken(1); }
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

        private void initializeLexer($prs_stream_class prsStream, int start_offset, int end_offset)
        {
            if (getInputChars() == null)
                throw new NullPointerException("LexStream was not initialized");
            setPrsStream(prsStream);
            prsStream.makeToken(start_offset, end_offset, 0); // Token list must start with a bad token
        }

        private void addEOF($prs_stream_class prsStream, int end_offset)
        {
            prsStream.makeToken(end_offset, end_offset, $eof_token); // and end with the end of file token
            prsStream.setStreamLength(prsStream.getSize());
        }

        public void lexer($prs_stream_class prsStream)
        {
            lexer(null, prsStream);
        }
        
        public void lexer(Monitor monitor, $prs_stream_class prsStream)
        {
            initializeLexer(prsStream, 0, -1);
            lexParser.parseCharacters(monitor);  // Lex the input characters
            addEOF(prsStream, getStreamIndex());
        }

        public void lexer($prs_stream_class prsStream, int start_offset, int end_offset)
        {
            lexer(null, prsStream, start_offset, end_offset);
        }
        
        public void lexer(Monitor monitor, $prs_stream_class prsStream, int start_offset, int end_offset)
        {
            if (start_offset <= 1)
                 initializeLexer(prsStream, 0, -1);
            else initializeLexer(prsStream, start_offset - 1, start_offset - 1);

            lexParser.parseCharacters(monitor, start_offset, end_offset);

            addEOF(prsStream, (end_offset >= getStreamIndex() ? getStreamIndex() : end_offset + 1));
        }

        /**
         * If a parse stream was not passed to this Lexical analyser then we
         * simply report a lexical error. Otherwise, we produce a bad token.
         */
        public void reportLexicalError(int startLoc, int endLoc) {
            IPrsStream prs_stream = getPrsStream();
            if (prs_stream == null)
                super.reportLexicalError(startLoc, endLoc);
            else {
                //
                // Remove any token that may have been processed that fall in the
                // range of the lexical error... then add one error token that spans
                // the error range.
                //
                for (int i = prs_stream.getSize() - 1; i > 0; i--) {
                    if (prs_stream.getStartOffset(i) >= startLoc)
                         prs_stream.removeLastToken();
                    else break;
                }
                prs_stream.makeToken(startLoc, endLoc, 0); // add an error token to the prsStream
            }        
        }
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
