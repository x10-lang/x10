package x10.finish.table;

import java.io.ByteArrayInputStream;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ArrayList;



/**
 * This class is from the internet to deep-copy a serializable object
 */
public class OutputUtil {
    /**
     * Returns a copy of the object, or null if the object cannot be serialized.
     */
    public static int PLACE_NUMBER=16;
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


    
    private static class PlotRecord{
	long root;
	long remote;
	long all;
	public PlotRecord(long ro, long re, long a){
	    root = ro;
	    remote = re;
	    all = a;
	}
    }
    public static void Write2Plot(String dir) throws Exception {
	String path = "../x10.tests/examples/ScalableFinish/Patterns/expr2/";
	PlotRecord[] newStat = new PlotRecord[16];
	PlotRecord[] oldStat = new PlotRecord[16];
	for(int i=0;i<PLACE_NUMBER;i++){
	    int index = i+1;
	    String file_name = path + dir + "/" + dir + "_"+index+".java.hprof.txt";
	    HprofParser p = new HprofParser(file_name);
	    p.parse();
	    long root = p.getRootStat();
	    long remote = p.getRemoteStat();
	    long all = p.getAllStat();
	    PlotRecord pr = new PlotRecord(root,remote,all);
	    newStat[i] = pr;
	}
	for(int i=0;i<PLACE_NUMBER;i++){
	    int index = i + 1;
	    String file_name = path + dir + "/" + dir + "_old_"+index+".java.hprof.txt";
	    HprofParser p = new HprofParser(file_name);
	    p.parse();
	    long root = p.getRootStat();
	    long remote = p.getRemoteStat();
	    long all = p.getAllStat();
	    PlotRecord pr = new PlotRecord(root,remote,all);
	    oldStat[i] = pr;
	}
	String outPath = path + dir;
	BufferedWriter rootData = 
            new BufferedWriter(new FileWriter(outPath+"/root.dat"));
	BufferedWriter remoteData = 
            new BufferedWriter(new FileWriter(outPath+"/remote.dat"));
	BufferedWriter allData = 
            new BufferedWriter(new FileWriter(outPath+"/all.dat"));
	for(int i=0;i<PLACE_NUMBER;i++){
	    int index = i+1;
	    PlotRecord newRec = newStat[i];
	    PlotRecord oldRec = oldStat[i];
	    String rootOut = index + "\t" + oldRec.root + "\t" + newRec.root + "\n";
	    String remoteOut = index + "\t" + oldRec.remote + "\t" + newRec.remote + "\n";
	    String allOut = index + "\t" + oldRec.all + "\t" + newRec.all + "\n";
	    rootData.write(rootOut);
	    remoteData.write(remoteOut);
	    allData.write(allOut);
	}
	System.out.println(dir+" finished");
	rootData.close();
	remoteData.close();
	allData.close();

    }
}