package com.rs;

import java.util.Calendar;

import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;

/**
 * A class used to store all server-wide/in-game configurations.
 * 
 * @author Zeus
 */
public final class Settings {

	/** Server data configuration **/
	public static final String SERVER_NAME = "Helwyr";
	public static final String REPOSITORY = "information/";

	/** Message to send on login **/
	public static String LATEST_UPDATE = "Helwyr has been updated , ::UPDATE for more details.";

	/* Server management */
	public static final int REVISION = 718; // revision of the client
	public static final int SUB_REVISION = 1; // client sub-build
	public static final int MINIMUM_RAM_ALLOCATED = 1000000000; // 1000mb
	public static int SERVER_PORT = 43594;
	public static int WORLD_ID = 1;

	/** SQL IP connection **/
	public static String WEBHOST_IP = Settings.DEBUG ? "1.1.1.1" : "localhost";
	public static boolean SQL_ENABLED = true;

	/** Player configurations **/
	public static final int START_PLAYER_HITPOINTS = 100;
	// public static final WorldTile START_PLAYER_LOCATION = new WorldTile(2474,
	// 2709, 2);
	// public static final WorldTile RESPAWN_PLAYER_LOCATION = new
	// WorldTile(2495, 2712, 2);
	public static final WorldTile START_PLAYER_LOCATION = new WorldTile(4316, 825, 0);
    public static final WorldTile RESPAWN_PLAYER_LOCATION = new WorldTile(4315, 867, 0);
    public static final int MAX_STARTER_COUNT = 3;
	public static final int MAX_CONNECTED_SESSIONS_PER_IP = 2;

	/** Game configuration **/
	public static final int AIR_GUITAR_MUSICS_COUNT = 250;
	public static final int QUESTS = 183;
	public static final long MAX_PACKETS_DECODER_PING_DELAY = 30000;
	public static final int WORLD_CYCLE_TIME = 600; // recycle 600 milliseconds

	/** Home region **/
	public static int MARKET_REGION_ID = 11324;
	public static int HOME_REGION_ID = 10026;

	/** Instancing the system Calendar **/
	private static Calendar cal = Calendar.getInstance();

	/** Special weekend boosts **/
	public static boolean SLAYER_WEEKEND = cal.get(Calendar.MONTH) == 11
			&& (cal.get(Calendar.DATE) >= 25 && cal.get(Calendar.DATE) <= 27),

			DUNGEONEERING_WEEKEND = cal.get(Calendar.MONTH) == 11
					&& (cal.get(Calendar.DATE) >= 25 && cal.get(Calendar.DATE) <= 27);

	// Website links
	public static String WEBSITE = "https://Helwyr3.com";
	public static String DONATE = WEBSITE + "/donate";
	public static String FORUM = WEBSITE + "/forums";
	public static String HISCORES = WEBSITE + "/highscores";
	public static String VOTE = WEBSITE + "/vote";
	public static String UPDATES = FORUM + "/index.php?/forum/4-news-announcement/";
	public static String DISCORD = "https://discord.gg/z2KCTFZ";
	public static String GUIDES = FORUM + "/index.php?/forum/12-guides/";
	public static String TWITCH = "";
	public static String LIVECODE = "";
	
	//staffs
	public static String[] OWNER = {"zeus"};
	public static String[] DEV = {"lumine",""};
	public static String[] ADMIN = {""};
	public static String[] MOD = {"dimitriuse","likx",""};
	public static String[] SUPPORT = {"arrow","vex"};
	public static String[] YOUTUBE = {"helwyr"};

	// All set using parameters on server launch
	public static boolean ECONOMY_MODE, DEBUG, GUI_MODE;

	// XP configuration
	public static int VET_XP = 25, INTERM_XP = 100, EASY_XP = 200, IRONMAN_XP = 10, EXPERT_XP = 5;

	// Drop configuration
	public static double VET_DROP = 3, INTERM_DROP = 2, EASY_DROP = 1, IRONMAN_DROP = 2, HCIRONMAN_DROP = 3,
			EXPERT_DROP = 4;

