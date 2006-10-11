/*
 * Created on Nov 14, 2004
 */
package x10.base;
import x10.lang.dist;
/**
 * @author Christoph von Praun
 */
public abstract class MemoryBlockSafe extends MemoryBlock {

    public void keepItLive() {}
    
    public long getUnsafeAddress() {
        throw new Error("MemoryBlockSafe: no unsafe address for safe memory.");
    }
    
    public long getUnsafeDescriptor() {
        throw new Error("MemoryBlockSafe: no descriptor for safe memory.");
    } 
   public int[] getDescriptor() {return descriptor_;}
   public void setDescriptor(int rank[]) { descriptor_ = rank;};
   public void setDescriptor(dist d){
      int rank = d.region.rank;
      descriptor_ = new int[rank+1];
      descriptor_[0] = rank;
      for (int i = 1; i <= rank; ++i)
         descriptor_[i] = d.region.rank(i-1).size();
   }
   private int[] descriptor_;
}
