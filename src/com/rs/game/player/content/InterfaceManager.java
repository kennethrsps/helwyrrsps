package com.rs.game.player.content;

import java.util.concurrent.ConcurrentHashMap;

import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.npc.Drop;
import com.rs.game.player.Inventory;
import com.rs.game.player.Player;
import com.rs.game.player.content.clans.ClansManager;
import com.rs.utils.Colors;
import com.rs.utils.NPCDrops;
import com.rs.utils.Utils;

public class InterfaceManager {

	public static final int FIXED_WINDOW_ID = 548;
	public static final int RESIZABLE_WINDOW_ID = 746;
	public static final int CHAT_BOX_TAB = 13;
	public static final int FIXED_SCREEN_TAB_ID = 27;
	public static final int RESIZABLE_SCREEN_TAB_ID = 28;
	public static final int FIXED_INV_TAB_ID = 166;
	public static final int RESIZABLE_INV_TAB_ID = 108;
	private Player player;
	private int windowsPane;

	private final ConcurrentHashMap<Integer, int[]> openedinterfaces = new ConcurrentHashMap<Integer, int[]>();

	private boolean resizableScreen;

	// new combat styles 1503

	public void sendFullScreenInterfaces() {
		player.getPackets().sendWindowsPane(746, 0);
		sendTab(21, 752); // chatbox
		sendRightSideScreenTabs();// sendTab(22, 751);
		sendTab(15, 745);
		sendTab(24, 754);
		sendTab(195, 748); // hp bar
		sendTab(196, 749); // prayer bar
		sendTab(197, 750); // run bar
		sendTab(198, 747); // hide run?
		player.getPackets().sendInterface(true, 752, 9, 137); /// another
																/// chatbox? lol
		sendCombatStyles();
		sendTaskSystem();
		sendSkills();
		sendInventory();
		sendEquipment();
		sendPrayerBook();
		sendMagicBook();
		sendTab(120, 550); // friend list
		sendTab(121, 1109); // 551 ignore now friendchat
		sendTab(122, 1110); // 589 old clan chat now new clan chat
		sendSettings();
		sendEmotes();
		sendQuests();
		sendTab(125, 187); // music
		sendTab(126, 34); // notes
		sendTab(129, 182); // logout*/
		sendSquealOfFortuneTab();
	}

	public void sendFixedInterfaces() {
		player.getPackets().sendWindowsPane(548, 0);
		sendTab(161, 752);
		sendRightSideScreenTabs();// sendTab(37, 751);
		sendTab(23, 745);
		sendTab(25, 754);
		sendTab(155, 747);
		sendTab(151, 748);
		sendTab(152, 749);
		sendTab(153, 750);
		player.getPackets().sendInterface(true, 752, 9, 137);
		player.getPackets().sendInterface(true, 548, 9, 167);
		sendMagicBook();
		sendPrayerBook();
		sendEquipment();
		sendInventory();
		sendTab(181, 1109);// 551 ignore now friendchat
		sendTab(182, 1110);// 589 old clan chat now new clan chat
		sendTab(180, 550);// friend list
		sendTab(185, 187);// music
		sendTab(186, 34); // notes
		sendTab(189, 182);
		sendSkills();
		sendEmotes();
		sendSettings();
		sendTaskSystem();
		sendQuests();
		sendCombatStyles();
		sendSquealOfFortuneTab();
	}

	/**
	 * Overrides the "Report" button.
	 */
	public void sendRightSideScreenTabs() {
		sendTab(resizableScreen ? 22 : 37, 751);
		sendIComponentText(751, 16, "<shad=000000>"
				+ (player.getCombatDefinitions().isSheathe() ? Colors.green : Colors.red) + "Sheathe</col></shad>");
	}

	public void sendTaskSystem() {
		sendTab(resizableScreen ? 112 : 172, 930);
		TaskTab.sendTab(player);
	}

	public void sendQuests() {
		sendTab(resizableScreen ? 114 : 174, 506);
		QuestTab.sendTab(player);
	}

	public boolean isResizableScreen() {
		return resizableScreen;
	}

	public InterfaceManager(Player player) {
		this.player = player;
	}

	public void sendTab(int tabId, int interfaceId) {
		player.getPackets().sendInterface(true, resizableScreen ? RESIZABLE_WINDOW_ID : FIXED_WINDOW_ID, tabId,
				interfaceId);
	}

