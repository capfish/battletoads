package team161;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Upgrade;

public class hqCode {
	private static boolean hasShield = false;
	private static Message msg;
    private static Direction randomDir(RobotController rc) {
        Direction dir = Direction.values()[(int)(Math.random()*8)];
        if(rc.canMove(dir)) return dir;
        else return randomDir(rc);
    }

	public static void hqRun(RobotController rc, MapLocation enemyHQ) throws GameActionException {
		msg = new Message(rc);
		MapLocation shieldLoc = null;
		while(true) {
			msg.reset();
			for (int i = 0; i < 101; i++) {
				msg.receive(i);
				if (msg.action != null) System.out.println(msg.action + " : " + msg.location);
				if (msg.action == Action.CAP_SHIELD) {
					hasShield = true;
					shieldLoc = msg.location;
				} else if (msg.action == Action.DISTRESS) {
					hasShield = false;
				}
			}
			if (!hasShield) {
				int numEnemyMines = rc.senseMineLocations(rc.getLocation(), 10000, rc.getTeam().opponent()).length;
				if (numEnemyMines > 3) {
					msg.send(Action.CAP_SHIELD, enemyHQ);
					rc.setIndicatorString(0, "want shield");
				}
			} else {
				//tell them to gather at shield;
				msg.send(Action.RALLY_AT, shieldLoc);
				rc.setIndicatorString(0, "dont care about shield");
			}
			if (rc.isActive()) {
				/*if (rc.getTeamPower() < 50) {
					if (!rc.hasUpgrade(Upgrade.PICKAXE)) rc.researchUpgrade(Upgrade.PICKAXE);
					else if(!rc.hasUpgrade(Upgrade.FUSION)) rc.researchUpgrade(Upgrade.FUSION);
					else rc.researchUpgrade(Upgrade.NUKE);
				} else {
					if (Math.random() < 0.2) rc.spawn(randomDir(rc));
					else if (!rc.hasUpgrade(Upgrade.PICKAXE)) rc.researchUpgrade(Upgrade.PICKAXE);
					else rc.spawn(randomDir(rc));
				}*/
				
		         
				 if (rc.canMove(rc.getLocation().directionTo(enemyHQ))) rc.spawn(rc.getLocation().directionTo(enemyHQ));
				 else rc.spawn(randomDir(rc));
			}
			rc.yield();
		}
	}

}
