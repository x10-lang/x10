--
-- The X10 KeyWord Lexer
--
%Options verbose
%Options fp=X10KWLexer
%options package=x10.parser
%options template=KeywordTemplateF.gi

%Notice
    /./*
     *  This file is part of the X10 project (http://x10-lang.org).
     *
     *  This file is licensed to You under the Eclipse Public License (EPL);
     *  You may not use this file except in compliance with the License.
     *  You may obtain a copy of the License at
     *      http://www.opensource.org/licenses/eclipse-1.0.php
     *
     *  (C) Copyright IBM Corporation 2006-2014.
     */
    /****************************************************************************
     * WARNING!  THIS JAVA FILE IS AUTO-GENERATED FROM $input_file *
     ****************************************************************************/
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
            tokenKind['A'] = Char_A;
            tokenKind['B'] = Char_B;
            tokenKind['C'] = Char_C;
            tokenKind['D'] = Char_D;
            tokenKind['E'] = Char_E;
            tokenKind['F'] = Char_F;
            tokenKind['G'] = Char_G;
            tokenKind['H'] = Char_H;
            tokenKind['I'] = Char_I;
            tokenKind['J'] = Char_J;
            tokenKind['K'] = Char_K;
            tokenKind['L'] = Char_L;
            tokenKind['M'] = Char_M;
            tokenKind['N'] = Char_N;
            tokenKind['O'] = Char_O;
            tokenKind['P'] = Char_P;
            tokenKind['Q'] = Char_Q;
            tokenKind['R'] = Char_R;
            tokenKind['S'] = Char_S;
            tokenKind['T'] = Char_T;
            tokenKind['U'] = Char_U;
            tokenKind['V'] = Char_V;
            tokenKind['W'] = Char_W;
            tokenKind['X'] = Char_X;
            tokenKind['Y'] = Char_Y;
            tokenKind['Z'] = Char_Z;
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
    A    B    C    D    E    F    G    H    I    J    K    L    M
    N    O    P    Q    R    S    T    U    V    W    X    Y    Z
%End
    
%Start
    KeyWord
%End

%Export
    abstract
    as
    assert
    async
    at
    athome
    ateach
    atomic
    break
    case
    catch
    class
    clocked
    continue
    def
    default
    do
    else
    extends
    false
    final
    finally
    finish
    for
    goto
    haszero
    here
    if
    implements
    import
    in
    instanceof
    interface
    isref
    native
    new
    null
    offer
    offers
    operator
    package
    private
    property
    protected
    public
    return
    self
    static
    struct
    super
    switch
    this
    throw
    throws
    transient
    true
    try
    type
    val
    var
    void
    when
    while
%End

%Rules

    KeyWord ::= a b s t r a c t
        /.$BeginAction
                $setResult($_abstract);
          $EndAction
        ./
              | a s
        /.$BeginAction
                $setResult($_as);
          $EndAction
        ./
              | a s s e r t
        /.$BeginAction
                $setResult($_assert);
          $EndAction
        ./
              | a s y n c
        /.$BeginAction
                $setResult($_async);
          $EndAction
        ./
              | a t
        /.$BeginAction
                $setResult($_at);
          $EndAction
        ./
              | a t h o m e
        /.$BeginAction
                $setResult($_athome);
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
              | c l o c k e d
        /.$BeginAction
                $setResult($_clocked);
          $EndAction
        ./
              | c o n t i n u e
        /.$BeginAction
                $setResult($_continue);
          $EndAction
        ./
              | d e f
        /.$BeginAction
                $setResult($_def);
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
              | f i n i s h
        /.$BeginAction
                $setResult($_finish);
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
              | h e r e
        /.$BeginAction
                $setResult($_here);
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
              | i n
        /.$BeginAction
                $setResult($_in);
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
              | i s r e f
        /.$BeginAction
                $setResult($_isref);
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
              | o f f e r
        /.$BeginAction
                $setResult($_offer);
          $EndAction
        ./
              | o f f e r s
        /.$BeginAction
                $setResult($_offers);
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
              | p r o p e r t y
        /.$BeginAction
                $setResult($_property);
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
              | s e l f
        /.$BeginAction
                $setResult($_self);
          $EndAction
        ./
              | s t a t i c
        /.$BeginAction
                $setResult($_static);
          $EndAction
        ./
              | s t r u c t
        /.$BeginAction
                $setResult($_struct);
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
              | t y p e
        /.$BeginAction
                $setResult($_type);
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
              | v o i d
        /.$BeginAction
                $setResult($_void);
          $EndAction
        ./
              | w h e n
        /.$BeginAction
                $setResult($_when);
          $EndAction
        ./
              | w h i l e
        /.$BeginAction
                $setResult($_while);
          $EndAction
        ./
              | h a s z e r o
        /.$BeginAction
                $setResult($_haszero);
          $EndAction
        ./
%End
