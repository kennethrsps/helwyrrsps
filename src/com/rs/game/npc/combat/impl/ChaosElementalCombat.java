package com.rs.game.npc.combat.impl;

import java.util.ArrayList;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

public class ChaosElementalCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 3200 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int attackStyle = Utils.getRandom(5);
		int size = npc.getSize();

		if (attackStyle == 0) {
			int distanceX = target.getX() - npc.getX();
			int distanceY = target.getY() - npc.getY();
			if (distanceX > size || distanceX < -1 || distanceY > size
					|| distanceY < -1)
				attackStyle = Utils.getRandom(4) + 1;
			else {
				delayHit(
						npc,
						0,
						target,
						getMeleeHit(
								npc,
								getRandomMaxHit(npc, defs.getMaxHit(),
										NPCCombatDefinitions.MELEE, target)));
				npc.setNextAnimation(new Animation(defs.getAttackEmote()));
				return defs.getAttackDelay();
			}
		} else if (attackStyle == 1 || attackStyle == 2) {
			int damage = getRandomMaxHit(npc, 189, NPCCombatDefinitions.MAGE,
					target);
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			target.setNextGraphics(new Graphics(556));
			delayHit(npc, 2, target, getMagicHit(npc, damage));
			ArrayList<Entity> possibleTargets = npc.getPossibleTargets();
			for (Entity t : possibleTargets) {
				if (t instanceof Player) {
					Player p = (Player) t;
					if (p.getFreezeDelay() == 0) {
						p.sendMessage("<col=F7A6A6>The Chaos Elemental has frozen you!", true);
						p.addFreezeDelay(15000);
					}
				}
			}
		} else if (attackStyle == 3) {
			int damage = getRandomMaxHit(npc, 189, NPCCombatDefinitions.MELEE,
					target);
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			npc.setNextGraphics(new Graphics(550));
			delayHit(npc, 2, target, getMeleeHit(npc, damage));
			ArrayList<Entity> possibleTargets = npc.getPossibleTargets();
			for (Entity t : possibleTargets) {
				if (t instanceof Player) {
					Player p = (Player) t;
					if (!p.getPoison().isPoisoned()) {
						p.getPoison().makePoisoned(80);
						p.sendMessage("<col=F7A6A6>The Chaos Elemental has poisoned you!", true);
					}
				}
			}
		} else if (attackStyle == 4) {
			final Player player = target instanceof Player ? (Player) target
					: null;
			int damage = getRandomMaxHit(npc, 189, NPCCombatDefinitions.RANGE,
					target);
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			npc.setNextGraphics(new Graphics(553));
			if (player != null)
				delayHit(npc, 1, target, getRangeHit(npc, damage));
		} else if (attackStyle == 5) {
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					int way = Utils.random(3);
					ArrayList<Entity> possibleTargets = npc
							.getPossibleTargets();
					for (Entity t : possibleTargets) {
						if (t instanceof Player) {
							Player p = (Player) t;
							if (way == 0)
								p.setNextWorldTile(new WorldTile(p.getX()
										- Utils.random(2), p.getY()
										- Utils.random(2), p.getPlane()));
							else if (way == 1)
								p.setNextWorldTile(new WorldTile(p.getX()
										- Utils.random(2), p.getY()
										+ Utils.random(2), p.getPlane()));
							else if (way == 2)
								p.setNextWorldTile(new WorldTile(p.getX()
										+ Utils.random(2), p.getY()
										+ Utils.random(2), p.getPlane()));
							else if (way == 3)
								p.setNextWorldTile(new WorldTile(p.getX()
										+ Utils.random(2), p.getY()
										- Utils.random(2), p.getPlane()));
						}
					}
				}
			}, 1);
		}
		return defs.getAttackDelay();
	}
}