


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


    public void ruleAction( int ruleNumber)
    {
	switch(ruleNumber)
	{

 
    //
    // Rule 1:  KeyWord ::= a b s t r a c t
    //
            case 1:
            { 
		    setSym1(TK_abstract);
		  
            }
            break; 
		 
    //
    // Rule 2:  KeyWord ::= a s s e r t
    //
            case 2:
            { 
		    setSym1(TK_assert);
		  
            }
            break; 
		 
    //
    // Rule 3:  KeyWord ::= b o o l e a n
    //
            case 3:
            { 
		    setSym1(TK_boolean);
		  
            }
            break; 
		 
    //
    // Rule 4:  KeyWord ::= b r e a k
    //
            case 4:
            { 
		    setSym1(TK_break);
		  
            }
            break; 
		 
    //
    // Rule 5:  KeyWord ::= b y t e
    //
            case 5:
            { 
		    setSym1(TK_byte);
		  
            }
            break; 
		 
    //
    // Rule 6:  KeyWord ::= c a s e
    //
            case 6:
            { 
		    setSym1(TK_case);
		  
            }
            break; 
		 
    //
    // Rule 7:  KeyWord ::= c a t c h
    //
            case 7:
            { 
		    setSym1(TK_catch);
		  
            }
            break; 
		 
    //
    // Rule 8:  KeyWord ::= c h a r
    //
            case 8:
            { 
		    setSym1(TK_char);
		  
            }
            break; 
		 
    //
    // Rule 9:  KeyWord ::= c l a s s
    //
            case 9:
            { 
		    setSym1(TK_class);
		  
            }
            break; 
		 
    //
    // Rule 10:  KeyWord ::= c o n s t
    //
            case 10:
            { 
		    setSym1(TK_const);
		  
            }
            break; 
		 
    //
    // Rule 11:  KeyWord ::= c o n t i n u e
    //
            case 11:
            { 
		    setSym1(TK_continue);
		  
            }
            break; 
		 
    //
    // Rule 12:  KeyWord ::= d e f a u l t
    //
            case 12:
            { 
		    setSym1(TK_default);
		  
            }
            break; 
		 
    //
    // Rule 13:  KeyWord ::= d o
    //
            case 13:
            { 
		    setSym1(TK_do);
		  
            }
            break; 
		 
    //
    // Rule 14:  KeyWord ::= d o u b l e
    //
            case 14:
            { 
		    setSym1(TK_double);
		  
            }
            break; 
		 
    //
    // Rule 15:  KeyWord ::= e l s e
    //
            case 15:
            { 
		    setSym1(TK_else);
		  
            }
            break; 
		 
    //
    // Rule 16:  KeyWord ::= e n u m
    //
            case 16:
            { 
		    setSym1(TK_enum);
		  
            }
            break; 
		 
    //
    // Rule 17:  KeyWord ::= e x t e n d s
    //
            case 17:
            { 
		    setSym1(TK_extends);
		  
            }
            break; 
		 
    //
    // Rule 18:  KeyWord ::= f a l s e
    //
            case 18:
            { 
		    setSym1(TK_false);
		  
            }
            break; 
		 
    //
    // Rule 19:  KeyWord ::= f i n a l
    //
            case 19:
            { 
		    setSym1(TK_final);
		  
            }
            break; 
		 
    //
    // Rule 20:  KeyWord ::= f i n a l l y
    //
            case 20:
            { 
		    setSym1(TK_finally);
		  
            }
            break; 
		 
    //
    // Rule 21:  KeyWord ::= f l o a t
    //
            case 21:
            { 
		    setSym1(TK_float);
		  
            }
            break; 
		 
    //
    // Rule 22:  KeyWord ::= f o r
    //
            case 22:
            { 
		    setSym1(TK_for);
		  
            }
            break; 
		 
    //
    // Rule 23:  KeyWord ::= g o t o
    //
            case 23:
            { 
		    setSym1(TK_goto);
		  
            }
            break; 
		 
    //
    // Rule 24:  KeyWord ::= i f
    //
            case 24:
            { 
		    setSym1(TK_if);
		  
            }
            break; 
		 
    //
    // Rule 25:  KeyWord ::= i m p l e m e n t s
    //
            case 25:
            { 
		    setSym1(TK_implements);
		  
            }
            break; 
		 
    //
    // Rule 26:  KeyWord ::= i m p o r t
    //
            case 26:
            { 
		    setSym1(TK_import);
		  
            }
            break; 
		 
    //
    // Rule 27:  KeyWord ::= i n s t a n c e o f
    //
            case 27:
            { 
		    setSym1(TK_instanceof);
		  
            }
            break; 
		 
    //
    // Rule 28:  KeyWord ::= i n t
    //
            case 28:
            { 
		    setSym1(TK_int);
		  
            }
            break; 
		 
    //
    // Rule 29:  KeyWord ::= i n t e r f a c e
    //
            case 29:
            { 
		    setSym1(TK_interface);
		  
            }
            break; 
		 
    //
    // Rule 30:  KeyWord ::= l o n g
    //
            case 30:
            { 
		    setSym1(TK_long);
		  
            }
            break; 
		 
    //
    // Rule 31:  KeyWord ::= n a t i v e
    //
            case 31:
            { 
		    setSym1(TK_native);
		  
            }
            break; 
		 
    //
    // Rule 32:  KeyWord ::= n e w
    //
            case 32:
            { 
		    setSym1(TK_new);
		  
            }
            break; 
		 
    //
    // Rule 33:  KeyWord ::= n u l l
    //
            case 33:
            { 
		    setSym1(TK_null);
		  
            }
            break; 
		 
    //
    // Rule 34:  KeyWord ::= p a c k a g e
    //
            case 34:
            { 
		    setSym1(TK_package);
		  
            }
            break; 
		 
    //
    // Rule 35:  KeyWord ::= p r i v a t e
    //
            case 35:
            { 
		    setSym1(TK_private);
		  
            }
            break; 
		 
    //
    // Rule 36:  KeyWord ::= p r o t e c t e d
    //
            case 36:
            { 
		    setSym1(TK_protected);
		  
            }
            break; 
		 
    //
    // Rule 37:  KeyWord ::= p u b l i c
    //
            case 37:
            { 
		    setSym1(TK_public);
		  
            }
            break; 
		 
    //
    // Rule 38:  KeyWord ::= r e t u r n
    //
            case 38:
            { 
		    setSym1(TK_return);
		  
            }
            break; 
		 
    //
    // Rule 39:  KeyWord ::= s h o r t
    //
            case 39:
            { 
		    setSym1(TK_short);
		  
            }
            break; 
		 
    //
    // Rule 40:  KeyWord ::= s t a t i c
    //
            case 40:
            { 
		    setSym1(TK_static);
		  
            }
            break; 
		 
    //
    // Rule 41:  KeyWord ::= s t r i c t f p
    //
            case 41:
            { 
		    setSym1(TK_strictfp);
		  
            }
            break; 
		 
    //
    // Rule 42:  KeyWord ::= s u p e r
    //
            case 42:
            { 
		    setSym1(TK_super);
		  
            }
            break; 
		 
    //
    // Rule 43:  KeyWord ::= s w i t c h
    //
            case 43:
            { 
		    setSym1(TK_switch);
		  
            }
            break; 
		 
    //
    // Rule 44:  KeyWord ::= s y n c h r o n i z e d
    //
            case 44:
            { 
		    setSym1(TK_synchronized);
		  
            }
            break; 
		 
    //
    // Rule 45:  KeyWord ::= t h i s
    //
            case 45:
            { 
		    setSym1(TK_this);
		  
            }
            break; 
		 
    //
    // Rule 46:  KeyWord ::= t h r o w
    //
            case 46:
            { 
		    setSym1(TK_throw);
		  
            }
            break; 
		 
    //
    // Rule 47:  KeyWord ::= t h r o w s
    //
            case 47:
            { 
		    setSym1(TK_throws);
		  
            }
            break; 
		 
    //
    // Rule 48:  KeyWord ::= t r a n s i e n t
    //
            case 48:
            { 
		    setSym1(TK_transient);
		  
            }
            break; 
		 
    //
    // Rule 49:  KeyWord ::= t r u e
    //
            case 49:
            { 
		    setSym1(TK_true);
		  
            }
            break; 
		 
    //
    // Rule 50:  KeyWord ::= t r y
    //
            case 50:
            { 
		    setSym1(TK_try);
		  
            }
            break; 
		 
    //
    // Rule 51:  KeyWord ::= v o i d
    //
            case 51:
            { 
		    setSym1(TK_void);
		  
            }
            break; 
		 
    //
    // Rule 52:  KeyWord ::= v o l a t i l e
    //
            case 52:
            { 
		    setSym1(TK_volatile);
		  
            }
            break; 
		 
    //
    // Rule 53:  KeyWord ::= w h i l e
    //
            case 53:
            { 
		    setSym1(TK_while);
		  
            }
            break; 
		 
    //
    // Rule 54:  KeyWord ::= a s y n c
    //
            case 54:
            { 
            setSym1(TK_async);
          
            }
            break; 
         
    //
    // Rule 55:  KeyWord ::= a t e a c h
    //
            case 55:
            { 
            setSym1(TK_ateach);
          
            }
            break; 
         
    //
    // Rule 56:  KeyWord ::= a t o m i c
    //
            case 56:
            { 
            setSym1(TK_atomic);
          
            }
            break; 
         
    //
    // Rule 57:  KeyWord ::= c l o c k
    //
            case 57:
            { 
            setSym1(TK_clock);
          
            }
            break; 
         
    //
    // Rule 58:  KeyWord ::= c l o c k e d
    //
            case 58:
            { 
            setSym1(TK_clocked);
          
            }
            break; 
         
    //
    // Rule 59:  KeyWord ::= c u r r e n t
    //
            case 59:
            { 
            setSym1(TK_current);
          
            }
            break; 
         
    //
    // Rule 60:  KeyWord ::= d i s t r i b u t i o n
    //
            case 60:
            { 
            setSym1(TK_distribution);
          
            }
            break; 
         
    //
    // Rule 61:  KeyWord ::= d r o p
    //
            case 61:
            { 
            setSym1(TK_drop);
          
            }
            break; 
         
    //
    // Rule 62:  KeyWord ::= f l o w
    //
            case 62:
            { 
            setSym1(TK_flow);
          
            }
            break; 
         
    //
    // Rule 63:  KeyWord ::= f o r c e
    //
            case 63:
            { 
            setSym1(TK_force);
          
            }
            break; 
         
    //
    // Rule 64:  KeyWord ::= f o r e a c h
    //
            case 64:
            { 
            setSym1(TK_foreach);
          
            }
            break; 
         
    //
    // Rule 65:  KeyWord ::= f u t u r e
    //
            case 65:
            { 
            setSym1(TK_future);
          
            }
            break; 
         
    //
    // Rule 66:  KeyWord ::= h e r e
    //
            case 66:
            { 
            setSym1(TK_here);
          
            }
            break; 
         
    //
    // Rule 67:  KeyWord ::= l o c a l
    //
            case 67:
            { 
            setSym1(TK_local);
          
            }
            break; 
         
    //
    // Rule 68:  KeyWord ::= n e x t
    //
            case 68:
            { 
            setSym1(TK_next);
          
            }
            break; 
         
    //
    // Rule 69:  KeyWord ::= n o w
    //
            case 69:
            { 
            setSym1(TK_now);
          
            }
            break; 
         
    //
    // Rule 70:  KeyWord ::= n u l l a b l e
    //
            case 70:
            { 
            setSym1(TK_nullable);
          
            }
            break; 
         
    //
    // Rule 71:  KeyWord ::= o r
    //
            case 71:
            { 
            setSym1(TK_or);
          
            }
            break; 
         
    //
    // Rule 72:  KeyWord ::= p l a c e
    //
            case 72:
            { 
            setSym1(TK_place);
          
            }
            break; 
         
    //
    // Rule 73:  KeyWord ::= r a n g e
    //
            case 73:
            { 
            setSym1(TK_range);
          
            }
            break; 
         
    //
    // Rule 74:  KeyWord ::= r a n k
    //
            case 74:
            { 
            setSym1(TK_rank);
          
            }
            break; 
         
    //
    // Rule 75:  KeyWord ::= r e f e r e n c e
    //
            case 75:
            { 
            setSym1(TK_reference);
          
            }
            break; 
         
    //
    // Rule 76:  KeyWord ::= r e g i o n
    //
            case 76:
            { 
            setSym1(TK_region);
          
            }
            break; 
         
    //
    // Rule 77:  KeyWord ::= v a l u e
    //
            case 77:
            { 
            setSym1(TK_value);
          
            }
            break; 
         
    //
    // Rule 78:  KeyWord ::= w h e n
    //
            case 78:
            { 
            setSym1(TK_when);
          
            }
            break; 
            
	    default:
	        break;
	}
	return;
    }


