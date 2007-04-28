#include <iostream>

#include <x10/assert.h>
#include <x10/dist.h>
#include <x10/types.h>

using namespace std;
using namespace x10lib;

int N = 1; 
int M = 1;

void testUniqueDist()
{
  Region<2> * r = new RectangularRegion<2> (Point<2>(N, M));

  place_t places [4] = {0, 1, 2, 3};

  UniqueDist<2> u (r, places);

  int k = 0;
  for (int i = 0; i < N; i++)
    for (int j = 0; j < M; j++)
      assert (u.place (Point<2>(i,j)) == places[k++]);

  delete r;
}

void testConstDist()
{
  Region<2> * r = new RectangularRegion<2> (Point<2>(N, M));
  ConstDist<2> c  (r, 3);

  for (int i = 0; i < N; i++)
    for (int j = 0; j < M; j++)
      assert (c.place (Point<2>(i,j)) == 3);
  
}

void testTiledUniqueDist()
{
  RectangularRegion<2>* r0 = new RectangularRegion<2>(Point<2>(3, 3));
  
  RectangularRegion<2>* r1 = new RectangularRegion<2>(Point<2>(0, 4), Point<2>(3, 7));
  
  RectangularRegion<2>* r2 = new RectangularRegion<2>(Point<2>(4, 0), Point<2>(7, 3));
  
  RectangularRegion<2>* r3 = new RectangularRegion<2>(Point<2>(4, 4), Point<2>(7, 7));
  
  const RectangularRegion<2>* bases [4] = {r0, r1, r2, r3};

  Region<2>* r  = new TiledRegion<2>(Point<2>(N, M), bases);
  
  place_t places [4] = {0, 1, 2, 3};

  UniqueDist<2> u (r, places);
  
  int k = 0;
  for (int i = 0; i < N; i++)
    for (int j = 0; j < M; j++)
      assert (u.place (Point<2>(i,j)) == places[k++]);
  delete r;
}

int main (int argc, char** argv)
{
  
  testUniqueDist();

  testConstDist();

  testTiledUniqueDist();

  cout << "Test_dist PASSED" << endl;

  return 0;
}
