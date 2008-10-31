/**  copyright (c) 2006 IBM
 * Implementation of the FinishRecord interface to for termination
 * detection using the Dijkstra-Scholten termination detection
 * algorithm.   
 * 
 * #include "DsFinishRecord.h" <BR>
 * -llib 
 *
 * Termination
 * is detected using the Dijkstra-Scholten algorithm. The finish
 * records form a tree, with the root at the place at which the finish
 * scope was created. Messages for the termination detection algorithm are sent
 * only when no activities is executing for the finish scope at the
 * current place (i.e., only on local termination). 
 *
 * :NOTE: DsFinishRecord should always be allocated on the heap. The
 * method Cleanup()'s implementation requires that. 
 *  
 * @see  Dijkstra-Scholten termination detection algorithm
 * @author Sriram Krishnamoorthy 
 */

#ifndef x10lib_DsFinishRecord_h
#define x10lib_DsFinishRecord_h

#include <map>

#include "x10base.h"      //Definition of Place
#include "FinishScope.h"  //Managed by finish record
#include "FinishRecord.h" //Inherited from

namespace x10lib
{
  class DsFinishRecord : public FinishRecord
    {
    public:
      // LIFECYCLE

      /** No Default constructor.*/

      /**Creates a finish record for a finish scope.
       * @param scope The finish scope for this finish record
       * @param parent The place containing the  parent of this finish
       * record. This record is the root of the tree if parent==here()
       */
      DsFinishRecord (const FinishScope& scope, 
		      Place              parent);

      /**Initializer of the protocol utilized. This might involve
       * registering active messages, etc. that is common to all
       * instances. This method is guaranteed to be invoked by all
       * places before any instance is created. 
       */
      static void Init();


      /**The finalizer for the protocol. No instances can be created
       * or instance operations performed after the invocation of this
       * method. 
       */
      static void Finalize();

      /** No copy constructor*/

      /** Destructor.
       */
      ~DsFinishRecord(void);


      /**No assignment operator*/

      // OPERATIONS                       
      /**Report creation of a local activity. This is done whenever an
       * activity 
       * is created at the current place with the finish scope managed
       * by this  finish record. 
       * @param spawner The place spawning this activity.
       */
      void LocalActivitySpawned(Place spawner);

      /**Resport destruction of a local activity. This is done
       * whenever an activity 
       * is destroyed at the current place with the finish scope
       * managed by this finish record. 
       * @param spawner The place spawning this activity.
       */
      void LocalActivityDestroyed(Place spawner);

      /**Report spawning of an activity at a remote place. Note that
       * the creation of a local activity is reported only when the
       * Activity object is created. Spawning of remote activities
       * does not involve creation of of an Activity object. 
       * @param target The place at which the activity is spawned. */
      void RemoteActivitySpawned(Place target);

      // ACCESS
      // INQUIRY
      /**Check if this finish scope has terminated. This check is only
       * valid for the root finish record.
       * @return true is the finish scope has terminated. false
       * otherwise. 
       */
      bool HasTerminated() const;

      /**Access the finish scope managed by this finish record.
       * @return The managed finish scope
       */
      FinishScope Scope() const;
      
    protected:
    private:
      /**Report completion of activities spawned from this place at a
       * remote place.
       * @param target Completion of activities spawned at this place
       * are acknowleged.  
       * @param count #activities acknowledged as completed
      */ 
      void RemoteActivityAck(Place target, 
			     int   count);

      /**Is the finish record the root for this finish scope (in terms
       * of the termination detection algorithm).
       * @return true if it is the root. false otherwise
       */
      bool IsRoot() const;      

      /**Cleanup this finish record. Once a finish record has
       * determined local termination and no outgoing spawned
       * activities that are yet to be acknowledged, it cleans itself
       *up using this method after any taking necessary action. 
       */
      void Cleanup();

      /**Active method to notify completion of activities spawned by
       * this node at a remote node. 
       * @param buf    Contains (FinishScope,#completions notified)
       * @param nbytes Size of buf in bytes
       * @param from   Place from which this am was invoked
       */
      static void NotifyActivityCompletionAm(void* buf,
					     int   nbytes,
					     Place from);

      /**Invoke NotifyActivityCompletionAn active method.
       * @param fs     The finish scope notified
       * @param parent The place to which notification is sent
       * @param count  #activities completions notified
       */
      static void InvokeNotifyActivityCompletion(FinishScope fs,
						 Place       dest,
						 int         count);


      /**Acknowledge all places that have spawned activities at this
       * place, except the parent of this finish record. 
       */
      void AckNonParentSpawners();

      /**Acknowledge the parent of this finish record, the completion
       * of all activities spawned by it at the current place.
       */ 
      void AckParent();

      FinishScope mScope;  /*<The finish scope being managed.*/
      Place       mParent; /*<Parent of this finish record in the tree
			     formed*/
      int         mLocalActiveCnt; /*<Number of local activities
				     executing in this place with this
				     finish scope.*/
      std::map<Place,int> mIncomingSpawnsMap; /*<The places that
						spawned activities at
						this place and the
						corresponding counts.*/ 
      int         mOutgoingSpawnsCnt; /*<Number of remote spawned
					activities that have not been
					acknowledged. */

      friend class DsActivityCompletion; /**<Friend class to invoke a
					    non-thread-safe function
					    to ack completion of
					    activities*/   
    }; //class DsFinishRecord
} //namespace x10lib

// INLINE METHODS
//

// EXTERNAL REFERENCES
//

#endif  // x10lib_DsFinishRecord_h

