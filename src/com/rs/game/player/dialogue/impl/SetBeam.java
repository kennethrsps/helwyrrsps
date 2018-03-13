package com.rs.game.player.dialogue.impl;

import com.rs.game.player.content.AccountInterfaceManager;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/**
 * Used for handling the Player Settings dialogue.
 * @author Zeus
 */
public class SetBeam extends Dialogue {

    @Override
    public void start() {
    	sendOptionsDialogue(Colors.cyan+"Loot Beam Settings", 
    			"Loot Beam: "+(player.hasLootBeam() ? Colors.green+"Enabled" : Colors.red+"Disabled")+"</col>.", "Trigger price: "+Colors.red+Utils.getFormattedNumber(player.setLootBeam)+"</col>.");
    }

    @Override
    public void run(int interfaceId, int componentId) {
    	switch (stage) {
    	case -1:
    		switch (componentId) {
    		case OPTION_1:
    			finish();
    			player.toggleLootBeam();
    			AccountInterfaceManager.sendInterface(player);
    			break;
    		case OPTION_2:
    			finish();
    			player.getTemporaryAttributtes().put("loot_beam", Boolean.TRUE);
				player.getPackets().sendRunScript(108, new Object[] {
		    		"Current drop price to trigger Loot beam: "+Utils.getFormattedNumber(player.setLootBeam)+"."+
		    		"<br>What price would you like to set?" });
    			break;
    		}
    		break;
    	}
    }

    @Override
    public void finish() { player.getInterfaceManager().closeChatBoxInterface(); }

}