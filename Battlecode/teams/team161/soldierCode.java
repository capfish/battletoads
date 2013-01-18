package team161;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.Upgrade;

public class soldierCode {
	private static MapLocation myLoc;
	private static MapLocation target;
	private static Direction prev;
	private static Message msg;


	public static void soldierRun(RobotController rc) throws GameActionException {
		msg = new Message(rc);
		while (true) {
			myLoc = rc.getLocation();
			target = RobotPlayer.enemyHQ;
			msg.reset();
			rc.setIndicatorString(0, "");
			rc.setIndicatorString(1, "");
			String command = msg.receive();
			if (command.equals("10")) {
				String t = msg.receive();
				int i = t.indexOf(':');
				int x = Integer.parseInt(t.substring(0, i));
				int y = Integer.parseInt(t.substring(i+1));
				target = new MapLocation(x,y);
			}
			if (rc.isActive()) {
				Robot[] enemy = rc.senseNearbyGameObjects(rc.getRobot().getClass(), myLoc, 49, RobotPlayer.enemyTeam);
				//if (enemy.length > 0) 				//If nearby enemies: ATTACK MODE
				//	attackMode(rc, enemy);
				//	flee(rc);
				if (target.equals(RobotPlayer.enemyHQ)) travelMode(rc);
				else if (!mineMode(rc))
					if (!colonizeMode(rc))
						travelMode(rc);
			}
			rc.yield();
		}
	}
//check if mines are present before making mines!!!!!
	/*--------------TRAVEL CODE--------------------*/
	
	private static void travelMode(RobotController rc) throws GameActionException
	{
		//ALTERNATE: get a command from the HQ.
		
		//go to enemy base
		rc.setIndicatorString(0, "travel mode");
		if (myLoc.equals(target)) return;
		Direction dir = myLoc.directionTo(target);
		if (!rc.canMove(dir)) dir = randomDir(rc, 10);
		Team t = rc.senseMine(myLoc.add(dir));
		if (t == Team.NEUTRAL || t == RobotPlayer.enemyTeam) {
			rc.defuseMine(myLoc.add(dir));
			rc.setIndicatorString(1, "target " + target + " defusing in direction " + dir);

		}
		else if (dir != Direction.NONE) {
			rc.move(dir);
			prev = dir;
			rc.setIndicatorString(1, "target " + target + " moved in direction " + dir);

		} else rc.setIndicatorString(1, "target " + target + " something is wrong");


	}
	
    private static Direction randomDir(RobotController rc, int depth) {
    	if (depth == 0) return Direction.NONE;
        Direction dir = Direction.values()[(int)(Math.random()*8)];
        if(rc.canMove(dir)) return dir;
        else return randomDir(rc, depth-1);
    }

	/*--------------TRAVEL CODE END--------------------*/

    
	/*--------------COLONIZE CODE--------------------*/

    private static boolean colonizeMode(RobotController rc) throws GameActionException {
		rc.setIndicatorString(0, "colonize mode");
    	if (rc.senseEncampmentSquare(myLoc)) { //if encamp is already ours, can't move there.
    		if (prev != null) {
    			for (int i = 0; i <= 2; i++) {
    				Direction dir = Direction.values()[(prev.ordinal()+i)%8];
    				if (rc.senseEncampmentSquare(myLoc.add(dir))) {
    					if (rc.canMove(dir)) {
    						rc.move(dir);
    						rc.yield();
    						return capture(rc);
    					}
    				}
    				dir = Direction.values()[(prev.ordinal()-i+8)%8];
    				if (rc.senseEncampmentSquare(myLoc.add(dir))) {
    					if (rc.canMove(dir)) {
    						rc.move(dir);
    						rc.yield();
    						return capture(rc);
    					}
    				}
    			}
    		}
        	return capture(rc);
    	}
    	MapLocation[] encamps = rc.senseEncampmentSquares(myLoc, 64, Team.NEUTRAL);
    	if (encamps.length == 0) return false;
    	/*int minDist = 10000;
    	MapLocation minEncamp = encamps[0];
    	for (MapLocation encamp: encamps) {
    		int dist = myLoc.distanceSquaredTo(encamp);		//code for getting closest encamp;
    		if (dist < minDist) {
    			minDist = dist;
    			minEncamp = encamp;
    		}
    	}
    	soldierCode.target = minEncamp;*/
    	target = encamps[(int)Math.random()*encamps.length];
    	
    	return false;
    }
    private static boolean surrounded(RobotController rc, MapLocation loc) {
    	int sides = 0;
    	for (int i = 0; i < 8; i++)
    		if (rc.senseEncampmentSquare(loc.add(Direction.values()[i]))) sides++;
    	if (sides > 7) return true;
    	return false;
    }
    private static boolean capture(RobotController rc) throws GameActionException{
    	if (rc.senseCaptureCost() > rc.getTeamPower()) {
    		rc.setIndicatorString(1, "not enough power");
    		return false;
    	}
    	//broadcast that its colonizing shit so people don't try to go to it.
    	myLoc = rc.getLocation();
    	if (rc.getTeamPower() < 2.1*rc.senseCaptureCost()) rc.captureEncampment(RobotType.GENERATOR);
    	else if (surrounded(rc, myLoc)) rc.captureEncampment(RobotType.MEDBAY);
    	else if (myLoc.distanceSquaredTo(RobotPlayer.myHQ) < 64) rc.captureEncampment(RobotType.ARTILLERY);
		else if (myLoc.distanceSquaredTo(RobotPlayer.enemyHQ) < 64) rc.captureEncampment(RobotType.ARTILLERY);
		else if (RobotPlayer.myHQ.directionTo(myLoc) == RobotPlayer.myHQ.directionTo(RobotPlayer.enemyHQ)
			  && RobotPlayer.enemyHQ.directionTo(myLoc) == RobotPlayer.enemyHQ.directionTo(RobotPlayer.myHQ))
			rc.captureEncampment(RobotType.ARTILLERY);
		else if (Math.random() < 0.5) rc.captureEncampment(RobotType.SUPPLIER);
		else rc.captureEncampment(RobotType.GENERATOR);
		return true;
    }
    
