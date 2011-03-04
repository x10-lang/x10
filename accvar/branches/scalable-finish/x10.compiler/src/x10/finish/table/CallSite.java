package x10.finish.table;

import java.io.Serializable;

public class CallSite implements Serializable{
    private static final long serialVersionUID = 1L;
    public final int line;
    public final int column;
    public final String srcpack;
    public final String srcname;
    public CallSite(String s, String n, int l, int c){
	srcpack = s;
	srcname = n;
	line = l;
	column = c;
    }
    public String toString(){
	return srcpack+"."+srcname+"."+line+"."+column;
    }
}
