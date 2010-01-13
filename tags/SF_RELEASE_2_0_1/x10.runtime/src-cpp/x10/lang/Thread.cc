/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX/C++ native layer implementation.
 */

/**
 * Implementation file for the low level thread interface.
 */

#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/throw.h>

#include <x10/lang/Thread.h>

#include <x10/lang/String.h>

#include <x10/lang/Debug.h>
#include <x10/lang/InterruptedException.h>
#include <x10/lang/IllegalThreadStateException.h>
#include <x10/lang/Runtime__Worker.h>

#include <unistd.h>
#include <errno.h>
#include <sstream>
#include <signal.h>
#include <sys/time.h>

#ifdef __CYGWIN__
#define pthread_attr_setguardsize(A,B) do { (void)A; (void)B; } while(0)
#define PTHREAD_STACK_MIN 0
#define pthread_attr_setstacksize(A,B) do { (void)A; (void)B; } while(0)
#endif

using namespace x10::lang;
using namespace x10aux;
using namespace std;


// initialize static data members
ref<Thread> Thread::__current_thread = null;
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
    // this thread is now running
    tp->__thread_running = true;

    ref<Reference> taskBody = nullCheck(tp->__taskBody);
    (taskBody.operator->()->*(x10aux::findITable<VoidFun_0_0>(taskBody->_getITables())->apply))();

    // finished running
    tp->__thread_running = false;
    __xrxDPrEnd();
    pthread_exit(NULL);
    return NULL; // quell compiler warning
}


ref<Thread>
Thread::_make(ref<x10::lang::VoidFun_0_0> task, ref<x10::lang::String> name) {
    return (new (alloc<Thread>()) Thread())->_constructor(task,name);
}

const serialization_id_t Thread::_serialization_id =
    DeserializationDispatcher::addDeserializer(Thread::_deserializer<Object>);


// Helper method to initialize a Thread object.
void
Thread::thread_init(ref<VoidFun_0_0> task, const ref<String> name)
{
    __xrxDPrStart();
    // increment the overall thread count
    __thread_cnt += 1;

    // set thread's external id
    __thread_id = __thread_cnt;

    // clear this thread's run flags
    __thread_already_started = false;
    __thread_running = false;

	__current_worker = null;
    __thread_name = String::_make(name);
    __taskBody = task;


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

    // create thread attributes object
    // ??check the return code for ENOMEM??
    (void)pthread_attr_init(&__xthread_attr);

    // set this thread's attributes
    // guardsize
#ifdef _AIX_
    size_t guardsize = PAGESIZE;
#else
    size_t guardsize = getpagesize();
#endif
    pthread_attr_setguardsize(&__xthread_attr, guardsize);
    // inheritsched
    int inheritsched = PTHREAD_INHERIT_SCHED;
    pthread_attr_setinheritsched(&__xthread_attr, inheritsched);
    // schedpolicy
    int policy = SCHED_OTHER;
    pthread_attr_setschedpolicy(&__xthread_attr, policy);
    // detachstate
    int detachstate = PTHREAD_CREATE_JOINABLE;
    //int detachstate = PTHREAD_CREATE_DETACHED;
    pthread_attr_setdetachstate(&__xthread_attr, detachstate);
    // contentionscope
    int contentionscope = PTHREAD_SCOPE_PROCESS;
    pthread_attr_setscope(&__xthread_attr, contentionscope);

    /*
     * NOTE: Setting the stacksize to small breaks BDWGC.
     *       Just use the default stacksize to avoid confusing the GC!
     */
    //stacksize
    //size_t stacksize = PTHREAD_STACK_MIN;
    //*pthread_attr_setstacksize(&__xthread_attr, stacksize);

    // suspendstate
    //int suspendstate = PTHREAD_CREATE_SUSPENDED_NP;
    //pthread_attr_setsuspendstate_np(&__xthread_attr, suspendstate);

    // create a new execution thread ??in suspended state??
    if (__taskBody!=x10aux::null) {
        (void)pthread_create(&__xthread, &__xthread_attr,
                             thread_start_routine, (void *)this);
    } else {
        pthread_setspecific(__thread_mapper, this);
        __thread_running = true;
    }
    // create this thread's permit object
    thread_permit_init(&__thread_permit);

    __xrxDPrEnd();
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
    __xrxDPrEnd();
}

// Returns a reference to the currently executing thread object.
ref<Thread>
Thread::currentThread(void)
{
    Thread *tp = (Thread *)pthread_getspecific(__thread_mapper);
    return ref<Thread>(tp);
}

// Begin thread execution.
void
Thread::start(void)
{
    __xrxDPrStart();
    if (__thread_already_started) {
        throwException<IllegalThreadStateException>();
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
    pthread_mutex_destroy(&(cmp->mutex));
    pthread_cond_destroy(&(cmp->cond));
    delete cmp;
    signal(SIGINT, SIG_DFL);
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
    cond_mutex_t *cmp;
    x10_boolean done = false;
    struct timeval tval;
    struct timespec tout;
    long sleep_usec;
    int rc;

    __xrxDPrStart();
    signal(SIGINT, intr_hndlr);
    cmp = new (cond_mutex_t);
    pthread_mutex_init(&(cmp->mutex), NULL);
    pthread_cond_init(&(cmp->cond), NULL);
    pthread_mutex_lock(&(cmp->mutex));
    pthread_cleanup_push(thread_sleep_cleanup, (void *)cmp);
    gettimeofday(&tval, NULL);
    tout.tv_sec = tval.tv_sec + (millis/1000);
    tout.tv_nsec = ((tval.tv_usec + ((millis%1000) * 1000)) * 1000) + nanos;
    sleep_usec = (tout.tv_sec * 1000 * 1000) +
                (tout.tv_nsec / 1000);
    while (!done) {
        rc = pthread_cond_timedwait(&(cmp->cond), &(cmp->mutex), &tout);
        if (rc == ETIMEDOUT) {
            // specified timeout has passed
            done = true;
        } else {
            // might be a spurious wakeup
            throwException<InterruptedException>();
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

// permit finalization
void
Thread::thread_permit_destroy(permit_t *perm)
{
    pthread_mutex_destroy(&(perm->mutex));
    pthread_cond_destroy(&(perm->cond));
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
    ref<Thread> th = currentThread();
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
    ref<Thread> th = currentThread();
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
ref<Runtime__Worker>
Thread::worker(void)
{
    return __current_worker;
}

x10_int
Thread::locInt(void)
{
    return (x10_int) location;
}

// Set the current worker.
void
Thread::worker(ref<Runtime__Worker> worker)
{
    __current_worker = worker;
}

// Returns the identifier of this thread.
long
Thread::getId()
{
    return __thread_id;
}

// Returns this thread's name.
const ref<String>
Thread::name(void)
{
    return __thread_name;
}

// Set the name of this thread.
void
Thread::name(ref<String> name)
{
    __thread_name = name;
}

void Thread::_serialize_body(serialization_buffer &buf) {
    this->Object::_serialize_body(buf);
}

void Thread::_deserialize_body(deserialization_buffer& buf) {
    this->Object::_deserialize_body(buf);
}

RTT_CC_DECLS1(Thread, "x10.lang.Thread", Object)

// vim:tabstop=4:shiftwidth=4:expandtab