	/*--------------COLONIZE CODE END--------------------*/
    
    
	/*--------------ATTACK CODE--------------------*/
    
    private static void attackMode(RobotController rc, Robot[] enemies) throws GameActionException {
		rc.setIndicatorString(0, "attack mode");
    	if (!chase(rc)) if (rc.canMove(prev)) rc.move(prev);
    	
    }
    
    private static boolean gang(RobotController rc) throws GameActionException {
    	Robot[] allied = rc.senseNearbyGameObjects(Robot.class, myLoc, 2, RobotPlayer.myTeam);
    	Robot[] enemy = rc.senseNearbyGameObjects(Robot.class, myLoc, 2, RobotPlayer.enemyTeam);
    	if (enemy.length!=0 && allied.length > enemy.length) return true;
    	
    	allied = rc.senseNearbyGameObjects(rc.getRobot().getClass(), rc.getLocation(), 10, rc.getTeam());
    	enemy = rc.senseNearbyGameObjects(rc.getRobot().getClass(), rc.getLocation(), 10, rc.getTeam().opponent());
    	int[] alliedBins = new int[8];
    	int[] enemyBins = new int[8];
    	for (int i = 0; i < 8; i++) 
    		for (int j = 0; j < allied.length; j++)
    			if (dir2robo(rc, allied[j]) == Direction.values()[i]) alliedBins[i]++;
    	for (int i = 0; i < 8; i++) 
    		for (int j = 0; j < enemy.length; j++)
    			if (dir2robo(rc, enemy[j]) == Direction.values()[i]) enemyBins[i]++;
    	for (int i = 0; i < 8; i++)
    		if (enemyBins[i] > 0 && alliedBins[i] > enemyBins[i]) 
    			if (rc.canMove(Direction.values()[i])) {
    				rc.move(Direction.values()[i]);
    				return true;
    			}
    	return false;
    }
    private static Direction dir2robo(RobotController rc, Robot robo) throws GameActionException {
    	return rc.getLocation().directionTo((rc.senseRobotInfo(robo).location));
    }
    private static boolean chase(RobotController rc) throws GameActionException {
    	Robot[] enemy = rc.senseNearbyGameObjects(rc.getRobot().getClass(), rc.getLocation(), RobotType.SOLDIER.sensorRadiusSquared, rc.getTeam().opponent());
    	if (enemy.length != 0) {
    		Direction dir = dir2robo(rc, enemy[0]);
    		if (rc.canMove(dir)) {
    			rc.move(dir);
    			prev = dir;
    		}
    		return true;
    	}
    	return false;
    }
    
    private static boolean flee(RobotController rc) throws GameActionException {
    	Robot[] enemy = rc.senseNearbyGameObjects(rc.getRobot().getClass(), rc.getLocation(), RobotType.SOLDIER.sensorRadiusSquared, rc.getTeam().opponent());
    	if (enemy.length != 0) {
    		Direction dir = dir2robo(rc, enemy[0]).opposite();
    		if (rc.canMove(dir)) {
    			rc.move(dir);
    			prev = dir;
    		}
    		return true;
    	}
    	return false;
    }
    
	/*--------------ATTACK CODE END--------------------*/


	/*--------------MINE CODE--------------------*/
    
    private static boolean mineMode(RobotController rc) throws GameActionException {
		rc.setIndicatorString(0, "mine mode");

    	if (rc.senseMine(myLoc) != null)
    		return false;
    	if (rc.hasUpgrade(Upgrade.PICKAXE))
    	{
    		if (rc.getLocation().distanceSquaredTo(rc.senseHQLocation()) < 25 && pickaxeMineField(rc))
    		{
    			rc.layMine();
    			return true;
    		}
    		else if (pickaxeMineField(rc))
    		{
    			rc.layMine();
    			return true;
    		}    	
    	}
    	else if (rc.getLocation().distanceSquaredTo(rc.senseHQLocation()) < 25 && sparseMineField(rc))
    	{
    		rc.layMine();
    		return true;
    	}
    	else if (sparserMineField(rc))
    	{
    		rc.yield();
    		return true;
    	}
    	return false;
    }

    
    private static boolean sparseMineField(RobotController rc) {
    	if (rc.getLocation().x % 2 == 0 && rc.getLocation().y % 2 == 0) return true;
    	else return false;
    }
    private static boolean sparserMineField(RobotController rc) {
    	int a = rc.getLocation().x % 2;
    	int b = rc.getLocation().y % 2;
    	if ((a+b) % 2 == 0) return true;
    	else return false;
    }
    private static boolean pickaxeMineField(RobotController rc) {
    	int a = rc.getLocation().x % 5;
    	int b = rc.getLocation().y % 5;
    	if (b == a*2 % 5) return true;
    	else return false;
    }
    private static boolean pickaxeSparseMineField(RobotController rc) {
    	int a = rc.getLocation().x % 4;
    	int b = rc.getLocation().y % 4;
    	if((a+b) % 4 == 0 && a == b) return true;
    	return false;
    }
    
	/*--------------MINE CODE END--------------------*/

}
