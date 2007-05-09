/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_array_async.cc,v 1.1 2007-05-09 06:36:32 ganeshvb Exp $ */

#include <iostream>

#include <x10/x10lib.h>
#include <x10/array.h>

using namespace std;

func_t handlers[128];
void
async2 (async_arg_t arg0)
{
    assert  (arg0 == (here() - 1) % numPlaces());
}

void
async1 (async_arg_t arg0, async_arg_t arg1)
{
   Array<int, 1> * a = (Array<int, 1>*) arg0;

   assert (a->localSize() == arg1);

  asyncSpawn <1, true> ((here() + 1) % numPlaces(), 1, here());
}

int 
main (int argc, char* argv[])
{
  handlers[0].fptr = (void_func_t) async1;
  handlers[1].fptr = (void_func_t) async2;

  x10lib::Init(handlers, 1);

  place_t p [4] = {0, 1, 2, 3};

  Region<1>* grid = new RectangularRegion<1>(Point<1>(3));
  UniqueDist<1>* u = new UniqueDist<1>(grid, p);
  if (here() == 0) {
    Array<int, 1>* a = makeArray <int,1, RectangularRegion, UniqueDist>(grid, u);
    uint64_t tableSize = a->localSize();
   for (place_t target = 0; target < numPlaces(); target++)
     asyncSpawn<2, false> (target, 0, (async_arg_t) (GlobalSMAlloc->addrTable(target)),
                    tableSize);

  } 
 
  cout << "Test_array_async PASSED" << endl;

  LAPI_Gfence (GetHandle()); 

  x10lib::Finalize();

  return 0;
}
