package x10.parser.antlr;

import polyglot.ast.FloatLit;
import polyglot.ast.IntLit;
import polyglot.ast.IntLit.Kind;
import polyglot.ast.NodeFactory;
import polyglot.frontend.FileSource;
import polyglot.lex.BooleanLiteral;
import polyglot.lex.CharacterLiteral;
import polyglot.lex.DoubleLiteral;
import polyglot.lex.FloatLiteral;
import polyglot.lex.StringLiteral;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.Position;
import x10.X10CompilerOptions;
import x10.parser.X10Parsersym;
import x10.parserGen.X10Lexer;
import x10.parserGen.X10Listener;
import x10.parserGen.X10Parser.BooleanLiteralContext;
import x10.parserGen.X10Parser.ByteLiteralContext;
import x10.parserGen.X10Parser.CharacterLiteralContext;
import x10.parserGen.X10Parser.DoubleLiteralContext;
import x10.parserGen.X10Parser.FloatingPointLiteralContext;
import x10.parserGen.X10Parser.IdentifierContext;
import x10.parserGen.X10Parser.IntLiteralContext;
import x10.parserGen.X10Parser.LiteralContext;
import x10.parserGen.X10Parser.LongLiteralContext;
import x10.parserGen.X10Parser.NullLiteralContext;
import x10.parserGen.X10Parser.ShortLiteralContext;
import x10.parserGen.X10Parser.StringLiteralContext;
import x10.parserGen.X10Parser.UnsignedByteLiteralContext;
import x10.parserGen.X10Parser.UnsignedIntLiteralContext;
import x10.parserGen.X10Parser.UnsignedLongLiteralContext;
import x10.parserGen.X10Parser.UnsignedShortLiteralContext;

public class ASTBuilderTop extends ASTBuilder implements X10Listener, polyglot.frontend.Parser {
	public ASTBuilderTop(X10CompilerOptions opts, TypeSystem t, NodeFactory n, FileSource source, ErrorQueue q) {
		super(opts,t,n,source,q);
	}
	
	private long parseLong(String s, int radix, Position pos)
    {
        long x = 0L;

        s = s.toLowerCase();

        boolean reportedError = false;
        for (int i = 0; i < s.length(); i++) {
            int c = s.charAt(i);

            if (c < '0' || c > '9') {
                c = c - 'a' + 10;
            }
            else {
                c = c - '0';
            }

            if (c >= radix) {
                if (!reportedError) {
                    err.syntaxError("Invalid digit: '"+s.charAt(i)+"'",pos);
                    reportedError = true;
                }
            }

            x *= radix;
            x += c;
        }

        return x;
    }
	
	private long parseLong(String s, Position pos)
    {
        int radix;
        int start_index;
        int end_index;

        end_index = s.length();

        boolean isUnsigned = false;
        boolean isLong = true;
        long min = Long.MIN_VALUE;
        while (end_index > 0) {
            char lastCh = s.charAt(end_index - 1);
            if (lastCh == 'u' || lastCh == 'U') isUnsigned = true;
            // todo: long need special treatment cause we have overflows
            // for signed values that start with 0, we need to make them negative if they are above max value
            if (lastCh == 'n' || lastCh == 'N') { isLong = false; min = Integer.MIN_VALUE; }
            if (lastCh == 'y' || lastCh == 'Y') { isLong = false; min = Byte.MIN_VALUE; }
            if (lastCh == 's' || lastCh == 'S') { isLong = false; min = Short.MIN_VALUE; }
            if (lastCh != 'y' && lastCh != 'Y' && lastCh != 's' && lastCh != 'S' && lastCh != 'l' && lastCh != 'L' && lastCh != 'n' && lastCh != 'N' && lastCh != 'u' && lastCh != 'U') {
                break;
            }
            end_index--;
        }
        long max = -min;

        if (s.charAt(0) == '0')
        {
            if (s.length() > 1 && (s.charAt(1) == 'x' || s.charAt(1) == 'X'))
            {
                radix = 16;
                start_index = 2;
            }
            else
            {
                radix = 8;
                start_index = 0;
            }
        }
        else
        {
            radix = 10;
            start_index = 0;
        }

        final long res = parseLong(s.substring(start_index, end_index), radix, pos);
        if (!isUnsigned && !isLong && radix!=10 && res>=max) {
            // need to make this value negative
            // e.g., 0xffUY == 255, 0xffY== 255-256 = -1  , 0xfeYU==254, 0xfeY== 254-256 = -2
            return res+min*2;
        }
        return res;
    }
	
