/**  copyright (c) 2006 IBM
 * Singleton to manage activities spawned at a given place. There is
 * one such object for each place.  
 *
 * #include "ActivityManager.h" <BR>
 * -llib 
 *
 * A longer description.
 *  
 * @see something
 * @author Sriram Krishnamoorthy 
 */

#ifndef x10lib_ActivityManager_h
#define x10lib_ActivityManager_h

#include <list>

// SYSTEM INCLUDES
//

#include "x10base.h"
#include "system.h"
// PROJECT INCLUDES
//

// LOCAL INCLUDES
//

// FORWARD REFERENCES
//

namespace x10lib
{
  class ActivityMaker;
  class Activity;
  class BaseMutex;
  class NonThreadSafeInvokable;

  class ActivityManager
    {
    public:
      // LIFECYCLE
      
      /** Default constructor is private. 
       * No copy constructor.
       * Using default destructor
       */

      /**Initialize the singleton object.*/
      static void Init();
      
      /**Destroy the singleton object and release resources*/
      static void Finalize();

      /**Access the singleton object*/
      static ActivityManager& rInstance();


      // OPERATORS
      
      /**No assignment operator*/

      // OPERATIONS                       

      /**Process an activity and its descendents. This is a collective
       * operations involving the activity managers at all places. The
       * activity encapsulated by the activity maker is constructed at
       * place==0 within a new finish scope. The processing is
       * complete when global termination is detected on this finish
       * scope. 
       * @param maker The maker of the activity to be processed
       */
      void Process(ActivityMaker& maker);
      
      /**Invoke the SpawnActivityAm active method on remote place
       * dest.
       * @param maker The maker of the activity to be spawned
       * @param dest  The place at which the activity is to be spawned 
       */ 
      void InvokeSpawnActivity(ActivityMaker& maker,
			       Place          dest);

      /**Add an activity to be executed at the given place. This
       * should be a dynamically allocated, newly created
       * activity. The activity manager takes over the task of deleting
       * it on completion of the activity.  
       */
      void AddActivity(Activity* act);

      /**Add a NonThreadSafeInvokable object to be executed later. The
       * ActivityManager takes the responsibility of deleting the
       * object after it is invoked. This method is invoked by an active
       * method and not directly by the user. 
       * @param obj The invokable object to be added
       */
      void AddNtsInvokableObject(NonThreadSafeInvokable* obj);

      // ACCESS
      // INQUIRY
      
    protected:
    private:

      /**Default constructor*/
      ActivityManager();

      /**Default destructor*/
      ~ActivityManager();

      /**Signal global termination to all the places. This method must
       * be invoked at here()==0 (the creator of the finish scope
       * enclosing the first activity) on detection of global
       * termination. 
       */
      void SignalGlobalTermination();

      /**Active method invoked from a remote node, to notify
       * termination of Process(). This method does not use any of its
       * arguments. They are specified to match the active method (am_t)
       * prototype.
       * @param buf    Buffer containing the arguments
       * @param nbytes Size of buf in bytes
       * @param from   The place from which this active method was invcked
       */
      static void NotifyTerminationAm(void* buf, 
				      int   nbytes,
				      Place from);

      /**Active method invoked from a remote node, to spawn an
       * activity. The buffer contains an ActivityMaker object.
       * @param buf    An ActivityMaker object
       * @param nbytes Size of the buffer in bytes
       * @param from   The place from which this active method was
       * invoked 
       */
      static void SpawnActivityAm(void* buf,
				  int   nbytes,
				  Place from);

      /**Check whether all activities within Process() have globally
       * terminated. This is true on detection of global termination
       * at here()==0 or on receipt of signal from here()==0 of the
       * same. 
       * @return true on global termination 
       */
      bool HasTerminated() const;

#if 0
      /**Type to enqueue maker objects till the activities are
	 constructed. */
      typedef struct MakerStructType 
      {
	ActivityMaker *mpMaker; /**<ActivityMaker object*/
	Place          mSpawner;/**<Spawned from this place*/

	/**Default constructor*/
	MakerStructType(ActivityMaker *maker,
			Place          spawner)
	  : mpMaker(maker),
	     mSpawner(spawner)
	{ //VOID
	}
      } MakerStructType;
#endif

      static ActivityManager*    mpObject; /**<The singleton object*/  
       
      volatile bool              mHasTerminated; /**<true on detection
						    of global
						    termination*/ 
      std::list<Activity *>      mReadyList; /**<List of activities
						ready to run*/ 
      std::list<NonThreadSafeInvokable*> mNtsLists[2]; /**<Lists
							  operating in
							  a twin-log fashion*/
      int                        mCurNtsListIdx; /**<Current list
						    among the two to
						    insert
						    NtsInvokable objects*/

      BaseMutex*                 mpNtsListMutex; /**<Mutex to modify
						    mNtsLists[mCurNtsListIdx]*/ 
      
    }; //class ActivityManager
  
  // INLINE METHODS
  //
  
  // EXTERNAL REFERENCES
  //
} //namespace x10lib

#endif  // x10lib_ActivityManager_h

