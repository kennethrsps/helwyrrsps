package com.rs.game.player;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

import com.rs.cache.loaders.ClientScriptMap;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.World;
import com.rs.game.item.Item;
import com.rs.game.player.content.TaskTab;
import com.rs.game.player.content.clans.ClansManager;
import com.rs.stream.OutputStream;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

public class GlobalPlayerUpdater implements Serializable {

	private static final long serialVersionUID = 7655608569741626586L;

	private transient int renderEmote;
	private int title;
	private int[] lookI;
	private byte[] colour;
	private boolean male;
	private transient boolean glowRed;
	private transient byte[] appeareanceData;
	private transient byte[] md5AppeareanceDataHash;
	private transient short transformedNpcId;
	private transient boolean hidePlayer;

	/**
	 * The cosmetic items
	 */
	public Item[] cosmeticItems;

	private transient Player player;

	public GlobalPlayerUpdater() {
		male = true;
		renderEmote = -1;
		title = -1;
		resetAppearence();
	}

	public void copyColors(short[] colors) {
		for (byte i = 0; i < this.colour.length; i = (byte) (i + 1))
			if (colors[i] != -1)
				this.colour[i] = (byte) colors[i];
	}

	public void female() {
		lookI[0] = 48; // Hair
		lookI[1] = 1000; // Beard
		lookI[2] = 57; // Torso
		lookI[3] = 65; // Arms
		lookI[4] = 68; // Bracelets
		lookI[5] = 77; // Legs
		lookI[6] = 80; // Shoes
		colour[2] = 16;
		colour[1] = 16;
		colour[0] = 3;
		male = false;
	}

