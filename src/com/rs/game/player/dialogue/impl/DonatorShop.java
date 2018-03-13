package com.rs.game.player.dialogue.impl;

import java.awt.Color;

import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;
import com.rs.utils.ShopsHandler;

/**
 * Class used to handle the WiseOldMan dialogue.
 * 
 * @author Zeus
 */
public class DonatorShop extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Choose an Option", "Donator Shop", "Silver Members Shop", "Gold Members Shop",
				"Platinum Members Shop", "Close");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			switch (componentId) {
			case OPTION_1:
				finish();
				ShopsHandler.openShop(player, 67);
				break;
			case OPTION_2:
				if (player.getMoneySpent() >= 50) {
					ShopsHandler.openShop(player, 68);
					finish();
					return;
				}
				finish();
				player.sendMessage(Colors.red + "Only Silver Members ++ can open this shop.");

				break;
			case OPTION_3:
				if (player.getMoneySpent() >= 100) {
					ShopsHandler.openShop(player, 69);
					finish();
					return;
				}
				finish();
				player.sendMessage(Colors.red + "Only Gold Members ++ can open this shop.");
				break;
			case OPTION_4:
				if (player.getMoneySpent() >= 250) {
					ShopsHandler.openShop(player, 70);
					finish();
					return;
				}
				finish();
				player.sendMessage(Colors.red + "Only Platinum Members ++ can open this shop.");
				break;
			case OPTION_5:
				finish();
				break;
			}
		}
	}
	@Override
	public void finish() {
		player.getInterfaceManager().closeChatBoxInterface();
	}

}