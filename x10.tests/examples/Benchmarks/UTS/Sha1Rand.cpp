#include "sha1.h"

struct Sha1Rand {
  RNG_state internal_state; /* internal state for the RNG */

  /*
   * Constructor --- takes in the initial seed value
   */
  Sha1Rand (const int& seed) { rng_init (&internal_state, seed); }

  /*
   * *Almost* a copy constructor --- takes in a spawn number in addition 
   * to modifying the original Sha1Rand parameter.
   */
  Sha1Rand (Sha1Rand& other, const int& spawn_number) : internal_state (0) { 
    rng_spawn (&(other.internal_state), &internal_state, spawn_number); 
  }

  /*
   * Return the random number stored in the current object
   */
  int operator()(void) { return rng_rand (&internal_state); }
};

#ifdef SHA1_TEST
#include <iostream>
int main (int argc, char** argv) {
  Sha1Rand rgen (0);
  std::cout << "Initial value: " << rgen() << std::endl;
  for (int i=0; i<10; ++i) {
    Sha1Rand ngen (rgen, i);
    std::cout << i << ":" << rgen() << ", " << ngen() << std::endl;
  }

  RNG_state mystate;
  rng_init (&mystate, 0);
  std::cout << "Initial value: " << rng_rand(&mystate) << std::endl;
  for (int i=0; i<10; ++i) {
    RNG_state newstate;
    rng_spawn (&mystate, &newstate, i);
    std::cout << i << ":" << rng_rand(&mystate) << ", "
              << rng_rand(&newstate) << std::endl;
  }

  return 0;
}
#endif
