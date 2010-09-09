#include <cstdlib>
#include <cstring>
#include <iostream>

#include <stdint.h>


#include <x10rt_front.h>
#include <x10rt_ser.h>
#include <x10rt_net.h>
#include <x10rt_logical.h>

#ifdef X10RT_PANE_HACK
#define TRANSPORT pane
#include <pgasrt.h>
#endif

#ifdef _AIX
#define PAGESIZE_4K  0x1000
#define PAGESIZE_64K 0x10000
#define PAGESIZE_16M 0x1000000
#include <sys/ipc.h>
#include <sys/shm.h>
#include <sys/vminfo.h>
#endif

#define OP_NEW

// {{{ nano_time
#include <sys/time.h>

unsigned long long nano_time() {
    struct ::timeval tv;
    gettimeofday(&tv, NULL);
    return (unsigned long long)(tv.tv_sec * 1000000000ULL + tv.tv_usec * 1000ULL);
} // }}}

const long long POLY    = 7LL;
const long long PERIOD  = 1317624576693539401LL;

// {{{ HPCC_starts
static uint64_t HPCC_starts(long long n) {
    int i, j;
    uint64_t m2[64];
    while (n < 0) n += PERIOD;
    while (n > PERIOD) n -= PERIOD;
    if (n == 0) return 0x1;
    uint64_t temp = 0x1;
    for (i=0; i<64; i++) {
        m2[i] = temp;
        temp = (temp << 1) ^ ((long long) temp < 0 ? POLY : 0);
        temp = (temp << 1) ^ ((long long) temp < 0 ? POLY : 0);
    }
    for (i=62; i>=0; i--) if ((n >> i) & 1) break;
    uint64_t ran = 0x2;
    while (i > 0) {
        temp = 0;
        for (j=0; j<64; j++) if ((ran >> j) & 1) temp ^= m2[j];
        ran = temp;
        i -= 1;
        if ((n >> i) & 1)
            ran = (ran << 1) ^ ((long long) ran < 0 ? POLY : 0);
    }
    return ran;
} // }}}

x10rt_msg_type DIST_ID, MAIN_ID, UPDATE_ID, PONG_ID, VALIDATE_ID, QUIT_ID;

uint64_t *localTable;
uint64_t *globalTable;
uint64_t localTableSize;

long long pongs_outstanding = 0;
bool finished = false;

void decrement (unsigned long place)
{
    if (x10rt_here()==place) {
        pongs_outstanding--;
    } else {
        x10rt_msg_params p2 = {0, PONG_ID, NULL, 0};
        x10rt_send_msg(&p2);
    }
}

// {{{ msg handlers

static void recv_dist (const x10rt_msg_params *p) {
    x10rt_deserbuf b; x10rt_deserbuf_init(&b, p);
    uint32_t src; x10rt_deserbuf_read(&b, &src);
    uint64_t address; x10rt_deserbuf_read(&b, &address);
    globalTable[src] = address;
    x10rt_msg_params p2 = {src, PONG_ID, NULL, 0};
    x10rt_send_msg(&p2);
}

static void do_update (uint64_t index, uint64_t update) {
    localTable[index] ^= update;

    #ifdef OP_EMULATE
    decrement(0);
    #endif
}

static void recv_update (const x10rt_msg_params *p)
{
    x10rt_deserbuf b; x10rt_deserbuf_init(&b, p);
    uint64_t index; x10rt_deserbuf_read(&b, &index);
    uint64_t update; x10rt_deserbuf_read(&b, &update);
    do_update(index, update);
}

static void recv_pong (const x10rt_msg_params *)
{
    pongs_outstanding--;
}

static void do_main (uint64_t logLocalTableSize, uint64_t numUpdates) {
    #ifdef X10RT_PANE_HACK
    __pgasrt_tsp_barrier();
    #endif
    uint64_t mask = (1<<logLocalTableSize)-1;
    uint64_t local_updates = numUpdates / x10rt_nhosts();

    uint64_t ran = HPCC_starts(x10rt_here() * local_updates);

    x10rt_place here = x10rt_here();
    x10rt_place nhosts = x10rt_nhosts();

    // HOT LOOP STARTS
    for (uint64_t i=0 ; i<local_updates ; ++i) {
        ran = (ran << 1) ^ (((int64_t)ran)<0L ? POLY : 0L);
        unsigned long place = ((ran>>logLocalTableSize) & (nhosts-1));
        uint64_t index = ran & mask;
        uint64_t update = ran;
        if (here==place) {
            do_update(index,update);
        } else {
            #ifdef OP_EMULATE
            x10rt_serbuf b; x10rt_serbuf_init(&b, place, UPDATE_ID);
            x10rt_serbuf_write(&b, &index);
            x10rt_serbuf_write(&b, &update);
            x10rt_send_msg(&b.p);
            x10rt_serbuf_free(&b);
            #endif
            #ifdef OP_OLD
            uint64_t remote_addr = globalTable[place];
            x10rt_remote_xor(place, remote_addr + sizeof(uint64_t)*index, update);
            #endif
            #ifdef OP_NEW
            uint64_t remote_addr = globalTable[place];
            remote_addr += sizeof(uint64_t)*(index);
            x10rt_remote_op(place, remote_addr, X10RT_OP_XOR, update);
            #endif
        }
    }
    // HOT LOOP ENDS

    #ifdef X10RT_PANE_HACK
    __pgasrt_tsp_barrier();
    #endif

    #ifndef OP_EMULATE
    #ifdef OP_OLD
    x10rt_remote_op_fence();
    #endif
    decrement(0);
    #endif
}

