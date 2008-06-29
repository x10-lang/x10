
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
    private final int keywordKind[] = new int[74 + 1];

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
        // Rule 11:  KeyWord ::= i n c o m p l e t e
        //
            keywordKind[11] = (TK_incomplete);
      
    
        //
        // Rule 12:  KeyWord ::= a t e a c h
        //
                keywordKind[12] = (TK_ateach);
      
    
        //
        // Rule 13:  KeyWord ::= a t o m i c
        //
                keywordKind[13] = (TK_atomic);
      
    
        //
        // Rule 14:  KeyWord ::= a w a i t
        //
                keywordKind[14] = (TK_await);
      
    
        //
        // Rule 15:  KeyWord ::= c l o c k e d
        //
                keywordKind[15] = (TK_clocked);
      
    
        //
        // Rule 16:  KeyWord ::= e x t e r n
        //
                keywordKind[16] = (TK_extern);
      
    
        //
        // Rule 17:  KeyWord ::= f i n i s h
        //
                keywordKind[17] = (TK_finish);
      
    
        //
        // Rule 18:  KeyWord ::= f o r e a c h
        //
                keywordKind[18] = (TK_foreach);
      
    
        //
        // Rule 19:  KeyWord ::= f u t u r e
        //
                keywordKind[19] = (TK_future);
      
    
        //
        // Rule 20:  KeyWord ::= h e r e
        //
                keywordKind[20] = (TK_here);
      
    
        //
        // Rule 21:  KeyWord ::= l o c a l
        //
                keywordKind[21] = (TK_local);
      
    
        //
        // Rule 22:  KeyWord ::= n e x t
        //
                keywordKind[22] = (TK_next);
      
    
        //
        // Rule 23:  KeyWord ::= n o n b l o c k i n g
        //
                keywordKind[23] = (TK_nonblocking);
      
    
        //
        // Rule 24:  KeyWord ::= n o w
        //
                keywordKind[24] = (TK_now);
      
    
        //
        // Rule 25:  KeyWord ::= o r
        //
                keywordKind[25] = (TK_or);
      
    
        //
        // Rule 26:  KeyWord ::= s a f e
        //
                keywordKind[26] = (TK_safe);
      
    
        //
        // Rule 27:  KeyWord ::= s e l f
        //
                keywordKind[27] = (TK_self);
      
    
        //
        // Rule 28:  KeyWord ::= s e q u e n t i a l
        //
                keywordKind[28] = (TK_sequential);
      
    
        //
        // Rule 29:  KeyWord ::= u n s a f e
        //
                keywordKind[29] = (TK_unsafe);
      
    
        //
        // Rule 30:  KeyWord ::= v a l u e
        //
                keywordKind[30] = (TK_value);
      
    
        //
        // Rule 31:  KeyWord ::= w h e n
        //
                keywordKind[31] = (TK_when);
      
    
        //
        // Rule 32:  KeyWord ::= a b s t r a c t
        //
            keywordKind[32] = (TK_abstract);
      
    
        //
        // Rule 33:  KeyWord ::= a s s e r t
        //
            keywordKind[33] = (TK_assert);
      
    
        //
        // Rule 34:  KeyWord ::= b r e a k
        //
            keywordKind[34] = (TK_break);
      
    
        //
        // Rule 35:  KeyWord ::= c a s e
        //
            keywordKind[35] = (TK_case);
      
    
        //
        // Rule 36:  KeyWord ::= c a t c h
        //
            keywordKind[36] = (TK_catch);
      
    
        //
        // Rule 37:  KeyWord ::= c l a s s
        //
            keywordKind[37] = (TK_class);
      
    
        //
        // Rule 38:  KeyWord ::= c o n s t
        //
            keywordKind[38] = (TK_const);
      
    
        //
        // Rule 39:  KeyWord ::= c o n t i n u e
        //
            keywordKind[39] = (TK_continue);
      
    
        //
        // Rule 40:  KeyWord ::= d e f a u l t
        //
            keywordKind[40] = (TK_default);
      
    
        //
        // Rule 41:  KeyWord ::= d o
        //
            keywordKind[41] = (TK_do);
      
    
        //
        // Rule 42:  KeyWord ::= e l s e
        //
            keywordKind[42] = (TK_else);
      
    
        //
        // Rule 43:  KeyWord ::= e x t e n d s
        //
            keywordKind[43] = (TK_extends);
      
    
        //
        // Rule 44:  KeyWord ::= f a l s e
        //
            keywordKind[44] = (TK_false);
      
    
        //
        // Rule 45:  KeyWord ::= f i n a l
        //
            keywordKind[45] = (TK_final);
      
    
        //
        // Rule 46:  KeyWord ::= f i n a l l y
        //
            keywordKind[46] = (TK_finally);
      
    
        //
        // Rule 47:  KeyWord ::= f o r
        //
            keywordKind[47] = (TK_for);
      
    
        //
        // Rule 48:  KeyWord ::= g o t o
        //
            keywordKind[48] = (TK_goto);
      
    
        //
        // Rule 49:  KeyWord ::= i f
        //
            keywordKind[49] = (TK_if);
      
    
        //
        // Rule 50:  KeyWord ::= i m p l e m e n t s
        //
            keywordKind[50] = (TK_implements);
      
    
        //
        // Rule 51:  KeyWord ::= i m p o r t
        //
            keywordKind[51] = (TK_import);
      
    
        //
        // Rule 52:  KeyWord ::= i n s t a n c e o f
        //
            keywordKind[52] = (TK_instanceof);
      
    
        //
        // Rule 53:  KeyWord ::= i n t e r f a c e
        //
            keywordKind[53] = (TK_interface);
      
    
        //
        // Rule 54:  KeyWord ::= n a t i v e
        //
            keywordKind[54] = (TK_native);
      
    
        //
        // Rule 55:  KeyWord ::= n e w
        //
            keywordKind[55] = (TK_new);
      
    
        //
        // Rule 56:  KeyWord ::= n u l l
        //
            keywordKind[56] = (TK_null);
      
    
        //
        // Rule 57:  KeyWord ::= p a c k a g e
        //
            keywordKind[57] = (TK_package);
      
    
        //
        // Rule 58:  KeyWord ::= p r i v a t e
        //
            keywordKind[58] = (TK_private);
      
    
        //
        // Rule 59:  KeyWord ::= p r o t e c t e d
        //
            keywordKind[59] = (TK_protected);
      
    
        //
        // Rule 60:  KeyWord ::= p u b l i c
        //
            keywordKind[60] = (TK_public);
      
    
        //
        // Rule 61:  KeyWord ::= r e t u r n
        //
            keywordKind[61] = (TK_return);
      
    
        //
        // Rule 62:  KeyWord ::= s t a t i c
        //
            keywordKind[62] = (TK_static);
      
    
        //
        // Rule 63:  KeyWord ::= s t r i c t f p
        //
            keywordKind[63] = (TK_strictfp);
      
    
        //
        // Rule 64:  KeyWord ::= s u p e r
        //
            keywordKind[64] = (TK_super);
      
    
        //
        // Rule 65:  KeyWord ::= s w i t c h
        //
            keywordKind[65] = (TK_switch);
      
    
        //
        // Rule 66:  KeyWord ::= s y n c h r o n i z e d
        //
            keywordKind[66] = (TK_synchronized);
      
    
        //
        // Rule 67:  KeyWord ::= t h i s
        //
            keywordKind[67] = (TK_this);
      
    
        //
        // Rule 68:  KeyWord ::= t h r o w
        //
            keywordKind[68] = (TK_throw);
      
    
        //
        // Rule 69:  KeyWord ::= t h r o w s
        //
            keywordKind[69] = (TK_throws);
      
    
        //
        // Rule 70:  KeyWord ::= t r a n s i e n t
        //
            keywordKind[70] = (TK_transient);
      
    
        //
        // Rule 71:  KeyWord ::= t r u e
        //
            keywordKind[71] = (TK_true);
      
    
        //
        // Rule 72:  KeyWord ::= t r y
        //
            keywordKind[72] = (TK_try);
      
    
        //
        // Rule 73:  KeyWord ::= v o l a t i l e
        //
            keywordKind[73] = (TK_volatile);
      
    
        //
        // Rule 74:  KeyWord ::= w h i l e
        //
            keywordKind[74] = (TK_while);
      
    

        for (int i = 0; i < keywordKind.length; i++)
        {
            if (keywordKind[i] == 0)
                keywordKind[i] = identifierKind;
        }
    }
}

