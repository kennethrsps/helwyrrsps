package com.rs.cache.loaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.rs.cache.Cache;
import com.rs.game.item.Item;
import com.rs.game.player.Equipment;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.Combat;
import com.rs.stream.InputStream;
import com.rs.tools.MapInformationParser;
import com.rs.utils.FileUtilities;
import com.rs.utils.ItemExamines;

@SuppressWarnings("unused")
public final class ItemDefinitions {

	private static final ConcurrentHashMap<Integer, ItemDefinitions> itemsDefinitions = new ConcurrentHashMap<Integer, ItemDefinitions>();

	public int getStageOnDeath() {
		if (clientScriptData == null)
			return 0;
		Object protectedOnDeath = clientScriptData.get(1397);
		if (protectedOnDeath != null && protectedOnDeath instanceof Integer)
			return (Integer) protectedOnDeath;
		return 0;
	}

	public int id;
	public boolean loaded;

	public void setValue(int value) {
		this.value = value;
	}

	public int modelId;
	public String name;

	// model size information
	public int modelZoom;
	public int modelRotation1;
	public int modelRotation2;
	public int modelOffset1;
	public int modelOffset2;

	public short[] originalTextureIds;
	private short[] modifiedTextureIds;

	// extra information
	public int stackable;
	public int value;
	public boolean membersOnly;

	// wearing model information
	public int maleEquip1;
	public int femaleEquip1;
	public int maleEquip2;
	public int femaleEquip2;

	// options
	public String[] groundOptions;
	public String[] inventoryOptions;

	// model information
	public int[] originalModelColors;
	public int[] modifiedModelColors;
	public short[] originalTextureColors;
	public short[] modifiedTextureColors;
	public byte[] unknownArray1;
	public byte[] unknownArray3;
	public int[] unknownArray2;
	// extra information, not used for newer items
	public boolean unnoted;

	public int maleEquipModelId3;
	public int femaleEquipModelId3;
	public int unknownInt1;
	public int unknownInt2;
	public int unknownInt3;
	public int unknownInt4;
	public int unknownInt5;
	public int unknownInt6;
	public int certId;
	public int certTemplateId;
	public int[] stackIds;
	public int[] stackAmounts;
	public int unknownInt7;
	public int unknownInt8;
	public int unknownInt9;
	public int unknownInt10;
	public int unknownInt11;
	public int teamId;
	public int lendId;
	public int lendTemplateId;
	public int unknownInt12;
	public int unknownInt13;
	public int unknownInt14;
	public int unknownInt15;
	public int unknownInt16;
	public int unknownInt17;
	public int unknownInt18;
	public int unknownInt19;
	public int unknownInt20;
	public int unknownInt21;
	public int unknownInt22;
	public int unknownInt23;
	public int equipSlot;
	public int equipType;

	// extra added
	public boolean noted;
	public boolean lended;

	public HashMap<Integer, Object> clientScriptData;
	public HashMap<Integer, Integer> itemRequiriments;
	public int[] unknownArray5;
	public int[] unknownArray4;
	public byte[] unknownArray6;

	public static int getItemDefinitionsSize() {
		return itemsDefinitions != null ? itemsDefinitions.size() : 0;
	}

	public static final ItemDefinitions getItemDefinitions(int itemId) {
		ItemDefinitions def = itemsDefinitions.get(itemId);
		if (def == null)
			itemsDefinitions.put(itemId, def = new ItemDefinitions(itemId));
		return def;
	}

	public static final void clearItemsDefinitions() {
		itemsDefinitions.clear();
	}

	public ItemDefinitions(int id) {
		this.id = id;
		setDefaultsVariableValues();
		setDefaultOptions();
		loadItemDefinitions();
	}

	public String getExamine() {
		return ItemExamines.getExamine(new Item(id));
	}

	public boolean isLoaded() {
		return loaded;
	}

