#include  <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/time.h>
#include <time.h>
#include <lapi.h>
#include "timers.h"
#include <iostream>

using namespace std;

#define RC(statement) \
{ \
    if (int rc = statement) { \
        printf(#statement " rc = %d, line %d\n", rc, __LINE__); \
        exit(-1); \
    } \
}

//#ifdef  VERIFY
//#define UPDATE(offset, ran) table[offset] ++;
//#else
#define UPDATE(offset, ran) table[offset] ^= ran;
//#endif

typedef unsigned long long u64Int;
typedef signed long long s64Int;

const u64Int MIN_NUM_UPDATES = 1<<23;
int    log_num_updates = -1;
int    log_table_size = 28;
u64Int num_updates, table_size;
u64Int  *table;
bool aggregate = false, embarassing = false;

lapi_handle_t  hndl;
int            num_tasks;
unsigned int            my_id;
lapi_am_t      am;
lapi_thread_func_t  tf;

double microseconds()
{
    struct timeval time_v;
    gettimeofday ( &time_v, NULL );
    return time_v.tv_sec * 1e6 +  time_v.tv_usec;
}

/* Utility routine to start random number generator at Nth step */

const u64Int POLY    = 7LL;
const u64Int PERIOD  = 1317624576693539401LL;

u64Int HPCC_starts(s64Int n)
{
  int i, j;
  u64Int m2[64];
  u64Int temp, ran;

  while (n < 0) n += PERIOD;
  while (n > PERIOD) n -= PERIOD;
  if (n == 0) return 0x1;

  temp = 0x1;
  for (i=0; i<64; i++) {
    m2[i] = temp;
    temp = (temp << 1) ^ ((s64Int) temp < 0 ? POLY : 0);
    temp = (temp << 1) ^ ((s64Int) temp < 0 ? POLY : 0);
  }

  for (i=62; i>=0; i--)
    if ((n >> i) & 1)
      break;

  ran = 0x2;
  while (i > 0) {
    temp = 0;
    for (j=0; j<64; j++)
      if ((ran >> j) & 1)
        temp ^= m2[j];
    ran = temp;
    i -= 1;
    if ((n >> i) & 1)
      ran = (ran << 1) ^ ((s64Int) ran < 0 ? POLY : 0);
  }

  return ran;
}

const int AGG_LIMIT = 1024;
const int MAX_TASKS = 256;

struct element {
    int dest;
    int count;
    struct element *before, *after;
    u64Int update[AGG_LIMIT];
};

element bucket[MAX_TASKS];
element *head[AGG_LIMIT+1];
long nSends = 0, nElements = 0;
long maxBucketSize;
long searchCount = 0;

void push(element *e)
{
    e->before = NULL;
    e->after = head[e->count];
    head[e->count] = e;
    if (e->after)
        e->after->before = e;
}

void deq(element *e)
{
    if (e->before)
        e->before->after = e->after;
    else
        head[e->count] = e->after;
    if (e->after)
        e->after->before = e->before;
}

void init_buckets()
{
    for (int i = 1; i < AGG_LIMIT; i++)
        head[i] = NULL;
    maxBucketSize = 0;
    head[0] = &bucket[0];
    for (int i = 0; i < num_tasks; i++) {
        bucket[i].dest = i;
        bucket[i].count = 0;
        bucket[i].before = &bucket[i-1];
        bucket[i].after = &bucket[i+1];
    }
    bucket[0].before = NULL;
    bucket[num_tasks-1].after = NULL;
}

void add_to_bucket(int dest, u64Int ran)
{
    element *e = &bucket[dest];
    deq(e);
    e->dest = dest;
    e->update[e->count] = ran;
    e->count++;
    push(e);

    if (e->count > maxBucketSize)
        maxBucketSize = e->count;

    nElements++;
    if (nElements >= AGG_LIMIT) {
        element *e = head[maxBucketSize];
        //assert(e->count == maxBucketSize);

        nSends++;

        lapi_cntr_t cntr;
        int tmp;
        LAPI_Setcntr (hndl, &cntr, 0);
        RC( LAPI_Amsend(hndl, e->dest, (void *)1, NULL, 0,
                    e->update, e->count * sizeof(u64Int),
                    NULL, &cntr, NULL ));
        LAPI_Waitcntr (hndl, &cntr, 1, &tmp);

        deq(e);
        e->count = 0;
        push(e);
        nElements -= maxBucketSize;

        
        while (head[maxBucketSize] == NULL) {
            searchCount++;
            maxBucketSize--;
        }

       /* 
       maxBucketSize = 0;
       for (int i = 0; i < num_tasks; i++) {
            searchCount++;
         if (bucket[i].count > maxBucketSize)
             maxBucketSize = bucket[i].count;
       }*/
    }
    //assert(nElements < AGG_LIMIT);
}

void flush_buckets()
{
    for (int dest = 0; dest < num_tasks; dest++) {
        element *e = &bucket[dest];
        if (e->count > 0) {
            nSends++;
            RC( LAPI_Amsend(hndl, dest, (void *)1, NULL, 0,
                        e->update, e->count * sizeof(u64Int),
                        NULL, NULL, NULL) );
        }
    }
}

u64Int update_buffer[AGG_LIMIT];
#ifdef USE_WORKERS
const int WORK_LIMIT = 256;
const int LOG_NUM_WORKER_THREADS = 0;
const int NUM_WORKER_THREADS = (1<<LOG_NUM_WORKER_THREADS);
struct work_t {
    volatile int head;
    volatile int tail;
    u64Int update[WORK_LIMIT];
} work_load[NUM_WORKER_THREADS];

void *worker_thread(void *param)
{
    long id = (long)param;
    work_t *work = &work_load[id];
    do {
        while (work->head == work->tail);
        u64Int value = work->update[work->head];
        u64Int offset = value & (table_size-1);
        UPDATE(offset, value);
        work->head++;
        if (work->head == WORK_LIMIT)
            work->head = 0;
    } while (1);
}

void init_workers()
{
    for (long i = 1; i < NUM_WORKER_THREADS; i++) {
        work_t *work = &work_load[i];
        work->head = work->tail = 0;
        
        pthread_t th;
        pthread_create(&th, NULL, worker_thread, (void *)i);
    }
}
#endif

u64Int gups_initialize()
{
#ifdef USE_WORKERS
    init_workers();
#endif
    init_buckets();
    return HPCC_starts(my_id * num_updates);
}

void batch_update(u64Int *value, ulong msg_len)
{
    for (; msg_len > 0; msg_len -= sizeof(u64Int)) {
        u64Int offset = *value & (table_size-1);
#ifdef USE_WORKERS
        u64Int worker = offset >> (log_table_size - LOG_NUM_WORKER_THREADS);
        if (worker == 0)
            UPDATE(offset, *value);
        else {
            work_t *work = &work_load[worker];
            int new_tail = work->tail + 1;
            if (new_tail == WORK_LIMIT)
                new_tail = 0;
            while (work->head == new_tail);
            work->update[work->tail] = *value;
            work->tail = new_tail;
        }
#else
        UPDATE(offset, *value);
#endif
        value++;
    }
}
struct comp
{
 ulong len;
 void* buf;
};
void complete_update(lapi_handle_t *hndl, void *completion_param)
{
    comp* c = (comp*) completion_param;
    batch_update((u64Int*) (c->buf), c->len); 
    delete[] c->buf;
    delete c;
}

void * receive_update(lapi_handle_t *hndl, void *uhdr, uint *uhdr_len,
        ulong *msg_len, compl_hndlr_t **ucomp, void **uinfo)
{
    lapi_return_info_t *ret_info = (lapi_return_info_t *)msg_len;
    if (ret_info->udata_one_pkt_ptr) {
        batch_update( (u64Int *)ret_info->udata_one_pkt_ptr, *msg_len );
        ret_info->ctl_flags = LAPI_BURY_MSG;
        *ucomp = NULL;
        return NULL;
    } else {
        ret_info->ret_flags = LAPI_LOCAL_STATE;
        *ucomp = complete_update;
        comp* c = new comp;
        c->len = *msg_len;
        c->buf = (void*) new char[*msg_len];
        *uinfo = (void *) c;
        return c->buf;
    }
}

inline
void send_update(int dest, u64Int value)
{
    am.tgt       = dest;
    am.udata      = &value;
    RC( LAPI_Xfer(hndl, (lapi_xfer_t *)&am) );
    nSends++;
}

void read_arguments(int argc, char *argv[])
{
    int pos = 0;
    for (int i = 1; i < argc; i++) {
        if (argv[i][0] == '-') {
            switch (argv[i][1]) {
                case 'a': aggregate = true; break;
                case 'e': embarassing = true; break;
                default:  printf("unknown switch -%c\n", argv[i][1]); exit(0);
            }
        } else {
            switch (pos) {
                case 0: log_table_size = atoi(argv[i]); break;
                case 1: log_num_updates = atoi(argv[i]); break;
                default: printf("extra argument: %s\n", argv[i]); exit(0);
            }
            pos++;
        }
    }
    table_size = 1UL << log_table_size;
    table = new u64Int[table_size];
    assert(table);
    memset(table, 0, table_size * sizeof(uint64_t));
    if (log_num_updates == -1)
        log_num_updates = log_table_size + 2;
    num_updates = 1UL << log_num_updates;
    //if (num_updates < MIN_NUM_UPDATES)
      //  num_updates = MIN_NUM_UPDATES;
}

void lapi_initialize()
{
    // initialize LAPI
    lapi_info_t    lapi_info;
    memset(&lapi_info, 0, sizeof(lapi_info));
    RC( LAPI_Init(&hndl, &lapi_info) );

    // communication setup
    RC( LAPI_Addr_set(hndl, (void *)receive_update, 1) );
    RC( LAPI_Qenv(hndl, NUM_TASKS, &num_tasks) );
    RC( LAPI_Qenv(hndl, TASK_ID, (int*) &my_id) );
    RC( LAPI_Senv(hndl, INTERRUPT_SET, 0) );
    RC( LAPI_Senv(hndl, ERROR_CHK, 0) );

    // initialize LW command
    memset(&am, 0, sizeof(am));
    am.Xfer_type = LAPI_AM_LW_XFER;
    am.hdr_hdl   = 1;
    am.udata_len  = sizeof(u64Int);

    // get shared lock to prevent other threads from running
    tf.Util_type = LAPI_GET_THREAD_FUNC;
    RC( LAPI_Util(hndl, (lapi_util_t *)&tf) );
    tf.mutex_lock(hndl);
}

void lapi_terminate()
{
    tf.mutex_unlock(hndl);
    RC( LAPI_Term(hndl) );
}

int main(int argc, char *argv[])
{
    read_arguments(argc, argv);
    lapi_initialize();
    if (num_tasks & (num_tasks - 1)) {
        printf("num_tasks must be power of 2\n");
        exit(-1);
    }
    if (my_id == 0) {
        printf("%lld dwords  %lld updates  ", table_size, num_updates);
        fflush(stdout);
    }

    u64Int ran = gups_initialize();
    RC( LAPI_Gfence(hndl) );
    //double t = microseconds();
    s64Int mask = table_size - 1;
    int placeidmask = num_tasks - 1;
    double t = nanoTime();
    for (s64Int i = 0; i < num_updates; i++) {
        unsigned int dest = ((int) (ran >> log_table_size)) & placeidmask;

        u64Int temp = ran;
        ran = (ran << 1) ^ ((s64Int)ran < 0 ? POLY : 0);
        if (dest == my_id) {
            UPDATE((temp & mask), temp);
        } else {
            if (aggregate)
                add_to_bucket(dest, temp);
            else
                send_update(dest, temp);
        }
    }
    if (aggregate)
        flush_buckets();
    RC( LAPI_Gfence(hndl) );
    //t = microseconds() - t;
    t = nanoTime() - t;
    if (my_id == 0) {
        printf("%f GUPS  %.5f seconds\n", (double)num_updates * num_tasks/t,
                t*1e-9);
        printf("nSends = %ld, searchCount = %ld\n", nSends, searchCount);
    }

#ifdef VERIFY
    ran = gups_initialize();
    for (s64Int i = 0; i < num_updates; i++) {
        unsigned int dest = ((int) (ran >> log_table_size)) & placeidmask;

        u64Int temp = ran;
        ran = (ran << 1) ^ ((s64Int)ran < 0 ? POLY : 0);
        if (dest == my_id) {
            UPDATE((temp & mask), temp);
        } else {
            if (aggregate)
                add_to_bucket(dest, temp);
            else
                send_update(dest, temp);
        }
    }
    if (aggregate)
        flush_buckets();
    RC( LAPI_Gfence(hndl) );
    u64Int sum = 0;
    for (ran = 0; ran < table_size; ran++)
        sum += table[ran];
    printf("sum = %lld\n", sum);
    assert (sum == 0);
#endif
   
    lapi_terminate();
    return 0;
}
