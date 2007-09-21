/*
 * Crude benchmark to measure communication initiation and roundtip costs.
 */

#include <strings.h>
#include "lapi.h"
#include <assert.h>

#include <iostream>

using namespace std;

/*--------------assert macro-------------------------*/

#define RC(statement) \
{ int rc; \
    if ((rc = statement) != LAPI_SUCCESS) { \
        printf(#statement " rc = %d, line %d\n", rc, __LINE__); \
        exit(-1); \
    } \
}

/*---------------Timing routines---------------------*/

typedef long long sint64_t;

typedef sint64_t nano_time_t;

nano_time_t nanoTime() {
  struct timespec ts;
  // clock_gettime is POSIX!
  ::clock_gettime(CLOCK_REALTIME, &ts);
  return (nano_time_t)(ts.tv_sec * 1000000000LL + ts.tv_nsec);
}

/*--------------Communication interface---------------*/

struct {
lapi_handle_t lapi_hndl;
int num_tasks, my_id;
lapi_cntr_t wait_cntr;
lapi_thread_func_t  tf;        /*< ??? */
} comm_stuff;

void lapi_initialize(lapi_handle_t *hndl, int *num_tasks, int *my_id,
		     lapi_cntr_t *wait_cntr, lapi_thread_func_t *tf) {
  lapi_info_t    lapi_info;
  memset(&lapi_info, 0, sizeof(lapi_info));
  RC( LAPI_Init(hndl, &lapi_info) );
  
  // communication setup
  //     RC( LAPI_Addr_set(hndl, (void *)receive_update, 1) );
    RC( LAPI_Qenv(*hndl, NUM_TASKS, num_tasks) );
    RC( LAPI_Qenv(*hndl, TASK_ID, my_id) );
    RC( LAPI_Senv(*hndl, INTERRUPT_SET, 0) );
    RC( LAPI_Senv(*hndl, ERROR_CHK, 0) );
    (void)LAPI_Setcntr(*hndl, wait_cntr, 0);
    
    //     // initialize LW command
//     memset(&am, 0, sizeof(am));
//     am.Xfer_type = LAPI_AM_LW_XFER;
//     am.hdr_hdl   = 1;
//     am.udata_len  = sizeof(u64Int);
    
    // get shared lock to prevent other threads from running
    tf->Util_type = LAPI_GET_THREAD_FUNC;
    RC( LAPI_Util(*hndl, (lapi_util_t *)tf) );
    tf->mutex_lock(*hndl);
}

void lapi_terminate(lapi_handle_t hndl, lapi_thread_func_t *tf) {
  tf->mutex_unlock(hndl);
  LAPI_Term(hndl);
}

int main(int argc, char *argv[]) {
  int startbs, endbs, stepbs, reps;
  lapi_initialize(&comm_stuff.lapi_hndl, 
		  &comm_stuff.num_tasks, 
		  &comm_stuff.my_id, &comm_stuff.wait_cntr,
		  &comm_stuff.tf);


  assert(comm_stuff.num_tasks>1); /*need atleast 2 for this test*/

  if(argc < 5) {
    cout<<"Usage: "<<argv[0]<<" <startbs> <endbs> <stepbs> <reps> "<<endl;
    lapi_terminate(comm_stuff.lapi_hndl, &comm_stuff.tf);
    return 0;
  }

  startbs = atoi(argv[1]);
  endbs = atoi(argv[2]);
  stepbs = atoi(argv[3]);
  reps = atoi(argv[4]);

  cout<<"startbs="<<startbs<<" endbs="<<endbs<<" stepbs="<<stepbs<<" reps="<<reps<<endl;

  assert(startbs>0);
  assert(endbs > startbs);
  assert(stepbs>0);
  assert(reps>0);

  for(int sz=startbs; sz<=endbs; sz+=stepbs) {
    char *buf = new char[sz];
    assert(buf != NULL);
    char **table = new char *[comm_stuff.num_tasks];
    assert(table != NULL);

    LAPI_Address_init(comm_stuff.lapi_hndl, buf, (void **)table);
    for(int j=0; j<comm_stuff.num_tasks; j++) {
      assert(table[j] != NULL);
    }
    
    if(comm_stuff.my_id==0) {
      int proc = 1;
      int bytes = sz;

      nano_time_t s1, s2, s3;
      nano_time_t initn=0, waitn=0, rtn=0;

      s1 = nanoTime();
      for(int r = 0; r<reps; r++) {
	int v;
	LAPI_Getcntr(comm_stuff.lapi_hndl, &comm_stuff.wait_cntr, &v);
	assert(v==0);
	
	s2 = nanoTime();
// 	LAPI_Get(comm_stuff.lapi_hndl, proc, bytes, table[1], buf, 
// 		 NULL, &comm_stuff.wait_cntr);
	LAPI_Put(comm_stuff.lapi_hndl, proc, bytes, table[1], buf, 
		 NULL, NULL, &comm_stuff.wait_cntr);
	initn += nanoTime()-s2;
	
	s3 = nanoTime();
	LAPI_Waitcntr(comm_stuff.lapi_hndl, &comm_stuff.wait_cntr, v+1, NULL);
	waitn += nanoTime()-s3;
      }
      rtn += nanoTime()-s1;
      
      cout<<"bytes="<<bytes<<" init="<<initn/1000/reps<<"us"
	  <<" wait="<<waitn/1000/reps<<"us"
	  <<" rt="<<rtn/1000/reps<<"us"
	  <<endl;
    }

    LAPI_Gfence(comm_stuff.lapi_hndl);
    delete [] buf;
    delete [] table;
  }
  
  lapi_terminate(comm_stuff.lapi_hndl, &comm_stuff.tf);
}

