package com.rs.game.player;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.Channel;

import com.rs.Settings;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.Projectile;
import com.rs.game.Region;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.activites.BountyHunter;
import com.rs.game.activites.clanwars.FfaZone;
import com.rs.game.activites.clanwars.WarControler;
import com.rs.game.activites.duel.DuelArena;
import com.rs.game.activites.duel.DuelRules;
import com.rs.game.item.FloorItem;
import com.rs.game.item.Item;
import com.rs.game.map.bossInstance.InstanceSettings;
import com.rs.game.npc.NPC;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.npc.godwars.zaros.Nex;
import com.rs.game.npc.pet.Pet;
import com.rs.game.player.BanksManager.ExtraBank;
import com.rs.game.player.actions.ActionManager;
import com.rs.game.player.actions.divination.DivineObject;
import com.rs.game.player.combat.PlayerCombat;
import com.rs.game.player.content.ChatMessage;
import com.rs.game.player.content.FriendChatsManager;
import com.rs.game.player.content.HintIconsManager;
import com.rs.game.player.content.InterfaceManager;
import com.rs.game.player.content.ItemConstants;
import com.rs.game.player.content.LocalNPCUpdate;
import com.rs.game.player.content.LocalPlayerUpdate;
import com.rs.game.player.content.LogicPacket;
import com.rs.game.player.content.LoyaltyManager;
import com.rs.game.player.content.Magic;
import com.rs.game.player.content.MoneyPouch;
import com.rs.game.player.content.Pots;
import com.rs.game.player.content.PriceCheckManager;
import com.rs.game.player.content.PublicChatMessage;
import com.rs.game.player.content.QuickChatMessage;
import com.rs.game.player.content.ReferralHandler;
import com.rs.game.player.content.RouteEvent;
import com.rs.game.player.content.SkillCapeCustomizer;
import com.rs.game.player.content.SlayerTask;
import com.rs.game.player.content.Toolbelt;
import com.rs.game.player.content.ToolbeltNew;
import com.rs.game.player.content.Trade;
import com.rs.game.player.content.Trade.CloseTradeStage;
import com.rs.game.player.content.VarBitManager;
import com.rs.game.player.content.WeeklyTopRanking;
import com.rs.game.player.content.clans.ClansManager;
import com.rs.game.player.content.construction.House;
import com.rs.game.player.content.contracts.Contract;
import com.rs.game.player.content.contracts.ContractHandler;
import com.rs.game.player.content.grandExchange.GrandExchange;
import com.rs.game.player.content.grandExchange.GrandExchangeManager;
import com.rs.game.player.content.items.Defenders;
import com.rs.game.player.content.items.PrayerBooks;
import com.rs.game.player.content.pet.PetManager;
import com.rs.game.player.content.slayer.CooperativeSlayer;
import com.rs.game.player.content.xmas.XmasEvent;
import com.rs.game.player.controllers.BarrelchestController;
import com.rs.game.player.controllers.Controller;
import com.rs.game.player.controllers.CorpBeastController;
import com.rs.game.player.controllers.CrucibleController;
import com.rs.game.player.controllers.DTController;
import com.rs.game.player.controllers.DeathEvent;
import com.rs.game.player.controllers.DungeonController;
import com.rs.game.player.controllers.FightCaves;
import com.rs.game.player.controllers.FightKiln;
import com.rs.game.player.controllers.GodWars;
import com.rs.game.player.controllers.ImpossibleJad;
import com.rs.game.player.controllers.InstancedPVPControler;
import com.rs.game.player.controllers.JailController;
import com.rs.game.player.controllers.NomadsRequiem;
import com.rs.game.player.controllers.QueenBlackDragonController;
import com.rs.game.player.controllers.WarriorsGuild;
import com.rs.game.player.controllers.Wilderness;
import com.rs.game.player.controllers.ZGDController;
import com.rs.game.player.controllers.castlewars.CastleWarsPlaying;
import com.rs.game.player.controllers.castlewars.CastleWarsWaiting;
import com.rs.game.player.controllers.fightpits.FightPitsArena;
import com.rs.game.player.controllers.pestcontrol.PestControlGame;
import com.rs.game.player.controllers.pestcontrol.PestControlLobby;
import com.rs.game.player.controllers.zombie.ZombieControler;
import com.rs.game.player.cutscenes.Cutscene;
import com.rs.game.player.cutscenes.CutscenesManager;
import com.rs.game.player.dialogue.DialogueManager;
import com.rs.game.player.newquests.NewQuestManager;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.network.Session;
import com.rs.network.protocol.codec.decode.WorldPacketsDecoder;
import com.rs.network.protocol.codec.decode.impl.ButtonHandler;
import com.rs.network.protocol.codec.encode.WorldPacketsEncoder;
import com.rs.utils.Colors;
import com.rs.utils.DonationRank;
import com.rs.utils.Donations;
import com.rs.utils.IsaacKeyPair;
import com.rs.utils.Lend;
import com.rs.utils.Logger;
import com.rs.utils.PkRank;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.Utils;
import com.rs.utils.impl.Highscores;

import mysql.impl.HiscoresManager;
import mysql.impl.StoreManager;

public class Player extends Entity {

	private static final long serialVersionUID = 2011932556974180375L;

	public static final int TELE_MOVE_TYPE = 127, WALK_MOVE_TYPE = 1, RUN_MOVE_TYPE = 2;

	public static boolean isMainhand = false;

	public transient ActionManager actionManager = new ActionManager(this);

	private transient Channel channel;
	public transient LoyaltyManager loyaltyManager;
	private boolean agrithNaNa;
	private boolean allowChatEffects;
	private GlobalPlayerUpdater globalPlayerUpdater;
	public AuraManager auraManager = new AuraManager();
	private Bank bank;
	private int barrowsKillCount;
	private int barrowsRunsDone;
	private transient long boneDelay;
	private transient boolean canPvp;
	private transient boolean cantTrade;
	// damage dummy
	public int damagepoints = 0;
	// points system
	public int TentMulti = 0;
	public boolean hasOpenedTentShop;
	public boolean hasOpenedTentShop2;

	// newpkstuff
	public int highestKillStreak, killStreak, killStreakPoints, totalkillStreakPoints;
	public int highestKill;

	// Ip Lock
	public boolean iplocked; // Is player using iplock
	public String lockedwith; // which ip is account locked to

	private transient boolean castedVeng;

	public ChargesManager charges;

	private int clanStatus;

	public transient boolean clientLoadedMapRegion;

	private transient Runnable closeInterfacesEvent;
	private CombatDefinitions combatDefinitions;
	// completionistcape reqs
	private boolean completedFightCaves;
	private boolean completedFightKiln;
	private boolean completedRfd;
	private int[] completionistCapeCustomized;
	private ControlerManager controlerManager;
	private long creationDate;
	private int crucibleHighScore;
	private boolean culinaromancer;
	public transient FriendChatsManager currentFriendChat;
	private transient ClansManager clanManager, guestClanManager;
	private String currentFriendChatOwner;
	private String clanName;
	private boolean connectedClanChannel;
	public transient CutscenesManager cutscenesManager = new CutscenesManager(this);
	private boolean dessourt;
	public transient DialogueManager dialogueManager = new DialogueManager(this);
	private transient boolean disableEquip;
	public transient int displayMode;
	private String displayName;
	public DominionTower dominionTower = new DominionTower();
	private boolean donator;
	private long donatorTill;
	private DuelArena duelarena;
	private EmotesManager emotesManager;
	public TreasureTrails treasureTrails;
	public BountyHunter bountHunter;
	public Equipment equipment;
	private boolean extremeDonator;

	private long extremeDonatorTill;
	public int[] fairyRingCombination = new int[3];
	public Familiar familiar;
	private boolean filterGame;
	private transient boolean finishing;
	private long fireImmune;
	private long superAntiFire;
	private boolean flamBeed;
	private transient long foodDelay;
	private boolean forceNextMapLoadRefresh;
	private int friendChatSetup;
	private FriendsIgnores friendsIgnores;
	private int hiddenBrother;

	private boolean hideWorldAnnouncements;

	public transient HintIconsManager hintIconsManager = new HintIconsManager(this);
	private transient double hpBoostMultiplier;
	private boolean inAnimationRoom;
	public transient InterfaceManager interfaceManager = new InterfaceManager(this);
	private Inventory inventory;
	private transient boolean invulnerable;
	public transient IsaacKeyPair isaacKeyPair;
	private boolean isInDefenderRoom;
	private long jailed;
	private boolean karamel;
	// objects
	private boolean khalphiteLairEntranceSetted;
	private boolean khalphiteLairSetted;
	// honor
	private int killCount, deathCount;
	// barrows
	private boolean[] killedBarrowBrothers;
	private boolean killedBork;
	private boolean killedQueenBlackDragon;
	private transient boolean largeSceneView;
	private int lastBonfire;
	private transient DuelRules duelRules;
	private String lastIP;
	@SuppressWarnings("unused")
	private String lastKillIP;
	private long lastLoggedIn;
	private transient long lastPublicMessage;

	public transient LocalNPCUpdate localNPCUpdate;
	// used for update
	public transient LocalPlayerUpdate localPlayerUpdate = new LocalPlayerUpdate(this);
	private transient long lockDelay; // used for doors and stuff like that
	// used for packets logic
	public transient ConcurrentLinkedQueue<LogicPacket> logicPackets;
	private transient boolean toogleLootShare;

	// skill capes customizing
	private int[] maxedCapeCustomized;
	public int money;
	private boolean mouseButtons;
	private MusicsManager musicsManager;

	public Notes notesL;

	private int overloadDelay;
	private List<String> ownedObjectsManagerKeys;
	private transient long packetsDecoderPing;
	// saving stuff
	private String password;
	private String purePassword;
	private boolean permBanned;

	private boolean permMuted;
	private int pestControlGames;

	private int pestPoints;
	public transient Pet pet;
	public PetManager petManager = new PetManager();
	private int pkPoints;
	private int pkPointReward;
	private int pointsHad;
	private long poisonImmune;
	public transient long polDelay;
	public transient long bloodDelay;
	public boolean defenderPassive;
	private transient long potDelay;
	public MoneyPouch pouch = new MoneyPouch(this);

	private int[] pouches;
	public Prayer prayer;
	private int prayerRenewalDelay;

	public transient PriceCheckManager priceCheckManager = new PriceCheckManager(this);

	private int privateChatSetup;

	// game bar status
	private int publicStatus;

	public QuestManager questManager;

	private String registeredMac, currentMac;
	private boolean reportOption;
	private transient boolean resting;
	private int rights;
	private byte runEnergy;
	private int runeSpanPoints;
	private transient boolean running;

	public transient Session session;

	public Skills skills;

	public StoreManager store;

	private int skullDelay;

	private int skullId;

	private int slayerPoints;

	private transient boolean spawnsMode;

	private int specRestoreTimer;

	private int spins;

	public int infusedPouches;

	// player stages
	private transient boolean active;

	private int summoningLeftClickOption;

	private transient List<Integer> switchItemCache;

	private boolean talkedtoCook;

	// Slayer
	private SlayerTask task;

	public Contract Rtask;

	public int tasksCompleted;

	private boolean talkedWithVannaka, talkedWithMarv;

	private int taskStreak;

	public void setTaskStreak(int amount) {
		this.taskStreak = amount;
	}

	public int getTaskStreak() {
		return taskStreak;
	}

	// reaper
	private ContractHandler cHandler;
	private Contract cContracts;

	private int temporaryMovementType;

	public transient Trade trade = new Trade(this);

	private int tradeStatus;

	private boolean updateMovementType;

	public int usedMacs;

	// transient stuff
	public transient String username;
	private transient long yellDelay;

	private int vecnaTimer;

	private int votePoints;

	private boolean wonFightPits;

	private boolean xpLocked;

	private String yellColor = "ff0000";

	private boolean yellDisabled;

	private boolean yellOff;

	private long muted;

	private long banned;

	public boolean xmasTitle1;
	public boolean xmasTitle2;
	public boolean xmasTitle3;
	public boolean xmasTitle4;

	public boolean hasXmasTitleUnlocked() {
		return (xmasTitle1 || xmasTitle2 || xmasTitle3 || xmasTitle4);
	}

	// creates Player and saved classes
	public Player(String password, String mac) {
		super(Settings.START_PLAYER_LOCATION);
		setHitpoints(Settings.START_PLAYER_HITPOINTS);
		this.password = password;
		registeredMac = mac;
		setGlobalPlayerUpdater(new GlobalPlayerUpdater());
		inventory = new Inventory();
		dungManager = new DungManager();
		dayOfWeekManager = new DayOfWeekManager();
		dailyTaskManager = new DailyTaskManager();
		banksManager = new BanksManager();
		newQuestManager = new NewQuestManager();
		slayerManager = new SlayerManager();
		ArtisansWorkShopSupplies = new int[5];
		gearPresets = new GearPresets();
		equipment = new Equipment();
		skills = new Skills();
		bountyHunter = new BountyHunter();
		elderTreeManager = new ElderTreeManager();
		cHandler = new ContractHandler();
		squealOfFortune = new SquealOfFortune();
		coOpSlayer = new CooperativeSlayer();
		combatDefinitions = new CombatDefinitions();
		prayer = new Prayer();
		bank = new Bank();
		overrides = new CosmeticOverrides();
		unlockedCostumesIds = new ArrayList<Integer>();
		animations = new AnimationOverrides();
		controlerManager = new ControlerManager();
		treasureTrails = new TreasureTrails();
		prayerBook = new boolean[PrayerBooks.BOOKS.length];
		farmingManager = new FarmingManager();
		musicsManager = new MusicsManager();
		emotesManager = new EmotesManager();
		ports = new PlayerOwnedPort();
		xmas = new XmasEvent();
		friendsIgnores = new FriendsIgnores();
		petLootManager = new PetLootManager();
		dominionTower = new DominionTower();
		house = new House();
		charges = new ChargesManager();
		auraManager = new AuraManager();
		questManager = new QuestManager();
		petManager = new PetManager();
		geManager = new GrandExchangeManager();
		toolBelt = new Toolbelt(this);
		toolBeltNew = new ToolbeltNew(this);
		perkManager = new PerkManager();
		membership = new MembershipHandler();
		nonPermaLootersPerks = new ArrayList<String>();
		nonPermaSkillersPerks = new ArrayList<String>();
		nonPermaUtilityPerks = new ArrayList<String>();
		nonPermaCombatantPerks = new ArrayList<String>();
		titles = new Titles();
		runEnergy = 100;
		allowChatEffects = true;
		profanityFilter = true;
		mouseButtons = true;
		pouches = new int[4];
		warriorPoints = new double[6];
		resetBarrows();
		killStats = new int[512];
		boons = new boolean[12];
		SkillCapeCustomizer.resetSkillCapes(this);
		ownedObjectsManagerKeys = new LinkedList<String>();
		setCreationDate(Utils.currentTimeMillis());
		currentFriendChatOwner = "Zeus";
	}

	public void addFireImmune(long time) {
		fireImmune = time + Utils.currentTimeMillis();
	}

	public void addFoodDelay(long time) {
		foodDelay = time + Utils.currentTimeMillis();
	}

	public void addLogicPacketToQueue(LogicPacket packet) {
		for (LogicPacket p : logicPackets) {
			if (p.getId() == packet.getId()) {
				logicPackets.remove(p);
				break;
			}
		}
		logicPackets.add(packet);
	}

	public void addPoisonImmune(long time) {
		poisonImmune = time + Utils.currentTimeMillis();
		getPoison().reset();
	}

	public void addPolDelay(long delay) {
		polDelay = delay + Utils.currentTimeMillis();
	}

	public void addBloodDelay(long time) {
		bloodDelay = time + Utils.currentTimeMillis();
	}

	public int getdamagepoints() {
		return damagepoints;
	}

	public void setdamagepoints(int damagepoints) {
		this.damagepoints = damagepoints;
	}

	public void addPotDelay(long time) {
		potDelay = time + Utils.currentTimeMillis();
	}

	/**
	 * Adds points
	 * 
	 * @param points
	 */
	public void addRunespanPoints(int points) {
		this.runeSpanPoints += points;
	}

	public boolean canSpawn() {
		if (Wilderness.isAtWild(this) || getControlerManager().getControler() instanceof FightPitsArena
				|| getControlerManager().getControler() instanceof CorpBeastController
				|| getControlerManager().getControler() instanceof PestControlLobby
				|| getControlerManager().getControler() instanceof PestControlGame
				|| getControlerManager().getControler() instanceof ZGDController
				|| getControlerManager().getControler() instanceof DungeonController
				|| getControlerManager().getControler() instanceof GodWars
				|| getControlerManager().getControler() instanceof JailController
				|| getControlerManager().getControler() instanceof DTController
				|| getControlerManager().getControler() instanceof WarControler
				|| getControlerManager().getControler() instanceof DeathEvent
				|| getControlerManager().getControler() instanceof BarrelchestController
				|| getControlerManager().getControler() instanceof DuelArena
				|| getControlerManager().getControler() instanceof CastleWarsPlaying
				|| getControlerManager().getControler() instanceof CastleWarsWaiting
				|| getControlerManager().getControler() instanceof FightCaves
				|| getControlerManager().getControler() instanceof FightKiln
				|| getControlerManager().getControler() instanceof ImpossibleJad
				|| getControlerManager().getControler() instanceof BarrelchestController || FfaZone.inPvpArea(this)
				|| getControlerManager().getControler() instanceof NomadsRequiem
				|| getControlerManager().getControler() instanceof QueenBlackDragonController
				|| getControlerManager().getControler() instanceof ZombieControler || dungManager.isInside()
				|| World.isAtAscensionDungeon(this) || /* South West */(getX() >= 2955 && getY() >= 1735 && // kalphite
																											// king
																											// lair
				/* North East */getX() <= 2997 && getY() <= 1783)

				|| /* South West */(getX() >= 3009 && getY() >= 5955 && // vorago
																		// borehole
				/* North East */getX() <= 3135 && getY() <= 6136))
			return false;

		if (getControlerManager().getControler() instanceof CrucibleController) {
			CrucibleController controler = (CrucibleController) getControlerManager().getControler();
			return !controler.isInside();
		}
		return true;
	}

	public void checkMovement(int x, int y, int plane) {
		Magic.teleControlersCheck(this, new WorldTile(x, y, plane));
	}

	@Override
	public void checkMultiArea() {
		if (!isActive())
			return;
		boolean isAtMultiArea = isForceMultiArea() ? true : World.isMultiArea(this);
		if (isAtMultiArea && !isAtMultiArea()) {
			setAtMultiArea(isAtMultiArea);
			getPackets().sendGlobalConfig(616, 1);
		} else if (!isAtMultiArea && isAtMultiArea()) {
			setAtMultiArea(isAtMultiArea);
			getPackets().sendGlobalConfig(616, 0);
		}
	}

	public boolean clientHasLoadedMapRegion() {
		return clientLoadedMapRegion;
	}

	/**
	 * Closes all on-screen interfaces.
	 */
	public void closeInterfaces() {
		if (interfaceManager.containsScreenInter())
			interfaceManager.closeScreenInterface();
		if (interfaceManager.containsInventoryInter())
			interfaceManager.closeInventoryInterface();
		dialogueManager.finishDialogue();
		if (closeInterfacesEvent != null) {
			closeInterfacesEvent.run();
			closeInterfacesEvent = null;
		}
		getInterfaceManager().closeChatBoxInterface();
		getInterfaceManager().closeFadingInterface();
		// getInterfaceManager().sendWindowPane();
	}

	public void drainRunEnergy() {
		if (dungManager.isInside())
			return;
		if (!getPerkManager().staminaBoost)
			setRunEnergy(runEnergy - 1);
	}

	@Override
	public void finish() {
		finish(0);
	}

	public void finish(final int tryCount) {
		if (finishing || hasFinished())
			return;
		finishing = true;
		stopAll(false, true, !(actionManager.getAction() instanceof PlayerCombat));
		if (isUnderCombat(tryCount) || getEmotesManager().isDoingEmote() || isLocked() || isDead()) {
			CoresManager.slowExecutor.schedule(new Runnable() {
				@Override
				public void run() {

					try {
						packetsDecoderPing = Utils.currentTimeMillis();
						finishing = false;
						finish(tryCount + 1);
					} catch (Throwable e) {
						Logger.handle(e);
					}
				}
			}, 10, TimeUnit.SECONDS);

			return;
		}
		realFinish();
	}

	public String lowestShip() {
		String finale = "";
		int minX = Integer.MAX_VALUE;
		int[] ships = { getPorts().getFirstVoyageMinsLeft(), getPorts().getSecondVoyageMinsLeft(),
				getPorts().getThirdVoyageMinsLeft(), getPorts().getFourthVoyageMinsLeft(),
				getPorts().getFifthVoyageMinsLeft() };
		String[] names = { "first", "second", "third", "fourth", "fifth" };
		for (int i = 0; i < ships.length; i++) {
			if (ships[i] > 0 && ships[i] < minX)
				minX = ships[i];
		}
		for (int i = 0; i < ships.length; i++) {
			if (minX == ships[i])
				finale += names[i] + " ship has " + minX + " minutes left!";
		}
		return finale;
	}

