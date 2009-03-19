/*
 * (c) Copyright IBM Corporation 2007
 *
 * x10lib version of RandomAccess
 * Author : Ganesh Bikshandi
 */

/* $Id: RandomAccess_hyper.cc,v 1.1 2007-10-24 08:34:26 ganeshvb Exp $ */

/* Main Version 
        minus finshStart/End (ie. uses Gfence)*/

#include "RandomAccess_spmd.h"
#include "timers.h"


struct __async__0__args
{
  __async__0__args (glong_t _captVar1) : captVar1 (_captVar1) {}
  glong_t captVar1;
};

struct __async__1__args
{
  __async__1__args (glong_t _captVar1) : captVar1 (_captVar1) {}
  glong_t captVar1;
};

struct __async__2__args
{
  __async__2__args (glong_t _captVar1, int  _captVar2)
    : captVar1 (_captVar1),
      captVar2 (_captVar2)
  {}
  
  glong_t captVar1;

  int captVar2;
};


void
inline __async__0 (__async__0__args args)
{
  glong_t ran = args.captVar1;
  glong_t off = ran & GLOBAL_SPACE.Table->mask;
  GLOBAL_SPACE.Table->array[off]  ^= ran;
}

void
inline __async__1 (__async__1__args args)
{
  glong_t ran = args.captVar1;
  //GLOBAL_SPACE.Table->verify (ran);
  glong_t off = ran & GLOBAL_SPACE.Table->mask;
  GLOBAL_SPACE.Table->array[off]++; 
}

void
inline __async__2 (__async__2__args args)
{
  GLOBAL_SPACE.SUM[(int)(args.captVar2)] = args.captVar1;
}

void
asyncSwitch0 (x10_async_handler_t h, void* arg, int niter)
{
/*    __async__0__args* args = (__async__0__args*) arg;
    ulong mask = GLOBAL_SPACE.Table->mask;
    glong_t* array = GLOBAL_SPACE.Table->array; 
    for (int i = 0; i < niter; i++) {
       glong_t ran = args[i].captVar1;
       glong_t off = ran & mask;
       array[off]  ^= ran;
    } */


    int i,dindex,index;
    int div_num = niter / 8;
    int loop_total = div_num * 8;
    ulong index0,index1,index2,index3,index4,index5,index6,index7;
    ulong ltable0,ltable1,ltable2,ltable3,ltable4,ltable5,ltable6,ltable7;
    __async__0__args* data = (__async__0__args*) arg;
    ulong nlocalm1 = GLOBAL_SPACE.Table->mask;
    glong_t* table = GLOBAL_SPACE.Table->array; 
    for (i = 0; i < div_num; i++) {
    dindex = i*8;

    index0 = data[dindex].captVar1 & nlocalm1;
    index1 = data[dindex+1].captVar1 & nlocalm1;
    index2 = data[dindex+2].captVar1 & nlocalm1;
    index3 = data[dindex+3].captVar1 & nlocalm1;
    index4 = data[dindex+4].captVar1 & nlocalm1;
    index5 = data[dindex+5].captVar1 & nlocalm1;
    index6 = data[dindex+6].captVar1 & nlocalm1;
    index7 = data[dindex+7].captVar1 & nlocalm1;
    ltable0 = table[index0];
    ltable1 = table[index1];
    ltable2 = table[index2];
    ltable3 = table[index3];
    ltable4 = table[index4];
    ltable5 = table[index5];
    ltable6 = table[index6];
    ltable7 = table[index7];

    table[index0] ^= data[dindex].captVar1;
    table[index1] ^= data[dindex+1].captVar1;
    table[index2] ^= data[dindex+2].captVar1;
    table[index3] ^= data[dindex+3].captVar1;
    table[index4] ^= data[dindex+4].captVar1;
    table[index5] ^= data[dindex+5].captVar1;
    table[index6] ^= data[dindex+6].captVar1;
    table[index7] ^= data[dindex+7].captVar1;  

/*    table[index0] ^= data[dindex].captVar1;
    table[index1] = ltable1 ^ data[dindex+1].captVar1;
    table[index2] = ltable2 ^ data[dindex+2].captVar1;
    table[index3] = ltable3 ^ data[dindex+3].captVar1;
    table[index4] = ltable4 ^ data[dindex+4].captVar1;
    table[index5] = ltable5 ^ data[dindex+5].captVar1;
    table[index6] = ltable6 ^ data[dindex+6].captVar1;
    table[index7] = ltable7 ^ data[dindex+7].captVar1; */
  }
   //loop_total = 0;
   for (i = loop_total; i < niter; i++) {
      ulong datum = data[i].captVar1;
      index = datum & nlocalm1;
      table[index] ^= datum;
    }

   return;
}

