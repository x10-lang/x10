/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_dist.cc,v 1.9 2008-01-06 03:28:51 ganeshvb Exp $ */

#include <iostream>

#include <x10/x10lib.h>
#include <x10/dist.h>

using namespace std;
using namespace x10lib;

void
testBlockDist()
{
  Region<1>* region = new RectangularRegion<1> (Point<1>(100));

  int num_places = 4;
  int* places = new int [num_places];
  for (int i = 0; i < num_places; i++)
    places[i] = i;

  int blk_size = 100 / num_places;
  BlockDist<1> b (region, places, num_places);
  
  /* test place() */
  for (int i = 0; i < 100; ++i)
    assert (b.place (Point<1>(i)) == i / blk_size);
  
  /* test restriction() */
  for (int p = 0; p < num_places; ++p) {
    RectangularRegion<1> r = b.restriction (p);
    assert (r.origin().value(0) == p * blk_size);
    assert (r.diagonal().value(0) == (p+1) * blk_size - 1);
    assert (r.card() == blk_size);
  }  
}

void 
testUniqueDist()
{
  UniqueDist u;

  for (int i = 0; i < __x10_num_places; i++)
    assert (u.place (Point<1>(i)) == i); 
}

void 
testConstDist()
{
  Region<2> * r = new RectangularRegion<2> (Point<2>(10, 10));

  x10_place_t place[1] = {3};

  ConstDist<2> c  (r, place);

  for (int i = 0; i < 10; i++)
    for (int j = 0; j < 10; j++)
      assert (c.place (Point<2>(i,j)) == 3);
   
  delete r;  
}

int 
main (int argc, char* argv[])
{  
  testUniqueDist();

  testConstDist();

  testBlockDist();

  cout << "Test_dist PASSED" << endl;

  return 0;
}
