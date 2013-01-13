package juliabot;


import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.Upgrade;


public class RobotPlayer {
	private static MapLocation enemyHQ;
	private static MapLocation myHQ;
	public static void run(RobotController rc) {
		enemyHQ = rc.senseEnemyHQLocation();
		myHQ = rc.senseHQLocation();
		System.out.println(myHQ);
		for (int i = 0; i < Direction.values().length; i++) System.out.println(Direction.values()[i]);
		while (true) {
			rc.yield();
			/*try {
				if (rc.getType() == RobotType.HQ) {
					if (rc.isActive()) {
							Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
							if (rc.canMove(dir)) rc.spawn(dir);
							for (int i = 0; i < 5; i++) rc.researchUpgrade(Upgrade.PICKAXE);
					}
				} else if (rc.getType() == RobotType.SOLDIER) {
					if (rc.isActive()) {
						if (Clock.getRoundNum() < 100) {
							if (rc.senseEncampmentSquare(rc.getLocation())) rc.captureEncampment(RobotType.GENERATOR);
							else {
								MapLocation[] encampments = rc.senseEncampmentSquares(rc.getLocation(), 25, Team.NEUTRAL);
								if (encampments.length != 0) rc.move(rc.getLocation().directionTo(encampments[(int)(Math.random()*encampments.length)]));
							}
						}
						if (rc.isActive()) {
							MapLocation[] neutralMines = rc.senseMineLocations(rc.getLocation(), 2, Team.NEUTRAL);
							MapLocation[] enemyMines = rc.senseMineLocations(rc.getLocation(), 2, rc.getTeam().opponent());
							if (enemyMines.length != 0 )
								rc.defuseMine(enemyMines[0]);
							else if (neutralMines.length != 0)
								rc.defuseMine(neutralMines[0]);
							if ((rc.getLocation().distanceSquaredTo(rc.senseHQLocation()) < 25 && Math.random()<0.5) || Math.random()<0.05) {
								if(rc.senseMine(rc.getLocation())==null) rc.layMine(); // Lay a mine
							} else { 
								// move towards headquarters
								//Direction dir = Direction.values()[(int)(Math.random()*8)];
								Direction dir;
								if(rc.getLocation().distanceSquaredTo(rc.senseHQLocation()) > 16) dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
								else {
									dir = randomDir(rc);
								}
								if (rc.isActive()) {
									if(rc.canMove(dir)) {
										rc.move(dir);
										//rc.setIndicatorString(0, "Last direction moved: "+dir.toString());
									} else {
										rc.move(randomDir(rc));
									}
								}
							}
						}
					}
					
					if (Math.random()<0.01 && rc.getTeamPower()>5) {
						// Write the number 5 to a position on the message board corresponding to the robot's ID
						rc.broadcast(rc.getRobot().getID()%GameConstants.BROADCAST_MAX_CHANNELS, 5);
					}
				}

				// End turn
				rc.yield();
			} catch (Exception e) {
				e.printStackTrace();
			}*/
		}
	}
	private static Direction randomDir(RobotController rc) {
		Direction dir = Direction.values()[(int)(Math.random()*8)];
		if(rc.canMove(dir)) return dir;
		else return randomDir(rc);
	}
	/*private static void chase(RobotController rc) {
		if (rc.senseNearbyGameObjects(rc.getType(), 16, rc.getTeam().opponent())) rc.move(rc.getLocation().directionTo(location))
	}*/
}
