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

/**
 * Implementation file for the low level thread interface.
 */

#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/throw.h>

#include <x10/lang/Thread.h>

#include <x10/lang/Place.h>
#include <x10/lang/String.h>

#include <x10/lang/Debug.h>
#include <x10/lang/InterruptedException.h>
#include <x10/lang/IllegalArgumentException.h>
#include <x10/lang/Runtime__Worker.h>

#include <unistd.h>
#include <errno.h>
#include <sstream>
#include <signal.h>
#include <sys/time.h>
#include <sys/types.h>

#ifdef __CYGWIN__
#define pthread_attr_setguardsize(A,B) do { (void)A; (void)B; } while(0)
#define PTHREAD_STACK_MIN 0
#define pthread_attr_setstacksize(A,B) do { (void)A; (void)B; } while(0)
#endif

using namespace x10::lang;
using namespace x10aux;
using namespace std;

// this __thread_start_trap gets used by the X10 debugger, to help it differentiate
// an X10 worker thread from other threads in the application (GC, network, etc).
extern "C" void __thread_start_trap() {}

// initialize static data members
long x10::lang::Thread::__thread_cnt = 0;
pthread_key_t Thread::__thread_mapper = 0;
x10_boolean Thread::__thread_mapper_inited = false;

// Thread start routine.
void*
x10::lang::Thread::thread_start_routine(void *arg)
{
    // simply call the run method of the invoking thread object
    __xrxDPrStart();
    Thread *tp = (Thread *)arg;

    // store this object reference in the place wide mapper key
    if (__thread_mapper_inited) {
        pthread_setspecific(__thread_mapper, arg);
    }
    pthread_mutex_lock(&(tp->__thread_start_lock));
    while (tp->__thread_already_started == false) {
        pthread_cond_wait(&(tp->__thread_start_cond), &(tp->__thread_start_lock));
    }
    pthread_mutex_unlock(&(tp->__thread_start_lock));
    tp->thread_bind_cpu();
    // this thread is now running
    tp->__thread_running = true;
    __thread_start_trap();

    tp->__apply();

    // finished running
    tp->__thread_running = false;
    __xrxDPrEnd();
    pthread_exit(NULL);
    return NULL; // quell compiler warning
}


Thread* Thread::_make(x10::lang::String* name) {
    return (new (alloc<Thread>()) Thread())->_constructor(name);
}

Thread* Thread::_make() {
    return NULL;
}

// method to bind the process to a single processor
void Thread::thread_bind_cpu()
{
	// open the file specified by X10RT_CPUMAP
	char * filename = getenv("X10RT_CPUMAP");
	if (filename == NULL) return;
#ifdef __linux__
	FILE * fd = fopen(filename, "r");
	if (fd == NULL)
	{
		fprintf(stderr, "Unable to read %s, specified by X10RT_CPUMAP.  Continuing without cpu binding...\n", filename);
		return;
	}

	int lineNumber = 0;
	char buffer[32];
	while (lineNumber <= x10aux::here)
	{
		char* s = fgets(buffer, sizeof(buffer), fd);
		if (s == NULL)
		{
			fprintf(stderr, "Unable to bind place %u to a CPU because there %s only %i line%s in the file %s. Continuing without cpu binding...\n", x10aux::here, lineNumber==1?"is":"are", lineNumber, lineNumber==1?"":"s",filename);
			fclose(fd);
			return;
		}

		if (lineNumber < x10aux::here)
		{
			lineNumber++;
			continue;
		}

		int processor = (int) strtol(s, (char **)NULL, 10);
		if (processor==0 && (errno == EINVAL || errno == ERANGE))
			fprintf(stderr, "Unable to bind place %u to CPU \"%s\": %s.  Continuing without cpu binding...\n", x10aux::here, s, strerror(errno));

		cpu_set_t mask;
		CPU_ZERO(&mask); // disable all CPUs (all are enabled by default)
		CPU_SET(processor, &mask); // enable the one CPU specified in the file
		if( sched_setaffinity(0, sizeof(mask), &mask ) == -1 )
			fprintf(stderr, "Unable to bind place %u to CPU %i: %s. Continuing without cpu binding...\n", x10aux::here, processor, strerror(errno));
		break;
	}
	fclose(fd);
#else
	fprintf(stderr, "X10RT_CPUMAP is not supported on this platform.  Continuing without cpu binding....\n");
#endif
}

