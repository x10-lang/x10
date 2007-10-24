/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_strided_region.cc,v 1.2 2007-10-24 10:47:43 ganeshvb Exp $ */
#include <iostream>

#include <x10/x10lib.h>

using namespace std;

using namespace x10lib;

void testDefaultOrigin ()
{
  RectangularRegion <2> r(Point<2>(0, 0), Point<2>(6, 12), Point<2> (2, 3));
 
  //cout << "here " << r.ord(Point<2>(0, 0)) << " " << r.ord(Point<2>(2,4)) << " " << r.ord(Point<2>(2, 2)) <<  " " << r.card() << endl;
  
  assert (r.ord(Point<2>(0, 0)) == 0);
  
  assert (r.ord(Point<2>(6, 12)) == 19);

  assert (r.ord(Point<2>(2, 6)) == 7);

  assert (r.card() == 20);
}

void testDiffOrigin ()
{
  RectangularRegion <2> r(Point<2>(1, 2), Point<2>(7, 14), Point<2> (2, 3));
 
  //cout << "here " << r.ord(Point<2>(1, 2)) << " " << r.ord(Point<2>(7, 14)) << " " << r.ord(Point<2>(3, 8)) <<  " " << r.card() << endl;
  
  assert (r.ord(Point<2>(1, 2)) == 0);
  
  assert (r.ord(Point<2>(6+1, 12+2)) == 19);

  assert (r.ord(Point<2>(2+1, 6+2)) == 7);

  assert (r.card() == 20);
}


void testNegativeOrigin ()
{
  RectangularRegion <2> r(Point<2>(-1, -2), Point<2>(5, 10), Point<2> (2, 3));
 
  //cout << "here " << r.ord(Point<2>(0, 0)) << " " << r.ord(Point<2>(2,4)) << " " << r.ord(Point<2>(2, 2)) <<  " " << r.card() << endl;
  
  assert (r.ord(Point<2>(-1, -2)) == 0);
  
  assert (r.ord(Point<2>(5, 10)) == 19);

  assert (r.ord(Point<2>(2-1, 6-2)) == 7);

  assert (r.card() == 20);
}

int 
main (int argc, char* argv[])
{ 

  Init(NULL, 0);
 
  testDefaultOrigin();
  
  testDiffOrigin();

  testNegativeOrigin();

  cout << "Test_strided_region PASSED" << endl;  

  Finalize();
  return 0;
}
