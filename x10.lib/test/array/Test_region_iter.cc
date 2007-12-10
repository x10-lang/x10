/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_region_iter.cc,v 1.2 2007-12-10 16:44:40 ganeshvb Exp $ */
#include <iostream>

#include <x10/x10lib.h>
#include <x10/region.h>

using namespace std;

using namespace x10lib;

void sum (Point<1> p)
{
  cout << p.value(0) << endl;  
}

void sum_2d (Point<2> p)
{
  cout << p.value(0) << " " << p.value(1) << endl;  
}

void test2d ()
{
  RectangularRegion <2> r(Point<2>(1, 2), Point<2>(4, 5));

  /* rectangular region */
  
  int idx = 0;
  for_local_2d (I, J, :, r) {
    //cout << "( " << I << ", " << J << ") " << endl;
    assert (isEqual (r.coord(idx), Point<2>(I, J)));
    idx++;
  }

  assert (idx == r.card());

  idx = 0;
  int n = r.size()[0];
  /* lower triangular region */
  for_local_2d_lower (I, J, :, r) {
    //cout << "( " << I << ", " << J << ") " << endl;
    idx++;
  }
  assert (idx == n * (n-1) / 2);

  idx = 0;
  /* upper triangular region */
  for_local_2d_upper (I, J, :, r) {
    //cout << "( " << I << ", " << J << ") " << endl;
    idx++;
  }
  assert (idx == n * (n-1) / 2);

  /* diagonal */
   idx = 0;
  for_local_2d_diag (I, J, :, r) {
    //cout << "( " << I << ", " << J << ") " << endl;
    idx++;
  }
  assert (idx == n); 

  /* nested iteration */
  idx = 0;
  RectangularRegion <2> s(Point<2>(0, 0), Point<2>(1, 1));
  for_local_2d (I, J, :, r) {
     for_local_2d (M, N, :, s) {
        idx++;
     }
  }
  assert (idx == r.card() * s.card());

  foreach <2, sum_2d> (&r);
}

void test1d ()
{
  RectangularRegion <1> r(Point<1>(4));

  int idx = 0;
  for_local_1d (I,  :, r) {
    assert (isEqual (r.coord(idx), Point<1>(I)));
    idx++;
    //cout << "( " << I << ") " << endl;
  }
  assert (idx == r.card()); 

  foreach <sum> (&r);
}

int 
main (int argc, char* argv[])
{ 

  Init(NULL, 0);
 
  test1d();

  test2d();

  cout << "Test_region PASSED" << endl;  

  Finalize();
  return 0;
}
