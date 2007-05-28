/*
 * (c) Copyright IBM Corporation 2007
 *
 * x10lib version of RandomAccess
 * Author : Ganesh Bikshandi
 */

/* $Id: RandomAccess_spmd.cc,v 1.11 2007-05-28 12:46:21 ganeshvb Exp $ */

#include "RandomAccess_spmd.h"
#include "timers.h"


void
inline async0 (async_arg_t arg0)
{
  glong_t ran = arg0;
  GLOBAL_SPACE.Table->update (ran);
}

void
inline async1 (async_arg_t arg0)
{
  glong_t ran = arg0;
  GLOBAL_SPACE.Table->verify (ran);
}

void
async2 (async_arg_t arg0, async_arg_t arg1)
{
  GLOBAL_SPACE.SUM[(int)arg1] = arg0;
}
int
asyncSwitch (async_handler_t h, async_arg_t* args, int niter)
{
  for (int i = 0; i < niter; i++)
    switch (h) {
    case 0: 
      async0(*args++);
      break;
    case 1:
      async1(*args++);
      break;
    case 2:
      async2(*args++, *args++);
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
RandomAccess_Dist::Verify (const glong_t LogTableSize, const bool Embarrassing,
                           localTable* Table)
{
  //cout << "Before "<< endl;
  const glong_t tableSize = (1UL << LogTableSize);
  const glong_t numUpdates = tableSize*4;
  
  if (here() == 0) { 
    for (int i = 0; i <  NUMPLACES; i++) {
      glong_t ran = HPCC_starts (i * numUpdates);
      for (glong_t i = 0; i < numUpdates; i++) {
	int placeID;
	if (Embarrassing)
	  placeID = here();
	else
	  placeID = (int) ((ran>>LogTableSize) & PLACEIDMASK);
	const glong_t temp = ran;
	ran = (ran << 1) ^ ((long) ran < 0 ? POLY : 0);
	if (placeID == here()) async0 (temp);
	else asyncSpawnInlineAgg (placeID, 0, temp);
      }
    }
    asyncFlush (0, 1);
  }
  
  Gfence();
}
void  
RandomAccess_Dist::main (x10::array<String>& args)
{

  RandomAccess_Dist::UNIQUE = Dist<1>::makeUnique();
  RandomAccess_Dist::NUMPLACES = numPlaces();
  RandomAccess_Dist::PLACEIDMASK = numPlaces()-1;
    
  if ((NUMPLACES & (NUMPLACES-1)) > 0) {
    cout << "the number of places must be a power of 2!";
    exit (-1);
  } 
  
  bool doIO=false; 
  bool embarrasing = false;
  int logTableSize = 10;
  
  int VERIFY = UPDATE;
  for (int q = 0; q < args.length; ++q) {
    if (args[q].equals ("-o")) {
      doIO = true;
    }
    
    if (args[q].equals("-e")) {
      embarrasing = true;
    }
    
    if (args[q].equals("-m")) {
      ++q;
      logTableSize = java::lang::Integer::parseInt(args[q]);
    }

    if (args[q].equals("-v")) {
      ++q;
      VERIFY = java::lang::Integer::parseInt(args[q])%3;
    }
  }
  
  const glong_t tableSize = (1UL << logTableSize);
  GLOBAL_SPACE.Table = new localTable (tableSize);
  Gfence();
  
  double GUPs;
  double cputime;    /* CPU time to update table */
  if (here() == 0) {
    
    /* Print parameters for run */
    if (doIO) {
      cout << "Distributed table size  = 2 ^ " << logTableSize 
	   << "*" << NUMPLACES << "=" << tableSize * NUMPLACES << " words" << endl;
      cout << "Number of total updates = " << 4 * tableSize * NUMPLACES << endl; 
    }
    
    /* Begin time here */
    cputime = -mysecond();
  }
  
  //RandomAccessUpdate (logTableSize, embarrasing, GLOBAL_SPACE.Table);
  
  const glong_t numUpdates = tableSize*4;
  
  if (VERIFY == VERIFICATION_P) { 
    glong_t ran = HPCC_starts (here()*numUpdates);
    for (glong_t i = 0; i < numUpdates; i++) {
      int placeID;
      if (embarrasing)
	placeID = here();
      else
	placeID = (int) ((ran>>logTableSize) & PLACEIDMASK);
      
      glong_t temp = ran;
      ran = ((ran << 1) ^ ((sglong_t) ran < 0 ? POLY : 0));     
      
      assert (UNIQUE->place(placeID) == placeID);
      if (placeID == here()) async1(temp); 
      else asyncSpawnInlineAgg(placeID, 1, temp);
    } 
    asyncFlush(1, 1);
  } else {   
    glong_t ran = HPCC_starts (here()*numUpdates);
    for (glong_t i = 0; i < numUpdates; i++) {
      int placeID;
      if (embarrasing)
	placeID = here();
      else
	placeID = (int) ((ran>>logTableSize) & PLACEIDMASK);
      
      glong_t temp = ran;
      ran = ((ran << 1) ^ ((sglong_t) ran < 0 ? POLY : 0));     
      if (placeID == here()) async0(temp);
      else asyncSpawnInlineAgg (placeID, 0, temp);
    }   
    
    asyncFlush (0, 1);
  }
  
  
  Gfence();
  if (here() == 0) {
    /* End time section */
    cputime += mysecond();
    
    /* make sure no division by zero */
    GUPs = (cputime > 0.0 ? 1.0 / cputime : -1.0);
    GUPs *= 1e-9*(4*tableSize*NUMPLACES);
    /* Print timing results */
    if (doIO) {
      cout << "CPU time used = " << cputime << " seconds " << endl;
    }
    cout << GUPs << " Billion (10^9) Updates  per second [GUP/s]" << endl;
  }
  
  if (VERIFY == UPDATE_AND_VERIFICATION){
    if (here() == 0) { 
      for (int i = 0; i <  NUMPLACES; i++) {
	glong_t ran = HPCC_starts (i * numUpdates);
	for (glong_t i = 0; i < numUpdates; i++) {
	  int placeID;
	  if (embarrasing)
	    placeID = here();
	  else
	    placeID = (int) ((ran>>logTableSize) & PLACEIDMASK);
	  const glong_t temp = ran;
	  ran = (ran << 1) ^ ((long) ran < 0 ? POLY : 0);
	  if (placeID == here()) async0 (temp);
	  else asyncSpawnInlineAgg (placeID, 0, temp);
	}
      }
      asyncFlush (0, 1);
    }
    
    Gfence();    
  }

  if (VERIFY > 0) {
    const glong_t numUpdates = tableSize*4*NUMPLACES;
   
    int p = here(); 
    if (p == 0) {
      GLOBAL_SPACE.SUM = new glong_t [NUMPLACES];
    }
    
    glong_t sum =0;
    for (glong_t i = 0; i < tableSize; i++) 
      sum += GLOBAL_SPACE.Table->array[i];
   
    if (p == 0) async2 (sum ,p); 
    else asyncSpawnInlineAgg (0,2, sum, here());
    
    asyncFlush (2, 2);
    
    Gfence();
   
    cout << "sum : " << sum << endl;

    if (here() == 0) {
      glong_t globalSum = 0;
      for (int i = 0;i < NUMPLACES; i++) {
        globalSum += GLOBAL_SPACE.SUM[i];
      }
      if (VERIFY == VERIFICATION_P) {
	double missedUpdateRate = (double) (globalSum - numUpdates) / (double) numUpdates*100;
	cout << " the rate of missed updates " << missedUpdateRate << " % " << endl;
      }else {
	cout << " The global sum is " << globalSum << " (correct=0) " << endl;
      }
    }
  }
}


//DUMMY initialization
Dist<1>* RandomAccess_Dist::UNIQUE = Dist<1>::makeUnique();
int RandomAccess_Dist::NUMPLACES = numPlaces();
int RandomAccess_Dist::PLACEIDMASK = RandomAccess_Dist::NUMPLACES-1;

func_t handlers[] = {(void_func_t) async0,
                     (void_func_t) async1,
                     (void_func_t) async2};

extern "C" {
  int ::main (int ac, char* av[])
  {

    Init (handlers, 3); 

    x10::array<String>* args = x10::convert_args (ac, av);
    
    RandomAccess_Dist::main (*args);

    x10::free_args (args);
    
    Finalize();
    
    return x10::exitCode;
  }
}
