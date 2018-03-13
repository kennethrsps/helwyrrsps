package com.rs.game.player;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Arrays;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.item.Item;
import com.rs.game.npc.others.randoms.CraftingRandom;
import com.rs.game.npc.others.randoms.FarmingRandom;
import com.rs.game.npc.others.randoms.FletchingRandom;
import com.rs.game.player.actions.Cooking;
import com.rs.game.player.actions.divination.DivineObject;
import com.rs.game.player.actions.summoning.Summoning;
import com.rs.game.player.actions.thieving.Thieving;
import com.rs.game.player.content.RuneCrafting;
import com.rs.game.player.content.TaskTab;
import com.rs.game.player.controllers.zombie.ZombieControler;
import com.rs.game.player.dialogue.impl.LevelUp;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/**
 * A class used to handle all players Skills & Experience.
 * 
 * @author Zeus
 */
public final class Skills implements Serializable {

	/**
	 * The generated serial UID.
	 */
	private static final long serialVersionUID = -7086829989489745985L;

	/**
	 * A Double Integer representing maximum experience per Skill.
	 */
	public static final double MAXIMUM_EXP = 1000000000;

	public short level[];
	private double xp[];
	private double[] xpTracks;
	private boolean[] trackSkills;
	private byte[] trackSkillsIds;

	private boolean xpDisplay, xpPopup;
	private transient int currentCounter;

	private boolean[] enabledSkillsTargets;
	private boolean[] skillsTargetsUsingLevelMode;
	private int[] skillsTargetsValues;

	/**
	 * Calls the Player file.
	 */
	private transient Player player;

	/**
	 * Initializes the Skills Serializable.
	 */
	public Skills() {
		level = new short[26];
		xp = new double[26];
		for (int i = 0; i < level.length; i++) {
			level[i] = 1;
			xp[i] = 0;
		}
		level[3] = 10;
		xp[3] = 1155;
		level[HERBLORE] = 3;
		xp[HERBLORE] = 175;
		xpPopup = true;
		xpTracks = new double[3];
		trackSkills = new boolean[3];
		trackSkillsIds = new byte[3];
		trackSkills[0] = true;
		for (int i = 0; i < trackSkillsIds.length; i++)
			trackSkillsIds[i] = 30;
	}

	public void addSkillXpRefresh(int skill, double xp) {
		this.xp[skill] += xp;
		level[skill] = (short) getLevelForXp(skill);
	}

	private double getXPRates(int skill, double exp) {
		if (player.isVeteran())
			return Settings.VET_XP;
		if (player.isIntermediate())
			return Settings.INTERM_XP;
		if (player.isEasy())
			return Settings.EASY_XP;
		if (player.isExpert())
			return Settings.EXPERT_XP;
		return Settings.IRONMAN_XP;
	}

	public double xpNoBonus(int skill, double exp) {
		return exp / (getXPRates(skill, exp) * ((World.isWellActive() || World.isWeekend()) ? 2 : 1));
	}

	public double artisansBonus() {
		double xpBoost = 1.0;
		if (player.getEquipment().getHatId() == 25185)
			xpBoost *= 1.01;
		if (player.getEquipment().getHatId() == 32281)
			xpBoost *= 1.03;
		if (player.getEquipment().getChestId() == 25186)
			xpBoost *= 1.01;
		if (player.getEquipment().getLegsId() == 25187)
			xpBoost *= 1.01;
		if (player.getEquipment().getBootsId() == 25188)
			xpBoost *= 1.01;
		if (player.getEquipment().getGlovesId() == 25189)
			xpBoost *= 1.01;
		if (player.getEquipment().getHatId() == 25185 && player.getEquipment().getChestId() == 25186
				&& player.getEquipment().getLegsId() == 25187 && player.getEquipment().getBootsId() == 25188
				&& player.getEquipment().getGlovesId() == 25189)
			xpBoost *= 1.01;
		if (player.getEquipment().getHatId() == 32281 && player.getEquipment().getChestId() == 25186
				&& player.getEquipment().getLegsId() == 25187 && player.getEquipment().getBootsId() == 25188
				&& player.getEquipment().getGlovesId() == 25189)
			xpBoost *= 1.03;
		return xpBoost;
	}

