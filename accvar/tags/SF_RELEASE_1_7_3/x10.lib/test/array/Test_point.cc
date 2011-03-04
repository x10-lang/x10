/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_point.cc,v 1.3 2007-12-07 14:08:59 ganeshvb Exp $ */

#include <iostream>

#include <x10/x10lib.h>

using namespace std;
using namespace x10lib;

void 
test_point_1d()
{

  Point<1> p(2);
  
  assert (p.value(0) == 2);

  assert (p.valueI() == 2);

  Point<1> q(p);

  assert (q.value(0) == 2);
  
}

void 
test_point_2d()
{

  Point<2> p(2, 5);
  
  assert (p.value(0) == 2 && p.value(1) == 5);

  assert (p.valueI() == 2 && p.valueJ() == 5);

  Point<2> q(p);

  assert (q.value(0) == 2 && q.value(1) == 5);

}

void 
test_point_nd()
{

  int values[4] = {1, 2, 5, 7};

  Point<4> p(values);
  
  for (int i = 0; i < 4; i++)
    assert (p.value(i) == values[i]);

  Point<4> q(p);

  for (int i = 0; i < 4; i++)
    assert (q.value(i) == values[i]);

}

int 
main (int argc, char* argv[])
{
  Init (NULL, 0);
 
  test_point_1d();

  test_point_2d();

  test_point_nd();

  cout << "Test_point PASSED" << endl;
 
  Finalize();
 
  return 0;
}