	public void sendChatBoxInterface(int interfaceId) {
		player.getPackets().sendInterface(true, 752, CHAT_BOX_TAB, interfaceId);
	}

	public void closeChatBoxInterface() {
		player.getPackets().closeInterface(CHAT_BOX_TAB);
	}

	public void sendOverlay(int interfaceId, boolean fullScreen) {
		sendTab(resizableScreen ? fullScreen ? 1 : 11 : 0, interfaceId);
	}

	public void closeOverlay(boolean fullScreen) {
		player.getPackets().closeInterface(resizableScreen ? fullScreen ? 1 : 11 : 0);
	}

	public void sendInterface(int interfaceId) {
		player.getPackets().sendInterface(false, resizableScreen ? RESIZABLE_WINDOW_ID : FIXED_WINDOW_ID,
				resizableScreen ? RESIZABLE_SCREEN_TAB_ID : FIXED_SCREEN_TAB_ID, interfaceId);
	}

	public static int getComponentUId(int interfaceId, int componentId) {
		return interfaceId << 16 | componentId;
	}

	public final void sendInterfaces() {
		if (player.getDisplayMode() == 2 || player.getDisplayMode() == 3) {
			resizableScreen = true;
			sendFullScreenInterfaces();
		} else {
			resizableScreen = false;
			sendFixedInterfaces();
		}
		player.getSkills().sendInterfaces();
		player.getCombatDefinitions().sendUnlockAttackStylesButtons();
		player.getMusicsManager().unlockMusicPlayer();
		player.getEmotesManager().unlockEmotesBook();
		player.getInventory().unlockInventoryOptions();
		player.getPrayer().unlockPrayerBookButtons();
		ClansManager.unlockBanList(player);
		if (player.getFamiliar() != null && player.isRunning())
			player.getFamiliar().unlock();
		player.getControlerManager().sendInterfaces();
	}

	public void replaceRealChatBoxInterface(int interfaceId) {
		player.getPackets().sendInterface(true, 752, 11, interfaceId);
	}

	public void closeReplacedRealChatBoxInterface() {
		player.getPackets().closeInterface(752, 11);
	}

	public void sendWindowPane() {
		player.getPackets().sendWindowsPane(resizableScreen ? 746 : 548, 0);
	}

	public void sendSquealOfFortuneTab() {
		player.getSquealOfFortune().sendSpinCounts();
		// this config used in cs2 to tell current extra tab type (0 - none, 1 -
		// sof, 2 - armies minigame tab)
		player.getPackets().sendGlobalConfig(823, 1);
		sendTab(resizableScreen ? 119 : 179, 1139);
	}

	public void sendXPPopup() {
		sendTab(resizableScreen ? 38 : 10, 1213); // xp
	}

	public void sendXPDisplay() {
		sendXPDisplay(1215); // xp counter
	}

	public void sendXPDisplay(int interfaceId) {
		sendTab(resizableScreen ? 27 : 29, interfaceId); // xp counter
	}

	public void closeXPPopup() {
		player.getPackets().closeInterface(resizableScreen ? 38 : 10);
	}

	public void closeXPDisplay() {
		player.getPackets().closeInterface(resizableScreen ? 27 : 29);
	}

	public void sendEquipment() {
		sendTab(resizableScreen ? 116 : 176, 387);
	}

	public void closeInterface(int one, int two) {
		player.getPackets().closeInterface(resizableScreen ? two : one);
	}

	public void closeEquipment() {
		player.getPackets().closeInterface(resizableScreen ? 116 : 176);
	}

	public void sendInventory() {
		sendTab(resizableScreen ? 115 : 175, Inventory.INVENTORY_INTERFACE);
	}

	public void closeInventory() {
		player.getPackets().closeInterface(resizableScreen ? 115 : 175);
	}

	public void closeSkills() {
		player.getPackets().closeInterface(resizableScreen ? 113 : 173);
	}

	public void closeCombatStyles() {
		player.getPackets().closeInterface(resizableScreen ? 111 : 171);
	}

	public void sendCombatStyles() {
		sendTab(resizableScreen ? 111 : 171, 884);
	}

