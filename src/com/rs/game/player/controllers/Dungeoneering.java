package com.rs.game.player.controllers;

import com.rs.Settings;
import com.rs.game.Animation;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/**
 * Handles the Dungeoneering controller.
 * @author Zeus
 */
public class Dungeoneering extends Controller {

    @Override
    public void start() { 
    	sendInterfaces();
    }

    @Override
    public void sendInterfaces() {
    	player.getInterfaceManager().sendOverlay(988, false);
    	player.getPackets().sendIComponentText(988, 0, ""+player.dungKills);
    	player.getPackets().sendIComponentText(988, 1, "Kills:");
    }

    @Override
    public boolean login() {
    	return false;
    }

    @Override
    public boolean logout() {
    	return false;
    }
    
    @Override
    public void magicTeleported(int teleType) {
    	player.inDungeoneering = false;
    	player.getControlerManager().forceStop();
    }
    
    /**
     * Gets the required kills amount to enter next room.
     * @return the amount as Integer.
     */
    private int getKcRequired() {
    	if (player.getPerkManager().dungeon)
    		return 5;
    	return 15;
    }
    
    /**
     * Handles Dungeoneering magical barriers.
     * @param destX The X coordinate to walk to.
     * @param destY The Y coordinate to walk to.
     * @param requireKC if requires killcount to pass.
     */
    private void handleDoor(int destX, int destY, boolean requireKC, int requireLVL) {
    	if (requireKC) {
    		if (player.getSkills().getLevel(Skills.DUNGEONEERING) < requireLVL) {
    			player.sendMessage("You'll need a Dungeoneering level of "+requireLVL+" to enter this room.");
    			return;
    		}
    		if (player.dungKills < getKcRequired()) {
    			player.sendMessage("You need "+Colors.red + getKcRequired()+"</col> kills to enter this room; "
    					+ "you only have "+Colors.red+player.dungKills+"</col>.");
    			return;
    		}
			player.dungKills = 0;
			sendInterfaces();
		}
    	player.addWalkSteps(destX, destY, 2, false);
    	player.lock(2);
    }

    @Override
    public boolean processObjectClick1(final WorldObject object) {
    	if (object.getId() == 11005) {
			player.lock(1);
    		if (object.getX() == 3978 && object.getY() == 5552) {
    			player.dungKills = 0;
    			player.inDungeoneering = false;
    			player.addWalkSteps(3977, 5552, 2, false);
    			player.getControlerManager().forceStop();
        		return false;
    		}
    		if (object.getX() == 3989 && object.getY() == 5548) {
    			if (player.getY() >= 5549)
    				handleDoor(3989, 5547, true, 10);
    			else
    				handleDoor(3989, 5549, false, 10);
    			return false;
    		}
    		if (object.getX() == 4004 && object.getY() == 5544) {
    			if (player.getX() <= 4003)
    				handleDoor(4005, 5544, true, 20);
    			else
    				handleDoor(4003, 5544, false, 20);
    			return false;
    		}
    		if (object.getX() == 4016 && object.getY() == 5544) {
    			if (player.getX() <= 4015)
    				handleDoor(4017, 5544, true, 30);
    			else
    				handleDoor(4015, 5544, false, 30);
    			return false;
    		}
    		if ((object.getX() == 4023 || object.getX() == 4024) && object.getY() == 5537) {
    			if (player.getY() >= 5538)
    				handleDoor(player.getX(), 5536, true, 40);
    			else
    				handleDoor(player.getX(), 5538, false, 40);
    			return false;
    		}
    		if (object.getX() == 4024 && object.getY() == 5520) {
    			if (player.getY() >= 5521)
    				handleDoor(4024, 5519, true, 50);
    			else
    				handleDoor(4024, 5521, false, 50);
    			return false;
    		}
    		if (object.getX() == 4017 && object.getY() == 5503) {
    			if (player.getY() >= 5504)
    				handleDoor(4017, 5502, true, 60);
    			else
    				handleDoor(4017, 5504, false, 60);
    			return false;
    		}
    		if (object.getX() == 4009 && object.getY() == 5495) {
    			if (player.getX() >= 4010)
    				handleDoor(4008, 5495, true, 70);
    			else
    				handleDoor(4010, 5495, false, 70);
    			return false;
    		}
    		if (object.getX() == 3995 && object.getY() == 5490) {
    			if (player.getY() >= 5491)
    				handleDoor(3995, 5489, true, 80);
    			else
    				handleDoor(3995, 5491, false, 80);
    			return false;
    		}
    		if (object.getX() == 3995 && object.getY() == 5474) {
    			if (player.getY() >= 5475)
    				handleDoor(3995, 5473, true, 90);
    			else
    				handleDoor(3995, 5475, false, 90);
    			return false;
    		}
    	}
    	return true;
    }

