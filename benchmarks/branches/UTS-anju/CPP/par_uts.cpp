#include "sha1_rand.hpp"
#include <cstdio>
#include <iostream>
#include <x10rt_front.h>
#include <cassert>
#include <cstring>
#include <numeric>
#include <sys/time.h>
#include <queue>
#include <vector>

std::deque<sha1_rand> work_queue;

static int b0 = 2000; // Root branching factor
static int r = 42; // Root seed for the random number
static int m = 8; // Number of children 
static double q = 0.124875; // Probability of having a child
static int k = m; // number of work packets that can be stolen at one go
static unsigned int num_nodes = 0; // There is a root node that we don't count
static const double NORMALIZER = static_cast<double>(2147483647);
static bool work_response_received = false;
static bool termination_initiated = false;
static std::vector<bool> terminated_process_list;
static x10rt_place my_true_rank;
static std::vector<double> load_breakdown;


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
  const x10rt_place stealer = *(static_cast<x10rt_place*>(request_ptr->msg));
#if DEBUG
  std::cout << x10rt_here() << " got work request: " << stealer << std::endl;
#endif
  if (!work_queue.empty()) {
    // Try to hand out STEAL_LIMIT if there is enough left over --- otherwise,
    // go on decreasing the number of work packets handed out until something
    // reasonable size is reached.
    unsigned int num_work_packets = 1;

    if (k < work_queue.size()) 
      num_work_packets = k;
    else if (k/2 < work_queue.size()) 
      num_work_packets = k/2;
    else 
      num_work_packets = 1;

#if DEBUG
    std::cout << x10rt_here() << " transfering " << num_work_packets 
              << " work packets to " << stealer << std::endl;
#endif

    const unsigned int msg_len = 20*num_work_packets + sizeof(unsigned int);

    unsigned char* work = 
      static_cast<unsigned char*> (x10rt_msg_realloc (NULL, 0, msg_len));

    *(reinterpret_cast<unsigned int*>(work)) = num_work_packets;

    unsigned char* cur_work_ptr = work + sizeof(unsigned int);
    for (int i=0; i<num_work_packets; ++i, cur_work_ptr+=20) {
      work_queue.back().copy (cur_work_ptr);
      work_queue.pop_back();
    }
    x10rt_msg_params work_packet = {stealer, WORK, work, msg_len};
#if DEBUG
    std::cout << x10rt_here() << " sending work out" << std::endl;
#endif
    x10rt_send_msg (&work_packet);
  } else {
    unsigned int* work = static_cast<unsigned int*>
                        (x10rt_msg_realloc (NULL, 0, sizeof(unsigned int)));
    *work = 0;
    x10rt_msg_params work_packet = {stealer, WORK, work, sizeof(unsigned)};
#if DEBUG
    std::cout << x10rt_here() << " no work to send out" << std::endl;
#endif
    x10rt_send_msg (&work_packet);
  }
}

// Handle work coming in
static void recv_work (const x10rt_msg_params* work_ptr) {
  work_response_received = true;
  const unsigned int num_work_packets = 
                      *(static_cast<unsigned int*>(work_ptr->msg));
  if (0!=num_work_packets) { // got work
#if DEBUG
    std::cout << x10rt_here() << " got work" << std::endl;
#endif
    unsigned char* cur_work_ptr = static_cast<unsigned char*>(work_ptr->msg) + 
                                  sizeof (unsigned int);
    for (int i=0; i<num_work_packets; ++i, cur_work_ptr+=20) {
      work_queue.push_back (sha1_rand(cur_work_ptr));
    }
  } else { // no work
    // nothing 
#if DEBUG
    std::cout << x10rt_here() << " did not get work" << std::endl;
#endif
  }
}

// Handle termination 
static void recv_termination (const x10rt_msg_params* terminate_ptr) {
  // Process all the remaining nodes
  const x10rt_place terminated_rank = 
                  *(static_cast<x10rt_place*>(terminate_ptr->msg));
  terminated_process_list [terminated_rank] = true;

#if DEBUG
  std::cout << x10rt_here() << " termination notice received from " 
            << terminated_rank << std::endl;
#endif

  // Add up the count at 0
  if (0==x10rt_here()) {
    const unsigned int num_remote_nodes = 
                *(static_cast <unsigned int*> (terminate_ptr->msg)+2);
    num_nodes += num_remote_nodes;

#if DEBUG
    std::cout << "Adding " << num_remote_nodes 
              << " from " << terminated_rank 
              << ", total = " << num_nodes << std::endl;
#endif
    load_breakdown [terminated_rank] = static_cast<double>(num_remote_nodes);
  }
}


/* When a termination notice is received, all we have to do is send the count
 * to place 0 and then exit.
 */
