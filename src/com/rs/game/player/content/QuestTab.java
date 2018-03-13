package com.rs.game.player.content;

import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.utils.Colors;

/**
 * Handles the custom Quest Tab.
 * @author Zeus
 */
public class QuestTab {

	/**
	 * Sends the tab.
	 * @param player The player.
	 */
	public static void sendTab(Player player) {
		player.getPackets().sendIComponentText(506, 0, Colors.green+"Teleport cPanel");
    	player.getPackets().sendIComponentText(506, 2, Colors.white+"Account Manager");
		player.getPackets().sendIComponentText(506, 4, Colors.white+"Training");
    	player.getPackets().sendIComponentText(506, 6, Colors.white+"Bosses");
    	player.getPackets().sendIComponentText(506, 8, Colors.white+"Minigames");
		player.getPackets().sendIComponentText(506, 10, Colors.white+"PvP");
    	player.getPackets().sendIComponentText(506, 12, Colors.white+"Skilling");
		player.getPackets().sendIComponentText(506, 14, (player.isDonator() ? Colors.green : Colors.red)+"Members Zone");
	}
	
	/**
	 * Handles the custom Quest Tabs buttons.
	 * @param player The player using the tab.
	 * @param componentId The interfaces childId's.
	 */
	public static void handleTab(Player player, int componentId) {
		switch (componentId) {
		case 2: /** Account Manager **/
			AccountInterfaceManager.sendInterface(player);
			break;
		case 4: /** Training **/
			player.getDialogueManager().startDialogue("TrainingTeleport");
			//InterfaceManager.setPlayerInterfaceSelected(3);
			//TrainingTeleports.sendInterface(player);
			break;
		case 6: /** Bosses **/
			player.getDialogueManager().startDialogue("BossTeleports");
			//InterfaceManager.setPlayerInterfaceSelected(1);
			//BossTeleports.sendInterface(player);
			break;
		case 8: /** Minigames **/
			//InterfaceManager.setPlayerInterfaceSelected(2);
			//MinigameTeleports.sendInterface(player);
			player.getDialogueManager().startDialogue("MinigameTeleport");
			break;
		case 10: /** PvP **/
			player.getDialogueManager().startDialogue("PvpTele");
			//InterfaceManager.setPlayerInterfaceSelected(4);
			//PvPTeleports.sendInterface(player);
			//player.getDialogueManager().startDialogue("PkingTeleports"); TODO delete
			break;
		case 12: /** Skilling **/
			player.getDialogueManager().startDialogue("SkillTeleport");
			//InterfaceManager.setPlayerInterfaceSelected(5);
			//SkillingTeleports.sendInterface(player);
			//player.getDialogueManager().startDialogue("SkillingTeleports"); TODO delete
			break;
		case 14: /** Donator Zone **/
			player.getDialogueManager().startDialogue("MembersTeleport");
			/*if (!player.isDonator()) {
				player.getDialogueManager().startDialogue("SimpleMessage", 
						"If you'd like to visit the Donator Zone, you'll have to ;;donate at least 20$ first!");
				return;
			}
			Magic.sendAncientTeleportSpell(player, 1, 0, new WorldTile(4382, 5919, 0));*/
			break;
		default:
			player.sendMessage("Unhandled interface button: "+componentId+"; report this to an Administrator!");
			break;
		}
	}
}