	/** Well of Goodwill **/
	public static final int WELL_MAX_AMOUNT = 50000000;

	// NPC Settings
	public static final int[] NON_WALKING_NPCS = { 522, 557, 44, 554, 537, 549, 546, 550, 2676, 548, 598, 531, 659,
			2824, 2234, 5913, 4247, 6539, 9400, 19519, 278, 22153, 14381, 10021, 526, 527, 15085 ,2465, 587 };

	public static final int[] FORCE_WALKING_NPCS = { 15309, 18150, 18151, 18153, 18155, 18157, 18159, 18161, 18163,
			18165, 18167, 18169, 18171, 18204 };

	// Disable or Enable Yell.
	public static boolean serverYell = true;
	public static String yellChangedBy;

	public static boolean yellEnabled() {
		return serverYell;
	}

	// MISC
	public static boolean LENDING_DISABLED = true;

	public static final String[] FORBIDDEN_SOUL_WARS_ITEMS = { "torva", "virtus", "pernix", "divine spirit shield",
			"arcane spirit shield", "spectral spirit shield", "elysian spirit shield", "ganodermic", "knife", "sled",
			"ascension", "zaryte", "sirenic", "noxious", "drygore" };

	public static int ZEAL_MODIFIER = World.isWeekend() ? 3 : 2;

	/**
	 * Checks for drop rates.
	 * 
	 * @param player
	 *            The player.
	 * @return The rate.
	 */
	public static double getDropQuantityRate(Player player) {
		if (player.isIronMan())
			return IRONMAN_DROP;
		if (player.isHCIronMan())
			return HCIRONMAN_DROP;
		if (player.isVeteran())
			return VET_DROP;
		if (player.isIntermediate())
			return INTERM_DROP;
		if (player.isExpert())
			return EXPERT_DROP;
		if (player.isEasy())
			return EASY_DROP;
		return 1;
	}

	// 35% for uncommon
	// 0.089% for rare
	// 0.09% for jackpot
	public static final double[] SOF_CHANCES = new double[] { 1.0D, 0.52D, 0.0052D, 0.009D };

	public static final int[] SOF_COMMON_CASH_AMOUNTS = new int[] { 100000, 250000, 500000, 1000000, 5000000 };
	public static final int[] SOF_UNCOMMON_CASH_AMOUNTS = new int[] { 100000, 250000, 500000, 1000000, 5000000 };
	public static final int[] SOF_RARE_CASH_AMOUNTS = new int[] { 10000000, 20000000, 30000000, 40000000, 50000000 };
	public static final int[] SOF_JACKPOT_CASH_AMOUNTS = new int[] { 50 * 1000000, 100 * 1000000, 200 * 1000000,
			500 * 1000000 };
	public static final int[] SOF_COMMON_LAMPS = new int[] { 23713, 23717, 23721, 23725, 23729, 23737, 23733, 23741,
			23745, 23749, 23753, 23757, 23761, 23765, 23769, 23778, 23774, 23786, 23782, 23794, 23790, 23802, 23798,
			23810, 23806, 23814 };
	public static final int[] SOF_UNCOMMON_LAMPS = new int[] { 23714, 23718, 23722, 23726, 23730, 23738, 23734, 23742,
			23746, 23750, 23754, 23758, 23762, 23766, 23770, 23779, 23775, 23787, 23783, 23795, 23791, 23803, 23799,
			23811, 23807, 23815 };
	public static final int[] SOF_RARE_LAMPS = new int[] { 23715, 23719, 23723, 23727, 23731, 23739, 23735, 23743,
			23747, 23751, 23755, 23759, 23763, 23767, 23771, 23780, 23776, 23788, 23784, 23796, 23792, 23804, 23800,
			23812, 23808, 23816 };
	public static final int[] SOF_JACKPOT_LAMPS = new int[] { 23716, 23720, 23724, 23728, 23732, 23740, 23736, 23744,
			23748, 23752, 23756, 23760, 23764, 23768, 23773, 23781, 23777, 23789, 23785, 23797, 23793, 23805, 23801,
			23813, 23809, 23817 };
	public static final int[] SOF_COMMON_OTHERS = new int[] { 13666, 11732, 11126, 15271, 384, 537, 1359, 1275, 2364,
			23048, 1745, 13103, 2506, 2508, 2510, 29304, 29301, 29305, 29306, 29310, 31080, 31081, 29294, 29295, 29296,
			29300 };

