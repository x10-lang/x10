public class Transpose1 {
   // a simplified example of array transpose.  Assumes number of places is even.
   // each place represents one block of matrix elements.  Entire blocks will be exchanged, and then
   // transposed remotely.  
   // Each block is x by y elements, and there are P by Q blocks--this constitutes the matrix layout
   // 
    public static void main(String[] args) {
        int N = 10;
        int dimX,dimY,blockP,blockQ;
        dimX=dimY=blockP=blockQ=0;
        boolean debug = false;
        for (int i = 0; i < args.length; ++i) {
            if (args[i].charAt(0) == '-') {
                if (args[i].equals("-debug")) {
                    debug = true;
                }
                else if (args[i].equals("-P")) {
	                blockP = java.lang.Integer.parseInt(args[++i]);
                }
                else if (args[i].equals("-Q")) {
                blockQ = java.lang.Integer.parseInt(args[++i]);
                }
                else if (args[i].equals("-x")) {
                dimX = java.lang.Integer.parseInt(args[++i]);
                }
                else if (args[i].equals("-y")) {
                dimY = java.lang.Integer.parseInt(args[++i]);
                }
                
            } else {
                System.out.println("Usage: -x <numElements> -y <numElements> -P <blockSize> -Q <blockSize>");
                x10.lang.Runtime.setExitCode(99);
                return;
            }
          }	
            checkVal(dimX,"-x");
            checkVal(dimY,"-y");
            checkVal(blockQ,"-Q");
            checkVal(blockP,"-P");
        }
        
     public static void checkVal(int val,String msg){
        if(val <= 0){
          System.out.println("Invalid value "+val+" for "+msg);
          x10.lang.Runtime.setExitCode(99);
        }	
      }
  }