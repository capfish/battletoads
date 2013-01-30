package team161;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.Team;
import battlecode.common.Upgrade;

public class hqCode {
	private static boolean hasShield = false;
	private static boolean nukeMode = true;
	private static MapLocation shieldLoc = null;
	private static Message msg;
	private static RobotController rc;	
	private static int width, height, num_suppliers, num_generators, dist_btw_HQs, roundsTillCaptured, killing;
	private static Direction dir2enemyHQ;
	private static Team spawnMine;
	private static MapLocation myHQ, enemyHQ, rally_point;
	private static Team myTeam, opponent;
	private static MapLocation spawnSpot = null;

	
    private static Direction randomDir(RobotController rc, int depth) {
    	if ( depth == 0 ) return null;
        Direction dir = Direction.values()[(int)(Math.random()*8)];
        if(rc.canMove(dir)) return dir;
        else return randomDir(rc, depth -1);
    }
    private static void spawnSoldier(RobotController rc) throws GameActionException {
		Direction dir = randomDir(rc, 15);
		if (dir != null) spawnMine = rc.senseMine(myHQ.add(dir));
		if ( dir == null ) rc.researchUpgrade(Upgrade.NUKE);
		else if (spawnMine == opponent || spawnMine == Team.NEUTRAL)
        {
            dir = randomDir(rc, 15);
            if ( dir == null ) rc.researchUpgrade(Upgrade.NUKE);
            else rc.spawn(randomDir(rc, 15));
        }
		else rc.spawn(dir);
	}
    public static MapLocation whichSector(RobotController rc)
	{
		return whichSectorHelper(rc, myHQ, enemyHQ);
	}
	
	public static MapLocation whichSectorHelper(RobotController rc, MapLocation start, MapLocation end)
	{
		int dist = (int)Math.sqrt(start.distanceSquaredTo(end));
		int delta = 5;
		Direction dir = start.directionTo(end);

		if (dist < 5)
			return start;
		
		MapLocation center = start.add(dir, dist/2);
		int centerCount = rc.senseMineLocations(center, delta, myTeam).length;
		if (centerCount > 10)
			return whichSectorHelper(rc, center, end);
		if (centerCount < 5)
			return whichSectorHelper(rc, start, center);
		return center;
	}
	
