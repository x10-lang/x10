
//
// Licensed Material 
// (C) Copyright IBM Corp, 2006
//

package x10.parser;

import lpg.runtime.*;
import java.util.*;

public class X10KWLexer extends X10KWLexerprs implements X10Parsersym
{
    private char[] inputChars;
    private final int keywordKind[] = new int[73 + 1];

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
        // Rule 1:  KeyWord ::= a s y n c
        //
                keywordKind[1] = (TK_async);
      
    
        //
        // Rule 2:  KeyWord ::= t y p e
        //
            keywordKind[2] = (TK_type);
      
    
        //
        // Rule 3:  KeyWord ::= p r o p e r t y
        //
            keywordKind[3] = (TK_property);
      
    
        //
        // Rule 4:  KeyWord ::= d e f
        //
            keywordKind[4] = (TK_def);
      
    
        //
        // Rule 5:  KeyWord ::= v a l
        //
            keywordKind[5] = (TK_val);
      
    
        //
        // Rule 6:  KeyWord ::= v a r
        //
            keywordKind[6] = (TK_var);
      
    
        //
        // Rule 7:  KeyWord ::= a s
        //
            keywordKind[7] = (TK_as);
      
    
        //
        // Rule 8:  KeyWord ::= t o
        //
            keywordKind[8] = (TK_to);
      
    
        //
        // Rule 9:  KeyWord ::= h a s
        //
            keywordKind[9] = (TK_has);
      
    
        //
        // Rule 10:  KeyWord ::= i n
        //
            keywordKind[10] = (TK_in);
      
    
        //
        // Rule 11:  KeyWord ::= a t e a c h
        //
                keywordKind[11] = (TK_ateach);
      
    
        //
        // Rule 12:  KeyWord ::= a t o m i c
        //
                keywordKind[12] = (TK_atomic);
      
    
        //
        // Rule 13:  KeyWord ::= a w a i t
        //
                keywordKind[13] = (TK_await);
      
    
        //
        // Rule 14:  KeyWord ::= c l o c k e d
        //
                keywordKind[14] = (TK_clocked);
      
    
        //
        // Rule 15:  KeyWord ::= e x t e r n
        //
                keywordKind[15] = (TK_extern);
      
    
        //
        // Rule 16:  KeyWord ::= f i n i s h
        //
                keywordKind[16] = (TK_finish);
      
    
        //
        // Rule 17:  KeyWord ::= f o r e a c h
        //
                keywordKind[17] = (TK_foreach);
      
    
        //
        // Rule 18:  KeyWord ::= f u t u r e
        //
                keywordKind[18] = (TK_future);
      
    
        //
        // Rule 19:  KeyWord ::= h e r e
        //
                keywordKind[19] = (TK_here);
      
    
        //
        // Rule 20:  KeyWord ::= l o c a l
        //
                keywordKind[20] = (TK_local);
      
    
        //
        // Rule 21:  KeyWord ::= n e x t
        //
                keywordKind[21] = (TK_next);
      
    
        //
        // Rule 22:  KeyWord ::= n o n b l o c k i n g
        //
                keywordKind[22] = (TK_nonblocking);
      
    
        //
        // Rule 23:  KeyWord ::= n o w
        //
                keywordKind[23] = (TK_now);
      
    
        //
        // Rule 24:  KeyWord ::= o r
        //
                keywordKind[24] = (TK_or);
      
    
        //
        // Rule 25:  KeyWord ::= s a f e
        //
                keywordKind[25] = (TK_safe);
      
    
        //
        // Rule 26:  KeyWord ::= s e l f
        //
                keywordKind[26] = (TK_self);
      
    
        //
        // Rule 27:  KeyWord ::= s e q u e n t i a l
        //
                keywordKind[27] = (TK_sequential);
      
    
        //
        // Rule 28:  KeyWord ::= u n s a f e
        //
                keywordKind[28] = (TK_unsafe);
      
    
        //
        // Rule 29:  KeyWord ::= v a l u e
        //
                keywordKind[29] = (TK_value);
      
    
        //
        // Rule 30:  KeyWord ::= w h e n
        //
                keywordKind[30] = (TK_when);
      
    
        //
        // Rule 31:  KeyWord ::= a b s t r a c t
        //
            keywordKind[31] = (TK_abstract);
      
    
        //
        // Rule 32:  KeyWord ::= a s s e r t
        //
            keywordKind[32] = (TK_assert);
      
    
        //
        // Rule 33:  KeyWord ::= b r e a k
        //
            keywordKind[33] = (TK_break);
      
    
        //
        // Rule 34:  KeyWord ::= c a s e
        //
            keywordKind[34] = (TK_case);
      
    
        //
        // Rule 35:  KeyWord ::= c a t c h
        //
            keywordKind[35] = (TK_catch);
      
    
        //
        // Rule 36:  KeyWord ::= c l a s s
        //
            keywordKind[36] = (TK_class);
      
    
        //
        // Rule 37:  KeyWord ::= c o n s t
        //
            keywordKind[37] = (TK_const);
      
    
        //
        // Rule 38:  KeyWord ::= c o n t i n u e
        //
            keywordKind[38] = (TK_continue);
      
    
        //
        // Rule 39:  KeyWord ::= d e f a u l t
        //
            keywordKind[39] = (TK_default);
      
    
        //
        // Rule 40:  KeyWord ::= d o
        //
            keywordKind[40] = (TK_do);
      
    
        //
        // Rule 41:  KeyWord ::= e l s e
        //
            keywordKind[41] = (TK_else);
      
    
        //
        // Rule 42:  KeyWord ::= e x t e n d s
        //
            keywordKind[42] = (TK_extends);
      
    
        //
        // Rule 43:  KeyWord ::= f a l s e
        //
            keywordKind[43] = (TK_false);
      
    
        //
        // Rule 44:  KeyWord ::= f i n a l
        //
            keywordKind[44] = (TK_final);
      
    
        //
        // Rule 45:  KeyWord ::= f i n a l l y
        //
            keywordKind[45] = (TK_finally);
      
    
        //
        // Rule 46:  KeyWord ::= f o r
        //
            keywordKind[46] = (TK_for);
      
    
        //
        // Rule 47:  KeyWord ::= g o t o
        //
            keywordKind[47] = (TK_goto);
      
    
        //
        // Rule 48:  KeyWord ::= i f
        //
            keywordKind[48] = (TK_if);
      
    
        //
        // Rule 49:  KeyWord ::= i m p l e m e n t s
        //
            keywordKind[49] = (TK_implements);
      
    
        //
        // Rule 50:  KeyWord ::= i m p o r t
        //
            keywordKind[50] = (TK_import);
      
    
        //
        // Rule 51:  KeyWord ::= i n s t a n c e o f
        //
            keywordKind[51] = (TK_instanceof);
      
    
        //
        // Rule 52:  KeyWord ::= i n t e r f a c e
        //
            keywordKind[52] = (TK_interface);
      
    
        //
        // Rule 53:  KeyWord ::= n a t i v e
        //
            keywordKind[53] = (TK_native);
      
    
        //
        // Rule 54:  KeyWord ::= n e w
        //
            keywordKind[54] = (TK_new);
      
    
        //
        // Rule 55:  KeyWord ::= n u l l
        //
            keywordKind[55] = (TK_null);
      
    
        //
        // Rule 56:  KeyWord ::= p a c k a g e
        //
            keywordKind[56] = (TK_package);
      
    
        //
        // Rule 57:  KeyWord ::= p r i v a t e
        //
            keywordKind[57] = (TK_private);
      
    
        //
        // Rule 58:  KeyWord ::= p r o t e c t e d
        //
            keywordKind[58] = (TK_protected);
      
    
        //
        // Rule 59:  KeyWord ::= p u b l i c
        //
            keywordKind[59] = (TK_public);
      
    
        //
        // Rule 60:  KeyWord ::= r e t u r n
        //
            keywordKind[60] = (TK_return);
      
    
        //
        // Rule 61:  KeyWord ::= s t a t i c
        //
            keywordKind[61] = (TK_static);
      
    
        //
        // Rule 62:  KeyWord ::= s t r i c t f p
        //
            keywordKind[62] = (TK_strictfp);
      
    
        //
        // Rule 63:  KeyWord ::= s u p e r
        //
            keywordKind[63] = (TK_super);
      
    
        //
        // Rule 64:  KeyWord ::= s w i t c h
        //
            keywordKind[64] = (TK_switch);
      
    
        //
        // Rule 65:  KeyWord ::= s y n c h r o n i z e d
        //
            keywordKind[65] = (TK_synchronized);
      
    
        //
        // Rule 66:  KeyWord ::= t h i s
        //
            keywordKind[66] = (TK_this);
      
    
        //
        // Rule 67:  KeyWord ::= t h r o w
        //
            keywordKind[67] = (TK_throw);
      
    
        //
        // Rule 68:  KeyWord ::= t h r o w s
        //
            keywordKind[68] = (TK_throws);
      
    
        //
        // Rule 69:  KeyWord ::= t r a n s i e n t
        //
            keywordKind[69] = (TK_transient);
      
    
        //
        // Rule 70:  KeyWord ::= t r u e
        //
            keywordKind[70] = (TK_true);
      
    
        //
        // Rule 71:  KeyWord ::= t r y
        //
            keywordKind[71] = (TK_try);
      
    
        //
        // Rule 72:  KeyWord ::= v o l a t i l e
        //
            keywordKind[72] = (TK_volatile);
      
    
        //
        // Rule 73:  KeyWord ::= w h i l e
        //
            keywordKind[73] = (TK_while);
      
    

        for (int i = 0; i < keywordKind.length; i++)
        {
            if (keywordKind[i] == 0)
                keywordKind[i] = identifierKind;
        }
    }
}

