/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: gups.cpp,v 1.3 2007-05-10 05:42:38 srkodali Exp $
 * This file is part of X10 Runtime System.
 *
 * Original version of this code using LAPI C/C++ is due to
 * Hanhong Xue <hanhong@us.ibm.com>
 */

/* #include <assert.h> */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/time.h>
#include <time.h>
#include <x10/x10lib.h>

#define X10RC(statement) \
do { \
	int rc = statement; \
	if (rc != X10_OK) { \
		printf(#statement " rc = %d, line %d\n", rc, __LINE__); \
		exit(1); \
	} \
} while (0)

#ifdef  VERIFY
#define UPDATE(offset, ran) table[offset] ++;
#else
#define UPDATE(offset, ran) table[offset] ^= ran;
#endif

typedef unsigned long long u64Int;
typedef signed long long s64Int;

const u64Int MIN_NUM_UPDATES = 1<<23;
int    log_num_updates = -1;
int    log_table_size = 28;
u64Int num_updates, table_size;
u64Int  *table;
bool aggregate = false, embarassing = false;

lapi_handle_t hndl;
int            num_tasks;
int            my_id;

double microseconds()
{
    struct timeval time_v;
    gettimeofday ( &time_v, NULL );
    return time_v.tv_sec * 1e6 +  time_v.tv_usec;
}

/* Utility routine to start random number generator at Nth step */

const u64Int POLY    = 7ULL;
const u64Int PERIOD  = 1317624576693539401ULL;

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
const int MAX_TASKS = 64;

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
    e->update[e->count] = ran;
    e->count++;
    push(e);

    if (e->count > maxBucketSize)
        maxBucketSize = e->count;

    nElements++;
    if (nElements >= AGG_LIMIT) {
		x10_gas_ref_t gas_ref;

        element *e = head[maxBucketSize];
        //assert(e->count == maxBucketSize);

        nSends++;
		gas_ref = MakeGasRef(dest, 0);
		X10RC(x10lib::Xfer(e->update, gas_ref, \
				e->count * sizeof(u64Int), (void *)1));
        deq(e);
        e->count = 0;
        push(e);
        nElements -= maxBucketSize;

        while (head[maxBucketSize] == NULL) {
            searchCount++;
            maxBucketSize--;
        }
    }
    //assert(nElements < AGG_LIMIT);
}

void flush_buckets()
{
    for (int dest = 0; dest < num_tasks; dest++) {
        element *e = &bucket[dest];
        if (e->count > 0) {
            nSends++;
			X10RC(x10lib::Xfer(e->update, MakeGasRef(dest, 0),
					e->count * sizeof(u64Int), (void *)1));
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

void complete_update(lapi_handle_t *hndl, void *completion_param)
{
    batch_update(update_buffer, (ulong)completion_param);
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
        *uinfo = (void *)*msg_len;
        return update_buffer;
    }
}

inline
void send_update(int dest, u64Int value)
{
	X10RC(x10lib::Xfer((void *)&value, MakeGasRef(dest, 0),
			sizeof(u64Int), (void *)1));
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
    memset(table, 0, table_size);
    if (log_num_updates == -1)
        log_num_updates = log_table_size + 2;
    num_updates = 1UL << log_num_updates;
    if (num_updates < MIN_NUM_UPDATES)
        num_updates = MIN_NUM_UPDATES;
}

void x10_initialize()
{
    // initialize X10
    X10RC( x10lib::Init(NULL, 0) );

    // communication setup
	X10RC(x10lib::Register((void *)receive_update, 1));
	X10RC(x10lib::Getenv(NUM_TASKS, &num_tasks));
	X10RC(x10lib::Getenv(TASK_ID, &my_id));
	X10RC(x10lib::Setenv(INTERRUPT_SET, 0));
	X10RC(x10lib::Setenv(ERROR_CHK, 0));

    // get shared lock to prevent other threads from running
	X10RC(x10lib::Lock());
}

void x10_terminate()
{
	X10RC(x10lib::Unlock());
	X10RC(x10lib::Finalize());
}

int main(int argc, char *argv[])
{
    read_arguments(argc, argv);
    x10_initialize();
	hndl = x10lib::GetHandle();
    if (num_tasks & (num_tasks - 1)) {
        printf("num_tasks must be power of 2\n");
        exit(-1);
    }
    if (my_id == 0) {
        printf("%lld dwords  %lld updates  ", table_size, num_updates);
        fflush(stdout);
    }

    u64Int ran = gups_initialize();
	X10RC(x10lib::Gfence());
    double t = microseconds();
    for (s64Int i = 0; i < num_updates; i++) {
        ran = (ran << 1) ^ ((s64Int)ran < 0 ? POLY : 0);
        int dest = (ran >> log_table_size) & (num_tasks - 1);
        if (dest == my_id || embarassing) {
            UPDATE((ran & (table_size-1)), ran);
        } else {
            if (aggregate)
                add_to_bucket(dest, ran);
            else
                send_update(dest, ran);
        }
    }
    if (aggregate)
        flush_buckets();
	X10RC(x10lib::Gfence());
    t = microseconds() - t;
    if (my_id == 0) {
        printf("%f GUPS  %.1f seconds\n", (double)num_updates * num_tasks/t/1000,
                t*1e-6);
        printf("nSends = %ld, searchCount = %ld\n", nSends, searchCount);
    }
#ifdef VERIFY
    u64Int sum = 0;
    for (ran = 0; ran < table_size; ran++)
        sum += table[ran];
    printf("sum = %lld\n", sum);
#endif

    x10_terminate();
    return 0;
}
