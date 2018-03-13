package com.rs.game.npc.combat.impl;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.utils.Utils;

public class AbyssalDemonCombat extends CombatScript {

    @Override
    public int attack(NPC npc, Entity target) {
	final NPCCombatDefinitions defs = npc.getCombatDefinitions();
	npc.setNextAnimation(new Animation(1537));
	delayHit(npc, 1, target, getMeleeHit(npc, Utils.random(100)));
	if (Utils.getRandom(3) == 0) {
	    WorldTile teleTile = npc;
	    for (int trycount = 0; trycount < 2; trycount++) {
		teleTile = new WorldTile(target, 2);
		if (World.canMoveNPC(target.getPlane(), teleTile.getX(),
			teleTile.getY(), target.getSize()))
		    continue;
	    }
	    if (World.canMoveNPC(npc.getPlane(), teleTile.getX(),
		    teleTile.getY(), npc.getSize())) {
		npc.setNextGraphics(new Graphics(409));
		npc.setNextWorldTile(teleTile);
	    }
	}
	return defs.getAttackDelay();
    }

    @Override
    public Object[] getKeys() {
	return new Object[] { 1615 };
    }
}