	public int getTipitPrice() {
		try {
			for (String lines : FileUtilities.readFile("./input/entity/items/prices.txt")) {
				String[] data = lines.split(" - ");
				if (Integer.parseInt(data[0]) == id)
					return Integer.parseInt(data[1]);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 1;
	}

	public String getStreetPrice() {
		try {
			for (String lines : FileUtilities
					.readFile(System.getProperty("user.home") + "/Dropbox/streetprice/streetprice.txt")) {
				String[] data = lines.split(" - ");
				if (Integer.parseInt(data[0]) == id)
					return data[1];
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "There is currently no price for this item.";
	}

	public String obtainedItem() {
		try {
			for (String lines : FileUtilities
					.readFile(System.getProperty("user.home") + "/Dropbox/streetprice/guide.txt")) {
				String[] data = lines.split(" - ");
				if (Integer.parseInt(data[0]) == id)
					return data[1];
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "This item currently does not have a guide on how to obtain it.";
	}

	public String usesForItem() {
		try {
			for (String lines : FileUtilities
					.readFile(System.getProperty("user.home") + "/Dropbox/streetprice/uses.txt")) {
				String[] data = lines.split(" - ");
				if (Integer.parseInt(data[0]) == id)
					return data[1];
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "This item has no significant uses listed.";
	}

	public final void loadItemDefinitions() {
		byte[] data = Cache.STORE.getIndexes()[19].getFile(getArchiveId(), getFileId());
		if (data == null) {
			// System.out.println("Failed loading Item " + id+".");
			return;
		}
		readOpcodeValues(new InputStream(data));
		if (certTemplateId != -1)
			toNote();
		if (lendTemplateId != -1)
			toLend();
		if (unknownValue1 != -1)
			toBind();
		loaded = true;
	}

	public void toNote() {
		// ItemDefinitions noteItem; //certTemplateId
		ItemDefinitions realItem = getItemDefinitions(certId);
		membersOnly = realItem.membersOnly;
		value = realItem.value;
		name = realItem.name;
		stackable = 1;
		noted = true;
	}

	public void toBind() {
		// ItemDefinitions lendItem; //lendTemplateId
		ItemDefinitions realItem = getItemDefinitions(unknownValue2);
		originalModelColors = realItem.originalModelColors;
		maleEquipModelId3 = realItem.maleEquipModelId3;
		femaleEquipModelId3 = realItem.femaleEquipModelId3;
		teamId = realItem.teamId;
		value = 0;
		membersOnly = realItem.membersOnly;
		name = realItem.name;
		inventoryOptions = new String[5];
		groundOptions = realItem.groundOptions;
		if (realItem.inventoryOptions != null)
			for (int optionIndex = 0; optionIndex < 4; optionIndex++)
				inventoryOptions[optionIndex] = realItem.inventoryOptions[optionIndex];
		inventoryOptions[4] = "Discard";
		maleEquip1 = realItem.maleEquip1;
		maleEquip2 = realItem.maleEquip2;
		femaleEquip1 = realItem.femaleEquip1;
		femaleEquip2 = realItem.femaleEquip2;
		clientScriptData = realItem.clientScriptData;
		equipSlot = realItem.equipSlot;
		equipType = realItem.equipType;
		this.equipLookHideSlot2 = realItem.equipLookHideSlot2;
	}

	public void toLend() {
		// ItemDefinitions lendItem; //lendTemplateId
		ItemDefinitions realItem = getItemDefinitions(lendId);
		originalModelColors = realItem.originalModelColors;
		maleEquipModelId3 = realItem.maleEquipModelId3;
		femaleEquipModelId3 = realItem.femaleEquipModelId3;
		teamId = realItem.teamId;
		value = 0;
		membersOnly = realItem.membersOnly;
		name = realItem.name;
		inventoryOptions = new String[5];
		groundOptions = realItem.groundOptions;
		if (realItem.inventoryOptions != null)
			for (int optionIndex = 0; optionIndex < 4; optionIndex++)
				inventoryOptions[optionIndex] = realItem.inventoryOptions[optionIndex];
		inventoryOptions[4] = "Discard";
		maleEquip1 = realItem.maleEquip1;
		maleEquip2 = realItem.maleEquip2;
		femaleEquip1 = realItem.femaleEquip1;
		femaleEquip2 = realItem.femaleEquip2;
		clientScriptData = realItem.clientScriptData;
		equipSlot = realItem.equipSlot;
		equipType = realItem.equipType;
		lended = true;
	}

	public int getArchiveId() {
		return getId() >>> 8;
	}

	public int getFileId() {
		return 0xff & getId();
	}

	public boolean isDestroyItem() {
		if (inventoryOptions == null)
			return false;
		for (String option : inventoryOptions) {
			if (option == null)
				continue;
			if (option.equalsIgnoreCase("destroy"))
				return true;
		}
		return false;
	}

	public boolean containsOption(int i, String option) {
		if (inventoryOptions == null || inventoryOptions[i] == null || inventoryOptions.length <= i)
			return false;
		return inventoryOptions[i].equals(option);
	}

	public boolean containsOption(String option) {
		if (inventoryOptions == null)
			return false;
		for (String o : inventoryOptions) {
			if (o == null || !o.equals(option))
				continue;
			return true;
		}
		return false;
	}

	public boolean isWearItem() {
		return equipSlot != -1;
	}

	public boolean isWearItem(boolean male) {
		if (equipSlot < Equipment.SLOT_RING && (male ? getMaleWornModelId1() == -1 : getFemaleWornModelId1() == -1))
			return false;
		return equipSlot != -1;
	}

	public boolean hasSpecialBar() {
		if (clientScriptData == null)
			return false;
		Object specialBar = clientScriptData.get(686);
		if (specialBar != null && specialBar instanceof Integer)
			return (Integer) specialBar == 1;
		return false;
	}

	public int getAttackSpeed() {
		if (clientScriptData == null)
			return 4;
		Object attackSpeed = clientScriptData.get(14);
		if (attackSpeed != null && attackSpeed instanceof Integer)
			return (int) attackSpeed;
		return 4;
	}

	public int getStabAttack() {
		if (clientScriptData == null)
			return 0;
		Object value = clientScriptData.get(0);
		if (value != null && value instanceof Integer)
			return (int) value;
		return 0;
	}

	public int getSlashAttack() {
		if (clientScriptData == null)
			return 0;
		Object value = clientScriptData.get(1);
		if (value != null && value instanceof Integer)
			return (int) value;
		return 0;
	}

	public int getCrushAttack() {
		if (clientScriptData == null)
			return 0;
		Object value = clientScriptData.get(2);
		if (value != null && value instanceof Integer)
			return (int) value;
		return 0;
	}

	public int getMagicAttack() {
		if (clientScriptData == null)
			return 0;
		Object value = clientScriptData.get(3);
		if (value != null && value instanceof Integer)
			return (int) value;
		return 0;
	}

	public int getRangeAttack() {
		if (clientScriptData == null)
			return 0;
		Object value = clientScriptData.get(4);
		if (value != null && value instanceof Integer)
			return (int) value;
		return 0;
	}

	public int getStabDef() {
		if (clientScriptData == null)
			return 0;
		Object value = clientScriptData.get(5);
		if (value != null && value instanceof Integer)
			return (int) value;
		return 0;
	}

	public int getSlashDef() {
		if (clientScriptData == null)
			return 0;
		Object value = clientScriptData.get(6);
		if (value != null && value instanceof Integer)
			return (int) value;
		return 0;
	}

	public int getCrushDef() {
		if (clientScriptData == null)
			return 0;
		Object value = clientScriptData.get(7);
		if (value != null && value instanceof Integer)
			return (int) value;
		return 0;
	}

	public int getMagicDef() {
		if (clientScriptData == null)
			return 0;
		Object value = clientScriptData.get(8);
		if (value != null && value instanceof Integer)
			return (int) value;
		return 0;
	}

	public int getRangeDef() {
		if (clientScriptData == null)
			return 0;
		Object value = clientScriptData.get(9);
		if (value != null && value instanceof Integer)
			return (int) value;
		return 0;
	}

	public int getAbsorveMeleeBonus() {
		if (clientScriptData == null)
			return 0;
		Object value = clientScriptData.get(967);
		if (value != null && value instanceof Integer)
			return (int) value;
		return 0;
	}

	public int getAbsorveMageBonus() {
		if (clientScriptData == null)
			return 0;
		Object value = clientScriptData.get(969);
		if (value != null && value instanceof Integer)
			return (int) value;
		return 0;
	}

	public int getAbsorveRangeBonus() {
		if (clientScriptData == null)
			return 0;
		Object value = clientScriptData.get(968);
		if (value != null && value instanceof Integer)
			return (int) value;
		return 0;
	}

	public int getStrengthBonus() {
		if (clientScriptData == null)
			return 0;
		Object value = clientScriptData.get(641);
		if (value != null && value instanceof Integer)
			return (int) value / 10;
		return 0;
	}

	public int getRangedStrBonus() {
		if (clientScriptData == null)
			return 0;
		Object value = clientScriptData.get(643);
		if (value != null && value instanceof Integer)
			return (int) value / 10;
		return 0;
	}

	public int getModelZoom() {
		return modelZoom;
	}

	public int getModelOffset1() {
		return modelOffset1;
	}

	public int getModelOffset2() {
		return modelOffset2;
	}

	public int getQuestId() {
		if (clientScriptData == null)
			return -1;
		Object questId = clientScriptData.get(861);
		if (questId != null && questId instanceof Integer)
			return (Integer) questId;
		return -1;
	}

	public List<Item> getCreateItemRequirements() {
		if (clientScriptData == null)
			return null;
		List<Item> items = new ArrayList<Item>();
		int requiredId = -1;
		int requiredAmount = -1;
		for (int key : clientScriptData.keySet()) {
			Object value = clientScriptData.get(key);
			if (value instanceof String)
				continue;
			if (key >= 538 && key <= 770) {
				if (key % 2 == 0)
					requiredId = (Integer) value;
				else
					requiredAmount = (Integer) value;
				if (requiredId != -1 && requiredAmount != -1) {
					items.add(new Item(requiredId, requiredAmount));
					requiredId = -1;
					requiredAmount = -1;
				}
			}
		}
		return items;
	}

	public HashMap<Integer, Object> getClientScriptData() {
		return clientScriptData;
	}

	public int getExitCombatEmote() {
		if (MapInformationParser.getMap() == null || clientScriptData == null)
			return 18025;
		final Integer index = (Integer) clientScriptData.get(686);
		if (index == null)
			return 18025;
		final HashMap<Object, Object> map = MapInformationParser.getMap().get(index);
		final Object key = 2918;// needs legacy
		if (map != null) {
			if (map.get(key.toString()) != null) {
				Object value = map.get(key.toString());
				return (int) Integer.valueOf(value.toString());
			}
		}
		return 18025;
	}

	public int getCombatDefenceEmote(final boolean legacy) {
		if (MapInformationParser.getMap() == null || clientScriptData == null)
			return 424;
		final Integer index = (Integer) clientScriptData.get(686);
		if (index == null)
			return 424;
		final HashMap<Object, Object> map = MapInformationParser.getMap().get(index);
		final Object key = (legacy ? 4387 : 2917);// legacy == 4387
		if (map != null) {
			if (map.get(key.toString()) != null) {
				Object value = map.get(key.toString());
				return (int) Integer.valueOf(value.toString());
			}
		}
		return 424;
	}

	public int getRenderAnimId() {
		if (clientScriptData == null)
			return 2698;// 1284;
		Object animId = clientScriptData.get(644);
		if (animId != null && animId instanceof Integer)
			return (Integer) animId;
		animId = clientScriptData.get(2954);// eoc shit
		if (animId != null && animId instanceof Integer)
			return (Integer) animId;
		return 2698;// 1284;
	}

	public int getCombatRenderEmote(final boolean legacy) {
		if (MapInformationParser.getMap() == null || clientScriptData == null)
			return -1;// 2687 was old default
		final Integer index = (Integer) clientScriptData.get(686);
		if (index == null)
			return -1;
		final HashMap<Object, Object> map = MapInformationParser.getMap().get(index);
		final Object key = (legacy ? 2954 : 2955);
		if (map != null) {
			if (map.get(key.toString()) != null) {
				Object value = map.get(key.toString());
				return (int) Integer.valueOf(value.toString());
			}
		}
		return -1;
	}

	public int getStunnedEmote() {
		if (MapInformationParser.getMap() == null || clientScriptData == null)
			return 23613;
		final Integer index = (Integer) clientScriptData.get(686);
		if (index == null)
			return 23613;
		final HashMap<Object, Object> map = MapInformationParser.getMap().get(index);
		final Object key = 4264;
		if (map != null) {
			if (map.get(key.toString()) != null) {
				Object value = map.get(key.toString());
				return (int) Integer.valueOf(value.toString());
			}
		}
		return 23613;
	}

	public int getMainhandEmote(final boolean legacy) {
		if (getName().contains("scim") || getName().contains("rapier") || getName().contains("katana")) {
			return 18241;// 18226;
		} else if (getName().contains("longsword")) {
			return 18241;
		} else if (getName().contains("dart") || getName().contains("knife") || getName().contains("chinchompa")
				|| getName().contains("javelin")) {
			return 18229;
		} else if (getName().contains("maul") || getName().contains("mace") || getName().contains("Staff")) {
			return 18222;
		} else if (getName().contains("spear")) {
			return 18223;
		} else if (getName().contains("2h") || getName().contains("godsword")) {
			return 18236;
		} else if (getName().contains("crossbow")) {
			return 18230;
		} else if (getName().contains("bow")) {
			return 18237;
		} else if (getName().contains("Korasi's sword")) {
			return 18241;
		} else if (getName().contains("axe") || getName().contains("halberd")) {
			return 18245;
		} else {
			return getCombatOpcode(legacy ? 18241 : 18226);
		}
	}

	public int getOffhandEmote(final boolean legacy) {
		if (getName().contains("scimitar") || getName().contains("rapier") || getName().contains("defender")) {
			return 18246;
		} else if (getName().contains("longsword")) {
			return 18246;
		} else if (getName().contains("maul") || getName().contains("mace")) {
			return 18243;
		} else if (getName().contains("dart") || getName().contains("knife")) {
			return 18234;

		} else if (getName().contains("crossbow")) {
			return 18231;
		}
		return getCombatOpcode(legacy ? 18246 : 18246);
	}

	public int getItemTier() {
		if (clientScriptData == null)
			return 0;
		final Object data = clientScriptData.get(750);
		if (data != null && data instanceof Integer)
			return (int) data;
		return 0;
	}

	public int getProjectileId() {
		if (clientScriptData == null)
			return -1;
		final Object data = clientScriptData.get(2940);
		if (data != null && data instanceof Integer)
			return (int) data;
		return -1;
	}

	public int getWeaponAttackDelay() {
		if (clientScriptData == null)
			return 3;
		final Object data = clientScriptData.get(14);
		if (data != null && data instanceof Integer)
			return (int) data;
		return 3;
	}

	public boolean requiresAmmunitionToFunction() {
		if (clientScriptData == null)
			return false;
		final Object data = clientScriptData.get(4329);
		if (data != null && data instanceof Integer)
			return (Integer) data == 1;
		return false;
	}

	public boolean isMagicArmour() {
		if (clientScriptData == null)
			return false;
		final Object data = clientScriptData.get(2823);
		if (data != null && data instanceof Integer)
			return (Integer) data == 1;
		return false;
	}

	public boolean isMagicWeapon() {
		if (clientScriptData == null)
			return false;
		final Object data = clientScriptData.get(2827);
		if (data != null && data instanceof Integer)
			return (Integer) data == 1;
		return false;
	}

	public boolean isMeleeArmour() {
		if (clientScriptData == null)
			return false;
		final Object data = clientScriptData.get(2821);
		if (data != null && data instanceof Integer)
			return (Integer) data == 1;
		return false;
	}

	public boolean isMeleeWeapon() {
		if (clientScriptData == null)
			return false;
		final Object data = clientScriptData.get(2825);
		if (data != null && data instanceof Integer)
			return (Integer) data == 1;
		return false;
	}

	public boolean isRangedArmour() {
		if (clientScriptData == null)
			return false;
		final Object data = clientScriptData.get(2822);
		if (data != null && data instanceof Integer)
			return (Integer) data == 1;
		return false;
	}

	public boolean isRangedWeapon() {
		if (clientScriptData == null)
			return false;
		final Object data = clientScriptData.get(2826);
		if (data != null && data instanceof Integer)
			return (Integer) data == 1;
		return false;
	}

	public boolean isRangedWeaponDoesNotRequireAmmunitionToFunction() {
		if (clientScriptData == null)
			return false;
		// Object data = clientScriptData.get(2665);
		// if (data != null && data instanceof Integer)
		// return (Integer) data == 1;
		final Object data = clientScriptData.get(2195);
		if (data != null && data instanceof Integer)
			return (Integer) data == 1;
		return false;
	}

	public boolean isPoisonous() {
		if (clientScriptData == null)
			return false;
		final Object data = clientScriptData.get(31);
		if (data != null && data instanceof Integer)
			return (Integer) data == 1;
		return false;
	}

	public int getSpecialAttackAmount() {
		if (clientScriptData == null)
			return 0;
		final Object data = clientScriptData.get(4332);
		if (data != null && data instanceof Integer)
			return (Integer) data;
		return 0;
	}

	public boolean isRecolourable() {
		if (clientScriptData == null)
			return false;
		Object colourable = clientScriptData.get(1397);
		if (colourable != null && colourable instanceof Integer)
			return (Integer) colourable == 1;
		colourable = clientScriptData.get(2531);
		if (colourable != null && colourable instanceof Integer)
			return (Integer) colourable == 2;
		return false;
	}

	public String getSpecialAttackDescription() {
		if (clientScriptData == null)
			return null;
		final Object data = clientScriptData.get(4334);
		if (data != null && data instanceof String)
			return (String) data;
		return null;
	}

	public String getSpecialAttackName() {
		if (clientScriptData == null)
			return null;
		final Object data = clientScriptData.get(4333);
		if (data != null && data instanceof String)
			return (String) data;
		return null;
	}

	public int getMeleeDamage() {
		if (clientScriptData == null)
			return 0;
		final Object data = clientScriptData.get(641);
		if (data != null && data instanceof Integer)
			return (int) data;
		return 0;
	}

	public int getRangedDamage() {
		if (clientScriptData == null)
			return 0;
		final Object data = clientScriptData.get(643);
		if (data != null && data instanceof Integer)
			return (int) data;
		return 0;
	}

	public int getMagicDamage() {
		if (clientScriptData == null)
			return 0;
		final Object data = clientScriptData.get(686);
		if (data != null && data instanceof Integer)
			return (Integer) data;
		return 0;
	}

	public int getMeleeAccuracy() {
		if (clientScriptData == null)
			return 0;
		final Object data = clientScriptData.get(3267);
		if (data != null && data instanceof Integer)
			return (int) data;
		return 0;
	}

	public int getMagicAccuracy() {
		if (clientScriptData == null)
			return 0;
		final Object data = clientScriptData.get(3);
		if (data != null && data instanceof Integer)
			return (int) data;
		return 0;
	}

	public int getRangedAccuracy() {
		if (clientScriptData == null)
			return 0;
		final Object data = clientScriptData.get(4);
		if (data != null && data instanceof Integer)
			return (int) data;
		return 0;
	}

	public int getPrayerBonus() {
		if (clientScriptData == null)
			return 0;
		final Object data = clientScriptData.get(2946);
		if (data != null && data instanceof Integer)
			return (int) data;
		return 0;
	}

	public int getMagicBonus() {
		if (clientScriptData == null)
			return 0;
		final Object data = clientScriptData.get(965);
		if (data != null && data instanceof Integer)
			return (int) data;
		return 0;
	}

	public int getRangeBonus() {
		if (clientScriptData == null)
			return 0;
		final Object data = clientScriptData.get(643);
		if (data != null && data instanceof Integer)
			return (int) data;
		return 0;
	}

	public int getArmorBonus() {
		if (clientScriptData == null)
			return 0;
		final Object data = clientScriptData.get(2870);
		if (data != null && data instanceof Integer)
			return (int) data;
		return 0;
	}

	public int getLifeBonus() {
		if (clientScriptData == null)
			return 0;
		final Object data = clientScriptData.get(1326);
		if (data != null && data instanceof Integer)
			return (int) data;
		return 0;
	}

	public int getAbsorbMeleeBonus() {
		if (clientScriptData == null)
			return 0;
		final Object value = clientScriptData.get(967);
		if (value != null && value instanceof Integer)
			return (int) value;
		return 0;
	}

	public int getAbsorbMageBonus() {
		if (clientScriptData == null)
			return 0;
		final Object value = clientScriptData.get(969);
		if (value != null && value instanceof Integer)
			return (int) value;
		return 0;
	}

	public int getAbsorbRangeBonus() {
		if (clientScriptData == null)
			return 0;
		final Object value = clientScriptData.get(968);
		if (value != null && value instanceof Integer)
			return (int) value;
		return 0;
	}

	public int getSummoningDef() {
		if (clientScriptData == null)
			return 0;
		final Object value = clientScriptData.get(417);
		if (value != null && value instanceof Integer)
			return (int) value;
		return 0;
	}

	public boolean isMagicEquipment() {
		if (clientScriptData == null)
			return false;
		final Object data = clientScriptData.get(2823);
		if (data != null && data instanceof Integer)
			return (Integer) data == 1;
		return false;
	}

	public boolean isMeleeEquipment() {
		if (clientScriptData == null)
			return false;
		final Object data = clientScriptData.get(2824);
		if (data != null && data instanceof Integer)
			return (Integer) data == 1;
		return false;
	}

	public boolean isMiscEquipment() {
		return isMagicEquipment() && !isMagicWeapon() || isMeleeEquipment() && !isMeleeWeapon()
				|| isRangedEquipment() && !isRangedWeapon();
	}

	public boolean isRangedEquipment() {
		if (clientScriptData == null)
			return false;
		final Object data = clientScriptData.get(2822);
		if (data != null && data instanceof Integer)
			return (Integer) data == 1;
		return false;
	}

	public HashMap<Integer, Integer> getWearingSkillRequiriments() {
		if (clientScriptData == null)
			return null;
		if (itemRequiriments == null) {
			HashMap<Integer, Integer> skills = new HashMap<Integer, Integer>();
			for (int i = 0; i < 10; i++) {
				Integer skill = (Integer) clientScriptData.get(749 + (i * 2));
				if (skill != null) {
					Integer level = (Integer) clientScriptData.get(750 + (i * 2));
					if (level != null)
						skills.put(skill, level);
				}
			}
			Integer maxedSkill = (Integer) clientScriptData.get(277);
			if (maxedSkill != null)
				skills.put(maxedSkill, getId() == 19709 ? 120 : 99);
			itemRequiriments = skills;
			if (getId() == 7462)
				itemRequiriments.put(Skills.DEFENCE, 40);
			else if (getId() == 19784 || getId() == 22401 || getId() == 19780) { // Korasi
				itemRequiriments.put(Skills.ATTACK, 78);
				itemRequiriments.put(Skills.STRENGTH, 78);
			}

			else if (getId() == 1067)
				itemRequiriments.put(Skills.DEFENCE, 1);

			else if (getId() == 1153)
				itemRequiriments.put(Skills.DEFENCE, 1);

			else if (getId() == 1115)
				itemRequiriments.put(Skills.DEFENCE, 1);

			else if (getId() == 1081)
				itemRequiriments.put(Skills.DEFENCE, 1);

			else if (getId() == 1101)
				itemRequiriments.put(Skills.DEFENCE, 1);

			else if (getId() == 1137)
				itemRequiriments.put(Skills.DEFENCE, 1);

			else if (getId() == 1175)
				itemRequiriments.put(Skills.DEFENCE, 1);

			else if (getId() == 1203)
				itemRequiriments.put(Skills.ATTACK, 1);

			else if (getId() == 1239)
				itemRequiriments.put(Skills.ATTACK, 1);

			else if (getId() == 1267)
				itemRequiriments.put(Skills.ATTACK, 1);

			else if (getId() == 1279)
				itemRequiriments.put(Skills.ATTACK, 1);

			else if (getId() == 1293)
				itemRequiriments.put(Skills.ATTACK, 1);

			else if (getId() == 1309)
				itemRequiriments.put(Skills.ATTACK, 1);

			else if (getId() == 1335)
				itemRequiriments.put(Skills.ATTACK, 1);

			else if (getId() == 1349)
				itemRequiriments.put(Skills.ATTACK, 1);

			else if (getId() == 1363)
				itemRequiriments.put(Skills.ATTACK, 1);

			else if (getId() == 1420)
				itemRequiriments.put(Skills.ATTACK, 1);

			else if (getId() == 3096)
				itemRequiriments.put(Skills.ATTACK, 1);

			else if (getId() == 3192)
				itemRequiriments.put(Skills.ATTACK, 1);

			else if (getId() == 4121)
				itemRequiriments.put(Skills.DEFENCE, 1);

			else if (getId() == 1323)
				itemRequiriments.put(Skills.ATTACK, 1);

			else if (getId() == 1191)
				itemRequiriments.put(Skills.DEFENCE, 1);

			else if (getId() == 1069)
				itemRequiriments.put(Skills.DEFENCE, 5);

			else if (getId() == 1083)
				itemRequiriments.put(Skills.DEFENCE, 5);

			else if (getId() == 1105)
				itemRequiriments.put(Skills.DEFENCE, 5);

			else if (getId() == 1119)
				itemRequiriments.put(Skills.DEFENCE, 5);

			else if (getId() == 1141)
				itemRequiriments.put(Skills.DEFENCE, 5);

			else if (getId() == 1157)
				itemRequiriments.put(Skills.DEFENCE, 5);

			else if (getId() == 1177)
				itemRequiriments.put(Skills.DEFENCE, 5);

			else if (getId() == 1193)
				itemRequiriments.put(Skills.DEFENCE, 5);

			else if (getId() == 1207)
				itemRequiriments.put(Skills.ATTACK, 5);

			else if (getId() == 1207)
				itemRequiriments.put(Skills.ATTACK, 5);

			else if (getId() == 1241)
				itemRequiriments.put(Skills.ATTACK, 5);

			else if (getId() == 1269)
				itemRequiriments.put(Skills.ATTACK, 5);

			else if (getId() == 1281)
				itemRequiriments.put(Skills.ATTACK, 5);

			else if (getId() == 1295)
				itemRequiriments.put(Skills.ATTACK, 5);

			else if (getId() == 1311)
				itemRequiriments.put(Skills.ATTACK, 5);

			else if (getId() == 1325)
				itemRequiriments.put(Skills.ATTACK, 5);

			else if (getId() == 1339)
				itemRequiriments.put(Skills.ATTACK, 5);

			else if (getId() == 1353)
				itemRequiriments.put(Skills.ATTACK, 5);

			else if (getId() == 1365)
				itemRequiriments.put(Skills.ATTACK, 5);

			else if (getId() == 1424)
				itemRequiriments.put(Skills.ATTACK, 5);

			else if (getId() == 3097)
				itemRequiriments.put(Skills.ATTACK, 5);

			else if (getId() == 3194)
				itemRequiriments.put(Skills.ATTACK, 5);

			else if (getId() == 4123)
				itemRequiriments.put(Skills.DEFENCE, 5);

			else if (getId() == 1217)
				itemRequiriments.put(Skills.ATTACK, 10);

			else if (getId() == 1077)
				itemRequiriments.put(Skills.DEFENCE, 10);

			else if (getId() == 1089)
				itemRequiriments.put(Skills.DEFENCE, 10);

			else if (getId() == 1107)
				itemRequiriments.put(Skills.DEFENCE, 10);

			else if (getId() == 1125)
				itemRequiriments.put(Skills.DEFENCE, 10);

			else if (getId() == 1151)
				itemRequiriments.put(Skills.DEFENCE, 10);

			else if (getId() == 1165)
				itemRequiriments.put(Skills.DEFENCE, 10);

			else if (getId() == 1179)
				itemRequiriments.put(Skills.DEFENCE, 10);

			else if (getId() == 1195)
				itemRequiriments.put(Skills.DEFENCE, 10);

			else if (getId() == 1283)
				itemRequiriments.put(Skills.ATTACK, 10);

			else if (getId() == 1297)
				itemRequiriments.put(Skills.ATTACK, 10);

			else if (getId() == 1313)
				itemRequiriments.put(Skills.ATTACK, 10);

			else if (getId() == 1327)
				itemRequiriments.put(Skills.ATTACK, 10);

			else if (getId() == 1341)
				itemRequiriments.put(Skills.ATTACK, 10);

			else if (getId() == 1361)
				itemRequiriments.put(Skills.ATTACK, 10);

			else if (getId() == 1367)
				itemRequiriments.put(Skills.ATTACK, 10);

			else if (getId() == 1426)
				itemRequiriments.put(Skills.ATTACK, 10);

			else if (getId() == 3098)
				itemRequiriments.put(Skills.ATTACK, 10);

			else if (getId() == 3196)
				itemRequiriments.put(Skills.ATTACK, 10);

			else if (getId() == 4125)
				itemRequiriments.put(Skills.DEFENCE, 10);

			else if (getId() == 4580)
				itemRequiriments.put(Skills.ATTACK, 10);

			else if (getId() == 1071)
				itemRequiriments.put(Skills.DEFENCE, 20);

			else if (getId() == 1085)
				itemRequiriments.put(Skills.DEFENCE, 20);

			else if (getId() == 1109)
				itemRequiriments.put(Skills.DEFENCE, 20);

			else if (getId() == 1121)
				itemRequiriments.put(Skills.DEFENCE, 20);

			else if (getId() == 1143)
				itemRequiriments.put(Skills.DEFENCE, 20);

			else if (getId() == 1159)
				itemRequiriments.put(Skills.DEFENCE, 20);

			else if (getId() == 1181)
				itemRequiriments.put(Skills.DEFENCE, 20);

			else if (getId() == 1197)
				itemRequiriments.put(Skills.DEFENCE, 20);

			else if (getId() == 1209)
				itemRequiriments.put(Skills.ATTACK, 20);

			else if (getId() == 1243)
				itemRequiriments.put(Skills.ATTACK, 20);

			else if (getId() == 1273)
				itemRequiriments.put(Skills.ATTACK, 20);

			else if (getId() == 1285)
				itemRequiriments.put(Skills.ATTACK, 20);

			else if (getId() == 1299)
				itemRequiriments.put(Skills.ATTACK, 20);

			else if (getId() == 1315)
				itemRequiriments.put(Skills.ATTACK, 20);

			else if (getId() == 1329)
				itemRequiriments.put(Skills.ATTACK, 20);

			else if (getId() == 1343)
				itemRequiriments.put(Skills.ATTACK, 20);

			else if (getId() == 1355)
				itemRequiriments.put(Skills.ATTACK, 20);

			else if (getId() == 1369)
				itemRequiriments.put(Skills.ATTACK, 20);

			else if (getId() == 1428)
				itemRequiriments.put(Skills.ATTACK, 20);

			else if (getId() == 3099)
				itemRequiriments.put(Skills.ATTACK, 20);

			else if (getId() == 3198)
				itemRequiriments.put(Skills.ATTACK, 20);

			else if (getId() == 4127)
				itemRequiriments.put(Skills.DEFENCE, 20);

			else if (getId() == 1211)
				itemRequiriments.put(Skills.ATTACK, 30);

			else if (getId() == 1073)
				itemRequiriments.put(Skills.DEFENCE, 30);

			else if (getId() == 1091)
				itemRequiriments.put(Skills.DEFENCE, 30);

			else if (getId() == 1111)
				itemRequiriments.put(Skills.DEFENCE, 30);

			else if (getId() == 1223)
				itemRequiriments.put(Skills.DEFENCE, 30);

			else if (getId() == 1145)
				itemRequiriments.put(Skills.DEFENCE, 30);

			else if (getId() == 1161)
				itemRequiriments.put(Skills.DEFENCE, 30);

			else if (getId() == 1183)
				itemRequiriments.put(Skills.DEFENCE, 30);

			else if (getId() == 1199)
				itemRequiriments.put(Skills.DEFENCE, 30);

			else if (getId() == 1245)
				itemRequiriments.put(Skills.ATTACK, 30);

			else if (getId() == 1271)
				itemRequiriments.put(Skills.ATTACK, 30);

			else if (getId() == 1287)
				itemRequiriments.put(Skills.ATTACK, 30);

			else if (getId() == 1301)
				itemRequiriments.put(Skills.ATTACK, 30);

			else if (getId() == 1317)
				itemRequiriments.put(Skills.ATTACK, 30);

			else if (getId() == 1331)
				itemRequiriments.put(Skills.ATTACK, 30);

			else if (getId() == 1357)
				itemRequiriments.put(Skills.ATTACK, 30);

			else if (getId() == 1371)
				itemRequiriments.put(Skills.ATTACK, 30);

			else if (getId() == 1430)
				itemRequiriments.put(Skills.ATTACK, 30);

			else if (getId() == 3100)
				itemRequiriments.put(Skills.ATTACK, 30);

			else if (getId() == 3200)
				itemRequiriments.put(Skills.ATTACK, 30);

			else if (getId() == 4129)
				itemRequiriments.put(Skills.DEFENCE, 30);

			else if (getId() == 1079)
				itemRequiriments.put(Skills.DEFENCE, 40);

			else if (getId() == 1093)
				itemRequiriments.put(Skills.DEFENCE, 40);

			else if (getId() == 1113)
				itemRequiriments.put(Skills.DEFENCE, 40);

			else if (getId() == 1127)
				itemRequiriments.put(Skills.DEFENCE, 40);

			else if (getId() == 1147)
				itemRequiriments.put(Skills.DEFENCE, 40);

			else if (getId() == 1163)
				itemRequiriments.put(Skills.DEFENCE, 40);

			else if (getId() == 1185)
				itemRequiriments.put(Skills.DEFENCE, 40);

			else if (getId() == 1201)
				itemRequiriments.put(Skills.DEFENCE, 40);

			else if (getId() == 1213)
				itemRequiriments.put(Skills.ATTACK, 40);

			else if (getId() == 1247)
				itemRequiriments.put(Skills.ATTACK, 40);

			else if (getId() == 1275)
				itemRequiriments.put(Skills.ATTACK, 40);

			else if (getId() == 1289)
				itemRequiriments.put(Skills.ATTACK, 40);

			else if (getId() == 1303)
				itemRequiriments.put(Skills.ATTACK, 40);

			else if (getId() == 1319)
				itemRequiriments.put(Skills.ATTACK, 40);

			else if (getId() == 1333)
				itemRequiriments.put(Skills.ATTACK, 40);

			else if (getId() == 1347)
				itemRequiriments.put(Skills.ATTACK, 40);

			else if (getId() == 1359)
				itemRequiriments.put(Skills.ATTACK, 40);

			else if (getId() == 1373)
				itemRequiriments.put(Skills.ATTACK, 40);

			else if (getId() == 1432)
				itemRequiriments.put(Skills.ATTACK, 40);

			else if (getId() == 3101)
				itemRequiriments.put(Skills.ATTACK, 40);

			else if (getId() == 3202)
				itemRequiriments.put(Skills.ATTACK, 40);

			else if (getId() == 4131)
				itemRequiriments.put(Skills.DEFENCE, 40);

			else if (getId() == 11724)
				itemRequiriments.put(Skills.DEFENCE, 65);

			else if (getId() == 11726)
				itemRequiriments.put(Skills.DEFENCE, 65);

			else if (getId() == 11728)
				itemRequiriments.put(Skills.DEFENCE, 65);

			else if (getId() == 25022)
				itemRequiriments.put(Skills.DEFENCE, 65);

			else if (getId() == 25019)
				itemRequiriments.put(Skills.DEFENCE, 65);

			else if (getId() == 25025)
				itemRequiriments.put(Skills.DEFENCE, 65);

			else if (getId() == 11730)
				itemRequiriments.put(Skills.ATTACK, 70);

			// scims
			else if (getId() == 1333)
				itemRequiriments.put(Skills.ATTACK, 50);
			else if (getId() == 4587)
				itemRequiriments.put(Skills.ATTACK, 60);
			// Sara amulets
			// broken capes
			else if (getId() == 22953)
				itemRequiriments.put(Skills.ATTACK, 120);
			else if (getId() == 22952)
				itemRequiriments.put(Skills.AGILITY, 120);
			else if (getId() == 22951)
				itemRequiriments.put(Skills.COOKING, 120);
			else if (getId() == 22950)
				itemRequiriments.put(Skills.CRAFTING, 120);
			else if (getId() == 22949)
				itemRequiriments.put(Skills.DEFENCE, 120);
			else if (getId() == 22948)
				itemRequiriments.put(Skills.FARMING, 120);
			else if (getId() == 22947)
				itemRequiriments.put(Skills.FIREMAKING, 120);
			else if (getId() == 22946)
				itemRequiriments.put(Skills.FISHING, 120);
			else if (getId() == 22945)
				itemRequiriments.put(Skills.FLETCHING, 120);
			else if (getId() == 22944)
				itemRequiriments.put(Skills.HERBLORE, 120);
			else if (getId() == 22943)
				itemRequiriments.put(Skills.HITPOINTS, 120);
			else if (getId() == 22942)
				itemRequiriments.put(Skills.MAGIC, 120);
			else if (getId() == 22941)
				itemRequiriments.put(Skills.MINING, 120);
			else if (getId() == 22940)
				itemRequiriments.put(Skills.PRAYER, 120);
			else if (getId() == 22939)
				itemRequiriments.put(Skills.RANGE, 120);
			else if (getId() == 22938)
				itemRequiriments.put(Skills.RUNECRAFTING, 120);
			else if (getId() == 22937)
				itemRequiriments.put(Skills.SLAYER, 120);
			else if (getId() == 22936)
				itemRequiriments.put(Skills.SMITHING, 120);
			else if (getId() == 22935)
				itemRequiriments.put(Skills.STRENGTH, 120);
			else if (getId() == 22934)
				itemRequiriments.put(Skills.THIEVING, 120);
			else if (getId() == 22933)
				itemRequiriments.put(Skills.WOODCUTTING, 120);
			else if (getId() == 28001)
				itemRequiriments.put(Skills.DUNGEONEERING, 120);
			else if (getId() == 28002)
				itemRequiriments.put(Skills.ATTACK, 70);
			else if (getId() == 28003)
				itemRequiriments.put(Skills.ATTACK, 70);
			else if (getId() == 27198 || getId() == 27199 || getId() == 27200)
				itemRequiriments.put(Skills.DEFENCE, 100);
			else if (getId() == 27069 || getId() == 27071)
				itemRequiriments.put(Skills.ATTACK, 80);
			else if (getId() == 28000)
				itemRequiriments.put(Skills.ATTACK, 85);
			else if (getId() == 29955)
				itemRequiriments.put(Skills.RANGE, 90);
			else if (getId() == 26579 || getId() == 26584 || getId() == 26587 || getId() == 26591 || getId() == 26595
					|| getId() == 26599)
				itemRequiriments.put(Skills.ATTACK, 90);
			else if (getId() == 20822 || getId() == 20823 || getId() == 20824 || getId() == 20825 || getId() == 20826)
				itemRequiriments.put(Skills.DEFENCE, 99);
		} else if (name.equals("Dragon defender")) {
			itemRequiriments.put(Skills.ATTACK, 60);
			itemRequiriments.put(Skills.DEFENCE, 60);
		} else if (getId() == 20767) {
			itemRequiriments.put(Skills.ATTACK, 99);
			itemRequiriments.put(Skills.DEFENCE, 99);
			itemRequiriments.put(Skills.STRENGTH, 99);
			itemRequiriments.put(Skills.HITPOINTS, 99);
			itemRequiriments.put(Skills.RANGE, 99);
			itemRequiriments.put(Skills.PRAYER, 99);
			itemRequiriments.put(Skills.MAGIC, 99);
			itemRequiriments.put(Skills.RUNECRAFTING, 99);
			itemRequiriments.put(Skills.AGILITY, 99);
			itemRequiriments.put(Skills.HERBLORE, 99);
			itemRequiriments.put(Skills.THIEVING, 99);
			itemRequiriments.put(Skills.CRAFTING, 99);
			itemRequiriments.put(Skills.FLETCHING, 99);
			itemRequiriments.put(Skills.SLAYER, 99);
			itemRequiriments.put(Skills.HUNTER, 99);
			itemRequiriments.put(Skills.MINING, 99);
			itemRequiriments.put(Skills.SMITHING, 99);
			itemRequiriments.put(Skills.FISHING, 99);
			itemRequiriments.put(Skills.COOKING, 99);
			itemRequiriments.put(Skills.FIREMAKING, 99);
			itemRequiriments.put(Skills.WOODCUTTING, 99);
			itemRequiriments.put(Skills.SUMMONING, 99);
		}
		return itemRequiriments;
	}

	public void setDefaultOptions() {
		groundOptions = new String[] { null, null, "take", null, null };
		inventoryOptions = new String[] { null, null, null, null, "drop" };
	}

	public void setDefaultsVariableValues() {
		name = "null";
		maleEquip1 = -1;
		maleEquip2 = -1;
		femaleEquip1 = -1;
		femaleEquip2 = -1;
		modelZoom = 2000;
		lendId = -1;
		lendTemplateId = -1;
		certId = -1;
		certTemplateId = -1;
		unknownInt9 = 128;
		value = 1;
		maleEquipModelId3 = -1;
		femaleEquipModelId3 = -1;
		unknownValue1 = -1;
		unknownValue2 = -1;
		teamId = -1;
		equipSlot = -1;
		equipType = -1;
		equipLookHideSlot2 = -1;
	}

	public final void readValues(InputStream stream, int opcode) {
		if (opcode == 1)
			modelId = stream.readBigSmart();
		else if (opcode == 2)
			name = stream.readString();
		else if (opcode == 4)
			modelZoom = stream.readUnsignedShort();
		else if (opcode == 5)
			modelRotation1 = stream.readUnsignedShort();
		else if (opcode == 6)
			modelRotation2 = stream.readUnsignedShort();
		else if (opcode == 7) {
			modelOffset1 = stream.readUnsignedShort();
			if (modelOffset1 > 32767)
				modelOffset1 -= 65536;
			modelOffset1 <<= 0;
		} else if (opcode == 8) {
			modelOffset2 = stream.readUnsignedShort();
			if (modelOffset2 > 32767)
				modelOffset2 -= 65536;
			modelOffset2 <<= 0;
		} else if (opcode == 11)
			stackable = 1;
		else if (opcode == 12)
			value = stream.readInt();
		else if (opcode == 13) {
			equipSlot = stream.readUnsignedByte();
		} else if (opcode == 14) {
			equipType = stream.readUnsignedByte();
		} else if (opcode == 16)
			membersOnly = true;
		else if (opcode == 18) { // added
			stream.readUnsignedShort();
		} else if (opcode == 23)
			maleEquip1 = stream.readBigSmart();
		else if (opcode == 24)
			maleEquip2 = stream.readBigSmart();
		else if (opcode == 25)
			femaleEquip1 = stream.readBigSmart();
		else if (opcode == 26)
			femaleEquip2 = stream.readBigSmart();
		else if (opcode == 27)
			equipLookHideSlot2 = stream.readUnsignedByte();
		else if (opcode >= 30 && opcode < 35)
			groundOptions[opcode - 30] = stream.readString();
		else if (opcode >= 35 && opcode < 40)
			inventoryOptions[opcode - 35] = stream.readString();
		else if (opcode == 40) {
			int length = stream.readUnsignedByte();
			originalModelColors = new int[length];
			modifiedModelColors = new int[length];
			for (int index = 0; index < length; index++) {
				originalModelColors[index] = stream.readUnsignedShort();
				modifiedModelColors[index] = stream.readUnsignedShort();
			}
		} else if (opcode == 41) {
			int length = stream.readUnsignedByte();
			originalTextureColors = new short[length];
			modifiedTextureColors = new short[length];
			for (int index = 0; index < length; index++) {
				originalTextureColors[index] = (short) stream.readUnsignedShort();
				modifiedTextureColors[index] = (short) stream.readUnsignedShort();
			}
		} else if (opcode == 42) {
			int length = stream.readUnsignedByte();
			unknownArray1 = new byte[length];
			for (int index = 0; index < length; index++)
				unknownArray1[index] = (byte) stream.readByte();
		} else if (opcode == 44) {
			int length = stream.readUnsignedShort();
			int arraySize = 0;
			for (int modifier = 0; modifier > 0; modifier++) {
				arraySize++;
				unknownArray3 = new byte[arraySize];
				byte offset = 0;
				for (int index = 0; index < arraySize; index++) {
					if ((length & 1 << index) > 0) {
						unknownArray3[index] = offset;
					} else {
						unknownArray3[index] = -1;
					}
				}
			}
		} else if (45 == opcode) {
			int i_97_ = (short) stream.readUnsignedShort();
			int i_98_ = 0;
			for (int i_99_ = i_97_; i_99_ > 0; i_99_ >>= 1)
				i_98_++;
			unknownArray6 = new byte[i_98_];
			byte i_100_ = 0;
			for (int i_101_ = 0; i_101_ < i_98_; i_101_++) {
				if ((i_97_ & 1 << i_101_) > 0) {
					unknownArray6[i_101_] = i_100_;
					i_100_++;
				} else
					unknownArray6[i_101_] = (byte) -1;
			}
		} else if (opcode == 65)
			unnoted = true;
		else if (opcode == 78)
			maleEquipModelId3 = stream.readBigSmart();
		else if (opcode == 79)
			femaleEquipModelId3 = stream.readBigSmart();
		else if (opcode == 90)
			unknownInt1 = stream.readBigSmart();
		else if (opcode == 91)
			unknownInt2 = stream.readBigSmart();
		else if (opcode == 92)
			unknownInt3 = stream.readBigSmart();
		else if (opcode == 93)
			unknownInt4 = stream.readBigSmart();
		else if (opcode == 94) {// new
			int anInt7887 = stream.readUnsignedShort();
		} else if (opcode == 95)
			unknownInt5 = stream.readUnsignedShort();
		else if (opcode == 96)
			unknownInt6 = stream.readUnsignedByte();
		else if (opcode == 97)
			certId = stream.readUnsignedShort();
		else if (opcode == 98)
			certTemplateId = stream.readUnsignedShort();
		else if (opcode >= 100 && opcode < 110) {
			if (stackIds == null) {
				stackIds = new int[10];
				stackAmounts = new int[10];
			}
			stackIds[opcode - 100] = stream.readUnsignedShort();
			stackAmounts[opcode - 100] = stream.readUnsignedShort();
		} else if (opcode == 110)
			unknownInt7 = stream.readUnsignedShort();
		else if (opcode == 111)
			unknownInt8 = stream.readUnsignedShort();
		else if (opcode == 112)
			unknownInt9 = stream.readUnsignedShort();
		else if (opcode == 113)
			unknownInt10 = stream.readByte();
		else if (opcode == 114)
			unknownInt11 = stream.readByte() * 5;
		else if (opcode == 115)
			teamId = stream.readUnsignedByte();
		else if (opcode == 121)
			lendId = stream.readUnsignedShort();
		else if (opcode == 122)
			lendTemplateId = stream.readUnsignedShort();
		else if (opcode == 125) {
			unknownInt12 = stream.readByte() << 2;
			unknownInt13 = stream.readByte() << 2;
			unknownInt14 = stream.readByte() << 2;
		} else if (opcode == 126) {
			unknownInt15 = stream.readByte() << 2;
			unknownInt16 = stream.readByte() << 2;
			unknownInt17 = stream.readByte() << 2;
		} else if (opcode == 127) {
			unknownInt18 = stream.readUnsignedByte();
			unknownInt19 = stream.readUnsignedShort();
		} else if (opcode == 128) {
			unknownInt20 = stream.readUnsignedByte();
			unknownInt21 = stream.readUnsignedShort();
		} else if (opcode == 129) {
			unknownInt20 = stream.readUnsignedByte();
			unknownInt21 = stream.readUnsignedShort();
		} else if (opcode == 130) {
			unknownInt22 = stream.readUnsignedByte();
			unknownInt23 = stream.readUnsignedShort();
		} else if (opcode == 132) {
			int length = stream.readUnsignedByte();
			unknownArray2 = new int[length];
			for (int index = 0; index < length; index++)
				unknownArray2[index] = stream.readUnsignedShort();
		} else if (opcode == 134) {
			int unknownValue = stream.readUnsignedByte();
		} else if (opcode == 139) {
			stream.readUnsignedShort();// Dung stuff
		} else if (opcode == 140) {
			stream.readUnsignedShort();
		} else if (opcode >= 142 && opcode < 147) {
			if (unknownArray4 == null) {
				unknownArray4 = new int[6];
				Arrays.fill(unknownArray4, -1);
			}
			unknownArray4[opcode - 142] = stream.readUnsignedShort();
		} else if (opcode >= 150 && opcode < 155) {
			if (null == unknownArray5) {
				unknownArray5 = new int[5];
				Arrays.fill(unknownArray5, -1);
			}
			unknownArray5[opcode - 150] = stream.readUnsignedShort();
		} else if (opcode == 156) { // new

		} else if (157 == opcode) {// new
			boolean aBool7955 = true;
		} else if (161 == opcode) {// new
			int anInt7904 = stream.readUnsignedShort();
		} else if (162 == opcode) {// new
			int anInt7923 = stream.readUnsignedShort();
		} else if (163 == opcode) {// new
			int anInt7939 = stream.readUnsignedShort();
		} else if (164 == opcode) {// new coinshare shard
			String aString7902 = stream.readString();
		} else if (opcode == 165) {// new
			stackable = 2;
		} else if (opcode == 242) {
			int oldInvModel = stream.readBigSmart();
		} else if (opcode == 243) {
			int oldMaleEquipModelId3 = stream.readBigSmart();
		} else if (opcode == 244) {
			int oldFemaleEquipModelId3 = stream.readBigSmart();
		} else if (opcode == 245) {
			int oldMaleEquipModelId2 = stream.readBigSmart();
		} else if (opcode == 246) {
			int oldFemaleEquipModelId2 = stream.readBigSmart();
		} else if (opcode == 247) {
			int oldMaleEquipModelId1 = stream.readBigSmart();
		} else if (opcode == 248) {
			int oldFemaleEquipModelId1 = stream.readBigSmart();
		} else if (opcode == 251) {
			int length = stream.readUnsignedByte();
			int[] oldoriginalModelColors = new int[length];
			int[] oldmodifiedModelColors = new int[length];
			for (int index = 0; index < length; index++) {
				oldoriginalModelColors[index] = stream.readUnsignedShort();
				oldmodifiedModelColors[index] = stream.readUnsignedShort();
			}
		} else if (opcode == 252) {
			int length = stream.readUnsignedByte();
			short[] oldoriginalTextureColors = new short[length];
			short[] oldmodifiedTextureColors = new short[length];
			for (int index = 0; index < length; index++) {
				oldoriginalTextureColors[index] = (short) stream.readUnsignedShort();
				oldmodifiedTextureColors[index] = (short) stream.readUnsignedShort();
			}
		} else if (opcode == 249) {
			int length = stream.readUnsignedByte();
			if (clientScriptData == null)
				clientScriptData = new HashMap<Integer, Object>(length);
			for (int index = 0; index < length; index++) {
				boolean stringInstance = stream.readUnsignedByte() == 1;
				int key = stream.read24BitInt();
				Object value = stringInstance ? stream.readString() : stream.readInt();
				clientScriptData.put(key, value);
			}
		}
	}

	public int unknownValue1;
	public int unknownValue2;

	public final void readOpcodeValues(InputStream stream) {
		while (true) {
			int opcode = stream.readUnsignedByte();
			if (opcode == 0)
				break;
			readValues(stream, opcode);
		}
	}

	public String getName() {
		return name;
	}

	public int getFemaleWornModelId1() {
		return femaleEquip1;
	}

	public int getFemaleWornModelId2() {
		return femaleEquip2;
	}

	public int getMaleWornModelId1() {
		return maleEquip1;
	}

	public int getMaleWornModelId2() {
		return maleEquip2;
	}

	public boolean isOverSized() {
		return modelZoom > 5000;
	}

	public boolean isLended() {
		return lended;
	}

	public boolean isMembersOnly() {
		return membersOnly;
	}

	public boolean isStackable() {
		switch (getId()) {
		case 7478:
		case 4621:
			return true;
		}
		return stackable == 1;
	}

	public boolean isNoted() {
		return noted;
	}

	public int getLendId() {
		return lendId;
	}

	public int getCertId() {
		return certId;
	}

	public int getId() {
		return id;
	}

	public int getEquipSlot() {
		return equipSlot;
	}

	public int getEquipType() {
		return equipType;
	}

	/**
	 * public int getValue() { return value; }
	 **/
	public int equipLookHideSlot2;

	public int getEquipType2() {
		return equipLookHideSlot2;
	}

	public int getValue() {
		return value;
	}

	public static ItemDefinitions forName(String name) {
		for (ItemDefinitions definition : itemsDefinitions.values()) {
			if (definition.name.equalsIgnoreCase(name)) {
				return definition;
			}
		}
		return null;
	}

	public static String getEquipType(Item item) {
		if (item.getDefinitions().name.contains("sword") || item.getDefinitions().name.contains("dagger")
				|| item.getDefinitions().name.contains("scimitar") || item.getDefinitions().name.contains("whip")
				|| item.getDefinitions().name.contains("spear") || item.getDefinitions().name.contains("mace")
				|| item.getDefinitions().name.contains("battleaxe") || item.getDefinitions().name.contains("staff")
				|| item.getDefinitions().name.contains("Staff") || item.getDefinitions().name.contains("battleaxe")
				|| item.getDefinitions().name.contains("hatchet") || item.getDefinitions().name.contains("pickaxe")) {
			return "wielded in the right hand";
		}
		if (item.getDefinitions().name.contains("plate") || item.getDefinitions().name.contains("body")
				|| item.getDefinitions().name.contains("apron") || item.getDefinitions().name.contains("chest")) {
			return "worn on the torso";
		}
		if (item.getDefinitions().name.contains("gloves")) {
			return "worn on the hands";
		}
		if (item.getDefinitions().name.contains("boots")) {
			return "worn on the feet";
		}
		if (item.getDefinitions().name.contains("skirt") || item.getDefinitions().name.contains("legs")) {
			return "worn on the legs";
		}
		if (item.getDefinitions().name.contains("helm") || item.getDefinitions().name.contains("hat")) {
			return "worn on the head";
		}
		if (item.getDefinitions().name.contains("shield")) {
			return "held in the left hand";
		}
		if (item.getDefinitions().name.contains("shield") || item.getDefinitions().name.contains("2h")
				|| item.getDefinitions().name.contains("maul") || item.getDefinitions().name.contains("claws")) {
			return "wielded in both hands";
		}
		if (item.getDefinitions().name.contains("cape") || item.getDefinitions().name.contains("Cape")) {
			return "worn on the back";
		}
		return "an item";
	}

	public String getItemType(Item item) {
		if (item.getDefinitions().name.contains("sword") || item.getDefinitions().name.contains("dagger")
				|| item.getDefinitions().name.contains("scimitar") || item.getDefinitions().name.contains("maul")
				|| item.getDefinitions().name.contains("whip") || item.getDefinitions().name.contains("claws")
				|| item.getDefinitions().name.contains("spear") || item.getDefinitions().name.contains("mace")
				|| item.getDefinitions().name.contains("battleaxe")) {
			return "a melee weapon";
		}
		if (item.getDefinitions().name.contains("Staff") || item.getDefinitions().name.contains("staff")) {
			return "a weapon for mages";
		}
		if (item.getDefinitions().name.contains("body") || item.getDefinitions().name.contains("legs")
				|| item.getDefinitions().name.contains("robe") || item.getDefinitions().name.contains("priest")
				|| item.getDefinitions().name.contains("helm")) {
			return "a piece of apparel";
		}
		if (item.getDefinitions().name.contains("shield")) {
			return "a shield";
		}
		if (item.getDefinitions().name.contains("arrow") || item.getDefinitions().name.contains("bolt")
				|| item.getDefinitions().name.contains("ball")) {
			return "ammunition for a ranged weapon";
		}
		if (item.getDefinitions().name.contains("bow")) {
			return "a ranged weapon";
		}
		return "an item";
	}

	public List<Item> getCreateItemRequirements(boolean infusingScroll) {
		if (clientScriptData == null)
			return null;
		List<Item> items = new ArrayList<Item>();
		int requiredId = -1;
		int requiredAmount = -1;
		for (int key : clientScriptData.keySet()) {
			Object value = clientScriptData.get(key);
			if (value instanceof String)
				continue;

			if (key >= 2656 && key <= 2660) {
				requiredId = (Integer) value;
				requiredAmount = (clientScriptData.containsKey(key + 10) && ((Integer) clientScriptData.get(key) != 0))
						? (Integer) clientScriptData.get(key + 10) : -1;
				if (requiredId != -1 && requiredAmount > 0) {
					if (infusingScroll) {
						requiredId = getId();
						requiredAmount = 1;
					}
					items.add(new Item(requiredId, requiredAmount));
					requiredId = -1;
					requiredAmount = -1;
					if (infusingScroll) {
						break;
					}
				}
			}
		}
		return items;
	}

	public boolean isBindItem() {
		if (inventoryOptions == null)
			return false;
		for (String option : inventoryOptions) {
			if (option == null)
				continue;
			if (option.equalsIgnoreCase("bind"))
				return true;
		}
		return false;
	}

	public boolean isBinded() {
		return (id >= 15775 && id <= 16272) || (id >= 19865 && id <= 19866);
	}

	public double getDungShopValueMultiplier() {
		if (clientScriptData == null)
			return 1;
		Object value = clientScriptData.get(1046);
		if (value != null && value instanceof Integer)
			return ((Integer) value).doubleValue() / 100;
		return 1;
	}

	public int getCSOpcode(int opcode) {
		if (clientScriptData != null) {
			Object value = clientScriptData.get(opcode);
			if (value != null && value instanceof Integer)
				return (int) value;
		}
		return 0;
	}

	public GeneralRequirementMap getCombatMap() {
		int csMapOpcode = getCSOpcode(686);
		if (csMapOpcode != 0)
			return GeneralRequirementMap.getMap(csMapOpcode);
		return null;
	}

	public int getCombatOpcode(int opcode) {
		if (clientScriptData == null)
			return 0;
		Integer value = (Integer) clientScriptData.get(opcode);
		if (value != null)
			return value;
		GeneralRequirementMap map = getCombatMap();
		return map == null ? 0 : map.getIntValue(opcode);
	}

	public int getCombatStyle() {
		return getCombatOpcode(2853);
	}

	public int getAttackLevel() {
		if (getWearingSkillRequiriments() == null)
			return 1;
		Integer level = getWearingSkillRequiriments().get(Skills.ATTACK);
		return level == null ? 1 : level;
	}

	public int getRangedLevel() {
		if (getWearingSkillRequiriments() == null)
			return 1;
		Integer level = getWearingSkillRequiriments().get(Skills.RANGE);
		return level == null ? 1 : level;
	}

	public int getSheatheModelId() {
		if (clientScriptData == null)
			return -1;
		Object modelId = clientScriptData.get(2820);
		if (modelId != null && modelId instanceof Integer)
			return (Integer) modelId;
		return -1;
	}

	public static int getEquipType(String name) {
		if (name.contains("sword") || name.contains("dagger") || name.contains("scimitar") || name.contains("whip")
				|| name.contains("spear") || name.contains("mace") || name.contains("battleaxe")
				|| name.contains("staff") || name.contains("Staff") || name.contains("battleaxe")
				|| name.contains("hatchet") || name.contains("pickaxe") || name.contains("axe") || name.contains("wand")
				|| name.contains("katana") || name.contains("Katana") || name.contains("scythe")
				|| name.contains("maul")) {
			return 11;
		}
		if (name.contains("plate") || name.contains("body") || name.contains("apron") || name.contains("chest")
				|| name.contains("top")) {
			return 3;
		}
		if (name.contains("gloves")) {
			return 5;
		}
		if (name.contains("boots")) {
			return 6;
		}
		if (name.contains("skirt") || name.contains("legs") || name.contains("bottom")) {
			return 4;
		}
		if (name.contains("helm") || name.contains("hat") || name.contains("hood")) {
			return 0;
		}
		if (name.contains("shield") || name.contains("ket-xil") || name.equalsIgnoreCase("book")
				|| name.contains("defender") || name.contains("teddy") || name.contains("vyre'lector")) {
			return 9;
		}
		if (name.contains("cape") || name.contains("Cape")) {
			return 1;
		}
		if (name.contains("neck")) {
			return 2;
		}
		if (name.contains("arrow") || name.contains("bolt")) {
			return 7;
		}
		if (name.contains("bow")) {
			return 12;
		}
		return -1;
	}

	public int getHealth() {
		return getCSOpcode(1326);
	}

	public int getRangeDamage() {
		return getCSOpcode(643);
	}

	public int getMageDamage() {
		return getCSOpcode(965);
	}

	public int getArmor() {
		return getCSOpcode(2870);
	}

	public int getRangeAccuracy() {
		return getCSOpcode(4);
	}

	public int getMageAccuracy() {
		return getCSOpcode(3);
	}

	public boolean isMeleeTypeGear() {
		return getCSOpcode(2821) == 1;
	}

	public boolean isRangeTypeGear() {
		return getCSOpcode(2822) == 1;
	}

	public boolean isMagicTypeGear() {
		return getCSOpcode(2823) == 1;
	}

	public boolean isAllTypeGear() {
		return getCSOpcode(2824) == 1;
	}

	public boolean isMeleeTypeWeapon() {
		return getCSOpcode(2825) == 1;
	}

	public boolean isRangeTypeWeapon() {
		return getCSOpcode(2826) == 1;
	}

	public boolean isMagicTypeWeapon() {
		return getCSOpcode(2827) == 1;
	}

	public boolean isShield() {
		return getCSOpcode(2832) == 1;
	}

	public boolean isDungeoneeringItem() {
		return getCSOpcode(1047) == 1;
	}

	public int getSpecialAmmount() {
		int original = getOriginalItemSpec();
		return original == 0 ? getCSOpcode(4332) / 10 : getItemDefinitions(original).getSpecialAmmount();
	}

	public int getOriginalItemSpec() {
		return getCSOpcode(4338);
	}

	public int getMaleWornModelId3() {
		return this.maleEquipModelId3;
	}

	public int getFemaleWornModelId3() {
		return this.femaleEquipModelId3;
	}

	public boolean containsInventoryOption(int i, String option) {
		if (inventoryOptions == null || inventoryOptions[i] == null || inventoryOptions.length <= i)
			return false;
		return inventoryOptions[i].equals(option);
	}
}