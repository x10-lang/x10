#include <cstdlib>
#include <cstring>
#include <iostream>
#include <cstdio>

#include <stdint.h>


#include <x10rt_front.h>
#include <x10rt_ser.h>

#ifdef _AIX
#define PAGESIZE_4K  0x1000
#define PAGESIZE_64K 0x10000
#define PAGESIZE_16M 0x1000000
#include <sys/ipc.h>
#include <sys/shm.h>
#include <sys/vminfo.h>
#endif

// what follows is some stuff copied from x10.runtime/src-cpp/x10aux/alloc.cc

#ifdef _AIX
#include <sys/vminfo.h>
#endif
#include <sys/ipc.h>
#include <sys/shm.h>
#include <unistd.h>
#include <sys/mman.h>

// darwin doesn't have MAP_ANONYMOUS but it does have MAP_ANON which does the same thing
#ifdef __APPLE__
#ifndef MAP_ANONYMOUS
#define MAP_ANONYMOUS MAP_ANON
#endif
#endif

#ifdef __linux__
// partial reimplemntation of glibc's getline
static ssize_t mygetline (char **lineptr, size_t *sz, FILE *f)
{
    *sz = 0;
    char tmp[10];
    size_t bytesread;
    do {
        if (tmp!=fgets(tmp, sizeof(tmp), f)) return -1;
        bytesread = strlen(tmp);
        *lineptr = static_cast<char*>(::realloc(*lineptr, *sz+bytesread));
        strncpy(*lineptr+*sz, tmp, bytesread);
        *sz += bytesread;
    } while (tmp[bytesread-1] != '\n');

    *lineptr = static_cast<char*>(::realloc(*lineptr, *sz+1));
    (*lineptr)[*sz] = '\0';
    *sz += 1;
    return *sz;
}
#endif

#if !defined(SHM_R) || !defined(SHM_W)
#include <sys/stat.h>
#undef SHM_R
#define SHM_R S_IRUSR
#undef SHM_W
#define SHM_W S_IWUSR
#endif

static void x10rt_aborting_probe (void)
{
    x10rt_error err = x10rt_probe();
    if (err != X10RT_ERR_OK) {
        if (x10rt_error_msg() != NULL)
            std::cerr << "X10RT fatal error: " << x10rt_error_msg() << std::endl;
        abort();
    }
}


