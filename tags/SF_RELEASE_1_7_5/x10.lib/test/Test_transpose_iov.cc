/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: Test_transpose_iov.cc,v 1.5 2008-01-06 03:28:51 ganeshvb Exp $ 
 * This file is part of X10 Runtime System.
 */

/* Transpose a 2D array */
#include <iostream>
#include <x10/xassert.h>
#include <x10/x10lib.h>
#include "lapi.h"

using namespace std;
using namespace x10lib;

double* data;
double* data2;
const int* lda;

struct __closure__0__args
{
  __closure__0__args (int rank, Point<2> origin, Point<2> diagonal) :		      
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
  __closure__0 (int rank, Point<2> origin, Point<2> diagonal) : 
    Closure (sizeof(__closure__0), 0),
    _args (rank, origin, diagonal)
  {    
  }

  __closure__0__args _args;
};


lapi_vec_t* genArrayCopySwitch (int handler, void* buf)
{
  __closure__0__args* closure_args = (__closure__0__args*) buf;
  
  lapi_vec_t* vec = GetIOVector (data2, 2, (int*) lda, sizeof (double), closure_args->_origin, closure_args->_diagonal);
 

  return vec;
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

  lda = r.lda();

  x10lib::SyncGlobal();
  
  for (int k=0; k <__x10_num_places; ++k) { //for each block

    /* in-place transpostion of sub-blocks */
    int colStartA = k*nRows;
    for (int i=0; i<nRows; ++i)
      for (int j=i; j<nRows; ++j) {
	int idxA = SQRT_N * (i) + colStartA + j;
	int idxB = SQRT_N * (j) + colStartA + i;

	double tmp0 = data[idxA];	
	data[idxA] = data[idxB]; 	
	data[idxB] = tmp0; 

      }
       

    /* single asyncArrrayCopy using iovectors */ 
    int a_values[2] = {0, k * nRows};
    int b_values[2] = {X-1, (k+1) * nRows - 1};
    lapi_vec_t* vec = GetIOVector (data, 2, (int*) lda, sizeof(double), a_values, b_values);
    int aprime_values[2] = {0, P * nRows};
    int bprime_values[2] = {X-1, (P+1) * nRows - 1};
    Point<2> a (aprime_values);
    Point<2> b (bprime_values);
    __closure__0 args (2, a, b);
    AsyncArrayPut (vec, &args, k);
    FreeIOVector (vec);
  }
  
  x10lib::SyncGlobal();
  
  for (int i = 0; i < X; i++)
    for (int j = 0; j < Y; j++)
      {
	assert (data2 [i * Y + j] == j * Y + i + P * nRows);       	
      }
   
  cout << "Test_transpse_iov PASSED" << endl;
  
  x10lib::Finalize();
  
  return 0;
}
