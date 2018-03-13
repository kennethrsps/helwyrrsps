package com.rs;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.alex.store.Index;
import com.rs.cache.Cache;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.ItemsEquipIds;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cores.CoresManager;
import com.rs.game.MapBuilder;
import com.rs.game.World;
import com.rs.game.map.bossInstance.BossInstanceHandler;
import com.rs.game.npc.combat.CombatScriptsHandler;
import com.rs.game.player.LendingManager;
import com.rs.game.player.Player;
import com.rs.game.player.actions.runecrafting.SiphonActionNodes;
import com.rs.game.player.content.CosmeticsHandler;
import com.rs.game.player.content.FishingSpotsHandler;
import com.rs.game.player.content.FriendChatsManager;
import com.rs.game.player.content.InstancedPVP;
import com.rs.game.player.content.LividFarm;
import com.rs.game.player.content.Lottery;
import com.rs.game.player.content.ReferralHandler;
import com.rs.game.player.content.WeeklyTopRanking;
import com.rs.game.player.content.WellOfGoodWill;
import com.rs.game.player.content.clans.ClansManager;
import com.rs.game.player.content.grandExchange.GrandExchange;
import com.rs.game.player.controllers.ControllerHandler;
import com.rs.game.player.cutscenes.CutscenesHandler;
import com.rs.game.player.dialogue.DialogueHandler;
import com.rs.game.player.newquests.NewQuestManager;
import com.rs.game.worldlist.WorldList;
import com.rs.network.ServerChannelHandler;
import com.rs.tools.NPCDropPacker;
import com.rs.utils.AutoBackup;
import com.rs.utils.DTRank;
import com.rs.utils.DisplayNames;
import com.rs.utils.DonationRank;
import com.rs.utils.IPBanL;
import com.rs.utils.IPMute;
import com.rs.utils.ItemBonuses;
import com.rs.utils.ItemExamines;
import com.rs.utils.ItemSpawns;
import com.rs.utils.ItemWeights;
import com.rs.utils.Logger;
import com.rs.utils.MACBan;
import com.rs.utils.MusicHints;
import com.rs.utils.NPCBonuses;
import com.rs.utils.NPCCombatDefinitionsL;
import com.rs.utils.NPCDrops;
import com.rs.utils.NPCExamines;
import com.rs.utils.NPCSpawns;
import com.rs.utils.ObjectSpawns;
import com.rs.utils.PkRank;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.ShopsHandler;
import com.rs.utils.StarterMap;
import com.rs.utils.VoteHiscores;
import com.rs.utils.npcNames;
import com.rs.utils.huffman.Huffman;

/**
 * The Main class for those who cannot read.
 * 
 * @author Zeus
 */
public final class ServerLauncher {

	/**
	 * The Main server launch method.
	 * 
	 * @param args
	 *            The launch arguments.
	 * @throws Exception
	 *             The exception to throw.
	 */
	public static void main(String[] args) throws Exception {
		// if (args.length < 4) {
		// Logger.log("USE: guimode(boolean) debug(boolean)
		// economy_mode(boolean) port(integer) worldid(integer)");
		// return;
		// }
		/** Run Arguments **/
		Settings.GUI_MODE = false;// Boolean.parseBoolean(args[0]);
		Settings.DEBUG = false;// Boolean.parseBoolean(args[1]);
		Settings.ECONOMY_MODE = true;// Boolean.parseBoolean(args[2]);
		Settings.SERVER_PORT = 43594;// Integer.parseInt(args[3]);
		Settings.WORLD_ID = 1;// Integer.parseInt(args[4]);

		/** Start server **/
		init();
	}

