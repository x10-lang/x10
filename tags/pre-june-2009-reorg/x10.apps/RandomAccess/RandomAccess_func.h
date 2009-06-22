/*
 * (c) Copyright IBM Corporation 2007
 *
 * x10lib version of RandomAccess  
 * Author : Ganesh Bikshandi
 */

/* $Id: RandomAccess_func.h,v 1.1 2007-05-23 11:37:04 ganeshvb Exp $ */

#include <iostream>

#include <x10/x10lib.h>
#include <x10/array.h>
#include <x10/aggregate.tcc>


#include "x10lang.h"
#include "x10lang.cc"

using namespace std;
using namespace x10lib;
using namespace x10::lang;

/* This gives the opportunity to use either int64_t or uint64_t */

typedef int64_t glong_t;
typedef int64_t sglong_t;

#ifdef VERIFY
bool doVerify = true;
#else
bool doVerify = false;
#endif

class RandomAccess_Dist {

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
      array (makeLocalArray<glong_t, 1>(size)) 
    { }
        
    inline void update (glong_t ran); 

    inline void verify (glong_t ran);
  };  
 
private:
 
  static double mysecond (); 
  static const glong_t POLY = 0x0000000000000007LL;  
  static const glong_t PERIOD = 131762457669539401LL;
 
  static Dist<1>*  UNIQUE;
  static int NUMPLACES;
  static int PLACEIDMASK;
  
  static glong_t HPCC_starts(sglong_t n);

  static void Verify (glong_t LogTableSize, bool embarrassing, localTable* Table);
  
  //static void RandomAccessUpdate (const glong_t LogTableSize, const bool Embarrasing, localTable* Table); 
  
  public:
  static void main (x10::array<String>& args);

};

struct 
{
  RandomAccess_Dist::localTable* Table; 
  glong_t* SUM;
}GLOBAL_SPACE;

extern "C" {
  extern int main (int ac, char** av);
}

// Local Variables:
// mode: C++
// End:
