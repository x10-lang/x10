/**  copyright (c) 2006 IBM
 * Interface mutex local to a place.
 *
 * #include "Mutex.h" <BR>
 * -llib 
 *
 * This mutex cannot be used to synchronize between places. This is
 * also not a mutex to be used to synchronize between activities. This
 * is primarily for use to provide thread-safety in the presence of
 * multiple physical threads. Different runtime systems instantiate
 * this functionality using the respective platform-specific
 * support. 
 *
 * @see 
 * @author Sriram Krishnamoorthy 
 */

#ifndef x10lib_BaseMutex_h
#define x10lib_BaseMutex_h

namespace x10lib
{
  class BaseMutex
  {
  public:
    // OPERATORS
    // OPERATIONS                       
    
    /**Obtained lock on the mutex. This is a blocking call. Locking
     * an already locked mutex will result in a deadlock. 
     */
    virtual void Lock()=0;
    
    /**Try to obtain lock on the mutex. This is a non-blocking
     * call. Trying to lock an already locked mutex will return
     * false.
     * @return true if the mutex was obtained in this try. false
     * otherwise.  
     */ 
    virtual bool TryLock()=0;

    /**Unlock the mutex.*/
    virtual void Unlock() = 0;
    
    // ACCESS
    // INQUIRY
    
  protected:
  private:
  }; //class BaseMutex

// INLINE METHODS
//

// EXTERNAL REFERENCES
//

} //namespace x10lib

#endif  // x10lib_BaseMutex_h


