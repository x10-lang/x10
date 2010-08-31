import x10.compiler.*;
public class finishTest7_p1  {

        public def f1():void {
                // method contains async
                async{}
        }
        public def f2():void {
                // method contains at
                at(here){}
        } 
	public def run() {
		
                //TODO: test code
        var i:int = 1;
        @FinishAsync(0,0,true,1) finish{
        	for(i=1;i<10;i++){
        		for(i=1;i<10;i++){
        			async{}
        			if(i==3) break;
        		}
        		f1();
        	}
        	for(i=1;i<10;i++){
        		for(i=1;i<10;i++){
        			async{}
        			if(i==3) continue;	
        		}
        		
        	}
        	f1();
        }
        for(i=1;i<10;i++){
		 @FinishAsync(0,0,true,1) finish{
			 for(i=1;i<10;i++){
				 async{}
			 }
		 }
        }
        @FinishAsync(0,0,true,1) finish{
		 for(i=1;i<10;i++){
			 async{}
		 }
		
	 }

	}

	public static def main(args: Rail[String]) {
		new finishTest7_p1().run();
	}
}


         