	public double fletchersBonus() {
		double xpBoost = 1.0;
		if (player.getEquipment().getHatId() == 36899)
			xpBoost *= 1.01;
		if (player.getEquipment().getChestId() == 36898)
			xpBoost *= 1.01;
		if (player.getEquipment().getLegsId() == 36897)
			xpBoost *= 1.01;
		if (player.getEquipment().getBootsId() == 36895)
			xpBoost *= 1.01;
		if (player.getEquipment().getGlovesId() == 36896)
			xpBoost *= 1.01;
		if (player.getEquipment().getHatId() == 36899 && player.getEquipment().getChestId() == 36898
				&& player.getEquipment().getLegsId() == 36897 && player.getEquipment().getBootsId() == 36895
				&& player.getEquipment().getGlovesId() == 36896)
			xpBoost *= 1.01;
		return xpBoost;
	}

	public double farmersBonus() {
		double xpBoost = 1.0;
		if (player.getEquipment().getHatId() == 31347)
			xpBoost *= 1.01;
		if (player.getEquipment().getHatId() == 34926)
			xpBoost *= 1.03;
		if (player.getEquipment().getChestId() == 31346)
			xpBoost *= 1.01;
		if (player.getEquipment().getLegsId() == 31345)
			xpBoost *= 1.01;
		if (player.getEquipment().getBootsId() == 31343)
			xpBoost *= 1.01;
		if (player.getEquipment().getGlovesId() == 31344)
			xpBoost *= 1.01;
		if (player.getEquipment().getHatId() == 31347 && player.getEquipment().getChestId() == 31346
				&& player.getEquipment().getLegsId() == 31345 && player.getEquipment().getBootsId() == 31343
				&& player.getEquipment().getGlovesId() == 31344)
			xpBoost *= 1.01;
		if (player.getEquipment().getHatId() == 34926 && player.getEquipment().getChestId() == 31346
				&& player.getEquipment().getLegsId() == 31345 && player.getEquipment().getBootsId() == 31343
				&& player.getEquipment().getGlovesId() == 31344)
			xpBoost *= 1.03;
		return xpBoost;
	}

	public boolean isCombatSkill(int skill) {
		return skill <= 6;
	}

