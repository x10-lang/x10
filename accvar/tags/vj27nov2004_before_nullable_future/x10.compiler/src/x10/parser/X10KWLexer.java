


	package x10.parser;

	import java.util.*;
	import com.ibm.lpg.*;

public class X10KWLexer extends X10KWLexerprs implements X10Parsersym
{
    char[] inputChars;
    int startIndex = -1;
    int index = -1;
    int len = 0;

	    public X10KWLexer(char[] inputChars)
	    {
			this.inputChars = inputChars;
	    }

    public int lexer(int startOffset, int endOffset)
    {
		reset(startOffset, endOffset);
		return parseCharacters();
    }

    int next(int i) { return (++i < len ? i : len); }

    int peek() { return next(index); }

    int token()
    {
    	index = next(index);
        return index;
    }

    int kind( int i )
    {
    	if (i >= len ) return X10KWLexerCharKindMap.EOF;
    	char c = inputChars[i];
    	return X10KWLexerCharKindMap.getKind(c);
    }

    void reset(int startToken, int endToken)
    {
		len = endToken + 1;
		index = startToken - 1;
		startIndex = index;
    }

	    // The KeyWord parser
	    int act, curtok, currentKind, result = 0;
	    int stateStackTop, stack[] = new int [1024];
	
	    public int parseCharacters()
	    {
	    	curtok = token();
	    	act = START_STATE;
	    	currentKind = kind(curtok);
	    	stateStackTop = -1;
	    	
	    	ProcessTerminals: for (;;)
	    	{
	    		stack[++stateStackTop] = act;
	    		act = tAction(act, currentKind);
	    		
	    		if (act <= NUM_RULES)
	    		{
	    			stateStackTop--; // make reduction look like a shift-reduce
	    			stateStackTop -= (rhs[act] - 1);
	    			ruleAction(act);
	    			act = ntAction(stack[stateStackTop], lhs[act]);
	    		}
	    		else if (act > ERROR_ACTION)
	    		{
	    			curtok = token();
	    			currentKind = kind(curtok);
	    			
	    			act -= ERROR_ACTION;
	    			stateStackTop -= (rhs[act] - 1);
	    			ruleAction(act);
	    			act = ntAction(stack[stateStackTop], lhs[act]);
	    		}
	    		else if (act < ACCEPT_ACTION)
	    		{
	    			curtok = token();
	    			currentKind = kind(curtok);
	    		}
	    		else break ProcessTerminals;
	    	}
	    	
	    	return  (act == ERROR_ACTION ? 0 : result);
	    }
	    
	    public void setResult(int n) { result = n; }
    

