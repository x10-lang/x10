/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: dist_array_spmd_cc.cc,v 1.1 2007-12-08 14:28:15 ganeshvb Exp $ 
 * This file is part of X10 Runtime System.
 */

/** Example that illustrates the creation of distributed array 
 ** in SPMD mode. That is, each local section is created in 
 ** the nodes independently, but have a globally unique ID. The 
 ** example also illustrates the usage of block disribution, 
 ** and range restriction over a distribution. 
 ** 
 ** All the operations are completely local. 
 **/
 
#include <x10/x10lib.h>

#include <iostream>

using namespace std;
using namespace x10lib;

int 
main (int argc, char* argv[])
{
  /* Initialize x10lib */
  x10lib::Init(NULL,0);

  int here = __x10_my_place;

  int N = 64;

  /* create a region */  
  Region<1>* region = new RectangularRegion<1> (Point<1>(N));

  /* create an array of places */
  int* places = new int [__x10_num_places];
  for (int i = 0; i < __x10_num_places; ++i)
    places [i] = i;

  /* create block distribution from region to places */
  Dist<1>* b = new BlockDist<1> (region, places, __x10_num_places);

  /* get the range restriction of b to here */
  RectangularRegion<1> local_region = b->restriction(here);

  /* create local arrays with a globally unique ID */
  LocalArray<double, 1>* a = new LocalArray<double, 1> (&local_region, 1);

  /* iterate over local section of the distributed array and assign a value */
  for_local_1d (I, :, b->restriction(here)) 
      a->elementAt (Point<1>(I)) = I;

  /* print the value togther with the expected value (for verification) */
  int k = 0;
  int blk_size = N / __x10_num_places;
  for_local_1d (I, :, b->restriction(here))  {
    cout << "(value: "<< a->elementAt (Point<1>(I)) << ", expected:" << k + blk_size * __x10_my_place << ")" << endl;
    k++;
  }

  /* delete a*/
  delete a;

  /* Finalize x10lib */
  x10lib::Finalize();
  
  return 0;
}
