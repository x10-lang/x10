/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_async_agg.cc,v 1.1 2007-06-08 13:51:42 srkodali Exp $ */

#include <iostream>
#include <x10/xassert.h>
#include <x10/x10lib.h>

using namespace std;
using namespace x10lib;

int m = 5;

async_arg_t I = 0;
async_arg_t K = 0;

void async0 (async_arg_t arg0)
{
  I += arg0 *  m;
  K++;
}
int asyncSwitch (async_handler_t h, void* arg, size_t size) 
{
  async_arg_t* args = (async_arg_t*) arg;
  int niter = size / sizeof(async_arg_t);
  switch (h) {
  case 0:
  for (int i = 0; i < niter ;i++)
    async0 (*args++);
  }
}



typedef void (*void_func_t) ();

int 
main (int argc, char* argv[])
{
  int N = 2048;

  x10lib::Init(NULL,0);

   for (place_t target = 0; target < numPlaces(); target++)
     for (int64_t i = 0; i < N; i++)
       asyncSpawnInlineAgg (target, 0,  i);
     
   asyncFlush (0);

     
  x10lib::Gfence (); 
  cout << here() << " I " << I << " " << K << endl;
  assert (I == numPlaces() * N * (N-1) / 2 * m);
  assert (K == numPlaces() * N) ;

  cout << "Test_async_agg PASSED" << endl;

  x10lib::Finalize();

  return 0;
}
