package team161;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.Team;
import battlecode.common.Upgrade;
import battlecode.engine.instrumenter.RobotMonitor;

public class hqCode {
	private static int width, height, area, num_suppliers, num_generators, dist_btw_HQs, roundsTillCaptured;
	private static Direction dir2enemyHQ;
	private static Team spawnMine;
	private static MapLocation[] encamps;
	private static Message msg;
	private static MapLocation myHQ, enemyHQ, rally_point;
	private static Team myTeam, opponent;

    private static Direction randomDir(RobotController rc, int depth) {
    	if ( depth == 0 ) return null;
        Direction dir = Direction.values()[(int)(Math.random()*8)];
        if(rc.canMove(dir)) return dir;
        else return randomDir(rc, depth -1);
    }

	public static void hqRun(RobotController rc) throws GameActionException {
		width = rc.getMapWidth();
		height = rc.getMapHeight();
		area = height*width;
		encamps = rc.senseEncampmentSquares(rc.getLocation(), 5000, Team.NEUTRAL);
		msg = new Message(rc);
		num_suppliers = num_generators = roundsTillCaptured = 0;
		myHQ = rc.senseHQLocation();
		enemyHQ = rc.senseEnemyHQLocation();
		rally_point = myHQ;
		dist_btw_HQs = myHQ.distanceSquaredTo(enemyHQ);
		dir2enemyHQ = myHQ.directionTo(enemyHQ);
		myTeam = rc.getTeam();
		opponent = rc.getTeam().opponent();

		while (true) {
			roundsTillCaptured --;
			if (rc.isActive()) {
				if (rc.getTeamPower() < 60) {
					if (!rc.hasUpgrade(Upgrade.PICKAXE)) rc.researchUpgrade(Upgrade.PICKAXE);
					//else if(!rc.hasUpgrade(Upgrade.FUSION)) rc.researchUpgrade(Upgrade.FUSION);
					else rc.researchUpgrade(Upgrade.NUKE);
				} else {
					if (Math.random() < 0.6 && !rc.hasUpgrade(Upgrade.PICKAXE)) rc.researchUpgrade(Upgrade.PICKAXE);
					else {
						Direction dir = randomDir(rc, 15);
						spawnMine = rc.senseMine(myHQ.add(dir));
						if ( dir == null ) rc.researchUpgrade(Upgrade.NUKE);
						else if (spawnMine == opponent || spawnMine == Team.NEUTRAL)
                        {
                            dir = randomDir(rc, 15);
                            if ( dir == null ) rc.researchUpgrade(Upgrade.NUKE);
                            else rc.spawn(randomDir(rc, 15));
                        }
                    else rc.spawn(dir);
					}
				}
			}
				//MapLocation[] encamps = rc.senseEncampmentSquares(rc.getLocation(), (width*height)/4, Team.NEUTRAL);
				/*for (MapLocation encamp: encamps) {
					msg.send("1");
					msg.sendLoc(encamp);
				}*/
			
			//find out which sector the end of our mines is in
			//sectors are:
//			System.out.println(Clock.getBytecodesLeft());
			MapLocation frontLines = whichSector(rc);
//			System.out.println(Clock.getBytecodesLeft());

			msg.reset();
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
				}
			}
			if ((roundsTillCaptured <= 0 && rc.getTeamPower() < 40) || Clock.getRoundNum() > 2000 || rc.senseEnemyNukeHalfDone()) {
				msg.send(Action.ATTACK, rc.senseEnemyHQLocation());
				rc.setIndicatorString(0, "attack eHQ");
			}
			else if (((area < 400 || encamps.length < area/10) && Clock.getRoundNum() < 80)
					|| rc.senseNearbyGameObjects(Robot.class, dist_btw_HQs/4, opponent).length != 0) msg.send(Action.DISTRESS, myHQ);
			else {
				Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, 100000, opponent);
				if (enemies.length < 3) rally_point = rally_point.add(dir2enemyHQ, 2); //make these numbers based on #allies later
				else if (enemies.length > 7) rally_point.add(dir2enemyHQ, -2);
				msg.send(Action.RALLY_AT, rally_point);
				if (rc.senseEncampmentSquares(rally_point, 100, Team.NEUTRAL).length < 2) {
					//tell soldiers about faraway encamps.
				}
			}
			msg.send(Action.GEN_SUP, new MapLocation( num_suppliers, num_generators ));
			rc.yield();
		}
	}
	
	public static MapLocation whichSector(RobotController rc)
	{
		int diagonal = dist_btw_HQs;
		int delta = diagonal/5;
		int deltaX = width/5;
		int deltaY = height/5;
		//check middle section
		MapLocation sect3 = new MapLocation(width/2, height/2);
		MapLocation sect2 = new MapLocation(sect3.x - deltaX, sect3.y - deltaY);
		MapLocation sect1 = new MapLocation(sect2.x - deltaX, sect2.y - deltaY);
		
		int sect3Count = rc.senseMineLocations(sect3, delta, myTeam).length;
		int sect2Count = rc.senseMineLocations(sect2, delta, myTeam).length;
		if (sect3Count == 0)
			if (sect2Count > 0)
				return sect2;
			else
				return sect1;
		else
		{
			MapLocation sect4 = new MapLocation(sect3.x + deltaX, sect3.y + deltaY);
			int sect4Count = rc.senseMineLocations(sect4, delta, myTeam).length;
			if (sect4Count == 0)
				return sect3;
			else
			{
				MapLocation sect5 = new MapLocation(sect4.x + deltaX, sect4.y + deltaY);
				int sect5Count = rc.senseMineLocations(sect5, delta, myTeam).length;
				if (sect5Count == 0)
					return sect4;
				return sect5;
			}
		}
	}
}
