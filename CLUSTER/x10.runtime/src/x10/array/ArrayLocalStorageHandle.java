package x10.array;
import java.util.Iterator;
import x10.lang.region;
import x10.lang.x10Array;
import x10.lang.Runtime;
import x10.runtime.distributed.FatPointer;
import x10.lang.point;
import x10.lang.place;
import x10.lang.floatArray;
import x10.lang.doubleArray;
import x10.runtime.distributed.DLocalPlace_c;
import x10.runtime.distributed.DeserializerBuffer;
import x10.runtime.distributed.SerializerBuffer;
import x10.runtime.distributed.DistributedRuntime;
import x10.array.sharedmemory.IntArray_c;
import x10.array.sharedmemory.DoubleArray_c;
import x10.array.sharedmemory.ShortArray_c;
import x10.array.sharedmemory.LongArray_c;
import x10.array.sharedmemory.FloatArray_c;
import x10.array.sharedmemory.CharArray_c;
import x10.array.sharedmemory.ByteArray_c;
import x10.array.sharedmemory.BooleanArray_c;

public class ArrayLocalStorageHandle   {
    x10Array _destArray;
    x10Array _srcArray;
    int _startIndex;
    int _endIndex;
    region _localRegion;
   
    // facilitate transfer of local data to equivalent block of memory
    // 
    public ArrayLocalStorageHandle(x10Array destArray,x10Array srcArray,int start,int end) {
       _destArray = destArray;
       _srcArray = srcArray;
       _localRegion = _srcArray.getDistribution().restrictToRegion(Runtime.here());
       _startIndex=start;
       _endIndex = end;
    }

    public ArrayLocalStorageHandle(){}
   
    // format: <fp><dump of local array>
    public void serialize(SerializerBuffer outputBuffer){
     int count = _localRegion.size();

     FatPointer dest_fp = DistributedRuntime.registerGlobalObject(_destArray);
     
     dest_fp.serialize(outputBuffer);
     _srcArray.SerializeLocalData(_startIndex,_endIndex,outputBuffer);
   }

   // a nop in distributed mode
   public void transferDataToArray(){
      if(x10.runtime.Configuration.isMultiNodeVM()) return;

      region localRegion = _destArray.getDistribution().restrictToRegion(Runtime.here());

      if(_destArray instanceof IntArray_c){
	  IntArray_c src = (IntArray_c) _srcArray;
	  IntArray_c dest = (IntArray_c) _destArray;
	  for (Iterator it = localRegion.iterator();it.hasNext();){
	      final point p = (point)it.next();
	      dest.set(src.get(p),p);
	  }
      }
      else if(_destArray instanceof FloatArray_c){
	  FloatArray_c src = (FloatArray_c) _srcArray;
	  FloatArray_c dest = (FloatArray_c) _destArray;
	  for (Iterator it = localRegion.iterator();it.hasNext();){
	      final point p = (point)it.next();
	      dest.set(src.get(p),p);
	  }
      }
      else if(_destArray instanceof DoubleArray_c){
	  DoubleArray_c src = (DoubleArray_c) _srcArray;
	  DoubleArray_c dest = (DoubleArray_c) _destArray;
	  for (Iterator it = localRegion.iterator();it.hasNext();){
	      final point p = (point)it.next();
	      dest.set(src.get(p),p);
	  }
      }
      else if(_destArray instanceof LongArray_c){
	  LongArray_c src = (LongArray_c) _srcArray;
	  LongArray_c dest = (LongArray_c) _destArray;
	  for (Iterator it = localRegion.iterator();it.hasNext();){
	      final point p = (point)it.next();
	      dest.set(src.get(p),p);
	  }
      }
      else if(_destArray instanceof ShortArray_c){
	  ShortArray_c src = (ShortArray_c) _srcArray;
	  ShortArray_c dest = (ShortArray_c) _destArray;
	  for (Iterator it = localRegion.iterator();it.hasNext();){
	      final point p = (point)it.next();
	      dest.set(src.get(p),p);
	  }
      }
      else if(_destArray instanceof ByteArray_c){
	  ByteArray_c src = (ByteArray_c) _srcArray;
	  ByteArray_c dest = (ByteArray_c) _destArray;
	  for (Iterator it = localRegion.iterator();it.hasNext();){
	      final point p = (point)it.next();
	      dest.set(src.get(p),p);
	  }
      }
      else if(_destArray instanceof BooleanArray_c){
	  BooleanArray_c src = (BooleanArray_c) _srcArray;
	  BooleanArray_c dest = (BooleanArray_c) _destArray;
	  for (Iterator it = localRegion.iterator();it.hasNext();){
	      final point p = (point)it.next();
	      dest.set(src.get(p),p);
	  }
      }
      else if(_destArray instanceof CharArray_c){
	  CharArray_c src = (CharArray_c) _srcArray;
	  CharArray_c dest = (CharArray_c) _destArray;
	  for (Iterator it = localRegion.iterator();it.hasNext();){
	      final point p = (point)it.next();
	      dest.set(src.get(p),p);
	  }
      }
   }


   public static void transferDataToArray(DeserializerBuffer inputBuffer,place thisPlace){
      FatPointer destFp = FatPointer.deserialize(inputBuffer);
      destFp = DLocalPlace_c.findGlobalObject(thisPlace,destFp.getKey());
      x10Array destArray = (x10Array)destFp.getObject();

      if(destArray instanceof x10.array.distributed.IntArray_c)
         ((x10.array.distributed.IntArray_c)destArray).loadDataLocally(inputBuffer);
      else if(destArray instanceof x10.array.distributed.CharArray_c)
         ((x10.array.distributed.CharArray_c)destArray).loadDataLocally(inputBuffer);
      else if(destArray instanceof x10.array.distributed.DoubleArray_c)
         ((x10.array.distributed.DoubleArray_c)destArray).loadDataLocally(inputBuffer);
      else if(destArray instanceof x10.array.distributed.LongArray_c)
         ((x10.array.distributed.LongArray_c)destArray).loadDataLocally(inputBuffer);
      else if(destArray instanceof x10.array.distributed.FloatArray_c)
         ((x10.array.distributed.FloatArray_c)destArray).loadDataLocally(inputBuffer);
      else if(destArray instanceof x10.array.distributed.ByteArray_c)
         ((x10.array.distributed.ByteArray_c)destArray).loadDataLocally(inputBuffer);
      else if(destArray instanceof x10.array.distributed.BooleanArray_c)
         ((x10.array.distributed.BooleanArray_c)destArray).loadDataLocally(inputBuffer);
      else if(destArray instanceof x10.array.distributed.ShortArray_c)
         ((x10.array.distributed.ShortArray_c)destArray).loadDataLocally(inputBuffer);
   }
}
