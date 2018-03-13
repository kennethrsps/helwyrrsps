package com.rs.game.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.activites.BountyHunter;
import com.rs.game.player.actions.ActionManager;
import com.rs.game.player.content.FriendChatsManager;
import com.rs.game.player.content.HintIconsManager;
import com.rs.game.player.content.InterfaceManager;
import com.rs.game.player.content.LividFarm;
import com.rs.game.player.content.LocalNPCUpdate;
import com.rs.game.player.content.LocalPlayerUpdate;
import com.rs.game.player.content.LogicPacket;
import com.rs.game.player.content.LoyaltyManager;
import com.rs.game.player.content.MoneyPouch;
import com.rs.game.player.content.OwnedObjectManager;
import com.rs.game.player.content.PriceCheckManager;
import com.rs.game.player.content.Toolbelt;
import com.rs.game.player.content.ToolbeltNew;
import com.rs.game.player.content.Trade;
import com.rs.game.player.content.VarBitManager;
import com.rs.game.player.content.clans.ClansManager;
import com.rs.game.player.content.construction.House;
import com.rs.game.player.content.grandExchange.GrandExchangeManager;
import com.rs.game.player.content.items.PrayerBooks;
import com.rs.game.player.content.pet.PetManager;
import com.rs.game.player.content.slayer.CooperativeSlayer;
import com.rs.game.player.content.xmas.XmasEvent;
import com.rs.game.player.cutscenes.CutscenesManager;
import com.rs.game.player.dialogue.DialogueManager;
import com.rs.game.player.dialogue.impl.LevelUp;
import com.rs.game.player.dialogue.impl.StarterTutorialD;
import com.rs.game.player.newquests.NewQuestManager;
import com.rs.network.Session;
import com.rs.utils.Colors;
import com.rs.utils.DonationRank;
import com.rs.utils.IsaacKeyPair;
import com.rs.utils.Logger;
import com.rs.utils.LoggingSystem;
import com.rs.utils.Utils;

/**
 * Handles player login.
 * 
 * @author Zeus
 */
public class LoginManager {

