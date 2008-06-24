
Options in effect for /Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/X10KWLexer.gi:

    ACTION-BLOCK=("/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/X10KWLexer.java","/.","./")

    AST-DIRECTORY=""  AST-TYPE="Ast"  NOATTRIBUTES  NOAUTOMATIC-AST  NOBACKTRACK
    BYTE  CONFLICTS  
    DAT-DIRECTORY="/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/"
    DAT-FILE="/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/X10KWLexerdcl.data"
    DAT-DIRECTORY="/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/"
    DCL-FILE="/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/X10KWLexerdcl.java"
    DAT-DIRECTORY="/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/"
    NODEBUG  
    DEF-FILE="/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/X10KWLexerdef.java"
    NODEBUG  NOEDIT  NOERROR-MAPS  ESCAPE='$'  
    EXPORT-TERMINALS=("/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/X10Parsersym.java","TK_","")
    NODEBUG  NOEDIT  NOERROR-MAPS  ESCAPE='$'  EXTENDS-PARSETABLE  
    FACTORY="new "  FILE-PREFIX="X10KWLexer"  NOFIRST  NOFOLLOW  NOGLR  
    NOGOTO-DEFAULT  
    GRM-FILE="/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/X10KWLexer.gi"
    NOGOTO-DEFAULT  
    IMP-FILE="/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/X10KWLexerimp.java"
    NOGOTO-DEFAULT  
    INCLUDE-DIRECTORY="/Users/nystrom/work/x10/cvs/p3/org.eclipse.imp.lpg.metatooling/templates"
    NOGOTO-DEFAULT  LALR=1  LEGACY  LIST  MARGIN=4  MAX-CASES=1024  
    NAMES=OPTIMIZED  NONT-CHECK  OR_MARKER='|'  
    OUT-DIRECTORY="/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/"
    PACKAGE="x10.parser"  NOPARENT-SAVE  
    PARSETABLE-INTERFACES="lpg.runtime.ParseTable"  PREFIX="Char_"  PRIORITY  
    PROGRAMMING_LANGUAGE=JAVA  
    PRS-FILE="/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/X10KWLexerprs.java"
    PROGRAMMING_LANGUAGE=JAVA  NOQUIET  READ-REDUCE  REMAP-TERMINALS  NOSCOPES
    NOSERIALIZE  NOSHIFT-DEFAULT  NOSINGLE-PRODUCTIONS  NOSOFT-KEYWORDS  
    NOSTATES  SUFFIX=""  
    SYM-FILE="/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/X10KWLexersym.java"
    NOSTATES  SUFFIX=""  TAB-FILE="X10KWLexer.t"  TABLE  
    TEMPLATE="KeywordTemplate.gi"  TRACE=CONFLICTS  NOVARIABLES  NOVERBOSE  
    NOVISITOR  VISITOR-TYPE="Visitor"  WARNINGS  NOXREF  


Predefined Macros:

    $rule_number
    $rule_text
    $rule_size
    $input_file
    $current_line
    $next_line
    $#identifier
    $symbol_declarations
    $num_rules
    $num_terminals
    $num_nonterminals
    $num_non_terminals
    $num_symbols
    $template
    $file_prefix
    $package
    $ast_package
    $ast_type
    $exp_type
    $prs_type
    $sym_type
    $action_type
    $visitor_type
    $entry_name
    $entry_marker


Exported symbols:

    abstract
    as
    assert
    async
    ateach
    atomic
    await
    break
    case
    catch
    class
    clocked
    const
    continue
    def
    default
    do
    else
    extends
    extern
    false
    final
    finally
    finish
    for
    foreach
    future
    in
    to
    goto
    has
    here
    if
    implements
    import
    instanceof
    interface
    local
    native
    new
    next
    nonblocking
    now
    null
    or
    package
    private
    property
    protected
    public
    return
    safe
    self
    sequential
    static
    strictfp
    super
    switch
    synchronized
    this
    throw
    throws
    transient
    true
    try
    type
    unsafe
    val
    value
    var
    volatile
    when
    while


Defined Symbols:



    $eof_char
    /.Char_EOF./


    $setResult
    /.keywordKind[$rule_number] = ./


    $Header
    /.
                //
                // Rule $rule_number:  $rule_text
                //./


    $BeginAction
    /.$Header./


    $EndAction
    /../


    $BeginJava
    /.$BeginAction./


    $EndJava
    /.$EndAction./


Start:

    KeyWord

Eof:

    EOF

Terminals:

    a
    b
    c
    d
    e
    f
    g
    h
    i
    j
    k
    l
    m
    n
    o
    p
    q
    r
    s
    t
    u
    v
    w
    x
    y
    z
    EOF


Soft Keywords:



Rules:

0      '$accept' ::= KeyWord

1      KeyWord ::= a s y n c
2                | t y p e
3                | p r o p e r t y
4                | d e f
5                | v a l
6                | v a r
7                | a s
8                | t o
9                | h a s
10               | i n
11               | a t e a c h
12               | a t o m i c
13               | a w a i t
14               | c l o c k e d
15               | e x t e r n
16               | f i n i s h
17               | f o r e a c h
18               | f u t u r e
19               | h e r e
20               | l o c a l
21               | n e x t
22               | n o n b l o c k i n g
23               | n o w
24               | o r
25               | s a f e
26               | s e l f
27               | s e q u e n t i a l
28               | u n s a f e
29               | v a l u e
30               | w h e n
31               | a b s t r a c t
32               | a s s e r t
33               | b r e a k
34               | c a s e
35               | c a t c h
36               | c l a s s
37               | c o n s t
38               | c o n t i n u e
39               | d e f a u l t
40               | d o
41               | e l s e
42               | e x t e n d s
43               | f a l s e
44               | f i n a l
45               | f i n a l l y
46               | f o r
47               | g o t o
48               | i f
49               | i m p l e m e n t s
50               | i m p o r t
51               | i n s t a n c e o f
52               | i n t e r f a c e
53               | n a t i v e
54               | n e w
55               | n u l l
56               | p a c k a g e
57               | p r i v a t e
58               | p r o t e c t e d
59               | p u b l i c
60               | r e t u r n
61               | s t a t i c
62               | s t r i c t f p
63               | s u p e r
64               | s w i t c h
65               | s y n c h r o n i z e d
66               | t h i s
67               | t h r o w
68               | t h r o w s
69               | t r a n s i e n t
70               | t r u e
71               | t r y
72               | v o l a t i l e
73               | w h i l e
/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/X10KWLexer.gi:63:50:63:50:1506:1506: Informative: The terminal j is useless.



/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/X10KWLexer.gi is LALR(1).


Number of Terminals: 27
Number of Nonterminals: 1
Number of Productions: 74
Number of Items: 477
Number of States: 242
Number of Shift actions: 240
Number of Goto actions: 1
Number of Shift/Reduce actions: 66
Number of Goto/Reduce actions: 0
Number of Reduce actions: 8
Number of Shift-Reduce conflicts: 0
Number of Reduce-Reduce conflicts: 0

Number of entries in base Action Table: 243
Additional space required for compaction of Action Table: 0.4%

Number of unique terminal states: 242
Number of Shift actions saved by merging: 0
Number of Conflict points saved by merging: 0
Number of Reduce actions saved by merging: 0
Number of Reduce saved by default: 7

Number of entries in Terminal Action Table: 549
Additional space required for compaction of Terminal Table: 2.5%

Actions in Compressed Tables:
     Number of Shifts: 240
     Number of Shift/Reduces: 66
     Number of Gotos: 1
     Number of Goto/Reduces: 0
     Number of Reduces: 1
     Number of Defaults: 7

Parsing Tables storage:
    Storage required for BASE_CHECK: 74 Bytes
    Storage required for BASE_ACTION: 638 Bytes
    Storage required for TERM_CHECK: 579 Bytes
    Storage required for TERM_ACTION: 1128 Bytes
