package dumbbot; 

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.MapLocation;
import battlecode.common.Clock;
import battlecode.common.Upgrade;

/** The example funcs player is a player meant to demonstrate basic usage of the most common commands.
 * Robots will move around randomly, occasionally mining and writing useless messages.
 * The HQ will spawn soldiers continuously. 
 */

public class RobotPlayer {
    public static void run(RobotController rc) {
        while (true) {
            try {
                if (rc.getType() == RobotType.HQ) {
                    if (rc.isActive()) {
                        if (Clock.getRoundNum() < 80) rc.researchUpgrade(Upgrade.PICKAXE);
                        // Spawn a soldier
                        else {
                            Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());//randomDir(rc);
                            if (rc.canMove(dir)) rc.spawn(dir);
                        }
                    }

                } else if (rc.getType() == RobotType.SOLDIER) {
                    if (rc.isActive()) {

                        MapLocation[] neutralMines = rc.senseMineLocations(rc.getLocation(), 2, Team.NEUTRAL);
                        MapLocation[] enemyMines = rc.senseMineLocations(rc.getLocation(), 2, rc.getTeam().opponent());
                        MapLocation[] nearbyEncampments = new MapLocation[]{};
                        if (Clock.getRoundNum() < 250) {
                            nearbyEncampments = rc.senseEncampmentSquares(rc.getLocation(), 16, Team.NEUTRAL);
                        //    MapLocation[] allEncampments = rc.senseAllEncampmentSquares();
                        //MapLocation[] myEncampments = rc.senseAlliedEncampmentSquares();
                        }
                                                
                        if (enemyMines.length != 0) rc.defuseMine(enemyMines[0]);
                        else if (neutralMines.length != 0) rc.defuseMine(neutralMines[0]);

                        if(Clock.getRoundNum() < 250){
                            if (Math.random() < .5) {// && rc.getLocation().distanceSquaredTo(rc.senseHQLocation()) < 16) {
                                // Lay a mine 
                                if(rc.senseMine(rc.getLocation())==null) rc.layMine();
                            } else if (nearbyEncampments.length != 0) {
                                if (rc.senseEncampmentSquare(rc.getLocation())) rc.captureEncampment(RobotType.GENERATOR);
                                else {
                                    Direction dir = rc.getLocation().directionTo(nearbyEncampments[0]);
                                    if (rc.canMove(dir)) rc.move(dir);
                                    else rc.move(randomDir(rc));
                                }
                            } else { 
                                // Choose a random direction, and move that way if possible
                                Direction dir = randomDir(rc);
                                if(rc.canMove(dir)) {
                                    rc.move(dir);
                                    rc.setIndicatorString(0, "Last direction moved: "+dir.toString());
                                }
                            }
                        }
                            
                        
                        if (Clock.getRoundNum() >= 250){
                            if (Math.random() < .25) {// && rc.getLocation().distanceSquaredTo(rc.senseHQLocation()) < 16) {
                                // Lay a mine 
                                if(rc.senseMine(rc.getLocation())==null)
                                    rc.layMine();
                            } else if (rc.getLocation().distanceSquaredTo(rc.senseHQLocation()) > 16 && nearbyEncampments.length != 0) {
                                if (rc.senseEncampmentSquare(rc.getLocation())) rc.captureEncampment(RobotType.GENERATOR);
                                else {
                                    Direction dir = rc.getLocation().directionTo(nearbyEncampments[0]);
                                    if (rc.canMove(dir)) rc.move(dir);
                                    else rc.move(randomDir(rc));
                                }
                            } else if (rc.getLocation().distanceSquaredTo(rc.senseHQLocation()) > 16 && nearbyEncampments.length == 0) {
                                Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
                                if (rc.canMove(dir)) rc.move(dir);
                                else rc.move(randomDir(rc));
                            } else { 
                                // Choose a random direction, and move that way if possible
                                Direction dir = randomDir(rc);
                                if(rc.canMove(dir)) {
                                    rc.move(dir);
                                    rc.setIndicatorString(0, "Last direction moved: "+dir.toString());
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