void
asyncSwitch (x10_async_handler_t h, void* arg, int niter)
{
  switch (h) {
  case 0:
    {
    /* __async__0__args* args = (__async__0__args*) arg;
    ulong mask = GLOBAL_SPACE.Table->mask;
    glong_t* array = GLOBAL_SPACE.Table->array; 
    for (int i = 0; i < niter; i++) {
       glong_t ran = args[i].captVar1
       glong_t off = ran & mask;
       array[off]  ^= ran;
    }*/ 
      /*__async__0__args* args = (__async__0__args*) arg;
      for (int i = 0; i < niter; i++) {
        __async__0(args[i]);
      }*/

    int i,dindex,index;
    int div_num = niter / 8;
    int loop_total = div_num * 8;
    ulong index0,index1,index2,index3,index4,index5,index6,index7;
    ulong ltable0,ltable1,ltable2,ltable3,ltable4,ltable5,ltable6,ltable7;
    __async__0__args* data = (__async__0__args*) arg;
    ulong nlocalm1 = GLOBAL_SPACE.Table->mask;
    glong_t* table = GLOBAL_SPACE.Table->array; 
    for (i = 0; i < div_num; i++) {
    dindex = i*8;

    index0 = data[dindex].captVar1 & nlocalm1;
    index1 = data[dindex+1].captVar1 & nlocalm1;
    index2 = data[dindex+2].captVar1 & nlocalm1;
    index3 = data[dindex+3].captVar1 & nlocalm1;
    index4 = data[dindex+4].captVar1 & nlocalm1;
    index5 = data[dindex+5].captVar1 & nlocalm1;
    index6 = data[dindex+6].captVar1 & nlocalm1;
    index7 = data[dindex+7].captVar1 & nlocalm1;
    ltable0 = table[index0];
    ltable1 = table[index1];
    ltable2 = table[index2];
    ltable3 = table[index3];
    ltable4 = table[index4];
    ltable5 = table[index5];
    ltable6 = table[index6];
    ltable7 = table[index7];

    table[index0] ^= data[dindex].captVar1;
    table[index1] ^= data[dindex+1].captVar1;
    table[index2] ^= data[dindex+2].captVar1;
    table[index3] ^= data[dindex+3].captVar1;
    table[index4] ^= data[dindex+4].captVar1;
    table[index5] ^= data[dindex+5].captVar1;
    table[index6] ^= data[dindex+6].captVar1;
    table[index7] ^= data[dindex+7].captVar1;  

    /*table[index0] ^= data[dindex].captVar1;
    table[index1] = ltable1 ^ data[dindex+1].captVar1;
    table[index2] = ltable2 ^ data[dindex+2].captVar1;
    table[index3] = ltable3 ^ data[dindex+3].captVar1;
    table[index4] = ltable4 ^ data[dindex+4].captVar1;
    table[index5] = ltable5 ^ data[dindex+5].captVar1;
    table[index6] = ltable6 ^ data[dindex+6].captVar1;
    table[index7] = ltable7 ^ data[dindex+7].captVar1; */
  }
   //loop_total = 0;
   for (i = loop_total; i < niter; i++) {
      ulong datum = data[i].captVar1;
      index = datum & nlocalm1;
      table[index] ^= datum;
    }
    }

    break;
  case 1:
    {
      __async__1__args* args = (__async__1__args*) arg;
      for (int i = 0; i < niter; i++) {
        __async__1(*(args));
        args++;
      }
    }
    break;
  case 2:
    {
      __async__2__args* args = (__async__2__args*) arg;
      for (int i = 0; i < niter; i++) {
        __async__2(*(args));
        args++;
      }
    }
    break;
  }

}


inline void 
RandomAccess_Dist::localTable::update (glong_t ran) 
{
  glong_t off = ran & mask;
  assert (off >=0 && off < tableSize);
  array[off]  ^= ran;
}

inline void  
RandomAccess_Dist::localTable::verify (glong_t ran) 
{
  glong_t off = ran & mask;
  assert (off >=0 && off < tableSize);
  array[off]++;
}

double  
RandomAccess_Dist::mysecond () 
{
  return (double) ((double) (nanoTime() / 1000) * 1.e-6);
}

