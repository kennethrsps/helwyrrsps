package com.rs.game.npc.barrelchest;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.WorldTile;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.controllers.BarrelchestController;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;

@SuppressWarnings("serial")
public class BarrelChest extends BarrelNPC {

    private BarrelchestController controler;

    public BarrelChest(int id, WorldTile tile, BarrelchestController controler) {
		super(id, tile);
		this.controler = controler;
    }

    @Override
    public void processNPC() {
	super.processNPC();
    }

    @Override
    public void sendDeath(Entity source) {
	final NPCCombatDefinitions defs = getCombatDefinitions();
	resetWalkSteps();
	getCombat().removeTarget();
	setNextAnimation(null);
	WorldTasksManager.schedule(new WorldTask() {
	    int loop;

	    @Override
	    public void run() {
		if (loop == 0) {
		    setNextAnimation(new Animation(defs.getDeathEmote()));
		} else if (loop >= defs.getDeathDelay()) {
		    reset();
		    finish();
		    controler.win();
		    stop();
		}
		loop++;
	    }
	}, 0, 1);
    }

}