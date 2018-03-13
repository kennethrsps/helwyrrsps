package com.rs.game.map.bossInstance;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.map.bossInstance.impl.CorporealBeastInstance;
import com.rs.game.map.bossInstance.impl.DagannothKingsInstance;
import com.rs.game.map.bossInstance.impl.GodWarsInstance;
import com.rs.game.map.bossInstance.impl.KalphiteQueenInstance;
import com.rs.game.map.bossInstance.impl.KingBlackDragonInstance;
import com.rs.game.player.Player;
import com.rs.utils.Logger;
import com.rs.utils.Utils;

public class BossInstanceHandler {

	// to make sure no issues
	public static final Object LOCK = new Object();

	public static enum Boss {
		King_Black_Dragon(KingBlackDragonInstance.class, 1250000, 200, false, true, new WorldTile(3051, 3520, 0),
				new WorldTile(2273, 4681, 0), null, "BossInstanceController",
				14), Kalphite_Queen(KalphiteQueenInstance.class, 1000000, 200, false, true,
						new WorldTile(3446, 9496, 0), new WorldTile(3508, 9494, 0), null,
						"KalphiteQueenInstanceController", 212), Dagannoth_Kings(DagannothKingsInstance.class, 1500000,
								200, false, true, new WorldTile(1912, 4367, 0), new WorldTile(2900, 4449, 0), null,
								"DagannothKingsInstanceController", 365), Corporeal_Beast(CorporealBeastInstance.class,
										3000000, 200, false, true, new WorldTile(2970, 4384, 2),
										new WorldTile(2974, 4384, 2), null, "CorporealBeastInstanceController",
										617), Bandos(GodWarsInstance.class, 2000000, 50, false, true,
												new WorldTile(2862, 5357, 0), new WorldTile(2863, 5357, 0), null,
												"GodWars", -1), Zamorak(GodWarsInstance.class, 2000000, 50, false, true,
														new WorldTile(2925, 5333, 0), new WorldTile(2925, 5332, 0),
														null, "GodWars", -1), Saradomin(GodWarsInstance.class, 2000000,
																50, false, true, new WorldTile(2923, 5257, 0),
																new WorldTile(2923, 5256, 0), null, "GodWars",
																-1), Armadyl(GodWarsInstance.class, 2000000, 50, false,
																		true, new WorldTile(2835, 5294, 0),
																		new WorldTile(2835, 5295, 0), null, "GodWars",
																		-1);

		private final Map<String, BossInstance> cachedInstances = Collections
				.synchronizedMap(new HashMap<String, BossInstance>());
		private Class<? extends BossInstance> instance;
		private int initialCost, maxPlayers, musicId;
		private boolean hasHM, publicVersion;
		private WorldTile insideTile, outsideTile, graveStoneTile;
		private String controllerName;

		private Boss(Class<? extends BossInstance> instance, int initialCost, int maxPlayers, boolean hasHM,
				boolean publicVersion, WorldTile outsideTile, WorldTile insideTile, WorldTile graveStoneTile,
				String controllerName, int musicId) {
			this.instance = instance;
			this.initialCost = initialCost;
			this.maxPlayers = maxPlayers;
			this.hasHM = hasHM;
			this.publicVersion = publicVersion;
			this.insideTile = insideTile;
			this.outsideTile = outsideTile;
			this.graveStoneTile = graveStoneTile;
			this.controllerName = controllerName;
			this.musicId = musicId;
		}

		public Map<String, BossInstance> getCachedInstances() {
			return cachedInstances;
		}

		public String getControllerName() {
			return controllerName;
		}

		public WorldTile getInsideTile() {
			return insideTile;
		}

		public WorldTile getOutsideTile() {
			return outsideTile;
		}

		public WorldTile getGraveStoneTile() {
			return graveStoneTile;
		}

		public boolean isHasHM() {
			return hasHM;
		}

		public int getMaxPlayers() {
			return maxPlayers;
		}

