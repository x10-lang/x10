#include <cstdlib>

#include <x10rt_types.h>

/** \file
 * User API.  Most applications should target this layer.  \see \ref structure
 *
 */

/** @mainpage X10RT API Specification
 *
 * @section intro Introduction
 *
 * X10RT is a C library (implemented in C++) that a compiled X10 program uses to communicate between
 * places.  It can also be used by other languages or applications, if they have sufficiently
 * similar semantics.  It also should be possible for third parties to reimplement certain X10RT
 * components on top of new networking layers and HPC systems.  This allows the running of X10
 * programs in those environments.  The API documentation discusses what the X10 process needs from
 * this layer in terms of semantics and performance.
 *
 * @subsection goals Technical Goals
 *
 * @li define a minimal set of network operations fundamentally needed by X10
 *
 * @li represent only the communication elements of the X10 implementation
 *
 * @li abstract from details of the X10 object model, serialisation, remote references, etc.
 *
 * @li allow highly scaled X10 configurations (e.g. 250000 places) 
 *
 * @li avoid forcing implementations to introduce latency/bandwidth overhead 
 *
 * @li allow implementations to use hardware accelerated memory copies (where appropriate)
 *
 * @subsection launch Configuration and Launch
 *
 * The X10RT implementation defines the application launch process, e.g., requiring the use of
 * mpirun. The configuration of the system (i.e. number of places) should be controlled by the
 * end-user of the X10 program. Determining the identity of a particular X10 process in the
 * execution is the responsibility of the X10RT implementation. When executing X10 processes, the
 * standard C entry point (int main) should be invoked as normal.  The application should then
 * explicitly initialize and finalize X10RT like any other library.
 * 
 * @subsection places Places
 *
 * An X10 program runs on more than one place.  These places consist firstly of hosts (e.g. x86 /
 * PPC machines, or some mixture of the above), on a network such as Ethernet, Infiniband, etc.
 * However there are also accelerator places that are children of these hosts.  Thus the topology of
 * places is a 2 level tree where all the hosts are equals on the network, and under each host are
 * optionally some accelerator cards.  Currently the accelerators are CUDA-capable GPUs only.  We
 * have plans for OpenCL places and there is some incomplete code for supporting QS21 blades.  Note
 * that in the current implementation, there is no distinction between hosts and processes.
 *
 * @subsection messages Messages 
 * 
 * During the execution of an X10 program, messages are exchanged between places. Messages have a
 * type/length/value format, where the type is a number in the range 0-65535. Messages are
 * registered by the X10 program but X10RT chooses the message type values that identify messages.
 * Messages can have arbitrary size, and two messages with the same type need not be the same size.
 * In all cases, individual messages may be re-ordered but delivery must be guaranteed.
 *
 * There are three types of messages:  'Plain' messages carry data that has been specially packed
 * into a buffer (allocated with the x10rt_*_realloc family of functions), and invoke a callback on
 * the remote side to handle the message.  'Put' messages additionally carry large amount of data
 * that is transmitted directly from a given existing datastructure, and have callbacks on the
 * remote side to locate a home for the data as well as to indicate that the transfer is complete.
 * 'Get' messages cause data to be retrieved from a place directly into the given datastructure, and
 * have a callback on the remote side to locate the source of the data and a callback on the
 * destination to indicate that the transfer is complete.  In X10, plain messages are used to
 * implement asyncs whereas put/get messages are used to implement the Rail copy functions.
 *
 * All of these messages cause callbacks to be invoked on the far side, so can be considered
 * 'active'.  They all carry a packed buffer of serialised data that is swallowed by X10RT.  There
 * is therefore a lot of overlap in functionality.  Note that if the amount of data is small or is
 * not in a contiguous form, or if the data formatting (endian, alignment, etc.) is not the same
 * between the two hosts, it is appropriate to use the 'plain' messages.  If the data is in a
 * contiguous form, the allocation and copy overhead of using a big packed buffer can be avoided by
 * using the get/put messages.  Note that the get/put messages still require a packed buffer, but it
 * would in this case only be used for metadata and thus the overhead would be negligible.
 *
 * @subsection structure Library Structure
 *
 * The X10RT library has various modules for implementing specific behavior.  The highest level API
 * is accessed via the functions in x10rt_front.h.  X10 and other languages should usually target
 * the x10rt_front.h API.  There is an auxiliary header x10rt_types.h that defines some common types
 * for the whole system.  More low level than x10rt_front.h there is the Logical Layer, found in
 * x10rt_logical.h, which deals only with communication.  The goal of the Logical Layer to join
 * together the various backend networking libraries presenting a uniform set of places.  It exports
 * functions that allow communication to and from these places no matter where they are found in the
 * topology.
 *
 * Underneath the Logical Layer are the networking layers.  There is a Core Networking Layer
 * x10rt_net.h which provides the links between hosts.  There are currently many implementations of
 * the core networking layer:  There is a standalone implementation that only supports a single host
 * place.  There is an MPI implementation that uses MPI for communication between hosts.  There is
 * also a proprietary implementation on top of the PGAS library, which internally supports many HPC
 * libraries and also has a sockets implementation.  All of these implement the symbols in
 * x10rt_net.h so they cannot currently be used simultaneously.  However one can link against
 * whichever implementation is preferred for inter-host communication.  Details on the available
 * implementations of the Core Networking Layer can be found <a
 * href=http://x10.codehaus.org/X10RT+Implementations>here</a>.
 *
 * In addition to the Core Networking Layer x10rt_net.h there is a layer for CUDA, which is intended
 * to wrap the NVidia CUDA API in a way that provides an interface that is very similar to
 * x10rt_net.h.  This is found in x10rt_cuda.h.  Similarly if there were completed Cell / OpenCL
 * layers, these would be separate interfaces x10rt_cell.h and x10rt_opencl.h defining their own
 * distinct symbols.  It is the job of the Logical Layer to take the Core Networking Layer and allow
 * crosstalk between this and the various accelerator places by connecting these distinct APIs
 * together and dispatching messages accordingly.
 *
 * \image html cake.png "X10RT structural diagram"
 * 
 *
 * @subsection performance Performance Notes
 *
 * The x10rt_send_* functions may block until some internal operation has completed, such as writing
 * to a network buffer. But blocking until acknowledgement from the other side of the transmission
 * is not allowed as it may impact the performance of the X10 program.
 *
 * The X10RT implementation is permitted to handle messages when it is called via one of the
 * x10rt_send_* calls. This relaxation may be useful for increasing throughput.
 *
 * The #x10rt_probe() function should not block waiting for network traffic to arrive, as to do so
 * would impact performance. Instead of blocking, #x10rt_probe() should return and the action should
 * be deferred to some future call of #x10rt_probe() (which may be from another thread) whereupon
 * the action can be completed instantly.
 *
 * @subsection callbacks Callbacks
 *
 * The X10RT API makes heavy use of callbacks at all levels for handling of incoming messages and
 * other events.  These callbacks are fully re-entrant.  This means they can call back into X10RT in
 * arbitrary recursive ways.  This is necessary because in X10 everything is an async and thus
 * asyncs are capable of executing arbitrary code, including sending other asyncs and waiting for
 * remote termination.  These callbacks can execute for as long as they need to but if they become
 * idle for whatever reason, they should not block or busy wait.  They should call #x10rt_probe() to
 * trigger more callbacks.
*/