	/**
	 * Inits the login; from LoginPacketsDecoder.
	 * 
	 * @param player
	 *            The player to login.
	 * @param session
	 *            The current Session data.
	 * @param username
	 *            The players username.
	 * @param mac
	 *            The players MACAddress.
	 * @param displayMode
	 *            The players display mode.
	 * @param screenWidth
	 *            The player clients screen width.
	 * @param screenHeight
	 *            The player clients screen height.
	 * @param machineInformation
	 *            The players computer information.
	 * @param isaacKeyPair
	 *            The players unique isaac key.
	 */
	public static void init(Player player, Session session, String username, String mac, int displayMode,
			IsaacKeyPair isaacKeyPair) {
		player.session = session;
		player.username = username;
		player.displayMode = displayMode;
		player.setCurrentMac(mac);
		player.isaacKeyPair = isaacKeyPair;
		if (player.getNotes() == null)
			player.notesL = new Notes();
		if (player.geManager == null)
			player.geManager = new GrandExchangeManager();
		if (player.ArtisansWorkShopSupplies == null)
			player.ArtisansWorkShopSupplies = new int[5];
		if (player.dungManager == null)
			player.dungManager = new DungManager();
		if (player.petLootManager == null)
			player.petLootManager = new PetLootManager();
		if (player.slayerManager == null) {
			player.slayerManager = new SlayerManager();
		}
		if (player.squealOfFortune == null)
			player.squealOfFortune = new SquealOfFortune();
		if (player.elderTreeManager == null)
			player.elderTreeManager = new ElderTreeManager();
		if (player.gearPresets == null)
			player.gearPresets = new GearPresets();
		if (player.newQuestManager == null)
			player.newQuestManager = new NewQuestManager();
		if (player.dailyTaskManager == null)
			player.dailyTaskManager = new DailyTaskManager();
		if (player.dayOfWeekManager == null)
			player.dayOfWeekManager = new DayOfWeekManager();

		if (player.prayerBook == null)
			player.prayerBook = new boolean[PrayerBooks.BOOKS.length];
		if (player.boons == null)
			player.boons = new boolean[12];
		if (player.overrides == null)
			player.overrides = new CosmeticOverrides();
		if (player.unlockedCostumesIds == null)
			player.unlockedCostumesIds = new ArrayList<Integer>();
		if (player.animations == null)
			player.animations = new AnimationOverrides();
		if (player.ports == null)
			player.ports = new PlayerOwnedPort();
		if (player.xmas == null)
			player.xmas = new XmasEvent();
		if (player.banksManager == null)
			player.banksManager = new BanksManager();
		player.interfaceManager = new InterfaceManager(player);
		player.dialogueManager = new DialogueManager(player);
		player.hintIconsManager = new HintIconsManager(player);
		player.priceCheckManager = new PriceCheckManager(player);
		player.varsManager = new VarsManager(player);
		player.localPlayerUpdate = new LocalPlayerUpdate(player);
		player.localNPCUpdate = new LocalNPCUpdate(player);
		player.actionManager = new ActionManager(player);
		player.cutscenesManager = new CutscenesManager(player);
		player.pouch = new MoneyPouch(player);
		if (player.skills == null)
			player.skills = new Skills();
		if (player.petManager == null)
			player.petManager = new PetManager();
		if (player.auraManager == null)
			player.auraManager = new AuraManager();
		if (player.VBM == null)
			player.VBM = new VarBitManager(player);
		if (player.house == null)
			player.house = new House();
		player.coOpSlayer = new CooperativeSlayer();
		if (player.dominionTower == null)
			player.dominionTower = new DominionTower();
		player.trade = new Trade(player);
		player.loyaltyManager = new LoyaltyManager(player);
		if (player.getGlobalPlayerUpdater() == null)
			player.setGlobalPlayerUpdater(new GlobalPlayerUpdater());
		if (player.farmingManager == null)
			player.farmingManager = new FarmingManager();
		if (player.getToolBelt() == null)
			player.toolBelt = new Toolbelt(player);
		if (player.getToolBeltNew() == null)
			player.toolBeltNew = new ToolbeltNew(player);
		if (player.getTitles() == null)
			player.titles = new Titles();
		if (player.perkManager == null)
			player.perkManager = new PerkManager();
		if (player.treasureTrails == null)
			player.treasureTrails = new TreasureTrails();
		if (player.bountyHunter == null)
			player.bountyHunter = new BountyHunter();
		if (player.membership == null)
			player.membership = new MembershipHandler();
		player.getGlobalPlayerUpdater().setPlayer(player);
		player.getVarBitManager().setPlayer(player);
		player.getInventory().setPlayer(player);
		player.getEquipment().setPlayer(player);
		player.getFarmingManager().setPlayer(player);
		player.getToolBelt().setPlayer(player);
		player.getToolBeltNew().setPlayer(player);
		player.getDungManager().setPlayer(player);
		player.getHouse().setPlayer(player);
		player.getGearPresets().setPlayer(player);
		player.getBanksManager().setPlayer(player);
		player.getSquealOfFortune().setPlayer(player);
		player.getDailyTaskManager().setPlayer(player);
		player.getDayOfWeekManager().setPlayer(player);
		player.getSkills().setPlayer(player);
		player.getCombatDefinitions().setPlayer(player);
		player.getSlayerManager().setPlayer(player);
		player.getPrayer().setPlayer(player);
		player.getPetLootManager().setPlayer(player);
		player.getElderTreeManager().setPlayer(player);
		player.getBank().setPlayer(player);
		player.getControlerManager().setPlayer(player);
		player.getTitles().setPlayer(player);
		player.getNewQuestManager().setPlayer(player);
		player.getOverrides().setPlayer(player);
		player.getAnimations().setPlayer(player);
		player.getGEManager().setPlayer(player);
		player.getMusicsManager().setPlayer(player);
		player.getEmotesManager().setPlayer(player);
		player.getPorts().setPlayer(player);
		player.getXmas().setPlayer(player);
		player.getFriendsIgnores().setPlayer(player);
		player.getDominionTower().setPlayer(player);
		player.getAuraManager().setPlayer(player);
		player.getTreasureTrails().setPlayer(player);
		player.getPerkManager().setPlayer(player);
		player.getNotes().setPlayer(player);
		player.getCharges().setPlayer(player);
		player.getQuestManager().setPlayer(player);
		player.getPetManager().setPlayer(player);
		player.getBountyHunter().setPlayer(player);
		player.setDirection(Utils.getFaceDirection(0, -1));
		player.fairyRingCombination = new int[3];
		player.warriorCheck();
		player.setTemporaryMoveType(-1);
		player.logicPackets = new ConcurrentLinkedQueue<LogicPacket>();
		player.setSwitchItemCache(Collections.synchronizedList(new ArrayList<Integer>()));
		player.initEntity();
		World.addPlayer(player);
		player.setPacketsDecoderPing(Utils.currentTimeMillis());
		World.updateEntityRegion(player);
		player.increaseAFKTimer();
	}

