package graph;

public class Options {
	public final int BATCH_SIZE, N, P,D;  float MULTIPLIER;
	public final boolean reporting, graphOnly, verification;
	public final char graphType;
	
	public Options(String progName, String[] args) throws Exception {
		int P=1, N=-1, D=4, BATCH_SIZE=1; float MULTIPLIER=1;
		boolean reporting=false, graphOnly=false, verification=true;
		char graphType='E';
		try {
			P = Integer.parseInt(args[0]);
			
			if (args.length > 1) {
				String s = args[1];
				if (s.equalsIgnoreCase("T")) {
					graphType='T'; 
				}
				else if (s.equalsIgnoreCase("R"))
					graphType='R'; 
				else if (s.equalsIgnoreCase("K"))
					graphType='K'; 
				
			}
			assert (graphType=='T' || graphType=='K' || graphType=='E');
			if (args.length > 2) {
				N = Integer.parseInt(args[2]);
			}
			if (args.length > 3) {
				D = Integer.parseInt(args[3]);
			
			}
			if (args.length > 4) {
				boolean b = Boolean.parseBoolean(args[4]);
				reporting=b;
			}
			if (args.length > 5) {
				boolean b = Boolean.parseBoolean(args[5]);
				graphOnly=b;
			}
			if (args.length > 6) {
				boolean b = Boolean.parseBoolean(args[6]);
				verification=b;
			}
			if (args.length > 7) {
				BATCH_SIZE=Integer.parseInt(args[7]);
				
			}
			if (args.length > 8) {
				MULTIPLIER=Float.parseFloat(args[8]);
				
			}
		}
		catch (Exception e) {
			System.out.println("Usage: java " + progName + " <threads:nat> [<Type:T,E,K> [<N:nat> [<Degree:nat> [<reporting:boolean>" +
			"[<graphonly:boolean> [<verification:boolean> [<batchSize:boolean]]]]]]]");
			throw e;
		} finally {
			this.P=P;System.out.println("Number of procs=" + P);
			this.graphType=graphType;System.out.println("graphType=" + graphType);
			this.N=N; System.out.println("N=" + N);
			this.D=D;	System.out.println("D=" + D);
			this.reporting=reporting;	System.out.println("reporting=" + reporting);
			this.graphOnly=graphOnly;System.out.println("graphOnly=" + graphOnly);
			this.verification=verification;System.out.println("verification=" + verification);
			this.BATCH_SIZE = BATCH_SIZE;System.out.println("BATCH_SIZE=" + BATCH_SIZE);
			this.MULTIPLIER=MULTIPLIER; System.out.println("MULTIPLIER=" + MULTIPLIER);
		}
	}

}
