package attitudebot;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class encampCode {

	public static void artilleryRun(RobotController rc) throws GameActionException {
		// TODO Auto-generated method stub
		if (rc.isActive()) {
			// if enemy HQ within attack distance: attack it.
			MapLocation eHQ = rc.senseEnemyHQLocation();
			if (rc.canAttackSquare(eHQ))
				rc.attackSquare(eHQ);
			else
			{
				//if artillery towers to attack: attack them
				
				//else:
				Robot[] targets = rc.senseNearbyGameObjects(Robot.class, rc.getLocation(), RobotType.ARTILLERY.attackRadiusMaxSquared, rc.getTeam().opponent());
				
			}
		}
		// find enemies within shooting radius.
		
		// if enemies not empty:
		//
	}

	public static void generatorRun(RobotController rc) {
		//UNUSED
	}

	public static void medbayRun(RobotController rc) {
		//UNUSED
		
	}

	public static void shieldsRun(RobotController rc) {
		//UNUSED
	}

	public static void supplierRun(RobotController rc) {
		//UNUSED
	}

}
