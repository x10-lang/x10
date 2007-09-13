/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_array_async.cc,v 1.5 2007-09-13 15:20:05 ganeshvb Exp $ */

#include <iostream>

#include <x10/x10lib.h>
#include <x10/array.h>

using namespace std;
using namespace x10lib;


void async1 (x10_async_arg_t arg0)
{
    assert  (arg0 == (here() - 1) % (uint) numPlaces());
}

void async0 (x10_async_arg_t arg0, x10_async_arg_t arg1)
{
    Array<int, 1> * a = (Array<int, 1>*) (arg0);

    cout << a->localSize()  << " " << arg1 << endl;
    assert (a->localSize() == arg1);

    asyncSpawnInline ((here() + 1) % numPlaces(), 1, 1, here());
}

void asyncSwitch (x10_async_handler_t h, void* arg, int niter)
{
  x10_async_arg_t * args =(x10_async_arg_t*) arg;
  switch (h) {
   case 0:
     async0(*args, *(args + 1));
     break;
  case 1:
    async1(*args);
    break;
  }
}

int 
main (int argc, char* argv[])
{
  x10lib::Init(NULL, 0);

  x10_place_t p [4] = {0, 1, 2, 3};

  Region<1>* grid = new RectangularRegion<1>(Point<1>(3));
  UniqueDist<1>* u = new UniqueDist<1>(grid, p);
  if (here() == 0) {
    Array<int, 1>* a = makeArray <int,1, RectangularRegion, UniqueDist>(grid, u);
    uint64_t tableSize = a->localSize();
    cout << "table size " << tableSize << endl;
    for (x10_place_t target = 0; target < numPlaces(); target++) {
      cout << "size " << GlobalSMAlloc->addrTable (target) << endl;
      asyncSpawnInline(target, 0, 2, (x10_async_arg_t) (GlobalSMAlloc->addrTable(target)),
		       tableSize);
    }

  } 
 

  LAPI_Gfence (GetHandle()); 

  cout << "Test_array_async PASSED" << endl;

  x10lib::Finalize();

  return 0;
}
