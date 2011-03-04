/*
    Lonestar BarnesHut: Simulation of the gravitational forces in a
    galactic cluster using the Barnes-Hut n-body algorithm

    Author: Martin Burtscher
    Center for Grid and Distributed Computing
    The University of Texas at Austin

    Copyright (C) 2007, 2008 The University of Texas at Austin

    Licensed under the Eclipse Public License, Version 1.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

      http://www.eclipse.org/legal/epl-v10.html

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

    File: BarnesHut.java
    Modified: Dec. 13, 2007 by Martin Burtscher (initial Java version)
    Modified: Jan 26, 2008 by Nicholas Chen (refactoring to decompose methods into smaller logical chunks)
 	Modified: June 20, 2009 by Nalini Vasudevan (to convert to X10)
 */

import x10.util.Random;

class OctTreeNode {
	var mass: double;
	var posx: double;
	var posy: double;
	var posz: double;
};

class OctTreeLeafNode extends OctTreeNode { // the tree leaves are the bodies
	var velx: double;
	var vely: double;
	var velz: double;
	var accx: double;
	var accy: double;
	var accz: double;

	public def setVelocity(x: double, y: double, z: double): void {
		velx = x;
		vely = y;
		velz = z;
	}

	// advances a body's velocity and position by one time step
	public def advance(hut: BarnesHutOrig!) : void {
		var dvelx: double;
		var dvely: double;
		var dvelz: double;
		var velhx: double;
		var velhy: double;
		var velhz: double;

		dvelx = accx * hut.dthf;
		dvely = accy * hut.dthf;
		dvelz = accz * hut.dthf;

		velhx = velx + dvelx;
		velhy = vely + dvely;
		velhz = velz + dvelz;

		posx += velhx * hut.dtime;
		posy += velhy * hut.dtime;
		posz += velhz * hut.dtime;

		velx = velhx + dvelx;
		vely = velhy + dvely;
		velz = velhz + dvelz;
	}

	// computes the acceleration and velocity of a body
	public def computeForce(root: OctTreeInternalNode, size: double, hut: BarnesHutOrig!): void {
		var ax: double;
		var ay: double;
		var az: double;

		ax = accx;
		ay = accy;
		az = accz;

		accx = 0.0;
		accy = 0.0;
		accz = 0.0;

		recurseForce(root as OctTreeNode!, size * size * hut.itolsq, hut);

		if (hut.step > 0) {
			velx += (accx - ax) * hut.dthf;
			vely += (accy - ay) * hut.dthf;
			velz += (accz - az) * hut.dthf;
		}
	}

	// recursively walks the tree to compute the force
	// on a body
	private def recurseForce(nn: OctTreeNode, dsqq: double, hut: BarnesHutOrig!): void {
		val n = nn as OctTreeNode!;
		var drx: double;
		var dry: double;
		var drz: double;
		var drsq: double;
		var nphi: double;
		var scale: double;
		var idr: double;
		var dsq: double = dsqq;

		drx = n.posx - posx;
		dry = n.posy - posy;
		drz = n.posz - posz;
		drsq = drx * drx + dry * dry + drz * drz;
		if (drsq < dsq) {
			if (n instanceof OctTreeInternalNode) { 
				val inn = n as OctTreeInternalNode!;
				dsq *= 0.25;
				val child = inn.child as Rail[OctTreeNode]!;
				if (inn.child(0) != null) {
					recurseForce(child(0), dsq, hut);
					if (child(1) != null) {
						recurseForce(child(1), dsq, hut);
						if (child(2) != null) {
							recurseForce(child(2), dsq, hut);
							if (child(3) != null) {
								recurseForce(child(3), dsq, hut);
								if (child(4) != null) {
									recurseForce(child(4), dsq, hut);
									if (child(5) != null) {
										recurseForce(child(5), dsq, hut);
										if (child(6) != null) {
											recurseForce(child(6), dsq, hut);
											if (child(7) != null) {
												recurseForce(child(7), dsq, hut);
											}
										}
									}
								}
							}
						}
					}
				}
			} else { // n is a body
				if (n != this) {
					drsq += hut.epssq;
					idr = 1 / Math.sqrt(drsq);
					nphi = n.mass * idr;
					scale = nphi * idr * idr;
					accx += drx * scale;
					accy += dry * scale;
					accz += drz * scale;
				}
			}
		} else { // node is far enough away, don't recurse any deeper
			drsq += hut.epssq;
			idr = 1 / Math.sqrt(drsq);
			nphi = n.mass * idr;
			scale = nphi * idr * idr;
			accx += drx * scale;
			accy += dry * scale;
			accz += drz * scale;
		}
	}
}