	public void addXp(int skill, double exp, boolean forceRSXP) {
		if (player.isXpLocked())
			return;
		if (isCombatSkill(skill) && player.nearDummy())
			return;
		boolean insideDung = player.getDungManager().isInside();
		player.getControlerManager().trackXP(skill, (int) exp);
		exp *= getXPRates(skill, exp);
		if (insideDung)
			exp /= 2;

		/** Equipment exp increasers **/
		if (!insideDung) {
			if (skill == Skills.DIVINATION)
				exp *= DivineObject.divinationSuit(player);
			if (skill == Skills.THIEVING)
				exp *= Thieving.outfitBoost(player);
			if (skill == Skills.RUNECRAFTING)
				exp *= RuneCrafting.runecrafterSuit(player);
			if (skill == Skills.SUMMONING)
				exp *= Summoning.shamanSuit(player);
			if (skill == Skills.COOKING)
				exp *= Cooking.chefsSuit(player);
			if (skill == Skills.FLETCHING) {
				exp *= fletchersBonus();
				if (Utils.random(750) == 0)
					new FletchingRandom(player, player);
			}
			if (skill == Skills.CRAFTING) {
				exp *= artisansBonus();
				if (Utils.random(750) == 0)
					new CraftingRandom(player, player);
			}
			if (skill == Skills.FARMING) {
				exp *= farmersBonus();
				if (Utils.random(250) == 0)
					new FarmingRandom(player, player);
			}
		}
		if (player.getAuraManager().usingWisdom() || player.isDoubleXp())
			exp *= 1.25;

		if (skill == Skills.HERBLORE) {
			if (player.getPerkManager().herbivore)
				exp *= 1.25;
		}
		if (skill == Skills.PRAYER) {
			if (player.getPerkManager().prayerBetrayer)
				exp *= 1.25;
		}
		if (skill == Skills.DIVINATION) {
			if (player.getPerkManager().masterDiviner)
				exp *= 1.25;
		}
		if (skill == Skills.HUNTER) {
			if (player.getPerkManager().huntsman)
				exp *= 1.25;
		}

		if (World.isWellActive())
			exp *= 2;
		else if (World.isWeekend())
			exp *= 2;
		if (player.getDailyTaskManager().hasDoubleXpActivated())
			exp *= 1.25;
		if (player.getControlerManager().getControler() != null
				&& player.getControlerManager().getControler() instanceof ZombieControler)
			exp *= 0.5;

		if (player.customEXP != 0) {
			exp *= player.customEXP;
		}

		int oldLevel = getLevelForXp(skill);
		int oldXP = (int) xp[skill];
		xp[skill] += exp;
		for (int i = 0; i < trackSkills.length; i++) {
			if (trackSkills[i]) {
				if (trackSkillsIds[i] == 30
						|| (trackSkillsIds[i] == 29
								&& (skill == Skills.ATTACK || skill == Skills.DEFENCE || skill == Skills.STRENGTH
										|| skill == Skills.MAGIC || skill == Skills.RANGE || skill == Skills.HITPOINTS))
						|| trackSkillsIds[i] == getCounterSkill(skill)) {
					xpTracks[i] += exp;
					refreshCounterXp(i);
				}
			}
		}

		if (xp[skill] >= MAXIMUM_EXP)
			xp[skill] = MAXIMUM_EXP;

		if (oldXP < 104273167 && xp[skill] >= 104273167 && skill != DUNGEONEERING)
			LevelUp.send104m(player, skill);
		if (oldXP < 250000000 && xp[skill] >= 250000000)
			LevelUp.send250m(player, skill);
		if (oldXP < 500000000 && xp[skill] >= 500000000)
			LevelUp.send500m(player, skill);
		if (oldXP < 750000000 && xp[skill] >= 750000000)
			LevelUp.send750m(player, skill);
		if (oldXP < 1000000000 && xp[skill] == MAXIMUM_EXP)
			LevelUp.send1000m(player, skill);

		int newLevel = getLevelForXp(skill);
		int levelDiff = newLevel - oldLevel;
		if (newLevel > oldLevel) {
			level[skill] += levelDiff;
			player.getDialogueManager().startDialogue("LevelUp", skill);
			if (skill == SUMMONING || (skill >= ATTACK && skill <= MAGIC)) {
				player.getGlobalPlayerUpdater().generateAppearenceData();
				if (skill == HITPOINTS)
					player.heal(levelDiff * 10);
				else if (skill == PRAYER)
					player.getPrayer().restorePrayer(levelDiff * 10);
			}
			player.getQuestManager().checkCompleted();
		}
		refresh(skill);
		if (!insideDung)
			handleSkillShards(skill);
		if (skill != HITPOINTS) {
			if (player.isDoubleXp() || player.getAuraManager().usingWisdom())
				player.getPackets().sendConfig(2044, (int) (exp * 10) / 4);
			else if (World.isWeekend() || World.isWellActive())
				player.getPackets().sendConfig(2044, (int) (exp * 10) / 2);
		}
	}

	public void addXp(int skill, double exp) {
		addXp(skill, exp, false);
	}

	public int drainLevel(int skill, int drain) {
		int drainLeft = drain - level[skill];
		if (drainLeft < 0)
			drainLeft = 0;
		level[skill] -= drain;
		if (level[skill] < 0)
			level[skill] = 0;
		refresh(skill);
		return drainLeft;
	}

	public void drainSummoning(int amt) {
		int level = getLevel(Skills.SUMMONING);
		if (level == 0)
			return;
		set(Skills.SUMMONING, amt > level ? 0 : level - amt);
	}

	public int getCombatLevel() {
		int attack = getLevelForXp(0);
		int defence = getLevelForXp(1);
		int strength = getLevelForXp(2);
		int hp = getLevelForXp(3);
		int prayer = getLevelForXp(5);
		int ranged = getLevelForXp(4);
		int magic = getLevelForXp(6);
		int combatLevel = 3;
		combatLevel = (int) ((defence + hp + Math.floor(prayer / 2)) * 0.25) + 1;
		double melee = (attack + strength) * 0.325;
		double ranger = Math.floor(ranged * 1.5) * 0.325;
		double mage = Math.floor(magic * 1.5) * 0.325;
		if (melee >= ranger && melee >= mage)
			combatLevel += melee;
		else if (ranger >= melee && ranger >= mage)
			combatLevel += ranger;
		else if (mage >= melee && mage >= ranger)
			combatLevel += mage;
		return combatLevel;
	}

