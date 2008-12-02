/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: Test_gen_array_copy.cc,v 1.1 2007-11-12 07:56:59 ganeshvb Exp $ 
 * This file is part of X10 Runtime System.
 */

#include <iostream>
#include <x10/xassert.h>
#include <x10/x10lib.h>
#include "lapi.h"

using namespace std;
using namespace x10lib;

double* data;
const int* lda;

struct __closure__0__args
{
  __closure__0__args (int rank, Point<3> origin, Point<3> diagonal) :		      
     _rank (rank)
  {
    for (int i = 0; i < rank; i++) {
      _origin[i] = origin.value(i);
      _diagonal[i] = diagonal.value(i);
    }
    
  }

  int _rank;
  int _origin[7];
  int _diagonal[7];
};

struct __closure__0 : Closure 
{
  __closure__0 (int rank, Point<3> origin, Point<3> diagonal) : 
    Closure (sizeof(__closure__0), 0),
    _args (rank, origin, diagonal)
  {    
  }

  __closure__0__args _args;
};


lapi_vec_t* genArrayCopySwitch (int handler, void* buf)
{
  __closure__0__args* closure_args = (__closure__0__args*) buf;
  
  lapi_vec_t* vec = getIOVector (data, 3, (int*) lda, sizeof (double), closure_args->_origin, closure_args->_diagonal);
  
  return vec;
}

int 
main (int argc, char* argv[])
{

  int X = 13;

  int Y = 11;

  int Z = 5;

  int N = (X+1) * (Y+1) * (Z+1);
  
  x10lib::Init(NULL,0);
  
  data = new double[N];
  int rank = 3;
  Point<3> origin (0, 0, 0);
  Point<3> diagonal (X, Y, Z);
  RectangularRegion<3> r (origin, diagonal);

  lda = r.lda();
  
  for (int i = 0; i <= X; i++)
      for (int j = 0; j <= Y; j++)
	for (int k = 0; k <= Z; k++)
	  data [i * lda[0] + j * lda[1] + k] = 0;
  
  x10lib::SyncGlobal();
  
  int a0 = 2;
  int b0 = 3;
  int c0 = 2;
  int a1 = 7;
  int b1 = 10;
  int c1 = 6; 
  if (__x10_my_place == 0) {
    
    for (int i = 0; i <= X; i++)
      for (int j = 0; j <= Y; j++)
	for (int k = 0; k <= Z; k++)
	  data [i * lda[0] + j * lda[1] + k] = i * lda[0] + j * lda[1] + k;
   
    int a_values[] = {a0, b0, c0};
    int b_values[] = {a1, b1, c1};

    Point<3> a (a_values);
    Point<3> b (b_values);
    RectangularRegion<3> s (a, b);

    __closure__0 args (3, a, b);

    lapi_vec_t* vec = getIOVector (data, 3, lda, sizeoef(double), a_values, b_values);
    
    asyncArrayPut (vec,
		   &args,
		   1);

    freeIOVector (vec);

  } 
  
  x10lib::SyncGlobal();
  
  if (__x10_my_place) 
    {
      for (int i = a0; i <= a1; i++)
        for (int j = b0; j <= b1; j++)
         for (int k = c0; k <= c1; k++) 
	    
	    {
	       assert (data [i * lda[0] + j * lda[1] + k] == i * lda[0]+ j * lda[1] + k);
	    }
    }
  
 
  cout << "Test_array_table PASSED" << endl;
  
  x10lib::Finalize();
  
  return 0;
}
