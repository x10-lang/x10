import x10.compiler.*;
public class finishPatterns {
     public def f1():void {
    	 async{}
     }
     public def f2():void {
    	 async(here.next()){}
     }
     public def f3():void{
            async{
                   f4();
           }
     }

     public def f4() {
             async{
                     f3();
             }
     }

     public def f5(){
             val body:()=>void = () => {async{}};
             body();
     }
     public def run() {
	
    	 //TODO: test code
    	 var i:int = 0;
         // All local asyncs, including no async in finish
    	 // nested local async
         @FinishAsync(0,0,true,1)
         finish{
                async{
                        async{}
                }         
    	 }
         // simple local async
    	 @FinishAsync(0,0,true,1)
         finish{
    		 async{}
    	 }
    	 // method calls closure which contains async  
         @FinishAsync(0,0,true,1)
         finish{
                 f5();
         }
         //trivial finish
         @FinishAsync(0,0,true,1)
    	 finish{
    		 i = i + 1;
    	 }
         // method calls async
    	 @FinishAsync(0,0,true,1)
         finish{
    		 f1();
    	 }

         //async calls method which contains async
    	 @FinishAsync(0,0,true,1) 
         finish{
                async{
                       f1();
                }
         }
        // closure contains method call
    	 @FinishAsync(0,0,true,1) 
         finish{
                val body:()=>void = ()=>f1();
                body();
         }
        // closure contains async        
         @FinishAsync(0,0,true,1) 
         finish{
                val body:()=>void = ()=>{async{}};
                body();
         }
         // closure contains async which has method call
         @FinishAsync(0,0,true,1) 
         finish{
                val body:()=>void = ()=>{async{f1();}};
                body();
         }
         // async calls closure contains async and method
         @FinishAsync(0,0,true,1) 
         finish{
                val body:()=>void = ()=>{async{f1();}};
                async{body();}
         }
         // closure calls closure
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

        // recursive call with async
        @FinishAsync(0,0,true,1) 
         finish{
                 f3();
         }
         @FinishAsync(0,0,true,1) 
         finish{
                 f4();
         }
        // recursive call with finish and async
         rec2();
        
        
         @FinishAsync(0,0,true,0)
        finish{

                val body:()=>void = ()=>{f6(3);};
                async(Place.place(3)){
                        body();
                }
        }


        // no nested remote async
         // remote async but no nested ones
         @FinishAsync(0,0,true,4)
         finish{
                 async(here){}
                 async(here.next()){}
                 async(here.next().next()){}
        }
        // remote async and method 
         @FinishAsync(0,0,true,4)
         finish{
                 f1();
                 async(here.next()){}
                 async(here.next().next()){}
        }

         @FinishAsync(0,0,true,4)
         finish{
                 async(here){}
                 f6(5);
                 async(here.next().next()){}
        }
         // remote async and closure
         @FinishAsync(0,0,true,4)
         finish{
                 val body:()=>void = ()=>{async(here.next()){}};
                 async(here){}
                 f6(3);
                 body();
        }
        // nested local async 
         @FinishAsync(0,0,true,4)
         finish{
                 async(here){
                        async{};
                        async(here.next()){
                                async{}
                        }
                 }
                 async(here.next()){
                        async{
                                async{}
                        }
                 }
                 async(here.next().next()){
                        async{
                                f1();
                        }
                 }
        }
        // remote async contains local closure 
         @FinishAsync(0,0,true,4)
         finish{
                 val body:()=>void = ()=>{val i = 3;};
                 async(here){}
                 f6(3);
                 async(Place.place(4)){body();}
        }
         // remote async calls closure which invokes local async
         @FinishAsync(0,0,true,4)
         finish{
                 val body:()=>void = ()=>{f1();};
                 async(here.next()){body();}
        }

        // neither all local nor no nested remote
        @FinishAsync(0,0,true,0)
        finish{
                async{
                        async(here.next()){}
                }
                async(here.next()){
                        async(here.next()){}
                }
        }

        @FinishAsync(0,0,true,0)
        finish{
                async(Place.place(3)){
                        f6(6);
                }
        }
        
        
               //rec4();

     }

     public def rec1(){
            async{
                   rec2();
            }
     }

     public def rec2(){
            @FinishAsync(0,0,true,1)
            finish{
                   rec1();
            }
     }

     public def f6(p:int){
             async(Place.place(p)){}
     }

     public def rec3(){
             async(Place.place(3)){
                     rec4();
             }
     }

     public def rec4(){
             
             @FinishAsync(0,0,true,4)
             finish{
                     rec3();
             }
             
            @FinishAsync(0,0,true,0)
            finish{
                    async(here.next()){
                            rec3();
                    }
           }
     }

     public static def main(args: Rail[String]) {
    	 new finishPatterns().run();
     }


 }



