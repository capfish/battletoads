package spambot;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.MapLocation;
import battlecode.common.Clock;

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
                        Direction dir = randomDir(rc);
                        rc.spawn(dir);
                    }
                } else if (rc.getType() == RobotType.SOLDIER) {
                    if (rc.isActive()) {
                        Direction dir = randomDir(rc);
                        rc.move(dir);
                        rc.broadcast((int)(Math.random()*10000), (int)(Math.random()*23539));
                    }
                }
                rc.yield();
            } catch( Exception e) {
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
