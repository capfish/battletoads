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
		rangeS = RobotType.ARTILLERY.sensorRadiusSquared;
		range = (int)Math.sqrt(rangeS);
		int[][] map = null;					

		MapLocation eHQ = rc.senseEnemyHQLocation();
		
		map = new int[range*2+1][range*2+1];					
		MapLocation[] us = rc.senseEncampmentSquares(rc.getLocation(), rangeS, rc.getTeam());
		for (MapLocation l: us) {
			updateMap(rc,l,map,-1);
		}
		updateMap(rc, rc.getLocation(), map, -1);
		
		while (true) {
			if (rc.canAttackSquare(eHQ)) {
				if (rc.isActive()) {
					rc.attackSquare(eHQ);
					rc.yield();
				}
			}
			else if (rc.roundsUntilActive() == 2) {
				map = new int[range*2+1][range*2+1];					
				us = rc.senseEncampmentSquares(rc.getLocation(), rangeS, rc.getTeam());
				for (MapLocation l: us) {
					updateMap(rc,l,map,-1);
				}
				updateMap(rc, rc.getLocation(), map, -1);
			} 
			else if ( rc.senseNearbyGameObjects(Robot.class, rc.getLocation(), rangeS, rc.getTeam().opponent()).length != 0 )
				if (rc.roundsUntilActive() < 2) {
				int damage = 0;
				int x = 0;
				int y = 0;
				Robot[] allies = rc.senseNearbyGameObjects(Robot.class, rc.getLocation(), rangeS, rc.getTeam());
				rc.setIndicatorString(2, "length of allies " + allies.length);
				for (Robot a: allies) {
					RobotInfo info = rc.senseRobotInfo(a);
					updateMap(rc, info, map, -1);
				}
				if(Clock.getBytecodesLeft() < 5000) rc.yield();
				Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, rc.getLocation(), rangeS, rc.getTeam().opponent());
				for (Robot e: enemies) {
					RobotInfo info = rc.senseRobotInfo(e);
					updateMap(rc, info, map, 1);
				}
				
				for (int i = 0; i < range*2+1; i++) for (int j = 0; j < range*2+1; j++) 
					if (damage < map[i][j]) {
						damage = map[i][j];
						x = i;
						y = j;
					}
				/*System.out.println("round num " + Clock.getRoundNum());
				for (int i = 0; i < range*2+1; i++) {
					for (int j = 0; j < range*2+1; j++) System.out.printf("%3d", map[i][j]);
					System.out.println("");
				}*/
				if (damage > 0) {
					MapLocation target = rc.getLocation().add(x - range, y - range);
					rc.attackSquare(target);
					rc.setIndicatorString(0, "attacking" + target);
					rc.yield();
				} else rc.setIndicatorString(0, "not attacking");
			}
			rc.yield();
		}
}
	public static void updateMap(RobotController rc, RobotInfo info, int[][] map, int team) {
		int x = info.location.x - rc.getLocation().x + range;
		int y = info.location.y - rc.getLocation().y + range;
		for (int i = -1; i < 2; i++) for (int j = -1; j < 2; j++)
			if (withinRange(rc, info.location.add(i,j)))
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
	
	public static boolean withinRange(RobotController rc, MapLocation loc) {
		if (loc.x < 0 || loc.x >= width || loc.y < 0 || loc.y >= height) return false;
		if (loc.distanceSquaredTo(rc.getLocation()) <= rangeS) return true;
		return false;
	}

	public static void generatorRun(RobotController rc) {
		if (Clock.getRoundNum() % 100 == 0)
			if (rc.getTeamPower() > rc.senseCaptureCost() * 3 && 
				(rc.senseNearbyGameObjects(Robot.class, rc.getLocation(), 4, rc.getTeam()).length > 2) &&
				(rc.senseNearbyGameObjects(Robot.class, rc.getLocation(), 4, rc.getTeam().opponent()).length == 0)) rc.suicide();
	}

}
