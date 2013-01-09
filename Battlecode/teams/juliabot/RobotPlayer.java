package juliabot;


import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;


public class RobotPlayer {
	public static void run(RobotController rc) {
		while (true) {
			try {
				if (rc.getType() == RobotType.HQ) {
					if (rc.isActive()) {
						// Spawn a soldier
						Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
						if (rc.canMove(dir)) rc.spawn(dir);
					}
				} else if (rc.getType() == RobotType.SOLDIER) {
					if (rc.isActive()) {
						/*if (Clock.getRoundNum() < 50) {
							MapLocation[] encampments = rc.senseAllEncampmentSquares();
							rc.move(rc)
						}*/
						MapLocation[] neutralMines = rc.senseMineLocations(rc.getLocation(), 2, Team.NEUTRAL);
						MapLocation[] enemyMines = rc.senseMineLocations(rc.getLocation(), 2, rc.getTeam().opponent());
						if (enemyMines.length != 0 )
							rc.defuseMine(enemyMines[0]);
						else if (neutralMines.length != 0)
							rc.defuseMine(neutralMines[0]);
						if (rc.getLocation().distanceSquaredTo(rc.senseHQLocation()) < 16 && Math.random()<0.05) {
							// Lay a mine 
								if(rc.senseMine(rc.getLocation())==null) rc.layMine();
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
					
					if (Math.random()<0.01 && rc.getTeamPower()>5) {
						// Write the number 5 to a position on the message board corresponding to the robot's ID
						rc.broadcast(rc.getRobot().getID()%GameConstants.BROADCAST_MAX_CHANNELS, 5);
					}
				}

				// End turn
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
}