	private static void init() throws IOException {

		/* Initialize characters auto-backup */
		if (!Settings.DEBUG) {
		//	AutoBackup.init();
		//	Settings.SQL_ENABLED = true;
		}

		/* Initialize cache */
		Cache.init();
		ItemsEquipIds.init();
		Huffman.init();

		/* Initialize player data */
		DisplayNames.init();
		IPBanL.init();
		MACBan.init();
		IPMute.init();
		PkRank.init();
		DTRank.init();
		ItemSpawns.init();
		DonationRank.init();
		VoteHiscores.init();
		LividFarm.init();
		WeeklyTopRanking.init();
		//new Motivote<Vote>(new RewardHandler(), "http://www.Helwyr.com/vote/", "3b2a5c7f").start();

		/* Initialize map data */
		ObjectSpawns.init();
		CosmeticsHandler.init();
		npcNames.init();
		Lottery.init();
		/* Initialize non-player-character data */
		NPCSpawns.init();
		NPCCombatDefinitionsL.init();
		NPCBonuses.init();
		NPCDrops.init();
		NPCExamines.init();
		CombatScriptsHandler.init();
		FishingSpotsHandler.init();

		/* Loads the G.E. */
		GrandExchange.init();

		/* Initialize item data */
		ItemExamines.init();
		ItemWeights.init();
		ItemBonuses.init();

		/* Initializes music hints */
		MusicHints.init();

		/* Initializes shops data */
		ShopsHandler.init();

		/* Initializes lobby world data */
		WorldList.init();

		/* Initializes clans manager */
		ClansManager.init();

		/* Initializes In-game Scripts */
		DialogueHandler.init();
		ControllerHandler.init();
		CutscenesHandler.init();
		FriendChatsManager.init();

		/* Initializes Login Server */
		/**
		 * Logger.log("Preparing Login Server Connection...");
		 * LoginServerCommunication loginServer = new
		 * LoginServerCommunication(Settings.SERVER_IP, 43596);
		 * loginServer.run();
		 */
		ReferralHandler.init();
		/* Initializes World thread */
		CoresManager.init();
		World.init();
		WellOfGoodWill.load();

		/* Initializes Region Builder */
		MapBuilder.init();

		/* Initializes Lending Manager */
		LendingManager.init();

		NewQuestManager.Quests.init();
		/* Initializes Vote queue reward task */
		addAccountsSavingTask();

		if (!Settings.DEBUG) {
			addCleanMemoryTask();
			addRecalculatePricesTask();
		}

		/* Initializes player starter kits */
		StarterMap.getSingleton().init();

		SiphonActionNodes.init();
		InstancedPVP.init();
		BossInstanceHandler.init();

		ObjectSpawns.addCustomSpawns();

		try {
			/* Initializes Networking */
			Logger.log("Preparing Netty...");
			ServerChannelHandler.init();

			/** Server successfully launched :] **/
			Logger.log("Server launched in "
					+ (Settings.ECONOMY_MODE ? Settings.DEBUG ? "DEVELOPMENT" : "ECONOMY" : "PVP") + " mode; "
					+ "data: " + Settings.REVISION + "/" + Settings.SUB_REVISION + "/" + Settings.SERVER_PORT + ".");
		} catch (Exception e) {
			System.err.println("Helwyr failed to launch!");
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * The scheduled data saving task.
	 */
	private static void addAccountsSavingTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					saveFiles();
					Logger.log("All files successfully saved; players: " + World.getPlayersOnline() + ".");
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 1, 1, TimeUnit.MINUTES);
	}

	private static void addRecalculatePricesTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					GrandExchange.recalcPrices();
				} catch (Throwable e) {
					Logger.handle(e);
				}

			}
		}, 1, 3, TimeUnit.HOURS);
	}

	/**
	 * The scheduled Garbage collector task.
	 */
	private static void addCleanMemoryTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					for (Index index : Cache.STORE.getIndexes())
						index.resetCachedFiles();
					CoresManager.fastExecutor.purge();
					System.gc();
					if (!Settings.SQL_ENABLED) {
						Settings.SQL_ENABLED = true;
						Logger.log("Executing clean memory task; turning SQL back on.");
					}
				} catch (Throwable e) {
					Logger.log("Launcher", "Failed executing clean memory task...");
					cleanMemory(Runtime.getRuntime().freeMemory() < Settings.MINIMUM_RAM_ALLOCATED);
				}
			}
		}, 60, 60, TimeUnit.MINUTES);
	}

	/**
	 * Cleans out the useless shit.
	 * 
	 * @param force
	 *            If force clean.
	 */
	public static void cleanMemory(boolean force) {
		if (force) {
			ItemDefinitions.clearItemsDefinitions();
			NPCDefinitions.clearNPCDefinitions();
			ObjectDefinitions.clearObjectDefinitions();
		}
		for (Index index : Cache.STORE.getIndexes())
			index.resetCachedFiles();
		CoresManager.fastExecutor.purge();
		System.gc();
	}

	/**
	 * Shuts down networking.
	 */
	public static void closeServices() {
		ServerChannelHandler.shutdown();
		CoresManager.shutdown();
	}

	/**
	 * Saves all data.
	 * 
	 * @throws Exception
	 *             if Failed.
	 */
	private static void saveFiles() throws Exception {
		for (Player player : World.getPlayers()) {
			if (player == null || !player.isActive() || player.hasFinished())
				continue;
			SerializableFilesManager.savePlayer(player);
		}
		if (!Settings.DEBUG) {
			GrandExchange.save();
			ReferralHandler.save();
			IPBanL.save();
			IPMute.save();
			MACBan.save();
			DisplayNames.save();
			WellOfGoodWill.save();
			PkRank.save();
			DTRank.save();
			DonationRank.save();
			VoteHiscores.save();
			Lottery.save();
			WeeklyTopRanking.save();
		}
	}

	/**
	 * Restarts the server.
	 */
	public static void restartEmulator() {
		closeServices();
		System.gc();
		try {
			Runtime.getRuntime().exec("cmd /c start run.bat");
			System.exit(0);
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	/**
	 * Shuts down the server.
	 */
	public static void shutdown() {
		try {
			closeServices();
		} finally {
			System.exit(0);
		}
	}
}