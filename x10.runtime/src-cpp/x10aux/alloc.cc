/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

#include <x10aux/config.h>

#include <x10aux/alloc.h>

#include <cstdio>
#include <cstdarg>

#include <x10aux/throw.h>
#include <x10/lang/OutOfMemoryError.h>

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

using namespace x10aux;

void x10aux::reportOOM(size_t size) {
    throwException<x10::lang::OutOfMemoryError>();
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
    std::size_t sz = vsnprintf(try_buf, 0, fmt, args);
    va_end(args);
    char *r = x10aux::realloc(buf, original_sz+sz+1);
    // append the new stuff onto the original stuff
    va_start(args, fmt);
    std::size_t s1 = vsnprintf(&r[original_sz], sz+1, fmt, args);
    (void) s1;
    assert (s1 == sz);
    va_end(args);
    return r;
}

char * x10aux::alloc_utils::strdup(const char* old) {
#ifdef X10_USE_BDWGC
    int len = strlen(old);
    char *ans = x10aux::alloc<char>(len+1);
    memcpy(ans, old, len);
    ans[len] = 0;
    return ans;
#else
    return ::strdup(old);
#endif
}

char * x10aux::alloc_utils::strndup(const char* old, int len) {
#if defined(X10_USE_BDWGC) || defined(__SVR4) || defined(__APPLE__)
    int len2 = strlen(old);
    if (len2 < len) len = len2;
    char *ans = x10aux::alloc<char>(len+1);
    memcpy(ans, old, len);
    ans[len] = 0;
    return ans;
#else
    return ::strndup(old, len);
#endif
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
    if (!x10aux::disable_dealloc) {
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

void x10aux::trigger_gc() {
#ifdef X10_USE_BDWGC
    GC_gcollect();
#else
    // DO nothing.
#endif
}

namespace {
    bool have_init_congruent = false;
    unsigned char *congruent_base;
    unsigned char *congruent_cursor;
    size_t congruent_sz;
}

#ifdef __linux__
// partial reimplemntation of glibc's getline
static ssize_t mygetline (char **lineptr, size_t *sz, FILE *f)
{
    assert(*lineptr==NULL);
    assert(lineptr!=NULL);
    assert(sz!=NULL);
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

static void ensure_init_congruent (size_t req_size) {

    if (have_init_congruent) return;
    have_init_congruent = true;

    char *size_ = x10aux::get_congruent_size();
    size_t size = size_!=NULL ? strtoull(size_,NULL,0) : 0;
    size_t count = 0;

    long page = 0;

    // get the page size
    if (x10aux::congruent_huge) {

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
                }
                if (strcmp(key_c,"HugePages_Total") == 0) {
                    const char *val_c   = strtok_r(NULL,"k",&saveptr);
                    if (val_c == NULL) { fprintf(stderr, "Formatting error in /proc/meminfo!!\n"); abort(); }
                    size_t val = strtoull(val_c,NULL,10);
                    count = val;
                }
            }
            ::free(lineptr);
        }
        fclose(f);

        #elif defined(__aix)

        page = 16 * 1024 * 1024; // 16MB

        #else

        fprintf(stderr, "Using huge pages for congruent memory is not supported on your platform.  Please unset "ENV_CONGRUENT_HUGE".\n");
        abort();

        #endif

    } else {
        // do not use PAGE_SIZE as the compile-time value may not reflect the machine the code runs on
        page = sysconf(_SC_PAGESIZE); 
    }

    if (page == 0) {
        fprintf(stderr, "Was not able to determine the page size!\n");
        abort();
    }

    // if it's the first allocation, may as well make it big enough -- further allocations will fail
    if (size == 0) size = page * (count / 32);
    if (size < req_size) size = req_size;

    // round it up to the nearest page
    size = ((size+page-1) / page) * page;

    congruent_sz = size;

    if (size==0) {
        congruent_base = NULL;
        congruent_cursor = NULL;
        return;
    }

    // otherwise, we have some very system-specific work to do...
    void *obj;

    if (x10aux::congruent_huge) {

        // huge pages are useful for performance, e.g. due to reducing TLB misses
        // they are non-standard, currently aix and linux are supported.

        // we are assuming that huge pages (being very special) are going to always occupy the same virtual address space

        #if defined(__linux__) && !defined(SHM_HUGETLB)
            fprintf(stderr, "Using huge pages for congruent memory is only supported on Linux >= 2.6.  Please unset "ENV_CONGRUENT_HUGE".\n");
            abort();
        #elif defined(_AIX) && !defined(SHM_PAGESIZE)
            fprintf(stderr, "This AIX system appears not to have SHM_PAGESIZE.  Please unset "ENV_CONGRUENT_HUGE".\n");
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

            char *base_addr_ = x10aux::get_congruent_base();
            // Test different addresses by overriding with X10_CONGRUENT_BASE environment variable (takes decimal and hex)
            size_t base_addr = base_addr_!=NULL ? strtoull(base_addr_,NULL,0) : 0;

            if (base_addr % page) {
                fprintf(stderr, ENV_CONGRUENT_BASE"=0x%llx is not a multiple of the page size (0x%llx)\n",
                        (unsigned long long)base_addr, (unsigned long long)page);
                abort();
            }

            if (congruent_offset % page) {
                fprintf(stderr, ENV_CONGRUENT_OFFSET"=0x%llx is not a multiple of the page size (0x%llx)\n",
                        (unsigned long long)congruent_offset, (unsigned long long)page);
                abort();
            }

            obj = shmat(shm_id,(void*)(base_addr + congruent_offset * (here % (1 << congruent_period))),0);  // 'attach' the shared memory at any arbitrary address (seemingly, this is always the same)
            shmctl(shm_id, IPC_RMID, NULL); // mark for destruction, will be deallocated when shmdt is called (for x10, never)
        #endif


    } else {

        // we're not using huge pages, however we still need an address that is consistent across all places
        // so we use mmap with a fixed address

        #if !defined(_AIX) && !defined(__linux__) && !defined(__APPLE__)

            // in particular, cygwin can fall in this trap
            // other platforms have yet to be investigated for possible support

            if (x10aux::num_places == 1) {
                // Because there is only a single place, we can just fall back to malloc
                obj = x10aux::alloc_internal(size, false);
            } else {
                // In a multi-place run, we have to return the same virtual address in all
                // places or the program won't work.  Getting here indicates that we can't
                // do that, so we must abort the program.
                fprintf(stderr,"alloc_internal_congruent not supported in multi-place executions on this platform\n");
                fprintf(stderr,"aborting execution\n");
                abort();
            }

        #else

            char *base_addr_ = x10aux::get_congruent_base();
            // Default addresses based on some experimentation on 32 bit and 64 bit platforms.  Not very reliable.
            // Test different addresses by overriding with X10_CONGRUENT_BASE environment variable (takes decimal and hex)
            #ifdef _ARCH_PPC
            size_t default_base_addr = sizeof(void*)==4 ? 0x70000000LL : 0x10000000000LL;
            #else
            size_t default_base_addr = sizeof(void*)==4 ? 0x70000000LL : 0x100000000000LL;
            #endif
            size_t base_addr = base_addr_!=NULL ? strtoull(base_addr_,NULL,0) : default_base_addr;

            if (base_addr % page) {
                fprintf(stderr, ENV_CONGRUENT_BASE"=0x%llx is not a multiple of the page size (0x%llx)\n",
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
                        fprintf(stderr, "Please specify alternative address range with environment variable "ENV_CONGRUENT_BASE".\n");
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

    #if 0
    unsigned char *obj2 = static_cast<unsigned char*>(obj);
    fprintf(stderr, "%p ... %p\n", obj, (void*)(size_t(obj)+size-1));
    // test: write
    for (size_t i=0 ; i<size ; ++i) {
            obj2[i] = 1 - (i&0xFF);
    }
    // test: read and check correct
    for (size_t i=0 ; i<size ; ++i) {
            unsigned char oracle = 1-(i&0xFF);
            if (obj2[i] != oracle) {
                fprintf(stderr, "After populating array, %p[%llx] == %u (should be %u)\n", obj, (unsigned long long)i, obj2[i], oracle);
                abort();
            }
    }
    #endif

    // register all congruent memory with x10rt so that remote ops can be used
    x10aux::register_mem(obj, size);
    congruent_base = static_cast<unsigned char*>(obj);
    congruent_cursor = congruent_base;
    
}


void *x10aux::alloc_internal_huge(size_t size) {
    if (size==0) {
        return NULL;
    }

    long page = 0;

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

	fprintf(stderr, "Using huge pages for congruent memory is not supported on your platform.  Please unset "ENV_CONGRUENT_HUGE".\n");
	abort();

	#endif

    if (page == 0) {
        fprintf(stderr, "Was not able to determine the page size!\n");
        abort();
    }

    // round it up to the nearest page
    size = ((size+page-1) / page) * page;

    // otherwise, we have some very system-specific work to do...
    void *obj;

	#if defined(__linux__) && !defined(SHM_HUGETLB)
		fprintf(stderr, "Using huge pages for congruent memory is only supported on Linux >= 2.6.  Please unset "ENV_CONGRUENT_HUGE".\n");
		abort();
	#elif defined(_AIX) && !defined(SHM_PAGESIZE)
		fprintf(stderr, "This AIX system appears not to have SHM_PAGESIZE.  Please unset "ENV_CONGRUENT_HUGE".\n");
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
		int shm_id=shmget(IPC_PRIVATE, size, shmflag);
		if (shm_id == -1) {
			perror("shmget");
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

	return obj;
}

void *x10aux::alloc_internal_congruent(size_t size) {

    ensure_init_congruent(size);

    if (congruent_cursor - congruent_base + size > congruent_sz) {
        // run out of space
        throwException<x10::lang::OutOfMemoryError>();
    }

    void *r = congruent_cursor;
    size_t alignment = 8;
    // in case size is not a multiple of alignment
    congruent_cursor += (size+alignment-1) / alignment * alignment;

    return r;
}

void *x10aux::compute_congruent_addr(void* addr, int src, int dst) {

    if (x10aux::congruent_huge) {
        int modSrc = src % (1 << x10aux::congruent_period);
        int modDst = dst % (1 << x10aux::congruent_period);
        addr = (void*)((x10_ulong)addr - (x10_ulong)(x10aux::congruent_offset * modSrc) + (x10_ulong)(x10aux::congruent_offset *  modDst));
    }
    return addr;
}