	private static void sendGameData(Player player) {
		LoggingSystem.logIP(player);
		LoggingSystem.logAddress(player);
		player.farmingManager.init();
		// friend chat connect
		player.dayOfWeekManager.init();
		player.sendSawMillConfig();
		if (player.getCurrentFriendChatOwner() != null) {
			FriendChatsManager.joinChat(player.getCurrentFriendChatOwner(), player);
			if (player.getCurrentFriendChat() == null)
				player.setCurrentFriendChatOwner(null);
		}

		// connect to current clan
		if (player.getClanName() != null) {
			if (!ClansManager.connectToClan(player, player.getClanName(), false))
				player.setClanName(null);
		}

		// respawn familiar
		if (player.getFamiliar() != null)
			player.getFamiliar().respawnFamiliar(player);
		else
			player.getPetManager().init();

		// Vecna timer
		player.vecnaTimer(player.getVecnaTimer());

		if (player.isOwner())
			player.setRights(2);

		/**
		 * Inits G.E. for the player.
		 */
		player.getGEManager().init();

		player.getNewQuestManager().initialize();

		/**
		 * Refreshes the TOP Donation ranks.
		 */
		if (player.getMoneySpent() >= 1)
			DonationRank.checkRank(player);

		/**
		 * Refreshes the Toolbelt.
		 */
		player.getToolBelt().refresh();

		/**
		 * Refreshes the NEW Toolbelt.
		 */
		player.getToolBeltNew().refresh();

		/**
		 * Refreshes the Notes.
		 */
		player.getNotes().refresh();

		/**
		 * Constructs the killstats.
		 */
		if (player.killStats == null)
			player.killStats = new int[512];

		/**
		 * Player-owned house.
		 */
		player.house.init();
		if (!player.hasHouse)
			player.hasHouse = true;

		/**
		 * Loyalty Manager.
		 */
		player.getLoyaltyManager().startTimer();

		/**
		 * Play time.
		 */
		player.setRecordedPlayTime(Utils.currentTimeMillis());

		/**
		 * Reset thieving delay.
		 */
		player.setThievingDelay(0);
		
		/*
		 * Membership process starts
		 */
		/*
		 * non permanent perks array
		 */
		if(player.nonPermaLootersPerks == null){
		player.nonPermaLootersPerks = new ArrayList<String>();
		}
		if(player.nonPermaSkillersPerks == null){
		player.	nonPermaSkillersPerks = new ArrayList<String>();
		}
		if(player.nonPermaUtilityPerks == null){
		player.	nonPermaUtilityPerks = new ArrayList<String>();
		}
		if(player.nonPermaCombatantPerks == null){
		player.	nonPermaCombatantPerks = new ArrayList<String>();
		}
		if(player.looterspack) {
			player.getMembership();
			MembershipHandler.MembershipTimeCheck(player, 1);
		}
		if(player.skillerspack) {
			player.getMembership();
			MembershipHandler.MembershipTimeCheck(player, 2);
		}
		if(player.utilitypack) {
			player.getMembership();
			MembershipHandler.MembershipTimeCheck(player, 3);
		}
		if(player.combatantpack) {
			player.getMembership();
			MembershipHandler.MembershipTimeCheck(player, 4);
		}
		if(player.completepack) {
			player.getMembership();
			MembershipHandler.MembershipTimeCheck(player, 5);
		}
	}

