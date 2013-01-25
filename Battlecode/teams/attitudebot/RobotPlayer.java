package attitudebot;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
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
            	if (rc.getType() == RobotType.SOLDIER){
            		attitudebot.soldierCode.soldierRun(rc);
            	}
            	else if (rc.getType() == RobotType.HQ){
            		attitudebot.hqCode.hqRun(rc);
            	}
            	else if (rc.getType() == RobotType.ARTILLERY){
            		attitudebot.encampCode.artilleryRun(rc);
            	}
/*
            	else if (rc.getType() == RobotType.GENERATOR){
            		attitudebot.encampCode.generatorRun(rc);
            	}
            	else if (rc.getType() == RobotType.MEDBAY){
            		attitudebot.encampCode.medbayRun(rc);
            	}
            	else if (rc.getType() == RobotType.SHIELDS){
            		attitudebot.encampCode.shieldsRun(rc);
            	}
            	else if (rc.getType() == RobotType.SUPPLIER){
            		attitudebot.encampCode.supplierRun(rc);
            	}
*/
            	rc.yield();
            } catch (Exception e) {
            	e.printStackTrace();
            }
            		
/*            		
                 Navigation

                 Mine Avoidance
                 Dont defuse mines while being attacked
                 
                 HQ destruction

                 Mine Laying

                 Researching UpgrayDD

                 Some Encampment Camping

                 Chasing around little bitches

                 Sweet Rallying and Swarms like bees3

                 Broadcasting read is only .003 bytecodes!
                 
                 } catch (Exception e) {
                    e.printStackTrace();
*/
        }
    }
}