	private polyglot.lex.FloatLiteral float_lit(LiteralContext ctx)
    {
		String s = ctx.getText();
        try {
            int end_index = (s.charAt(s.length() - 1) == 'f' || s.charAt(s.length() - 1) == 'F'
                    ? s.length() - 1
                    : s.length());
            float x = Float.parseFloat(s.substring(0, end_index));
            return new FloatLiteral(pos(ctx), x, X10Parsersym.TK_FloatingPointLiteral); //TODO: check this!!
        }
        catch (NumberFormatException e) {
            //unrecoverableSyntaxError = true;
            eq.enqueue(ErrorInfo.LEXICAL_ERROR,
                    "Illegal float literal \"" + s + "\"", pos(ctx));
            return null;
        }
    }
	
	private polyglot.lex.DoubleLiteral double_lit(LiteralContext ctx)
    {
		String s = ctx.getText();
        try {
            int end_index = (s.charAt(s.length() - 1) == 'd' || s.charAt(s.length() - 1) == 'D'
                    ? s.length() - 1
                    : s.length());
            double x = Double.parseDouble(s.substring(0, end_index));
            return new DoubleLiteral(pos(ctx), x, X10Parsersym.TK_DoubleLiteral); //TODO: Check this!!
        }
        catch (NumberFormatException e) {
            //unrecoverableSyntaxError = true;
            eq.enqueue(ErrorInfo.LEXICAL_ERROR,
                    "Illegal float literal \"" + s + "\"", pos(ctx));
            return null;
        }
    }
	
	private polyglot.lex.BooleanLiteral boolean_lit(LiteralContext ctx)
    {
		 
        return new BooleanLiteral(pos(ctx), ctx.start.getType()==X10Lexer.TRUE, ctx.start.getType());
    }
	
	private polyglot.lex.CharacterLiteral char_lit(LiteralContext ctx)
    {
        char x;
        String s = ctx.getText();
        if (s.charAt(1) == '\\') {
            switch(s.charAt(2)) {
                case 'u':
                    x = (char) parseLong(s.substring(3, s.length() - 1), 16, pos(ctx));
                    break;
                case 'b':
                    x = '\b';
                    break;
                case 't':
                    x = '\t';
                    break;
                case 'n':
                    x = '\n';
                    break;
                case 'f':
                    x = '\f';
                    break;
                case 'r':
                    x = '\r';
                    break;
                case '\"':
                    x = '\"';
                    break;
                case '\'':
                    x = '\'';
                    break;
                case '\\':
                    x = '\\';
                    break;
                default:
                    x = (char) parseLong(s.substring(2, s.length() - 1), 8, pos(ctx));
                    if (x > 255) {
                       // unrecoverableSyntaxError = true;
                        eq.enqueue(ErrorInfo.LEXICAL_ERROR,
                                "Illegal character literal " + s, pos(ctx));
                    }
            }
        }
        else {
            assert(s.length() == 3);
            x = s.charAt(1);
        }

        return new CharacterLiteral(pos(ctx), x, X10Parsersym.TK_CharacterLiteral);
    }
	
