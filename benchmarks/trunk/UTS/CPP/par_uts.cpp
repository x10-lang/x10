#include "sha1_rand.hpp"
#include <iostream>
#include <x10rt_front.h>
#include <cassert>
#include <cstring>
#include <numeric>
#include <sys/time.h>
#include <queue>

std::deque<sha1_rand> work_queue;

static int b0 = 4; // Root branching factor
static int r = 0; // Root seed for the random number
static int m = 4; // Number of children 
static double q = 15.0/64.0; // Probability of having a child
static uint32_t num_nodes = 0; // There is a root node that we don't count
static const double NORMALIZER = std::numeric_limits<int>::max();
static bool work_response_received = false;
static bool termination_initiated = false;

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

// Message types
x10rt_msg_type WORK_REQUEST, WORK, TERMINATE;

static void recv_work_request (const x10rt_msg_params*);
static void recv_work (const x10rt_msg_params*);
static void recv_termination (const x10rt_msg_params*);
static void initiate_termination ();
static void process_node ();
static void get_work ();
static void binomial_tree_search ();

// Handle a work request
static void recv_work_request (const x10rt_msg_params* request_ptr) {
  static unsigned char* work = static_cast<unsigned char*>
                                (x10rt_msg_realloc (NULL, 0, 20));
  if (!work_queue.empty()) {
    work_queue.back().copy (work);
    work_queue.pop_back();
    x10rt_msg_params work_packet = 
    {*(static_cast<x10rt_place*>(request_ptr->msg)), WORK, work, 20};
    std::cout << x10rt_here() << " sending work out" << std::endl;
    x10rt_send_msg (&work_packet);
  } else {
    x10rt_msg_params work_packet = 
    {*(static_cast<x10rt_place*>(request_ptr->msg)), WORK, NULL, 0};
    std::cout << x10rt_here() << " no work to send out" << std::endl;
    x10rt_send_msg (&work_packet);
  }
}

// Handle work coming in
static void recv_work (const x10rt_msg_params* work_ptr) {
  work_response_received = true;
  if (20==work_ptr->len) { // got work
    std::cout << x10rt_here() << " got work" << std::endl;
    work_queue.push_back(sha1_rand(static_cast<unsigned char*>(work_ptr->msg)));
  } else { // no work
    // nothing 
    std::cout << x10rt_here() << " did not get work" << std::endl;
  }
}

// Handle termination 
static void recv_termination (const x10rt_msg_params* terminate_ptr) {
  // Process all the remaining nodes
  std::cout << x10rt_here() << " termination notice received" << std::endl;
  while (!work_queue.empty()) process_node();
  termination_initiated = true;
}

static void initiate_termination () {
  static x10rt_place* my_place = static_cast<x10rt_place*>
                (x10rt_msg_realloc (NULL, NULL, sizeof(x10rt_place)));

  std::cout << *my_place << " initiating termination" << std::endl;

  for (int i=0; i<x10rt_nplaces(); ++i) {
    if (x10rt_here() != i) {
      x10rt_msg_params message = 
                    {i, TERMINATE, my_place, sizeof(x10rt_place)};
      x10rt_send_msg (&message);
    }
  }
}

static void get_work(void) {
  static x10rt_place* my_place = static_cast<x10rt_place*>
                (x10rt_msg_realloc (NULL, NULL, sizeof(x10rt_place)));
  *my_place = x10rt_here ();

  std::cout << *my_place << " trying to steal work" << std::endl;

  for (int i=0; i<x10rt_nplaces(); ++i) {
    if (x10rt_here() != i) {
      x10rt_msg_params message = 
                    {i, WORK_REQUEST, my_place, sizeof(x10rt_place)};
      x10rt_send_msg (&message);
      work_response_received = false;
      while (!work_response_received) x10rt_probe (); 
      if (!work_queue.empty()) break;
    }
  }
}

/*
 * Process a single node. This routine is called ONLY when you are sure there
 * is some work to do.
 */
static void process_node (void) {
  assert (!work_queue.empty());
  const sha1_rand rng = work_queue.back ();
  work_queue.pop_back ();
  const int num_children = (static_cast<double>(rng()) < q) ? m : 0;
  for (int i=0; i<num_children; ++i) {
    work_queue.push_back(sha1_rand(rng, i));
  }
  ++num_nodes;
}

/*
 * Algorithm:
 * While (true) {
 *   1. Check if there is local work. If so, process work.
 *   2. If no work, try to steal.
 *   3. If no steal either, then maybe there is nothing.
 * }
 */
static void binomial_tree_search (void) {
  while (!termination_initiated) { 

    // Can work! Yahoo
    if (!work_queue.empty()) { 
      process_node ();
    } else { 
      get_work();
      // Initiate termination proceedings if the queue is empty
      if (work_queue.empty()) {
        initiate_termination ();
        break;
      } else {
        process_node ();
      }
    }

    // Handle requests
    x10rt_probe ();
  }
}

int main (int argc, char** argv) {
  // Initialize the runtime and register the handler
  x10rt_init (&argc, &argv);
  WORK_REQUEST = 
    x10rt_register_msg_receiver (recv_work_request, NULL, NULL, NULL, NULL);
  WORK = 
    x10rt_register_msg_receiver (recv_work, NULL, NULL, NULL, NULL);
  TERMINATE = 
    x10rt_register_msg_receiver (recv_termination, NULL, NULL, NULL, NULL);
  x10rt_registration_complete ();

  // Executed everywhere --- so all nodes know what m, q, r, and b0 are
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
      // Optimization --- don't have to multiply everytime
      q *= NORMALIZER;
    } else {
      std::cout << "Error --- invalid option: " << argv[i] << std::endl;
      exit (3);
    }
  } // End for

  if (0==x10rt_here ()) {
    std::cout << "==================== UTS ======================" << std::endl;
    std::cout << "Root branching factor (b0) = " << b0 << std::endl;
    std::cout << "Root seed (r) = " << r << std::endl;
    std::cout << "Number of children (m) = " << m << std::endl;
    std::cout << "Probability of a child (q) = " << q/NORMALIZER << std::endl;
  }

  // Add all the children of the root node at PLACE 0
  if (0==x10rt_here ()) {
    sha1_rand root_rng = sha1_rand (r);
    for (int i=0; i<b0; ++i) work_queue.push_back (sha1_rand (root_rng, i));
  }

  double time;
  if (0==x10rt_here ()) time = wsmprtc ();
  binomial_tree_search ();
  if (0==x10rt_here ()) time = wsmprtc () - time;

  // Print out the statistics at PLACE 0
  if (0==x10rt_here ()) {
    std::cout << "There were " << (1+num_nodes) << " nodes mined in " 
              << time << " secs" << std::endl;
    std::cout << "===============================================" << std::endl;
  }

  // Finalize the runtime
  x10rt_finalize ();

  return 0;
}
