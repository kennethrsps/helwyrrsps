package com.rs.game.npc.combat.impl;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.others.Bork;

/**
 * Handles Bork's combat script.
 * @author Zeus
 */
public class BorkCombat extends CombatScript {

    @Override
    public int attack(NPC npc, Entity target) {
    	final NPCCombatDefinitions cdef = npc.getCombatDefinitions();
		Bork bork = (Bork) npc;
		if (bork.getHitpoints() <= (cdef.getHitpoints() * 0.6) && !bork.isSpawnedMinions()) {
			bork.spawnMinions();
			return 0;
		}
		bork.setNextAnimation(new Animation(cdef.getAttackEmote()));
		delayHit(bork, 0, target, getMeleeHit(bork, getRandomMaxHit(bork, bork.getMaxHit(), NPCCombatDefinitions.MELEE, target)));
		return cdef.getAttackDelay();
    }

    @Override
    public Object[] getKeys() {
    	return new Object[] { "Bork" };
    }
}