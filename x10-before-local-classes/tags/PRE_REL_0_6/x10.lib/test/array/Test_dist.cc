/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_dist.cc,v 1.5 2007-10-19 16:04:29 ganeshvb Exp $ */

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

  x10_place_t places [4] = {0, 1, 2, 3};

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
  x10_place_t place[1] = {3};

  ConstDist<2> c  (r, place);

  for (int i = 0; i < N; i++)
    for (int j = 0; j < M; j++)
      assert (c.place (Point<2>(i,j)) == 3);
   
  delete r;  
}

int 
main (int argc, char* argv[])
{
  Init(NULL, 0);
  
  testUniqueDist();

  testConstDist();

  cout << "Test_dist PASSED" << endl;

  Finalize();

  return 0;
}
