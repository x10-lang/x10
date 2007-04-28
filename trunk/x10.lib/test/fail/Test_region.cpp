#include <iostream>

#include <x10/assert.h>
#include <x10/region.h>

using namespace std;

void testDefaultOrigin()
{
  RectangularRegion <2> r(Point<2>(3,4));

  int a;

  a = r.ord(Point<2>(4,4));
  a = r.ord(Point<2>(3,5));
  a = r.ord(Point<2>(5,5));
}

void testDiffOrigin()
{
  RectangularRegion <2> r(Point<2>(1,1), Point<2>(3,4));
  int a;

  a = r.ord(Point<2>(4,4));
  a = r.ord(Point<2>(3,5));
  a = r.ord(Point<2>(5,5));

  RectangularRegion <2> r2(Point<2>(1,5), Point<2>(3,4));
}

int main (int argc, char** argv)
{
  testDefaultOrigin();

  testDiffOrigin();
 
  cout << "Test_region PASSED" << endl;  
  return 0;
}