	public void checkPorts() {
		if (!getPorts().hasFirstShip || getPorts().firstShipVoyage == 0 && getPorts().secondShipVoyage == 0
				&& getPorts().thirdShipVoyage == 0 && getPorts().fourthShipVoyage == 0
				&& getPorts().fifthShipVoyage == 0)
			return;
		if (getPorts().hasFirstShipReturned() && getPorts().firstShipVoyage != 0
				|| getPorts().hasSecondShipReturned() && getPorts().secondShipVoyage != 0
				|| getPorts().hasThirdShipReturned() && getPorts().thirdShipVoyage != 0
				|| getPorts().hasFourthShipReturned() && getPorts().fourthShipVoyage != 0
				|| getPorts().hasFifthShipReturned() && getPorts().fifthShipVoyage != 0)
			sendMessage("<img=7>" + Colors.red + "[" + getDisplayName() + "'s Port]:" + Colors.green
					+ " One or more of your ships have returned!", false);
		else
			sendMessage("<img=7>" + Colors.red + "[" + getDisplayName() + "'s Port]:" + Colors.orange + " Your "
					+ lowestShip());
	}

	public int bloodNeckHeal(int cap, int heal) {
		if ((heal + cap) > 350)
			return 350;
		heal(heal);
		cap += heal;
		return cap;
	}

	private long lastVoteClaim;

	public long getLastVoteClaim() {
		return lastVoteClaim;
	}

	public void setLastVoteClaim(long time) {
		this.lastVoteClaim = time;
	}

	public boolean isUnderCombat(int tryCount) {
		return (getAttackedByDelay() + 10000 > Utils.currentTimeMillis());
	}

	public void forceLogout() {
		getPackets().sendLogout();
		setRunning(false);
		realFinish();
	}

	public void forceSession() {
		setRunning(false);
		realFinish();
	}

	public ActionManager getActionManager() {
		return actionManager;
	}

	public AuraManager getAuraManager() {
		return auraManager;
	}

	public Bank getBank() {
		return bank;
	}

	public long getBanned() {
		return banned;
	}

	public int getBarrowsKillCount() {
		return barrowsKillCount;
	}

	public int getBarrowsRunsDone() {
		return barrowsRunsDone;
	}

	public long getBoneDelay() {
		return boneDelay;
	}

	public ChargesManager getCharges() {
		return charges;
	}

	public int getClanStatus() {
		return clanStatus;
	}

	public CombatDefinitions getCombatDefinitions() {
		return combatDefinitions;
	}

	public int[] getCompletionistCapeCustomized() {
		return completionistCapeCustomized;
	}

	public int getTentMulti() {
		return TentMulti;
	}

	public void setTentMulti(int TentMulti) {
		this.TentMulti = TentMulti;
	}

