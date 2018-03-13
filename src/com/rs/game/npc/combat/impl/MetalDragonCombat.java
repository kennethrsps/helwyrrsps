package com.rs.game.npc.combat.impl;

import java.util.ArrayList;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.content.Combat;
import com.rs.utils.Utils;

public class MetalDragonCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Bronze dragon", "Iron dragon", "Steel dragon" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		final Player player = target instanceof Player ? (Player) target : null;
		int damage;
		if (npc.withinDistance(target, npc.getSize())) {
			damage = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target);
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(npc, 0, target, getMeleeHit(npc, damage));
			ArrayList<Entity> possibleTargets = npc.getPossibleTargets();
			for (Entity t : possibleTargets) {
				if (target.withinDistance(t, 1)) {
					if (t instanceof Player) {
						Player p = (Player) t;
						playSound(408, p, target);
					}
				}
			}
			return defs.getAttackDelay();
		} else {
			damage = Utils.random(80, 450 + npc.getCombatLevel());
			if (target instanceof Player) {
				String message = Combat.getProtectMessage(player);
				if (message != null) {
					player.sendMessage(message, true);
					if (message.contains("fully")) {
						damage *= 0;
					}
					if (message.contains("most")) {
						damage *= 0.05;
					}
					if (message.contains("some")) {
						damage *= 0.1;
					}
				} else
					player.sendMessage("You are hit by the dragon's fiery breath!", true);
			}
			npc.setNextAnimation(new Animation(13164));
			World.sendProjectile(npc, target, 393, 28, 16, 35, 20, 16, 0);
			delayHit(npc, 1, target, getRegularHit(npc, damage));
		}
		return defs.getAttackDelay();
	}
}