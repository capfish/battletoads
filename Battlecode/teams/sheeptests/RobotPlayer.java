package sheeptests;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.MapLocation;
import battlecode.common.Clock;
import battlecode.common.Upgrade;

public class RobotPlayer {
	public static MapLocation enemyHQ, myHQ;
	public static int mapHeight, mapWidth;

	public static void run(RobotController rc) {

		enemyHQ = rc.senseEnemyHQLocation();
    	myHQ = rc.senseHQLocation();
    	mapHeight = rc.getMapHeight();
    	mapWidth = rc.getMapWidth();
    	
        while (true) {
            try {
            	if (rc.getType() == RobotType.SOLDIER){
            		if (rc.isActive()) soldierRun(rc);
            	}
            	else if (rc.getType() == RobotType.HQ){
            		hqCode.hqRun(rc, enemyHQ);
            	}
            	else if (rc.getType() == RobotType.ARTILLERY){
            		encampCode.artilleryRun(rc);
            	}
            	rc.yield();
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }
    }
    private static Direction randomDir(RobotController rc) {
        Direction dir = Direction.values()[(int)(Math.random()*8)];
        if(rc.canMove(dir)) return dir;
        else return randomDir(rc);
    }
	private static void soldierRun(RobotController rc) throws GameActionException {
		//MapLocation[] neutralMines = rc.senseMineLocations(rc.getLocation(), 2, Team.NEUTRAL);
		//MapLocation[] enemyMines = rc.senseMineLocations(rc.getLocation(), 2, rc.getTeam().opponent());
        if (rc.senseEncampmentSquare(rc.getLocation())) {
        	rc.captureEncampment(RobotType.ARTILLERY);
        	return;
        }
        MapLocation[] nearbyEncampments;
        nearbyEncampments = rc.senseEncampmentSquares(rc.getLocation(), 16, Team.NEUTRAL);
        if (nearbyEncampments.length != 0) {
        	Direction dir = rc.getLocation().directionTo(nearbyEncampments[0]);
            if (rc.canMove(dir)) rc.move(dir);
            else rc.move(randomDir(rc));
        } else rc.move(randomDir(rc));
	}
}
