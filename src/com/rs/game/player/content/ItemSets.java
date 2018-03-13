package com.rs.game.player.content;

import com.rs.cache.loaders.ClientScriptMap;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.network.protocol.codec.decode.WorldPacketsDecoder;
import com.rs.utils.ItemExamines;

public class ItemSets {

	public enum Sets {

		BRONZE_LG(11814, 1155, 1117, 1075, 1189), BRONZE_SK(11816, 1155, 1117,
				1087, 1189), IRON_LG(11818, 1153, 1115, 1067, 1191), IRON_SK(
				11820, 1153, 1115, 1081, 1191), STEEL_LG(11822, 1157, 1119,
				1069, 1193), STEEL_SK(11824, 1157, 1119, 1083, 1193), BLACK_LG(
				11826, 1165, 1125, 1077, 1195), BLACK_SK(11828, 1165, 1125,
				1089, 1195), MITHRIL_LG(11830, 1159, 1121, 1071, 1197), MITHRIL_SK(
				11832, 1159, 1121, 1085, 1197), ADAMANT_LG(11834, 1161, 1123,
				1073, 1199), ADAMANT_SK(11836, 1161, 1123, 1091, 1199), PROSELYTE_LG(
				9666, 9672, 9674, 9676), PROSELYTE_SK(9670, 9672, 9674, 9678), RUNE_LG(
				11838, 1163, 1127, 1079, 1201), RUNE_SK(11840, 1163, 1127,
				1093, 1201), DRAG_CHAIN_LG(11842, 1149, 3140, 4087, 1187), DRAG_CHAIN_SK(
				11844, 1149, 3140, 4585, 1187), DRAG_PLATE_LG(14529, 11335,
				14479, 4087, 1187), DRAG_PLATE_SK(14531, 11335, 14479, 4585,
				1187), BLACK_H1_LG(19520, 10306, 19167, 7332, 19169), BLACK_H1_SK(
				19530, 10306, 19167, 7332, 19171), BLACK_H2_LG(19522, 10308,
				19188, 7338, 19190), BLACK_H2_SK(19532, 10308, 19188, 7338,
				19192), BLACK_H3_LG(19524, 10310, 19209, 7344, 19211), BLACK_H3_SK(
				19534, 10310, 19209, 7344, 19213), BLACK_H4_LG(19526, 10312,
				19230, 7350, 19232), BLACK_H4_SK(19536, 10312, 19230, 7350,
				19234), BLACK_H5_LG(19528, 10314, 19251, 7356, 19253), BLACK_H5_SK(
				19538, 10314, 19251, 7356, 19255), BLACK_TRIM_LG(11878, 2587,
				2583, 2585, 2589), BLACK_TRIM_SK(11880, 2587, 2583, 3472, 2589), BLACK_GTRIM_LG(
				11878, 2595, 2591, 2593, 2597), BLACK_GTRIM_SK(11880, 2595,
				2591, 3473, 2597), ADAMANT_H1_LG(19540, 10296, 19173, 7334,
				19175), ADAMANT_H1_SK(19550, 10296, 19173, 7334, 19177), ADAMANT_H2_LG(
				19542, 10298, 19194, 7340, 19196), ADAMANT_H2_SK(19552, 10298,
				19194, 7340, 19198), ADAMANT_H3_LG(19544, 10300, 19215, 7346,
				19217), ADAMANT_H3_SK(19554, 10300, 19215, 7346, 19219), ADAMANT_H4_LG(
				19546, 10302, 19236, 7352, 19238), ADAMANT_H4_SK(19556, 10302,
				19236, 7352, 19240), ADAMANT_H5_LG(19548, 10304, 19257, 7358,
				19259), ADAMANT_H5_SK(19558, 10304, 19257, 7358, 19261), ADAMANT_TRIM_LG(
				11886, 2605, 2599, 2601, 2603), ADAMANT_TRIM_SK(11888, 2605,
				2599, 3474, 2603), ADAMANT_GTRIM_LG(11890, 2613, 2607, 2609,
				2611), ADAMANT_GTRIM_SK(11892, 2613, 2607, 3475, 2611), RUNE_H1_LG(
				19560, 10286, 19179, 19182, 7336), RUNE_H1_SK(19570, 10286,
				19179, 19185, 7336), RUNE_H2_LG(19562, 10288, 19200, 19203,
				7342), RUNE_H2_SK(19572, 10288, 19200, 19206, 7342), RUNE_H3_LG(
				19564, 10290, 19221, 19224, 7348), RUNE_H3_SK(19574, 10290,
				19221, 19227, 7348), RUNE_H4_LG(19566, 10292, 19242, 19245,
				7354), RUNE_H4_SK(19576, 10292, 19242, 19248, 7354), RUNE_H5_LG(
				19568, 10294, 19263, 19266, 7360), RUNE_H5_SK(19578, 10294,
				19263, 19269, 7360), RUNE_TRIM_LG(11894, 2627, 2623, 2625, 2629), RUNE_TRIM_SK(
				11896, 2627, 2623, 3477, 2629), RUNE_GTRIM_LG(11898, 2619,
				2615, 2617, 2621), RUNE_GTRIM_SK(11900, 2619, 2615, 3672, 2621), GUTHIX_LG(
				11926, 2673, 2669, 2671, 2675), GUTHIX_SK(11932, 2673, 2669,
				3480, 2675), SARADOMIN_LG(11928, 2665, 2661, 2663, 2667), SARADOMIN_SK(
				11934, 2665, 2661, 3479, 2667), ZAMORAK_LG(11930, 2657, 2653,
				2655, 2659), ZAMORAK_SK(11936, 2657, 2653, 3478, 2659), ROCKSHELL(
				11942, 6128, 6129, 6130, 6151, 6145), ELITEBLACK(14527, 14494,
				14492, 14490), THIRDAGEMELEE(11858, 10350, 10348, 10352, 10346), THIRDAGERANGE(
				11860, 10334, 10330, 10332, 10336), THIRDAGEMAGE(11862, 10342,
				10338, 10340, 10344), BARROWSAHRIM(11846, 4708, 4710, 4712,
				4714), BARROWSDHAROK(11848, 4716, 4718, 4720, 4722), BARROWSGUTHAN(
				11850, 4724, 4726, 4728, 4730), BARROWSKARIL(11852, 4732, 4734,
				4736, 4738), BARROWSTORAG(11854, 4745, 4747, 4749, 4751), 
		BARROWSVERAC(11856, 4753, 4755, 4757, 4759), 
		