// Helper method to initialize a Thread object.
void
Thread::thread_init(String* name)
{
    __xrxDPrStart();
    // increment the overall thread count
    __thread_cnt += 1;

    // set thread's external id
    __thread_id = __thread_cnt;

    // clear this thread's run flags
    __thread_already_started = false;
    __thread_running = false;

	__current_worker = NULL;
    __thread_name = String::_make(name);

    // create start condition object with default attributes
    // ??check the return code for ENOMEM/EAGAIN??
    (void)pthread_cond_init(&__thread_start_cond, NULL);

    // create the associated lock object with default attributes
    // ??check the return code for ENOMEM??
    (void)pthread_mutex_init(&__thread_start_lock, NULL);

    /**
     * create place wide pthread to Thread mapping key to
     * store thread object reference
     */
    if (!__thread_mapper_inited) {
        pthread_key_create(&__thread_mapper, NULL);
        __thread_mapper_inited = true;
    }

    if (__thread_id != 1) {
        // default: create a new execution thread

        // ??check the return code for ENOMEM??
        (void)pthread_attr_init(&__xthread_attr);

        initAttributes(&__xthread_attr);

        int err = pthread_create(&__xthread, &__xthread_attr,
                                 thread_start_routine, (void *)this);
        if (err) {
            ::fprintf(stderr,"Could not create worker thread: %s\n", ::strerror(err));
            ::abort();
        }
    } else {
        // hack: if this is the first worker thread ever created (in bootstrap.cc)
        // then take over the current thread instead of creating a new one
        pthread_setspecific(__thread_mapper, this);
        thread_bind_cpu();
        __thread_running = true;
        __thread_start_trap();
    }
    // create this thread's permit object
    thread_permit_init(&__thread_permit);
    // create this thread's cond_mutex object
    thread_cmp_init(&__thread_cmp);

    __xrxDPrEnd();
}


void Thread::initAttributes(pthread_attr_t* attr) {
    // guardsize
#ifdef _AIX
    size_t guardsize = PAGESIZE;
#else
    size_t guardsize = getpagesize();
#endif
    pthread_attr_setguardsize(attr, guardsize);
    // inheritsched
    int inheritsched = PTHREAD_INHERIT_SCHED;
    pthread_attr_setinheritsched(attr, inheritsched);
    // schedpolicy
    int policy = SCHED_OTHER;
    pthread_attr_setschedpolicy(attr, policy);
    // detachstate
    int detachstate = PTHREAD_CREATE_JOINABLE;
    //int detachstate = PTHREAD_CREATE_DETACHED;
    pthread_attr_setdetachstate(attr, detachstate);
    // contentionscope
    int contentionscope = PTHREAD_SCOPE_PROCESS;
    pthread_attr_setscope(attr, contentionscope);

    // Check to see if the user is trying to explictly set the stack size.
    // If they are, do what they say.  If not do nothing and just use the default.
    bool defined = false;
    size_t stacksize = getMemSizeEnvVar("X10_STACK_SIZE", &defined);
    if (defined) {
#if defined(__CYGWIN__) 
        ::fprintf(stderr, "Sorry setting the stacksize is not supported on cygwin. Using default size.\n");
#else
        int rc = pthread_attr_setstacksize(attr, stacksize);
        if (rc != 0) {
            ::fprintf(stderr, "Cannot set stack size to %d; %s. Using default size instead.\n", (int)stacksize, ::strerror(rc));
        } else {
            ::fprintf(stderr, "Successfully set stack size to %d\n", (int)stacksize);
        }
#endif
    }

    // suspendstate
    //int suspendstate = PTHREAD_CREATE_SUSPENDED_NP;
    //pthread_attr_setsuspendstate_np(attr, suspendstate);
}
    

// destructor
Thread::~Thread()
{
    __xrxDPrStart();

    // free start condition object & its lock
    pthread_mutex_destroy(&__thread_start_lock);
    pthread_cond_destroy(&__thread_start_cond);
    // free thread attributes
    pthread_attr_destroy(&__xthread_attr);
    // free thread permit
    thread_permit_destroy(&__thread_permit);
    // free thread cond_mutex
    thread_cmp_destroy(&__thread_cmp);
    __xrxDPrEnd();
}