		public int getInitialCost() {
			return initialCost;
		}

		public int getMusicId() {
			return musicId;
		}

		public boolean hasPublicVersion() {
			return publicVersion;
		}

	}

	public static void enterInstance(Player player, Boss boss) {
		player.getDialogueManager().startDialogue("BossInstanceD", boss);
	}

	private static void createInstance(Player player, Boss boss, int maxPlayers, int minCombat, int spawnSpeed,
			int protection, boolean practiseMode, boolean hardMode) {
		createInstance(player,
				new InstanceSettings(boss, maxPlayers, minCombat, spawnSpeed, protection, practiseMode, hardMode));
	}

	public static void createInstance(Player player, InstanceSettings settings) {
		synchronized (LOCK) {

			try {
				String key = player == null ? "" : player.getUsername();
				BossInstance instance = findInstance(settings.getBoss(), key);
				if (instance == null) {
					if (player == null && !settings.getBoss().publicVersion) {
						if (Settings.DEBUG)
							Logger.log(BossInstanceHandler.class, "Not a public instance. Can't create it.");
						return;
					}
					instance = settings.getBoss().instance.getDeclaredConstructor(Player.class, InstanceSettings.class)
							.newInstance(player, settings);
					settings.getBoss().cachedInstances.put(key, instance);
				} else {// recreating the instance but not gonna replace
						// settings since already exists(instead, increase time)
					settings.setCreationTime(Utils.currentTimeMillis());
					joinInstance(player, settings.getBoss(), key, false); // enter
																			// the
																			// instance
																			// normally
				}
			} catch (Throwable e) {
				Logger.handle(e);
			}

		}
	}

	/*
	 * login means reloging in a public instance(u cant login into private, but
	 * lets keep this in case rs lets u in future)
	 */
	public static BossInstance joinInstance(Player player, Boss boss, String key, boolean login) {
		synchronized (LOCK) {

			BossInstance instance = findInstance(boss, key);
			if (instance == null) { // not username
				Player owner = World.getPlayerByDisplayName(key);
				if (owner != null) {
					key = owner.getUsername();
					instance = findInstance(boss, key);
				}
			}

			if (instance == null) {
				if (key.equals("")) { // supposed to be public instance
					player.getPackets().sendGameMessage("This boss has no public instance.");
					return null;
				}
				player.getPackets().sendGameMessage("That player is offline, or has privacy mode enabled.");
				return null;
			}
			// loading
			if (!instance.isInstanceReady())
				return null;
			if (!key.equals("") && !player.getUsername().equals(key)) {
				if (instance.getSettings().getMinCombat() > player.getSkills().getCombatLevelWithSummoning()) {
					player.getPackets().sendGameMessage("Your combat level is too low to enter this session.");
					return null;
				}
				if (instance.getSettings().getProtection() == BossInstance.FRIENDS_ONLY) {
					Player owner = World.getPlayer(key);
					if (owner == null || !owner.getFriendsIgnores().getFriends().contains(player.getDisplayName())) {
						player.getPackets().sendGameMessage("That player is offline, or has privacy mode enabled.");
						return null;
					}
				}
				if (instance.getSettings().getMaxPlayers() - 1 <= instance.getPlayersCount()) {
					player.getPackets().sendGameMessage("This instance is full.");
					return null;
				}
			}
			instance.enterInstance(player, login);
			return instance;
		}
	}

	public static BossInstance findInstance(Boss boss, String key) {
		synchronized (LOCK) {
			return boss.cachedInstances.get(key);
		}
	}

	public static final void init() {
		for (Boss boss : Boss.values()) {
			if (!boss.publicVersion || boss.ordinal() < Boss.Bandos.ordinal())
				continue;
			try {
				createInstance(null, boss, boss.maxPlayers, 1, BossInstance.STANDARD, BossInstance.FFA, false, false);
			} catch (Throwable e) {
				Logger.handle(e);
			}
		}
	}
}