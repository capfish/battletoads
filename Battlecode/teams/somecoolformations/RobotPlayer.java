package somecoolformations;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.MapLocation;
import battlecode.common.Clock;
import battlecode.common.Upgrade;

public class RobotPlayer {
    public static void run(RobotController rc) {
        while (true) {
            try {
                if (rc.getType() == RobotType.HQ) {
                    if (rc.isActive()) {
                        if (Clock.getRoundNum() < 80) rc.researchUpgrade(Upgrade.PICKAXE);
                        // Spawn a soldier
                        else {
                            Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
                            if (rc.canMove(dir)) rc.spawn(dir);
                        }
                    }

                } else if (rc.getType() == RobotType.SOLDIER) {
                    if (rc.isActive()) {
                    }
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
    private static void gang(RobotController rc) {
    	Robot[] allied = rc.senseNearbyGameObjects(rc.getRobot().getClass(), rc.getLocation(), 10, rc.getTeam());
    	Robot[] enemy = rc.senseNearbyGameObjects(rc.getRobot().getClass(), rc.getLocation(), 10, rc.getTeam().opponent());
    }
    private static void sparseMineField(RobotController rc) {
    	
    }
    private static void pickaxeMineField(RobotController rc, MapLocation center) {
    	if(rc.getLocation().x == )
    }
    }
}
