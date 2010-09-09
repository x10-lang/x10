import x10.compiler.FinishAsync;
public class finishTest2_p1 {

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
        
        var i:int = 0;

        // finish with multiple asyncs
        
    	@FinishAsync(23,16,true,1)
        finish{
                async{}
                async{}
        }

    	@FinishAsync(23,16,true,1)
        finish{
                async{}
                f1();
                async{}
        }

        // finish with multiple at
    	@FinishAsync(23,16,true,1)
        finish{
                at(here){}
                at(here){}
        }
    	@FinishAsync(23,16,true,1)
        finish{
                at(here){}
                f2();
                at(here){}
        }

        // finish with async and at
    	@FinishAsync(23,16,true,1)
        finish{
                at(here){}
                i = i + 1;
                async{}
        }
    	@FinishAsync(23,16,true,1)
        finish{
                at(here){}
                f1();
                //i = i + 1;
        }
        
    	@FinishAsync(23,16,true,1)
        finish{
                async{}
                f2();
        }
	}

	public static def main(args: Rail[String]) {
		new finishTest2_p1().run();
	}
}


         
