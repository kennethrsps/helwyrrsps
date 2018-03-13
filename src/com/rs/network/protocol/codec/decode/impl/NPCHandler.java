package com.rs.network.protocol.codec.decode.impl;

import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.Animation;
import com.rs.game.ForceTalk;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.activites.pest.CommendationExchange;
import com.rs.game.npc.Drop;
import com.rs.game.npc.NPC;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.npc.others.LivingRock;
import com.rs.game.npc.others.WildyWyrm;
import com.rs.game.npc.others.randoms.AgilityPenguinNPC;
import com.rs.game.npc.others.randoms.ChronicleFragmentNPC;
import com.rs.game.npc.others.randoms.CookNPC;
import com.rs.game.npc.others.randoms.CraftingRandom;
import com.rs.game.npc.others.randoms.DwarvenMinerNPC;
import com.rs.game.npc.others.randoms.EliNPC;
import com.rs.game.npc.others.randoms.FarmingRandom;
import com.rs.game.npc.others.randoms.FireSpirit;
import com.rs.game.npc.others.randoms.FletchingRandom;
import com.rs.game.npc.others.randoms.LiquidGoldNymph;
import com.rs.game.npc.others.randoms.MemstixNPC;
import com.rs.game.npc.others.randoms.NatureSpiritNPC;
import com.rs.game.npc.others.randoms.PrayerRandom;
import com.rs.game.npc.others.randoms.RiverTrollNPC;
import com.rs.game.npc.others.randoms.RogueNPC;
import com.rs.game.npc.others.randoms.WizardFinixNPC;
import com.rs.game.npc.pet.Pet;
import com.rs.game.npc.slayer.Strykewyrm;
import com.rs.game.player.Player;
import com.rs.game.player.SlayerManager;
import com.rs.game.player.actions.Fishing;
import com.rs.game.player.actions.Fishing.FishingSpots;
import com.rs.game.player.actions.Rest;
import com.rs.game.player.actions.divination.Wisp;
import com.rs.game.player.actions.hunter.FlyingEntityHunter;
import com.rs.game.player.actions.mining.LivingMineralMining;
import com.rs.game.player.actions.mining.MiningBase;
import com.rs.game.player.actions.runecrafting.SiphonActionCreatures;
import com.rs.game.player.actions.thieving.PickPocketAction;
import com.rs.game.player.actions.thieving.PickPocketableNPC;
import com.rs.game.player.content.BobBarter;
import com.rs.game.player.content.CarrierTravel;
import com.rs.game.player.content.CarrierTravel.Carrier;
import com.rs.game.player.content.CustomFurClothing;
import com.rs.game.player.content.FadingScreen;
import com.rs.game.player.content.HideTanning;
import com.rs.game.player.content.LividFarm;
import com.rs.game.player.content.Lottery;
import com.rs.game.player.content.PlayerLook;
import com.rs.game.player.content.PrifddinasCity;
import com.rs.game.player.content.RouteEvent;
import com.rs.game.player.content.SheepShearing;
import com.rs.game.player.content.Slayer.SlayerMaster;
import com.rs.game.player.content.dungeoneering.DungeonRewardShop;
import com.rs.game.player.content.dungeoneering.rooms.puzzles.SlidingTilesRoom;
import com.rs.game.player.content.SlayerTask;
import com.rs.game.player.content.items.AncientEffigy;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.player.dialogue.impl.Aubury;
import com.rs.game.player.dialogue.impl.BoatingDialouge;
import com.rs.game.player.dialogue.impl.FremennikShipmaster;
import com.rs.game.player.dialogue.impl.PetShopOwner;
import com.rs.game.player.dialogue.impl.TrialAnnouncerD;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.stream.InputStream;
import com.rs.utils.Colors;
import com.rs.utils.Logger;
import com.rs.utils.NPCDrops;
import com.rs.utils.NPCExamines;
import com.rs.utils.NPCSpawns;
import com.rs.utils.ShopsHandler;
import com.rs.utils.Utils;

public class NPCHandler {

