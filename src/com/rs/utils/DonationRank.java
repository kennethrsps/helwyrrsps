package com.rs.utils;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.player.Player;

public final class DonationRank implements Serializable {

	private static final long serialVersionUID = 5403480618483552509L;

	private String username;
	private int amountDonated, totalLevel;
	private String XPMode;

	private static DonationRank[] ranks;

	private static final String PATH = "data/hiscores/donation.chry";

	public DonationRank(Player player) {
		this.username = player.getUsername();
		this.amountDonated = player.getMoneySpent();
		this.XPMode = player.getXPMode();
		this.totalLevel = player.getSkills().getTotalLevel(player);
	}

	public static void init() {
		File file = new File(PATH);
		if (file.exists())
			try {
				ranks = (DonationRank[]) SerializableFilesManager.loadSerializedFile(file);
				return;
			} catch (Throwable e) {
				Logger.handle(e);
			}
		ranks = new DonationRank[10];
	}

	public static final void save() {
		try {
			SerializableFilesManager.storeSerializableClass(ranks, new File(PATH));
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}
	
	private static String playerOnline(DonationRank player) {
		if (World.getPlayer(player.username) != null) {
			return Colors.green+"Online";
		}
		return Colors.red+"Offline";
	}

	public static void showRanks(Player player) {
		player.getInterfaceManager().sendInterface(1158);
		player.getPackets().sendIComponentText(1158, 74, Colors.white+"Top 10 Respected Donators</col>");
		int count = 0;
		for (DonationRank rank : ranks) {
			if (rank == null)
				return;
			player.getPackets().sendIComponentText(1158, 9 + count * 5,
					Utils.formatPlayerNameForDisplay(rank.username));
			player.getPackets().sendIComponentText(
					1158,
					10 + count * 5,
					"Total level: "+Utils.getFormattedNumber(rank.totalLevel)+"; Game Mode: "+rank.XPMode+".");
			player.getPackets().sendIComponentText(1158, 11 + count * 5, playerOnline(rank));
			count++;
		}
	}

	public static void sort() {
		Arrays.sort(ranks, new Comparator<DonationRank>() {
			@Override
			public int compare(DonationRank arg0, DonationRank arg1) {
				if (arg0 == null)
					return 1;
				if (arg1 == null)
					return -1;
				if (arg0.amountDonated < arg1.amountDonated)
					return 1;
				else if (arg0.amountDonated > arg1.amountDonated)
					return -1;
				else
					return 0;
			}

		});
	}

	public static void checkRank(Player player) {
		if (player.getRights() == 2) {
			return;
		}
		int amountDonated = player.getMoneySpent();
		for (int i = 0; i < ranks.length; i++) {
			DonationRank rank = ranks[i];
			if (rank == null)
				break;
			if (rank.username.equalsIgnoreCase(player.getUsername())) {
				ranks[i] = new DonationRank(player);
				sort();
				return;
			}
		}
		for (int i = 0; i < ranks.length; i++) {
			DonationRank rank = ranks[i];
			if (rank == null) {
				ranks[i] = new DonationRank(player);
				sort();
				return;
			}
		}
		for (int i = 0; i < ranks.length; i++) {
			if (ranks[i].amountDonated < amountDonated) {
				ranks[i] = new DonationRank(player);
				sort();
				return;
			}
		}
	}
	
	/**
	 * Gets the Top 3 Donator ranks.
	 * @return the top 3 player names as String.
	 */
	public static String getDonatorTop() {
		short count = 0;
		String top1 = null, top2 = null, top3 = null;
		for (DonationRank rank : ranks) {
		    if (rank == null)
		    	return null;
		    switch (count) {
		    case 0:
		    	top1 = Utils.formatPlayerNameForDisplay(rank.username);
		    	break;
		    case 1:
		    	top2 = Utils.formatPlayerNameForDisplay(rank.username);
		    	break;
		    case 2:
		    	top3 = Utils.formatPlayerNameForDisplay(rank.username);
		    	break;
		    }
		    count ++;
		}
		return "Top 3 "+Settings.SERVER_NAME+" contributors  -  "+top1+"  -  "+top2+"  -  "+top3+".";
	}
}