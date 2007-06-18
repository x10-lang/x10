/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_async_agg.cc,v 1.2 2007-06-18 10:33:48 ganeshvb Exp $ */

#include <iostream>
#include <x10/xassert.h>
#include <x10/x10lib.h>

using namespace std;
using namespace x10lib;

uint64_t m = 5;

async_arg_t I = 0;
async_arg_t K = 0;
async_arg_t J = 0;

void async0 (async_arg_t arg0)
{
  I += arg0 *  m;
}

void async1 ()
{
  K++;
}

void async2(async_arg_t arg0, async_arg_t arg1)
{
  J += arg0 * arg1; 
}

int asyncSwitch (async_handler_t h, void* arg, int niter) 
{

  //cout << "h.id: " << here() << "i: " << h.id << " "  << niter << endl;
  async_arg_t* args;
  switch (h) {
  case 0:
  args = (async_arg_t*) arg;
  for (int i = 0; i < niter ;i++)
    async0 (*args++);
   break;
  case 1:
  for (int i = 0; i < niter; i++)
    async1 ();
   break;
  case 2:
  args = (async_arg_t*) arg;
  for (int i = 0; i < niter; i++) {
    async2 (*args, *(args+1));
    args = args + 2;
  }
  break;
  }
 
}



typedef void (*void_func_t) ();

struct args {


};
int 
main (int argc, char* argv[])
{
  int N = 2048;

  x10lib::Init(NULL,0);

  struct args arg;
   for (place_t target = 0; target < numPlaces(); target++)
     for (int64_t i = 0; i < N; i++) {
       asyncSpawnInlineAgg (target, 0,  i);
       asyncSpawnInlineAgg (target, 1, &arg, 0);
       asyncSpawnInlineAgg (target, 2, i, m);
    } 
   asyncFlush (0, 0);
   asyncFlush (1, 0);
   asyncFlush (2, 0);

     
  x10lib::Gfence (); 
  assert (I == numPlaces() * N * (N-1) / 2 * m);
  assert (J == numPlaces() * N * (N-1) / 2 * m);
  assert (K == numPlaces() * N) ;
 
//  cout << "I " << I << " " << J << " " << K << endl;
  cout << "Test_async_agg PASSED" << endl;

  x10lib::Finalize();

  return 0;
}
