/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_array_async.cc,v 1.2 2007-06-18 10:33:50 ganeshvb Exp $ */

#include <iostream>

#include <x10/x10lib.h>
#include <x10/array.h>

using namespace std;


void async1 (async_arg_t arg0)
{
    assert  (arg0 == (here() - 1) % numPlaces());
}

void async0 (async_arg_t arg0, async_arg_t arg1)
{
    Array<int, 1> * a = (Array<int, 1>*) (arg0);

    assert (a->localSize() == arg1);

    asyncSpawnInline ((here() + 1) % numPlaces(), 1, 1, here());
}

int asyncSwitch (async_handler_t h, void* arg, int niter)
{
  async_arg_t * args =(async_arg_t*) arg;
  switch (h) {
   case 0:
     async0(*args++, *args);
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

  place_t p [4] = {0, 1, 2, 3};

  Region<1>* grid = new RectangularRegion<1>(Point<1>(3));
  UniqueDist<1>* u = new UniqueDist<1>(grid, p);
  if (here() == 0) {
    Array<int, 1>* a = makeArray <int,1, RectangularRegion, UniqueDist>(grid, u);
    uint64_t tableSize = a->localSize();
   for (place_t target = 0; target < numPlaces(); target++)
     asyncSpawnInline(target, 0, 2, (async_arg_t) (GlobalSMAlloc->addrTable(target)),
                    tableSize);

  } 
 
  cout << "Test_array_async PASSED" << endl;

  LAPI_Gfence (GetHandle()); 

  x10lib::Finalize();

  return 0;
}
