package com.rs.game.player.dialogue.impl;

import com.rs.game.player.dialogue.Dialogue;

public class Goliaths extends Dialogue {

	private int gloves = 22358;

	@Override
	public void start() {
		sendOptionsDialogue("Which color would you like?", "Black", "White", "Yellow", "Red");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			if (componentId == OPTION_1) {
				removeGloves();
				player.getInventory().addItem(gloves, 1);
				end();
			}
			if (componentId == OPTION_2) {
				removeGloves();
				player.getInventory().addItem(gloves + 1, 1);
				end();
			}
			if (componentId == OPTION_3) {
				removeGloves();
				player.getInventory().addItem(gloves + 2, 1);
				end();
			}
			if (componentId == OPTION_4) {
				removeGloves();
				player.getInventory().addItem(gloves + 3, 1);
				end();
			}
		}
	}

	@Override
	public void finish() { }
	
	private void removeGloves() {
		player.getInventory().deleteItem(22358, 1);
		player.getInventory().deleteItem(22359, 1);
		player.getInventory().deleteItem(22360, 1);
		player.getInventory().deleteItem(22361, 1);
	}
}