static void initiate_termination () {
#if DEBUG
  std::cout << x10rt_here() << " initiating termination" << std::endl;
#endif


  unsigned int* num_nodes_buffer = static_cast <unsigned int*> 
                      (x10rt_msg_realloc (NULL, 0, sizeof(unsigned int)));
  for (int i=0; i<x10rt_nplaces(); ++i) {
    if (x10rt_here() != i) {
      if (0==i) {
        void* num_nodes_buffer = 
      (x10rt_msg_realloc (NULL, 0, sizeof(unsigned int)+sizeof(x10rt_place)));
        *(static_cast<x10rt_place*>(num_nodes_buffer)) = x10rt_here();
        *(static_cast<unsigned int*>(num_nodes_buffer)+2) = num_nodes;
        x10rt_msg_params terminate_msg = 
                      {i, TERMINATE, num_nodes_buffer, 
                       sizeof (unsigned int)+sizeof (x10rt_place)};
        x10rt_send_msg (&terminate_msg);

      } else {
        x10rt_place* id_buffer = static_cast <x10rt_place*> 
                   (x10rt_msg_realloc (NULL, 0, sizeof(x10rt_place)));
        *id_buffer = x10rt_here();
        x10rt_msg_params terminate_msg = 
                      {i, TERMINATE, id_buffer, sizeof (x10rt_place)};
        x10rt_send_msg (&terminate_msg);
      }
    }
  }
  terminated_process_list [x10rt_here()] = true;
  termination_initiated = true;
}

static void get_work(void) {
#if DEBUG
  std::cout << x10rt_here() << " trying to steal work" << std::endl;
#endif

  const int begging_start_node = (x10rt_here()+1) % x10rt_nplaces();
  const int begging_stop_node = (begging_start_node+x10rt_nplaces()-1) 
                                % x10rt_nplaces();

  for (int i=begging_start_node; 
       i!=begging_stop_node;
       i=(i+1)%x10rt_nplaces()) {
    if (x10rt_here() != i && !terminated_process_list[i]) {
      x10rt_place* my_rank = static_cast <x10rt_place*> 
          (x10rt_msg_realloc (NULL, 0, sizeof(x10rt_place)));
      *my_rank = x10rt_here();
#if DEBUG
        std::cout << x10rt_here() << " sending request to " 
                  << i << std::endl;
#endif
        x10rt_msg_params message = 
                      {i, WORK_REQUEST, my_rank, sizeof(x10rt_place)};
        x10rt_send_msg (&message);
        work_response_received = false;
        while (!work_response_received && !terminated_process_list[i]) 
                                                            x10rt_probe (); 
        if (!work_queue.empty()) break;
      }
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
#if DEBUG
  std::cout << x10rt_here() << "," << num_nodes << ", Number: " << rng()
            << ", Children: " << num_children
            << std::endl;
#endif

#if DEBUG
  std::cout << x10rt_here() << " num nodes = " << num_nodes << std::endl;
#endif
}

static bool all_process_are_terminated() {
  for (int i=0; i<terminated_process_list.size(); ++i) 
    if (false == terminated_process_list[i]) return false;
  return true;
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

  if (0==x10rt_here()) {
    while (!all_process_are_terminated()) x10rt_probe();
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
    if (0 == strncmp (argv[i], "-b0", 3)) {
      b0 = atoi (argv[i+1]);
    } else if (0 == strncmp (argv[i], "-r" ,2)) {
      r = atoi (argv[i+1]);
    } else if (0 == strncmp (argv[i], "-m", 2)) {
      m = atoi (argv[i+1]);
    } else if (0 == strncmp (argv[i], "-q", 2)) {
      q = static_cast<double> (atof (argv[i+1]));
      // Optimization --- don't have to multiply everytime
    } else if (0 == strncmp (argv[i], "-k", 2)) {
      k = atoi (argv[i+1]);
    } else {
      std::cout << "Error --- invalid option: " << argv[i] << std::endl;
      exit (3);
    }
  } // End for
  
  // Optimization --- dont have to normalize the rng() if we do this.
  q *= NORMALIZER;

  // Allocate the placeholder for the terminated processes
  terminated_process_list.resize(x10rt_nplaces(), false);
  load_breakdown.resize(x10rt_nplaces(), 0.0);

  my_true_rank = x10rt_here();

#if DEBUG
  if (0==x10rt_here ()) {
    std::cout << "==================== UTS ======================" << std::endl;
    std::cout << "Root branching factor (b0) = " << b0 << std::endl;
    std::cout << "Root seed (r) = " << r << std::endl;
    std::cout << "Number of children (m) = " << m << std::endl;
    std::cout << "Probability of a child (q) = " << q << std::endl;
  }
#endif

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
#if DEBUG
  if (0==x10rt_here ()) {
    std::cout << "There were " << (1+num_nodes) << " nodes mined in " 
              << time << " secs" << std::endl;
    std::cout << "===============================================" << std::endl;
  }
#endif
  if (0==x10rt_here ()) {
    // Lets calculate the load breakdown of all the places
    unsigned int nodes_processed_outside_rank_0 = 0;
    for (int i=1; i<x10rt_nplaces(); ++i) {
      nodes_processed_outside_rank_0 +=
                     static_cast<unsigned int>(load_breakdown[i]);
      load_breakdown[i] /= static_cast<double>(num_nodes+1);
    }
    load_breakdown[0] = 
      static_cast<double>(num_nodes+1-nodes_processed_outside_rank_0)/
      static_cast<double>(num_nodes+1);

    std::cout << x10rt_nplaces() << " " << k << " " << (num_nodes+1) << " " 
              << time << " "
              << static_cast<double>(1+num_nodes)/(time*1e06) << " ";

    // print out the load breakdown
    std::cout << "(";
    for (int i=0; i<x10rt_nplaces(); ++i) {
      printf ("%0.3f%%", load_breakdown[i]);
      if ((x10rt_nplaces()-1)!=i) std::cout << " ";
    }
    std::cout << ")" << std::endl;
  }

  // Finalize the runtime
  x10rt_finalize ();

  return 0;
}
