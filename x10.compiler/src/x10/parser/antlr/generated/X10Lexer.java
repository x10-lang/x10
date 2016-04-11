// Generated from /Users/lmandel/x10/x10-dsl/x10.compiler/src/x10/parser/antlr/X10.g4 by ANTLR 4.5

  package x10.parser.antlr.generated;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class X10Lexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		MINUS_MINUS=1, OR=2, MINUS=3, MINUS_EQUAL=4, NOT=5, NOT_EQUAL=6, REMAINDER=7, 
		REMAINDER_EQUAL=8, AND=9, AND_AND=10, AND_EQUAL=11, LPAREN=12, RPAREN=13, 
		MULTIPLY=14, MULTIPLY_EQUAL=15, COMMA=16, DOT=17, DIVIDE=18, DIVIDE_EQUAL=19, 
		COLON=20, SEMICOLON=21, QUESTION=22, ATsymbol=23, LBRACKET=24, RBRACKET=25, 
		XOR=26, XOR_EQUAL=27, LBRACE=28, OR_OR=29, OR_EQUAL=30, RBRACE=31, TWIDDLE=32, 
		PLUS=33, PLUS_PLUS=34, PLUS_EQUAL=35, LESS=36, LEFT_SHIFT=37, LEFT_SHIFT_EQUAL=38, 
		RIGHT_SHIFT=39, RIGHT_SHIFT_EQUAL=40, UNSIGNED_RIGHT_SHIFT=41, UNSIGNED_RIGHT_SHIFT_EQUAL=42, 
		LESS_EQUAL=43, EQUAL=44, EQUAL_EQUAL=45, GREATER=46, GREATER_EQUAL=47, 
		ELLIPSIS=48, RANGE=49, ARROW=50, DARROW=51, SUBTYPE=52, SUPERTYPE=53, 
		STARSTAR=54, NTWIDDLE=55, LARROW=56, FUNNEL=57, LFUNNEL=58, DIAMOND=59, 
		BOWTIE=60, RANGE_EQUAL=61, ARROW_EQUAL=62, STARSTAR_EQUAL=63, TWIDDLE_EQUAL=64, 
		LARROW_EQUAL=65, FUNNEL_EQUAL=66, LFUNNEL_EQUAL=67, DIAMOND_EQUAL=68, 
		BOWTIE_EQUAL=69, ABSTRACT=70, AS=71, ASSERT=72, ASYNC=73, AT=74, ATHOME=75, 
		ATEACH=76, ATOMIC=77, BREAK=78, CASE=79, CATCH=80, CLASS=81, CLOCKED=82, 
		CONTINUE=83, DEF=84, DEFAULT=85, DO=86, ELSE=87, EXTENDS=88, FALSE=89, 
		FINAL=90, FINALLY=91, FINISH=92, FOR=93, GOTO=94, HASZERO=95, HERE=96, 
		IF=97, IMPLEMENTS=98, IMPORT=99, IN=100, INSTANCEOF=101, INTERFACE=102, 
		ISREF=103, NATIVE=104, NEW=105, NULL=106, OFFER=107, OFFERS=108, OPERATOR=109, 
		PACKAGE=110, PRIVATE=111, PROPERTY=112, PROTECTED=113, PUBLIC=114, RETURN=115, 
		SELF=116, STATIC=117, STRUCT=118, SUPER=119, SWITCH=120, THIS=121, THROW=122, 
		THROWS=123, TRANSIENT=124, TRUE=125, TRY=126, TYPE=127, VAL=128, VAR=129, 
		VOID=130, WHEN=131, WHILE=132, IDENTIFIER=133, IntLiteral=134, LongLiteral=135, 
		ByteLiteral=136, ShortLiteral=137, UnsignedIntLiteral=138, UnsignedLongLiteral=139, 
		UnsignedByteLiteral=140, UnsignedShortLiteral=141, FloatingPointLiteral=142, 
		DoubleLiteral=143, CharacterLiteral=144, StringLiteral=145, WS=146, DOCCOMMENT=147, 
		COMMENT=148, LINE_COMMENT=149;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"MINUS_MINUS", "OR", "MINUS", "MINUS_EQUAL", "NOT", "NOT_EQUAL", "REMAINDER", 
		"REMAINDER_EQUAL", "AND", "AND_AND", "AND_EQUAL", "LPAREN", "RPAREN", 
		"MULTIPLY", "MULTIPLY_EQUAL", "COMMA", "DOT", "DIVIDE", "DIVIDE_EQUAL", 
		"COLON", "SEMICOLON", "QUESTION", "ATsymbol", "LBRACKET", "RBRACKET", 
		"XOR", "XOR_EQUAL", "LBRACE", "OR_OR", "OR_EQUAL", "RBRACE", "TWIDDLE", 
		"PLUS", "PLUS_PLUS", "PLUS_EQUAL", "LESS", "LEFT_SHIFT", "LEFT_SHIFT_EQUAL", 
		"RIGHT_SHIFT", "RIGHT_SHIFT_EQUAL", "UNSIGNED_RIGHT_SHIFT", "UNSIGNED_RIGHT_SHIFT_EQUAL", 
		"LESS_EQUAL", "EQUAL", "EQUAL_EQUAL", "GREATER", "GREATER_EQUAL", "ELLIPSIS", 
		"RANGE", "ARROW", "DARROW", "SUBTYPE", "SUPERTYPE", "STARSTAR", "NTWIDDLE", 
		"LARROW", "FUNNEL", "LFUNNEL", "DIAMOND", "BOWTIE", "RANGE_EQUAL", "ARROW_EQUAL", 
		"STARSTAR_EQUAL", "TWIDDLE_EQUAL", "LARROW_EQUAL", "FUNNEL_EQUAL", "LFUNNEL_EQUAL", 
		"DIAMOND_EQUAL", "BOWTIE_EQUAL", "ABSTRACT", "AS", "ASSERT", "ASYNC", 
		"AT", "ATHOME", "ATEACH", "ATOMIC", "BREAK", "CASE", "CATCH", "CLASS", 
		"CLOCKED", "CONTINUE", "DEF", "DEFAULT", "DO", "ELSE", "EXTENDS", "FALSE", 
		"FINAL", "FINALLY", "FINISH", "FOR", "GOTO", "HASZERO", "HERE", "IF", 
		"IMPLEMENTS", "IMPORT", "IN", "INSTANCEOF", "INTERFACE", "ISREF", "NATIVE", 
		"NEW", "NULL", "OFFER", "OFFERS", "OPERATOR", "PACKAGE", "PRIVATE", "PROPERTY", 
		"PROTECTED", "PUBLIC", "RETURN", "SELF", "STATIC", "STRUCT", "SUPER", 
		"SWITCH", "THIS", "THROW", "THROWS", "TRANSIENT", "TRUE", "TRY", "TYPE", 
		"VAL", "VAR", "VOID", "WHEN", "WHILE", "IDENTIFIER", "Letter", "LetterOrDigit", 
		"NotBQ", "IntLiteral", "LongLiteral", "ByteLiteral", "ShortLiteral", "UnsignedIntLiteral", 
		"UnsignedLongLiteral", "UnsignedByteLiteral", "UnsignedShortLiteral", 
		"FloatingPointLiteral", "ExponentPart", "FloatingTypeSuffix", "DoubleLiteral", 
		"DoubleTypeSuffix", "IntegerLiteral", "DecimalNumeral", "Digits", "Digit", 
		"NonZeroDigit", "DigitOrUnderscore", "Underscores", "HexNumeral", "HexDigits", 
		"HexDigit", "HexDigitOrUnderscore", "OctalNumeral", "OctalDigits", "OctalDigit", 
		"OctalDigitOrUnderscore", "BinaryNumeral", "BinaryDigits", "BinaryDigit", 
		"BinaryDigitOrUnderscore", "CharacterLiteral", "NotQ", "EscapeSequence", 
		"StringLiteral", "StringCharacters", "StringCharacter", "OctalEscape", 
		"UnicodeEscape", "ZeroToThree", "WS", "DOCCOMMENT", "COMMENT", "LINE_COMMENT"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'--'", "'|'", "'-'", "'-='", "'!'", "'!='", "'%'", "'%='", "'&'", 
		"'&&'", "'&='", "'('", "')'", "'*'", "'*='", "','", "'.'", "'/'", "'/='", 
		"':'", "';'", "'?'", "'@'", "'['", "']'", "'^'", "'^='", "'{'", "'||'", 
		"'|='", "'}'", "'~'", "'+'", "'++'", "'+='", "'<'", "'<<'", "'<<='", "'>>'", 
		"'>>='", "'>>>'", "'>>>='", "'<='", "'='", "'=='", "'>'", "'>='", "'...'", 
		"'..'", "'->'", "'=>'", "'<:'", "':>'", "'**'", "'!~'", "'<-'", "'-<'", 
		"'>-'", "'<>'", "'><'", "'..='", "'->='", "'**='", "'~='", "'<-='", "'-<='", 
		"'>-='", "'<>='", "'><='", "'abstract'", "'as'", "'assert'", "'async'", 
		"'at'", "'athome'", "'ateach'", "'atomic'", "'break'", "'case'", "'catch'", 
		"'class'", "'clocked'", "'continue'", "'def'", "'default'", "'do'", "'else'", 
		"'extends'", "'false'", "'final'", "'finally'", "'finish'", "'for'", "'goto'", 
		"'haszero'", "'here'", "'if'", "'implements'", "'import'", "'in'", "'instanceof'", 
		"'interface'", "'isref'", "'native'", "'new'", "'null'", "'offer'", "'offers'", 
		"'operator'", "'package'", "'private'", "'property'", "'protected'", "'public'", 
		"'return'", "'self'", "'static'", "'struct'", "'super'", "'switch'", "'this'", 
		"'throw'", "'throws'", "'transient'", "'true'", "'try'", "'type'", "'val'", 
		"'var'", "'void'", "'when'", "'while'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "MINUS_MINUS", "OR", "MINUS", "MINUS_EQUAL", "NOT", "NOT_EQUAL", 
		"REMAINDER", "REMAINDER_EQUAL", "AND", "AND_AND", "AND_EQUAL", "LPAREN", 
		"RPAREN", "MULTIPLY", "MULTIPLY_EQUAL", "COMMA", "DOT", "DIVIDE", "DIVIDE_EQUAL", 
		"COLON", "SEMICOLON", "QUESTION", "ATsymbol", "LBRACKET", "RBRACKET", 
		"XOR", "XOR_EQUAL", "LBRACE", "OR_OR", "OR_EQUAL", "RBRACE", "TWIDDLE", 
		"PLUS", "PLUS_PLUS", "PLUS_EQUAL", "LESS", "LEFT_SHIFT", "LEFT_SHIFT_EQUAL", 
		"RIGHT_SHIFT", "RIGHT_SHIFT_EQUAL", "UNSIGNED_RIGHT_SHIFT", "UNSIGNED_RIGHT_SHIFT_EQUAL", 
		"LESS_EQUAL", "EQUAL", "EQUAL_EQUAL", "GREATER", "GREATER_EQUAL", "ELLIPSIS", 
		"RANGE", "ARROW", "DARROW", "SUBTYPE", "SUPERTYPE", "STARSTAR", "NTWIDDLE", 
		"LARROW", "FUNNEL", "LFUNNEL", "DIAMOND", "BOWTIE", "RANGE_EQUAL", "ARROW_EQUAL", 
		"STARSTAR_EQUAL", "TWIDDLE_EQUAL", "LARROW_EQUAL", "FUNNEL_EQUAL", "LFUNNEL_EQUAL", 
		"DIAMOND_EQUAL", "BOWTIE_EQUAL", "ABSTRACT", "AS", "ASSERT", "ASYNC", 
		"AT", "ATHOME", "ATEACH", "ATOMIC", "BREAK", "CASE", "CATCH", "CLASS", 
		"CLOCKED", "CONTINUE", "DEF", "DEFAULT", "DO", "ELSE", "EXTENDS", "FALSE", 
		"FINAL", "FINALLY", "FINISH", "FOR", "GOTO", "HASZERO", "HERE", "IF", 
		"IMPLEMENTS", "IMPORT", "IN", "INSTANCEOF", "INTERFACE", "ISREF", "NATIVE", 
		"NEW", "NULL", "OFFER", "OFFERS", "OPERATOR", "PACKAGE", "PRIVATE", "PROPERTY", 
		"PROTECTED", "PUBLIC", "RETURN", "SELF", "STATIC", "STRUCT", "SUPER", 
		"SWITCH", "THIS", "THROW", "THROWS", "TRANSIENT", "TRUE", "TRY", "TYPE", 
		"VAL", "VAR", "VOID", "WHEN", "WHILE", "IDENTIFIER", "IntLiteral", "LongLiteral", 
		"ByteLiteral", "ShortLiteral", "UnsignedIntLiteral", "UnsignedLongLiteral", 
		"UnsignedByteLiteral", "UnsignedShortLiteral", "FloatingPointLiteral", 
		"DoubleLiteral", "CharacterLiteral", "StringLiteral", "WS", "DOCCOMMENT", 
		"COMMENT", "LINE_COMMENT"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}



	  /** Hidden channel for white spaces */
	  public static final int WHITESPACES = 1;
	  /** Hidden channel for X10Doc comments */
	  public static final int DOCCOMMENTS = 2;
	  /** Hidden channel for simple comments */
	  public static final int COMMENTS = 3;

	  boolean isDecimal() {
	      int next = _input.LA(1);
	      return (next != '.') &&
	          !('a' <= next && next <= 'z') &&
	          !('A' <= next && next <= 'Z') &&
	          (next != '_') && (next != '`');
	      // return ('0' <= next && next <= '9') ||
	      //     next == ' ' || next == '\t' || next == '\r' || next == '\n' || next == '\u000C';
	  }


	public X10Lexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "X10.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 147:
			return DoubleLiteral_sempred((RuleContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean DoubleLiteral_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return isDecimal();
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\u0097\u0528\b\1\4"+
		"\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n"+
		"\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64"+
		"\t\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t"+
		"=\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4"+
		"I\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\t"+
		"T\4U\tU\4V\tV\4W\tW\4X\tX\4Y\tY\4Z\tZ\4[\t[\4\\\t\\\4]\t]\4^\t^\4_\t_"+
		"\4`\t`\4a\ta\4b\tb\4c\tc\4d\td\4e\te\4f\tf\4g\tg\4h\th\4i\ti\4j\tj\4k"+
		"\tk\4l\tl\4m\tm\4n\tn\4o\to\4p\tp\4q\tq\4r\tr\4s\ts\4t\tt\4u\tu\4v\tv"+
		"\4w\tw\4x\tx\4y\ty\4z\tz\4{\t{\4|\t|\4}\t}\4~\t~\4\177\t\177\4\u0080\t"+
		"\u0080\4\u0081\t\u0081\4\u0082\t\u0082\4\u0083\t\u0083\4\u0084\t\u0084"+
		"\4\u0085\t\u0085\4\u0086\t\u0086\4\u0087\t\u0087\4\u0088\t\u0088\4\u0089"+
		"\t\u0089\4\u008a\t\u008a\4\u008b\t\u008b\4\u008c\t\u008c\4\u008d\t\u008d"+
		"\4\u008e\t\u008e\4\u008f\t\u008f\4\u0090\t\u0090\4\u0091\t\u0091\4\u0092"+
		"\t\u0092\4\u0093\t\u0093\4\u0094\t\u0094\4\u0095\t\u0095\4\u0096\t\u0096"+
		"\4\u0097\t\u0097\4\u0098\t\u0098\4\u0099\t\u0099\4\u009a\t\u009a\4\u009b"+
		"\t\u009b\4\u009c\t\u009c\4\u009d\t\u009d\4\u009e\t\u009e\4\u009f\t\u009f"+
		"\4\u00a0\t\u00a0\4\u00a1\t\u00a1\4\u00a2\t\u00a2\4\u00a3\t\u00a3\4\u00a4"+
		"\t\u00a4\4\u00a5\t\u00a5\4\u00a6\t\u00a6\4\u00a7\t\u00a7\4\u00a8\t\u00a8"+
		"\4\u00a9\t\u00a9\4\u00aa\t\u00aa\4\u00ab\t\u00ab\4\u00ac\t\u00ac\4\u00ad"+
		"\t\u00ad\4\u00ae\t\u00ae\4\u00af\t\u00af\4\u00b0\t\u00b0\4\u00b1\t\u00b1"+
		"\4\u00b2\t\u00b2\4\u00b3\t\u00b3\4\u00b4\t\u00b4\4\u00b5\t\u00b5\4\u00b6"+
		"\t\u00b6\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3"+
		"\b\3\b\3\t\3\t\3\t\3\n\3\n\3\13\3\13\3\13\3\f\3\f\3\f\3\r\3\r\3\16\3\16"+
		"\3\17\3\17\3\20\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\24"+
		"\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33"+
		"\3\34\3\34\3\34\3\35\3\35\3\36\3\36\3\36\3\37\3\37\3\37\3 \3 \3!\3!\3"+
		"\"\3\"\3#\3#\3#\3$\3$\3$\3%\3%\3&\3&\3&\3\'\3\'\3\'\3\'\3(\3(\3(\3)\3"+
		")\3)\3)\3*\3*\3*\3*\3+\3+\3+\3+\3+\3,\3,\3,\3-\3-\3.\3.\3.\3/\3/\3\60"+
		"\3\60\3\60\3\61\3\61\3\61\3\61\3\62\3\62\3\62\3\63\3\63\3\63\3\64\3\64"+
		"\3\64\3\65\3\65\3\65\3\66\3\66\3\66\3\67\3\67\3\67\38\38\38\39\39\39\3"+
		":\3:\3:\3;\3;\3;\3<\3<\3<\3=\3=\3=\3>\3>\3>\3>\3?\3?\3?\3?\3@\3@\3@\3"+
		"@\3A\3A\3A\3B\3B\3B\3B\3C\3C\3C\3C\3D\3D\3D\3D\3E\3E\3E\3E\3F\3F\3F\3"+
		"F\3G\3G\3G\3G\3G\3G\3G\3G\3G\3H\3H\3H\3I\3I\3I\3I\3I\3I\3I\3J\3J\3J\3"+
		"J\3J\3J\3K\3K\3K\3L\3L\3L\3L\3L\3L\3L\3M\3M\3M\3M\3M\3M\3M\3N\3N\3N\3"+
		"N\3N\3N\3N\3O\3O\3O\3O\3O\3O\3P\3P\3P\3P\3P\3Q\3Q\3Q\3Q\3Q\3Q\3R\3R\3"+
		"R\3R\3R\3R\3S\3S\3S\3S\3S\3S\3S\3S\3T\3T\3T\3T\3T\3T\3T\3T\3T\3U\3U\3"+
		"U\3U\3V\3V\3V\3V\3V\3V\3V\3V\3W\3W\3W\3X\3X\3X\3X\3X\3Y\3Y\3Y\3Y\3Y\3"+
		"Y\3Y\3Y\3Z\3Z\3Z\3Z\3Z\3Z\3[\3[\3[\3[\3[\3[\3\\\3\\\3\\\3\\\3\\\3\\\3"+
		"\\\3\\\3]\3]\3]\3]\3]\3]\3]\3^\3^\3^\3^\3_\3_\3_\3_\3_\3`\3`\3`\3`\3`"+
		"\3`\3`\3`\3a\3a\3a\3a\3a\3b\3b\3b\3c\3c\3c\3c\3c\3c\3c\3c\3c\3c\3c\3d"+
		"\3d\3d\3d\3d\3d\3d\3e\3e\3e\3f\3f\3f\3f\3f\3f\3f\3f\3f\3f\3f\3g\3g\3g"+
		"\3g\3g\3g\3g\3g\3g\3g\3h\3h\3h\3h\3h\3h\3i\3i\3i\3i\3i\3i\3i\3j\3j\3j"+
		"\3j\3k\3k\3k\3k\3k\3l\3l\3l\3l\3l\3l\3m\3m\3m\3m\3m\3m\3m\3n\3n\3n\3n"+
		"\3n\3n\3n\3n\3n\3o\3o\3o\3o\3o\3o\3o\3o\3p\3p\3p\3p\3p\3p\3p\3p\3q\3q"+
		"\3q\3q\3q\3q\3q\3q\3q\3r\3r\3r\3r\3r\3r\3r\3r\3r\3r\3s\3s\3s\3s\3s\3s"+
		"\3s\3t\3t\3t\3t\3t\3t\3t\3u\3u\3u\3u\3u\3v\3v\3v\3v\3v\3v\3v\3w\3w\3w"+
		"\3w\3w\3w\3w\3x\3x\3x\3x\3x\3x\3y\3y\3y\3y\3y\3y\3y\3z\3z\3z\3z\3z\3{"+
		"\3{\3{\3{\3{\3{\3|\3|\3|\3|\3|\3|\3|\3}\3}\3}\3}\3}\3}\3}\3}\3}\3}\3~"+
		"\3~\3~\3~\3~\3\177\3\177\3\177\3\177\3\u0080\3\u0080\3\u0080\3\u0080\3"+
		"\u0080\3\u0081\3\u0081\3\u0081\3\u0081\3\u0082\3\u0082\3\u0082\3\u0082"+
		"\3\u0083\3\u0083\3\u0083\3\u0083\3\u0083\3\u0084\3\u0084\3\u0084\3\u0084"+
		"\3\u0084\3\u0085\3\u0085\3\u0085\3\u0085\3\u0085\3\u0085\3\u0086\3\u0086"+
		"\7\u0086\u03c6\n\u0086\f\u0086\16\u0086\u03c9\13\u0086\3\u0086\3\u0086"+
		"\7\u0086\u03cd\n\u0086\f\u0086\16\u0086\u03d0\13\u0086\3\u0086\5\u0086"+
		"\u03d3\n\u0086\3\u0087\5\u0087\u03d6\n\u0087\3\u0088\5\u0088\u03d9\n\u0088"+
		"\3\u0089\3\u0089\3\u0089\3\u0089\5\u0089\u03df\n\u0089\3\u008a\3\u008a"+
		"\3\u008a\3\u008b\3\u008b\5\u008b\u03e6\n\u008b\3\u008c\3\u008c\3\u008c"+
		"\3\u008d\3\u008d\3\u008d\3\u008e\3\u008e\3\u008e\3\u008e\3\u008e\5\u008e"+
		"\u03f3\n\u008e\3\u008f\3\u008f\3\u008f\5\u008f\u03f8\n\u008f\3\u008f\3"+
		"\u008f\5\u008f\u03fc\n\u008f\3\u0090\3\u0090\3\u0090\3\u0090\3\u0090\5"+
		"\u0090\u0403\n\u0090\3\u0091\3\u0091\3\u0091\3\u0091\3\u0091\5\u0091\u040a"+
		"\n\u0091\3\u0092\3\u0092\3\u0092\5\u0092\u040f\n\u0092\3\u0092\5\u0092"+
		"\u0412\n\u0092\3\u0092\3\u0092\3\u0092\3\u0092\3\u0092\5\u0092\u0419\n"+
		"\u0092\3\u0092\3\u0092\3\u0092\3\u0092\3\u0092\3\u0092\3\u0092\3\u0092"+
		"\5\u0092\u0423\n\u0092\3\u0092\3\u0092\5\u0092\u0427\n\u0092\3\u0093\3"+
		"\u0093\5\u0093\u042b\n\u0093\3\u0093\3\u0093\3\u0094\3\u0094\3\u0095\3"+
		"\u0095\3\u0095\5\u0095\u0434\n\u0095\3\u0095\3\u0095\5\u0095\u0438\n\u0095"+
		"\3\u0095\3\u0095\3\u0095\5\u0095\u043d\n\u0095\3\u0095\3\u0095\3\u0095"+
		"\3\u0095\3\u0095\3\u0095\5\u0095\u0445\n\u0095\3\u0095\3\u0095\3\u0095"+
		"\5\u0095\u044a\n\u0095\3\u0095\5\u0095\u044d\n\u0095\3\u0095\3\u0095\3"+
		"\u0095\5\u0095\u0452\n\u0095\3\u0095\3\u0095\5\u0095\u0456\n\u0095\3\u0095"+
		"\3\u0095\5\u0095\u045a\n\u0095\3\u0096\3\u0096\3\u0097\3\u0097\3\u0097"+
		"\3\u0097\5\u0097\u0462\n\u0097\3\u0098\3\u0098\3\u0098\5\u0098\u0467\n"+
		"\u0098\3\u0098\3\u0098\3\u0098\5\u0098\u046c\n\u0098\5\u0098\u046e\n\u0098"+
		"\3\u0099\3\u0099\7\u0099\u0472\n\u0099\f\u0099\16\u0099\u0475\13\u0099"+
		"\3\u0099\5\u0099\u0478\n\u0099\3\u009a\3\u009a\5\u009a\u047c\n\u009a\3"+
		"\u009b\3\u009b\3\u009c\3\u009c\5\u009c\u0482\n\u009c\3\u009d\6\u009d\u0485"+
		"\n\u009d\r\u009d\16\u009d\u0486\3\u009e\3\u009e\3\u009e\3\u009e\3\u009f"+
		"\3\u009f\7\u009f\u048f\n\u009f\f\u009f\16\u009f\u0492\13\u009f\3\u009f"+
		"\5\u009f\u0495\n\u009f\3\u00a0\3\u00a0\3\u00a1\3\u00a1\5\u00a1\u049b\n"+
		"\u00a1\3\u00a2\3\u00a2\5\u00a2\u049f\n\u00a2\3\u00a2\3\u00a2\3\u00a3\3"+
		"\u00a3\7\u00a3\u04a5\n\u00a3\f\u00a3\16\u00a3\u04a8\13\u00a3\3\u00a3\5"+
		"\u00a3\u04ab\n\u00a3\3\u00a4\3\u00a4\3\u00a5\3\u00a5\5\u00a5\u04b1\n\u00a5"+
		"\3\u00a6\3\u00a6\3\u00a6\3\u00a6\3\u00a7\3\u00a7\7\u00a7\u04b9\n\u00a7"+
		"\f\u00a7\16\u00a7\u04bc\13\u00a7\3\u00a7\5\u00a7\u04bf\n\u00a7\3\u00a8"+
		"\3\u00a8\3\u00a9\3\u00a9\5\u00a9\u04c5\n\u00a9\3\u00aa\3\u00aa\3\u00aa"+
		"\3\u00aa\3\u00ab\3\u00ab\5\u00ab\u04cd\n\u00ab\3\u00ac\3\u00ac\3\u00ac"+
		"\3\u00ac\5\u00ac\u04d3\n\u00ac\3\u00ad\3\u00ad\5\u00ad\u04d7\n\u00ad\3"+
		"\u00ad\3\u00ad\3\u00ae\6\u00ae\u04dc\n\u00ae\r\u00ae\16\u00ae\u04dd\3"+
		"\u00af\3\u00af\5\u00af\u04e2\n\u00af\3\u00b0\3\u00b0\3\u00b0\3\u00b0\3"+
		"\u00b0\3\u00b0\3\u00b0\3\u00b0\3\u00b0\3\u00b0\3\u00b0\5\u00b0\u04ef\n"+
		"\u00b0\3\u00b1\3\u00b1\3\u00b1\3\u00b1\3\u00b1\3\u00b1\3\u00b1\3\u00b2"+
		"\3\u00b2\3\u00b3\6\u00b3\u04fb\n\u00b3\r\u00b3\16\u00b3\u04fc\3\u00b3"+
		"\3\u00b3\3\u00b4\3\u00b4\3\u00b4\3\u00b4\3\u00b4\7\u00b4\u0506\n\u00b4"+
		"\f\u00b4\16\u00b4\u0509\13\u00b4\3\u00b4\3\u00b4\3\u00b4\3\u00b4\3\u00b4"+
		"\3\u00b5\3\u00b5\3\u00b5\3\u00b5\7\u00b5\u0514\n\u00b5\f\u00b5\16\u00b5"+
		"\u0517\13\u00b5\3\u00b5\3\u00b5\3\u00b5\3\u00b5\3\u00b5\3\u00b6\3\u00b6"+
		"\3\u00b6\3\u00b6\7\u00b6\u0522\n\u00b6\f\u00b6\16\u00b6\u0525\13\u00b6"+
		"\3\u00b6\3\u00b6\4\u0507\u0515\2\u00b7\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21"+
		"\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30"+
		"/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.["+
		"/]\60_\61a\62c\63e\64g\65i\66k\67m8o9q:s;u<w=y>{?}@\177A\u0081B\u0083"+
		"C\u0085D\u0087E\u0089F\u008bG\u008dH\u008fI\u0091J\u0093K\u0095L\u0097"+
		"M\u0099N\u009bO\u009dP\u009fQ\u00a1R\u00a3S\u00a5T\u00a7U\u00a9V\u00ab"+
		"W\u00adX\u00afY\u00b1Z\u00b3[\u00b5\\\u00b7]\u00b9^\u00bb_\u00bd`\u00bf"+
		"a\u00c1b\u00c3c\u00c5d\u00c7e\u00c9f\u00cbg\u00cdh\u00cfi\u00d1j\u00d3"+
		"k\u00d5l\u00d7m\u00d9n\u00dbo\u00ddp\u00dfq\u00e1r\u00e3s\u00e5t\u00e7"+
		"u\u00e9v\u00ebw\u00edx\u00efy\u00f1z\u00f3{\u00f5|\u00f7}\u00f9~\u00fb"+
		"\177\u00fd\u0080\u00ff\u0081\u0101\u0082\u0103\u0083\u0105\u0084\u0107"+
		"\u0085\u0109\u0086\u010b\u0087\u010d\2\u010f\2\u0111\2\u0113\u0088\u0115"+
		"\u0089\u0117\u008a\u0119\u008b\u011b\u008c\u011d\u008d\u011f\u008e\u0121"+
		"\u008f\u0123\u0090\u0125\2\u0127\2\u0129\u0091\u012b\2\u012d\2\u012f\2"+
		"\u0131\2\u0133\2\u0135\2\u0137\2\u0139\2\u013b\2\u013d\2\u013f\2\u0141"+
		"\2\u0143\2\u0145\2\u0147\2\u0149\2\u014b\2\u014d\2\u014f\2\u0151\2\u0153"+
		"\u0092\u0155\2\u0157\2\u0159\u0093\u015b\2\u015d\2\u015f\2\u0161\2\u0163"+
		"\2\u0165\u0094\u0167\u0095\u0169\u0096\u016b\u0097\3\2\33\7\2&&C\\aac"+
		"|\u0082\0\b\2&&\62;C\\aac|\u0082\0\4\2^^bb\4\2PPpp\4\2NNnn\4\2[[{{\4\2"+
		"UUuu\4\2WWww\5\2AANNnn\4\2GGgg\4\2--//\4\2HHhh\4\2FFff\3\2\63;\4\2ZZz"+
		"z\5\2\62;CHch\3\2\629\4\2DDdd\3\2\62\63\4\2))^^\n\2$$))^^ddhhppttvv\4"+
		"\2$$^^\3\2\62\65\5\2\13\f\16\17\"\"\4\2\f\f\17\17\u0548\2\3\3\2\2\2\2"+
		"\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2"+
		"\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2"+
		"\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2"+
		"\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2"+
		"\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2"+
		"\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2"+
		"K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3"+
		"\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2"+
		"\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2"+
		"q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3"+
		"\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2\2\2\u0083\3\2\2\2\2\u0085\3\2\2\2\2"+
		"\u0087\3\2\2\2\2\u0089\3\2\2\2\2\u008b\3\2\2\2\2\u008d\3\2\2\2\2\u008f"+
		"\3\2\2\2\2\u0091\3\2\2\2\2\u0093\3\2\2\2\2\u0095\3\2\2\2\2\u0097\3\2\2"+
		"\2\2\u0099\3\2\2\2\2\u009b\3\2\2\2\2\u009d\3\2\2\2\2\u009f\3\2\2\2\2\u00a1"+
		"\3\2\2\2\2\u00a3\3\2\2\2\2\u00a5\3\2\2\2\2\u00a7\3\2\2\2\2\u00a9\3\2\2"+
		"\2\2\u00ab\3\2\2\2\2\u00ad\3\2\2\2\2\u00af\3\2\2\2\2\u00b1\3\2\2\2\2\u00b3"+
		"\3\2\2\2\2\u00b5\3\2\2\2\2\u00b7\3\2\2\2\2\u00b9\3\2\2\2\2\u00bb\3\2\2"+
		"\2\2\u00bd\3\2\2\2\2\u00bf\3\2\2\2\2\u00c1\3\2\2\2\2\u00c3\3\2\2\2\2\u00c5"+
		"\3\2\2\2\2\u00c7\3\2\2\2\2\u00c9\3\2\2\2\2\u00cb\3\2\2\2\2\u00cd\3\2\2"+
		"\2\2\u00cf\3\2\2\2\2\u00d1\3\2\2\2\2\u00d3\3\2\2\2\2\u00d5\3\2\2\2\2\u00d7"+
		"\3\2\2\2\2\u00d9\3\2\2\2\2\u00db\3\2\2\2\2\u00dd\3\2\2\2\2\u00df\3\2\2"+
		"\2\2\u00e1\3\2\2\2\2\u00e3\3\2\2\2\2\u00e5\3\2\2\2\2\u00e7\3\2\2\2\2\u00e9"+
		"\3\2\2\2\2\u00eb\3\2\2\2\2\u00ed\3\2\2\2\2\u00ef\3\2\2\2\2\u00f1\3\2\2"+
		"\2\2\u00f3\3\2\2\2\2\u00f5\3\2\2\2\2\u00f7\3\2\2\2\2\u00f9\3\2\2\2\2\u00fb"+
		"\3\2\2\2\2\u00fd\3\2\2\2\2\u00ff\3\2\2\2\2\u0101\3\2\2\2\2\u0103\3\2\2"+
		"\2\2\u0105\3\2\2\2\2\u0107\3\2\2\2\2\u0109\3\2\2\2\2\u010b\3\2\2\2\2\u0113"+
		"\3\2\2\2\2\u0115\3\2\2\2\2\u0117\3\2\2\2\2\u0119\3\2\2\2\2\u011b\3\2\2"+
		"\2\2\u011d\3\2\2\2\2\u011f\3\2\2\2\2\u0121\3\2\2\2\2\u0123\3\2\2\2\2\u0129"+
		"\3\2\2\2\2\u0153\3\2\2\2\2\u0159\3\2\2\2\2\u0165\3\2\2\2\2\u0167\3\2\2"+
		"\2\2\u0169\3\2\2\2\2\u016b\3\2\2\2\3\u016d\3\2\2\2\5\u0170\3\2\2\2\7\u0172"+
		"\3\2\2\2\t\u0174\3\2\2\2\13\u0177\3\2\2\2\r\u0179\3\2\2\2\17\u017c\3\2"+
		"\2\2\21\u017e\3\2\2\2\23\u0181\3\2\2\2\25\u0183\3\2\2\2\27\u0186\3\2\2"+
		"\2\31\u0189\3\2\2\2\33\u018b\3\2\2\2\35\u018d\3\2\2\2\37\u018f\3\2\2\2"+
		"!\u0192\3\2\2\2#\u0194\3\2\2\2%\u0196\3\2\2\2\'\u0198\3\2\2\2)\u019b\3"+
		"\2\2\2+\u019d\3\2\2\2-\u019f\3\2\2\2/\u01a1\3\2\2\2\61\u01a3\3\2\2\2\63"+
		"\u01a5\3\2\2\2\65\u01a7\3\2\2\2\67\u01a9\3\2\2\29\u01ac\3\2\2\2;\u01ae"+
		"\3\2\2\2=\u01b1\3\2\2\2?\u01b4\3\2\2\2A\u01b6\3\2\2\2C\u01b8\3\2\2\2E"+
		"\u01ba\3\2\2\2G\u01bd\3\2\2\2I\u01c0\3\2\2\2K\u01c2\3\2\2\2M\u01c5\3\2"+
		"\2\2O\u01c9\3\2\2\2Q\u01cc\3\2\2\2S\u01d0\3\2\2\2U\u01d4\3\2\2\2W\u01d9"+
		"\3\2\2\2Y\u01dc\3\2\2\2[\u01de\3\2\2\2]\u01e1\3\2\2\2_\u01e3\3\2\2\2a"+
		"\u01e6\3\2\2\2c\u01ea\3\2\2\2e\u01ed\3\2\2\2g\u01f0\3\2\2\2i\u01f3\3\2"+
		"\2\2k\u01f6\3\2\2\2m\u01f9\3\2\2\2o\u01fc\3\2\2\2q\u01ff\3\2\2\2s\u0202"+
		"\3\2\2\2u\u0205\3\2\2\2w\u0208\3\2\2\2y\u020b\3\2\2\2{\u020e\3\2\2\2}"+
		"\u0212\3\2\2\2\177\u0216\3\2\2\2\u0081\u021a\3\2\2\2\u0083\u021d\3\2\2"+
		"\2\u0085\u0221\3\2\2\2\u0087\u0225\3\2\2\2\u0089\u0229\3\2\2\2\u008b\u022d"+
		"\3\2\2\2\u008d\u0231\3\2\2\2\u008f\u023a\3\2\2\2\u0091\u023d\3\2\2\2\u0093"+
		"\u0244\3\2\2\2\u0095\u024a\3\2\2\2\u0097\u024d\3\2\2\2\u0099\u0254\3\2"+
		"\2\2\u009b\u025b\3\2\2\2\u009d\u0262\3\2\2\2\u009f\u0268\3\2\2\2\u00a1"+
		"\u026d\3\2\2\2\u00a3\u0273\3\2\2\2\u00a5\u0279\3\2\2\2\u00a7\u0281\3\2"+
		"\2\2\u00a9\u028a\3\2\2\2\u00ab\u028e\3\2\2\2\u00ad\u0296\3\2\2\2\u00af"+
		"\u0299\3\2\2\2\u00b1\u029e\3\2\2\2\u00b3\u02a6\3\2\2\2\u00b5\u02ac\3\2"+
		"\2\2\u00b7\u02b2\3\2\2\2\u00b9\u02ba\3\2\2\2\u00bb\u02c1\3\2\2\2\u00bd"+
		"\u02c5\3\2\2\2\u00bf\u02ca\3\2\2\2\u00c1\u02d2\3\2\2\2\u00c3\u02d7\3\2"+
		"\2\2\u00c5\u02da\3\2\2\2\u00c7\u02e5\3\2\2\2\u00c9\u02ec\3\2\2\2\u00cb"+
		"\u02ef\3\2\2\2\u00cd\u02fa\3\2\2\2\u00cf\u0304\3\2\2\2\u00d1\u030a\3\2"+
		"\2\2\u00d3\u0311\3\2\2\2\u00d5\u0315\3\2\2\2\u00d7\u031a\3\2\2\2\u00d9"+
		"\u0320\3\2\2\2\u00db\u0327\3\2\2\2\u00dd\u0330\3\2\2\2\u00df\u0338\3\2"+
		"\2\2\u00e1\u0340\3\2\2\2\u00e3\u0349\3\2\2\2\u00e5\u0353\3\2\2\2\u00e7"+
		"\u035a\3\2\2\2\u00e9\u0361\3\2\2\2\u00eb\u0366\3\2\2\2\u00ed\u036d\3\2"+
		"\2\2\u00ef\u0374\3\2\2\2\u00f1\u037a\3\2\2\2\u00f3\u0381\3\2\2\2\u00f5"+
		"\u0386\3\2\2\2\u00f7\u038c\3\2\2\2\u00f9\u0393\3\2\2\2\u00fb\u039d\3\2"+
		"\2\2\u00fd\u03a2\3\2\2\2\u00ff\u03a6\3\2\2\2\u0101\u03ab\3\2\2\2\u0103"+
		"\u03af\3\2\2\2\u0105\u03b3\3\2\2\2\u0107\u03b8\3\2\2\2\u0109\u03bd\3\2"+
		"\2\2\u010b\u03d2\3\2\2\2\u010d\u03d5\3\2\2\2\u010f\u03d8\3\2\2\2\u0111"+
		"\u03de\3\2\2\2\u0113\u03e0\3\2\2\2\u0115\u03e3\3\2\2\2\u0117\u03e7\3\2"+
		"\2\2\u0119\u03ea\3\2\2\2\u011b\u03ed\3\2\2\2\u011d\u03f4\3\2\2\2\u011f"+
		"\u03fd\3\2\2\2\u0121\u0404\3\2\2\2\u0123\u0426\3\2\2\2\u0125\u0428\3\2"+
		"\2\2\u0127\u042e\3\2\2\2\u0129\u0459\3\2\2\2\u012b\u045b\3\2\2\2\u012d"+
		"\u0461\3\2\2\2\u012f\u046d\3\2\2\2\u0131\u046f\3\2\2\2\u0133\u047b\3\2"+
		"\2\2\u0135\u047d\3\2\2\2\u0137\u0481\3\2\2\2\u0139\u0484\3\2\2\2\u013b"+
		"\u0488\3\2\2\2\u013d\u048c\3\2\2\2\u013f\u0496\3\2\2\2\u0141\u049a\3\2"+
		"\2\2\u0143\u049c\3\2\2\2\u0145\u04a2\3\2\2\2\u0147\u04ac\3\2\2\2\u0149"+
		"\u04b0\3\2\2\2\u014b\u04b2\3\2\2\2\u014d\u04b6\3\2\2\2\u014f\u04c0\3\2"+
		"\2\2\u0151\u04c4\3\2\2\2\u0153\u04c6\3\2\2\2\u0155\u04cc\3\2\2\2\u0157"+
		"\u04d2\3\2\2\2\u0159\u04d4\3\2\2\2\u015b\u04db\3\2\2\2\u015d\u04e1\3\2"+
		"\2\2\u015f\u04ee\3\2\2\2\u0161\u04f0\3\2\2\2\u0163\u04f7\3\2\2\2\u0165"+
		"\u04fa\3\2\2\2\u0167\u0500\3\2\2\2\u0169\u050f\3\2\2\2\u016b\u051d\3\2"+
		"\2\2\u016d\u016e\7/\2\2\u016e\u016f\7/\2\2\u016f\4\3\2\2\2\u0170\u0171"+
		"\7~\2\2\u0171\6\3\2\2\2\u0172\u0173\7/\2\2\u0173\b\3\2\2\2\u0174\u0175"+
		"\7/\2\2\u0175\u0176\7?\2\2\u0176\n\3\2\2\2\u0177\u0178\7#\2\2\u0178\f"+
		"\3\2\2\2\u0179\u017a\7#\2\2\u017a\u017b\7?\2\2\u017b\16\3\2\2\2\u017c"+
		"\u017d\7\'\2\2\u017d\20\3\2\2\2\u017e\u017f\7\'\2\2\u017f\u0180\7?\2\2"+
		"\u0180\22\3\2\2\2\u0181\u0182\7(\2\2\u0182\24\3\2\2\2\u0183\u0184\7(\2"+
		"\2\u0184\u0185\7(\2\2\u0185\26\3\2\2\2\u0186\u0187\7(\2\2\u0187\u0188"+
		"\7?\2\2\u0188\30\3\2\2\2\u0189\u018a\7*\2\2\u018a\32\3\2\2\2\u018b\u018c"+
		"\7+\2\2\u018c\34\3\2\2\2\u018d\u018e\7,\2\2\u018e\36\3\2\2\2\u018f\u0190"+
		"\7,\2\2\u0190\u0191\7?\2\2\u0191 \3\2\2\2\u0192\u0193\7.\2\2\u0193\"\3"+
		"\2\2\2\u0194\u0195\7\60\2\2\u0195$\3\2\2\2\u0196\u0197\7\61\2\2\u0197"+
		"&\3\2\2\2\u0198\u0199\7\61\2\2\u0199\u019a\7?\2\2\u019a(\3\2\2\2\u019b"+
		"\u019c\7<\2\2\u019c*\3\2\2\2\u019d\u019e\7=\2\2\u019e,\3\2\2\2\u019f\u01a0"+
		"\7A\2\2\u01a0.\3\2\2\2\u01a1\u01a2\7B\2\2\u01a2\60\3\2\2\2\u01a3\u01a4"+
		"\7]\2\2\u01a4\62\3\2\2\2\u01a5\u01a6\7_\2\2\u01a6\64\3\2\2\2\u01a7\u01a8"+
		"\7`\2\2\u01a8\66\3\2\2\2\u01a9\u01aa\7`\2\2\u01aa\u01ab\7?\2\2\u01ab8"+
		"\3\2\2\2\u01ac\u01ad\7}\2\2\u01ad:\3\2\2\2\u01ae\u01af\7~\2\2\u01af\u01b0"+
		"\7~\2\2\u01b0<\3\2\2\2\u01b1\u01b2\7~\2\2\u01b2\u01b3\7?\2\2\u01b3>\3"+
		"\2\2\2\u01b4\u01b5\7\177\2\2\u01b5@\3\2\2\2\u01b6\u01b7\7\u0080\2\2\u01b7"+
		"B\3\2\2\2\u01b8\u01b9\7-\2\2\u01b9D\3\2\2\2\u01ba\u01bb\7-\2\2\u01bb\u01bc"+
		"\7-\2\2\u01bcF\3\2\2\2\u01bd\u01be\7-\2\2\u01be\u01bf\7?\2\2\u01bfH\3"+
		"\2\2\2\u01c0\u01c1\7>\2\2\u01c1J\3\2\2\2\u01c2\u01c3\7>\2\2\u01c3\u01c4"+
		"\7>\2\2\u01c4L\3\2\2\2\u01c5\u01c6\7>\2\2\u01c6\u01c7\7>\2\2\u01c7\u01c8"+
		"\7?\2\2\u01c8N\3\2\2\2\u01c9\u01ca\7@\2\2\u01ca\u01cb\7@\2\2\u01cbP\3"+
		"\2\2\2\u01cc\u01cd\7@\2\2\u01cd\u01ce\7@\2\2\u01ce\u01cf\7?\2\2\u01cf"+
		"R\3\2\2\2\u01d0\u01d1\7@\2\2\u01d1\u01d2\7@\2\2\u01d2\u01d3\7@\2\2\u01d3"+
		"T\3\2\2\2\u01d4\u01d5\7@\2\2\u01d5\u01d6\7@\2\2\u01d6\u01d7\7@\2\2\u01d7"+
		"\u01d8\7?\2\2\u01d8V\3\2\2\2\u01d9\u01da\7>\2\2\u01da\u01db\7?\2\2\u01db"+
		"X\3\2\2\2\u01dc\u01dd\7?\2\2\u01ddZ\3\2\2\2\u01de\u01df\7?\2\2\u01df\u01e0"+
		"\7?\2\2\u01e0\\\3\2\2\2\u01e1\u01e2\7@\2\2\u01e2^\3\2\2\2\u01e3\u01e4"+
		"\7@\2\2\u01e4\u01e5\7?\2\2\u01e5`\3\2\2\2\u01e6\u01e7\7\60\2\2\u01e7\u01e8"+
		"\7\60\2\2\u01e8\u01e9\7\60\2\2\u01e9b\3\2\2\2\u01ea\u01eb\7\60\2\2\u01eb"+
		"\u01ec\7\60\2\2\u01ecd\3\2\2\2\u01ed\u01ee\7/\2\2\u01ee\u01ef\7@\2\2\u01ef"+
		"f\3\2\2\2\u01f0\u01f1\7?\2\2\u01f1\u01f2\7@\2\2\u01f2h\3\2\2\2\u01f3\u01f4"+
		"\7>\2\2\u01f4\u01f5\7<\2\2\u01f5j\3\2\2\2\u01f6\u01f7\7<\2\2\u01f7\u01f8"+
		"\7@\2\2\u01f8l\3\2\2\2\u01f9\u01fa\7,\2\2\u01fa\u01fb\7,\2\2\u01fbn\3"+
		"\2\2\2\u01fc\u01fd\7#\2\2\u01fd\u01fe\7\u0080\2\2\u01fep\3\2\2\2\u01ff"+
		"\u0200\7>\2\2\u0200\u0201\7/\2\2\u0201r\3\2\2\2\u0202\u0203\7/\2\2\u0203"+
		"\u0204\7>\2\2\u0204t\3\2\2\2\u0205\u0206\7@\2\2\u0206\u0207\7/\2\2\u0207"+
		"v\3\2\2\2\u0208\u0209\7>\2\2\u0209\u020a\7@\2\2\u020ax\3\2\2\2\u020b\u020c"+
		"\7@\2\2\u020c\u020d\7>\2\2\u020dz\3\2\2\2\u020e\u020f\7\60\2\2\u020f\u0210"+
		"\7\60\2\2\u0210\u0211\7?\2\2\u0211|\3\2\2\2\u0212\u0213\7/\2\2\u0213\u0214"+
		"\7@\2\2\u0214\u0215\7?\2\2\u0215~\3\2\2\2\u0216\u0217\7,\2\2\u0217\u0218"+
		"\7,\2\2\u0218\u0219\7?\2\2\u0219\u0080\3\2\2\2\u021a\u021b\7\u0080\2\2"+
		"\u021b\u021c\7?\2\2\u021c\u0082\3\2\2\2\u021d\u021e\7>\2\2\u021e\u021f"+
		"\7/\2\2\u021f\u0220\7?\2\2\u0220\u0084\3\2\2\2\u0221\u0222\7/\2\2\u0222"+
		"\u0223\7>\2\2\u0223\u0224\7?\2\2\u0224\u0086\3\2\2\2\u0225\u0226\7@\2"+
		"\2\u0226\u0227\7/\2\2\u0227\u0228\7?\2\2\u0228\u0088\3\2\2\2\u0229\u022a"+
		"\7>\2\2\u022a\u022b\7@\2\2\u022b\u022c\7?\2\2\u022c\u008a\3\2\2\2\u022d"+
		"\u022e\7@\2\2\u022e\u022f\7>\2\2\u022f\u0230\7?\2\2\u0230\u008c\3\2\2"+
		"\2\u0231\u0232\7c\2\2\u0232\u0233\7d\2\2\u0233\u0234\7u\2\2\u0234\u0235"+
		"\7v\2\2\u0235\u0236\7t\2\2\u0236\u0237\7c\2\2\u0237\u0238\7e\2\2\u0238"+
		"\u0239\7v\2\2\u0239\u008e\3\2\2\2\u023a\u023b\7c\2\2\u023b\u023c\7u\2"+
		"\2\u023c\u0090\3\2\2\2\u023d\u023e\7c\2\2\u023e\u023f\7u\2\2\u023f\u0240"+
		"\7u\2\2\u0240\u0241\7g\2\2\u0241\u0242\7t\2\2\u0242\u0243\7v\2\2\u0243"+
		"\u0092\3\2\2\2\u0244\u0245\7c\2\2\u0245\u0246\7u\2\2\u0246\u0247\7{\2"+
		"\2\u0247\u0248\7p\2\2\u0248\u0249\7e\2\2\u0249\u0094\3\2\2\2\u024a\u024b"+
		"\7c\2\2\u024b\u024c\7v\2\2\u024c\u0096\3\2\2\2\u024d\u024e\7c\2\2\u024e"+
		"\u024f\7v\2\2\u024f\u0250\7j\2\2\u0250\u0251\7q\2\2\u0251\u0252\7o\2\2"+
		"\u0252\u0253\7g\2\2\u0253\u0098\3\2\2\2\u0254\u0255\7c\2\2\u0255\u0256"+
		"\7v\2\2\u0256\u0257\7g\2\2\u0257\u0258\7c\2\2\u0258\u0259\7e\2\2\u0259"+
		"\u025a\7j\2\2\u025a\u009a\3\2\2\2\u025b\u025c\7c\2\2\u025c\u025d\7v\2"+
		"\2\u025d\u025e\7q\2\2\u025e\u025f\7o\2\2\u025f\u0260\7k\2\2\u0260\u0261"+
		"\7e\2\2\u0261\u009c\3\2\2\2\u0262\u0263\7d\2\2\u0263\u0264\7t\2\2\u0264"+
		"\u0265\7g\2\2\u0265\u0266\7c\2\2\u0266\u0267\7m\2\2\u0267\u009e\3\2\2"+
		"\2\u0268\u0269\7e\2\2\u0269\u026a\7c\2\2\u026a\u026b\7u\2\2\u026b\u026c"+
		"\7g\2\2\u026c\u00a0\3\2\2\2\u026d\u026e\7e\2\2\u026e\u026f\7c\2\2\u026f"+
		"\u0270\7v\2\2\u0270\u0271\7e\2\2\u0271\u0272\7j\2\2\u0272\u00a2\3\2\2"+
		"\2\u0273\u0274\7e\2\2\u0274\u0275\7n\2\2\u0275\u0276\7c\2\2\u0276\u0277"+
		"\7u\2\2\u0277\u0278\7u\2\2\u0278\u00a4\3\2\2\2\u0279\u027a\7e\2\2\u027a"+
		"\u027b\7n\2\2\u027b\u027c\7q\2\2\u027c\u027d\7e\2\2\u027d\u027e\7m\2\2"+
		"\u027e\u027f\7g\2\2\u027f\u0280\7f\2\2\u0280\u00a6\3\2\2\2\u0281\u0282"+
		"\7e\2\2\u0282\u0283\7q\2\2\u0283\u0284\7p\2\2\u0284\u0285\7v\2\2\u0285"+
		"\u0286\7k\2\2\u0286\u0287\7p\2\2\u0287\u0288\7w\2\2\u0288\u0289\7g\2\2"+
		"\u0289\u00a8\3\2\2\2\u028a\u028b\7f\2\2\u028b\u028c\7g\2\2\u028c\u028d"+
		"\7h\2\2\u028d\u00aa\3\2\2\2\u028e\u028f\7f\2\2\u028f\u0290\7g\2\2\u0290"+
		"\u0291\7h\2\2\u0291\u0292\7c\2\2\u0292\u0293\7w\2\2\u0293\u0294\7n\2\2"+
		"\u0294\u0295\7v\2\2\u0295\u00ac\3\2\2\2\u0296\u0297\7f\2\2\u0297\u0298"+
		"\7q\2\2\u0298\u00ae\3\2\2\2\u0299\u029a\7g\2\2\u029a\u029b\7n\2\2\u029b"+
		"\u029c\7u\2\2\u029c\u029d\7g\2\2\u029d\u00b0\3\2\2\2\u029e\u029f\7g\2"+
		"\2\u029f\u02a0\7z\2\2\u02a0\u02a1\7v\2\2\u02a1\u02a2\7g\2\2\u02a2\u02a3"+
		"\7p\2\2\u02a3\u02a4\7f\2\2\u02a4\u02a5\7u\2\2\u02a5\u00b2\3\2\2\2\u02a6"+
		"\u02a7\7h\2\2\u02a7\u02a8\7c\2\2\u02a8\u02a9\7n\2\2\u02a9\u02aa\7u\2\2"+
		"\u02aa\u02ab\7g\2\2\u02ab\u00b4\3\2\2\2\u02ac\u02ad\7h\2\2\u02ad\u02ae"+
		"\7k\2\2\u02ae\u02af\7p\2\2\u02af\u02b0\7c\2\2\u02b0\u02b1\7n\2\2\u02b1"+
		"\u00b6\3\2\2\2\u02b2\u02b3\7h\2\2\u02b3\u02b4\7k\2\2\u02b4\u02b5\7p\2"+
		"\2\u02b5\u02b6\7c\2\2\u02b6\u02b7\7n\2\2\u02b7\u02b8\7n\2\2\u02b8\u02b9"+
		"\7{\2\2\u02b9\u00b8\3\2\2\2\u02ba\u02bb\7h\2\2\u02bb\u02bc\7k\2\2\u02bc"+
		"\u02bd\7p\2\2\u02bd\u02be\7k\2\2\u02be\u02bf\7u\2\2\u02bf\u02c0\7j\2\2"+
		"\u02c0\u00ba\3\2\2\2\u02c1\u02c2\7h\2\2\u02c2\u02c3\7q\2\2\u02c3\u02c4"+
		"\7t\2\2\u02c4\u00bc\3\2\2\2\u02c5\u02c6\7i\2\2\u02c6\u02c7\7q\2\2\u02c7"+
		"\u02c8\7v\2\2\u02c8\u02c9\7q\2\2\u02c9\u00be\3\2\2\2\u02ca\u02cb\7j\2"+
		"\2\u02cb\u02cc\7c\2\2\u02cc\u02cd\7u\2\2\u02cd\u02ce\7|\2\2\u02ce\u02cf"+
		"\7g\2\2\u02cf\u02d0\7t\2\2\u02d0\u02d1\7q\2\2\u02d1\u00c0\3\2\2\2\u02d2"+
		"\u02d3\7j\2\2\u02d3\u02d4\7g\2\2\u02d4\u02d5\7t\2\2\u02d5\u02d6\7g\2\2"+
		"\u02d6\u00c2\3\2\2\2\u02d7\u02d8\7k\2\2\u02d8\u02d9\7h\2\2\u02d9\u00c4"+
		"\3\2\2\2\u02da\u02db\7k\2\2\u02db\u02dc\7o\2\2\u02dc\u02dd\7r\2\2\u02dd"+
		"\u02de\7n\2\2\u02de\u02df\7g\2\2\u02df\u02e0\7o\2\2\u02e0\u02e1\7g\2\2"+
		"\u02e1\u02e2\7p\2\2\u02e2\u02e3\7v\2\2\u02e3\u02e4\7u\2\2\u02e4\u00c6"+
		"\3\2\2\2\u02e5\u02e6\7k\2\2\u02e6\u02e7\7o\2\2\u02e7\u02e8\7r\2\2\u02e8"+
		"\u02e9\7q\2\2\u02e9\u02ea\7t\2\2\u02ea\u02eb\7v\2\2\u02eb\u00c8\3\2\2"+
		"\2\u02ec\u02ed\7k\2\2\u02ed\u02ee\7p\2\2\u02ee\u00ca\3\2\2\2\u02ef\u02f0"+
		"\7k\2\2\u02f0\u02f1\7p\2\2\u02f1\u02f2\7u\2\2\u02f2\u02f3\7v\2\2\u02f3"+
		"\u02f4\7c\2\2\u02f4\u02f5\7p\2\2\u02f5\u02f6\7e\2\2\u02f6\u02f7\7g\2\2"+
		"\u02f7\u02f8\7q\2\2\u02f8\u02f9\7h\2\2\u02f9\u00cc\3\2\2\2\u02fa\u02fb"+
		"\7k\2\2\u02fb\u02fc\7p\2\2\u02fc\u02fd\7v\2\2\u02fd\u02fe\7g\2\2\u02fe"+
		"\u02ff\7t\2\2\u02ff\u0300\7h\2\2\u0300\u0301\7c\2\2\u0301\u0302\7e\2\2"+
		"\u0302\u0303\7g\2\2\u0303\u00ce\3\2\2\2\u0304\u0305\7k\2\2\u0305\u0306"+
		"\7u\2\2\u0306\u0307\7t\2\2\u0307\u0308\7g\2\2\u0308\u0309\7h\2\2\u0309"+
		"\u00d0\3\2\2\2\u030a\u030b\7p\2\2\u030b\u030c\7c\2\2\u030c\u030d\7v\2"+
		"\2\u030d\u030e\7k\2\2\u030e\u030f\7x\2\2\u030f\u0310\7g\2\2\u0310\u00d2"+
		"\3\2\2\2\u0311\u0312\7p\2\2\u0312\u0313\7g\2\2\u0313\u0314\7y\2\2\u0314"+
		"\u00d4\3\2\2\2\u0315\u0316\7p\2\2\u0316\u0317\7w\2\2\u0317\u0318\7n\2"+
		"\2\u0318\u0319\7n\2\2\u0319\u00d6\3\2\2\2\u031a\u031b\7q\2\2\u031b\u031c"+
		"\7h\2\2\u031c\u031d\7h\2\2\u031d\u031e\7g\2\2\u031e\u031f\7t\2\2\u031f"+
		"\u00d8\3\2\2\2\u0320\u0321\7q\2\2\u0321\u0322\7h\2\2\u0322\u0323\7h\2"+
		"\2\u0323\u0324\7g\2\2\u0324\u0325\7t\2\2\u0325\u0326\7u\2\2\u0326\u00da"+
		"\3\2\2\2\u0327\u0328\7q\2\2\u0328\u0329\7r\2\2\u0329\u032a\7g\2\2\u032a"+
		"\u032b\7t\2\2\u032b\u032c\7c\2\2\u032c\u032d\7v\2\2\u032d\u032e\7q\2\2"+
		"\u032e\u032f\7t\2\2\u032f\u00dc\3\2\2\2\u0330\u0331\7r\2\2\u0331\u0332"+
		"\7c\2\2\u0332\u0333\7e\2\2\u0333\u0334\7m\2\2\u0334\u0335\7c\2\2\u0335"+
		"\u0336\7i\2\2\u0336\u0337\7g\2\2\u0337\u00de\3\2\2\2\u0338\u0339\7r\2"+
		"\2\u0339\u033a\7t\2\2\u033a\u033b\7k\2\2\u033b\u033c\7x\2\2\u033c\u033d"+
		"\7c\2\2\u033d\u033e\7v\2\2\u033e\u033f\7g\2\2\u033f\u00e0\3\2\2\2\u0340"+
		"\u0341\7r\2\2\u0341\u0342\7t\2\2\u0342\u0343\7q\2\2\u0343\u0344\7r\2\2"+
		"\u0344\u0345\7g\2\2\u0345\u0346\7t\2\2\u0346\u0347\7v\2\2\u0347\u0348"+
		"\7{\2\2\u0348\u00e2\3\2\2\2\u0349\u034a\7r\2\2\u034a\u034b\7t\2\2\u034b"+
		"\u034c\7q\2\2\u034c\u034d\7v\2\2\u034d\u034e\7g\2\2\u034e\u034f\7e\2\2"+
		"\u034f\u0350\7v\2\2\u0350\u0351\7g\2\2\u0351\u0352\7f\2\2\u0352\u00e4"+
		"\3\2\2\2\u0353\u0354\7r\2\2\u0354\u0355\7w\2\2\u0355\u0356\7d\2\2\u0356"+
		"\u0357\7n\2\2\u0357\u0358\7k\2\2\u0358\u0359\7e\2\2\u0359\u00e6\3\2\2"+
		"\2\u035a\u035b\7t\2\2\u035b\u035c\7g\2\2\u035c\u035d\7v\2\2\u035d\u035e"+
		"\7w\2\2\u035e\u035f\7t\2\2\u035f\u0360\7p\2\2\u0360\u00e8\3\2\2\2\u0361"+
		"\u0362\7u\2\2\u0362\u0363\7g\2\2\u0363\u0364\7n\2\2\u0364\u0365\7h\2\2"+
		"\u0365\u00ea\3\2\2\2\u0366\u0367\7u\2\2\u0367\u0368\7v\2\2\u0368\u0369"+
		"\7c\2\2\u0369\u036a\7v\2\2\u036a\u036b\7k\2\2\u036b\u036c\7e\2\2\u036c"+
		"\u00ec\3\2\2\2\u036d\u036e\7u\2\2\u036e\u036f\7v\2\2\u036f\u0370\7t\2"+
		"\2\u0370\u0371\7w\2\2\u0371\u0372\7e\2\2\u0372\u0373\7v\2\2\u0373\u00ee"+
		"\3\2\2\2\u0374\u0375\7u\2\2\u0375\u0376\7w\2\2\u0376\u0377\7r\2\2\u0377"+
		"\u0378\7g\2\2\u0378\u0379\7t\2\2\u0379\u00f0\3\2\2\2\u037a\u037b\7u\2"+
		"\2\u037b\u037c\7y\2\2\u037c\u037d\7k\2\2\u037d\u037e\7v\2\2\u037e\u037f"+
		"\7e\2\2\u037f\u0380\7j\2\2\u0380\u00f2\3\2\2\2\u0381\u0382\7v\2\2\u0382"+
		"\u0383\7j\2\2\u0383\u0384\7k\2\2\u0384\u0385\7u\2\2\u0385\u00f4\3\2\2"+
		"\2\u0386\u0387\7v\2\2\u0387\u0388\7j\2\2\u0388\u0389\7t\2\2\u0389\u038a"+
		"\7q\2\2\u038a\u038b\7y\2\2\u038b\u00f6\3\2\2\2\u038c\u038d\7v\2\2\u038d"+
		"\u038e\7j\2\2\u038e\u038f\7t\2\2\u038f\u0390\7q\2\2\u0390\u0391\7y\2\2"+
		"\u0391\u0392\7u\2\2\u0392\u00f8\3\2\2\2\u0393\u0394\7v\2\2\u0394\u0395"+
		"\7t\2\2\u0395\u0396\7c\2\2\u0396\u0397\7p\2\2\u0397\u0398\7u\2\2\u0398"+
		"\u0399\7k\2\2\u0399\u039a\7g\2\2\u039a\u039b\7p\2\2\u039b\u039c\7v\2\2"+
		"\u039c\u00fa\3\2\2\2\u039d\u039e\7v\2\2\u039e\u039f\7t\2\2\u039f\u03a0"+
		"\7w\2\2\u03a0\u03a1\7g\2\2\u03a1\u00fc\3\2\2\2\u03a2\u03a3\7v\2\2\u03a3"+
		"\u03a4\7t\2\2\u03a4\u03a5\7{\2\2\u03a5\u00fe\3\2\2\2\u03a6\u03a7\7v\2"+
		"\2\u03a7\u03a8\7{\2\2\u03a8\u03a9\7r\2\2\u03a9\u03aa\7g\2\2\u03aa\u0100"+
		"\3\2\2\2\u03ab\u03ac\7x\2\2\u03ac\u03ad\7c\2\2\u03ad\u03ae\7n\2\2\u03ae"+
		"\u0102\3\2\2\2\u03af\u03b0\7x\2\2\u03b0\u03b1\7c\2\2\u03b1\u03b2\7t\2"+
		"\2\u03b2\u0104\3\2\2\2\u03b3\u03b4\7x\2\2\u03b4\u03b5\7q\2\2\u03b5\u03b6"+
		"\7k\2\2\u03b6\u03b7\7f\2\2\u03b7\u0106\3\2\2\2\u03b8\u03b9\7y\2\2\u03b9"+
		"\u03ba\7j\2\2\u03ba\u03bb\7g\2\2\u03bb\u03bc\7p\2\2\u03bc\u0108\3\2\2"+
		"\2\u03bd\u03be\7y\2\2\u03be\u03bf\7j\2\2\u03bf\u03c0\7k\2\2\u03c0\u03c1"+
		"\7n\2\2\u03c1\u03c2\7g\2\2\u03c2\u010a\3\2\2\2\u03c3\u03c7\5\u010d\u0087"+
		"\2\u03c4\u03c6\5\u010f\u0088\2\u03c5\u03c4\3\2\2\2\u03c6\u03c9\3\2\2\2"+
		"\u03c7\u03c5\3\2\2\2\u03c7\u03c8\3\2\2\2\u03c8\u03d3\3\2\2\2\u03c9\u03c7"+
		"\3\2\2\2\u03ca\u03ce\7b\2\2\u03cb\u03cd\5\u0111\u0089\2\u03cc\u03cb\3"+
		"\2\2\2\u03cd\u03d0\3\2\2\2\u03ce\u03cc\3\2\2\2\u03ce\u03cf\3\2\2\2\u03cf"+
		"\u03d1\3\2\2\2\u03d0\u03ce\3\2\2\2\u03d1\u03d3\7b\2\2\u03d2\u03c3\3\2"+
		"\2\2\u03d2\u03ca\3\2\2\2\u03d3\u010c\3\2\2\2\u03d4\u03d6\t\2\2\2\u03d5"+
		"\u03d4\3\2\2\2\u03d6\u010e\3\2\2\2\u03d7\u03d9\t\3\2\2\u03d8\u03d7\3\2"+
		"\2\2\u03d9\u0110\3\2\2\2\u03da\u03df\n\4\2\2\u03db\u03df\5\u0157\u00ac"+
		"\2\u03dc\u03dd\7^\2\2\u03dd\u03df\7b\2\2\u03de\u03da\3\2\2\2\u03de\u03db"+
		"\3\2\2\2\u03de\u03dc\3\2\2\2\u03df\u0112\3\2\2\2\u03e0\u03e1\5\u012d\u0097"+
		"\2\u03e1\u03e2\t\5\2\2\u03e2\u0114\3\2\2\2\u03e3\u03e5\5\u012d\u0097\2"+
		"\u03e4\u03e6\t\6\2\2\u03e5\u03e4\3\2\2\2\u03e5\u03e6\3\2\2\2\u03e6\u0116"+
		"\3\2\2\2\u03e7\u03e8\5\u012d\u0097\2\u03e8\u03e9\t\7\2\2\u03e9\u0118\3"+
		"\2\2\2\u03ea\u03eb\5\u012d\u0097\2\u03eb\u03ec\t\b\2\2\u03ec\u011a\3\2"+
		"\2\2\u03ed\u03f2\5\u012d\u0097\2\u03ee\u03ef\t\t\2\2\u03ef\u03f3\t\5\2"+
		"\2\u03f0\u03f1\t\5\2\2\u03f1\u03f3\t\t\2\2\u03f2\u03ee\3\2\2\2\u03f2\u03f0"+
		"\3\2\2\2\u03f3\u011c\3\2\2\2\u03f4\u03fb\5\u012d\u0097\2\u03f5\u03f7\t"+
		"\t\2\2\u03f6\u03f8\t\6\2\2\u03f7\u03f6\3\2\2\2\u03f7\u03f8\3\2\2\2\u03f8"+
		"\u03fc\3\2\2\2\u03f9\u03fa\t\n\2\2\u03fa\u03fc\t\t\2\2\u03fb\u03f5\3\2"+
		"\2\2\u03fb\u03f9\3\2\2\2\u03fc\u011e\3\2\2\2\u03fd\u0402\5\u012d\u0097"+
		"\2\u03fe\u03ff\t\t\2\2\u03ff\u0403\t\7\2\2\u0400\u0401\t\7\2\2\u0401\u0403"+
		"\t\t\2\2\u0402\u03fe\3\2\2\2\u0402\u0400\3\2\2\2\u0403\u0120\3\2\2\2\u0404"+
		"\u0409\5\u012d\u0097\2\u0405\u0406\t\t\2\2\u0406\u040a\t\b\2\2\u0407\u0408"+
		"\t\b\2\2\u0408\u040a\t\t\2\2\u0409\u0405\3\2\2\2\u0409\u0407\3\2\2\2\u040a"+
		"\u0122\3\2\2\2\u040b\u040c\5\u0131\u0099\2\u040c\u040e\7\60\2\2\u040d"+
		"\u040f\5\u0131\u0099\2\u040e\u040d\3\2\2\2\u040e\u040f\3\2\2\2\u040f\u0411"+
		"\3\2\2\2\u0410\u0412\5\u0125\u0093\2\u0411\u0410\3\2\2\2\u0411\u0412\3"+
		"\2\2\2\u0412\u0413\3\2\2\2\u0413\u0414\5\u0127\u0094\2\u0414\u0427\3\2"+
		"\2\2\u0415\u0416\7\60\2\2\u0416\u0418\5\u0131\u0099\2\u0417\u0419\5\u0125"+
		"\u0093\2\u0418\u0417\3\2\2\2\u0418\u0419\3\2\2\2\u0419\u041a\3\2\2\2\u041a"+
		"\u041b\5\u0127\u0094\2\u041b\u0427\3\2\2\2\u041c\u041d\5\u0131\u0099\2"+
		"\u041d\u041e\5\u0125\u0093\2\u041e\u041f\5\u0127\u0094\2\u041f\u0427\3"+
		"\2\2\2\u0420\u0422\5\u0131\u0099\2\u0421\u0423\5\u0125\u0093\2\u0422\u0421"+
		"\3\2\2\2\u0422\u0423\3\2\2\2\u0423\u0424\3\2\2\2\u0424\u0425\5\u0127\u0094"+
		"\2\u0425\u0427\3\2\2\2\u0426\u040b\3\2\2\2\u0426\u0415\3\2\2\2\u0426\u041c"+
		"\3\2\2\2\u0426\u0420\3\2\2\2\u0427\u0124\3\2\2\2\u0428\u042a\t\13\2\2"+
		"\u0429\u042b\t\f\2\2\u042a\u0429\3\2\2\2\u042a\u042b\3\2\2\2\u042b\u042c"+
		"\3\2\2\2\u042c\u042d\5\u0131\u0099\2\u042d\u0126\3\2\2\2\u042e\u042f\t"+
		"\r\2\2\u042f\u0128\3\2\2\2\u0430\u0431\5\u0131\u0099\2\u0431\u0433\7\60"+
		"\2\2\u0432\u0434\5\u0131\u0099\2\u0433\u0432\3\2\2\2\u0433\u0434\3\2\2"+
		"\2\u0434\u0435\3\2\2\2\u0435\u0437\5\u0125\u0093\2\u0436\u0438\5\u012b"+
		"\u0096\2\u0437\u0436\3\2\2\2\u0437\u0438\3\2\2\2\u0438\u045a\3\2\2\2\u0439"+
		"\u043a\5\u0131\u0099\2\u043a\u043c\7\60\2\2\u043b\u043d\5\u0131\u0099"+
		"\2\u043c\u043b\3\2\2\2\u043c\u043d\3\2\2\2\u043d\u043e\3\2\2\2\u043e\u043f"+
		"\5\u012b\u0096\2\u043f\u045a\3\2\2\2\u0440\u0441\5\u0131\u0099\2\u0441"+
		"\u0442\7\60\2\2\u0442\u0444\6\u0095\2\2\u0443\u0445\5\u0131\u0099\2\u0444"+
		"\u0443\3\2\2\2\u0444\u0445\3\2\2\2\u0445\u045a\3\2\2\2\u0446\u0447\7\60"+
		"\2\2\u0447\u0449\5\u0131\u0099\2\u0448\u044a\5\u0125\u0093\2\u0449\u0448"+
		"\3\2\2\2\u0449\u044a\3\2\2\2\u044a\u044c\3\2\2\2\u044b\u044d\5\u012b\u0096"+
		"\2\u044c\u044b\3\2\2\2\u044c\u044d\3\2\2\2\u044d\u045a\3\2\2\2\u044e\u044f"+
		"\5\u0131\u0099\2\u044f\u0451\5\u0125\u0093\2\u0450\u0452\5\u012b\u0096"+
		"\2\u0451\u0450\3\2\2\2\u0451\u0452\3\2\2\2\u0452\u045a\3\2\2\2\u0453\u0455"+
		"\5\u0131\u0099\2\u0454\u0456\5\u0125\u0093\2\u0455\u0454\3\2\2\2\u0455"+
		"\u0456\3\2\2\2\u0456\u0457\3\2\2\2\u0457\u0458\5\u012b\u0096\2\u0458\u045a"+
		"\3\2\2\2\u0459\u0430\3\2\2\2\u0459\u0439\3\2\2\2\u0459\u0440\3\2\2\2\u0459"+
		"\u0446\3\2\2\2\u0459\u044e\3\2\2\2\u0459\u0453\3\2\2\2\u045a\u012a\3\2"+
		"\2\2\u045b\u045c\t\16\2\2\u045c\u012c\3\2\2\2\u045d\u0462\5\u012f\u0098"+
		"\2\u045e\u0462\5\u013b\u009e\2\u045f\u0462\5\u0143\u00a2\2\u0460\u0462"+
		"\5\u014b\u00a6\2\u0461\u045d\3\2\2\2\u0461\u045e\3\2\2\2\u0461\u045f\3"+
		"\2\2\2\u0461\u0460\3\2\2\2\u0462\u012e\3\2\2\2\u0463\u046e\7\62\2\2\u0464"+
		"\u046b\5\u0135\u009b\2\u0465\u0467\5\u0131\u0099\2\u0466\u0465\3\2\2\2"+
		"\u0466\u0467\3\2\2\2\u0467\u046c\3\2\2\2\u0468\u0469\5\u0139\u009d\2\u0469"+
		"\u046a\5\u0131\u0099\2\u046a\u046c\3\2\2\2\u046b\u0466\3\2\2\2\u046b\u0468"+
		"\3\2\2\2\u046c\u046e\3\2\2\2\u046d\u0463\3\2\2\2\u046d\u0464\3\2\2\2\u046e"+
		"\u0130\3\2\2\2\u046f\u0477\5\u0133\u009a\2\u0470\u0472\5\u0137\u009c\2"+
		"\u0471\u0470\3\2\2\2\u0472\u0475\3\2\2\2\u0473\u0471\3\2\2\2\u0473\u0474"+
		"\3\2\2\2\u0474\u0476\3\2\2\2\u0475\u0473\3\2\2\2\u0476\u0478\5\u0133\u009a"+
		"\2\u0477\u0473\3\2\2\2\u0477\u0478\3\2\2\2\u0478\u0132\3\2\2\2\u0479\u047c"+
		"\7\62\2\2\u047a\u047c\5\u0135\u009b\2\u047b\u0479\3\2\2\2\u047b\u047a"+
		"\3\2\2\2\u047c\u0134\3\2\2\2\u047d\u047e\t\17\2\2\u047e\u0136\3\2\2\2"+
		"\u047f\u0482\5\u0133\u009a\2\u0480\u0482\7a\2\2\u0481\u047f\3\2\2\2\u0481"+
		"\u0480\3\2\2\2\u0482\u0138\3\2\2\2\u0483\u0485\7a\2\2\u0484\u0483\3\2"+
		"\2\2\u0485\u0486\3\2\2\2\u0486\u0484\3\2\2\2\u0486\u0487\3\2\2\2\u0487"+
		"\u013a\3\2\2\2\u0488\u0489\7\62\2\2\u0489\u048a\t\20\2\2\u048a\u048b\5"+
		"\u013d\u009f\2\u048b\u013c\3\2\2\2\u048c\u0494\5\u013f\u00a0\2\u048d\u048f"+
		"\5\u0141\u00a1\2\u048e\u048d\3\2\2\2\u048f\u0492\3\2\2\2\u0490\u048e\3"+
		"\2\2\2\u0490\u0491\3\2\2\2\u0491\u0493\3\2\2\2\u0492\u0490\3\2\2\2\u0493"+
		"\u0495\5\u013f\u00a0\2\u0494\u0490\3\2\2\2\u0494\u0495\3\2\2\2\u0495\u013e"+
		"\3\2\2\2\u0496\u0497\t\21\2\2\u0497\u0140\3\2\2\2\u0498\u049b\5\u013f"+
		"\u00a0\2\u0499\u049b\7a\2\2\u049a\u0498\3\2\2\2\u049a\u0499\3\2\2\2\u049b"+
		"\u0142\3\2\2\2\u049c\u049e\7\62\2\2\u049d\u049f\5\u0139\u009d\2\u049e"+
		"\u049d\3\2\2\2\u049e\u049f\3\2\2\2\u049f\u04a0\3\2\2\2\u04a0\u04a1\5\u0145"+
		"\u00a3\2\u04a1\u0144\3\2\2\2\u04a2\u04aa\5\u0147\u00a4\2\u04a3\u04a5\5"+
		"\u0149\u00a5\2\u04a4\u04a3\3\2\2\2\u04a5\u04a8\3\2\2\2\u04a6\u04a4\3\2"+
		"\2\2\u04a6\u04a7\3\2\2\2\u04a7\u04a9\3\2\2\2\u04a8\u04a6\3\2\2\2\u04a9"+
		"\u04ab\5\u0147\u00a4\2\u04aa\u04a6\3\2\2\2\u04aa\u04ab\3\2\2\2\u04ab\u0146"+
		"\3\2\2\2\u04ac\u04ad\t\22\2\2\u04ad\u0148\3\2\2\2\u04ae\u04b1\5\u0147"+
		"\u00a4\2\u04af\u04b1\7a\2\2\u04b0\u04ae\3\2\2\2\u04b0\u04af\3\2\2\2\u04b1"+
		"\u014a\3\2\2\2\u04b2\u04b3\7\62\2\2\u04b3\u04b4\t\23\2\2\u04b4\u04b5\5"+
		"\u014d\u00a7\2\u04b5\u014c\3\2\2\2\u04b6\u04be\5\u014f\u00a8\2\u04b7\u04b9"+
		"\5\u0151\u00a9\2\u04b8\u04b7\3\2\2\2\u04b9\u04bc\3\2\2\2\u04ba\u04b8\3"+
		"\2\2\2\u04ba\u04bb\3\2\2\2\u04bb\u04bd\3\2\2\2\u04bc\u04ba\3\2\2\2\u04bd"+
		"\u04bf\5\u014f\u00a8\2\u04be\u04ba\3\2\2\2\u04be\u04bf\3\2\2\2\u04bf\u014e"+
		"\3\2\2\2\u04c0\u04c1\t\24\2\2\u04c1\u0150\3\2\2\2\u04c2\u04c5\5\u014f"+
		"\u00a8\2\u04c3\u04c5\7a\2\2\u04c4\u04c2\3\2\2\2\u04c4\u04c3\3\2\2\2\u04c5"+
		"\u0152\3\2\2\2\u04c6\u04c7\7)\2\2\u04c7\u04c8\5\u0155\u00ab\2\u04c8\u04c9"+
		"\7)\2\2\u04c9\u0154\3\2\2\2\u04ca\u04cd\n\25\2\2\u04cb\u04cd\5\u0157\u00ac"+
		"\2\u04cc\u04ca\3\2\2\2\u04cc\u04cb\3\2\2\2\u04cd\u0156\3\2\2\2\u04ce\u04cf"+
		"\7^\2\2\u04cf\u04d3\t\26\2\2\u04d0\u04d3\5\u015f\u00b0\2\u04d1\u04d3\5"+
		"\u0161\u00b1\2\u04d2\u04ce\3\2\2\2\u04d2\u04d0\3\2\2\2\u04d2\u04d1\3\2"+
		"\2\2\u04d3\u0158\3\2\2\2\u04d4\u04d6\7$\2\2\u04d5\u04d7\5\u015b\u00ae"+
		"\2\u04d6\u04d5\3\2\2\2\u04d6\u04d7\3\2\2\2\u04d7\u04d8\3\2\2\2\u04d8\u04d9"+
		"\7$\2\2\u04d9\u015a\3\2\2\2\u04da\u04dc\5\u015d\u00af\2\u04db\u04da\3"+
		"\2\2\2\u04dc\u04dd\3\2\2\2\u04dd\u04db\3\2\2\2\u04dd\u04de\3\2\2\2\u04de"+
		"\u015c\3\2\2\2\u04df\u04e2\n\27\2\2\u04e0\u04e2\5\u0157\u00ac\2\u04e1"+
		"\u04df\3\2\2\2\u04e1\u04e0\3\2\2\2\u04e2\u015e\3\2\2\2\u04e3\u04e4\7^"+
		"\2\2\u04e4\u04ef\5\u0147\u00a4\2\u04e5\u04e6\7^\2\2\u04e6\u04e7\5\u0147"+
		"\u00a4\2\u04e7\u04e8\5\u0147\u00a4\2\u04e8\u04ef\3\2\2\2\u04e9\u04ea\7"+
		"^\2\2\u04ea\u04eb\5\u0163\u00b2\2\u04eb\u04ec\5\u0147\u00a4\2\u04ec\u04ed"+
		"\5\u0147\u00a4\2\u04ed\u04ef\3\2\2\2\u04ee\u04e3\3\2\2\2\u04ee\u04e5\3"+
		"\2\2\2\u04ee\u04e9\3\2\2\2\u04ef\u0160\3\2\2\2\u04f0\u04f1\7^\2\2\u04f1"+
		"\u04f2\7w\2\2\u04f2\u04f3\5\u013f\u00a0\2\u04f3\u04f4\5\u013f\u00a0\2"+
		"\u04f4\u04f5\5\u013f\u00a0\2\u04f5\u04f6\5\u013f\u00a0\2\u04f6\u0162\3"+
		"\2\2\2\u04f7\u04f8\t\30\2\2\u04f8\u0164\3\2\2\2\u04f9\u04fb\t\31\2\2\u04fa"+
		"\u04f9\3\2\2\2\u04fb\u04fc\3\2\2\2\u04fc\u04fa\3\2\2\2\u04fc\u04fd\3\2"+
		"\2\2\u04fd\u04fe\3\2\2\2\u04fe\u04ff\b\u00b3\2\2\u04ff\u0166\3\2\2\2\u0500"+
		"\u0501\7\61\2\2\u0501\u0502\7,\2\2\u0502\u0503\7,\2\2\u0503\u0507\3\2"+
		"\2\2\u0504\u0506\13\2\2\2\u0505\u0504\3\2\2\2\u0506\u0509\3\2\2\2\u0507"+
		"\u0508\3\2\2\2\u0507\u0505\3\2\2\2\u0508\u050a\3\2\2\2\u0509\u0507\3\2"+
		"\2\2\u050a\u050b\7,\2\2\u050b\u050c\7\61\2\2\u050c\u050d\3\2\2\2\u050d"+
		"\u050e\b\u00b4\3\2\u050e\u0168\3\2\2\2\u050f\u0510\7\61\2\2\u0510\u0511"+
		"\7,\2\2\u0511\u0515\3\2\2\2\u0512\u0514\13\2\2\2\u0513\u0512\3\2\2\2\u0514"+
		"\u0517\3\2\2\2\u0515\u0516\3\2\2\2\u0515\u0513\3\2\2\2\u0516\u0518\3\2"+
		"\2\2\u0517\u0515\3\2\2\2\u0518\u0519\7,\2\2\u0519\u051a\7\61\2\2\u051a"+
		"\u051b\3\2\2\2\u051b\u051c\b\u00b5\4\2\u051c\u016a\3\2\2\2\u051d\u051e"+
		"\7\61\2\2\u051e\u051f\7\61\2\2\u051f\u0523\3\2\2\2\u0520\u0522\n\32\2"+
		"\2\u0521\u0520\3\2\2\2\u0522\u0525\3\2\2\2\u0523\u0521\3\2\2\2\u0523\u0524"+
		"\3\2\2\2\u0524\u0526\3\2\2\2\u0525\u0523\3\2\2\2\u0526\u0527\b\u00b6\4"+
		"\2\u0527\u016c\3\2\2\2;\2\u03c7\u03ce\u03d2\u03d5\u03d8\u03de\u03e5\u03f2"+
		"\u03f7\u03fb\u0402\u0409\u040e\u0411\u0418\u0422\u0426\u042a\u0433\u0437"+
		"\u043c\u0444\u0449\u044c\u0451\u0455\u0459\u0461\u0466\u046b\u046d\u0473"+
		"\u0477\u047b\u0481\u0486\u0490\u0494\u049a\u049e\u04a6\u04aa\u04b0\u04ba"+
		"\u04be\u04c4\u04cc\u04d2\u04d6\u04dd\u04e1\u04ee\u04fc\u0507\u0515\u0523"+
		"\5\2\3\2\2\4\2\2\5\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}