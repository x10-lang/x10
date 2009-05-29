/**  copyright (c) 2006 IBM
 * Finish record to track the status of a FinishScope at a given place. 
 *
 * #include "FinishRecord.h" <BR>
 * -llib 
 *
 * One finish record is created at a place for every known finish
 * scope at a given place. All the finish records for a given finish
 * scope co-ordinate termination detection for that scope. This class
 * defines an interface that is implemented to provide termination
 * detection. Any activity at a place registers(deregisters)
 * with the finish record for the relevant finish scope at the local
 * place, enabling detection of local termination. 
 *
 * @author Sriram Krishnamoorthy 
 */

#ifndef x10lib_FinishRecord_h
#define x10lib_FinishRecord_h


namespace x10lib
{
  class FinishRecord
    {
    public:
      // LIFECYCLE
      
      // OPERATIONS             

      /**Report creation of a local activity. This is done whenever an
       * activity 
       * is created at the current place with the finish scope managed
       * by this  finish record. 
       * @param spawner The place spawning this activity.
       */
      virtual void LocalActivitySpawned(Place spawner) = 0;

      /**Resport destruction of a local activity. This is done
       * whenever an activity 
       * is destroyed at the current place with the finish scope
       * managed by this finish record. 
       * @param spawner The place spawning this activity.
       */
      virtual void LocalActivityDestroyed(Place spawner) = 0;

      /**Report spawning of an activity at a remote place. Note that
       * the creation of a local activity is reported only when the
       * Activity object is created. Spawning of remote activities
       * does not involve creation of of an Activity object. 
       * @param target The place at which the activity is spawned. */
      virtual void RemoteActivitySpawned(Place target)=0;

      // ACCESS
      // INQUIRY

      /**Check if this finish scope has terminated. This check is only
       * valid for the root finish record.
       * @return true is the finish scope has terminated. false
       * otherwise. 
       */
      virtual bool HasTerminated() const = 0;

      /**Returns the enclosed finish scope.
       */
      virtual FinishScope Scope() const = 0;

    protected:
    private:
}; //class FinishRecord

// INLINE METHODS
//

// EXTERNAL REFERENCES
//

} //namespace x10lib

#endif  // x10lib_FinishRecord_h