/** \name Initialization functions
 *
 * The execution has two phases.  The first phase is single threaded and is used to initialize the
 * X10 process.  No messages are sent in this phase.  The phase begins with #x10rt_init and ends
 * with #x10rt_registration_complete.  The second phase is the execution of the actual X10 program,
 * during which communication occurs.  This phase is multi-threaded and the X10RT library may be
 * called from multiple threads simultaneously.  The second phase ends with the #x10rt_finalize
 * function.
 */

/** \{ */

/** Initialize the X10RT API.
 *
 * This should be the first call made by a process into X10RT.  This allows the X10RT implementation
 * to inspect and modify the command-line parameters to the X10 process.  This is needed e.g. for
 * the implementation of X10RT on MPI.
 *
 * Also, the X10RT_ACCELS environment variable is used to decide how to configure accelerators at
 * the particular host.  The value of this variable is a string containing a comma separated list of
 * accelerators to use.  Each accelerator is specified as a string containing the kind of
 * accelerator, currently 1 of CUDA and SPE (unsupported), and the index of the particular piece of
 * hardware.  For example "CUDA0,CUDA0,CUDA1,SPE0" will configure the host to use the first CUDA
 * device twice (two separate places on the same piece of hardware, as well as a 3rd place on the
 * second CUDA device, and a single SPE as well.  It is also possible to specify "ALL" to use each
 * piece of available hardware exactly once, or "NONE" (the default) to use no accelerators.
 * Lowercase can also be used.
 *
 * \param argc A pointer to the argc parameter from the application's ``main'' function.
 *
 * \param argv A pointer to the argv parameter from the application's ``main'' function.
*/
X10RT_C void x10rt_init (int *argc, char ***argv);

