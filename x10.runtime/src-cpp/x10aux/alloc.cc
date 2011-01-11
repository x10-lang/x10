/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

#include <x10aux/config.h>

#include <x10aux/alloc.h>

#include <cstdio>
#include <cstdarg>

#include <x10aux/throw.h>
#include <x10/lang/OutOfMemoryError.h>

#ifdef _AIX
#define PAGESIZE_4K  0x1000
#define PAGESIZE_64K 0x10000
#define PAGESIZE_16M 0x1000000
#include <sys/ipc.h>
#include <sys/shm.h>
#include <sys/vminfo.h>
#else
#include <unistd.h>
#include <sys/mman.h>
#endif


using namespace x10aux;

#ifdef __CYGWIN__
extern "C" int vsnprintf(char *, size_t, const char *, va_list); 
#endif

void x10aux::reportOOM(size_t size) {
    _M_("Out of memory allocating " << size << " bytes");
#ifndef NO_EXCEPTIONS
    throwException<x10::lang::OutOfMemoryError>();
#else
    fprintf(stderr,"Out of memory\n");
    abort();
#endif
}

char *x10aux::alloc_printf(const char *fmt, ...) {
    va_list args;
    char try_buf[1];
    va_start(args, fmt);
    std::size_t sz = vsnprintf(try_buf, 0, fmt, args);
    va_end(args);
    char *r = x10aux::alloc<char>(sz+1, false);
    va_start(args, fmt);
    std::size_t s1 = vsnprintf(r, sz+1, fmt, args);
    (void) s1;
    assert (s1 == sz);
    va_end(args);
    return r;
}

char *x10aux::realloc_printf(char *buf, const char *fmt, ...) {
    std::size_t original_sz = strlen(buf);
    va_list args;
    char try_buf[1];
    va_start(args, fmt);
    std::size_t sz = original_sz + vsnprintf(try_buf, 0, fmt, args);
    va_end(args);
    char *r = x10aux::realloc(buf, sz+1);
    // append the new stuff onto the original stuff
    va_start(args, fmt);
    std::size_t s1 = vsnprintf(&r[original_sz], sz+1, fmt, args);
    (void) s1;
    assert (s1 == sz);
    va_end(args);
    return r;
}

#ifdef X10_USE_BDWGC
bool x10aux::gc_init_done;
#endif        

void *x10aux::realloc_internal (void *src, size_t dsz) {
    void *ret;
#ifdef X10_USE_BDWGC
    ret = GC_REALLOC(src, dsz);
#else
    ret = ::realloc(src, dsz);
#endif
    if (ret==NULL && dsz>0) {
        reportOOM(dsz);
    }
    return ret;
}

void x10aux::dealloc_internal (const void *obj_) {
    if (!x10aux::disable_dealloc_) {
        void *obj = const_cast<void*>(obj_); // free does not take const void *
#ifdef X10_USE_BDWGC
        GC_FREE(obj);
#else
        ::free(obj);
#endif        
    }
}

size_t x10aux::heap_size() {
#ifdef X10_USE_BDWGC
    return GC_get_heap_size();
#else
    // TODO: an actual useful implementation of this function when we aren't using GC.
    return (size_t)(-1);
#endif
}


#define ENV_CONGRUENT_BASE "X10_CONGRUENT_BASE"
#define ENV_CONGRUENT_SIZE "X10_CONGRUENT_SIZE"
namespace {
    bool have_init_congruent = false;
    unsigned char *congruent_base;
    unsigned char *congruent_cursor;
    size_t congruent_sz;
}
        
