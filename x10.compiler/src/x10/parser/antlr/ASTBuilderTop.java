package x10.parser.antlr;

import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.FloatLit;
import polyglot.ast.IntLit;
import polyglot.ast.IntLit.Kind;
import polyglot.ast.NodeFactory;
import polyglot.ast.Unary;
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
import x10.parserGen.X10Parser.AssignmentOperator0Context;
import x10.parserGen.X10Parser.AssignmentOperator10Context;
import x10.parserGen.X10Parser.AssignmentOperator11Context;
import x10.parserGen.X10Parser.AssignmentOperator12Context;
import x10.parserGen.X10Parser.AssignmentOperator13Context;
import x10.parserGen.X10Parser.AssignmentOperator14Context;
import x10.parserGen.X10Parser.AssignmentOperator15Context;
import x10.parserGen.X10Parser.AssignmentOperator16Context;
import x10.parserGen.X10Parser.AssignmentOperator17Context;
import x10.parserGen.X10Parser.AssignmentOperator18Context;
import x10.parserGen.X10Parser.AssignmentOperator19Context;
import x10.parserGen.X10Parser.AssignmentOperator1Context;
import x10.parserGen.X10Parser.AssignmentOperator20Context;
import x10.parserGen.X10Parser.AssignmentOperator2Context;
import x10.parserGen.X10Parser.AssignmentOperator3Context;
import x10.parserGen.X10Parser.AssignmentOperator4Context;
import x10.parserGen.X10Parser.AssignmentOperator5Context;
import x10.parserGen.X10Parser.AssignmentOperator6Context;
import x10.parserGen.X10Parser.AssignmentOperator7Context;
import x10.parserGen.X10Parser.AssignmentOperator8Context;
import x10.parserGen.X10Parser.AssignmentOperator9Context;
import x10.parserGen.X10Parser.BinOp0Context;
import x10.parserGen.X10Parser.BinOp10Context;
import x10.parserGen.X10Parser.BinOp11Context;
import x10.parserGen.X10Parser.BinOp12Context;
import x10.parserGen.X10Parser.BinOp13Context;
import x10.parserGen.X10Parser.BinOp14Context;
import x10.parserGen.X10Parser.BinOp15Context;
import x10.parserGen.X10Parser.BinOp16Context;
import x10.parserGen.X10Parser.BinOp17Context;
import x10.parserGen.X10Parser.BinOp18Context;
import x10.parserGen.X10Parser.BinOp19Context;
import x10.parserGen.X10Parser.BinOp1Context;
import x10.parserGen.X10Parser.BinOp20Context;
import x10.parserGen.X10Parser.BinOp21Context;
import x10.parserGen.X10Parser.BinOp22Context;
import x10.parserGen.X10Parser.BinOp23Context;
import x10.parserGen.X10Parser.BinOp24Context;
import x10.parserGen.X10Parser.BinOp25Context;
import x10.parserGen.X10Parser.BinOp26Context;
import x10.parserGen.X10Parser.BinOp27Context;
import x10.parserGen.X10Parser.BinOp28Context;
import x10.parserGen.X10Parser.BinOp29Context;
import x10.parserGen.X10Parser.BinOp2Context;
import x10.parserGen.X10Parser.BinOp3Context;
import x10.parserGen.X10Parser.BinOp4Context;
import x10.parserGen.X10Parser.BinOp5Context;
import x10.parserGen.X10Parser.BinOp6Context;
import x10.parserGen.X10Parser.BinOp7Context;
import x10.parserGen.X10Parser.BinOp8Context;
import x10.parserGen.X10Parser.BinOp9Context;
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
import x10.parserGen.X10Parser.PrefixOp0Context;
import x10.parserGen.X10Parser.PrefixOp1Context;
import x10.parserGen.X10Parser.PrefixOp2Context;
import x10.parserGen.X10Parser.PrefixOp3Context;
import x10.parserGen.X10Parser.PrefixOp4Context;
import x10.parserGen.X10Parser.PrefixOp5Context;
import x10.parserGen.X10Parser.PrefixOp6Context;
import x10.parserGen.X10Parser.PrefixOp7Context;
import x10.parserGen.X10Parser.PrefixOp8Context;
import x10.parserGen.X10Parser.PrefixOp9Context;
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
	
	@Override
	public void exitAssignmentOperator0(AssignmentOperator0Context ctx){
		ctx.ast = Assign.ASSIGN;
	}
	
	@Override
	public void exitAssignmentOperator1(AssignmentOperator1Context ctx){
		ctx.ast = Assign.MUL_ASSIGN;
	}
	
	@Override
	public void exitAssignmentOperator2(AssignmentOperator2Context ctx){
		ctx.ast = Assign.DIV_ASSIGN;
	}
	
	@Override
	public void exitAssignmentOperator3(AssignmentOperator3Context ctx){
		ctx.ast = Assign.MOD_ASSIGN;
	}
	
	@Override
	public void exitAssignmentOperator4(AssignmentOperator4Context ctx){
		ctx.ast = Assign.ADD_ASSIGN;
	}
	
	@Override
	public void exitAssignmentOperator5(AssignmentOperator5Context ctx){
		ctx.ast = Assign.SUB_ASSIGN;
	}
	
	@Override
	public void exitAssignmentOperator6(AssignmentOperator6Context ctx){
		ctx.ast = Assign.SHL_ASSIGN;
	}
	
	@Override
	public void exitAssignmentOperator7(AssignmentOperator7Context ctx){
		ctx.ast = Assign.SHR_ASSIGN;
	}
	
	@Override
	public void exitAssignmentOperator8(AssignmentOperator8Context ctx){
		ctx.ast = Assign.USHR_ASSIGN;
	}
	
	@Override
	public void exitAssignmentOperator9(AssignmentOperator9Context ctx){
		ctx.ast = Assign.BIT_AND_ASSIGN;
	}
	
	@Override
	public void exitAssignmentOperator10(AssignmentOperator10Context ctx){
		ctx.ast = Assign.BIT_XOR_ASSIGN;
	}
	
	@Override
	public void exitAssignmentOperator11(AssignmentOperator11Context ctx){
		ctx.ast = Assign.BIT_OR_ASSIGN;
	}
	
	@Override
	public void exitAssignmentOperator12(AssignmentOperator12Context ctx){
		ctx.ast = Assign.DOT_DOT_ASSIGN;
	}
	
	@Override
	public void exitAssignmentOperator13(AssignmentOperator13Context ctx){
		ctx.ast = Assign.ARROW_ASSIGN;
	}
	
	@Override
	public void exitAssignmentOperator14(AssignmentOperator14Context ctx){
		ctx.ast = Assign.LARROW_ASSIGN;
	}
	
	@Override
	public void exitAssignmentOperator15(AssignmentOperator15Context ctx){
		ctx.ast = Assign.FUNNEL_ASSIGN;
	}
	
	@Override
	public void exitAssignmentOperator16(AssignmentOperator16Context ctx){
		ctx.ast = Assign.LFUNNEL_ASSIGN;
	}
	
	@Override
	public void exitAssignmentOperator17(AssignmentOperator17Context ctx){
		ctx.ast = Assign.STARSTAR_ASSIGN;
	}
	
	@Override
	public void exitAssignmentOperator18(AssignmentOperator18Context ctx){
		ctx.ast = Assign.DIAMOND_ASSIGN;
	}
	
	@Override
	public void exitAssignmentOperator19(AssignmentOperator19Context ctx){
		ctx.ast = Assign.BOWTIE_ASSIGN;
	}
	
	@Override
	public void exitAssignmentOperator20(AssignmentOperator20Context ctx){
		ctx.ast = Assign.TWIDDLE_ASSIGN;
	}
	
	@Override
	public void exitPrefixOp0(PrefixOp0Context ctx){
		ctx.ast = Unary.POS;
	}
	
	@Override
	public void exitPrefixOp1(PrefixOp1Context ctx){
		ctx.ast = Unary.NEG;
	}
	
	@Override
	public void exitPrefixOp2(PrefixOp2Context ctx){
		ctx.ast = Unary.NOT;
	}
	
	@Override
	public void exitPrefixOp3(PrefixOp3Context ctx){
		ctx.ast = Unary.BIT_NOT;
	}
	
	@Override
	public void exitPrefixOp4(PrefixOp4Context ctx){
		ctx.ast = Unary.CARET;
	}
	
	@Override
	public void exitPrefixOp5(PrefixOp5Context ctx){
		ctx.ast = Unary.BAR;
	}
	
	@Override
	public void exitPrefixOp6(PrefixOp6Context ctx){
		ctx.ast = Unary.AMPERSAND;
	}
	
	@Override
	public void exitPrefixOp7(PrefixOp7Context ctx){
		ctx.ast = Unary.STAR;
	}
	
	@Override
	public void exitPrefixOp8(PrefixOp8Context ctx){
		ctx.ast = Unary.SLASH;
	}
	
	@Override
	public void exitPrefixOp9(PrefixOp9Context ctx){
		ctx.ast = Unary.PERCENT;
	}
	
	@Override
	public void exitBinOp0(BinOp0Context ctx){
		ctx.ast = Binary.ADD;
	}
	
	@Override
	public void exitBinOp1(BinOp1Context ctx){
		ctx.ast = Binary.SUB;
	}
	
	@Override
	public void exitBinOp2(BinOp2Context ctx){
		ctx.ast = Binary.MUL;
	}
	
	@Override
	public void exitBinOp3(BinOp3Context ctx){
		ctx.ast = Binary.DIV;
	}
	
	@Override
	public void exitBinOp4(BinOp4Context ctx){
		ctx.ast = Binary.MOD;
	}
	
	@Override
	public void exitBinOp5(BinOp5Context ctx){
		ctx.ast = Binary.BIT_AND;
	}
	
	@Override
	public void exitBinOp6(BinOp6Context ctx){
		ctx.ast = Binary.BIT_OR;
	}
	
	@Override
	public void exitBinOp7(BinOp7Context ctx){
		ctx.ast = Binary.BIT_XOR;
	}
	
	@Override
	public void exitBinOp8(BinOp8Context ctx){
		ctx.ast = Binary.COND_AND;
	}
	
	@Override
	public void exitBinOp9(BinOp9Context ctx){
		ctx.ast = Binary.COND_OR;
	}
	
	@Override
	public void exitBinOp10(BinOp10Context ctx){
		ctx.ast = Binary.SHL;
	}
	
	@Override
	public void exitBinOp11(BinOp11Context ctx){
		ctx.ast = Binary.SHR;
	}
	
	@Override
	public void exitBinOp12(BinOp12Context ctx){
		ctx.ast = Binary.USHR;
	}
	
	@Override
	public void exitBinOp13(BinOp13Context ctx){
		ctx.ast = Binary.GE;
	}
	
	@Override
	public void exitBinOp14(BinOp14Context ctx){
		ctx.ast = Binary.LE;
	}
	
	@Override
	public void exitBinOp15(BinOp15Context ctx){
		ctx.ast = Binary.GT;
	}
	
	@Override
	public void exitBinOp16(BinOp16Context ctx){
		ctx.ast = Binary.LT;
	}
	
	@Override
	public void exitBinOp17(BinOp17Context ctx){
		ctx.ast = Binary.EQ;
	}
	
	@Override
	public void exitBinOp18(BinOp18Context ctx){
		ctx.ast = Binary.NE;
	}
	
	@Override
	public void exitBinOp19(BinOp19Context ctx){
		ctx.ast = Binary.DOT_DOT;
	}
	
	@Override
	public void exitBinOp20(BinOp20Context ctx){
		ctx.ast = Binary.ARROW;
	}
	
	@Override
	public void exitBinOp21(BinOp21Context ctx){
		ctx.ast = Binary.LARROW;
	}
	
	@Override
	public void exitBinOp22(BinOp22Context ctx){
		ctx.ast = Binary.FUNNEL;
	}
	
	@Override
	public void exitBinOp23(BinOp23Context ctx){
		ctx.ast = Binary.LFUNNEL;
	}
	
	@Override
	public void exitBinOp24(BinOp24Context ctx){
		ctx.ast = Binary.STARSTAR;
	}
	
	@Override
	public void exitBinOp25(BinOp25Context ctx){
		ctx.ast = Binary.TWIDDLE;
	}
	
	@Override
	public void exitBinOp26(BinOp26Context ctx){
		ctx.ast = Binary.NTWIDDLE;
	}
	
	@Override
	public void exitBinOp27(BinOp27Context ctx){
		ctx.ast = Binary.BANG;
	}
	
	@Override
	public void exitBinOp28(BinOp28Context ctx){
		ctx.ast = Binary.DIAMOND;
	}
	
	@Override
	public void exitBinOp29(BinOp29Context ctx){
		ctx.ast = Binary.BOWTIE;
	}

	
}
