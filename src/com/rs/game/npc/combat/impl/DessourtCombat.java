package com.rs.game.npc.combat.impl;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;

public class DessourtCombat extends CombatScript {

    @Override
    public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		switch (1) {
		case 1:
			int damage = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MAGE, target);
		    npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		    delayHit(npc, 1, target, getMeleeHit(npc, damage));
		    break;
		}
		return defs.getAttackDelay();
    }

    @Override
    public Object[] getKeys() {
    	return new Object[] { "Dessourt" };
    }
}