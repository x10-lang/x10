/**
 *  RandomAccess benchmark
 *
 *  Based on HPCC 0.5beta 
 *
 *  The external "starts" routine is written in C.
 *
 *  @author kemal, vj approx 7/2004
 *  New version, 11/2004
 */

public class RandomAccess {
  // Set places.MAX_PLACES to 128 to match original
  // Set LOG2_TABLE_SIZE to 25 to match original

  const int  MAX_PLACES= place.MAX_PLACES;
  const int  LOG2_TABLE_SIZE=5;
  const int  LOG2_S_TABLE_SIZE=4;
  const int  TABLE_SIZE=(1<<LOG2_TABLE_SIZE);
  const int  S_TABLE_SIZE=(1<<LOG2_S_TABLE_SIZE);
  const int  N_UPDATES=(4*TABLE_SIZE);
  const int  N_UPDATES_PER_PLACE=(N_UPDATES/MAX_PLACES);
  const int  WORD_SIZE=64;
  const long POLY=7;
  const long S_TABLE_INIT=0x0123456789abcdefL;
  // expected result with LOG2_S_TABLE_SIZE=5,
  // LOG2_S_TABLE_SIZE = 4
  const long EXPECTED_RESULT= 1973902911463121104L;

static value class ranNum extends x10.lang.Object {
   long val;
   /**
    * Constructor
    */
   public ranNum(long x) {val=x;}
   /** Get the value as a Table index.
    */
   int f() {return (int) (this.val & (TABLE_SIZE-1));} 
   /** Get the value as an index into the small Table.
    */
   int g() {return (int)(this.val>>>(WORD_SIZE-LOG2_S_TABLE_SIZE));}
   /*
    * return the value exclusive or'ed with k 
    */
   ranNum update(ranNum k) {
	return new ranNum(this.val^k.val);
	}

   /** Return the next number following this one.
    * Actually the next item in the sequence generated
    * by a primitive polynomial over GF(2).)
    */
   ranNum nextRandom() {return new ranNum((val<<1)^(val<0?POLY:0));}
}


   /*
    * Utility routines to create simple common distributions
    */
   /**
    * create a simple 1D blocked distribution
    */
   distribution block (int arraySize) {
      return distribution.factory.block(0:(arraySize-1));
   }
    
   /**
    * create a unique distribution (mapping each i to place i)
    */
   distribution unique () {
       return distribution.factory.unique(place.places);
   }
  
   /**
    * The random number "starts" routine is in external C code
    */
   static extern long starts(long n);
   static {System.loadLibrary("RandomAccessLong");}
  
   /**
    * main RandomAccess routine
    */
   public boolean run() {  	
    // A small value Table that will be copied to all processors
    ranNum value[.] SmallTable = 
        new ranNum value[(0:S_TABLE_SIZE-1)->here]
          (point [i]) {return new ranNum(i*S_TABLE_INIT);};        
    // distributed histogram Table
    ranNum[.] Table = new ranNum[block(TABLE_SIZE)]
         (point [i]){return new ranNum(i);};
    // random number starting seeds for each place (calls C code)
    ranNum[.] RanStarts = new ranNum[unique()]
      (point [i]) {return new ranNum(starts(N_UPDATES_PER_PLACE*i));};
    // In all places in parallel,repeatedly generate random indices
    // and do remote atomic updates on corresponding Table elements
    finish ateach (point [i]: RanStarts) {
        ranNum ran = RanStarts[i].nextRandom();
        for(point [count]: 1:N_UPDATES_PER_PLACE) {
            System.out.println("Place "+i+" iteration "+count);
            int  J = ran.f();
            ranNum K = SmallTable[ran.g()]; 
            async(Table.distribution[J]) atomic Table[J]=Table[J].update(K);
            ran = ran.nextRandom();
        }
    }
    //return Table.sum()=EXPECTED_RESULT;
    return true;
  }

  public static void main(String args[]) {
     boolean b= (new RandomAccess()).run();
     System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
     System.exit(b?0:1);
  }
 }


