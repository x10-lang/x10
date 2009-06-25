
//
// Licensed Material 
// (C) Copyright IBM Corp, 2006
//

package x10.parser;

import lpg.runtime.*;
import java.util.*;
import java.util.*;

public class X10KWLexer extends X10KWLexerprs implements X10Parsersym
{
    private char[] inputChars;
    private final int keywordKind[] = new int[86 + 1];

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
                                   ? Char_EOF
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


    public X10KWLexer(char[] inputChars, int identifierKind)
    {
        this.inputChars = inputChars;
        keywordKind[0] = identifierKind;

        //
        // Rule 1:  KeyWord ::= a b s t r a c t
        //
            keywordKind[1] = (TK_abstract);
      
    
        //
        // Rule 2:  KeyWord ::= a s s e r t
        //
            keywordKind[2] = (TK_assert);
      
    
        //
        // Rule 3:  KeyWord ::= b o o l e a n
        //
            keywordKind[3] = (TK_boolean);
      
    
        //
        // Rule 4:  KeyWord ::= b r e a k
        //
            keywordKind[4] = (TK_break);
      
    
        //
        // Rule 5:  KeyWord ::= b y t e
        //
            keywordKind[5] = (TK_byte);
      
    
        //
        // Rule 6:  KeyWord ::= c a s e
        //
            keywordKind[6] = (TK_case);
      
    
        //
        // Rule 7:  KeyWord ::= c a t c h
        //
            keywordKind[7] = (TK_catch);
      
    
        //
        // Rule 8:  KeyWord ::= c h a r
        //
            keywordKind[8] = (TK_char);
      
    
        //
        // Rule 9:  KeyWord ::= c l a s s
        //
            keywordKind[9] = (TK_class);
      
    
        //
        // Rule 10:  KeyWord ::= c o n s t
        //
            keywordKind[10] = (TK_const);
      
    
        //
        // Rule 11:  KeyWord ::= c o n t i n u e
        //
            keywordKind[11] = (TK_continue);
      
    
        //
        // Rule 12:  KeyWord ::= d e f a u l t
        //
            keywordKind[12] = (TK_default);
      
    
        //
        // Rule 13:  KeyWord ::= d o
        //
            keywordKind[13] = (TK_do);
      
    
        //
        // Rule 14:  KeyWord ::= d o u b l e
        //
            keywordKind[14] = (TK_double);
      
    
        //
        // Rule 15:  KeyWord ::= e l s e
        //
            keywordKind[15] = (TK_else);
      
    
        //
        // Rule 16:  KeyWord ::= e n u m
        //
            keywordKind[16] = (TK_enum);
      
    
        //
        // Rule 17:  KeyWord ::= e x t e n d s
        //
            keywordKind[17] = (TK_extends);
      
    
        //
        // Rule 18:  KeyWord ::= f a l s e
        //
            keywordKind[18] = (TK_false);
      
    
        //
        // Rule 19:  KeyWord ::= f i n a l
        //
            keywordKind[19] = (TK_final);
      
    
        //
        // Rule 20:  KeyWord ::= f i n a l l y
        //
            keywordKind[20] = (TK_finally);
      
    
        //
        // Rule 21:  KeyWord ::= f l o a t
        //
            keywordKind[21] = (TK_float);
      
    
        //
        // Rule 22:  KeyWord ::= f o r
        //
            keywordKind[22] = (TK_for);
      
    
        //
        // Rule 23:  KeyWord ::= g o t o
        //
            keywordKind[23] = (TK_goto);
      
    
        //
        // Rule 24:  KeyWord ::= i f
        //
            keywordKind[24] = (TK_if);
      
    
        //
        // Rule 25:  KeyWord ::= i m p l e m e n t s
        //
            keywordKind[25] = (TK_implements);
      
    
        //
        // Rule 26:  KeyWord ::= i m p o r t
        //
            keywordKind[26] = (TK_import);
      
    
        //
        // Rule 27:  KeyWord ::= i n s t a n c e o f
        //
            keywordKind[27] = (TK_instanceof);
      
    
        //
        // Rule 28:  KeyWord ::= i n t
        //
            keywordKind[28] = (TK_int);
      
    
        //
        // Rule 29:  KeyWord ::= i n t e r f a c e
        //
            keywordKind[29] = (TK_interface);
      
    
        //
        // Rule 30:  KeyWord ::= l o n g
        //
            keywordKind[30] = (TK_long);
      
    
        //
        // Rule 31:  KeyWord ::= n a t i v e
        //
            keywordKind[31] = (TK_native);
      
    
        //
        // Rule 32:  KeyWord ::= n e w
        //
            keywordKind[32] = (TK_new);
      
    
        //
        // Rule 33:  KeyWord ::= n u l l
        //
            keywordKind[33] = (TK_null);
      
    
        //
        // Rule 34:  KeyWord ::= p a c k a g e
        //
            keywordKind[34] = (TK_package);
      
    
        //
        // Rule 35:  KeyWord ::= p r i v a t e
        //
            keywordKind[35] = (TK_private);
      
    
        //
        // Rule 36:  KeyWord ::= p r o t e c t e d
        //
            keywordKind[36] = (TK_protected);
      
    
        //
        // Rule 37:  KeyWord ::= p r o p e r t y
        //
            keywordKind[37] = (TK_property);
      
    
        //
        // Rule 38:  KeyWord ::= p u b l i c
        //
            keywordKind[38] = (TK_public);
      
    
        //
        // Rule 39:  KeyWord ::= r e t u r n
        //
            keywordKind[39] = (TK_return);
      
    
        //
        // Rule 40:  KeyWord ::= s h o r t
        //
            keywordKind[40] = (TK_short);
      
    
        //
        // Rule 41:  KeyWord ::= s t a t i c
        //
            keywordKind[41] = (TK_static);
      
    
        //
        // Rule 42:  KeyWord ::= s t r i c t f p
        //
            keywordKind[42] = (TK_strictfp);
      
    
        //
        // Rule 43:  KeyWord ::= s u p e r
        //
            keywordKind[43] = (TK_super);
      
    
        //
        // Rule 44:  KeyWord ::= s w i t c h
        //
            keywordKind[44] = (TK_switch);
      
    
        //
        // Rule 45:  KeyWord ::= s y n c h r o n i z e d
        //
            keywordKind[45] = (TK_synchronized);
      
    
        //
        // Rule 46:  KeyWord ::= t h i s
        //
            keywordKind[46] = (TK_this);
      
    
        //
        // Rule 47:  KeyWord ::= t h r o w
        //
            keywordKind[47] = (TK_throw);
      
    
        //
        // Rule 48:  KeyWord ::= t h r o w s
        //
            keywordKind[48] = (TK_throws);
      
    
        //
        // Rule 49:  KeyWord ::= t r a n s i e n t
        //
            keywordKind[49] = (TK_transient);
      
    
        //
        // Rule 50:  KeyWord ::= t r u e
        //
            keywordKind[50] = (TK_true);
      
    
        //
        // Rule 51:  KeyWord ::= t r y
        //
            keywordKind[51] = (TK_try);
      
    
        //
        // Rule 52:  KeyWord ::= v o i d
        //
            keywordKind[52] = (TK_void);
      
    
        //
        // Rule 53:  KeyWord ::= v o l a t i l e
        //
            keywordKind[53] = (TK_volatile);
      
    
        //
        // Rule 54:  KeyWord ::= w h i l e
        //
            keywordKind[54] = (TK_while);
      
    
        //
        // Rule 55:  KeyWord ::= a s y n c
        //
                keywordKind[55] = (TK_async);
      
    
        //
        // Rule 56:  KeyWord ::= a n y
        //
                keywordKind[56] = (TK_any);
      
    
        //
        // Rule 57:  KeyWord ::= a c t i v i t y l o c a l
        //
                keywordKind[57] = (TK_activitylocal);
      
    
        //
        // Rule 58:  KeyWord ::= a t e a c h
        //
                keywordKind[58] = (TK_ateach);
      
    
        //
        // Rule 59:  KeyWord ::= a t o m i c
        //
                keywordKind[59] = (TK_atomic);
      
    
        //
        // Rule 60:  KeyWord ::= a w a i t
        //
                keywordKind[60] = (TK_await);
      
    
        //
        // Rule 61:  KeyWord ::= b o x e d
        //
                keywordKind[61] = (TK_boxed);
      
    
        //
        // Rule 62:  KeyWord ::= c l o c k e d
        //
                keywordKind[62] = (TK_clocked);
      
    
        //
        // Rule 63:  KeyWord ::= c o m p i l e r t e s t
        //
                keywordKind[63] = (TK_compilertest);
      
    
        //
        // Rule 64:  KeyWord ::= c u r r e n t
        //
                keywordKind[64] = (TK_current);
      
    
        //
        // Rule 65:  KeyWord ::= e x t e r n
        //
                keywordKind[65] = (TK_extern);
      
    
        //
        // Rule 66:  KeyWord ::= f i n i s h
        //
                keywordKind[66] = (TK_finish);
      
    
        //
        // Rule 67:  KeyWord ::= f o r e a c h
        //
                keywordKind[67] = (TK_foreach);
      
    
        //
        // Rule 68:  KeyWord ::= f u n
        //
                keywordKind[68] = (TK_fun);
      
    
        //
        // Rule 69:  KeyWord ::= f u t u r e
        //
                keywordKind[69] = (TK_future);
      
    
        //
        // Rule 70:  KeyWord ::= h e r e
        //
                keywordKind[70] = (TK_here);
      
    
        //
        // Rule 71:  KeyWord ::= l o c a l
        //
                keywordKind[71] = (TK_local);
      
    
        //
        // Rule 72:  KeyWord ::= m e t h o d
        //
                keywordKind[72] = (TK_method);
      
    
        //
        // Rule 73:  KeyWord ::= m u t a b l e
        //
                keywordKind[73] = (TK_mutable);
      
    
        //
        // Rule 74:  KeyWord ::= n e x t
        //
                keywordKind[74] = (TK_next);
      
    
        //
        // Rule 75:  KeyWord ::= n o n b l o c k i n g
        //
                keywordKind[75] = (TK_nonblocking);
      
    
        //
        // Rule 76:  KeyWord ::= n o w
        //
                keywordKind[76] = (TK_now);
      
    
        //
        // Rule 77:  KeyWord ::= n u l l a b l e
        //
                keywordKind[77] = (TK_nullable);
      
    
        //
        // Rule 78:  KeyWord ::= o r
        //
                keywordKind[78] = (TK_or);
      
    
        //
        // Rule 79:  KeyWord ::= p l a c e l o c a l
        //
                keywordKind[79] = (TK_placelocal);
      
    
        //
        // Rule 80:  KeyWord ::= r e f e r e n c e
        //
                keywordKind[80] = (TK_reference);
      
    
        //
        // Rule 81:  KeyWord ::= s a f e
        //
                keywordKind[81] = (TK_safe);
      
    
        //
        // Rule 82:  KeyWord ::= s e l f
        //
                keywordKind[82] = (TK_self);
      
    
        //
        // Rule 83:  KeyWord ::= s e q u e n t i a l
        //
                keywordKind[83] = (TK_sequential);
      
    
        //
        // Rule 84:  KeyWord ::= u n s a f e
        //
                keywordKind[84] = (TK_unsafe);
      
    
        //
        // Rule 85:  KeyWord ::= v a l u e
        //
                keywordKind[85] = (TK_value);
      
    
        //
        // Rule 86:  KeyWord ::= w h e n
        //
                keywordKind[86] = (TK_when);
      
    

        for (int i = 0; i < keywordKind.length; i++)
        {
            if (keywordKind[i] == 0)
                keywordKind[i] = identifierKind;
        }
    }
}

