/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_async.cc,v 1.6 2007-09-24 14:34:10 ganeshvb Exp $ */

#include <iostream>

#include <x10/x10lib.h>
#include <x10/array.h>

using namespace std;
using namespace x10lib;


void async0 (x10_async_arg_t arg)
{
  assert (arg == 333);
}

void asyncSwitch (x10_async_handler_t h, void* arg, int niter) 
{
  x10_async_arg_t* args = (x10_async_arg_t*) arg;
  switch (h) {
   case 0:
     async0 (*args);
     break;
  }
}

int 
main (int argc, char* argv[])
{

  x10lib::Init(NULL, 0);

  x10_async_arg_t a = 333;
  if (__x10_my_place== 0)
    for (x10_place_t target = 0; target < __x10_num_places; target++)
       asyncSpawnInline (target, 0, &a, sizeof(a));


  x10lib::SyncGlobal (); 

  cout << "Test_async PASSED" << endl;

  x10lib::Finalize();

  return 0;
}
