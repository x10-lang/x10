// Posted on Wiki for discussion by Vivek

final distribution D = block(TABLE_SIZE);
 final long[:dist==D] Table = new long[D] (point [i]) { return i; }
 RanStarts = new long[unique()] 
      (point [i]) { return starts(i*N_UPDATES_PER_PLACE);};
 SmallTable = new long value[TABLE_SIZE] 
      (point [i]) {return i*S_TABLE_INIT;};
 finish ateach (point [i] : RanStarts ) {
   long ran = nextRandom(RanStarts[i]);
   for (point [count]: 1:N_UPDATES_PER_PLACE) {
     final int J = f(ran);
     long K = SmallTable[g(ran)];  
     async (D[J]) atomic Table[J] ^= K;
     ran = nextRandom(ran);
    }
 } // finish ateach
