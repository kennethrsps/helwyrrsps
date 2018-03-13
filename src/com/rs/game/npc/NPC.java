package com.rs.game.npc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.HeadIcon;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.map.bossInstance.BossInstance;
import com.rs.game.npc.combat.NPCCombat;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.player.Player;
import com.rs.game.player.SlayerManager;
import com.rs.game.player.actions.hunter.FlyingEntityHunter.FlyingEntities;
import com.rs.game.player.content.Combat;
import com.rs.game.player.content.Slayer.SlayerMaster;
//import com.rs.game.player.content.SlayerTask;
import com.rs.game.player.content.contracts.ContractHandler;
import com.rs.game.player.content.grandExchange.GrandExchange;
import com.rs.game.player.content.items.BoneCrusher;
import com.rs.game.player.content.items.CharmingImp;
import com.rs.game.player.content.items.CoinAccumulator;
import com.rs.game.player.content.items.Defenders;
import com.rs.game.player.content.items.Herbicide;
import com.rs.game.player.content.items.RingOfWealth;
import com.rs.game.player.controllers.DTController;
import com.rs.game.player.controllers.Dungeoneering;
import com.rs.game.player.controllers.GodWars;
import com.rs.game.player.controllers.WarriorsGuild;
import com.rs.game.player.controllers.Wilderness;
import com.rs.game.route.RouteFinder;
import com.rs.game.route.strategy.FixedTileStrategy;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Logger;
import com.rs.utils.MapAreas;
import com.rs.utils.NPCBonuses;
import com.rs.utils.NPCCombatDefinitionsL;
import com.rs.utils.NPCDrops;
import com.rs.utils.Utils;
import com.rs.utils.npcNames;

import mysql.impl.NewsManager;

/**
 * A class holding all NPC data.
 * 
 * @author Zeus
 */
public class NPC extends Entity implements Serializable {

	/**
	 * The generated serial UID for Serializable.
	 */
	private static final long serialVersionUID = -4794678936277614443L;

	/**
	 * Integers representing NPC movement masks.
	 */
	public static int NORMAL_WALK = 0x2, WATER_WALK = 0x4, FLY_WALK = 0x8;

	/**
	 * NPC Configurations.
	 */
	protected int id;
	private WorldTile respawnTile;
	private int mapAreaNameHash;
	private boolean canBeAttackFromOutOfArea;
	private boolean randomwalk;
	private int[] bonuses; // NPC bonuses go up to 9
	private boolean spawned;
	private transient NPCCombat combat;

	public WorldTile forceWalk;
	private long lastAttackedByTarget;
	private boolean cantInteract;
	private int capDamage;
	private int lureDelay;
	private int walkType;
	private boolean cantFollowUnderCombat;
	private boolean forceAgressive;
	private int forceTargetDistance;
	private boolean forceFollowClose;
	private boolean forceMultiAttacked;

	private boolean noDistanceCheck;
	// npc masks
	private transient Transformation nextTransformation;
	private transient boolean changedName;
	private transient boolean changedCombatLevel;
	private transient boolean refreshHeadIcon;
	// name changing masks
	private String name;
	private int combatLevel;

	private transient boolean locked;

	/**
	 * Initializes the NPC.
	 * 
	 * @param id
	 *            The NPC ID.
	 * @param tile
	 *            The WorldTIle.
	 * @param mapAreaNameHash
	 *            The Region area name.
	 * @param canBeAttackFromOutOfArea
	 *            if Can be attacked out of Region.
	 */
	public NPC(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		this(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, false);
	}

	/*
	 * creates and adds npc
	 */
	public NPC(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(tile);
		this.id = id;
		this.respawnTile = new WorldTile(tile);
		this.mapAreaNameHash = mapAreaNameHash;
		this.canBeAttackFromOutOfArea = canBeAttackFromOutOfArea;
		this.spawned = spawned;
		combatLevel = -1;
		setHitpoints(getMaxHitpoints());
		setDirection(getRespawnDirection());
		setRandomWalk(getDefinitions().movementCapabilities);
		setBonuses();
		combat = new NPCCombat(this);
		capDamage = -1;
		lureDelay = 12000;
		initEntity();
		World.addNPC(this);
		World.updateEntityRegion(this);
		loadMapRegions();
		checkMultiArea();
	}

	public boolean canBeAttackedByAutoRetaliate() {
		return Utils.currentTimeMillis() - lastAttackedByTarget > lureDelay;
	}

	public boolean canBeAttackFromOutOfArea() {
		return canBeAttackFromOutOfArea;
	}

	/**
	 * Checks if the NPC should force aggression towards the entities around it.
	 * 
	 * @return if should be agressive.
	 */
	public boolean checkAgressivity() {
		if (!forceAgressive) {
			NPCCombatDefinitions defs = getCombatDefinitions();
			if (defs.getAgressivenessType() == NPCCombatDefinitions.PASSIVE)
				return false;
		}
		ArrayList<Entity> possibleTarget = getPossibleTargets();
		if (!possibleTarget.isEmpty()) {
			Entity target = possibleTarget.get(Utils.random(possibleTarget.size()));
			if (target instanceof Player) {
				Player player = (Player) target;
				int aggressionLevel = (getCombatLevel() * 2) + 1;
				if (player.getControlerManager().getControler() != null || forceAgressive) {
					setTarget(target);
					target.setAttackedBy(target);
					target.setFindTargetDelay(Utils.currentTimeMillis() + 5000);
					return true;
				}
				if (player.getSkills().getCombatLevel() <= aggressionLevel) {
					if ((System.currentTimeMillis() - player.toleranceTimer) < Utils.random(600000, 700000)) {
						setTarget(target);
						target.setAttackedBy(target);
						target.setFindTargetDelay(Utils.currentTimeMillis() + 5000);
						return true;
					}
					return false;
				}
				return false;
			}
			return false;
		}
		return false;
	}

	public void checkGodwarsKillcount() {
		Player killer = getMostDamageReceivedSourcePlayer();
		if (killer == null)
			return;
		if (killer.getControlerManager().getControler() instanceof GodWars)
			((GodWars) killer.getControlerManager().getControler()).handleKC(this);
	}

	public boolean containsItem(int id) {
		Item item = new Item(id);
		return containsItem(item);
	}

	public boolean containsItem(Item item) {
		Player killer = getMostDamageReceivedSourcePlayer();
		return killer.getInventory().getItems().contains(new Item(item.getId(), 1))
				|| killer.getEquipment().getItems().contains(new Item(item.getId(), 1));
	}

	public void deserialize() {
		if (combat == null)
			combat = new NPCCombat(this);
		spawn();
	}

	/**
	 * Handles increasing NPC kill statistics.
	 * 
	 * @param killer
	 *            The killer.
	 * @param name
	 *            The NPC name.
	 * @param id
	 *            The statistic ID.
	 */
	protected void increaseKillStatistics(Player killer, String name) {
		if (killer.increaseKillStatistics(name, false) != -1)
			killer.sendMessage("You've killed a total of " + Colors.red + killer.increaseKillStatistics(name, true) + ""
					+ "</col> x " + Colors.red + name + "'s</col>.", true);
	}

