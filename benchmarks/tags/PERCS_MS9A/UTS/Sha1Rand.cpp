#include "sha1.h"
#include <stdint.h>

struct Sha1Rand {
  unsigned char rng_state[20]; /* internal state for the RNG */

  /*
   * Constructor --- takes in the initial seed value
   */
  Sha1Rand (const int& seed) { 
    struct sha1_context context;

    /* Initialize rng_state */
    for (int i=0; i < 16; i++) rng_state[i] = 0;
    rng_state[16] = 0xFF & (seed >> 24);
    rng_state[17] = 0xFF & (seed >> 16);
    rng_state[18] = 0xFF & (seed >> 8);
    rng_state[19] = 0xFF & (seed >> 0);

    /* Call SHA1 */
    sha1_begin (&context);
    sha1_hash (rng_state, 20, &context);
    sha1_end (rng_state, &context);
  }

  /*
   * Copy constructor
   */
  Sha1Rand (Sha1Rand& parent) { 
    for (int i=0; i<20; ++i) rng_state[i] = parent.rng_state[i];
  }

  /*
   * *Almost* a copy constructor --- takes in a spawn number in addition 
   * to modifying the original Sha1Rand parameter.
   */
  Sha1Rand (const Sha1Rand& parent, const int& spawn_number) { 
    struct sha1_context context;

    /* Call SHA1 */
    sha1_begin (&context);
    sha1_hash (parent.rng_state, 20, &context);

    /* Call it again --- this time, incorporate spawn_number */
    rng_state[0] = 0xFF & (spawn_number >> 24);
    rng_state[1] = 0xFF & (spawn_number >> 16);
    rng_state[2] = 0xFF & (spawn_number >> 8);
    rng_state[3] = 0xFF & (spawn_number >> 0);
    sha1_hash (rng_state, 4, &context);

    sha1_end (rng_state, &context);
  }

  /*
   * Return the random number stored in the current object
   */
  int operator()(void) { 
    uint32_t b =  (rng_state[16] << 24) | 
                  (rng_state[17] << 16) |
                  (rng_state[18] << 8) | 
                  (rng_state[19] << 0);

    return static_cast<int> (b & POS_MASK);
  }
};

#ifdef SHA1_TEST
#include <iostream>
int main (int argc, char** argv) {
  Sha1Rand rgen (0);
  std::cout << "Initial value (Sha1Rand): " << rgen() << std::endl;
  for (int i=0; i<10; ++i) {
    Sha1Rand ngen (rgen, i);
    std::cout << i << ":" << ngen() << std::endl;
  }

  std::cout << std::endl;

  unsigned char mystate[20];
  rng_init ((RNG_state*)mystate, 0);
  std::cout << "Initial value (C Interface): " 
            << rng_rand((RNG_state*)mystate) << std::endl;
  for (int i=0; i<10; ++i) {
    unsigned char newstate[20];
    rng_spawn ((RNG_state*)mystate, (RNG_state*)newstate, i);
    std::cout << i << ":" << rng_rand((RNG_state*)newstate) << std::endl;
  }

  return 0;
}
#endif
