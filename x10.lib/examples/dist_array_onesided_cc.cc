/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: dist_array_onesided_cc.cc,v 1.1 2007-12-08 14:28:15 ganeshvb Exp $ 
 * This file is part of X10 Runtime System.
 */

/** Example to illustrate the creation and deletion of distributed arrays
 ** in one-sided fashion. That is, for eg, place 0 creates
 ** a array that is distributed in all the places.
 ** Distributed arrays are referenced using a globally unique ID.
 **
 ** Example also illustrates the usage of GenericArrays and array tables.
 ** GenericArrays are arrays with no base-type (or base type == char*) and
 ** no rank. At the non-zero places, the type and rank of a distributed array
 ** is not known statically and hence we use GenericArrays. 
 ** x10lib provides array tables, that store the pointers to the arrays at 
 ** the position indexed by its ID. Methods to insert, look-up and free
 ** the entries from the array tables are provided (see api.txt). 
 **
 **/


#include <iostream>
#include <x10/xassert.h>
#include <x10/x10lib.h>

using namespace std;
using namespace x10lib;

int 
main (int argc, char* argv[])
{
  
  /* initialize x10lib */
  x10lib::Init(NULL,0);

  /* dimension parameters */
  int N = 1024;
 
  int here = __x10_my_place;

  int blk_size = N / __x10_num_places;
 
  /* a pointer to dist array */
  DistArray<double, 1>* a;

  /* place zero creates the distributed array in one-sided fashion*/
  if (here==0) 
    {
      /* create a 1D region */
      Region<1>* region = new RectangularRegion<1> (Point<1>(N));
      
      /* create a set of places */
      int* places = new int [__x10_num_places];
      for (int i = 0; i < __x10_num_places; ++i)
	places [i] = i;
      
      /* create a block ditribution */
      Dist<1>* b = new BlockDist<1> (region, places, __x10_num_places);

      
      /* create the distributed array -- create a chunk in all the places */
      /* "a" is uniquely identified by its global id */
      a = MakeDistArray <double, 1> (b, 1);
    }

  /* sync up */
  x10lib::SyncGlobal();

  /* check if the arrays is really created on all the places */

  /* getLocalSection is the array table look-up method */

  GenericArray* local_array = (GenericArray*) getLocalSection (1); 
  assert (local_array->_data != NULL); 
  assert (local_array->_nelements == blk_size);
  assert (local_array->_elsize == sizeof(double));
  assert (local_array->_rank == 1);
  assert (local_array->_origin[0] == __x10_my_place * blk_size);
  assert (local_array->_diagonal[0] == (__x10_my_place + 1) * blk_size - 1);
  assert (local_array->_stride[0] == 1);

  /* sync up */
  x10lib::SyncGlobal();

  /* one-sided deletion of the distributed array */
  if (!here)
    FreeDistArray (a);

  /* finalize x10lib */
  x10lib::Finalize();
  
  return 0;
}
