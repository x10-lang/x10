/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_async_agg.cc,v 1.1 2007-05-17 09:49:52 ganeshvb Exp $ */

#include <iostream>

#include <x10/x10lib.h>
#include <x10/array.h>
#include <x10/aggregate.h>

func_t handlers[128];

using namespace std;
using namespace x10lib;

uint64_t I = 0;
struct helloWorld
{
  void operator () (async_arg_t* arg0, int n)
  {
    I += *arg0 * *(arg0 + 1);
  }
};

typedef void (*void_func_t) ();

int 
main (int argc, char* argv[])
{

  int N = 2100;

  x10lib::Init(handlers, 0);

    for (place_t target = 0; target < numPlaces(); target++)
      for (uint64_t i = 0; i < N; i++)
       asyncSpawnInlineAgg <2, helloWorld> (target, 0, i, 5);
     
     flush<2, helloWorld> (0);

     
  x10lib::Gfence (); 
  cout << here() << " I " << I << endl;
  assert (I == numPlaces() * N * (N-1) / 2 * 5);

  cout << "Test_async PASSED" << endl;

  x10lib::Finalize();

  return 0;
}
