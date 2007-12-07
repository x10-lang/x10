/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_point2.cc,v 1.3 2007-12-07 14:08:59 ganeshvb Exp $ */

#include <iostream>

#include <x10/x10lib.h>

using namespace std;
using namespace x10lib;

void 
test_point_1d()
{

  Point<1> p(2);
  
  p.value(1);
}

void 
test_point_2d()
{

  Point<2> p(2, 5);
  
  p.value(2);

}

void 
test_point_nd()
{
  int values[4] = {1, 2, 5, 7};

  Point<4> p(values);
  
  p.value(4);
}

int 
main (int argc, char* argv[])
{
  Init(NULL, 0);

  test_point_1d();

  test_point_2d();

  test_point_nd();

  cout <<"Test_point PASSED" << endl;
 
  Finalize();

  return 0;
}