	public void generateAppearenceData() {
		OutputStream stream = new OutputStream(1);
		int flag = 0;
		if (!male)
			flag |= 0x1;
		if (transformedNpcId >= 0)
			flag |= 0x2;
		if (title != 0)
			flag |= title >= 32 && title <= 37 || title == 999 || title == 40 || title == 43 || title == 45
					|| title == 47 || title == 48 || title == 49 || title == 51 || title == 53 || title == 55
					|| title == 56 || title >= 58 && title <= 63 || title == 65 || title == 72 || title == 73
					|| title == 81 || title == 83 || title == 84 || title == 88 || title >= 1500 ? 0x80 : 0x40; // after/before
		stream.writeByte(flag);
		if (title != 0) {
			String titleName = title == 25 ? "<col=c12006>Yt'Haar </col>"

					: title == 1000 ? Colors.gray + (isMale() ? "Ironman" : "Ironwoman") + "</col> "
							: title == 1500 ? Colors.gray + (isMale() ? " the Ironman" : " the Ironwoman") + "</col>"

									: title == 1001
											? Colors.darkRed + (isMale() ? "HC Ironman" : "HC Ironwoman") + "</col> "
											: title == 1501 ? Colors.darkRed
													+ (isMale() ? " the HC Ironman" : " the HC Ironwoman") + "</col>"

													: title == 1502 ? Colors.darkRed + " the Survivor</col>"

															: title == 1002 ? Colors.darkRed + "Easy</col> "
																	: title == 1503 ? Colors.darkRed + " the Easy</col>"

																			: title == 1003
																					? Colors.darkRed
																							+ "Intermediate</col> "
																					: title == 1504 ? Colors.darkRed
																							+ " the Intermediate</col>"

																							: title == 1004
																									? Colors.darkRed
																											+ "Veteran</col> "
																									: title == 1505
																											? Colors.darkRed
																													+ " the Veteran</col>"

																											: title == 1033
																													? Colors.darkRed
																															+ "Expert</col> "
																													: title == 1518
																															? Colors.darkRed
																																	+ " the Expert</col>"

																															: title == 1506
																																	? Colors.brown
																																			+ " the Wishful</col>"
																																	: title == 1507
																																			? Colors.brown
																																					+ " the Generous</col>"
																																			: title == 1508
																																					? Colors.brown
																																							+ " the Millionaire</col>"
																																					: title == 1509
																																							? Colors.gold
																																									+ " the Charitable</col>"
																																							: title == 1510
																																									? Colors.orange
																																											+ " the Billionaire</col>"

																																									: title == 1514
																																											? Colors.green
																																													+ "<shad=000000> of Seasons</col></shad>"

																																											: title == 1515
																																													? Colors.green
																																															+ "<shad=000000> of Guthix</col></shad>"

																																													// Prifddinas
																																													// Titles
																																													: title == 1511
																																															? "<col=FF08A0> of the Trahaearn"
																																															: title == 1516
																																																	? "<col=964F03> of the Hefin"

																																																	// reaper
																																																	// titles
																																																	: title == 1512
																																																			? "<col=DF0101> the Reaper"
																																																			: title == 1513
																																																					? "<col=DF0101><shad=9D1309> the Insane Reaper"
																																																					: title == 1014
																																																							? "<col=DF0101>Final Boss</col> "
																																																							: title == 1015
																																																									? "<col=DF0101><shad=9D1309>Insane Final Boss</col></shad> "

																																																									// Member
																																																									// rank
																																																									// titles
																																																									: title == 1016
																																																											? "<col=B56A02>Bronze member</col> "
																																																											: title == 1017
																																																													? "<col=A3A3A3>Silver member</col> "
																																																													: title == 1018
																																																															? "<col=D6D600>Gold member</col> "
																																																															: title == 1019
																																																																	? "<col=41917B>Platinum member</col> "
																																																																	: title == 1020
																																																																			? "<col=13D6D6>Diamond member</col> "

																																																																			// Special
																																																																			// titles
																																																																			: title == 1021
																																																																					? "<col=FC0000>No-lifer</col> " // Online
																																																																													// time
																																																																													// above
																																																																													// 250h
																																																																					: title == 1022
																																																																							? "<col=977EBF>Enthusiast</col> " // Voted
																																																																																// at
																																																																																// least
																																																																																// 250
																																																																																// times
																																																																							: title == 1023
																																																																									? "<col=D966AF>The Maxed</col> " // Has
																																																																																		// claimed
																																																																																		// max
																																																																																		// cape
																																																																									: title == 1024
																																																																											? "<col=A200FF>The Completionist</col> " // Has
																																																																																						// claimed
																																																																																						// comp
																																																																																						// cape
																																																																											: title == 1025
																																																																													? "<col=6200FF>The Perfectionist</col> " // Has
																																																																																								// claimed
																																																																																								// trimemd
																																																																																								// comp
																																																																																								// cape

																																																																													// Player-owned-port
																																																																													// titles
																																																																													: title == 1517
																																																																															? Colors.darkRed
																																																																																	+ " the Cabin "
																																																																																	+ (isMale()
																																																																																			? "Boy"
																																																																																			: "Girl") // 1
																																																																																						// point
																																																																															: title == 1026
																																																																																	? Colors.darkRed
																																																																																			+ "Bo'sun</col> " // 400
																																																																																								// point
																																																																																	: title == 1027
																																																																																			? Colors.darkRed
																																																																																					+ "First Mate</col> " // 800
																																																																																											// point
																																																																																			: title == 1028
																																																																																					? Colors.darkRed
																																																																																							+ "Cap'n</col> " // 1200
																																																																																												// point
																																																																																					: title == 1029
																																																																																							? Colors.darkRed
																																																																																									+ "Commodore</col> " // 1600
																																																																																															// point
																																																																																							: title == 1030
																																																																																									? Colors.darkRed
																																																																																											+ "Admiral</col> " // 2000
																																																																																																// point
																																																																																									: title == 1031
																																																																																											? Colors.darkRed
																																																																																													+ "Admiral of the Fleet</col> " // 3500
																																																																																																					// point
																																																																																											: title == 1032
																																																																																													? Colors.darkRed
																																																																																															+ "Portmaster</col> " // 4500
																																																																																																					// point

																																																																																													// Christmas
																																																																																													// titles
																																																																																													: title == 2001
																																																																																															? Colors.red
																																																																																																	+ " of</col>"
																																																																																																	+ Colors.green
																																																																																																	+ " Christmas</col>"
																																																																																															: title == 2002
																																																																																																	? Colors.rcyan
																																																																																																			+ " of Winter</col>"
																																																																																																	: title == 2003
																																																																																																			? Colors.green
																																																																																																					+ " the Grinch</col>"
																																																																																																			: title == 2004
																																																																																																					? Colors.cyan
																																																																																																							+ " Frostweb</col>"
																																																																																																					:

																																																																																																					ClientScriptMap
																																																																																																							.getMap(male
																																																																																																									? 1093
																																																																																																									: 3872)
																																																																																																							.getStringValue(
																																																																																																									title);
			stream.writeVersionedString(titleName);
		}
		stream.writeByte(player.hasSkull() ? player.getSkullId() : -1); // pk
																		// icon
		stream.writeByte(player.getPrayer().getPrayerHeadIcon()); // prayer icon
		stream.writeByte(hidePlayer ? 1 : 0);
		// npc
		if (transformedNpcId >= 0) {
			stream.writeShort(-1); // 65535 tells it a npc
			stream.writeShort(transformedNpcId);
			stream.writeByte(0);
		}else {
			// player
			Item[] cosmetics = player.getEquipment().getCosmeticItems().getItems();

			boolean[] hiddenIndex = player.getEquipment().getHiddenSlots();
			for (int index = 0; index < 4; index++) {
				Item item = player.getEquipment().getItems().get(index);
				if (glowRed) {
					if (index == 0) {
						stream.writeShort(0x4000 + 2910);
						continue;
					}
					if (index == 1) {
						stream.writeShort(0x4000 + 14641);
						continue;
					}
				}
				
				if (item == null) {
					if(cosmetics[index] != null)
						stream.writeShort(16384 + cosmetics[index].getId());
					else
						stream.writeByte(0);
				} else {
					if(cosmetics[index] != null)
						stream.writeShort(16384 + cosmetics[index].getId());
					else
						stream.writeShort(16384 + item.getId());
				}
			}
		
			/**
			  * chest render
			  **/
			Item item = player.getEquipment().getItems().get(Equipment.SLOT_CHEST);
			if(item == null){
				if(cosmetics[Equipment.SLOT_CHEST] != null && player.getEquipment().isCanDisplayCosmetic())
					stream.writeShort(0x4000 + cosmetics[Equipment.SLOT_CHEST].getId());
				else
				stream.writeShort(0x100 + lookI[2]);
			} else {
				if(cosmetics[Equipment.SLOT_CHEST] != null && player.getEquipment().isCanDisplayCosmetic())
					stream.writeShort(0x4000 + cosmetics[Equipment.SLOT_CHEST].getId());
				else
				stream.writeShort(0x4000 + item.getId());
			}
			/*
			 * shield render
			 */
			item = player.getEquipment().getItems().get(Equipment.SLOT_SHIELD);
			if (item == null) {
				if(cosmetics[Equipment.SLOT_SHIELD] != null && player.getEquipment().isCanDisplayCosmetic())
					stream.writeShort(0x4000 + cosmetics[Equipment.SLOT_SHIELD].getId());
				else
				stream.writeByte(0);
			} else {
				if(cosmetics[Equipment.SLOT_SHIELD] != null && player.getEquipment().isCanDisplayCosmetic())
					stream.writeShort(0x4000 + cosmetics[Equipment.SLOT_SHIELD].getId());
				else
				stream.writeShort(0x4000 + item.getId());
			}
			/*
			 * chest without arms
			 */
			item = player.getEquipment().getItems().get(Equipment.SLOT_CHEST);
			if (item == null || !Equipment.hideArms(item)) {
					stream.writeShort(0x100 + lookI[3]);
			} else {
			
				stream.writeByte(0);
			}
			/*
			 * legs render
			 */
			item = player.getEquipment().getItems().get(Equipment.SLOT_LEGS);
			if(item == null){
				if(glowRed)
					stream.writeShort(0x4000 + 2908);
				else {
					if(cosmetics[Equipment.SLOT_LEGS] != null && player.getEquipment().isCanDisplayCosmetic())
						stream.writeShort(0x4000 + cosmetics[Equipment.SLOT_LEGS].getId());
					else
					stream.writeShort(0x100 + lookI[5]);
				}
				
				 
			} else {
				if(glowRed)
					stream.writeShort(0x4000 + 2908);
				else {
					if(cosmetics[Equipment.SLOT_LEGS] != null && player.getEquipment().isCanDisplayCosmetic())
						stream.writeShort(0x4000 + cosmetics[Equipment.SLOT_LEGS].getId());
					else
						stream.writeShort(0x4000 + item.getId());
				}
				
			}
			/*
			 * hat with hair
			 */
			item = player.getEquipment().getItems().get(Equipment.SLOT_HAT);
			if (!glowRed && (item == null || !Equipment.hideHair(item))) {
				stream.writeShort(0x100 + lookI[0]);
			} else {
				stream.writeByte(0);
			}
			/* hands render */
			item = player.getEquipment().getItems().get(Equipment.SLOT_HANDS);
			stream.writeShort(glowRed ? 0x4000 + 2912 : item == null ? 0x100 + lookI[4] : 0x4000 + item.getId());
			 /* feed render */
			item = player.getEquipment().getItems().get(Equipment.SLOT_FEET);
			stream.writeShort(glowRed ? 0x4000 + 2904 : item == null ? 0x100 + lookI[6] : 0x4000 + item.getId());
			item = player.getEquipment().getItems().get(male ? Equipment.SLOT_HAT : Equipment.SLOT_CHEST);
			if (item == null || male && Equipment.showBear(item)) {
				stream.writeShort(0x100 + lookI[1]);
			} else {
				stream.writeByte(0);
			}
			item = player.getEquipment().getItems().get(Equipment.SLOT_AURA);
			if (item == null) {
				stream.writeByte(0);
			} else {
				stream.writeShort(0x4000 + item.getId());
			}
			stream.writeByte(0);
			stream.writeByte(0);
			stream.writeByte(0);
			int pos = stream.getOffset();
			stream.writeShort(0);
			int hash = 0;
			int slotFlag = -1;
			for (int slotId = 0; slotId < player.getEquipment().getItems().getSize(); slotId++) {
				if (Equipment.DISABLED_SLOTS[slotId] != 0) {
					continue;
				}
				slotFlag++;
				if (slotId == Equipment.SLOT_HAT) {
					int hatId = player.getEquipment().getHatId();
					if (hatId == 20768 || hatId == 20770 || hatId == 20772) {
						ItemDefinitions defs = ItemDefinitions.getItemDefinitions(hatId - 1);
						if (hatId == 20768 && Arrays.equals(player.getMaxedCapeCustomized(), defs.originalModelColors) || (hatId == 20770 || hatId == 20772) && Arrays.equals(player.getCompletionistCapeCustomized(), defs.originalModelColors)) {
							continue;
						}
						hash |= 1 << slotFlag;
						stream.writeByte(0x4); // modify 4 model colors
						int[] hat = hatId == 20768 ? player.getMaxedCapeCustomized() : player.getCompletionistCapeCustomized();
						int slots = 0 | 1 << 4 | 2 << 8 | 3 << 12;
						stream.writeShort(slots);
						for (int i = 0; i < 4; i++) {
							stream.writeShort(hat[i]);
						}
					}
				} else if (slotId == Equipment.SLOT_WEAPON) {
					int weaponId = player.getEquipment().getWeaponId();
					if (weaponId == 20709) {
						ClansManager manager = player.getClanManager();
						if (manager == null) {
							continue;
						}
						int[] colors = manager.getClan().getMottifColors();
						ItemDefinitions defs = ItemDefinitions.getItemDefinitions(20709);
						boolean modifyColor = !Arrays.equals(colors, defs.originalModelColors);
						int bottom = manager.getClan().getMottifBottom();
						int top = manager.getClan().getMottifTop();
						if (bottom == 0 && top == 0 && !modifyColor) {
							continue;
						}
						hash |= 1 << slotFlag;
						stream.writeByte((modifyColor ? 0x4 : 0) | (bottom != 0 || top != 0 ? 0x8 : 0));
						if (modifyColor) {
							int slots = 0 | 1 << 4 | 2 << 8 | 3 << 12;
							stream.writeShort(slots);
							for (int i = 0; i < 4; i++) {
								stream.writeShort(colors[i]);
							}
						}
						if (bottom != 0 || top != 0) {
							int slots = 0 | 1 << 4;
							stream.writeByte(slots);
							stream.writeShort(ClansManager.getMottifTexture(top));
							stream.writeShort(ClansManager.getMottifTexture(bottom));
						}

					}
				} else if (slotId == Equipment.SLOT_CAPE) {
					int capeId = player.getEquipment().getCapeId();
					if (capeId == 20767 || capeId == 20769 || capeId == 20771 || capeId == 32152 || capeId == 32153) {
						ItemDefinitions defs = ItemDefinitions.getItemDefinitions(capeId);
						if (capeId == 20767 && Arrays.equals(player.getMaxedCapeCustomized(), defs.originalModelColors) || (capeId == 20769 || capeId == 20771 || capeId == 32152 || capeId == 32153) && Arrays.equals(player.getCompletionistCapeCustomized(), defs.originalModelColors)) {
							continue;
						}
						hash |= 1 << slotFlag;
						stream.writeByte(0x4); // modify 4 model colors
						int[] cape = capeId == 20767 ? player.getMaxedCapeCustomized() : player.getCompletionistCapeCustomized();
						int slots = 0 | 1 << 4 | 2 << 8 | 3 << 12;
						stream.writeShort(slots);
						for (int i = 0; i < 4; i++) {
							stream.writeShort(cape[i]);
						}
					} else if (capeId == 20708) {
						ClansManager manager = player.getClanManager();
						if (manager == null) {
							continue;
						}
						int[] colors = manager.getClan().getMottifColors();
						ItemDefinitions defs = ItemDefinitions.getItemDefinitions(20709);
						boolean modifyColor = !Arrays.equals(colors, defs.originalModelColors);
						int bottom = manager.getClan().getMottifBottom();
						int top = manager.getClan().getMottifTop();
						if (bottom == 0 && top == 0 && !modifyColor) {
							continue;
						}
						hash |= 1 << slotFlag;
						stream.writeByte((modifyColor ? 0x4 : 0) | (bottom != 0 || top != 0 ? 0x8 : 0));
						if (modifyColor) {
							int slots = 0 | 1 << 4 | 2 << 8 | 3 << 12;
							stream.writeShort(slots);
							for (int i = 0; i < 4; i++) {
								stream.writeShort(colors[i]);
							}
						}
						if (bottom != 0 || top != 0) {
							int slots = 0 | 1 << 4;
							stream.writeByte(slots);
							stream.writeShort(ClansManager.getMottifTexture(top));
							stream.writeShort(ClansManager.getMottifTexture(bottom));
						}

					}
				} else if (slotId == Equipment.SLOT_AURA) {
					int auraId = player.getEquipment().getAuraId();
					if (auraId == -1 || !player.getAuraManager().isActivated()) {
						continue;
					}
					ItemDefinitions auraDefs = ItemDefinitions.getItemDefinitions(auraId);
					if (auraDefs.getMaleWornModelId1() == -1 || auraDefs.getFemaleWornModelId1() == -1) {
						continue;
					}
					hash |= 1 << slotFlag;
					stream.writeByte(0x1); // modify model ids
					int modelId = player.getAuraManager().getAuraModelId();
					stream.writeBigSmart(modelId); // male modelid1
					stream.writeBigSmart(modelId); // female modelid1
					if (auraDefs.getMaleWornModelId2() != -1 || auraDefs.getFemaleWornModelId2() != -1) {
						int modelId2 = player.getAuraManager().getAuraModelId2();
						stream.writeBigSmart(modelId2);
						stream.writeBigSmart(modelId2);
					}
				}
			}
			int pos2 = stream.getOffset();
			stream.setOffset(pos);
			stream.writeShort(hash);
			stream.setOffset(pos2);
		}

		for (byte element : colour) {
			// colour length 10
			stream.writeByte(element);
		}

		stream.writeShort(getRenderEmote());
		stream.writeString(player.getDisplayName());
		boolean pvpArea = World.isPvpArea(player);
		stream.writeByte(pvpArea ? player.getSkills().getCombatLevel() : player.getSkills().getCombatLevelWithSummoning());
		stream.writeByte(pvpArea ? player.getSkills().getCombatLevelWithSummoning() : 0);
		stream.writeByte(-1); // higher level acc name appears in front :P
		stream.writeByte(transformedNpcId >= 0 ? 1 : 0); // to end here else id
		// need to send more
		// data
		if (transformedNpcId >= 0) {
			NPCDefinitions defs = NPCDefinitions.getNPCDefinitions(transformedNpcId);
			stream.writeShort(defs.anInt3029);
			stream.writeShort(defs.anInt3065);
			stream.writeShort(defs.anInt3050);
			stream.writeShort(defs.anInt3042);
			stream.writeByte(defs.anInt3068);
		}

		stream.writeByte(0); // Particles customization look @ R-S if you want
								// this

		// done separated for safe because of synchronization
		byte[] appeareanceData = new byte[stream.getOffset()];
		System.arraycopy(stream.getBuffer(), 0, appeareanceData, 0, appeareanceData.length);
		byte[] md5Hash = Utils.encryptUsingMD5(appeareanceData);
		this.appeareanceData = appeareanceData;
		md5AppeareanceDataHash = md5Hash;
		TaskTab.sendTab(player);
	}

