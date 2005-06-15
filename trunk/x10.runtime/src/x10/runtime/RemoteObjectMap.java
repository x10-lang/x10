package x10.runtime;

/**
 * When dealing with objects on remote VMs, we have a key which is simply
 * a long.  This key was passed in via a LAPI message.  We need to map
 * some of these (e.g., Clock c on VM v) to our own representation of
 * that object (e.g., our RemoteClock).  This class provides that mapping.
 * 
 * The method names and semantics are meant to be the same as HashMap.
 *
 * @author Allan Kielstra
 **/

public class RemoteObjectMap {

    static private RemoteObjectMapForVM remoteObjectsOnVM_[] = new RemoteObjectMapForVM[Configuration.NUMBER_OF_VMS];
    static {
        for (int i = 0; i < remoteObjectsOnVM_.length; ++i) {
            remoteObjectsOnVM_[i] = new RemoteObjectMapForVM();
        }
    }

    static Object put(int vm, long key, Object val) {
        return remoteObjectsOnVM_[vm].put(key, val);
    }
    static Object get(int vm, long key) {
        return remoteObjectsOnVM_[vm].get(key);
    }
    static Object remove(int vm, long key) {
        return remoteObjectsOnVM_[vm].remove(key);
    }
    static Object containsKey(int vm, long key) {
        return remoteObjectsOnVM_[vm].get(key);
    }
    static void clear(int vm) {
        remoteObjectsOnVM_[vm].clear();
    }
    static long[] deleteClockEntries(int vm) {
        return remoteObjectsOnVM_[vm].deleteClockEntries();
    }

    static void dump() {
        for (int i = 0; i < remoteObjectsOnVM_.length; ++i) {
            System.out.println("the following are remote objects on vm " + i);
            remoteObjectsOnVM_[i].dump();
        }
    }
}
    
class RemoteObjectMapForVM {
    public RemoteObjectMapForVM() {
        currentNumberOfChains_ = 32;
        currentChainsInUse_ = 0;
        loadFactor_ = (float) 0.75;
        chains_ = new keyValuePair[currentNumberOfChains_];
    }
    
    void dump() {
        System.out.println(currentChainsInUse_ + " out of " + currentNumberOfChains_ + " elements...");
        for (int i = 0; i < currentNumberOfChains_; ++i) {
            for (keyValuePair kvp = chains_[i]; kvp != null; kvp = kvp.next_) {
                System.out.println("chain " + i + " " + Long.toHexString(kvp.key_) + ", " + kvp.value_);
            }
        }
    }

    /**
     * find all the clock entries that no longer have any Activities
     * registered and (a) delete them from the map and (b) return the
     * corresponding keys in an array.
     **/
    long[] deleteClockEntries() {
        int c = 0;
        keyValuePair deleteKeys = null;
        for (int i = 0; i < currentNumberOfChains_; ++i) {
            keyValuePair keyBeforeDelete = chains_[i];
            keyValuePair x = chains_[i];
            keyValuePair prev = null;
            if (keyBeforeDelete != null) {
                while (keyBeforeDelete != null &&
                       keyBeforeDelete.value_ instanceof RemoteClock &&
                       ((RemoteClock) keyBeforeDelete.value_).activityCount() == 0) {
                    ++c;
                    if (keyBeforeDelete.next_ == null) {
                        // we've wiped out this entire chain
                        keyBeforeDelete.next_ = deleteKeys;
                        deleteKeys = chains_[i];
                        chains_[i] = null;
                        --currentChainsInUse_;
                    }
                    prev = keyBeforeDelete;
                    keyBeforeDelete = keyBeforeDelete.next_;
                }
                if (keyBeforeDelete != null) {
                    chains_[i] = keyBeforeDelete;
                    if (prev != null) {
                        prev.next_ = deleteKeys;
                        deleteKeys = x;
                    }
                    while (keyBeforeDelete != null) {
                        // keyBeforeDelete points to a value we need to keep
                        while (keyBeforeDelete.next_ != null &&
                               (!(keyBeforeDelete.next_.value_ instanceof RemoteClock) ||
                                ((RemoteClock) keyBeforeDelete.value_).activityCount() != 0)) {
                            keyBeforeDelete = keyBeforeDelete.next_;
                        }
                        // keyBeforeDelete.next_ is either null or something
                        // we want to delete
                        if (keyBeforeDelete.next_ == null) {
                            keyBeforeDelete = null; //get out of outer while
                        } else {
                            keyValuePair c2 = keyBeforeDelete.next_;
                            while (c2 != null &&
                                   c2.value_ instanceof RemoteClock &&
                                   ((RemoteClock) c2.value_).activityCount() == 0) {
                                ++c;
                                if (c2.next_ == null) {
                                    c2.next_ = deleteKeys;
                                    deleteKeys = keyBeforeDelete.next_;
                                    keyBeforeDelete.next_ = null;
                                }
                                prev = c2;
                                c2 = c2.next_;
                            }
                            if (c2 != null) {
                                prev.next_ = deleteKeys;
                                deleteKeys = prev;
                                keyBeforeDelete.next_ = c2;
                            }
                            keyBeforeDelete = c2;
                        }
                    }
                }
            }
        }
        long[] rv = new long[c];
        for (keyValuePair kvp = deleteKeys; kvp != null; kvp = kvp.next_) {
            rv[--c] = kvp.key_;
        }
        assert c == 0;
        return rv;
    }
    
    void clear() {
        currentChainsInUse_ = 0;
        for (int i = 0; i < currentNumberOfChains_; ++i) {
            chains_[i] = null;
        }
    }
    
    public Object put(long key, Object val) {
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
    
    public Object get(long key) {
        int chainNumber = (int) ((key >> 4) & (currentNumberOfChains_ - 1));

        for (keyValuePair kvp = chains_[chainNumber]; kvp != null; kvp = kvp.next_) {
            if (kvp.key_ == key) return kvp.value_;
        }
        return null;
    }
    
    public Object remove(long key) {
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
    
    private int currentNumberOfChains_;
    private int currentChainsInUse_;
    private float loadFactor_;
    private keyValuePair[] chains_;
    public class keyValuePair {
        keyValuePair(long key, Object value) {
            key_ = key; value_ = value; next_ = null;
        }
        long            key_;
        Object          value_;
        keyValuePair    next_;
    }
}
