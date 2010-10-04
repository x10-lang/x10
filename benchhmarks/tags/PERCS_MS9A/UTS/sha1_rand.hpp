#ifndef SHA1_RAND_HPP
#define SHA1_RAND_HPP

#include "sha1.h"
#include <stdint.h>

struct sha1_rand {
  unsigned char rng_state[20]; /* internal state for the RNG */

  /*
   * Default constructor
   */
  sha1_rand (void) {}

  /*
   * Constructor punning --- takes in the initial seed value
   */
  void init (const int& seed) { 
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
   * *Almost* a copy constructor punning --- takes in a spawn number in
   * addition to modifying the original sha1_rand parameter.
   */
  void init (const sha1_rand& parent, const int& spawn_number) { 
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

#endif
