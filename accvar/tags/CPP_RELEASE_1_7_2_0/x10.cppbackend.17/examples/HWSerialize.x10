public class HWSerialize {
	public static void main(String[] args) {
		final place h = here;
		final point q = [0,1];
		final region(:self.rank==1 && self.rect) r1 = [2:3];
		final region r2 = [[4:5],[6:7]];
		final dist dU = dist.UNIQUE;
		final dist dU1 = dist.UNIQUE | here;
		final dist dl = r2->here;
		final dist de = dl | dist.UNIQUE[1];
		final int value [:self.rank==1 && self.rect] ia = new int value [r1] (point [p]) { return p; };
		final place value [:self.rank==1 && self.rect] ip = new place value [r1] (point [p]) { return dist.UNIQUE[p]; };
//		final place value [:self.rank==1 && self.rect] ip = new place value [r1] (point [p]) { return h; };
		//final dist db = dist.block(r2);
		finish ateach (point p : dist.UNIQUE) {
			if (here.id == 0)
				System.out.println("q=["+q[0]+","+q[1]+"]");
		}
	}
}