// the internal nodes are cells that summarize their children's properties
class OctTreeInternalNode extends OctTreeNode {
 public val child = Rail.make[OctTreeNode](8, (i:int) => new OctTreeNode());

	

	// builds the tree
	def insert(bb: OctTreeLeafNode , r: double): void {
		val b = bb as OctTreeLeafNode!;
		var i: int = 0;
		var x: double = 0.0;
		var y: double = 0.0;
		var z: double = 0.0;

		if (posx < b.posx) {
			i = 1;
			x = r;
		}
		if (posy < b.posy) {
			i += 2;
			y = r;
		}
		if (posz < b.posz) {
			i += 4;
			z = r;
		}

		if (child(i) == null) {
			child(i) = b;
		} else if (child(i) instanceof OctTreeInternalNode) {
			val ch =  child(i)  as OctTreeInternalNode!;
			ch.insert(b, 0.5 * r);
		} else {
			val rh = 0.5 * r;
			val cell = newNode(posx - rh + x, posy - rh + y, posz - rh + z) as OctTreeInternalNode!;
			cell.insert(b, rh);
			val ch = child(i) as OctTreeLeafNode;
			cell.insert(ch, rh);
			child(i) = cell;
		}
	}

	// recursively summarizes info about subtrees
    def computeCenterOfMass(hut: BarnesHutOrig!): void {
		var m: double;
		var px: double = 0.0;
		var py: double = 0.0;
		var pz: double = 0.0;
		var ch: OctTreeNode!;

		var j: int = 0;
		mass = 0.0;
		var i: int = 0;
		for (i = 0; i < 8; i++) {
			ch = child(i) as OctTreeNode!;
			if (ch != null) {
				child(i) = null; // move non-null children to the front (needed
				// later to make other code faster)
				child(j++) = ch;

				if (ch instanceof OctTreeLeafNode) {
					val bod = hut.body as Rail[OctTreeLeafNode]!;
					bod(hut.curr++) =  ch as OctTreeLeafNode;
					// sort bodies in tree order ( approximation of putting
					// nearby nodes together for locality )
				} else {
					val chi = ch as OctTreeInternalNode!;
					(chi).computeCenterOfMass(hut);
				}
				m = ch.mass;
				mass += m;
				px += ch.posx * m;
				py += ch.posy * m;
				pz += ch.posz * m;
			}
		}

		m = 1.0 / mass;
		posx = px * m;
		posy = py * m;
		posz = pz * m;
	}
	

	
	static def newNode(px: double, py: double, pz: double): OctTreeInternalNode {
		var i: int = 0;
		val inn =  new OctTreeInternalNode() as OctTreeInternalNode!;
		inn.mass = 0.0;
		inn.posx = px;
		inn.posy = py;
		inn.posz = pz;

		for (i = 0; i < 8; i++)
			inn.child(i) = null;

		return inn;
	}
}

public class BarnesHutOrig {
	var nbodies: int; // number of bodies in system
	var ntimesteps: int; // number of time steps to run
	var dtime: double; // length of one time step
    var eps: double; // potential softening parameter
    var tol: double; // tolerance for stopping recursion, should be less than 0.57 for 3D case to bound error

	var dthf: double;
	var epssq: double;
	var itolsq: double;

	var step: int = 0;
	var curr: int = 0;
	var body: Rail[OctTreeLeafNode]!; // the n bodies

	var diameter: double;
	var centerx: double;
	var centery: double;
	var centerz: double;

	// For parallel version
	/*static Lock partitionLock = new ReentrantLock();
	static Condition partitionsCondition = partitionLock.newCondition(); */
	var partitionsDone: boolean;
	

	val numberOfProcessors = 8; 
	
	/*static CyclicBarrier barrier;
	static CyclicBarrier specialLatch;*/
	var workers: Rail[ComputePartitionTask]!;
	//static Thread[] threads;

