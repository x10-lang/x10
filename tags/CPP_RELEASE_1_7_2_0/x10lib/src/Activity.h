/**  copyright (c) 2006 IBM
 * Abstract class for activities.
 *
 * #include "Activity.h" <BR>
 * -llib 
 *
 * A longer description.
 *  
 * @see something
 * @author Sriram Krishnamoorthy 
 */

#ifndef x10lib_Activity_h
#define x10lib_Activity_h

#include "x10base.h"      //Definition of Place
#include "SltType.h"

namespace x10lib
{
  class ActivityMaker;
  class FinishRecord;

  typedef enum ProbeStatusType { 
    ACTIVITY_DONE,          /**<Activity is done processing, and can
			       be cleaned up*/   
    ACTIVITY_CLOCK_WAITING, /**<Activity is waiting on a clock*/
    ACTIVITY_FINISH_WAITING /**<Activity is waiting on a finish*/
  } ProbeStatusType;
  
  class Activity
    {
    public:
      // LIFECYCLE
      
      /**No default constructor*/

      /** Only constructor.
       * @param rFinishRecord The finish record that manages the
       * relevant fnish scope. 
       * @param spawner The place from which thie activity was
       * originally spawned.  
       */
      Activity(FinishRecord& rFinishRecord,
	       Place         spawner);

      /** Destructor.*/
      virtual ~Activity();


      // OPERATORS
      // OPERATIONS                       

      /**Spawn an activity with the given activity maker at the
       * specified place using the same finish scope as the invoking
       * activity. 
       * @param maker The activity maker
       * @param place The place at which the activity is to be spawned
       */
      void spawn(ActivityMaker& maker, 
		 Place          place);

      /**Function implementing the core method in the activity. 
       */
      virtual ProbeStatusType run()=0;

      // ACCESS
      // INQUIRY
      
    protected:
      SltType&         Slt();
    private:
      SltType          mSlt;           /**< Stackless thread state*/
      FinishRecord&    mrFinishRecord; /*<The local finish record for
					 this activity*/
      Place            mSpawner;       /*<The place from which this
					 activity was spawned. */
    };

// INLINE METHODS
//

// EXTERNAL REFERENCES
//

} //namespace x10lib

#endif  // x10lib_Activity_h


