package com.rs.game.player.dialogue.impl;

import com.rs.Settings;
import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Equipment;
import com.rs.game.player.Player;
import com.rs.game.player.content.FadingScreen;
import com.rs.game.player.content.Magic;
import com.rs.game.player.content.PlayerDesign;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Logger;
import com.rs.utils.StarterMap;
import com.rs.utils.Utils;

/**
 * Handles the Starter tutorial for new players.
 * 
 * @author Sky
 */
public class StarterTutorialD extends Dialogue {

	@Override
	public void start() {
		if (player.hasCompleted()) {
			sendNPCDialogue(6139, SAD, "These shoes will kill me...");
			// TODO add the actual town crier dialogue.
			stage = 99;
			return;
		}
		sendNPCDialogue(6139, NORMAL, "Hello, and Welcome to " + Settings.SERVER_NAME + "!");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue("Choose your Gamemode:",
					"<img=16>Expert (x<col=ff0000>" + Settings.EXPERT_XP + "</col> EXP & x<col=ff0000>"
							+ Settings.EXPERT_DROP + "</col> drop rate)",
					"Veteran (x<col=ff0000>" + Settings.VET_XP + "</col> EXP & x<col=ff0000>" + Settings.VET_DROP
							+ "</col> drop rate)",
					"Intermediate (x<col=ff0000>" + Settings.INTERM_XP + "</col> EXP & x<col=ff0000>"
							+ Settings.INTERM_DROP + "</col> drop rate)",
					"Easy (x<col=ff0000>" + Settings.EASY_XP + "</col> EXP & x<col=ff0000>" + Settings.EASY_DROP
							+ "</col> drop rate)",
					"<img=10>IronMan (x<col=ff0000>" + Settings.IRONMAN_XP + "</col> EXP & x<col=ff0000>"
							+ Settings.IRONMAN_DROP + "</col> drop rate)");
			stage = 0;
			break;
		case 0:
			if (componentId == OPTION_1) {
				sendOptionsDialogue("Choose the <col=ff0000>Expert</col> gamemode?", "Yes", "No");
				stage = 50;
			}
			if (componentId == OPTION_2) {
				sendOptionsDialogue("Choose the <col=ff0000>Veteran</col> gamemode?", "Yes", "No");
				stage = 1;
			}
			if (componentId == OPTION_3) {
				sendOptionsDialogue("Choose the <col=ff0000>Intermediate</col> gamemode?", "Yes", "No");
				stage = 10;
			}
			if (componentId == OPTION_4) {
				sendOptionsDialogue("Choose the <col=ff0000>Easy</col> gamemode?", "Yes", "No");
				stage = 20;
			}
			if (componentId == OPTION_5) {
				sendOptionsDialogue("Which Ironman mode would you like to play on?",
						"<img=10>IronMan (x<col=ff0000>" + Settings.IRONMAN_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.IRONMAN_DROP + "</col> drop rate)",
						"<img=11>HC IronMan (x<col=ff0000>" + Settings.IRONMAN_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.HCIRONMAN_DROP + "</col> drop rate)",
						"Back to previous options..");
				stage = 2;
			}
			break;
		case 2:
			if (componentId == OPTION_1) {
				sendOptionsDialogue("Choose the <col=ff0000>Ironman</col> gamemode?", "Yes", "No");
				stage = 30;
			}
			if (componentId == OPTION_2) {
				sendOptionsDialogue("Choose the <col=ff0000>Hardcore Ironman</col> gamemode?", "Yes", "No");
				stage = 40;
			}
			if (componentId == OPTION_3) {
				sendOptionsDialogue("Choose your Gamemode:",
						"<img=16>Expert (x<col=ff0000>" + Settings.EXPERT_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.EXPERT_DROP + "</col> drop rate)",
						"Veteran (x<col=ff0000>" + Settings.VET_XP + "</col> EXP & x<col=ff0000>" + Settings.VET_DROP
								+ "</col> drop rate)",
						"Intermediate (x<col=ff0000>" + Settings.INTERM_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.INTERM_DROP + "</col> drop rate)",
						"Easy (x<col=ff0000>" + Settings.EASY_XP + "</col> EXP & x<col=ff0000>" + Settings.EASY_DROP
								+ "</col> drop rate)",
						"<img=10>IronMan (x<col=ff0000>" + Settings.IRONMAN_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.IRONMAN_DROP + "</col> drop rate)");
				stage = 0;
			}
			break;
		case 1:
			if (componentId == OPTION_1) {
				if (canAddReward())
					player.addMoney(2500000);
				sendNPCDialogue(6139, NORMAL,
						"You've chosen the <col=ff0000>Veteran</col> Game Mode! You now have an EXP multiplier of x<col=ff0000>"
								+ Settings.VET_XP + "</col> and a Drop Rate of x<col=ff0000>" + Settings.VET_DROP
								+ "</col>.");
				player.setVeteran(true);
				completeTutorial();
				stage = 99;
			}
			if (componentId == OPTION_2) {
				sendOptionsDialogue("Choose your Gamemode:",
						"<img=16>Expert (x<col=ff0000>" + Settings.EXPERT_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.EXPERT_DROP + "</col> drop rate)",
						"Veteran (x<col=ff0000>" + Settings.VET_XP + "</col> EXP & x<col=ff0000>" + Settings.VET_DROP
								+ "</col> drop rate)",
						"Intermediate (x<col=ff0000>" + Settings.INTERM_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.INTERM_DROP + "</col> drop rate)",
						"Easy (x<col=ff0000>" + Settings.EASY_XP + "</col> EXP & x<col=ff0000>" + Settings.EASY_DROP
								+ "</col> drop rate)",
						"<img=10>IronMan (x<col=ff0000>" + Settings.IRONMAN_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.IRONMAN_DROP + "</col> drop rate)");
				stage = 0;
			}
			break;
		case 10:
			if (componentId == OPTION_1) {
				if (canAddReward())
					player.addMoney(2500000);
				sendNPCDialogue(6139, NORMAL,
						"You've chosen the <col=ff0000>Intermediate</col> Game Mode! You now have an EXP multiplier of x<col=ff0000>"
								+ Settings.INTERM_XP + "</col> and a Drop Rate of x<col=ff0000>" + Settings.INTERM_DROP
								+ "</col>.");
				player.setIntermediate(true);
				completeTutorial();
				stage = 99;
			}
			if (componentId == OPTION_2) {
				sendOptionsDialogue("Choose your Gamemode:",
						"<img=16>Expert (x<col=ff0000>" + Settings.EXPERT_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.EXPERT_DROP + "</col> drop rate)",
						"Veteran (x<col=ff0000>" + Settings.VET_XP + "</col> EXP & x<col=ff0000>" + Settings.VET_DROP
								+ "</col> drop rate)",
						"Intermediate (x<col=ff0000>" + Settings.INTERM_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.INTERM_DROP + "</col> drop rate)",
						"Easy (x<col=ff0000>" + Settings.EASY_XP + "</col> EXP & x<col=ff0000>" + Settings.EASY_DROP
								+ "</col> drop rate)",
						"<img=10>IronMan (x<col=ff0000>" + Settings.IRONMAN_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.IRONMAN_DROP + "</col> drop rate)");
				stage = 0;
			}
			break;
		case 20:
			if (componentId == OPTION_1) {
				if (canAddReward())
					player.addMoney(2500000);
				sendNPCDialogue(6139, NORMAL,
						"You've chosen the <col=ff0000>Easy</col> Game Mode! You now have an EXP multiplier of x<col=ff0000>"
								+ Settings.EASY_XP + "</col> and a Drop Rate of x<col=ff0000>" + Settings.EASY_DROP
								+ "</col>.");
				player.setEasy(true);
				completeTutorial();
				stage = 99;
			}
			if (componentId == OPTION_2) {
				sendOptionsDialogue("Choose your Gamemode:",
						"<img=16>Expert (x<col=ff0000>" + Settings.EXPERT_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.EXPERT_DROP + "</col> drop rate)",
						"Veteran (x<col=ff0000>" + Settings.VET_XP + "</col> EXP & x<col=ff0000>" + Settings.VET_DROP
								+ "</col> drop rate)",
						"Intermediate (x<col=ff0000>" + Settings.INTERM_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.INTERM_DROP + "</col> drop rate)",
						"Easy (x<col=ff0000>" + Settings.EASY_XP + "</col> EXP & x<col=ff0000>" + Settings.EASY_DROP
								+ "</col> drop rate)",
						"<img=10>IronMan (x<col=ff0000>" + Settings.IRONMAN_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.IRONMAN_DROP + "</col> drop rate)");
				stage = 0;
			}
			break;
		case 30:
			if (componentId == OPTION_1) {
				if (canAddReward())
					player.addMoney(2500000);
				sendNPCDialogue(6139, NORMAL,
						"You've chosen the <col=ff0000>Ironman</col> Game Mode! You now have an EXP multiplier of x<col=ff0000>"
								+ Settings.IRONMAN_XP + "</col> and a Drop Rate of x<col=ff0000>"
								+ Settings.IRONMAN_DROP + "</col>.");
				player.setIronMan(true);
				completeTutorial();
				stage = 99;
			}
			if (componentId == OPTION_2) {
				sendOptionsDialogue("Choose your Gamemode:",
						"<img=16>Expert (x<col=ff0000>" + Settings.EXPERT_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.EXPERT_DROP + "</col> drop rate)",
						"Veteran (x<col=ff0000>" + Settings.VET_XP + "</col> EXP & x<col=ff0000>" + Settings.VET_DROP
								+ "</col> drop rate)",
						"Intermediate (x<col=ff0000>" + Settings.INTERM_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.INTERM_DROP + "</col> drop rate)",
						"Easy (x<col=ff0000>" + Settings.EASY_XP + "</col> EXP & x<col=ff0000>" + Settings.EASY_DROP
								+ "</col> drop rate)",
						"<img=10>IronMan (x<col=ff0000>" + Settings.IRONMAN_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.IRONMAN_DROP + "</col> drop rate)");
				stage = 0;
			}
			break;
		case 40:
			if (componentId == OPTION_1) {
				if (canAddReward())
					player.addMoney(2500000);
				sendNPCDialogue(6139, NORMAL,
						"You've chosen the <col=ff0000>Hardcore Ironman</col> Game Mode! You now have an EXP multiplier of x<col=ff0000>"
								+ Settings.IRONMAN_XP + "</col> and a Drop Rate of x<col=ff0000>"
								+ Settings.HCIRONMAN_DROP + "</col>.");
				player.setHCIronMan(true);
				completeTutorial();
				stage = 99;
			}
			if (componentId == OPTION_2) {
				sendOptionsDialogue("Choose your Gamemode:",
						"<img=16>Expert (x<col=ff0000>" + Settings.EXPERT_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.EXPERT_DROP + "</col> drop rate)",
						"Veteran (x<col=ff0000>" + Settings.VET_XP + "</col> EXP & x<col=ff0000>" + Settings.VET_DROP
								+ "</col> drop rate)",
						"Intermediate (x<col=ff0000>" + Settings.INTERM_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.INTERM_DROP + "</col> drop rate)",
						"Easy (x<col=ff0000>" + Settings.EASY_XP + "</col> EXP & x<col=ff0000>" + Settings.EASY_DROP
								+ "</col> drop rate)",
						"<img=10>IronMan (x<col=ff0000>" + Settings.IRONMAN_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.IRONMAN_DROP + "</col> drop rate)");
				stage = 0;
			}
			break;
		case 50:
			if (componentId == OPTION_1) {
				if (canAddReward())
					player.addMoney(100000);
				sendNPCDialogue(6139, NORMAL,
						"You've chosen the <col=ff0000>Expert</col> Game Mode! You now have an EXP multiplier of x<col=ff0000>"
								+ Settings.EXPERT_XP + "</col> and a Drop Rate of x<col=ff0000>" + Settings.EXPERT_DROP
								+ "</col>.");
				player.setExpert(true);
				completeTutorial();
				stage = 99;
			}
			if (componentId == OPTION_2) {
				sendOptionsDialogue("Choose your Gamemode:",
						"<img=16>Expert (x<col=ff0000>" + Settings.EXPERT_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.EXPERT_DROP + "</col> drop rate)",
						"Veteran (x<col=ff0000>" + Settings.VET_XP + "</col> EXP & x<col=ff0000>" + Settings.VET_DROP
								+ "</col> drop rate)",
						"Intermediate (x<col=ff0000>" + Settings.INTERM_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.INTERM_DROP + "</col> drop rate)",
						"Easy (x<col=ff0000>" + Settings.EASY_XP + "</col> EXP & x<col=ff0000>" + Settings.EASY_DROP
								+ "</col> drop rate)",
						"<img=10>IronMan (x<col=ff0000>" + Settings.IRONMAN_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.IRONMAN_DROP + "</col> drop rate)");
				stage = 0;
			}
			break;

		case 99:
			player.getInterfaceManager().sendHelpInterface();
			player.getInterfaceManager().closeChatBoxInterface();
			end();

			break;
		}
	}

	@Override
	public void finish() {
		player.getInterfaceManager().closeChatBoxInterface();
	}

	/**
	 * Teleports the player.
	 * 
	 * @param player
	 *            The player.
	 */
	public static void teleport(final Player player) {
		player.setNextWorldTile(Settings.START_PLAYER_LOCATION);
		player.setNextFaceWorldTile(new WorldTile(4316, 825, 4));
		player.lock(5);
		WorldTasksManager.schedule(new WorldTask() {
			int tick;

			@Override
			public void run() {
				tick++;
				if (tick == 1) {
					player.setNextGraphics(new Graphics(3018));
					player.setNextAnimation(new Animation(16386));
				} else if (tick == 3)
					player.setNextAnimation(new Animation(16393));
				else if (tick == 4) {
					player.setNextWorldTile(Settings.START_PLAYER_LOCATION);
					player.setNextAnimation(new Animation(-1));
					PlayerDesign.open(player);
					StarterTutorialD.addNPCHintIcon(player);
					player.setLogedIn();
					player.unlock();
					stop();
				}
			}
		}, 0, 1);
	}

	/**
	 * The starter area square coords.
	 * 
	 * @param tile
	 *            The tiles.
	 * @param player
	 *            The player.
	 * @return if Inside square.
	 */
	private static boolean starterArea(WorldTile tile, Player player) {
		int destX = player.getX();
		int destY = player.getY();
		return (destX >= 4312 && destY >= 822 && destX <= 4321 && destY <= 828);
	}

	/**
	 * Checks the starter area.
	 * 
	 * @param player
	 *            The player to check.
	 * @return if In area.
	 */
	public static boolean checkStarterArea(Player player) {
		if (!player.hasCompleted()) {
			if (!starterArea(player, player)) {
				player.setNextWorldTile(Settings.START_PLAYER_LOCATION);
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds hint icon to the players map.
	 * 
	 * @param player
	 *            The player.
	 */
	public static void addNPCHintIcon(Player player) {
		NPC guide = World.findNPC(player, 6139);
		if (guide != null)
			player.getHintIconsManager().addHintIcon(guide, 0, -1, false);
		guide.faceEntity(player);
		Dialogue.sendNPCDialogueNoContinue(player, 6139, Dialogue.NORMAL, "'Ey there! Come talk to me for a second.");
	}

	/**
	 * Completes tutorial - hands out rewards.
	 */
	private void completeTutorial() {
		player.getHintIconsManager().removeUnsavedHintIcon();
		Dialogue.closeNoContinueDialogue(player);
		player.setCompleted();
		player.setDoubleXpTimer(System.currentTimeMillis() + 7200000);
		player.getGlobalPlayerUpdater().generateAppearenceData();
		player.getInterfaceManager().sendTaskSystem();
		player.getInterfaceManager().openGameTab(1);
		if (!canAddReward()) {
			player.sendMessage(
					Colors.red + "You did not receive your starter kit, you've already received it 2 times.");
			return;
		}
		player.getEquipment().set(Equipment.SLOT_AMULET, new Item(1712));
		player.getEquipment().set(Equipment.SLOT_FEET, new Item(3105));
		player.getEquipment().set(Equipment.SLOT_HANDS, new Item(7453));
		player.getEquipment().set(Equipment.SLOT_ARROWS, new Item(882, 100));
		player.getEquipment().set(Equipment.SLOT_RING, new Item(2552));
		player.getEquipment().set(Equipment.SLOT_AURA, new Item(22302));
		player.getEquipment().set(Equipment.SLOT_CAPE, new Item(1052));
		player.getEquipment().refresh(Equipment.SLOT_AMULET, Equipment.SLOT_FEET, Equipment.SLOT_HANDS,
				Equipment.SLOT_AMULET, Equipment.SLOT_RING, Equipment.SLOT_AURA, Equipment.SLOT_CAPE,
				Equipment.SLOT_ARROWS);
		player.getInventory().addItem(new Item(841, 1));
		player.getInventory().addItem(new Item(1321, 1));
		player.getInventory().addItem(new Item(1381, 1));
		player.getInventory().addItem(new Item(11814, 1));
		player.getInventory().addItem(new Item(1129, 1));
		player.getInventory().addItem(new Item(1095, 1));
		player.getInventory().addItem(new Item(577, 1));
		player.getInventory().addItem(new Item(1011, 1));
		player.getInventory().addItem(new Item(8013, 15));
		player.getInventory().addItem(new Item(554, 200));
		player.getInventory().addItem(new Item(558, 200));
		player.getInventory().addItem(new Item(555, 200));
		player.getInventory().addItem(new Item(386, 100));
		player.getInventory().addItem(new Item(2677, 1));
		player.getInventory().addItem(new Item(4155, 1));
		player.getEquipment().refreshConfigs(false);

		World.sendWorldMessage(Colors.red + "<img=7>News:</col> Welcome: [" + player.getDisplayName() + "] - "
				+ "mode: [" + player.getXPMode() + "] - to " + Settings.SERVER_NAME + "!", false);

		StarterMap.getSingleton().addIP(player.getSession().getIP());
		Magic.sendNormalTeleportSpell(player, 0, 0, player.getHomeTile());

		WorldTasksManager.schedule(new WorldTask() {
			int loop = 0;

			@Override
			public void run() {
				loop++;
				if (loop == 1) {
					player.getPackets().receivePrivateMessage("Zeus", "Zeus", 2, "Hello " + player.getUsername()
							+ ". Welcome to Helwyr , need anything? Feel free to ask in friendschat.");

				}
				if (loop == 2) {
					player.getPackets().receivePrivateMessage("Zeus", "Zeus", 2,
							"Also do not forget to check our forums for the latest news and updates on the server.");
					this.stop();
				}

			}

		}, Utils.random(18, 26), Utils.random(15, 25));
		/*
		 * player.getTemporaryAttributtes().put("referralName", Boolean.TRUE);
		 * player.getPackets().
		 * sendInputNameScript("Name of player who referred you:(leave empty if none or not player)"
		 * );
		 */
		return;
	}

	public static void fade(final Player player) {
		final long time = FadingScreen.fade(player);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				try {
					FadingScreen.unfade(player, time, new Runnable() {
						@Override
						public void run() {
							player.lock(3);
						}
					});
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 5);
	}

	/**
	 * If the player's eligible for item reward.
	 * 
	 * @return If eligible.
	 */
	private boolean canAddReward() {
		int count = StarterMap.getSingleton().getCount(player.getSession().getIP());
		if (count > Settings.MAX_STARTER_COUNT)
			return false;
		return true;
	}
}