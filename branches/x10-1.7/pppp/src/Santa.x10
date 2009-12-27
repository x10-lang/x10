/** Santa, the elves and the reindeer

<p> This problem was specified originally by Trono in 1994. Also comments
by Nick Benton were useful in providing the formulation below. Ben Ari
has also published a solution in Ada.

<ul>
<li>
Santa repeatedly sleeps until wakened by either all of his nine
 reindeer, back from their holidays, or by a group of three of his
 ten elves.

<li> If awakened by the reindeer, he harnesses each of them to his
 sleigh, delivers toys with them and finally unharnesses them
 (allowing them to off on holiday).

<li> If awakened by a group of elves, he shows each of the group into
 his study, consults them on toy R\&D and finally shows them each out
 (allowing them to go back to work).

<li> Santa should give priority to the reindeer in the case that
 there is both a group of elves and a group of reindeer waiting.
</ul>

<p>The following errors should be avoided:
<ul>

<li> Once Santa has started to show elves around his office, extra
 elves should not be able to sneak into the group.

<li> Santa should not be able to go off to deliver toys while the
 reindeer are still waiting in the stable.
</ul>

@author vj This problem was assigned in the finals of the PPPP Course at Columbia, Fall 2009.
 */
public class Santa {
    static class Team(n:Int) {
	var team:Rail[Worker];
	var ready:Boolean=false;
	def this(n:Int) {
	    property(n);
	    team = Rail.makeVar[Worker](n);
	}
    }
    static class Worker(room:Room, santa:Santa) {
	var readyForWork:Boolean =false;
	var doneWithWork:Boolean =false;
	def this(room:Room, santa:Santa) {
	    property(room, santa);
	}
	def startWork() { // called by Santa
	    when (readyForWork)
		readyForWork=false;
	}
	def stopWork() { // called by Santa
	    atomic doneWithWork=true;
	}
	def run() {
	    while (true) {
		room.register(this);
		atomic readyForWork=true;
		// work
		when (doneWithWork) 
		    doneWithWork=false;
	    }
	}
    }
    static class Elf extends Worker {
	def this(room:Room, santa:Santa) {
	    super(room, santa);
	}
	static def make(room:Room, santa:Santa) {
	    val elf = new Elf(room, santa);
	    async elf.run();
	    return elf;
	}
    }
    static class Reindeer extends Worker {
	def this(stable:Room, santa:Santa) {
	    super(stable, santa);
	}
	static def make(stable:Room, santa:Santa) {
	    val reindeer = new Reindeer(stable, santa);
	    async reindeer.run();
	    return reindeer;
	}
    }
    static class Room(size:Int, santa:Santa) {
	var team:Team;
	var count:Int=0;
	def this(size:Int, santa:Santa) {
	    property(size, santa);
	    team = new Team(size);
	}
	def register(e:Worker) {
	    val team:Team = team(e);
	    await team.ready;
	}
	def team(e:Worker):Team {
	    var completed:Boolean=false;
	    atomic {
		team.team(count++)=e;
		val result = team;
		if (count==size-1) {
		    team.ready=true;
		    completed=true;
		    team = new Team(size);
		    count=0;
		} 
	    }
	    if (completed)
		e.santa.awake(team);
	    return result;
	}
    }
    var elves:Team = null;
    var reindeer:Team = null;
    var sleeping:Boolean = true;
    static def make() {
	val santa = new Santa();
	async santa.run();
	return santa;
    }
    /** Called by Room on Santa to notify
	Santa of a team's arrival.
    */
    def awake(t:Team) { 
	when (sleeping) {
	    if (t.team.length()==3) 
		elves = t;
	    else
		reindeer = t;
	    sleeping=false;
	}
    }
    def run() {
	while (true) {
	    var myTeam:Team = null;
	    atomic sleeping = true;
	    // sleep until elves or reindeer arrive
	    when (elves != null || reindeer != null) {
		if (reindeer != null) {
		    // give preference to reindeer.
		    myTeam=reindeer;
		    reindeer = null;
		} else {
		    myTeam = elves;
		    elves=null;
		}
	    }
	    for (worker:Worker in myTeam.team) worker.startWork();
	    // work
	    for (worker:Worker in myTeam.team) worker.stopWork();
	}
    }

    public static def main(Rail[String]) {
	finish {
	    val santa = Santa.make();
	    val room = new Room(3, santa);
	    val elves = Rail.makeVal(10, (i:Int)=>Elf.make(room,santa));
	    val stable = new Room(9, santa);
	    val reindeer = Rail.makeVal(9, (i:Int)=>Reindeer.make(stable,santa));
	}
    }
}