	public byte[] getAppeareanceData() {
		return appeareanceData;
	}

	public int getBeardStyle() {
		return lookI[1];
	}

	public int getFacialHair() {
		return lookI[1];
	}

	public int getHairColor() {
		return colour[0];
	}

	public int getHairStyle() {
		return lookI[0];
	}

	public byte[] getMD5AppeareanceDataHash() {
		return md5AppeareanceDataHash;
	}

	public int getRenderEmote() {
		Item[] cosmetics = player.getEquipment().getCosmeticItems().getItems();
		if (renderEmote >= 0)
			return renderEmote;
		if (transformedNpcId >= 0) {
			NPCDefinitions defs = NPCDefinitions.getNPCDefinitions(transformedNpcId);
			HashMap<Integer, Object> data = defs.clientScriptData;
			if (data != null && !data.containsKey(2805))
				return defs.renderEmote;
		}
		if (!player.getCombatDefinitions().isCombatStance() && transformedNpcId < 0) {
			if (player.getAnimations().sandWalk)
				return 3388;
			else if (player.getAnimations().sadWalk)
				return 3531;
			else if (player.getAnimations().angryWalk)
				return 3529;
			else if (player.getAnimations().proudWalk)
				return 3528;
			else if (player.getAnimations().happyWalk)
				return 3527;
			else if (player.getAnimations().barbarianWalk)
				return 3525;
			else if (player.getAnimations().revenantWalk)
				return 3600;
			if (player.getCombatDefinitions().isSheathe())
				return 2699;
		}
		if ((cosmetics[Equipment.SLOT_WEAPON] != null && player.getEquipment().isCanDisplayCosmetic())) {
			return player.getEquipment().getWeaponRenderEmote();
		}
		return player.getEquipment().getWeaponStance();
	}

