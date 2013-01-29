package sheeptests;

import battlecode.common.*;

public class soldierCode {
    private static int numEnemyMines;
    private static MapLocation myLoc;
    private static Team enemy;

    public static void soldierRun(RobotController rc) throws GameActionException {
        Bug b = new Bug(rc.senseEnemyHQLocation(), rc);
        
        while(true){
            myLoc = rc.getLocation();
            enemy = rc.getTeam().opponent();
            numEnemyMines = rc.senseMineLocations(myLoc, 10000, enemy).length;
        
            if (rc.isActive()) {
            	/*            }
                if (numEnemyMines <= 3) {
                    b.go();
                    rc.yield();
                } else {
                */
            	if (numEnemyMines <= 3) {
            		
            	}
            }
        }
    }
}
                    
