#include "sha1_rand.hpp"
#include <iostream>
#include <x10rt_front.h>
#include <cassert>
#include <cstring>
#include <numeric>
#include <sys/time.h>

static int b0 = 4; // Root branching factor
static int r = 0; // Root seed for the random number
static int m = 4; // Number of children 
static double q = 15.0/64.0; // Probability of having a child
static const double NORMALIZER = static_cast<double>(2147483647);

static double wsmprtc(void) {
  struct timeval tp;
  static long start=0, startu;

  if (!start) {
    gettimeofday(&tp, NULL);
    start = tp.tv_sec;
    startu = tp.tv_usec;
    return(0.0);
  }
  gettimeofday(&tp, NULL);
  return( (static_cast<double>(tp.tv_sec - start)) + 
          (tp.tv_usec-startu)*1.e-6);
}

static int binomial_tree_search (const sha1_rand& rng) {
  const int random_number = rng();
  const double prob = static_cast<double>(random_number)/NORMALIZER;

  int num_children = (prob < q) ? m : 0;
  int num_descendents = 0;

#if DEBUG
  std::cout << " Number: " << random_number 
            << " Children: " << num_children
            << std::endl;
#endif

  for (int i=0; i<num_children; ++i) 
    num_descendents += binomial_tree_search (sha1_rand (rng, i)); 
 
  return (num_children+num_descendents);
}

static int root_binomial_tree_search (const sha1_rand& rng) {
  int num_children = b0;
  int num_descendents = 0;

  for (int i=0; i<num_children; ++i) 
    num_descendents += binomial_tree_search (sha1_rand (rng, i)); 
 
  return (num_children+num_descendents);
}

int main (int argc, char** argv) {
  for (int i =1; // Program name is always the first argument -- argc >= 1
       i < argc; // Till all the arguments are consumed
       i += 2) { // Consume two at a time
    if (0 == strcmp (argv[i], "-b0")) {
      b0 = atoi (argv[i+1]);
    } else if (0 == strcmp (argv[i], "-r")) {
      r = atoi (argv[i+1]);
    } else if (0 == strcmp (argv[i], "-m")) {
      m = atoi (argv[i+1]);
    } else if (0 == strcmp (argv[i], "-q")) {
      q = static_cast<double> (atof (argv[i+1]));
    } else {
      std::cout << "Error --- invalid option: " << argv[i] << std::endl;
      exit (3);
    }
  } // End for

  std::cout << "==================== UTS ======================" << std::endl;
  std::cout << "Root branching factor (b0) = " << b0 << std::endl;
  std::cout << "Root seed (r) = " << r << std::endl;
  std::cout << "Number of children (m) = " << m << std::endl;
  std::cout << "Probability of a child (q) = " << q << std::endl;

  double time = wsmprtc ();
  int num_nodes = 1 + root_binomial_tree_search (sha1_rand (r));
  time = wsmprtc () - time;

  std::cout << "There were " << num_nodes << " nodes mined in " 
            << time << " secs" << std::endl;
  std::cout << "===============================================" << std::endl;

  return 0;
}
