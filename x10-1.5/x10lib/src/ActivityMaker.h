/**  copyright (c) 2006 IBM
 * Serializable object that can make an activity. 
 *
 * #include "ActivityMaker.h" <BR>
 * -llib 
 *
 * This is an abstract base class which has been derived to define new
 * activity makers.  
 *  
 * @see something
 * @author Sriram Krishnamoorthy 
 */

#ifndef x10lib_ActivityMaker_h
#define x10lib_ActivityMaker_h

#include <boost/archive/binary_iarchive.hpp>
#include <boost/archive/binary_oarchive.hpp>
#include <boost/serialization/access.hpp>
#include <boost/serialization/base_object.hpp>
#include <boost/serialization/export.hpp>

#include "FinishScope.h"
#include "NonThreadSafeInvokable.h"

namespace x10lib 
{
  class Activity;
  class FinishRecord;

  class ActivityMaker : public NonThreadSafeInvokable
    {
    public:
      // LIFECYCLE
      
      /**Default constructor is private.*/

      /**Only public constructor*/
      ActivityMaker(Place spawner);
      
      /** Destructor.
       */
      ~ActivityMaker();
      
      // OPERATORS
      // OPERATIONS                       

      /**Set the finish scope.
       * @param rScope Reference to the finish scope
       */
      void Scope(const FinishScope &rScope);

      /**Retrieve the finish scope.
       * @return The finish scope
       */
      FinishScope Scope() const;

      /** The Make() interface to make an activity.
       * @param fr The finish record managing the finish scope
       * @param spawner The place that initiated the activity maker
       * that spawned this activity.
       * @return Pointer to newly created activity
       */
      virtual Activity* Make(FinishRecord& fr,
			     Place         spawner)=0;

      /**Implementation of the NonThreadSafeInvokable interface. Just
       * calls Make() and puts the returned Activity in the act5ivity
       * queue. 
       */
      void Invoke();

      // ACCESS
      // INQUIRY
      
    protected:
    private:
      /** Default constructor is private to enable serialization.
       */
      ActivityMaker();

      FinishScope mScope; /*<Finish scope for the activity to be spawned*/
      Place       mSpawner;

      friend class boost::serialization::access;
      template<class Archive>
	void serialize(Archive&     ar, 
		       unsigned int version) {
	ar & boost::serialization::base_object<NonThreadSafeInvokable>(*this);
	ar & mScope & mSpawner;
      }      
    }; //class ActivityMaker
} //namespace x10lib


#endif  // x10lib_ActivityMaker_h

