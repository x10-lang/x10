/*
 * (c) Copyright IBM Corporation 2007
 *
 * x10lib version of RandomAccess
 * Author : Ganesh Bikshandi
 */

/* $Id: RandomAccess_spmd.v03.cc,v 1.2 2007-10-24 08:34:26 ganeshvb Exp $ */

/* Main Version 
        - minus finshStart/End (i.e. uses Gfence)
        - minus struct args (i.e. uses plain arguments for asyncs)
        - the async0 invocation in the main update loop  replaced by functor
*/

#include "RandomAccess_spmd.h"
#include "timers.h"
#include <x10/aggregate.tcc>

struct Async0{
inline void operator () (x10_async_arg_t ran)
{
  glong_t off = ran & GLOBAL_SPACE.Table->mask;
  GLOBAL_SPACE.Table->array[off]  ^= ran;
}
};

void
inline __async__0 (x10_async_arg_t ran)
{
  GLOBAL_SPACE.Table->update (ran);
}


void
inline __async__1 (x10_async_arg_t ran)
{
  GLOBAL_SPACE.Table->verify (ran);
}

void
inline __async__2 (x10_async_arg_t captVar1, x10_async_arg_t captVar2)
{
  GLOBAL_SPACE.SUM[(int)(captVar2)] = captVar1;
}

void
asyncSwitch (x10_async_handler_t h, void* buf, int niter)
{
  char* args = (char*) buf;
  switch (h) {
  case 0:
    for (int i = 0; i < niter; i++) {
      __async__0(*((x10_async_arg_t*)args));
      args += sizeof(x10_async_arg_t);
    }
    break;
  case 1:
    for (int i = 0; i < niter; i++) {
      __async__1(*((x10_async_arg_t*)args));
      args += sizeof(x10_async_arg_t);
    }
    break;
  case 2:
    for (int i = 0; i < niter; i++) {
      __async__2(*((x10_async_arg_t*)args), *((x10_async_arg_t*) (args+sizeof(x10_async_arg_t))));
      args += 2*sizeof(x10_async_arg_t);
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
  double cputime;    /* CPU time to update table */
  if (x10lib::__x10_my_place == 0) {
    
    /* Print parameters for run */
    if (doIO) {
      cout << "Distributed table size  = 2 ^ " << logTableSize 
	   << "*" << NUMPLACES << "=" << tableSize * NUMPLACES << " words" << endl;
      cout << "Number of total updates = " << 4 * tableSize * NUMPLACES << endl; 
    }
    
    /* Begin time x10lib::here */
    cputime = -mysecond();
  }
 
  const long LogTableSize = logTableSize; 
  const bool Embarrassing = embarrassing; 
  const glong_t NumUpdates = tableSize*4;
   
  if (VERIFY == VERIFICATION_P) { 
    glong_t ran = HPCC_starts (x10lib::__x10_my_place*NumUpdates);
    for (glong_t i = 0; i < NumUpdates; i++) {
      int placeID;
      if (Embarrassing)
	placeID = x10lib::__x10_my_place;
      else
	placeID = (int) ((ran>>LogTableSize) & PLACEIDMASK);
      
      glong_t temp = ran;
      
      if (placeID == x10lib::__x10_my_place) __async__1 (temp); 
      else asyncSpawnInlineAgg(placeID, 1, temp);
      ran = ((ran << 1) ^ ((sglong_t) ran < 0 ? POLY : 0));     
    } 
    asyncFlush (1, sizeof(x10_async_arg_t));
   
    SyncGlobal();
  } else {   
    glong_t ran = HPCC_starts (x10lib::__x10_my_place*NumUpdates);
    for (glong_t i = 0; i < NumUpdates; i++) {
      int placeID;
      if (Embarrassing)
	placeID = x10lib::__x10_my_place;
      else
	placeID = (int) ((ran>>LogTableSize) & PLACEIDMASK);
      
      glong_t temp = ran;

      if (placeID == x10lib::__x10_my_place) __async__0(temp);
      else asyncSpawnInlineAgg_t<Async0> (placeID, 0, temp);
      ran = ((ran << 1) ^ ((sglong_t) ran < 0 ? POLY : 0));     
    }   
    asyncFlush_t<1> (0);

    SyncGlobal();
  }

  if (x10lib::__x10_my_place == 0) {
    /* End time section */
    cputime += mysecond();
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

	if (placeID == x10lib::__x10_my_place) __async__0 (temp);
	else asyncSpawnInlineAgg (placeID, 0, temp);
	ran = (ran << 1) ^ ((long) ran < 0 ? POLY : 0);
      }
    }
    asyncFlush (0, sizeof(x10_async_arg_t));
  L2:
    SyncGlobal();
  }
 
  if (x10lib::__x10_my_place ==0) {
    /* make sure no division by zero */
    GUPs = (cputime > 0.0 ? 1.0 / cputime : -1.0);
    GUPs *= 1e-9*(4*tableSize*NUMPLACES);
    /* Print timing results */
    if (doIO) {
      cout << "CPU time used = " << cputime << " seconds " << endl;
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

    if (0 == x10lib::__x10_my_place) __async__2 (temp, p); 
    else asyncSpawnInlineAgg (0, 2, temp, (x10_async_arg_t) p);
    
    asyncFlush (2, 2*sizeof(x10_async_arg_t));
    
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
    asyncRegister_t<1, Async0>(0);

    x10::array<x10::ref<x10::lang::String> >* args = x10::convert_args (ac, av);
    
    RandomAccess_Dist::main (*args);
    
    x10::free_args (args);
        
    return x10::exitCode;    
  }
}