	public int getCombatLevelWithSummoning() {
		return getCombatLevel() + getSummoningCombatLevel();
	}

	public int getCounterSkill(int skill) {
		switch (skill) {
		case ATTACK:
			return 0;
		case STRENGTH:
			return 1;
		case DEFENCE:
			return 4;
		case RANGE:
			return 2;
		case HITPOINTS:
			return 5;
		case PRAYER:
			return 6;
		case AGILITY:
			return 7;
		case HERBLORE:
			return 8;
		case THIEVING:
			return 9;
		case CRAFTING:
			return 10;
		case MINING:
			return 12;
		case SMITHING:
			return 13;
		case FISHING:
			return 14;
		case COOKING:
			return 15;
		case FIREMAKING:
			return 16;
		case WOODCUTTING:
			return 17;
		case SLAYER:
			return 19;
		case FARMING:
			return 20;
		case CONSTRUCTION:
			return 21;
		case HUNTER:
			return 22;
		case SUMMONING:
			return 23;
		case DUNGEONEERING:
			return 24;
		case MAGIC:
			return 3;
		case FLETCHING:
			return 18;
		case RUNECRAFTING:
			return 11;
		default:
			return -1;
		}
	}

	public int getLevel(int skill) {
		return level[skill];
	}

	public int getLevelForXp(int skill) {
		double exp = xp[skill];
		int points = 0;
		int output = 0;
		for (int lvl = 1; lvl <= (skill == DUNGEONEERING ? 120 : 99); lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			output = (int) Math.floor(points / 4);
			if ((output - 1) >= exp)
				return lvl;
		}
		return skill == DUNGEONEERING ? 120 : 99;
	}

	public short[] getLevels() {
		return level;
	}

	public String getSkillName(int skill) {
		switch (skill) {
		case ATTACK:
			return "Attack";
		case STRENGTH:
			return "Strength";
		case DEFENCE:
			return "Defence";
		case RANGE:
			return "Ranged";
		case HITPOINTS:
			return "Hitpoints";
		case PRAYER:
			return "Prayer";
		case AGILITY:
			return "Agility";
		case HERBLORE:
			return "Herblore";
		case THIEVING:
			return "Thieving";
		case CRAFTING:
			return "Crafting";
		case MINING:
			return "Mining";
		case SMITHING:
			return "Smithing";
		case FISHING:
			return "Fishing";
		case COOKING:
			return "Cooking";
		case FIREMAKING:
			return "Firemaking";
		case WOODCUTTING:
			return "Woodcutting";
		case SLAYER:
			return "Slayer";
		case FARMING:
			return "Farming";
		case CONSTRUCTION:
			return "Construction";
		case HUNTER:
			return "Hunter";
		case SUMMONING:
			return "Summoning";
		case DUNGEONEERING:
			return "Dungeoneering";
		case MAGIC:
			return "Magic";
		case FLETCHING:
			return "Fletching";
		case RUNECRAFTING:
			return "Runecrafting";
		case DIVINATION:
			return "Divination";
		case INVENTION:
			return "Invention";
		default:
			return "Null";
		}
	}

	public int getSummoningCombatLevel() {
		return getLevelForXp(Skills.SUMMONING) / 8;
	}

	public int getTotalLevel(Player player) {
		int totallevel = 0;
		for (int i = 0; i <= 26; i++)
			totallevel += player.getSkills().getLevelForXp(i);
		return totallevel;
	}

	public double[] getXp() {
		return xp;
	}

	public double getXp(int skill) {
		return xp[skill];
	}

