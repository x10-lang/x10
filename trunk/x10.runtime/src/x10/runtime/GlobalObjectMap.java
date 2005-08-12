package x10.runtime;

/**
 * 
 * @author donawa
 *
 * Need a hashmap like datastructure that uses a long for a value.  Used for keeping track of FatPointers and remote clocks
 */
public class GlobalObjectMap {
    public GlobalObjectMap() {
        currentNumberOfChains_ = 32;
        currentChainsInUse_ = 0;
        loadFactor_ = (float) 0.75;
        chains_ = new keyValuePair[currentNumberOfChains_];
    }
    
    public void dump() {
        System.out.println(currentChainsInUse_ + " out of " + currentNumberOfChains_ + " elements...");
        for (int i = 0; i < currentNumberOfChains_; ++i) {
            for (keyValuePair kvp = chains_[i]; kvp != null; kvp = kvp.next_) {
                System.out.println("chain " + i + " " + Long.toHexString(kvp.key_) + ", " + kvp.value_);
            }
        }
    }
    synchronized public Object put(long key, Object val) {
        Object rv = null;
        int chainNumber = (int) ((key >> 4) & (currentNumberOfChains_ - 1));

        if (chains_[chainNumber] == null) {
            //if (((float)(currentChainsInUse_+1) / (float) currentNumberOfChains_) > loadFactor_) {
            //    reHash();
            //}
            chains_[chainNumber] = new keyValuePair(key, val);
            ++currentChainsInUse_;
        } else {
            keyValuePair addAfter = null;
            for (keyValuePair kvp = chains_[chainNumber]; kvp != null; kvp = kvp.next_) {
                if (kvp.key_ == key) {
                    rv = kvp.value_;
                    kvp.value_ = val;
                    return rv;
                }
                if (kvp.next_ == null) addAfter = kvp;
            }
            addAfter.next_ = new keyValuePair(key, val);
        }
        return null;
    }
   
    public void clear() {
        currentChainsInUse_ = 0;
        for (int i = 0; i < currentNumberOfChains_; ++i) {
            chains_[i] = null;
        }
    }
    
   
    
    public Object get(long key) {
        int chainNumber = (int) ((key >> 4) & (currentNumberOfChains_ - 1));

        for (keyValuePair kvp = chains_[chainNumber]; kvp != null; kvp = kvp.next_) {
            if (kvp.key_ == key) return kvp.value_;
        }
        return null;
    }
    
    synchronized public Object remove(long key) {
        int chainNumber = (int) ((key >> 4) & (currentNumberOfChains_ - 1));

        keyValuePair keyBeforeDelete = chains_[chainNumber];
        if (keyBeforeDelete != null) {
            if (keyBeforeDelete.key_ == key) {
                chains_[chainNumber] = keyBeforeDelete.next_;
                if (chains_[chainNumber] == null) {
                    --currentChainsInUse_;
                }
                return keyBeforeDelete.value_;
            } else {
                for (keyValuePair kvp = keyBeforeDelete.next_; kvp != null; kvp = kvp.next_) {
                    if (kvp.key_ == key) {
                        keyBeforeDelete.next_ = kvp.next_;
                        return kvp.value_;
                    }
                    keyBeforeDelete = kvp;
                }
            }
        }
        return null;
    }
    
    public boolean containsKey(long key) {
        return get(key) != null;
    }
    
    protected int currentNumberOfChains_;
    protected int currentChainsInUse_;
    private float loadFactor_;
    protected keyValuePair[] chains_;
    
    protected class keyValuePair {
        keyValuePair(long key, Object value) {
            key_ = key; value_ = value; next_ = null;
        }
        public long            key_;
        public Object          value_;
        public keyValuePair    next_;
    }
}
