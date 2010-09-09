/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_async.cc,v 1.8 2008-01-06 03:28:51 ganeshvb Exp $ */

#include <iostream>

#include <x10/x10lib.h>

using namespace std;
using namespace x10lib;


void async0 (x10_async_arg_t arg)
{
  assert (arg == 333);
}

void AsyncSwitch (x10_async_handler_t h, void* arg, int niter) 
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
       AsyncSpawnInline (target, 0, &a, sizeof(a));


  x10lib::SyncGlobal (); 

  cout << "Test_async PASSED" << endl;

  x10lib::Finalize();

  return 0;
}