	public int getSize() {
		if (transformedNpcId >= 0)
			return NPCDefinitions.getNPCDefinitions(transformedNpcId).size;
		return 1;
	}

	public int getSkinColor() {
		return colour[4];
	}

	public int getTopStyle() {
		return lookI[2];
	}

	public boolean isFemale() {
		return !male;
	}

	public boolean isGlowRed() {
		return glowRed;
	}

	public boolean isHidden() {
		return hidePlayer;
	}

	public boolean isMale() {
		return male;
	}

	public void male() {
		lookI[0] = 3; // Hair
		lookI[1] = 14; // Beard
		lookI[2] = 18; // Torso
		lookI[3] = 26; // Arms
		lookI[4] = 34; // Bracelets
		lookI[5] = 38; // Legs
		lookI[6] = 42; // Shoes~
		colour[2] = 16;
		colour[1] = 16;
		colour[0] = 3;
		male = true;
	}

	public void resetAppearence() {
		lookI = new int[7];
		colour = new byte[10];
		if (cosmeticItems == null)
			cosmeticItems = new Item[14];
		male();
	}

	public void setArmsStyle(int i) {
		lookI[3] = i;
	}

	public void setBeardStyle(int i) {
		lookI[1] = i;
	}

	public void setColor(int i, int i2) {
		colour[i] = (byte) i2;
	}

