package com.rs.game;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.rs.Protocol;
import com.rs.ServerLauncher;
import com.rs.Settings;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cores.CoresManager;
import com.rs.game.activites.PuroPuro;
import com.rs.game.activites.ShootingStar;
import com.rs.game.activites.ZarosGodwars;
import com.rs.game.activites.clanwars.FfaZone;
import com.rs.game.activites.clanwars.RequestController;
import com.rs.game.activites.duel.DuelControler;
import com.rs.game.activites.soulwars.SoulWarsManager;
import com.rs.game.item.FloorItem;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.npc.araxxor.AraxyteNPC;
import com.rs.game.npc.corp.CorporealBeast;
import com.rs.game.npc.dragons.CelestialDragonB;
import com.rs.game.npc.dragons.KingBlackDragon;
import com.rs.game.npc.dragons.RuneDragon;
import com.rs.game.npc.glacor.Glacor;
import com.rs.game.npc.godwars.armadyl.GodwarsArmadylFaction;
import com.rs.game.npc.godwars.bandos.GodwarsBandosFaction;
import com.rs.game.npc.godwars.saradomin.GodwarsSaradominFaction;
import com.rs.game.npc.godwars.zammorak.GodwarsZammorakFaction;
import com.rs.game.npc.godwars.zaros.GodwarsZarosFaction;
import com.rs.game.npc.godwars.zaros.Nex;
import com.rs.game.npc.godwars.zaros.NexMinion;
import com.rs.game.npc.kalphite.KalphiteKing;
import com.rs.game.npc.kalphite.KalphiteQueen;
import com.rs.game.npc.nomad.FlameVortex;
import com.rs.game.npc.nomad.Nomad;
import com.rs.game.npc.others.Bork;
import com.rs.game.npc.others.ConditionalDeath;
import com.rs.game.npc.others.DagannothKing;
import com.rs.game.npc.others.HauntedTree;
import com.rs.game.npc.others.HunterTrapNPC;
import com.rs.game.npc.others.LivingRock;
import com.rs.game.npc.others.Lucien;
import com.rs.game.npc.others.Revenant;
import com.rs.game.npc.others.Sheep;
import com.rs.game.npc.others.StarSprite;
import com.rs.game.npc.others.TormentedDemon;
import com.rs.game.npc.others.WildyWyrm;
import com.rs.game.npc.slayer.Strykewyrm;
import com.rs.game.npc.sorgar.Elemental;
import com.rs.game.npc.vorago.Vorago;
import com.rs.game.npc.xmas.AsteaFrostweb;
import com.rs.game.npc.xmas.SmallSnowman;
import com.rs.game.player.DayOfWeekManager;
import com.rs.game.player.MembershipHandler;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.divination.Wisp;
import com.rs.game.player.actions.divination.WispInfo;
import com.rs.game.player.actions.hunter.TrapAction.HunterNPC;
import com.rs.game.player.content.ArtisansWorkShop;
import com.rs.game.player.content.ItemConstants;
import com.rs.game.player.content.LividFarm;
import com.rs.game.player.content.LivingRockCavern;
import com.rs.game.player.content.Lottery;
import com.rs.game.player.content.OwnedObjectManager;
import com.rs.game.player.content.ReferralHandler;
import com.rs.game.player.content.TriviaBot;
import com.rs.game.player.content.WeeklyTopRanking;
import com.rs.game.player.content.WellOfGoodWill;
import com.rs.game.player.content.grandExchange.GrandExchange;
import com.rs.game.player.content.ports.JohnStrum;
import com.rs.game.player.controllers.Kalaboss;
import com.rs.game.player.controllers.ArtisansWorkShopControler;
import com.rs.game.player.controllers.WarriorsGuild;
import com.rs.game.player.controllers.Wilderness;
import com.rs.game.player.dialogue.impl.StarterTutorialD;
import com.rs.game.route.Flags;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.network.protocol.codec.decode.impl.ButtonHandler;
import com.rs.utils.AntiFlood;
import com.rs.utils.Colors;
import com.rs.utils.DTRank;
import com.rs.utils.DonationRank;
import com.rs.utils.IPBanL;
import com.rs.utils.IPMute;
import com.rs.utils.Logger;
import com.rs.utils.LoggingSystem;
import com.rs.utils.MACBan;
import com.rs.utils.MapUtils;
import com.rs.utils.MapUtils.Structure;
import com.rs.utils.PkRank;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.ShopsHandler;
import com.rs.utils.Utils;
import com.rs.utils.VoteHiscores;

import mysql.impl.VoteManager;

/**
 * A class which handles every World event & interactions.
 * 
 * @author Zeus
 */
public final class World {

	/**
	 * A list holding all Player-Entities online.
	 */
	private static final EntityList<Player> players = new EntityList<Player>(Protocol.SV_PLAYERS_LIMIT);

	/**
	 * A list holding all NPC-Entities online.
	 */
	private static final EntityList<NPC> npcs = new EntityList<NPC>(Protocol.SV_NPCS_LIMIT);

	/**
	 * A map containing all Regions as Integers.
	 */
	private static final Map<Integer, Region> regions = Collections.synchronizedMap(new HashMap<Integer, Region>());

	/**
	 * Starts all World activites.
	 */
	public static final void init() {
		addRestoreRunEnergyTask();
		addDrainPrayerTask();
		addRestoreHitPointsTask();
		addRestoreSkillsTask();
		addRestoreSpecialAttackTask();
		addSummoningEffectTask();
		addOwnedObjectsTask();
		LivingRockCavern.init();
		addRestoreShopItemsTask();
		messageEvent();
		WarriorsGuild.init();
		ShootingStar.init();
		soulWars = new SoulWarsManager();
		soulWars.start();
		addTriviaBotTask();
		addLeechedStatsTask();
		PuroPuro.initPuroImplings();
		Lottery.addLotteryProcessTask();
		addReferralTask();
		addWorldCalendarTask();
		addLividFarmProcessTask();
		addArtisansWorkShopProcessTask();
		MembershipStatusChecker();
		}

