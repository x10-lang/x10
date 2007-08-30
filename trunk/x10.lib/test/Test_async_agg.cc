/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_async_agg.cc,v 1.11 2007-08-30 14:19:15 ganeshvb Exp $ */

#include <iostream>
#include <x10/xassert.h>
#include <x10/x10lib.h>

#ifdef HC_ROUTING
#define ASYNC_SPAWN asyncSpawnInlineAgg_hc
#define ASYNC_FLUSH asyncFlush_hc
#else
#define ASYNC_SPAWN asyncSpawnInlineAgg
#define ASYNC_FLUSH asyncFlush
#endif

using namespace std;
using namespace x10lib;

x10_async_arg_t m = 5;

x10_async_arg_t I = 0;
x10_async_arg_t K = 0;
x10_async_arg_t J = 0;

void async0 (x10_async_arg_t arg0)
{
  I += arg0 *  m;
}

void async1 ()
{
  K++;
}

void async2(x10_async_arg_t arg0, x10_async_arg_t arg1)
{
  J += arg0 * arg1; 
}

void asyncSwitch (x10_async_handler_t h, void* arg, int niter) 
{

  x10_async_arg_t* args;
  switch (h) {
  case 0:
  args = (x10_async_arg_t*) arg;
  for (int i = 0; i < niter ;i++)
    async0 (*args++);
   break;
  case 1:
  for (int i = 0; i < niter; i++)
    async1 ();
   break;
  case 2:
  args = (x10_async_arg_t*) arg;
  for (int i = 0; i < niter; i++) {
    async2 (*args, *(args+1));
    args = args + 2;
  }
  break;
  }
 
}

typedef void (*void_func_t) ();

struct args 
{

};

int 
main (int argc, char* argv[])
{
  int N = 1024;

  x10lib::Init(NULL,0);

  struct args arg;
  int total=0;
  for (long i = 0; i < N; i++) {
    for (x10_place_t target = 0; target < __x10_num_places; target++, total++) {
      //       if (target == x10lib::here()) {
      //        async0 ((x10_async_arg_t) i);
      //        async1 ();
      //        async2 (i, m);
      //        continue; 
      //       }
//      ASYNC_SPAWN (target, 0, (x10_async_arg_t) i);
 //     ASYNC_SPAWN (target, 1, &arg, 0);

        ASYNC_SPAWN (target, 2, i, m);
      if ((total + 1) % (N/2) == 0) 
        ASYNC_FLUSH (2, 2*sizeof(x10_async_arg_t));
    }
  } 
 // ASYNC_FLUSH (0, sizeof(x10_async_arg_t));
 // ASYNC_FLUSH  (1, 0);
  ASYNC_FLUSH (2, 2*sizeof(x10_async_arg_t));
  
  
  x10lib::SyncGlobal(); 
  assert (I == __x10_num_places * N * (N-1) / 2 * m);
  assert (J == __x10_num_places * N * (N-1) / 2 * m);
  assert (K == __x10_num_places * N) ;
  
  cout << __x10_my_place << " I " << I << " " << J << " " << K << endl;
  cout << "Test_async_agg PASSED" << endl;
  
  x10lib::Finalize();

  return 0;
}