// The KeyWord parser
int act, curtok, currentKind;

int processReductions(int act)
{
    do
    {
        stateStackTop -= (rhs(act) - 1);
        ruleAction(act);
        // lexers will reset the stateStack whenever a token has been recognized
        if (stateStackTop == -1) return START_STATE;
        act = ntAction(stack[stateStackTop], lhs(act));
    } while(act <= NUM_RULES);

    return act;
}

//
//
//
public int parseCharacters()
{
    curtok = token();
    act = START_STATE;
    currentKind = kind(curtok);

    //
    // Start parsing.
    //
    resetStateStack();

    ProcessTerminals: for (;;)
    {
        if (++stateStackTop >= stackLength)
            reallocateStacks();

        stack[stateStackTop] = act;

        locationStack[stateStackTop] = curtok;

        act = termAction(act, currentKind);

        if (act <= NUM_RULES)
        {
            stateStackTop--; // make reduction look like a shift-reduce
            act = processReductions(act);
        }
        else if (act > ERROR_ACTION)
        {
            curtok = token();
            currentKind = kind(curtok);

            act = processReductions(act - ERROR_ACTION);
        }
        else if (act < ACCEPT_ACTION)
        {
            curtok = token();
            currentKind = kind(curtok);
        }
        else if (act == ERROR_ACTION)
        {
            return 0;
        }
        else break ProcessTerminals;
    }

    return parseStack[0];
}

