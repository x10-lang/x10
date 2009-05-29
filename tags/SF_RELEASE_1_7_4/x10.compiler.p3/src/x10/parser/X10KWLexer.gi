--
-- The X10 KeyWord Lexer
--
%Options list
%Options fp=X10KWLexer
%options package=x10.parser
%options template=KeywordTemplateF.gi

%Notice
/.
//
// Licensed Material 
// (C) Copyright IBM Corp, 2006
//
./
%End

%Globals
    /.import java.util.*;./
%End

%Headers
    /.
        final static int tokenKind[] = new int[128];
        static
        {
            tokenKind['a'] = Char_a;
            tokenKind['b'] = Char_b;
            tokenKind['c'] = Char_c;
            tokenKind['d'] = Char_d;
            tokenKind['e'] = Char_e;
            tokenKind['f'] = Char_f;
            tokenKind['g'] = Char_g;
            tokenKind['h'] = Char_h;
            tokenKind['i'] = Char_i;
            tokenKind['j'] = Char_j;
            tokenKind['k'] = Char_k;
            tokenKind['l'] = Char_l;
            tokenKind['m'] = Char_m;
            tokenKind['n'] = Char_n;
            tokenKind['o'] = Char_o;
            tokenKind['p'] = Char_p;
            tokenKind['q'] = Char_q;
            tokenKind['r'] = Char_r;
            tokenKind['s'] = Char_s;
            tokenKind['t'] = Char_t;
            tokenKind['u'] = Char_u;
            tokenKind['v'] = Char_v;
            tokenKind['w'] = Char_w;
            tokenKind['x'] = Char_x;
            tokenKind['y'] = Char_y;
            tokenKind['z'] = Char_z;
        };
    
        final int getKind(char c)
        {
            return (c < 128 ? tokenKind[c] : 0);
        }
    ./
%End

%Terminals
    a    b    c    d    e    f    g    h    i    j    k    l    m
    n    o    p    q    r    s    t    u    v    w    x    y    z
%End
    
%Start
    KeyWord
%End

%Export
    abstract
    as
    at
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
    goto
    has
    here
    if
    implements
    import
    incomplete
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
    operator
    package
    private
    property
    protected
    public
    return
    safe
    self
    sequential
    shared
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
%End