	public void sendSkills() {
		sendTab(resizableScreen ? 113 : 173, 320);
	}

	public void refreshSkills() {
		player.getPackets().sendIComponentText(320, 151, "Total level: " + player.getSkills().getTotalLevel());
	}

	public void sendSettings() {
		sendSettings(261);
	}

	public void closeSettings() {
		player.getPackets().closeInterface(resizableScreen ? 123 : 183);
	}

	public void sendSettings(int interfaceId) {
		sendTab(resizableScreen ? 123 : 183, interfaceId);
	}

	public void sendPrayerBook() {
		sendTab(resizableScreen ? 117 : 177, 271);
	}

	public void closePrayerBook() {
		player.getPackets().closeInterface(resizableScreen ? 117 : 177);
	}

	public void sendMagicBook() {
		sendTab(resizableScreen ? 118 : 178, player.getCombatDefinitions().getSpellBook());
	}

	public void closeMagicBook() {
		player.getPackets().closeInterface(resizableScreen ? 118 : 178);
	}

	public void sendEmotes() {
		sendTab(resizableScreen ? 124 : 184, 590);
	}

	public void closeEmotes() {
		player.getPackets().closeInterface(resizableScreen ? 124 : 184);
	}

	public boolean addInterface(int windowId, int tabId, int childId) {
		if (openedinterfaces.containsKey(tabId))
			player.getPackets().closeInterface(tabId);
		openedinterfaces.put(tabId, new int[] { childId, windowId });
		return openedinterfaces.get(tabId)[0] == childId;
	}

	public boolean containsInterface(int tabId, int childId) {
		if (childId == windowsPane)
			return true;
		if (!openedinterfaces.containsKey(tabId))
			return false;
		return openedinterfaces.get(tabId)[0] == childId;
	}

	public int getTabWindow(int tabId) {
		if (!openedinterfaces.containsKey(tabId))
			return FIXED_WINDOW_ID;
		return openedinterfaces.get(tabId)[1];
	}

	public boolean containsInterface(int childId) {
		if (childId == windowsPane)
			return true;
		for (int[] value : openedinterfaces.values())
			if (value[0] == childId)
				return true;
		return false;
	}

	public boolean containsTab(int tabId) {
		return openedinterfaces.containsKey(tabId);
	}

	public void removeAll() {
		openedinterfaces.clear();
	}

	public boolean containsScreenInter() {
		return containsTab(resizableScreen ? RESIZABLE_SCREEN_TAB_ID : FIXED_SCREEN_TAB_ID);
	}

	public void closeScreenInterface() {
		player.getPackets().closeInterface(resizableScreen ? RESIZABLE_SCREEN_TAB_ID : FIXED_SCREEN_TAB_ID);
	}

	public boolean containsInventoryInter() {
		return containsTab(resizableScreen ? RESIZABLE_INV_TAB_ID : FIXED_INV_TAB_ID);
	}

	public void closeInventoryInterface() {
		player.getPackets().closeInterface(resizableScreen ? RESIZABLE_INV_TAB_ID : FIXED_INV_TAB_ID);
	}

	public boolean containsChatBoxInter() {
		return containsTab(CHAT_BOX_TAB);
	}

	public boolean removeTab(int tabId) {
		return openedinterfaces.remove(tabId) != null;
	}

	public void sendFadingInterface(int backgroundInterface) {
		if (hasRezizableScreen())
			player.getPackets().sendInterface(true, RESIZABLE_WINDOW_ID, 12, backgroundInterface);
		else
			player.getPackets().sendInterface(true, FIXED_WINDOW_ID, 11, backgroundInterface);
	}

	public void closeFadingInterface() {
		if (hasRezizableScreen())
			player.getPackets().closeInterface(12);
		else
			player.getPackets().closeInterface(11);
	}

	public void sendScreenInterface(int backgroundInterface, int interfaceId) {
		player.getInterfaceManager().closeScreenInterface();
		if (hasRezizableScreen()) {
			player.getPackets().sendInterface(false, RESIZABLE_WINDOW_ID, 40, backgroundInterface);
			player.getPackets().sendInterface(false, RESIZABLE_WINDOW_ID, 41, interfaceId);
		} else {
			player.getPackets().sendInterface(false, FIXED_WINDOW_ID, 200, backgroundInterface);
			player.getPackets().sendInterface(false, FIXED_WINDOW_ID, 201, interfaceId);
		}
		player.setCloseInterfacesEvent(new Runnable() {

			@Override
			public void run() {
				if (hasRezizableScreen()) {
					player.getPackets().closeInterface(40);
					player.getPackets().closeInterface(41);
				} else {
					player.getPackets().closeInterface(200);
					player.getPackets().closeInterface(201);
				}
			}
		});
	}

