package dumbbot; 

import battlecode.common.*;

/** The example funcs player is a player meant to demonstrate basic usage of the most common commands.
 * Robots will move around randomly, occasionally mining and writing useless messages.
 * The HQ will spawn soldiers continuously. 
 */

public class RobotPlayer {
    public static MapLocation enemyHQ;
    public static MapLocation myHQ;
    public static Team myTeam;
    public static Team enemyTeam;
    
    public static void run(RobotController rc) {
        enemyHQ = rc.senseEnemyHQLocation();
        myHQ = rc.senseHQLocation();
        myTeam = rc.getTeam();
        enemyTeam = myTeam.opponent();
        while (true) {
            try {
                if (rc.getType() == RobotType.HQ) {
                    dumbbot.hqCode.hqRun(rc, enemyHQ);

//                } else if (rc.getType() == RobotType.ARTILLERY) {
//                    dumbbot.encampCode.artilleryRun(rc);
                    
                } else if (rc.getType() == RobotType.SOLDIER) {
                    dumbbot.soldierCode.soldierRun(rc);
                    
                } else if (rc.getType() == RobotType.GENERATOR) {
                    dumbbot.encampCode.generatorRun(rc);
                    
                }else if (rc.getType() == RobotType.MEDBAY) {
                    dumbbot.encampCode.generatorRun(rc);
                    
                }else if (rc.getType() == RobotType.SHIELDS) {
                    dumbbot.encampCode.generatorRun(rc);
                    
                }else if (rc.getType() == RobotType.SUPPLIER) {
                    dumbbot.encampCode.generatorRun(rc);
                }
                
                // End turn
//                rc.yield();
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
