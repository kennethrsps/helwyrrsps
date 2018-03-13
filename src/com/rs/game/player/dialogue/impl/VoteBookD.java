package com.rs.game.player.dialogue.impl;

import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.item.Item;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/**
 * Handles opening of Vote Books.
 * @author Zeus
 */
public class VoteBookD extends Dialogue {

	@Override
	public void start() {
		sendItemDialogue(11640, 1, "Opening this Vote Book will give you a reward of your choice. To get "
				+ "more Vote Books simply ;;vote for Helwyr on the most popular Gaming Top Sites.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(Colors.cyan+"Select your Reward", 
					"No coins and +25% bonus experience for 1 Hour.", 
					"250,000 coins and +25% bonus experience for 45 Minutes.",
					"500,000 coins and +25% bonus experience for 30 Minutes.",
					"750,000 coins and +25% bonus experience for 15 minutes.",
					"1,000,000 coins and no bonus experience.");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				handleReward(3600000, 0);
				sendDialogue("You've chosen no coins and +25% bonus experience for 1 hour.");
				stage = 1;
				break;
			case OPTION_2:
				handleReward(2700000, 250000);
				sendDialogue("You've chosen 250,000 coins and +25% bonus experience for 45 Minutes.");
				stage = 1;
				break;
			case OPTION_3:
				handleReward(1800000, 500000);
				sendDialogue("You've chosen 500,000 coins and +25% bonus experience for 30 Minutes.");
				stage = 1;
				break;
			case OPTION_4:
				handleReward(900000, 750000);
				sendDialogue("You've chosen 750,000 coins and +25% bonus experience for 15 minutes.");
				stage = 1;
				break;
			case OPTION_5:
				handleReward(0, 1000000);
				sendDialogue("You've chosen 1,000,000 coins and no bonus experience.");
				stage = 1;
				break;
			}
			break;
			
		case 1:
			finish();
			break;
		}
	}

	@Override
	public void finish() { player.getInterfaceManager().closeChatBoxInterface(); }
	
	/**
	 * Handles the chosen vote book reward.
	 * @param xpBonus The xpBonus in millisecs.
	 * @param coins The amount of coins.
	 */
	private boolean handleReward(long xpBonus, int coins) {
		if (player.getInventory().containsItem(new Item(11640, 1))) {
			if (xpBonus > 0)
				player.setDoubleXpTimer(Utils.currentTimeMillis() + xpBonus);
			if (coins > 0)
				player.addMoney(coins);
			player.getInventory().deleteItem(new Item(11640, 1));
			return true;
		}
		return false;
	}
}