/**  copyright (c) 2006 IBM
 * System-dependent common functionality
 *
 * #include "system.h" <BR>
 * -llib 
 *
 * This file and others in this sub-directory encapsulate
 * system-dependent funtionality. This should hopefully enable usage
 * of other runtime systems if needed. To compile this folder the
 * runtime environment needs to be defined in terms of pre-processor
 * symbols. These define the source files that would be compiler and
 * linked. 
 *  
 * @see something
 * @author Sriram Krishnamoorthy 
 */

#ifndef x10lib_system_h
#define x10lib_system_h

namespace x10lib
{
  class BaseMutex; //forward declaration
  namespace system
  {
    /**The ID for this node in the SPMD environment. This is equal to
     * the process ID in an linear ordering of processes in the SPMD
     * execution environm,ent. In general, there could be more than
     * one process per SMP node or processor.  
     * @return Node id
     */
    int NodeId();

    /**The number of nodes in the SPMD environment. This is equal to
     * the number of processes in the execution environment, and need
     * not related to the number of processors or SMP nodes in the
     * system.
     * @return Number of nodes
     */
    int NumNodes();

    /**Initialize the SPMD execution environment.
     */
    void Init(int argc, char *argv[]);

    /**Finalize the SPMD execution environment.
     */
    void Finalize();

    /**Create a mutex. The returned mutex is a platform-specific
     * sub-class of BaseMutex.
     */
    BaseMutex* CreateMutex();

    /**Active method type
     */
    typedef void (*am_t)(void* buf,    /**<Buffer containing data*/
			 int   nbytes, /**<sizeof(buffer)*/
			 int   from    /**<Node initiating this am*/
			 );
    
    /**Register an active method to be invoked on a remote node. Once
     * registered, an active method can be called at a remote
     * node. Note that this method should be invoked by all processes
     * unconditionally and in the same order.   
     * 
     * Active methods are required to be thread-safe. They cannot
     * block indefinitely. Also result of invocation of active method
     * by a place to itself might result in undefined behaviour.
     * @param am active method
     */
    void RegisterRemoteMethod(am_t am);

    /**Deregister a registered active method. Should be invoked by
     * all processes in the same order.
     * @param Registered active method
     */
    void DeregisterRemoteMethod(am_t am);

    /**Invoke a register active method. As of now, we require all
     * registrations and a synchronization to occur before any
     * invocation.   
     * @param am    Registered active method
     * @param buf   Pointer to buffer containing the arguments
     * @param bytes Size of the buffer
     * @param dest  The node at which am is to be invoked
     */
    void InvokeRemoteMethod(am_t am, void *buf, int bytes, int dest);

  } //namespace system

} //namespace x10lib


#endif  // x10lib_system_h

