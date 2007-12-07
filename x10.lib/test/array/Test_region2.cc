/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_region2.cc,v 1.2 2007-12-07 14:08:59 ganeshvb Exp $ */

#include <iostream>

#include <x10/x10lib.h>

using namespace std;
using namespace x10lib;

void 
testDefaultOrigin()
{
  RectangularRegion <2> r(Point<2>(3,4));

  int a;

  a = r.ord(Point<2>(4,4));
  a = r.ord(Point<2>(3,5));
  a = r.ord(Point<2>(5,5));
}

void 
testDiffOrigin()
{
  RectangularRegion <2> r(Point<2>(1,1), Point<2>(3,4));
  int a;

  a = r.ord(Point<2>(4,4));
  a = r.ord(Point<2>(3,5));
  a = r.ord(Point<2>(5,5));

  RectangularRegion <2> r2(Point<2>(1,5), Point<2>(3,4));
}

int 
main (int argc, char* argv[])
{
  Init (NULL, 0);

  testDefaultOrigin();

  testDiffOrigin();
 
  cout << "Test_region PASSED" << endl;  

  Finalize ();
  return 0;
}
