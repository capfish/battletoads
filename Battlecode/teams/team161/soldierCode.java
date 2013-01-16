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

	public static void soldierRun(RobotController rc) throws GameActionException {
		myLoc = rc.getLocation();
		target = RobotPlayer.enemyHQ;

		if (rc.isActive())
		{
			//If nearby enemies: ATTACK MODE
			Robot[] enemy = rc.senseNearbyGameObjects(rc.getRobot().getClass(), myLoc, 100, RobotPlayer.enemyTeam);
			if (enemy.length > 0)
				attackMode(rc, enemy);
			else if (!mineMode(rc))
				if (!colonizeMode(rc))
					travelMode(rc);
		}
	}
//check if mines are present before making mines!!!!!
	/*--------------TRAVEL CODE--------------------*/
	
	private static void travelMode(RobotController rc) throws GameActionException
	{
		//ALTERNATE: get a command from the HQ.
		
		//go to enemy base
		//Direction dir = myLoc.directionTo(RobotPlayer.enemyHQ);
		rc.setIndicatorString(0, "travel mode");
		rc.setIndicatorString(1, "target " + target);

		Direction dir = myLoc.directionTo(target);
		if (!rc.canMove(dir)) dir = randomDir(rc);
		Team t = rc.senseMine(myLoc.add(dir));
		if (t == Team.NEUTRAL || t == RobotPlayer.enemyTeam)
			rc.defuseMine(myLoc.add(dir));
		else rc.move(dir);
	}
	
    private static Direction randomDir(RobotController rc) {
        Direction dir = Direction.values()[(int)(Math.random()*8)];
        if(rc.canMove(dir)) return dir;
        else return randomDir(rc);
    }

	/*--------------TRAVEL CODE END--------------------*/

    
	/*--------------COLONIZE CODE--------------------*/

    private static boolean colonizeMode(RobotController rc) throws GameActionException {
		rc.setIndicatorString(0, "colonize mode");
    	if (rc.senseEncampmentSquare(myLoc)) //if encamp is already ours, don't get it!
    	{
    		if (rc.senseCaptureCost() > rc.getTeamPower()) return false;
    		if (myLoc.distanceSquaredTo(RobotPlayer.myHQ) < 64)
    			rc.captureEncampment(RobotType.SHIELDS);
    		else if (myLoc.distanceSquaredTo(RobotPlayer.enemyHQ) < 64 || RobotPlayer.myHQ.directionTo(myLoc) == RobotPlayer.myHQ.directionTo(RobotPlayer.enemyHQ))
    			rc.captureEncampment(RobotType.ARTILLERY);
    		else
    			rc.captureEncampment(RobotType.GENERATOR);
    		return true;
    	}
    	MapLocation[] encamps = rc.senseEncampmentSquares(myLoc, 64, Team.NEUTRAL);
    	if (encamps.length == 0) return false;
    	soldierCode.target = encamps[0];
    	return false;
    	//return setTargetAccessible(rc, encamps);
    }
    private static boolean setTargetAccessible(RobotController rc, MapLocation[] encamps) {
    	for (MapLocation encamp: encamps) 
    		if (rc.senseNearbyGameObjects(Robot.class, encamp, 2, RobotPlayer.myTeam).length < 8) {
    			soldierCode.target = encamp; 
    			return true;
    		}
    	return false;
    	
    }
    
	/*--------------COLONIZE CODE END--------------------*/
    
    
	/*--------------ATTACK CODE--------------------*/
    
    private static void attackMode(RobotController rc, Robot[] enemies) throws GameActionException {
		rc.setIndicatorString(0, "attack mode");

    	chase(rc);
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
    		if (rc.canMove(dir)) rc.move(dir);
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
    		else if (pickaxeSparseMineField(rc))
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
