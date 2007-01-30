/**  copyright (c) 2006 IBM
 * Mutex local to a place, based on pthreads
 *
 * #include "PthreadMutex.h" <BR>
 * -llib 
 *
 * This implements the mutex funtionality using pthread mutexes.
 *  
 * @see BaseMutex.h for details on the interface
 * @see pthreads for details on the pthreads mutex
 * @author Sriram Krishnamoorthy 
 */

#ifndef x10lib_PthreadMutex_h
#define x10lib_PthreadMutex_h

#include "BaseMutex.h" //inherits from

#include <pthread.h>   //pthread_mutex_*
#include <errno.h>     //checking return values
#include <assert.h>

namespace x10lib
{
  class PthreadMutex : public BaseMutex
  {
  public:
    // LIFECYCLE
    
    /** Default constructor.
     */
    PthreadMutex(void);
    
    /** Destructor.
     */
    ~PthreadMutex(void);
    
    // OPERATORS
    // OPERATIONS                       
    
    /**Obtained lock on the mutex. This is a blocking call. Locking
     * an already locked mutex will result in a deadlock. 
     */
    void Lock();
    
    /**Try to obtain lock on the mutex. This is a non-blocking
     * call. Trying to lock an already locked mutex will return
     * false.
     * @return true if the mutex was obtained in this try. false
     * otherwise.  
     */ 
    bool TryLock();
    
    /**Unlock the mutex.*/
    void Unlock();
    
    // ACCESS
    // INQUIRY
    
  protected:
  private:
    pthread_mutex_t mMutex; /**<The pthread mutex object*/
  }; //class PthreadMutex
  
  // INLINE METHODS
  //
  
  // EXTERNAL REFERENCES
  //

} //namespace x10lib

#endif  // x10lib_PthreadMutex_h


