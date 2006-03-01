$Terminals
    DollarSign ::= '$'
    _
    
    a    b    c    d    e    f    g    h    i    j    k    l    m
    n    o    p    q    r    s    t    u    v    w    x    y    z
$End

$Headers
    /.
        //
        // Each upper case letter is mapped into is corresponding
        // lower case counterpart. For example, if an 'A' appears
        // in the input, it is mapped into Char_a just like 'a'.
        //
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

            tokenKind['A'] = Char_a;
            tokenKind['B'] = Char_b;
            tokenKind['C'] = Char_c;
            tokenKind['D'] = Char_d;
            tokenKind['E'] = Char_e;
            tokenKind['F'] = Char_f;
            tokenKind['G'] = Char_g;
            tokenKind['H'] = Char_h;
            tokenKind['I'] = Char_i;
            tokenKind['J'] = Char_j;
            tokenKind['K'] = Char_k;
            tokenKind['L'] = Char_l;
            tokenKind['M'] = Char_m;
            tokenKind['N'] = Char_n;
            tokenKind['O'] = Char_o;
            tokenKind['P'] = Char_p;
            tokenKind['Q'] = Char_q;
            tokenKind['R'] = Char_r;
            tokenKind['S'] = Char_s;
            tokenKind['T'] = Char_t;
            tokenKind['U'] = Char_u;
            tokenKind['V'] = Char_v;
            tokenKind['W'] = Char_w;
            tokenKind['X'] = Char_x;
            tokenKind['Y'] = Char_y;
            tokenKind['Z'] = Char_z;
        };
    
        final int getKind(char c)
        {
            return (c < 128 ? tokenKind[c] : 0);
        }
    ./
$End

