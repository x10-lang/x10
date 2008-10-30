/*
 * (c) Copyright IBM Corporation 2007
 *
 * x10lib version of RandomAccess  
 * $Id: RandomAccess.h,v 1.1 2007-12-10 07:58:00 ganeshvb Exp $ 
 */

#include <x10/x10lib.h>
#include <x10/timers.h>

#include "x10lang.h"

#include <iostream>

using namespace std;

using namespace x10lib;

using namespace x10::lang;

using namespace java::lang;

/* This gives the opportunity to use either int64_t or uint64_t */

typedef long glong_t;
typedef long sglong_t;

//typedef int32_t glong_t;
//typedef int32_t sglong_t;

class RandomAccess {
  
public:  
  
  class localTable {
    
  public:
    const glong_t tableSize;
    const glong_t mask;
    glong_t *array;
    
  public:
    localTable (glong_t size) :
      tableSize (size),
      mask (size-1),
      array (new glong_t [size])
    { }
        
    inline void update (glong_t ran); 

    inline void verify (glong_t ran);
  };  
 
private:
 
  static double mysecond (); 

  static const glong_t POLY = 0x0000000000000007LL;  
  static const glong_t PERIOD = 131762457669539401LL;
 
  static const Dist<1>*  UNIQUE;
  static int NUMPLACES;
  static int PLACEIDMASK;

  static const int UPDATE = 0;
  static const int VERIFICATION_P = 1;  
  static const int UPDATE_AND_VERIFICATION = 2;

  static glong_t HPCC_starts(sglong_t n);

  static void Verify (glong_t LogTableSize, bool embarrassing, localTable* Table);
    
public:

  static void main (x10::array<x10::ref<x10::lang::String> >& args);

};

/* global state */

struct 
{
  RandomAccess::localTable* Table; 
  glong_t* SUM;
} GLOBAL_STATE;

extern "C" 
{
  extern int main (int ac, char** av);
}

// Local Variables:
// mode: C++
// End:
