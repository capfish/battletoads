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
                        if (Clock.getRoundNum() < 50) rc.researchUpgrade(Upgrade.PICKAXE);
                        // Spawn a soldier
                        else {
                            Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
                            if (rc.canMove(dir)) rc.spawn(dir);
                        }
                    }

                } else if (rc.getType() == RobotType.SOLDIER) {
                    if (rc.isActive()) {
                    	if (pickaxeSparseMineField(rc) && rc.senseMine(rc.getLocation()) == null) rc.layMine();
                    	else rc.move(randomDir(rc));
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
    	if((a+b) % 4 == 0) return true;
    	return false;
    }
}