    public void ruleAction( int ruleNumber)
{
		switch(ruleNumber)
		{
	
 
    //
    // Rule 1:  KeyWord ::= a b s t r a c t
    //
        case 1:
        { 
		    setResult(TK_abstract);
		  
        }
        break; 
		 
    //
    // Rule 2:  KeyWord ::= a s s e r t
    //
        case 2:
        { 
		    setResult(TK_assert);
		  
        }
        break; 
		 
    //
    // Rule 3:  KeyWord ::= b o o l e a n
    //
        case 3:
        { 
		    setResult(TK_boolean);
		  
        }
        break; 
		 
    //
    // Rule 4:  KeyWord ::= b r e a k
    //
        case 4:
        { 
		    setResult(TK_break);
		  
        }
        break; 
		 
    //
    // Rule 5:  KeyWord ::= b y t e
    //
        case 5:
        { 
		    setResult(TK_byte);
		  
        }
        break; 
		 
    //
    // Rule 6:  KeyWord ::= c a s e
    //
        case 6:
        { 
		    setResult(TK_case);
		  
        }
        break; 
		 
    //
    // Rule 7:  KeyWord ::= c a t c h
    //
        case 7:
        { 
		    setResult(TK_catch);
		  
        }
        break; 
		 
    //
    // Rule 8:  KeyWord ::= c h a r
    //
        case 8:
        { 
		    setResult(TK_char);
		  
        }
        break; 
		 
    //
    // Rule 9:  KeyWord ::= c l a s s
    //
        case 9:
        { 
		    setResult(TK_class);
		  
        }
        break; 
		 
    //
    // Rule 10:  KeyWord ::= c o n s t
    //
        case 10:
        { 
		    setResult(TK_const);
		  
        }
        break; 
		 
    //
    // Rule 11:  KeyWord ::= c o n t i n u e
    //
        case 11:
        { 
		    setResult(TK_continue);
		  
        }
        break; 
		 
    //
    // Rule 12:  KeyWord ::= d e f a u l t
    //
        case 12:
        { 
		    setResult(TK_default);
		  
        }
        break; 
		 
    //
    // Rule 13:  KeyWord ::= d o
    //
        case 13:
        { 
		    setResult(TK_do);
		  
        }
        break; 
		 
    //
    // Rule 14:  KeyWord ::= d o u b l e
    //
        case 14:
        { 
		    setResult(TK_double);
		  
        }
        break; 
		 
    //
    // Rule 15:  KeyWord ::= e l s e
    //
        case 15:
        { 
		    setResult(TK_else);
		  
        }
        break; 
		 
    //
    // Rule 16:  KeyWord ::= e n u m
    //
        case 16:
        { 
		    setResult(TK_enum);
		  
        }
        break; 
		 
    //
    // Rule 17:  KeyWord ::= e x t e n d s
    //
        case 17:
        { 
		    setResult(TK_extends);
		  
        }
        break; 
		 
    //
    // Rule 18:  KeyWord ::= f a l s e
    //
        case 18:
        { 
		    setResult(TK_false);
		  
        }
        break; 
		 
    //
    // Rule 19:  KeyWord ::= f i n a l
    //
        case 19:
        { 
		    setResult(TK_final);
		  
        }
        break; 
		 
    //
    // Rule 20:  KeyWord ::= f i n a l l y
    //
        case 20:
        { 
		    setResult(TK_finally);
		  
        }
        break; 
		 
    //
    // Rule 21:  KeyWord ::= f l o a t
    //
        case 21:
        { 
		    setResult(TK_float);
		  
        }
        break; 
		 
    //
    // Rule 22:  KeyWord ::= f o r
    //
        case 22:
        { 
		    setResult(TK_for);
		  
        }
        break; 
		 
    //
    // Rule 23:  KeyWord ::= g o t o
    //
        case 23:
        { 
		    setResult(TK_goto);
		  
        }
        break; 
		 
    //
    // Rule 24:  KeyWord ::= i f
    //
        case 24:
        { 
		    setResult(TK_if);
		  
        }
        break; 
		 
    //
    // Rule 25:  KeyWord ::= i m p l e m e n t s
    //
        case 25:
        { 
		    setResult(TK_implements);
		  
        }
        break; 
		 
    //
    // Rule 26:  KeyWord ::= i m p o r t
    //
        case 26:
        { 
		    setResult(TK_import);
		  
        }
        break; 
		 
    //
    // Rule 27:  KeyWord ::= i n s t a n c e o f
    //
        case 27:
        { 
		    setResult(TK_instanceof);
		  
        }
        break; 
		 
    //
    // Rule 28:  KeyWord ::= i n t
    //
        case 28:
        { 
		    setResult(TK_int);
		  
        }
        break; 
		 
    //
    // Rule 29:  KeyWord ::= i n t e r f a c e
    //
        case 29:
        { 
		    setResult(TK_interface);
		  
        }
        break; 
		 
    //
    // Rule 30:  KeyWord ::= l o n g
    //
        case 30:
        { 
		    setResult(TK_long);
		  
        }
        break; 
		 
    //
    // Rule 31:  KeyWord ::= n a t i v e
    //
        case 31:
        { 
		    setResult(TK_native);
		  
        }
        break; 
		 
    //
    // Rule 32:  KeyWord ::= n e w
    //
        case 32:
        { 
		    setResult(TK_new);
		  
        }
        break; 
		 
    //
    // Rule 33:  KeyWord ::= n u l l
    //
        case 33:
        { 
		    setResult(TK_null);
		  
        }
        break; 
		 
    //
    // Rule 34:  KeyWord ::= p a c k a g e
    //
        case 34:
        { 
		    setResult(TK_package);
		  
        }
        break; 
		 
    //
    // Rule 35:  KeyWord ::= p r i v a t e
    //
        case 35:
        { 
		    setResult(TK_private);
		  
        }
        break; 
		 
    //
    // Rule 36:  KeyWord ::= p r o t e c t e d
    //
        case 36:
        { 
		    setResult(TK_protected);
		  
        }
        break; 
		 
    //
    // Rule 37:  KeyWord ::= p u b l i c
    //
        case 37:
        { 
		    setResult(TK_public);
		  
        }
        break; 
		 
    //
    // Rule 38:  KeyWord ::= r e t u r n
    //
        case 38:
        { 
		    setResult(TK_return);
		  
        }
        break; 
		 
    //
    // Rule 39:  KeyWord ::= s h o r t
    //
        case 39:
        { 
		    setResult(TK_short);
		  
        }
        break; 
		 
    //
    // Rule 40:  KeyWord ::= s t a t i c
    //
        case 40:
        { 
		    setResult(TK_static);
		  
        }
        break; 
		 
    //
    // Rule 41:  KeyWord ::= s t r i c t f p
    //
        case 41:
        { 
		    setResult(TK_strictfp);
		  
        }
        break; 
		 
    //
    // Rule 42:  KeyWord ::= s u p e r
    //
        case 42:
        { 
		    setResult(TK_super);
		  
        }
        break; 
		 
    //
    // Rule 43:  KeyWord ::= s w i t c h
    //
        case 43:
        { 
		    setResult(TK_switch);
		  
        }
        break; 
		 
    //
    // Rule 44:  KeyWord ::= s y n c h r o n i z e d
    //
        case 44:
        { 
		    setResult(TK_synchronized);
		  
        }
        break; 
		 
    //
    // Rule 45:  KeyWord ::= t h i s
    //
        case 45:
        { 
		    setResult(TK_this);
		  
        }
        break; 
		 
    //
    // Rule 46:  KeyWord ::= t h r o w
    //
        case 46:
        { 
		    setResult(TK_throw);
		  
        }
        break; 
		 
    //
    // Rule 47:  KeyWord ::= t h r o w s
    //
        case 47:
        { 
		    setResult(TK_throws);
		  
        }
        break; 
		 
    //
    // Rule 48:  KeyWord ::= t r a n s i e n t
    //
        case 48:
        { 
		    setResult(TK_transient);
		  
        }
        break; 
		 
    //
    // Rule 49:  KeyWord ::= t r u e
    //
        case 49:
        { 
		    setResult(TK_true);
		  
        }
        break; 
		 
    //
    // Rule 50:  KeyWord ::= t r y
    //
        case 50:
        { 
		    setResult(TK_try);
		  
        }
        break; 
		 
    //
    // Rule 51:  KeyWord ::= v o i d
    //
        case 51:
        { 
		    setResult(TK_void);
		  
        }
        break; 
		 
    //
    // Rule 52:  KeyWord ::= v o l a t i l e
    //
        case 52:
        { 
		    setResult(TK_volatile);
		  
        }
        break; 
		 
    //
    // Rule 53:  KeyWord ::= w h i l e
    //
        case 53:
        { 
		    setResult(TK_while);
		  
        }
        break; 
		 
    //
    // Rule 54:  KeyWord ::= a s y n c
    //
        case 54:
        { 
            setResult(TK_async);
          
        }
        break; 
         
    //
    // Rule 55:  KeyWord ::= a t e a c h
    //
        case 55:
        { 
            setResult(TK_ateach);
          
        }
        break; 
         
    //
    // Rule 56:  KeyWord ::= a t o m i c
    //
        case 56:
        { 
            setResult(TK_atomic);
          
        }
        break; 
         
    //
    // Rule 57:  KeyWord ::= c l o c k
    //
        case 57:
        { 
            setResult(TK_clock);
          
        }
        break; 
         
    //
    // Rule 58:  KeyWord ::= c l o c k e d
    //
        case 58:
        { 
            setResult(TK_clocked);
          
        }
        break; 
         
    //
    // Rule 59:  KeyWord ::= c u r r e n t
    //
        case 59:
        { 
            setResult(TK_current);
          
        }
        break; 
         
    //
    // Rule 60:  KeyWord ::= d i s t r i b u t i o n
    //
        case 60:
        { 
            setResult(TK_distribution);
          
        }
        break; 
         
    //
    // Rule 61:  KeyWord ::= d r o p
    //
        case 61:
        { 
            setResult(TK_drop);
          
        }
        break; 
         
    //
    // Rule 62:  KeyWord ::= f l o w
    //
        case 62:
        { 
            setResult(TK_flow);
          
        }
        break; 
         
    //
    // Rule 63:  KeyWord ::= f o r c e
    //
        case 63:
        { 
            setResult(TK_force);
          
        }
        break; 
         
    //
    // Rule 64:  KeyWord ::= f o r e a c h
    //
        case 64:
        { 
            setResult(TK_foreach);
          
        }
        break; 
         
    //
    // Rule 65:  KeyWord ::= f u t u r e
    //
        case 65:
        { 
            setResult(TK_future);
          
        }
        break; 
         
    //
    // Rule 66:  KeyWord ::= h e r e
    //
        case 66:
        { 
            setResult(TK_here);
          
        }
        break; 
         
    //
    // Rule 67:  KeyWord ::= l o c a l
    //
        case 67:
        { 
            setResult(TK_local);
          
        }
        break; 
         
    //
    // Rule 68:  KeyWord ::= m e t h o d l o c a l
    //
        case 68:
        { 
            setResult(TK_methodlocal);
          
        }
        break; 
         
    //
    // Rule 69:  KeyWord ::= n e x t
    //
        case 69:
        { 
            setResult(TK_next);
          
        }
        break; 
         
    //
    // Rule 70:  KeyWord ::= n o w
    //
        case 70:
        { 
            setResult(TK_now);
          
        }
        break; 
         
    //
    // Rule 71:  KeyWord ::= n u l l a b l e
    //
        case 71:
        { 
            setResult(TK_nullable);
          
        }
        break; 
         
    //
    // Rule 72:  KeyWord ::= o r
    //
        case 72:
        { 
            setResult(TK_or);
          
        }
        break; 
         
    //
    // Rule 73:  KeyWord ::= p l a c e
    //
        case 73:
        { 
            setResult(TK_place);
          
        }
        break; 
         
    //
    // Rule 74:  KeyWord ::= p l a c e l o c a l
    //
        case 74:
        { 
            setResult(TK_placelocal);
          
        }
        break; 
         
    //
    // Rule 75:  KeyWord ::= r a n g e
    //
        case 75:
        { 
            setResult(TK_range);
          
        }
        break; 
         
    //
    // Rule 76:  KeyWord ::= r a n k
    //
        case 76:
        { 
            setResult(TK_rank);
          
        }
        break; 
         
    //
    // Rule 77:  KeyWord ::= r e f e r e n c e
    //
        case 77:
        { 
            setResult(TK_reference);
          
        }
        break; 
         
    //
    // Rule 78:  KeyWord ::= r e g i o n
    //
        case 78:
        { 
            setResult(TK_region);
          
        }
        break; 
         
    //
    // Rule 79:  KeyWord ::= r u n s a t
    //
        case 79:
        { 
            setResult(TK_runsat);
          
        }
        break; 
         
    //
    // Rule 80:  KeyWord ::= r u n s o n
    //
        case 80:
        { 
            setResult(TK_runson);
          
        }
        break; 
         
    //
    // Rule 81:  KeyWord ::= t h r e a d l o c a l
    //
        case 81:
        { 
            setResult(TK_threadlocal);
          
        }
        break; 
         
    //
    // Rule 82:  KeyWord ::= v a l u e
    //
        case 82:
        { 
            setResult(TK_value);
          
        }
        break; 
         
    //
    // Rule 83:  KeyWord ::= w h e n
    //
        case 83:
        { 
            setResult(TK_when);
          
        }
        break; 
            
	    	default:
	        break;
		}
		return;
    }
	

}
	
