package com.rs.game.player.content.items;

import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.Burying.Bone;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/**
 * Handles the BoneCrusher item.
 * @author Zeus
 */
public class BoneCrusher {

	/**
	 * Handles the item.
	 * @param player The player.
	 * @param dropId The boneID.
	 * @return true if bone crushed.
	 */
	public static boolean handleBoneCrusher(Player player, int dropId) {
		if (player.getInventory().containsItem(18337, 1)) {
			if (dropId == 536) {
				double exp = Bone.increasedExperience(player, Bone.DRAGON_BONES.getExperience());
				handleCrushing(player, dropId, exp);
				return true;
			}
			if (dropId == 534) {
				double exp = Bone.increasedExperience(player, Bone.BABYDRAGON_BONES.getExperience());
				handleCrushing(player, dropId, exp);
				return true;
			}
			if (dropId == 2859) {
				double exp = Bone.increasedExperience(player, Bone.WOLF_BONES.getExperience());
				handleCrushing(player, dropId, exp);
				return true;
			}
			if (dropId == 6812) {
				double exp = Bone.increasedExperience(player, Bone.WYVERN_BONES.getExperience());
				handleCrushing(player, dropId, exp);
				return true;
			}
			if (dropId == 18830) {
				double exp = Bone.increasedExperience(player, Bone.FROST_DRAGON_BONES.getExperience());
				handleCrushing(player, dropId, exp);
				return true;
			}
			if (dropId == 35010) {
				double exp = Bone.increasedExperience(player, Bone.RUNE_DRAGON_BONES.getExperience());
				handleCrushing(player, dropId, exp);
				return true;
			}
			if (dropId == 4834) {
				double exp = Bone.increasedExperience(player, Bone.OURG_BONES.getExperience());
				handleCrushing(player, dropId, exp);
				return true;
			}
			if (dropId == 532) {
				double exp = Bone.increasedExperience(player, Bone.BIG_BONES.getExperience());
				handleCrushing(player, dropId, exp);
				return true;
			}
			if (dropId == 3125) {
				double exp = Bone.increasedExperience(player, Bone.JOGRE_BONES.getExperience());
				handleCrushing(player, dropId, exp);
				return true;
			}
			if (dropId == 35008) {
				double exp = Bone.increasedExperience(player, Bone.ADAMANT_DRAGON_BONES.getExperience());
				handleCrushing(player, dropId, exp);
				return true;
			}
			if (dropId == 6729) {
				double exp = Bone.increasedExperience(player, Bone.DAGANNOTH_BONES.getExperience());
				handleCrushing(player, dropId, exp);
				return true;
			}
			if (dropId == 526) {
				double exp = Bone.increasedExperience(player, Bone.BONES.getExperience());
				handleCrushing(player, dropId, exp);
				return true;
			}
			return false;
		}
		return false;
	}
	
	/**
	 * Handles the actual bone crushing.
	 * @param player The player.
	 * @param dropId The bone ID.
	 */
	private static void handleCrushing(Player player, int dropId, double xp) {
		player.addBonesOffered();
		player.sendMessage("Your Bonecrusher turned the bone drop into Prayer XP; "
				+ "bones offered: "+Colors.red+Utils.getFormattedNumber(player.getBonesOffered())+"</col>.", true);
		player.getSkills().addXp(Skills.PRAYER, xp);
		if (player.getAuraManager().getPrayerRestoration() != 0)
			player.getPrayer().restorePrayer((int) ((int) (Math.floor(player.getSkills()
				.getLevelForXp(Skills.PRAYER))) * player.getAuraManager().getPrayerRestoration()));
		/** Twisted Bird Skull necklace **/
		if (player.getEquipment().getAmuletId() == 19886)
			player.getPrayer().restorePrayer((int) ((int) xp * 0.5));
		/** Split dragontooth necklace **/
		else if (player.getEquipment().getAmuletId() == 19887)
			player.getPrayer().restorePrayer((int) ((int) xp * 0.75));
		/** Demon horn necklace **/
		else if (player.getEquipment().getAmuletId() == 19888)
			player.getPrayer().restorePrayer((int) xp);
	}
}