static void ensure_init_congruent (size_t req_size) {

    if (have_init_congruent) return;
    have_init_congruent = true;

    char *size_ = getenv(ENV_CONGRUENT_SIZE);
    size_t size = size_!=NULL ? strtoull(size_,NULL,0) : 0;

    // if it's the first allocation, may as well make it big enough -- further allocations will fail
    if (size < req_size) size = req_size;

    congruent_sz = size;

    if (size==0) {
        congruent_base = NULL;
        congruent_cursor = NULL;
        return;
    }

    // otherwise, we have some very system-specific work to do...
    void *obj;

#ifdef _AIX
    int shm_id=shmget(IPC_PRIVATE, size, (IPC_CREAT|IPC_EXCL|0600));
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
    obj = shmat(shm_id,0,0);  // 'attach' the shared memory at any arbitrary address (seemingly, this is always the same)
    shmctl(shm_id, IPC_RMID, NULL); // mark for destruction, will be deallocated when shmdt is called (for x10, never)

    #if 0
    // pagesizes OK? (validated at MS9B, comment back in to check)
    for (char *p = (char*)obj; p < ((char*)obj) + size;) {
        struct vm_page_info pginfo;
        pginfo.addr = (uint64_t) (size_t) p;
        vmgetinfo(&pginfo,VM_PAGE_INFO,sizeof(struct vm_page_info));
        if (pginfo.pagesize < PAGESIZE_64K) {
            fprintf(stderr, "alloc_internal_congruent: Found a page smaller than 64K at %p of %p\n", p, obj);
            abort();
        }
        if (pginfo.pagesize < PAGESIZE_16M) {
            fprintf(stderr, "alloc_internal_congruent: Found a page smaller than 16M at %p of %p (not checking for any more)\n", p, obj);
            break;
        }
        p += pginfo.pagesize;
    }
    #endif

#elif defined(__linux__) || defined(__APPLE__)

// darwin doesn't have MAP_ANONYMOUS but it does have MAP_ANON which does the same thing
#ifdef __APPLE__
#define MAP_ANONYMOUS MAP_ANON
#endif

    char *base_addr_ = getenv(ENV_CONGRUENT_SIZE);
    // Default addresses based on some experimentation on 32 bit and 64 bit platforms.  Not very reliable.
    size_t default_base_addr = sizeof(void*)==4 ? 0x40000000LL : 0x100000000000LL;
    size_t base_addr = base_addr_!=NULL ? strtoull(base_addr_,NULL,0) : default_base_addr;

    // do not use PAGE_SIZE as the compile-time value may not reflect the machine the code runs on
    long page = sysconf(_SC_PAGESIZE); 
    if (base_addr % page) {
        fprintf(stderr, ENV_CONGRUENT_BASE" (%llx) is not a multiple of PAGE_SIZE (%llx)\n", (unsigned long long)base_addr, (unsigned long long)page);
        abort();
    }


    // check whether or not there are existing pages mapped, if there are, mmap will clobber them
    // so we must detect and abort
    #ifdef __linux__
    FILE *f = fopen("/proc/self/maps","r");
    if (f==NULL) perror("fopening /proc/self/maps");
    bool eof = false;
    while (!eof) {
        char *lineptr = NULL;
        size_t sz;
        ssize_t r = getline(&lineptr,&sz,f);
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
                fprintf(stderr, "Please specify alternative address range with environment variables "ENV_CONGRUENT_BASE" and "ENV_CONGRUENT_SIZE".\n");
                abort();
            }
            std::cout << std::hex << from << " - " << to << std::endl;
        }
        free(lineptr);
    }
    fclose(f);
    #else // __APPLE__
    // TODO: work out how to do the same thing on bsd-based OS
    #endif

    obj = mmap((void*)base_addr, size, PROT_READ | PROT_WRITE, MAP_PRIVATE | MAP_ANONYMOUS | MAP_FIXED, -1, 0);
    if (obj==MAP_FAILED) { perror("Homogenous memory mmap"); abort(); }

    #if 1
    unsigned char *obj2 = static_cast<unsigned char*>(obj);
    fprintf(stderr, "%p ... %p\n", obj, (void*)(size_t(obj)+size-1));
    // test: write + read
    for (size_t i=0 ; i<size ; ++i) {
            obj2[i] = 1 - (i&0xFF);
    }

    for (size_t i=0 ; i<size ; ++i) {
            unsigned char oracle = 1-(i&0xFF);
            if (obj2[i] != oracle) {
                fprintf(stderr, "After populating array, %p[%llx] == %u (should be %u)\n", obj, (unsigned long long)i, obj2[i], oracle);
                abort();
            }
    }
    #endif


#else

    if (x10rt_nplaces() == 1) {
        // Because there is only a single place, we can just fall back to malloc
        // Don't call x10rt_register_mem because on most transports, it is
        // unimplemented and unhelpfully returns 0 to indicate that.
        obj = x10aux::alloc_internal(size, false);
    } else {
        // In a multi-place run, we have to return the same virtual address in all
        // places or the program won't work.  Getting here indicates that we can't
        // do that, so we must abort the program.
        std::cerr << "alloc_internal_congruent not supported in multi-place executions on this platform\n";
        std::cerr << "aborting execution\n";
        abort();
    }

#endif

    congruent_base = static_cast<unsigned char*>(reinterpret_cast<void*>(x10rt_register_mem(obj, size)));
    congruent_cursor = congruent_base;
    
}

void *x10aux::alloc_internal_congruent(size_t size) {

    ensure_init_congruent(size);

    if (congruent_cursor - congruent_base + size > congruent_sz) {
        // run out of space
        #ifndef NO_EXCEPTIONS
        throwException<x10::lang::OutOfMemoryError>();
        #else
        fprintf(stderr, "Out of congruent memory, see "ENV_CONGRUENT_SIZE"\n");
        abort();
        #endif
    }

    void *r = congruent_cursor;
    size_t alignment = 8;
    // in case size is not a multiple of alignment
    congruent_cursor += (size+alignment-1) / alignment * alignment;

    return r;
}
