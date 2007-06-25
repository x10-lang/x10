/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_point2.cc,v 1.1 2007-06-08 13:51:42 srkodali Exp $ */

#include <iostream>

#include <x10/x10lib.h>
#include <x10/point.h>


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

