import x10.util.HashMap;
import x10.compiler.Pragma;
import x10.compiler.NonEscaping;

public class ZYN {
	var plh:PlaceLocalHandle[ZYNPlayer];
	var params:Params;
	var getBuf:GlobalRail[Char];
	var putBuf:GlobalRail[Char];
	
	
	public def this(val _params:Params){
		this.params = _params;
	
	}
	
	
	public def init(){
		//Console.OUT.println("at beginning of init");
		getBuf = new GlobalRail(new Rail[Char](1024*1024));
		putBuf = new GlobalRail(new Rail[Char](1024*1024));
		//Console.OUT.println("about to create plh");
		plh = PlaceLocalHandle.makeFlat[ZYNPlayer](Place.places(), 
				()=>new ZYNPlayer(params.len));
		//Console.OUT.println("plh is created.");
		initSatellites();
		//Console.OUT.println("init is finished.");
	}
	
	
	public def run(){
		// step1 warmup
		warmup();
		//step2 do the actual testing
		if (params.automatic) {
			Console.OUT.println("[Warning] garbage collector (on native back-end) at some point will complain, as"+
					" we will repeatedly allocate up to 0.25 MB data, up to 320 times on each place.");
			val cellSize:Int = 8n;
			Utils.printAutoTableHeader(cellSize);
			
			for (var l:Long=0 ; l<512*1024 ; l=(l*2)>0?(l*2):1) {
				Console.OUT.print(Utils.format(""+l,0n,cellSize,'l'));
				var micros:Double = 1d;
				for (var j:Long=1 ; j<=16 ; ++j) {
					micros = runTests(params.iterations/j, j, l, params.put, params.get) / 
					1000 / (params.iterations/j*j) / 2 / (Place.numPlaces() - 1);
					Console.OUT.print(Utils.format(""+micros,6n, cellSize, 'l'));
				}
				Console.OUT.println(Utils.format(""+l/micros,6n, cellSize,'l'));
			}
		} else {
			Console.OUT.println("put/get test");
			var micros:Double = runTests(params.iterations, params.window, params.len, params.put, params.get)
			/ 1000 / (params.iterations*params.window) / 2 / (Place.numPlaces() - 1);
			Console.OUT.println("Half roundtrip time: " + micros +" us  Bandwidth: " + params.len/micros +" MB/s");
		}
		//Console.OUT.println("Finish testing");
	}
	
	public def warmup(){
		//Console.OUT.println("About to warm up...");
		for (var idx:Long=0 ; idx<16 ; ++idx) {
			runTests(1, 1, 1024, false, false);
			runTests(1, 1, 1024, true, false);
			runTests(1, 1, 1024, false, true);
		}
		
		//Console.OUT.println("Warmed up");
	}
	
	public def runTests(iterations:Long, window:Long, len:Long, put:Boolean, get:Boolean):Long
	{	
		var startTime:Long = System.nanoTime();
		tmpPlh:PlaceLocalHandle[ZYNPlayer] = plh;
		for (var i:Long=0 ; i< iterations ; ++i) {
			finish{
				for (var j:Long=0 ; j<window ; ++j) {
					if(put || get){
						runOnePutGetTest(plh,len, put);
					}else{
						runOneSendTest(plh, len);
					}
				}
			}
			
		}
		val endTime:Long = System.nanoTime();
		return (endTime - startTime);
	} 

	
	public def runOnePutGetTest(tmpPlh:PlaceLocalHandle[ZYNPlayer],_len:Long, put:Boolean){
		
		val P = Place.numPlaces(); 
		for(p in 1.. (P-1)){
			if(put){
				at(Place(p)) async{
				
					Rail.asyncCopy(putBuf, 0, tmpPlh().localBuf, 0, _len);	
				}
			}else{
				at(Place(p)) async{		
			
					Rail.asyncCopy(tmpPlh().localBuf,0, getBuf, 0,_len);
				}
			}
		}
	}
	
	public def runOneSendTest(tmpPlh:PlaceLocalHandle[ZYNPlayer],_len:Long){
		val P = Place.numPlaces();
		val sendBuf: Rail[Char] = (tmpPlh().sendbufPool).getOrThrow(_len) ;
		for(p in 1.. (P-1)){
			at(Place(p)) async{
				if(_len > 0){
					val c:Char = sendBuf(_len-1);
				}
			}
		}
	}
	
	public def initSatellites(){
	    val P = Place.numPlaces();
	    val tmpPlh = plh;
	    @Pragma(Pragma.FINISH_DENSE) finish {
	    	if (P < 256) {
	    		for(var i:Long=0; i<P; i++) {
	    			at (Place(i)) async {
	    				tmpPlh().setPutBuf(putBuf);
	    				tmpPlh().setGetBuf(getBuf);
	    			}
	    		}
	    	} else {
	    		for(var i:Long=P-1; i>=0; i-=32) {
	    			at (Place(i)) async {
	    				val max = here.id;
	    				val min = Math.max(max-31, 0);
	    				for (var j:Long=min; j<=max; ++j) {
	    					at (Place(j)) async {
	    						tmpPlh().setPutBuf(putBuf);
	    						tmpPlh().setGetBuf(getBuf);
	    					}
	    				}
	    			}
	    		}
	    	}
	    }
	
	 }
	
	public static def main(args:Rail[String]){
		params:Params = Utils.parseParams(args);
	    if(params.abort){
	    	return;
	    }
	    val zyn:ZYN = new ZYN(params); // not sure how to test if it is illegal 
	    zyn.init();
	    zyn.run();
	}
	
	
}


class ZYNPlayer{
	var putBuf: GlobalRail[Char];
	var getBuf: GlobalRail[Char];
	var localBuf:Rail[Char];
	var sendbufPool: HashMap[long, Rail[Char]];
	val placeID:Long = here.id;
	var len:Long; // max size of the buffer
	
	/**
	 * @param len: max 
	 */
	public def this(mylen:Long){
		this.len = mylen < 1024*1024 ? 1024*1024 : mylen;
		if(here.id == 0){ // at place zero
			getBuf = new GlobalRail(new Rail[Char](len));
			putBuf = new GlobalRail(new Rail[Char](len));
			sendbufPool = new HashMap[Long, Rail[Char]]();
			for (var l:Long=0 ; l<512*1024 ; l=(l*2)>0?(l*2):1) {
				sendbufPool.put(l, new Rail[Char](l));
			}
		}else{ // other places only need to init the local buffer
			localBuf = new Rail[Char](len, (Long)=>'z');
		}
	}
	
	public def setPutBuf(val _putBuf:GlobalRail[Char]){
		this.putBuf = _putBuf; 
	}
	public def setGetBuf(val _getBuf:GlobalRail[Char]){
		this.getBuf = _getBuf;
	}
	public def getPutBuf():GlobalRail[Char]{
		return this.getBuf;
	}
	public def getGetBuf():GlobalRail[Char]{
		return this.getBuf;
	}
	
	
	
	
}
