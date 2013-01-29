package dumbbot;

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
		rc = rc_;
		height = rc.getMapHeight();
		width = rc.getMapWidth();
		rangeS = RobotType.ARTILLERY.attackRadiusMaxSquared;
		range = (int)Math.sqrt(rangeS);
		rc.setIndicatorString(1, " roundsTillActive " + rc.roundsUntilActive());
		Robot[] eenemies = rc.senseNearbyGameObjects(Robot.class, rc.getLocation(), rangeS, rc.getTeam().opponent());
		rc.setIndicatorString(0, "num enemies " + eenemies.length);
		if (rc.isActive()) {
			MapLocation eHQ = rc.senseEnemyHQLocation();
			if (rc.canAttackSquare(eHQ)) rc.attackSquare(eHQ); 	// if enemy HQ within attack distance: attack it.
			else {
				Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, rc.getLocation(), rangeS, rc.getTeam().opponent());
				for (Robot enemy: enemies) {
					MapLocation enemy_loc = rc.senseRobotInfo(enemy).location;
					if (isGood(enemy_loc)) {
						rc.attackSquare(enemy_loc);
						break;
					}
				}
			}
		}
		//rc.yield();
	}
	public static boolean isGood(MapLocation loc) throws GameActionException {
		Robot[] neighbors = rc.senseNearbyGameObjects(Robot.class, 2);
		int goodness = 0;
		for (Robot neighbor: neighbors) {
			if (rc.senseRobotInfo(neighbor).team == rc.getTeam()) goodness --;
			else goodness ++;
		}
		if (goodness <= 0) return true;
		return false;
	}

}
