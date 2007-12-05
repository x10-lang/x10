/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_array_async.cc,v 1.6 2007-10-11 08:27:16 ganeshvb Exp $ */

#include <iostream>

#include <x10/x10lib.h>
#include <x10/array.h>

using namespace std;
using namespace x10lib;

/** This is an Illegal test case as far as LAPI is concerned
    Because, you can spawn an async from another async in
    polling mode and with inline completion handler */

void async1 (x10_async_arg_t arg0)
{
    assert  (arg0 == (here() - 1) % (uint) numPlaces());
}

void async0 (void* arg0, x10_async_arg_t arg1)
{
    Array<int, 1> * a = (Array<int, 1>*) (arg0);

    cout << a->localSize()  << " " << arg1 << endl;
    assert (a->localSize() == arg1);

    int _place = here();
    asyncSpawnInline ((here() + 1) % numPlaces(), 1, &_place, sizeof(int));
}

struct __async_0_args
{
  __async_0_args (void* _ptr, int _size):
    ptr (_ptr), size (_size) {}

  void* ptr;
  int size;
};

void asyncSwitch (x10_async_handler_t h, void* arg, int niter)
{
  switch (h) {
   case 0: {
      __async_0_args* args =(__async_0_args*) arg;
     async0(args->ptr, args->size);
     break;
    }
  case 1:
    async1(*((int*) arg));
    break;
  }
}


int 
main (int argc, char* argv[])
{
  cout << "Test case not working -- please do not use it " << endl;

  x10lib::Init(NULL, 0);

  x10_place_t p [4] = {0, 1, 2, 3};

  Region<1>* grid = new RectangularRegion<1>(Point<1>(3));
  UniqueDist<1>* u = new UniqueDist<1>(grid, p);
  if (here() == 0) {
    Array<int, 1>* a = makeArray <int,1, RectangularRegion, UniqueDist>(grid, u);
    int tableSize = a->localSize();
    for (x10_place_t target = 0; target < numPlaces(); target++) {
      __async_0_args args (GlobalSMAlloc->addrTable(target), tableSize);
      asyncSpawnInline(target, 0, &args, sizeof(args));
    }

  } 
 

  LAPI_Gfence (GetHandle()); 

  cout << "Test_array_async PASSED" << endl;

  x10lib::Finalize();

  return 0;
}