	public boolean hasRezizableScreen() {
		return resizableScreen;
	}

	public void setWindowsPane(int windowsPane) {
		this.windowsPane = windowsPane;
	}

	public int getWindowsPane() {
		return windowsPane;
	}

	public void gazeOrbOfOculus() {
		player.getPackets().sendWindowsPane(475, 0);
		player.getPackets().sendInterface(true, 475, 57, 751);
		player.getPackets().sendInterface(true, 475, 55, 752);
		player.setCloseInterfacesEvent(new Runnable() {

			@Override
			public void run() {
				player.getPackets().sendWindowsPane(player.getInterfaceManager().hasRezizableScreen() ? 746 : 548, 0);
				player.getPackets().sendResetCamera();
			}
		});
	}

	public int openGameTab(int tabId) {
		player.getPackets().sendGlobalConfig(168, tabId);
		int lastTab = 4;
		return lastTab;
	}

	public void sendSquealOfFortune() {
		sendTab(resizableScreen ? 119 : 179, 1139);
		player.getPackets().sendGlobalConfig(823, 1);
	}

	public void setWindowInterface(int componentId, int interfaceId) {
		player.getPackets().sendInterface(true, resizableScreen ? RESIZABLE_WINDOW_ID : FIXED_WINDOW_ID, componentId,
				interfaceId);
	}

	public void sendInventoryInterface(int childId) {
		player.getPackets().sendInterface(false, resizableScreen ? RESIZABLE_WINDOW_ID : FIXED_WINDOW_ID,
				resizableScreen ? RESIZABLE_INV_TAB_ID : FIXED_INV_TAB_ID, childId);
	}

	public void sendSquealOverlay() {
		sendTab(isResizableScreen() ? 0 : 10, 1252);
	}

	public void closeSquealOverlay() {
		player.getPackets().closeInterface(isResizableScreen() ? 0 : 10);
	}

	/**
	 * Sends an interface with the possible NPC's that drop a mentioned item.
	 * 
	 * @param defs
	 *            The Item Definitions.
	 */
	public void sendItemDrops(ItemDefinitions defs) {
		int i = 0;
		String dropEntry = "";
		player.getInterfaceManager().sendInterface(275);
		sendIComponentText(275, 1, "Drops: <col=9900FF><shad=000000>" + defs.name + "</col></shad>");
		for (i = 10; i < 310; i++)
			sendIComponentText(275, i, "");
		i = 0;
		for (int n = 0; n < Utils.getNPCDefinitionsSize(); n++) {
			NPCDefinitions def = NPCDefinitions.getNPCDefinitions(n);
			Drop[] drops = NPCDrops.getDrops(def.getId());
			if (drops != null) {
				for (Drop drop : drops) {
					if (drop.getItemId() == 0)
						continue;
					ItemDefinitions itemDefs = ItemDefinitions.getItemDefinitions(drop.getItemId());
					if (itemDefs.getId() != defs.getId() || !itemDefs.name.contains(defs.name)
							|| !itemDefs.name.equalsIgnoreCase(defs.name))
						continue;
					if (def == null || def.getName().toLowerCase().contains("null"))
						continue;
					StringBuilder sb = new StringBuilder("").append(def.name).append(": ").append(itemDefs.name)
							.append(drop.getMaxAmount() == 1 ? ("")
									: drop.getMinAmount() == drop.getMaxAmount() ? (" (" + drop.getMaxAmount() + ")")
											: (" (" + drop.getMinAmount() + "-" + drop.getMaxAmount() + ")"))
							.append(" [").append((int) drop.getRate()).append("% Chance]");
					dropEntry = sb.toString();
					if (i < 300)
						sendIComponentText(275, 10 + i, dropEntry);
					dropEntry = "";
					i++;
				}
			}
		}
	}

