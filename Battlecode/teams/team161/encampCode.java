package team161;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class encampCode {
	public static int height, width, rangeS, range;
	public static RobotController rc;
	public static void artilleryRun(RobotController rc_) throws GameActionException {
		height = rc.getMapHeight();
		width = rc.getMapWidth();
		rangeS = RobotType.ARTILLERY.attackRadiusMaxSquared;
		range = (int)Math.sqrt(rangeS);
		rc = rc_;
		if (rc.isActive()) {
			MapLocation eHQ = rc.senseEnemyHQLocation();
			if (rc.canAttackSquare(eHQ)) rc.attackSquare(eHQ); 	// if enemy HQ within attack distance: attack it.
			else {
				Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, rc.getLocation(), rangeS, rc.getTeam().opponent());
				Robot[] allies = rc.senseNearbyGameObjects(Robot.class, rc.getLocation(), rangeS, rc.getTeam());
				
				for (Robot enemy: enemies) {
					MapLocation enemy_loc = rc.senseRobotInfo(enemy).location;
					int tally = 0;
					for (Robot ally: allies) {
						if (tally == 2) break;
						MapLocation ally_loc = rc.senseRobotInfo(ally).location;
						if (ally_loc.isAdjacentTo(enemy_loc)) tally ++;
					}
				}
			}
		}
	}
	public boolean isGood(MapLocation loc) throws GameActionException {
		Robot[] neighbors = rc.senseNearbyGameObjects(Robot.class, 2);
		int goodness = 0;
		for (Robot neighbor: neighbors) {
			if (rc.senseRobotInfo(neighbor).team == rc.getTeam()) goodness --;
			else goodness ++;
		}
		if (goodness >= 0) return true;
		return false;
	}
	
	public static void shieldsRun(RobotController rc) throws GameActionException {
		Message msg = new Message(rc);
		while (true) {
			if (rc.getEnergon() < 60) {
				msg.reset();
				msg.send(Action.DISTRESS, rc.getLocation());
			}
			rc.yield();
		}
	}

}
