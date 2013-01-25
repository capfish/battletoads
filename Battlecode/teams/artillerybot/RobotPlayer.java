package artillerybot;

import battlecode.common.*;

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
                        // Spawn a soldier
                        if (Math.random() < .6 || rc.getTeamPower() > 2*rc.senseCaptureCost()) {
                            Direction dir = randomDir(rc,15);
                            if (rc.canMove(dir))
                                rc.spawn(dir);
                        } else {
                            rc.researchUpgrade(Upgrade.FUSION);
                        }
                        
                    }
                } else if (rc.getType() == RobotType.SOLDIER && rc.isActive()) {
                    if (Math.random() < 0.3 && rc.senseMine(rc.getLocation()) != rc.getTeam()){
                        rc.layMine();
                    } else if (rc.senseEncampmentSquare(rc.getLocation())) {
                               if (rc.getTeamPower() < 1.5*rc.senseCaptureCost()) {
                                   rc.captureEncampment(RobotType.GENERATOR);
                               } else {
                                   rc.captureEncampment(RobotType.ARTILLERY);
                               }
                    } else {
                        MapLocation[] encampments = rc.senseEncampmentSquares(rc.getLocation(), 64, Team.NEUTRAL);
                        Direction dir = Direction.NONE;
                        if (encampments.length != 0) {
                            dir = rc.getLocation().directionTo(encampments[0]);
                        } else {
                            dir = randomDir(rc, 15);
                        }
                        if (!rc.canMove(dir)){
                            dir = randomDir(rc, 15);
                        }
                        if (rc.senseMine(rc.getLocation().add(dir)) == Team.NEUTRAL || rc.senseMine(rc.getLocation().add(dir)) == rc.getTeam().opponent()) {
                            rc.defuseMine(rc.getLocation().add(dir));
                        } else if(rc.canMove(dir)) {
                                rc.move(dir);
                                rc.setIndicatorString(0, "Last direction moved: "+dir.toString());
                        } else {
                            rc.move(randomDir(rc, 15));
                        }
                    }
                    // if (Math.random()<0.01 && rc.getTeamPower()>5) {
                    //     // Write the number 5 to a position on the message board corresponding to the robot's ID
                    //     rc.broadcast(rc.getRobot().getID()%GameConstants.BROADCAST_MAX_CHANNELS, 5);
                }

                // End turn
                rc.yield();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
        private static Direction randomDir(RobotController rc, int depth) {
    	if ( depth == 0 ) return null;
        Direction dir = Direction.values()[(int)(Math.random()*8)];
        if(rc.canMove(dir)) return dir;
        else return randomDir(rc, depth -1);
    }
}
