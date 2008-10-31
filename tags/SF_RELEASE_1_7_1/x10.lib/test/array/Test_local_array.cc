/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: Test_local_array.cc,v 1.3 2007-12-10 16:44:40 ganeshvb Exp $ 
 * This file is part of X10 Runtime System.
 */

#include <iostream>
#include <x10/xassert.h>
#include <x10/x10lib.h>

using namespace std;
using namespace x10lib;

int 
main (int argc, char* argv[])
{
  int N = 1024;
  
  x10lib::Init(NULL,0);
  
  Region<1>* region = new RectangularRegion<1> (Point<1>(N));

  int blk_size = N / __x10_num_places;

  int* places = new int [__x10_num_places];
  for (int i = 0; i < __x10_num_places; ++i)
    places [i] = i;

  Dist<1>* b = new BlockDist<1> (region, places, __x10_num_places);

  int here = __x10_my_place;

  RectangularRegion<1> local_region = b->restriction(here);
  LocalArray<double, 1>* a = new LocalArray<double, 1> (&local_region, 1);

  for_local_1d (I, :, b->restriction(here)) 
      a->elementAt (Point<1>(I)) = I;

  int k = 0;
  for_local_1d (I, :, b->restriction(here))  {
    assert (a->elementAt (Point<1>(I)) == k + blk_size * __x10_my_place);
    k++;
  }

  delete a;
  
  cout << "Test_local_array PASSED" << endl;

  x10lib::Finalize();
  
  return 0;
}
