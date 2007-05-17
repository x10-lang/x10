/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_array_async.cc,v 1.2 2007-05-17 09:49:52 ganeshvb Exp $ */

#include <iostream>

#include <x10/x10lib.h>
#include <x10/array.h>

using namespace std;

func_t handlers[128];


struct async2 
{
  inline void operator() (async_arg_t* arg0, int n)
  {
    assert (n==1);
    assert  (*arg0 == (here() - 1) % numPlaces());
  }
};

struct async1{
  inline void operator () (async_arg_t* args, int n) 
  {
    assert (n == 2);

    Array<int, 1> * a = (Array<int, 1>*) (*args);

    assert (a->localSize() == *(args+1));

    asyncSpawnInline <1, async2> ((here() + 1) % numPlaces(), here());
  }
};

int 
main (int argc, char* argv[])
{
  x10lib::Init(handlers, 2);

  place_t p [4] = {0, 1, 2, 3};

  Region<1>* grid = new RectangularRegion<1>(Point<1>(3));
  UniqueDist<1>* u = new UniqueDist<1>(grid, p);
  if (here() == 0) {
    Array<int, 1>* a = makeArray <int,1, RectangularRegion, UniqueDist>(grid, u);
    uint64_t tableSize = a->localSize();
   for (place_t target = 0; target < numPlaces(); target++)
     asyncSpawnInline<2, async1> (target, (async_arg_t) (GlobalSMAlloc->addrTable(target)),
                    tableSize);

  } 
 
  cout << "Test_array_async PASSED" << endl;

  LAPI_Gfence (GetHandle()); 

  x10lib::Finalize();

  return 0;
}
