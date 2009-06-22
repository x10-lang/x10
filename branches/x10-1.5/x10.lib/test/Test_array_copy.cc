/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: Test_array_copy.cc,v 1.2 2007-11-28 19:13:38 ganeshvb Exp $ 
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
  __closure__0__args (int offset) :
     _offset (offset) 
  { }
 
  int _offset;
};

struct __closure__0 : Closure 
{
  __closure__0 (int offset) :
    Closure (sizeof(__closure__0), 0),
    _args (offset)
  {}

  __closure__0__args _args;
};


void* arrayCopySwitch (int handler, void* buf)
{
  __closure__0__args* closure_args = (__closure__0__args*) buf;
  return data + closure_args->_offset;
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

   cout << "start " << r.ord (a) << endl;

    double* tmp = data + r.ord(a);
    for (int i = a0; i <= a1; i++) {
      double* tmp1 = tmp;
      for (int j = b0; j <= b1; j++) {
              __closure__0 args (tmp1 - data);
              asyncArrayPut (tmp1,
                              &args,
                              (c1 - c0 + 1) * sizeof (double),
			      1,
			      NULL);
              tmp1 += lda[1];
        }

        tmp += lda[0];
    }
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
  
 
  cout << "Test_array_copy PASSED" << endl;
  
  x10lib::Finalize();
  
  return 0;
}
