package com.rs.game.player.dialogue.impl;

import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.DTRank;
import com.rs.utils.DonationRank;
import com.rs.utils.PkRank;
import com.rs.utils.VoteHiscores;

/**
 * Handles the In-game Scoreboard, used for Hiscores.
 * @author Zeus
 */
public class ScoreboardD extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Choose an Option",
				"Vote Hiscores",
				"Donator Hiscores",
				"Dominion Tower fighters",
				"Player killing Ranks table");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			switch (componentId) {
			case OPTION_1:
				finish();
				VoteHiscores.showRanks(player);
				break;
			case OPTION_2:
				finish();
				DonationRank.showRanks(player);
				break;
			case OPTION_3:
				finish();
				DTRank.showRanks(player);
				break;
			case OPTION_4:
				finish();
				PkRank.showRanks(player);
				break;
			}
			break;
		}
	}

	@Override
	public void finish() { player.getInterfaceManager().closeChatBoxInterface(); }
}