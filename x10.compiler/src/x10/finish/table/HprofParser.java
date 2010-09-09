package x10.finish.table;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.Vector;


public class HprofParser {
    class ExprStat {
	public double percentage = 0.0;
	public long liveBytes = 0;
	public long allocatedBytes = 0;
	public long allocatedObj = 0;
	public String name;
	public int debug = 0;

	public ExprStat(double p, long l, long b, long o, String n) {
	    // System.out.println("original name:"+n);
	    name = abstractName(n);
	    // System.out.println("processed name:"+name);
	    percentage = p;
	    liveBytes = l;
	    allocatedBytes = b;
	    allocatedObj = o;
	}

	public void add(ExprStat r) {
	    assert (r.name == this.name);
	    percentage += r.percentage;
	    liveBytes += r.liveBytes;
	    allocatedBytes += r.allocatedBytes;
	    allocatedObj += r.allocatedObj;
	    debug++;
	}

	public int hashCode() {
	    return name.hashCode();
	}

	public boolean equals(Object o) {
	    boolean result = false;
	    if (o instanceof ExprStat) {
		return name.equals(((ExprStat) o).name);
	    }
	    return result;
	}

	public String toString() {
	    return name + ":(" + percentage + "%," + liveBytes / 1000 + "KB,"
		    + allocatedBytes / 1000 + "KB," + allocatedObj + ")---"
		    + debug;
	}

	private String abstractName(String s) {
	    int i;
	    for (i = s.length() - 1; i >= 0; i--) {
		int c = s.charAt(i);
		boolean f1 = (c == '$');
		boolean f2 = Character.isDigit(c);
		if (!(f1 || f2)) {
		    break;
		}
	    }
	    String tmp = s.substring(0, i + 1);
	    return tmp;
	}
    }

    public final ReverseFileReader file;
    private Vector<ExprStat> results;
    private long root = 0;
    private long remote = 0;
    private long all = 0;

    public HprofParser(String name) throws Exception {
	file = new ReverseFileReader(name);
	results = new Vector<ExprStat>();
    }

    public long getRootStat() {
	if (root == 0)
	    add();
	return root / 1000;
    }

    public long getRemoteStat() {
	if (remote == 0)
	    add();
	return remote / 1000;
    }

    public long getAllStat() {
	if (all == 0)
	    add();
	return all / 1000;
    }

    private void add() {
	if (all == 0 && root == 0 && remote == 0) {
	    for (int i = 0; i < results.size(); i++) {
		ExprStat tmp = results.get(i);
		if (tmp.name.contains("Root")) {
		    root += tmp.allocatedBytes;
		    all += tmp.allocatedBytes;
		    // System.err.println("find root");
		} else if (tmp.name.contains("Remote")) {
		    remote += tmp.allocatedBytes;
		    all += tmp.allocatedBytes;
		    // System.err.println("find remote");
		} else if (tmp.name.contains("FinishStates")) {
		    all += tmp.allocatedBytes;
		}
	    }
	}
    }

    public void parse() throws Exception {

	String record = null;
	// the last two lines are not statistics, skip them
	record = file.readLine();
	//System.err.println(record);
	record = file.readLine();
	//System.err.println(record);
	while ((record = file.readLine()) != null) {
	    //System.err.println(record);
	    if (record.contains("rank")) {
		break;
	    }
	    // TODO: test it
	    
	    ExprStat r = parseRecord(record);
	    //System.out.println("record: "+r.toString());
	    //Thread.sleep(200);
	    int pos = results.indexOf(r);
	    if (pos == -1) {
		// not found
		//System.out.println("record: "+r.toString()+" first added");
		results.add(r);
	    } else {
		ExprStat pre = results.get(pos);
		//System.out.println("record found: "+pre.toString());
		//System.out.println("record added: "+r.toString());
		pre.add(r);

		//System.out.println("record updated: "+pre.toString());
		results.remove(pos);
		results.add(pre);
	    }
	}

    }

