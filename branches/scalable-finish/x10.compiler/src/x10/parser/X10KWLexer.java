/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.parser;

import lpg.runtime.*;
import java.util.*;

public class X10KWLexer extends X10KWLexerprs
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
                                   ? X10KWLexersym.Char_EOF
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
        
                keywordKind[1] = (X10Parsersym.TK_async);
      
    
        //
        // Rule 2:  KeyWord ::= t y p e
        //
        
            keywordKind[2] = (X10Parsersym.TK_type);
      
    
        //
        // Rule 3:  KeyWord ::= a t
        //
        
            keywordKind[3] = (X10Parsersym.TK_at);
      
    
        //
        // Rule 4:  KeyWord ::= p r o p e r t y
        //
        
            keywordKind[4] = (X10Parsersym.TK_property);
      
    
        //
        // Rule 5:  KeyWord ::= d e f
        //
        
            keywordKind[5] = (X10Parsersym.TK_def);
      
    
        //
        // Rule 6:  KeyWord ::= v a l
        //
        
            keywordKind[6] = (X10Parsersym.TK_val);
      
    
        //
        // Rule 7:  KeyWord ::= v a r
        //
        
            keywordKind[7] = (X10Parsersym.TK_var);
      
    
        //
        // Rule 8:  KeyWord ::= a s
        //
        
            keywordKind[8] = (X10Parsersym.TK_as);
      
    
        //
        // Rule 9:  KeyWord ::= i n
        //
        
            keywordKind[9] = (X10Parsersym.TK_in);
      
    
        //
        // Rule 10:  KeyWord ::= i n c o m p l e t e
        //
        
            keywordKind[10] = (X10Parsersym.TK_incomplete);
      
    
        //
        // Rule 11:  KeyWord ::= s h a r e d
        //
        
            keywordKind[11] = (X10Parsersym.TK_shared);
      
    
        //
        // Rule 12:  KeyWord ::= a t e a c h
        //
        
                keywordKind[12] = (X10Parsersym.TK_ateach);
      
    
        //
        // Rule 13:  KeyWord ::= a t o m i c
        //
        
                keywordKind[13] = (X10Parsersym.TK_atomic);
      
    
        //
        // Rule 14:  KeyWord ::= a w a i t
        //
        
                keywordKind[14] = (X10Parsersym.TK_await);
      
    
        //
        // Rule 15:  KeyWord ::= c l o c k e d
        //
        
                keywordKind[15] = (X10Parsersym.TK_clocked);
      
    
        //
        // Rule 16:  KeyWord ::= e x t e r n
        //
        
                keywordKind[16] = (X10Parsersym.TK_extern);
      
    
        //
        // Rule 17:  KeyWord ::= f i n i s h
        //
        
                keywordKind[17] = (X10Parsersym.TK_finish);
      
    
        //
        // Rule 18:  KeyWord ::= f o r e a c h
        //
        
                keywordKind[18] = (X10Parsersym.TK_foreach);
      
    
        //
        // Rule 19:  KeyWord ::= f u t u r e
        //
        
                keywordKind[19] = (X10Parsersym.TK_future);
      
    
        //
        // Rule 20:  KeyWord ::= h e r e
        //
        
                keywordKind[20] = (X10Parsersym.TK_here);
      
    
        //
        // Rule 21:  KeyWord ::= l o c a l
        //
        
                keywordKind[21] = (X10Parsersym.TK_local);
      
    
        //
        // Rule 22:  KeyWord ::= n e x t
        //
        
                keywordKind[22] = (X10Parsersym.TK_next);
      
    
        //
        // Rule 23:  KeyWord ::= n o n b l o c k i n g
        //
        
                keywordKind[23] = (X10Parsersym.TK_nonblocking);
      
    
        //
        // Rule 24:  KeyWord ::= o r
        //
        
                keywordKind[24] = (X10Parsersym.TK_or);
      
    
        //
        // Rule 25:  KeyWord ::= s a f e
        //
        
                keywordKind[25] = (X10Parsersym.TK_safe);
      
    
        //
        // Rule 26:  KeyWord ::= s e l f
        //
        
                keywordKind[26] = (X10Parsersym.TK_self);
      
    
        //
        // Rule 27:  KeyWord ::= s e q u e n t i a l
        //
        
                keywordKind[27] = (X10Parsersym.TK_sequential);
      
    
        //
        // Rule 28:  KeyWord ::= w h e n
        //
        
                keywordKind[28] = (X10Parsersym.TK_when);
      
    
        //
        // Rule 29:  KeyWord ::= a b s t r a c t
        //
        
            keywordKind[29] = (X10Parsersym.TK_abstract);
      
    
        //
        // Rule 30:  KeyWord ::= a s s e r t
        //
        
            keywordKind[30] = (X10Parsersym.TK_assert);
      
    
        //
        // Rule 31:  KeyWord ::= b r e a k
        //
        
            keywordKind[31] = (X10Parsersym.TK_break);
      
    
        //
        // Rule 32:  KeyWord ::= c a s e
        //
        
            keywordKind[32] = (X10Parsersym.TK_case);
      
    
        //
        // Rule 33:  KeyWord ::= c a t c h
        //
        
            keywordKind[33] = (X10Parsersym.TK_catch);
      
    
        //
        // Rule 34:  KeyWord ::= c l a s s
        //
        
            keywordKind[34] = (X10Parsersym.TK_class);
      
    
        //
        // Rule 35:  KeyWord ::= c o n s t
        //
        
            keywordKind[35] = (X10Parsersym.TK_const);
      
    
        //
        // Rule 36:  KeyWord ::= c o n t i n u e
        //
        
            keywordKind[36] = (X10Parsersym.TK_continue);
      
    
        //
        // Rule 37:  KeyWord ::= d e f a u l t
        //
        
            keywordKind[37] = (X10Parsersym.TK_default);
      
    
        //
        // Rule 38:  KeyWord ::= d o
        //
        
            keywordKind[38] = (X10Parsersym.TK_do);
      
    
        //
        // Rule 39:  KeyWord ::= e l s e
        //
        
            keywordKind[39] = (X10Parsersym.TK_else);
      
    
        //
        // Rule 40:  KeyWord ::= e x t e n d s
        //
        
            keywordKind[40] = (X10Parsersym.TK_extends);
      
    
        //
        // Rule 41:  KeyWord ::= f a l s e
        //
        
            keywordKind[41] = (X10Parsersym.TK_false);
      
    
        //
        // Rule 42:  KeyWord ::= f i n a l
        //
        
            keywordKind[42] = (X10Parsersym.TK_final);
      
    
        //
        // Rule 43:  KeyWord ::= f i n a l l y
        //
        
            keywordKind[43] = (X10Parsersym.TK_finally);
      
    
        //
        // Rule 44:  KeyWord ::= f o r
        //
        
            keywordKind[44] = (X10Parsersym.TK_for);
      
    
        //
        // Rule 45:  KeyWord ::= g o t o
        //
        
            keywordKind[45] = (X10Parsersym.TK_goto);
      
    
        //
        // Rule 46:  KeyWord ::= g l o b a l
        //
        
            keywordKind[46] = (X10Parsersym.TK_global);
      
    
        //
        // Rule 47:  KeyWord ::= i f
        //
        
            keywordKind[47] = (X10Parsersym.TK_if);
      
    
        //
        // Rule 48:  KeyWord ::= i m p l e m e n t s
        //
        
            keywordKind[48] = (X10Parsersym.TK_implements);
      
    
        //
        // Rule 49:  KeyWord ::= i m p o r t
        //
        
            keywordKind[49] = (X10Parsersym.TK_import);
      
    
        //
        // Rule 50:  KeyWord ::= i n s t a n c e o f
        //
        
            keywordKind[50] = (X10Parsersym.TK_instanceof);
      
    
        //
        // Rule 51:  KeyWord ::= i n t e r f a c e
        //
        
            keywordKind[51] = (X10Parsersym.TK_interface);
      
    
        //
        // Rule 52:  KeyWord ::= n a t i v e
        //
        
            keywordKind[52] = (X10Parsersym.TK_native);
      
    
        //
        // Rule 53:  KeyWord ::= n e w
        //
        
            keywordKind[53] = (X10Parsersym.TK_new);
      
    
        //
        // Rule 54:  KeyWord ::= n u l l
        //
        
            keywordKind[54] = (X10Parsersym.TK_null);
      
    
        //
        // Rule 55:  KeyWord ::= o p e r a t o r
        //
        
            keywordKind[55] = (X10Parsersym.TK_operator);
      
    
        //
        // Rule 56:  KeyWord ::= p a c k a g e
        //
        
            keywordKind[56] = (X10Parsersym.TK_package);
      
    
        //
        // Rule 57:  KeyWord ::= p r i v a t e
        //
        
            keywordKind[57] = (X10Parsersym.TK_private);
      
    
        //
        // Rule 58:  KeyWord ::= p r o t e c t e d
        //
        
            keywordKind[58] = (X10Parsersym.TK_protected);
      
    
        //
        // Rule 59:  KeyWord ::= p r o t o
        //
        
            keywordKind[59] = (X10Parsersym.TK_proto);
      
    
        //
        // Rule 60:  KeyWord ::= p u b l i c
        //
        
            keywordKind[60] = (X10Parsersym.TK_public);
      
    
        //
        // Rule 61:  KeyWord ::= r e t u r n
        //
        
            keywordKind[61] = (X10Parsersym.TK_return);
      
    
        //
        // Rule 62:  KeyWord ::= s t a t i c
        //
        
            keywordKind[62] = (X10Parsersym.TK_static);
      
    
        //
        // Rule 63:  KeyWord ::= s t r u c t
        //
        
            keywordKind[63] = (X10Parsersym.TK_struct);
      
    
        //
        // Rule 64:  KeyWord ::= s u p e r
        //
        
            keywordKind[64] = (X10Parsersym.TK_super);
      
    
        //
        // Rule 65:  KeyWord ::= s w i t c h
        //
        
            keywordKind[65] = (X10Parsersym.TK_switch);
      
    
        //
        // Rule 66:  KeyWord ::= t h i s
        //
        
            keywordKind[66] = (X10Parsersym.TK_this);
      
    
        //
        // Rule 67:  KeyWord ::= t h r o w
        //
        
            keywordKind[67] = (X10Parsersym.TK_throw);
      
    
        //
        // Rule 68:  KeyWord ::= t h r o w s
        //
        
            keywordKind[68] = (X10Parsersym.TK_throws);
      
    
        //
        // Rule 69:  KeyWord ::= t r u e
        //
        
            keywordKind[69] = (X10Parsersym.TK_true);
      
    
        //
        // Rule 70:  KeyWord ::= t r y
        //
        
            keywordKind[70] = (X10Parsersym.TK_try);
      
    
        //
        // Rule 71:  KeyWord ::= w h i l e
        //
        
            keywordKind[71] = (X10Parsersym.TK_while);
      
    
        //
        // Rule 72:  KeyWord ::= o f f e r
        //
        
            keywordKind[72] = (X10Parsersym.TK_offer);
      
    
        //
        // Rule 73:  KeyWord ::= o f f e r s
        //
        
            keywordKind[73] = (X10Parsersym.TK_offers);
      
    
        for (int i = 0; i < keywordKind.length; i++)
        {
            if (keywordKind[i] == 0)
                keywordKind[i] = identifierKind;
        }
    }
}