	public void setFacialHair(int i) {
		lookI[1] = i;
	}

	public void setGlowRed(boolean glowRed) {
		this.glowRed = glowRed;
		generateAppearenceData();
	}

	public void setHairColor(int color) {
		colour[0] = (byte) color;
	}

	public void setHairStyle(int i) {
		lookI[0] = i;
	}

	public void setLegsColor(int color) {
		colour[2] = (byte) color;
	}

	public void setLegsStyle(int i) {
		lookI[5] = i;
	}

	public void setLook(int i, int i2) {
		lookI[i] = i2;
	}

	public void setLooks(short[] look) {
		for (byte i = 0; i < this.lookI.length; i = (byte) (i + 1))
			if (look[i] != -1)
				this.lookI[i] = look[i];
	}

	public void setMale(boolean male) {
		this.male = male;
	}

	public void setPlayer(Player player) {
		this.player = player;
		transformedNpcId = -1;
		renderEmote = -1;
		if (lookI == null || cosmeticItems == null)
			resetAppearence();
	}

	public void setRenderEmote(int id) {
		this.renderEmote = id;
		generateAppearenceData();
	}

	public void setSkinColor(int color) {
		colour[4] = (byte) color;
	}

	public void setTitle(int title) {
		this.title = title;
		generateAppearenceData();
	}

