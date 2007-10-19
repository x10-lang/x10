/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_region.cc,v 1.4 2007-10-19 16:04:29 ganeshvb Exp $ */
#include <iostream>

#include <x10/x10lib.h>
#include <x10/region.h>

using namespace std;

using namespace x10lib;

void testDefaultOrigin ()
{
  RectangularRegion <2> r(Point<2>(3,4));
  
  assert (r.ord(Point<2>(0,0)) == 0);
  
  assert (r.ord(Point<2>(3,4)) == 19);

  assert (r.ord(Point<2>(1, 2)) == 7);

  assert (r.card() == 20);
}

void testDiffOrigin ()
{
  RectangularRegion <2> r(Point<2>(1, 1), Point<2>(4, 5));
    
  assert (r.ord(Point<2>(1, 1)) == 0);
  
  assert (r.ord(Point<2>(4, 5)) == 19);

  assert (r.ord(Point<2>(2, 3)) == 7);

  assert (r.card() == 20);
}

void testNegativeOrigin ()
{
  RectangularRegion <2> r(Point<2>(-1, -1), Point<2>(2, 3));
    
  assert (r.ord(Point<2>(-1, -1)) == 0);
  
  assert (r.ord(Point<2>(2,  3)) == 19);

  assert (r.ord(Point<2>(0, 1)) == 7);

  assert (r.card() == 20);
}

int 
main (int argc, char* argv[])
{ 

  Init(NULL, 0);
 
  testDefaultOrigin();
  
  testDiffOrigin();

  testNegativeOrigin();

  cout << "Test_region PASSED" << endl;  

  Finalize();
  return 0;
}
