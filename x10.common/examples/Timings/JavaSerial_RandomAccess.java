
/**
 *  RandomAccess benchmark
 *
 *  Based on HPCC 0.5beta 
 *
 *  @author cmd
 * 
 */

/** 
 * C routines to be linked with X10, written in X10 for now
 */

/**
 * Utility routine to start random number generator at Nth step
 * (original "starts" routine from RandomAccess)
 * <code>
 Functional synopsis:
 long starts(long n) :=
 long n1=for(long t=n; t<0 return t; next t=t+PERIOD){} ;
 long n2=for(long t=n1; t>PERIOD return t; next t=t-PERIOD){};
 if (n2==0) return 0x1;
 long m2[]= new long[0..63](i) {i==0?1:(nextRandom**2)(m2[i-1]);}
 int lastSetBit= findFirstSatisfying(int j:62..0)(getBit(n2,j));
 mutable long ran=2;
 for(int i=lastSetBit..1) {
 long ranXor= Xor(int j:0..63 where getBit(ran,j))(m2[j]);
 ran= getBit(n2,i-1)?nextRandom(ranXor):ranXor;}
 return ran;
 * </code>
 */



public class JavaSerial_RandomAccess {
	
	
	
	private static boolean getBit(long n, int i) {
		return  ((n>>>i)&1)!=0;
	}
	public static long starts(long n) {
		int i, j;
		long[] m2= new long[64];
		long temp, ran;
		
		while (n < 0) n += PERIOD;
		while (n > PERIOD) n -= PERIOD;
		if (n == 0) return 1;
		
		temp = 1;
		for (i=0; i<64; i++) {
			m2[i] = temp;
			temp = nextRandom(temp);
			temp = nextRandom(temp);
		}
		
		for (i=62; i>=0; i--)
			if (getBit(n,i))
				break;
			
		ran = 2;
		while (i > 0) {
			temp = 0;
			for (j=0; j<64; j++)
				if (getBit(ran,j))
					temp ^= m2[j];
			ran = temp;
			i -= 1;
			if (getBit(n,i))
				ran = nextRandom(ran);
		}
		
		return ran;
	}
	
	
	// Set places.MAX_PLACES to 128 to match original
	// Set LOG2_TABLE_SIZE to 25 to match original
	
	static final int  MAX_PLACES= 4;//x10.lang.place.MAX_PLACES;
	static final int  LOG2_TABLE_SIZE=17;//5;
	static final int  LOG2_S_TABLE_SIZE=4;
	static final int  TABLE_SIZE=(1 << LOG2_TABLE_SIZE);
	static final int  S_TABLE_SIZE=(1 <<LOG2_S_TABLE_SIZE);
	static final int  N_UPDATES=(4*TABLE_SIZE);
	static final int  N_UPDATES_PER_PLACE=(N_UPDATES/MAX_PLACES);
	static final int  WORD_SIZE=64;
	static final long POLY=7;
	static final long S_TABLE_INIT=0x0123456789abcdefL;
	private static final long PERIOD = 1317624576693539401L;
	// expected result with LOG2_S_TABLE_SIZE=5, MAX_PLACES=2
	// LOG2_S_TABLE_SIZE = 4
	//final long EXPECTED_RESULT= 1973902911463121104L;
	final long EXPECTED_RESULT= -4957642133356826143L;
	
	/** Get the value as a table index.
	 */
	
	static int f(long val) {
		return (int) (val & (TABLE_SIZE-1));
	}
	
	/** Get the value as an index into the small table.
	 */
	
	static int g(long val) {
		return (int)( val >>>
				(WORD_SIZE-LOG2_S_TABLE_SIZE));}
	
	/** Return the next number following this one.
	 * Actually the next item in the sequence generated
	 * by a primitive polynomial over GF(2).)
	 */
	
	static long nextRandom(long val) {
		return 
		((val<<1) ^ (val<0?POLY:0));}
	
	
	/*
	 */
	
	
		/**
	 * main RandomAccess routine
	 */
	
	public boolean run() {
		
		// A small value table that will be copied to all processors
		final long[] smallTable = new long [S_TABLE_SIZE];
		int i;
		for(i=0;i< smallTable.length;++i)
			smallTable[i] = i*S_TABLE_INIT;
						   
		
		// distributed histogram table
		final long[] table = new long[TABLE_SIZE];
		for( i=0;i < table.length;++i)
			table[i] = i;
				
		// random number starting seeds for each place
		final long[] ranStarts = new long[MAX_PLACES];
		for(i=0;i < ranStarts.length;++i)
			ranStarts[i] = starts(N_UPDATES_PER_PLACE *i);
		
		// In all places in parallel,repeatedly generate random indices
		// and do remote atomic updates on corresponding table elements
		for(i=0;i < ranStarts.length;++i){
			long ran = nextRandom(ranStarts[i]);
			for(int count=0;count <N_UPDATES_PER_PLACE;++count) {
				final int  j = f(ran);
				final long k = smallTable[g(ran)];
				table[j] = table[j] ^ k;
				ran = nextRandom(ran);
			}
		}
		
		 long sum=0;
		for(i=0;i< table.length;++i)
			sum += table[i];
		
		System.out.println("Check sum="+sum);
		return sum==EXPECTED_RESULT;
	}
	
	public static void main(String args[]) {
		long start = System.currentTimeMillis();
		boolean b= (new JavaSerial_RandomAccess()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		long stop = System.currentTimeMillis();
		System.out.println("Elapsed time:"+((double)(stop-start)/1000));
		System.exit(b?0:1);
	}
	
}