// Stacks portion
final static int STACK_INCREMENT = 1024;

int stateStackTop,
    stackLength = 0,
    stack[],
    locationStack[];
int parseStack[];

//
// This method is used in lexers (written as parsers) which recognize single tokens
//
public final void resetStateStack()
{
	stateStackTop = -1;
}

//
// Given a rule of the form     A ::= x1 x2 ... xn     n > 0
//
// the function TOKEN(i) yields the symbol xi, if xi is a terminal
// or ti, if xi is a nonterminal that produced a string of the form
// xi => ti w.
//
public final int getToken(int i)
{
    return locationStack[stateStackTop + (i - 1)];
}

//
// Given a rule of the form     A ::= x1 x2 ... xn     n > 0
//
// The function SYM(i) yields the int subtree associated with symbol
// xi. NOTE that if xi is a terminal, SYM(i) is undefined ! (However,
// see token_action below.)
//
// setSYM1(int ast) is a function that allows us to assign an int
// tree to SYM(1).
//
public final int getSym(int i) { return parseStack[stateStackTop + (i - 1)]; }
public final void setSym1(int n) { parseStack[stateStackTop] = n; }

void reallocateStacks()
{
    int old_stack_length = (stack == null ? 0 : stackLength);
    stackLength += STACK_INCREMENT;

    if (old_stack_length == 0)
    {
        stack = new int[stackLength];
        locationStack = new int[stackLength];
        parseStack = new int[stackLength];
    }
    else
    {
        System.arraycopy(stack, 0, stack = new int[stackLength], 0, old_stack_length);
        System.arraycopy(locationStack, 0, locationStack = new int[stackLength], 0, old_stack_length);
        System.arraycopy(parseStack, 0, parseStack = new int[stackLength], 0, old_stack_length);
    }
    return;
}

public int termAction(int act, int sym)
{
    act = tAction(act, sym);
    if (act > LA_STATE_OFFSET)
    {
        int next_token = peek();
        act = lookAhead(act - LA_STATE_OFFSET, kind(next_token));
        while(act > LA_STATE_OFFSET)
        {
            next_token = next(next_token);
            act = lookAhead(act - LA_STATE_OFFSET, kind(next_token));
        }
    }
    return act;
}

}