	/**
	 * Sends an interface with the possible item drops from the mentioned NPC
	 * 
	 * @param defs
	 *            The NPC Definitions.
	 */
	public void sendNPCDrops(NPCDefinitions defs) {
		int i = 0;
		String dropEntry = "";
		player.getInterfaceManager().sendInterface(275);
		sendIComponentText(275, 1, "Drops: <col=9900FF><shad=000000>" + defs.name + "</col></shad>");
		for (i = 10; i < 310; i++)
			sendIComponentText(275, i, "");

		i = 0;
		Drop[] drops = NPCDrops.getDrops(defs.getId());
		if (drops != null) {
			for (Drop drop : drops) {
				if (drop.getItemId() == 0)
					continue;
				ItemDefinitions itemDefs = ItemDefinitions.getItemDefinitions(drop.getItemId());
				StringBuilder sb = new StringBuilder("").append(itemDefs.name)
						.append(drop.getMaxAmount() == 1 ? ("")
								: drop.getMinAmount() == drop.getMaxAmount() ? (" (" + drop.getMaxAmount() + ")")
										: (" (" + drop.getMinAmount() + "-" + drop.getMaxAmount() + ")"))
						.append(" [").append((int) drop.getRate()).append("% Chance]");
				dropEntry = sb.toString();
				if (i < 300)
					sendIComponentText(275, 10 + i, dropEntry);
				dropEntry = "";
				i++;
			}
		}
	}

	/**
	 * Just a shortener.
	 * 
	 * @param interfaceId
	 *            The interface ID.
	 * @param componentId
	 *            The component ID.
	 * @param text
	 *            The text to display.
	 */
	private void sendIComponentText(int interfaceId, int componentId, String text) {
		player.getPackets().sendIComponentText(interfaceId, componentId, text);
	}

	public void sendHelpInterface() {
		player.getPackets().sendIComponentText(1245, 330,
				Colors.green + Settings.SERVER_NAME + " </col>- " + Colors.red + Settings.WEBSITE);
		player.getPackets().sendIComponentText(1245, 13,
				"Welcome to <col=0033CC>" + Settings.SERVER_NAME + "</col>; Owner = <col=990000>Zeus</col>.");
		player.getPackets().sendIComponentText(1245, 14, "How to teleport to bosses/skilling/training areas:");
		player.getPackets().sendIComponentText(1245, 15, "");
		player.getPackets().sendIComponentText(1245, 16,
				"You can teleport all around " + Settings.SERVER_NAME + " by using the Quest Tab button");
		player.getPackets().sendIComponentText(1245, 17, "");
		player.getPackets().sendIComponentText(1245, 18, "Basic Money Making methods:");
		player.getPackets().sendIComponentText(1245, 19,
				"One of the easiest and most reliable money sources is Voting!");
		player.getPackets().sendIComponentText(1245, 20, "");
		player.getPackets().sendIComponentText(1245, 20,
				"By doing ::vote you can receive up to 2m Gold for every vote book.");
		player.getPackets().sendIComponentText(1245, 21,
				"Another quick way of making money would be the Thieving Stalls at ::home");
		player.getPackets().sendIComponentText(1245, 22, "The main money making source is by defeating bosses though!");
		player.getPackets().sendIComponentText(1245, 23, "You can sell most of your items on the Grand Exchange.");
		player.getInterfaceManager().sendInterface(1245);
	}

	/* starts: handles new teleports system in quest tab */
	public static int getPlayerInterfaceSelected() {
		return playerSelected1156;
	}

	public static int setPlayerInterfaceSelected(int value) {
		return playerSelected1156 = value;
	}

	public static int playerSelected1156 = 0;
	// ends:

	public void openDungTab() {
		this.openGameTab(3);
	}

	public void sendDungPartyInterface() {
		sendTab(this.resizableScreen ? 114 : 174, 939);
		player.getPackets().sendGlobalConfig(234, 3);
	}

	public void closeDungPartyInterface() {
		sendTab(resizableScreen ? 114 : 174, 506);
		player.getPackets().sendGlobalConfig(234, 0);
		QuestTab.sendTab(player);
	}

	public void sendDungPartyTab(int interfaceId) {
		sendTab(this.resizableScreen ? 114 : 174, interfaceId);
		player.getPackets().sendGlobalConfig(234, 3);

	}
}