void *congruent_alloc (size_t size)
{

    long page = 0;
    if (getenv("X10_CONGRUENT_HUGE")!=NULL) {

        #if defined(__linux__)

        // on linux, the huge page size must be read from /proc
        FILE *f = fopen("/proc/meminfo","r");
        if (f==NULL) perror("fopening /proc/meminfo");
        bool eof = false;
        while (!eof) {
            char *lineptr = NULL;
            size_t sz;
            ssize_t r = mygetline(&lineptr,&sz,f);
            eof = r == -1;
            if (!eof) {
                char *saveptr;
                const char *key_c = strtok_r(lineptr,":",&saveptr);
                if (key_c == NULL) { fprintf(stderr, "Formatting error in /proc/meminfo!\n"); abort(); }
                if (strcmp(key_c,"Hugepagesize") == 0) {
                    const char *val_c   = strtok_r(NULL,"k",&saveptr);
                    if (val_c == NULL) { fprintf(stderr, "Formatting error in /proc/meminfo!!\n"); abort(); }
                    size_t val = strtoull(val_c,NULL,10);
                    page = val * 1024;
                    break;
                }
            }
            ::free(lineptr);
        }
        fclose(f);

        #elif defined(__aix)

        page = 16 * 1024 * 1024; // 16MB

        #else

        fprintf(stderr, "Using huge pages for congruent memory is not supported on your platform.  Please unset X10_CONGRUENT_HUGE.\n");
        abort();

        #endif

    } else {
        // do not use PAGE_SIZE as the compile-time value may not reflect the machine the code runs on
        page = sysconf(_SC_PAGESIZE);
    }

    // round it up to the nearest page
    size = ((size+page-1) / page) * page;

    void *obj;

    if (getenv("X10_CONGRUENT_HUGE")!=NULL) {

        // huge pages are useful for performance, e.g. due to reducing TLB misses
        // they are non-standard, currently aix and linux are supported.

        // we are assuming that huge pages (being very special) are going to always occupy the same virtual address space

        #if defined(__linux__) && !defined(SHM_HUGETLB)
            fprintf(stderr, "Using huge pages for congruent memory is only supported on Linux >= 2.6.  Please unset X10_CONGRUENT_HUGE.\n");
            abort();
        #elif defined(_AIX) && !defined(SHM_PAGESIZE)
            fprintf(stderr, "This AIX system appears not to have SHM_PAGESIZE.  Please unset X10_CONGRUENT_HUGE.\n");
            abort();
        #else
            // ok let's go
            int shmflag = 0;
            shmflag |= SHM_R | SHM_W; // permissions
            //shmflag |= IPC_CREAT|IPC_EXCL; // make a new allocation, ensure it is unique
            shmflag |= IPC_CREAT; // make a new allocation
            #if defined(__linux__)
            shmflag |= SHM_HUGETLB; // huge pages, please
            #endif
            //fprintf(stderr,"size = %d\n",(int)size);
            int shm_id=shmget(IPC_PRIVATE, size, shmflag);
            if (shm_id == -1) {
                perror("congruent shmget");
                abort();
            }

            #ifdef __aix
            // on AIX we ask for pages of a particular size
            struct shmid_ds shm_buf = { 0 };
            shm_buf.shm_pagesize = page;
            if (shmctl(shm_id, SHM_PAGESIZE, &shm_buf) != 0) {
                fprintf(stderr, "Could not get 16M pages\n");
                abort();
            }
            #endif

            obj = shmat(shm_id,0,0);  // 'attach' the shared memory at any arbitrary address (seemingly, this is always the same)
            shmctl(shm_id, IPC_RMID, NULL); // mark for destruction, will be deallocated when shmdt is called (for x10, never)
        #endif


    } else {


        // we're not using huge pages, however we still need an address that is consistent across all places
        // so we use mmap with a fixed address

        #if !defined(_AIX) && !defined(__linux__) && !defined(__APPLE__)

            // in particular, cygwin can fall in this trap
            // other platforms have yet to be investigated for possible support

            if (x10rt_nplaces() == 1) {
                // Because there is only a single place, we can just fall back to malloc
                // Don't call x10rt_register_mem because on most transports, it is
                // unimplemented and unhelpfully returns 0 to indicate that.
                obj = malloc(size);
            } else {
                // In a multi-place run, we have to return the same virtual address in all
                // places or the program won't work.  Getting here indicates that we can't
                // do that, so we must abort the program.
                fprintf(stderr,"alloc_internal_congruent not supported in multi-place executions on this platform\n");
                fprintf(stderr,"aborting execution\n");
                abort();
            }

        #else

            char *base_addr_ = getenv("X10_CONGRUENT_BASE");
            // Default addresses based on some experimentation on 32 bit and 64 bit platforms.  Not very reliable.
            // Test different addresses by overriding with X10_CONGRUENT_BASE environment variable (takes decimal and hex)
            #ifdef _ARCH_PPC
            size_t default_base_addr = sizeof(void*)==4 ? 0x70000000LL : 0x10000000000LL;
            #else
            size_t default_base_addr = sizeof(void*)==4 ? 0x70000000LL : 0x100000000000LL;
            #endif
            size_t base_addr = base_addr_!=NULL ? strtoull(base_addr_,NULL,0) : default_base_addr;

            if (base_addr % page) {
                fprintf(stderr, "X10_CONGRUENT_BASE=0x%llx is not a multiple of the page size (0x%llx)\n",
                        (unsigned long long)base_addr, (unsigned long long)page);
                abort();
            }


            #ifdef __linux__
            // check whether or not there are existing pages mapped, if there are, mmap will clobber them
            // so we must detect and abort
            FILE *f = fopen("/proc/self/maps","r");
            if (f==NULL) perror("fopening /proc/self/maps");
            bool eof = false;
            while (!eof) {
                char *lineptr = NULL;
                size_t sz;
                ssize_t r = mygetline(&lineptr,&sz,f);
                eof = r == -1;
                if (!eof) {
                    char *saveptr;
                    const char *from_c = strtok_r(lineptr,"-",&saveptr);
                    if (from_c == NULL) { fprintf(stderr, "Formatting error in /proc/self/maps!\n"); abort(); }
                    size_t from = strtoull(from_c,NULL,16);
                    const char *to_c   = strtok_r(NULL," ",&saveptr);
                    if (to_c == NULL) { fprintf(stderr, "Formatting error in /proc/self/maps!!!\n"); abort(); }
                    size_t to = strtoull(to_c,NULL,16);
                    bool completely_before = base_addr+size <= from;
                    bool completely_after = base_addr >= to;
                    if (!(completely_before||completely_after)) {
                        fprintf(stderr, "Cannot map congruent memory at the address specified.\n");
                        fprintf(stderr, "Tried to map %llx-%llx but there was an existing map %llx-%llx.\n",
                                        (unsigned long long)base_addr, (unsigned long long)base_addr+size, (unsigned long long)from, (unsigned long long)to);
                        fprintf(stderr, "Please specify alternative address range with environment variable X10_CONGRUENT_BASE.\n");
                        abort();
                    }
                }
                ::free(lineptr);
            }
            fclose(f);
            #else // __APPLE__
            // apparently MAP_FIXED will fail on macosx if there is an existing mapping in the way
            #endif

            obj = ::mmap((void*)base_addr, size, PROT_READ | PROT_WRITE, MAP_PRIVATE | MAP_ANONYMOUS | MAP_FIXED, -1, 0);
            if (obj==MAP_FAILED) { perror("Congruent memory mmap"); abort(); }

        #endif
    }

    return obj;

}

