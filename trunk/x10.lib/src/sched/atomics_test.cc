#include "Sys.h"
#include <iostream>
#include <pthread.h>
#include <assert.h>

using namespace std;
using namespace x10lib_cws;

const int nReps = 1000;

class arg_t {
public:
  volatile int *el;
  int tid;
  arg_t(volatile int *_el, int _tid) 
    : el(_el), tid(_tid) {}
};

void *atomic_add_test(void *in) {
  arg_t *args = (arg_t *)in;

  for(int i=0; i<nReps; i++) {
    atomic_add(args->el, args->tid);
  }
  delete args;
  return NULL;
}

int main(int argc, char *argv[]) {
  int nthreads;
  volatile int arr[1];
  pthread_attr_t attr;
  pthread_t *threads;
  arg_t *args;
  
  if(argc != 2) {
    cerr<<"Usage:"<<argv[0]<<" <nthreads>"<<endl;
    exit(0);
  }

  nthreads = atoi(argv[1]);
  assert(nthreads>0);
  
  threads = (pthread_t *)malloc(sizeof(pthread_t) * nthreads);
  assert(threads != NULL);

  arr[0] = 0;

  pthread_attr_init(&attr); 
  pthread_attr_setscope(&attr, PTHREAD_SCOPE_SYSTEM); 
  
  for(int i=0; i<nthreads; i++) {
    args = new arg_t(arr, i+1);
    int res = pthread_create(&threads[i], 
			     &attr,
			     atomic_add_test, 
			     (void *) args);
    if (res) {cerr << "could not create" <<endl; abort(); }
  }

  for(int i=0; i<nthreads; i++) {
    pthread_join(threads[i], (void **)NULL);
  }
  free(threads);

  cout<<"numThreads="<<nthreads<<" nReps="<<nReps<<endl;
  cout<<"Expected="<<nthreads*(nthreads+1)/2*nReps<<" Obtained="
      <<arr[0]<<endl;
  cout<<((arr[0]==nthreads*(nthreads+1)/2*nReps)?"OK":"Error!")<<endl;
}
