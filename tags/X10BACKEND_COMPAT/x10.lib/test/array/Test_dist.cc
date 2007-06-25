/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_dist.cc,v 1.2 2007-06-18 10:33:50 ganeshvb Exp $ */

#include <iostream>

#include <x10/array.h>
#include <x10/x10lib.h>

using namespace std;
using namespace x10lib;

int N = 1; 
int M = 1;

void 
testUniqueDist()
{
  Region<2> * r = new RectangularRegion<2> (Point<2>(N, M));

  place_t places [4] = {0, 1, 2, 3};

  UniqueDist<2> u (r, places);

  int k = 0;
  for (int i = 0; i < N; i++)
    for (int j = 0; j < M; j++)
      assert (u.place (Point<2>(i,j)) == places[k++]);

  delete r;
}

void 
testConstDist()
{
  Region<2> * r = new RectangularRegion<2> (Point<2>(N, M));
  ConstDist<2> c  (r, 3);

  for (int i = 0; i < N; i++)
    for (int j = 0; j < M; j++)
      assert (c.place (Point<2>(i,j)) == 3);
   
  delete r;  
}

void 
testTiledUniqueDist()
{
  RectangularRegion<2>* r0 = new RectangularRegion<2>(Point<2>(3, 3));
  
  RectangularRegion<2>* r1 = new RectangularRegion<2>(Point<2>(0, 4), Point<2>(3, 7));
  
  RectangularRegion<2>* r2 = new RectangularRegion<2>(Point<2>(4, 0), Point<2>(7, 3));
  
  RectangularRegion<2>* r3 = new RectangularRegion<2>(Point<2>(4, 4), Point<2>(7, 7));
  
  const RectangularRegion<2>* bases [4] = {r0, r1, r2, r3};

  Region<2>* r  = new TiledRegion<2>(Point<2>(N, M), bases);
  
  place_t places [4] = {0, 1, 2, 3};

  UniqueDist<2> u (r, places);
  
  int k = 0;
  for (int i = 0; i < N; i++)
    for (int j = 0; j < M; j++)
      assert (u.place (Point<2>(i,j)) == places[k++]);

  delete r;
  delete r0;
  delete r1;
  delete r2;
  delete r3;
}

int 
main (int argc, char* argv[])
{
  Init(NULL, 0);
  
  testUniqueDist();

  testConstDist();

  testTiledUniqueDist();

  cout << "Test_dist PASSED" << endl;

  Finalize();

  return 0;
}
