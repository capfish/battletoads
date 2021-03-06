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
        rc = rc_;
        height = rc.getMapHeight();
        width = rc.getMapWidth();
        rangeS = RobotType.ARTILLERY.attackRadiusMaxSquared;
        range = (int)Math.sqrt(rangeS);
		Message msg = new Message(rc);
		msg.reset();
		msg.send(Action.CAP_ART, rc.getLocation());
        while (true) {
	        rc.setIndicatorString(1, " roundsTillActive " + rc.roundsUntilActive());
	        Robot[] eenemies = rc.senseNearbyGameObjects(Robot.class, rc.getLocation(), rangeS, rc.getTeam().opponent());
	        rc.setIndicatorString(0, "num enemies " + eenemies.length);
	        if (rc.isActive()) {
	            MapLocation eHQ = rc.senseEnemyHQLocation();
	            if (rc.canAttackSquare(eHQ)) rc.attackSquare(eHQ);  // if enemy HQ within attack distance: attack it.
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
	        rc.yield();
        }
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
	
	public static void shieldsRun(RobotController rc) throws GameActionException {
		Message msg = new Message(rc);
		msg.reset();
		msg.send(Action.CAP_SHIELD, rc.getLocation());
		while (true) {
			if (rc.getEnergon() < 60) {
				msg.reset();
				msg.send(Action.DISTRESS, rc.getLocation());
			}
			rc.yield();
		}
	}
	
	public static void generatorRun(RobotController rc) throws GameActionException {
		Message msg = new Message(rc);
		msg.reset();
		msg.send(Action.CAP_GEN, rc.getLocation());
		while (true) {
			// Or, broadcast before suicide and don't suicide soon after suicide. (instead of upping the prime)
			//if (rc.getTeamPower() > 1000 && (Clock.getRoundNum() % 743 == rc.getRobot().getID() % 743)) rc.suicide();
			rc.yield();
		}
	}
	
	public static void supplierRun(RobotController rc) throws GameActionException {
		Message msg = new Message(rc);
		msg.reset();
		msg.send(Action.CAP_SUP, rc.getLocation());
		while (true) {
			rc.yield();
		}
	}

}
