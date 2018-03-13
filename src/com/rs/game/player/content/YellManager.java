package com.rs.game.player.content;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.utils.Utils;

/**
 * Handles the player sent World Messages (Yell).
 * @author Vichy
 */
public class YellManager {

	/**
	 * Sends the World Message.
	 * @param player The player sending the message.
	 * @param message The message String to send.
	 */
    public static void sendYell(Player player, String message) {
    	if (!(player.isDonor() || player.isStaff3())) {
    	player.getDialogueManager().startDialogue("SimpleMessage", 
    		"You have to be a donator or a staff member in order to yell. Type ;;store to rank-up and enjoy the "
    	+ "unlimited World Messages :)!");
    	return;
    	}
		if (message.length() > 80)
			message = message.substring(0, 80);
		String color = (player.getYellColor() == "ff0000" || player.getYellColor() == null ? 
				player.isYoutube() ? "000000" :
					player.isDonator() ? "8B4513" :
					player.isExtremeDonator() ? "ffffff" :
					player.isLegendaryDonator() ? "e6e600":
					player.isSupremeDonator() ? "008000":
					player.isUltimateDonator() ? "00FFFF":
					player.isSponsorDonator() ? "FF8C00" :
						player.isDev() ? "00BFFF":
					"" :
					"<col="+player.getYellColor()+">");
		String text = player.getDisplayName()+": "+color+Utils.fixChatMessage(message);
		if (player.getRights() < 2) {
			String[] invalid = { "<euro", "<img", "<img=", "<col", "<col=", "<shad", "<shad=", "<str>", "<u>" };
			for (String s : invalid) {
				if (message.contains(s)) {
					player.sendMessage("You are not allowed to add additional code to the message.");
					return;
				}
			}
			if (message.toLowerCase().contains("www")
					|| message.toLowerCase().contains(".org")
					|| message.toLowerCase().contains(".com")
					|| message.toLowerCase().contains(".net")
					|| message.toLowerCase().contains(".tv")
					|| message.toLowerCase().contains(".us")
					|| message.toLowerCase().contains("rsps") 
					|| message.toLowerCase().contains("scape") && !message.toLowerCase().contains(Settings.SERVER_NAME)) {
				player.sendMessage("You are not allowed to advertise/insert URL's into the yell channel.");
				return;
			}
			if (player.getYellDelay() > 10 && player.getRights() == 0) {
				player.sendMessage("You're typing to fast!");
				return;
			}
	    	if (player.getMuted() > Utils.currentTimeMillis()) {
	    		player.sendMessage("You are muted and cannot yell.");
				return;
			}
			player.setYellDelay(20);
			if (player.getRights() == 1) {
				World.sendWorldYellMessage("["+color+"Moderator</col>] <img=0>"+text, player);
				return;
			}
			if (player.getRights() == 13) {
				World.sendWorldYellMessage("["+color+"Support</col>] <img=13>"+text, player);
				return;
			}
			if (player.isSupport()) {
				World.sendWorldYellMessage("["+color+"Support</col>] <img=13>"+text, player);
				return;
			}
			if(player.isYoutube()){
				World.sendWorldYellMessage("[<shad=ff0000>"+color+"Youtube</col></shad>] <img=17>"+text, player);
			return;
			}
			if (player.isSponsorDonator()) {
				World.sendWorldYellMessage("[<shad=FFD700"+color+"Sponsor</col></shad>] <img=16>"+text, player);
				return;
			}
			if (player.isUltimateDonator()) {
				World.sendWorldYellMessage("["+color+"Diamond Member</col>] <img=20>"+text, player);
				return;
			}
			if (player.is420Donator()) {
				World.sendWorldYellMessage("["+color+"420</col></shad>] <img=23>"+text, player);
				return;
			}
			if (player.isSupremeDonator()) {
				World.sendWorldYellMessage("["+color+"Platinum Member</col>] <img=21>"+text, player);
				return;
			}
			if (player.isLegendaryDonator()) {
				World.sendWorldYellMessage("["+color+"Gold Member</col>] <img=12>"+text, player);
				return;
			}
			if (player.isExtremeDonator()) {
				World.sendWorldYellMessage("["+color+"Silver Memeber</col>] <img=22>"+text, player);
				return;
			}
			if (player.isDonator()) {
				World.sendWorldYellMessage("["+color+"Bronze Member</col>] <img=19>"+text, player);
				return;
			}
			return;
		} 
		if(player.isDev()){
			World.sendWorldYellMessage("[<shad=0000FF>"+color+"Dev</col></shad>] <img=18>"+text, player);
		return;
		}
		if (player.getUsername().equalsIgnoreCase("")) {
			World.sendWorldYellMessage("["+color+"Head Mod</col>] <img=0>"+text, player);
			return;
		}
		if (player.getUsername().equalsIgnoreCase("dimitriuse") || player.getUsername().equalsIgnoreCase("likx")) {
			World.sendWorldYellMessage("["+color+"Moderator</col>] <img=0>"+text, player);
			return;
		}
		if (player.getUsername().equalsIgnoreCase("arrow") || player.getUsername().equalsIgnoreCase("vex")) {
			World.sendWorldYellMessage("["+color+"Support</col>] <img=13>"+text, player);
			return;
		}
		if (player.getUsername().equalsIgnoreCase("")) {
			World.sendWorldYellMessage("["+color+"Support</col>] <img=13>"+text, player);
			return;
		}
		if (player.getUsername().equalsIgnoreCase("")) {
			World.sendWorldYellMessage("["+color+"Support</col>] <img=13>"+text, player);
			return;
		}
		if (player.getUsername().equalsIgnoreCase("")) {
			World.sendWorldYellMessage("["+color+"Support</col>] <img=13>"+text, player);
			return;
		}
		if (player.getUsername().equalsIgnoreCase("")) {
			World.sendWorldYellMessage("["+color+"Support</col>] <img=13>"+text, player);
			return;
		}
		if (player.getUsername().equalsIgnoreCase("")) {
			World.sendWorldYellMessage("["+color+"Support</col>] <img=13>"+text, player);
			return;
		}
		if (player.getUsername().equalsIgnoreCase("Zeus")) {
			World.sendWorldYellMessage("["+color+"Owner/Developer</col>] <img=1>"+text, player);
			return;
		}
		//World.sendWorldYellMessage("["+color+"Administrator</col>] <img=1>"+text, player);
    }
}