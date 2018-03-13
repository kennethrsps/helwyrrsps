package com.rs.game.player.content;

import java.text.NumberFormat;
import java.util.Locale;

import com.rs.Settings;
import com.rs.game.Animation;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.utils.Colors;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.Utils;

/**
 * Handles the Well Of Good Will.
 * 
 * @author Zeus
 */
public class WellOfGoodWill {

	/**
	 * Sends a dialogue for the amount to give.
	 *
	 * @param player
	 *            The Player giving the amount.
	 */
	public static void give(Player player) {
		if (World.isWellActive()) {
			player.getDialogueManager().startDialogue("SimpleMessage", "The XP well is already active.");
			return;
		}
		if (World.isWeekend()) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"Well of Good Will does not stack with Weekend bonus EXP.");
			return;
		}
		player.getPackets().sendInputIntegerScript(true,
				"Progress: " + NumberFormat.getNumberInstance(Locale.US).format(World.getWellAmount()) + " GP ("
						+ ((World.getWellAmount() * 100) / Settings.WELL_MAX_AMOUNT) + "% of Goal); " + "Goal: "
						+ NumberFormat.getNumberInstance(Locale.US).format(Settings.WELL_MAX_AMOUNT) + " GP");
		player.getTemporaryAttributtes().put("donate_xp_well", Boolean.TRUE);
	}

	/**
	 * Donates to the well the amount to give.
	 *
	 * @param player
	 *            The Player donating.
	 * @param amount
	 *            The amount to give.
	 */
	public static void donate(Player player, int amount) {
		if (amount < 0)
			return;
		if ((World.getWellAmount() + amount) >= Settings.WELL_MAX_AMOUNT)
			amount = Settings.WELL_MAX_AMOUNT - World.getWellAmount();
		if (!player.hasMoney(amount)) {
			player.sendMessage("You don't have that much money.");
			return;
		}
		if(amount == 0 || (World.getWellAmount() >= Settings.WELL_MAX_AMOUNT)) {
			postDonation();
			return;
		}
		if (amount < 1) {
			player.sendMessage("You must donate at least 1 GP to the Well of Good Will.");
			return;
		}
		if (player.getSkills().getTotalLevel(player) < 100) {
			player.sendMessage("New players cannot donate to the Well of Good Will.");
			return;
		}
		player.takeMoney(amount);
		player.donatedToWell += amount;
		player.sendMessage("You've donated a total of " + Colors.red + Utils.moneyToString(player.donatedToWell)
				+ "</col> coins to the Well of Good Will.");
		player.setNextAnimation(new Animation(21841));
		World.addWellAmount(player.getDisplayName(), amount);
		postDonation();
	}

	/**
	 * A check after donating to the well to see if the x2 XP should start.
	 */
	private static void postDonation() {
		if (World.getWellAmount() >= Settings.WELL_MAX_AMOUNT) {
			World.sendWorldMessage("<col=FF0000>The goal of "
					+ NumberFormat.getNumberInstance(Locale.US).format(Settings.WELL_MAX_AMOUNT)
					+ " GP has been reached! Double XP for 2 hours begins now!", false);
			World.setWellActive((2 * 60 * 60 * 1000) + Utils.currentTimeMillis());
		}
	}

	/**
	 * Saves the progress of the well. If the x2 event is already active, this
	 * sends the amount left in gameticks.
	 */
	public static void save() {
		SerializableFilesManager.saveWellActiveTime(World.isWellActive() ? World.getWellActiveTime() : 0);
		SerializableFilesManager.saveWellAmount(World.isWellActive() ? 0 : World.getWellAmount());
	}

	public static void load() {
		World.setWellActive(SerializableFilesManager.loadWellActiveTime());
		World.setWellAmount(SerializableFilesManager.loadWellAmount());
	}
}