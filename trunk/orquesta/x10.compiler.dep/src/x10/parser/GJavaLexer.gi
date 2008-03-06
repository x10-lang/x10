--
-- The Java Lexer
--
%Options la=2,list
%Options fp=JavaLexer
%options single_productions
%options package=javaparser
%options template=LexerTemplate.gi
%options filter=GJavaKWLexer.gi

%Include
    LexerBasicMap.gi
%End

%Headers
    --
    -- Additional methods for the action class not provided in the template
    --
    /.
        public $action_type(java.io.Reader reader, String filename) throws java.io.IOException
        {
            ArrayList buffers = new ArrayList();
            int size = 0;
            while (true)
            {
                char block[]= new char[8192];
                int n = reader.read(block, 0, block.length);
                if (n < 0)
                    break;
                size += n;
                buffers.add((Object) block);
            }

            char buffer[] = new char[size];
            for (int i = 0; i < buffers.size(); i++)
            {
                char block[] = (char []) buffers.get(i);
                int blocksize = (size / block.length > 0 ? block.length : size);
                size -= blocksize;
                System.arraycopy(block, 0, buffer, i * block.length, blocksize);
            }
            assert(size == 0);
        
            initialize(buffer, filename);
            kwLexer = new $kw_lexer_class(getInputChars(), $_IDENTIFIER);
        }
        
        private static int LINES = 0,
                           TOKENS = 1,
                           JAVA = 2,
                           X10 = 3;
        private static int differ_mode = X10; // default

        private static boolean ignore_braces = true,
                               dump_input = false;

        private static String extension = "";

        private static int changeCount = 0,
                           insertCount = 0,
                           deleteCount = 0,
                           moveCount = 0;

        static public class DifferJava extends DifferLines implements $exp_type
        {
            protected DifferJava() {}
            
            boolean dump_input = false;

            public DifferJava(PrsStream newStream, PrsStream oldStream)
            {
                super(newStream, oldStream);
            }
            
            public DifferJava(boolean dump_input, PrsStream stream)
            {
                this.dump_input = dump_input;
                ILine [] lines = getBuffer(stream);
            }
            
            private int leftBraceCount = 0,
                        rightBraceCount = 0,
                        classCount = 0,
                        interfaceCount = 0,
                        elementCount;

            public int getLeftBraceCount() { return leftBraceCount; }
            public int getRightBraceCount() { return rightBraceCount; }
            public int getClassCount() { return classCount; }
            public int getInterfaceCount() { return interfaceCount; }
            public int getElementCount() { return elementCount; }

            public int balanceParentheses(PrsStream stream, int token)
            {
                int count = 0;
                if (stream.getKind(token) == TK_LPAREN)
                {
                    count++;
                    for (token++; token < stream.getSize(); token++)
                    {
                        if (stream.getKind(token) == TK_LPAREN)
                            count++;
                        else if (stream.getKind(token) == TK_RPAREN)
                        {
                            if (--count == 0)
                            {
                                token++;
                                break;
                            }
                        }
                    }
                }

                return token;
            }

            public ILine[] getBuffer(PrsStream stream)
            {
                IntTuple line_start = new IntTuple();
                int left_brace_count = 0,
                    right_brace_count = 0,
                    class_count = 0,
                    interface_count = 0;

                line_start.add(0); // skip 0th element
                int token = 1;
                while (token < stream.getSize())
                {
                    line_start.add(token);
                    if (stream.getKind(token) == TK_LBRACE)
                    {
                        left_brace_count++;
                        token++;
                    }
                    else if (stream.getKind(token) == TK_RBRACE)
                    {
                        right_brace_count++;
                        token++;
                    }
                    else
                    {
                        if (stream.getKind(token) == TK_else &&
                            token + 1 < stream.getSize())
                            token++;
                            
                        if (stream.getKind(token) == TK_while ||
                            stream.getKind(token) == TK_for ||
                            stream.getKind(token) == TK_if ||
                            stream.getKind(token) == TK_switch)
                        {
                            token = balanceParentheses(stream, token + 1);
                        }
                        else if (stream.getKind(token) == TK_case ||
                                 stream.getKind(token) == TK_default)
                        {
                            for (; token < stream.getSize(); token++)
                            {
                                if (stream.getKind(token) == TK_COLON)
                                {
                                    token++;
                                    break;
                                }
                                if (stream.getKind(token) == TK_LBRACE || stream.getKind(token) == TK_RBRACE)
                                    break;
                            }
                        }
                        else
                        {
                            for (; token < stream.getSize(); token++)
                            {
                                try
                                {
                                    if (stream.getKind(token) == TK_class && stream.getKind(token+1) == TK_IDENTIFIER)
                                        class_count++;
                                    else if (stream.getKind(token) == TK_interface  && stream.getKind(token+1) == TK_IDENTIFIER)
                                        interface_count++;
                                }
                                catch(ArrayIndexOutOfBoundsException e)
                                {
                                }

                                if (stream.getKind(token) == TK_SEMICOLON)
                                {
                                    token++;
                                    break;
                                }
                                if (stream.getKind(token) == TK_LBRACE || stream.getKind(token) == TK_RBRACE)
                                    break;
                            }
                        }
                    }
                }

                Line buffer[] = new Line[line_start.size() - (ignore_braces ? (left_brace_count + right_brace_count) : 0)];
                buffer[0] = new Line(stream, 0, 0); // always add the starting gate line consisting only of token 0
                line_start.add(stream.getSize()); // add a fence for the last line
                int index = 1;
                for (int line_no = 1; line_no < line_start.size() - 1; line_no++)
                {
                    if (ignore_braces &&
                        (stream.getKind(line_start.get(line_no)) == TK_LBRACE ||
                         stream.getKind(line_start.get(line_no)) == TK_RBRACE))
                        continue;
                    buffer[index++] = new Line(stream, line_start.get(line_no), line_start.get(line_no + 1));
                }
                assert (buffer.length == index);

                leftBraceCount += left_brace_count;
                rightBraceCount += right_brace_count;
                classCount += class_count;
                interfaceCount += interface_count;
                //
                // recall that buffer[0] is not used and the last statement
                // consists only of the EOF character. It is important
                // to treat the EOF as a statement in case the user
                // specifies a null file.
                //
                elementCount += (buffer.length - 2);
                if (dump_input)
                {
                    System.out.println();
                    System.out.println("Dumping file " + stream.getFileName());
                    for (int i = 1; i < buffer.length; i++)
                        System.out.println("    " + i + " " + buffer[i].toString());
                }
                
                return buffer;
            }
        }

        protected static int fileCount = 0,
                             lineCount = 0,
                             leftBraceCount = 0,
                             rightBraceCount = 0,
                             classCount = 0,
                             interfaceCount = 0,
                             statementCount;

        private static void computeStats(String file)
        {
            try
            {
                fileCount++;

                $action_type lexer = new $action_type(file);

                PrsStream stream = new PrsStream(lexer);
                lexer.lexer(stream);
    
                DifferJava diff = (DifferJava) (differ_mode == JAVA
                                                    ? new DifferJava(dump_input, stream)
                                                    : new DifferX10(dump_input, stream));

                lineCount += (stream.getLexStream().getLineCount());
                classCount += diff.getClassCount();
                interfaceCount += diff.getInterfaceCount();
                statementCount += (diff.getElementCount() - diff.getLeftBraceCount() - diff.getRightBraceCount());
                leftBraceCount += diff.getLeftBraceCount();
                rightBraceCount += diff.getRightBraceCount();
            }
            catch (Exception e)
            {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }

        private static void computeStats(File dir)
        {
            try
            {
                File file[] = dir.listFiles();
                for (int i = 0; i < file.length; i++)
                {
                    if (file[i].isDirectory())
                         computeStats(file[i]);
                    else if (file[i].getName().endsWith(extension))
                         computeStats(file[i].getPath());
                }
            }
            catch (Exception e)
            {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }

        private static void compareFiles(String old_file, String new_file)
        {
            try
            {
                $action_type old_lexer, new_lexer;
                
                if (old_file.equals(""))
                {
                    char[] input_chars = new char[0];
                    old_lexer = new $action_type(input_chars, "null_file");
                }
                else old_lexer = new $action_type(old_file);

                if (new_file.equals(""))
                {
                    char[] input_chars = new char[0];
                    new_lexer = new $action_type(input_chars, "null_file");
                }
                else new_lexer = new $action_type(new_file);

                PrsStream old_stream = new PrsStream(old_lexer);
                old_lexer.lexer(old_stream);
    
                PrsStream new_stream = new PrsStream(new_lexer);
                new_lexer.lexer(new_stream);

                Differ diff = (differ_mode == LINES
                                   ? (Differ) new DifferLines(old_stream, new_stream)
                                   : differ_mode == TOKENS
                                         ? (Differ) new DifferTokens(old_stream, new_stream)
                                         : differ_mode == JAVA
                                               ? (Differ) new DifferJava(old_stream, new_stream)
                                               : (Differ) new DifferX10(old_stream, new_stream));

                diff.compare();

                if (diff.getChangeCount() > 0)
                {
                    diff.outputChanges();

                    changeCount += diff.getChangeCount();
                    insertCount += (diff.getInsertCount() + diff.getReplaceInsertCount());
                    deleteCount += (diff.getDeleteCount() + diff.getReplaceDeleteCount());
                    moveCount += diff.getMoveCount();
                }
            }
            catch (Exception e)
            {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }

        private static void compareDirectories(File old_dir, File new_dir)
        {
            try
            {
                File old_file[] = old_dir.listFiles(),
                     new_file[] = new_dir.listFiles();
                HashMap old_map = new HashMap();
                for (int i = 0; i < old_file.length; i++)
                {
                    String name = old_file[i].getName();
                    if (old_file[i].isDirectory() || name.endsWith(extension))
                        old_map.put(name, old_file[i]);
                }

                for (int i = 0; i < new_file.length; i++)
                {
                    File file = (File) old_map.get(new_file[i].getName());
                    if (file != null)
                    {
                        old_map.remove(new_file[i].getName());

                        if (file.isDirectory() && new_file[i].isDirectory())
                             compareDirectories(file, new_file[i]);
                        else compareFiles(file.getPath(), new_file[i].getPath());
                    }
                    else if (new_file[i].isDirectory() || new_file[i].getName().endsWith(extension))
                    {
                        String s = new_file[i].getName() +
                                   " found in directory " +
                                   new_dir.getPath() +
                                   " does not exist in directory " +
                                   old_dir.getPath();
                        System.err.println("*Warning: " + s);

                        if (! new_file[i].isDirectory())
                            compareFiles("", new_file[i].getPath());
                    } 
                }

                for (Iterator i = old_map.entrySet().iterator(); i.hasNext(); )
                {
                    Map.Entry e = (Map.Entry) i.next();
                    File file = (File) e.getValue();

                    String s = file.getName() +
                               " found in directory " +
                               old_dir.getPath() +
                               " does not exist in directory " +
                               new_dir.getPath();
                    System.err.println("*Warning: " + s);

                    if (! file.isDirectory())
                        compareFiles(file.getPath(), "");
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public static void main(String[] args)
        {
            String new_file = null,
                   old_file = null;
            boolean help = false;

            int i;
            for (i = 0; i < args.length; i++)
            {
                if (args[i].charAt(0) == '-')
                {
                    if (args[i].equals("-b"))
                         ignore_braces = false;
                    else if (args[i].equals("-d"))
                         dump_input = true;
                    else if (args[i].equals("-ext"))
                         extension = (i + 1 < args.length ? args[++i] : "");
                    else if (args[i].equals("-h"))
                         help = true;
                    else if (args[i].equals("-j"))
                         differ_mode = JAVA;
                    else if (args[i].equals("-l"))
                         differ_mode = LINES;
                    else if (args[i].equals("-t"))
                         differ_mode = TOKENS;
                    else if (args[i].equals("-x"))
                         differ_mode = X10;
                }
                else break;
            }
            if (i < args.length)
            {
                new_file = args[i++];
                old_file = new_file; // assume only one file specified
            }
            if (i < args.length) 
                old_file = args[i++];
            for (; i < args.length; i++)
                System.err.println("Invalid argument: " + args[i]);

            if (help || (new_file == null &&  old_file == null))
            {
                System.out.println();
                System.out.println("Usage: diff [OPTION]... file1 [file2]");
                System.out.println("Compute stats for file1 or compare file1 to file2 statement by statement.");
                System.out.println();
                System.out.println("  -b     -- do not ignore braces");
                System.out.println("  -d     -- if file2 is not specified, dump the relevant content of file1");
                System.out.println("  -ext s -- if file1 and file2 are directories, compare only files that end\n" +
                                   "            with the extension (suffix) s.");
                System.out.println("  -h     -- print this help message");
                System.out.println("  -j     -- assume input is Java");
                System.out.println("  -l     -- compare line by line instead of statement by statement");
                System.out.println("  -t     -- compare token by token instead of statement by statement");
                System.out.println("  -x     -- assume input is X10 (default)");
            }
            else if (old_file.equals(new_file))
            {
assert(old_file != null);
assert(new_file != null);
                File old_dir = new File(old_file);
                if (old_dir.isDirectory())
                     computeStats(old_dir);
                else computeStats(old_file);

                System.out.println("Stats for " + old_file + ":");
                System.out.println("    Number of files: " + fileCount);
                System.out.println("    Number of lines: " + lineCount);
                System.out.println("    Number of types (classes/interfaces): " + (classCount + interfaceCount) + " (" + classCount + "/" + interfaceCount + ")");
                System.out.println("    Number of statements: " + statementCount);
                System.out.println("    Number of braces (left/right): (" + leftBraceCount + "/" + rightBraceCount + ")");
            }
            else
            {
assert(old_file != null);
assert(new_file != null);
                File old_dir = new File(old_file),
                     new_dir = new File(new_file);
                if (old_dir.isDirectory() && new_dir.isDirectory())
                     compareDirectories(old_dir, new_dir);
                else compareFiles(old_file, new_file);

                if (changeCount == 0)
                    System.out.println("***** No difference *****");
                else
                {
                    System.out.println("***** " +
                                       changeCount +
                                       " different " +
                                       (changeCount == 1 ? "section" : "sections") + " *****");
                    System.out.println("    " + moveCount    + " statements moved");
                    System.out.println("    " + insertCount  + " statements inserted");
                    System.out.println("    " + deleteCount  + " statements deleted");
                }
            }
            return;
        }
    ./
%End

%Globals
    /.import java.util.*;./
%End
    
%Define
    --
    -- Definition of macro used in the included file LexerBasicMapB.g
    --
    $kw_lexer_class /.$GJavaKWLexer./

%End

%Export

    IDENTIFIER

    SlComment
    MlComment
    DocComment
    IntegerLiteral
    LongLiteral
    FloatingPointLiteral
    DoubleLiteral
    CharacterLiteral
    StringLiteral
    PLUS_PLUS
    MINUS_MINUS
    EQUAL_EQUAL
    LESS_EQUAL
    GREATER_EQUAL
    NOT_EQUAL
    LEFT_SHIFT
    RIGHT_SHIFT
    UNSIGNED_RIGHT_SHIFT
    PLUS_EQUAL
    MINUS_EQUAL
    MULTIPLY_EQUAL
    DIVIDE_EQUAL
    AND_EQUAL
    OR_EQUAL
    XOR_EQUAL
    REMAINDER_EQUAL
    LEFT_SHIFT_EQUAL
    RIGHT_SHIFT_EQUAL
    UNSIGNED_RIGHT_SHIFT_EQUAL
    OR_OR
    AND_AND
    PLUS
    MINUS
    NOT
    REMAINDER
    XOR
    AND
    MULTIPLY
    OR
    TWIDDLE
    DIVIDE
    GREATER
    LESS
    LPAREN
    RPAREN
    LBRACE
    RBRACE
    LBRACKET
    RBRACKET
    SEMICOLON
    QUESTION
    AT
    COLON
    COMMA
    DOT
    EQUAL
    ELLIPSIS

%End

%Terminals
    CtlCharNotWS

    LF   CR   HT   FF

    a    b    c    d    e    f    g    h    i    j    k    l    m
    n    o    p    q    r    s    t    u    v    w    x    y    z
    _

    A    B    C    D    E    F    G    H    I    J    K    L    M
    N    O    P    Q    R    S    T    U    V    W    X    Y    Z

    0    1    2    3    4    5    6    7    8    9

    AfterASCII   ::= '\u0080..\ufffe'
    Space        ::= ' '
    LF           ::= NewLine
    CR           ::= Return
    HT           ::= HorizontalTab
    FF           ::= FormFeed
    DoubleQuote  ::= '"'
    SingleQuote  ::= "'"
    Percent      ::= '%'
    VerticalBar  ::= '|'
    Exclamation  ::= '!'
    AtSign       ::= '@'
    BackQuote    ::= '`'
    Tilde        ::= '~'
    Sharp        ::= '#'
    DollarSign   ::= '$'
    Ampersand    ::= '&'
    Caret        ::= '^'
    Colon        ::= ':'
    SemiColon    ::= ';'
    BackSlash    ::= '\'
    LeftBrace    ::= '{'
    RightBrace   ::= '}'
    LeftBracket  ::= '['
    RightBracket ::= ']'
    QuestionMark ::= '?'
    Comma        ::= ','
    Dot          ::= '.'
    LessThan     ::= '<'
    GreaterThan  ::= '>'
    Plus         ::= '+'
    Minus        ::= '-'
    Slash        ::= '/'
    Star         ::= '*'
    LeftParen    ::= '('
    RightParen   ::= ')'
    Equal        ::= '='

%End

%Rules

    Token ::= Identifier
        /.$BeginAction
                    checkForKeyWord();
          $EndAction
        ./
    Token ::= '"' SLBody '"'
        /.$BeginAction
                    makeToken($_StringLiteral);
          $EndAction
        ./
    Token ::= "'" NotSQ "'"
        /.$BeginAction
                    makeToken($_CharacterLiteral);
          $EndAction
        ./
    Token ::= IntegerLiteral
        /.$BeginAction
                    makeToken($_IntegerLiteral);
          $EndAction
        ./
    Token ::= LongLiteral
        /.$BeginAction
                    makeToken($_LongLiteral);
          $EndAction
        ./
    Token ::= FloatingPointLiteral
        /.$BeginAction
                    makeToken($_FloatingPointLiteral);
          $EndAction
        ./
    Token ::= DoubleLiteral
        /.$BeginAction
                    makeToken($_DoubleLiteral);
          $EndAction
        ./

    Token ::= MultiLineComment

    Token ::= SingleLineComment

    Token ::= WS -- White Space is scanned but not added to output vector
        /.$BeginAction
                    skipToken();
          $EndAction
        ./
    Token ::= '+'
        /.$BeginAction
                    makeToken($_PLUS);
          $EndAction
        ./
    Token ::= '-'
        /.$BeginAction
                    makeToken($_MINUS);
          $EndAction
        ./

    Token ::= '*'
        /.$BeginAction
                    makeToken($_MULTIPLY);
          $EndAction
        ./

    Token ::= '/'
        /.$BeginAction
                    makeToken($_DIVIDE);
          $EndAction
        ./

    Token ::= '('
        /.$BeginAction
                    makeToken($_LPAREN);
          $EndAction
        ./

    Token ::= ')'
        /.$BeginAction
                    makeToken($_RPAREN);
          $EndAction
        ./

    Token ::= '='
        /.$BeginAction
                    makeToken($_EQUAL);
          $EndAction
        ./

    Token ::= ','
        /.$BeginAction
                    makeToken($_COMMA);
          $EndAction
        ./

    Token ::= ':'
        /.$BeginAction
                    makeToken($_COLON);
          $EndAction
        ./

    Token ::= ';'
        /.$BeginAction
                    makeToken($_SEMICOLON);
          $EndAction
        ./

    Token ::= '^'
        /.$BeginAction
                    makeToken($_XOR);
          $EndAction
        ./

    Token ::= '%'
        /.$BeginAction
                    makeToken($_REMAINDER);
          $EndAction
        ./

    Token ::= '~'
        /.$BeginAction
                    makeToken($_TWIDDLE);
          $EndAction
        ./

    Token ::= '|'
        /.$BeginAction
                    makeToken($_OR);
          $EndAction
        ./

    Token ::= '&'
        /.$BeginAction
                    makeToken($_AND);
          $EndAction
        ./

    Token ::= '<'
        /.$BeginAction
                    makeToken($_LESS);
          $EndAction
        ./

    Token ::= '>'
        /.$BeginAction
                    makeToken($_GREATER);
          $EndAction
        ./

    Token ::= '.'
        /.$BeginAction
                    makeToken($_DOT);
          $EndAction
        ./

    Token ::= '!'
        /.$BeginAction
                    makeToken($_NOT);
          $EndAction
        ./

    Token ::= '['
        /.$BeginAction
                    makeToken($_LBRACKET);
          $EndAction
        ./

    Token ::= ']'
        /.$BeginAction
                    makeToken($_RBRACKET);
          $EndAction
        ./

    Token ::= '{'
        /.$BeginAction
                    makeToken($_LBRACE);
          $EndAction
        ./

    Token ::= '}'
        /.$BeginAction
                    makeToken($_RBRACE);
          $EndAction
        ./

    Token ::= '?'
        /.$BeginAction
                    makeToken($_QUESTION);
          $EndAction
        ./

    Token ::= '@'
        /.$BeginAction
                    makeToken($_AT);
          $EndAction
        ./

    Token ::= '+' '+'
        /.$BeginAction
                    makeToken($_PLUS_PLUS);
          $EndAction
        ./

    Token ::= '-' '-'
        /.$BeginAction
                    makeToken($_MINUS_MINUS);
          $EndAction
        ./

    Token ::= '=' '='
        /.$BeginAction
                    makeToken($_EQUAL_EQUAL);
          $EndAction
        ./

    Token ::= '<' '='
        /.$BeginAction
                    makeToken($_LESS_EQUAL);
          $EndAction
        ./

    Token ::= '!' '='
        /.$BeginAction
                    makeToken($_NOT_EQUAL);
          $EndAction
        ./

    Token ::= '<' '<'
        /.$BeginAction
                    makeToken($_LEFT_SHIFT);
          $EndAction
        ./

    Token ::= '+' '='
        /.$BeginAction
                    makeToken($_PLUS_EQUAL);
          $EndAction
        ./

    Token ::= '-' '='
        /.$BeginAction
                    makeToken($_MINUS_EQUAL);
          $EndAction
        ./

    Token ::= '*' '='
        /.$BeginAction
                    makeToken($_MULTIPLY_EQUAL);
          $EndAction
        ./

    Token ::= '/' '='
        /.$BeginAction
                    makeToken($_DIVIDE_EQUAL);
          $EndAction
        ./

    Token ::= '&' '='
        /.$BeginAction
                    makeToken($_AND_EQUAL);
          $EndAction
        ./

    Token ::= '|' '='
        /.$BeginAction
                    makeToken($_OR_EQUAL);
          $EndAction
        ./

    Token ::= '^' '='
        /.$BeginAction
                    makeToken($_XOR_EQUAL);
          $EndAction
        ./

    Token ::= '%' '='
        /.$BeginAction
                    makeToken($_REMAINDER_EQUAL);
          $EndAction
        ./

    Token ::= '<' '<' '='
        /.$BeginAction
                    makeToken($_LEFT_SHIFT_EQUAL);
          $EndAction
        ./

    Token ::= '|' '|'
        /.$BeginAction
                    makeToken($_OR_OR);
          $EndAction
        ./

    Token ::= '&' '&'
        /.$BeginAction
                    makeToken($_AND_AND);
          $EndAction
        ./

    Token ::= '.' '.' '.'
        /.$BeginAction
                    makeToken($_ELLIPSIS);
          $EndAction
        ./

    IntegerLiteral -> Integer
                    | '0' LetterXx HexDigits

    LongLiteral ::= IntegerLiteral LetterLl

    FloatingPointLiteral -> Decimal LetterFf
                          | Decimal Exponent LetterFf
                          | Integer Exponent LetterFf
                          | Integer LetterFf

    DoubleLiteral -> Decimal
                   | Decimal Exponent
                   | Integer Exponent
                   | Decimal LetterDd
                   | Decimal Exponent LetterDd
                   | Integer Exponent LetterDd
                   | Integer LetterDd

    MultiLineComment ::= '/' '*' Inside Stars '/'
        /.$BeginAction
                    if (getKind(getRhsFirstTokenIndex(3)) == Char_Star && getKind(getNext(getRhsFirstTokenIndex(3))) != Char_Star)
                         makeComment($_DocComment);
                    else makeComment($_MlComment);
          $EndAction
        ./

    Inside ::= Inside Stars NotSlashOrStar
             | Inside '/'
             | Inside NotSlashOrStar
             | %empty

    Stars -> '*'
           | Stars '*'

    SingleLineComment ::= SLC
        /.$BeginAction
                    makeComment($_SlComment);
          $EndAction
        ./

    SLC ::= '/' '/'
          | SLC NotEol

    SLBody -> %empty
            | SLBody NotDQ

    Integer -> Digit
             | Integer Digit

    HexDigits -> HexDigit
               | HexDigits HexDigit

    Decimal ::= '.' Integer
              | Integer '.'
              | Integer '.' Integer

    Exponent ::= LetterEe Integer
               | LetterEe '+' Integer
               | LetterEe '-' Integer

    WSChar -> Space
            | LF
            | CR
            | HT
            | FF

    Letter -> LowerCaseLetter
            | UpperCaseLetter
            | _
            | '$'
            | '\u0080..\ufffe'

    LowerCaseLetter -> a | b | c | d | e | f | g | h | i | j | k | l | m |
                       n | o | p | q | r | s | t | u | v | w | x | y | z

    UpperCaseLetter -> A | B | C | D | E | F | G | H | I | J | K | L | M |
                       N | O | P | Q | R | S | T | U | V | W | X | Y | Z

    Digit -> 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9

    OctalDigit -> 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7

    a..f -> a | b | c | d | e | f | A | B | C | D | E | F

    HexDigit -> Digit
              | a..f

    OctalDigits3 -> OctalDigit
                  | OctalDigit OctalDigit
                  | OctalDigit OctalDigit OctalDigit

    LetterFf -> 'F'
              | 'f'

    LetterDd -> 'D'
              | 'd'

    LetterLl ->  'L'
              | 'l'

    LetterEe -> 'E'
              | 'e'

    LetterXx -> 'X'
              | 'x'

    WS -> WSChar
        | WS WSChar

    Identifier -> Letter
                | Identifier Letter
                | Identifier Digit

    SpecialNotStar -> '+' | '-' | '/' | '(' | ')' | '"' | '!' | '@' | '`' | '~' |
                      '%' | '&' | '^' | ':' | ';' | "'" | '\' | '|' | '{' | '}' |
                      '[' | ']' | '?' | ',' | '.' | '<' | '>' | '=' | '#'

    SpecialNotSlash -> '+' | '-' | -- exclude the star as well
                       '(' | ')' | '"' | '!' | '@' | '`' | '~' |
                       '%' | '&' | '^' | ':' | ';' | "'" | '\' | '|' | '{' | '}' |
                       '[' | ']' | '?' | ',' | '.' | '<' | '>' | '=' | '#'

    SpecialNotDQ -> '+' | '-' | '/' | '(' | ')' | '*' | '!' | '@' | '`' | '~' |
                    '%' | '&' | '^' | ':' | ';' | "'" | '|' | '{' | '}' |
                    '[' | ']' | '?' | ',' | '.' | '<' | '>' | '=' | '#'

    SpecialNotSQ -> '+' | '-' | '*' | '(' | ')' | '"' | '!' | '@' | '`' | '~' |
                    '%' | '&' | '^' | ':' | ';' | '/' | '|' | '{' | '}' |
                    '[' | ']' | '?' | ',' | '.' | '<' | '>' | '=' | '#'

    NotSlashOrStar -> Letter
                    | Digit
                    | SpecialNotSlash
                    | WSChar

    Eol -> LF
         | CR

    NotEol -> Letter
            | Digit
            | Space
            | '*'
            | SpecialNotStar
            | HT
            | FF
            | CtlCharNotWS

    NotDQ -> Letter
           | Digit
           | SpecialNotDQ
           | Space
           | HT
           | FF
           | EscapeSequence
           | '\' u HexDigit HexDigit HexDigit HexDigit
           | '\' OctalDigit

    NotSQ -> Letter
           | Digit
           | SpecialNotSQ
           | Space
           | HT
           | FF
           | EscapeSequence
           | '\' u HexDigit HexDigit HexDigit HexDigit
           | '\' OctalDigits3

    EscapeSequence -> '\' b
                    | '\' t
                    | '\' n
                    | '\' f
                    | '\' r
                    | '\' '"'
                    | '\' "'"
                    | '\' '\'
%End
