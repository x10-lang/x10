/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: Test_transpose.cc,v 1.6 2008-01-19 18:20:18 ganeshvb Exp $ 
 * This file is part of X10 Runtime System.
 */

/* Transpose a 2D array */

#include <iostream>
#include <x10/timers.h>
#include <x10/xassert.h>
#include <x10/x10lib.h>
#include "lapi.h"

using namespace std;
using namespace x10lib;

double* data;
double* data2;

struct __closure__0__args
{
  __closure__0__args (int offset)
    : _offset (offset)
  {}

  int _offset;
};

struct __closure__0 : Closure 
{
  __closure__0() :
    Closure (sizeof(__closure__0), 0),
  _args (0) {}

  __closure__0 (int offset) : 
    Closure (sizeof(__closure__0), 0),
    _args (offset)
  {    
  }

  __closure__0__args _args;
};


void* ArrayCopySwitch (int handler, void* buf)
{
  __closure__0__args* closure_args = (__closure__0__args*) buf;
  
  return data2 + closure_args->_offset;
}

int 
main (int argc, char* argv[])
{
  x10lib::Init(NULL,0);

  if (argc != 2)  {
     cout << "Syntax: ./a.out SQRTN " << endl; 
     exit (-1);
  }

  int SQRT_N = atoi (argv[1]);

  int X = SQRT_N /  __x10_num_places;

  int Y = SQRT_N;

  int nRows = SQRT_N / __x10_num_places;

  int N = X * Y; 
  
  data = new double[N];
  data2 = new double[N];

  int P = x10lib::__x10_my_place;

  for (int i = 0; i < X; i++)
    for (int j = 0; j < Y; j++)
      {
	data [i * Y + j] = (i + P * nRows) * Y + j;
       	data2 [i * Y + j] =  0;
      }
  

  int rank = 2;
  Point<2> origin (0, 0);
  Point<2> diagonal (X-1, Y-1);
  RectangularRegion<2> r (origin, diagonal);

  __closure__0* closure = new __closure__0 [nRows * __x10_num_places];

  x10lib::SyncGlobal();
  
  int n = 0;

  double timers[4];

  for (int i = 0; i < 4; i++) timers[i] = 0;

  timers[0] = nanoTime();

  for (int k=0; k <__x10_num_places; ++k) { //for each block

    timers[1] += nanoTime();
    /* In-place transposition of sub-blocks*/
    int colStartA = k*nRows;
    for (int i=0; i<nRows; ++i)
      for (int j=i; j<nRows; ++j) {
	int idxA = SQRT_N * (i) + colStartA + j;
	int idxB = SQRT_N * (j) + colStartA + i;

	double tmp0 = data[idxA];	
	data[idxA] = data[idxB]; 	
	data[idxB] = tmp0; 

      }

    timers[2] += nanoTime();
      
    /* remote array copy using nRows asyncArrayCopies*/ 
    for (int i=0; i<nRows;++i) {
      int srcI=  i*SQRT_N + colStartA;
      int destI= i*SQRT_N + P*nRows;                                      
     
      (closure + n)->_args._offset = destI;
      AsyncArrayIput (data + srcI, closure + n, nRows*sizeof(double), k);
      n++;
    }
    timers[3] += nanoTime();
  }
 
  x10lib::SyncGlobal();
  timers[4] = nanoTime();
 
  
  for (int i = 0; i < X; i++)
    for (int j = 0; j < Y; j++)
      {
	assert (data2 [i * Y + j] == j * Y + i + P * nRows);       	
      }

  cout << "*************** Summary BEGIN ***********************************" << endl
       << "***************** Timing (Seconds) BEGIN************************* " << endl
       << "Total Time: " << (timers[4] - timers[0]) * 1e-9 << endl    
       << "in-place transposition : " << (timers[2] - timers[1]) * 1e-9  << endl
       << "array copy : " << (timers[3] - timers[2]) * 1e-9 << endl
       << "***************** Timing (Seconds) END ************************* " << endl
       << "Total Memory (per place) : " << 2 * X * Y * sizeof(double) / (1024 * 1024) << "Mega Bytes" << endl
       << "*************** Summary END ***********************************" << endl ;
 
  cout << "Test_transpose PASSED" << endl;

  delete [] closure;
  x10lib::Finalize();
  
  return 0;
}
