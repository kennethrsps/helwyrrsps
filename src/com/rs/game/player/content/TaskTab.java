package com.rs.game.player.content;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.World;
import com.rs.game.player.DailyTaskManager.DailyTask;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.controllers.InstancedPVPControler;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/**
 * @author Zeus
 * @date 15.01.2014
 */
public class TaskTab {

	/**
	 * Sends the actual Noticeboard tab.
	 * 
	 * @param player
	 *            The player to send to.
	 */
	public static void sendTab(final Player player) {
		int rights = player.getRights();
		String pName = player.getUsername();
		String title = Colors.cyan + "Player";
		String gameMode = null;
		String status = null;
		String npcName = null;
		String slayerTask = null;

		// I've added check for if is not null, always check if it's not null
		// else RIP >.>
		if (player.getContract() != null)
			npcName = NPCDefinitions.getNPCDefinitions(player.getContract().getNpcId()).getName().toLowerCase();
		if (player.getSlayerManager().getCurrentTask() != null)
			slayerTask = player.getSlayerManager().getCurrentTask().getName();

		/**
		 * Defining player ranks
		 */
		if (player.isExpert())
			gameMode = Colors.cyan + "Expert (x" + Settings.EXPERT_XP + " XP)";
		if (player.isVeteran())
			gameMode = Colors.cyan + "Veteran (x" + Settings.VET_XP + " XP)";
		if (player.isIntermediate())
			gameMode = Colors.cyan + "Intermed. (x" + Settings.INTERM_XP + " XP)";
		if (player.isEasy())
			gameMode = Colors.cyan + "Easy (x" + Settings.EASY_XP + " XP)";
		if (player.isIronMan())
			gameMode = Colors.cyan + "Ironman (x" + Settings.IRONMAN_XP + " XP)";
		if (player.isHCIronMan())
			gameMode = Colors.cyan + "HC Ironman (x" + Settings.IRONMAN_XP + " XP)";

		if (player.isDonator())
			status = "<col=8B4513>Bronze";
		if (player.isExtremeDonator())
			status = "<col=ffffff>Silver";
		if (player.isLegendaryDonator())
			status = "<col=e6e600>Gold";
		if (player.isSupremeDonator())
			status = "<col=008000>Platinum";
		if (player.is420Donator())
			status = "<col=000000><shad=00ff00>420";
		if (player.isUltimateDonator())
			status = "<col=00FFFF>Diamond";
		if (player.isSponsorDonator())
			status = "<shad=FFD700><col=FF8C00>Sponsor</shad>";
		if (player.isYoutube())
			status = "<shad=ff0000><col=000000>Youtube</shad>";
		if (player.isDev())
			status = "<shad=0000FF><col=00BFFF>Dev</shad>";
		if (player.isSupport())
			title = "<col=83A6F2>Support</col>";
		if (rights == 13)
			title = "<col=B5B5B5>Support</col>";
		if (rights == 1)
			title = "<col=B5B5B5>Moderator</col>";
		if (rights == 2)
			title = "<col=1589FF>Administrator</col>";

		if (pName.equalsIgnoreCase("Zeus"))
			title = "<col=D400FF>Owner</col>";

		// DAILY TASK
		String TaskType = player.getDailyTaskManager().getCurrentTask() == null ? ""
				: player.getDailyTaskManager().getCurrentTask().toString();

		long milliseconds = World.getWellActiveTime() - Utils.currentTimeMillis();
		long seconds = milliseconds / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		long days = hours / 24;
		// wellofGoodwill timer

		/**
		 * Start sending the tab
		 */
		player.getPackets().sendIComponentText(930, 10, Colors.white + Settings.SERVER_NAME + " Noticeboard</col>");

		player.getPackets().sendIComponentText(930, 16, Colors.red + "-- Server --<br>"

				+ Colors.white + "- Server time: " + Colors.green + time("hh:mm:ss a") + "<br>" + Colors.white
				+ "- Players online: " + Colors.green + World.getPlayersOnline() + "<br>" + Colors.white
				+ "- Double XP: " + Colors.green
				+ (World.isWeekend() ? "Active (weeknd)"
						: (World.isWellActive() ? "Active (well)" : Colors.red + "Not Active"))
				+ "<br>" + Colors.white + "- Time Left: " + Colors.green + ""
				+ (World.isWeekend() ? "Unlimited"
						: (World.isWellActive() ? ((hours % 24) + "h:" + (minutes % 60) + "m:" + (seconds % 60) + "s")
								: Colors.red + "0"))

				+ "<br>"
				+ (World.getLastVoter() != null
						? "<br>" + Colors.white + " -- Last voter: " + Colors.green + World.getLastVoter() + "<br>"
						: "")
				+ "<br>"

				+ Colors.red + "-- Player --<br>" + Colors.white + "- Name: " + Colors.green
				+ Utils.formatPlayerNameForDisplay(player.getDisplayName()) + "<br>" + Colors.white + "- Rank: " + title
				+ (gameMode != null ? "<br>" + Colors.white + "- Mode: " + gameMode : "")
				+ (player.isDonator() || player.isYoutube() ? "<br>" + Colors.white + "- Status: " + status : "")
				+ "<br>" + Colors.white + "- Time played: " + Colors.green + Utils.getTimePlayed(player.getTimePlayed())
				+ "<br>" + Colors.white + "- Referral Points: " + Colors.green + player.getReferralPoints() + "<br>"
				+ Colors.white + "- Quest Points: " + Colors.green + player.getQuestPoints() + "<br>" + Colors.white
				+ "- Zombie Points: " + Colors.green + player.getZombiesMinigamePoints()
				+ (player.isDoubleXp() ? "<br>" + Colors.white + "- EXP Boost: " + Colors.green
						+ Utils.getFormattedNumber((int) player.getTimeLeft()) + " minutes" : "")
				+(player.Subscribed() ? "<br><br>" + Colors.red + " -- MemberShip Status --<br>" : "")
				+ (player.looterspack ? Colors.white+"- Looters Perk: "
						+Colors.green+Utils.getDaysRemaining(player.getLooterPackSubLong())+"<br>" : "")
				+ (player.skillerspack ? Colors.white+"- Skillers Perk: "
						+Colors.green+Utils.getDaysRemaining(player.getSkillerPackSubLong())+"<br>" : "")
				+ (player.utilitypack ? Colors.white+"- Utility Perk: "
						+Colors.green+Utils.getDaysRemaining(player.getUtilityPackSubLong())+"<br>" : "")
				+ (player.combatantpack ? Colors.white+"- Combat Perk: "
						+Colors.green+Utils.getDaysRemaining(player.getCombatPackSubLong())+"<br>" : "")
				+ (player.completepack ? Colors.white+"- Complete Perk: "
						+Colors.green+Utils.getDaysRemaining(player.getCompletePackSubLong())+"<br>" : "")
				+ "<br><br>" + Colors.orange + " -- Divination --<br>" + Colors.white + " - Level: " + Colors.green
				+ player.getSkills().getLevelForXp(Skills.DIVINATION) + "<br>" + Colors.white + " - Exp: "
				+ Colors.green + Utils.getFormattedNumber((int) player.getSkills().getXp(Skills.DIVINATION))
				+ "<br><br>" + Colors.orange + " -- Invention --<br>" + Colors.white + " - Level: " + Colors.green
				+ player.getSkills().getLevelForXp(Skills.INVENTION) + "<br>" + Colors.white + " - Exp: " + Colors.green
				+ Utils.getFormattedNumber((int) player.getSkills().getXp(Skills.INVENTION)) + "<br><br>" + Colors.white
				+ Colors.red + "-- Statistics --<br>" + Colors.white + "- Donated: " + Colors.green + "$"
				+ Utils.getFormattedNumber(player.getMoneySpent()) + "<br>" + Colors.white + "- Vote points: "
				+ Colors.green + Utils.getFormattedNumber(player.getVotePoints()) + "<br>" + Colors.white
				+ "- Quest points: " + Colors.green + Utils.getFormattedNumber(player.getQuestPoints()) + "<br>"
				+ Colors.white + "- Loyalty points: " + Colors.green
				+ Utils.getFormattedNumber(player.getLoyaltyPoints()) + "<br>" + Colors.white + "- Trivia points: "
				+ Colors.green + Utils.getFormattedNumber(player.getTriviaPoints()) + "<br>" + Colors.white
				+ "- PC points: " + Colors.green + Utils.getFormattedNumber(player.getPestPoints()) + "<br>"
				+ Colors.white + "- SW zeals: " + Colors.green + Utils.getFormattedNumber(player.getZeals()) + "<br>"
				+ Colors.white + "- Dung tokens: " + Colors.green
				+ Utils.getFormattedNumber(player.getDungeoneeringTokens()) + "<br>" + Colors.white + "- DT Kills: "
				+ Colors.green + Utils.getFormattedNumber(player.getDominionTower().getKilledBossesCount())

				+ "<br><br>" + Colors.red + "-- PvP Information --" + "<br>" + Colors.white + "- Pk Points: "
				+ Colors.green + Utils.getFormattedNumber(player.getPkPoints()) + "<br>" + Colors.white
				+ "- Killstreak Points: " + Colors.green + Utils.getFormattedNumber(player.getTotalKillStreakPoints())
				+ "<br>" + Colors.white + "- Kills: " + Colors.green + Utils.getFormattedNumber(player.getKillCount())
				+ "<br>" + Colors.white + "- Deaths: " + Colors.green + Utils.getFormattedNumber(player.getDeathCount())
				+ "<br>" + Colors.white + "- Killstreak: " + Colors.green
				+ Utils.getFormattedNumber(player.getKillStreak())

				+ "<br><br>" + Colors.red + "-- Slayer Information --"
				+ (player.getSlayerManager().getCurrentTask() != null ? "<br>" + Colors.white + "- Task: "
						+ Colors.green + slayerTask + "" + "<br>" + Colors.white + "- Kills Left: " + Colors.green
						+ Utils.getFormattedNumber(player.getSlayerManager().getCount()) : "")
				+ "<br>" + Colors.white + "- Slayer Points: " + Colors.green
				+ Utils.getFormattedNumber(player.getSlayerManager().getSlayerPoints())

				+ "<br><br>" + Colors.red + "-- Reaper Information --"
				// XXX I've added check for if is not null, always check if it's
				// not null else RIP >.>
				+ (player.getContract() != null
						? !player.getContract().hasCompleted() ? "<br>" + Colors.white + "- Contract: <br>"
								+ Colors.green + "-- '" + npcName + "' --<br>" + Colors.white + "- Kills Left: "
								+ Colors.green + Utils.getFormattedNumber(player.getContract().getKillAmount()) + "<br>"
								+ Colors.white + "- Reward Amount: " + Colors.green
								+ Utils.getFormattedNumber(player.getContract().getRewardAmount()) : ""
						: "")
				+ "<br>" + Colors.white + "- Reaper Points: " + Colors.green
				+ Utils.getFormattedNumber(player.getReaperPoints()) + "<br>" + Colors.white + "- Total kills: "
				+ Colors.green + Utils.getFormattedNumber(player.getTotalKills()) + "<br>" + Colors.white
				+ "- Completed Contracts: " + Colors.green + Utils.getFormattedNumber(player.getTotalContract())

				+ "<br><br>" + Colors.red + "-- Instanced PVP Information"
				// XXX I've added check for if is not null, always check if it's
				// not null else RIP >.>
				+ (player.getControlerManager().getControler() != null
						&& player.getControlerManager().getControler() instanceof InstancedPVPControler
								? "<br>" + Colors.white + "- Wilderness level: " + Colors.green
										+ Utils.getFormattedNumber(InstancedPVPControler.getWildLevel(player))
								: "")
				+ "<br>" + Colors.white + "- Instanced PVP Points: " + Colors.green
				+ Utils.getFormattedNumber(player.getInstancedPVPPoints()) + "<br>" + Colors.white + "- Kill Streak: "
				+ Colors.green + Utils.getFormattedNumber(player.getInstancedPVPKillStreak())

				+ "<br><br>" + Colors.red + "-- Weekly Scores --" + "<br>" + Colors.white + "- Time Online: "
				+ Colors.green + Utils.getTimeToString(player.getTimePlayedWeekly()) + "<br>" + Colors.white
				+ "- Vote Count: " + Colors.green + Utils.getFormattedNumber(player.getVoteCountWeekly()) + "<br>"
				+ Colors.white + "- Donation Amount: " + Colors.green + "$"
				+ Utils.getFormattedNumber(player.getDonationAmountWeekly())

				+ "<br><br>" + Colors.red + "-- Daily Task --" + "<br>" + Colors.white + "- Task Type: " + Colors.green
				+ TaskType + "<br>" + Colors.white + "- Task: " + Colors.green
				+ ItemDefinitions.getItemDefinitions(player.getDailyTaskManager().getProductId()).getName() + "<br>"
				+ Colors.white + "- Task Count: " + Colors.green + player.getDailyTaskManager().getAmountLeft()

		);
	}

	/**
	 * Gets the current systems time.
	 * 
	 * @param dateFormat
	 *            The format to use.
	 * @return The formatted time.
	 */
	public static String time(String dateFormat) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(cal.getTime());
	}
}