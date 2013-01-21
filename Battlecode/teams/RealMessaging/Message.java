package RealMessaging;


import java.io.UnsupportedEncodingException;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

/* So it seems like each channel can have at most 1 int in it.
 * If another int gets written, then the original one is overwritten.
 * For long messages, the only way seems to be writing on different channels.
 * lsb = isLocation
 */

public class Message {
	private static int MULT = 743; //prime
	private static int q = 593; //anything
	private static int p = 853; //prime
	private static int shift = 15;
	private static int loc_size = 7;
	private static int msg_size = 2;
	private static int NUM_CHANNELS = 65535;
	private RobotController rc;
	private int s_channel, channel_origin;
	private int ID;
	private static int xmask = 0xFE00; //0b1111111000000000;
	private static int ymask = 0x1FC; //0b0000000111111100;
	private static int mmask = 0x3; //0b0000000000000011;
	public int command;
	public int xloc;
	public int yloc;
	Message(RobotController rc) {
		if (rc.getType() == RobotType.HQ) ID = -1;
		else ID = rc.getRobot().getID() % 101;
		this.rc = rc;
		s_channel = 0;
	}
	//String should be 16 digits and consist of 1s and 0s. 
	//msb = isLocation? here, should be 0.
	//if !isLocation, then second lsb bit = gotowards/getencampments/mines/whatever
	public void send(int message, MapLocation loc) throws GameActionException {
		int temp = ((((loc.x << loc_size) + loc.y) << msg_size) + message) << shift;
		int msg = pad(temp);
		int channel = (channel_origin - s_channel + ID) % NUM_CHANNELS;
		rc.broadcast(channel, msg);
		//System.out.println("sending on channel " + channel);
		s_channel ++;
	}
	
	private int pad(int msg) {
		int msg_sum = msg % p;
		//if (msg_sum < 0) msg_sum = p - msg_sum;	//-a % b returns negative number.
		if (msg_sum <= q) msg = msg + (q - msg_sum);
		else msg = msg + (p-msg_sum) + q;
		return msg + Clock.getRoundNum();
	}
	
	public void receive(int cell) throws GameActionException {
		int channel = channel_origin + cell;
		int round = Clock.getRoundNum();
		if (ID == -1) {
			channel -= MULT; //this might need to get fixed in case of negative shits. prolly not tho.
			if (channel < 0) {
				channel = NUM_CHANNELS + channel;
			}
			round --;
		}
		int msg = rc.readBroadcast(channel % NUM_CHANNELS) - round;
		int msg_sum = msg % p;
		if (msg_sum != q && (p+msg_sum) != q) command = -1;	//checks to see if its bad. if so, return -1.
		else {
			msg = msg >> shift;
			command = mmask & msg;
			yloc = (ymask & msg) >> msg_size;
			xloc = (xmask & msg) >> (msg_size+loc_size);
		}
		//System.out.println("receiving on channel " + channel);
	}
	public void reset() {
		s_channel = 0;
		channel_origin = (((Clock.getRoundNum()+1) * MULT) % NUM_CHANNELS);
	}
}
