package team161;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;
import battlecode.common.Upgrade;
import battlecode.engine.instrumenter.RobotMonitor;

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
		int num_suppliers, num_generators;

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
			
			//find out which sector the end of our mines is in
			//sectors are:
//			System.out.println(Clock.getBytecodesLeft());
			MapLocation frontLines = whichSector(rc);
//			System.out.println(Clock.getBytecodesLeft());

			msg.reset();
			
			if (/*rc.getTeamPower() < 50 ||*/ Clock.getRoundNum() > 2000) {
				msg.send("10");
				msg.sendLoc(rc.senseEnemyHQLocation());	
				rc.setIndicatorString(0, "target = eHQ");
			}
			else {//if (encamps.length < 5) {
				msg.send("10");
				msg.sendLoc(rc.getLocation().add(rc.getLocation().directionTo(rc.senseEnemyHQLocation()), (width+height)/7));
				rc.setIndicatorString(0, "target = rally");
			}
			
			rc.yield();
		}
	}
	
	public static MapLocation whichSector(RobotController rc)
	{
		int diagonal = RobotPlayer.myHQ.distanceSquaredTo(RobotPlayer.enemyHQ);
		int delta = (int)(diagonal/5);
		int deltaX = (int)(width/5);
		int deltaY = (int)(height/5);
		//check middle section
		MapLocation sect3 = new MapLocation(width/2, height/2);
		MapLocation sect2 = new MapLocation(sect3.x - deltaX, sect3.y - deltaY);
		MapLocation sect1 = new MapLocation(sect2.x - deltaX, sect2.y - deltaY);
		
		int sect3Count = rc.senseMineLocations(sect3, delta, RobotPlayer.myTeam).length;
		int sect2Count = rc.senseMineLocations(sect2, delta, RobotPlayer.myTeam).length;
		if (sect3Count == 0)
			if (sect2Count > 0)
				return sect2;
			else
				return sect1;
		else
		{
			MapLocation sect4 = new MapLocation(sect3.x + deltaX, sect3.y + deltaY);
			int sect4Count = rc.senseMineLocations(sect4, delta, RobotPlayer.myTeam).length;
			if (sect4Count == 0)
				return sect3;
			else
			{
				MapLocation sect5 = new MapLocation(sect4.x + deltaX, sect4.y + deltaY);
				int sect5Count = rc.senseMineLocations(sect5, delta, RobotPlayer.myTeam).length;
				if (sect5Count == 0)
					return sect4;
				return sect5;
			}
		}
	}
}
