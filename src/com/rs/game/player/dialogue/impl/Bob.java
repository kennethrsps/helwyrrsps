package com.rs.game.player.dialogue.impl;

import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.ShopsHandler;

public class Bob extends Dialogue {

    private int npcId;

    @Override
    public void start() {
    	npcId = (Integer) parameters[0];
    	sendOptionsDialogue("Select an Option", 
    			"Can you repair my items for me?", "Open skilling tools shop.");
    }

    @Override
    public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
		    switch (componentId) {
		    	case OPTION_1:
		    		sendPlayerDialogue(9827, "Can you repair my items for me?");
		    		stage = 0;
		    		break;
		    	case OPTION_2:
		    		finish();
		    		ShopsHandler.openShop(player, 16);
		    		break;
		    	}
		    	break;
		case 0:
		    sendNPCDialogue(npcId, 9827,
			    "Of course I can, though the materials may cost you. Just hand me the item "
			    + "and I'll have a look.");
		    player.sendMessage("Simply use the broken item on Bob if you wish to repair it!");
		    stage = 1;
		    break;
		case 1:
		   	finish();
		    break;
		}
    }

    @Override
    public void finish() { player.getInterfaceManager().closeChatBoxInterface(); }
}