	public void handleSetupXPCounter(int componentId) {
		if (componentId == 18)
			player.getInterfaceManager().closeXPDisplay();
		else if (componentId >= 22 && componentId <= 24)
			setCurrentCounter(componentId - 22);
		else if (componentId == 27)
			switchTrackCounter();
		else if (componentId == 61)
			resetCounterXP();
		else if (componentId >= 31 && componentId <= 57)
			if (componentId == 33)
				setCounterSkill(4);
			else if (componentId == 34)
				setCounterSkill(2);
			else if (componentId == 35)
				setCounterSkill(3);
			else if (componentId == 42)
				setCounterSkill(18);
			else if (componentId == 49)
				setCounterSkill(11);
			else
				setCounterSkill(componentId >= 56 ? componentId - 27 : componentId - 31);
	}

	public boolean hasRequiriments(int... skills) {
		for (int i = 0; i < skills.length; i += 2) {
			int skillId = skills[i];
			int skillLevel = skills[i + 1];
			if (getLevelForXp(skillId) < skillLevel)
				return false;
		}
		return true;
	}

	public void init() {
		for (int skill = 0; skill < level.length; skill++)
			refresh(skill);
		sendXPDisplay();
		refreshEnabledSkillsTargets();
		refreshUsingLevelTargets();
		refreshSkillsTargetsValues();
	}

	public void passLevels(Player p) {
		this.level = p.getSkills().level;
		this.xp = p.getSkills().xp;
	}

	/**
	 * Refreshes the skill by sending a packet to the client.
	 * 
	 * @param skill
	 *            the Skill ID to refresh.
	 */
	public void refresh(int skill) {
		if (skill != DIVINATION && skill != INVENTION) // TODO temporary fix
														// until chry finds a
														// solution for this.
			player.getPackets().sendSkillLevel(skill);
		else
			TaskTab.sendTab(player);
		player.getGlobalPlayerUpdater().generateAppearenceData();
	}

	public void refreshCounterXp(int counter) {
		player.getPackets().sendConfig(counter == 0 ? 1801 : 2474 + counter, (int) (xpTracks[counter] * 10));
	}

	public void refreshCurrentCounter() {
		player.getPackets().sendConfig(2478, currentCounter + 1);
	}

	public void resetCounterXP() {
		xpTracks[currentCounter] = 0;
		refreshCounterXp(currentCounter);
	}

	public void resetSkillNoRefresh(int skill) {
		xp[skill] = 0;
		level[skill] = 1;
	}

	public void resetAllSkills() {
		for (int skill = 0; skill < xp.length; skill++) {
			resetSkillNoRefresh(skill);
			refresh(skill);
		}
		level[3] = 10;
		xp[3] = 1155;
	}

	public void restoreSkills() {
		for (int skill = 0; skill < level.length; skill++) {
			level[skill] = (short) getLevelForXp(skill);
			refresh(skill);
		}
	}

	public void restoreNewSkills() {
		level[25] = (short) getLevelForXp(25);
		refresh(25);
		level[26] = (short) getLevelForXp(26);
		refresh(26);
	}

	public void restoreSummoning() {
		level[23] = (short) getLevelForXp(23);
		refresh(23);
	}

	public void sendInterfaces() {
		if (xpDisplay)
			player.getInterfaceManager().sendXPDisplay();
		if (xpPopup)
			player.getInterfaceManager().sendXPPopup();
	}

	public void sendXPDisplay() {
		for (int i = 0; i < trackSkills.length; i++) {
			player.getPackets().sendConfigByFile(10444 + i, trackSkills[i] ? 1 : 0);
			player.getPackets().sendConfigByFile(10440 + i, trackSkillsIds[i] + 1);
			refreshCounterXp(i);
		}
	}

	public void set(int skill, int newLevel) {
		level[skill] = (short) newLevel;
		refresh(skill);
	}

	/**
	 * Increases the level.
	 * 
	 * @param skill
	 *            The skill to increase.
	 * @param levels
	 *            The amount of levels to increase.
	 */
	public void updateLevel(int skill, int levels) {
		level[skill] += (short) levels;
		refresh(skill);
	}

	public void setCounterSkill(int skill) {
		xpTracks[currentCounter] = 0;
		trackSkillsIds[currentCounter] = (byte) skill;
		player.getPackets().sendConfigByFile(10440 + currentCounter, trackSkillsIds[currentCounter] + 1);
		refreshCounterXp(currentCounter);
	}

