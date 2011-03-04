#include "Sys.h"
#include <iostream>
#include <pthread.h>
#include <assert.h>
#include <time.h>

using namespace std;
using namespace x10lib_cws;

#if 0
static long long nanoTime() {
  struct timespec ts;
  // clock_gettime is POSIX!
  ::clock_gettime(CLOCK_REALTIME, &ts);
  return (long long)(ts.tv_sec * 1000000000LL + ts.tv_nsec);
}
#endif

//const int nReps = 1000;

class arg_t {
public:
  volatile int *el;
  int tid, count;
  arg_t(volatile int *_el, int _tid, int _count) 
    : el(_el), tid(_tid), count(_count) {}
};

void *atomic_add_test(void *in) {
  arg_t *args = (arg_t *)in;

  for(int i=0; i<args->count; i++) {
    atomic_add(args->el, args->tid);
  }
  delete args;
  return NULL;
}

int main(int argc, char *argv[]) {
  int nthreads, count;
  volatile int arr[1];
  pthread_attr_t attr;
  pthread_t *threads;
  arg_t *args;
  long long s, t;
  
  if(argc != 3) {
    cerr<<"Usage:"<<argv[0]<<" <nthreads> <count>"<<endl;
    exit(0);
  }

  nthreads = atoi(argv[1]);
  count = atoi(argv[2]);
  assert(nthreads>0);
  assert(count>0);
  
  threads = (pthread_t *)malloc(sizeof(pthread_t) * nthreads);
  assert(threads != NULL);

  arr[0] = 0;

  pthread_attr_init(&attr); 
  pthread_attr_setscope(&attr, PTHREAD_SCOPE_SYSTEM); 

  s = nanoTime();
  for(int i=0; i<nthreads; i++) {
    args = new arg_t(arr, i+1, count);
    int res = pthread_create(&threads[i], 
			     &attr,
			     atomic_add_test, 
			     (void *) args);
    if (res) {cerr << "could not create" <<endl; abort(); }
  }

  for(int i=0; i<nthreads; i++) {
    pthread_join(threads[i], (void **)NULL);
  }
  t = nanoTime();
  free(threads);

  cout<<"numThreads="<<nthreads<<" count="<<count<<endl;
  cout<<"Expected="<<nthreads*(nthreads+1)/2*count<<" Obtained="
      <<arr[0]<<endl;
  cout<<((arr[0]==nthreads*(nthreads+1)/2*count)?"OK":"Error!")<<endl;

  cout<<"Time = "<<(t-s)/1000<<"us"<<endl;
}