	public int getTitle() {
		return title;
	}

	public void setTopColor(int color) {
		colour[1] = (byte) color;
	}

	public void setTopStyle(int i) {
		lookI[2] = i;
	}

	public void setWristsStyle(int i) {
		lookI[4] = i;
	}

	public void switchHidden() {
		hidePlayer = !hidePlayer;
		generateAppearenceData();
	}

	public void transformIntoNPC(int id) {
		transformedNpcId = (short) id;
		generateAppearenceData();
	}

	public int getTransformedNpcId() {
		return transformedNpcId;
	}

	private ItemModify[] generateItemModify(Item[] items, Item[] cosmetics) {
		ItemModify[] modify = new ItemModify[15];
		for (int slotId = 0; slotId < modify.length; slotId++) {
			if ((slotId == Equipment.SLOT_WEAPON || slotId == Equipment.SLOT_SHIELD)
					&& player.getCombatDefinitions().isSheathe()
					&& player.getEquipment().getCosmeticItems().getItems() == cosmetics) {
				Item item = items[slotId];
				if (item != null) {
					int modelId = items[slotId].getDefinitions().getSheatheModelId();
					setItemModifyModel(items[slotId], slotId, modify, modelId, modelId, -1, -1, -1, -1);
				}
			}
			if (items[slotId] != null && items[slotId] == cosmetics[slotId]) {
				int id = cosmetics[slotId] == null ? -1 : cosmetics[slotId].getId();
				if (id == 32152 || id == 32153 || id == 20768 || id == 20770 || id == 20772 || id == 20767
						|| id == 20769 || id == 20771)
					setItemModifyColor(items[slotId], slotId, modify, id == 32151 || id == 20768 || id == 20767
							? player.getMaxedCapeCustomized() : player.getCompletionistCapeCustomized());
				else {
					int[] colors = new int[4];
					colors[0] = player.getEquipment().getCostumeColor();
					colors[1] = colors[0] + 12;
					colors[2] = colors[1] + 12;
					colors[3] = colors[2] + 12;
					setItemModifyColor(items[slotId], slotId, modify, colors);
				}
			} else {
				int id = items[slotId] == null ? -1 : items[slotId].getId();
				if (id == 32152 || id == 32153 || id == 20768 || id == 20770 || id == 20772 || id == 20767
						|| id == 20769 || id == 20771)
					setItemModifyColor(items[slotId], slotId, modify, id == 32151 || id == 20768 || id == 20767
							? player.getMaxedCapeCustomized() : player.getCompletionistCapeCustomized());
				else if (id == 20708 || id == 20709) {
					ClansManager manager = player.getClanManager();
					if (manager == null)
						continue;
					int[] colors = manager.getClan().getMottifColors();
					setItemModifyColor(items[slotId], slotId, modify, colors);
					setItemModifyTexture(items[slotId], slotId, modify,
							new short[] { (short) ClansManager.getMottifTexture(manager.getClan().getMottifTop()),
									(short) ClansManager.getMottifTexture(manager.getClan().getMottifBottom()) });
				}
			}
		}
		return modify;
	}