x10rt_remote_op_params *opv;
size_t opc;
size_t remote_op_batch;


inline void remote_op (x10rt_place place, x10rt_remote_ptr remote_addr,
                       x10rt_op_type type, unsigned long long value)
{
    opv[opc].dest = place;
    opv[opc].dest_buf = remote_addr;
    opv[opc].op = type;
    opv[opc].value = value;
    opc++;
    if (opc == remote_op_batch) {
        x10rt_remote_ops(opv,opc);
        opc = 0;
    }
}


static inline uint64_t initial_value (uint64_t i) {
    return 10000 / (i+1);
}

static inline uint64_t operand_value (uint64_t i) {
    return 2*i + 1;
}


// {{{ nano_time
#include <sys/time.h>

unsigned long long nano_time() {
    struct ::timeval tv;
    gettimeofday(&tv, NULL);
    return (unsigned long long)(tv.tv_sec * 1000000000ULL + tv.tv_usec * 1000ULL);
} // }}}

const long long POLY    = 7LL;
const long long PERIOD  = 1317624576693539401LL;

x10rt_msg_type MAIN_ID, PONG_ID, VALIDATE_ID, QUIT_ID;

uint64_t *localTable;
uint64_t localTableSize;

long long pongs_outstanding = 0;
bool finished = false;

void decrement (unsigned long place)
{
    if (x10rt_here()==place) {
        pongs_outstanding--;
    } else {
        x10rt_msg_params p2 = {0, PONG_ID, NULL, 0, 0};
        x10rt_send_msg(&p2);
    }
}

// {{{ msg handlers

static void recv_pong (const x10rt_msg_params *)
{
    pongs_outstanding--;
}

static void do_main () {
    x10rt_place there = x10rt_nhosts()==1 ? 0 : (x10rt_here() + 1) % x10rt_nhosts();

    // HOT LOOP STARTS
    for (uint64_t i=0 ; i<localTableSize ; ++i) {
            remote_op(there, (x10rt_remote_ptr)&localTable[i], X10RT_OP_XOR, operand_value(i));
    }
    // HOT LOOP ENDS

    decrement(0);
}

static void recv_main (const x10rt_msg_params *p) {
    x10rt_deserbuf b; x10rt_deserbuf_init(&b, p);
    do_main();
}

