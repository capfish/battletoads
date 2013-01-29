package team161;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.MapLocation;
import battlecode.common.Clock;
import battlecode.common.Upgrade;

public class RobotPlayer {
	public static MapLocation enemyHQ, myHQ;
	public static int mapHeight, mapWidth;
//	public static int shield = 20;

	public static void run(RobotController rc) {

		enemyHQ = rc.senseEnemyHQLocation();
    	myHQ = rc.senseHQLocation();
    	mapHeight = rc.getMapHeight();
    	mapWidth = rc.getMapWidth();
    	
        while (true) {
            try {
            	if (rc.getType() == RobotType.SOLDIER) {
            		SoldierCode.soldierRun(rc);
            	}
            	else if (rc.getType() == RobotType.HQ) {
            		hqCode.hqRun(rc, enemyHQ);
            	}
            	else if (rc.getType() == RobotType.ARTILLERY) {
            		encampCode.artilleryRun(rc);
            	}
            	else if (rc.getType() == RobotType.SHIELDS) {
            		encampCode.shieldsRun(rc);
            	}
            	else if (rc.getType() == RobotType.GENERATOR) {
            		encampCode.generatorRun(rc);
            	}
            	rc.yield();
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }
    }
   	/*
    private static Direction randomDir(RobotController rc) {
        Direction dir = Direction.values()[(int)(Math.random()*8)];
        if(rc.canMove(dir)) return dir;
        else return randomDir(rc);
    }
	private static void soldierRun(RobotController rc) throws GameActionException {
		Message msg = new Message(rc);
		Bug b = new Bug(rc.senseEnemyHQLocation(), rc);
		boolean getShield = false;
		while (true) {
			msg.reset();
			msg.receive(-1);
			if (msg.action == Action.CAP_SHIELD ){//&& b.target.equals(rc.senseEnemyHQLocation())) {
				MapLocation[] encamps = rc.senseEncampmentSquares(rc.getLocation(), 100, Team.NEUTRAL);
				for (MapLocation encamp: encamps)
					if (encamp.distanceSquaredTo(myHQ) < 400 && rc.senseNearbyGameObjects(Robot.class, encamp, 25, rc.getTeam().opponent()).length == 0) {
						b.target = encamp;
						getShield = true;
						break;
					}
			} else if (msg.action == Action.RALLY_AT && shield > 0) {
				if (rc.getLocation().distanceSquaredTo(msg.location) <= 2) {
					shield --;
				}
				b.target = msg.location;
				getShield = false;
			} else {
				b.target = rc.senseEnemyHQLocation();
				getShield = false;
			}
			if (rc.isActive()) {
				if (shield <= 0) b.shieldGo();
				else b.go();
				rc.setIndicatorString(0, "target " + b.target + " getShield " + getShield);
				rc.yield();
				if (rc.getLocation().equals(b.target) && getShield) {
					msg.reset();
					rc.captureEncampment(RobotType.SHIELDS);
					msg.send(Action.CAP_SHIELD, rc.getLocation());
					System.out.println("GOT HERE SOLDIERCODE");
				}
			}
		}
		//MapLocation[] neutralMines = rc.senseMineLocations(rc.getLocation(), 2, Team.NEUTRAL);
		//MapLocation[] enemyMines = rc.senseMineLocations(rc.getLocation(), 2, rc.getTeam().opponent());
        /*if (rc.senseEncampmentSquare(rc.getLocation())) {
        	if (rc.senseCaptureCost() < rc.getTeamPower()) rc.captureEncampment(RobotType.ARTILLERY);
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
*/
}