	public void setCurrentCounter(int counter) {
		if (counter != currentCounter) {
			currentCounter = counter;
			refreshCurrentCounter();
		}
	}

	public void setPlayer(Player player) {
		this.player = player;
		// temporary
		if (xpTracks == null) {
			xpPopup = true;
			xpTracks = new double[3];
			trackSkills = new boolean[3];
			trackSkillsIds = new byte[3];
			trackSkills[0] = true;
			for (int i = 0; i < trackSkillsIds.length; i++)
				trackSkillsIds[i] = 30;
		}
		enabledSkillsTargets = new boolean[25];
		skillsTargetsUsingLevelMode = new boolean[25];
		skillsTargetsValues = new int[25];

		if (xp.length != 26) {
			xp = Arrays.copyOf(xp, 26);
			level = Arrays.copyOf(level, 26);
			level[DIVINATION] = 1;
		}

		if (xp.length != 27) {
			xp = Arrays.copyOf(xp, 27);
			level = Arrays.copyOf(level, 27);
			level[INVENTION] = 1;
		}
	}

	public void setupXPCounter() {
		player.getInterfaceManager().sendXPDisplay(1214);
	}

	public void setXp(int skill, double exp) {
		xp[skill] = exp;
		refresh(skill);
	}

	public void switchTrackCounter() {
		trackSkills[currentCounter] = !trackSkills[currentCounter];
		player.getPackets().sendConfigByFile(10444 + currentCounter, trackSkills[currentCounter] ? 1 : 0);
	}

	public void switchXPDisplay() {
		xpDisplay = !xpDisplay;
		if (xpDisplay)
			player.getInterfaceManager().sendXPDisplay();
		else
			player.getInterfaceManager().closeXPDisplay();
	}

	public void switchXPPopup() {
		xpPopup = !xpPopup;
		player.sendMessage("XP pop-ups are now " + (xpPopup ? "en" : "dis") + "abled.");
		if (xpPopup)
			player.getInterfaceManager().sendXPPopup();
		else
			player.getInterfaceManager().closeXPPopup();
	}

	public long getTotalXp() {
		long totalxp = 0;
		for (double xp : getXp()) {
			totalxp += xp;
		}
		return totalxp;
	}

	public String getTotalXp(Player player) {
		double totalxp = 0;
		for (double xp : player.getSkills().getXp())
			totalxp += xp;
		return new DecimalFormat("#,###,##0").format(totalxp).toString();
	}

	public int getTargetIdByComponentId(int componentId) {
		switch (componentId) {
		case 150: // Attack
			return 0;
		case 9: // Strength
			return 1;
		case 40: // Range
			return 2;
		case 71: // Magic
			return 3;
		case 22: // Defence
			return 4;
		case 145: // Constitution
			return 5;
		case 58: // Prayer
			return 6;
		case 15: // Agility
			return 7;
		case 28: // Herblore
			return 8;
		case 46: // Theiving
			return 9;
		case 64: // Crafting
			return 10;
		case 84: // Runecrafting
			return 11;
		case 140: // Mining
			return 12;
		case 135: // Smithing
			return 13;
		case 34: // Fishing
			return 14;
		case 52: // Cooking
			return 15;
		case 130: // Firemaking
			return 16;
		case 125: // Woodcutting
			return 17;
		case 77: // Fletching
			return 18;
		case 90: // Slayer
			return 19;
		case 96: // Farming
			return 20;
		case 102: // Construction
			return 21;
		case 108: // Hunter
			return 22;
		case 114: // Summoning
			return 23;
		case 120: // Dungeoneering
			return 24;
		default:
			return -1;
		}
	}