// Returns a reference to the currently executing thread object.
Thread* Thread::currentThread(void)
{
    Thread *tp = (Thread *)pthread_getspecific(__thread_mapper);
    return tp;
}

// Begin thread execution.
void
Thread::start(void)
{
    __xrxDPrStart();
    if (__thread_already_started) {
        throwException<IllegalArgumentException>();
    }
    pthread_mutex_lock(&__thread_start_lock);
    __thread_already_started = true;
    pthread_cond_signal(&__thread_start_cond);
    pthread_mutex_unlock(&__thread_start_lock);
    __xrxDPrEnd();
}

// Waits forever for this thread to die.
void
Thread::join(void)
{
    __xrxDPrStart();
    pthread_join(__xthread, NULL);
    __xrxDPrEnd();
}

// Tests if this thread is alive.
x10_boolean
Thread::isAlive()
{
    __xrxDPrStart();
    if (__thread_already_started && __thread_running) {
        return true;
    }
    return false;
    __xrxDPrEnd();
}

// Interrupts this thread.
void
Thread::interrupt()
{
    __xrxDPrStart();
    if (isAlive()) {
        pthread_kill(__xthread, SIGINT);
    };
    __xrxDPrEnd();
}

// Clean-up routine for sleep method call.
void
Thread::thread_sleep_cleanup(void *arg)
{
    cond_mutex_t *cmp = (cond_mutex_t *)arg;

    __xrxDPrStart();
    pthread_mutex_unlock(&(cmp->mutex));
//    signal(SIGINT, SIG_DFL);
    __xrxDPrEnd();
}

/**
 * Put the current thread to sleep for the specified number of
 * milliseconds.
 */
void
Thread::sleep(x10_long millis)
{
    sleep(millis, 0);
}

// Dummy interrupt handler.
void
Thread::intr_hndlr(int signo)
{
    (void)signo;
    __xrxDPr();
}

/**
 * Put the current thread to sleep for the specified number of
 * milliseconds plus the specified number of nano seconds.
 */
void
Thread::sleep(x10_long millis, x10_int nanos)
{
    Thread* th = currentThread();
    cond_mutex_t *cmp = &(th->__thread_cmp);

    x10_boolean done = false;
    struct timeval tval;
    struct timespec tout;
    long sleep_usec;
    int rc;

    __xrxDPrStart();
//    signal(SIGINT, intr_hndlr);
    pthread_mutex_lock(&(cmp->mutex));
    pthread_cleanup_push(thread_sleep_cleanup, (void *)cmp);
    gettimeofday(&tval, NULL);
    tout.tv_sec = tval.tv_sec + (millis/1000);
    x10_long tout_nanos = ((tval.tv_usec + ((millis%1000) * 1000)) * 1000) + nanos;
    tout.tv_sec += tout_nanos / 1000000000UL;
    tout.tv_nsec = tout_nanos % 1000000000UL;
    sleep_usec = (tout.tv_sec * 1000 * 1000) + (tout.tv_nsec / 1000);
    while (!done) {
        rc = pthread_cond_timedwait(&(cmp->cond), &(cmp->mutex), &tout);
        if (rc == ETIMEDOUT) {
            // specified timeout has passed
            done = true;
        } else {
            // might be a spurious wakeup
            // throwException<InterruptedException>();
            break;
            /*
            struct timeval cval;
            long cur_usec;
            gettimeofday(&cval, NULL);
            cur_usec = (cval.tv_sec * 1000 * 1000) + cval.tv_usec;
            if (cur_usec < sleep_usec) {
                throwException<InterruptedException>();
                done = false;
                continue;
            }
            */
        }
    }
    pthread_cleanup_pop(1);
    if (!done) throwException<InterruptedException>();
    __xrxDPrEnd();
}

// permit initialization
void
Thread::thread_permit_init(permit_t *perm)
{
    pthread_mutex_init(&(perm->mutex), NULL);
    pthread_cond_init(&(perm->cond), NULL);
    perm->permit = false;
}

// cond_mutex initialization
void
Thread::thread_cmp_init(cond_mutex_t *cmp)
{
    pthread_mutex_init(&(cmp->mutex), NULL);
    pthread_cond_init(&(cmp->cond), NULL);
}