	var  starttime: long;
	var endtime: long;
	var runtime: long;
	var lasttime: long;
	var mintime: long;
	var run: long;

	private  def readInput() {
		var vx: double;
		var vy: double;
		var vz: double;

		nbodies =  1000;
		ntimesteps = 25;
		dtime = 0.025;
		eps = 0.05;
		tol = 0.5;

		dthf = 0.5 * dtime;
		epssq = eps * eps;
		itolsq = 1.0 / (tol * tol);

		if (body == null) {
			Console.OUT.println("configuration: " + nbodies + " bodies, " + ntimesteps + " time steps");

			body = Rail.make[OctTreeLeafNode](nbodies, (i: int) => new OctTreeLeafNode()); 
			
		}

		var i: int;
		val r = new Random();
		for (i = 0; i < nbodies; i++) {
			val bodyi = body(i) as OctTreeLeafNode!;
			/*bodyi.mass = r.nextDouble();
			bodyi.posx = r.nextDouble();
			bodyi.posy = r.nextDouble();
			bodyi.posz = r.nextDouble();
			vx = r.nextDouble();
			vy = r.nextDouble();
			vz = r.nextDouble();*/
			bodyi.mass = (i+1) * 0.1;
			bodyi.posx = (i+1) * 0.2;
			bodyi.posy = (i+1) * 0.3;
			bodyi.posz = (i+1) * 0.4;
			vx = (i+1) * 0.5;
			vy = (i+1) * 0.6;
			vz = (i+1) * 0.7;
			
			bodyi.setVelocity(vx, vy, vz);
		}
	}

	private def computeCenterAndDiameter(): void {
		var minx: double;
		var miny: double;
		var minz: double;
		var maxx: double;
		var maxy: double;
		var maxz: double;
		var posx: double;
		var posy: double;
		var posz: double;

		minx = 1.0E90;
		miny = 1.0E90;
		minz = 1.0E90;
		maxx = -1.0E90;
		maxy = -1.0E90;
		maxz = -1.0E90;

		var i: int;
		for (i = 0; i < nbodies; i++) {
			val bodyi = body(i) as OctTreeLeafNode!;
			posx = bodyi.posx;
			posy = bodyi.posy;
			posz = bodyi.posz;

			if (minx > posx)
				minx = posx;
			if (miny > posy)
				miny = posy;
			if (minz > posz)
				minz = posz;

			if (maxx < posx)
				maxx = posx;
			if (maxy < posy)
				maxy = posy;
			if (maxz < posz)
				maxz = posz;
		}

		diameter = maxx - minx;
		if (diameter < (maxy - miny))
			diameter = (maxy - miny);
		if (diameter < (maxz - minz))
			diameter = (maxz - minz);

		centerx = (maxx + minx) * 0.5;
		centery = (maxy + miny) * 0.5;
		centerz = (maxz + minz) * 0.5;
	}

    public static def main(args:Rail[String]!){



		

		val barnesHutRunner = new BarnesHutOrig();
		barnesHutRunner.runtime = 0;
		barnesHutRunner.lasttime = Long.MAX_VALUE;
		barnesHutRunner.mintime = Long.MAX_VALUE;
		barnesHutRunner.run = 0;
		//barnesHutRunner.workers = Rail.make[ComputePartitionTask] (barnesHutRunner.numberOfProcessors);
		var i: int;
		//for (i = 0; i < numberOfProcessors; i++)
		//   	barnesHutRunner.workers(i) =  new ComputePartitionTask();
		/*
		threads = new Thread[numberOfProcessors];
		barrier = new CyclicBarrier(numberOfProcessors);
		specialLatch = new CyclicBarrier(numberOfProcessors + 1);
		*/

		while (((barnesHutRunner.run < 3) || (Math.abs(barnesHutRunner.lasttime - barnesHutRunner.runtime) * 64 > Math.min(barnesHutRunner.lasttime, barnesHutRunner.runtime))) && (barnesHutRunner.run < 7)) {

			barnesHutRunner.readInput();
			barnesHutRunner.runSimulation();

		}

		Console.OUT.println("runtime: " + (barnesHutRunner.mintime / 1000000) + " ms");
		Console.OUT.println("");

	
		for (i = 0; i < barnesHutRunner.nbodies; i++) {
			// print result
			val bodyi = barnesHutRunner.body(i) as OctTreeLeafNode!;
			Console.OUT.println(bodyi.posx + " " + bodyi.posy + " " + bodyi.posz);
		}

	}

