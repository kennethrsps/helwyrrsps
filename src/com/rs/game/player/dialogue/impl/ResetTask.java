package com.rs.game.player.dialogue.impl;

import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.Utils;

/**
 * Handles the Boss Teleport dialogue.
 * @author Zeus
 */
public class ResetTask extends Dialogue {
	int npcId;
    @Override
    public void start() {
       	stage = 2;
		sendOptionsDialogue("Reset SlayerTask", 
			"yes",
			"no");
	
	
    	
    	
		
    }
    
    @Override
    public void run(int interfaceId, int componentId) {
    	switch (stage) {
    	case -1:
    		if (componentId == OPTION_1) {
    			stage = 2;
    			sendOptionsDialogue("Reset Task", 
    				"Yes",
    				"No");
    		
    		}
    		break;
    	case 2:
    		if (componentId == OPTION_1) {
    			String tUsername = Utils.formatPlayerNameForDisplay(player.getUsername());
    			if (player.getTask() != null) {
    				SerializableFilesManager.savePlayer(player);
    				player.sendMessage(
    		    			"Your <col=ff0000>Slayer Task</col> has been Reset.");
    				player.setTask(null);
    				player.getInventory().deleteItem(27360,1);
    			} else
    				player.sendMessage(Colors.red + tUsername + " you do not have an active Slayer Task.");
    			
    			player.setTask(null);
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