/** Register a new type of 'plain' message.  Messages are used to send serialized object graphs to
 * another place.  They are intended for when the data is not organized in a sequential fashion and
 * thus direct copies cannot be used.  They trigger execution of code on the destination, so the
 * messages can be considered 'active'.
 *
 * If the messages are not intended to be interpreted by a GPU, set the parameters marked as 'CUDA
 * only' to NULL.
 *
 * \see \ref messages \see \ref callbacks
 *
 * \param cb A callback that is invoked to process a newly received message of this type.
 *
 * \param pre CUDA only: A callback that runs on the CPU just before the message is processed on the
 * GPU.
 *
 * \param post CUDA only: A callback that runs on the CPU after the message has been processed by
 * the GPU.
 *
 * \param cubin CUDA only: The name of the cubin file where the kernel is found.
 *
 * \param kernel_name CUDA only: The name of the kernel that will be invoked to handle this message.
*/
X10RT_C x10rt_msg_type x10rt_register_msg_receiver (x10rt_handler *cb, x10rt_cuda_pre *pre,
x10rt_cuda_post *post, const char *cubin, const char *kernel_name);

/** Register a new type of 'get' message.  These are used to do direct copies from one place to
 * another, and also invoke code on the remote place.
 *
 * If the messages are not intended to be interpreted by a GPU, set the parameters marked as 'CUDA only' to NULL.
 *
 * \see \ref messages
 * \see \ref callbacks
 *
 * \param cb1 A callback that is invoked to find a buffer from which to fetch the data.
 *
 * \param cb2 A callback that is invoked at the location where the data has been copied, upon
 * completion of the copy.
 *
 * \param cuda_cb1 CUDA only: A callback that runs on the CPU on behalf of a GPU to find the buffer
 * from which to fetch the data.
 *
 * \param cuda_cb2 CUDA only: A callback that runs on the CPU after the data has been copied from
 * the GPU memory.  \todo Get callback cuda_cb2 seems to be pointless?
*/

X10RT_C x10rt_msg_type x10rt_register_get_receiver (x10rt_finder *cb1, x10rt_notifier *cb2,
                                                    x10rt_finder *cuda_cb1,
                                                    x10rt_notifier *cuda_cb2);


/** Register a new type of 'put' message.  These are used to do direct copies from one place to
 * another, and also invoke code on the remote place.
 *
 * If the messages are not intended to be interpreted by a GPU, set the parameters marked as 'CUDA only' to NULL.
 *
 * \see \ref messages
 * \see \ref callbacks
 *
 * \param cb1 A callback that is invoked to find a buffer into which to receive the data.
 *
 * \param cb2 A callback that is invoked at the location where the data has been copied, upon
 * completion of the copy.
 *
 * \param cuda_cb1 CUDA only: A callback that runs on the CPU on behalf of a GPU to find the buffer
 * into which to push the data.
 *
 * \param cuda_cb2 CUDA only: A callback that runs on the CPU hosting the GPU after the data has
 * been copied to the GPU memory.
*/

