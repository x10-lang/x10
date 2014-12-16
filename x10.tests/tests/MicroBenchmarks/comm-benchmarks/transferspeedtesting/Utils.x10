import x10.util.OptionsParser;
import x10.util.Option;
import x10.util.HashMap;
public class Utils {
	
	public static def parseParams(val args:Rail[String]):Params{
		
		val cmdLineParams = new OptionsParser(args,  [
		                                              Option("h", "help", "Print help messages"),
		                                              Option("-v", "validate", "Check whether messages are mangled in transit (not implemented)"),
		                                              Option("-p", "put", "Use x10rt_send_put instead of x10rt_send_msg (we use asyncCopy in x10)"),
		                                              Option("-g", "get", "Use x10rt_send_get instead of x10rt_send_msg (we use asyncCopy in x10)"),
		                                              Option("-a", "auto", "Test a variety of --length and --window")
		                                              ], 
		                                              [                               
		                                               Option("-l", "length", "Size of individual message"),
		                                               Option("-w", "window", "number of pongs to wait for in parallel (window size)"),
		                                               Option("-i", "iterations", "Top-level iterations (round trips))")
		                                               
		                                               ]);
		
		// cmdLineParams.onUnknownKey = (key: String, keyMap: HashMap[String, Option])=>{
		// 	Console.ERR.println("Unknown key, boy, I wish I knew how to do this gracefully.");
		// 	assert(false);
		// 	return -1n;
		// };
		val h:Boolean = cmdLineParams("h", false);
		val len:Int = cmdLineParams("-l", 1024n);
		val window:Long = cmdLineParams("-w", 100l);
		val iterations:Int = cmdLineParams("-i", 320n);
		val v:Boolean = cmdLineParams("-v", false); // didn't implement it
		val put:Boolean = cmdLineParams("-p", false); // off by default
		val get:Boolean = cmdLineParams("-g", false); // off by default
		val automatic:Boolean = cmdLineParams("-a", false); // on by default
		var abort:Boolean = false;
		
		if(h){
			showHelp();
			return Params(true,len, window, iterations, v, put, get, automatic);
		}if(cmdLineParams.hasUnexpectedArgs(true)){
			return Params(true,len, window, iterations, v, put, get, automatic);
		}
		
		return Params(false,len, window, iterations, v, put, get, automatic);
	}
	
	public static def showHelp()
	{
		
		Console.OUT.println("Usage: ZYN"  +" <args>");
		Console.OUT.print( "-h (--help)        ");
		Console.OUT.println("this message");
		Console.OUT.print("-l (--length) <n>      ");
		Console.OUT.print("size of individual message\n");
		Console.OUT.print("-w (--window) <n>");       
		Console.OUT.print("number of pongs to wait for in parallel (window size)\n");
		Console.OUT.print( "-i (--iterations) <n>  ");
		Console.OUT.print( "top-level iterations (round trips)\n");
		Console.OUT.print("-v (--validate)    ");
		Console.OUT.print("check whether messages are mangled in transit\n");
		Console.OUT.print("-p (--put)    ");
		Console.OUT.print("use x10rt_send_put instead of x10rt_send_msg\n");
		Console.OUT.print("-g (--get)    ");
		Console.OUT.print("use x10rt_send_get instead of x10rt_send_msg\n");
		Console.OUT.print("-a (--auto)    ");
		Console.OUT.println("test a variety of --length and --window");
	} 
	
	
	public static def printAutoTableHeader(val cellSize:Int){
		Console.OUT.print(format("",0n,cellSize, 'c'));
		for (var j:Long=1 ; j<=16 ; ++j) {
			Console.OUT.print(format(""+j,0n,cellSize,'l'));
		}
		Console.OUT.println(format("b/w (MB)",0n, cellSize,'l') );
	}
	
	/**
	 * @param str: raw string
	 * @precision: how many bits to display
	 * @param cellLen: length of the cell
	 * @param char : (l)eft, (r)ight, (c)enter
	 */
	public static def format(str:String, precision:Int, cellLen:Int, pos: Char):String{
		var prec:Int = precision;
		if(prec <= 0n) prec = str.length();
		if(prec > str.length()) prec = str.length();
		if(prec > cellLen) prec = cellLen;
		
		
		// now prec <= cellLen, prec < str.length
		val realStr:String = str.substring(0n, prec);
		var leftPadding:Int = 0n;
		var rightPadding:Int = 0n;
		if(pos == 'c'){
			leftPadding = (cellLen - realStr.length())/2 as Int;
			rightPadding = cellLen - realStr.length() - leftPadding;
			
		}else if(pos == 'l'){
			rightPadding = cellLen - realStr.length();
			
		}else if(pos == 'r'){
			leftPadding = cellLen - realStr.length();
			
		}
		return paddStr(realStr, leftPadding, rightPadding);
	
	}
	
	public static def paddStr(str:String,l:Int, val r:Int){
		var result:String ="";
		for(var i:Int=0n; i < l; ++i){
			result+=" ";
		}
		result += str;
		for(var i:Int = 0n; i < r; ++i){
			result +=" ";
		}
		return result;
	}
	
	public static def main(args:Rail[String]){
		Utils.printAutoTableHeader(8n);
	}
}
