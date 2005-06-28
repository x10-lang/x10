package x10.runtime;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.io.*;
import java.lang.reflect.*;

import x10.array.sharedmemory.Distribution_c;
import x10.base.MemoryBlockSafeBooleanArray;
import x10.base.MemoryBlockSafeByteArray;
import x10.base.MemoryBlockSafeCharArray;
import x10.base.MemoryBlockSafeDoubleArray;
import x10.base.MemoryBlockSafeFloatArray;
import x10.base.MemoryBlockSafeIntArray;
import x10.base.MemoryBlockSafeLongArray;
import x10.base.MemoryBlockSafeObjectArray;
import x10.base.MemoryBlockSafeShortArray;
import x10.lang.dist;
import x10.lang.region;
import x10.lang.Future;
import x10.runtime.ElementType;


/**
 * A RemotePlace (no _c because different javah programs tend
 * to tread _ characters in file names differently) is an object
 * on this VM that represents a LocalPlace_c object on some other
 * VM.
 *
 * @author Allan Kielstra
 **/

// this file is under construction....note that runFuture has not been
// started
public class RemotePlace extends Place {
   public native long runAsync_(final Activity a, boolean isAFuture);
   public void runAsync(final Activity a) {
       Thread currentThread = Thread.currentThread();  
       if (currentThread instanceof ActivityRunner) {
           Activity parent = ((ActivityRunner) currentThread).getActivity();
           parent.finalizeActivitySpawn(a);
       } else {
           throw new Error("remote task invoked by non X10 thread");
       }
       
       a.initializeActivity();
       // this is now a surrogate for the remote activity
       a.activityAsSeenByInvokingVM = Activity.thisActivityIsASurrogate;
       a.placeWhereRealActivityIsRunning = this;
       a.pseudoSerialize();
       // Yes, it would be better OO style to have runAync_ check
       // the instanceof.  But that would mean even more JNI
       a.globalRefAddr = runAsync_(a, a instanceof Activity.Expr);

   }
   
   /**
    * @author donawa
    * Create an array of the specified type at the remote place
    *
    */
   // FIXME: this is a work in progress--had to make static because typecast from Place->RemotePlace
   // fails since not all pieces are coordinated
  
   public /*REMOVE static */static void runArrayConstructor(FatPointer owningObject,int elementType,
   		int elementSize,dist d,boolean safe,boolean mutable,
		/*FIXME: remove once not static */int placeId){
   	int i;
   	region theRegion = d.region;
   	
   	System.out.println("Creating storage at place "+placeId);
   	
   	final int numberDimensions = theRegion.rank;
   
   	
   	region theRegions[] = ((Distribution_c)d).getAllocatedRegions();
	
   	System.out.println("Size of local storage for "+theRegions[placeId]+" :"+elementSize+"*"+theRegions[placeId].size());
	System.out.println("Need to create a distribution:");
   	// Need to create an array of ranges
   	for(i=0;i < numberDimensions;++i){
   		System.out.println("Dim["+i+"] range:"+theRegion.rank(i).low()+".."+theRegion.rank(i).high());
   	}
   	String distType="unknown";
   	switch(d.distributionType){
   	case dist.BLOCK: distType="Block";break;
   	case dist.BLOCK_CYCLIC: distType = "BlockCyclic";break;
   	case dist.CONSTANT: distType = "Constant";break;
   	case dist.CYCLIC: distType = "Cyclic";break;
   	case dist.UNIQUE: distType = "Unique";break;
   	}
   System.out.println("Distrbution type:"+distType+" cyclicValue:"+d.cyclicValue);
   
    
    System.out.println(" ----------------");
   }
   
   RemotePlace(int vm_, int place_no_) {
      super(vm_, place_no_);
   }
   public Future runFuture(final Activity.Expr a) {
      Future_c result = a.future = new Future_c();
      runAsync(a);
      return result;
   }
   public native void shutdown_();
   public void shutdown() {
      if (!Configuration.VM_[vm_].hasShutdownMsgBeenSent) {
         shutdown_();
         Configuration.VM_[vm_].hasShutdownMsgBeenSent = true;
      }
   }
}
