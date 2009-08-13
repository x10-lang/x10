// This will only work on AIX
#include "x10_runtime_VMInterface.h"
#include <pthread.h>
#include <procinfo.h>
#include <sys/types.h>
#include <unistd.h>
#include <errno.h>
#include <stdio.h>
#include <sys/processor.h>
#include <sys/systemcfg.h>

// Returns the number of CPUs on the current system (by consulting
// an AIX data structure.)
JNIEXPORT jint JNICALL Java_x10_runtime_VMInterface_getNumCPUs
(JNIEnv *e, jclass c) {
  return _system_configuration.ncpus;
}

// Binds the calling thread to the specified CPU (by calling an AIX
// specific method.)
JNIEXPORT void JNICALL Java_x10_runtime_VMInterface_putMeOnCPU
(JNIEnv *e, jclass c, jint cpu) {
  pthread_t x = pthread_self();
  struct __pthrdsinfo p;
  int q;
  if (0 == pthread_getthrds_np(&x,                      // Start with myself
                               PTHRDSINFO_QUERY_TID,    // Get my TID
                               &p,                      // Put it here
                               sizeof(p),               // This much room
                               NULL,                    // Don't need regs.
                               &q)) {                   // size of retured res
    if (0 == bindprocessor(BINDTHREAD, p.__pi_tid, cpu)) {
    } else {
      // Throw exception ?
    }
  } else {
    // Throw exception ?
  }
}

