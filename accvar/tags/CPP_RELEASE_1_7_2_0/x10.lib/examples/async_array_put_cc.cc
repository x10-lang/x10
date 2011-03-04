/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: async_array_put_cc.cc,v 1.2 2007-12-10 17:22:27 srkodali Exp $ 
 * This file is part of X10 Runtime System.
 */

/** Example to illustrate asyncArrayPut. A (logical) 2D C++ array, 
 ** array_a, is transposed and the result is stored in array_b. The
 ** arrays are assumed to be distributed in SPMD mode and the
 ** transposition is a "global" transposition.  
 **
 ** Example also illustrates the usage of Closures and arrayCopySwitch.
 **
 ** Closure is an object that can be used to pack the environment
 ** variables of the source, required at the desinatin (e.g. destination
 ** offset of the remote pointer, in this case).
 **
 ** arrayCopySwitch is a call back used by x10lib. arrayCopySwitch should 
 ** return the base address of destination pointer, from where
 ** the copy starts, to x10lib.
 **/

#include <iostream>
#include <x10/timers.h>
#include <x10/xassert.h>
#include <x10/x10lib.h>
#include "lapi.h"

using namespace std;
using namespace x10lib;

double* array_a;
double* array_b;


/* a filed of closure */
struct __closure__0__args
{
  __closure__0__args (int offset)
    : _offset (offset)
  {}

  int _offset;
};

/* closure */
struct __closure__0 : Closure 
{
  __closure__0 (int offset) : 
    Closure (sizeof(__closure__0), 0),
    _args (offset)
  {    
  }

  __closure__0__args _args;
};


/*array copy switch routine */
extern "C"
void* arrayCopySwitch (int handler, void* buf)
{
  __closure__0__args* closure_args = (__closure__0__args*) buf;
  
  return array_b + closure_args->_offset;
}

int 
main (int argc, char* argv[])
{
  /* initialize x10lib */
  x10lib::Init(NULL,0);
  
  /* set the dimesions for the 2D array */
  int SQRT_N = 128;
  int X = SQRT_N /  __x10_num_places;
  int Y = SQRT_N;
  int nRows = SQRT_N / __x10_num_places;
  int N = X * Y; 

  /* allocate the arrays as 1-D C++ arrays*/
  array_a = new double[N];
  array_b = new double[N];
  
  /* initialize the arrays */
  int P = x10lib::__x10_my_place;  
  for (int i = 0; i < X; i++)
    for (int j = 0; j < Y; j++)
      {
	array_a [i * Y + j] = (i + P * nRows) * Y + j;
       	array_b [i * Y + j] =  0;
      }
  
  /* sync up */
  x10lib::SyncGlobal();

  /* transposition */
  for (int k=0; k <__x10_num_places; ++k) { //for each block

    /* step 1: in-place transposition of sub-blocks*/
    int colStartA = k*nRows;
    for (int i=0; i<nRows; ++i)
      for (int j=i; j<nRows; ++j) {
	int idxA = SQRT_N * (i) + colStartA + j;
	int idxB = SQRT_N * (j) + colStartA + i;

	double tmp0 = array_a[idxA];	
	array_a[idxA] = array_a[idxB]; 	
	array_a[idxB] = tmp0; 

      }
      
    /* step 2 : asyncArrayPuts*/ 
    for (int i=0; i<nRows;++i) {
      int srcI=  i*SQRT_N + colStartA;
      int destI= i*SQRT_N + P*nRows;                                      
     
      __closure__0 closure (destI);
      asyncArrayPut (array_a + srcI, &closure, nRows*sizeof(double), k);
    }
  }

  
  x10lib::SyncGlobal();

  /* verify */
  for (int i = 0; i < X; i++)
    for (int j = 0; j < Y; j++)
      {
	assert (array_b [i * Y + j] == j * Y + i + P * nRows);       	
      }

  delete [] array_a;
  delete [] array_b;
  /* finalize x10lib */
  x10lib::Finalize();
  
  return 0;
}