static void recv_main (const x10rt_msg_params *p) {
    x10rt_deserbuf b; x10rt_deserbuf_init(&b, p);
    uint64_t logLocalTableSize; x10rt_deserbuf_read(&b, &logLocalTableSize);
    uint64_t numUpdates; x10rt_deserbuf_read(&b, &numUpdates);
    do_main(logLocalTableSize, numUpdates);
}

static void do_validate (void) {
    int err = 0;

    for (uint64_t i=0 ; i<localTableSize ; ++i) {
        if (localTable[i] != i) err++;
    }

    std::cout<<"Found "<<err<<" errors."<<std::endl;

    decrement(0);
}

static void recv_validate (const x10rt_msg_params *) {
    do_validate();
}

void recv_quit(const x10rt_msg_params *) { finished = true; }

// }}}


// {{{ show_help
void show_help(std::ostream &out, char* name)
{
    if (x10rt_here()!=0) return;
    out << "Usage: "<<name<<" <args>\n"
        << "-h (--help)         this message\n"
        << "-m (--mem) <n>      amount of memory to use per node (log 2)\n"
        << "-u (--updates) <n>  number of updates per table element\n"
        << "-v (--validate) <n> enables validation (not included in reported time)"
        << std::endl;
} // }}}


// {{{ run_test
void runBenchmark (uint64_t logLocalTableSize,
                   uint64_t numUpdates)
{
    #ifdef OP_EMULATE
    pongs_outstanding=numUpdates;
    #else
    pongs_outstanding=x10rt_nhosts();
    #endif
    for (unsigned long p=1 ; p<x10rt_nhosts() ; ++p) {
        x10rt_serbuf b; x10rt_serbuf_init(&b, p, MAIN_ID);
        x10rt_serbuf_write(&b, &logLocalTableSize);
        x10rt_serbuf_write(&b, &numUpdates);
        x10rt_send_msg(&b.p);
        x10rt_serbuf_free(&b);
    }
    do_main(logLocalTableSize, numUpdates);
    while (pongs_outstanding) {
         x10rt_probe();
    }
} // }}}


// {{{ validate
void validate (void)
{
    pongs_outstanding=x10rt_nhosts();
    for (unsigned long p=1 ; p<x10rt_nhosts() ; ++p) {
        x10rt_msg_params params = {p, VALIDATE_ID, NULL, 0};
        x10rt_send_msg(&params);
    }
    do_validate();
    while (pongs_outstanding) x10rt_probe();
} // }}}


