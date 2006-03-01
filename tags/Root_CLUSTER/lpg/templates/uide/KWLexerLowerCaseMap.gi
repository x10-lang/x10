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
        };
    
        final int getKind(char c)
        {
            return (c < 128 ? tokenKind[c] : 0);
        }
    ./
$End

