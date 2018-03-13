package com.rs.game.player.dialogue.impl;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.player.Equipment;
import com.rs.game.player.Equipment.Cosmetic;
import com.rs.game.player.content.CosmeticsHandler;
import com.rs.game.item.Item;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Utils;

public class CosmeticsD extends Dialogue {

	private int slotId;
	private ArrayList<Integer> availableCostumes;
	private int[][] pages;
	private int currentPage;
	private int maxPagesNeeded;
	private Cosmetic choosenCosmetic;
	private String keyWord;

	@Override
	public void start() {
		slotId = (int) this.parameters[0];
		keyWord = parameters.length > 1 ? ((String) this.parameters[1]).toLowerCase() : null;
		availableCostumes = new ArrayList<Integer>();
		if (slotId == Equipment.SLOT_ARROWS) {
			for (int i = 0; i < CosmeticsHandler.FULL_OUTFITS.length; i++) {
				if (keyWord != null && !CosmeticsHandler.OUTFIT_NAMES[i].toLowerCase().contains(keyWord))
					continue;
				availableCostumes.add(i);
			}
		} else if (slotId == Equipment.SLOT_RING) {
			if (player.getEquipment().getSavedCosmetics().isEmpty()) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You don't have any saved costumes. To save your current costume do ::savecurrentcostume or ::savecurrentcosmetic .");
				return;
			}
			for (int i = 0; i < player.getEquipment().getSavedCosmetics().size(); i++) {
				Cosmetic cosmetic = player.getEquipment().getSavedCosmetics().get(i);
				if (cosmetic == null)
					continue;
				if (keyWord != null && !cosmetic.getCosmeticName().toLowerCase().contains(keyWord))
					continue;
				availableCostumes.add(i);
			}
		} else {
			for (Item item : player.getEquipment().getKeepSakeItems()) {
				if (item == null)
					continue;
				if (item.getDefinitions().getEquipSlot() != slotId
						|| (keyWord != null && !item.getName().toLowerCase().contains(keyWord)))
					continue;
				availableCostumes.add(item.getId());
			}
			int[] costumes = slotId == Equipment.SLOT_HAT ? CosmeticsHandler.HATS
					: slotId == Equipment.SLOT_CAPE ? CosmeticsHandler.CAPES
							: slotId == Equipment.SLOT_AMULET ? CosmeticsHandler.AMULETS
									: slotId == Equipment.SLOT_CHEST ? CosmeticsHandler.CHESTS
											: slotId == Equipment.SLOT_LEGS ? CosmeticsHandler.LEGS
													: slotId == Equipment.SLOT_HANDS ? CosmeticsHandler.GLOVES
															: slotId == Equipment.SLOT_WEAPON ? CosmeticsHandler.WEAPONS
																	: slotId == Equipment.SLOT_SHIELD
																			? CosmeticsHandler.SHIELDS
																			: slotId == Equipment.SLOT_AURA
																					? CosmeticsHandler.WINGS
																					: CosmeticsHandler.BOOTS;
			for (int costumeItemId : costumes) {
				if (costumeItemId == -1)
					continue;
				if (keyWord != null
						&& !CosmeticsHandler.getNameOnDialogue(costumeItemId).toLowerCase().contains(keyWord))
					continue;
				availableCostumes.add(costumeItemId);
			}
		}
		if (keyWord != null && availableCostumes.isEmpty()) {
			player.getDialogueManager().startDialogue("SimpleMessge",
					"We couldn't find any available costume with keyword:" + keyWord + " .");
			return;
		}
		currentPage = 0;
		if (player.getEquipment().getCosmeticItems().get(slotId) != null) {
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE,
					(player.getEquipment().getHiddenSlots()[slotId] ? "Unhide" : "Hide"), "Remove current costume",
					"Nevermind");
		} else
			sendOptionsDialogue("CHOOSE THE COSTUME YOU WANT, PAGE: " + (currentPage + 1), getDialogueOptions());
	}

	private String[] getDialogueOptions() {
		ArrayList<String> dialogueOptions = new ArrayList<String>(5);
		if (slotId == Equipment.SLOT_RING) {
			maxPagesNeeded = ((int) Math.ceil((double) (availableCostumes.size() / (currentPage == 0 ? 4.00 : 3.00))));
			maxPagesNeeded = maxPagesNeeded == 0 ? 1 : maxPagesNeeded;
			choosenCosmetic = null;
			int index = 0;
			pages = new int[maxPagesNeeded][4];
			for (int i = 0; i < pages.length; i++) {
				for (int j = 0; j < (i > 0 ? (pages[i].length - 1) : pages[i].length); j++) {
					pages[i][j] = -1;
				}
			}
			for (int i = 0; i < pages.length; i++) {
				for (int j = 0; j < (i > 0 ? (pages[i].length - 1) : pages[i].length); j++) {
					if (index > (availableCostumes.size() - 1))
						continue;
					pages[i][j] = availableCostumes.get(index);
					index++;
				}
			}
			List<Cosmetic> savedCosmetics = player.getEquipment().getSavedCosmetics();
			dialogueOptions.add(currentPage == 0
					? (Utils.formatPlayerNameForDisplay(savedCosmetics.get(pages[currentPage][0]).getCosmeticName()))
					: "Back");
			int itemsCount = getItemsCount();

			if (currentPage == 0)
				itemsCount--;

			for (int i = 0; i < itemsCount; i++) {
				Cosmetic cosmetic = savedCosmetics.get(pages[currentPage][currentPage == 0 ? (i + 1) : i]);
				String name = Utils.formatPlayerNameForDisplay(cosmetic.getCosmeticName());
				dialogueOptions.add(name);
			}

			if (currentPage < (maxPagesNeeded - 1))
				dialogueOptions.add("More");
			else
				dialogueOptions.add("Cancel");
		} else {
			maxPagesNeeded = ((int) Math.ceil((double) (availableCostumes.size() / 3.00)));
			maxPagesNeeded = maxPagesNeeded == 0 ? 1 : maxPagesNeeded;
			int index = 0;
			pages = new int[maxPagesNeeded][3];
			for (int i = 0; i < pages.length; i++) {
				for (int j = 0; j < pages[i].length; j++) {
					if (index > (availableCostumes.size() - 1))
						continue;
					pages[i][j] = availableCostumes.get(index);
					index++;
				}
			}
			dialogueOptions.add(currentPage == 0
					? (slotId == Equipment.SLOT_ARROWS ? (player.getEquipment().isHideAll() ? "Unhide All" : "Hide All")
							: (player.getEquipment().getHiddenSlots()[slotId] ? "Unhide" : "Hide"))
					: "Back");
			int itemsCount = getItemsCount();
			if (slotId == Equipment.SLOT_ARROWS) {
				for (int i = 0; i < itemsCount; i++) {
					boolean restricted = CosmeticsHandler.isRestrictedOutfit(player, pages[currentPage][i]);
					dialogueOptions.add((restricted ? "<col=ff0000>" : "<col=00ff00>")
							+ CosmeticsHandler.OUTFIT_NAMES[pages[currentPage][i]]);
				}
			} else {
				for (int i = 0; i < itemsCount; i++) {
					Item item = new Item(pages[currentPage][i]);
					if (item.getId() != 0) {
						String name = CosmeticsHandler.getNameOnDialogue(item.getId());
						boolean restricted = CosmeticsHandler.isRestrictedItem(player, item.getId());
						dialogueOptions.add((restricted ? "<col=ff0000>" : "<col=00ff00>") + name);
					}
				}
			}
			if (currentPage < (maxPagesNeeded - 1))
				dialogueOptions.add("More");
			else
				dialogueOptions.add("Cancel");
		}
		String[] options = new String[dialogueOptions.size()];
		for (int i = 0; i < options.length; i++) {
			String option = dialogueOptions.get(i);
			if (option == null)
				continue;
			options[i] = option;
		}
		return options;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (player.getEquipment().getCosmeticItems().get(slotId) != null) {
			if (componentId == OPTION_1)
				player.getEquipment().getHiddenSlots()[slotId] = !player.getEquipment().getHiddenSlots()[slotId];
			else if (componentId == OPTION_2)
				player.getEquipment().getCosmeticItems().set(slotId, null);
			player.getGlobalPlayerUpdater().generateAppearenceData();
			end();
			return;
		}
		int itemsCount = getItemsCount();
		List<Cosmetic> savedCosmetics = player.getEquipment().getSavedCosmetics();
		switch (stage) {
		case -1:
			switch (componentId) {
			case OPTION_1:
				if (slotId == Equipment.SLOT_RING) {
					if (currentPage == 0) {
						choosenCosmetic = savedCosmetics.get(pages[currentPage][0]);
						sendSavedCosmeticOptions();
					} else {// back
						currentPage--;
						sendOptionsDialogue("CHOOSE THE KEEPSAKE YOU WANT TO RECLAIM", getDialogueOptions());
					}
				} else {
					if (currentPage == 0) {// hide/unhide
						if (slotId == Equipment.SLOT_ARROWS) {
							player.getEquipment().setHideAll(!player.getEquipment().isHideAll());
							for (int i = 0; i < player.getEquipment().getHiddenSlots().length; i++)
								player.getEquipment().getHiddenSlots()[i] = player.getEquipment().isHideAll();
						} else
							player.getEquipment()
									.getHiddenSlots()[slotId] = !player.getEquipment().getHiddenSlots()[slotId];
						player.getGlobalPlayerUpdater().generateAppearenceData();
						end();
					} else {// back
						currentPage--;
						sendOptionsDialogue("CHOOSE THE COSTUME YOU WANT, PAGE: " + (currentPage + 1),
								getDialogueOptions());
					}
				}
				break;
			case OPTION_2:
				if (slotId == Equipment.SLOT_RING) {
					if (currentPage == 0) {
						if (itemsCount > 1)
							choosenCosmetic = savedCosmetics.get(pages[currentPage][1]);
					} else {
						if (itemsCount > 0)
							choosenCosmetic = savedCosmetics.get(pages[currentPage][0]);
					}
					if (choosenCosmetic != null)
						sendSavedCosmeticOptions();
					else
						end();
				} else {
					if (itemsCount > 0) {
						if (slotId == Equipment.SLOT_ARROWS) {
							int index = pages[currentPage][0];
							if (CosmeticsHandler.isRestrictedOutfit(player, index)) {
								end();
								return;
							}
							player.getEquipment().resetCosmetics();
							for (int itemId : CosmeticsHandler.FULL_OUTFITS[index]) {
								if (itemId == -1)
									continue;
								Item item = new Item(itemId);
								setCostume(item);
							}
						} else {
							Item item = new Item(pages[currentPage][0]);
							setCostume(item);
						}
					}
					end();
				}
				break;
			case OPTION_3:
				if (slotId == Equipment.SLOT_RING) {
					if (currentPage == 0) {
						if (itemsCount > 2)
							choosenCosmetic = savedCosmetics.get(pages[currentPage][2]);
					} else {
						if (itemsCount > 1)
							choosenCosmetic = savedCosmetics.get(pages[currentPage][1]);
					}
					if (choosenCosmetic != null)
						sendSavedCosmeticOptions();
					else
						end();
				} else {
					if (itemsCount > 1) {
						if (slotId == Equipment.SLOT_ARROWS) {
							int index = pages[currentPage][1];
							if (CosmeticsHandler.isRestrictedOutfit(player, index)) {
								end();
								return;
							}
							player.getEquipment().resetCosmetics();
							for (int itemId : CosmeticsHandler.FULL_OUTFITS[index]) {
								if (itemId == -1)
									continue;
								Item item = new Item(itemId);
								setCostume(item);
							}
						} else {
							Item item = new Item(pages[currentPage][1]);
							setCostume(item);
						}
					}
					end();
				}
				break;
			case OPTION_4:
				if (slotId == Equipment.SLOT_RING) {
					if (currentPage == 0) {
						if (itemsCount > 3)
							choosenCosmetic = savedCosmetics.get(pages[currentPage][3]);
					} else {
						if (itemsCount > 2)
							choosenCosmetic = savedCosmetics.get(pages[currentPage][2]);
					}
					if (choosenCosmetic != null)
						sendSavedCosmeticOptions();
					else
						end();
				} else {
					if (itemsCount > 2) {
						if (slotId == Equipment.SLOT_ARROWS) {
							int index = pages[currentPage][2];
							if (CosmeticsHandler.isRestrictedOutfit(player, index)) {
								end();
								return;
							}
							player.getEquipment().resetCosmetics();
							for (int itemId : CosmeticsHandler.FULL_OUTFITS[index]) {
								if (itemId == -1)
									continue;
								Item item = new Item(itemId);
								setCostume(item);
							}
						} else {
							Item item = new Item(pages[currentPage][2]);
							setCostume(item);
						}
					}
					end();
				}
				break;
			case OPTION_5:
				if (currentPage < (maxPagesNeeded - 1)) {
					currentPage++;
					sendOptionsDialogue("CHOOSE THE COSTUME YOU WANT, PAGE: " + (currentPage + 1),
							getDialogueOptions());
				} else
					end();
				break;
			}
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				player.getEquipment().resetCosmetics();
				for (int i = 0; i < player.getEquipment().getHiddenSlots().length; i++) {
					player.getEquipment().getHiddenSlots()[i] = choosenCosmetic.getHiddenSlots()[i];
				}
				for (Item item : choosenCosmetic.getCosmeticItems().getItems()) {
					if (item == null)
						continue;
					setCostume(item);
				}
				player.getGlobalPlayerUpdater().generateAppearenceData();
				end();
				break;
			case OPTION_2:
				int index = getIndex();
				if (index == -1) {
					end();
					return;
				}
				player.getEquipment().getSavedCosmetics().remove(index);
				player.getPackets().sendGameMessage(
						"You have removed " + choosenCosmetic.getCosmeticName() + " from your saved cosmetics.");
				end();
				break;
			case OPTION_3:
				stage = -1;
				sendOptionsDialogue("CHOOSE THE COSTUME YOU WANT, PAGE: " + (currentPage + 1), getDialogueOptions());
				break;
			case OPTION_4:
				end();
				break;
			}
			break;
		}

	}

	public int getIndex() {
		for (int i = 0; i < player.getEquipment().getSavedCosmetics().size(); i++) {
			Cosmetic cosmetic = player.getEquipment().getSavedCosmetics().get(i);
			if (cosmetic == null)
				continue;
			if (cosmetic == choosenCosmetic)
				return i;
		}
		return -1;
	}

	public void sendSavedCosmeticOptions() {
		stage = 0;
		sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Choose this outfit.", "Remove this outfit.", "Back.",
				"Cancel.");
	}

	public void setCostume(Item item) {
		if (CosmeticsHandler.isRestrictedItem(player, item.getId())) {
			player.getPackets().sendGameMessage("You don't have this costume unlocked.");
			return;
		}
		if ((slotId == Equipment.SLOT_ARROWS && item.getDefinitions().getEquipSlot() == Equipment.SLOT_WEAPON)
				|| slotId == Equipment.SLOT_WEAPON) {
			boolean twoHanded = Equipment.isTwoHandedWeapon(item);
			boolean hasShield = player.getEquipment().getCosmeticItems().get(Equipment.SLOT_SHIELD) != null
					|| player.getEquipment().getShieldId() != -1;
			if (twoHanded && hasShield) {
				player.getPackets().sendGameMessage("You can't put on two handed weapon while having a shield.");
				return;
			}
		}
		int itemSlotId = item.getDefinitions().getEquipSlot();
		if (itemSlotId == 18)
			itemSlotId = Equipment.SLOT_CAPE;
		int[] costumes = itemSlotId == Equipment.SLOT_HAT ? CosmeticsHandler.HATS
				: itemSlotId == Equipment.SLOT_CAPE ? CosmeticsHandler.CAPES
						: itemSlotId == Equipment.SLOT_AMULET ? CosmeticsHandler.AMULETS
								: itemSlotId == Equipment.SLOT_CHEST ? CosmeticsHandler.CHESTS
										: itemSlotId == Equipment.SLOT_LEGS ? CosmeticsHandler.LEGS
												: itemSlotId == Equipment.SLOT_HANDS ? CosmeticsHandler.GLOVES
														: itemSlotId == Equipment.SLOT_WEAPON ? CosmeticsHandler.WEAPONS
																: itemSlotId == Equipment.SLOT_SHIELD
																		? CosmeticsHandler.SHIELDS
																		: itemSlotId == Equipment.SLOT_AURA
																				? CosmeticsHandler.WINGS
																				: CosmeticsHandler.BOOTS;
		boolean hasItem = player.getEquipment().containsKeepSakeItem(item.getId());
		for (int i = 0; i < costumes.length; i++) {
			if (item.getId() == costumes[i])
				hasItem = true;
		}
		if (!hasItem) {
			player.getPackets()
					.sendGameMessage("<col=ff0000>We couldn't find " + item.getName() + " in your available outfits.");
			return;
		}
		player.getEquipment().getCosmeticItems()
				.set(slotId == Equipment.SLOT_ARROWS || slotId == Equipment.SLOT_RING ? itemSlotId : slotId, item);
		player.getGlobalPlayerUpdater().generateAppearenceData();
	}

	public int getItemsCount() {
		int itemsCount = 0;
		if (slotId == Equipment.SLOT_ARROWS) {
			for (int i = 0; i < pages[currentPage].length; i++)
				itemsCount++;
		} else if (slotId == Equipment.SLOT_RING) {
			for (int i = 0; i < (currentPage == 0 ? (pages[currentPage].length) : (pages[currentPage].length - 1)); i++)
				if (pages[currentPage][i] != -1)
					itemsCount++;
		} else {
			for (int i = 0; i < pages[currentPage].length; i++) {
				Item item = new Item(pages[currentPage][i]);
				if (item.getId() != 0)
					itemsCount++;
			}
		}
		return itemsCount;
	}

	@Override
	public void finish() {
	}

}
