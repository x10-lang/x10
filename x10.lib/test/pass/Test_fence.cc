#include <iostream>
#include <x10/x10lib.h>
#include "timers.h"

using namespace x10lib;
using namespace std;

int main (int argc, char** argv)
{
  Init(NULL, 0);

  int N = atoi(argv[1]);


  int cs = 0; 

  LAPI_Gfence (GetHandle());
  double t0 = -nanoTime();
  for (int i = 0; i < N; i++) {
    finishEnd (NULL);
    cs = finishStart (cs);
  }
  t0 += nanoTime();

  LAPI_Gfence (GetHandle());
  double t1 = -nanoTime();
  for (int i = 0; i < N; i++) {
    LAPI_Gfence (GetHandle());
  }
  t1 += nanoTime();
 
  LAPI_Gfence (GetHandle());
  if (!here())
  cout << "Time: " << t0 << " " << t1 << endl;
  return 0;
}