	public int getSkillIdByTargetId(int targetId) {
		switch (targetId) {
		case 0: // Attack
			return ATTACK;
		case 1: // Strength
			return STRENGTH;
		case 2: // Range
			return RANGE;
		case 3: // Magic
			return MAGIC;
		case 4: // Defence
			return DEFENCE;
		case 5: // Constitution
			return HITPOINTS;
		case 6: // Prayer
			return PRAYER;
		case 7: // Agility
			return AGILITY;
		case 8: // Herblore
			return HERBLORE;
		case 9: // Thieving
			return THIEVING;
		case 10: // Crafting
			return CRAFTING;
		case 11: // Runecrafting
			return RUNECRAFTING;
		case 12: // Mining
			return MINING;
		case 13: // Smithing
			return SMITHING;
		case 14: // Fishing
			return FISHING;
		case 15: // Cooking
			return COOKING;
		case 16: // Firemaking
			return FIREMAKING;
		case 17: // Woodcutting
			return WOODCUTTING;
		case 18: // Fletching
			return FLETCHING;
		case 19: // Slayer
			return SLAYER;
		case 20: // Farming
			return FARMING;
		case 21: // Construction
			return CONSTRUCTION;
		case 22: // Hunter
			return HUNTER;
		case 23: // Summoning
			return SUMMONING;
		case 24: // Dungeoneering
			return DUNGEONEERING;
		default:
			return -1;
		}
	}

	public void refreshEnabledSkillsTargets() {
		int value = Utils.get32BitValue(enabledSkillsTargets, true);
		player.getPackets().sendConfig(1966, value);
	}

	public void refreshUsingLevelTargets() {
		int value = Utils.get32BitValue(skillsTargetsUsingLevelMode, true);
		player.getPackets().sendConfig(1968, value);
	}

	public void refreshSkillsTargetsValues() { // Don't set to 26, we don't
												// support it client sided yet.
		for (int i = 0; i < 25; i++) {
			player.getPackets().sendConfig(1969 + i, skillsTargetsValues[i]);
		}
	}

	public void setSkillTargetEnabled(int id, boolean enabled) {
		enabledSkillsTargets[id] = enabled;
		refreshEnabledSkillsTargets();
	}

	public void setSkillTargetUsingLevelMode(int id, boolean using) {
		skillsTargetsUsingLevelMode[id] = using;
		refreshUsingLevelTargets();
	}

	public void setSkillTargetValue(int skillId, int value) {
		skillsTargetsValues[skillId] = value;
		refreshSkillsTargetsValues();
	}

	public void setSkillTarget(boolean usingLevel, int skillId, int target) {
		setSkillTargetEnabled(skillId, true);
		setSkillTargetUsingLevelMode(skillId, usingLevel);
		setSkillTargetValue(skillId, target);
	}

	/**
	 * Integers representing Skill ID's.
	 */
	public static final int ATTACK = 0, DEFENCE = 1, STRENGTH = 2, HITPOINTS = 3, RANGE = 4, PRAYER = 5, MAGIC = 6,
			COOKING = 7, WOODCUTTING = 8, FLETCHING = 9, FISHING = 10, FIREMAKING = 11, CRAFTING = 12, SMITHING = 13,
			MINING = 14, HERBLORE = 15, AGILITY = 16, THIEVING = 17, SLAYER = 18, FARMING = 19, RUNECRAFTING = 20,
			CONSTRUCTION = 22, HUNTER = 21, SUMMONING = 23, DUNGEONEERING = 24, DIVINATION = 25, INVENTION = 26;

	/**
	 * Strings representing Skill names.
	 */
	public static final String[] SKILL_NAME = { "Attack", "Defence", "Strength", "Constitution", "Ranged", "Prayer",
			"Magic", "Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining",
			"Herblore", "Agility", "Thieving", "Slayer", "Farming", "Runecrafting", "Hunter", "Construction",
			"Summoning", "Dungeoneering", "Divination", "Invention" };

	/**
	 * Gets the players total level.
	 * 
	 * @return Total Level as Integer.
	 */
	public int getTotalLevel() {
		int totallevel = 0;
		for (int i = 0; i <= 26; i++)
			totallevel += getLevelForXp(i);
		return totallevel;
	}

	public static int getXPForLevel(int level) {
		int points = 0;
		int output = 0;
		for (int lvl = 1; lvl <= level; lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			if (lvl >= level)
				return output;
			output = (int) Math.floor(points / 4);
		}
		return 0;
	}

