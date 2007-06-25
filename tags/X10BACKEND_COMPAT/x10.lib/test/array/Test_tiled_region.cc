/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_tiled_region.cc,v 1.2 2007-06-18 10:33:50 ganeshvb Exp $ */

#include <iostream>

#include <x10/x10lib.h>
#include <x10/array.h>

using namespace std;
using namespace x10lib;

int 
main (int argc, char* argv[])
{
  Init (NULL, 0);
 
  RectangularRegion<2>* r0 = new RectangularRegion<2>(Point<2>(3, 3));
  
  RectangularRegion<2>* r1 = new RectangularRegion<2>(Point<2>(0, 4), Point<2>(3, 7));
  
  RectangularRegion<2>* r2 = new RectangularRegion<2>(Point<2>(4, 0), Point<2>(7, 3));
  
  RectangularRegion<2>* r3 = new RectangularRegion<2>(Point<2>(4, 4), Point<2>(7, 7));
  
  const RectangularRegion<2>* bases [4] = {r0, r1, r2, r3};

  TiledRegion<2> t (Point<2>(1,1), bases);

  assert (t.ord(Point<2>(0,0)) == 0);

  assert (t.ord(Point<2>(1,1)) == 3);

  assert (t.regionAt (Point<2>(0, 1)) == r1);
  assert (t.regionAt (Point<2>(1, 0)) == r2);

  assert (t.card() == 4);

  assert (t.totalCard() == 64);
  
  const Point<2> p = t.indexOf (Point<2>(4,6));
 
  const Region<1>* base = new RectangularRegion<1>(Point<1>(64)); 
  const Region<1>* grid = new RectangularRegion<1>(Point<1>(3)); 
  TiledRegion<1>* t2 = TiledRegion<1>::makeBlock (base, grid);
 
  cout << "Test_tiled_region PASSED" << endl;  

  delete r0;
  delete r1;
  delete r2;
  delete r3;

  Finalize();
  return 0;
}
