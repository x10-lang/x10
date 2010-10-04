#include "sha1_rand.hpp"
#include <iostream>
#include <x10rt_front.h>
#include <cassert>
#include <cstring>
#include <numeric>
#include <sys/time.h>

#if defined(RECURSIVE)
# warning "Using RECURSIVE"
# define IMPLEMENTATION 1
#elif defined(STACK)
# warning "Using STACK"
# define IMPLEMENTATION 2
#elif defined(QUEUE)
# warning "Using QUEUE"
# define IMPLEMENTATION 3
#elif defined(DEQUE)
# warning "Using DEQUE"
# define IMPLEMENTATION 4
#else
#warning "Implementation not defined, using QUEUE"
# define IMPLEMENTATION 3
#endif

static int b0 = 2000; // Root branching factor
static int r = 42; // Root seed for the random number
static int m = 8; // Number of children 
static double q = .124975; // Probability of having a child
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

#if (IMPLEMENTATION==1)
static int binomial_tree_search (const sha1_rand& rng) {
  const int random_number = rng();
  const double prob = static_cast<double>(random_number);

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
#elif (IMPLEMENTATION==2)
#include <stack>
std::stack<sha1_rand> work_queue;

static unsigned int binomial_tree_search () {
  unsigned int num_nodes = 0;
  while (!work_queue.empty()) {
    const sha1_rand rng = work_queue.top();
    work_queue.pop();
    const int num_children = (static_cast<double>(rng()) < q) ? m : 0;

    for (int i=0; i<num_children; ++i) {
      work_queue.push(sha1_rand(rng, i));
    }
    ++num_nodes;
  }

  return num_nodes;
}
#elif (IMPLEMENTATION==3)
#include <queue>
std::queue<sha1_rand> work_queue;

static unsigned int binomial_tree_search () {
  unsigned int num_nodes = 0;
  while (!work_queue.empty()) {
    const sha1_rand rng = work_queue.front();
    work_queue.pop();
    const int num_children = (static_cast<double>(rng()) < q) ? m : 0;

    for (int i=0; i<num_children; ++i) {
      work_queue.push(sha1_rand(rng, i));
    }
    ++num_nodes;
  }

  return num_nodes;
}
#elif (IMPLEMENTATION==4)
#include <queue>
std::deque<sha1_rand> work_queue;

static unsigned int binomial_tree_search () {
  unsigned int num_nodes = 0;
  while (!work_queue.empty()) {
    const sha1_rand rng = work_queue.front();
    work_queue.pop_front();
    const int num_children = (static_cast<double>(rng()) < q) ? m : 0;

    for (int i=0; i<num_children; ++i) {
      work_queue.push_back(sha1_rand(rng, i));
    }
    ++num_nodes;
  }

  return num_nodes;
}
#else
#error "Define implementation"
#endif

int main (int argc, char** argv) {
  for (int i =1; // Program name is always the first argument -- argc >= 1
       i < argc; // Till all the arguments are consumed
       i += 2) { // Consume two at a time
    if (0 == strncmp (argv[i], "-b0", 3)) {
      b0 = atoi (argv[i+1]);
    } else if (0 == strncmp (argv[i], "-r", 2)) {
      r = atoi (argv[i+1]);
    } else if (0 == strncmp (argv[i], "-m", 2)) {
      m = atoi (argv[i+1]);
    } else if (0 == strncmp (argv[i], "-q", 2)) {
      q = static_cast<double> (atof (argv[i+1]));
    } else {
      std::cout << "Error --- invalid option: " << argv[i] << std::endl;
      exit (3);
    }
  } // End for

  // optimization
  q *= NORMALIZER;

  std::cout << "==================== UTS ======================" << std::endl;
  std::cout << "Root branching factor (b0) = " << b0 << std::endl;
  std::cout << "Root seed (r) = " << r << std::endl;
  std::cout << "Number of children (m) = " << m << std::endl;
  std::cout << "Probability of a child (q) = " << q << std::endl;

  double time = wsmprtc ();
#if (IMPLEMENTATION==1)
  int num_nodes = 1 + root_binomial_tree_search (sha1_rand (r));
#elif (IMPLEMENTATION==2)
  sha1_rand root_rng(r);
  for (int i=0; i<b0; ++i) work_queue.push(sha1_rand(root_rng, i));
  const unsigned int num_nodes = 1 + binomial_tree_search ();
#elif (IMPLEMENTATION==3)
  sha1_rand root_rng(r);
  for (int i=0; i<b0; ++i) work_queue.push(sha1_rand(root_rng, i));
  const unsigned int num_nodes = 1 + binomial_tree_search ();
#elif (IMPLEMENTATION==4)
  sha1_rand root_rng(r);
  for (int i=0; i<b0; ++i) work_queue.push_back(sha1_rand(root_rng, i));
  const unsigned int num_nodes = 1 + binomial_tree_search ();
#else
#error "Define implementation"
#endif
  time = wsmprtc () - time;

  std::cout << "Sequential " << (num_nodes) << " " 
            << time << " "
            << static_cast<double>(num_nodes)/(time*1e06) << std::endl;

  return 0;
}
