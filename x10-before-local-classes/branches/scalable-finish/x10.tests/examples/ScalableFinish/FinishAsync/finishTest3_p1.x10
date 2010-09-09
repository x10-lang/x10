import x10.compiler.*;
public class finishTest3_p1 {
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
    	// nested finish without statements
    	
    	@FinishAsync(23,16,true,1)
        finish{
    		
    	        @FinishAsync(23,16,true,1)
                finish{
    		}
    	}
    	
    	// finish1 should only have async2 
    	
    	@FinishAsync(23,16,true,1)
        finish{// finish1
    		
    	        @FinishAsync(23,16,true,1)
                finish{
    		//async1
    		async{}               
    	    }
    	//async2
    	async{}
    	}
    	
    	@FinishAsync(23,16,true,1)
    	finish{
    		async{

    	                @FinishAsync(23,16,true,1)
    			finish{
    				async{}
    			}
    			f1();
    		}
    	}
    	
    	
    	@FinishAsync(23,16,true,1)
    	finish{
                
    	        @FinishAsync(23,16,true,1)
                finish{
                	at(here){}               
                }
                at(here){}
    	}
    	
    	@FinishAsync(23,16,true,1)
        finish{
        	at(here){

    	                @FinishAsync(23,16,true,1)
        		finish{
        			at(here){}
        		}
        		f2();
        	}
        }

    	@FinishAsync(23,16,true,1)
        finish{
        	at(here){

    	                @FinishAsync(23,16,true,1)
        		finish{
        			async{}
        			
					 at(here){
						 async{}
					 }
					 
        		}
        		
		 }
        	async{}
        }
    }
    
	public static def main(args: Rail[String]) {
		new finishTest3_p1().run();
	}
 }


         