	 private polyglot.lex.StringLiteral string_lit(LiteralContext ctx)
	    {
	        String s = ctx.getText();
	        char x[] = new char[s.length()];
	        int j = 1,
	                k = 0;
	        while(j < s.length() - 1) {
	            if (s.charAt(j) != '\\')
	                x[k++] = s.charAt(j++);
	            else {
	                switch(s.charAt(j + 1)) {
	                    case 'u':
	                        x[k++] = (char) parseLong(s.substring(j + 2, j + 6), 16, pos(ctx));
	                        j += 6;
	                        break;
	                    case 'b':
	                        x[k++] = '\b';
	                        j += 2;
	                        break;
	                    case 't':
	                        x[k++] = '\t';
	                        j += 2;
	                        break;
	                    case 'n':
	                        x[k++] = '\n';
	                        j += 2;
	                        break;
	                    case 'f':
	                        x[k++] = '\f';
	                        j += 2;
	                        break;
	                    case 'r':
	                        x[k++] = '\r';
	                        j += 2;
	                        break;
	                    case '\"':
	                        x[k++] = '\"';
	                        j += 2;
	                        break;
	                    case '\'':
	                        x[k++] = '\'';
	                        j += 2;
	                        break;
	                    case '`':
	                        x[k++] = '`';
	                        j += 2;
	                        break;
	                    case '\\':
	                        x[k++] = '\\';
	                        j += 2;
	                        break;
	                    default:
	                    {
	                        int n = j + 1;
	                        for (int l = 0; l < 3 && Character.isDigit(s.charAt(n)); l++)
	                            n++;
	                        char c = (char) parseLong(s.substring(j + 1, n), 8, pos(ctx));
	                        if (c > 255) {
	                            //unrecoverableSyntaxError = true;
	                            eq.enqueue(ErrorInfo.LEXICAL_ERROR,
	                                    "Illegal character (" + s.substring(j, n) + ") in string literal " + s, pos(ctx));
	                        }
	                        x[k++] = c;
	                        j = n;
	                    }
	                }
	            }
	        }

	        return new StringLiteral(pos(ctx), new String(x, 0, k), X10Parsersym.TK_StringLiteral);
	    }
	
	private IntLit getIntLit(LiteralContext ctx, Kind k){
		return nf.IntLit(pos(ctx), k, parseLong(ctx.getText(), pos(ctx)));
	}
	
	@Override
	public void exitIdentifier(IdentifierContext ctx){
		ctx.ast = nf.Id(pos(ctx), ctx.start.getText());
	}
	   
	@Override
	public void exitIntLiteral(IntLiteralContext ctx){
		ctx.ast = getIntLit(ctx, IntLit.INT);
	}
	
	@Override
	public void exitLongLiteral(LongLiteralContext ctx){
		ctx.ast = getIntLit(ctx, IntLit.LONG);
	}
	
	@Override
	public void exitByteLiteral(ByteLiteralContext ctx){
		ctx.ast = getIntLit(ctx, IntLit.BYTE);
	}
	
	@Override
	public void exitUnsignedByteLiteral(UnsignedByteLiteralContext ctx){
		ctx.ast = getIntLit(ctx, IntLit.UBYTE);
	}
	
	@Override
	public void exitShortLiteral(ShortLiteralContext ctx){
		ctx.ast = getIntLit(ctx, IntLit.SHORT);
	}
	
	@Override
	public void exitUnsignedShortLiteral(UnsignedShortLiteralContext ctx){
		ctx.ast = getIntLit(ctx, IntLit.USHORT);
	}

	@Override
	public void exitUnsignedIntLiteral(UnsignedIntLiteralContext ctx){
		ctx.ast = getIntLit(ctx, IntLit.UINT);
	}
	
	@Override
	public void exitUnsignedLongLiteral(UnsignedLongLiteralContext ctx){
		ctx.ast = getIntLit(ctx, IntLit.ULONG);
	}
	
	@Override
	public void exitFloatingPointLiteral(FloatingPointLiteralContext ctx){
		polyglot.lex.FloatLiteral a = float_lit(ctx);
	    ctx.ast = nf.FloatLit(pos(ctx), FloatLit.FLOAT, a.getValue().floatValue());
	}
	
	@Override
	public void exitDoubleLiteral(DoubleLiteralContext ctx){
		polyglot.lex.DoubleLiteral a = double_lit(ctx);
		ctx.ast = nf.FloatLit(pos(ctx), FloatLit.DOUBLE, a.getValue().doubleValue());
	}
	
	@Override
	public void exitBooleanLiteral(BooleanLiteralContext ctx){
		ctx.ast = nf.BooleanLit(pos(ctx), boolean_lit(ctx).getValue().booleanValue());
	}
	
	@Override
	public void exitCharacterLiteral(CharacterLiteralContext ctx){
		ctx.ast = nf.CharLit(pos(ctx), char_lit(ctx).getValue().charValue());
	}
	
	@Override
	public void exitStringLiteral(StringLiteralContext ctx){
		ctx.ast = nf.StringLit(pos(ctx), string_lit(ctx).getValue());
	}
	
	@Override
	public void exitNullLiteral(NullLiteralContext ctx){
		ctx.ast = nf.NullLit(pos(ctx));
	}
	
}