    @Override
    public boolean sendDeath() {
    	WorldTasksManager.schedule(new WorldTask() {
    		int loop;
    		@Override
    		public void run() {
    			if (loop == 0) {
    				player.setNextAnimation(new Animation(836));
    				player.sendMessage("Oh dear, you have died.");
    			} 
    			if (loop == 3) {
    				player.setNextWorldTile(new WorldTile(3972, 5553, 0));
    				player.setNextAnimation(new Animation(-1));
    				player.getControlerManager().forceStop();
    				player.getPackets().sendMusicEffect(90);
    				player.dungKills = 0;
    				player.inDungeoneering = false;
    				player.reset();
    				stop();
    			}
    			loop++;
    		}
    	}, 0, 1);
    	return false;
    }
    
    @Override
    public void forceClose() {
    	player.getInterfaceManager().closeOverlay(false);
    	player.dungKills = 0;
    }
    
    /**
     * Used to handle NPC killing in the dungeon.
     * @param player The killer.
     * @param npcId The NPCId.
     * @param xp The amount of exp to give.
     * @param tokens The amount of tokens to give.
     */
    public static void handleDrop(Player player, NPC npc) {
    	switch (npc.getName().toLowerCase()) {
    	case "dungeon rat":
    		handleReward(player, Utils.random(5, 30), Utils.random(10, 40));
    		break;
    	case "dungeon spider":
    		handleReward(player, Utils.random(15, 50), Utils.random(20, 80));
    		break;
    	case "skeleton":
    		handleReward(player, Utils.random(25, 90), Utils.random(40, 160));
    		break;
    	case "armoured zombie":
    		handleReward(player, Utils.random(45, 170), Utils.random(80, 320));
    		break;
    	case "ghost":
    		handleReward(player, Utils.random(85, 250), Utils.random(150, 500));
    		break;
    	case "earth warrior":
    		handleReward(player, Utils.random(125, 270), Utils.random(250, 700));
    		break;
    	case "icefiend":
    		handleReward(player, Utils.random(180, 400), Utils.random(350, 900));
    		break;
    	case "ice warrior":
    		handleReward(player, Utils.random(250, 600), Utils.random(450, 1200));
    		break;
    	case "ice elemental":
    		handleReward(player, Utils.random(350, 800), Utils.random(550, 1300));
    		break;
    	case "skeletal warrior":
    		handleReward(player, Utils.random(1000, 3500), Utils.random(550, 5000));
    		break;
    	case "skeletal minion":
    		handleReward(player, Utils.random(450, 1000), Utils.random(650, 1500));
    		break;
    	}
    }

    /**
     * Used to handle NPC rewards in the dungeon.
     * @param player The killer.
     * @param xp The amount of exp to give.
     * @param tokens The amount of tokens to give.
     */
    private static void handleReward(Player player, int xp, int tokens) {
    	if (!(player.getControlerManager().getControler() instanceof Dungeoneering))
    		return;
    	player.setDungeoneeringTokens((int) (player.getDungeoneeringTokens() + 
    			((player.getPerkManager().dungeon ? 1.25 : 1) * tokens * (Settings.DUNGEONEERING_WEEKEND ? 2 : 1))));
    	player.getSkills().addXp(Skills.DUNGEONEERING, (player.getPerkManager().dungeon ? 1.25 : 1) 
    			* xp * (Settings.DUNGEONEERING_WEEKEND ? 2 : 1));
    	player.dungKills ++;
    	player.getInterfaceManager().sendOverlay(988, false);
    	player.getPackets().sendIComponentText(988, 0, ""+player.dungKills);
    	player.getPackets().sendIComponentText(988, 1, "Kills:");
    }
}