	class ComputePartitionTask {
		var root: OctTreeInternalNode ;
		var lowerBound: int;
		var upperBound: int;
		var hut: BarnesHutOrig!;

		public def this(_lowerBound: int,  _upperBound: int, _hut: BarnesHutOrig) {
			lowerBound = _lowerBound;
			upperBound = _upperBound;
			hut = _hut as BarnesHutOrig!;
		}

		public def run(): void {
			var step: int = 0;
			while (step < hut.ntimesteps) {
				//try {
					next;
					//specialLatch.await();

					// Compute Force first
					var i: int = 0;
					for ( i = lowerBound; i < upperBound; i++) {
						val bodyi = hut.body(i) as OctTreeLeafNode!;
						bodyi.computeForce(root, hut.diameter, hut);
					}
					next;
					//barrier.await();

					// Then advance once all forces have been computed
				
					for (i = lowerBound; i < upperBound; i++) {
						val bodyi = hut.body(i) as OctTreeLeafNode!;
						bodyi.advance(hut);
					}
					next;
					/* if (barrier.await() == 0) {
						// Notify main thread that we are ready
						try {
							partitionLock.lock();
							partitionsDone = true;
							partitionsCondition.signal();
						} finally {
							partitionLock.unlock();
						}
					}*/

					step++;
				/* } catch (InterruptedException e) {
					return;
				} catch (BrokenBarrierException e) {
					return;
				}*/
			}
		}
	}

	private  def runSimulation(): void {
	{
		var i: int;
		val c = Clock.make();
		val lWorkers = Rail.make[ComputePartitionTask](numberOfProcessors) as Rail[ComputePartitionTask]!;
		workers = lWorkers;
		for (i = 0; i < numberOfProcessors; i++) {
			val bounds = getLowerAndUpperBoundsFor(i, numberOfProcessors) as Rail[int]!;
			//val i = workers(i) as ComputePartitionTask!;
		    lWorkers(i) = new ComputePartitionTask(bounds(0), bounds(1), this) as ComputePartitionTask!;
			val workeri = lWorkers(i) as ComputePartitionTask!;
			async clocked(c) workeri.run();
		}

		lasttime = runtime;
	

		for (step = 0; step < ntimesteps; step++) {
			computeCenterAndDiameter();

			// create the tree's root
			val root = OctTreeInternalNode.newNode(centerx, centery, centerz) as OctTreeInternalNode!;

			val radius = diameter * 0.5;
	
			for (i = 0; i < nbodies; i++) {
				root.insert(body(i), radius);
			}

			for (i = 0; i < numberOfProcessors; i++) {
				val workersi = workers(i) as ComputePartitionTask!;
				workersi.root = root;
			}

			curr = 0;
			root.computeCenterOfMass(this);
			next;
			/*try {
				specialLatch.await();
			} catch (InterruptedException e) {
				return;
			} catch (BrokenBarrierException e) {
				return;
			}*/
			next;
			// Each time step needs to wait until the previous one is done first
			// Still need to keep this in a while loop because the await() could be
			// spuriouosly awaken.
			next;
			/*while (!partitionsDone) {
				try {
					partitionLock.lock();
					try {
						partitionsCondition.await();
					} catch (InterruptedException e) {
						return;
					}
				} finally {
					partitionLock.unlock();
				}
			}*/

			partitionsDone = false;
		}

		endtime = System.nanoTime();
		runtime = endtime - starttime;

		if ((run == 0) || (runtime < mintime))
			mintime = runtime;
		run++;
		
	 }
	}

	private def getLowerAndUpperBoundsFor(count: int, numberOfProcessors: int): Rail[int] {
		val results = Rail.make[int](2);
		val increment = nbodies / numberOfProcessors;
		results(0) = count * increment;
		if (count + 1 == numberOfProcessors) {
			results(1) = nbodies;
		} else {
			results(1) = (count + 1) * increment;
		}

		return results;
	}




}