X10RT_C x10rt_msg_type x10rt_register_put_receiver (x10rt_finder *cb1, x10rt_notifier *cb2,
                                                    x10rt_finder *cuda_cb1,
                                                    x10rt_notifier *cuda_cb2);

/** Signal that all message types have been registered.  This acts like a barrier.  After the
 * function returns, the process may start sending messages to arbitrary places.  The X10RT
 * implementation shall not call any of the receivers registered by X10 until registration is
 * complete.
 */
X10RT_C void x10rt_registration_complete (void);

/** Terminate execution.
 *
 * The thread that calls #x10rt_finalize is the same thread that called #x10rt_init. X10 has
 * internal termination mechanisms that ensure that #x10rt_finalize is not called until the place in
 * question has finished sending and receiving messages from other places.
 */
X10RT_C void x10rt_finalize (void); 

/** \} */


/** \name Introspection functions
 *
 * These functions provide the X10 application with a way to find out about the places it is running
 * on and their interrelationships.
 *
 * The number of places must be representable in a 32 bit unsigned integer. A place's identity is
 * described by a 32 bit integer in the range 0..#x10rt_nplaces()-1. 
 *
 * \see \ref places
 */

/** \{ */

/** Number of places.  The total number of places (which is constant throughout a particular
 * execution), including accelerators and hosts.
 * \returns The number of places.
 */
X10RT_C x10rt_place x10rt_nplaces (void);

/** Number of hosts.  The total number of hosts (which is constant throughout a particular
 * execution).  Hosts can contain accelerators and are never accelerators themselves.  Note that
 * place ids for the hosts range from 0 to #x10rt_nhosts()-1.
 * \returns The number of hosts.
 */
X10RT_C x10rt_place x10rt_nhosts (void);

/** The local place.  An X10 process will discover its
 * own identity by calling this function.
 * \returns The place id of the current place.
 */
X10RT_C x10rt_place x10rt_here (void);

/** Find out about a particular place.
 * \param place The place about which we are interested.
 * \returns Whether or not the given place is a host (i.e. not an accelerator).
 */
X10RT_C bool x10rt_is_host (x10rt_place place);

/** Find out about a particular place.
 * \param place The place about which we are interested.
 * \returns Whether or not the given place is a CUDA-capable GPU.
 */
X10RT_C bool x10rt_is_cuda (x10rt_place place);

/** Find out about a particular place.
 * \param place The place about which we are interested.
 * \returns Whether or not the given place is an synergistic processing element of a Cell.
 */
X10RT_C bool x10rt_is_spe (x10rt_place place);


/** Find the host of the given accelerator.  If the place is not an accelerator, then itself is
 * returned.
 * \param place The place about which we are interested.
 * \returns The host.
 */
X10RT_C x10rt_place x10rt_parent (x10rt_place place);

/** Find the number of accelerators at a place.
 * \param place The place about which we are interested.
 * \returns The number of accelerators at that place.
 */
X10RT_C x10rt_place x10rt_nchildren (x10rt_place place);

/** Get a particular accelerator at a place.
 * \param host The place hosting the accelerator.
 * \param index A number between 0 and #x10rt_nchildren(#host).
 * \returns The identity of the accelerator found.
 */
X10RT_C x10rt_place x10rt_child (x10rt_place host, x10rt_place index);


/** Get a particular accelerator's position within a place.
 * \param child The accelerator.
 * \returns A number between 0 and #x10rt_nchildren(#x10rt_parent(#child)).
 */
X10RT_C x10rt_place x10rt_child_index (x10rt_place child);

/** \} */



/** \name Communication functions
 *
 * The call used to send a message corresponds to the kind of registration call used to register
 * callbacks for the same message type in the first phase of execution. All messages take a set of
 * common parameters defined in the structure #x10rt_msg_params.
 *
 * \see \ref messages
 */