    private ExprStat parseRecord(String rec) {
	double perc = 0;
	long live = 0;
	long allocated = 0;
	long obj = 0;
	String name = "";
	int cnt = 0;
	StringTokenizer st = new StringTokenizer(rec);
	while (st.hasMoreTokens()) {
	    String tmp = st.nextToken();
	    switch (cnt) {
	    case 1:
		int lastPos = tmp.indexOf('%');
		perc = Double.parseDouble(tmp.substring(0, lastPos));
		// System.out.println(perc);
		break;
	    case 3:
		live = Integer.parseInt(tmp);
		// System.out.println(live);
		break;
	    case 5:
		allocated = Integer.parseInt(tmp);
		// System.out.println(allocated);
		break;
	    case 6:
		obj = Integer.parseInt(tmp);
		// System.out.println(obj);
		break;
	    case 8:
		name = tmp;
		// System.out.println(name);
		break;
	    default:
		// System.err.println(tmp);
	    }
	    cnt++;
	}

	return new ExprStat(perc, live, allocated, obj, name);
    }

    class PercentageComparer implements Comparator {
	public int compare(Object obj1, Object obj2) {
	    double i1 = ((ExprStat) obj1).percentage;
	    double i2 = ((ExprStat) obj2).percentage;
	    return (int) (i1 - i2);
	}
    }

    class LiveBytesComparer implements Comparator {
	public int compare(Object obj1, Object obj2) {
	    long i1 = ((ExprStat) obj1).liveBytes;
	    long i2 = ((ExprStat) obj2).liveBytes;
	    return (int) (i1 - i2);
	}
    }

    class AllocatedBytesComparer implements Comparator {
	public int compare(Object obj1, Object obj2) {
	    long i1 = ((ExprStat) obj1).allocatedBytes;
	    long i2 = ((ExprStat) obj2).allocatedBytes;
	    return (int) (i1 - i2);
	}
    }

    class AllocatedObjsComparer implements Comparator {
	public int compare(Object obj1, Object obj2) {
	    long i1 = ((ExprStat) obj1).allocatedObj;
	    long i2 = ((ExprStat) obj2).allocatedObj;
	    return (int) (i1 - i2);
	}
    }

    public void dump(int sel) {
	Comparator c;
	switch (sel) {
	case 1:
	    c = new PercentageComparer();
	    break;
	case 2:
	    c = new LiveBytesComparer();
	    break;
	case 3:
	    c = new AllocatedBytesComparer();
	    break;
	case 4:
	    c = new AllocatedObjsComparer();
	    break;
	default:
	    c = new AllocatedBytesComparer();
	}
	Collections.sort(results, c);
	for (int i = results.size() - 1; i >= 0; i--) {
	    ExprStat es = results.get(i);
	    if (es.toString().contains("Finish"))
		System.out.println(es);
	}

    }

}

class ReverseFileReader {
    private String filename;
    private RandomAccessFile randomfile;
    private long position;

    public ReverseFileReader(String filename) throws Exception {
	// Open up a random access file
	this.randomfile = new RandomAccessFile(filename, "r");
	// Set our seek position to the end of the file
	this.position = this.randomfile.length();

	// Seek to the end of the file
	this.randomfile.seek(this.position);
	// Move our pointer to the first valid position at the end of the file.
	String thisLine = this.randomfile.readLine();
	while (thisLine == null) {
	    this.position--;
	    this.randomfile.seek(this.position);
	    thisLine = this.randomfile.readLine();
	    this.randomfile.seek(this.position);
	}
    }

    // Read one line from the current position towards the beginning
    public String readLine() throws Exception {
	int thisCode;
	char thisChar;
	String finalLine = "";

	// If our position is less than zero already, we are at the beginning
	// with nothing to return.
	if (this.position < 0) {
	    return null;
	}

	for (;;) {
	    // we've reached the beginning of the file
	    if (this.position < 0) {
		break;
	    }
	    // Seek to the current position
	    this.randomfile.seek(this.position);

	    // Read the data at this position
	    thisCode = this.randomfile.readByte();
	    thisChar = (char) thisCode;

	    // If this is a line break or carrige return, stop looking
	    if (thisCode == 13 || thisCode == 10) {
		// See if the previous character is also a line break character.
		// this accounts for crlf combinations
		this.randomfile.seek(this.position - 1);
		int nextCode = this.randomfile.readByte();
		if ((thisCode == 10 && nextCode == 13)
			|| (thisCode == 13 && nextCode == 10)) {
		    // If we found another linebreak character, ignore it
		    this.position = this.position - 1;
		}
		// Move the pointer for the next readline
		this.position--;
		break;
	    }
	    // This is a valid character append to the string
	    finalLine = thisChar + finalLine;
	    // Move to the next char
	    this.position--;
	}
	// return the line
	return finalLine;
    }
}
