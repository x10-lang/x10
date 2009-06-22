/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_async_agg_func.cc,v 1.4 2007-10-11 08:27:16 ganeshvb Exp $ */

#include <iostream>

#include <x10/x10lib.h>
#include <x10/aggregate.tcc>

using namespace std;
using namespace x10lib;

int m = 5;

x10_async_arg_t I = 0;
x10_async_arg_t K = 0;

struct Async0 
{
  void operator () (x10_async_arg_t arg0)
  {
    I += arg0 * m;
    K++;
  }
};

void asyncSwitch (x10_async_handler_t h, x10_async_arg_t* args)
{
}

int 
main (int argc, char* argv[])
{
  int N = 2048;

  x10lib::Init(NULL,0);

  asyncRegister_t<1, Async0> (0);

   for (x10_place_t target = 0; target < numPlaces(); target++)
     for (long i = 0; i < N; i++)
       asyncSpawnInlineAgg_t<Async0> (target, 0, i);
     
   asyncFlush_t<1> (0);
     
  x10lib::SyncGlobal (); 
//  cout << here() << " I " << I << " " << K << endl;
  assert (I == numPlaces() * N * (N-1) / 2 * m);
  assert (K == numPlaces() * N) ;

  cout << "Test_async_agg_func PASSED" << endl;

  x10lib::Finalize();

  return 0;
}
