/*
 * (c) Copyright IBM Corporation 2007
 *
 * x10lib version of RandomAccess
 * Author : Ganesh Bikshandi
 */

/* $Id: RandomAccess_spmd.1.cc,v 1.1 2007-06-28 09:55:00 ganeshvb Exp $ */

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
  __async__2__args (glong_t _captVar1, glong_t _captVar2) 
     : captVar1 (_captVar1),
      captVar2 (_captVar2)
     {}

  glong_t captVar1;
  glong_t captVar2;
};



void
inline __async__0 (__async__0__args args)
{
  glong_t ran = args.captVar1;
  GLOBAL_SPACE.Table->update (ran);
}

void
inline __async__1 (__async__1__args args)
{
  glong_t ran = args.captVar1;
  GLOBAL_SPACE.Table->verify (ran);
}

void
__async__2 (__async__2__args args)
{
  GLOBAL_SPACE.SUM[(int)(args.captVar2)] = args.captVar1;
}

void
asyncSwitch (x10_async_handler_t h, void* arg, int niter)
{
    char* args = (char*) arg;
    switch (h) {
    case 0: 
      for (int i = 0; i < niter; i++) {
        __async__0(*((__async__0__args*)args));
        args += sizeof(__async__0__args);
       }
      break;
    case 1:
      for (int i = 0; i < niter; i++) {
        __async__1(*((__async__1__args*)args));
        args += sizeof(__async__1__args);
      }
      break;
    case 2:
      for (int i = 0; i < niter; i++) {
        __async__2(*((__async__2__args*)args));
        args += sizeof(__async__2__args);
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

  RandomAccess_Dist::UNIQUE = Dist<1>::makeUnique();
  RandomAccess_Dist::NUMPLACES = numPlaces();
  RandomAccess_Dist::PLACEIDMASK = numPlaces()-1;

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
  if (__x10_my_place == 0) {
    
    /* Print parameters for run */
    if (doIO) {
      cout << "Distributed table size  = 2 ^ " << logTableSize 
	   << "*" << NUMPLACES << "=" << tableSize * NUMPLACES << " words" << endl;
      cout << "Number of total updates = " << 4 * tableSize * NUMPLACES << endl; 
    }
    
    /* Begin time here */
    cputime = -mysecond();
  }
  
  //RandomAccessUpdate (logTableSize, embarrassing, GLOBAL_SPACE.Table);
  
  const glong_t NumUpdates = tableSize*4;
  
  if (VERIFY == VERIFICATION_P) { 
    glong_t ran = HPCC_starts (__x10_my_place*NumUpdates);
    for (glong_t i = 0; i < NumUpdates; i++) {
      int placeID;
      if (embarrassing)
	placeID = __x10_my_place;
      else
	placeID = (int) ((ran>>logTableSize) & PLACEIDMASK);
      
      glong_t temp = ran;
      ran = ((ran << 1) ^ ((sglong_t) ran < 0 ? POLY : 0));     
      
      assert (UNIQUE->place(placeID) == placeID);
      __async__1__args a(temp);
      if (placeID == __x10_my_place) __async__1(a);
      else asyncSpawnInlineAgg(placeID, 1, &a, sizeof(a));
    } 
    asyncFlush(1, sizeof(__async__1__args));
  } else {   
    glong_t ran = HPCC_starts (__x10_my_place*NumUpdates);
    for (glong_t i = 0; i < NumUpdates; i++) {
      int placeID;
      if (embarrassing)
	placeID = __x10_my_place;
      else
	placeID = (int) ((ran>>logTableSize) & PLACEIDMASK);
      
      glong_t temp = ran;
      ran = ((ran << 1) ^ ((sglong_t) ran < 0 ? POLY : 0));     
      __async__0__args a(temp);
      if (placeID == __x10_my_place) __async__0(a);
      else asyncSpawnInlineAgg (placeID, 0, &a, sizeof(a));
    }   
    
    asyncFlush (0, sizeof(__async__0__args));
  }
  
  
  SyncGlobal();
  if (__x10_my_place == 0) {
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
    if (__x10_my_place == 0) { 
      for (int i = 0; i <  NUMPLACES; i++) {
	glong_t ran = HPCC_starts (i * NumUpdates);
	for (glong_t i = 0; i < NumUpdates; i++) {
	  int placeID;
	  if (embarrassing)
	    placeID = __x10_my_place;
	  else
	    placeID = (int) ((ran>>logTableSize) & PLACEIDMASK);
	  const glong_t temp = ran;
	  ran = (ran << 1) ^ ((long) ran < 0 ? POLY : 0);
          __async__0__args a (temp);
	  if (placeID == __x10_my_place) __async__0 (a);
	  else asyncSpawnInlineAgg (placeID, 0, &a, sizeof(a));
	}
      }
      asyncFlush (0, sizeof(__async__0__args));
    }
    
    SyncGlobal();    
  }

  if (VERIFY > 0) {
    const glong_t NumUpdates = tableSize*4*NUMPLACES;
   
    int p = __x10_my_place; 
    if (p == 0) {
      GLOBAL_SPACE.SUM = new glong_t [NUMPLACES];
    }
    
    glong_t sum =0;
    for (glong_t i = 0; i < tableSize; i++) 
      sum += GLOBAL_SPACE.Table->array[i];
  
    __async__2__args a (sum, p); 
    if (p == 0) __async__2 (a);
    else asyncSpawnInlineAgg (0, 2, &a, sizeof(a));
    
    asyncFlush (2, sizeof(__async__2__args));
    
    SyncGlobal();
   
    cout << "sum : " << sum << endl;

    if (__x10_my_place == 0) {
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


extern "C" {
  int main (int ac, char* av[])
  {
    //Init(NULL, 0);
    x10::array<x10::ref<x10::lang::String> >* args = x10::convert_args (ac, av);

    RandomAccess_Dist::main (*args);

    x10::free_args (args);

    //Finalize();

    return x10::exitCode;
  }
}

