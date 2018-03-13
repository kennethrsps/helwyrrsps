package com.rs.game.player.dialogue.impl;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.player.content.contracts.ContractHandler;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.Utils;

/**
 * Handles the Boss Teleport dialogue.
 * 
 * @author Zeus
 */
public class ResetGrim extends Dialogue {
	int npcId;

	@Override
	public void start() {
		stage = 2;
		sendOptionsDialogue("Reset ReaperTask", "yes", "no");

	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			if (componentId == OPTION_1) {
				stage = 2;
				sendOptionsDialogue("Reset ReaperTask", "Yes", "No");

			}
			break;
		case 2:
			if (componentId == OPTION_1) {
				String tUsername = Utils.formatPlayerNameForDisplay(player.getUsername());
				if (player.getContract() == null) {
					sendNPCDialogue(14386, CROOKED_HEAD, "You don't have an active contract, ",
							"collect a new one from me when you're ready.");
					stage = 99;

				} else
					player.sendMessage("Your <col=ff0000>Reaper Task</col> has been Reset.");
				player.getInventory().deleteItem(11780, 1);
				player.setContract(null);

				end();
			}
			if (componentId == OPTION_2) {
				end();
			}
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}
}