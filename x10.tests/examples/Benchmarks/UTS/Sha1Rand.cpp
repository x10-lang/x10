#include "sha1.h"

struct Sha1Rand {
  private:
  RNG_state internal_state; 
  struct true_type {};
  struct false_type {};

  int get_rand (false_type) { return rng_rand (&internal_state); }

  int get_rand (true_type) { return rng_nextrand (&internal_state); }

  public:
  Sha1Rand (const int& seed) { rng_init (&internal_state, seed); }

  int operator()(const bool reset=false) {
    return ((false==reset) ? get_rand (false_type()) :
                             get_rand (true_type()));
  }
};

#ifdef SHA1_TEST
#include <iostream>
int main (int argc, char** argv) {
  Sha1Rand rgen (0);
  for (int i=0; i<10; ++i) std::cout << rgen(true) << std::endl;

  RNG_state mystate;
  rng_init (&mystate, 0);
  for (int i=0; i<10; ++i) std::cout << rng_nextrand(&mystate) << std::endl;

  return 0;
}
#endif
