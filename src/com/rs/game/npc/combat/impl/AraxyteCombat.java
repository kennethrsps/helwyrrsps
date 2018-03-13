package com.rs.game.npc.combat.impl;

import java.util.ArrayList;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.araxxor.AraxyteNPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

/**
 * Handles the Araxxor's Combat script.
 * @author Zeus
 */
public class AraxyteCombat extends CombatScript {

    @Override
    public int attack(final NPC npc, final Entity target) {
		final AraxyteNPC araxxor = (AraxyteNPC) npc;
		final NPCCombatDefinitions defs = araxxor.getCombatDefinitions();
		final ArrayList<Entity> possibleTargets = araxxor.getPossibleTargets();
		int attackStyle = Utils.getRandom(AraxyteNPC.phase2 ? 3 : 2);
    	if (Utils.getRandom(10) == 0 && !AraxyteNPC.phase2)
    	    araxxor.spawnSpider();
    	if (araxxor.isCantInteract())
    		return 2;
		if (Utils.random(30) == 0 && araxxor.getHitpoints() >= 10000) { // multi-target cleave
			if (possibleTargets != null) {
			    for (final Entity t : possibleTargets) {
			    	if (t == null || t.isDead() || t.hasFinished())
			    		continue;
					 WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							if (araxxor == null || araxxor.isCantInteract())
								stop();
							if (step == 0) {
								if (!t.withinDistance(araxxor, 1)) {
									t.setNextWorldTile(araxxor);
									t.resetWalkSteps();
									if (t instanceof Player)
										((Player) t).getActionManager().forceStop();
								}
							}
							if (step == 2) {
								if (t.withinDistance(araxxor, 1)) {
									delayHit(araxxor, 1, t, getMeleeHit(araxxor, 
											getRandomMaxHit(araxxor, defs.getMaxHit(), NPCCombatDefinitions.MELEE, t)));
									t.faceEntity(araxxor);
								}
								araxxor.setNextAnimation(new Animation(24050));
								araxxor.setNextGraphics(new Graphics(4986));
								stop();
							}
							step++;
						}
						int step;
					}, 0, 1);
			    }
			}
			return AraxyteNPC.phase2 ? 17 : 19;
		}
		if (attackStyle == 0 || attackStyle == 1) { // melee
		    if (!target.withinDistance(araxxor, 1))
				acidMultiSpit(araxxor);
		    else {
		    	araxxor.setNextAnimation(new Animation(defs.getAttackEmote()));
		    	delayHit(araxxor, 0, target, getMeleeHit(araxxor, 
		    			getRandomMaxHit(araxxor, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target)));
		    }
		}
		else if (attackStyle == 2) { // acid spit move - multi
			if (AraxyteNPC.phase2 && Utils.random(100) >= 50)
				acidMultiSpit(araxxor);
			else
				cocoonMultiSpit(araxxor);
		}
		else if (attackStyle == 3) { // cocoon spit move - multi
			cocoonMultiSpit(araxxor);
		}
		
		//XXX TODO Make it cocoon the player (make player frozen) / need more target distance
		
	    return getAttackSpeed();
    }

    @Override
    public Object[] getKeys() {
    	return new Object[] { "Araxxor", "Araxxi" };
    }
    
    /**
     * Gets Araxxor's attack speed based on its phase.
     * @return attack speed as Integer.
     */
    private int getAttackSpeed() {
    	return AraxyteNPC.phase2 ? 5 : 7;
    }
    
    /**
     * Inits the Acid (Magic) Multi spit attack move.
     * @param araxxor The Araxxor NPC.
     */
    private void acidMultiSpit(final AraxyteNPC araxxor) {
		final ArrayList<Entity> possibleTargets = araxxor.getPossibleTargets();
		final NPCCombatDefinitions defs = araxxor.getCombatDefinitions();
		for (final Entity t : possibleTargets) {
		   	if (t == null || t.isDead() || t.hasFinished())
		    	continue;
			araxxor.setNextAnimation(new Animation(24047));
			delayHit(araxxor, 1, t, getMagicHit(araxxor, getRandomMaxHit(araxxor, defs.getMaxHit(), NPCCombatDefinitions.MAGE, t)));
			World.sendProjectile(araxxor.getMiddleWorldTile(), t, 4979, 60, 30, 45, 0, 5, 200);
			araxxor.setNextGraphics(new Graphics(4988, 0, 180));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					World.sendGraphics(araxxor, new Graphics(4980), t);
					stop();
				}
			}, 1);
			t.getPoison().makePoisoned(150);
		}
    }
    
    /**
     * Inits the Cocoon (Ranged) Multi spit attack move.
     * @param araxxor The Araxxor NPC.
     */
    private void cocoonMultiSpit(final AraxyteNPC araxxor) {
		final ArrayList<Entity> possibleTargets = araxxor.getPossibleTargets();
		final NPCCombatDefinitions defs = araxxor.getCombatDefinitions();
		for (final Entity t : possibleTargets) {
		    if (t == null || t.isDead() || t.hasFinished())
		    	continue;
		    araxxor.setNextAnimation(new Animation(24047));
			delayHit(araxxor, 1, t, getRangeHit(araxxor, getRandomMaxHit(araxxor, defs.getMaxHit(), NPCCombatDefinitions.RANGE, t)));
			World.sendProjectile(araxxor.getMiddleWorldTile(), t, 4997, 60, 30, 45, 0, 5, 200);
			araxxor.setNextGraphics(new Graphics(4988, 0, 180));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					World.sendGraphics(araxxor, new Graphics(4993), t);
					stop();
				}
			}, 1);
		}
    }
}