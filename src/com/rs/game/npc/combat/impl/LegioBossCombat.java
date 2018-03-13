package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.Hit.HitLook;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

/**
 * Handles all six Legio bosses within the Monastery of Ascension.
 * @author Zeus
 */
public class LegioBossCombat extends CombatScript {

    @Override
    public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (Utils.getRandom(10) == 0) {
		    switch (Utils.getRandom(2)) {
		    case 0:
				npc.setNextForceTalk(new ForceTalk("My power increases!"));
				break;
		    case 1:
				npc.setNextForceTalk(new ForceTalk("Nothing will stop my power!"));
				break;
		    case 2:
				npc.setNextForceTalk(new ForceTalk("You will not survive this!"));
				break;
		    }
		}
		final WorldTile tile = new WorldTile(target);
	    World.sendProjectile(npc, tile, 3978, 41, 16, 30, 0, 50, 0);
	    WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				final WorldTile newTile = new WorldTile(tile);
				WorldTasksManager.schedule(new WorldTask() {
					int count = 0;
					@Override
					public void run() {
						if (count == 0) {
							World.sendGraphics(npc, new Graphics(3974), newTile);
							if (target.withinDistance(newTile, 2))
								target.applyHit(new Hit(target, Utils.getRandom(defs.getMaxHit()), HitLook.MAGIC_DAMAGE));
						}
						if (count == 1) {
							if (target.withinDistance(newTile, 2))
								target.applyHit(new Hit(target, Utils.getRandom(defs.getMaxHit()), HitLook.MAGIC_DAMAGE));
							this.stop();
						}
						count ++;
					}
				}, 0, 1);
				this.stop();
			}
	    });
		return defs.getAttackDelay();
    }

    @Override
    public Object[] getKeys() {
    	return new Object[] { 17149, 17150, 17151, 17152, 17153, 17154 };
    }
}