		GREENDHIDE(11864, 1135, 1099, 1065), 
		BLUEDHIDE(11866, 2499, 2493, 2487), 
		REDDHIDE(11868, 2501, 2495, 2489), 
		BLACKHIDE(11870, 2503, 2497, 2491),
		
		BLUEMYSTICROBES(11872, 4089, 4091, 4093, 4095, 4097), LIGHTMYSTICROBES(11960,
				4109, 4111, 4113, 4115, 4117), DARKMYSTICROBES(11962, 4099,
				4101, 4103, 4105, 4107), INFINITYROBES(11874, 6918, 6916, 6924,
				6920, 6922), SPLITBARK(11876, 3385, 3387, 3389, 3391, 3393), ENCHANTEDROBES(
				11902, 7398, 7399, 7400), BLUEWIZROBE_T(11904, 7396, 7392, 7388), BLUEWIZROBE_G(
				11906, 7394, 7390, 7386), LEATHER_T(11908, 7364, 7368), LEATHER_G(
				11910, 7362, 7366), GREENDHIDE_T(11912, 7372, 7380), GREENDHIDE_G(
				11914, 7370, 7378), BLUEDHIDE_T(11916, 7376, 7384), BLUEDHIDE_G(
				11918, 7374, 7382), GUTHIXRANGE(11920, 10382, 10378, 10380,
				10376), SARADOMINRANGE(11922, 10390, 10386, 10388, 10384), ZAMORAKRANGE(
				11924, 10374, 10370, 10372, 10368), GILDED_LG(11938, 3486,
				3481, 3483, 3488), GILDED_SK(11940, 3486, 3481, 3485, 3488), SPINED(
				11944, 6131, 6133, 6135, 6143, 6149), SKELETAL(11946, 6137,
				6139, 6141, 6147, 6153), DWARFCANNON(11967, 6, 8, 10, 12), DAGONHAIROBES(
				14525, 14499, 14497, 14501), THIRDAGEPRAYER(19580, 19314,
				19317, 19320, 19311, 19308), BANDOSRANGE(19582, 19457, 19453,
				19455, 19451), ANCIENTRANGE(19584, 19449, 19445, 19447, 19443), ARMADYLRANGE(
				19586, 19465, 19461, 19463, 19459), ARMADYLRUNE_LG(19588,
				19422, 19413, 19416, 19425), ARMADYLRUNE_SK(19590, 19422,
				19413, 19414, 19425), BANDOSRUNE_LG(19592, 19437, 19428, 19431,
				19440), BANDOSRUNE_SK(19594, 19437, 19428, 19434, 19440), ANCIENTRUNE_LG(
				19596, 19407, 19398, 19401, 19410), ANCIENTRUNE_SK(19598,
				19407, 19398, 19404, 19410), BARROWSAKRISAE(21768, 21736,
				21744, 21752, 21760), ROYALDHIDE(24386, 24382, 24379, 24376);