static void do_validate (void) {
    int err = 0;

    for (uint64_t i=0 ; i<localTableSize ; ++i) {
        if (localTable[i] != (initial_value(i) ^ operand_value(i))) err++;
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
        << "-v (--validate) <n> enables validation (not included in reported time)"
        << std::endl;
} // }}}


// {{{ run_test
void runBenchmark (void)
{
    pongs_outstanding=x10rt_nhosts();
    for (unsigned long p=1 ; p<x10rt_nhosts() ; ++p) {
        x10rt_serbuf b; x10rt_serbuf_init(&b, p, MAIN_ID);
        x10rt_send_msg(&b.p);
        x10rt_serbuf_free(&b);
    }
    do_main();
    while (pongs_outstanding) {
         x10rt_aborting_probe();
    }
} // }}}


// {{{ validate
void validate (void)
{
    pongs_outstanding=x10rt_nhosts();
    for (unsigned long p=1 ; p<x10rt_nhosts() ; ++p) {
        x10rt_msg_params params = {p, VALIDATE_ID, NULL, 0, 0};
        x10rt_send_msg(&params);
    }
    do_validate();
    while (pongs_outstanding) x10rt_aborting_probe();
} // }}}


// {{{ main
int main(int argc, char **argv)
{
    x10rt_error init_err = x10rt_init(&argc, &argv);
    if (init_err != X10RT_ERR_OK) {
        if (x10rt_error_msg() != NULL)
            std::cerr << "X10RT fatal initialization error:  " << x10rt_error_msg() << std::endl;
        abort();
    }
    MAIN_ID = x10rt_register_msg_receiver(&recv_main,NULL,NULL,NULL,NULL);
    PONG_ID = x10rt_register_msg_receiver(&recv_pong,NULL,NULL,NULL,NULL);
    VALIDATE_ID = x10rt_register_msg_receiver(&recv_validate,NULL,NULL,NULL,NULL);
    QUIT_ID = x10rt_register_msg_receiver(&recv_quit,NULL,NULL,NULL,NULL);

    uint64_t logLocalTableSize = 12;
    bool enable_validate = false;

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

    localTable = (uint64_t*) congruent_alloc(localTableSize*sizeof(uint64_t));
    x10rt_register_mem(localTable, localTableSize*sizeof(uint64_t));

    const char *remote_op_batch_ = getenv("X10_REMOTE_OP_BATCH");
    remote_op_batch = remote_op_batch_ == NULL ? 64 : strtoul(remote_op_batch_, NULL, 10);
    opv = (x10rt_remote_op_params*)malloc(remote_op_batch * sizeof(*opv));

    if (localTable == NULL) {
        std::cerr<<"Could not allocate memory for local table ("
                 << localTableSize*sizeof(uint64_t) << " bytes)" << std::endl;
        abort();
    }
    for (unsigned int i=0 ; i<localTableSize ; ++i) localTable[i] = initial_value(i);

    x10rt_registration_complete();

    if (x10rt_here()==0) {
        std::cout<<"Main table size:         2^"<<logLocalTableSize<<"*"<<x10rt_nhosts()
                                                 <<" == "<<tableSize<<" words ("<<(tableSize*8)/1024/1024<<" MB)"<<std::endl;
        std::cout<<"Per-process table size:  2^"<<logLocalTableSize
                                                 <<" == "<<localTableSize<<" words ("<<(localTableSize*8)/1024/1024<<" MB)"<<std::endl;
        std::cout<<"Number of places:        "<<x10rt_nhosts()<<std::endl;

        uint64_t nanos = -nano_time();
        runBenchmark();
        nanos += nano_time();

        // print statistics
        double cpuTime = nanos / 1E9;
        double GUPs = tableSize / cpuTime;
        std::cout << "CPU time used: "<<cpuTime<<" seconds" << std::endl;
        std::cout << GUPs/1E9<<" Billion(10^9) Updates per second (GUP/s)" << std::endl;

        if (enable_validate) {
            validate();
        }

        for (unsigned long i=1 ; i<x10rt_nhosts() ; ++i) {
            x10rt_msg_params p = {i, QUIT_ID, NULL, 0, 0};
            x10rt_send_msg(&p);
        }
        finished = true;
    }

    while (!finished) x10rt_aborting_probe();

    x10rt_finalize();

    return EXIT_SUCCESS;
} // }}}

// vim: shiftwidth=4:tabstop=4:expandtab

