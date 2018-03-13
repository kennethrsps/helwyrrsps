package com.rs.game.player.dialogue.impl;

import com.rs.game.WorldTile;
import com.rs.game.player.content.Magic;
import com.rs.game.player.controllers.RunespanController;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

public class SkillTeleport2 extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Choose an Option", "RuneCrafting", "Summoning", "Farming", "Hunter", Colors.red + "Back");

	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			if (componentId == OPTION_1) {
				sendOptionsDialogue("Runecrafting", "RuneSpan", "Classic Altar", Colors.red + "Back");
				stage = 2;

			}
			if (componentId == OPTION_2) {
				Magic.vineTeleport(player, new WorldTile(2923, 3449, 0));
				end();
			}
			if (componentId == OPTION_3) {
				Magic.vineTeleport(player, new WorldTile(3052, 3304, 0));
				end();

			}
			if (componentId == OPTION_4) {
				sendOptionsDialogue("Hunter", "Fieldip Hills", "Falconry", "PuroPuro", Colors.red + "Back");
				stage = 3;

			}
			if (componentId == OPTION_5) {
				player.getDialogueManager().startDialogue("SkillTeleport");

			}
			break;
		case 2:
			if (componentId == OPTION_1) {
				if (!canTeleport()) {
					end();
					return;
				}
				RunespanController.enterRunespan(player);
				player.sendMessage("Welcome to Runespan!");

			}
			if (componentId == OPTION_2) {
				Magic.vineTeleport(player, new WorldTile(2598, 3157, 0));
				end();
			}
			if (componentId == OPTION_3) {
				player.getDialogueManager().startDialogue("SkillTeleport");
			}
			break;
		case 3:
			if (componentId == OPTION_1) {
				sendOptionsDialogue("Hunter", "Crimson Swift Area (level 1)", "Chinchompa/Tropical Wigtail (Level 19+)",
						Colors.red + "Back");
				stage = 4;

			}
			if (componentId == OPTION_2) {
				Magic.vineTeleport(player, new WorldTile(2362, 3623, 0));
				end();
			}
			if (componentId == OPTION_3) {
				if (!canTeleport()) {
					end();
					return;
				}
				player.getControlerManager().startControler("PuroPuro");
			}
			if (componentId == OPTION_4) {
				player.getDialogueManager().startDialogue("SkillTeleport");
			}
			break;
		case 4:
			if (componentId == OPTION_1) {
				Magic.vineTeleport(player, new WorldTile(2597, 2913, 0));
				end();
			}
			if (componentId == OPTION_2) {
				Magic.vineTeleport(player, new WorldTile(2526, 2916, 0));
				end();
			}
			if (componentId == OPTION_3) {
				player.getDialogueManager().startDialogue("SkillTeleport");
			}
			break;

		default:
			end();
		}

	}

	public boolean canTeleport() {
		long currentTime = Utils.currentTimeMillis();
		if (player.getLockDelay() > currentTime)
			return false;
		if (player.getX() >= 2956 && player.getX() <= 3067 && player.getY() >= 5512 && player.getY() <= 5630
				|| (player.getX() >= 2756 && player.getX() <= 2875 && player.getY() >= 5512 && player.getY() <= 5627)) {
			player.getPackets().sendGameMessage("A magical force is blocking you from teleporting.");
			return false;
		}
		if (!player.getControlerManager().processMagicTeleport(player))
			return false;
		if (!player.getControlerManager().processItemTeleport(player))
			return false;
		if (!player.getControlerManager().processObjectTeleport(player))
			return false;
		return true;
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