// permit finalization
void
Thread::thread_permit_destroy(permit_t *perm)
{
    pthread_mutex_destroy(&(perm->mutex));
    pthread_cond_destroy(&(perm->cond));
}

// cond_mutex finalization
void
Thread::thread_cmp_destroy(cond_mutex_t *cmp)
{
    pthread_mutex_destroy(&(cmp->mutex));
    pthread_cond_destroy(&(cmp->cond));
}

// permit cleanup
void
Thread::thread_permit_cleanup(void *arg)
{
    permit_t *perm = (permit_t *)arg;

    pthread_mutex_unlock(&(perm->mutex));
}

/**
 * Disables the current thread for thread scheduling purposes
 * unless the permit is available.
 */
void
Thread::park(void)
{
    Thread* th = currentThread();
    permit_t *perm = &(th->__thread_permit);

    pthread_mutex_lock(&(perm->mutex));
    pthread_cleanup_push(thread_permit_cleanup, (void *)perm);
    while (perm->permit == false) {
        pthread_cond_wait(&(perm->cond), &(perm->mutex));
    }
    perm->permit = false;
    pthread_cleanup_pop(1);
}

/**
 * Disables the current thread for thread scheduling purposes,
 * for up to the specified waiting time, unless the permit is
 * available.
 */
void
Thread::parkNanos(x10_long nanos)
{
    Thread* th = currentThread();
    permit_t *perm = &(th->__thread_permit);
    struct timeval tval;
    struct timespec tout;
    int rc;
    x10_long nanosPerSecond = 1000000000LL;

    gettimeofday(&tval, NULL);

    /* One must take care to ensure that the final value of tout.tv_nsec is valid (ie, between 0 and 1e9-1). */
    x10_long timeOutNanos = nanos + (tval.tv_usec * 1000);
    x10_long timeOutSeconds = (timeOutNanos/nanosPerSecond);
    timeOutNanos -= (timeOutSeconds*nanosPerSecond);
    assert(timeOutNanos >= 0 && timeOutNanos < nanosPerSecond);

    tout.tv_sec = tval.tv_sec + timeOutSeconds;
    tout.tv_nsec = timeOutNanos;

    pthread_mutex_lock(&(perm->mutex));
    pthread_cleanup_push(thread_permit_cleanup, (void *)perm);
    while (perm->permit == false) {
        rc = pthread_cond_timedwait(&(perm->cond), &(perm->mutex), &tout);
        if (rc == ETIMEDOUT) {
            perm->permit = true;
        }
    }
    perm->permit = false;
    pthread_cleanup_pop(1);
}

/**
 * Makes available the permit for the given thread, if it was
 * not already available.
 */
void
Thread::unpark()
{
    permit_t *perm = &(__thread_permit);

    pthread_mutex_lock(&(perm->mutex));
    if (!perm->permit) {
        perm->permit = true;
        pthread_cond_signal(&(perm->cond));
    }
    pthread_mutex_unlock(&(perm->mutex));
}

// Returns the current worker.
Runtime__Worker*
Thread::worker(void)
{
    return __current_worker;
}

x10::lang::Place
Thread::home(void)
{
    return x10::lang::Place::_make(x10aux::here);
}

// Set the current worker.
void
Thread::worker(Runtime__Worker* worker)
{
    __current_worker = worker;
}

// Returns the identifier of this thread.
long
Thread::getId()
{
    return __thread_id;
}

// Thread context is not used in Native X10, only in Managed X10
void
Thread::removeWorkerContext()
{
}

// Returns the system thread id.
x10_long
Thread::getTid()
{
    return (x10_long)pthread_self();
}

// Returns this thread's name.
String*
Thread::name(void)
{
    return __thread_name;
}

// Set the name of this thread.
void
Thread::name(String* name)
{
    __thread_name = name;
}

void Thread::__apply()
{
}

x10aux::serialization_id_t Thread::_get_serialization_id() {
    x10aux::throwNotSerializableException("Can't serialize x10.lang.Thread");
}

void Thread::_serialize_body(x10aux::serialization_buffer &buf) {
    x10aux::throwNotSerializableException("Can't serialize x10.lang.Thread");
}

RTT_CC_DECLS0(Thread, "x10.lang.Thread", RuntimeType::class_kind)

// vim:tabstop=4:shiftwidth=4:expandtab
