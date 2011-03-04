/**  copyright (c) 2006 IBM
 * ARMCI-based implementation of the system-dependent functionality. 
 *
 * The implementation provides a wrapper around ARMCI GPC
 * mechanism. Implications of this ARMCI implementation on
 * thread-safety:  
 * - ActivityMaker and its subclass's constructor needs to be
 * thread-safe. They cannot performance any communication events
 * (send/recv/wait on commn.)
 *
 * @author Sriram Krishnamoorthy
 */

#include "system.h" //namespace implemented
#include "mpi.h"
#include "armci.h"
#include "gpc.h"
#include <iostream>

extern "C" 
{
#  include "mpi.h"
}

#include <map>

namespace x10lib
{
  namespace system
  {
    int NodeId()
    {
      int rank;
      MPI_Comm_rank(MPI_COMM_WORLD, &rank);
      return rank;
    } //NodeId

    int NumNodes()
    {
      int size;
      MPI_Comm_size(MPI_COMM_WORLD, &size);
      return size;
    } //NumNodes

    static int sGpcAmHandle=0; /**<GPC handle to invoke AMs*/
    
    static std::map<am_t,int> sAmHandleMap;   /**<Map (active methods->AM handles)*/
    static std::map<int,am_t> sAmHandleRevMap;/**<The reverse map of sAmhandleMap*/
    static int                sAmHandleCtr=0; /**<A counter to assign am handles*/


    static int GpcAmHandler(int to, int from, void *hdr,   int hlen,
			    void *data,  int dlen,
			    void *rhdr,  int rhlen, int *rhsize,
			    void *rdata, int rdlen, int *rdsize,
			    int rtype)
    {
      assert(hlen == sizeof(int));
      int am_handle = *(int*)hdr;
      assert(sAmHandleRevMap.find(am_handle) != sAmHandleRevMap.end());

      am_t am   = sAmHandleRevMap[am_handle];
      void* buf = data;
      int bytes = dlen;

      (*am)(data, bytes, from);

      *rhsize = 0;
      *rdsize = sizeof(int);
      *(int *)rdata = GPC_DONE;
      return GPC_DONE;
    }

    /**Typecast to override C++ type-checking and work with the GPC types.
     * Might not be needed if GPC implementation is changed or the typedef
     * is put inside gpc.h in ARMCI. 
     */ 
    typedef int (*GPC_HANDLER_T)();

    void Init(int argc, char *argv[])
    { 
      MPI_Init(&argc, &argv);
      ARMCI_Init();
      sGpcAmHandle = ARMCI_Gpc_register((GPC_HANDLER_T)GpcAmHandler);
    } //Init

    void Finalize()
    { 
      ARMCI_Gpc_release(sGpcAmHandle);
      ARMCI_Finalize();
      MPI_Finalize();
    } //Finalize

    /**The implementation of active methods maintains a local registry
     * for mapping of active method pointers to integers and
     * vice-versa. Note that the integers assigned to the active
     * methods should match at all the processes. This can be achieved
     * by registering the active methods in the same order in all the
     * processes.
     */
    void RegisterRemoteMethod(am_t am)
    {
      sAmHandleMap[am]              = sAmHandleCtr;
      sAmHandleRevMap[sAmHandleCtr] = am;
      sAmHandleCtr                 += 1;
    } //RegisterRemoteMethod

    void DeregisterRemoteMethod(am_t am) 
    {
      int handle              = sAmHandleMap[am];
      sAmHandleMap[am]        = -1;
      sAmHandleRevMap[handle] = NULL;
    } //DeregisterRemoteMethod

    /**
     * :KLUDGE: The method used to obtained this length is named
     * different in the header file (gpc.h)  than in the source file
     * (gpc.c). Hence, the code would not link if the function
     * specified in the header file is used. So declaring the function 
     * down below.   
     *
     */
    extern "C" { int ARMCI_Gpc_get_dlen(); }

    /**Invoking an active method at a remote process translates
     * invoking a default GPC handler at that process by passing it
     * the integer corresponding to the active method and the relevant
     * arguments. The active method is then invoked by the GPC
     * handler. Note that the invoked method must have been registered
     * both locally and the remote node (at the least) before it is
     * called. Since the process of registration and deregistration is
     * non-collective, a synchronization is required between the
     * processes after both have registered the active method, before
     * the active method can be invoked. 
     *
     * ARMCI GPC calls return a result to the caller. The buffers
     * passed cannot be reused (technically) until the return value
     * has arrived. Since the higher-level layers require a pure
     * one-sided calling semantics (they just spawn/call a method on
     * the remote node without expecting a return value), a dummy
     * acknowledged by the GPC handler which is waited upon here. Note
     * that this can be changed when GPC supports PUT-type method
     * calls with no return values.
     * 
     * :FIXME: GPC calls allow a certain maximum size in the data that
     * can be sent in them. When mroe data needs to be sent in a GPC
     * call, the user is expected to handle fragmentation and
     * reassembly (including buffer allocation at the remote
     * node). For small and medium sized messages (upto about 64KB I
     * think -- not part of GPC spec) there is no such overhead. For
     * larger messages, additional work is required. This is not
     * implemented currently in the code. It just asserts that the
     * size is small enough.
     *
     * :WARNING: In general, the map and reverse map must be accessed
     * through a mutex so as to ensure thread-safety when both the main
     * thread and the GPC handler are accessing it. I am not sure on
     * the performance penalty on this. We now punt on this and
     * require that all registrations and a barrier (or some
     * synchronization) occur before all invocations. Whenever this
     * restrictions is relaxed, this issue of locking needs to be
     * handled. 
     */
    void InvokeRemoteMethod(am_t   am, 
			    void*  buf, 
			    int    bytes, 
			    int    dest) 
    {
      assert(dest != NodeId());
      assert(sAmHandleMap.find(am) != sAmHandleMap.end());

      int shdr    = sAmHandleMap[am];
      int shlen   = sizeof(shdr);
      void *sdata = buf;
      int sdlen   = bytes;

      void* rhdr  = NULL;
      int rhlen   = 0;
      int rdata;
      int rdlen   = sizeof(rdata);

      gpc_hdl_t nbh;

      ARMCI_Gpc_init_handle(&nbh);

      if(sdlen >= ARMCI_Gpc_get_dlen()) {
	std::cerr<<NodeId()<<"::Attempting to send a very large GPC. Not yet implemented splitting and combining"<<std::endl;
	//TheX10().abort();
	assert(0);
      }

//       std::cerr<<"Invoking gpc_exec to dest="<<dest<<std::endl;
       ARMCI_Gpc_exec(sGpcAmHandle, dest,
 		     &shdr, shlen, sdata, sdlen,
 		     rhdr, rhlen, &rdata, rdlen, &nbh);
//       std::cerr<<"Done invoking gpc_exec to dest="<<dest<<std::endl;
      
      ARMCI_Gpc_wait(&nbh);
//       std::cerr<<"Done waiting gpc_exec to dest="<<dest<<std::endl;
    } //InvokeRemoteMethod

  } //namespace system
} //namespace x10lib

