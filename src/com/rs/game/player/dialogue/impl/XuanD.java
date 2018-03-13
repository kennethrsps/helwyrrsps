package com.rs.game.player.dialogue.impl;

import com.rs.Settings;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.ShopsHandler;

/**
 * Class used to handle the Xuan's dialogue.
 * @author Zeus
 */
public class XuanD extends Dialogue {
	
	/**
	 * Ints representing the NPC id and the stage of the dialogue.
	 */
	int npcId, stage;

    @Override
    public void start() {
    	npcId = (Integer) parameters[0];
    	stage = (Integer) parameters[1];
    	if (stage == 1) {
    		sendNPCDialogue(npcId, NORMAL, "Hello, what can I do for you?");
    		stage = 1;
    	} else {
        	sendOptionsDialogue("Choose an Option", "Regular Aura shop", 
        			"Greater Aura shop", "Master Aura shop", "Supreme Aura shop", "Legendary Aura shop");
        	stage = 4;
    	}
    }

    @Override
    public void run(int interfaceId, int componentId) {
    	switch (stage) {
    	case 1:
    		sendPlayerDialogue(NORMAL, "Hello.. what could you do for me..?");
    		stage = 2;
    		break;
    	case 2:
    		sendNPCDialogue(npcId, NORMAL, "I offer a wide variety of Auras that you can use to help your "
    				+ "game-play throughout "+Settings.SERVER_NAME+". Donators also have access to "
    						+ "higher-tier auras.");
    		stage = 3;
    		break;
    	case 3:
        	sendOptionsDialogue("Choose an Option", "Regular Aura shop", 
        			"Greater Aura shop", "Master Aura shop", "Supreme Aura shop");
        	stage = 4;
    		break;
    	case 4:
    		switch (componentId) {
    		case OPTION_1:
        		finish();
        		ShopsHandler.openShop(player, 53);
    			break;
    		case OPTION_2:
        		finish();
        		ShopsHandler.openShop(player, 54);
    			break;
    		case OPTION_3:
    			if (!player.isDonator()) {
    				sendNPCDialogue(npcId, SAD, "I'm sorry, but master tier auras are only available to Bronze members and higher..");
    				stage = 3;
    				return;
    			}
        		finish();
        		ShopsHandler.openShop(player, 55);
    			break;
    		case OPTION_4:
    			if (!player.isLegendaryDonator()) {
    				sendNPCDialogue(npcId, SAD, "I'm sorry, but supreme tier auras are only available to Gold members and higher..");
    				stage = 3;
    				return;
    			}
        		finish();
        		ShopsHandler.openShop(player, 56);
    			break;
    		case OPTION_5:
    			if (!player.isUltimateDonator()) {
    				sendNPCDialogue(npcId, SAD, "I'm sorry, but legendary tier auras are only available to Diamond members..");
    				stage = 3;
    				return;
    			}
        		finish();
        		ShopsHandler.openShop(player, 57);
    			break;
    		}
    		break;
    	}
    }

    @Override
    public void finish() { player.getInterfaceManager().closeChatBoxInterface(); }
}