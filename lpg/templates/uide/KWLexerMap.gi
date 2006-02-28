$Terminals
    DollarSign ::= '$'
    _
    
    a    b    c    d    e    f    g    h    i    j    k    l    m
    n    o    p    q    r    s    t    u    v    w    x    y    z

    A    B    C    D    E    F    G    H    I    J    K    L    M
    N    O    P    Q    R    S    T    U    V    W    X    Y    Z
$End

$Headers
    /.
        final static int tokenKind[] = new int[128];
        static
        {
            tokenKind['$'] = Char_DollarSign;
            tokenKind['_'] = Char__;

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
$End