		private int setId;
		private int[] items;

		private Sets(int setId, int... items) {
			this.setId = setId;
			this.items = items;
		}

		public int getId() {
			return setId;
		}

		public int[] getItems() {
			return items;
		}
	}

	public static Sets getSet(int id) {
		for (Sets set : Sets.values())
			if (set.setId == id)
				return set;
		return null;
	}

	public static void sendComponentsBySlot(Player player, int slot, int itemId) {
		Item item = player.getInventory().getItem(slot);
		if (item == null || item.getId() != itemId)
			return;
		sendComponents(player, itemId);
	}

	/**
	 * Exchanges players box set for its components.
	 * @param player The player.
	 * @param slot The interface slot id.
	 * @param id The item id.
	 */
	public static void exchangeSet(Player player, int slot, int id) {
		Item item = player.getInventory().getItem(slot);
		if (item == null || item.getId() != id) {
			player.sendMessage("This isn't an Item Set.");
			return;
		}
		Sets set = getSet(id);
		if (set == null) {
			player.sendMessage("This isn't a set item, you can't break it up into component parts.", true);
			return;
		}
		if (player.getInventory().getFreeSlots() < set.items.length - 1) {
			player.sendMessage("You don't have enough inventory space for the component parts.", true);
			return;
		}
		player.getInventory().deleteItem(slot, item);
		for (int itemId : set.items)
			player.getInventory().addItem(itemId, 1);
		player.sendMessage("You sucessfully traded your item set for its components!", true);
	}

	/**
	 * Exchanges players items for a set box.
	 * @param player The player.
	 * @param id The box set ID.
	 */
	public static void exchangeSet(Player player, int id) {
		Sets set = getSet(id);
		if (set == null) {
			player.getPackets().sendGameMessage("This isn't a set item.", true);
			return;
		}
		for (int itemId : set.items) {
			if (!player.getInventory().containsItem(itemId, 1)) {
				player.sendMessage("You don't have the parts to make up this set.", true);
				return;
			}
		}
		for (int itemId : set.items)
			player.getInventory().deleteItem(itemId, 1);
		player.getInventory().addItem(id, 1);
		player.sendMessage("You sucessfully traded your item components for a set!", true);
	}

	/**
	 * Examines the box set.
	 * @param player The player.
	 * @param id The box set ID.
	 */
	public static void examineSet(Player player, int id) {
		Sets set = getSet(id);
		if (set == null) {
			player.sendMessage("This isn't a set item.", true);
			return;
		}
		player.getPackets().sendGameMessage(ItemExamines.getExamine(new Item(id)));
	}

	public static void sendComponents(Player player, int id) {
		Sets set = getSet(id);
		if (set == null) {
			player.sendMessage("This isn't a set item.", true);
			return;
		}
		String message = ClientScriptMap.getMap(1088).getStringValue(id);
		if (message == null)
			return;
		player.getPackets().sendGameMessage(message);
	}
	
	/**
	 * Handles interface buttons.
	 * @param player The player.
	 * @param intefaceId The interface ID.
	 * @param slotId The interface slot ID.
	 * @param itemId The item ID.
	 * @param packetId The action button.
	 */
	public static void handleButtons(Player player, int interfaceId, int slotId, int itemId, int packetId) {
		switch (interfaceId) {
		case 645:
			switch (packetId) {
			case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
				sendComponents(player, itemId);
				break;
			case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
				exchangeSet(player, itemId);
				break;
			case WorldPacketsDecoder.ACTION_BUTTON3_PACKET:
				examineSet(player, itemId);
				break;
			}
			break;
		case 644:
			switch (packetId) {
			case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
				sendComponents(player, itemId);
				break;
			case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
				exchangeSet(player, slotId, itemId);
				break;
			case WorldPacketsDecoder.ACTION_BUTTON3_PACKET:
				examineSet(player, itemId);
				break;
			}
			break;
		}
		return;
	}
}