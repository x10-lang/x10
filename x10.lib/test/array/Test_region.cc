/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_region.cc,v 1.3 2007-06-26 16:05:58 ganeshvb Exp $ */
#include <iostream>

#include <x10/x10lib.h>
#include <x10/array.h>

using namespace std;
using namespace x10lib;

void testDefaultOrigin ()
{
  RectangularRegion <2> r(Point<2>(3,4));
  
  assert (r.ord(Point<2>(0,0)) == 0);
  
  assert (r.ord(Point<2>(3,4)) == 19);

  assert (r.card() == 20);
  
}

void testDiffOrigin ()
{
  RectangularRegion <2> r(Point<2>(1, 1), Point<2>(3, 4));
    
  assert (r.ord(Point<2>(1, 1)) == 0);
  
  assert (r.ord(Point<2>(3,4)) == 11);

  assert (r.card() == 12);
}

int 
main (int argc, char* argv[])
{ 

  Init(NULL, 0);
 
  testDefaultOrigin();
  
  testDiffOrigin();

  cout << "Test_region PASSED" << endl;  

  Finalize();
  return 0;
}
