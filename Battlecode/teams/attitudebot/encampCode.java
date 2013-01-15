package attitudebot;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class encampCode {
	public static int height, width, range;
	public static void artilleryRun(RobotController rc) throws GameActionException {
		height = rc.getMapHeight();
		width = rc.getMapWidth();
		range = RobotType.ARTILLERY.attackRadiusMaxSquared;
		if (rc.isActive()) {
			MapLocation eHQ = rc.senseEnemyHQLocation();
			if (rc.canAttackSquare(eHQ)) rc.attackSquare(eHQ); 	// if enemy HQ within attack distance: attack it.
			else {
				//if encampments to attack: attack them
				MapLocation[] targets = rc.senseEncampmentSquares(rc.getLocation(), range, rc.getTeam().opponent());
				if (targets.length != 0) rc.attackSquare(targets[0]);
				else {
					Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, rc.getLocation(), range, rc.getTeam().opponent());
					Robot[] allies = rc.senseNearbyGameObjects(Robot.class, rc.getLocation(), range, rc.getTeam());
					
					int[][] map = new int[range*2+1][range*2+1];
					for (int i = 0; i < range*2+1; i++) for (int j = 0; j < range*2+1; j++) map[i][j] = 0;
					for (Robot e: enemies) {
						RobotInfo info = rc.senseRobotInfo(e);
						updateMap(rc, info, map, 1);
					}
					for (Robot a: allies) {
						RobotInfo info = rc.senseRobotInfo(a);
						updateMap(rc, info, map, -1);
					}
					int damage = 0;
					int x = 0;
					int y = 0;
					
					for (int i = 0; i < range*2+1; i++) for (int j = 0; j < range*2+1; j++) 
						if (damage < map[i][j]) {
							damage = map[i][j];
							x = i;
							y = j;
						}
					if (damage > 0) rc.attackSquare(new MapLocation(rc.getLocation().x + x, rc.getLocation().y + y));
				}
			}
		}
	}
	public static void updateMap(RobotController rc, RobotInfo info, int[][] map, int team) {
		int x = info.location.x - rc.getLocation().x + range;
		int y = info.location.y - rc.getLocation().y + range;
		for (int i = -1; i < 2; i++) for (int j = -1; j < 2; j++)
			if (withinRange(rc, new MapLocation(info.location.x + i,info.location.y + j)))
				if (i == 0 && j == 0) map[x][y] += team * Math.min(40, (int) info.energon);
				else map[x+i][y+j] += team * Math.min(20, (int) info.energon);
	}
	public static boolean withinRange(RobotController rc, MapLocation loc) {
		if (loc.x < 0 || loc.x >= width || loc.y < 0 || loc.y >= height) return false;
		if (loc.distanceSquaredTo(rc.getLocation()) <= range) return true;
		return false;
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
