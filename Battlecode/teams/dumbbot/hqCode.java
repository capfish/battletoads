package dumbbot;

import battlecode.common.*;

public class hqCode {
    private static int width, height;
    private static Team spawnMine;
    private static Direction randomDir(RobotController rc, int depth) {
    	if ( depth == 0 ) return null;
        Direction dir = Direction.values()[(int)(Math.random()*8)];
        if(rc.canMove(dir)) return dir;
        else return randomDir(rc, depth -1);
    }

	public static void hqRun(RobotController rc, MapLocation enemyHQ) throws GameActionException {
		width = rc.getMapWidth();
		height = rc.getMapHeight();
		Message msg = new Message(rc);

		while (true) {
			rc.setIndicatorString(0, "doing shit");
			if (rc.isActive()) {
				if (rc.getTeamPower() < 50) {
					if (!rc.hasUpgrade(Upgrade.PICKAXE)) rc.researchUpgrade(Upgrade.PICKAXE);
					//else if(!rc.hasUpgrade(Upgrade.FUSION)) rc.researchUpgrade(Upgrade.FUSION);
					else rc.researchUpgrade(Upgrade.NUKE);
				} else {
					if (Math.random() < 0.6 && !rc.hasUpgrade(Upgrade.PICKAXE)) rc.researchUpgrade(Upgrade.PICKAXE);
					else {
						Direction dir = randomDir(rc, 15);
                        spawnMine = rc.senseMine(RobotPlayer.myHQ.add(dir));
						if ( dir == null ) rc.researchUpgrade(Upgrade.NUKE);
						else if (spawnMine == RobotPlayer.enemyTeam || spawnMine == Team.NEUTRAL)
                                                    {
                                                        dir = randomDir(rc, 15);
                                                        if ( dir == null ) rc.researchUpgrade(Upgrade.NUKE);
                                                        else rc.spawn(randomDir(rc, 15));
                                                    }
                                                else rc.spawn(dir);
					}
				}
			}
				//MapLocation[] encamps = rc.senseEncampmentSquares(rc.getLocation(), (width*height)/4, Team.NEUTRAL);
				/*for (MapLocation encamp: encamps) {
					msg.send("1");
					msg.sendLoc(encamp);
				}*/
				msg.reset();

				if (/*rc.getTeamPower() < 50 ||*/ Clock.getRoundNum() > 1000) {
					msg.send("10");
					msg.sendLoc(rc.senseEnemyHQLocation());	
					rc.setIndicatorString(0, "target = eHQ");
				}
				else {//if (encamps.length < 5) {
					msg.send("10");
					msg.sendLoc(rc.getLocation().add(rc.getLocation().directionTo(rc.senseEnemyHQLocation()), (width+height)/8));
					rc.setIndicatorString(0, "target = rally");
				}
			
			rc.yield();
		}
	}
}
