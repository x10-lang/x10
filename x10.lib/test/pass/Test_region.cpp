#include <iostream>

#include <x10/region.h>
#include <x10/assert.h>

using namespace std;


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

int main (int argc, char** argv)
{
 
  testDefaultOrigin();
  
  testDiffOrigin();

  cout << "Test_region PASSED" << endl;  
  return 0;
}