		private static void MembershipStatusChecker() {
			CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					try {
						for(Player p : getPlayers()){
								if(p.looterspack && p.getLooterPackSubLong() < System.currentTimeMillis()) {
									p.getMembership();
									MembershipHandler.checkStatus(p,true,false,1);
								}
									if(p.skillerspack && p.getSkillerPackSubLong() < System.currentTimeMillis()) {
										p.getMembership();
										MembershipHandler.checkStatus(p,true,false,2);
									}
									if(p.utilitypack && p.getUtilityPackSubLong() < System.currentTimeMillis()) {
										p.getMembership();
										MembershipHandler.checkStatus(p,true,false,3);
									}
									if(p.combatantpack && p.getCombatPackSubLong() < System.currentTimeMillis()) {
										p.getMembership();
										MembershipHandler.checkStatus(p,true,false,4);
									}
									if(p.completepack && p.getCompletePackSubLong() < System.currentTimeMillis()) {
										p.getMembership();
										MembershipHandler.checkStatus(p,true,false,5);
									}
						}
					} catch (Throwable e) {
						Logger.handle(e);
					}
				}
			}, 1, 1, TimeUnit.MINUTES);
		}

		
	private static void addArtisansWorkShopProcessTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					ArtisansWorkShop.processArtisansWorkShop();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 60, TimeUnit.SECONDS);
	}

	private static void addLividFarmProcessTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					LividFarm.process();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 60, TimeUnit.SECONDS);
	}

	private static void addReferralTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					ReferralHandler.processReferralRewards();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 15, 15, TimeUnit.MINUTES);
	}

	/**
	 * Handles informative random world messages.
	 */
	private static void messageEvent() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				int random = Utils.random(100);
				if (random < 5)
					sendWorldMessage(
							"<img=7><shad=000000><col=" + colors[Utils.random(colors.length)] + ">"
									+ "Server: Don't forget to check the hiscore board by the home for top Voters!",
							false);
				if (random >= 5 && random < 10)
					sendWorldMessage(
							"<img=7><shad=000000><col=" + colors[Utils.random(colors.length)] + ">"
									+ "Server: Don't forget to check the hiscore board by the home for top Pkers!",
							false);
				else if (random >= 10 && random < 15)
					sendWorldMessage(
							"<img=7><shad=000000><col=" + colors[Utils.random(colors.length)] + ">"
									+ "Server: Don't forget to check the hiscore board by the home for top Dominon tower clears!",
							false);
				else if (random >= 15 && random < 20)
					sendWorldMessage(
							"<img=7><shad=000000><col=" + colors[Utils.random(colors.length)] + ">"
									+ "Server: Don't forget to check the hiscore board by the home for top Donators!",
							false);
				else
					sendWorldMessage("<img=7><shad=000000><col=" + colors[Utils.random(colors.length)] + ">"
							+ "Server: " + messageString[Utils.random(messageString.length)], false);
			}
		}, 15, 6, TimeUnit.MINUTES);
	}

	private static void addTriviaBotTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					if (World.getPlayers().size() >= 1)
						TriviaBot.Run();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 10, Utils.random(5, 10), TimeUnit.MINUTES);
	}

	private static void addWorldCalendarTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					DayOfWeekManager.processWorldCalendar();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 1, 5, TimeUnit.SECONDS);
	}

	private static void addOwnedObjectsTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					OwnedObjectManager.processAll();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

	private static void addRestoreShopItemsTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					ShopsHandler.restoreShops();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 30, TimeUnit.SECONDS);
	}

	private static final void addSummoningEffectTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					for (Player player : getPlayers()) {
						if (player == null || player.getFamiliar() == null || player.isDead() || !player.hasFinished())
							continue;
						if (player.getFamiliar().getOriginalId() == 6814) {
							player.heal(20);
							player.setNextGraphics(new Graphics(1507));
						}
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 15, TimeUnit.SECONDS);
	}

	private static final void addRestoreSpecialAttackTask() {

		CoresManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					for (Player player : getPlayers()) {
						if (player == null || player.isDead() || !player.isRunning())
							continue;
						player.getCombatDefinitions().restoreSpecialAttack();
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 30000);
	}

	private static final void addLeechedStatsTask() {
		CoresManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					for (Player player : getPlayers()) {
						if (player == null || player.isDead() || !player.isRunning())
							continue;

						if (player.getPrayer().usingPrayer(1, 10)) {
							if (player.getPrayer().leechBonuses[0] == 1 || player.getPrayer().leechBonuses[0] == -1) {
								player.getPackets().sendGameMessage(
										"Your Attack is now unaffected by sap and leech curses.", true);
							}
							if (player.getPrayer().leechBonuses[0] < 0) {
								player.getPrayer().increase(0);
							}
							if (player.getPrayer().leechBonuses[0] > 0) {
								player.getPrayer().decrease(0);
							}
						} else {
							if (player.getPrayer().leechBonuses[0] == 1 || player.getPrayer().leechBonuses[0] == -1) {
								player.getPackets().sendGameMessage(
										"Your Attack is now unaffected by sap and leech curses.", true);
							}
							if (player.getPrayer().leechBonuses[0] < 0) {
								player.getPrayer().increase(0);
							}
							if (player.getPrayer().leechBonuses[0] > 0) {
								player.getPrayer().decrease(0);
							}
						}

						if (player.getPrayer().usingPrayer(1, 11)) {
							if (player.getPrayer().leechBonuses[3] == 1 || player.getPrayer().leechBonuses[3] == -1) {
								player.getPackets().sendGameMessage(
										"Your Ranging is now unaffected by sap and leech curses.", true);
							}
							if (player.getPrayer().leechBonuses[3] < 0) {
								player.getPrayer().increase(3);
							}
							if (player.getPrayer().leechBonuses[3] > 0) {
								player.getPrayer().decrease(3);
							}
						} else {
							if (player.getPrayer().leechBonuses[3] == 1 || player.getPrayer().leechBonuses[3] == -1) {
								player.getPackets().sendGameMessage(
										"Your Ranging is now unaffected by sap and leech curses.", true);
							}
							if (player.getPrayer().leechBonuses[3] < 0) {
								player.getPrayer().increase(3);
							}
							if (player.getPrayer().leechBonuses[3] > 0) {
								player.getPrayer().decrease(3);
							}
						}

						if (player.getPrayer().usingPrayer(1, 12)) {
							if (player.getPrayer().leechBonuses[4] == 1 || player.getPrayer().leechBonuses[4] == -1) {
								player.getPackets()
										.sendGameMessage("Your Magic is now unaffected by sap and leech curses.", true);
							}
							if (player.getPrayer().leechBonuses[4] < 0) {
								player.getPrayer().increase(4);
							}
							if (player.getPrayer().leechBonuses[4] > 0) {
								player.getPrayer().decrease(4);
							}
						} else {
							if (player.getPrayer().leechBonuses[3] == 1 || player.getPrayer().leechBonuses[3] == -1) {
								player.getPackets()
										.sendGameMessage("Your Magic is now unaffected by sap and leech curses.", true);
							}
							if (player.getPrayer().leechBonuses[4] < 0) {
								player.getPrayer().increase(4);
							}
							if (player.getPrayer().leechBonuses[4] > 0) {
								player.getPrayer().decrease(4);
							}
						}

						if (player.getPrayer().usingPrayer(1, 13)) {
							if (player.getPrayer().leechBonuses[2] == 1 || player.getPrayer().leechBonuses[2] == -1) {
								player.getPackets().sendGameMessage(
										"Your Defence is now unaffected by sap and leech curses.", true);
							}
							if (player.getPrayer().leechBonuses[2] < 05) {
								player.getPrayer().increase(2);
							}
							if (player.getPrayer().leechBonuses[2] > 0) {
								player.getPrayer().decrease(2);
							}
						} else {
							if (player.getPrayer().leechBonuses[2] == 1 || player.getPrayer().leechBonuses[2] == -1) {
								player.getPackets().sendGameMessage(
										"Your Defence is now unaffected by sap and leech curses.", true);
							}
							if (player.getPrayer().leechBonuses[2] < 0) {
								player.getPrayer().increase(2);
							}
							if (player.getPrayer().leechBonuses[2] > 0) {
								player.getPrayer().decrease(2);
							}
						}
						if (player.getPrayer().usingPrayer(1, 14)) {
							if (player.getPrayer().leechBonuses[1] == 1 || player.getPrayer().leechBonuses[1] == -1) {
								player.getPackets().sendGameMessage(
										"Your Strength is now unaffected by sap and leech curses.", true);
							}
							if (player.getPrayer().leechBonuses[1] < 0) {
								player.getPrayer().increase(1);
							}
							if (player.getPrayer().leechBonuses[1] > 0) {
								player.getPrayer().decrease(1);
							}
						} else {
							if (player.getPrayer().leechBonuses[1] == 1 || player.getPrayer().leechBonuses[1] == -1) {
								player.getPackets().sendGameMessage(
										"Your Strength is now unaffected by sap and leech curses.", true);
							}
							if (player.getPrayer().leechBonuses[1] < 0) {
								player.getPrayer().increase(1);
							}
							if (player.getPrayer().leechBonuses[1] > 0) {
								player.getPrayer().decrease(1);
							}
						}
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 20000);
	}

	private static void addRestoreRunEnergyTask() {
		CoresManager.fastExecutor.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					for (Player player : getPlayers()) {
						if (player == null || player.isDead() || !player.isRunning())
							continue;
						if (player.getNextRunDirection() == -1) {
							player.restoreRunEnergy();
						} else
							player.drainRunEnergy();
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 1500);
	}

	private static void addDrainPrayerTask() {
		CoresManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					for (Player player : getPlayers()) {
						if (player == null || player.isDead() || !player.isRunning())
							continue;
						player.getPrayer().processPrayerDrain();
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 600);
	}

	private static void addRestoreHitPointsTask() {
		CoresManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					for (Player player : getPlayers()) {
						if (player == null || player.isDead() || !player.isRunning())
							// || player.getControlerManager().getControler()
							// instanceof DuelArena
							// || !player.isAcceptingAid())
							continue;
						player.restoreHitPoints();
					}
					for (NPC npc : npcs) {
						if (npc == null || npc.isDead() || npc.hasFinished())
							continue;
						npc.restoreHitPoints();
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 6000);
	}

	private static void addRestoreSkillsTask() {
		CoresManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					for (Player player : getPlayers()) {
						if (player == null || !player.isRunning())
							continue;
						int ammountTimes = player.getPrayer().usingPrayer(0, 8) ? 2 : 1;
						if (player.isResting())
							ammountTimes += 1;
						boolean berserker = player.getPrayer().usingPrayer(1, 9);
						b: for (int skill = 0; skill < 25; skill++) {
							if (skill == Skills.SUMMONING)
								continue b;
							c: for (int time = 0; time < ammountTimes; time++) {
								int currentLevel = player.getSkills().getLevel(skill);
								int normalLevel = player.getSkills().getLevelForXp(skill);
								if (currentLevel > normalLevel && time == 0) {
									if (skill == Skills.ATTACK || skill == Skills.STRENGTH || skill == Skills.DEFENCE
											|| skill == Skills.RANGE || skill == Skills.MAGIC) {
										if (berserker && Utils.random(100) <= 15)
											continue c;
									}
									player.getSkills().set(skill, currentLevel - 1);
								} else if (currentLevel < normalLevel)
									player.getSkills().set(skill, currentLevel + 1);
								else
									break c;
							}
						}
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 60000);
	}

	public static final Map<Integer, Region> getRegions() {
		return regions;
	}

	public static final Region getRegion(int id) {
		return getRegion(id, false);
	}

	public static final Region getRegion(int id, boolean load) {
		Region region = regions.get(id);
		if (region == null) {
			region = new Region(id);
			regions.put(id, region);
		}
		if (load)
			region.checkLoadMap();
		return region;
	}

	public static final void addNPC(NPC npc) {
		npcs.add(npc);
	}

	public static final void removeNPC(NPC npc) {
		npcs.remove(npc);
	}

	public static final NPC spawnNPC(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea,
			boolean spawned) {
		NPC n = null;
		if (id == 5079)
			n = new HunterTrapNPC(HunterNPC.GREY_CHINCHOMPA, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		else if (id == 5080)
			n = new HunterTrapNPC(HunterNPC.RED_CHINCHOMPA, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		else if (id == 152)
			n = new HauntedTree(id, tile, mapAreaNameHash, false);
		else if (id == 5081)
			n = new HunterTrapNPC(HunterNPC.FERRET, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 6916)
			n = new HunterTrapNPC(HunterNPC.GECKO, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 7272)
			n = new HunterTrapNPC(HunterNPC.MONKEY, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 7272)
			n = new HunterTrapNPC(HunterNPC.RACCOON, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 5073)
			n = new HunterTrapNPC(HunterNPC.CRIMSON_SWIFT, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		else if (id == 5075)
			n = new HunterTrapNPC(HunterNPC.GOLDEN_WARBLER, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		else if (id == 5076)
			n = new HunterTrapNPC(HunterNPC.COPPER_LONGTAIL, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		else if (id == 5074)
			n = new HunterTrapNPC(HunterNPC.CERULEAN_TWITCH, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		else if (id == 5072)
			n = new HunterTrapNPC(HunterNPC.TROPICAL_WAGTAIL, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		else if (id == 7031)
			n = new HunterTrapNPC(HunterNPC.WIMPY_BIRD, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 5088)
			n = new HunterTrapNPC(HunterNPC.BARB_TAILED_KEBBIT, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		else if (id == 1631 || id == 1632)
			n = new ConditionalDeath(4161, "The rockslug shrivels and dies.", true, id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea);
		else if (id == 14301)
			n = new Glacor(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 1610)
			n = new ConditionalDeath(4162, "The gargoyle breaks into peices as you slam the hammer onto its head.",
					false, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 14849)
			n = new ConditionalDeath(23035, null, false, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 1627 || id == 1628 || id == 1629 || id == 1630)
			n = new ConditionalDeath(4158, null, false, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id >= 2803 && id <= 2808)
			n = new ConditionalDeath(6696, null, true, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id >= 5533 && id <= 5558)
			n = new Elemental(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 2417)
			n = new WildyWyrm(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 43 || (id >= 5156 && id <= 5164) || id == 5156 || id == 1765)
			n = new Sheep(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 14301)
			n = new Glacor(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 9441)
			n = new FlameVortex(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 8832 && id <= 8834)
			n = new LivingRock(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 13465 && id <= 13481)
			n = new Revenant(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 1158 || id == 1160)
			n = new KalphiteQueen(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 8528 && id <= 8532)
			n = new Nomad(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 8091)
			n = new StarSprite();
		/**
		 * Godwars factions.
		 */
		else if (id >= 6210 && id <= 6221)
			n = new GodwarsZammorakFaction(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 6254 && id <= 6259)
			n = new GodwarsSaradominFaction(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 6229 && id <= 6246)
			n = new GodwarsArmadylFaction(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 13456 && id <= 13459)
			n = new GodwarsZarosFaction(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 6268 && id <= 6283)
			n = new GodwarsBandosFaction(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 50 || id == 2642)
			n = new KingBlackDragon(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 9462 && id <= 9467)
			n = new Strykewyrm(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);

		else if (id == 10021)
			n = new AsteaFrostweb(id, tile);
		else if (id == 8133)
			n = new CorporealBeast(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 13447)
			n = ZarosGodwars.nex = new Nex(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 13451)
			n = new NexMinion(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 13452)
			n = new NexMinion(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 13453)
			n = new NexMinion(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 13454)
			n = new NexMinion(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 14256)
			n = new Lucien(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 8349 || id == 8450 || id == 8451)
			n = new TormentedDemon(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 19463)
			n = new AraxyteNPC(id, tile);
		else if (WispInfo.forNpcId(id) != null)
			n = new Wisp(id, tile);
		else if (id == 19109)
			n = new CelestialDragonB(id, tile);
		else if (id >= 2881 && id <= 2883)
			n = new DagannothKing(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 16697 || id == 16698 || id == 16699)
			n = new KalphiteKing(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 17182)
			n = new Vorago(id, tile);
		// else if (id == 17158 || id == 17159 || id == 17160 || id == 17185)
		// n = new VoragoMinion(id, tile, mapAreaNameHash,
		// canBeAttackFromOutOfArea, spawned);
		else if (id == 21136)
			n = new RuneDragon(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 16554)
			n = new JohnStrum(id, tile);
		else if (id == 2272 || id == 2269 || id == 2271)
			n = new SmallSnowman(id, tile);
		else
			n = new NPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		return n;
	}

	public static final NPC spawnNPC(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		return spawnNPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, false);
	}

	/*
	 * check if the entity region changed because moved or teled then we update
	 * it
	 */
	public static final void updateEntityRegion(Entity entity) {
		if (entity.hasFinished()) {
			if (entity instanceof Player)
				getRegion(entity.getLastRegionId()).removePlayerIndex(entity.getIndex());
			else
				getRegion(entity.getLastRegionId()).removeNPCIndex(entity.getIndex());
			return;
		}
		int regionId = entity.getRegionId();
		if (entity.getLastRegionId() != regionId) { // map region entity at
													// changed
			if (entity instanceof Player) {
				if (entity.getLastRegionId() > 0)
					getRegion(entity.getLastRegionId()).removePlayerIndex(entity.getIndex());
				Region region = getRegion(regionId);
				region.addPlayerIndex(entity.getIndex());
				Player player = (Player) entity;
				int musicId = region.getRandomMusicId();
				if (musicId != -1)
					player.getMusicsManager().checkMusic(musicId);
				player.getControlerManager().moved();
				StarterTutorialD.checkStarterArea(player);
				if (player.isActive())
					checkControlersAtMove(player);
				player.setToleranceTimer();
			} else {
				if (entity.getLastRegionId() > 0)
					getRegion(entity.getLastRegionId()).removeNPCIndex(entity.getIndex());
				getRegion(regionId).addNPCIndex(entity.getIndex());
			}
			entity.checkMultiArea();
			entity.setLastRegionId(regionId);
		} else {
			if (entity instanceof Player) {
				Player player = (Player) entity;
				player.getControlerManager().moved();
				StarterTutorialD.checkStarterArea(player);
				if (player.isActive())
					checkControlersAtMove(player);
			}
			entity.checkMultiArea();
		}
	}

	public static void checkControlersAtMove(Player player) {
		if (LividFarm.isAtLividFarm(player))
			LividFarm.EnterLividFarm(player);
		if (player.getControlerManager().getControler() == null
				&& ArtisansWorkShopControler.isInsideArtisansShop(player))
			player.getControlerManager().startControler("ArtisansWorkShopControler");
		if (player.getControlerManager().getControler() == null && Kalaboss.isAtKalaboss(player))
			player.getControlerManager().startControler("Kalaboss");

		if (!(player.getControlerManager().getControler() instanceof RequestController)
				&& RequestController.inWarRequest(player))
			player.getControlerManager().startControler("clan_wars_request");
		else if (DuelControler.isAtDuelArena(player))
			player.getControlerManager().startControler("DuelControler");
		else if (FfaZone.inArea(player))
			player.getControlerManager().startControler("clan_wars_ffa");
		else if (!player.isApeAtoll()) {
			if (player.getEquipment().getWeaponId() == 4024) {
				ButtonHandler.sendRemove2(player, 3);
			}
		}
	}

	/*
	 * checks clip
	 */
	public static boolean canMoveNPC(int plane, int x, int y, int size) {
		for (int tileX = x; tileX < x + size; tileX++)
			for (int tileY = y; tileY < y + size; tileY++)
				if (getMask(plane, tileX, tileY) != 0)
					return false;
		if (x == 2497 && y == 2716 && plane == 2)
			return false;
		if (x == 2504 && y == 2678 && plane == 2)
			return false;
		return true;
	}

	/*
	 * checks clip
	 */
	public static boolean isNotCliped(int plane, int x, int y, int size) {
		for (int tileX = x; tileX < x + size; tileX++)
			for (int tileY = y; tileY < y + size; tileY++)
				if ((getMask(plane, tileX, tileY) & 2097152) != 0)
					return false;
		return true;
	}

	public static void setMask(int plane, int x, int y, int mask) {
		WorldTile tile = new WorldTile(x, y, plane);
		int regionId = tile.getRegionId();
		Region region = getRegion(regionId);
		if (region == null)
			return;
		int baseLocalX = x - ((regionId >> 8) * 64);
		int baseLocalY = y - ((regionId & 0xff) * 64);
		region.setMask(tile.getPlane(), baseLocalX, baseLocalY, mask);
	}

	public static int getRotation(int plane, int x, int y) {
		WorldTile tile = new WorldTile(x, y, plane);
		int regionId = tile.getRegionId();
		Region region = getRegion(regionId);
		if (region == null)
			return 0;
		// int baseLocalX = x - ((regionId >> 8) * 64);
		// int baseLocalY = y - ((regionId & 0xff) * 64);
		// return region.getRotation(tile.getPlane(), baseLocalX, baseLocalY);
		return 0;
	}

	/*
	 * checks clip
	 */
	public static boolean isRegionLoaded(int regionId) {
		Region region = getRegion(regionId);
		if (region == null)
			return false;
		return region.getLoadMapStage() == 2;
	}

	public static boolean isTileFree(int plane, int x, int y, int size) {
		for (int tileX = x; tileX < x + size; tileX++)
			for (int tileY = y; tileY < y + size; tileY++)
				if (!isFloorFree(plane, tileX, tileY) || !isWallsFree(plane, tileX, tileY))
					return false;
		return true;
	}

	public static boolean isFloorFree(int plane, int x, int y, int size) {
		for (int tileX = x; tileX < x + size; tileX++)
			for (int tileY = y; tileY < y + size; tileY++)
				if (!isFloorFree(plane, tileX, tileY))
					return false;
		return true;
	}

	public static boolean isFloorFree(int plane, int x, int y) {
		return (getMask(plane, x, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ)) == 0;
	}

	public static boolean isWallsFree(int plane, int x, int y) {
		return (getMask(plane, x, y) & (Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_NORTHWEST
				| Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST | Flags.WALLOBJ_EAST | Flags.WALLOBJ_NORTH
				| Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST)) == 0;
	}

	public static int getMask(int plane, int x, int y) {
		WorldTile tile = new WorldTile(x, y, plane);
		Region region = getRegion(tile.getRegionId());
		if (region == null)
			return -1;
		return region.getMask(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion());
	}

	private static int getClipedOnlyMask(int plane, int x, int y) {
		WorldTile tile = new WorldTile(x, y, plane);
		Region region = getRegion(tile.getRegionId());
		if (region == null)
			return -1;
		return region.getMaskClipedOnly(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion());
	}

	public static final boolean checkProjectileStep(int plane, int x, int y, int dir, int size) {
		int xOffset = Utils.DIRECTION_DELTA_X[dir];
		int yOffset = Utils.DIRECTION_DELTA_Y[dir];
		/*
		 * int rotation = getRotation(plane,x+xOffset,y+yOffset); if(rotation !=
		 * 0) { dir += rotation; if(dir >= Utils.DIRECTION_DELTA_X.length) dir =
		 * dir - (Utils.DIRECTION_DELTA_X.length-1); xOffset =
		 * Utils.DIRECTION_DELTA_X[dir]; yOffset = Utils.DIRECTION_DELTA_Y[dir];
		 * }
		 */
		if (size == 1) {
			int mask = getClipedOnlyMask(plane, x + Utils.DIRECTION_DELTA_X[dir], y + Utils.DIRECTION_DELTA_Y[dir]);
			if (xOffset == -1 && yOffset == 0)
				return (mask & 0x42240000) == 0;
			if (xOffset == 1 && yOffset == 0)
				return (mask & 0x60240000) == 0;
			if (xOffset == 0 && yOffset == -1)
				return (mask & 0x40a40000) == 0;
			if (xOffset == 0 && yOffset == 1)
				return (mask & 0x48240000) == 0;
			if (xOffset == -1 && yOffset == -1) {
				return (mask & 0x43a40000) == 0 && (getClipedOnlyMask(plane, x - 1, y) & 0x42240000) == 0
						&& (getClipedOnlyMask(plane, x, y - 1) & 0x40a40000) == 0;
			}
			if (xOffset == 1 && yOffset == -1) {
				return (mask & 0x60e40000) == 0 && (getClipedOnlyMask(plane, x + 1, y) & 0x60240000) == 0
						&& (getClipedOnlyMask(plane, x, y - 1) & 0x40a40000) == 0;
			}
			if (xOffset == -1 && yOffset == 1) {
				return (mask & 0x4e240000) == 0 && (getClipedOnlyMask(plane, x - 1, y) & 0x42240000) == 0
						&& (getClipedOnlyMask(plane, x, y + 1) & 0x48240000) == 0;
			}
			if (xOffset == 1 && yOffset == 1) {
				return (mask & 0x78240000) == 0 && (getClipedOnlyMask(plane, x + 1, y) & 0x60240000) == 0
						&& (getClipedOnlyMask(plane, x, y + 1) & 0x48240000) == 0;
			}
		} else if (size == 2) {
			if (xOffset == -1 && yOffset == 0)
				return (getClipedOnlyMask(plane, x - 1, y) & 0x43a40000) == 0
						&& (getClipedOnlyMask(plane, x - 1, y + 1) & 0x4e240000) == 0;
			if (xOffset == 1 && yOffset == 0)
				return (getClipedOnlyMask(plane, x + 2, y) & 0x60e40000) == 0
						&& (getClipedOnlyMask(plane, x + 2, y + 1) & 0x78240000) == 0;
			if (xOffset == 0 && yOffset == -1)
				return (getClipedOnlyMask(plane, x, y - 1) & 0x43a40000) == 0
						&& (getClipedOnlyMask(plane, x + 1, y - 1) & 0x60e40000) == 0;
			if (xOffset == 0 && yOffset == 1)
				return (getClipedOnlyMask(plane, x, y + 2) & 0x4e240000) == 0
						&& (getClipedOnlyMask(plane, x + 1, y + 2) & 0x78240000) == 0;
			if (xOffset == -1 && yOffset == -1)
				return (getClipedOnlyMask(plane, x - 1, y) & 0x4fa40000) == 0
						&& (getClipedOnlyMask(plane, x - 1, y - 1) & 0x43a40000) == 0
						&& (getClipedOnlyMask(plane, x, y - 1) & 0x63e40000) == 0;
			if (xOffset == 1 && yOffset == -1)
				return (getClipedOnlyMask(plane, x + 1, y - 1) & 0x63e40000) == 0
						&& (getClipedOnlyMask(plane, x + 2, y - 1) & 0x60e40000) == 0
						&& (getClipedOnlyMask(plane, x + 2, y) & 0x78e40000) == 0;
			if (xOffset == -1 && yOffset == 1)
				return (getClipedOnlyMask(plane, x - 1, y + 1) & 0x4fa40000) == 0
						&& (getClipedOnlyMask(plane, x - 1, y + 1) & 0x4e240000) == 0
						&& (getClipedOnlyMask(plane, x, y + 2) & 0x7e240000) == 0;
			if (xOffset == 1 && yOffset == 1)
				return (getClipedOnlyMask(plane, x + 1, y + 2) & 0x7e240000) == 0
						&& (getClipedOnlyMask(plane, x + 2, y + 2) & 0x78240000) == 0
						&& (getClipedOnlyMask(plane, x + 1, y + 1) & 0x78e40000) == 0;
		} else {
			if (xOffset == -1 && yOffset == 0) {
				if ((getClipedOnlyMask(plane, x - 1, y) & 0x43a40000) != 0
						|| (getClipedOnlyMask(plane, x - 1, -1 + (y + size)) & 0x4e240000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getClipedOnlyMask(plane, x - 1, y + sizeOffset) & 0x4fa40000) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == 0) {
				if ((getClipedOnlyMask(plane, x + size, y) & 0x60e40000) != 0
						|| (getClipedOnlyMask(plane, x + size, y - (-size + 1)) & 0x78240000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getClipedOnlyMask(plane, x + size, y + sizeOffset) & 0x78e40000) != 0)
						return false;
			} else if (xOffset == 0 && yOffset == -1) {
				if ((getClipedOnlyMask(plane, x, y - 1) & 0x43a40000) != 0
						|| (getClipedOnlyMask(plane, x + size - 1, y - 1) & 0x60e40000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getClipedOnlyMask(plane, x + sizeOffset, y - 1) & 0x63e40000) != 0)
						return false;
			} else if (xOffset == 0 && yOffset == 1) {
				if ((getClipedOnlyMask(plane, x, y + size) & 0x4e240000) != 0
						|| (getClipedOnlyMask(plane, x + (size - 1), y + size) & 0x78240000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getClipedOnlyMask(plane, x + sizeOffset, y + size) & 0x7e240000) != 0)
						return false;
			} else if (xOffset == -1 && yOffset == -1) {
				if ((getClipedOnlyMask(plane, x - 1, y - 1) & 0x43a40000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getClipedOnlyMask(plane, x - 1, y + (-1 + sizeOffset)) & 0x4fa40000) != 0
							|| (getClipedOnlyMask(plane, sizeOffset - 1 + x, y - 1) & 0x63e40000) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == -1) {
				if ((getClipedOnlyMask(plane, x + size, y - 1) & 0x60e40000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getClipedOnlyMask(plane, x + size, sizeOffset + (-1 + y)) & 0x78e40000) != 0
							|| (getClipedOnlyMask(plane, x + sizeOffset, y - 1) & 0x63e40000) != 0)
						return false;
			} else if (xOffset == -1 && yOffset == 1) {
				if ((getClipedOnlyMask(plane, x - 1, y + size) & 0x4e240000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getClipedOnlyMask(plane, x - 1, y + sizeOffset) & 0x4fa40000) != 0
							|| (getClipedOnlyMask(plane, -1 + (x + sizeOffset), y + size) & 0x7e240000) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == 1) {
				if ((getClipedOnlyMask(plane, x + size, y + size) & 0x78240000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getClipedOnlyMask(plane, x + sizeOffset, y + size) & 0x7e240000) != 0
							|| (getClipedOnlyMask(plane, x + size, y + sizeOffset) & 0x78e40000) != 0)
						return false;
			}
		}
		return true;
	}

	public static final boolean checkWalkStep(int plane, int x, int y, int dir, int size) {
		return checkWalkStep(plane, x, y, Utils.DIRECTION_DELTA_X[dir], Utils.DIRECTION_DELTA_Y[dir], size);
	}

	public static final boolean checkWalkStep(int plane, int x, int y, int xOffset, int yOffset, int size) {
		if (size == 1) {
			int mask = getMask(plane, x + xOffset, y + yOffset);
			if (xOffset == -1 && yOffset == 0)
				return (mask
						& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST)) == 0;
			if (xOffset == 1 && yOffset == 0)
				return (mask
						& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_WEST)) == 0;
			if (xOffset == 0 && yOffset == -1)
				return (mask
						& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH)) == 0;
			if (xOffset == 0 && yOffset == 1)
				return (mask
						& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH)) == 0;
			if (xOffset == -1 && yOffset == -1)
				return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH
						| Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) == 0
						&& (getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
								| Flags.WALLOBJ_EAST)) == 0
						&& (getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
								| Flags.WALLOBJ_NORTH)) == 0;
			if (xOffset == 1 && yOffset == -1)
				return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH
						| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) == 0
						&& (getMask(plane, x + 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
								| Flags.WALLOBJ_WEST)) == 0
						&& (getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
								| Flags.WALLOBJ_NORTH)) == 0;
			if (xOffset == -1 && yOffset == 1)
				return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST
						| Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) == 0
						&& (getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
								| Flags.WALLOBJ_EAST)) == 0
						&& (getMask(plane, x, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
								| Flags.WALLOBJ_SOUTH)) == 0;
			if (xOffset == 1 && yOffset == 1)
				return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH
						| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) == 0
						&& (getMask(plane, x + 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
								| Flags.WALLOBJ_WEST)) == 0
						&& (getMask(plane, x, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
								| Flags.WALLOBJ_SOUTH)) == 0;
		} else if (size == 2) {
			if (xOffset == -1 && yOffset == 0)
				return (getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) == 0
						&& (getMask(plane, x - 1, y + 1)
								& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST
										| Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) == 0;
			if (xOffset == 1 && yOffset == 0)
				return (getMask(plane, x + 2, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) == 0
						&& (getMask(plane, x + 2, y + 1)
								& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH
										| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
			if (xOffset == 0 && yOffset == -1)
				return (getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) == 0
						&& (getMask(plane, x + 1, y - 1)
								& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH
										| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) == 0;
			if (xOffset == 0 && yOffset == 1)
				return (getMask(plane, x, y + 2) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) == 0
						&& (getMask(plane, x + 1, y + 2)
								& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH
										| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
			if (xOffset == -1 && yOffset == -1)
				return (getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST
						| Flags.CORNEROBJ_SOUTHEAST)) == 0
						&& (getMask(plane, x - 1, y - 1)
								& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH
										| Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) == 0
						&& (getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
								| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_WEST
								| Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) == 0;
			if (xOffset == 1 && yOffset == -1)
				return (getMask(plane, x + 1, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST
						| Flags.CORNEROBJ_NORTHEAST)) == 0
						&& (getMask(plane, x + 2, y - 1)
								& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH
										| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) == 0
						&& (getMask(plane, x + 2, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
								| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST
								| Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
			if (xOffset == -1 && yOffset == 1)
				return (getMask(plane, x - 1, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST
						| Flags.CORNEROBJ_SOUTHEAST)) == 0
						&& (getMask(plane, x - 1, y + 1)
								& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST
										| Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) == 0
						&& (getMask(plane, x, y + 2) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
								| Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST
								| Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
			if (xOffset == 1 && yOffset == 1)
				return (getMask(plane, x + 1, y + 2) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHEAST
						| Flags.CORNEROBJ_SOUTHWEST)) == 0
						&& (getMask(plane, x + 2, y + 2)
								& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH
										| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) == 0
						&& (getMask(plane, x + 1, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK
								| Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST
								| Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
		} else {
			if (xOffset == -1 && yOffset == 0) {
				if ((getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) != 0
						|| (getMask(plane, x - 1, -1 + (y + size))
								& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST
										| Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getMask(plane, x - 1, y + sizeOffset) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK
							| Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH
							| Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == 0) {
				if ((getMask(plane, x + size, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) != 0
						|| (getMask(plane, x + size, y - (-size + 1))
								& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH
										| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getMask(plane, x + size, y + sizeOffset) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK
							| Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST
							| Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
						return false;
			} else if (xOffset == 0 && yOffset == -1) {
				if ((getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) != 0
						|| (getMask(plane, x + size - 1, y - 1)
								& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH
										| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getMask(plane, x + sizeOffset, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK
							| Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_WEST
							| Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) != 0)
						return false;
			} else if (xOffset == 0 && yOffset == 1) {
				if ((getMask(plane, x, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) != 0
						|| (getMask(plane, x + (size - 1), y + size)
								& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH
										| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getMask(plane, x + sizeOffset, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK
							| Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST
							| Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
						return false;
			} else if (xOffset == -1 && yOffset == -1) {
				if ((getMask(plane, x - 1, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getMask(plane, x - 1, y + (-1 + sizeOffset)) & (Flags.FLOOR_BLOCKSWALK
							| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST
							| Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) != 0
							|| (getMask(plane, sizeOffset - 1 + x, y - 1) & (Flags.FLOOR_BLOCKSWALK
									| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST
									| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == -1) {
				if ((getMask(plane, x + size, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getMask(plane, x + size, sizeOffset + (-1 + y)) & (Flags.FLOOR_BLOCKSWALK
							| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH
							| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) != 0
							|| (getMask(plane, x + sizeOffset, y - 1) & (Flags.FLOOR_BLOCKSWALK
									| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST
									| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) != 0)
						return false;
			} else if (xOffset == -1 && yOffset == 1) {
				if ((getMask(plane, x - 1, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getMask(plane, x - 1, y + sizeOffset) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK
							| Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH
							| Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) != 0
							|| (getMask(plane, -1 + (x + sizeOffset), y + size) & (Flags.FLOOR_BLOCKSWALK
									| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH
									| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == 1) {
				if ((getMask(plane, x + size, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK
						| Flags.OBJ | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getMask(plane, x + sizeOffset, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK
							| Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST
							| Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) != 0
							|| (getMask(plane, x + size, y + sizeOffset) & (Flags.FLOOR_BLOCKSWALK
									| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH
									| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
						return false;
			}
		}
		return true;
	}

	public static final boolean containsPlayer(String username) {
		for (Player p2 : players) {
			if (p2 == null)
				continue;
			if (p2.getUsername().equals(username))
				return true;
		}
		return false;
	}

	public static Player getPlayer(String username) {
		for (Player player : getPlayers()) {
			if (player == null)
				continue;
			if (player.getUsername().equals(username))
				return player;
		}
		return null;
	}

	public static final Player getPlayerByDisplayName(String username) {
		String formatedUsername = Utils.formatPlayerNameForDisplay(username);
		for (Player player : getPlayers()) {
			if (player == null)
				continue;
			if (player.getUsername().equalsIgnoreCase(formatedUsername)
					|| player.getDisplayName().equalsIgnoreCase(formatedUsername))
				return player;
		}
		return null;
	}

	public static final EntityList<Player> getPlayers() {
		return players;
	}

	public static final EntityList<NPC> getNPCs() {
		return npcs;
	}

	public static int exiting_delay;
	public static long exiting_start;

	public static final void safeShutdown(final boolean restart, int delay) {
		if (exiting_start != 0) {
			System.err.println("You cannot launch another restart if there's already one running!");
			return;
		}
		exiting_start = Utils.currentTimeMillis();
		exiting_delay = delay;
		for (Player player : World.getPlayers()) {
			if (player == null || !player.isActive() || player.hasFinished())
				continue;
			player.getPackets().sendSystemUpdate(delay);
		}
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					for (Player player : World.getPlayers()) {
						if (player == null || !player.isActive())
							continue;
						player.realFinish();
					}
					IPBanL.save();
					IPMute.save();
					MACBan.save();
					PkRank.save();
					GrandExchange.save();
					ReferralHandler.save();
					Lottery.save();
					WeeklyTopRanking.save();
					DonationRank.save();
					VoteHiscores.save();
					WellOfGoodWill.save();
					DTRank.save();
					if (restart)
						ServerLauncher.restartEmulator();
					else
						ServerLauncher.restartEmulator();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, delay, TimeUnit.SECONDS);
	}

	public static final void safeShutdown(int delay) {
		if (exiting_start != 0)
			return;
		exiting_start = Utils.currentTimeMillis();
		exiting_delay = delay;
		for (Player player : World.getPlayers()) {
			if (player == null || player.hasFinished())
				continue;
			player.getPackets().sendSystemUpdate(delay);
		}
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					for (Player player : World.getPlayers()) {
						if (player == null)
							continue;
						player.realFinish();
					}
					IPBanL.save();
					PkRank.save();
					GrandExchange.save();
					ReferralHandler.save();
					Lottery.save();
					WeeklyTopRanking.save();
					WellOfGoodWill.save();
					ServerLauncher.restartEmulator();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, delay, TimeUnit.SECONDS);
	}

	public static final boolean isSpawnedObject(WorldObject object) {
		return getRegion(object.getRegionId()).getSpawnedObjects().contains(object);
	}

	public static final boolean isSpawnedObject(Player player, WorldObject object) {
		return getRegion(player.getRegionId()).getSpawnedObjects().contains(object);
	}

	public static final void spawnObject(WorldObject object) {
		getRegion(object.getRegionId()).spawnObject(object, object.getPlane(), object.getXInRegion(),
				object.getYInRegion(), false);
	}

	public static final void spawnObject(Player player, WorldObject object) {
		getRegion(player.getRegionId()).spawnObject(player, object, object.getPlane(), object.getXInRegion(),
				object.getYInRegion(), false);
	}

	public static final void unclipTile(WorldTile tile) {
		getRegion(tile.getRegionId()).unclip(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion());
	}

	public static final void removeObject(WorldObject object, boolean removeClip) {
		getRegion(object.getRegionId()).removeObject(object, object.getPlane(), object.getXInRegion(),
				object.getYInRegion(), removeClip);
	}

	public static final void removeObject(WorldObject object) {
		getRegion(object.getRegionId()).removeObject(object, object.getPlane(), object.getXInRegion(),
				object.getYInRegion());
	}

	public static final void removeObject(Player player, WorldObject object, boolean removeClip) {
		getRegion(player.getRegionId()).removeObject(player, object, object.getPlane(), object.getXInRegion(),
				object.getYInRegion(), removeClip);
	}

	public static final void removeObject(Player player, WorldObject object) {
		getRegion(player.getRegionId()).removeObject(player, object, object.getPlane(), object.getXInRegion(),
				object.getYInRegion());
	}

	public static final void spawnObjectTemporary(final WorldObject object, long time) {
		spawnObject(object);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					if (!World.isSpawnedObject(object))
						return;
					removeObject(object);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, time, TimeUnit.MILLISECONDS);
	}

	public static final void spawnObjectTemporary(final Player player, final WorldObject object, long time) {
		spawnObject(player, object);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					if (!World.isSpawnedObject(player, object)) {
						return;
					}
					removeObject(player, object);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, time, TimeUnit.MILLISECONDS);
	}

	public static final boolean removeObjectTemporary(final WorldObject object, long time, boolean removeClip) {
		removeObject(object, removeClip);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					spawnObject(object);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, time, TimeUnit.MILLISECONDS);
		return true;
	}

	public static final boolean removeObjectTemporary(final Player player, final WorldObject object, long time,
			boolean removeClip) {
		removeObject(player, object, removeClip);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					spawnObject(player, object);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, time, TimeUnit.MILLISECONDS);
		return true;
	}

	public static final void spawnTempGroundObject(final WorldObject object, final int replaceId, long time,
			final boolean removeClip) {
		spawnObject(object);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					removeObject(object, removeClip);
					addGroundItem(new Item(replaceId), object, null, false, 180);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, time, TimeUnit.MILLISECONDS);
	}

	public static final WorldObject getStandartObject(WorldTile tile) {
		return getRegion(tile.getRegionId()).getStandartObject(tile.getPlane(), tile.getXInRegion(),
				tile.getYInRegion());
	}

	public static final WorldObject getObjectWithType(WorldTile tile, int type) {
		return getRegion(tile.getRegionId()).getObjectWithType(tile.getPlane(), tile.getXInRegion(),
				tile.getYInRegion(), type);
	}

	public static final WorldObject getObjectWithSlot(WorldTile tile, int slot) {
		return getRegion(tile.getRegionId()).getObjectWithSlot(tile.getPlane(), tile.getXInRegion(),
				tile.getYInRegion(), slot);
	}

	public static final boolean containsObjectWithId(WorldTile tile, int id) {
		return getRegion(tile.getRegionId()).containsObjectWithId(tile.getPlane(), tile.getXInRegion(),
				tile.getYInRegion(), id);
	}

	public static final WorldObject getObjectWithId(WorldTile tile, int id) {
		return getRegion(tile.getRegionId()).getObjectWithId(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion(),
				id);
	}

	public static final void addGroundItem(final Item item, final WorldTile tile) {
		addGroundItem(item, tile, null, false, -1, 2, -1);
	}

	public static final void addGroundItem(final Item item, final WorldTile tile, int publicTime) {
		addGroundItem(item, tile, null, false, -1, 2, publicTime);
	}

	public static final void addGroundItem(final Item item, final WorldTile tile, final Player owner, boolean invisible,
			long hiddenTime) {
		addGroundItem(item, tile, owner, invisible, hiddenTime, 2, 60);
	}

	public static final FloorItem addGroundItem(final Item item, final WorldTile tile, final Player owner,
			boolean invisible, long hiddenTime, int type) {
		return addGroundItem(item, tile, owner, invisible, hiddenTime, type, 60);
	}

	public static final void turnPublic(FloorItem floorItem, int publicTime) {
		if (!floorItem.isInvisible())
			return;
		int regionId = floorItem.getTile().getRegionId();
		final Region region = getRegion(regionId);
		if (!region.getGroundItemsSafe().contains(floorItem))
			return;
		Player realOwner = floorItem.hasOwner() ? World.getPlayer(floorItem.getOwner()) : null;
		floorItem.setInvisible(false);
		for (Player player : players) {
			if (player == null || player == realOwner || player.hasFinished()
					|| player.getPlane() != floorItem.getTile().getPlane()
					|| !player.getMapRegionsIds().contains(regionId) || !ItemConstants.isTradeable(floorItem))
				continue;
			player.getPackets().sendGroundItem(floorItem);
		}
		if (publicTime != -1)
			removeGroundItem(floorItem, publicTime);
	}

	@Deprecated
	public static final void addGroundItemForever(Item item, final WorldTile tile) {
		int regionId = tile.getRegionId();
		final FloorItem floorItem = new FloorItem(item, new WorldTile(tile.getX(), tile.getY(), tile.getPlane()), true);
		final Region region = getRegion(tile.getRegionId());
		region.getGroundItemsSafe().add(floorItem);
		for (Player player : players) {
			if (player == null || player.hasFinished() || player.getPlane() != floorItem.getTile().getPlane()
					|| !player.getMapRegionsIds().contains(regionId))
				continue;
			player.getPackets().sendGroundItem(floorItem);
		}
	}

	/*
	 * type 0 - if not tradeable type 1 - if destroyable type 2 - no
	 */
	public static final FloorItem addGroundItem(final Item item, final WorldTile tile, final Player owner,
			boolean invisible, long hiddenTime, int type, final int publicTime) {
		final FloorItem floorItem = new FloorItem(item, tile, owner, false, invisible);
		final Region region = getRegion(tile.getRegionId());
		if (type == 1) {
			if (ItemConstants.isTradeable(item) || ItemConstants.turnCoins(item))
				region.getGroundItemsSafe().add(floorItem);
			if (invisible) {
				if (owner != null) {
					if (ItemConstants.isTradeable(item) || ItemConstants.turnCoins(item))
						owner.getPackets().sendGroundItem(floorItem);
				}
				if (hiddenTime != -1) {
					CoresManager.slowExecutor.schedule(new Runnable() {
						@Override
						public void run() {
							try {
								turnPublic(floorItem, publicTime);
							} catch (Throwable e) {
								Logger.handle(e);
							}
						}
					}, hiddenTime, TimeUnit.SECONDS);
				}
			} else {
				int regionId = tile.getRegionId();
				for (Player player : players) {
					if (player == null || player.hasFinished() || player.getPlane() != tile.getPlane()
							|| !player.getMapRegionsIds().contains(regionId))
						continue;
					player.getPackets().sendGroundItem(floorItem);
				}
				if (publicTime != -1)
					removeGroundItem(floorItem, publicTime);
			}
		} else {
			region.getGroundItemsSafe().add(floorItem);
			if (invisible) {
				if (owner != null) {
					owner.getPackets().sendGroundItem(floorItem);
				}
				if (hiddenTime != -1) {
					CoresManager.slowExecutor.schedule(new Runnable() {
						@Override
						public void run() {
							try {
								turnPublic(floorItem, publicTime);
							} catch (Throwable e) {
								Logger.handle(e);
							}
						}
					}, hiddenTime, TimeUnit.SECONDS);
				}
			} else {
				int regionId = tile.getRegionId();
				for (Player player : players) {
					if (player == null || player.hasFinished() || player.getPlane() != tile.getPlane()
							|| !player.getMapRegionsIds().contains(regionId) || !ItemConstants.isTradeable(item))
						continue;
					player.getPackets().sendGroundItem(floorItem);
				}
				if (publicTime != -1)
					removeGroundItem(floorItem, publicTime);
			}
		}
		return floorItem;
	}

	public static final void updateGroundItem(Item item, final WorldTile tile, final Player owner) {
		final FloorItem floorItem = World.getRegion(tile.getRegionId()).getGroundItem(item.getId(), tile, owner);
		if (floorItem == null) {
			addGroundItem(item, tile, owner, true, 60);
			return;
		}
		floorItem.setAmount(floorItem.getAmount() + item.getAmount());
	}

	public static final void updateGroundItem(Item item, final WorldTile tile, final Player owner, final int time,
			final int type) {
		updateGroundItem(item, tile, owner, time, type, false);
	}

	public static final void updateGroundItem(Item item, final WorldTile tile, final Player owner, final int time,
			final int type, boolean npcDrop) {
		final FloorItem floorItem = World.getRegion(tile.getRegionId()).getGroundItem(item.getId(), tile, owner);
		boolean stackable = item.getDefinitions().isStackable();
		if (floorItem == null) {
			if (!stackable && item.getAmount() > 1) {
				for (int i = 0; i < item.getAmount(); i++) {
					if (!npcDrop || !owner.getPetLootManager().canLootItem(new Item(item.getId(), 1)))
						addGroundItem(item, tile, owner, true, time, type);
				}
			} else {
				if (!npcDrop || !owner.getPetLootManager().canLootItem(item))
					addGroundItem(item, tile, owner, true, time, type);
			}
			return;
		}

		if (floorItem.getDefinitions().isStackable()) {
			if (floorItem.getAmount() + item.getAmount() < 0) {
				int totalAmount = Integer.MAX_VALUE - floorItem.getAmount();
				floorItem.setAmount(Integer.MAX_VALUE);
				item.setAmount(item.getAmount() - totalAmount);
				if (!npcDrop || !owner.getPetLootManager().canLootItem(item))
					addGroundItem(item, tile, owner, true, time, type);
				owner.getPackets().sendRemoveGroundItem(floorItem);
				owner.getPackets().sendGroundItem(floorItem);
			} else
				floorItem.setAmount(floorItem.getAmount() + item.getAmount());
			owner.getPackets().sendRemoveGroundItem(floorItem);
			owner.getPackets().sendGroundItem(floorItem);
		} else {
			for (int i = 0; i < item.getAmount(); i++) {
				if (!npcDrop || !owner.getPetLootManager().canLootItem(new Item(item.getId(), 1)))
					addGroundItem(item, tile, owner, true, time, type);
			}
		}
	}

	private static final void removeGroundItem(final FloorItem floorItem, long publicTime) {
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					int regionId = floorItem.getTile().getRegionId();
					Region region = getRegion(regionId);
					if (!region.getGroundItemsSafe().contains(floorItem))
						return;
					region.getGroundItemsSafe().remove(floorItem);
					for (Player player : World.getPlayers()) {
						if (player == null

								|| player.hasFinished() || player.getPlane() != floorItem.getTile().getPlane()
								|| !player.getMapRegionsIds().contains(regionId))
							continue;
						player.getPackets().sendRemoveGroundItem(floorItem);
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, publicTime, TimeUnit.SECONDS);
	}

	public static final boolean removeGroundItem(Player player, FloorItem floorItem) {
		return removeGroundItem(player, floorItem, true);
	}

	public static final void spawnTemporaryObject(final WorldObject object, long time) {
		spawnTemporaryObject(object, time, false);
	}

	public static final void spawnTemporaryObject(final WorldObject object, long time, final boolean clip) {
		spawnObject(object);
		CoresManager.get().slowExecutor().schedule(new Runnable() {
			@Override
			public void run() {
				try {
					if (!World.isSpawnedObject(object))
						return;
					removeObject(object);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, time, TimeUnit.MILLISECONDS);
	}

	public static final boolean removeGroundItem(Player player, final FloorItem floorItem, boolean add) {
		int regionId = floorItem.getTile().getRegionId();
		Region region = getRegion(regionId);
		WorldTile playerTile = new WorldTile(player.getX(), player.getY(), player.getPlane());
		if (!region.getGroundItemsSafe().contains(floorItem))
			return false;
		int amount = floorItem.getAmount();
		LoggingSystem.logItemPickup(player, floorItem, playerTile);
		if (floorItem.getId() == 995) {
			amount = floorItem.getAmount();
			int leftOver = 0;
			int inventoryLeftOver = 0;
			if (player.getMoneyPouch().getTotal() + amount > Integer.MAX_VALUE
					|| player.getMoneyPouch().getTotal() + amount < 0) {
				player.getPackets().sendGameMessage("Your money pouch is not big enough to hold that much cash.");
				leftOver = Integer.MAX_VALUE - player.getMoneyPouch().getTotal();
				amount = amount - leftOver;
				if (player.getMoneyPouch().getTotal() != Integer.MAX_VALUE) {
					player.getPackets().sendRunScript(5561, 1, leftOver);
					player.getMoneyPouch().setTotal(Integer.MAX_VALUE);
					player.getPackets().sendGameMessage(
							Utils.getFormattedNumber(leftOver, ',') + " coins have been added to your money pouch.");
					player.getMoneyPouch().refresh();
					floorItem.setAmount(Integer.MAX_VALUE - leftOver);
				}
				if (player.getInventory().getNumberOf(995) + amount > Integer.MAX_VALUE
						|| player.getInventory().getNumberOf(995) + amount < 0) {
					if (!player.getInventory().hasFreeSlots()) {
						player.getPackets().sendGameMessage("You don't have enough inventory space.");
						return false;
					}
					inventoryLeftOver = Integer.MAX_VALUE - player.getInventory().getNumberOf(995);
					amount = amount - inventoryLeftOver;
					if (player.getInventory().getNumberOf(995) != Integer.MAX_VALUE) {
						player.getInventory().deleteItem(995, Integer.MAX_VALUE);
						player.getInventory().addItem(995, Integer.MAX_VALUE);
					}
					floorItem.setAmount(amount);
					player.getPackets().sendRemoveGroundItem(floorItem);
					player.getPackets().sendGroundItem(floorItem);
					return false;
				} else {
					if (!player.getInventory().hasFreeSlots()) {
						player.getPackets().sendGameMessage("You don't have enough inventory space.");
						return false;
					}
					player.getInventory().addItem(995, amount);
				}
				region.getGroundItemsSafe().remove(floorItem);
				if (floorItem.isInvisible()) {
					player.getPackets().sendRemoveGroundItem(floorItem);
					return true;
				} else {
					for (Player p2 : World.getPlayers()) {
						if (p2 == null || p2.hasFinished() || p2.getPlane() != floorItem.getTile().getPlane()
								|| !p2.getMapRegionsIds().contains(regionId))
							continue;
						p2.getPackets().sendRemoveGroundItem(floorItem);
					}
					if (floorItem.isForever()) {
						CoresManager.slowExecutor.schedule(new Runnable() {
							@Override
							public void run() {
								try {
									addGroundItemForever(floorItem, floorItem.getTile());
								} catch (Throwable e) {
									Logger.handle(e);
								}
							}
						}, 60, TimeUnit.SECONDS);
					}
					return true;
				}
			}
			if (player.getMoneyPouch().getTotal() == Integer.MAX_VALUE)
				return false;
			if (amount > 1) {
				player.getPackets().sendGameMessage(
						Utils.getFormattedNumber(amount, ',') + " coins have been added to your money pouch.");
			} else {
				player.getPackets().sendGameMessage("One coin has been added to your money pouch.");
			}
			player.getPackets().sendRunScript(5561, 1, amount);
			player.getMoneyPouch().setTotal(player.getMoneyPouch().getTotal() + amount);
			player.getMoneyPouch().refresh();
			region.getGroundItemsSafe().remove(floorItem);
			if (floorItem.isInvisible()) {
				player.getPackets().sendRemoveGroundItem(floorItem);
				return true;
			} else {
				for (Player p2 : World.getPlayers()) {
					if (p2 == null || p2.hasFinished() || p2.getPlane() != floorItem.getTile().getPlane()
							|| !p2.getMapRegionsIds().contains(regionId))
						continue;
					p2.getPackets().sendRemoveGroundItem(floorItem);
				}
				if (floorItem.isForever()) {
					CoresManager.slowExecutor.schedule(new Runnable() {
						@Override
						public void run() {
							try {
								addGroundItemForever(floorItem, floorItem.getTile());
							} catch (Throwable e) {
								Logger.handle(e);
							}
						}
					}, 60, TimeUnit.SECONDS);
				}
				return true;
			}
		} else if (floorItem.getId() != 995) {
			if (player.getInventory().getNumberOf(floorItem.getId()) + floorItem.getAmount() > Integer.MAX_VALUE
					|| player.getInventory().getNumberOf(floorItem.getId()) + floorItem.getAmount() < 0) {
				amount = floorItem.getAmount();
				amount = Integer.MAX_VALUE - player.getInventory().getNumberOf(floorItem.getAmount());
				floorItem.setAmount(Integer.MAX_VALUE - amount);
				player.getInventory().deleteItem(floorItem.getId(), Integer.MAX_VALUE);
				player.getInventory().addItem(floorItem.getId(), Integer.MAX_VALUE);
				player.sendMessage("Inventory full. To make more room, sell, drop or bank something.");
				return false;
			} else if (player.getInventory().getFreeSlots() == 0 && (floorItem.getDefinitions().isStackable()
					&& !player.getInventory().containsItem(floorItem.getId(), 1))) {
				player.sendMessage("Inventory full. To make more room, sell, drop or bank something.");
				return false;
			} else if (player.getInventory().getFreeSlots() == 0 && (!floorItem.getDefinitions().isStackable())) {
				player.sendMessage("Inventory full. To make more room, sell, drop or bank something.");
				return false;
			}
			if (player.getFreezeDelay() >= Utils.currentTimeMillis()) {
				if (!floorItem.getTile().matches(playerTile))
					player.setNextAnimation(new Animation(537));
			}
			region.getGroundItemsSafe().remove(floorItem);
			if (add)
				player.getInventory().addItem(new Item(floorItem.getId(), floorItem.getAmount()));
			if (floorItem.isInvisible()) {
				player.getPackets().sendRemoveGroundItem(floorItem);
				return true;
			} else {
				for (Player p2 : World.getPlayers()) {
					if (p2 == null || p2.hasFinished() || p2.getPlane() != floorItem.getTile().getPlane()
							|| !p2.getMapRegionsIds().contains(regionId))
						continue;
					p2.getPackets().sendRemoveGroundItem(floorItem);
				}
				if (floorItem.isForever()) {
					CoresManager.slowExecutor.schedule(new Runnable() {
						@Override
						public void run() {
							try {
								addGroundItemForever(floorItem, floorItem.getTile());
							} catch (Throwable e) {
								Logger.handle(e);
							}
						}
					}, 60, TimeUnit.SECONDS);
				}
			}
		}
		return true;
	}

	public static final void sendObjectAnimation(WorldObject object, Animation animation) {
		sendObjectAnimation(null, object, animation);
	}

	public static final void sendObjectAnimation(Entity creator, WorldObject object, Animation animation) {
		if (creator == null) {
			for (Player player : World.getPlayers()) {
				if (player == null || player.hasFinished() || !player.withinDistance(object))
					continue;
				player.getPackets().sendObjectAnimation(object, animation);
			}
		} else {
			for (int regionId : creator.getMapRegionsIds()) {
				List<Integer> playersIndexes = getRegion(regionId).getPlayerIndexes();
				if (playersIndexes == null)
					continue;
				for (Integer playerIndex : playersIndexes) {
					Player player = players.get(playerIndex);
					if (player == null || player.hasFinished() || !player.withinDistance(object))
						continue;
					player.getPackets().sendObjectAnimation(object, animation);
				}
			}
		}
	}

	public static final void sendGraphics(Entity creator, Graphics graphics, WorldTile tile) {
		if (creator == null) {
			for (Player player : World.getPlayers()) {
				if (player == null || player.hasFinished() || !player.withinDistance(tile))
					continue;
				player.getPackets().sendGraphics(graphics, tile);
			}
		} else {
			for (int regionId : creator.getMapRegionsIds()) {
				List<Integer> playersIndexes = getRegion(regionId).getPlayerIndexes();
				if (playersIndexes == null)
					continue;
				for (Integer playerIndex : playersIndexes) {
					Player player = players.get(playerIndex);
					if (player == null || player.hasFinished() || !player.withinDistance(tile))
						continue;
					player.getPackets().sendGraphics(graphics, tile);
				}
			}
		}
	}

	/**
	 * Sends the Loot Beam graphics.
	 * 
	 * @param player
	 *            The player to send to.
	 * @param tile
	 *            The tile to send on.
	 */

	public static final void sendLootbeam(Player player, WorldTile tile) {
		if (player == null || player.hasFinished() || !player.withinDistance(tile))
			return;
		player.getPackets().sendGraphics(new Graphics(4422), tile);
		player.sendMessage(Colors.orange + "<shad=000000>A golden beam shines over one of your items.", true);
	}

	public static final void sendProjectile(Entity shooter, Entity receiver, int gfxId, int startHeight, int endHeight,
			int speed, int delay, int curve) {
		for (int regionId : shooter.getMapRegionsIds()) {
			List<Integer> playersIndexes = getRegion(regionId).getPlayerIndexes();
			if (playersIndexes == null)
				continue;
			for (Integer playerIndex : playersIndexes) {
				Player player = players.get(playerIndex);
				if (player == null

						|| player.hasFinished()
						|| (!player.withinDistance(shooter) && !player.withinDistance(receiver)))
					continue;
				int size = shooter.getSize();
				int distance = Utils.getDistance(shooter, receiver);
				int startOffsetDistance = distance > 2 ? 0 : 11;
				int startOffsetDistance2 = 0;
				player.getPackets().sendProjectile(receiver,
						new WorldTile(shooter.getCoordFaceX(size), shooter.getCoordFaceY(size), shooter.getPlane()),
						receiver, gfxId, startHeight, endHeight, speed, delay, curve,
						receiver instanceof Player ? startOffsetDistance : startOffsetDistance2, size);
			}
		}
	}

	public static final void sendProjectile(Entity shooter, WorldTile startTile, WorldTile receiver, int gfxId,
			int startHeight, int endHeight, int speed, int delay, int curve, int startDistanceOffset) {
		for (int regionId : shooter.getMapRegionsIds()) {
			List<Integer> playersIndexes = getRegion(regionId).getPlayerIndexes();
			if (playersIndexes == null)
				continue;
			for (Integer playerIndex : playersIndexes) {
				Player player = players.get(playerIndex);
				if (player == null

						|| player.hasFinished()
						|| (!player.withinDistance(shooter) && !player.withinDistance(receiver)))
					continue;
				player.getPackets().sendProjectile(null, startTile, receiver, gfxId, startHeight, endHeight, speed,
						delay, curve, startDistanceOffset, shooter.getSize());
			}
		}
	}

	public static final void sendProjectile(WorldTile shooter, Entity receiver, int gfxId, int startHeight,
			int endHeight, int speed, int delay, int curve, int startDistanceOffset) {
		for (int regionId : receiver.getMapRegionsIds()) {
			List<Integer> playersIndexes = getRegion(regionId).getPlayerIndexes();
			if (playersIndexes == null)
				continue;
			for (Integer playerIndex : playersIndexes) {
				Player player = players.get(playerIndex);
				if (player == null

						|| player.hasFinished()
						|| (!player.withinDistance(shooter) && !player.withinDistance(receiver)))
					continue;
				player.getPackets().sendProjectile(receiver, shooter, receiver, gfxId, startHeight, endHeight, speed,
						delay, curve, startDistanceOffset, 1);
			}
		}
	}

	public static final void sendProjectile(Entity shooter, WorldTile receiver, int gfxId, int startHeight,
			int endHeight, int speed, int delay, int curve, int startDistanceOffset) {
		for (int regionId : shooter.getMapRegionsIds()) {
			List<Integer> playersIndexes = getRegion(regionId).getPlayerIndexes();
			if (playersIndexes == null)
				continue;
			for (Integer playerIndex : playersIndexes) {
				Player player = players.get(playerIndex);
				if (player == null

						|| player.hasFinished()
						|| (!player.withinDistance(shooter) && !player.withinDistance(receiver)))
					continue;
				player.getPackets().sendProjectile(null, shooter, receiver, gfxId, startHeight, endHeight, speed, delay,
						curve, startDistanceOffset, shooter.getSize());
			}
		}
	}

	public static final void sendProjectile(Entity shooter, Entity receiver, int gfxId, int startHeight, int endHeight,
			int speed, int delay, int curve, int startDistanceOffset) {
		for (int regionId : shooter.getMapRegionsIds()) {
			List<Integer> playersIndexes = getRegion(regionId).getPlayerIndexes();
			if (playersIndexes == null)
				continue;
			for (Integer playerIndex : playersIndexes) {
				Player player = players.get(playerIndex);
				if (player == null || player.hasFinished()
						|| (!player.withinDistance(shooter) && !player.withinDistance(receiver)))
					continue;
				int size = shooter.getSize();
				player.getPackets().sendProjectile(receiver, shooter, receiver, gfxId, startHeight, endHeight, speed,
						delay, curve, startDistanceOffset, size);
			}
		}
	}

	public static final boolean isMultiArea(WorldTile tile) {
		int destX = tile.getX();
		int destY = tile.getY();
		return (destX >= 3462 && destX <= 3511 && destY >= 9481 && destY <= 9521 && tile.getPlane() == 0) // kalphite
																											// lair
				|| (destX >= 4540 && destX <= 4799 && destY >= 5052 && destY <= 5183 && tile.getPlane() == 0) // thzaar
																												// city
				|| (destX >= 1721 && destX <= 1791 && destY >= 5123 && destY <= 5249)
				|| (destX >= 2250 && destX <= 2280 && destY >= 4670 && destY <= 4720)
				|| (destX >= 2987 && destX <= 3006 && destY >= 3912 && destY <= 3937)
				|| (destX >= 2895 && destX <= 2937 && destY >= 4430 && destY <= 4472)
				|| (destX >= 2245 && destX <= 2295 && destY >= 4675 && destY <= 4720)
				|| (destX >= 2450 && destX <= 3520 && destY >= 9450 && destY <= 9550)
				|| (destX >= 3006 && destX <= 3071 && destY >= 3602 && destY <= 3710)
				|| (destX >= 3134 && destX <= 3192 && destY >= 3519 && destY <= 3646)
				|| (destX >= 2815 && destX <= 2966 && destY >= 5240 && destY <= 5375)// wild
				|| (destX >= 1790 && destX <= 1987 && destY >= 3194 && destY <= 3273) // soul
																						// wars
				|| (destX >= 2840 && destX <= 2950 && destY >= 5190 && destY <= 5230) // godwars
				|| (destX >= 3547 && destX <= 3555 && destY >= 9690 && destY <= 9699)
				|| (destX >= 3136 && destX <= 3327 && destY >= 3519 && destY <= 3607) // WILDY
				|| (destX >= 3190 && destX <= 3327 && destY >= 3648 && destY <= 3839)
				|| (destX >= 3200 && destX <= 3390 && destY >= 3840 && destY <= 3967)
				|| (destX >= 2992 && destX <= 3007 && destY >= 3912 && destY <= 3967)
				|| (destX >= 2946 && destX <= 2959 && destY >= 3816 && destY <= 3831)
				|| (destX >= 3008 && destX <= 3199 && destY >= 3856 && destY <= 3903)
				|| (destX >= 3008 && destX <= 3071 && destY >= 3600 && destY <= 3711)
				|| (destX >= 3072 && destX <= 3327 && destY >= 3608 && destY <= 3647)
				|| (destX >= 2624 && destX <= 2690 && destY >= 2550 && destY <= 2619)
				|| (destX >= 2371 && destX <= 2422 && destY >= 5062 && destY <= 5117)
				|| (destX >= 2896 && destX <= 2927 && destY >= 3595 && destY <= 3630)
				|| (destX >= 2892 && destX <= 2932 && destY >= 4435 && destY <= 4464)
				|| (destX >= 2256 && destX <= 2287 && destY >= 4680 && destY <= 4711)
				// damage dummy
				|| (destX >= 2128 && destX <= 2158 && destY >= 5518 && destY <= 5551)
				|| (destX >= 2863 && destX <= 2878 && destY >= 5350 && destY <= 5372) || KingBlackDragon.atKBD(tile) // KBD
				|| TormentedDemon.atTD(tile) // Tormented demon's area
				|| Bork.atBork(tile) // Bork's area
				|| (destX >= 2970 && destX <= 3000 && destY >= 4365 && destY <= 4400)// corp
				|| (destX >= 3195 && destX <= 3327 && destY >= 3520 && destY <= 3970
						|| (destX >= 2376 && 5127 >= destY && destX <= 2422 & 5168 <= destY))
				|| (destX >= 2374 && destY >= 5129 && destX <= 2424 && destY <= 5168) // pits
				|| (destX >= 2622 && destY >= 5696 && destX <= 2573 && destY <= 5752) // torms
				|| (destX >= 2368 && destY >= 3072 && destX <= 2431 && destY <= 3135) // castlewars
				|| (destX >= 2365 && destY >= 9470 && destX <= 2436 && destY <= 9532) // castlewars
				|| (destX >= 2948 && destY >= 5537 && destX <= 3071 && destY <= 5631) // Risk
																						// ffa.
				|| (destX >= 2756 && destY >= 5537 && destX <= 2879 && destY <= 5631) // Safe
				|| (destX >= 4160 && destY >= 5695 && destX <= 4223 && destY <= 5760) // Glacors

				|| /* South West */(destX >= 2654 && destY >= 3706 && // rock
																		// crabs
				/* North East */destX <= 2723 && destY <= 3754) || /*
																	 * South
																	 * West
																	 */(destX >= 4481 && destY >= 6207 && // araxyve
																										// cave
				/* North East */destX <= 4607 && destY <= 6333) || /*
																	 * South
																	 * West
																	 */(destX >= 2691 && destY >= 9410 && // brimhaven
																										// dungeon
																										// -
																										// dragons
				/* North East */destX <= 2748 && destY <= 9538) || /*
																	 * South
																	 * West
																	 */(destX >= 999 && destY >= 558 && // ascension
																										// dungeon
				/* North East */destX <= 1209 && destY <= 765) || /*
																	 * South
																	 * West
																	 */(destX >= 2955 && destY >= 1735 && // kalphite
																										// king
																										// lair
				/* North East */destX <= 2997 && destY <= 1783) || /*
																	 * South
																	 * West
																	 */(destX >= 3009 && destY >= 5955 && // vorago
																										// borehole
				/* North East */destX <= 3135 && destY <= 6136) || /*
																	 * South
																	 * West
																	 */(destX >= 3218 && destY >= 5079 && // duel
																										// arena
																										// -
																										// summoning
				/* North East */destX <= 3246 && destY <= 5159)

				|| (tile.getX() >= 3011 && tile.getX() <= 3132 && tile.getY() >= 10052 && tile.getY() <= 10175
						&& (tile.getY() >= 10066 || tile.getX() >= 3094)); // forin
																			// dung
	}

	public static final boolean isPvpArea(WorldTile tile) {
		return Wilderness.isAtWild(tile);
	}

	public static final void addPlayer(Player player) {
		players.add(player);
		AntiFlood.add(player.getSession().getIP());
	}

	public static void removePlayer(Player player) {
		for (Player p : players) {
			if (p.getUsername().equalsIgnoreCase(player.getUsername())) {
				players.remove(p);
			}
		}
		AntiFlood.remove(player.getSession().getIP());
	}

	public static void sendWorldMessage(String message, boolean forStaff) {
		for (Player p : World.getPlayers()) {
			if (p == null || !p.isRunning() || p.isYellOff() || (forStaff && p.getRights() == 0))
				continue;
			p.getPackets().sendGameMessage(message);
		}
	}

	public static void addItemsAll(Item item) {
		for (Player p : World.getPlayers()) {
			if (p == null)
				continue;
			p.getInventory().addItemDrop(item.getId(), item.getAmount());
		}
	}

	public static final void sendProjectile(WorldObject object, WorldTile startTile, WorldTile endTile, int gfxId,
			int startHeight, int endHeight, int speed, int delay, int curve, int startOffset) {
		for (Player pl : players) {
			if (pl == null || !pl.withinDistance(object, 20))
				continue;
			pl.getPackets().sendProjectile(null, startTile, endTile, gfxId, startHeight, endHeight, speed, delay, curve,
					startOffset, 1);
		}
	}

	/**
	 * Gets the Players Online amount.
	 * 
	 * @return players online as Integer.
	 */
	public static final int getPlayersOnline() {
		return getPlayers().size();
	}

	/**
	 * Checks for the day of the week.
	 * 
	 * @return dayOfWeek the weekday to return.
	 */
	private static int dayOfWeek() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * Checks if it's the weekend.
	 * 
	 * @return theDayOfWeek.
	 */
	public static boolean isWeekend() {
		return dayOfWeek() == 1 ? true : dayOfWeek() == 6 ? true : dayOfWeek() == 7 ? true : false;
	}

	/**
	 * Long representing the current World time in ticks.
	 */
	public static long currentTime;

	/**
	 * Finds an NPC in the world by its NPC ID.
	 * 
	 * @param id
	 *            The ID to find.
	 * @return The NPC.
	 */
	public static NPC findNPC(int id) {
		NPC npc = null;
		for (NPC n : getNPCs()) {
			if (n == null)
				continue;
			if (n.getId() == id)
				npc = n;
		}
		return npc;
	}

	/**
	 * Finds the NPC by it's id.
	 * 
	 * @param player
	 *            The player searching.
	 * @param id
	 *            The NPC ID to search for.
	 * @return If NPC found.
	 */
	public static NPC findNPC(Player player, int id) {
		for (NPC npc : World.getNPCs()) {
			if (npc == null || npc.getId() != id)
				continue;
			if (npc.getRegionId() == player.getRegionId())
				return npc;
		}
		return null;
	}

	/**
	 * Gets the real object by tile and slot.
	 * 
	 * @param tile
	 *            The worldtile.
	 * @param slot
	 *            The object slot.
	 * @return the Object to return.
	 */
	public static final WorldObject getRealObject(WorldTile tile, int slot) {
		return getRegion(tile.getRegionId()).getRealObject(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion(),
				slot);
	}

	/**
	 * Spawns a temporary world object.
	 * 
	 * @param object
	 *            The object to spawn.
	 * @param time
	 *            The time for it to stay.
	 * @param checkObjectInstance
	 *            checks if the object is in an instance.
	 * @param checkObjectBefore
	 *            checks before adding.
	 */
	public static final void spawnObjectTemporary(final WorldObject object, long time,
			final boolean checkObjectInstance, boolean checkObjectBefore) {
		final WorldObject before = checkObjectBefore ? World.getObjectWithType(object, object.getType()) : null;
		spawnObject(object);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					if (checkObjectInstance && World.getObjectWithId(object, object.getId()) != object)
						return;
					if (before != null)
						spawnObject(before);
					else
						removeObject(object); // this method allows to remove
												// object with
												// just tile and type actualy so
												// the removing
												// object may be diferent and
												// still gets removed
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, time, TimeUnit.MILLISECONDS);
	}

	/**
	 * Sends the players online list on an interface.
	 * 
	 * @param player
	 *            The player to send the list to.
	 */
	public static void playersList(Player player) {
		if (getPlayers().size() >= 308) // Interface stops at 309 <.<
			return;
		player.getInterfaceManager().sendInterface(275);
		for (int i = 0; i < 309; i++)
			player.getPackets().sendIComponentText(275, i, "");
		int number = 0;
		for (Player p5 : World.getPlayers()) {
			if (p5 == null)
				continue;
			number++;
			String titles = "[<col=000000>Player</col>] ";
			String color = (p5.getYellColor() == "ff0000" || p5.getYellColor() == null ? ""
					: "<col=" + p5.getYellColor() + ">");
			if (p5.isExpert() && !p5.isStaff2())
				titles = "[<col=000000>Expert</col>]";
			if (p5.isIronMan() && !p5.isStaff2())
				titles = "[<col=000000>Ironman</col>] <img=10>";
			if (p5.isHCIronMan() && !p5.isStaff2())
				titles = "[<col=FF0000>HC Ironman</col>] <img=11>";
			if (p5.isDonator() && !p5.isStaff2())
				titles = "[<col=8B4513>Bronze Member</col>] <img=19>";
			if (p5.isExtremeDonator() && !p5.isStaff2())
				titles = "[<col=ffffff>Silver Member</col>] <img=22>";
			if (p5.isLegendaryDonator() && !p5.isStaff2())
				titles = "[<col=e6e600>Gold Member</col>] <img=12>";
			if (p5.isSupremeDonator() && !p5.isStaff2())
				titles = "[<col=008000>Platinum Member</col>] <img=21>";
			if (p5.is420Donator() && !p5.isStaff2())
				titles = "[<col=000000><shad=00ff00>420</shad></col>] <img=23>";
			if (p5.isUltimateDonator() && !p5.isStaff2())
				titles = "[<col=00FFFF>Diamond Member</col>] <img=20>";
			if (p5.isSupport())
				titles = "[" + color + "Support</col>] <img=13>";
			if (p5.getRights() == 1) {
				if (p5.getUsername().equalsIgnoreCase("ex0dus"))
					titles = "[" + color + "Head-Mod</col>] <img=0>";
				else
					titles = "[" + color + "Mod</col>] <img=0>";
			}
			if (p5.isSponsorDonator())
				titles = "[<shad=FFD700><col=FF8C00>Sponsor</col></shad>] <img=16>";
			if (p5.isYoutube())
				titles = "[<shad=ff0000><col=000000>Youtube</col></shad>] <img=17>";
			if (p5.isDev())
				titles = "[<shad=0000FF><col=00BFFF>Dev</col></shad>] <img=18>";
			if (p5.getRights() == 2 && !p5.isDev())
				titles = "[" + color + "Admin</col>] <img=1>";
			if (p5.getUsername().equalsIgnoreCase("Zeus"))
				titles = "[" + color + "Owner</col>] <img=1>";
			player.getPackets().sendIComponentText(275, 1, "<shade=B00000><u=000080>Helwyr RSPS players</u>");
			player.getPackets().sendIComponentText(275, 10,
					"Players online: " + World.getPlayersOnline() + " [" + World.getPlayers().size() + "].");
			player.getPackets().sendIComponentText(275, (11 + number),
					titles + p5.getDisplayName() + "</col></shad> - " + "[TP: "
							+ Utils.getTimePlayed(p5.getTimePlayed()) + "] - " + "[Level: "
							+ p5.getSkills().getCombatLevelWithSummoning() + "]");
		}
	}

	/**
	 * Sends a Message to all Players online.
	 * 
	 * @param message
	 *            The message to send.
	 * @param me
	 *            The Player sending the Message.
	 */
	public static void sendWorldYellMessage(String message, Player me) {
		for (Player player : World.getPlayers()) {
			if (player == null || !player.isRunning() || !Settings.yellEnabled())
				continue;
			if (player.getFriendsIgnores().getIgnores().equals(me.getUsername()))
				continue;
			if (player != me && player.isYellOff())
				continue;
			player.getPackets().sendGameMessage(message);
		}
	}

	/**
	 * A String[] containing all World random messages.
	 */
	private static String[] messageString = { "Please report all bugs; glitches & missing content on our forum.",
			"Type ;;ticket in the chatbox if you need assistance from staff.",
			"Join our ;;discord channel for voice/text chat; music & events.",
			"Visit our ';;Forum' for all the latest News & Updates.",
			"You can spend your Trivia points by talking to the Wise Old Man.",
			"Use the Grand Exchange if you can't find an item you're looking for.",
			"You can spend your Vote points by trading Party Pete.",
			"Join our ;;discord channel for voice/text chat; music & events.",
			"Easier Buying; Selling; Trading - in the ';;Market' area.",
			"Remember to ::Vote every 12 hours - receive awesome rewards.",
			"You can add tools to your toolbelt to save inventory space.",
			"You can toggle World/Yell messages in your Account Manager.",
			"You can change your Loyalty Title in your Account Manager.",
			"You can change your Appearence & Gender by typing ';;settings'.",
			"Join our ;;discord channel for voice/text chat; music & events.",
			"Most equipment/potions/food can be auto-bought on the Grand Exchange.",
			"Donation ranks are given when spent over 20$ in total.",
			"Type ;;commands to see a list of all available commands.",
			"Read up on some quick basic help by typing ;;help.",
			"You can reset your Slayer tasks from Kuradal's reward shop.",
			"Zeals earned from Soul Wars are multiplied by 3 on weekends.",
			"Join our ;;discord channel for voice/text chat; music & events.", "Every weekend is double xp weekend.",
			"You can quickly open the Titles manager interface by typing ;;titles.",
			"Type ;;ticket in the chatbox if you need assistance from staff.",
			"Join our ;;discord channel for voice/text chat; music & events.",
			"Use the Grand Exchange if you can't find an item you're looking for.",
			"You can zoom in using page up/page down keys on your keyboard.",
			"You can zoom out by scrolling your mouse wheel while holding shift.",
			"You can change your Loot Beam settings in your Account Manager.",
			"Make a thread on our ';;forum' if you want your donator rank there.",
			"Completing Slayer tasks with a partner earns you both extra EXP.",
			"Progressing rooms in Dungeoneering requires KC, backtracking - doesn't.",
			"Use the Grand Exchange if you can't find an item you're looking for.",
			"Type ;;voted to claim your vote rewards (if you voted successfully).",
			"You can access the Prifddinas city with a total level of at least 2250.",
			"Most items can be auto-bought on the Grand Exchange with a +5% price.",
			"You can use coins on the well at home for 2 hours of double experience.",
			"You can view all your currently active game perks by typing ';;perks'.",
			"You can change your default Home area in your Account Manager.",
			"Join our ;;discord channel for voice/text chat; music & events.",
			"There's a Vote Party every 100 total server votes." };

	/**
	 * A String[] containing all Colors used for Server messages.
	 */
	public static String[] colors = { "3399FF", "00CC99", "003399", "0099CC", "993300", "993333", "9900FF", "7D1616",
			"CC3300", "00FF00", "3399FF" };

	/**
	 * Well of Goodwill.
	 */
	private static int wellAmount;
	private static long wellActiveTime;

	public static int getWellAmount() {
		return wellAmount;
	}

	public static void addWellAmount(String displayName, int amount) {
		wellAmount += amount;
		if (wellAmount < Settings.WELL_MAX_AMOUNT && amount >= 10000000)
			sendWorldMessage(
					"<col=FF0000>" + displayName + " " + "has contributed "
							+ NumberFormat.getNumberInstance(Locale.US).format(amount) + " GP to the Well of Goodwill!",
					false);
	}

	public static void setWellAmount(int amount) {
		wellAmount = amount;
	}

	public static void resetWell() {
		wellAmount = 0;
		sendWorldMessage("<col=FF0000>The Well of Good Will has been reset.", false);
	}

	public static boolean isWellActive() {
		return wellActiveTime != 0 && wellActiveTime > Utils.currentTimeMillis();
	}

	public static void setWellActive(long wellActiveTime) {
		World.wellActiveTime = wellActiveTime;
	}

	public static long getWellActiveTime() {
		return wellActiveTime;
	}

	public static final void spawnTemporaryDivineObject(final WorldObject object, long time, final Player player) {
		spawnObject(object);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					if (!World.isSpawnedObject(object))
						return;
					removeObject(object);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, time, TimeUnit.MILLISECONDS);
	}

	public static final WorldObject getObject(WorldTile tile) {
		return getRegion(tile.getRegionId()).getStandartObject(tile.getPlane(), tile.getXInRegion(),
				tile.getYInRegion());
	}

	public static final boolean removeObjectTemporary(final WorldObject object, long time) {
		removeObject(object);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					spawnObject(object);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, time, TimeUnit.MILLISECONDS);
		return true;
	}

	/**
	 * Checks if the player is at the Ascensiopns Dungeon.
	 * 
	 * @param player
	 *            The player to check for.
	 * @return if is in Monastery of Ascension.
	 */
	public static boolean isAtAscensionDungeon(Player player) {
		int destX = player.getX(), destY = player.getY();
		return /* South West */(destX >= 999 && destY >= 558 &&
		/* North East */destX <= 1209 && destY <= 765);
	}

	/**
	 * Checks if the player is at the Kuradal's Dungeon.
	 * 
	 * @param player
	 *            The player to check for.
	 * @return if is in Kuradal's Dungeon.
	 */
	public static boolean isAtKuradalsDungeon(Player player) {
		int destX = player.getX(), destY = player.getY();
		return /* South West */(destX >= 1591 && destY >= 5241 &&
		/* North East */destX <= 1672 && destY <= 5337);
	}

	public static final void spawnObject(WorldObject object, boolean clip) {
		getRegion(object.getRegionId()).spawnObject(object, object.getPlane(), object.getXInRegion(),
				object.getYInRegion(), false);
	}

	public static void removeProjectiles() {
		synchronized (getRegions()) {
			for (Region region : getRegions().values())
				region.removeProjectiles();
		}
	}

	public static final Projectile sendProjectileNew(WorldTile from, WorldTile to, int graphicId, int startHeight,
			int endHeight, int startTime, double speed, int angle, int slope) {
		return sendProjectile(from, to, false, false, 0, graphicId, startHeight, endHeight, startTime, speed, angle,
				slope);
	}

	public static final Projectile sendProjectile(WorldTile from, WorldTile to, boolean adjustFlyingHeight,
			boolean adjustSenderHeight, int senderBodyPartId, int graphicId, int startHeight, int endHeight,
			int startTime, double speed, int angle, int slope) {
		int fromSizeX, fromSizeY;
		if (from instanceof Entity)
			fromSizeX = fromSizeY = ((Entity) from).getSize();
		else if (from instanceof WorldObject) {
			ObjectDefinitions defs = ((WorldObject) from).getDefinitions();
			fromSizeX = defs.getSizeX();
			fromSizeY = defs.getSizeY();
		} else
			fromSizeX = fromSizeY = 1;
		int toSizeX, toSizeY;
		if (to instanceof Entity)
			toSizeX = toSizeY = ((Entity) to).getSize();
		else if (to instanceof WorldObject) {
			ObjectDefinitions defs = ((WorldObject) to).getDefinitions();
			toSizeX = defs.getSizeX();
			toSizeY = defs.getSizeY();
		} else
			toSizeX = toSizeY = 1;

		Projectile projectile = new Projectile(from, to, adjustFlyingHeight, adjustSenderHeight, senderBodyPartId,
				graphicId, startHeight, endHeight, startTime,
				startTime + (speed == -1
						? Utils.getProjectileTimeSoulsplit(from, fromSizeX, fromSizeY, to, toSizeX, toSizeY)
						: Utils.getProjectileTimeNew(from, fromSizeX, fromSizeY, to, toSizeX, toSizeY, speed)),
				slope, angle);
		getRegion(from.getRegionId()).addProjectile(projectile);
		return projectile;
	}

	/**
	 * We represent the Soul Wars mini-game as static.
	 */
	public static SoulWarsManager soulWars;

	/**
	 * Throws a party!
	 */
	public static void edelarParty() {
		final String message = "<col=00CCCC>#" + Settings.SERVER_NAME;
		for (final Player players : World.getPlayers()) {
			if (players == null)
				continue;
			players.setNextAnimation(new Animation(9098));
			players.setNextGraphics(new Graphics(92));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					players.setNextForceTalk(new ForceTalk(message));
					this.stop();
				}
			}, 4);
		}
		for (final NPC npc : World.getNPCs()) {
			if (npc == null)
				continue;
			npc.setNextGraphics(new Graphics(92));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					npc.setNextForceTalk(new ForceTalk(message));

					/** Reset votes & Parties at 500 **/
					if (VoteManager.VOTES >= 100) {
						VoteManager.VOTES = 0;
						VoteManager.PARTIES = 0;
					}

					this.stop();
				}
			}, 4);
		}
	}

	/**
	 * Last player who has successfully voted.
	 */
	private static String lastVoter;

	public static String getLastVoter() {
		return lastVoter;
	}

	public static void setLastVoter(String voter) {
		lastVoter = voter;
	}

	public static Player findPlayer(String name) {
		Player plr = getPlayerByDisplayName(name);
		if (plr == null) {
			plr = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
			if (plr != null) {
				plr.setUsername(Utils.formatPlayerNameForProtocol(name));
			}
		}
		return plr;
	}

	public static void sendNews(String message, int type) {
		sendNews(null, message, type); // dont use type 2(FRIEND_NEWS) with this
										// one
	}

	/*
	 * 0 - all worlds 1 - just this world 2 - friend 3 - game news
	 */
	public static void sendNews(Player from, String message, int type) {
		String m = "<shad=000>News: " + message + "</shad></col>";
		if (type == 0)
			m = "<img=7><col=D80000>" + m;
		else if (type == 1)
			m = "<img=6><col=ff8c38>" + m;
		else if (type == 2)
			m = "<img=5><col=45b247>" + m;
		else if (type == 3)
			m = "<img=7><col=FFFF00>" + m;

		for (Player p : World.getPlayers()) {
			if (p == null || !p.isRunning()
					|| (type == 2 && p != from && !p.getFriendsIgnores().getFriends().contains(from.getUsername())))
				continue;
			p.getPackets().sendGameMessage(m, true);
		}
	}

	public static void executeAfterLoadRegion(final int regionId, final Runnable event) {
		executeAfterLoadRegion(regionId, 0, event);
	}

	public static void executeAfterLoadRegion(final int regionId, long startTime, final Runnable event) {
		executeAfterLoadRegion(regionId, startTime, 10000, event);
	}

	public static void executeAfterLoadRegion(final int fromRegionX, final int fromRegionY, final int toRegionX,
			final int toRegionY, long startTime, final long expireTime, final Runnable event) {
		final long start = Utils.currentTimeMillis();
		/*
		 * for (int x = fromRegionX; x <= toRegionX; x++) { for (int y =
		 * fromRegionY; y <= toRegionY; y++) { int regionId =
		 * MapUtils.encode(Structure.REGION, x, y); World.getRegion(regionId,
		 * true); // forces check load if not // loaded } }
		 */
		CoresManager.fastExecutor.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					for (int x = fromRegionX; x <= toRegionX; x++) {
						for (int y = fromRegionY; y <= toRegionY; y++) {
							int regionId = MapUtils.encode(Structure.REGION, x, y);
							if (!World.isRegionLoaded(regionId) && Utils.currentTimeMillis() - start < expireTime)
								return;
						}
					}
					event.run();
					cancel();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

		}, startTime, 600);
	}

	/*
	 * TODO make this use code from above to save lines lo, they do same
	 */
	public static void executeAfterLoadRegion(final int regionId, long startTime, final long expireTime,
			final Runnable event) {
		final long start = Utils.currentTimeMillis();
		World.getRegion(regionId, true); // forces check load if not loaded
		CoresManager.fastExecutor.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					if (!World.isRegionLoaded(regionId) && Utils.currentTimeMillis() - start < expireTime)
						return;
					event.run();
					cancel();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

		}, startTime, 600);
	}

	public static final boolean removeGroundItem(final FloorItem floorItem) {
		int regionId = floorItem.getTile().getRegionId();
		Region region = getRegion(regionId);
		if (!region.getGroundItemsSafe().contains(floorItem))
			return false;
		region.getGroundItemsSafe().remove(floorItem);
		for (Player player : World.getPlayers()) {
			if (player == null || !player.isActive() || player.hasFinished()
					|| !player.getMapRegionsIds().contains(regionId))
				continue;
			player.getPackets().sendRemoveGroundItem(floorItem);
		}
		return true;
	}

}