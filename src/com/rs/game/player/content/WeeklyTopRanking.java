package com.rs.game.player.content;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.TimeZone;

import com.rs.game.player.Player;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.Utils;

public class WeeklyTopRanking {

	private static TimeOnlineRank[] TimeOnlineRanks;
	private static VoteRank[] VoteRanks;
	private static DonationRank[] DonationRanks;
	private static int currentDay;

	public static void init() {
		TimeOnlineRanks = SerializableFilesManager.loadTimeOnlineRanks();
		VoteRanks = SerializableFilesManager.loadVoteRanks();
		DonationRanks = SerializableFilesManager.loadDonationRanks();
		if (TimeOnlineRanks == null)
			TimeOnlineRanks = new TimeOnlineRank[10];
		if (VoteRanks == null)
			VoteRanks = new VoteRank[10];
		if (DonationRanks == null)
			DonationRanks = new DonationRank[10];
		currentDay = SerializableFilesManager.loadCurrentDay();
	}

	public static void process() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("EST"));
		if (currentDay != calendar.get(Calendar.DAY_OF_WEEK)) {
			if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
				TimeOnlineRanks = new TimeOnlineRank[10];
				VoteRanks = new VoteRank[10];
				DonationRanks = new DonationRank[10];
			}
			currentDay = calendar.get(Calendar.DAY_OF_WEEK);
		}
	}

	public static final void save() {
		if (TimeOnlineRanks == null)
			TimeOnlineRanks = new TimeOnlineRank[10];
		if (VoteRanks == null)
			VoteRanks = new VoteRank[10];
		if (DonationRanks == null)
			DonationRanks = new DonationRank[10];
		SerializableFilesManager.saveTimeOnlineRanks(TimeOnlineRanks);
		SerializableFilesManager.saveVoteRanks(VoteRanks);
		SerializableFilesManager.saveDonationRanks(DonationRanks);
		SerializableFilesManager.saveCurrentDay(currentDay);
	}

	public static void showRanks(Player player, int type) {
		for (int i = 10; i < 310; i++)
			player.getPackets().sendIComponentText(275, i, "");
		for (int i = 0; i < 10; i++) {
			if ((type == 0 && TimeOnlineRanks[i] == null) || (type == 1 && VoteRanks[i] == null)
					|| (type == 2 && DonationRanks[i] == null))
				break;
			String text;
			if (i == 0)
				text = "<col=ff9900>";
			else if (i < 2)
				text = "<col=ff0000>";
			else if (i == 3)
				text = "<col=38610B>";
			else
				text = "<col=000000>";
			player.getPackets().sendIComponentText(275, i + 10, text + "Top " + (i + 1) + " - "
					+ Utils.formatPlayerNameForDisplay((type == 0 ? TimeOnlineRanks[i].getUsername()
							: type == 1 ? VoteRanks[i].getUsername() : DonationRanks[i].getUsername()))
					+ " - "
					+ (type == 0 ? ("Time Online : " + Utils.getTimeToString(TimeOnlineRanks[i].getTimeOnline()))
							: type == 1 ? ("Vote Count : " + Utils.getFormattedNumber(VoteRanks[i].getVoteCount()))
									: ("Donation Amount : $"
											+ Utils.getFormattedNumber(DonationRanks[i].getDonationAmount()))));
		}
		player.getPackets().sendIComponentText(275, 1,
				"Top 10 " + (type == 0 ? "Time Online" : type == 1 ? "Vote Count" : "Donation Amount") + " weekly");
		player.getInterfaceManager().sendInterface(275);
	}

	public static void sortTimeOnlineRanks() {
		Arrays.sort(TimeOnlineRanks, new Comparator<TimeOnlineRank>() {
			@Override
			public int compare(TimeOnlineRank arg0, TimeOnlineRank arg1) {
				if (arg0 == null)
					return 1;
				if (arg1 == null)
					return -1;
				if (arg0.timeOnline < arg1.timeOnline)
					return 1;
				else if (arg0.timeOnline > arg1.timeOnline)
					return -1;
				else
					return 0;
			}

		});
	}

	public static void checkTimeOnlineRank(Player player) {
		long timeOnline = player.getRights() == 2 ? 0 : player.getTimePlayedWeekly();
		for (int i = 0; i < TimeOnlineRanks.length; i++) {
			TimeOnlineRank rank = TimeOnlineRanks[i];
			if (rank == null)
				break;
			if (rank.username.equalsIgnoreCase(player.getUsername()) && player.getRights() < 2) {
				TimeOnlineRanks[i] = new TimeOnlineRank(player);
				sortTimeOnlineRanks();
				return;
			}
		}
		for (int i = 0; i < TimeOnlineRanks.length; i++) {
			TimeOnlineRank rank = TimeOnlineRanks[i];
			if (rank == null && player.getRights() < 2) {
				TimeOnlineRanks[i] = new TimeOnlineRank(player);
				sortTimeOnlineRanks();
				return;
			}
		}
		for (int i = 0; i < TimeOnlineRanks.length; i++) {
			if (TimeOnlineRanks[i] != null && TimeOnlineRanks[i].timeOnline < timeOnline && player.getRights() < 2) {
				TimeOnlineRanks[i] = new TimeOnlineRank(player);
				sortTimeOnlineRanks();
				return;
			}
		}
	}

	public static void sortVoteRanks() {
		Arrays.sort(VoteRanks, new Comparator<VoteRank>() {
			@Override
			public int compare(VoteRank arg0, VoteRank arg1) {
				if (arg0 == null)
					return 1;
				if (arg1 == null)
					return -1;
				if (arg0.voteCount < arg1.voteCount)
					return 1;
				else if (arg0.voteCount > arg1.voteCount)
					return -1;
				else
					return 0;
			}

		});
	}

	public static void checkVoteRank(Player player) {
		int voteCount = player.getRights() == 2 ? 0 : player.getVoteCountWeekly();
		for (int i = 0; i < VoteRanks.length; i++) {
			VoteRank rank = VoteRanks[i];
			if (rank == null)
				break;
			if (rank.username.equalsIgnoreCase(player.getUsername()) && player.getRights() < 2) {
				VoteRanks[i] = new VoteRank(player);
				sortVoteRanks();
				return;
			}
		}
		for (int i = 0; i < VoteRanks.length; i++) {
			VoteRank rank = VoteRanks[i];
			if (rank == null && player.getRights() < 2) {
				VoteRanks[i] = new VoteRank(player);
				sortVoteRanks();
				return;
			}
		}
		for (int i = 0; i < VoteRanks.length; i++) {
			if (VoteRanks[i] != null && VoteRanks[i].voteCount < voteCount && player.getRights() < 2) {
				VoteRanks[i] = new VoteRank(player);
				sortVoteRanks();
				return;
			}
		}
	}

	public static void sortDonationRanks() {
		Arrays.sort(DonationRanks, new Comparator<DonationRank>() {
			@Override
			public int compare(DonationRank arg0, DonationRank arg1) {
				if (arg0 == null)
					return 1;
				if (arg1 == null)
					return -1;
				if (arg0.donationAmount < arg1.donationAmount)
					return 1;
				else if (arg0.donationAmount > arg1.donationAmount)
					return -1;
				else
					return 0;
			}

		});
	}

	public static void checkDonationRank(Player player) {
		int donationAmount = player.getRights() == 2 ? 0 : player.getDonationAmountWeekly();
		for (int i = 0; i < DonationRanks.length; i++) {
			DonationRank rank = DonationRanks[i];
			if (rank == null)
				break;
			if (rank.username.equalsIgnoreCase(player.getUsername()) && player.getRights() < 2) {
				DonationRanks[i] = new DonationRank(player);
				sortDonationRanks();
				return;
			}
		}
		for (int i = 0; i < DonationRanks.length; i++) {
			DonationRank rank = DonationRanks[i];
			if (rank == null && player.getRights() < 2) {
				DonationRanks[i] = new DonationRank(player);
				sortDonationRanks();
				return;
			}
		}
		for (int i = 0; i < DonationRanks.length; i++) {
			if (DonationRanks[i] != null && DonationRanks[i].donationAmount < donationAmount && player.getRights() < 2) {
				DonationRanks[i] = new DonationRank(player);
				sortDonationRanks();
				return;
			}
		}
	}

	public static class TimeOnlineRank implements Serializable {

		private static final long serialVersionUID = 1374198656035761578L;
		private String username;
		private long timeOnline;

		public TimeOnlineRank(Player player) {
			this.username = player.getUsername();
			this.timeOnline = player.getTimePlayedWeekly();
		}

		public String getUsername() {
			return username;
		}

		public long getTimeOnline() {
			return timeOnline;
		}
	}

	public static class VoteRank implements Serializable {

		private static final long serialVersionUID = 6586073987522525887L;
		private String username;
		private int voteCount;

		public VoteRank(Player player) {
			this.username = player.getUsername();
			this.voteCount = player.getVoteCountWeekly();
		}

		public String getUsername() {
			return username;
		}

		public int getVoteCount() {
			return voteCount;
		}
	}

	public static class DonationRank implements Serializable {

		private static final long serialVersionUID = -4377661366781875709L;
		private String username;
		private int donationAmount;

		public DonationRank(Player player) {
			this.username = player.getUsername();
			this.donationAmount = player.getDonationAmountWeekly();
		}

		public String getUsername() {
			return username;
		}

		public int getDonationAmount() {
			return donationAmount;
		}
	}

}
