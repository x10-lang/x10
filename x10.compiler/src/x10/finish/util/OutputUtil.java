package x10.finish.util;

import java.io.ByteArrayInputStream;


import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;

import x10.finish.*;
import x10.finish.table.CallTableKey;
import x10.finish.table.CallTableVal;

/**
 * This class is from the internet to deep-copy a serializable object
 */
public class OutputUtil {
    /**
     * Returns a copy of the object, or null if the object cannot be serialized.
     */
    public static Object copy(Object orig) {
	Object obj = null;
	try {
	    // Write the object out to a byte array
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    ObjectOutputStream out = new ObjectOutputStream(bos);
	    out.writeObject(orig);
	    out.flush();
	    out.close();

	    // Make an input stream from the byte array and read
	    // a copy of the object back in.
	    ObjectInputStream in = new ObjectInputStream(
		    new ByteArrayInputStream(bos.toByteArray()));
	    obj = in.readObject();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (ClassNotFoundException cnfe) {
	    cnfe.printStackTrace();
	}
	return obj;
    }

    public static void saveCallTable(String filename,
	    HashMap<CallTableKey, LinkedList<CallTableVal>> calltable) {
	FileOutputStream fos = null;
	ObjectOutputStream out = null;
	try {
	    fos = new FileOutputStream(filename);
	    out = new ObjectOutputStream(fos);
	    out.writeObject(calltable);
	    out.close();
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
    }
    
    public static HashMap<CallTableKey, LinkedList<CallTableVal>> loadCallTable(String filename){
	FileInputStream fis = null;
	ObjectInputStream in = null;
	HashMap<CallTableKey, LinkedList<CallTableVal>> calltable = null;
	try{
	    fis = new FileInputStream(filename);
	    in = new ObjectInputStream(fis);
	    calltable = (HashMap<CallTableKey, LinkedList<CallTableVal>>)in.readObject();
    
	}
	catch(IOException ex){
	    ex.printStackTrace();
	}
	catch(ClassNotFoundException ex){
	    ex.printStackTrace();
	}
	return calltable;
    }
}