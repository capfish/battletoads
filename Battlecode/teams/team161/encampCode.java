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
	//public static double range;
	public static void artilleryRun(RobotController rc) throws GameActionException {
		height = rc.getMapHeight();
		width = rc.getMapWidth();
		//range = RobotType.ARTILLERY.attackRadiusMaxSquared;
		rangeS = RobotType.ARTILLERY.sensorRadiusSquared;
		range = (int)Math.sqrt(rangeS);
		if (rc.isActive()) {
			MapLocation eHQ = rc.senseEnemyHQLocation();
			if (rc.canAttackSquare(eHQ)) rc.attackSquare(eHQ); 	// if enemy HQ within attack distance: attack it.
			else {
					MapLocation[] us = rc.senseEncampmentSquares(rc.getLocation(), range, rc.getTeam());
					Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, rc.getLocation(), rangeS, rc.getTeam().opponent());
					Robot[] allies = rc.senseNearbyGameObjects(Robot.class, rc.getLocation(), rangeS, rc.getTeam());
					
					int[][] map = new int[range*2+1][range*2+1];
					for (int i = 0; i < range*2+1; i++) for (int j = 0; j < range*2+1; j++) map[i][j] = 0;
					for (MapLocation l: us) {
						updateMap(rc,l,map,-1);
					}
					for (Robot e: enemies) {
						RobotInfo info = rc.senseRobotInfo(e);
						updateMap(rc, info, map, 1);
					}
					for (Robot a: allies) {
						RobotInfo info = rc.senseRobotInfo(a);
						updateMap(rc, info, map, -1);
					}
					updateMap(rc, map, -1);
					int damage = 0;
					int x = 0;
					int y = 0;
					
					for (int i = 0; i < range*2+1; i++) for (int j = 0; j < range*2+1; j++) 
						if (damage < map[i][j]) {
							damage = map[i][j];
							x = i;
							y = j;
						}
					System.out.println(Clock.getRoundNum());
					for (int i = 0; i < range*2+1; i++) {
						for (int j = 0; j < range*2+1; j++) System.out.printf("%3d", map[i][j]);
						System.out.println("");
					}
					if (damage > 0) {
						MapLocation target = new MapLocation(rc.getLocation().x + x - range, rc.getLocation().y + y - range);
						rc.attackSquare(target);
						rc.setIndicatorString(0, "attacking" + target);
						rc.setIndicatorString(1, "relative " + x + " " + y + " damage" + damage);
					} else rc.setIndicatorString(0, "not attacking");
				}
			//}
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
	public static void updateMap(RobotController rc, MapLocation loc, int[][] map, int team) {
		int x = loc.x - rc.getLocation().x + range;
		int y = loc.y - rc.getLocation().y + range;
		for (int i = -1; i < 2; i++) for (int j = -1; j < 2; j++)
			if (withinRange(rc, new MapLocation(loc.x + i,loc.y + j)))
				if (i == 0 && j == 0) map[x][y] += team * 40;
				else map[x+i][y+j] += team * 20;
	}
	public static void updateMap(RobotController rc, int[][] map, int team) {
		int x = range;
		int y = range;
		for (int i = -1; i < 2; i++) for (int j = -1; j < 2; j++)
			if (withinRange(rc, new MapLocation(rc.getLocation().x + i,rc.getLocation().y + j)))
				if (i == 0 && j == 0) map[x][y] += team * Math.min(40, (int) rc.getEnergon());
				else map[x+i][y+j] += team * Math.min(20, (int) rc.getEnergon());
	}
	
	public static boolean withinRange(RobotController rc, MapLocation loc) {
		if (loc.x < 0 || loc.x >= width || loc.y < 0 || loc.y >= height) return false;
		if (loc.distanceSquaredTo(rc.getLocation()) <= rangeS) return true;
		return false;
	}

	public static void generatorRun(RobotController rc) {
		//UNUSED
	}

}
