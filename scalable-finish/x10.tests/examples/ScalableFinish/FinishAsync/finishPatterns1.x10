import x10.compiler.*;
public class finishPatterns1 {
     public def f1():void {
    	 async{}
     }
     public def run() {
	
    	 @FinishAsync(0,0,true,1) 
         finish{
                val body:()=>void = ()=>f1();
                body();
         }
         
         
         @FinishAsync(0,0,true,1) 
         finish{
                val body:()=>void = ()=>{async{}};
                body();
         }
         
         
         @FinishAsync(0,0,true,1) 
         finish{
                val body:()=>void = ()=>{async{f1();}};
                body();
         }
         
         
         @FinishAsync(0,0,true,1) 
         finish{
                val body:()=>void = ()=>{async{f1();}};
                async{body();}
         }
         
         
         @FinishAsync(0,0,true,1)
         finish{
                 val body:()=>void = ()=>{
                         val body2:()=>void = ()=>{
                                 async{}
                         };
                         async{
                                 body2();
                         }
                 };
                 body();
         }



        finish{

                val body:()=>void = ()=>{f6(3);};
                async(Place.place(3)){
                        body();
                }
        }


         @FinishAsync(0,0,true,4)
         finish{
                 val body:()=>void = ()=>{async(here.next()){}};
                 async(here){}
                 f6(3);
                 body();
        }
 
  
         @FinishAsync(0,0,true,4)
         finish{
                 val body:()=>void = ()=>{val i = 3;};
                 async(here){}
                 f6(3);
                 async(Place.place(4)){body();}
        }
  
  
         @FinishAsync(0,0,true,4)
         finish{
                 val body:()=>void = ()=>{f1();};
                 async(here.next()){body();}
        }

     }

     public def f6(p:int){
             async(Place.place(p)){}
     }


     public static def main(args: Rail[String]) {
    	 new finishPatterns1().run();
     }


 }