/** \{ */

/** Allocate memory in which to pack data for a message send.  The X10 process will use
 * x10rt_msg_realloc like the standard C realloc function, i.e. will first call
 * x10rt_msg_realloc(NULL,0,n) for some n and then grow the buffer with further calls as necessary.
 * It is guaranteed that the X10 process will use the same thread to both prepare the buffer and
 * send it. The X10RT implementation should recover and reuse the msg buffer at its own leisure
 * after the #x10rt_send_msg call, as if there had been a call to #x10rt_msg_realloc to resize the
 * buffer down to 0. The X10RT implementation can therefore make sure msg is aligned / pinned /
 * padded or generally allocated in any special way that it needs.
 *
 * \param old The buffer returned by a previous call, or NULL for the first call.
 *
 * \param old_sz The size that was requested in the previous call, or NULL for the first call.
 *
 * \param new_sz The size that is now needed.  If new_sz is 0, the function should return NULL and
 * free the buffer at old, if any.
 */
X10RT_C void *x10rt_msg_realloc (void *old, size_t old_sz, size_t new_sz);

/** Send a message.  The #x10rt_send_msg call should not block waiting for the remote side to finish
 * executing its callbacks, as to do so would introduce deadlocks in the X10 program.
 *
 * \param p The particulars of the message.
 */
X10RT_C void x10rt_send_msg (x10rt_msg_params *p);

/** Allocate memory in which to pack data for a message 'get'.  See #x10rt_msg_realloc for intended
 * usage.
 *
 * \param old The buffer returned by a previous call, or NULL for the first call.
 *
 * \param old_sz The size that was requested in the previous call, or NULL for the first call.
 *
 * \param new_sz The size that is now needed.  If new_sz is 0, the function should return NULL and
 * free the buffer at old, if any.
 */
X10RT_C void *x10rt_get_realloc (void *old, size_t old_sz, size_t new_sz);

/** Send a 'get' message.  The #x10rt_send_msg function is sufficient for implementing asyncs, but
 * is cumbersome and inefficient for implementing large rail copies.  This function is an
 * alternative API that can be used when a large amount of data needs to be transfered and is
 * already stored in a simple contiguous format.  The #x10rt_send_get call should not block waiting
 * for the remote side to finish executing its callbacks, as to do so would introduce deadlocks in
 * the X10 program.
 *
 * \param p The particulars of the message.
 *
 * \param buf The local location where the copy will receive the data.
 *
 * \param len The amount of data to copy.
 */
X10RT_C void x10rt_send_get (x10rt_msg_params *p, void *buf, x10rt_copy_sz len);

/** Allocate memory in which to pack data for a message 'put'.  See #x10rt_msg_realloc for intended
 * usage.
 *
 * \param old The buffer returned by a previous call, or NULL for the first call.
 *
 * \param old_sz The size that was requested in the previous call, or NULL for the first call.
 *
 * \param new_sz The size that is now needed.  If new_sz is 0, the function should return NULL and
 * free the buffer at old, if any.
 */
X10RT_C void *x10rt_put_realloc (void *old, size_t old_sz, size_t new_sz);

/** Send a 'put' message.  See #x10rt_send_get for more information.  The #x10rt_send_put call
 * should not block waiting for the remote side to finish executing its callbacks, as to do so would
 * introduce deadlocks in the X10 program.
 *
 * \param p The particulars of the message.
 *
 * \param buf The local location from where the data will be copied.
 *
 * \param len The amount of data to copy.
 */
X10RT_C void x10rt_send_put (x10rt_msg_params *p, void *buf, x10rt_copy_sz len);

/** Allocate memory at a remote place.
 *
 * \bug This is a blocking API.
 *
 * \param place The location where memory will be allocated.
 * \param sz The amount of memory to allocate
 * \returns A pointer that is valid on the remote place only.
 */