	/** Weekly SOF promotion - Ethereal Outfits **/
	// public static final int[] SOF_UNCOMMON_OTHERS = new int[] { 32342, 32343,
	// 32344, 32345, 32346, 32347, 32348, 32349, 32350, 32351, 32352, 32353,
	// 32354, 32355, 32356, 29307, 29312, 31088, 29298, 29299, 29302, 29303,
	// 31310};
	public static final int[] SOF_UNCOMMON_OTHERS = new int[] { 35968, 35969, 35970, 35971, 35972, 35973, 35974, 35975,
			35976, 35977, 35963, 35964, 35965, 35966, 35967, 35886, 29307, 29312, 31088, 29298, 29299, 29302, 29303,
			31310 };
	/** Old uncommon list **/
	// public static final int[] SOF_UNCOMMON_OTHERS = new int[] { 25312, 25314,
	// 30372, 4566, 9013, 7158, 19346, 19348, 19350, 19352, 22412, 13316, 13317,
	// 5042, 5044, 5046, 29307, 29312, 31088, 29298, 29299, 29302, 29303,
	// 31310};
	public static final int[] SOF_RARE_OTHERS = new int[] { 995, 995, 995, 995, 995, 30372, 31041, 23691, 23684, 23685,
			23686, 23687, 23688, 23689, 23690, 23683, 23692, 23693, 23694, 23675, 23676, 23677, 23678, 23673 };
	public static final int[] SOF_JACKPOT_OTHERS = new int[] { 995, 995, 995, 995, 962, 14484, 23679, 23680, 23681,
			23682, 23697, 23698, 23699, 23700 };
	public static final int LOCAL_NPCS_LIMIT = 250;
	public static final int PACKET_SIZE_LIMIT = 7500;
	public static final int PLAYERS_LIMIT = 2000;
	public static Object[][] PRODUCTS = {
			{ 1, 5, "Familiar Expert", "Increases your Familiar timer by 50%. Also increases Familiar health by 25%.",
					1 },
			{ 2, 15, "Charge Befriender",
					"With this perk, your items will never degrade. This perk covers any and all degradable items.",
					1 },
			{ 3, 3, "Charm Collector",
					"Charms are automatically sent to your Bank account after killing monsters. Also increases charm drop rate by 25%.",
					1 },
			{ 4, 3, "Coin Collector",
					"Coins that are dropped while killing monsters are automatically picked up. Also increases coin drop rate and coin drop amounts by 25%.",
					1 },
			{ 5, 10, "Prayer Betrayer",
					"Your Prayer points decrease at a 25% lower rate. Also increases experience gained by 25%.", 1 },
			{ 6, 5, "Avas Secret",
					"Acts as a permanently equipped Ava\"s Alerter no matter what cape you\"re wearing. You also have a higher chance of recovering fired ammunition.",
					1 },
			{ 7, 6, "Key Expert", "Receive double the loot upon opening the Crystal chest at Home.", 1 },
			{ 8, 5, "Dragon Trainer",
					"Acts as a permanently equipped anti-dragon shield. Also increases the chance of receiving a baby pet dragon by 25%.",
					1 },
			{ 9, 3, "GWD Specialist	",
					"Removes the requirement of having kill-count to enter any of the 5 Godwars dungeon boss rooms.",
					1 },
			{ 10, 10, "Dungeons Master",
					"Reduced kill-count requirement while progressing in Dungeoneering from 15 to 5. Also increases EXP and token ratios by 25%.",
					1 },
			{ 11, 3, "Petchanter", "Increases Boss pet drop rates by 25%.", 1 },
			{ 12, 10, "Perslaysion",
					"This perk allows to persuade Kuradal to reset Slayer tasks free of charge. Also increases EXP and point ratios by 25% on task completion.",
					1 },
			{ 13, 3, "The Pyromaniac",
					"There\"s now a 25% higher chance of receiving Fire spirits, Bonfiring will now grant +15% more experience, and health boost from stoking a Bonfire increased to 200%.",
					1 },
			{ 14, 4, "Ports Master",
					"Player-Ports: With this perk your ships will have a 20% increased return rate and you will receive +15% more rewards from successful voyages.",
					1 },
			{ 15, 3, "Green Thumb",
					"Increases the chance of Farming plants growing up healthy and increases their yield. Stacks with Magic Secateurs effect.",
					2 },
			{ 16, 1, "Bird Man", "Increases the rate of receiving Bird Nest drops while Woodcutting.", 2 },
			{ 17, 1, "Unbreakable Forge", "Your Ring of Forging will never deplete {Iron ore smelting}.", 2 },
			{ 18, 4, "Sleight of Hand",
					"Sleight of Hand ensures your success rates to 100% in all aspects of Thieving. This includes pick-pocketing and removes damage taken from Home thieving stalls.",
					2 },
			{ 19, 8, "Herbivore",
					"All monsters now have the ability to drop a box that contains 10 random grimy herbs. Also increases Herblore experience by 25% and extends potion timers by 200%.",
					2 },
			{ 20, 5, "Master Fisherman",
					"This perk allows you at a chance to catch double the fish at once. Exp is given for both fish caught and stacks with Agility\"s multi-catch. Also removes the requirement of having appropriate baits.",
					2 },
			{ 21, 3, "Delicate Craftsman",
					"Gain an extra 10% experience whilst spinning on the wheel, 25% extra experience for creating dragonhide armours and removes the requirement of having thread.",
					2 },
			{ 22, 4, "Elf Fiend", "Allows instant access to the Prifddinas city.", 2 },
			{ 23, 3, "Master Chef",
					"Decreases the rate at which your food burns, increases Cooking EXP gained by 25% and increases the speed of Cooking by 33%.",
					2 },
			{ 24, 10, "Master Diviner",
					"Increases the rate at which you receive Chronicle Fragments by 25%, higher chance of receiving enriched memories while harvesting wisps by 25% and increases Divination EXP gained by 25%.",
					2 },
			{ 25, 5, "Quarrymaster",
					"Increases the speed of mining ores by 33%, and increases the Mining EXP gained by 25%. Also, has a 5% chance of converting your ore into the bar of the ore you\"re currently mining.",
					2 },
			{ 26, 6, "Huntsman",
					"You can now place an extra 2 traps at once {7 traps at level 80+}, gain an additional +25% experience on successful catches, +15% success rate increase AND a 10% chance to receive double loot.",
					2 },
			{ 27, 10, "Divine Doubler", "This perk allows you to gather double your divine limit every day!", 2 },
			{ 28, 5, "Imbued Focus",
					"This perk allows you to drain the energy from Runespan creatures at a much slower rate! Creatures will stay alive for longer, allowing you to get more XP.",
					2 },
			{ 29, 7, "Alchemic Smithing",
					"Grants you the ability to smith bars without coal {adamant bars require 2 adamantite ore, and runite bars require 3 runite ore.The bonus is effectively equal to x = {amount of coal}-2/2, if x < 2, then it will only require one ore.",
					2 },
			{ 30, 20, "Bank Command",
					"This game perk allows you to access your Bank Account all across Helwyr via typing ;;bank in the chat-box! Does not work in Mini-games/PvP/Controllers.",
					3 },
			{ 31, 5, "Stamina Boost", "With this perk, your run energy will never deplete and stay at 100% forever.",
					3 },
			{ 32, 6, "Overclocked", "Your Aura active times are now doubled. Also halves recharge time.", 3 },
			{ 33, 5, "The Mini-Gamer",
					"Doubles the Points received from Mini-game rewards {Warriors Guild, Soul Wars, Pest Control}.",
					3 },
			{ 34, 10, "Investigator",
					"This perk allows you to get the reward from any clue after finishing only one step!", 3 },
			{ 35, 10, "+1 Bank Container", "Adds an additional container to your bank!", 3 },
			{ 36, 25, "+3 Bank Containers", "Adds 3 containers to your bank list!", 3 },
			{ 37, 50, "+7 Bank Containers", "Adds 7 Bank containers to your list!", 3 },
			{ 38, 10, "Petloot",
					"This will allow your pet to automatically loot all the drops {besides Coins and Charms} and choose either put it in your inventory or bank.",
					3 },
			{ 39, 15, "Looters Perk Package",
					"This Perk package contains the following game perks with a small discount: Bird Man; Charm Collector; Coin Collector; Key Expert; Petchanter.",
					4 },
			{ 40, 56, "Skillers Perk Package",
					"This Perk package contains ALL of the perks in the - Skilling Perks - category.", 4 },
			{ 41, 50, "Utility Perk Package",
					"This Perk package contains the following game perks with a small discount: Bank Command; Stamina Boost; Overclocked; Elf Fiend, The Mini-Gamer.",
					4 },
			{ 42, 60, "Combatants Perk Package",
					"This Perk package contains Familiar Exper, Charge befriender , prayer betrayer, avas secret , dragon trainer , gwd specialist , dungeon and perslaysion.",
					4 },
			{ 43, 175, "Complete Perk Package",
					"This Perk package contains greenthumb, unbreakable forge, sleightofhand, herbivore, masterfisherman, delicate craftsman, familiar expert, charge befriender, prayer betrayer, avas secret, dragon trainer, gwd specialist, dungeon, perslaysion, birdman, charmcollector, coin collector, keyexpert, petchanter, bankcommand, staminaboost, elffiend, overclocked, master chef, master diviner, minigamer, quarrymaster, huntsman, masterfledger, the piromaniac, portsmaster, divine double and investigator",
					4 },
			{ 44, 3, "Infernal Gaze", "This aura allows you to override your eye color to red!", 5 },
			{ 45, 3, "Serene Gaze", "This aura allows you to override your eye color to blue!", 5 },
			{ 46, 3, "Vernal Gaze", "This aura allows you to override your eye color to green", 5 },
			{ 47, 3, "Mystical Gaze", "This aura allows you to override your eye color to purple!", 5 },
			{ 48, 3, "Blazing Gaze", "This aura allows you to override your eye color to orange!", 5 },
			{ 49, 3, "Nocturnal Gaze", "This aura allows you to override your eye color to yellow!", 5 },
			{ 50, 3, "Abyssal Gaze", "This aura allows you to override your eye color to black!", 5 },
			{ 51, 1, "Keepsake x 1", "converts a wearable item into a cosmetic override", 5 },
			{ 52, 2, "Keepsake x 3", "converts a wearable item into a cosmetic override", 5 },
			{ 53, 4, "Keepsake x 7", "converts a wearable item into a cosmetic override", 5 },
			{ 54, 5, "Keepsake x 10", "converts a wearable item into a cosmetic override", 5 },
			{ 55, 1.5, "Arcane Fishing", "An animation override that plays while fishing any standard fish.", 6 },
			{ 56, 1.5, "Strongarm Burial",
					"An animation override that plays while burying bones and offering bones at an altar.", 6 },
			{ 57, 1.5, "Arcane Cooking", "An animation override that plays while cooking using a range or log fire.",
					6 },
			{ 58, 1.5, "Power Divination", "An animation override that plays while gathering energies from a location.",
					6 },
			{ 59, 1.5, "Powerful Conversion", "An animation override that plays while converting energies at a rift.",
					6 },
			{ 60, 1.5, "Agile Divination", "An animation override that plays while gathering energies from a location.",
					6 },
			{ 61, 1.5, "Sinister Slumber", "An animation override that plays while resting.", 6 },
			{ 62, 1.5, "Crystal Impling Resting", "An animation override that plays while resting.", 6 },
			{ 63, 1.5, "Headbutt Mining", "An animation override that plays while mining any standard ore.", 6 },
			{ 64, 3.5, "Sandstorm Walk", "An animation override that plays while walking, running, or idling.", 6 },
			{ 65, 3.5, "Proud Walk", "An animation override that plays while walking, running, or idling.", 6 },
			{ 66, 3.5, "Barbarian Walk", "An animation override that plays while walking, running, or idling.", 6 },
			{ 67, 3.5, "Revenant Walk", "An animation override that plays while walking, running, or idling.", 6 },
			{ 68, 1.5, "Slayer Battle Cry", "An animation that plays after killing an assigned slayer monster.", 6 },
			{ 69, 1.5, "Enhanced Potion Making", "An animation override that plays while brewing potions.", 6 },
			{ 70, 1.5, "Lumberjack Woodcutting",
					"An animation override that plays while woodcutting any standard tree.", 6 },
			{ 71, 1.5, "Deep-Sea Fishing", "An animation override that plays while fishing any standard fish.", 6 },
			{ 72, 1.5, "Zen Resting", "An animation override that plays while resting.", 6 },
			{ 73, 1.5, "Karate Chop Fletching", "An animation override that plays while fletching any logs.", 6 },
			{ 74, 1.5, "Iron-Fist Smithing", "An animation override that plays while smithing with an anvil.", 6 },
			{ 75, 1.5, "Chi-Blast Mining", "An animation override that plays while mining any standard ore.", 6 },
			{ 76, 1.5, "Samurai Cooking", "An animation override that plays while cooking using a range or log fire.",
					6 },
			{ 77, 1.5, "Roundhouse Woodcutting",
					"An animation override that plays while woodcutting any standard tree.", 6 },
			{ 78, 1.5, "Chi-Blast Mining", "An animation override that plays while mining any standard ore.", 6 },
			{ 79, 1.5, "Arcane Smelting", "An animation override that plays while smelting ores to bars.", 6 },
			{ 80, 1.5, "Arcane Resting", "An animation override that plays while resting.", 6 },
			{ 81, 1.5, "Strongarm Woodcutting", "An animation override that plays while woodcutting any standard tree.",
					6 },
			{ 82, 1.5, "Strongarm Mining", "An animation override that plays while mining any standard ore", 6 },
			{ 83, 3.5, "Sad Walk", "An animation override that plays while walking, running, or idling.", 6 },
			{ 84, 3.5, "Happy Walk", "An animation override that plays while walking, running, or idling.", 6 },
			{ 85, 1.5, "Agile Conversion", "An animation override that plays while converting energies at a rift.", 6 },
			{ 86, 1.5, "Strongarm Resting", "An animation override that plays while resting.", 6 },
			{ 87, 1.5, "Energy Drain Resting", "An animation override that plays while resting.", 6 },
			{ 88, 1.5, "Armchair Warrior", "An animation override that plays while resting.", 6 },
			{ 89, 2, "SoF Spins x5",
					"Purchase x5 Squeal of Fortune spins. Warning: Spins will NOT be received on ironman accounts.",
					7 },
			{ 90, 8, "SoF Spins x25 + 2 FREE",
					"Purchase x25 + 2 Squeal of Fortune spins. Warning: Spins will NOT be received on ironman accounts",
					7 },
			{ 91, 16, "SoF Spins x50 + 5 FREE",
					"SoF Spins x50 + 5 FREE. Warning: Spins will NOT be received on ironman accounts.", 7 },
			{ 92, 40, "SoF Spins x150 + 25 FREE",
					"Purchase x150 + 25 Squeal of Fortune spins. Warning: Spins will NOT be received on ironman accounts.",
					7 },
			{ 93, 80, "SoF Spins x300 + 50 FREE",
					"Purchase x300 + 50 free Squeal of Fortune spins. Warning: Spins will NOT be received on ironman accounts.",
					7 } };

	public static String[] TABS = { "Combat Perks", "Skilling Perks", "Utility Perks", "Perk Packages",
			"Cosmetics Overrides", "Animation Overrides", "Squeal Of Fortune" };

}
