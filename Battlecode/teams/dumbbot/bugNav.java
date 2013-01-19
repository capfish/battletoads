package dumbbot;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class bugNav {
	
//	public MapLocation target;
	
	public bugNav()	{}

	//decides what direction to go next based on current location and target location
	public Direction getNextStep(MapLocation curLoc, MapLocation target, MapLocation[] nMines, MapLocation[] eMines)
	{
		return curLoc.directionTo(target);
	}

}