glong_t 
RandomAccess_Dist::HPCC_starts(sglong_t n)
{
  int i, j;
  glong_t m2[64];
  glong_t temp, ran;
  
  while (n < 0) n += PERIOD;
  while (n > PERIOD) n -= PERIOD;
  if (n == 0) return 0x1;
  
  temp = 0x1;
  for (i=0; i<64; i++) {
    m2[i] = temp;
    temp = (temp << 1) ^ ((sglong_t) temp < 0 ? POLY : 0);
    temp = (temp << 1) ^ ((sglong_t) temp < 0 ? POLY : 0);
  }
  
  for (i=62; i>=0; i--)
    if (((n >> i) & 1) !=0 )
      break;
  
  ran = 0x2;
  while (i > 0) {
    temp = 0;
    for (j=0; j<64; j++)
      if ((ran >> j) & 1)
	temp ^= m2[j];
    ran = temp;
    i -= 1;
    if ((n >> i) & 1)
      ran = (ran << 1) ^ ((sglong_t) ran < 0 ? POLY : 0);
  }
    
  return ran;
}

void  
RandomAccess_Dist::main (x10::array<x10::ref<x10::lang::String> >& args)
{
  if ((NUMPLACES & (NUMPLACES-1)) > 0) {
    cout << "the number of places must be a power of 2!";
    exit (-1);
  } 
  
  int VERIFY = UPDATE;
  bool doIO=false; 
  bool embarrassing = false;
  int logTableSize = 10;
  
  for (int q = 0; q < args.length; ++q) {
    if ((*args[q]).equals (x10::ref<x10::lang::String>(x10::lang::String("-o")))) {
      doIO = true;
    }
    
    if ((*args[q]).equals (x10::ref<x10::lang::String>(x10::lang::String("-e")))) {
      embarrassing = true;
    }
    
    if ((*args[q]).equals (x10::ref<x10::lang::String>(x10::lang::String("-m")))) {
      ++q;
      logTableSize = java::lang::Integer::parseInt(args[q]);
    }

    if ((*args[q]).equals (x10::ref<x10::lang::String>(x10::lang::String("-v")))) {
      ++q;
      VERIFY = java::lang::Integer::parseInt(args[q])%3;
    }
  }
  
  const glong_t tableSize = (1UL << logTableSize);
  const glong_t numUpdates = tableSize*4*NUMPLACES;
  GLOBAL_SPACE.Table = new localTable (tableSize);

  SyncGlobal(); 
 
  double GUPs;
  double Realtime;    /* CPU time to update table */
  double CPUtime;    /* CPU time to update table */
  if (x10lib::__x10_my_place == 0) {
    
    /* Print parameters for run */
    if (doIO) {
      cout << "Distributed table size  = 2 ^ " << logTableSize 
	   << "*" << NUMPLACES << "=" << tableSize * NUMPLACES << " words" << endl;
      cout << "Number of total updates = " << 4 * tableSize * NUMPLACES << endl; 
    }
    
    /* Begin time x10lib::here */
    Realtime = -mysecond();
    CPUtime = -cputime();
  }
 
  const long LogTableSize = logTableSize; 
  const bool Embarrassing = embarrassing; 
  const glong_t NumUpdates = tableSize*4;
  
  glong_t total = 0; 
  if (VERIFY == VERIFICATION_P) { 
    glong_t ran = HPCC_starts (x10lib::__x10_my_place*NumUpdates);
    for (glong_t i = 0; i < NumUpdates; i+=1024) {
     for (int j = 0; j < 1024; j++) {
      int placeID;
      if (Embarrassing)
	placeID = x10lib::__x10_my_place;
      else
	placeID = (int) ((ran>>LogTableSize) & PLACEIDMASK);
      
      glong_t temp = ran;
      
      __async__1__args a(temp);
      if (placeID == x10lib::__x10_my_place) __async__1 (a); 
      else  {
        asyncSpawnInlineAgg_hc(placeID, 1, &a, sizeof(a));
      }

      ran = ((ran << 1) ^ ((sglong_t) ran < 0 ? POLY : 0));     

     }
    asyncFlush_hc (1, sizeof(__async__1__args));
    } 
    asyncFlush_hc (1, sizeof(__async__1__args));
   
    SyncGlobal();
  } else {   
    glong_t ran = HPCC_starts (x10lib::__x10_my_place*NumUpdates);
    for (glong_t i = 0; i < NumUpdates; i += 1024) {

     for (int j = 0; j < 1024; j++) {
      int placeID;

        if (Embarrassing)
	placeID = x10lib::__x10_my_place;
      else 
	placeID = (int) ((ran>>LogTableSize) & PLACEIDMASK);
      

      glong_t temp = ran;
       __async__0__args a(temp);
      /* if (placeID == x10lib::__x10_my_place) 
            __async__0(a);
      else*/  {
       asyncSpawnInlineAgg_hc (placeID, 0, &a, sizeof(a));
     }

      ran = ((ran << 1) ^ ((sglong_t) ran < 0 ? POLY : 0));     
      }
      asyncFlush_hc (0, sizeof(__async__0__args));

    }

     //cout << "Hello " << endl;   
    //asyncFlush_hc(0, sizeof(__async__0__args));

    SyncGlobal();
  }

  if (x10lib::__x10_my_place == 0) {
    /* End time section */
    Realtime += mysecond();
    CPUtime += cputime();
  } 

  if (VERIFY == UPDATE_AND_VERIFICATION){
    if (x10lib::__x10_my_place != 0) goto L1;
    cout << "Verifying result by repeating the update sequentially " << endl;
  L1:
    if (x10lib::__x10_my_place != 0) goto L2;
    for (int i = 0; i <  NUMPLACES; i++) {
      glong_t ran = HPCC_starts (i * NumUpdates);
      for (glong_t i = 0; i < NumUpdates; i++) {
	int placeID;
	if (Embarrassing)
	  placeID = x10lib::__x10_my_place;
	else
	  placeID = (int) ((ran>>LogTableSize) & PLACEIDMASK);
	const glong_t temp = ran;
	__async__0__args a(temp);
	if (placeID == x10lib::__x10_my_place) __async__0 (a);
	else asyncSpawnInlineAgg (placeID, 0, &a, sizeof(a));
	ran = (ran << 1) ^ ((long) ran < 0 ? POLY : 0);
      }
    }
    asyncFlush (0, sizeof(__async__0__args));
  L2:
    SyncGlobal();
  }
 
  if (x10lib::__x10_my_place ==0) {
    /* make sure no division by zero */
    GUPs = (Realtime > 0.0 ? 1.0 / Realtime : -1.0);
    GUPs *= 1e-9*(4*tableSize*NUMPLACES);
    /* Print timing results */
    if (doIO) {
      cout << "CPU time used = " << Realtime << " seconds " << endl;
      cout << "(actual cputime) " << CPUtime << "seconds " << endl;
    }
    if (VERIFY == UPDATE) cout << GUPs << " Billion (10^9) Updates  per second [GUP/s]" << endl;
  }
  
  if (VERIFY > 0) {
    int p = x10lib::__x10_my_place; 
    if (p == 0) {
      GLOBAL_SPACE.SUM = new glong_t [NUMPLACES];
    }
 
    SyncGlobal();

    glong_t sum =0;
    for (glong_t i = 0; i < tableSize; i++) 
      sum += GLOBAL_SPACE.Table->array[i];
   
    const long temp = sum; 
    __async__2__args a(temp, p);
    if (0 == x10lib::__x10_my_place) __async__2 (a); 
    else asyncSpawnInlineAgg (0, 2, &a, sizeof(a));
    
    asyncFlush (2, sizeof(__async__2__args));
    
    SyncGlobal();

    if (x10lib::__x10_my_place == 0) {
      glong_t globalSum = 0;
      for (int i = 0;i < NUMPLACES; i++) {
        globalSum += GLOBAL_SPACE.SUM[i];
      }
      if (VERIFY == VERIFICATION_P) {
	double missedUpdateRate = (globalSum - numUpdates) / (double) numUpdates*100;
	cout << " the rate of missed updates " << missedUpdateRate << " % " << endl;
      }else {
	cout << " The global sum is " << globalSum << " (correct=0) " << endl;
      }
    }
  }
  
}


const Dist<1>* RandomAccess_Dist::UNIQUE = Dist<1>::makeUnique();

int RandomAccess_Dist::NUMPLACES = numPlaces();

int RandomAccess_Dist::PLACEIDMASK = RandomAccess_Dist::NUMPLACES-1;

extern "C" {
  int main (int ac, char* av[])
  {
    x10::array<x10::ref<x10::lang::String> >* args = x10::convert_args (ac, av);
    
    RandomAccess_Dist::main (*args);
    
    x10::free_args (args);
        
    return x10::exitCode;    
  }
}