	public static void handleOption1(final Player player, InputStream stream) {
		int npcIndex = stream.readUnsignedShort128();
		boolean forceRun = stream.read128Byte() == 1;
		final NPC npc = World.getNPCs().get(npcIndex);
		if (npc == null || npc.isCantInteract() || npc.isDead() || npc.hasFinished()
				|| !player.getMapRegionsIds().contains(npc.getRegionId()))
			return;
		boolean snowMans = (npc.getId() >= 2269 && npc.getId() <= 2272 || npc.getId() == 12183) ? true : false;
		player.stopAll(false);
		if (forceRun)
			player.setRun(forceRun);
		if (player.isHeadStaff())
			player.getPackets().sendPanelBoxMessage("Option 1: " + npc);
		if (LividFarm.HandleLividFarmNpc(player, npc, 1))
			return;
		/* Snowmen */
		if (snowMans) {
			if (!player.getXmas().intro)
				return;
			if (!player.getInventory().containsItem(33590, 1) && player.getEquipment().getWeaponId() != 33590) {
				player.sendMessage("You need a snowball equipped or in your inventory to do this!", true);
				return;
			}
			if (player.getXmas().getDistance(npc) >= 100) {
				player.sendMessage("You are too far away from this snowman!", true);
				return;
			}
			if (player.getXmas().inThrow)
				return;
			if (player.withinDistance(npc, 7))
				player.getXmas().throwSnowball(npc);
			else {
				player.addWalkSteps(npc.getX(), npc.getY());
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						player.getXmas().throwSnowball(npc);
					}
				}, 0);
			}
			return;
		}
		if (npc.getName().toLowerCase().contains("grand exchange")) {
			if (player.withinDistance(npc, 2)) {
				player.getGEManager().openGrandExchange();
				npc.faceEntity(player);
				player.faceEntity(npc);
				return;
			}
		}
		if (npc.getDefinitions().name.toLowerCase().contains("bank")) {
			if (player.withinDistance(npc, 2)) {
				npc.faceEntity(player);
				player.faceEntity(npc);
				player.getBank().openBank();
				return;
			}
		}
		if (SlidingTilesRoom.handleSlidingBlock(player, npc))
			return;
		if (SiphonActionCreatures.siphon(player, npc))
			return;
		if (npc.getId() == 733) {
			if (player.getTreasureTrails().useNPC(npc))
				return;
		}
		player.setRouteEvent(new RouteEvent(npc, new Runnable() {
			@Override
			public void run() {
				npc.resetWalkSteps();
				player.faceEntity(npc);
				if (!player.getControlerManager().processNPCClick1(npc))
					return;
				if (!player.getNewQuestManager().processNPCClick1(npc))
					return;
				Object[] shipAttributes = BoatingDialouge.getBoatForShip(npc.getId());
				if (shipAttributes != null) {
					player.getDialogueManager().startDialogue("BoatingDialouge", npc.getId());
					return;
				}
				// easter
				if (npc.getId() == 7197 || npc.getId() == 7890 || npc.getId() == 641 || npc.getId() == 755) {
					player.getDialogueManager().startDialogue("EasterBunnyD", npc.getId());
					return;
				}
				if (PrifddinasCity.handleNPCOption1(player, npc))
					return;
				if (SlayerMaster.startInteractionForId(player, npc.getId(), 1))
					return;
				if (npc.getId() == 3404) {
					player.getDialogueManager().startDialogue("TeplinMacaganD", npc.getId(), 1);
					return;
				}
				FishingSpots spot = FishingSpots.forId(npc.getId() | 1 << 24);
				if (spot != null) {
					player.getActionManager().setAction(new Fishing(spot, npc));
					return; // its a spot, they wont face us
				} else if (npc.getId() >= 8837 && npc.getId() <= 8839) {
					player.getActionManager().setAction(new LivingMineralMining((LivingRock) npc));
					return;
				}
				if (npc.getId() == 2538) {
					int prize = Lottery.LOTTERY_TICKETS.size() * Lottery.TICKET_PRICE;
					long milliseconds = Lottery.getLotteryCycle() - Utils.currentTimeMillis();
					int seconds = (int) (milliseconds / 1000) % 60;
					int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
					npc.setNextForceTalk(new ForceTalk("Lottery pot is :" + Utils.getFormattedNumber(prize)
							+ " , time left: " + minutes + " minutes and " + seconds + " seconds"));
					player.getDialogueManager().startDialogue("LotteryD", npc.getId());
					return;
				}

				if (npc.getId() == 17161) {
					player.getDialogueManager().startDialogue("VoragoD", npc.getId());
					return;
				}
				if (npc.getId() == 587) {
					player.getDialogueManager().startDialogue("DonatorShop", npc.getId());
					return;
				}
				if (npc.getDefinitions().name.toLowerCase().contains("bank") || npc.getId() == 15194
						|| npc.getId() == 13455) {
					player.getBank().openBank();
					return;
				}

				if (npc.getId() == 9712) {
					player.getDialogueManager().startDialogue("DungeoneeringTutor");
					return;
				}
				if (npc.getDefinitions().name.toLowerCase().contains("musician")) {
					long currentTime = Utils.currentTimeMillis();
					if (player.getEmotesManager().getNextEmoteEnd() >= currentTime
							|| player.getLockDelay() >= currentTime) {
						player.sendMessage("You can't rest while perfoming an emote.");
						return;
					}
					player.stopAll();
					player.getActionManager().setAction(new Rest());
					return;
				}
				if (npc.getName().toLowerCase().contains("impling")) {
					FlyingEntityHunter.captureFlyingEntity(player, npc);
					return;
				}
				if (npc instanceof Pet) {
					Pet pet = (Pet) npc;
					if (pet != player.getPet()) {
						player.sendMessage("This isn't your pet.", true);
						return;
					}
					if (npc.getId() < 15980) {
						if (!player.getInventory().hasFreeSlots()) {
							player.sendMessage("You do not have enough inventory space to pickup your pet.", true);
							return;
						}
						player.setNextAnimation(new Animation(827));
						pet.pickup();
						return;
					}
					player.getDialogueManager().startDialogue("SimplePlayerMessage", "I better not touch it..");
				}
				if (npc instanceof Familiar) {
					if (npc.getDefinitions().hasOption("store")) {
						if (player.getFamiliar() != npc) {
							player.sendMessage("That isn't your familiar.");
							return;
						}
						player.getFamiliar().store();
					} else if (npc.getDefinitions().hasOption("cure")) {
						if (player.getFamiliar() != npc) {
							player.sendMessage("That isn't your familiar.");
							return;
						}
						if (!player.getPoison().isPoisoned()) {
							player.sendMessage("You're already healthy.");
							return;
						} else {
							player.getFamiliar().drainSpecial(2);
							player.addPoisonImmune(120);
						}
					}
					return;
				}
				if (npc.getDefinitions().hasOption("Listen-to")) {
					if (player.isResting()) {
						player.stopAll();
						return;
					}
					long currentTime = Utils.currentTimeMillis();
					if (player.getEmotesManager().getNextEmoteEnd() >= currentTime) {
						player.getPackets().sendGameMessage("You can't rest while perfoming an emote.");
						return;
					}
					if (player.getLockDelay() >= currentTime) {
						player.getPackets().sendGameMessage("You can't rest while perfoming an action.");
						return;
					}
					player.stopAll();
					player.getActionManager().setAction(new Rest());
				}
				npc.faceEntity(player);
				if (npc.getId() == 15085) {// TODO
					player.getDialogueManager().startDialogue("SpinRewardExchanger", npc.getId());
					return;
				}
				if (npc instanceof Wisp) {
					Wisp wisp = (Wisp) npc;
					wisp.harvest(player);
					return;
				}
				if (npc.getId() == 8091) {
					player.getDialogueManager().startDialogue("StarSpriteD");
					return;
				}
				if (npc.getId() == 18516) {
					player.getDialogueManager().startDialogue("ContractDialogue");
					return;
				}
				if (npc.getId() == 6524) {
					player.getDialogueManager().startDialogue("BobBarterD", npc.getId());
					return;
				}
				if (npc.getId() == 17143) {
					player.getDialogueManager().startDialogue("Ocellus", npc.getId(), (byte) 1);
					return;
				}
				if (npc.getId() == 14381) {
					if (player.withinDistance(npc, 2)) {
						npc.faceEntity(player);
						player.faceEntity(npc);
					}
					player.getDialogueManager().startDialogue(new Dialogue() {

						@Override
						public void start() {
							sendNPCDialogue(npc.getId(), CALM,
									"Hey " + player.getDisplayName() + ", how can I help you?");
							stage = 0;
						}

						@Override
						public void run(int interfaceId, int componentId) {
							switch (stage) {
							case 0:
								sendOptionsDialogue("Choose an option", "Check for cosmetic rewards", "Reward list",
										"Walk away");
								stage = 1;
								break;
							case 1:
								switch (componentId) {
								case OPTION_1:
									String sfCol = AncientEffigy.getEffigies(player) >= 25 ? Colors.green : Colors.red;
									String fbCol = player.getInventory().containsItem(6723, 100000) ? Colors.green
											: Colors.red;
									sendOptionsDialogue("Choose a reward", sfCol + "Sack of effigies",
											fbCol + "Fishbowl challenge");
									stage = 11;
									break;
								case OPTION_2:
									sendOptionsDialogue("Choose a reward", "Sack of effigies", "Fishbowl challenge",
											"Walk away");
									stage = 3;
									break;
								case OPTION_3:
									finish();
									break;
								}
								break;
							case 11:
								switch (componentId) {
								case OPTION_1:
									if (AncientEffigy.getEffigies(player) > 25) {
										sendNPCDialogue(npc.getId(), CALM, "You have "
												+ (AncientEffigy.getEffigies(player) - 25) + " too many effigies!");
										stage = 99;
										break;
									}
									if (AncientEffigy.canSack(player)) {
										sendOptionsDialogue("Are you sure?", "Yes", "No");
										stage = 14;
									} else {
										sendNPCDialogue(npc.getId(), CALM, "You need 25 effigies to do that!");
										stage = 99;
									}
									break;
								case OPTION_2:
									if (player.getInventory().containsItem(6723, 100000)) {
										sendOptionsDialogue("Are you sure?", "Yes", "No");
										stage = 15;
									} else {
										sendNPCDialogue(npc.getId(), CALM, "You need 100,000 fishbowls for that!");
										stage = 99;
									}
									break;
								}
								break;
							case 14:
								switch (componentId) {
								case OPTION_1:
									finish();
									AncientEffigy.effigySack(player);
									break;
								case OPTION_2:
									finish();
									break;
								}
								break;
							case 15:
								switch (componentId) {
								case OPTION_1:
									if (player.getItemsMade() < 100000) {
										sendNPCDialogue(npc.getId(), ANGRY,
												"Hey, you didn't craft all of these yourself!");
										stage = 16;
										break;
									}
									int rewards[] = { 7534, 7535, 8929, 9634, 9636, 9638 };
									finish();
									if (player.getInventory().getFreeSlots() >= 6) {
										player.getInventory().deleteItem(6723, 100000);
										for (int items : rewards)
											player.getInventory().addItem(items, 1);
										World.sendWorldMessage("<img=6>" + Colors.cyan + "<shad=000000>News: "
												+ player.getDisplayName() + " has completed the fishbowl challenge!",
												false);
									} else {
										player.sendMessage(Colors.red + "You need at least 6 inventory spaces!");
									}
									break;
								case OPTION_2:
									finish();
									break;
								}
								break;
							case 16:
								finish();
								player.sendMessage(
										Colors.red + "You need 100,000 items crafted total to complete the challenge.");
								break;
							case 3:
								switch (componentId) {
								case OPTION_1:
									sendNPCDialogue(npc.getId(), CALM,
											"You'll need 25 effigies for a sack of effigies.");
									break;
								case OPTION_2:
									sendNPCDialogue(npc.getId(), CALM,
											"For 100,000 fishbowls, I'll give you a unique diving set!");
									player.sendMessage(Colors.green
											+ "Set contains: vyrewatch set, crabclaw and hook, diving apparatus, and fishbowl helmet.");
									break;
								case OPTION_3:
									finish();
								}
								stage = 99;
								break;
							case 99:
								finish();
								break;
							}
						}

						@Override
						public void finish() {
							player.getInterfaceManager().closeChatBoxInterface();
						}

					});
					return;
				}
				if (npc.getName().toLowerCase().contains("grand exchange")) {
					player.getGEManager().openGrandExchange();
					return;
				}
				if (player.getTreasureTrails().useNPC(npc))
					return;
				if (npc.getId() == 2824) {
					player.getDialogueManager().startDialogue("EllisD", npc.getId());
					return;
				}
				if (npc.getId() == 587) {
					player.getDialogueManager().startDialogue("Jatix", npc.getId(), 1);
					return;
				}
				if (npc.getId() == 5941) {
					player.getDialogueManager().startDialogue("SimplePlayerMessage",
							"Better not to disturb him while he's at work.");
					return;
				}
				if (npc.getId() == 6539) {
					player.getDialogueManager().startDialogue("NastrothD", npc.getId());
					return;
				}
				if (npc.getId() == 12378) {
					player.getDialogueManager().startDialogue("PeteJackNigga", 12378);
					return;
				}
				if (npc.getId() == 13727) {
					player.getDialogueManager().startDialogue("XuanD", npc.getId(), 1);
					return;
				}
				if (npc.getId() == 14874) {
					player.getDialogueManager().startDialogue("DailyTaskD", npc.getId());
					return;
				}
				if (npc.getId() == 5913) {
					player.getDialogueManager().startDialogue("Aubury", npc);
					return;
				}
				if (npc.getId() == 2465) {
					player.getDialogueManager().startDialogue("SolomonD", npc.getId());
					return;
				}
				if (npc.getName().toLowerCase().contains("estate agent")) {
					player.getDialogueManager().startDialogue("EstateAgent", npc.getId());
					return;
				}
				if (npc.getId() == 9400) {
					player.getDialogueManager().startDialogue("SantaClause", npc.getId());
					return;
				}
				if (npc.getId() == 5113) {
					player.getDialogueManager().startDialogue("HunterExpertD", npc.getId());
					return;
				}
				if (npc.getId() == 19519) {
					player.getDialogueManager().startDialogue("ElfHermitD", npc);
					return;
				}
				if (npc.getId() == 18198) {
					player.getDialogueManager().startDialogue("MayStormbrewerD", npc.getId(), false);
					return;
				}
				if (npc.getId() == 659) {
					player.getDialogueManager().startDialogue("PartyPete", 659);
					return;
				}
				if (npc.getId() == 9711) {
					player.getDialogueManager().startDialogue("RewardsTraderD", npc.getId(), 1);
					return;
				}
				if (npc.getId() == 14620) {
					player.getDialogueManager().startDialogue("MerchantD", npc.getId());
					return;
				}
				if (npc.getId() == 2253) {
					player.getDialogueManager().startDialogue("WiseOldMan", npc.getId());
					return;
				}
				if (npc.getName().toLowerCase().contains("trial announcer")) {
					player.getDialogueManager().startDialogue("TrialAnnouncerD", npc.getId());
					return;
				}
				if (npc.getId() == 15158) {
					player.getDialogueManager().startDialogue("AIOShop", npc.getId());
					return;
				}
				if (npc.getId() == 554) {
					player.getDialogueManager().startDialogue("FancyDressShopOwner", npc.getId());
					return;
				}

				if (npc.getId() == 13651) {
					player.getDialogueManager().startDialogue("EasterBunny", npc);
					return;
				}
				if (npc.getId() == 6893) {
					player.getDialogueManager().startDialogue("PetShopOwner", npc.getId());
					return;
				}
				if (npc.getId() == 2417) {
					WildyWyrm.handleInspect(player, npc);
					return;
				}
				if (npc.getId() == 594) {
					ShopsHandler.openShop(player, 59);
				}
				switch (npc.getDefinitions().name.toLowerCase()) {
				case "void knight":
					CommendationExchange.openExchangeShop(player);
					break;
				case "shopkeeper":
				case "shop assistant":
					player.getDialogueManager().startDialogue("GeneralStore", npc.getId(), 1);
					break;
				case "sheep":
					SheepShearing.shearAttempt(player, npc);
					break;
				case "tool leprechaun":
					player.getDialogueManager().startDialogue("SimpleNPCMessage", npc.getId(),
							"Uhh.. These giants just ruin everything...");
					break;
				}
				if (npc.getId() == 15451 && npc instanceof FireSpirit) {
					FireSpirit spirit = (FireSpirit) npc;
					spirit.giveReward(player);
					return;
				}
				if (npc.getId() == 17169 && npc instanceof PrayerRandom) {
					PrayerRandom monk = (PrayerRandom) npc;
					monk.giveReward(player);
					return;
				}
				if (npc.getId() == 7954 && npc instanceof CraftingRandom) {
					CraftingRandom crafting = (CraftingRandom) npc;
					crafting.giveReward(player);
					return;
				}
				if (npc.getId() == 2170 && npc instanceof FarmingRandom) {
					FarmingRandom farming = (FarmingRandom) npc;
					farming.giveReward(player);
					return;
				}
				if (npc.getId() == 15105 && npc instanceof FletchingRandom) {
					FletchingRandom fletch = (FletchingRandom) npc;
					fletch.giveReward(player);
					return;
				}
				if (npc.getId() == 14 && npc instanceof LiquidGoldNymph) {
					LiquidGoldNymph goldNymph = (LiquidGoldNymph) npc;
					goldNymph.giveReward(player);
					return;
				}
				if (npc.getId() == 15419 && npc instanceof WizardFinixNPC) {
					WizardFinixNPC wizard = (WizardFinixNPC) npc;
					wizard.giveReward(player);
					return;
				}
				if (npc.getId() == 17347 && npc instanceof EliNPC) {
					EliNPC miner = (EliNPC) npc;
					miner.giveReward(player);
					return;
				}
				if (npc.getId() == 8122 && npc instanceof RogueNPC) {
					RogueNPC rogue = (RogueNPC) npc;
					rogue.giveReward(player);
					return;
				}
				if (npc.getId() == 2551 && npc instanceof DwarvenMinerNPC) {
					DwarvenMinerNPC miner = (DwarvenMinerNPC) npc;
					miner.giveReward(player);
					return;
				}
				if (npc.getId() == 5447 && npc instanceof AgilityPenguinNPC) {
					AgilityPenguinNPC penguin = (AgilityPenguinNPC) npc;
					penguin.giveReward(player);
					return;
				}
				if (npc.getId() == 18204 && npc instanceof ChronicleFragmentNPC) {
					ChronicleFragmentNPC chronicle = (ChronicleFragmentNPC) npc;
					chronicle.giveReward(player);
					return;
				}
				if (npc.getId() == 11454 && npc instanceof RiverTrollNPC) {
					RiverTrollNPC penguin = (RiverTrollNPC) npc;
					penguin.giveReward(player);
					return;
				}
				if (npc.getId() == 5910 && npc instanceof CookNPC) {
					CookNPC penguin = (CookNPC) npc;
					penguin.giveReward(player);
					return;
				}
				if (npc.getId() == 16887 && npc instanceof MemstixNPC) {
					MemstixNPC summoner = (MemstixNPC) npc;
					summoner.giveReward(player);
					return;
				}
				if (npc.getId() == 1051 && npc instanceof NatureSpiritNPC) {
					NatureSpiritNPC spirit = (NatureSpiritNPC) npc;
					spirit.giveReward(player);
					return;
				} else if (npc.getName().toLowerCase().contains("grand exchange"))
					player.getDialogueManager().startDialogue("Fiara");
				else if (npc.getId() == 1597)
					player.getDialogueManager().startDialogue("Vannaka");
				else if (npc.getId() == 9462 || npc.getId() == 9464 || npc.getId() == 9466)
					Strykewyrm.handleStomping(player, npc);
				else if (npc.getId() == 6139)
					player.getDialogueManager().startDialogue("StarterTutorialD");
				else if (npc.getId() == 5141)
					player.getDialogueManager().startDialogue("UgiD", npc);
				/**
				 * Christmas event.
				 */
				else if (npc.getId() == 8541)
					player.getDialogueManager().startDialogue("HeadImpD", npc.getId());
				/**
				 * Shop dialogues.
				 */
				else if (npc.getId() == 15583)
					player.getDialogueManager().startDialogue("GuardD", npc.getId());
				else if (npc.getId() == 549 || npc.getId() == 577)
					player.getDialogueManager().startDialogue("HorvikD", npc.getId());
				else if (npc.getId() == 537)
					player.getDialogueManager().startDialogue("ScavvoD", npc.getId());
				else if (npc.getId() == 550)
					player.getDialogueManager().startDialogue("LoweD", npc.getId());
				else if (npc.getId() == 557)
					player.getDialogueManager().startDialogue("WydinD", npc.getId());
				else if (npc.getId() == 546)
					player.getDialogueManager().startDialogue("ZaffD", npc.getId());
				else if (npc.getId() == 519)
					player.getDialogueManager().startDialogue("Bob", npc.getId());
				else if (npc.getId() == 9707)
					player.getDialogueManager().startDialogue("FremennikShipmaster", npc.getId(), true);
				else if (npc.getId() == 9708)
					player.getDialogueManager().startDialogue("FremennikShipmaster", npc.getId(), false);
				else if (npc.getId() == 278)
					player.getDialogueManager().startDialogue("Cook", npc.getId(), true);
				else if (npc.getId() == 598)
					player.getDialogueManager().startDialogue("Hairdresser", npc.getId());
				else if (npc.getId() == 548)
					player.getDialogueManager().startDialogue("Thessalia", npc.getId());
				else if (npc.getId() == 2676)
					player.getDialogueManager().startDialogue("MakeOverMage", npc.getId(), 0);
				else if (npc.getId() == 6988)
					player.getDialogueManager().startDialogue("SimpleNPCMessage", 6988,
							"We don't have anything to talk about. If you wish to buy items, then trade me.. If not - get lost!");

				else {
					// player.sendMessage("Nothing interesting happens.");
					if (Settings.DEBUG)
						System.out.println("cliked 1 at npc id : " + npc.getId() + ", " + npc.getX() + ", " + npc.getY()
								+ ", " + npc.getPlane());
				}
			}
		}));
	}

	public static void handleOption2(final Player player, InputStream stream) {
		int npcIndex = stream.readUnsignedShort128();
		boolean forceRun = stream.read128Byte() == 1;
		final NPC npc = World.getNPCs().get(npcIndex);
		if (npc == null || npc.isCantInteract() || npc.isDead() || npc.hasFinished()
				|| !player.getMapRegionsIds().contains(npc.getRegionId()))
			return;
		player.stopAll(false);
		if (forceRun)
			player.setRun(forceRun);
		if (player.isHeadStaff())
			player.getPackets().sendPanelBoxMessage("Option 2: " + npc);
		if (LividFarm.HandleLividFarmNpc(player, npc, 2))
			return;
		if (npc.getDefinitions().name.toLowerCase().contains("bank") || npc.getId() == 15194) {
			if (player.withinDistance(npc, 2)) {
				npc.faceEntity(player);
				player.faceEntity(npc);
				player.getDialogueManager().startDialogue("Banker", npc.getId());
				return;
			}
		}
		if (npc.getName().toLowerCase().contains("diango")) {
			npc.faceEntity(player);
			player.faceEntity(npc);
			ShopsHandler.openShop(player, 63);
			return;
		}

		if (npc.getName().toLowerCase().contains("grand exchange")) {
			if (player.withinDistance(npc, 2)) {
				player.getDialogueManager().startDialogue("GrandExchangeClerkD", npc.getId());
				npc.faceEntity(player);
				player.faceEntity(npc);
				return;
			}
		}
		player.setRouteEvent(new RouteEvent(npc, new Runnable() {
			@Override
			public void run() {
				npc.resetWalkSteps();
				player.faceEntity(npc);
				if (PrifddinasCity.handleNPCOption2(player, npc))
					return;
				Object[] shipAttributes = BoatingDialouge.getBoatForShip(npc.getId());
				if (shipAttributes != null) {
					Carrier ship = (Carrier) shipAttributes[0];
					if (!player.getNewQuestManager().hasCompletedQuest(9)
							&& (ship.name().equalsIgnoreCase("Karamja_Fare")
									|| ship.name().equalsIgnoreCase("Karamja"))) {
						player.getPackets().sendGameMessage(
								"You may only use the Pay-fare option after completing Pirate's Treasure.");
						return;
					}
					CarrierTravel.sendCarrier(player, ship, (boolean) shipAttributes[1]);
				}
				if (npc.getId() == 3404) {
					player.getDialogueManager().startDialogue("TeplinMacaganD", npc.getId(), 2);
					return;
				}
				// easter
				if (npc.getId() == 5442) {
					player.getDialogueManager().startDialogue("EasterBunnyD", npc.getId());
					return;
				}
				if (npc.getId() == 587) {
					player.getDialogueManager().startDialogue("DonatorShop", npc.getId());
					return;
				}
				FishingSpots spot = FishingSpots.forId(npc.getId() | (2 << 24));
				if (spot != null) {
					player.getActionManager().setAction(new Fishing(spot, npc));
					return;
				}
				PickPocketableNPC pocket = PickPocketableNPC.get(npc.getId());
				if (pocket != null) {
					player.getActionManager().setAction(new PickPocketAction(npc, pocket));
					return;
				}
				if (SlayerMaster.startInteractionForId(player, npc.getId(), 2))
					return;
				if (player.getTreasureTrails().useNPC(npc))
					return;
				if (npc.getId() == 17161) {
					player.getDialogueManager().startDialogue("VoragoChallenge");
					return;
				}
				if (npc instanceof Familiar) {
					if (npc.getDefinitions().hasOption("interact")) {
						if (player.getFamiliar() != npc) {
							player.getPackets().sendGameMessage("That isn't your familiar.", true);
							return;
						}
						// if (npc.getId() == 7341 || npc.getId() == 7342) {
						// player.getDialogueManager().startDialogue("FireTitan");
						// } else {
						player.getDialogueManager().startDialogue("SimplePlayerMessage",
								"Why would I talk to a familiar? That's just weird.");
						// }
					}
					return;
				}
				if (npc instanceof Pet) {
					Pet pet = (Pet) npc;
					if (npc.getId() < 15980) {
						if (pet != player.getPet()) {
							player.sendMessage("This isn't your pet.", true);
							return;
						}
						player.getDialogueManager().startDialogue("SimplePlayerMessage",
								"Why would I do that? That's just plain weird.");
						return;
					}
					if (pet != player.getPet()) {
						player.sendMessage("This isn't your pet.", true);
						return;
					}
					if (!player.getInventory().hasFreeSlots()) {
						player.sendMessage("You do not have enough inventory space to pickup your pet.", true);
						return;
					}
					player.setNextAnimation(new Animation(827));
					pet.pickup();
					return;
				}
				npc.faceEntity(player);
				if (!player.getControlerManager().processNPCClick2(npc))
					return;
				if (!player.getNewQuestManager().processNPCClick2(npc))
					return;
				switch (npc.getDefinitions().name.toLowerCase()) {
				case "void knight":
					CommendationExchange.openExchangeShop(player);
					break;
				case "shopkeeper":
				case "shop assistant":
					ShopsHandler.openShop(player, 1);
					break;
				case "tool leprechaun":
					ShopsHandler.openShop(player, 13);
					break;
				case "trial announcer":
					TrialAnnouncerD.teleport(player);
					break;
				}
				if (npc.getId() == 15194 || npc.getId() == 2617)
					player.getBank().openBank();
				if (npc.getId() == 9707)
					FremennikShipmaster.sail(player, true);
				else if (npc.getId() == 9708)
					FremennikShipmaster.sail(player, false);

				else if (npc.getDefinitions().name.toLowerCase().contains("bank") || npc.getId() == 15194
						|| npc.getId() == 13455)
					player.getDialogueManager().startDialogue("Banker", npc.getId());
				/**
				 * Shops.
				 */
				else if (npc.getId() == 6893)
					PetShopOwner.openShop(player);
				else if (npc.getId() == 538)
					ShopsHandler.openShop(player, 2);
				else if (npc.getId() == 549)
					ShopsHandler.openShop(player, 3);
				else if (npc.getId() == 537)
					ShopsHandler.openShop(player, 4);
				else if (npc.getId() == 554)
					ShopsHandler.openShop(player, 5);
				else if (npc.getId() == 557)
					ShopsHandler.openShop(player, 6);
				else if (npc.getId() == 546)
					ShopsHandler.openShop(player, 7);
				else if (npc.getId() == 550)
					ShopsHandler.openShop(player, 8);
				else if (npc.getId() == 519)
					ShopsHandler.openShop(player, 16);
				else if (npc.getId() == 659)
					ShopsHandler.openShop(player, 47);
				else if (npc.getId() == 14620)
					ShopsHandler.openShop(player, 51);
				else if (npc.getId() == 2620)
					ShopsHandler.openShop(player, 30);
				else if (npc.getId() == 2622)
					ShopsHandler.openShop(player, 29);
				else if (npc.getId() == 2623)
					ShopsHandler.openShop(player, 28);
				else if (npc.getId() == 5113)
					ShopsHandler.openShop(player, 31);
				else if (npc.getId() == 9711)
					DungeonRewardShop.openRewardShop(player);
				else if (npc.getId() == 5913)
					ShopsHandler.openShop(player, 7);
				else if (npc.getId() == 548)
					ShopsHandler.openShop(player, 21);
				else if (npc.getId() == 6988)
					player.getDialogueManager().startDialogue("SummoningStoreD", npc.getId());
				else if (npc.getId() == 598)
					PlayerLook.openHairdresserSalon(player);
				else if (npc.getId() == 2676)
					PlayerLook.openMageMakeOver(player);
				if (npc.getName().toLowerCase().contains("grand exchange")) {
					player.getDialogueManager().startDialogue("GrandExchangeClerkD", npc.getId());
					return;
				}
				if (npc.getId() == 2824) {
					HideTanning.tanHides(player);
					return;
				}

				if (npc.getId() == 13727) {
					player.getDialogueManager().startDialogue("XuanD", npc.getId(), 4);
					return;
				}
				if (npc.getId() == 17143) {
					player.getDialogueManager().startDialogue("Ocellus", npc.getId(), (byte) 2);
					return;
				}
				if (npc.getId() == 587) {
					player.getDialogueManager().startDialogue("Jatix", npc.getId(), 0);
					return;
				}
				/** Sailor (Travel to Miscellania) **/
				if (npc.getId() == 1304) {
					player.lock();
					player.sendMessage("You board the ship..", true);
					FadingScreen.fade(player, 0, new Runnable() {

						@Override
						public void run() {
							player.setNextWorldTile(new WorldTile(2581, 3845, 0));
							player.sendMessage("The Sailor has taken you to Miscellania.", true);
							player.unlock();
						}
					});
					return;
				}
				/** Sailor (Travel to Rellekka) **/
				if (npc.getId() == 1385) {
					player.lock();
					player.sendMessage("You board the ship..", true);
					FadingScreen.fade(player, 0, new Runnable() {

						@Override
						public void run() {
							player.setNextWorldTile(new WorldTile(2630, 3693, 0));
							player.sendMessage("The Sailor has taken you back to Rellekka.", true);
							player.unlock();
						}
					});
					return;
				} else {
					// player.sendMessage("Nothing interesting happens.");
					if (Settings.DEBUG)
						System.out.println("cliked 2 at npc id : " + npc.getId() + ", " + npc.getX() + ", " + npc.getY()
								+ ", " + npc.getPlane());
				}
			}
		}));
	}

	public static void handleOption3(final Player player, InputStream stream) {
		int npcIndex = stream.readUnsignedShort128();
		boolean forceRun = stream.read128Byte() == 1;
		final NPC npc = World.getNPCs().get(npcIndex);
		if (npc == null || npc.isCantInteract() || npc.isDead() || npc.hasFinished()
				|| !player.getMapRegionsIds().contains(npc.getRegionId()))
			return;
		player.stopAll(false);
		if (forceRun)
			player.setRun(forceRun);
		if (player.isHeadStaff())
			player.getPackets().sendPanelBoxMessage("Option 3: " + npc);
		if (LividFarm.HandleLividFarmNpc(player, npc, 3))
			return;
		if (npc.getDefinitions().name.toLowerCase().contains("banker") || npc.getId() == 15194) {
			if (player.withinDistance(npc, 2)) {
				npc.faceEntity(player);
				player.getGEManager().openCollectionBox();
				player.faceEntity(npc);
				return;
			}
		}
		if (npc.getName().toLowerCase().contains("grand exchange")) {
			if (player.withinDistance(npc, 2)) {
				player.getGEManager().openHistory();
				npc.faceEntity(player);
				player.faceEntity(npc);
				return;
			}
		}
		player.setRouteEvent(new RouteEvent(npc, new Runnable() {
			@Override
			public void run() {
				npc.resetWalkSteps();
				if (!player.getControlerManager().processNPCClick3(npc))
					return;
				if (!player.getNewQuestManager().processNPCClick3(npc))
					return;
				player.faceEntity(npc);
				if (PrifddinasCity.handleNPCOption3(player, npc))
					return;
				if (npc.getId() == 3404) {
					player.getDialogueManager().startDialogue("TeplinMacaganD", npc.getId(), 3);
					return;
				}
				if (npc.getId() >= 8837 && npc.getId() <= 8839) {
					MiningBase.propect(player, "You examine the remains...",
							"The remains contain traces of living minerals.");
					return;
				}
				if (npc.getDefinitions().name.contains("Banker") || npc.getDefinitions().name.contains("banker")
						|| npc.getId() == 15194 || npc.getId() == 2617 || npc.getId() == 13455) {
					npc.faceEntity(player);
					player.getGEManager().openCollectionBox();
					player.faceEntity(npc);
				}
				if (npc.getId() == 554) {
					CustomFurClothing.openInterface(player);
				}
				if (SlayerMaster.startInteractionForId(player, npc.getId(), 3)) {
					ShopsHandler.openShop(player, 14);
					return;
				}
				if (npc.getName().toLowerCase().contains("grand exchange")) {
					player.getGEManager().openHistory();
					return;
				}
				if (npc.getId() == 18198) {
					player.getDialogueManager().startDialogue("MayStormbrewerD", npc.getId(), true);
					return;
				}
				if (npc.getId() == 9711) {
					player.getDialogueManager().startDialogue("RewardsTraderD", npc.getId(), 3);
					return;
				}
				if (npc.getId() == 9398) {
					player.getDialogueManager().startDialogue("XmasDialogue", npc.getId());
					return;
				}
				if (npc.getId() == 17143) {
					player.getDialogueManager().startDialogue("Ocellus", npc.getId(), (byte) 3);
					return;
				}
				if (npc.getId() == 6524) {
					BobBarter decanter = new BobBarter(player);
					player.getDialogueManager().startDialogue(new Dialogue() {

						@Override
						public void start() {
							decanter.decant();
							sendNPCDialogue(npc.getId(), CALM, "There ya go chum..");
							stage = 1;
						}

						@Override
						public void run(int interfaceId, int componentId) {
							switch (stage) {
							case 0:
								switch (componentId) {
								case OPTION_1:
								case OPTION_2:
									decanter.decant();
									sendNPCDialogue(npc.getId(), CALM, "There ya go chum..");
									stage = 1;
									break;
								case OPTION_3:
									finish();
									break;
								}
								break;
							case 1:
								finish();
								break;
							}
						}

						@Override
						public void finish() {
							player.getInterfaceManager().closeChatBoxInterface();
						}

					});
				}
				npc.faceEntity(player);
				if (npc.getId() == 548)
					PlayerLook.openThessaliasMakeOver(player);
				else if (npc.getId() == 6892 || npc.getId() == 6893)
					PetShopOwner.sellShards(player);
				else if (npc.getId() == 6988)
					player.getDialogueManager().startDialogue("SimpleNPCMessage", 6988,
							"My enchanting skills are depleted.. Get lost!");
				else if (npc.getId() == 14620)
					player.getDialogueManager().startDialogue("SimpleNPCMessage", 14620,
							"The gentleman wants to know if we can store flowers "
									+ "for them. We don't store flowers for folks anymore.");
				else if (npc.getDefinitions().name.contains("Tool leprechaun"))
					player.getDialogueManager().startDialogue("ToolLeprechaunTeleD");
				else if (npc.getId() == 5532) {
					npc.setNextForceTalk(new ForceTalk("Senventior Disthinte Molesko!"));
					player.getControlerManager().startControler("SorceressGarden");
				}
			}
		}));
		if (Settings.DEBUG)
			System.out.println("cliked 3 at npc id : " + npc.getId() + ", " + npc.getX() + ", " + npc.getY() + ", "
					+ npc.getPlane());
	}

	public static void handleOption4(final Player player, InputStream stream) {
		int npcIndex = stream.readUnsignedShort128();
		boolean forceRun = stream.read128Byte() == 1;
		final NPC npc = World.getNPCs().get(npcIndex);
		if (npc == null || npc.isCantInteract() || npc.isDead() || npc.hasFinished()
				|| !player.getMapRegionsIds().contains(npc.getRegionId())) {
			return;
		}
		if (player.isHeadStaff())
			player.getPackets().sendPanelBoxMessage("Option 4: " + npc);
		player.stopAll(false);
		if (forceRun) {
			player.setRun(forceRun);
		}
		if (npc.getName().toLowerCase().contains("grand exchange")) {
			if (player.withinDistance(npc, 2)) {
				player.getGEManager().openItemSets();
				npc.faceEntity(player);
				player.faceEntity(npc);
				return;
			}
		}
		player.setRouteEvent(new RouteEvent(npc, new Runnable() {
			@Override
			public void run() {
				npc.resetWalkSteps();
				if (!player.getControlerManager().processNPCClick4(npc))
					return;
				player.faceEntity(npc);
				npc.faceEntity(player);
				if (PrifddinasCity.handleNPCOption4(player, npc))
					return;
				if (npc.getName().toLowerCase().contains("grand exchange")) {
					player.getGEManager().openItemSets();
					return;
				}
				if (npc.getId() == 3404) {
					player.getDialogueManager().startDialogue("TeplinMacaganD", npc.getId(), 4);
					return;
				}
				if (SlayerMaster.startInteractionForId(player, npc.getId(), 4)) {
					player.getSlayerManager().sendSlayerInterface(SlayerManager.BUY_INTERFACE);
					return;
				}
				if (npc.getId() == 5913) {
					Aubury.teleportToEssenceMines(player, npc);
					return;
				}
				return;
			}
		}));
		return;
	}

	public static void handleExamine(final Player player, InputStream stream) {
		int npcIndex = stream.readUnsignedShort128();
		boolean forceRun = stream.read128Byte() == 1;
		final NPC npc = World.getNPCs().get(npcIndex);
		if (npc == null || npc.isCantInteract() || npc.isDead() || npc.hasFinished()
				|| !player.getMapRegionsIds().contains(npc.getRegionId()))
			return;
		if (forceRun)
			player.setRun(forceRun);
		player.getPackets().sendNPCMessage(0, 15263739, npc, NPCExamines.getExamine(npc));
		player.getPackets().sendResetMinimapFlag();
		int count = 0;
		Drop[] drops = NPCDrops.getDrops(npc.getId());
		if (drops != null) {
			player.getPackets()
					.sendGameMessage("Open client console or press ` on your keyboard to see full npc details");
			for (Drop drop : drops) {
				if (drops == null)
					continue;
				ItemDefinitions defs = ItemDefinitions.getItemDefinitions(drop.getItemId());
				String dropName = defs.getName().toLowerCase();
				count++;
				player.getPackets().sendClientConsoleMessage("<col=00ff00>" + count + ".)</col> ItemName: <col=ff0000>"
						+ dropName + "</col>  DropRate: <col=ff0000>" + drop.getRate() + "%");
			}
			player.getPackets()
					.sendClientConsoleMessage("Total Drop Amount: <col=ff0000>" + count + "</col> NPCname: <col=ff0000>"
							+ npc.getName() + "</col> NPClevel: <col=ff0000>" + npc.getCombatLevel());
		} else {
			player.getPackets().sendClientConsoleMessage("<col=ff000> this npc doesnt have normal drops");
		}
		if (player.isHeadStaff())
			player.getPackets().sendPanelBoxMessage("examined npc: index= " + npcIndex + ", Id=" + npc.getId());
		player.getPackets().sendClientConsoleMessage(
				npc.getName() + " Health: <col=ff0000>" + npc.getHitpoints() + "</col>/" + npc.getMaxHitpoints());
		// Logger.log("NPCHandler", "examined npc: " + npcIndex + ", " +
		// npc.getId());
		// player.getPackets().sendNPCMessage(0, npc,
		// NPCExamines.getExamine(npc) + "");
		if (player.isSpawnsMode()) {
			try {
				if (NPCSpawns.removeSpawn(npc)) {
					player.getPackets().sendGameMessage("Removed spawn!");
					return;
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
			player.getPackets().sendGameMessage("Failed removing spawn!");
		}
		if (Settings.DEBUG || player.getUsername().equalsIgnoreCase(""))
			Logger.log("NPCHandler", "Examined npc: " + npc);

	}
}