X10RT_C x10rt_remote_ptr x10rt_remote_alloc (x10rt_place place, x10rt_remote_ptr sz);

/** Free memory at a remote place.
 *
 * \param place The place where the memory resides.
 * \param ptr The address in memory, valid at the remote place.
 */
X10RT_C void x10rt_remote_free (x10rt_place place, x10rt_remote_ptr ptr);

/** Do an 64 bit XOR operation at a remote place.
 * \param place The place where the memory resides.
 * \param addr The location of the 64 bit word at the remote place.
 * \param update The value that will be XORed with the existing value at that location.
 */
X10RT_C void x10rt_remote_xor (x10rt_place place, x10rt_remote_ptr addr, long long update);

/** Wait for outstanding operations to complete.  \see x10rt_remote_xor
 */
X10RT_C void x10rt_remote_op_fence (void);

/** Perform a global SPMD-style barrier. Blocks until every place calls the barrier operation.
 * \bug Should be a non-blocking operation. \bug Should allow a subset of places. \bug Should be
 * distinguishable from other concurrent barriers in the system.
 */
X10RT_C void x10rt_barrier (void);


/** Automatically configure a CUDA kernel.  By studying the characteristics of the hardware upon
 * which the kernel will be executed, and the kernel itself, we can traverse a list of supported
 * configurations to find the first one that 'fits'.  If the configurations are listed in order of
 * preference then this will select the 'best' configuration for the given hardware and software.
 *
 * \param d The GPU where the kernel will be executed.
 *
 * \param type The message type that the kernel is registered to process.
 *
 * \param dyn_shm The amount of dynamic ``shared'' memory that will be used by the kernel.
 *
 * \param blocks The number of blocks to use will be written back here.
 *
 * \param threads The number of threads to use will be written back here.
 *
 * \param cfg Pointer to the first element of an array that contains pairs of (blocks,threads) to
 * consider, terminated with a (0,0) pair.  For example, {8,512, 8,256, 8,128, 0,0}.
 */
X10RT_C void x10rt_blocks_threads (x10rt_place d, x10rt_msg_type type, int dyn_shm,
                                   int *blocks, int *threads, const int *cfg);

/** Handle outstanding incoming messages.  A place should periodically call the following function
 * to handle outstanding messages that have arrived since the last call.
 *
 * The X10RT implementation should call back into X10 via the appropriate receivers for each pending
 * message, registered in the early stages of execution. Note that the library should be fully
 * re-entrant as the receivers may be executing arbitrary X10 code and thus may call back into the
 * X10RT implementation.
 *
 * \li For a message sent with #x10rt_send_msg the callback is simply executed on the remote side
 * with the x10rt_msg_params structure filled in with appropriate copies of the data.
 *
 * \li For a message sent with #x10rt_send_get the first callback will return a local pointer on the
 * remote side, from which the bytes should be read and transferred back, over the network. After
 * the data has arrived at the initiating place, the second callback should be executed at the
 * initiating place. The len parameter of the second callback is the size of the copied buffer,
 * which may be different to p.len.
 *
 * \li For a message sent with #x10rt_send_put the first callback will return a pointer on the
 * remote side into which the bytes should be received from the network. After the data has arrived
 * at the destination, the second callback should be executed at the destination. The len parameter
 * of the first and second callbacks is the size of the copied buffer, which may be different to
 * p.len.
 *
 * The X10RT implementation may supply an a argument of #x10rt_msg_params to a callback with the
 * length field set to a higher value than was originally supplied by the x10rt_send*_ functions.
 * This is useful when implementing X10RT on top of network layers that have constraints on the size
 * of a packet. However, changing the additional len parameter involved in the get and put
 * operations is not allowed.
 *
 * In all cases X10RT is responsible for freeing the buffer within the x10rt_msg_params structure
 * after the callbacks have used it.
 *
 * \see \ref callbacks
 */
X10RT_C void x10rt_probe (void);

/** \} */

// vim: tabstop=4:shiftwidth=4:expandtab:textwidth=100
