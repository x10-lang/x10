/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: Test_dist_array.cc,v 1.2 2007-12-10 16:44:40 ganeshvb Exp $ 
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
  

  int here = __x10_my_place;


  int blk_size = N / __x10_num_places;
  DistArray<double, 1>* a;

  if (!here) 
    {
      Region<1>* region = new RectangularRegion<1> (Point<1>(N));
      
      
      int* places = new int [__x10_num_places];
      for (int i = 0; i < __x10_num_places; ++i)
	places [i] = i;
      
      Dist<1>* b = new BlockDist<1> (region, places, __x10_num_places);
      a = MakeDistArray <double, 1> (b, 1);
    }

  x10lib::SyncGlobal();

  GenericArray* local_array = (GenericArray*) getLocalSection (1);
 
  assert (local_array->_data != NULL); 
  assert (local_array->_nelements == blk_size);
  assert (local_array->_elsize == sizeof(double));
  assert (local_array->_rank == 1);
  assert (local_array->_origin[0] == __x10_my_place * blk_size);
  assert (local_array->_diagonal[0] == (__x10_my_place + 1) * blk_size - 1);
  assert (local_array->_stride[0] == 1);
  
  x10lib::SyncGlobal();
  if (!here)
    FreeDistArray (a);
  
  cout << "Test_dist_array PASSED" << endl;

  x10lib::Finalize();
  
  return 0;
}
