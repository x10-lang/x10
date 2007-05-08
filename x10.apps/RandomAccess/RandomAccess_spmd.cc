/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: RandomAccess_spmd.cc,v 1.1 2007-05-08 10:48:15 ganeshvb Exp $ */

#include <iostream>

#include <x10/x10lib.h>
#include <x10/array.h>

using namespace std;

func_t handlers[128];

class localTable;

class RandomAccess_Dist {
  
  class localTable {
    
  private:
    const uint64_t tableSize;
    const uint64_t mask;
  public:
    uint64_t *array;
    
  public:
    localTable (uint64_t size) :
      tableSize (size),
      mask (size-1),
      array (new uint64_t [size]) 
    { memset (array, 0, sizeof(uint64_t) * size);}
    
    void update (uint64_t ran) {
      uint64_t off = ran & mask;
      assert (off >=0 && off < tableSize);
      array[off]  ^= ran;
    }

    void verify (uint64_t ran) {
      uint64_t off = ran & mask;
      assert (off >=0 && off < tableSize);
      array[off]++;
    }
  };  
  
  static double mysecond () {
    assert (false);
  }
  
private :
  static const uint64_t POLY = 0x0000000000000007; 
  static const long PERIOD = 131762457669539401;
 
public:

  static localTable* Table; 
  static Dist<1>*  UNIQUE;
  static int NUMPLACES;
  static int PLACEIDMASK;
  
public:
  static uint64_t HPCC_starts(int64_t n)
  {
    int i, j;
    uint64_t m2[64];
    uint64_t temp, ran;
    
    while (n < 0) n += PERIOD;
    while (n > PERIOD) n -= PERIOD;
    if (n == 0) return 0x1;
    
    temp = 0x1;
    for (i=0; i<64; i++) {
      m2[i] = temp;
      temp = (temp << 1) ^ ((int64_t) temp < 0 ? POLY : 0);
      temp = (temp << 1) ^ ((int64_t) temp < 0 ? POLY : 0);
    }
    
    for (i=62; i>=0; i--)
      if ((n >> i) & 1)
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
	ran = (ran << 1) ^ ((int64_t) ran < 0 ? POLY : 0);
    }
    
    return ran;
  }

  static void verify (uint64_t LogTableSize)
  {
    const uint64_t TableSize = (1UL<<LogTableSize);
    const uint64_t numUpdates = TableSize*4;

    uint64_t sum =0;
    for (uint64_t i = 0; i < TableSize; i++) 
      sum += Table->array[i];

    cout << "verify: " << sum << endl;
  }
 
  static void RandomAccessUpdate (const uint64_t LogTableSize, const bool Embarrasing, localTable* Table) {

    const uint64_t TableSize = (1UL<<LogTableSize);
    const uint64_t numUpdates = TableSize*4;
    
    //start_finish();
   
    if (VERIFY) { 
      uint64_t ran = HPCC_starts (here()*numUpdates);
      for (uint64_t i = 0; i < numUpdates; i++) {
        int placeID;
        if (Embarrasing)
	  placeID = here();
        else
	  placeID = (int) ((ran>>LogTableSize) & PLACEIDMASK);
      
        uint64_t temp = ran;
        ran = ((ran << 1) ^ ((long) ran < 0 ? POLY : 0));     
        asyncSpawn<2, true> (UNIQUE->place(placeID), 1, temp);
      } 
    } else {   
      uint64_t ran = HPCC_starts (here()*numUpdates);
      for (uint64_t i = 0; i < numUpdates; i++) {
        int placeID;
        if (Embarrasing)
          placeID = here();
        else
          placeID = (int) ((ran>>LogTableSize) & PLACEIDMASK);
    
        uint64_t temp = ran;
        ran = ((ran << 1) ^ ((long) ran < 0 ? POLY : 0));     
        asyncSpawn<2, true> (UNIQUE->place(placeID), 0, temp);
      }   
    }

    Gfence();
    //stop_finish();    
  }
  
  static void main (int argc, char* argv[])
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
    for (int q = 0; q < argc; ++q) {
      if (strcmp(argv[q], "-o") == NULL) {
	doIO = true;
      }

      if (strcmp (argv[q], "-e") == NULL) {
	embarrasing = true;
      }

      if (strcmp (argv[q], "-m") == NULL) {
	++q;
	logTableSize = atoi (argv[q]);
      }
    }


    const uint64_t tableSize = (1UL << logTableSize);
    Table = new localTable (tableSize);
            
    //timing

    Gfence();
    RandomAccessUpdate (logTableSize, embarrasing, Table);
    cout << "End " << endl;
    verify (logTableSize); 

    //printing and reporting 
  }

  static void 
  async0 (async_arg_t arg0)
  {
    uint64_t ran = arg0; 
    
    //cout << "Inside async0 " << here() << " " << Table << " " << ran <<endl;
    Table->update (ran);
  }
  
  static void 
  async1 (async_arg_t arg0)
  {
    uint64_t ran = arg0; 
    
    //cout << "Inside async1 " << here() << " " << Table << " " << ran <<endl;
    Table->verify (ran);
  }
  
};


//DUMMY initialization
RandomAccess_Dist::localTable* RandomAccess_Dist::Table = NULL;
Dist<1>* RandomAccess_Dist::UNIQUE = Dist<1>::makeUnique();
int RandomAccess_Dist::NUMPLACES = numPlaces();
int RandomAccess_Dist::PLACEIDMASK = NUMPLACES-1;

int main (int argc, char* argv[])
{
  handlers[0].fptr = (void_func_t) RandomAccess_Dist::async0;
  handlers[1].fptr = (void_func_t) RandomAccess_Dist::async1;

  Init (handlers, 2);
  Setenv (INTERRUPT_SET, 0);
  
  RandomAccess_Dist::main (argc, argv);

  Finalize();

  return 0;
}