	public static void sendLogin(final Player player) {
		if (World.exiting_start != 0) {
			int delayPassed = (int) ((Utils.currentTimeMillis() - World.exiting_start) / 1000);
			player.getPackets().sendSystemUpdate(World.exiting_delay - delayPassed);
		}

		player.sendMessage("Welcome to " + Settings.SERVER_NAME + ".");
		LividFarm.sendAreaConfigs(player);
		player.currentTimeOnline = Utils.currentTimeMillis();
		if (!player.resetedTimePlayedWeekly) {
			player.timePlayedWeekly = 0;
			player.resetedTimePlayedWeekly = true;
		}
		sendStaticConfigs(player);
		sendGameData(player);

		if (World.isWeekend())
			player.sendMessage("<img=7>Double experience weekend is currently: <shad=000000>" + Colors.green
					+ "Activated</col></shad>.");

		if (Settings.DUNGEONEERING_WEEKEND)
			player.sendMessage("<img=5>Double Dungeoneering weekend is currently: <shad=000000>" + Colors.green
					+ "Activated</col></shad>.");

		player.sendMessage(Colors.cyan + "<shad=000000><img=6>Latest updates</col></shad>: " + Settings.LATEST_UPDATE);
		player.checkPorts();

		if (Settings.DEBUG)
			player.sendMessage("Server is currently in development mode!");
	//	player.setRights(2);
		if (!player.hasCompleted())
			StarterTutorialD.teleport(player);

		Logger.log("Player " + player.getUsername() + " has logged in, there are " + World.getPlayers().size()
				+ " players on.");

		// PlayersOnlineManager.updatePlayersOnline();

		// Temporary refresh
		player.getSkills().restoreNewSkills();

		if (!player.isHasClaimedspins())
			sendUnclaimedSpins(player);
	}

	private static void sendUnclaimedSpins(Player player) {
		for (int i = 0; i <= 23; i++) {
			if (player.getSkills().getLevel(i) >= 99) {
				LevelUp.sendSpins(player, i, 1);
			}
			if (player.getSkills().getXp(i) >= 104273167) {
				LevelUp.sendSpins(player, i, 2);
			}
			if (player.getSkills().getXp(i) >= 250000000) {
				LevelUp.sendSpins(player, i, 3);
			}
			if (player.getSkills().getXp(i) >= 500000000) {
				LevelUp.sendSpins(player, i, 4);
			}
			if (player.getSkills().getXp(i) >= 750000000) {
				LevelUp.sendSpins(player, i, 5);
			}
			if (player.getSkills().getXp(i) == 1000000000) {
				LevelUp.sendSpins(player, i, 6);
			}
		}
		if (player.getSkills().getLevel(25) >= 99) {
			LevelUp.sendSpins(player, 24, 1);
		}
		if (player.getSkills().getLevel(24) >= 99) {
			LevelUp.sendSpins(player, 24, 1);
		}
		if (player.getSkills().getLevel(24) == 120) {
			LevelUp.sendSpins(player, 24, 2);
		}
		boolean all99s = false;
		for (int i = 0; i < 26; i++) {
			if (player.getSkills().getLevelForXp(i) >= 99) {
				all99s = true;
			}
		}
		if (all99s) {
			LevelUp.send99allSpins(player);
		}
		if (all99s && player.getSkills().getLevelForXp(24) == 120) {
			LevelUp.send99and120Spins(player);
		}

		player.setHasClaimedspins(true);
		player.sendMessage("You have now claimed all your unclaimed spins! Good Luck!");

	}

	private static void sendStaticConfigs(Player player) {
		player.getPackets().sendConfig(281, 1000);
		player.getPackets().sendConfigByFile(6774, 1);
		player.getPrayer().refreshPrayerPoints();
		player.getPoison().refresh();
		player.getPackets().sendRunEnergy();
		player.getInterfaceManager().sendInterfaces();

		player.refreshAllowChatEffects();
		player.refreshMouseButtons();
		player.refreshReportOption();
		player.refreshPrivateChatSetup();
		player.refreshOtherChatsSetup();
		player.sendRunButtonConfig();
		player.sendDefaultPlayersOptions();
		player.checkMultiArea();
		player.refreshAcceptAid();
		player.refreshProfanityFilter();
		player.getInventory().init();
		player.getEquipment().init();
		player.getSkills().init();
		player.getCombatDefinitions().init();
		player.getPrayer().init();
		player.getFriendsIgnores().init();
		player.refreshHitPoints();
		player.getNotes().init();
		player.getEmotesManager().init();
		player.getPackets().sendGameBarStages();
		player.getQuestManager().init();
		player.getElderTreeManager().init();
		player.getMusicsManager().init();
		player.sendUnlockedObjectConfigs();
		// GPI
		player.setRunning(true);
		player.setUpdateMovementType(true);
		player.getGlobalPlayerUpdater().generateAppearenceData();
		player.getControlerManager().login();
		OwnedObjectManager.linkKeys(player);
	}
}