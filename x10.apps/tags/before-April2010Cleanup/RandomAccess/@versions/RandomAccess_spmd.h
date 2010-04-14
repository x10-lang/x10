/*
 * (c) Copyright IBM Corporation 2007
 *
 * x10lib version of RandomAccess  
 * Author : Ganesh Bikshandi
 */

/* $Id: RandomAccess_spmd.h,v 1.3 2007-10-24 08:34:26 ganeshvb Exp $ */

/* Main version */
#include <iostream>

#include "x10lang.h"
#include <x10/x10lib.h>

using namespace std;
using namespace x10lib;
using namespace x10::lang;
using namespace java::lang;

/* This gives the opportunity to use either int64_t or uint64_t */

typedef long glong_t;
typedef long sglong_t;

//typedef int32_t glong_t;
//typedef int32_t sglong_t;

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
 
  const static Dist<1>*  UNIQUE;
  static int NUMPLACES;
  static int PLACEIDMASK;

  static const int UPDATE = 0;
  static const int VERIFICATION_P = 1;  
  static const int UPDATE_AND_VERIFICATION = 2;

  static glong_t HPCC_starts(sglong_t n);

  static void Verify (glong_t LogTableSize, bool embarrassing, localTable* Table);
  
  //static void RandomAccessUpdate (const glong_t LogTableSize, const bool Embarrasing, localTable* Table); 
  
public:
  static void main (x10::array<x10::ref<x10::lang::String> >& args);

};

struct 
{
  RandomAccess_Dist::localTable* Table; 
  glong_t* SUM;
}GLOBAL_SPACE;

extern "C" 
{
  extern int main (int ac, char** av);
}

// Local Variables:
// mode: C++
// End:
