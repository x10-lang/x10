
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
    assert
    boolean
    break
    byte
    case
    catch
    char
    class
    const
    continue
    default
    do
    double
    enum
    else
    extends
    false
    final
    finally
    float
    for
    goto
    if
    implements
    import
    instanceof
    int
    interface
    long
    native
    new
    null
    package
    private
    protected
    property
    public
    return
    short
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
    void
    volatile
    while
    activitylocal
    any
    async
    ateach
    atomic
    await
    boxed
    clocked
    compilertest
    current
    extern
    finish
    foreach
    fun
    future
    here
    local
    method
    mutable
    next
    nonblocking
    now
    nullable
    or
    placelocal
    reference
    safe
    self
    sequential
    unsafe
    value
    when


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

1      KeyWord ::= a b s t r a c t
2                | a s s e r t
3                | b o o l e a n
4                | b r e a k
5                | b y t e
6                | c a s e
7                | c a t c h
8                | c h a r
9                | c l a s s
10               | c o n s t
11               | c o n t i n u e
12               | d e f a u l t
13               | d o
14               | d o u b l e
15               | e l s e
16               | e n u m
17               | e x t e n d s
18               | f a l s e
19               | f i n a l
20               | f i n a l l y
21               | f l o a t
22               | f o r
23               | g o t o
24               | i f
25               | i m p l e m e n t s
26               | i m p o r t
27               | i n s t a n c e o f
28               | i n t
29               | i n t e r f a c e
30               | l o n g
31               | n a t i v e
32               | n e w
33               | n u l l
34               | p a c k a g e
35               | p r i v a t e
36               | p r o t e c t e d
37               | p r o p e r t y
38               | p u b l i c
39               | r e t u r n
40               | s h o r t
41               | s t a t i c
42               | s t r i c t f p
43               | s u p e r
44               | s w i t c h
45               | s y n c h r o n i z e d
46               | t h i s
47               | t h r o w
48               | t h r o w s
49               | t r a n s i e n t
50               | t r u e
51               | t r y
52               | v o i d
53               | v o l a t i l e
54               | w h i l e
55     KeyWord ::= a s y n c
56               | a n y
57               | a c t i v i t y l o c a l
58               | a t e a c h
59               | a t o m i c
60               | a w a i t
61               | b o x e d
62               | c l o c k e d
63               | c o m p i l e r t e s t
64               | c u r r e n t
65               | e x t e r n
66               | f i n i s h
67               | f o r e a c h
68               | f u n
69               | f u t u r e
70               | h e r e
71               | l o c a l
72               | m e t h o d
73               | m u t a b l e
74               | n e x t
75               | n o n b l o c k i n g
76               | n o w
77               | n u l l a b l e
78               | o r
79               | p l a c e l o c a l
80               | r e f e r e n c e
81               | s a f e
82               | s e l f
83               | s e q u e n t i a l
84               | u n s a f e
85               | v a l u e
86               | w h e n
/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/GJavaKWLexer.gi:110:50:110:50:2008:2008: Informative: The terminal j is useless.



/Users/nystrom/work/x10/cvs/p3/x10.compiler/src/x10/parser/X10KWLexer.gi is LALR(1).


Number of Terminals: 27
Number of Nonterminals: 1
Number of Productions: 87
Number of Items: 597
Number of States: 318
Number of Shift actions: 316
Number of Goto actions: 1
Number of Shift/Reduce actions: 80
Number of Goto/Reduce actions: 0
Number of Reduce actions: 7
Number of Shift-Reduce conflicts: 0
Number of Reduce-Reduce conflicts: 0

Number of entries in base Action Table: 319
Additional space required for compaction of Action Table: 0.3%

Number of unique terminal states: 318
Number of Shift actions saved by merging: 0
Number of Conflict points saved by merging: 0
Number of Reduce actions saved by merging: 0
Number of Reduce saved by default: 6

Number of entries in Terminal Action Table: 715
Additional space required for compaction of Terminal Table: 1.5%

Actions in Compressed Tables:
     Number of Shifts: 316
     Number of Shift/Reduces: 80
     Number of Gotos: 1
     Number of Goto/Reduces: 0
     Number of Reduces: 1
     Number of Defaults: 6

Parsing Tables storage:
    Storage required for BASE_CHECK: 87 Bytes
    Storage required for BASE_ACTION: 816 Bytes
    Storage required for TERM_CHECK: 750 Bytes
    Storage required for TERM_ACTION: 1454 Bytes