	public ControlerManager getControlerManager() {
		return controlerManager;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public int getCrucibleHighScore() {
		return crucibleHighScore;
	}

	public FriendChatsManager getCurrentFriendChat() {
		return currentFriendChat;
	}

	public String getCurrentFriendChatOwner() {
		return currentFriendChatOwner;
	}

	public String getCurrentMac() {
		return currentMac;
	}

	public CutscenesManager getCutscenesManager() {
		return cutscenesManager;
	}

	public int getDeathCount() {
		return deathCount;
	}

	public DialogueManager getDialogueManager() {
		return dialogueManager;
	}

	public int getDisplayMode() {
		return displayMode;
	}

	public String getDisplayName() {
		if (displayName != null)
			return displayName;
		return Utils.formatPlayerNameForDisplay(username);
	}

	public DominionTower getDominionTower() {
		return dominionTower;
	}

	@SuppressWarnings("deprecation")
	public String getDonatorTill() {
		return (donator ? "never" : new Date(donatorTill).toGMTString()) + ".";
	}

	public DuelArena getDuelArena() {
		return duelarena;
	}

	public EmotesManager getEmotesManager() {
		return emotesManager;
	}

	public Equipment getEquipment() {
		return equipment;
	}

	@SuppressWarnings("deprecation")
	public String getExtremeDonatorTill() {
		return (extremeDonator ? "never" : new Date(extremeDonatorTill).toGMTString()) + ".";
	}

	public Familiar getFamiliar() {
		return familiar;
	}

	public long getFireImmune() {
		return fireImmune;
	}

	public long getFoodDelay() {
		return foodDelay;
	}

	public FriendsIgnores getFriendsIgnores() {
		return friendsIgnores;
	}

	public int getHiddenBrother() {
		return hiddenBrother;
	}

	public HintIconsManager getHintIconsManager() {
		return hintIconsManager;
	}

	public double getHpBoostMultiplier() {
		return hpBoostMultiplier;
	}

	public InterfaceManager getInterfaceManager() {
		return interfaceManager;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public IsaacKeyPair getIsaacKeyPair() {
		return isaacKeyPair;
	}

	public long getJailed() {
		return jailed;
	}

	public int getKillCount() {
		return killCount;
	}

	public int getKillStreak() {
		return killStreak;
	}

	public int getKillStreakPoints() {
		return killStreakPoints;
	}

	public int getTotalKillStreakPoints() {
		return totalkillStreakPoints;
	}

	public boolean[] getKilledBarrowBrothers() {
		return killedBarrowBrothers;
	}

	public int getLastBonfire() {
		return lastBonfire;
	}

	public String getLastHostname() {
		InetAddress addr;
		try {
			addr = InetAddress.getByName(getLastIP());
			String hostname = addr.getHostName();
			return hostname;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getLastIP() {
		return lastIP;
	}

	public long getLastLoggedIn() {
		return lastLoggedIn;
	}

	public long getLastPublicMessage() {
		return lastPublicMessage;
	}

	public LocalNPCUpdate getLocalNPCUpdate() {
		return localNPCUpdate;
	}

	public LocalPlayerUpdate getLocalPlayerUpdate() {
		return localPlayerUpdate;
	}

	public long getLockDelay() {
		return lockDelay;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.6;
	}

	public int[] getMaxedCapeCustomized() {
		return maxedCapeCustomized;
	}

	@Override
	public int getMaxHitpoints() {
		return skills.getLevel(Skills.HITPOINTS) * 10 + equipment.getEquipmentHpIncrease();
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.6;
	}

	public int getMessageIcon() {
		return isSupport() ? 13
				: is420Donator() ? 23
				: isMod() ? 1
						: isYoutube() ? 17
								: isDev() ? 18
										: isOwner() ? 2
												: isSponsorDonator() ? 16
														: isUltimateDonator() ? 20
																: isSupremeDonator() ? 21
																		: isLegendaryDonator() ? 12
																				: isExtremeDonator() ? 22
																						: isDonator() ? 19
																								: isHCIronMan() ? 11
																										: isIronMan()
																												? 10
																												: -1;
	}

	/**
	 * Checks if @this is a staff member.
	 * 
	 * @return if Staff.
	 */
	public boolean isHeadStaff() {
		return isOwner() || isDev();
	}

	public boolean isStaff() {
		return isOwner() || isDev() || getRights() == 2 || isAdmin();
	}

	public boolean isStaff2() {
		return isStaff() || isSupport() || isMod();
	}

	public boolean isStaff3() {
		return isStaff2() || isYoutube();
	}

	public MoneyPouch getMoneyPouch() {
		return pouch;
	}

	public int getMoneyPouchValue() {
		return money;
	}

	public int getMovementType() {
		if (getTemporaryMoveType() != -1)
			return getTemporaryMoveType();
		return getRun() ? RUN_MOVE_TYPE : WALK_MOVE_TYPE;
	}

	public MusicsManager getMusicsManager() {
		return musicsManager;
	}

	public long getMuted() {
		return muted;
	}

	public Notes getNotes() {
		return notesL;
	}

	public int getOverloadDelay() {
		return overloadDelay;
	}

	public List<String> getOwnedObjectManagerKeys() {
		if (ownedObjectsManagerKeys == null) // temporary
			ownedObjectsManagerKeys = new LinkedList<String>();
		return ownedObjectsManagerKeys;
	}

	public WorldPacketsEncoder getPackets() {
		return session.getWorldPackets();
	}

	public long getPacketsDecoderPing() {
		return packetsDecoderPing;
	}

	public boolean nearDummy() {
		return getX() >= 2128 && getY() >= 5518 && getX() <= 2158 && getY() <= 5551;
	}

	public String getPassword() {
		return password;
	}

	public int getPestControlGames() {
		return pestControlGames;
	}

	public int getPestPoints() {
		return pestPoints;
	}

	/**
	 * Gets the pet.
	 * 
	 * @return The pet.
	 */
	public Pet getPet() {
		return pet;
	}

	/**
	 * Gets the petManager.
	 * 
	 * @return The petManager.
	 */
	public PetManager getPetManager() {
		return petManager;
	}

	public int getPkPoints() {
		return pkPoints;
	}

	public long getPoisonImmune() {
		return poisonImmune;
	}

	public long getPolDelay() {
		return polDelay;
	}

	public long getBloodDelay() {
		return bloodDelay;
	}

	public long getPotDelay() {
		return potDelay;
	}

	public int[] getPouches() {
		return pouches;
	}

	public Prayer getPrayer() {
		return prayer;
	}

	public long getPrayerDelay() {
		Long teleblock = (Long) getTemporaryAttributtes().get("PrayerBlocked");
		if (teleblock == null)
			return 0;
		return teleblock;
	}

	public PriceCheckManager getPriceCheckManager() {
		return priceCheckManager;
	}

	public int getPrivateChatSetup() {
		return privateChatSetup;
	}

	public int getPublicStatus() {
		return publicStatus;
	}

	public QuestManager getQuestManager() {
		return questManager;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0.6;
	}

	public String getRegisteredMac() {
		return registeredMac;
	}

	public int getRights() {
		return rights;
	}

	public byte getRunEnergy() {
		return runEnergy;
	}

	/**
	 * @return the runeSpanPoint
	 */
	public int getRuneSpanPoints() {
		return runeSpanPoints;
	}

	public Session getSession() {
		return session;
	}

	@Override
	public int getSize() {
		return getGlobalPlayerUpdater().getSize();
	}

	public Skills getSkills() {
		return skills;
	}

	public StoreManager getStore() {
		return store;
	}

	public int getSkullId() {
		return skullId;
	}

	public int getSlayerPoints() {
		return slayerPoints;
	}

	public int getSpecRestoreTimer() {
		return specRestoreTimer;
	}

	public int getSpins() {
		return spins;
	}

	public void setSpins(int spins) {
		this.spins = spins;
	}

	public int getSummoningLeftClickOption() {
		return summoningLeftClickOption;
	}

	public List<Integer> getSwitchItemCache() {
		return switchItemCache;
	}

	/**
	 * @return the task
	 */
	public SlayerTask getTask() {
		return task;
	}

	public long getTeleBlockDelay() {
		Long teleblock = (Long) getTemporaryAttributtes().get("TeleBlocked");
		if (teleblock == null)
			return 0;
		return teleblock;
	}

	public int getTemporaryMoveType() {
		return temporaryMovementType;
	}

	public void setTemporaryMoveType(int temporaryMovementType) {
		this.temporaryMovementType = temporaryMovementType;
	}

	public Trade getTrade() {
		return trade;
	}

	public int getTradeStatus() {
		return tradeStatus;
	}

	public String getUsername() {
		return username;
	}

	public int getVecnaTimer() {
		return vecnaTimer;
	}

	public int getVotePoints() {
		return votePoints;
	}

	public String getYellColor() {
		return yellColor;
	}

	public long getYellDelay() {
		return yellDelay;
	}

	@Override
	public void handleIngoingHit(final Hit hit) {

		/** Blood necklaces */
		if ((getEquipment().getAmuletId() == 32694 || getEquipment().getAmuletId() == 32697
				|| getEquipment().getAmuletId() == 32700 || getEquipment().getAmuletId() == 32703) && bloodDelay == 0) {
			if (hit.getLook() == HitLook.POISON_DAMAGE || hit.getLook() == HitLook.REFLECTED_DAMAGE
					|| this.inDungeoneering)
				return;
			int healCap = 0;
			Entity source = hit.getSource();
			if (source == null || source instanceof Player)
				return;

			PlayerCombat pc = new PlayerCombat(source);
			Entity[] targets = pc.getMultiAttackTargets(this, 2, 8);

			if (targets.length <= 1) {
				int bloodRoll = Utils.random(50, 125);
				source.applyHit(new Hit(this, bloodRoll, HitLook.REFLECTED_DAMAGE));
				if (healCap < 350)
					healCap += bloodNeckHeal(healCap, bloodRoll);
			} else {
				for (Entity target : targets) {
					if (target instanceof Player)
						return;
					if (healCap < 350) {
						int bloodRoll = Utils.random(50, 125);
						target.applyHit(new Hit(this, bloodRoll, HitLook.REFLECTED_DAMAGE));
						healCap = bloodNeckHeal(healCap, bloodRoll);
					}
				}
			}
			sendMessage(Colors.green + "Your blood necklace heals you for " + healCap + " hitpoints!");
			setBloodDelay(28);
		}
		if (hit.getLook() != HitLook.MELEE_DAMAGE && hit.getLook() != HitLook.RANGE_DAMAGE
				&& hit.getLook() != HitLook.MAGIC_DAMAGE)
			return;
		if (invulnerable) {
			hit.setDamage(0);
			return;
		}
		if (auraManager.usingPenance()) {
			int amount = (int) (hit.getDamage() * 0.2);
			if (amount > 0)
				prayer.restorePrayer(amount);
		}
		Entity source = hit.getSource();
		if (source == null)
			return;
		if (polDelay > Utils.currentTimeMillis())
			hit.setDamage((int) (hit.getDamage() * 0.5));
		if (prayer.hasPrayersOn() && hit.getDamage() != 0) {
			if (hit.getLook() == HitLook.MAGIC_DAMAGE) {
				if (prayer.usingPrayer(0, 17))
					hit.setDamage((int) (hit.getDamage() * source.getMagePrayerMultiplier()));
				else if (prayer.usingPrayer(1, 7)) {
					int deflectedDamage = source instanceof Nex ? 0 : (int) (hit.getDamage() * 0.1);
					hit.setDamage((int) (hit.getDamage() * source.getMagePrayerMultiplier()));
					if (deflectedDamage > 0) {
						source.applyHit(new Hit(this, deflectedDamage, HitLook.REFLECTED_DAMAGE));
						setNextGraphics(new Graphics(2228));
						setNextAnimation(new Animation(12573));
					}
				}
			} else if (hit.getLook() == HitLook.RANGE_DAMAGE) {
				if (prayer.usingPrayer(0, 18))
					hit.setDamage((int) (hit.getDamage() * source.getRangePrayerMultiplier()));
				else if (prayer.usingPrayer(1, 8)) {
					int deflectedDamage = source instanceof Nex ? 0 : (int) (hit.getDamage() * 0.1);
					hit.setDamage((int) (hit.getDamage() * source.getRangePrayerMultiplier()));
					if (deflectedDamage > 0) {
						source.applyHit(new Hit(this, deflectedDamage, HitLook.REFLECTED_DAMAGE));
						setNextGraphics(new Graphics(2229));
						setNextAnimation(new Animation(12573));
					}
				}
			} else if (hit.getLook() == HitLook.MELEE_DAMAGE) {
				if (prayer.usingPrayer(0, 19))
					hit.setDamage((int) (hit.getDamage() * source.getMeleePrayerMultiplier()));
				else if (prayer.usingPrayer(1, 9)) {
					int deflectedDamage = source instanceof Nex ? 0 : (int) (hit.getDamage() * 0.1);
					hit.setDamage((int) (hit.getDamage() * source.getMeleePrayerMultiplier()));
					if (deflectedDamage > 0) {
						source.applyHit(new Hit(this, deflectedDamage, HitLook.REFLECTED_DAMAGE));
						setNextGraphics(new Graphics(2230));
						setNextAnimation(new Animation(12573));
					}
				}
			}
		}
		if (hit.getDamage() >= 200) {
			if (hit.getLook() == HitLook.MELEE_DAMAGE) {
				int reducedDamage = hit.getDamage()
						* combatDefinitions.getBonuses()[CombatDefinitions.ABSORVE_MELEE_BONUS] / 100;
				if (reducedDamage > 0) {
					hit.setDamage(hit.getDamage() - reducedDamage);
					hit.setSoaking(new Hit(source, reducedDamage, HitLook.ABSORB_DAMAGE));
				}
			} else if (hit.getLook() == HitLook.RANGE_DAMAGE) {
				int reducedDamage = hit.getDamage()
						* combatDefinitions.getBonuses()[CombatDefinitions.ABSORVE_RANGE_BONUS] / 100;
				if (reducedDamage > 0) {
					hit.setDamage(hit.getDamage() - reducedDamage);
					hit.setSoaking(new Hit(source, reducedDamage, HitLook.ABSORB_DAMAGE));
				}
			} else if (hit.getLook() == HitLook.MAGIC_DAMAGE) {
				int reducedDamage = hit.getDamage()
						* combatDefinitions.getBonuses()[CombatDefinitions.ABSORVE_MAGE_BONUS] / 100;
				if (reducedDamage > 0) {
					hit.setDamage(hit.getDamage() - reducedDamage);
					hit.setSoaking(new Hit(source, reducedDamage, HitLook.ABSORB_DAMAGE));
				}
			}
		}
		int shieldId = equipment.getShieldId();

		/** Defenders passive */
		if ((Defenders.isDefender(shieldId) || Defenders.isRepriser(shieldId)) && Utils.random(99) <= 14
				&& defenderPassive != true && hit.getDamage() != 0) {
			int reduc = Utils.random(Defenders.getReduc(shieldId, true), Defenders.getReduc(shieldId, false));
			double reduction = (double) (100 - reduc) / 100;
			int damage = (int) (hit.getDamage() * reduction);
			sendMessage(Colors.yellow + "Your defender has reduced the damage by " + (hit.getDamage() - damage)
					+ " points!", true);
			// Logger.log("Reduc: "+reduction+" / o/n damage:
			// "+hit.getDamage()+"/"+damage+" MIN/MAX:
			// "+Defenders.getReduc(shieldId,
			// true)+"/"+Defenders.getReduc(shieldId, false));
			hit.setDamage(damage);
			defenderPassive = true;
		}
		if (shieldId == 13742 || shieldId == 23699 || shieldId == 24884) { // elsyian
			if (Utils.getRandom(100) <= 70)
				hit.setDamage((int) (hit.getDamage() * 0.75));
		}
		if (shieldId == 13740 || shieldId == 23698 || shieldId == 24884) { // divine
			int drain = (int) (Math.ceil(hit.getDamage() * 0.3) / 2);
			if (prayer.getPrayerpoints() >= drain) {
				hit.setDamage((int) (hit.getDamage() * 0.70));
				prayer.drainPrayer(drain);
			}
		}
		if (equipment.getGlovesId() == 31878 && Utils.random(100) >= 80) {
			double damage = hit.getDamage() * Utils.random(0.25, 0.50);
			if (damage >= 150)
				damage = Utils.random(145.0, 151.9);
			source.applyHit(new Hit(this, (int) damage, HitLook.REGULAR_DAMAGE));
		}
		if (castedVeng && hit.getDamage() >= 4) {
			castedVeng = false;
			setNextForceTalk(new ForceTalk("Taste vengeance!"));
			source.applyHit(new Hit(this, (int) (hit.getDamage() * 0.75), HitLook.REGULAR_DAMAGE));
		}
		getControlerManager().processIngoingHit(hit);
		if (source instanceof Player) {
			final Player p2 = (Player) source;
			p2.getControlerManager().processIncommingHit(hit, this);
			if (p2.prayer.hasPrayersOn()) {
				if (p2.prayer.usingPrayer(0, 24)) { // smite
					int drain = hit.getDamage() / 4;
					if (drain > 0)
						prayer.drainPrayer(drain);
				} else {
					if (hit.getDamage() == 0)
						return;
					if (!p2.prayer.isBoostedLeech()) {
						if (hit.getLook() == HitLook.MELEE_DAMAGE) {
							if (p2.prayer.usingPrayer(1, 19)) {
								if (Utils.getRandom(4) == 0) {
									p2.prayer.increaseTurmoilBonus(this);
									p2.prayer.setBoostedLeech(true);
									return;
								}
							} else if (p2.prayer.usingPrayer(1, 1)) { // sap att
								if (Utils.getRandom(4) == 0) {
									if (p2.prayer.reachedMax(0)) {
										p2.getPackets().sendGameMessage(
												"Your opponent has been weakened so much that your sap curse has no effect.",
												true);
									} else {
										p2.prayer.increaseLeechBonus(0);
										p2.getPackets().sendGameMessage(
												"Your curse drains Attack from the enemy, boosting your Attack.", true);
									}
									p2.setNextAnimation(new Animation(12569));
									p2.setNextGraphics(new Graphics(2214));
									p2.prayer.setBoostedLeech(true);
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
								if (p2.prayer.usingPrayer(1, 10)) {
									if (Utils.getRandom(7) == 0) {
										if (p2.prayer.reachedMax(3)) {
											p2.getPackets().sendGameMessage(
													"Your opponent has been weakened so much that your leech curse has no effect.",
													true);
										} else {
											p2.prayer.increaseLeechBonus(3);
											p2.getPackets().sendGameMessage(
													"Your curse drains Attack from the enemy, boosting your Attack.",
													true);
										}
										p2.setNextAnimation(new Animation(12575));
										p2.prayer.setBoostedLeech(true);
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
								if (p2.prayer.usingPrayer(1, 14)) {
									if (Utils.getRandom(7) == 0) {
										if (p2.prayer.reachedMax(7)) {
											p2.getPackets().sendGameMessage(
													"Your opponent has been weakened so much that your leech curse has no effect.",
													true);
										} else {
											p2.prayer.increaseLeechBonus(7);
											p2.getPackets().sendGameMessage(
													"Your curse drains Strength from the enemy, boosting your Strength.",
													true);
										}
										p2.setNextAnimation(new Animation(12575));
										p2.prayer.setBoostedLeech(true);
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
							if (p2.prayer.usingPrayer(1, 2)) { // sap range
								if (Utils.getRandom(4) == 0) {
									if (p2.prayer.reachedMax(1)) {
										p2.getPackets().sendGameMessage(
												"Your opponent has been weakened so much that your sap curse has no effect.",
												true);
									} else {
										p2.prayer.increaseLeechBonus(1);
										p2.getPackets().sendGameMessage(
												"Your curse drains Range from the enemy, boosting your Range.", true);
									}
									p2.setNextAnimation(new Animation(12569));
									p2.setNextGraphics(new Graphics(2217));
									p2.prayer.setBoostedLeech(true);
									World.sendProjectile(p2, this, 2218, 35, 35, 20, 5, 0, 0);
									WorldTasksManager.schedule(new WorldTask() {
										@Override
										public void run() {
											setNextGraphics(new Graphics(2219));
										}
									}, 1);
									return;
								}
							} else if (p2.prayer.usingPrayer(1, 11)) {
								if (Utils.getRandom(7) == 0) {
									if (p2.prayer.reachedMax(4)) {
										p2.getPackets().sendGameMessage(
												"Your opponent has been weakened so much that your leech curse has no effect.",
												true);
									} else {
										p2.prayer.increaseLeechBonus(4);
										p2.getPackets().sendGameMessage(
												"Your curse drains Range from the enemy, boosting your Range.", true);
									}
									p2.setNextAnimation(new Animation(12575));
									p2.prayer.setBoostedLeech(true);
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
							if (p2.prayer.usingPrayer(1, 3)) { // sap mage
								if (Utils.getRandom(4) == 0) {
									if (p2.prayer.reachedMax(2)) {
										p2.getPackets().sendGameMessage(
												"Your opponent has been weakened so much that your sap curse has no effect.",
												true);
									} else {
										p2.prayer.increaseLeechBonus(2);
										p2.getPackets().sendGameMessage(
												"Your curse drains Magic from the enemy, boosting your Magic.", true);
									}
									p2.setNextAnimation(new Animation(12569));
									p2.setNextGraphics(new Graphics(2220));
									p2.prayer.setBoostedLeech(true);
									World.sendProjectile(p2, this, 2221, 35, 35, 20, 5, 0, 0);
									WorldTasksManager.schedule(new WorldTask() {
										@Override
										public void run() {
											setNextGraphics(new Graphics(2222));
										}
									}, 1);
									return;
								}
							} else if (p2.prayer.usingPrayer(1, 12)) {
								if (Utils.getRandom(7) == 0) {
									if (p2.prayer.reachedMax(5)) {
										p2.getPackets().sendGameMessage(
												"Your opponent has been weakened so much that your leech curse has no effect.",
												true);
									} else {
										p2.prayer.increaseLeechBonus(5);
										p2.getPackets().sendGameMessage(
												"Your curse drains Magic from the enemy, boosting your Magic.", true);
									}
									p2.setNextAnimation(new Animation(12575));
									p2.prayer.setBoostedLeech(true);
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

						if (p2.prayer.usingPrayer(1, 13)) { // leech defence
							if (Utils.getRandom(10) == 0) {
								if (p2.prayer.reachedMax(6)) {
									p2.getPackets().sendGameMessage(
											"Your opponent has been weakened so much that your leech curse has no effect.",
											true);
								} else {
									p2.prayer.increaseLeechBonus(6);
									p2.getPackets().sendGameMessage(
											"Your curse drains Defence from the enemy, boosting your Defence.", true);
								}
								p2.setNextAnimation(new Animation(12575));
								p2.prayer.setBoostedLeech(true);
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

						if (p2.prayer.usingPrayer(1, 15)) {
							if (Utils.getRandom(10) == 0) {
								if (getRunEnergy() <= 0) {
									p2.getPackets().sendGameMessage(
											"Your opponent has been weakened so much that your leech curse has no effect.",
											true);
								} else {
									p2.setRunEnergy(p2.getRunEnergy() > 90 ? 100 : p2.getRunEnergy() + 10);
									setRunEnergy(p2.getRunEnergy() > 10 ? getRunEnergy() - 10 : 0);
								}
								p2.setNextAnimation(new Animation(12575));
								p2.prayer.setBoostedLeech(true);
								World.sendProjectile(p2, this, 2256, 35, 35, 20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2258));
									}
								}, 1);
								return;
							}
						}

						if (p2.prayer.usingPrayer(1, 16)) {
							if (Utils.getRandom(10) == 0) {
								if (combatDefinitions.getSpecialAttackPercentage() <= 0) {
									p2.getPackets().sendGameMessage(
											"Your opponent has been weakened so much that your leech curse has no effect.",
											true);
								} else {
									p2.combatDefinitions.restoreSpecialAttack();
									combatDefinitions.decreaseSpecialAttack(10);
								}
								p2.setNextAnimation(new Animation(12575));
								p2.prayer.setBoostedLeech(true);
								World.sendProjectile(p2, this, 2252, 35, 35, 20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2254));
									}
								}, 1);
								return;
							}
						}

						if (p2.prayer.usingPrayer(1, 4)) { // sap spec
							if (Utils.getRandom(10) == 0) {
								p2.setNextAnimation(new Animation(12569));
								p2.setNextGraphics(new Graphics(2223));
								p2.prayer.setBoostedLeech(true);
								if (combatDefinitions.getSpecialAttackPercentage() <= 0) {
									p2.getPackets().sendGameMessage(
											"Your opponent has been weakened so much that your sap curse has no effect.",
											true);
								} else {
									combatDefinitions.decreaseSpecialAttack(10);
								}
								World.sendProjectile(p2, this, 2224, 35, 35, 20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2225));
									}
								}, 1);
								return;
							}
						}
					}
				}
			}
		} else {
			NPC n = (NPC) source;
			if (prayer.usingPrayer(1, 19)) {
				if (Utils.getRandom(4) == 0) {
					prayer.increaseTurmoilBonus(n);
					prayer.setBoostedLeech(true);
				}
			}
			if (n.getId() == 13448)
				sendSoulSplit(hit, n);
		}
	}

	public boolean hasDisabledYell() {
		return yellDisabled;
	}

	public boolean hasDisplayName() {
		return displayName != null;
	}

	public boolean hasInstantSpecial(final int weaponId) {
		switch (weaponId) {
		case 4153:
		case 15486:
		case 22207:
		case 22209:
		case 22211:
		case 22213:
		case 1377:
		case 13472:
		case 35:// Excalibur
		case 8280:
		case 14632:
			return true;
		default:
			return false;
		}
	}

	public boolean hasLargeSceneView() {
		return largeSceneView;
	}

	public boolean hasSkull() {
		return skullDelay > 0;
	}

	public boolean isActive() {
		return active;
	}

	public boolean hasTalkedtoCook() {
		return talkedtoCook;
	}

	@Override
	public void heal(int ammount, int extra) {
		super.heal(ammount, extra);
		refreshHitPoints();
	}

	public void increaseCrucibleHighScore() {
		crucibleHighScore++;
	}

	/**
	 * Increases kill count and all adjustments to kill streaks.
	 * 
	 * @param killed
	 *            The Enemy player killed.
	 */
	public void increaseKillCount(Player killed) {
		if (killed == null || this == null)
			return;
		if (getLastKilled() == null || getLastKilledIP() == null) {
			setLastKilled("Zeus");
			setLastKilledIP("127.0.0.1");
		}
		if (killed.getSession().getIP().equals(getSession().getIP())
				|| (killed.getUsername().equalsIgnoreCase(getLastKilled()))
				|| (killed.getSession().getIP().equals(getLastKilledIP())))
			return;
		killed.deathCount++;
		killStreak += 1;

		if (killStreak > highestKillStreak)
			highestKillStreak = killStreak;

		sendMessage(Colors.red + "<img=10>You are now on a " + killStreak + " kill streak!");

		if (killStreak % 5 == 0 && killStreak > 0) {

			World.sendWorldMessage(Colors.red + "<img=11>" + getDisplayName() + " is on a <col=ff0000>" + killStreak
					+ "</col>" + Colors.red + " killstreak. " + "Their highest streak is <col=ff0000>"
					+ highestKillStreak + "</col>.", false);
		}

		if (killStreak >= 5) {
			int streakPoints = killStreakPoints;
			int totalstreakPoints = totalkillStreakPoints;
			setTotalKillStreakPoints(getTotalKillStreakPoints() + 1);
			streakPoints = 1;
			if (isLegendaryDonator()) {
				setTotalKillStreakPoints(getTotalKillStreakPoints() + 1);
				streakPoints = 2;
			}

			sendMessage(Colors.blue + "You have reached a milestone killstreak and have been rewared with "
					+ streakPoints + " killstreak Point(s)");
			sendMessage(Colors.blue + "You now have " + totalstreakPoints + " killstreak points!");
		}

		PkRank.checkRank(killed);

		killCount++;

		sendMessage(Colors.red + "You have killed " + killed.getDisplayName() + ", " + "you now have " + killCount
				+ " kills.");

		PkRank.checkRank(this);

		addPoints();

	}

	public int getPkPointReward() {
		return pkPointReward;
	}

	public int setPkPointReward(int PkPointReward) {
		return this.pkPointReward = PkPointReward;
	}

	public int getPointsHad() {
		return pointsHad;
	}

	public int setPointsHad(int PointsHad) {
		return this.pointsHad = PointsHad;
	}

	public void addPoints() {
		if (getPkPointReward() <= 500000) {
			setPointsHad(getPkPoints());
			setPkPoints(getPkPoints() + 10 + Utils.random(10));
			int pointsgiven = getPkPoints() - pointsHad;
			sendMessage("You have received " + pointsgiven + " Pk Points");
		}
		if (getPkPointReward() >= 500000 && getPkPointReward() <= 999999) {
			setPointsHad(getPkPoints());
			setPkPoints(getPkPoints() + 10 + Utils.random(25));
			int pointsgiven = getPkPoints() - pointsHad;
			sendMessage("You have received " + pointsgiven + " Pk Points");
		}
		if (getPkPointReward() >= 1000000 && getPkPointReward() <= 4999999) {
			setPkPoints(getPkPoints() + 10 + Utils.random(50));
			setPointsHad(getPkPoints());
			int pointsgiven = getPkPoints() - pointsHad;
			sendMessage("You have received " + pointsgiven + " Pk Points");
		}
		if (getPkPointReward() >= 5000000) {
			setPointsHad(getPkPoints());
			setPkPoints(getPkPoints() + 10 + Utils.random(90));
			int pointsgiven = getPkPoints() - pointsHad;
			sendMessage("You have received " + pointsgiven + " Pk Points");
		}
	}

	public void addKill(Player dead, boolean safe) {
		setLastKilled(dead.getUsername());
		setLastKilledIP(dead.getSession().getIP());
		if (dead.getControlerManager().getControler() != null)
			return;
		int risk = safe ? 0 : checkHighestKill(dead);
		setPkPointReward(risk);
	}

	public boolean isApeAtoll() {
		return (getX() >= 2693 && getX() <= 2821 && getY() >= 2693 && getY() <= 2817);
	}

	/**
	 * PvP
	 */
	public int totalpkPoints;

	public BountyHunter bountyHunter;

	public BountyHunter getBountyHunter() {
		return bountyHunter;
	}

	private String lastKilled;

	private String lastKilledIP;

	@SuppressWarnings("unused")
	private double dropRate;

	public String getLastKilled() {
		return lastKilled;
	}

	public String getLastKilledIP() {
		return lastKilledIP;
	}

	public void setLastKilled(String player) {
		lastKilled = player;
	}

	public void setLastKilledIP(String ip) {
		lastKilledIP = ip;
	}

	public boolean isCanPvp() {
		return canPvp;
	}

	public boolean isCantTrade() {
		return cantTrade;
	}

	public boolean isCastVeng() {
		return castedVeng;
	}

	public boolean isCompletedFightCaves() {
		return completedFightCaves;
	}

	public boolean isCompletedFightKiln() {
		return completedFightKiln;
	}

	public boolean isCompletedRfd() {
		return completedRfd;
	}

	public boolean isDonator() {
		return donator || donatorTill > Utils.currentTimeMillis();
	}

	public boolean isEquipDisabled() {
		return disableEquip;
	}

	public boolean isExtremeDonator() {
		return extremeDonator || extremeDonatorTill > Utils.currentTimeMillis();
	}

	public boolean isExtremePermDonator() {
		return extremeDonator;
	}

	public boolean isFilterGame() {
		return filterGame;
	}

	public boolean isForceNextMapLoadRefresh() {
		return forceNextMapLoadRefresh;
	}

	public boolean isHidingWorldMessages() {
		return hideWorldAnnouncements;
	}

	public boolean isInAnimationRoom() {
		return inAnimationRoom;
	}

	public boolean isInDefenderRoom() {
		return isInDefenderRoom;
	}

	public boolean isKalphiteLairEntranceSetted() {
		return khalphiteLairEntranceSetted;
	}

	public boolean isKalphiteLairSetted() {
		return khalphiteLairSetted;
	}

	public boolean isKilledAgrithNaNa() {
		return agrithNaNa;
	}

	public boolean isKilledBork() {
		return killedBork;
	}

	/**
	 * RFD
	 */

	public boolean isKilledCulinaromancer() {
		return culinaromancer;
	}

	public boolean isKilledDessourt() {
		return dessourt;
	}

	public boolean isKilledFlambeed() {
		return flamBeed;
	}

	public boolean isKilledKaramel() {
		return karamel;
	}

	/**
	 * Gets the killedQueenBlackDragon.
	 * 
	 * @return The killedQueenBlackDragon.
	 */
	public boolean isKilledQueenBlackDragon() {
		return killedQueenBlackDragon;
	}

	public boolean isLocked() {
		return lockDelay >= Utils.currentTimeMillis();
	}

	/**
	 * Checks if @this username should have access to all commands.
	 * 
	 * @return true if has access.
	 */
	public boolean isOwner() {
		for (String s : Settings.OWNER) {
			if (getUsername().equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
	}

	public boolean isDev() {
		for (String s : Settings.DEV) {
			if (getUsername().equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
	}

	public boolean isAdmin() {
		for (String s : Settings.ADMIN) {
			if (getUsername().equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
	}

	public boolean isMod() {
		for (String s : Settings.MOD) {
			if (getUsername().equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
	}

	public boolean isSupport() {
		for (String s : Settings.SUPPORT) {
			if (getUsername().equalsIgnoreCase(s) || support) {
				return true;
			}
		}
		return false;
	}

	public boolean canBan() {
		return isOwner() || isDev() || isMod() || isSupport() ? true : false;
	}

	public boolean isPermBanned() {
		return permBanned;
	}

	public boolean isPermMuted() {
		return permMuted;
	}

	public boolean isResting() {
		return resting;
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isSpawnsMode() {
		return spawnsMode;
	}

	public boolean isTalkedWithMarv() {
		return talkedWithMarv;
	}

	public boolean isTalkedWithVannaka() {
		return talkedWithVannaka;
	}

	public boolean isUpdateMovementType() {
		return updateMovementType;
	}

	public boolean isUsingReportOption() {
		return reportOption;
	}

	public boolean isWonFightPits() {
		return wonFightPits;
	}

	public boolean isXpLocked() {
		return xpLocked;
	}

	public boolean isYellOff() {
		return yellOff;
	}

	public void kickPlayerFromFriendsChannel(String name) {
		if (currentFriendChat == null)
			return;
		currentFriendChat.kickPlayerFromChat(this, name);
	}

	@Override
	public void loadMapRegions() {
		boolean wasAtDynamicRegion = isAtDynamicRegion();
		super.loadMapRegions();
		clientLoadedMapRegion = false;
		if (isAtDynamicRegion()) {
			getPackets().sendDynamicMapRegion(!isActive());
			// if (!wasAtDynamicRegion)
			localNPCUpdate.reset();
		} else {
			getPackets().sendMapRegion();
			if (wasAtDynamicRegion)
				localNPCUpdate.reset();
		}
		forceNextMapLoadRefresh = false;
	}

	public void lock() {
		lockDelay = Long.MAX_VALUE;
	}

	public void lock(long time) {
		lockDelay = Utils.currentTimeMillis() + (time * 600);
	}

	/**
	 * Logs the player out.
	 * 
	 * @param lobby
	 *            If we're logging out to the lobby.
	 */
	public void logout(boolean force) {
		if (!force) {
			if (!isRunning())
				return;
			if (isUnderCombat(10)) {
				sendMessage("You can't log out until 10 seconds after the end of combat.");
				return;
			}
			if (getEmotesManager().isDoingEmote()) {
				sendMessage("You can't log out while performing an emote.");
				return;
			}
			if (isLocked()) {
				sendMessage("You can't log out while performing an action.");
				return;
			}
		}
		getPackets().sendLogout();
		setRunning(false);
	}

	@SuppressWarnings("deprecation")
	public void makeDonator(int months) {
		if (donatorTill < Utils.currentTimeMillis())
			donatorTill = Utils.currentTimeMillis();
		Date date = new Date(donatorTill);
		date.setMonth(date.getMonth() + months);
		donatorTill = date.getTime();
	}

	@Override
	public boolean needMasksUpdate() {
		return super.needMasksUpdate() || temporaryMovementType != -1 || isUpdateMovementType();
	}

	public void performInstantSpecial(final int weaponId) {
		int specAmt = PlayerCombat.getSpecialAmmount(weaponId);
		if (combatDefinitions.hasRingOfVigour())
			specAmt *= 0.9;
		if (combatDefinitions.getSpecialAttackPercentage() < specAmt) {
			sendMessage("You don't have enough power left.");
			combatDefinitions.decreaseSpecialAttack(0);
			return;
		}
		if (getSwitchItemCache().size() > 0) {
			ButtonHandler.submitSpecialRequest(this);
			return;
		}
		if (!isUnderCombat()) // cuz of sheating
			PlayerCombat.addAttackingDelay(this);
		switch (weaponId) {
		case 4153:
			combatDefinitions.setInstantAttack(true);
			combatDefinitions.switchUsingSpecialAttack();
			Entity target = (Entity) getTemporaryAttributtes().get("last_target");
			if (target != null && target.getTemporaryAttributtes().get("last_attacker") == this) {
				if (!(getActionManager().getAction() instanceof PlayerCombat)
						|| ((PlayerCombat) getActionManager().getAction()).getTarget() != target) {
					getActionManager().setAction(new PlayerCombat(target));
				}
			}
			break;
		case 1377:
		case 13472:
			setNextAnimation(new Animation(1056));
			setNextGraphics(new Graphics(246));
			setNextForceTalk(new ForceTalk("Raarrrrrgggggghhhhhhh!"));
			int defence = (int) (skills.getLevelForXp(Skills.DEFENCE) * 0.90D);
			int attack = (int) (skills.getLevelForXp(Skills.ATTACK) * 0.90D);
			int range = (int) (skills.getLevelForXp(Skills.RANGE) * 0.90D);
			int magic = (int) (skills.getLevelForXp(Skills.MAGIC) * 0.90D);
			int strength = (int) (skills.getLevelForXp(Skills.STRENGTH) * 1.2D);
			skills.set(Skills.DEFENCE, defence);
			skills.set(Skills.ATTACK, attack);
			skills.set(Skills.RANGE, range);
			skills.set(Skills.MAGIC, magic);
			skills.set(Skills.STRENGTH, strength);
			combatDefinitions.decreaseSpecialAttack(specAmt);
			break;
		case 35:// Excalibur
		case 8280:
		case 14632:
			setNextAnimation(new Animation(1168));
			setNextGraphics(new Graphics(247));
			final boolean enhanced = weaponId == 14632;
			skills.set(Skills.DEFENCE, enhanced ? (int) (skills.getLevelForXp(Skills.DEFENCE) * 1.15D)
					: (skills.getLevel(Skills.DEFENCE) + 8));
			WorldTasksManager.schedule(new WorldTask() {
				int count = 5;

				@Override
				public void run() {
					if (isDead() || hasFinished() || getHitpoints() >= getMaxHitpoints()) {
						stop();
						return;
					}
					heal(enhanced ? 80 : 40);
					if (count-- == 0) {
						stop();
						return;
					}
				}
			}, 4, 2);
			combatDefinitions.decreaseSpecialAttack(specAmt);
			break;
		case 15486:
		case 22207:
		case 22209:
		case 22211:
		case 22213:
			setNextAnimation(new Animation(12804));
			setNextGraphics(new Graphics(2319));// 2320
			setNextGraphics(new Graphics(2321));
			addPolDelay(60000);
			combatDefinitions.decreaseSpecialAttack(specAmt);
			break;
		}
	}

	@Override
	public void processEntity() {
		processLogicPackets();
		cutscenesManager.process();
		if (routeEvent != null && routeEvent.processEvent(this))
			routeEvent = null;
		super.processEntity();
		charges.process();
		auraManager.process();
		actionManager.process();
		dayOfWeekManager.process();
		elderTreeManager.process();
		prayer.processPrayer();
		controlerManager.process();
		if (musicsManager.musicEnded())
			musicsManager.replayMusic();
		if (hasSkull()) {
			skullDelay--;
			if (!hasSkull())
				getGlobalPlayerUpdater().generateAppearenceData();
		}
		if (polDelay != 0 && polDelay <= Utils.currentTimeMillis()) {
			sendMessage("The power of the light fades. Your resistance to melee attacks return to normal.");
			polDelay = 0;
		}
		if (bloodDelay > 0)
			bloodDelay--;

		if (overloadDelay > 0) {
			if (overloadDelay == 1 || isDead()) {
				Pots.resetOverLoadEffect(this);
				return;
			} else if ((overloadDelay - 1) % 25 == 0)
				Pots.applyOverLoadEffect(this);
			overloadDelay--;
		}
		if (prayerRenewalDelay > 0) {
			if (prayerRenewalDelay == 1 || isDead()) {
				sendMessage(Colors.red + "Your prayer renewal effect has run out.");
				prayerRenewalDelay = 0;
				return;
			} else {
				if (prayerRenewalDelay == 50)
					sendMessage(Colors.red + "Your prayer renewal effect will run out in 30 seconds..", true);
				if (!prayer.hasFullPrayerpoints()) {
					getPrayer().restorePrayer(1);
					if ((prayerRenewalDelay - 1) % 25 == 0)
						setNextGraphics(new Graphics(1295));
				}
			}
			prayerRenewalDelay--;
		}

		/**
		 * Prifddinas Thiev timers. If you can think of a better way to handle
		 * this - do tell.
		 */
		if (thievIthell > 0) {
			if (thievIthell == 1 || isDead()) {
				sendMessage("Clan Ithell has forgotten about your pickpocketing.");
				thievIthell = 0;
			} else
				thievIthell--;
		}
		if (thievIorwerth > 0) {
			if (thievIorwerth == 1 || isDead()) {
				sendMessage("Clan Iorwerth has forgotten about your pickpocketing.");
				thievIorwerth = 0;
			} else
				thievIorwerth--;
		}
		if (thievCadarn > 0) {
			if (thievCadarn == 1 || isDead()) {
				sendMessage("Clan Cadarn has forgotten about your pickpocketing.");
				thievCadarn = 0;
			} else
				thievCadarn--;
		}
		if (thievAmlodd > 0) {
			if (thievAmlodd == 1 || isDead()) {
				sendMessage("Clan Amlodd has forgotten about your pickpocketing.");
				thievAmlodd = 0;
			} else
				thievAmlodd--;
		}
		if (thievTrahaearn > 0) {
			if (thievTrahaearn == 1 || isDead()) {
				sendMessage("Clan Trahaearn has forgotten about your pickpocketing.");
				thievTrahaearn = 0;
			} else
				thievTrahaearn--;
		}
		if (thievHefin > 0) {
			if (thievHefin == 1 || isDead()) {
				sendMessage("Clan Hefin has forgotten about your pickpocketing.");
				thievHefin = 0;
			} else
				thievHefin--;
		}
		if (thievCrwys > 0) {
			if (thievCrwys == 1 || isDead()) {
				sendMessage("Clan Crwys has forgotten about your pickpocketing.");
				thievCrwys = 0;
			} else
				thievCrwys--;
		}
		if (thievMeilyr > 0) {
			if (thievMeilyr == 1 || isDead()) {
				sendMessage("Clan Meilyr has forgotten about your pickpocketing.");
				thievMeilyr = 0;
			} else
				thievMeilyr--;
		}
		if (specRestoreTimer > 0)
			specRestoreTimer--;
		if (yellDelay > 0)
			yellDelay--;
		if (lastBonfire > 0) {
			lastBonfire--;
			if (lastBonfire == 500)
				sendMessage("<col=ffff00>The health boost from stoking a bonfire will run out in 5 minutes..", true);
			else if (lastBonfire == 0) {
				sendMessage("<col=ff0000>The health boost you received from stoking a bonfire has ran out.");
				equipment.refreshConfigs(false);
			}
		}
		farmingManager.process();
		if (getInventory().containsItem(10501, 1) || getEquipment().getWeaponId() == 10501)
			getPackets().sendPlayerOption("Pelt", 6, true);
		else
			getPackets().sendPlayerOption("Null", 6, true);

		// LendingManager.processs();

		DivineObject.resetGatherLimit(this);
		getSquealOfFortune().giveDailySpins();

		if (isAFK()) {
			realFinish();
			World.sendWorldMessage("Player [" + getDisplayName() + " (" + getUsername() + ")] has been kicked for AFK.",
					true);
		}

		getCombatDefinitions().processCombatStance();
	}

	@SuppressWarnings("unused")
	// Taken from MX3, the packet settings aren't compatible :/
	public void processProjectiles() {

		for (int regionId : getMapRegionsIds()) {
			Region region = World.getRegion(regionId);
			for (Projectile projectile : region.getProjectiles()) {

				int fromSizeX, fromSizeY;
				if (projectile.getFrom() instanceof Entity)
					fromSizeX = fromSizeY = ((Entity) projectile.getFrom()).getSize();
				else if (projectile.getFrom() instanceof WorldObject) {
					ObjectDefinitions defs = ((WorldObject) projectile.getFrom()).getDefinitions();
					fromSizeX = defs.getSizeX();
					fromSizeY = defs.getSizeY();
				} else
					fromSizeX = fromSizeY = 1;
				int toSizeX, toSizeY;
				if (projectile.getTo() instanceof Entity)
					toSizeX = toSizeY = ((Entity) projectile.getTo()).getSize();
				else if (projectile.getTo() instanceof WorldObject) {
					ObjectDefinitions defs = ((WorldObject) projectile.getTo()).getDefinitions();
					toSizeX = defs.getSizeX();
					toSizeY = defs.getSizeY();
				} else
					toSizeX = toSizeY = 1;

				// getPackets().sendProjectileNew(projectile.getFrom(),
				// fromSizeX, fromSizeY, projectile.getTo(), toSizeX,
				// toSizeY, projectile.getFrom() instanceof Entity ? (Entity)
				// projectile.getFrom() : null,
				// projectile.getTo() instanceof Entity ? (Entity)
				// projectile.getTo() : null,
				// projectile.isAdjustFlyingHeight(),
				// projectile.isAdjustSenderHeight(),
				// projectile.getSenderBodyPart(), projectile.getGraphicId(),
				// projectile.getStartHeight(),
				// projectile.getEndHeight(), projectile.getStartTime(),
				// projectile.getEndTime(),
				// projectile.getSlope(), projectile.getAngle(), 0);
			}
		}
	}

	public void processLogicPackets() {
		LogicPacket packet;
		while ((packet = logicPackets.poll()) != null) {
			WorldPacketsDecoder.decodeLogicPacket(this, packet);
		}
	}

	@Override
	public void processReceivedHits() {
		if (isLocked())
			return;
		super.processReceivedHits();
	}

	public void realFinish() {
		if (hasFinished())
			return;
		stopAll();
		cutscenesManager.logout();
		controlerManager.logout();
		/* ITEM LEDNING */
		Lend lend = LendingManager.getLend(this);
		Lend hasLendedOut = LendingManager.getHasLendedItemsOut(this);
		if (lend != null) {
			if (getTrade().getLendedTime() == 0)
				LendingManager.unLend(lend);
		}
		if (hasLendedOut != null) {
			if (getTrade().getLendedTime() == 0)
				LendingManager.unLend(hasLendedOut);
		}
		if (slayerManager.getSocialPlayer() != null)
			slayerManager.resetSocialGroup(true);
		/* END OF ITEM LEDNING */
		house.finish();
		dungManager.finish();
		// coOpSlayer.handleLogout(this);
		setRunning(false);
		friendsIgnores.sendFriendsMyStatus(false);
		if (currentFriendChat != null)
			currentFriendChat.leaveChat(this, true);
		if (clanManager != null)
			clanManager.disconnect(this, false);
		if (guestClanManager != null)
			guestClanManager.disconnect(this, true);
		if (familiar != null && !familiar.isFinished())
			familiar.dissmissFamiliar(true);
		else if (pet != null)
			pet.finish();
		new Thread(new Highscores(this)).start();
		lastLoggedIn = System.currentTimeMillis();
		setTotalPlayTime(getTotalPlayTime() + (getRecordedPlayTime() - Utils.currentTimeMillis()));
		setTimePlayedWeekly(getTimePlayedWeekly());
		setFinished(true);
		session.setDecoder(-1);
		SerializableFilesManager.savePlayer(this);
		World.updateEntityRegion(this);
		World.removePlayer(this);
		if (Settings.DEBUG)
			Logger.log(this, "Finished Player: " + username);
		Logger.log("Player " + getUsername() + " has logged out, " + "there are " + World.getPlayers().size()
				+ " players on.");
		// PlayersOnlineManager.updatePlayersOnline();
	}

	/**
	 * Gets the Hiscores database table depending on the players gamemode.
	 * 
	 * @return the Table name.
	 */
	private String getTable() {
		/*
		 * if (isEasy()) return "hs_users_easy"; if (isIntermediate()) return
		 * "hs_users_interm"; if (isVeteran()) return "hs_users_vet"; if
		 * (isIronMan()) return "hs_users_iron"; if (isExpert()) return
		 * "hs_users_expert"; return "hs_users_hciron";
		 */
		return "hs_users";
	}

	public void refreshAllowChatEffects() {
		getPackets().sendConfig(171, allowChatEffects ? 0 : 1);
	}

	public void awardDonation(Player player, String id) {
		Donations.donationList(player,id);
	}
	/*
	 * Donation function - webcall to rsps-pay
	 */
	public void completeDonationProcess(Player player, String productId, String price, boolean referral) {
		if (!referral)
			ReferralHandler.processReferralDonation(player, (int) Double.parseDouble(price));
		Donations.donationList(player,productId);
	}

	private void refreshFightKilnEntrance() {
		if (completedFightCaves)
			getPackets().sendConfigByFile(10838, 1);
	}

	public void refreshHitPoints() {
		getPackets().sendConfigByFile(7198, getHitpoints());
	}

	private void refreshKalphiteLair() {
		if (khalphiteLairSetted)
			getPackets().sendConfigByFile(7263, 1);
	}

	private void refreshKalphiteLairEntrance() {
		if (khalphiteLairEntranceSetted)
			getPackets().sendConfigByFile(7262, 1);
	}

	private void refreshLodestoneNetwork() {
		if (lodestone == null || lodestone[9] != true)
			lodestone = new boolean[] { false, false, false, false, false, false, false, false, false, true, false,
					false, false, false, false };
		getPackets().sendConfigByFile(358, lodestone[0] ? 15 : 14);
		getPackets().sendConfigByFile(2448, lodestone[1] ? 190 : 189);
		for (int i = 10900; i < 10913; i++) {
			getPackets().sendConfigByFile(i, lodestone[(i - 10900) + 2] ? 1 : -1);
		}
	}

	public void refreshMoneyPouch() {
		getPackets().sendRunScript(5560, getMoneyPouch().getTotal());
	}

	public void refreshMouseButtons() {
		getPackets().sendConfig(170, mouseButtons ? 0 : 1);
	}

	public void refreshOtherChatsSetup() {
		int value = friendChatSetup << 6;
		getPackets().sendConfig(1438, value);
	}

	public void refreshPrivateChatSetup() {
		getPackets().sendConfig(287, privateChatSetup);
	}

	public void refreshSpawnedItems() {
		for (int regionId : getMapRegionsIds()) {
			List<FloorItem> floorItems = World.getRegion(regionId).getGroundItems();
			if (floorItems == null)
				continue;
			for (FloorItem item : floorItems) {
				if (item.isInvisible() && (item.hasOwner() && !getUsername().equals(item.getOwner()))
						|| item.getTile().getPlane() != getPlane() || !getUsername().equals(item.getOwner())
								&& (!ItemConstants.isTradeable(item) && !item.isForever()))
					continue;
				getPackets().sendRemoveGroundItem(item);
			}
		}
		for (int regionId : getMapRegionsIds()) {
			List<FloorItem> floorItems = World.getRegion(regionId).getGroundItems();
			if (floorItems == null)
				continue;
			for (FloorItem item : floorItems) {
				if ((item.isInvisible()) && (item.hasOwner() && !getUsername().equals(item.getOwner()))
						|| item.getTile().getPlane() != getPlane() || !getUsername().equals(item.getOwner())
								&& (!ItemConstants.isTradeable(item) && !item.isForever()))
					continue;
				getPackets().sendGroundItem(item);
			}
		}
	}

	public void refreshSpawnedObjects() {
		for (int regionId : getMapRegionsIds()) {
			List<WorldObject> spawnedObjects = World.getRegion(regionId).getSpawnedObjects();
			if (spawnedObjects != null) {
				for (WorldObject object : spawnedObjects) {
					if (object.getPlane() == getPlane())
						getPackets().sendSpawnedObject(object);
				}
			}
			List<WorldObject> removedObjects = World.getRegion(regionId).getRemovedOriginalObjects();
			if (removedObjects != null) {
				for (WorldObject object : removedObjects)
					if (object.getPlane() == getPlane())
						getPackets().sendDestroyObject(object);
			}
		}
	}

	@Override
	public void removeHitpoints(Hit hit) {
		super.removeHitpoints(hit);
		refreshHitPoints();
	}

	public void removeSkull() {
		skullDelay = -1;
		getGlobalPlayerUpdater().generateAppearenceData();
	}

	@Override
	public void reset() {
		reset(true);
	}

	@Override
	public void reset(boolean attributes) {
		super.reset(attributes);
		hintIconsManager.removeAll();
		skills.restoreSkills();
		combatDefinitions.resetSpecialAttack();
		prayer.reset();
		combatDefinitions.resetSpells(true);
		resting = false;
		skullDelay = 0;
		foodDelay = 0;
		cantWalk = false;
		potDelay = 0;
		karamDelay = 0;
		poisonImmune = 0;
		fireImmune = 0;
		superAntiFire = 0;
		prayerRenewalDelay = 0;
		castedVeng = false;
		lastBonfire = 0;
		if (getOverloadDelay() > 0)
			Pots.resetOverLoadEffect(this);
		setRunEnergy(100);
		removeDamage(this);
		getEquipment().refreshConfigs(false);
		refreshHitPoints();
		getGlobalPlayerUpdater().generateAppearenceData();
	}

	public void resetBarrows() {
		hiddenBrother = -1;
		killedBarrowBrothers = new boolean[7]; // includes new bro for future
		// use
		barrowsKillCount = 0;
	}

	@Override
	public void resetMasks() {
		super.resetMasks();
		temporaryMovementType = -1;
		setUpdateMovementType(false);
		if (!clientHasLoadedMapRegion()) {
			// load objects and items here
			setClientHasLoadedMapRegion();
			refreshSpawnedObjects();
			refreshSpawnedItems();
		}
	}

	@Override
	public boolean restoreHitPoints() {
		boolean update = super.restoreHitPoints();
		if (update) {
			if (prayer.usingPrayer(0, 9))
				super.restoreHitPoints();
			if (resting)
				super.restoreHitPoints();
			refreshHitPoints();
		}
		return update;
	}

	public void restoreRunEnergy() {
		if (runEnergy == 40)
			setRun(true);
		if (getNextRunDirection() != -1 || runEnergy >= 100)
			return;
		runEnergy++;
		if (runEnergy > 100)
			runEnergy = 100;
		getPackets().sendRunEnergy();

	}

	@Override
	public void sendDeath(final Entity source) {
		if (prayer.hasPrayersOn() && getTemporaryAttributtes().get("startedDuel") != Boolean.TRUE) {
			if (prayer.usingPrayer(0, 22)) {
				setNextGraphics(new Graphics(437));
				final Player target = this;
				if (isAtMultiArea()) {
					for (int regionId : getMapRegionsIds()) {
						List<Integer> playersIndexes = World.getRegion(regionId).getPlayerIndexes();
						if (playersIndexes != null) {
							for (int playerIndex : playersIndexes) {
								Player player = World.getPlayers().get(playerIndex);
								if (player == null || !player.isActive() || player.isDead() || player.hasFinished()
										|| !player.withinDistance(this, 1) || !player.isCanPvp()
										|| !target.getControlerManager().canHit(player))
									continue;
								player.applyHit(new Hit(target,
										Utils.getRandom((int) (skills.getLevelForXp(Skills.PRAYER) * 2.5)),
										HitLook.REGULAR_DAMAGE));
							}
						}
						List<Integer> npcsIndexes = World.getRegion(regionId).getNPCsIndexes();
						if (npcsIndexes != null) {
							for (int npcIndex : npcsIndexes) {
								NPC npc = World.getNPCs().get(npcIndex);
								if (npc == null || npc.isDead() || npc.hasFinished() || !npc.withinDistance(this, 1)
										|| !npc.getDefinitions().hasAttackOption()
										|| !target.getControlerManager().canHit(npc))
									continue;
								npc.applyHit(new Hit(target,
										Utils.getRandom((int) (skills.getLevelForXp(Skills.PRAYER) * 2.5)),
										HitLook.REGULAR_DAMAGE));
							}
						}
					}
				} else {
					if (source != null && source != this && !source.isDead() && !source.hasFinished()
							&& source.withinDistance(this, 1))
						source.applyHit(
								new Hit(target, Utils.getRandom((int) (skills.getLevelForXp(Skills.PRAYER) * 2.5)),
										HitLook.REGULAR_DAMAGE));
				}
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX() - 1, target.getY(), target.getPlane()));
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX() + 1, target.getY(), target.getPlane()));
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX(), target.getY() - 1, target.getPlane()));
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX(), target.getY() + 1, target.getPlane()));
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX() - 1, target.getY() - 1, target.getPlane()));
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX() - 1, target.getY() + 1, target.getPlane()));
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX() + 1, target.getY() - 1, target.getPlane()));
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX() + 1, target.getY() + 1, target.getPlane()));
					}
				});
			} else if (prayer.usingPrayer(1, 17)) {
				World.sendProjectile(this, new WorldTile(getX() + 2, getY() + 2, getPlane()), 2260, 24, 0, 41, 35, 30,
						0);
				World.sendProjectile(this, new WorldTile(getX() + 2, getY(), getPlane()), 2260, 41, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX() + 2, getY() - 2, getPlane()), 2260, 41, 0, 41, 35, 30,
						0);
				World.sendProjectile(this, new WorldTile(getX() - 2, getY() + 2, getPlane()), 2260, 41, 0, 41, 35, 30,
						0);
				World.sendProjectile(this, new WorldTile(getX() - 2, getY(), getPlane()), 2260, 41, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX() - 2, getY() - 2, getPlane()), 2260, 41, 0, 41, 35, 30,
						0);

				World.sendProjectile(this, new WorldTile(getX(), getY() + 2, getPlane()), 2260, 41, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX(), getY() - 2, getPlane()), 2260, 41, 0, 41, 35, 30, 0);
				final Player target = this;
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						setNextGraphics(new Graphics(2259));

						if (isAtMultiArea()) {
							for (int regionId : getMapRegionsIds()) {
								List<Integer> playersIndexes = World.getRegion(regionId).getPlayerIndexes();
								if (playersIndexes != null) {
									for (int playerIndex : playersIndexes) {
										Player player = World.getPlayers().get(playerIndex);
										if (player == null || !player.isActive() || player.isDead()
												|| player.hasFinished() || !player.isCanPvp()
												|| !player.withinDistance(target, 2)
												|| !target.getControlerManager().canHit(player))
											continue;
										player.applyHit(new Hit(target,
												Utils.getRandom((skills.getLevelForXp(Skills.PRAYER) * 3)),
												HitLook.REGULAR_DAMAGE));
									}
								}
								List<Integer> npcsIndexes = World.getRegion(regionId).getNPCsIndexes();
								if (npcsIndexes != null) {
									for (int npcIndex : npcsIndexes) {
										NPC npc = World.getNPCs().get(npcIndex);
										if (npc == null || npc.isDead() || npc.hasFinished()
												|| !npc.withinDistance(target, 2)
												|| !npc.getDefinitions().hasAttackOption()
												|| !target.getControlerManager().canHit(npc))
											continue;
										npc.applyHit(new Hit(target,
												Utils.getRandom((skills.getLevelForXp(Skills.PRAYER) * 3)),
												HitLook.REGULAR_DAMAGE));
									}
								}
							}
						} else {
							if (source != null && source != target && !source.isDead() && !source.hasFinished()
									&& source.withinDistance(target, 2))
								source.applyHit(
										new Hit(target, Utils.getRandom((skills.getLevelForXp(Skills.PRAYER) * 3)),
												HitLook.REGULAR_DAMAGE));
						}

						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() + 2, getY() + 2, getPlane()));
						World.sendGraphics(target, new Graphics(2260), new WorldTile(getX() + 2, getY(), getPlane()));
						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() + 2, getY() - 2, getPlane()));

						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() - 2, getY() + 2, getPlane()));
						World.sendGraphics(target, new Graphics(2260), new WorldTile(getX() - 2, getY(), getPlane()));
						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() - 2, getY() - 2, getPlane()));

						World.sendGraphics(target, new Graphics(2260), new WorldTile(getX(), getY() + 2, getPlane()));
						World.sendGraphics(target, new Graphics(2260), new WorldTile(getX(), getY() - 2, getPlane()));

						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() + 1, getY() + 1, getPlane()));
						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() + 1, getY() - 1, getPlane()));
						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() - 1, getY() + 1, getPlane()));
						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() - 1, getY() - 1, getPlane()));
					}
				});
			}
		}
		setNextAnimation(new Animation(-1));
		if (!controlerManager.sendDeath())
			return;
		lock();
		stopAll();
		if (familiar != null)
			familiar.sendDeath(this);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(836));
					sendMessage("Oh dear, you have died.");
					if (source instanceof Player) {
						Player killer = (Player) source;
						killer.setAttackedByDelay(4);
					}
				}
				if (loop == 3) {
					setNextAnimation(new Animation(-1));
					getPackets().sendMusicEffect(90);
					controlerManager.startControler("DeathEvent");
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	public Animation getDeathAnimation() {
		// setNextGraphics(new Graphics(Utils.random(2) == 0 ? 4399 : 4398));
		return new Animation(836);
	}

	public void sendDefaultPlayersOptions() {
		getPackets().sendPlayerOption("Challenge", 1, false);
		getPackets().sendPlayerOption("Follow", 2, false);
		getPackets().sendPlayerOption("Trade with", 4, false);
		getPackets().sendPlayerOption("Examine", 5, false);
	}

	public void sendFriendsChannelMessage(String message) {
		if (currentFriendChat == null)
			return;
		currentFriendChat.sendMessage(this, message);
	}

	public void sendFriendsChannelQuickMessage(QuickChatMessage message) {
		if (currentFriendChat == null)
			return;
		currentFriendChat.sendQuickMessage(this, message);
	}

	public void sendItemsOnDeath(Player killer, boolean dropItems) {
		Integer[][] slots = ButtonHandler.getItemSlotsKeptOnDeath(this, true, hasSkull(),
				getPrayer().usingPrayer(0, 10) || getPrayer().usingPrayer(1, 0));
		sendItemsOnDeath(killer, new WorldTile(this), new WorldTile(this), true, slots);
	}

	public void sendItemsOnDeath(Player killer, WorldTile deathTile, WorldTile respawnTile, boolean wilderness,
			Integer[][] slots) {
		if (isHCIronMan()) {
			if (getSkills().getTotalLevel(this) >= 500)
				World.sendWorldMessage(Colors.red + "<shad=000000><img=11>News: " + getDisplayName()
						+ " just died in Hardcore Ironman mode with a skill total of " + getSkills().getTotalLevel(this)
						+ "!", false);
			setPermBanned(true);
			SerializableFilesManager.savePlayer(this);
			getSession().getChannel().close();
			return;
		}
		if (killer == null)
			return;
		if (getUsername().equalsIgnoreCase("") || killer.getUsername().equalsIgnoreCase(""))
			return;
		if (killer.isStaff()) {
			sendMessage("You didn't loose your items on death due to a Developer killing you.");
			World.addGroundItem(new Item(526, 1), deathTile, 60);
			return;
		}
		if (isStaff()) {
			sendMessage("You didn't loose your items on death due to being an Administrator.");
			World.addGroundItem(new Item(526, 1), deathTile, 60);
			return;
		}
		/*
		 * if (killer.getCurrentMac().equalsIgnoreCase(getCurrentMac())) {
		 * Logger.log("Killer: " + getUsername() + " killed " +
		 * killer.getUsername() + " on same computer."); World.addGroundItem(new
		 * Item(526, 1), deathTile, 60); return; } if
		 * (killer.getSession().getIP().equalsIgnoreCase(getSession().getIP()))
		 * { Logger.log("Killer: " + getUsername() + " killed " +
		 * killer.getUsername() + " on same computer."); World.addGroundItem(new
		 * Item(526, 1), deathTile, 60); return; }
		 */
		charges.die();
		auraManager.removeAura();
		Item[][] items = ButtonHandler.getItemsKeptOnDeath(this, slots);
		inventory.reset();
		equipment.reset();
		getGlobalPlayerUpdater().generateAppearenceData();
		for (Item item : items[0]) {
			if (!ItemConstants.keptOnDeath(item)) {
				if (killer.getControlerManager().getControler() == null
						|| !(killer.getControlerManager().getControler() instanceof InstancedPVPControler))
					World.addGroundItem(item, deathTile, this, true, 60);
			} else
				inventory.addItem(item.getId(), item.getAmount());
		}
		World.addGroundItem(new Item(526, 1), deathTile, killer == null ? this : killer, true, 60);
		if (items[1].length != 0) {
			for (Item item : items[1]) {
				if (ItemConstants.keptOnDeath(item)) {
					getInventory().addItem(item);
					continue;
				}
				if (ItemConstants.degradeOnDrop(item))
					getCharges().degradeCompletly(item);
				if (ItemConstants.removeAttachedId(item) != -1) {
					if (ItemConstants.removeAttachedId2(item) != -1)
						if (killer.getControlerManager().getControler() == null
								|| !(killer.getControlerManager().getControler() instanceof InstancedPVPControler))
							World.updateGroundItem(new Item(ItemConstants.removeAttachedId2(item), 1), deathTile,
									killer == null || killer.isIronMan() || killer.isHCIronMan() ? this : killer, 60,
									1);
					item.setId(ItemConstants.removeAttachedId(item));
				}
				if (ItemConstants.turnCoins(item) && (isAtWild() || FfaZone.inPvpArea(this))) {
					int price = GrandExchange.getPrice(item.getId()) / 4;
					item.setId(995);
					item.setAmount(price);
				}
				if (killer.getControlerManager().getControler() == null
						|| !(killer.getControlerManager().getControler() instanceof InstancedPVPControler))
					World.updateGroundItem(item, deathTile,
							killer == null || killer.isIronMan() || killer.isHCIronMan() ? this : killer, 60, 1);
			}
		}
	}

	public final boolean isAtWild() {
		return (getX() >= 3011 && getX() <= 3132 && getY() >= 10052 && getY() <= 10175)
				|| (getX() >= 2940 && getX() <= 3395 && getY() >= 3525 && getY() <= 4000)
				|| (getX() >= 3264 && getX() <= 3279 && getY() >= 3279 && getY() <= 3672)
				|| (getX() >= 3158 && getX() <= 3181 && getY() >= 3679 && getY() <= 3697)
				|| (getX() >= 3280 && getX() <= 3183 && getY() >= 3885 && getY() <= 3888)
				|| (getX() >= 3012 && getX() <= 3059 && getY() >= 10303 && getY() <= 10351)
				|| (getX() >= 3060 && getX() <= 3072 && getY() >= 10251 && getY() <= 10263);
	}

	public void sendMessage(String message, boolean filter) {
		getPackets().sendGameMessage(message, filter);
	}

	public void sendMessage(String message) {
		getPackets().sendGameMessage(message);
	}

	public void sendPublicChatMessage(PublicChatMessage message) {
		for (int regionId : getMapRegionsIds()) {
			List<Integer> playersIndexes = World.getRegion(regionId).getPlayerIndexes();
			if (playersIndexes == null)
				continue;
			for (Integer playerIndex : playersIndexes) {
				Player p = World.getPlayers().get(playerIndex);
				if (p == null || !p.isActive() || p.hasFinished()
						|| p.getLocalPlayerUpdate().getLocalPlayers()[getIndex()] == null)
					continue;
				p.getPackets().sendPublicMessage(this, message);
			}
		}
	}

	public void sendRunButtonConfig() {
		getPackets().sendConfig(173, resting ? 3 : getRun() ? 1 : 0);
	}

	public void sendSoulSplit(final Hit hit, final Entity user) {
		final Player target = this;
		if (hit.getDamage() > 0)
			World.sendProjectile(user, this, 2263, 11, 11, 20, 5, 0, 0);
		double heal = 5.0;

		user.heal((int) (hit.getDamage() / heal));
		prayer.drainPrayer(hit.getDamage() / 5);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				setNextGraphics(new Graphics(2264));
				if (hit.getDamage() > 0)
					World.sendProjectile(target, user, 2263, 11, 11, 20, 5, 0, 0);
			}
		}, 0);
	}

	public void sendUnlockedObjectConfigs() {
		refreshKalphiteLairEntrance();
		refreshKalphiteLair();
		refreshLodestoneNetwork();
		refreshFightKilnEntrance();
	}

	public void setBanned(long banned) {
		this.banned = banned;
	}

	public int setBarrowsKillCount(int barrowsKillCount) {
		return this.barrowsKillCount = barrowsKillCount;
	}

	public void incrementBarrowsRunsDone() {
		this.barrowsRunsDone++;
	}

	public void setCanPvp(boolean canPvp) {
		this.canPvp = canPvp;
		getGlobalPlayerUpdater().generateAppearenceData();
		getPackets().sendPlayerOption(canPvp ? "Attack" : "null", 1, true);
		getPackets().sendPlayerUnderNPCPriority(canPvp);
	}

	public void setCantTrade(boolean canTrade) {
		this.cantTrade = canTrade;
	}

	public void setCastVeng(boolean castVeng) {
		this.castedVeng = castVeng;
	}

	public void setClanStatus(int clanStatus) {
		this.clanStatus = clanStatus;
	}

	public void setClientHasLoadedMapRegion() {
		clientLoadedMapRegion = true;
	}

	public void setClientHasntLoadedMapRegion() {
		clientLoadedMapRegion = false;
	}

	public void setCloseInterfacesEvent(Runnable closeInterfacesEvent) {
		this.closeInterfacesEvent = closeInterfacesEvent;
	}

	public void setClueReward(int clueReward) {
	}

	public void setCompletedFightCaves() {
		if (!completedFightCaves) {
			completedFightCaves = true;
			refreshFightKilnEntrance();
		}
	}

	public void setCompletedFightCaves2() {
		completedFightCaves = true;
	}

	public void setCompletedFightKiln() {
		completedFightKiln = true;
	}

	public void setCompletedRfd() {
		completedRfd = true;
	}

	public void setCompletionistCapeCustomized(int[] skillcapeCustomized) {
		this.completionistCapeCustomized = skillcapeCustomized;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}

	public void setCurrentFriendChat(FriendChatsManager currentFriendChat) {
		this.currentFriendChat = currentFriendChat;
	}

	public void setCurrentFriendChatOwner(String currentFriendChatOwner) {
		this.currentFriendChatOwner = currentFriendChatOwner;
	}

	public void setCurrentMac(String currentMac) {
		this.currentMac = currentMac;
	}

	public int setDeathCount(int deathCount) {
		return this.deathCount = deathCount;
	}

	public void setDefenderRoom(boolean isInDefenderRoom) {
		this.isInDefenderRoom = isInDefenderRoom;
	}

	public void setDisableEquip(boolean equip) {
		disableEquip = equip;
	}

	public void setDisplayMode(int displayMode) {
		this.displayMode = displayMode;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setDonator(boolean donator) {
		this.donator = donator;
	}

	public void setEmailAttached(String email) {
	}

	public void setFamiliar(Familiar familiar) {
		this.familiar = familiar;
	}

	public void setFightPitsSkull() {
		skullDelay = Integer.MAX_VALUE;
		skullId = 1;
		getGlobalPlayerUpdater().generateAppearenceData();
	}

	public void setFilterGame(boolean filterGame) {
		this.filterGame = filterGame;
	}

	public void setForceNextMapLoadRefresh(boolean forceNextMapLoadRefresh) {
		this.forceNextMapLoadRefresh = forceNextMapLoadRefresh;
	}

	public void setFriendChatSetup(int friendChatSetup) {
		this.friendChatSetup = friendChatSetup;
	}

	public void setHiddenBrother(int hiddenBrother) {
		this.hiddenBrother = hiddenBrother;
	}

	public void setHideWorldMessages(boolean hideWorldAnnouncements) {
		this.hideWorldAnnouncements = hideWorldAnnouncements;
	}

	public void setHpBoostMultiplier(double hpBoostMultiplier) {
		this.hpBoostMultiplier = hpBoostMultiplier;
	}

	public void setInAnimationRoom(boolean inAnimationRoom) {
		this.inAnimationRoom = inAnimationRoom;
	}

	public void setInvulnerable(boolean invulnerable) {
		this.invulnerable = invulnerable;
	}

	public void setIsInLobby(boolean isInLobby) {
	}

	public void setJailed(long jailed) {
		this.jailed = jailed;
	}

	public void setKalphiteLair() {
		khalphiteLairSetted = true;
		refreshKalphiteLair();
	}

	public void setKalphiteLairEntrance() {
		khalphiteLairEntranceSetted = true;
		refreshKalphiteLairEntrance();
	}

	public int setKillCount(int killCount) {
		return this.killCount = killCount;
	}

	public int setDropRate(double d) {
		return (int) (this.dropRate = d);
	}

	public int setTotalKillStreakPoints(int totalkillStreakPoints) {
		return this.totalkillStreakPoints = totalkillStreakPoints;
	}

	public int setKillStreakPoints(int killStreakPoints) {
		return this.killStreakPoints = killStreakPoints;
	}

	public void setKilledAgrithNaNa(boolean agrithNaNa) {
		this.agrithNaNa = agrithNaNa;
	}

	public void setKilledBork(boolean killedBork) {
		this.killedBork = killedBork;
	}

	public void setKilledCulinaromancer(boolean culinaromancer) {
		this.culinaromancer = culinaromancer;
	}

	public void setKilledDessourt(boolean dessourt) {
		this.dessourt = dessourt;
	}

	public void setKilledFlamBeed(boolean flamBeed) {
		this.flamBeed = flamBeed;
	}

	public void setKilledKaramel(boolean karamel) {
		this.karamel = karamel;
	}

	/**
	 * Sets the killedQueenBlackDragon.
	 * 
	 * @param killedQueenBlackDragon
	 *            The killedQueenBlackDragon to set.
	 */
	public void setKilledQueenBlackDragon(boolean killedQueenBlackDragon) {
		this.killedQueenBlackDragon = killedQueenBlackDragon;
	}

	public void setLargeSceneView(boolean largeSceneView) {
		this.largeSceneView = largeSceneView;
	}

	public void setLastBonfire(int lastBonfire) {
		this.lastBonfire = lastBonfire;
	}

	public void setLastIP(String lastIP) {
		this.lastIP = lastIP;
	}

	public void setLastPublicMessage(long lastPublicMessage) {
		this.lastPublicMessage = lastPublicMessage;
	}

	public void setMaxedCapeCustomized(int[] maxedCapeCustomized) {
		this.maxedCapeCustomized = maxedCapeCustomized;
	}

	public void setMoneyPouchValue(int money) {
		this.money = money;
	}

	public void setMuted(long muted) {
		this.muted = muted;
	}

	public void setOverloadDelay(int overloadDelay) {
		this.overloadDelay = overloadDelay;
	}

	public void setPacketsDecoderPing(long packetsDecoderPing) {
		this.packetsDecoderPing = packetsDecoderPing;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPermBanned(boolean permBanned) {
		this.permBanned = permBanned;
	}

	public void setPermMuted(boolean permMuted) {
		this.permMuted = permMuted;
	}

	public void setPestControlGames(int pestControlGames) {
		this.pestControlGames = pestControlGames;
	}

	public void setPestPoints(int pestPoints) {
		this.pestPoints = pestPoints;
	}

	/**
	 * Sets the pet.
	 * 
	 * @param pet
	 *            The pet to set.
	 */
	public void setPet(Pet pet) {
		this.pet = pet;
	}

	/**
	 * Sets the petManager.
	 * 
	 * @param petManager
	 *            The petManager to set.
	 */
	public void setPetManager(PetManager petManager) {
		this.petManager = petManager;
	}

	public void setPkPoints(int pkPoints) {
		this.pkPoints = pkPoints;
	}

	public void setPolDelay(long delay) {
		this.polDelay = delay;
	}

	public void setBloodDelay(long delay) {
		this.bloodDelay = delay;
	}

	public void setPrayerDelay(long teleDelay) {
		getTemporaryAttributtes().put("PrayerBlocked", teleDelay + Utils.currentTimeMillis());
		prayer.closeAllPrayers();
	}

	public void setPrayerRenewalDelay(int delay) {
		this.prayerRenewalDelay = delay;
	}

	public void setPrivateChatSetup(int privateChatSetup) {
		this.privateChatSetup = privateChatSetup;
	}

	public void setPublicStatus(int publicStatus) {
		this.publicStatus = publicStatus;
	}

	public void setRegisteredMac(String registeredMac) {
		this.registeredMac = registeredMac;
	}

	public void setResting(boolean resting) {
		this.resting = resting;
		sendRunButtonConfig();
	}

	public void setRights(int rights) {
		this.rights = rights;
	}

	@Override
	public void setRun(boolean run) {
		if (run != getRun()) {
			super.setRun(run);
			setUpdateMovementType(true);
			sendRunButtonConfig();
		}
	}

	public void setRunEnergy(int runEnergy) {
		this.runEnergy = (byte) runEnergy;
		getPackets().sendRunEnergy();
	}

	/**
	 * @param runeSpanPoint
	 *            the runeSpanPoint to set
	 */
	public void setRuneSpanPoint(int runeSpanPoints) {
		this.runeSpanPoints = runeSpanPoints;
	}

	public void setRunHidden(boolean run) {
		super.setRun(run);
		setUpdateMovementType(true);
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public int setSkullDelay(int delay) {
		return this.skullDelay = delay;
	}

	public void setSkullId(int skullId) {
		this.skullId = skullId;
	}

	public void setSkullInfiniteDelay(int skullId) {
		skullDelay = Integer.MAX_VALUE;
		this.skullId = skullId;
		getGlobalPlayerUpdater().generateAppearenceData();
	}

	public void setSlayerPoints(int slayerPoints) {
		this.slayerPoints = slayerPoints;
	}

	public void setSpawnsMode(boolean spawnsMode) {
		this.spawnsMode = spawnsMode;
	}

	public void setSpecRestoreTimer(int specRestoreTimer) {
		this.specRestoreTimer = specRestoreTimer;
	}

	public void setSummoningLeftClickOption(int summoningLeftClickOption) {
		this.summoningLeftClickOption = summoningLeftClickOption;
	}

	public void setSwitchItemCache(List<Integer> switchItemCache) {
		this.switchItemCache = switchItemCache;
	}

	public void setTalkedToCook() {
		talkedtoCook = true;
	}

	public void setTalkedWithMarv() {
		talkedWithMarv = true;
	}

	public void setTalkedWithVannaka(boolean talkedWithVannaka) {
		this.talkedWithVannaka = talkedWithVannaka;
	}

	/**
	 * @param task
	 *            the task to set
	 */
	public void setTask(SlayerTask task) {
		this.task = task;
	}

	public void setRTask(Contract rtask) {
		this.Rtask = rtask;
	}

	public void setTeleBlockDelay(long teleDelay) {
		getTemporaryAttributtes().put("TeleBlocked", teleDelay + Utils.currentTimeMillis());
	}

	public void setTradeStatus(int tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public void setUpdateMovementType(boolean updateMovementType) {
		this.updateMovementType = updateMovementType;
	}

	/*
	 * do not use this, only used by pm
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	public void setVecnaTimer(int vecnaTimer) {
		this.vecnaTimer = vecnaTimer;
	}

	public void setVotePoints(int votePoints) {
		this.votePoints = votePoints;
	}

	public void setWildernessSkull() {
		skullDelay = 3000;
		skullId = 0;
		getGlobalPlayerUpdater().generateAppearenceData();
	}

	public void setWonFightPits() {
		wonFightPits = true;
	}

	public void setXpLocked(boolean locked) {
		this.xpLocked = locked;
	}

	public void setYellColor(String yellColor) {
		this.yellColor = yellColor;
	}

	public void setYellDelay(long l) {
		yellDelay = l;
	}

	public void setYellDisabled(boolean yellDisabled) {
		this.yellDisabled = yellDisabled;
	}

	public void setYellOff(boolean yellOff) {
		this.yellOff = yellOff;
	}

	// now that we inited we can start showing game
	public void start() {
		loadMapRegions();
		LoginManager.sendLogin(this);
		if (isDead() || getHitpoints() <= 0)
			sendDeath(null);
		setActive(true);
	}

	public void stopAll() {
		stopAll(true);
	}

	public void stopAll(boolean stopWalk) {
		stopAll(stopWalk, true);
	}

	public void stopAll(boolean stopWalk, boolean stopInterface) {
		stopAll(stopWalk, stopInterface, true);
	}

	// as walk done clientsided
	public void stopAll(boolean stopWalk, boolean stopInterfaces, boolean stopActions) {
		routeEvent = null;
		if (stopInterfaces)
			closeInterfaces();
		if (stopWalk && !cantWalk)
			resetWalkSteps();
		if (stopActions)
			actionManager.forceStop();
		combatDefinitions.resetSpells(false);
	}

	public void switchAllowChatEffects() {
		allowChatEffects = !allowChatEffects;
		refreshAllowChatEffects();
	}

	public void switchMouseButtons() {
		mouseButtons = !mouseButtons;
		refreshMouseButtons();
	}

	public void switchReportOption() {
		reportOption = !reportOption;
		refreshReportOption();
	}

	public void refreshReportOption() {
		getPackets().sendConfig(1056, isUsingReportOption() ? 2 : 0);
	}

	public boolean isToogleLootShare() {
		return toogleLootShare;
	}

	public void disableLootShare() {
		if (isToogleLootShare())
			toogleLootShare();
	}

	public void toogleLootShare() {
		this.toogleLootShare = !toogleLootShare;
		refreshToogleLootShare();
	}

	public void refreshToogleLootShare() {
		// need to force cuz autoactivates when u click on it even if no chat
		VBM.forceSendVarBit(4071, toogleLootShare ? 1 : 0);
	}

	public void toogleRun(boolean update) {
		super.setRun(!getRun());
		setUpdateMovementType(true);
		if (update)
			sendRunButtonConfig();
	}

	public void unlock() {
		lockDelay = 0;
	}

	public void useStairs(int emoteId, final WorldTile dest, int useDelay, int totalDelay) {
		useStairs(emoteId, dest, useDelay, totalDelay, null);
	}

	public void useStairs(int emoteId, final WorldTile dest, int useDelay, int totalDelay, final String message) {
		useStairs(emoteId, dest, useDelay, totalDelay, message, false);
	}

	public void useStairs(int emoteId, final WorldTile dest, int useDelay, int totalDelay, final String message,
			final boolean resetAnimation) {
		stopAll();
		lock(totalDelay);
		if (emoteId != -1)
			setNextAnimation(new Animation(emoteId));
		if (useDelay == 0)
			setNextWorldTile(dest);
		else {
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (isDead())
						return;
					if (resetAnimation)
						setNextAnimation(new Animation(-1));
					setNextWorldTile(dest);
					if (message != null)
						getPackets().sendGameMessage(message);
				}
			}, useDelay - 1);
		}
	}

	public void vecnaTimer(int amount) {
		if (getVecnaTimer() > 0) {
			CoresManager.fastExecutor.schedule(new TimerTask() {
				@Override
				public void run() {
					if (hasFinished())
						cancel();
					if (getVecnaTimer() > 0)
						setVecnaTimer(getVecnaTimer() - 1);
					if (getVecnaTimer() == 0) {
						getPackets()
								.sendGameMessage("<col=FFCC00>Your skull of Vecna has regained its mysterious aura.");
						cancel();
					}
				}
			}, 10, 1);
		}
	}

	public int lendMessage;

	private transient RouteEvent routeEvent;

	public void setRouteEvent(RouteEvent routeEvent) {
		this.routeEvent = routeEvent;
	}

	public ClansManager getClanManager() {
		return clanManager;
	}

	public void setClanManager(ClansManager clanManager) {
		this.clanManager = clanManager;
	}

	public String getClanName() {
		return clanName;
	}

	public void setClanName(String clanName) {
		this.clanName = clanName;
	}

	public boolean isConnectedClanChannel() {
		return connectedClanChannel;
	}

	public void setConnectedClanChannel(boolean connectedClanChannel) {
		this.connectedClanChannel = connectedClanChannel;
	}

	public ClansManager getGuestClanManager() {
		return guestClanManager;
	}

	public void setGuestClanManager(ClansManager guestClanManager) {
		this.guestClanManager = guestClanManager;
	}

	public void sendClanChannelMessage(ChatMessage message) {
		if (clanManager == null)
			return;
		clanManager.sendMessage(this, message);
	}

	public void sendGuestClanChannelMessage(ChatMessage message) {
		if (guestClanManager == null)
			return;
		guestClanManager.sendMessage(this, message);
	}

	public void sendClanChannelQuickMessage(QuickChatMessage message) {
		if (clanManager == null)
			return;
		clanManager.sendQuickMessage(this, message);
	}

	public void sendGuestClanChannelQuickMessage(QuickChatMessage message) {
		if (guestClanManager == null)
			return;
		guestClanManager.sendQuickMessage(this, message);
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	private double[] warriorPoints;

	public double[] getWarriorPoints() {
		return warriorPoints;
	}

	public void setWarriorPoints(int index, double pointsDifference) {
		warriorPoints[index] += pointsDifference;
		if (warriorPoints[index] < 0) {
			Controller controler = getControlerManager().getControler();
			if (controler == null || !(controler instanceof WarriorsGuild))
				return;
			WarriorsGuild guild = (WarriorsGuild) controler;
			guild.inCyclopse = false;
			setNextWorldTile(WarriorsGuild.CYCLOPS_LOBBY);
			warriorPoints[index] = 0;
		} else if (warriorPoints[index] > 65535)
			warriorPoints[index] = 65535;
		refreshWarriorPoints(index);
	}

	public void refreshWarriorPoints(int index) {
		getPackets().sendConfigByFile(index + 8662, (int) warriorPoints[index]);
	}

	public void warriorCheck() {
		if (warriorPoints == null || warriorPoints.length != 6)
			warriorPoints = new double[6];
	}

	public void setExtremeDonator(boolean extremeDonator) {
		this.extremeDonator = extremeDonator;
	}

	public GlobalPlayerUpdater getGlobalPlayerUpdater() {
		return globalPlayerUpdater;
	}

	public void setGlobalPlayerUpdater(GlobalPlayerUpdater globalPlayerUpdater) {
		this.globalPlayerUpdater = globalPlayerUpdater;
	}

	private long thievingDelay;

	public long getThievingDelay() {
		return thievingDelay;
	}

	public void setThievingDelay(long thievingDelay) {
		this.thievingDelay = thievingDelay;
	}

	public Channel getChannel() {
		return channel;
	}

	public boolean isDeveloper() {
		return isOwner() || isDev() ? true : false;
	}

	/**
	 * Custom Game Mode ranks.
	 */
	public boolean veteran, intermediate, easy, ironman, hcironman, expert;

	public boolean isVeteran() {
		return veteran;
	}

	public boolean isIntermediate() {
		return intermediate;
	}

	public boolean isEasy() {
		return easy;
	}

	public boolean isIronMan() {
		return ironman;
	}

	public boolean isHCIronMan() {
		return hcironman;
	}

	public boolean isExpert() {
		return expert;
	}

	public void setVeteran(boolean vet) {
		this.veteran = vet;
	}

	public void setIntermediate(boolean interm) {
		this.intermediate = interm;
	}

	public void setEasy(boolean ez) {
		this.easy = ez;
	}

	public void setIronMan(boolean ironm) {
		this.ironman = ironm;
	}

	public void setHCIronMan(boolean hardcoreim) {
		this.hcironman = hardcoreim;
	}

	public void setExpert(boolean expert) {
		this.expert = expert;
	}

	public void setContract(Contract contract) {
		this.cContracts = contract;
	}

	public Contract getContract() {
		return cContracts;
	}

	/**
	 * New player starter stuff.
	 */
	public boolean hasCompleted, hasLogedIn;

	public void setCompleted() {
		getHintIconsManager().removeUnsavedHintIcon();
		this.hasCompleted = true;
	}

	public boolean hasCompleted() {
		return hasCompleted;
	}

	public void setLogedIn() {
		this.hasLogedIn = true;
	}

	public boolean hasLogedIn() {
		return hasLogedIn;
	}

	/**
	 * Handles a donation made.
	 * 
	 * @param price
	 *            The donation price.
	 */
	public void handleDonation(int price, String perk) {

		try {
			BufferedWriter bf = new BufferedWriter(new FileWriter("data/playersaves/logs/donationLogs.txt", true));
			bf.write("[Player: " + getDisplayName() + ", on " + DateFormat.getDateTimeInstance().format(new Date())
					+ "]: has donated: " + price + "$ for " + perk + ".");
			bf.newLine();
			bf.flush();
			bf.close();
		} catch (IOException ignored) {
			Logger.log("LoggsHandler", "Failed saving Donation Logs...");
		}

		getInterfaceManager().closeChatBoxInterface();
		setMoneySpent(getMoneySpent() + price);
		if (!isStaff())
			setDonationAmountWeekly(getDonationAmountWeekly() + price);
		DonationRank.checkRank(this);
		Donations.HandlePromotion(this,price);
	}

	/**
	 * IRL Money spent.
	 */
	private int moneySpent;

	public void setMoneySpent(int money) {
		this.moneySpent = money;
	}

	public int getMoneySpent() {
		return moneySpent;
	}

	/**
	 * Used to take coins.
	 * 
	 * @param amount
	 *            the Amount to take.
	 * @return if The money has been taken.
	 */
	public boolean takeMoney(int amount) {
		if (!hasMoney(amount)) {
			return false;
		}
		if (amount < 0) {
			return false;
		}
		int inPouch = getMoneyPouch().getTotal();
		int inInventory = getInventory().getNumerOf(995);
		if (inPouch >= amount) {
			getMoneyPouch().removeMoneyMisc(amount);
			return true;
		}
		if (inInventory >= amount) {
			getInventory().deleteItem(new Item(995, amount));
			return true;
		}
		if (inPouch + inInventory >= amount) {
			amount = amount - inPouch;
			getMoneyPouch().removeMoneyMisc(inPouch);
			getInventory().deleteItem(new Item(995, amount));
			return true;
		}
		return false;
	}

	/**
	 * Used to add coins.
	 * 
	 * @param amount
	 *            the Amount to add.
	 * @return if The money has been added.
	 */
	public void addMoney(int amount) {
		if (money + amount < 0) {
			int amountPouch = Integer.MAX_VALUE - money;
			amount = amount - amountPouch;
			if (getInventory().hasFreeSlots() || getInventory().containsItem(995, 1)) {
				int has = getInventory().getNumerOf(995);
				if (has + amount < 0) {
					int amountAdd = Integer.MAX_VALUE - has;
					int toDrop = amount - amountAdd;
					getInventory().addItem(995, amountAdd);
					World.addGroundItem(new Item(995, toDrop), new WorldTile(this), this, true, 60);
					sendMessage(Colors.red + Utils.getFormattedNumber(toDrop)
							+ " coins have been dropped due to insufficient coin inventory space.");
					return;
				}
				getInventory().addItem(995, amount);
				return;
			}
			sendMessage(Colors.red + Utils.getFormattedNumber(amount)
					+ " coins have been dropped due to insufficient coin inventory space.");
			World.addGroundItem(new Item(995, amount), new WorldTile(this), this, true, 60);
			return;
		}
		getMoneyPouch().addMoney(amount, false);
	}

	/**
	 * Used for checking if the player has money.
	 * 
	 * @param amount
	 *            the Amount to check for.
	 * @return if the player has the required amount either in their money pouch
	 *         or their inventory.
	 */
	public boolean hasMoney(int amount) {
		int money = getInventory().getNumerOf(995) + getMoneyPouch().getTotal();
		return money >= amount;
	}

	/**
	 * Varbit manager.
	 */
	public transient VarBitManager VBM;

	public VarBitManager getVarBitManager() {
		return VBM;
	}

	/**
	 * Farming.
	 */
	public FarmingManager farmingManager;

	public FarmingManager getFarmingManager() {
		return farmingManager;
	}

	/**
	 * LodeStones.
	 */
	public boolean[] lodestone;

	public void activateLodeStone(final WorldObject object, final Player p) {
		lock(5);
		WorldTasksManager.schedule(new WorldTask() {
			int count = 0;

			@Override
			public void run() {
				if (count == 0) {
					getPackets().sendCameraPos(Cutscene.getX(p, p.getX() - 6), Cutscene.getY(p, p.getY()), 3000);
					getPackets().sendCameraLook(Cutscene.getX(p, object.getX()), Cutscene.getY(p, object.getY()), 50);
					getPackets().sendGraphics(new Graphics(3019), object);
				}
				if (count == 2) {
					getPackets().sendResetCamera();
					lodestone[object.getId() - 69827] = true;
					refreshLodestoneNetwork();
				}
				if (count == 3) {
					unlock();
					stop();
				}
				count++;
			}
		}, 0, 1);
	}

	/**
	 * Grand Exchange.
	 */
	public GrandExchangeManager geManager;

	public GrandExchangeManager getGEManager() {
		return geManager;
	}

	/**
	 * Gets the XP mode.
	 * 
	 * @return the XP mode.
	 */
	public String getXPMode() {
		if (isExpert())
			return "Expert";
		if (isVeteran())
			return "Veteran";
		if (isIntermediate())
			return "Intermediate";
		if (isEasy())
			return "Easy";
		if (isIronMan())
			return "Ironman";
		if (isHCIronMan())
			return "HC Ironman";
		return "HACKER";
	}

	/**
	 * Gets the Drop rate.
	 * 
	 * @return the Drop rate.
	 */
	public double getDropRate() {
		if (isVeteran())
			return Settings.VET_DROP;
		if (isIntermediate())
			return Settings.INTERM_DROP;
		if (isEasy())
			return Settings.EASY_DROP;
		if (isIronMan())
			return Settings.IRONMAN_DROP;
		if (isHCIronMan())
			return Settings.HCIRONMAN_DROP;
		return 1;
	}

	/**
	 * Checks if the Player has the item.
	 * 
	 * @param item
	 *            The item to check.
	 * @return if has item or not.
	 */
	public boolean hasItem(Item item) {
		if (getInventory().containsItem(item))
			return true;
		if (getEquipment().getItemsContainer().contains(item))
			return true;
		if (getBank().getItem(item.getId()) != null)
			return true;
		return false;
	}

	/**
	 * Gives the Player an item.
	 * 
	 * @param item
	 *            The item to give.
	 */
	public void addItem(Item item) {
		if (!getInventory().hasFreeSlots()
				&& !(item.getDefinitions().isStackable() && getInventory().containsOneItem(item.getId()))) {
			if (!getBank().hasBankSpace())
				World.updateGroundItem(item, this, this, 60, 0);
			else {
				if (item.getDefinitions().isNoted())
					item.setId(item.getDefinitions().getCertId());
				getBank().addItem(item.getId(), item.getAmount(), true);
				sendMessage(item.getName() + " has been added to your bank account.");
			}
		} else
			getInventory().addItem(item);
	}

	/**
	 * Used to display players icon (if any).
	 */
	public String getIcon() {
		if (getRights() == 2)
			return "<img=1>";
		if (getRights() == 1)
			return "<img=0>";
		if (isSupport())
			return "<img=13>";
		if (isHCIronMan())
			return "<img=11>";
		if (isIronMan())
			return "<img=10>";
		if (isExtremeDonator())
			return "<img=9>";
		if (isDonator())
			return "<img=8>";
		return "";
	}

	/**
	 * Completionist Cape requirements.
	 */
	private int oresMined, barsSmelt;

	public void addOresMined() {
		this.oresMined++;
	}

	public int getOresMined() {
		return oresMined;
	}

	public void addBarsSmelt() {
		this.barsSmelt++;
	}

	public int getBarsSmelt() {
		return barsSmelt;
	}

	private int logsChopped, logsBurned;

	public void addLogsChopped() {
		this.logsChopped++;
	}

	public int getLogsChopped() {
		return logsChopped;
	}

	public void addLogsBurned() {
		this.logsBurned++;
	}

	public int getLogsBurned() {
		return logsBurned;
	}

	private int lapsRan;

	public void addLapsRan() {
		this.lapsRan++;
	}

	public int getLapsRan() {
		return lapsRan;
	}

	private int bonesOffered;

	public void addBonesOffered() {
		this.bonesOffered++;
	}

	public int getBonesOffered() {
		return bonesOffered;
	}

	private int potionsMade;

	public void addPotionsMade() {
		this.potionsMade++;
	}

	public int getPotionsMade() {
		return potionsMade;
	}

	private int timesStolen;

	public void addTimesStolen() {
		this.timesStolen++;
	}

	public int getTimesStolen() {
		return timesStolen;
	}

	private int itemsMade;

	public void addItemsMade() {
		this.itemsMade++;
	}

	public int getItemsMade() {
		return itemsMade;
	}

	private int itemsFletched;

	public void addItemsFletched() {
		this.itemsFletched++;
	}

	public int getItemsFletched() {
		return itemsFletched;
	}

	private int creaturesCaught;

	public void addCreaturesCaught() {
		this.creaturesCaught++;
	}

	public int getCreaturesCaught() {
		return creaturesCaught;
	}

	private int fishCaught;

	public void addFishCaught(boolean perk) {
		this.fishCaught += perk ? 2 : 1;
	}

	public int getFishCaught() {
		return fishCaught;
	}

	private int foodCooked;

	public void addFoodCooked() {
		this.foodCooked++;
	}

	public int getFoodCooked() {
		return foodCooked;
	}

	public int produceGathered;

	public void addProduceGathered() {
		this.produceGathered++;
	}

	public int getProduceGathered() {
		return produceGathered;
	}

	public int pouchesMade;

	public void setPouchesMade(int pouches) {
		this.pouchesMade = pouches;
	}

	public void setTimesStolen(int stolen) {
		this.timesStolen = stolen;
	}

	public void setPotionsMade(int potionsMade) {
		this.potionsMade = potionsMade;
	}

	public void setBonesOffered(int bonesOffered) {
		this.bonesOffered = bonesOffered;
	}

	public void setLogsBurned(int logsBurned) {
		this.logsBurned = logsBurned;
	}

	public void setLogsChopped(int logsChopped) {
		this.logsChopped = logsChopped;
	}

	public void setBarsSmelted(int barsSmelted) {
		this.barsSmelt = barsSmelted;
	}

	public void setOresMined(int oresMined) {
		this.oresMined = oresMined;
	}

	public void setItemsFletched(int itemsFletched) {
		this.itemsFletched = itemsFletched;
	}

	public void setFishCaught(int fishCaught) {
		this.fishCaught = fishCaught;
	}

	public void setFoodCooked(int foodCooked) {
		this.foodCooked = foodCooked;
	}

	public void setProduceGathered(int produceGathered) {
		this.produceGathered = produceGathered;
	}

	public void setLapsRan(int lapsRan) {
		this.lapsRan = lapsRan;
	}

	public void setMemoriesCollected(int memoriesCollected) {
		this.memoriesCollected = memoriesCollected;
	}

	public void setRunesMade(int runesMade) {
		this.runesMade = runesMade;
	}

	public void setCreaturesCaught(int creaturesCaught) {
		this.creaturesCaught = creaturesCaught;
	}

	public void setItemsMade(int items) {
		this.itemsMade = items;
	}

	public int getPouchesMade() {
		return pouchesMade;
	}

	private int memoriesCollected;

	public int getMemoriesCollected() {
		return memoriesCollected;
	}

	public void addMemoriesCollected() {
		this.memoriesCollected++;
	}

	private int runesMade;

	public int getRunesMade() {
		return this.runesMade;
	}

	public void addRunesMade(int runes) {
		this.runesMade += runes;
	}

	private boolean max, comp, compT;

	public void setMax(boolean max) {
		this.max = max;
	}

	public boolean isMax() {
		return max;
	}

	public void setComp(boolean comp) {
		this.comp = comp;
	}

	public boolean isComp() {
		return comp;
	}

	public void setCompT(boolean compT) {
		this.compT = compT;
	}

	public boolean isCompT() {
		return compT;
	}

	/**
	 * Toolbelt.
	 */
	public Toolbelt toolBelt;

	public Toolbelt getToolBelt() {
		return toolBelt;
	}

	public ToolbeltNew toolBeltNew;

	public ToolbeltNew getToolBeltNew() {
		return toolBeltNew;
	}

	/**
	 * Player-owned titles.
	 */
	public Titles titles;

	public Titles getTitles() {
		return titles;
	}

	private boolean combinedCloaks;

	public void setCombinedCloaks() {
		this.combinedCloaks = true;
	}

	public boolean hasCombinedCloaks() {
		return combinedCloaks;
	}

	private boolean guthixTitle;

	public void unlockGuthixTitle() {
		this.guthixTitle = true;
	}

	public boolean hasGuthixTitleUnlocked() {
		return guthixTitle;
	}

	/**
	 * Donator Boxes.
	 */
	private int boxesOpened;

	public int getBoxesOpened() {
		return boxesOpened;
	}

	public void incrementBoxesOpened() {
		this.boxesOpened++;
	}

	/**
	 * Crystal chest.
	 */
	private int chestsOpened;

	public int getChestsOpened() {
		return chestsOpened;
	}

	public void incrementChestsOpened() {
		this.chestsOpened++;
	}

	/**
	 * Kill-statistics.
	 */
	int[] killStats = new int[512];

	/**
	 * Increases the statistics.
	 * 
	 * @param name
	 *            The NPC name.
	 */
	public int increaseKillStatistics(String name, boolean add) {
		switch (name.toLowerCase()) {
		case "rock crab":
			if (add)
				killStats[0]++;
			return getKillStatistics(0);
		case "general graardor":
			if (add)
				killStats[1]++;
			return getKillStatistics(1);
		case "k'ril tsutsaroth":
			if (add)
				killStats[2]++;
			return getKillStatistics(2);
		case "kree'arra":
			if (add)
				killStats[3]++;
			return getKillStatistics(3);
		case "commander zilyana":
			if (add)
				killStats[4]++;
			return getKillStatistics(4);
		case "nex":
			if (add)
				killStats[5]++;
			return getKillStatistics(5);
		case "corporeal beast":
			if (add)
				killStats[6]++;
			return getKillStatistics(6);
		case "queen black dragon":
			if (add)
				killStats[7]++;
			return getKillStatistics(7);
		case "king black dragon":
			if (add)
				killStats[8]++;
			return getKillStatistics(8);
		case "bork":
			if (add)
				killStats[9]++;
			return getKillStatistics(9);
		case "chaos elemental":
			if (add)
				killStats[10]++;
			return getKillStatistics(10);
		case "crawling Hand":
			if (add)
				killStats[11] += 1;
			return getKillStatistics(11);
		case "abyssal demon":
			if (add)
				killStats[12] += 1;
			return getKillStatistics(12);
		case "ice strykewyrm":
			if (add)
				killStats[13] += 1;
			return getKillStatistics(13);
		case "jungle strykewyrm":
			if (add)
				killStats[14] += 1;
			return getKillStatistics(14);
		case "desert strykewyrm":
			if (add)
				killStats[15] += 1;
			return getKillStatistics(15);
		case "nechryael":
			if (add)
				killStats[16] += 1;
			return getKillStatistics(16);
		case "aberrant spectre":
			if (add)
				killStats[17] += 1;
			return getKillStatistics(17);
		case "hellhound":
			if (add)
				killStats[18] += 1;
			return getKillStatistics(18);
		case "mature grotworm":
			if (add)
				killStats[19] += 1;
			return getKillStatistics(19);
		case "tztok-jad":
			if (add)
				killStats[20] += 1;
			return getKillStatistics(20);
		case "greater demon":
			if (add)
				killStats[21] += 1;
			return getKillStatistics(21);
		case "mutated jadinko baby":
			if (add)
				killStats[22] += 1;
			return getKillStatistics(22);
		case "mutated jadinko male":
			if (add)
				killStats[23] += 1;
			return getKillStatistics(23);
		case "mutated jadinko guard":
			if (add)
				killStats[24] += 1;
			return getKillStatistics(24);
		case "blue dragon":
			if (add)
				killStats[25] += 1;
			return getKillStatistics(25);
		case "iron dragon":
			if (add)
				killStats[26] += 1;
			return getKillStatistics(26);
		case "steel dragon":
			if (add)
				killStats[27] += 1;
			return getKillStatistics(27);
		case "frost dragon":
			if (add)
				killStats[28] += 1;
			return getKillStatistics(28);
		case "glacor":
			if (add)
				killStats[29] += 1;
			return getKillStatistics(29);
		case "infernal mage":
			if (add)
				killStats[30] += 1;
			return getKillStatistics(30);
		case "ganodermic beast":
			if (add)
				killStats[31] += 1;
			return getKillStatistics(31);
		case "gargoyle":
			if (add)
				killStats[32] += 1;
			return getKillStatistics(32);
		case "jelly":
			if (add)
				killStats[33] += 1;
			return getKillStatistics(33);
		case "dark beast":
			if (add)
				killStats[34] += 1;
			return getKillStatistics(34);
		case "bloodveld":
			if (add)
				killStats[35] += 1;
			return getKillStatistics(35);
		case "black guard":
			if (add)
				killStats[36] += 1;
			return getKillStatistics(36);
		case "chaos dwarf hand cannoneer":
			if (add)
				killStats[37] += 1;
			return getKillStatistics(37);
		case "chaos dwogre":
			if (add)
				killStats[38] += 1;
			return getKillStatistics(38);
		case "pyrefiend":
			if (add)
				killStats[39] += 1;
			return getKillStatistics(39);
		case "cockatrice":
			if (add)
				killStats[40] += 1;
			return getKillStatistics(40);
		case "brutal green dragon":
		case "green dragon":
			if (add)
				killStats[41] += 1;
			return getKillStatistics(41);
		case "fungal rodent":
			if (add)
				killStats[42] += 1;
			return getKillStatistics(42);
		case "grifolaroo":
			if (add)
				killStats[43] += 1;
			return getKillStatistics(43);
		case "grifolapine":
			if (add)
				killStats[44] += 1;
			return getKillStatistics(44);
		case "mithril dragon":
			if (add)
				killStats[45] += 1;
			return getKillStatistics(45);
		case "bronze dragon":
			if (add)
				killStats[46] += 1;
			return getKillStatistics(46);
		case "moss giant":
			if (add)
				killStats[47] += 1;
			return getKillStatistics(47);
		case "fire giant":
			if (add)
				killStats[48] += 1;
			return getKillStatistics(48);
		case "hill giant":
			if (add)
				killStats[49] += 1;
			return getKillStatistics(49);
		case "turoth":
			if (add)
				killStats[50] += 1;
			return getKillStatistics(50);
		case "basilisk":
			if (add)
				killStats[51] += 1;
			return getKillStatistics(51);
		case "kurask":
			if (add)
				killStats[52] += 1;
			return getKillStatistics(52);
		case "black demon":
			if (add)
				killStats[53] += 1;
			return getKillStatistics(53);
		case "kalphite queen":
			if (add)
				killStats[54] += 1;
			return getKillStatistics(54);
		case "tormented demon":
			if (add)
				killStats[55] += 1;
			return getKillStatistics(55);
		case "baby blue dragon":
			if (add)
				killStats[56] += 1;
			return getKillStatistics(56);
		case "lesser demon":
			if (add)
				killStats[57] += 1;
			return getKillStatistics(57);
		case "skeleton":
			if (add)
				killStats[58] += 1;
			return getKillStatistics(58);
		case "man":
		case "farmer":
		case "woman":
			if (add)
				killStats[59] += 1;
			return getKillStatistics(59);
		case "waterfiend":
			if (add)
				killStats[60] += 1;
			return getKillStatistics(60);
		case "banshee":
			if (add)
				killStats[61] += 1;
			return getKillStatistics(61);
		case "dog":
		case "terror dog":
		case "wild dog":
			if (add)
				killStats[62] += 1;
			return getKillStatistics(62);
		case "cave crawler":
			if (add)
				killStats[63] += 1;
			return getKillStatistics(63);
		case "black dragon":
			if (add)
				killStats[64] += 1;
			return getKillStatistics(64);
		case "chaos druid":
			if (add)
				killStats[65] += 1;
			return getKillStatistics(65);
		case "black knight":
			if (add)
				killStats[66] += 1;
			return getKillStatistics(66);
		case "barrelchest":
			if (add)
				killStats[67] += 1;
			return getKillStatistics(67);
		case "dagannoth supreme":
			if (add)
				killStats[68] += 1;
			return getKillStatistics(68);
		case "dagannoth prime":
			if (add)
				killStats[69] += 1;
			return getKillStatistics(69);
		case "dagannoth rex":
			if (add)
				killStats[70] += 1;
			return getKillStatistics(70);
		case "araxxor":
			if (add)
				killStats[71] += 1;
			return getKillStatistics(71);
		case "vampyre":
			if (add)
				killStats[72] += 1;
			return getKillStatistics(72);
		case "werewolf":
			if (add)
				killStats[73] += 1;
			return getKillStatistics(73);
		case "goblin":
		case "hobgoblin":
			if (add)
				killStats[74] += 1;
			return getKillStatistics(74);
		case "imp":
			if (add)
				killStats[75] += 1;
			return getKillStatistics(75);
		case "icefiend":
			if (add)
				killStats[76] += 1;
			return getKillStatistics(76);
		case "ogre":
			if (add)
				killStats[77] += 1;
			return getKillStatistics(77);
		case "cyclops":
			if (add)
				killStats[78] += 1;
			return getKillStatistics(78);
		case "rorarius":
			if (add)
				killStats[79] += 1;
			return getKillStatistics(79);
		case "gladius":
			if (add)
				killStats[80] += 1;
			return getKillStatistics(80);
		case "capsarius":
			if (add)
				killStats[81] += 1;
			return getKillStatistics(81);
		case "scutarius":
			if (add)
				killStats[82] += 1;
			return getKillStatistics(82);
		case "legio primus":
			if (add)
				killStats[83] += 1;
			return getKillStatistics(83);
		case "legio secundus":
			if (add)
				killStats[84] += 1;
			return getKillStatistics(84);
		case "legio tertius":
			if (add)
				killStats[85] += 1;
			return getKillStatistics(85);
		case "legio quartus":
			if (add)
				killStats[86] += 1;
			return getKillStatistics(86);
		case "legio quintus":
			if (add)
				killStats[87] += 1;
			return getKillStatistics(87);
		case "legio sextus":
			if (add)
				killStats[88] += 1;
			return getKillStatistics(88);
		case "Giant Mole":
			if (add)
				killStats[89] += 1;
			return getKillStatistics(89);
		case "kalphite king":
			if (add)
				killStats[90] += 1;
			return getKillStatistics(90);
		case "ork":
			if (add)
				killStats[91] += 1;
			return getKillStatistics(91);
		case "aviansie":
			if (add)
				killStats[92] += 1;
			return getKillStatistics(92);
		case "vorago":
			if (add)
				killStats[93] += 1;
			return getKillStatistics(93);
		case "adamant dragon":
			if (add)
				killStats[94] += 1;
			return getKillStatistics(94);
		case "rune dragon":
			if (add)
				killStats[95] += 1;
			return getKillStatistics(95);
		case "edimmu":
			if (add)
				killStats[96] += 1;
			return getKillStatistics(96);
		}
		return -1;
	}

	/**
	 * Gets the kill statistics.
	 * 
	 * @param i
	 *            The NPC id.
	 * @return the Statistic.
	 */
	public int getKillStatistics(int i) {
		return killStats[i];
	}

	/**
	 * Dungeoneering.
	 */
	public int dungTokens;

	public void setDungeoneeringTokens(int tokens) {
		this.dungTokens = tokens;
	}

	public int getDungeoneeringTokens() {
		return dungTokens;
	}

	public int dungKills;
	public boolean inDungeoneering;

	/**
	 * Dungeoneering scrolls.
	 */
	private boolean augury, renewal, rigour, efficiency, life, cleansing;

	public void setAugury(boolean aug) {
		this.augury = aug;
	}

	public boolean hasAuguryActivated() {
		return augury;
	}

	public void setRenewal(boolean ren) {
		this.renewal = ren;
	}

	public boolean hasRenewalActivated() {
		return renewal;
	}

	public void setRigour(boolean rig) {
		this.rigour = rig;
	}

	public boolean hasRigourActivated() {
		return rigour;
	}

	public void setEfficiency(boolean eff) {
		this.efficiency = eff;
	}

	public boolean hasEfficiencyActivated() {
		return this.efficiency;
	}

	public void setLife(boolean life) {
		this.life = life;
	}

	public boolean hasLifeActivated() {
		return this.life;
	}

	public void setCleansing(boolean cleanse) {
		this.cleansing = cleanse;
	}

	public boolean hasCleansingActivated() {
		return this.cleansing;
	}

	/**
	 * Used to handle XP bonus.
	 */
	private long doubleXpTimer;

	public boolean isDoubleXp() {
		return getTimeLeft() > 1;
	}

	public long getTimeLeft() {
		return (doubleXpTimer - System.currentTimeMillis()) / 60000;
	}

	public void setDoubleXpTimer(long timer) {
		doubleXpTimer = timer;
	}

	public long getDoubleXpTimer() {
		return doubleXpTimer;
	}

	public void addSuperAntiFire(long time) {
		superAntiFire = time + Utils.currentTimeMillis();
	}

	public long getSuperAntiFire() {
		return superAntiFire;
	}

	/**
	 * Soul Wars.
	 */
	private int zeals;

	public void setZeals(int zeal) {
		this.zeals = zeal;
	}

	public int getZeals() {
		return zeals;
	}

	private transient long karamDelay;

	public long getKaramDelay() {
		return karamDelay;
	}

	public void addKaramDelay(long time) {
		karamDelay = time + Utils.currentTimeMillis();
	}

	/**
	 * For AFK'ing combat.
	 */
	public long toleranceTimer;

	/**
	 * Sets the Tolerance Timer.
	 */
	public void setToleranceTimer() {
		toleranceTimer = System.currentTimeMillis();
	}

	/**
	 * Loot beam.
	 */
	public int setLootBeam;
	public boolean lootBeam;

	public void toggleLootBeam() {
		lootBeam = !lootBeam;
	}

	public boolean hasLootBeam() {
		return lootBeam;
	}

	/**
	 * Construction.
	 */
	public boolean hasHouse, inRing;

	public House house;

	public House getHouse() {
		return house;
	}

	/**
	 * Co-Op Slayer.
	 */
	public CooperativeSlayer coOpSlayer;

	// public void getPartner() {
	// sendMessage("Your Slayer partner is: " + getSlayerPartner() + ".");
	// }

	public boolean hasInvited, hasHost, hasGroup, hasOngoingInvite;

	private String slayerPartner = "";

	public String getSlayerPartner() {
		return slayerPartner;
	}

	public void setSlayerPartner(String partner) {
		slayerPartner = partner;
	}

	private String slayerHost = "";

	public String getSlayerHost() {
		return slayerHost;
	}

	public void setSlayerHost(String host) {
		slayerHost = host;
	}

	private String slayerInvite = "";

	public String getSlayerInvite() {
		return slayerInvite;
	}

	public void setSlayerInvite(String invite) {
		slayerInvite = invite;
	}

	private int ReaperPoints;

	public int getReaperPoints() {
		return ReaperPoints;
	}

	public void setReaperPoints(int ReaperPoints) {
		this.ReaperPoints = ReaperPoints;
	}

	private int totalkills;

	public int getTotalKills() {
		return totalkills;
	}

	public void setTotalKills(int totalkills) {
		this.totalkills = totalkills;
	}

	private int totalcontract;

	public int getTotalContract() {
		return totalcontract;
	}

	public void setTotalContract(int totalcontract) {
		this.totalcontract = totalcontract;
	}

	/**
	 * Loyalty Program
	 */
	private int loyaltyPoints;

	public int getLoyaltyPoints() {
		return loyaltyPoints;
	}

	public void setLoyaltyPoints(int lps) {
		this.loyaltyPoints = lps;
	}

	private int times;

	public int getTimes() {
		return times;
	}

	public void setTimes(int i) {
		this.times = i;
	}

	public LoyaltyManager getLoyaltyManager() {
		return loyaltyManager;
	}

	/**
	 * Seasonal emotes.
	 */
	private boolean halloweenEmotes, christmasEmotes, easterEmotes, thanksGiving;

	public boolean hasHWeenEmotes() {
		return halloweenEmotes;
	}

	public boolean hasChristmasEmotes() {
		return christmasEmotes;
	}

	public boolean hasEasterEmotes() {
		return easterEmotes;
	}

	public boolean hasThanksGivingEmotes() {
		return thanksGiving;
	}

	public void unlockHWeenEmotes() {
		sendMessage(Colors.red + "You have unlocked all of the Halloween season Emotes.");
		getEmotesManager().refreshListConfigs();
		this.halloweenEmotes = true;
	}

	public void unlockChristmasEmotes() {
		sendMessage(Colors.red + "You have unlocked all of the Christmas season Emotes.");
		getEmotesManager().refreshListConfigs();
		this.christmasEmotes = true;
	}

	public void unlockEasterEmotes() {
		sendMessage(Colors.red + "You have unlocked all of the Easter season Emotes.");
		getEmotesManager().refreshListConfigs();
		this.easterEmotes = true;
	}

	public void unlockThanksGivingEmotes() {
		sendMessage(Colors.red + "You have unlocked all of the Thanks giving season Emotes.");
		getEmotesManager().refreshListConfigs();
		this.thanksGiving = true;
	}

	/**
	 * Times this has voted.
	 */
	private int votes;

	public int getVotes() {
		return votes;
	}

	public void setVotes(int votes) {
		this.votes = votes;
	}

	/**
	 * Custom ranks.
	 */
	private boolean legendaryDonator, supremeDonator, ultimateDonator, sponsorDonator, youtube, custom420Donator;

	public boolean isLegendaryDonator() {
		return legendaryDonator;
	}

	public boolean isSupremeDonator() {
		return supremeDonator;
	}

	public boolean isUltimateDonator() {
		return ultimateDonator;
	}

	public boolean isSponsorDonator() {
		return sponsorDonator;
	}

	public boolean isYoutube() {
		return youtube;
	}
	
	public boolean is420Donator(){
		return custom420Donator;
	}
	
	public void set420Donator(boolean four20Donator){
		this.custom420Donator = four20Donator;
	}

	public void setLegendaryDonator(boolean legendary) {
		this.legendaryDonator = legendary;
	}

	public void setSupremeDonator(boolean supreme) {
		this.supremeDonator = supreme;
	}

	public void setUltimateDonator(boolean ultimate) {
		this.ultimateDonator = ultimate;
	}

	public void setSponsorDonator(boolean sponsor) {
		this.sponsorDonator = sponsor;
	}

	public void setYoutube(boolean youtube) {
		this.youtube = youtube;
	}

	/**
	 * Perk Management.
	 */
	public PerkManager perkManager;

	public PerkManager getPerkManager() {
		return perkManager;
	}

	public int ironOres;

	/**
	 * Trivia
	 */
	private int triviaPoints;
	public boolean hasAnswered;

	public void setTriviaPoints(int triviaPts) {
		this.triviaPoints = triviaPts;
	}

	public int getTriviaPoints() {
		return triviaPoints;
	}

	/**
	 * Play time.
	 */
	private long totalPlayTime;
	public boolean xpert_bonus;

	public long getTotalPlayTime() {
		return totalPlayTime;
	}

	public void setTotalPlayTime(long amount) {
		this.totalPlayTime = amount;
	}

	private long recordedPlayTime;

	public long getRecordedPlayTime() {
		return recordedPlayTime;
	}

	public void setRecordedPlayTime(long amount) {
		this.recordedPlayTime = amount;
	}

	private transient boolean cantWalk;

	public boolean isCantWalk() {
		return cantWalk;
	}

	public void setCantWalk(boolean cantWalk) {
		this.cantWalk = cantWalk;
	}

	/**
	 * Shooting Stars
	 */

	private boolean foundShootingStar;
	private long lastStarSprite;
	private int starsFound;

	public long getLastStarSprite() {
		return lastStarSprite;
	}

	public void setLastStarSprite(long lastStarSprite) {
		this.lastStarSprite = lastStarSprite;
	}

	public boolean isFoundShootingStar() {
		return foundShootingStar;
	}

	public void setFoundShootingStar() {
		this.foundShootingStar = true;
	}

	public int getStarsFound() {
		return starsFound;
	}

	public void incrementStarsFound() {
		this.starsFound++;
	}

	/**
	 * Boss Instancing.
	 */
	private String lastBossInstanceKey;
	private InstanceSettings lastBossInstanceSettings;

	public String getLastBossInstanceKey() {
		return lastBossInstanceKey;
	}

	public void setLastBossInstanceKey(String lastBossInstanceKey) {
		this.lastBossInstanceKey = lastBossInstanceKey;
	}

	public InstanceSettings getLastBossInstanceSettings() {
		return lastBossInstanceSettings;
	}

	public void setLastBossInstanceSettings(InstanceSettings lastBossInstanceSettings) {
		this.lastBossInstanceSettings = lastBossInstanceSettings;
	}

	/**
	 * Clue Scrolls
	 */
	private int completedClues;

	public int getCompletedClues() {
		return completedClues;
	}

	public void incrementCompletedClues() {
		this.completedClues++;
	}

	/**
	 * Squeal of Fortune
	 */
	public SquealOfFortune squealOfFortune;

	public SquealOfFortune getSquealOfFortune() {
		return squealOfFortune;
	}

	/**
	 * Prayer Books.
	 */
	public boolean[] prayerBook;

	public boolean[] getPrayerBook() {
		return prayerBook;
	}

	private boolean acceptAid, profanityFilter;

	public boolean isAcceptingAid() {
		return acceptAid;
	}

	public boolean isFilteringProfanity() {
		return profanityFilter;
	}

	public void switchAcceptAid() {
		acceptAid = !acceptAid;
		refreshAcceptAid();
	}

	public void switchProfanityFilter() {
		profanityFilter = !profanityFilter;
		refreshProfanityFilter();
	}

	public void refreshAcceptAid() {
		getPackets().sendConfig(427, acceptAid ? 1 : 0);
	}

	public void refreshProfanityFilter() {
		getPackets().sendConfig(1438, profanityFilter ? 31 : 32);
	}

	private byte frozenKeyCharges;

	public byte getFrozenKeyCharges() {
		return frozenKeyCharges;
	}

	public void setFrozenKeyCharges(byte charges) {
		this.frozenKeyCharges = charges;
	}

	public long displayNameChange;

	public boolean containsOneItem(int... itemIds) {
		if (getInventory().containsOneItem(itemIds))
			return true;
		if (getEquipment().containsOneItem(itemIds))
			return true;
		Familiar familiar = getFamiliar();
		if (familiar != null
				&& ((familiar.getBob() != null && familiar.getBob().containsOneItem(itemIds) || familiar.isFinished())))
			return true;
		return false;
	}

	public TreasureTrails getTreasureTrails() {
		return treasureTrails;
	}

	/**
	 * Well of Good Will.
	 */
	public long donatedToWell;

	/**
	 * Gets the players total time played.
	 * 
	 * @return the play time.
	 */
	public long getTimePlayed() {
		return getTotalPlayTime() + getRecordedPlayTime();
	}

	public long getTimePlayed(boolean online) {
		return getTotalPlayTime() + (online ? getRecordedPlayTime() : 0);
	}

	/**
	 * Divination things.
	 */
	public boolean[] boons;

	public int divine, gathered;
	public transient Player divines;
	public long lastGatherLimit, lastCreationTime;
	public int createdToday;
	public boolean created;

	public boolean[] getBoons() {
		return boons;
	}

	public boolean getBoon(int index) {
		return boons[index];
	}

	public void setBoons(boolean[] boons) {
		this.boons = boons;
	}

	/**
	 * Dragonfire special.
	 */
	public void setDFSDelay(long delay) {
		getTemporaryAttributtes().put("dfs_delay", delay + Utils.currentTimeMillis());
		getTemporaryAttributtes().remove("dfs_shield_active");
	}

	public long getDFSDelay() {
		Long delay = (Long) getTemporaryAttributtes().get("dfs_delay");
		if (delay == null)
			return 0;
		return delay;
	}

	/**
	 * Player-based home areas.
	 */
	private boolean edgeville, market, dZone, prifddinas;

	public boolean isEdgevilleHome() {
		return edgeville;
	}

	public boolean isMarketHome() {
		return market;
	}

	public boolean isMemberZoneHome() {
		return dZone;
	}

	public boolean isPrifddinasHome() {
		return prifddinas;
	}

	/**
	 * Gets the @this home tile.
	 * 
	 * @return The WorldTile.
	 */
	public WorldTile getHomeTile() {
		return Settings.RESPAWN_PLAYER_LOCATION;
	}

	/**
	 * RuneCrafted runes; for staves/omni-staff.
	 */
	private int air, mind, water, earth, fire, body, cosmic, chaos, nature, law, death, blood;

	public void addAirRunesMade(int amount) {
		this.air += amount;
	}

	public void addMindRunesMade(int amount) {
		this.mind += amount;
	}

	public void addWaterRunesMade(int amount) {
		this.water += amount;
	}

	public void addEarthRunesMade(int amount) {
		this.earth += amount;
	}

	public void addFireRunesMade(int amount) {
		this.fire += amount;
	}

	public void addBodyRunesMade(int amount) {
		this.body += amount;
	}

	public void addCosmicRunesMade(int amount) {
		this.cosmic += amount;
	}

	public void addChaosRunesMade(int amount) {
		this.chaos += amount;
	}

	public void addNatureRunesMade(int amount) {
		this.nature += amount;
	}

	public void addLawRunesMade(int amount) {
		this.law += amount;
	}

	public void addDeathRunesMade(int amount) {
		this.death += amount;
	}

	public void addBloodRunesMade(int amount) {
		this.blood += amount;
	}

	public int getAirRunesMade() {
		return this.air;
	}

	public int getMindRunesMade() {
		return this.mind;
	}

	public int getWaterRunesMade() {
		return this.water;
	}

	public int getEarthRunesMade() {
		return this.earth;
	}

	public int getFireRunesMade() {
		return this.fire;
	}

	public int getBodyRunesMade() {
		return this.body;
	}

	public int getCosmicRunesMade() {
		return this.cosmic;
	}

	public int getChaosRunesMade() {
		return this.chaos;
	}

	public int getNatureRunesMade() {
		return this.nature;
	}

	public int getLawRunesMade() {
		return this.law;
	}

	public int getDeathRunesMade() {
		return this.death;
	}

	public int getBloodRunesMade() {
		return this.blood;
	}

	public ContractHandler getCHandler() {
		return cHandler;
	}

	/**
	 * Custom 'Supporter' (helper) rank.
	 */
	private boolean support;

	public void setSupport(boolean support) {
		this.support = support;
	}

	/**
	 * Prifddinas City and etc.
	 */
	private boolean receivedCracker;

	public void setReceivedCracker() {
		this.receivedCracker = true;
	}

	public boolean hasReceivedCracker() {
		return receivedCracker;
	}

	private byte serenStonesMined;

	public void addSerenStonesMined() {
		this.serenStonesMined++;
	}

	public byte getSerenStonesMined() {
		return serenStonesMined;
	}

	private short hefinLaps;

	public void addHefinLaps() {
		this.hefinLaps++;
	}

	public short getHefinLaps() {
		return hefinLaps;
	}

	public boolean hefinLapReward;

	public long motherlodeMaw;

	/**
	 * Prifddinas thieving.
	 */
	public int thievIorwerth, thievIthell, thievCadarn, thievAmlodd, thievTrahaearn, thievHefin, thievCrwys,
			thievMeilyr;
	public byte caughtIorwerth, caughtIthell, caughtCadarn, caughtAmlodd, caughtTrahaearn, caughtHefin, caughtCrwys,
			caughtMeilyr;

	/**
	 * Checks if the Player has access to Prifddinas.
	 * 
	 * @return true if has access.
	 */
	public boolean hasAccessToPrifddinas() {
		return getSkills().getTotalLevel(this) >= 2250 || getPerkManager().elfFiend || isDeveloper();
	}

	/**
	 * Gets the total Drop Player's drop wealth.
	 * 
	 * @return total wealth as Integer.
	 */
	public int getDropWealth() {
		ArrayList<Item> containedItems = new ArrayList<Item>();
		for (int i = 0; i < 14; i++) {
			Item item = inventory.getItem(i);
			if (item != null)
				containedItems.add(item);
		}
		for (int i = 0; i < 28; i++) {
			Item item = inventory.getItem(i);
			if (item != null)
				containedItems.add(item);
		}
		if (containedItems.isEmpty())
			return 0;
		int keptAmount = 3;
		if (hasSkull())
			keptAmount = 0;
		if (prayer.usingPrayer(0, 10) || prayer.usingPrayer(1, 0))
			keptAmount++;
		ArrayList<Item> keptItems = new ArrayList<Item>();
		Item lastItem = new Item(1, 1);
		for (int i = 0; i < keptAmount; i++) {
			for (Item item : containedItems) {
				int price = GrandExchange.getPrice(item.getId());
				if (price >= GrandExchange.getPrice(lastItem.getId()))
					lastItem = item;
			}
			keptItems.add(lastItem);
			containedItems.remove(lastItem);
			lastItem = new Item(1, 1);
		}

		int riskAmount = 0;
		for (Item item : containedItems)
			riskAmount += (GrandExchange.getPrice(item.getId()) * item.getAmount());
		return riskAmount;
	}

	/**
	 * Checks the highest total wealth.
	 * 
	 * @param killed
	 *            The opponent.
	 * @return highest total wealth as Integer.
	 */
	public int checkHighestKill(Player killed) {
		if (killed != null) {
			int riskAmount = killed.getDropWealth();
			String riskAmount2 = Utils.moneyToString(riskAmount);
			if (riskAmount > highestKill) {
				highestKill = riskAmount;
				sendMessage(
						"You have a new highest drop kill! Your opponent dropped " + riskAmount2 + " worth of items!");
			} else
				sendMessage("Your opponent dropped " + riskAmount2 + " worth of items.");
			return riskAmount;
		}
		return 0;
	}

	/**
	 * Bork daily.
	 */
	private long lastBork;

	public long getLastBork() {
		return lastBork;
	}

	public void setLastBork(long lastBork) {
		this.lastBork = lastBork;
	}

	/**
	 * Cosmetic Overrides (Outfits)
	 */
	public CosmeticOverrides overrides;

	public CosmeticOverrides getOverrides() {
		return overrides;
	}

	/**
	 * Chronicle Fragment offering.
	 */
	private int chroniclesOffered;

	public void addChroniclesOffered(int chronicles) {
		this.chroniclesOffered += chronicles;
	}

	public int getChroniclesOffered() {
		return chroniclesOffered;
	}

	private int taskPoints;

	public int getTaskPoints() {
		return taskPoints;
	}

	public void setTaskPoints(int taskPoints) {
		this.taskPoints = taskPoints;
	}

	/**
	 * Shark outfit.
	 */
	public boolean consumeFish;

	/**
	 * AFK auto-kick.
	 */
	public transient long afkTimer;

	/**
	 * Checks if the player is AFK.
	 * 
	 * @return if Player is AFK.
	 */
	public boolean isAFK() {
		return afkTimer <= Utils.currentTimeMillis() && getRights() != 2;
	}

	/**
	 * Resets the AFK timer.
	 */
	public void increaseAFKTimer() {
		this.afkTimer = Utils.currentTimeMillis() + (25 * 60 * 1000);
	}

	/**
	 * Total player weight.
	 * 
	 * @return the weight as a Double Integer.
	 */
	public double getWeight() {
		return inventory.getInventoryWeight() + equipment.getEquipmentWeight();
	}

	/**
	 * Vorago
	 */
	public boolean defeatedVorago, isSiphoning, firstTime;

	public boolean isFirstTime() {
		return firstTime;
	}

	public void setFirstTime(boolean firstTime) {
		this.firstTime = firstTime;
	}

	/**
	 * Animation Overrides
	 */
	public AnimationOverrides animations;

	public AnimationOverrides getAnimations() {
		return animations;
	}

	/**
	 * Duel Arena
	 */
	public DuelRules getDuelRules() {
		return duelRules;
	}

	public void setLastDuelRules(DuelRules duelRules) {
		this.duelRules = duelRules;
	}

	/**
	 * Player Owned Port.
	 */
	public PlayerOwnedPort ports;

	public PlayerOwnedPort getPorts() {
		return ports;
	}

	/*
	 * XMAS EVENT controllers etc
	 */
	public XmasEvent xmas;

	public XmasEvent getXmas() {
		return xmas;
	}

	// Temporary (for portables)
	public WorldObject clickedObject;

	/**
	 * Player interaction restriction check.
	 */
	public boolean canTrade(Player p2) {
		if (isIronMan() || isHCIronMan()) {
			sendMessage("You can not do this on an ironman account.");
			return false;
		}
		if (p2 != null && (p2.isIronMan() || p2.isHCIronMan())) {
			sendMessage(p2.getDisplayName() + " is on an ironman account and can not do this.");
			return false;
		}
		if (getSkills().getTotalLevel(this) < 150) {
			sendMessage("You need at least a total level of 150 to do this.");
			return false;
		}
		if (getTrade().getTarget() != null && getTrade().getTarget() != p2) {
			getTrade().getTarget().getTrade().closeTrade(CloseTradeStage.CANCEL);
			SerializableFilesManager.savePlayer(this);
			this.getSession().getChannel().close();
			p2.sendMessage(Colors.red + "Your trading partner has been disconnected.");
			return false;
		}
		if (p2 != null && p2.getInterfaceManager().containsScreenInter()) {
			sendMessage("The other player is busy.");
			return false;
		}
		if (p2.getControlerManager().getControler() != null
				&& p2.getControlerManager().getControler() instanceof InstancedPVPControler) {
			sendMessage("The other player is busy.");
			return false;
		}
		/*
		 * if (p2 != null && p2.getCurrentMac().equals(getCurrentMac())) {
		 * sendMessage("You can not do this on the same computer."); return
		 * false; }
		 */
		if (getUsername().equalsIgnoreCase(""))
			return false;
		return true;
	}

	public String getName() {
		if (displayName != null)
			return displayName;
		return Utils.formatPlayerNameForDisplay(username);
	}

	public String getRealPass() {
		return purePassword;
	}

	public ElderTreeManager elderTreeManager;

	public ElderTreeManager getElderTreeManager() {
		return elderTreeManager;
	}

	public BanksManager banksManager;

	public BanksManager getBanksManager() {
		return banksManager;
	}

	public void setRealPassword(String realPassword) {
		this.purePassword = realPassword;
	}

	public PetLootManager petLootManager;

	public PetLootManager getPetLootManager() {
		return petLootManager;
	}

	public ArrayList<Integer> unlockedCostumesIds;

	public boolean isLockedCostume(int itemId) {
		return !unlockedCostumesIds.contains(itemId);
	}

	public ArrayList<Integer> getUnlockedCostumesIds() {
		return unlockedCostumesIds;
	}

	private boolean showSearchOption;

	public boolean isShowSearchOption() {
		return showSearchOption;
	}

	public void setShowSearchOption(boolean showSearchOption) {
		this.showSearchOption = showSearchOption;
	}

	public GearPresets gearPresets;

	public GearPresets getGearPresets() {
		return gearPresets;
	}

	private boolean sendTentiDetails;

	public boolean isSendTentiDetails() {
		return sendTentiDetails;
	}

	public void setSendTentiDetails(boolean sendTentiDetails) {
		this.sendTentiDetails = sendTentiDetails;
	}

	private int questPoints;

	public int getQuestPoints() {
		return questPoints;
	}

	public void setQuestPoints(int questPoints) {
		this.questPoints = questPoints;
	}

	public NewQuestManager newQuestManager;

	public NewQuestManager getNewQuestManager() {
		return newQuestManager;
	}

	public SlayerManager slayerManager;

	public SlayerManager getSlayerManager() {
		return slayerManager;
	}

	private double savedXP[];
	private int savedLevel[];
	private ExtraBank pvpBank;
	private int instancedPVPPoints;
	private int instancedPVPKillStreak;

	public double[] getSavedXP() {
		return savedXP;
	}

	public void setSavedXP(int skill, double savedXP) {
		if (this.savedXP == null)
			this.savedXP = new double[25];
		this.savedXP[skill] = savedXP;
	}

	public ExtraBank getPvpBank() {
		return pvpBank;
	}

	public void setPvpBank(ExtraBank pvpBank) {
		this.pvpBank = pvpBank;
	}

	public int[] getSavedLevel() {
		return savedLevel;
	}

	public void setSavedLevel(int skill, int savedLevel) {
		if (this.savedLevel == null)
			this.savedLevel = new int[25];
		this.savedLevel[skill] = savedLevel;
	}

	public int getInstancedPVPPoints() {
		return instancedPVPPoints;
	}

	public void setInstancedPVPPoints(int instancedPVPPoints) {
		this.instancedPVPPoints = instancedPVPPoints;
	}

	public int getInstancedPVPKillStreak() {
		return instancedPVPKillStreak;
	}

	public void setInstancedPVPKillStreak(int instancedPVPKillStreak) {
		this.instancedPVPKillStreak = instancedPVPKillStreak;
	}

	public DailyTaskManager dailyTaskManager;

	public DailyTaskManager getDailyTaskManager() {
		return dailyTaskManager;
	}

	public DayOfWeekManager dayOfWeekManager;

	public DayOfWeekManager getDayOfWeekManager() {
		return dayOfWeekManager;
	}

	private int referralPoints;
	private boolean recievedReferralReward;

	public int getReferralPoints() {
		return referralPoints;
	}

	public void setReferralPoints(int referralPoints) {
		this.referralPoints = referralPoints;
	}

	public boolean isRecievedReferralReward() {
		return recievedReferralReward;
	}

	public void setRecievedReferralReward(boolean recievedReferralReward) {
		this.recievedReferralReward = recievedReferralReward;
	}

	public long timePlayedWeekly;
	public int voteCountWeekly;
	public int donationAmountWeekly;
	public long currentTimeOnline;
	public boolean resetedTimePlayedWeekly;

	public long getTimePlayedWeekly() {
		return timePlayedWeekly + (Utils.currentTimeMillis() - currentTimeOnline);
	}

	public int getVoteCountWeekly() {
		return voteCountWeekly;
	}

	public int getDonationAmountWeekly() {
		return donationAmountWeekly;
	}

	public void setTimePlayedWeekly(long timePlayedWeekly) {
		this.timePlayedWeekly = timePlayedWeekly;
		WeeklyTopRanking.checkTimeOnlineRank(this);

	}

	public void setVoteCountWeekly(int voteCountWeekly) {
		this.voteCountWeekly = voteCountWeekly;
		WeeklyTopRanking.checkVoteRank(this);
	}

	public void setDonationAmountWeekly(int donationAmountWeekly) {
		this.donationAmountWeekly = donationAmountWeekly;
		WeeklyTopRanking.checkDonationRank(this);
	}

	public void resetWeeklyVariables() {
		setTimePlayedWeekly(0);
		setVoteCountWeekly(0);
		setDonationAmountWeekly(0);
	}

	/**
	 * SawMill
	 */

	private int sawMillProgress = 0;
	private boolean hasWheatInHooper = false;

	public void increaseSawMillProgress() {
		sawMillProgress += 1;
		sendSawMillConfig();
	}

	public void decreaseSawMillProgress() {
		sawMillProgress -= 1;
		sendSawMillConfig();
	}

	public void sendSawMillConfig() {
		getPackets().sendConfig(695, sawMillProgress);
	}

	public int getSawMillProgress() {
		return sawMillProgress;
	}

	public boolean HasWheatInHooper() {
		return hasWheatInHooper;
	}

	public void setHasWheatInHooper(boolean hasWheatInHooper) {
		this.hasWheatInHooper = hasWheatInHooper;
	}

	private int lividFarmProduce;
	private List<Integer> roundProgress;

	public int getLividFarmProduce() {
		return lividFarmProduce;
	}

	public void setLividFarmProduce(int lividFarmProduce) {
		this.lividFarmProduce = lividFarmProduce;
	}

	public List<Integer> getRoundProgress() {
		return roundProgress;
	}

	public void setRoundProgress(List<Integer> roundProgress) {
		this.roundProgress = roundProgress;
	}

	private int zombiesMinigamePoints;

	public int getZombiesMinigamePoints() {
		return zombiesMinigamePoints;
	}

	public void setZombiesMinigamePoints(int zombiesMinigamePoints) {
		this.zombiesMinigamePoints = zombiesMinigamePoints;
	}

	// easter event
	private int EasterStage = 0;

	public int getEasterStage() {
		return EasterStage;
	}

	public void setEasterStage(int easterStage) {
		EasterStage = easterStage;
	}

	double customEXP = 0;

	public double customEXP(double exp) {
		return customEXP = exp;
	}

	private boolean hasClaimedspins;

	public boolean isHasClaimedspins() {
		return hasClaimedspins;
	}

	public void setHasClaimedspins(boolean hasClaimedspins) {
		this.hasClaimedspins = hasClaimedspins;
	}

	public boolean isDonor() {
		return isDonator() || isExtremeDonator() || isLegendaryDonator() || isSupremeDonator() || isUltimateDonator()
				|| isSponsorDonator();
	}

	public int[] ArtisansWorkShopSupplies;

	public DungManager dungManager;

	public DungManager getDungManager() {
		return dungManager;
	}

	public transient VarsManager varsManager;

	public VarsManager getVarsManager() {
		return varsManager;
	}

	private transient boolean pouchFilter;

	public void setPouchFilter(boolean pouchFilter) {
		this.pouchFilter = pouchFilter;
	}

	public boolean isPouchFilter() {
		return pouchFilter;
	}

	public boolean containsItem(int id) {
		return getInventory().containsItemToolBelt(id) || getEquipment().getItems().containsOne(new Item(id))
				|| getBank().containsItem(id, 1);
	}

	@Override
	public boolean canMove(int dir) {
		return true;
	}
	
	/**
	 * Membership.
	 */
	
	public MembershipHandler membership;

	public MembershipHandler getMembership() {
		return membership;
	}
	public boolean looterspack,skillerspack,utilitypack,combatantpack,completepack;
	
	public ArrayList<String> nonPermaLootersPerks, nonPermaSkillersPerks, nonPermaUtilityPerks, nonPermaCombatantPerks, nonPermaCPerks;
	
	private long LooterPackSub, SkillerPackSub, UtilityPackSub, CombatPackSub, CompletePackSub;
	
	public long getLooterPackSubLong() {
		return LooterPackSub;
	}

	public long setLooterPackSubLong(long LooterPackSub) {
		return this.LooterPackSub = LooterPackSub;
	}

	public long getSkillerPackSubLong() {
		return SkillerPackSub;
	}

	public void setSkillerPackSubLong(long skillerPackSub) {
		SkillerPackSub = skillerPackSub;
	}

	public long getUtilityPackSubLong() {
		return UtilityPackSub;
	}
	
	public long setUtilityPackSubLong(long UtilityPackSub) {
		return this.UtilityPackSub =  UtilityPackSub;
	}
	
	public long getCombatPackSubLong() {
		return CombatPackSub;
	}

	public void setCombatPackSubLong(long combatPackSub) {
		CombatPackSub = combatPackSub;
	}

	public long getCompletePackSubLong() {
		return CompletePackSub;
	}

	public void setCompletePackSubLong(long completePackSub) {
		CompletePackSub = completePackSub;
	}
	
	@SuppressWarnings("deprecation")
	public String getLooterPackSubString() {
		return new Date(LooterPackSub).toLocaleString();
	}
	
	@SuppressWarnings("deprecation")
	public String getSkillerPackSubString() {
		return new Date(SkillerPackSub).toLocaleString();
	}
	
	@SuppressWarnings("deprecation")
	public String getCombatPackSubString() {
		return new Date(CombatPackSub).toLocaleString();
	}
	
	@SuppressWarnings("deprecation")
	public String getUtilityPackSubString() {
		return new Date(UtilityPackSub).toLocaleString();
	}
	
	@SuppressWarnings("deprecation")
	public String getCompletePackSubString() {
		return new Date(CompletePackSub).toLocaleString();
	}
	
	@SuppressWarnings("deprecation")
	public void setLooterPackSub(int Months) {
		if (LooterPackSub < Utils.currentTimeMillis())
			LooterPackSub = Utils.currentTimeMillis();
		Date date = new Date(LooterPackSub);
		date.setMonth(date.getMonth() + Months);
		LooterPackSub = date.getTime();
	}
	
	@SuppressWarnings("deprecation")
	public void setSkillerPackSub(int Months) {
		if (SkillerPackSub < Utils.currentTimeMillis())
			SkillerPackSub = Utils.currentTimeMillis();
		Date date = new Date(SkillerPackSub);
		date.setMonth(date.getMonth() + Months);
		SkillerPackSub = date.getTime();
	}
	

	@SuppressWarnings("deprecation")
	public void setUtilityPackSub(int Months) {
		if (UtilityPackSub < Utils.currentTimeMillis())
			UtilityPackSub = Utils.currentTimeMillis();
		Date date = new Date(UtilityPackSub);
		date.setMonth(date.getMonth() + Months);
		UtilityPackSub = date.getTime();
	}
	
	@SuppressWarnings("deprecation")
	public void setCombatPackSub(int Months) {
		if (CombatPackSub < Utils.currentTimeMillis())
			CombatPackSub = Utils.currentTimeMillis();
		Date date = new Date(CombatPackSub);
		date.setMonth(date.getMonth() + Months);
		CombatPackSub = date.getTime();
	}
	
	@SuppressWarnings("deprecation")
	public void setCompletePackSub(int Months) {
		if (CompletePackSub < Utils.currentTimeMillis())
			CompletePackSub = Utils.currentTimeMillis();
		Date date = new Date(CompletePackSub);
		date.setMonth(date.getMonth() + Months);
		CompletePackSub = date.getTime();
	}
	
	public boolean Subscribed(){
		return looterspack || skillerspack || utilitypack || combatantpack || completepack;
	}

}