// {{{ main
int main(int argc, char **argv)
{
    x10rt_init(&argc, &argv);
    DIST_ID = x10rt_register_msg_receiver(&recv_dist,NULL,NULL,NULL,NULL);
    MAIN_ID = x10rt_register_msg_receiver(&recv_main,NULL,NULL,NULL,NULL);
    UPDATE_ID = x10rt_register_msg_receiver(&recv_update,NULL,NULL,NULL,NULL);
    PONG_ID = x10rt_register_msg_receiver(&recv_pong,NULL,NULL,NULL,NULL);
    VALIDATE_ID = x10rt_register_msg_receiver(&recv_validate,NULL,NULL,NULL,NULL);
    QUIT_ID = x10rt_register_msg_receiver(&recv_quit,NULL,NULL,NULL,NULL);

    uint64_t logLocalTableSize = 12;
    bool enable_validate = false;
    uint64_t updates = 4;

    for (int i=1 ; i<argc; ++i) {
        if (!strncmp(argv[i], "--help", 100)) {
            show_help(std::cout,argv[0]);
            exit(EXIT_SUCCESS);
        } else if (!strncmp(argv[i], "-h", 100)) {
            show_help(std::cout,argv[0]);
            exit(EXIT_SUCCESS);

        } else if (!strncmp(argv[i], "--mem", 100)) {
            logLocalTableSize = strtoul(argv[++i],NULL,0);
        } else if (!strncmp(argv[i], "-m", 100)) {
            logLocalTableSize = strtoul(argv[++i],NULL,0);

        } else if (!strncmp(argv[i], "--updates", 100)) {
            updates = strtoul(argv[++i],NULL,0);
        } else if (!strncmp(argv[i], "-u", 100)) {
            updates = strtoul(argv[++i],NULL,0);

        } else if (!strncmp(argv[i], "--validate", 100)) {
            enable_validate = true;
        } else if (!strncmp(argv[i], "-v", 100)) {
            enable_validate = true;

        } else {
            std::cerr<< "Didn't understand: \""<<argv[i]<<"\""<<std::endl;
            show_help(std::cerr,argv[0]);
            exit(EXIT_FAILURE);
        }
    }

    localTableSize = 1 << logLocalTableSize;
    uint64_t tableSize = localTableSize * x10rt_nhosts();
    uint64_t numUpdates = updates * tableSize;

    #ifdef _AIX
    int shm_id=shmget(IPC_PRIVATE, localTableSize*sizeof(uint64_t), (IPC_CREAT|IPC_EXCL|0600));
    if (shm_id == -1) {
        std::cerr << "shmget failure" << std::endl;
        abort();
    }
    struct shmid_ds shm_buf = { 0 };
    shm_buf.shm_pagesize = PAGESIZE_16M;
    if (shmctl(shm_id, SHM_PAGESIZE, &shm_buf) != 0) {
        std::cerr << "Could not get 16M pages" << std::endl;
        abort();
    }
    localTable = (uint64_t*) shmat(shm_id,0,0); // map memory
    shmctl(shm_id, IPC_RMID, NULL); // no idea what this does

    // pagesizes OK?
    for (char *p = (char*)localTable; p < ((char*)localTable) + localTableSize*sizeof(uint64_t) ;) {
	struct vm_page_info pginfo;
	pginfo.addr = (uint64_t) (size_t) p;
	vmgetinfo(&pginfo,VM_PAGE_INFO,sizeof(struct vm_page_info));
	if (pginfo.pagesize < PAGESIZE_64K) {
	    fprintf(stderr, "x10rt_gups error: Found a page smaller than 64K at %p of %p\n", p, localTable);
	    abort();
	}
	if (pginfo.pagesize < PAGESIZE_16M) {
	    fprintf(stderr, "x10rt_gups error: Found a page smaller than 16M at %p of %p (not checking for any more)\n", p, localTable);
	    break;
	}
        p += pginfo.pagesize;
    }

    #else
    localTable = (uint64_t*) malloc(localTableSize*sizeof(uint64_t));
    #endif



    if (localTable == NULL) {
        std::cerr<<"Could not allocate memory for local table ("
                 << localTableSize*sizeof(uint64_t) << " bytes)" << std::endl;
        abort();
    }
    for (unsigned int i=0 ; i<localTableSize ; ++i) localTable[i] = i;

    globalTable = (uint64_t*) malloc(x10rt_nhosts() * sizeof(*globalTable));
    if (globalTable == NULL) {
        std::cerr<<"Could not allocate memory for global table ("
                 << x10rt_nhosts() * sizeof(*globalTable) << " bytes)" << std::endl;
        abort();
    }

    x10rt_registration_complete();

    // make sure everyone knows the address of everyone else's rail
    pongs_outstanding = x10rt_nhosts();
    uint64_t intptr = x10rt_register_mem(localTable, localTableSize*sizeof(uint64_t));
    uint32_t src = x10rt_here();
    for (unsigned long i=0 ; i<x10rt_nhosts() ; ++i) {
	intptr = (size_t) localTable;
        if (i==x10rt_here()) {
            globalTable[i] = intptr;
            pongs_outstanding--;
        } else {
            x10rt_serbuf b; x10rt_serbuf_init(&b, i, DIST_ID);
            x10rt_serbuf_write(&b, &src);
            x10rt_serbuf_write(&b, &intptr);
            x10rt_send_msg(&b.p);
            x10rt_serbuf_free(&b);
        }
    }
    while (pongs_outstanding) {
        x10rt_probe();
    }

    int finished = 0;
    x10rt_barrier(0, x10rt_here(), x10rt_one_setter, &finished);
    while (!finished) x10rt_probe();

/*
    std::cerr << "globalTable = { ";
    for (unsigned long i=0 ; i<x10rt_nhosts() ; ++i) {
        std::cerr << (i==0?"":", ") << globalTable[i];
    }
    std::cerr << " }\n" << std::endl;
*/

    if (x10rt_here()==0) {
        std::cout<<"Main table size:   2^"<<logLocalTableSize<<"*"<<x10rt_nhosts()
                                          <<" == "<<tableSize<<" words"<<std::endl;;
        std::cout<<"Number of places:  "<<x10rt_nhosts()<<std::endl;
        std::cout<<"Number of updates: "<<numUpdates<<std::endl;

        uint64_t nanos = -nano_time();
        runBenchmark(logLocalTableSize, numUpdates);
        nanos += nano_time();

        // print statistics
        double cpuTime = nanos / 1E9;
        double GUPs = numUpdates / cpuTime;
        std::cout << "CPU time used: "<<cpuTime<<" seconds" << std::endl;
        std::cout << GUPs/1E9<<" Billion(10^9) Updates per second (GUP/s)" << std::endl;

        if (enable_validate) {
            runBenchmark(logLocalTableSize, numUpdates);
            validate();
        }

        for (unsigned long i=1 ; i<x10rt_nhosts() ; ++i) {
            x10rt_msg_params p = {i, QUIT_ID, NULL, 0};
            x10rt_send_msg(&p);
        }
        finished = true;
    }

    while (!finished) x10rt_probe();

    x10rt_finalize();

    return EXIT_SUCCESS;
} // }}}

// vim: shiftwidth=4:tabstop=4:expandtab

