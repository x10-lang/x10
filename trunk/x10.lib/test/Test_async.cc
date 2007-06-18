/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_async.cc,v 1.3 2007-06-18 10:33:48 ganeshvb Exp $ */

#include <iostream>

#include <x10/x10lib.h>
#include <x10/array.h>

func_t handlers[128];

using namespace std;
using namespace x10lib;


void async0 (async_arg_t arg)
{
  assert (arg == 333);
}

int asyncSwitch (async_handler_t h, void* arg, int niter) 
{

  async_arg_t* args = (async_arg_t*) arg;
  switch (h) {
   case 0:
     async0 (*args);
  }
}

int 
main (int argc, char* argv[])
{

  x10lib::Init(NULL, 0);

  async_arg_t a = 333;
  if (here() == 0)
    for (place_t target = 0; target < numPlaces(); target++)
       asyncSpawnInline (target, 0, 1, a);

  x10lib::Gfence (); 

  cout << "Test_async PASSED" << endl;

  x10lib::Finalize();

  return 0;
}