	protected void handlePetDrop(Player killer, String name) {
		Item pet = null;
		String image = null;
		if (name.toLowerCase().equalsIgnoreCase("general graardor")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 3750 : 5000) == 1) {
				pet = new Item(33806);
				image = "bandos.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("k'ril tsutsaroth")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 3750 : 5000) == 1) {
				pet = new Item(33805);
				image = "zammy.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("commander zilyana")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 3750 : 5000) == 1) {
				pet = new Item(33807);
				image = "sara.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("kree'arra")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 3750 : 5000) == 1) {
				pet = new Item(33804);
				image = "arma.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("dagannoth prime")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1875 : 2500) == 1) {
				pet = new Item(33826);
				image = "dag.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("dagannoth supreme")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1875 : 2500) == 1) {
				pet = new Item(33828);
				image = "dag.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("dagannoth rex")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1875 : 2500) == 1) {
				pet = new Item(33827);
				image = "dag.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("chaos elemental")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1500 : 2000) == 1) {
				pet = new Item(33811);
				image = "ellie.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("kalphite queen")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1500 : 2000) == 1) {
				if (Utils.random(1) == 0) {
					pet = new Item(33816);
					image = "kq1.png";
				} else {
					pet = new Item(33817);
					image = "kq2.png";
				}
			}
		}
		if (name.equalsIgnoreCase("araxxor")) {
			int petChance = Utils.random(killer.getPerkManager().petChanter ? 4900 : 6250);
			{
				image = "araxx.png";
				switch (petChance) {
				case 1:
					pet = new Item(33748);
					break;
				case 2012:
					pet = new Item(33749);
					break;
				case 63:
					pet = new Item(33750);
					break;
				case 4874:
					pet = new Item(33751);
					break;
				case 0:
					pet = new Item(33752);
					break;
				case 3728:
					pet = new Item(33753);
					break;
				default:
					break;
				}
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("corporeal beast")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1875 : 2500) == 1) {
				pet = new Item(33812);
				image = "corp.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("king black dragon")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1875 : 2500) == 1) {
				pet = new Item(33818);
				image = "kbd.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("nex")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 750 : 1000) == 1) {
				pet = new Item(33808);
				image = "nex.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("queen black dragon")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 750 : 1000) == 1) {
				pet = new Item(33825);
				image = "qbd.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("kalphite king")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1875 : 2500) == 1) {
				pet = new Item(33815);
				image = "kalking.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("vorago")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 975 : 1250) == 1) {
				pet = new Item(28630);
				image = "vitalis.png";
			}
			if (Utils.random(killer.getPerkManager().petChanter ? 3750 : 5000) == 1) {
				pet = new Item(33717);
				image = "bombii.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("legio primus")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1875 : 2500) == 1) {
				pet = new Item(33819);
				image = "legio.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("legio secundus")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1875 : 2500) == 1) {
				pet = new Item(33820);
				image = "legio.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("legio tertius")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1875 : 2500) == 1) {
				pet = new Item(33821);
				image = "legio.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("legio quartus")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1875 : 2500) == 1) {
				pet = new Item(33822);
				image = "legio.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("legio quintus")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1875 : 2500) == 1) {
				pet = new Item(33823);
				image = "legio.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("legio sextus")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1875 : 2500) == 1) {
				pet = new Item(33824);
				image = "legio.png";
			}
		}
		if (pet != null && !killer.hasItem(pet)) {
			World.sendWorldMessage(Colors.orange + "<shad=000000><img=6>News: " + killer.getDisplayName()
					+ " received a " + pet.getName() + " pet drop.", false);

			killer.addItem(pet);

			new Thread(new NewsManager(killer, "<b><img src=\"./bin/images/news/" + image + "\" width=15> "
					+ killer.getDisplayName() + " received a " + pet.getName() + " pet drop.")).start();
		}
	}

	private static Item[] easterDropsCommon = { new Item(12158, Utils.random(1, 4)), // gold
			new Item(12159, Utils.random(1, 3)), // green
			new Item(12160, Utils.random(1, 2)), // crimson
			new Item(12163, 1), // blue
			new Item(532, 1), // bigbones
			// bronze set
			new Item(1155, 1), new Item(1117, 1), new Item(1075, 1), new Item(1189, 1)

	};
	private static Item[] easterDropsNonCommon = {
			// rune set
			new Item(1163, 1), new Item(1127, 1), new Item(1093, 1), new Item(1201, 1),
			// dbones
			new Item(536, 1),
			// tooth half
			new Item(985, 1),
			// loop half
			new Item(987, 1),
			// fdbones
			new Item(18830, 1), };
	private static Item[] easterDropsRare = {
			// easter eggs
			new Item(7928, 1), new Item(7929, 1), new Item(7930, 1), new Item(7931, 1), new Item(7932, 1),
			new Item(7933, 1),
			// dragon set
			new Item(11335, 1), new Item(14479, 1), new Item(4087, 1), new Item(1187, 1), };

	private void dropnormalbones(Player player) {
		World.addGroundItem(new Item(526, 1), new WorldTile(this), player, true, 60);// normal
																						// bones
	}

	/**
	 * Sends the Drop.
	 */
	public void drop() {
		try {
			Drop[] drops = NPCDrops.getDrops(id);
			Player killer = getMostDamageReceivedSourcePlayer();
			if (killer == null)
				return;
			if (killer.getControlerManager().getControler() instanceof DTController)
				return;
			Player otherPlayer = killer.getSlayerManager().getSocialPlayer();
			SlayerManager manager = killer.getSlayerManager();
			if (manager.isValidTask(npcNames.getNPCName(getId())))
				manager.checkCompletedTask(getDamageReceived(killer),
						otherPlayer != null ? getDamageReceived(otherPlayer) : 0);
			// easter
			int rng = Utils.random(100);
			if (id == 1320) {
				dropnormalbones(killer);
				if (rng >= 0 && rng <= 70) {
					World.addGroundItem(easterDropsCommon[Utils.random(easterDropsCommon.length)], new WorldTile(this),
							killer, true, 60);
				} else if (rng > 70 && rng <= 95) {
					World.addGroundItem(easterDropsNonCommon[Utils.random(easterDropsNonCommon.length)],
							new WorldTile(this), killer, true, 60);
				} else {
					Item drop = easterDropsRare[Utils.random(easterDropsRare.length)];
					World.addGroundItem(drop, new WorldTile(this), killer, true, 60);
					if (drop.getName().contains("Easter")) {
						World.sendWorldMessage(Colors.orange + "<shad=000000><img=6>News: " + killer.getDisplayName()
								+ " received " + drop.getName() + " from " + getName() + ".", false);
					}
				}
			}
			if (drops == null || getMaxHitpoints() == 1)
				return;
			if ((bossInstance != null && (bossInstance.isFinished() || bossInstance.getSettings().isPractiseMode())))
				return;

			Dungeoneering.handleDrop(killer, this);
			String name = getDefinitions().name.toLowerCase();
			increaseKillStatistics(killer, name);
			handlePetDrop(killer, name);
			if (name.contains("kalphite king") || name.contains("araxx") || name.contains("vorago")
					|| name.contains("graardor") || name.contains("zilyana") || name.contains("kree")
					|| name.contains("tsutsaroth") || name.contains("king black dragon")
					|| name.contains("queen black dragon") || name.contains("chaos elemental")
					|| name.contains("corporeal beast") || name.contains("nex") || name.contains("kalphite queen")
					|| name.contains("dagannoth ")) {
				/*
				 * if(Utils.random(1, 250) == 1 ||
				 * killer.getUsername().contains("Zeus")) { sendDrop(killer, new
				 * Drop(33611, 1, 1, 1, false)); World.sendLootbeam(killer,
				 * this); killer.getXmas().
				 * announceDrop(" has received a big Christmas present from "
				 * +getDefinitions().name+"!"); }
				 */
			}
			if (name.contains("kalphite king")) {
				if (Defenders.getCurrentTier(killer, 1) && Utils.random(74) == 0) {
					sendDrop(killer, new Drop(36163, 1, 1));
					World.sendWorldMessage(Colors.cyan + "<shad=000000><img=6>News: " + killer.getDisplayName()
							+ " received a perfect chitin from Kalphite King!", false);
					new Thread(new NewsManager(killer, "<b><img src=\"./bin/images/news/drop.png\" height=14> "
							+ killer.getDisplayName() + " received a perfect chitin from Kalphite King.")).start();
					World.sendLootbeam(killer, this);
				}
			}
			if (name.contains("nex")) {
				if (Defenders.getCurrentTier(killer, 0) && Utils.random(59) == 0) {
					sendDrop(killer, new Drop(36159, 1, 1));
					World.sendWorldMessage(Colors.cyan + "<shad=000000><img=6>News: " + killer.getDisplayName()
							+ " received an ancient emblem from Nex!", false);
					new Thread(new NewsManager(killer, "<b><img src=\"./bin/images/news/drop.png\" height=14> "
							+ killer.getDisplayName() + " received an ancient emblem from Nex!")).start();
					World.sendLootbeam(killer, this);
				}
			}
			if (name.contains("Commander Zilyana") && Utils.random(500) == 0) {
				World.addGroundItem(new Item(21476), new WorldTile(this), killer, true, 60);// vanguard
																							// boots
			}
			if (name.contains("revenant")) {
				int random = Utils.random(475 - getCombatLevel());
				if (random <= (1 + Settings.getDropQuantityRate(killer) + (RingOfWealth.checkRow(killer) ? 1 : 0))) {
					int pvpItem = PVP_ITEMS[Utils.random(PVP_ITEMS.length - 1)];
					World.updateGroundItem(
							new Item(pvpItem,
									pvpItem == 13882 ? Utils.random(30) : pvpItem == 13879 ? Utils.random(30) : 1),
							new WorldTile(this), killer, 60, 0, true);
					World.sendLootbeam(killer, this);
				}
				return;
			}
			if (getCombatLevel() >= 90 && Utils.random(750) == 0)
				World.addGroundItem(new Item(18778), new WorldTile(this), killer, true, 60);
			int level = Combat.getSlayerLevelForNPC(getId());
			if (level >= 78 && Utils.random(250) == 0) {
				World.updateGroundItem(new Item(29863), new WorldTile(this), killer, 60, 0, true);
				World.sendLootbeam(killer, this);
			}
			if (isCyclops(getDefinitions().name) && Utils.random(500) > 450) {
				if (killer.getControlerManager().getControler() instanceof WarriorsGuild) {
					World.updateGroundItem(new Item(whatDefender()), new WorldTile(this), killer, 60, 0, true);
					World.sendLootbeam(killer, this);
				}
			}
			if (isClueScrollNPC(getDefinitions().name) && Utils.random(300) <= 1) {
				if (!killer.getTreasureTrails().hasClueScrollItem()) {
					killer.getTreasureTrails().resetCurrentClue();
					int itemId = getCombatLevel() < 50 ? 2677
							: getCombatLevel() < 150 ? 2801 : getCombatLevel() < 250 ? 2722 : 19043;
					World.updateGroundItem(new Item(itemId), new WorldTile(this), killer, 60, 0, true);
					World.sendLootbeam(killer, this);
				}
			}
			if (killer.isHCIronMan() && Utils.random(50) <= 1) {
				if (name.contains("crab") || name.contains("crawling hand") || name.contains("banshee")
						|| name.contains("slug"))
					World.updateGroundItem(new Item(10498), new WorldTile(this), killer, 60, 0, true);
			}
			if (killer.getEquipment().getRingId() == 31869) {
				if (Utils.random(100) >= 50) {
					int toRestore = getMaxHitpoints() / 150;
					int special = killer.getCombatDefinitions().getSpecialAttackPercentage();
					if ((toRestore > 0 || toRestore <= 5) && (special + toRestore <= 100))
						killer.getCombatDefinitions().setSpecialAttackPercentage(special + toRestore);
				}
			}

			Drop[] possibleDrops = new Drop[drops.length];
			int possibleDropsCount = 0;
			for (Drop drop : drops) {
				if (killer.getTreasureTrails().isScroll(drop.getItemId())) {
					if (killer.getTreasureTrails().hasClueScrollItem())
						continue;
				}
				if (drop.getRate() == 100)
					sendDrop(killer, drop);
				else {
					if ((Utils.getRandomDouble(99) + 1) <= (drop.getRate() + Settings.getDropQuantityRate(killer)))
						possibleDrops[possibleDropsCount++] = drop;
				}
			}
			if (possibleDropsCount > 0)
				sendDrop(killer, possibleDrops[Utils.getRandom(possibleDropsCount - 1)]);
			// SlayerTask.onKill(killer, this);
			ContractHandler.checkContract(killer, id, this);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
	}

	@Override
	public void finish() {
		if (hasFinished())
			return;
		setFinished(true);
		World.updateEntityRegion(this);
		World.removeNPC(this);
	}

	public void forceWalkRespawnTile() {
		setForceWalk(respawnTile);
	}

	public int[] getBonuses() {
		return bonuses;
	}

	public int getCapDamage() {
		return capDamage;
	}

	public NPCCombat getCombat() {
		return combat;
	}

	public NPCCombatDefinitions getCombatDefinitions() {
		return NPCCombatDefinitionsL.getNPCCombatDefinitions(id);
	}

	public int getCombatLevel() {
		return combatLevel >= 0 ? combatLevel : getDefinitions().combatLevel;
	}

	public int getCustomCombatLevel() {
		return combatLevel;
	}

	public Item getDefender() {
		int id = 8844;
		if (containsItem(8850) || containsItem(20072)) {
			id = 20072;
		} else if (containsItem(8849) || containsItem(8850)) {
			id = 8850;
		} else if (containsItem(8848)) {
			id = 8849;
		} else if (containsItem(8847)) {
			id = 8848;
		} else if (containsItem(8846)) {
			id = 8847;
		} else if (containsItem(8845)) {
			id = 8846;
		} else if (containsItem(8844)) {
			id = 8845;
		} else {
			id = 8844;
		}
		return new Item(id);
	}

	public NPCDefinitions getDefinitions() {
		return NPCDefinitions.getNPCDefinitions(id);
	}

	public int getForceTargetDistance() {
		return forceTargetDistance;
	}

	public int getId() {
		return id;
	}

	public int getLureDelay() {
		return lureDelay;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0;
	}

	public int getMapAreaNameHash() {
		return mapAreaNameHash;
	}

	public int getMaxHit() {
		return getCombatDefinitions().getMaxHit();
	}

	public int getMaxHit(int attackStyle) {
		return getCombatDefinitions().getMaxHit();
	}

	@Override
	public int getMaxHitpoints() {
		return getCombatDefinitions().getHitpoints();
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0;
	}

	public WorldTile getMiddleWorldTile() {
		int size = getSize();
		return new WorldTile(getCoordFaceX(size), getCoordFaceY(size), getPlane());
	}

	public String getName() {
		return name != null ? name : getDefinitions().name;
	}

	public Transformation getNextTransformation() {
		return nextTransformation;
	}

	public ArrayList<Entity> getPossibleTargets(boolean checkNPCs, boolean checkPlayers) {
		int size = getSize();
		int agroRatio = 1;
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>();
		for (int regionId : getMapRegionsIds()) {
			if (checkPlayers) {
				List<Integer> playerIndexes = World.getRegion(regionId).getPlayerIndexes();
				if (playerIndexes != null) {
					for (int playerIndex : playerIndexes) {
						Player player = World.getPlayers().get(playerIndex);
						if (player == null || player.isDead() || player.hasFinished() || !player.isRunning()
								|| player.getGlobalPlayerUpdater().isHidden()
								|| !Utils.isOnRange(getX(), getY(), size, player.getX(), player.getY(),
										player.getSize(), forceTargetDistance > 0 ? forceTargetDistance : agroRatio)
								|| (!forceMultiAttacked && (!isAtMultiArea() || !player.isAtMultiArea())
										&& (player.getAttackedBy() != this
												&& (player.getAttackedByDelay() > Utils.currentTimeMillis()
														|| player.getFindTargetDelay() > Utils.currentTimeMillis())))
								|| !clipedProjectile(player, false) || (!forceAgressive && !Wilderness.isAtWild(this)
										&& player.getSkills().getCombatLevelWithSummoning() >= getCombatLevel() * 2))
							continue;
						possibleTarget.add(player);
					}
				}
			}
			if (checkNPCs) {
				List<Integer> npcsIndexes = World.getRegion(regionId).getNPCsIndexes();
				if (npcsIndexes != null) {
					for (int npcIndex : npcsIndexes) {
						NPC npc = World.getNPCs().get(npcIndex);
						if (npc == null || npc == this || npc.isDead() || npc.hasFinished()
								|| !Utils.isOnRange(getX(), getY(), size, npc.getX(), npc.getY(), npc.getSize(),
										forceTargetDistance > 0 ? forceTargetDistance : agroRatio)
								|| !npc.getDefinitions().hasAttackOption()
								|| ((!isAtMultiArea() || !npc.isAtMultiArea()) && npc.getAttackedBy() != this
										&& npc.getAttackedByDelay() > Utils.currentTimeMillis())
								|| !clipedProjectile(npc, false))
							continue;
						possibleTarget.add(npc);
					}
				}
			}
		}
		return possibleTarget;
	}

	public ArrayList<Entity> getPossibleTargets() {
		return getPossibleTargets(false, true);
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0;
	}

	public int getRespawnDirection() {
		NPCDefinitions definitions = getDefinitions();
		if (definitions.contrast << 32 != 0 && definitions.respawnDirection > 0 && definitions.respawnDirection <= 8)
			return (4 + definitions.respawnDirection) << 11;
		return 0;
	}

	public WorldTile getRespawnTile() {
		return respawnTile;
	}

	@Override
	public int getSize() {
		return getDefinitions().size;
	}

	@Override
	public void handleIngoingHit(final Hit hit) {
		if (capDamage != -1 && hit.getDamage() > capDamage)
			hit.setDamage(capDamage);
		if (hit.getLook() != HitLook.MELEE_DAMAGE && hit.getLook() != HitLook.RANGE_DAMAGE
				&& hit.getLook() != HitLook.MAGIC_DAMAGE)
			return;
		Entity source = hit.getSource();
		if (source == null)
			return;
		if (source instanceof Player) {
			final Player p2 = (Player) source;
			if (p2.getPrayer().hasPrayersOn()) {
				if (p2.getPrayer().usingPrayer(1, 18))
					sendSoulSplit(hit, p2);
				if (hit.getDamage() == 0)
					return;
				if (!p2.getPrayer().isBoostedLeech()) {
					if (hit.getLook() == HitLook.MELEE_DAMAGE) {
						if (p2.getPrayer().usingPrayer(1, 19)) {
							p2.getPrayer().setBoostedLeech(true);
							return;
						} else if (p2.getPrayer().usingPrayer(1, 1)) { // sap
							// att
							if (Utils.getRandom(4) == 0) {
								if (p2.getPrayer().reachedMax(0)) {
									p2.getPackets().sendGameMessage(
											"Your opponent has been weakened so much that your sap curse has no effect.",
											true);
								} else {
									p2.getPrayer().increaseLeechBonus(0);
									p2.getPackets().sendGameMessage(
											"Your curse drains Attack from the enemy, boosting your Attack.", true);
								}
								p2.setNextAnimation(new Animation(12569));
								p2.setNextGraphics(new Graphics(2214));
								p2.getPrayer().setBoostedLeech(true);
								World.sendProjectile(p2, this, 2215, 35, 35, 20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2216));
									}
								}, 1);
								return;
							}
						} else {
							if (p2.getPrayer().usingPrayer(1, 10)) {
								if (Utils.getRandom(7) == 0) {
									if (p2.getPrayer().reachedMax(3)) {
										p2.getPackets().sendGameMessage(
												"Your opponent has been weakened so much that your leech curse has no effect.",
												true);
									} else {
										p2.getPrayer().increaseLeechBonus(3);
										p2.getPackets().sendGameMessage(
												"Your curse drains Attack from the enemy, boosting your Attack.", true);
									}
									p2.setNextAnimation(new Animation(12575));
									p2.getPrayer().setBoostedLeech(true);
									World.sendProjectile(p2, this, 2231, 35, 35, 20, 5, 0, 0);
									WorldTasksManager.schedule(new WorldTask() {
										@Override
										public void run() {
											setNextGraphics(new Graphics(2232));
										}
									}, 1);
									return;
								}
							}
							if (p2.getPrayer().usingPrayer(1, 14)) {
								if (Utils.getRandom(7) == 0) {
									if (p2.getPrayer().reachedMax(7)) {
										p2.getPackets().sendGameMessage(
												"Your opponent has been weakened so much that your leech curse has no effect.",
												true);
									} else {
										p2.getPrayer().increaseLeechBonus(7);
										p2.getPackets().sendGameMessage(
												"Your curse drains Strength from the enemy, boosting your Strength.",
												true);
									}
									p2.setNextAnimation(new Animation(12575));
									p2.getPrayer().setBoostedLeech(true);
									World.sendProjectile(p2, this, 2248, 35, 35, 20, 5, 0, 0);
									WorldTasksManager.schedule(new WorldTask() {
										@Override
										public void run() {
											setNextGraphics(new Graphics(2250));
										}
									}, 1);
									return;
								}
							}

						}
					}
					if (hit.getLook() == HitLook.RANGE_DAMAGE) {
						if (p2.getPrayer().usingPrayer(1, 2)) { // sap range
							if (Utils.getRandom(4) == 0) {
								if (p2.getPrayer().reachedMax(1)) {
									p2.getPackets().sendGameMessage(
											"Your opponent has been weakened so much that your sap curse has no effect.",
											true);
								} else {
									p2.getPrayer().increaseLeechBonus(1);
									p2.getPackets().sendGameMessage(
											"Your curse drains Range from the enemy, boosting your Range.", true);
								}
								p2.setNextAnimation(new Animation(12569));
								p2.setNextGraphics(new Graphics(2217));
								p2.getPrayer().setBoostedLeech(true);
								World.sendProjectile(p2, this, 2218, 35, 35, 20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2219));
									}
								}, 1);
								return;
							}
						} else if (p2.getPrayer().usingPrayer(1, 11)) {
							if (Utils.getRandom(7) == 0) {
								if (p2.getPrayer().reachedMax(4)) {
									p2.getPackets().sendGameMessage(
											"Your opponent has been weakened so much that your leech curse has no effect.",
											true);
								} else {
									p2.getPrayer().increaseLeechBonus(4);
									p2.getPackets().sendGameMessage(
											"Your curse drains Range from the enemy, boosting your Range.", true);
								}
								p2.setNextAnimation(new Animation(12575));
								p2.getPrayer().setBoostedLeech(true);
								World.sendProjectile(p2, this, 2236, 35, 35, 20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2238));
									}
								});
								return;
							}
						}
					}
					if (hit.getLook() == HitLook.MAGIC_DAMAGE) {
						if (p2.getPrayer().usingPrayer(1, 3)) { // sap mage
							if (Utils.getRandom(4) == 0) {
								if (p2.getPrayer().reachedMax(2)) {
									p2.getPackets().sendGameMessage(
											"Your opponent has been weakened so much that your sap curse has no effect.",
											true);
								} else {
									p2.getPrayer().increaseLeechBonus(2);
									p2.getPackets().sendGameMessage(
											"Your curse drains Magic from the enemy, boosting your Magic.", true);
								}
								p2.setNextAnimation(new Animation(12569));
								p2.setNextGraphics(new Graphics(2220));
								p2.getPrayer().setBoostedLeech(true);
								World.sendProjectile(p2, this, 2221, 35, 35, 20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2222));
									}
								}, 1);
								return;
							}
						} else if (p2.getPrayer().usingPrayer(1, 12)) {
							if (Utils.getRandom(7) == 0) {
								if (p2.getPrayer().reachedMax(5)) {
									p2.getPackets().sendGameMessage(
											"Your opponent has been weakened so much that your leech curse has no effect.",
											true);
								} else {
									p2.getPrayer().increaseLeechBonus(5);
									p2.getPackets().sendGameMessage(
											"Your curse drains Magic from the enemy, boosting your Magic.", true);
								}
								p2.setNextAnimation(new Animation(12575));
								p2.getPrayer().setBoostedLeech(true);
								World.sendProjectile(p2, this, 2240, 35, 35, 20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2242));
									}
								}, 1);
								return;
							}
						}
					}

					// overall

					if (p2.getPrayer().usingPrayer(1, 13)) { // leech defence
						if (Utils.getRandom(10) == 0) {
							if (p2.getPrayer().reachedMax(6)) {
								p2.getPackets().sendGameMessage(
										"Your opponent has been weakened so much that your leech curse has no effect.",
										true);
							} else {
								p2.getPrayer().increaseLeechBonus(6);
								p2.getPackets().sendGameMessage(
										"Your curse drains Defence from the enemy, boosting your Defence.", true);
							}
							p2.setNextAnimation(new Animation(12575));
							p2.getPrayer().setBoostedLeech(true);
							World.sendProjectile(p2, this, 2244, 35, 35, 20, 5, 0, 0);
							WorldTasksManager.schedule(new WorldTask() {
								@Override
								public void run() {
									setNextGraphics(new Graphics(2246));
								}
							}, 1);
							return;
						}
					}
				}
			}
		}

	}

	public boolean hasChangedCombatLevel() {
		return changedCombatLevel;
	}

	public boolean hasDefender() {
		if (containsItem(8844) || containsItem(8845) || containsItem(8846) || containsItem(8847) || containsItem(8848)
				|| containsItem(8849) || containsItem(8850) || containsItem(20072)) {
			return true;
		}
		return false;
	}

	public boolean hasForceWalk() {
		return forceWalk != null;
	}

	public boolean hasRandomWalk() {
		return randomwalk;
	}

	public boolean isCantFollowUnderCombat() {
		return cantFollowUnderCombat;
	}

	public boolean isCantInteract() {
		return cantInteract;
	}

	public boolean isClueScrollNPC(String npcName) {
		switch (npcName) {
		case "'Rum'-pumped crab":
		case "Aberrant spectre":
		case "Abyssal demon":
		case "Abyssal leech":
		case "Air elemental":
		case "Ancient mage":
		case "Ancient ranger":
		case "Ankou":
		case "Armoured zombie":
		case "Arrg":
		case "Aviansie":
		case "Bandit":
		case "Banshee":
		case "Barbarian":
		case "Barbarian woman":
		case "Basilisk":
		case "Black Guard":
		case "Black Guard Berserker":
		case "Black Guard crossbowdwarf":
		case "Black Heather":
		case "Black Knight":
		case "Black Knight Titan":
		case "Black demon":
		case "Black dragon":
		case "Blood reaver":
		case "Bloodveld":
		case "Blue dragon":
		case "Bork":
		case "Brine rat":
		case "Bronze dragon":
		case "Brutal green dragon":
		case "Catablepon":
		case "Cave bug":
		case "Cave crawler":
		case "Cave horror":
		case "Cave slime":
		case "Chaos Elemental":
		case "Chaos druid":
		case "Chaos druid warrior":
		case "Chaos dwarf":
		case "Chaos dwarf hand cannoneer":
		case "Chaos dwogre":
		case "Cockatrice":
		case "Cockroach drone":
		case "Cockroach soldier":
		case "Cockroach worker":
		case "Columbarium":
		case "Columbarium key":
		case "Commander Zilyana":
		case "Corporeal Beast":
		case "Crawling Hand":
		case "Cyclops":
		case "Cyclossus":
		case "Dagannoth":
		case "Dagannoth Prime":
		case "Dagannoth Rex":
		case "Dagannoth Supreme":
		case "Dagannoth guardian":
		case "Dagannoth spawn":
		case "Dark beast":
		case "Desert Lizard":
		case "Desert strykewyrm":
		case "Dried zombie":
		case "Dust devil":
		case "Dwarf":
		case "Earth elemental":
		case "Earth warrior":
		case "Elf warrior":
		case "Elite Black Knight":
		case "Elite Dark Ranger":
		case "Elite Khazard guard":
		case "Exiled Kalphite Queen":
		case "Exiled kalphite guardian":
		case "Exiled kalphite marauder":
		case "Ferocious barbarian spirit":
		case "Fire elemental":
		case "Fire giant":
		case "Flesh Crawler":
		case "Forgotten Archer":
		case "Forgotten Mage":
		case "Forgotten Warrior":
		case "Frog":
		case "Frost dragon":
		case "Ganodermic beast":
		case "Gargoyle":
		case "General Graardor":
		case "General malpractitioner":
		case "Ghast":
		case "Ghostly warrior":
		case "Giant Mole":
		case "Giant ant soldier":
		case "Giant ant worker":
		case "Giant rock crab":
		case "Giant skeleton":
		case "Giant wasp":
		case "Glacor":
		case "Glod":
		case "Gnoeals":
		case "Goblin statue":
		case "Gorak":
		case "Greater demon":
		case "Greater reborn mage":
		case "Greater reborn ranger":
		case "Greater reborn warrior":
		case "Green dragon":
		case "Grotworm":
		case "Haakon the Champion":
		case "Harold":
		case "Harpie Bug Swarm":
		case "Hill giant":
		case "Hobgoblin":
		case "Ice giant":
		case "Ice strykewyrm":
		case "Ice troll":
		case "Ice troll female":
		case "Ice troll male":
		case "Ice troll runt":
		case "Ice warrior":
		case "Icefiend":
		case "Iron dragon":
		case "Jelly":
		case "Jogre":
		case "Jungle horror":
		case "Jungle strykewyrm":
		case "K'ril Tsutsaroth":
		case "Kalphite Guardian":
		case "Kalphite King":
		case "Kalphite Queen":
		case "Kalphite Soldier":
		case "Kalphite Worker":
		case "Killerwatt":
		case "King Black Dragon":
		case "Kraka":
		case "Kree'arra":
		case "Kurask":
		case "Lanzig":
		case "Lesser demon":
		case "Lesser reborn mage":
		case "Lesser reborn ranger":
		case "Lesser reborn warrior":
		case "Lizard":
		case "Locust lancer":
		case "Locust ranger":
		case "Locust rider":
		case "Mature grotworm":
		case "Mighty banshee":
		case "Minotaur":
		case "Mithril dragon":
		case "Molanisk":
		case "Moss giant":
		case "Mountain troll":
		case "Mummy":
		case "Mutated bloodveld":
		case "Mutated jadinko male":
		case "Mutated zygomite":
		case "Nechryael":
		case "Nex":
		case "Ogre":
		case "Ogre statue":
		case "Ork statue":
		case "Otherworldly being":
		case "Ourg statue":
		case "Paladin":
		case "Pee Hat":
		case "Pirate":
		case "Pyrefiend":
		case "Queen Black Dragon":
		case "Red dragon":
		case "Rock lobster":
		case "Rockslug":
		case "Salarin the Twisted":
		case "Scabaras lancer":
		case "Scarab mage":
		case "Sea Snake Hatchling":
		case "Shadow warrior":
		case "Skeletal Wyvern":
		case "Skeletal miner":
		case "Skeleton":
		case "Skeleton fremennik":
		case "Skeleton thug":
		case "Skeleton warlord":
		case "Small Lizard":
		case "Soldier":
		case "Sorebones":
		case "Speedy Keith":
		case "Spiritual mage":
		case "Spiritual warrior":
		case "Steel dragon":
		case "Stick":
		case "Suqah":
		case "Terror dog":
		case "Thrower Troll":
		case "Thug":
		case "Tortured soul":
		case "Trade floor guard":
		case "Tribesman":
		case "Troll general":
		case "Troll spectator":
		case "Tstanon Karlak":
		case "Turoth":
		case "Tyras guard":
		case "TzHaar-Hur":
		case "TzHaar-Ket":
		case "TzHaar-Mej":
		case "TzHaar-Xil":
		case "Undead troll":
		case "Vampyre":
		case "Vyre corpse":
		case "Vyrelady":
		case "Vyrelord":
		case "Vyrewatch":
		case "Wallasalki":
		case "Warped terrorbird":
		case "Warped tortoise":
		case "Warrior":
		case "Water elemental":
		case "Waterfiend":
		case "Werewolf":
		case "White Knight":
		case "WildyWyrm":
		case "Yeti":
		case "Yuri":
		case "Zakl'n Gritch":
		case "Zombie":
		case "Zombie hand":
		case "Zombie swab":
			return true;
		}
		return false;
	}

	public boolean isCyclops(String npcName) {
		switch (npcName) {
		case "Cyclops":
			return true;
		}
		return false;
	}

	public boolean isFamiliar() {
		return this instanceof Familiar;
	}

	public boolean isForceAgressive() {
		return forceAgressive;
	}

	public boolean isForceFollowClose() {
		return forceFollowClose;
	}

	public boolean isForceMultiAttacked() {
		return forceMultiAttacked;
	}

	public boolean isForceWalking() {
		return forceWalk != null;
	}

	/**
	 * Gets the locked.
	 * 
	 * @return The locked.
	 */
	public boolean isLocked() {
		return locked;
	}

	public boolean isNoDistanceCheck() {
		return noDistanceCheck;
	}

	public boolean isSpawned() {
		return spawned;
	}

	public boolean isUnderCombat() {
		return combat.underCombat();
	}

	@Override
	public boolean needMasksUpdate() {
		return super.needMasksUpdate() || refreshHeadIcon || nextTransformation != null || changedCombatLevel
				|| changedName;
	}

	public void setNextNPCTransformation(int id) {
		setNPC(id);
		nextTransformation = new Transformation(id);
		if (getCustomCombatLevel() != -1)
			changedCombatLevel = true;
		if (getCustomName() != null)
			changedName = true;
	}

	public String getCustomName() {
		return name;
	}

	@Override
	public void processEntity() {
		super.processEntity();
		processNPC();
	}

	/**
	 * We init custom NPC settings.
	 */
	public void loadNPCSettings() {
		if (id == 6892) {
			setName("Pet Manager");
			setRandomWalk(0);
		}
		for (int npcId : Settings.NON_WALKING_NPCS) {
			if (npcId == id) {
				setRandomWalk(0);
				break;
			}
		}
		for (SlayerMaster master : SlayerMaster.values()) {
			if (master == null)
				continue;
			if (master.getNPCId() == id) {
				setRandomWalk(0);
				break;
			}
		}
		for (int npcId : Settings.FORCE_WALKING_NPCS) {
			if (npcId == id) {
				setRandomWalk(NORMAL_WALK);
				break;
			}
		}
		if (id == 6139) {
			setName(Settings.SERVER_NAME + "'s welcomer");
			setRandomWalk(0);
		}
	}

	public void processNPC() {
		if (isDead() || locked)
			return;
		loadNPCSettings();
		if (!combat.process()) {
			if (!isForceWalking()) {
				if (!cantInteract) {
					if (!checkAgressivity()) {
						if (getFreezeDelay() < Utils.currentTimeMillis()) {
							if (!hasWalkSteps() && (walkType & NORMAL_WALK) != 0) {
								boolean can = false;
								for (int i = 0; i < 2; i++) {
									if (Math.random() * 1000.0 < 100.0) {
										can = true;
										break;
									}
								}
								if (can) {
									int moveX = (int) Math.round(Math.random() * 10.0 - 5.0);
									int moveY = (int) Math.round(Math.random() * 10.0 - 5.0);
									resetWalkSteps();
									if (getMapAreaNameHash() != -1) {
										if (!MapAreas.isAtArea(getMapAreaNameHash(), this)) {
											forceWalkRespawnTile();
											return;
										}
										addWalkSteps(getX() + moveX, getY() + moveY, 5, (walkType & FLY_WALK) == 0);
									} else
										addWalkSteps(respawnTile.getX() + moveX, respawnTile.getY() + moveY, 5,
												(walkType & FLY_WALK) == 0);
								}
							}
						}
					}
				}
			}
		}
		if (isForceWalking()) {
			if (getFreezeDelay() < Utils.currentTimeMillis()) {
				if (getX() != forceWalk.getX() || getY() != forceWalk.getY()) {
					if (!hasWalkSteps()) {
						int steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, getX(), getY(), getPlane(),
								getSize(), new FixedTileStrategy(forceWalk.getX(), forceWalk.getY()), true);
						int[] bufferX = RouteFinder.getLastPathBufferX();
						int[] bufferY = RouteFinder.getLastPathBufferY();
						for (int i = steps - 1; i >= 0; i--) {
							if (!addWalkSteps(bufferX[i], bufferY[i], 25, true))
								break;
						}
					}
					if (!hasWalkSteps()) {
						setNextWorldTile(new WorldTile(forceWalk));
						forceWalk = null;
					}
				} else
					forceWalk = null;
			}
		}
		if (id == 162 && Utils.random(20) == 0)
			setNextForceTalk(new ForceTalk(gnomeTrainerForceTalk[Utils.random(gnomeTrainerForceTalk.length)]));
		if (id == 15786 && Utils.random(20) == 0)
			setNextForceTalk(new ForceTalk(trialAnnouncerForceTalk[Utils.random(trialAnnouncerForceTalk.length)]));
	}

	public void removeTarget() {
		if (combat.getTarget() == null)
			return;
		combat.removeTarget();
	}

	@Override
	public void reset() {
		super.reset();
		setDirection(getRespawnDirection());
		combat.reset();
		bonuses = NPCBonuses.getBonuses(id); // back to real bonuses
		forceWalk = null;
	}

	@Override
	public void resetMasks() {
		super.resetMasks();
		nextTransformation = null;
		changedCombatLevel = false;
		changedName = false;
		refreshHeadIcon = false;
	}

	@Override
	public void sendDeath(final Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		combat.removeTarget();
		setNextAnimation(null);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0)
					setNextAnimation(new Animation(defs.getDeathEmote()));
				else if (loop >= defs.getDeathDelay()) {
					if (source instanceof Player) {
						Player player = (Player) source;
						player.getControlerManager().processNPCDeath(NPC.this);
					}
					drop();
					reset();
					setLocation(respawnTile);
					finish();
					if (!isSpawned())
						setRespawnTask();
					stop();

				}
				loop++;
			}
		}, 0, 1);
	}

	protected void sendDrop(final Player player, Drop drop) {
		final WorldTile tile = new WorldTile(getCoordFaceX(getSize()), getCoordFaceY(getSize()), getPlane());
		final String dropName = ItemDefinitions.getItemDefinitions(drop.getItemId()).getName().toLowerCase();
		boolean dropOk = true;
		if (drop.getItemId() == 618)
			dropOk = false;
		if (drop.getItemId() == 995)
			dropOk = false;
		if (drop.getItemId() == 0)
			dropOk = false;
		if (BoneCrusher.handleBoneCrusher(player, drop.getItemId()))
			dropOk = false;
		if (Herbicide.handleHerbicide(player, dropName, drop))
			dropOk = false;
		if (drop.getItemId() == 11286) {
			if (Utils.random(100) < 75)
				dropOk = false;
		}
		if (drop.getItemId() == 31203) {
			if (Utils.random(100) < 90)
				dropOk = false;
		}
		CharmingImp.handleCharmDrops(player, this);
		if (player.getPerkManager().herbivore) {
			if (Utils.getRandom(150) <= 1) {
				World.updateGroundItem(new Item(3062), tile, player, 60, 0, true);
				player.sendMessage("You've received a Herb Box drop thanks to your Herbivore perk.", true);
			}
		}
		if (dropName.contains("Clue"))
			dropOk = false;
		if (getName().equalsIgnoreCase("Green dragon")) {
			if (Utils.getRandom((player.getPerkManager().dragonTrainer ? 1000 : 1500)) == 0) {
				if (!player.hasItem(new Item(12473))) {
					World.updateGroundItem(new Item(12473, 1), tile, player, 60, 0, true);
					player.sendMessage("This green dragon was carrying an egg which hatched when dropped.", true);
				}
			}
		}
		if (getName().equalsIgnoreCase("Blue dragon")) {
			if (Utils.getRandom((player.getPerkManager().dragonTrainer ? 1000 : 1500)) == 0) {
				if (!player.hasItem(new Item(12471))) {
					World.updateGroundItem(new Item(12471, 1), tile, player, 60, 0, true);
					player.sendMessage("This blue dragon was carrying an egg which hatched when dropped.", true);
				}
			}
		}
		if (getName().equalsIgnoreCase("Red dragon")) {
			if (Utils.getRandom((player.getPerkManager().dragonTrainer ? 1000 : 1500)) == 0) {
				if (!player.hasItem(new Item(12469))) {
					World.updateGroundItem(new Item(12469, 1), tile, player, 60, 0, true);
					player.sendMessage("This red dragon was carrying an egg which hatched when dropped.", true);
				}
			}
		}
		if (getName().equalsIgnoreCase("Black dragon")
				|| getName().equalsIgnoreCase("King black dragon") && !getName().contains("Queen")) {
			if (Utils.getRandom((player.getPerkManager().dragonTrainer ? 1000 : 1500)) == 0) {
				if (!player.hasItem(new Item(12475))) {
					World.updateGroundItem(new Item(12475, 1), tile, player, 60, 0, true);
					player.sendMessage("This black dragon was carrying an egg which hatched when dropped.", true);
				}
			}
		}
		if (dropOk) {
			Item item = new Item(drop.getItemId());
			int amount = drop.getMinAmount() + Utils.getRandom(drop.getExtraAmount());
			if (GrandExchange.getPrice(item.getId()) >= player.setLootBeam && player.hasLootBeam())
				World.sendLootbeam(player, tile);
			int coins = (int) (Utils.random(getCombatLevel() * Utils.random(2, 100))
					* Settings.getDropQuantityRate(player));
			CoinAccumulator.handleCoinAccumulator(player, this, coins);

			World.updateGroundItem(new Item(drop.getItemId(), amount), tile, player, 60, 0, true);
			if (dropName.contains("pernix") || dropName.contains("torva") || dropName.contains("virtus")
					|| dropName.contains("bandos") || dropName.contains("hilt")
					|| (dropName.contains("armadyl") && !dropName.contains("rune") && !dropName.contains("shard"))
					|| dropName.contains("spirit shield")
					|| (dropName.contains("saradomin ") && !dropName.contains("brew"))
					|| dropName.contains("dragon claw") || dropName.contains("dragon full")
					|| dropName.contains("dragon pick") || dropName.contains("zaryte") || dropName.contains("steadf")
					|| dropName.contains("glaiven") || dropName.contains("ragef") || dropName.contains("hiss")
					|| dropName.contains("murmur") || dropName.contains("whisper") || dropName.contains("dragon claw")
					|| dropName.contains("ascension grip") || dropName.contains("drygore")
					|| dropName.contains("subjugation") || dropName.contains("draconic")
					|| (dropName.contains("zamorak") && !dropName.contains("wine") && !dropName.contains("brew"))
					|| dropName.contains("dragon kite") || dropName.contains("dragon pick")
					|| dropName.contains("scythe") || dropName.contains("seismic") || dropName.contains("elixi")
					|| dropName.contains("sigil") || dropName.contains("zamorakian spear")
					|| dropName.contains("vanguard")) {

				String message = Utils.getAorAn(dropName) + " " + dropName;

				if (dropName.contains("gloves") || dropName.contains("boots"))
					message = "a pair of " + dropName;

				if (dropName.contains("chaps") || dropName.contains("tassets"))
					message = dropName;

				World.sendWorldMessage(Colors.orange + "<shad=000000><img=6>News: " + player.getDisplayName()
						+ " received " + message + " from " + getName() + ".", false);

				new Thread(new NewsManager(player, "<b><img src=\"./bin/images/news/drop.png\" height=14> "
						+ player.getDisplayName() + " received " + message + " from " + getName() + ".")).start();

			}
		}
	}

	/**
	 * Sends Soulsplit - Player to NPC.
	 * 
	 * @param hit
	 *            The Hit.
	 * @param user
	 *            The Entity (Player) using SoulSplit.
	 */
	public void sendSoulSplit(final Hit hit, final Entity user) {
		final NPC target = this;
		if (hit.getDamage() > 0)
			World.sendProjectile(user, this, 2263, 11, 11, 20, 5, 0, 0);
		double heal = 5.0;
		/** Amulet of Souls **/
		if (((Player) user).getEquipment().getAmuletId() == 31875 && Utils.random(100) >= 50)
			heal -= Utils.random(1.25, 2.5); // We reduce since it divides
												// damage by heal
		user.heal((int) (hit.getDamage() / heal));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				setNextGraphics(new Graphics(2264));
				if (hit.getDamage() > 0)
					World.sendProjectile(target, user, 2263, 11, 11, 20, 5, 0, 0);
			}
		}, 1);
	}

	@Override
	public void setAttackedBy(Entity target) {
		super.setAttackedBy(target);
		if (target == combat.getTarget() && !(combat.getTarget() instanceof Familiar))
			lastAttackedByTarget = Utils.currentTimeMillis();
	}

	public void setBonuses() {
		bonuses = NPCBonuses.getBonuses(id);
		if (bonuses == null) {
			bonuses = new int[10];
			int level = getCombatLevel();
			for (int i = 0; i < bonuses.length; i++)
				bonuses[i] = level;
		}
	}

	public void setCanBeAttackFromOutOfArea(boolean b) {
		canBeAttackFromOutOfArea = b;
	}

	public void setCantFollowUnderCombat(boolean canFollowUnderCombat) {
		this.cantFollowUnderCombat = canFollowUnderCombat;
	}

	public void setCantInteract(boolean cantInteract) {
		this.cantInteract = cantInteract;
		if (cantInteract)
			combat.reset();
	}

	public void setCapDamage(int capDamage) {
		this.capDamage = capDamage;
	}

	public void setCombatLevel(int level) {
		combatLevel = getDefinitions().combatLevel == level ? -1 : level;
		changedCombatLevel = true;
	}

	public void setForceAgressive(boolean forceAgressive) {
		this.forceAgressive = forceAgressive;
	}

	public void setForceFollowClose(boolean forceFollowClose) {
		this.forceFollowClose = forceFollowClose;
	}

	public void setForceMultiAttacked(boolean forceMultiAttacked) {
		this.forceMultiAttacked = forceMultiAttacked;
	}

	public void setForceTargetDistance(int forceTargetDistance) {
		this.forceTargetDistance = forceTargetDistance;
	}

	public void setForceWalk(WorldTile tile) {
		resetWalkSteps();
		forceWalk = tile;
	}

	/**
	 * Sets the locked.
	 * 
	 * @param locked
	 *            The locked to set.
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public void setLureDelay(int lureDelay) {
		this.lureDelay = lureDelay;
	}

	public void setName(String string) {
		this.name = getDefinitions().name.equals(string) ? null : string;
		changedName = true;
	}

	public boolean hasChangedName() {
		return changedName;
	}

	public void setNoDistanceCheck(boolean noDistanceCheck) {
		this.noDistanceCheck = noDistanceCheck;
	}

	public void setNPC(int id) {
		this.id = id;
		setBonuses();
	}

	public void setRandomWalk(int forceRandomWalk) {
		this.walkType = forceRandomWalk;
	}

	public void setRespawnTask() {
		if (bossInstance != null && (bossInstance.isFinished()
				|| (!bossInstance.isPublic() && !bossInstance.getSettings().hasTimeRemaining())))
			return;
		if (!hasFinished()) {
			reset();
			setLocation(respawnTile);
			finish();
		}
		long respawnDelay = getCombatDefinitions().getRespawnDelay() * 600;
		if (getId() == 6898)
			respawnDelay = 600;
		if (getName().toLowerCase().contains("impling"))
			respawnDelay *= 3;
		if (bossInstance != null)
			respawnDelay /= bossInstance.getSettings().getSpawnSpeed();
		if (Settings.DEBUG)
			System.out.println("Respawn task initiated: [" + getName() + "]; time: [" + respawnDelay + "].");
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					spawn();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, respawnDelay, TimeUnit.MILLISECONDS);
	}

	public void setSpawned(boolean spawned) {
		this.spawned = spawned;
	}

	public void setTarget(Entity entity) {
		if (isForceWalking()) // if force walk not gonna get target
			return;
		combat.setTarget(entity);
		lastAttackedByTarget = Utils.currentTimeMillis();
	}

	public void spawn() {
		setFinished(false);
		World.addNPC(this);
		setLastRegionId(0);
		World.updateEntityRegion(this);
		loadMapRegions();
		checkMultiArea();
	}

	@Override
	public String toString() {
		return getDefinitions().name + " - " + id + " - " + getX() + " " + getY() + " " + getPlane();
	}

	public void transformIntoNPC(int id) {
		setNPC(id);
		nextTransformation = new Transformation(id);
	}

	public int whatDefender() {
		int id = 8844;
		if (containsItem(8850) || containsItem(20072)) {
			id = 20072;
		} else if (containsItem(8849) || containsItem(8850)) {
			id = 8850;
		} else if (containsItem(8848)) {
			id = 8849;
		} else if (containsItem(8847)) {
			id = 8848;
		} else if (containsItem(8846)) {
			id = 8847;
		} else if (containsItem(8845)) {
			id = 8846;
		} else if (containsItem(8844)) {
			id = 8845;
		} else {
			id = 8844;
		}
		return id;
	}

	public boolean withinDistance(Player tile, int distance) {
		return super.withinDistance(tile, distance);
	}

	private boolean intelligentRouteFinder;

	public boolean isIntelligentRouteFinder() {
		return intelligentRouteFinder;
	}

	public void setIntelligentRouteFinder(boolean intelligentRouteFinder) {
		this.intelligentRouteFinder = intelligentRouteFinder;
	}

	private transient BossInstance bossInstance; // if its a instance npc

	public void setBossInstance(BossInstance instance) {
		bossInstance = instance;
	}

	public BossInstance getBossInstance() {
		return bossInstance;
	}

	/**
	 * Head Icons.
	 */
	public HeadIcon[] getIcons() {
		return new HeadIcon[0];
	}

	public void requestIconRefresh() {
		refreshHeadIcon = true;
	}

	public boolean isRefreshHeadIcon() {
		return refreshHeadIcon;
	}

	private String[] gnomeTrainerForceTalk = { "That's it, straight up!", "Come on scaredy cat get across that rope!",
			"My granny can move faster than you!", "Move it, move it, move it!" };

	private String[] trialAnnouncerForceTalk = { "Welcome to the world of " + Settings.SERVER_NAME + "!",
			"Fear Botany Bay, citizens!", "Fear the wild...", "Hmm... Who will it be today?",
			"Beware! You may be next.", "Who will be the next victim?", "The wild is a ruthless place!",
			"Muhahaha.. who's next?", "The wild has NO remorse!", "I can smell the blood from here... Ooh.",
			"Another death; another blood stain!" };

	private int[] PVP_ITEMS = { 13887, 13893, 13899, 13905, 13911, 13917, 13923, 13929, 13884, 13890, 13896, 13902,
			13908, 13914, 13920, 13926, 13870, 13873, 13876, 13879, 13882, 13944, 13947, 13950, 13858, 13861, 13864,
			13867, 13938, 13941, 13944, 13947, 13950, 13953, 13958, 13961, 13964, 13967, 13970, 13973, 13976, 13979,
			13982, 13985, 13988, 14876, 14877, 14878, 14879, 14880, 14881, 14882, 14883, 14884, 14885, 14886, 14887,
			14888, 14889, 14890, 14891, 14892, 13845, 13846, 13847, 13848, 13849, 13850, 13851, 13852, 13853, 13854,
			13855, 13856, 13857 };

	/**
	 * Exclusively used for the Impetuous Impulses minigame.
	 */
	public void setRespawnTaskImpling() {
		if (!hasFinished()) {
			reset();
			setLocation(respawnTile);
			finish();
			if (Settings.DEBUG)
				System.out.println("Finishing NPC: [" + toString() + "].");
		}
		/*
		 * id =
		 * FlyingEntities.values()[Utils.random(FlyingEntities.values().length)]
		 * .getNpcId(); setLocation(new WorldTile(Utils.random(2558 + 3, 2626 -
		 * 3), Utils.random(4285 + 3, 4354 - 3), 0)); long respawnDelay =
		 * getCombatDefinitions().getRespawnDelay() * 600;
		 */
		if (id == 7420) {
			setLocation(new WorldTile(respawnTile));
		} else
			setLocation(new WorldTile(Utils.random(2558 + 3, 2626 - 3), Utils.random(4285 + 3, 4354 - 3), 0));
		long respawnDelay = 1000 * 30;
		if (Settings.DEBUG)
			System.out.println("Respawn task initiated: [" + toString() + "]; time: [" + respawnDelay + "].");
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					spawn();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, respawnDelay, TimeUnit.MILLISECONDS);
	}

	private transient boolean cantSetTargetAutoRelatio;

	public boolean isCantSetTargetAutoRelatio() {
		return cantSetTargetAutoRelatio;
	}

	public void setCantSetTargetAutoRelatio(boolean cantSetTargetAutoRelatio) {
		this.cantSetTargetAutoRelatio = cantSetTargetAutoRelatio;
	}

	public int getAttackSpeed() {
		return getCombatDefinitions().getAttackDelay();
	}

	public void setRespawnTile(WorldTile respawnTile) {
		this.respawnTile = respawnTile;
	}

	public int getAttackStyle() {
		return getCombatDefinitions().getAttackStyle();
	}

	@Override
	public boolean canMove(int dir) {
		return true;
	}
}