    public static void HQrush() throws GameActionException {
    	rc.setIndicatorString(1, "rushmode" + killing);
    	msg.reset();
    	msg.send(Action.RUSH, enemyHQ);

		if (!hasShield) {
			int numEnemyMines = rc.senseMineLocations(rc.getLocation(), 10000, rc.getTeam().opponent()).length;
			if (numEnemyMines > 3) {
				msg.send(Action.CAP_SHIELD, enemyHQ);
				rc.setIndicatorString(0, "want shield");
			}
		} else {
			//tell them to gather at shield;
			msg.send(Action.RALLY_AT, shieldLoc);
			rc.setIndicatorString(0, "dont care about shield");
		}
		if (rc.isActive()) spawnSoldier(rc);
    }
    public static void HQrally() throws GameActionException {
    	rc.setIndicatorString(1, "rallymode");
		roundsTillCaptured --;
		if (rc.isActive()) {
			if (rc.getTeamPower() < 60) {
				if (!rc.hasUpgrade(Upgrade.PICKAXE)) rc.researchUpgrade(Upgrade.PICKAXE);
				else if(!rc.hasUpgrade(Upgrade.FUSION)) rc.researchUpgrade(Upgrade.FUSION);
				else rc.researchUpgrade(Upgrade.NUKE);
			} else {
				if (Math.random() < 0.6) {
					if(!rc.hasUpgrade(Upgrade.PICKAXE)) rc.researchUpgrade(Upgrade.PICKAXE);
					else if(!rc.hasUpgrade(Upgrade.FUSION)) rc.researchUpgrade(Upgrade.FUSION);
					else spawnSoldier(rc);
				}
				else spawnSoldier(rc);
			}
		}
		
		Robot[] friends = rc.senseNearbyGameObjects(Robot.class, 100000, myTeam);
		int soldiers = friends.length - rc.senseAlliedEncampmentSquares().length;
		if ((roundsTillCaptured <= 0 && rc.getTeamPower() < 40) || Clock.getRoundNum() > 2000 || rc.senseEnemyNukeHalfDone()) {
			if (soldiers < 15) {
//				rally_point = myHQ.add(dir2enemyHQ, 3);
				msg.send(Action.RALLY_AT, rally_point);
			}
			else {
				msg.send(Action.ATTACK, enemyHQ);
				rc.setIndicatorString(0, "attack eHQ");
			}
		}
		else if (rc.senseNearbyGameObjects(Robot.class, 36, opponent).length != 0) //dist_btw_HQs/16, opponent).length != 0) 
		{
			msg.send(Action.DISTRESS, myHQ);
			nukeMode = false;
			rc.setIndicatorString(0, "distress");
		}
		//else if ((dist_btw_HQs < 900 || encamps.length < area/10) && Clock.getRoundNum() < 300)
		else {
			int swarmSize = (int)Math.sqrt(dist_btw_HQs)/2;
			if (soldiers < swarmSize) msg.send(Action.RALLY_AT,  rally_point); //rally_point = myHQ.add(dir2enemyHQ, 9);
			else
			{
				MapLocation frontLines = whichSector(rc);

				Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, 100000, opponent);
				if (enemies.length < 3) frontLines = frontLines.add(dir2enemyHQ, 1); //make these numbers based on #allies later
				else if (enemies.length > 4) frontLines = frontLines.add(dir2enemyHQ, -1);
			
				msg.send(Action.RALLY_AT, frontLines);
				//rc.setIndicatorString(0, "rally at" + frontLines.x + ", " + frontLines.y);
			}
			
			if (rc.senseEncampmentSquares(rally_point, 100, Team.NEUTRAL).length < 2) {
				//tell soldiers about faraway encamps.
			}
		}
		msg.send(Action.GEN_SUP, new MapLocation( num_generators, num_suppliers ));
		if (roundsTillCaptured > 0) msg.send(Action.CAPTURING, rc.getLocation());
    }
	public static void hqRun(RobotController rc_, MapLocation enemyHQ_) throws GameActionException {
		killing = 200;
		rc = rc_;
		enemyHQ = enemyHQ_;
		msg = new Message(rc);
		num_suppliers = num_generators = roundsTillCaptured = 0;
		myHQ = rc.senseHQLocation();
		enemyHQ = rc.senseEnemyHQLocation();
		dist_btw_HQs = myHQ.distanceSquaredTo(enemyHQ);
		dir2enemyHQ = myHQ.directionTo(enemyHQ);
		rally_point = myHQ.add(dir2enemyHQ, 3);
		myTeam = rc.getTeam();
		opponent = rc.getTeam().opponent();
		int adj_encamps = rc.senseEncampmentSquares(myHQ, 2, Team.NEUTRAL).length + rc.senseEncampmentSquares(myHQ, 2, myTeam).length;
		if (myHQ.x == 0 || myHQ.x == width) {
			if (myHQ.y == 0 || myHQ.y == height) if (adj_encamps >= 3) spawnSpot = myHQ.add(dir2enemyHQ);
			else if (adj_encamps >= 5) spawnSpot = myHQ.add(dir2enemyHQ);
		} else if (adj_encamps >= 8) spawnSpot = myHQ.add(dir2enemyHQ);
		
		
		while(true) {
			msg.reset();
			if (spawnSpot != null) msg.send(Action.DONT_CAP, spawnSpot);
			for (int i = 0; i < 101; i ++) {
				msg.receive(i);
				if (msg.action != null) {
					if (msg.action == Action.CAP_GEN) {
						num_generators ++;
						msg.send(Action.CAP, msg.location);
					}
					else if (msg.action == Action.CAP_SUP) {
						num_suppliers ++;
						msg.send(Action.CAP, msg.location);
					}
					else if (msg.action == Action.DEFUSING) {
						msg.send(Action.DEFUSING, msg.location);
					}
					else if (msg.action == Action.CAPTURING) {
						roundsTillCaptured = 25;
						msg.send(Action.CAPTURING, msg.location);
					}
					else if (msg.action == Action.KILLING && msg.location.equals(enemyHQ)) {
						killing = 100;
					}
					else if (msg.action == Action.CAP_SHIELD) {
						hasShield = true;
						shieldLoc = msg.location;
					}
					else if (msg.action == Action.DISTRESS) {
						hasShield = false;
						nukeMode = false;
					}
					else if (msg.action == Action.ENEMY) {
						if (msg.location.distanceSquaredTo(enemyHQ) > 30)
							nukeMode = false;
					}
				}
			}
			killing --;
			if (nukeMode && Clock.getRoundNum() > 200 && rc.senseNearbyGameObjects(Robot.class, 100000, myTeam).length < 10)
			{
				rc.researchUpgrade(Upgrade.NUKE);
				msg.send(Action.ATTACK, myHQ);
			}
			else if (killing > 0 && rc.senseNearbyGameObjects(Robot.class, 36, opponent).length == 0)
				HQrush();
			else HQrally();
			rc.yield();
		}
	}
}
