#include <cstdlib>
#include <cstring>
#include <iostream>
#include <stdint.h>

#include <x10rt_api.h>

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
static unsigned long long HPCC_starts(long long n) {
    int i, j;
    unsigned long long m2[64];
    while (n < 0) n += PERIOD;
    while (n > PERIOD) n -= PERIOD;
    if (n == 0) return 0x1;
    unsigned long long temp = 0x1;
    for (i=0; i<64; i++) {
        m2[i] = temp;
        temp = (temp << 1) ^ ((long long) temp < 0 ? POLY : 0);
        temp = (temp << 1) ^ ((long long) temp < 0 ? POLY : 0);
    }
    for (i=62; i>=0; i--) if ((n >> i) & 1) break;
    unsigned long long ran = 0x2;
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

enum id {
    UNUSED, // pgas currently broken for id==0
    DIST_ID,
    MAIN_ID,
    UPDATE_ID,
    PONG_ID,
    VALIDATE_ID,
    QUIT_ID
};

unsigned long long *localTable;
unsigned long long *globalTable;
unsigned long long localTableSize;

long long pongs_outstanding = 0;
bool finished = false;

void decrement (unsigned long place)
{
    if (x10rt_here()==place) {
        pongs_outstanding--;
    } else {
        x10rt_msg_params p2 = {0, PONG_ID, NULL, 0};
        x10rt_send_msg(p2);
    }
}

// {{{ msg handlers

static void recv_dist (const x10rt_msg_params &p) {
    uint32_t src;
    uint64_t address;
    unsigned char *buf = (unsigned char*) p.msg;
    memcpy(&src, buf+0, 4);
    memcpy(&address, buf+8, 8);
    globalTable[src] = address;
    x10rt_msg_params p2 = {src, PONG_ID, NULL, 0};
    x10rt_send_msg(p2);
}

static void do_update (unsigned long long index, unsigned long long update) {
    localTable[index] ^= update;

    #if defined(NO_REMOTE_XOR) && !defined(NO_FINISH)
    decrement(0);
    #endif
}

static void recv_update (const x10rt_msg_params &p)
{
    unsigned long long index;
    unsigned long long update;

    char *buf = (char*) p.msg;
    memcpy(&index, buf+0, 8);
    memcpy(&update, buf+8, 8);

    do_update(index, update);
}

static void recv_pong (const x10rt_msg_params &p)
{
    pongs_outstanding--;
}

static void do_main (unsigned long long logLocalTableSize, unsigned long long numUpdates) {
    unsigned long long mask = (1<<logLocalTableSize)-1;
    unsigned long long local_updates = numUpdates / x10rt_nplaces();

    unsigned long long ran = HPCC_starts(x10rt_here() * local_updates);

    // HOT LOOP STARTS
    for (unsigned long long i=0 ; i<local_updates ; ++i) {
        unsigned long place = ((ran>>logLocalTableSize) & (x10rt_nplaces()-1));
        unsigned long long index = ran & mask;
        unsigned long long update = ran;
        if (x10rt_here()==place) {
            do_update(index,update);
        } else {
            #ifdef NO_REMOTE_XOR
            char *buf2 = (char*)x10rt_msg_realloc(NULL,0, 16);
            memcpy(buf2+0, &index, 8);
            memcpy(buf2+8, &update, 8);
            x10rt_msg_params params = {place, UPDATE_ID, buf2, 16};
            x10rt_send_msg(params);
            #else
            unsigned long long remote_addr = globalTable[place];
            x10rt_remote_xor(place, remote_addr + sizeof(long long)*index, update);
            #endif
        }

        ran = (ran << 1) ^ (((long long)ran)<0L ? POLY : 0L);
    }
    // HOT LOOP ENDS

    #ifndef NO_REMOTE_XOR
    x10rt_remote_op_fence();
    decrement(0);
    #endif
}

static void recv_main (const x10rt_msg_params &p) {
    unsigned long long logLocalTableSize;
    unsigned long long numUpdates;
    unsigned char *buf = (unsigned char*) p.msg;
    memcpy(&logLocalTableSize, buf+0, 8);
    memcpy(&numUpdates, buf+8, 8);

    do_main(logLocalTableSize, numUpdates);
}

static void do_validate (void) {
    int err = 0;

    for (unsigned long long i=0 ; i<localTableSize ; ++i) {
        if (localTable[i] != i) err++;
    }

    std::cout<<"Found "<<err<<" errors."<<std::endl;

    decrement(0);
}

static void recv_validate (const x10rt_msg_params &p) {
    do_validate();
}

void recv_quit(const x10rt_msg_params &) { finished = true; }

// }}}


// {{{ show_help
void show_help(FILE *out, char* name)
{
    if (x10rt_here()!=0) return;
    fprintf(out,"Usage: %s <args>\n", name);
    fprintf(out,"-h (--help)        ");
    fprintf(out,"this message\n");
    fprintf(out,"-m (--mem) <n>      ");
    fprintf(out,"amount of memory to use per node (log 2)\n");
    fprintf(out,"-v (--validate) <n>      ");
    fprintf(out,"enables validation (not included in reported time)\n");
} // }}}


// {{{ run_test
void runBenchmark (unsigned long long logLocalTableSize,
                   unsigned long long numUpdates)
{
    #ifdef NO_REMOTE_XOR
    pongs_outstanding=numUpdates;
    #else
    pongs_outstanding=x10rt_nplaces();
    #endif
    for (unsigned long p=1 ; p<x10rt_nplaces() ; ++p) {
        unsigned char *buf = (unsigned char*)x10rt_msg_realloc(NULL,0, 16);
        memcpy(buf+0, &logLocalTableSize, 8);
        memcpy(buf+8, &numUpdates, 8);
        x10rt_msg_params params = {p, MAIN_ID, buf, 16};
        x10rt_send_msg(params);
    }
    do_main(logLocalTableSize, numUpdates);
    #if !defined(NO_FINISH) || !defined(NO_REMOTE_XOR)
    while (pongs_outstanding) {
         x10rt_probe();
    }
    #endif
} // }}}


// {{{ validate
void validate (void)
{
    pongs_outstanding=x10rt_nplaces();
    for (unsigned long p=1 ; p<x10rt_nplaces() ; ++p) {
        x10rt_msg_params params = {p, VALIDATE_ID, NULL, 0};
        x10rt_send_msg(params);
    }
    do_validate();
    while (pongs_outstanding) x10rt_probe();
} // }}}

// {{{ main
int main(int argc, char **argv)
{
    x10rt_init(argc, argv);
    x10rt_register_msg_receiver(DIST_ID, &recv_dist);
    x10rt_register_msg_receiver(MAIN_ID, &recv_main);
    x10rt_register_msg_receiver(UPDATE_ID, &recv_update);
    x10rt_register_msg_receiver(PONG_ID, &recv_pong);
    x10rt_register_msg_receiver(VALIDATE_ID, &recv_validate);
    x10rt_register_msg_receiver(QUIT_ID, &recv_quit);

    unsigned long long logLocalTableSize = 12;
    bool enable_validate = false;

    for (int i=1 ; i<argc; ++i) {
        if (!strcmp(argv[i], "--help")) {
            show_help(stdout,argv[0]);
            exit(EXIT_SUCCESS);
        } else if (!strcmp(argv[i], "-h")) {
            show_help(stdout,argv[0]);
            exit(EXIT_SUCCESS);

        } else if (!strcmp(argv[i], "--mem")) {
            logLocalTableSize = strtoul(argv[++i],NULL,0);
        } else if (!strcmp(argv[i], "-m")) {
            logLocalTableSize = strtoul(argv[++i],NULL,0);

        } else if (!strcmp(argv[i], "--validate")) {
            enable_validate = true;
        } else if (!strcmp(argv[i], "-v")) {
            enable_validate = true;

        } else {
            fprintf(stderr,"Didn't understand: \"%s\"\n", argv[i]);
            show_help(stderr,argv[0]);
            exit(EXIT_FAILURE);
        }
    }

    localTableSize = 1 << logLocalTableSize;
    unsigned long long tableSize = localTableSize * x10rt_nplaces();
    unsigned long long numUpdates = 4 * tableSize;

    localTable = (unsigned long long*) malloc(localTableSize*sizeof(unsigned long long));
    for (unsigned int i=0 ; i<localTableSize ; ++i) localTable[i] = i;

    globalTable = (unsigned long long*) malloc(x10rt_nplaces() * sizeof(*globalTable));

    x10rt_registration_complete();

    // make sure everyone knows the address of everyone else's rail
    pongs_outstanding = x10rt_nplaces();
    for (unsigned long i=0 ; i<x10rt_nplaces() ; ++i) {
        uint64_t intptr = (uint64_t) localTable;
        if (i==x10rt_here()) {
            globalTable[i] = intptr;
            pongs_outstanding--;
        } else {
            char *buf = (char*)x10rt_msg_realloc(NULL,0,12);
            uint32_t src = x10rt_here();
            memcpy(buf+0, &src, 4);
            memcpy(buf+8, &intptr, 8);
            x10rt_msg_params p = {i, DIST_ID, buf, 12};
            x10rt_send_msg(p);
        }
    }
    while (pongs_outstanding) {
        x10rt_probe();
    }

    if (x10rt_here()==0) {
        std::cout<<"Main table size:   2^"<<logLocalTableSize<<"*"<<x10rt_nplaces()
                                          <<" == "<<tableSize<<" words"<<std::endl;;
        std::cout<<"Number of places:  "<<x10rt_nplaces()<<std::endl;
        std::cout<<"Number of updates: "<<numUpdates<<std::endl;

        unsigned long long nanos = -nano_time();
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

        for (unsigned long i=1 ; i<x10rt_nplaces() ; ++i) {
            x10rt_msg_params p = {i, QUIT_ID, NULL, 0};
            x10rt_send_msg(p);
        }
        finished = true;
    }

    while (!finished) x10rt_probe();

    x10rt_finalize();

    return EXIT_SUCCESS;
} // }}}

// vim: shiftwidth=4:tabstop=4:expandtab