	/**
	 * Handles adding Skill Shards for Expert Skillcapes.
	 * 
	 * @param skill
	 *            The Skill to add the shard to.
	 */
	private void handleSkillShards(int skill) {
		if (Utils.random(100) <= 50) // Doesn't immediately give the shard, same
										// as RS
			return;
		/**
		 * Combatant's cape
		 */
		if (!player.hasItem(new Item(32053))) {
			if (getLevelForXp(ATTACK) >= 99 && skill == ATTACK)
				addShard(new Item(32069));
			if (getLevelForXp(STRENGTH) >= 99 && skill == STRENGTH)
				addShard(new Item(32070));
			if (getLevelForXp(DEFENCE) >= 99 && skill == DEFENCE)
				addShard(new Item(32071));
			if (getLevelForXp(HITPOINTS) >= 99 && skill == HITPOINTS)
				addShard(new Item(32074));
			if (getLevelForXp(RANGE) >= 99 && skill == RANGE)
				addShard(new Item(32075));
			if (getLevelForXp(PRAYER) >= 99 && skill == PRAYER)
				addShard(new Item(32073));
			if (getLevelForXp(MAGIC) >= 99 && skill == MAGIC)
				addShard(new Item(32076));
			if (getLevelForXp(SUMMONING) >= 99 && skill == SUMMONING)
				addShard(new Item(32072));
		}
		/**
		 * Artisan's cape
		 */
		if (!player.hasItem(new Item(32054))) {
			if (getLevelForXp(CRAFTING) >= 99 && skill == CRAFTING)
				addShard(new Item(32082));
			if (getLevelForXp(CONSTRUCTION) >= 99 && skill == CONSTRUCTION)
				addShard(new Item(32083));
			if (getLevelForXp(FIREMAKING) >= 99 && skill == FIREMAKING)
				addShard(new Item(32079));
			if (getLevelForXp(FLETCHING) >= 99 && skill == FLETCHING)
				addShard(new Item(32080));
			if (getLevelForXp(HERBLORE) >= 99 && skill == HERBLORE)
				addShard(new Item(32081));
			if (getLevelForXp(SMITHING) >= 99 && skill == SMITHING)
				addShard(new Item(32084));
			if (getLevelForXp(COOKING) >= 99 && skill == COOKING)
				addShard(new Item(32077));
			if (getLevelForXp(RUNECRAFTING) >= 99 && skill == RUNECRAFTING)
				addShard(new Item(32078));
		}
		/**
		 * Gatherer's cape
		 */
		if (!player.hasItem(new Item(32052))) {
			if (getLevelForXp(DIVINATION) >= 99 && skill == DIVINATION)
				addShard(new Item(32066));
			if (getLevelForXp(FARMING) >= 99 && skill == FARMING)
				addShard(new Item(32067));
			if (getLevelForXp(FISHING) >= 99 && skill == FISHING)
				addShard(new Item(32063));
			if (getLevelForXp(MINING) >= 99 && skill == MINING)
				addShard(new Item(32065));
			if (getLevelForXp(HUNTER) >= 99 && skill == HUNTER)
				addShard(new Item(32068));
			if (getLevelForXp(WOODCUTTING) >= 99 && skill == WOODCUTTING)
				addShard(new Item(32064));
		}
		/**
		 * Support cape
		 */
		if (!player.hasItem(new Item(32055))) {
			if (getLevelForXp(AGILITY) >= 99 && skill == AGILITY)
				addShard(new Item(32087));
			if (getLevelForXp(DUNGEONEERING) >= 99 && skill == DUNGEONEERING)
				addShard(new Item(32085));
			if (getLevelForXp(SLAYER) >= 99 && skill == SLAYER)
				addShard(new Item(32088));
			if (getLevelForXp(THIEVING) >= 99 && skill == THIEVING)
				addShard(new Item(32086));
		}
	}

	/**
	 * Handles checking and adding of Skill Shards.
	 * 
	 * @param item
	 *            The Shard to check & add.
	 */
	private void addShard(Item item) {
		if (!player.hasItem(item)) {
			player.addItem(item);
			player.sendMessage(
					Colors.red + "You've found " + Utils.getAorAn(item.getName()) + " " + item.getName() + "!");
		}
	}

	public int getHighestSkillLevel() {
		int maxLevel = 1;
		for (int skill = 0; skill < level.length; skill++) {
			int level = getLevelForXp(skill);
			if (level > maxLevel)
				maxLevel = level;
		}
		return maxLevel;
	}

}