%Rules

    KeyWord ::= a s y n c
        /.$BeginAction
                    $setResult($_async);
          $EndAction
        ./
        
              | t y p e
        /.$BeginAction
                $setResult($_type);
          $EndAction
        ./
        
              | a t
        /.$BeginAction
                $setResult($_at);
          $EndAction
        ./

              | p r o p e r t y
        /.$BeginAction
                $setResult($_property);
          $EndAction
        ./

              | d e f
        /.$BeginAction
                $setResult($_def);
          $EndAction
        ./

              | v a l
        /.$BeginAction
                $setResult($_val);
          $EndAction
        ./

              | v a r
        /.$BeginAction
                $setResult($_var);
          $EndAction
        ./
              | a s
        /.$BeginAction
                $setResult($_as);
          $EndAction
        ./
              | h a s
        /.$BeginAction
                $setResult($_has);
          $EndAction
        ./
              | i n
        /.$BeginAction
                $setResult($_in);
          $EndAction
        ./
              | i n c o m p l e t e
        /.$BeginAction
                $setResult($_incomplete);
          $EndAction
        ./
              | s h a r e d
        /.$BeginAction
                $setResult($_shared);
          $EndAction
        ./
              | a t e a c h
        /.$BeginAction
                    $setResult($_ateach);
          $EndAction
        ./
              | a t o m i c
        /.$BeginAction
                    $setResult($_atomic);
          $EndAction
        ./
              | a w a i t
        /.$BeginAction
                    $setResult($_await);
          $EndAction
        ./
        | c l o c k e d
        /.$BeginAction
                    $setResult($_clocked);
          $EndAction
        ./
              | e x t e r n
        /.$BeginAction
                    $setResult($_extern);
          $EndAction
        ./
              | f i n i s h
        /.$BeginAction
                    $setResult($_finish);
          $EndAction
        ./
              | f o r e a c h
        /.$BeginAction
                    $setResult($_foreach);
          $EndAction
        ./
              | f u t u r e
        /.$BeginAction
                    $setResult($_future);
          $EndAction
        ./
              | h e r e
        /.$BeginAction
                    $setResult($_here);
          $EndAction
        ./
              | l o c a l
        /.$BeginAction
                    $setResult($_local);
          $EndAction
        ./
              | n e x t
        /.$BeginAction
                    $setResult($_next);
          $EndAction
        ./
              | n o n b l o c k i n g
        /.$BeginAction
                    $setResult($_nonblocking);
          $EndAction
        ./
              | n o w
        /.$BeginAction
                    $setResult($_now);
          $EndAction
        ./
              | o r
        /.$BeginAction
                    $setResult($_or);
          $EndAction
        ./
               | s a f e 
        /.$BeginAction
                    $setResult($_safe);
          $EndAction
        ./
              | s e l f
        /.$BeginAction
                    $setResult($_self);
          $EndAction
        ./
              | s e q u e n t i a l
        /.$BeginAction
                    $setResult($_sequential);
          $EndAction
        ./
              | u n s a f e
        /.$BeginAction
                    $setResult($_unsafe);
          $EndAction
        ./
              | v a l u e
        /.$BeginAction
                    $setResult($_value);
          $EndAction
        ./
              | w h e n
        /.$BeginAction
                    $setResult($_when);
          $EndAction
        ./

              | a b s t r a c t
        /.$BeginAction
                $setResult($_abstract);
          $EndAction
        ./

              | a s s e r t
        /.$BeginAction
                $setResult($_assert);
          $EndAction
        ./

              | b r e a k
        /.$BeginAction
                $setResult($_break);
          $EndAction
        ./

              | c a s e
        /.$BeginAction
                $setResult($_case);
          $EndAction
        ./

              | c a t c h
        /.$BeginAction
                $setResult($_catch);
          $EndAction
        ./

              | c l a s s
        /.$BeginAction
                $setResult($_class);
          $EndAction
        ./

              | c o n s t
        /.$BeginAction
                $setResult($_const);
          $EndAction
        ./

              | c o n t i n u e
        /.$BeginAction
                $setResult($_continue);
          $EndAction
        ./

              | d e f a u l t
        /.$BeginAction
                $setResult($_default);
          $EndAction
        ./

              | d o
        /.$BeginAction
                $setResult($_do);
          $EndAction
        ./

              | e l s e
        /.$BeginAction
                $setResult($_else);
          $EndAction
        ./

              | e x t e n d s
        /.$BeginAction
                $setResult($_extends);
          $EndAction
        ./

              | f a l s e
        /.$BeginAction
                $setResult($_false);
          $EndAction
        ./

              | f i n a l
        /.$BeginAction
                $setResult($_final);
          $EndAction
        ./

              | f i n a l l y
        /.$BeginAction
                $setResult($_finally);
          $EndAction
        ./

              | f o r
        /.$BeginAction
                $setResult($_for);
          $EndAction
        ./

              | g o t o
        /.$BeginAction
                $setResult($_goto);
          $EndAction
        ./

              | i f
        /.$BeginAction
                $setResult($_if);
          $EndAction
        ./

              | i m p l e m e n t s
        /.$BeginAction
                $setResult($_implements);
          $EndAction
        ./

              | i m p o r t
        /.$BeginAction
                $setResult($_import);
          $EndAction
        ./

              | i n s t a n c e o f
        /.$BeginAction
                $setResult($_instanceof);
          $EndAction
        ./

              | i n t e r f a c e
        /.$BeginAction
                $setResult($_interface);
          $EndAction
        ./

              | n a t i v e
        /.$BeginAction
                $setResult($_native);
          $EndAction
        ./

              | n e w
        /.$BeginAction
                $setResult($_new);
          $EndAction
        ./

              | n u l l
        /.$BeginAction
                $setResult($_null);
          $EndAction
        ./

              | o p e r a t o r
        /.$BeginAction
                $setResult($_operator);
          $EndAction
        ./

              | p a c k a g e
        /.$BeginAction
                $setResult($_package);
          $EndAction
        ./

              | p r i v a t e
        /.$BeginAction
                $setResult($_private);
          $EndAction
        ./

              | p r o t e c t e d
        /.$BeginAction
                $setResult($_protected);
          $EndAction
        ./

              | p u b l i c
        /.$BeginAction
                $setResult($_public);
          $EndAction
        ./

              | r e t u r n
        /.$BeginAction
                $setResult($_return);
          $EndAction
        ./

              | s t a t i c
        /.$BeginAction
                $setResult($_static);
          $EndAction
        ./

              | s t r i c t f p
        /.$BeginAction
                $setResult($_strictfp);
          $EndAction
        ./

              | s u p e r
        /.$BeginAction
                $setResult($_super);
          $EndAction
        ./

              | s w i t c h
        /.$BeginAction
                $setResult($_switch);
          $EndAction
        ./

              | s y n c h r o n i z e d
        /.$BeginAction
                $setResult($_synchronized);
          $EndAction
        ./

              | t h i s
        /.$BeginAction
                $setResult($_this);
          $EndAction
        ./

              | t h r o w
        /.$BeginAction
                $setResult($_throw);
          $EndAction
        ./

              | t h r o w s
        /.$BeginAction
                $setResult($_throws);
          $EndAction
        ./

              | t r a n s i e n t
        /.$BeginAction
                $setResult($_transient);
          $EndAction
        ./

              | t r u e
        /.$BeginAction
                $setResult($_true);
          $EndAction
        ./

              | t r y
        /.$BeginAction
                $setResult($_try);
          $EndAction
        ./

              | v o l a t i l e
        /.$BeginAction
                $setResult($_volatile);
          $EndAction
        ./

              | w h i l e
        /.$BeginAction
                $setResult($_while);
          $EndAction
        ./
%End
