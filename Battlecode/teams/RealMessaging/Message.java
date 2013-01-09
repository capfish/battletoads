package RealMessaging;


import java.io.UnsupportedEncodingException;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

/* So it seems like each channel can have at most 1 int in it.
 * If another int gets written, then the original one is overwritten.
 * For long messages, the only way seems to be writing on different channels.
 * lsb = isLocation
 */

public class Message {
	private static int MULT = 743; //prime
	private static int q = 593; //anything
	private static int p = 853; //prime
	private static int REDUNDANCY = 2;
	private static int shift = (int)Math.pow(2, 15);
	private static int msb = (int)Math.pow(2, 31);
	private static int loc_size = (int)Math.pow(2, 7);
	private RobotController rc;
	private int s_channel, r_channel;
	Message(RobotController rc) {
		this.rc = rc;
		s_channel = 0;
		r_channel = 0;
	}
	//String should be 16 digits and consist of 1s and 0s. 
	//msb = isLocation? here, should be 0.
	//if !isLocation, then second lsb bit = gotowards/getencampments/mines/whatever
	public void send(String bits) {
		try {
			int msg = pad(Integer.parseInt(bits, 2) * shift);
			int channel = calcChannel(s_channel);
			for (int i = 0; i < REDUNDANCY; i++) rc.broadcast(channel + i*REDUNDANCY, msg);
			s_channel = s_channel + 1;
		} catch (Exception e) {}
	}
	
	public void sendLoc(MapLocation loc) {
		int msg = pad(1*msb + (loc.x*loc_size + loc.y) * shift);
		try {
			int channel = calcChannel(s_channel);
			for (int i = 0; i < REDUNDANCY; i++) rc.broadcast(channel + i*REDUNDANCY, msg);
			s_channel = s_channel + 1;
		} catch (Exception e) {}
	}
	
	private int pad(int msg) {
		int msg_sum = msg % p;
		//if (msg_sum < 0) msg_sum = p - msg_sum;	//-a % b returns negative number.
		if (msg_sum <= q) return msg + (q - msg_sum);
		else return  msg + (p-msg_sum) + q;
	}
	
	public String receive() {
		try {
			int channel = calcChannel(r_channel);
			String msg = getChannel(channel, REDUNDANCY);
			r_channel = r_channel + 1;
			return msg;
		} catch (Exception e) {
			return "error";
		}
	}
	private String getChannel(int n, int counter) {
		try {
			int msg = rc.readBroadcast(n);
			int msg_sum = msg % p;
			if (msg_sum != q && (p+msg_sum) != q) {
				System.out.println("got " + msg_sum);
				if (counter == 0)
					return "corrupted";	//checks to see if its bad. if so, return null.
				else return getChannel(n + MULT, counter-1);
			}
			if (msg >= 0) return decrypt(msg);
			else return decryptLoc(msg);
		} catch (Exception e) {
			return "error";
		}
		
	}
	private static String decrypt(int msg) {
		return Integer.toBinaryString(msg/shift);
	}
	private static String decryptLoc(int msg) {
		msg = (msg-msb)/shift;
		int y = msg % loc_size;
		msg = msg/loc_size;
		int x = msg % loc_size;
		return "x: " + x + " y: " + y;
	}
	private int calcChannel(int offset) {
		return ((Clock.getRoundNum() * MULT) % 10000) + offset;
	}
	public void reset() {
		r_channel = 0;
		s_channel = 0;
	}
}
