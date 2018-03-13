package com.rs.game.player.content;

import com.rs.game.player.Player;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/**
 * Handles everything related to the Account Manager.
 * @author Zeus.
 */
public class AccountInterfaceManager {

	/**
	 * Sends the actual interface with all available options.
	 * @param player The player to send the interface to.
	 */
	public static void sendInterface(Player player) {
		player.getInterfaceManager().sendInterface(1157);
		player.getPackets().sendIComponentText(1157, 92,
				"<col=FFFF00>"+ player.getDisplayName() + "'s Helwyr Settings");
		player.getPackets().sendIComponentText(1157, 95, ""); //bottom line, under all scrollables
		player.getPackets().sendIComponentText(1157, 33, "Setting"); //table name
		player.getPackets().sendIComponentText(1157, 34, "Toggle"); //table name

		player.getPackets().sendIComponentText(1157, 46, "");
		player.getPackets().sendIComponentText(1157, 47, "Appearence");
		player.getPackets().sendIComponentText(1157, 48, "Press to customize");
		
		player.getPackets().sendIComponentText(1157, 49, "");
		player.getPackets().sendIComponentText(1157, 50, "Spawn Location");
		player.getPackets().sendIComponentText(1157, 51, "Ashdale");
		
		player.getPackets().sendIComponentText(1157, 52, "");
		player.getPackets().sendIComponentText(1157, 53, "Loyalty titles");
		player.getPackets().sendIComponentText(1157, 54, "Press to customize");
		
		player.getPackets().sendIComponentText(1157, 55, "");
		player.getPackets().sendIComponentText(1157, 56, "Loot Beam");
		player.getPackets().sendIComponentText(1157, 57, (player.hasLootBeam() ? Colors.green+"Enabled" : Colors.red+"Disabled")+". Trigger price: "+Utils.getFormattedNumber(player.setLootBeam)+".</col>");
		
		player.getPackets().sendIComponentText(1157, 58, "");
		player.getPackets().sendIComponentText(1157, 59, "World Messages");
		player.getPackets().sendIComponentText(1157, 60, (player.isHidingWorldMessages() ? Colors.red+"Disabled" : Colors.green+"Enabled")+"</col>. Press to customize");
		
		player.getPackets().sendIComponentText(1157, 61, "");
		player.getPackets().sendIComponentText(1157, 62, "Yell Messages");
		player.getPackets().sendIComponentText(1157, 63, (player.isYellOff() ? Colors.red+"Disabled" : Colors.green+"Enabled")+"</col>. Press to customize");
		
		player.getPackets().sendIComponentText(1157, 64, "");
		player.getPackets().sendIComponentText(1157, 65, "MacLock");
		player.getPackets().sendIComponentText(1157, 66, (player.iplocked ? Colors.green+"Enabled to : " +player.lockedwith : Colors.red+"Disabled"+"</col>.")+" Press to customize");
		
		int[] componentIds = {67, 68, 69, 70, 71, 72, 73, 74, 75, 76};
		//let's clear everything else
		for (int id : componentIds)
			player.getPackets().sendIComponentText(1157, id, "");
	}
	
	/**
	 * Handles the actual interfaces buttons.
	 * @param player The players interface to handle.
	 * @param componentId The players interface pressed component ID.
	 */
	public static void handleInterface(Player player, int componentId) {
		player.getInterfaceManager().closeChatBoxInterface();
		if (componentId == 0)
			player.getDialogueManager().startDialogue("PlayerSettings");
		
		if (componentId == 2)
			player.getTitles().openShop();
		
		if (componentId == 3)
			player.getDialogueManager().startDialogue("SetBeam");
			
		if (componentId == 4) {
			if (!player.isHidingWorldMessages()) {
				player.setHideWorldMessages(player.isHidingWorldMessages() ? false : true);
				AccountInterfaceManager.sendInterface(player);
				player.sendMessage("You have turned off World Messages");
				return;
			}
			player.setHideWorldMessages(player.isHidingWorldMessages() ? false : true);
			AccountInterfaceManager.sendInterface(player);
			player.sendMessage("You have turned on World Messages");
		}
		if (componentId == 5) {
			if (!player.isYellOff()) {
				player.setYellOff(player.isYellOff() ? false : true);
				AccountInterfaceManager.sendInterface(player);
				player.sendMessage("You have turned off Yell Messages");
				return;
			}
			player.setYellOff(player.isYellOff() ? false : true);
			AccountInterfaceManager.sendInterface(player);
			player.sendMessage("You have turned on Yell Messages");
		}
		if (componentId == 6)
			player.getDialogueManager().startDialogue("setIplock");
	}
}