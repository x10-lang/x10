import java.util.Set;
import java.util.TreeSet;


public class GraphSum {

	public GraphSum() {
		super();
		// TODO Auto-generated constructor stub
	}
	public boolean reporting=false;
	public class V   {
		public final int index;
		public int val,sum;
		public int degree;
		public V parent;
		public V [] neighbors;
		boolean visited;
		public V(int i){index=i;}
		
		public void sumVisit(int p)  {
			sum +=p;
			final int v = val;
			if (! visited) {
				visited=true;
				for (int k=0; k < degree; k++)
					neighbors[k].sumVisit(v);
			}
		}
		
		public void sumVisitPar(int p)  {
			sum +=p;
			final int v = val;
			if (! visited) {
				visited=true;
				for (int k=0; k < degree; k++)
					neighbors[k].sumVisitPar(v);
			}
		}
		
		
		@Override
		public String toString() {
			String s="[" + (neighbors.length==0? "]" : "" + neighbors[0].index);
			for (int i=1; i < neighbors.length; i++) s += ","+neighbors[i].index;
			return "v(" + index + ",degree="+degree+ ",n=" + s+"])";
		}
	}
	
	class E{
		public int v1,v2;
		public boolean in_tree;
		public E(int u1, int u2){ v1=u1;v2=u2;in_tree=false;}
	}

}