	private void setItemModifyModel(Item item, int slotId, ItemModify[] modify, int maleModelId1, int femaleModelId1,
			int maleModelId2, int femaleModelId2, int maleModelId3, int femaleModelId3) {
		if (item == null)
			return;
		ItemDefinitions defs = item.getDefinitions();
		if (defs.getMaleWornModelId1() == -1 || defs.getFemaleWornModelId1() == -1)
			return;
		if (modify[slotId] == null)
			modify[slotId] = new ItemModify();
		modify[slotId].maleModelId1 = maleModelId1;
		modify[slotId].femaleModelId1 = femaleModelId1;
		if (defs.getMaleWornModelId2() != -1 || defs.getFemaleWornModelId2() != -1) {
			modify[slotId].maleModelId2 = maleModelId2;
			modify[slotId].femaleModelId2 = femaleModelId2;
		}
		if (defs.getMaleWornModelId3() != -1 || defs.getFemaleWornModelId3() != -1) {
			modify[slotId].maleModelId2 = maleModelId3;
			modify[slotId].femaleModelId2 = femaleModelId3;
		}
	}

	private void setItemModifyTexture(Item item, int slotId, ItemModify[] modify, short[] textures) {
		ItemDefinitions defs = item.getDefinitions();
		if (defs.originalTextureColors == null || defs.originalTextureColors.length != textures.length)
			return;
		if (Arrays.equals(textures, defs.originalTextureColors))
			return;
		if (modify[slotId] == null)
			modify[slotId] = new ItemModify();
		modify[slotId].textures = textures;
	}

	private void setItemModifyColor(Item item, int slotId, ItemModify[] modify, int[] colors) {
		ItemDefinitions defs = item.getDefinitions();
		if (defs.originalModelColors == null || defs.originalModelColors.length != colors.length)
			return;
		if (Arrays.equals(colors, defs.originalModelColors))
			return;
		if (modify[slotId] == null)
			modify[slotId] = new ItemModify();
		modify[slotId].colors = colors;
	}

	private static class ItemModify {

		private int[] colors;
		private short[] textures;
		private int maleModelId1;
		private int femaleModelId1;
		private int maleModelId2;
		private int femaleModelId2;
		private int maleModelId3;
		private int femaleModelId3;

		private ItemModify() {
			maleModelId1 = femaleModelId1 = -1;
			maleModelId2 = femaleModelId2 = -2;
			maleModelId3 = femaleModelId3 = -2;
		}
	}

	public static String getTitle(boolean male, int title) {
		return title == 0 ? null : ClientScriptMap.getMap(male ? 1093 : 3872).getStringValue(title);
	}

	public boolean isNPC() {
		return transformedNpcId != -1;
	}
}