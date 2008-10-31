--
-- An LPG Lexer Template Using lpg.jar
--
-- An instance of this template must have a $Export section and the export_terminals option
-- There must be only one non-terminal, the start symbol, for the keywords
-- The action for each keyword should be a call to $setResult(terminal_symbol)
--
-- Macro that may be redefined in an instance of this template
--
--     $eof_char
--
-- B E G I N N I N G   O F   T E M P L A T E
--
%Options Programming_Language=java,margin=4
%Options table
%options action-block=("*.java", "/.", "./")
%options ParseTable=lpg.runtime.ParseTable
%Options prefix=Char_
%Options single-productions

--
-- This template requires that the name of the EOF token be set
-- to EOF and that the prefix be "Char_" to be consistent with
-- LexerTemplateD.
--
%Eof
    EOF
%End

%Define
    --
    -- Macro that may be respecified in an instance of this template
    --
    $eof_char /.$sym_type$.$prefix$EOF$suffix$./

    --
    -- Macros useful for specifying actions
    --
    $setResult /.keywordKind[$rule_number] = ./

    $Header
    /.
            //
            // Rule $rule_number:  $rule_text
            //
            ./

    $BeginAction /.$Header./

    $EndAction /../

    $BeginJava /.$BeginAction./

    $EndJava /.$EndAction./
%End

%Globals
    /.import lpg.runtime.*;
    ./
%End

%Headers
    /.
    public class $action_type extends $prs_type implements $exp_type
    {
        private char[] inputChars;
        private final int keywordKind[] = new int[$num_rules + 1];

        public int[] getKeywordKinds() { return keywordKind; }

        public int lexer(int curtok, int lasttok)
        {
            int current_kind = getKind(inputChars[curtok]),
                act;

            for (act = tAction(START_STATE, current_kind);
                 act > NUM_RULES && act < ACCEPT_ACTION;
                 act = tAction(act, current_kind))
            {
                curtok++;
                current_kind = (curtok > lasttok
                                       ? $eof_char
                                       : getKind(inputChars[curtok]));
            }

            if (act > ERROR_ACTION)
            {
                curtok++;
                act -= ERROR_ACTION;
            }

            return keywordKind[act == ERROR_ACTION  || curtok <= lasttok ? 0 : act];
        }

        public void setInputChars(char[] inputChars) { this.inputChars = inputChars; }

    ./
%End

%Rules
    /.

        public $action_type(char[] inputChars, int identifierKind)
        {
            this.inputChars = inputChars;
            keywordKind[0] = identifierKind;
    ./
%End

%Trailers
    /.
            for (int i = 0; i < keywordKind.length; i++)
            {
                if (keywordKind[i] == 0)
                    keywordKind[i] = identifierKind;
            }
        }
    }
    ./
%End

--
-- E N D   O F   T E M P L A T E
--
