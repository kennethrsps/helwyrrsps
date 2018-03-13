package com.rs.game.npc.combat.impl;

import java.util.Collections;
import java.util.List;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.kalphite.KalphiteKing;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

/**
 * Handles the Kalphite Kings combat script.
 * @author Zeus
 */
public class KalphiteKingCombat extends CombatScript {

    @Override
    public Object[] getKeys() {
    	return new Object[] { 16697, 16698, 16699 };
    }

	@Override
	public int attack(NPC npc, Entity target) {
		KalphiteKing king = (KalphiteKing) npc;
		final NPCCombatDefinitions defs = king.getCombatDefinitions();

		king.setForceFollowClose(king.getId() == 16697);
		
		if (Utils.random(15) == 1 && !king.isShieldActive && !king.hasActivatedShield && king.getHPPercentage() <= 50)
		    king.activateShield();
		
		//When he spawns minions, he skips an attack only if it's not melee phase and he hasnt thrown the green ball before the 1 hit
		if (king.getHPPercentage() < 75 && ((king.getPhase() != 6 && king.getId() == 16699) 
				|| (king.getPhase() != 9 && king.getId() == 16697) || king.getId() == 16698)) {
		    king.battleCry();
		    king.battleCry();
		    king.battleCry();
		    if (!(king.getPhase() == 5 && king.getId() == 16697) && !(king.getId() == 16697))
		    	king.nextPhase();
		    else if (king.getPhase() == 5 && king.getId() == 16699)
		    	king.setPhase(7);
		}
		else if (king.getHPPercentage() < 25 && ((king.getPhase() != 6 && king.getId() == 16699) 
				|| (king.getPhase() != 9 && king.getId() == 16697) || king.getId() == 16698)) {	//Same here
		    king.battleCry();
		    king.battleCry();
		    king.battleCry();
		    king.battleCry();
		    king.battleCry();
		    king.battleCry();
		    if (!(king.getPhase() == 5 && king.getId() == 16697) && !(king.getId() == 16697))
		    	king.nextPhase();
		    else if (king.getPhase() == 5 && king.getId() == 16699)
		    	king.setPhase(7);
		}

		if (Utils.random(7) == 0)
		    king.switchPhase();

		if (npc.getId() == 16699) { //Ranged KK
		    switch (king.getPhase()) {
		    case 0:
				rangeBasic(npc, target);
				break;
			case 1:
				rangeFrag(npc, target);
				break;
			case 2:
				rangeStun(npc, target);
				break;
			case 3:
				rangeBasic(npc, target);
				break;
			case 4:
				rangeBasic(npc, target);
				break;
			case 5:
				green(npc, target);
				king.nextPhase();
				return 8;
			case 6:
				if (excecuteGreen(npc, target))
				    return defs.getAttackDelay();
				else
				    return 1;
			case 7:
				king.setForceFollowClose(false);
				rangeIncendiaryShot(npc, target);
				break;
			case 8:
				rangeBasic(npc, target);
				break;
			case 9:
				dig(npc, target);
				king.setPhase(0);
				break;
		    }
		    king.nextPhase();
		    if (king.getPhase() < 0 || king.getPhase() > 9)
		    	king.setPhase(0);
		    return defs.getAttackDelay();
		}
		else if (npc.getId() == 16697) { //Melee KK
		    switch (king.getPhase()) {
		    case 0:
				meleeBleed(npc, target);
				break;
		    case 1:
				meleeStomp(npc, target);
				break;
		    case 2:
				//meleePush(npc, target);
		    	meleeStomp(npc, target);
				break;
		    case 3:
				meleeBleed(npc, target);
				break;
		    case 4:
				meleeBleed(npc, target);
				break;
		    case 5:
		    	meleeStomp(npc, target);
				rush(npc, target);
				king.nextPhase();
				return 17;
		    case 6:
				meleeStomp(npc, target);
				break;
		    case 7:
				meleeBleed(npc, target);
				break;
		    case 8:
				green(npc, target);
				king.nextPhase();
				return 8;
		    case 9:
				if (excecuteGreen(npc, target))
				    return defs.getAttackDelay();
				else
				    return 1;
		    }
		    king.nextPhase();
		    if (king.getPhase() < 0 || king.getPhase() > 9)
		    	king.setPhase(0);
		    return defs.getAttackDelay();
		}
		else if (npc.getId() == 16698) { //Magic KK
		    switch (king.getPhase()) {
		    case 0:
				mageBall(npc, target, 0);
				break;
		    case 1:
				mageBallBlue(npc, target);
				break;
		    case 2:
				mageBall(npc, target, 0);
				break;
		    case 3:
				mageBall(npc, target, 0);
				break;
		    case 4:
				dig(npc, target);
				break;
		    case 5:
				mageBallDouble(npc, target);
				break;
		    case 6:
				mageBall(npc, target, 0);
				break;
		    case 7:
				rush(npc, target);
				king.nextPhase();
				return 17;
		    case 8:
				mageBallBlue(npc, target);
				break;
		    case 9:
				mageBallBleed(npc, target);
				break;
		    }
		    king.nextPhase();
		    if (king.getPhase() < 0 || king.getPhase() > 9)
		    	king.setPhase(0);
		    return defs.getAttackDelay() + 3;
		}
		return defs.getAttackDelay();
	}

    /**
     * Range basic attack.
     * @param npc KK.
     * @param target The target.
     */
    private void rangeBasic(NPC npc, Entity target) {
		npc.setNextAnimation(new Animation(19450));
		for (Entity e : npc.getPossibleTargets()) {
		    Hit hit = getRangeHit(npc, getRandomMaxHit(npc, 600, NPCCombatDefinitions.RANGE, e));
		    delayHit(npc, 1, e, hit);
		}
    }
	
    /**
     * Range incendiary attack.
     * @param npc KK.
     * @param target The target.
     */
	private void rangeIncendiaryShot(NPC npc, Entity target) {
		npc.setNextAnimation(new Animation(19450));
		List<Entity> list = ((KalphiteKing) npc).getPossibleTargets();
		Collections.shuffle(list);
		int c = 0;
		while (c < 3) {
		    for (final Entity t : list) {
		    	Hit hit = new Hit(npc, 50 + Utils.random(100), HitLook.RANGE_DAMAGE);
		    	delayHit(npc, 0, t, hit);

				WorldTasksManager.schedule(new WorldTask() {
				    @Override
				    public void run() {
				    	t.setNextGraphics(new Graphics(3522));
				    }
				}, 3);
				c++;
			}
		}
	}
	
	/**
	 * Marks target player in green.
	 * @param npc KK.
	 * @param target The target.
	 */
	private void green(NPC npc, Entity target) {
		final KalphiteKing king = (KalphiteKing) npc;
		king.setForceFollowClose(true);
		king.setNextAnimation(new Animation(19464));
		king.setNextGraphics(new Graphics(3738));
		target.setNextGraphics(new Graphics(3740, 1, 0));	
		if (target instanceof Player) {
		    ((Player)target).lock(9);
		    ((Player)target).stopAll();
		    ((Player)target).setNextAnimation(new Animation(-1)); //to stop abilities emotes
		    ((Player)target).getPackets().sendGameMessage("The Kalphite King has imobilised you while preparing for a powerful attack.");
		}
		WorldTasksManager.schedule(new WorldTask() {
		    @Override
		    public void run() {	
		    	king.setForceFollowClose(true);
		    }
		}, 8);
	}

	/**
	 * Executes damage on the green targeted player.
	 * @param npc KK.
	 * @param target The target.
	 * @return if can execute.
	 */
	private boolean excecuteGreen(NPC npc, Entity target) {
		if (target instanceof Player) {
			if (Utils.isOnRange(npc, target, 5)) {
			    npc.setNextAnimation(new Animation(19449));
			    target.applyHit(new Hit(npc, 700, HitLook.MELEE_DAMAGE));
			    npc.setForceFollowClose(false);
			    ((KalphiteKing) npc).nextPhase();
			    return true;
			}
		}
		return false;
	}
	
	/**
	 * Executes the Dig attack.
	 * @param npc KK.
	 * @param target The target.
	 */
	private void dig(NPC npc, Entity target) {
		((KalphiteKing) npc).dig(target);
    }
	
	//MELEE incomplete
	/**
	 * Executes the Melee Stomp attack.
	 * @param npc KK.
	 * @param target The target.
	 */
    private void meleeStomp(NPC npc, Entity target)	{
		KalphiteKing king = (KalphiteKing) npc;
		king.setNextAnimation(new Animation(19435));
		king.setNextGraphics(new Graphics(3734));
		for (Entity t : npc.getPossibleTargets()) {
		    if (Utils.isOnRange(king, t, 5)) {
			    Hit hit = getMeleeHit(npc, getRandomMaxHit(npc, 600, NPCCombatDefinitions.MELEE, t));
				delayHit(npc, 1, t, hit);
				if (t instanceof Player) 
				    ((Player) t).getSkills().drainLevel(Skills.DEFENCE, hit.getDamage() / 200);
		    }
		}
    }

    /**
     * Executes the Melee Push attack.
     * @param npc KK.
     * @param target The target.
     */
    @SuppressWarnings("unused")
	private void meleePush(NPC npc, Entity target) {
		/**KalphiteKing king = (KalphiteKing) npc;
		king.setTarget(null);
		final byte[] dirs = Utils.getOrthogonalDirection(npc.getLastWorldTile(), target.getLastWorldTile());
	
		if (dirs[0] == 1)	//To make it face the correct way
		    king.setNextFaceWorldTile( new WorldTile(king.getX() + 10, king.getY() + 2, king.getPlane()) );
		else if (dirs[0] == -1)	
		    king.setNextFaceWorldTile( new WorldTile(king.getX() - 10, king.getY() + 2, king.getPlane()) );
		else if (dirs[1] == 1)	
		    king.setNextFaceWorldTile( new WorldTile(king.getX() + 2, king.getY() + 10, king.getPlane()) );
		else if (dirs[1] == -1)	
		    king.setNextFaceWorldTile( new WorldTile(king.getX() + 2, king.getY() - 10, king.getPlane()) );
	
		king.setNextAnimation(new Animation(19449));
		List<Entity> targets = king.getPossibleTargets();
	
		for (Entity t : targets) {
		    if (t instanceof Player) {
				Hit hit = getMeleeHit(npc, Utils.random(80, 360));
				if (dirs[0] == 1) {
				    if (t.getY() - king.getY() >= -1 && t.getY() - king.getY() <= 4 && t.getX() - king.getX() >= 2 && t.getX() - king.getX() <= 6) {
				    	t.setNextAnimation(new Animation(10070));
				    	t.setNextForceMovement(new ForceMovement(t, 0, new WorldTile(t.getX() + dirs[0], t.getY() + dirs[1], t.getPlane()), 1, ForceMovement.EAST));
				    	delayHit(npc, 0, t, hit);
				    }
				}
				if (dirs[0] == -1) {
				    if (t.getY() - king.getY() >= -1 && t.getY() - king.getY() <= 4 && t.getX() - king.getX() <= 0 && t.getX() - king.getX() <= -6) {
				    	t.setNextForceMovement(new ForceMovement(t, 0, new WorldTile(t.getX() + dirs[0], t.getY() + dirs[1], t.getPlane()), 1, ForceMovement.WEST));
				    	t.setNextAnimation(new Animation(10070));
				    	delayHit(npc, 0, t, hit);
				    }
				}
				if (dirs[1] == 1) {
				    if (t.getX() - king.getX() >= -2 && t.getX() - king.getX() <= 5 && t.getY() - king.getY() >= 2 && t.getY() - king.getY() <= 6) {
				    	t.setNextForceMovement(new ForceMovement(t, 0, new WorldTile(t.getX() + dirs[0], t.getY() + dirs[1], t.getPlane()), 1, ForceMovement.NORTH));
				    	t.setNextAnimation(new Animation(10070));
				    	delayHit(npc, 0, t, hit);
				    }
				}
				if (dirs[1] == -1) {
				    if (t.getX() - king.getX() >= -2 && t.getX() - king.getX() <= 5 && t.getY() - king.getY() <= -2 && t.getY() - king.getY() >= -6) {
				    	t.setNextForceMovement(new ForceMovement(t, 0, new WorldTile(t.getX() + dirs[0], t.getY() + dirs[1], t.getPlane()), 1, ForceMovement.SOUTH));
				    	t.setNextAnimation(new Animation(10070));
				    	delayHit(npc, 0, t, hit);
				    }
				}
		    }
		}*/
    }

    /**
     * Executes the Melee Bleed attack.
     * @param npc KK.
     * @param target The target.
     */
    private void meleeBleed(final NPC npc, final Entity target) {
		Entity t = null;
		try {
		    List<Entity> targets = npc.getPossibleTargets(); //To select random target and not tank
		    Collections.shuffle(targets);
		    t = targets.get(0);
		} catch (Exception e) {
			if (target instanceof Player)
				((Player) target).getPackets().sendGameMessage("Bleed didn't work!");
		}
		if (t != null) {
		    npc.setTarget(t);
		    npc.setNextFaceEntity(t);
		    npc.setNextAnimation(new Animation(19449));
		    Hit hit = getMeleeHit(npc, getRandomMaxHit(npc, 600, NPCCombatDefinitions.MELEE, t));
		    t.applyHit(hit);
		}
		WorldTasksManager.schedule(new WorldTask() {
		    @Override
		    public void run()  {
		    	npc.setTarget(target);
		    	stop();
		    }
		}, 1);
    }

    /**
     * Executes the Melee Rush attack.
     * @param npc
     * @param target
     */
    private void rush(NPC npc, Entity target) {
		/**KalphiteKing king = (KalphiteKing) npc;
		king.setTarget(null);
	
		final byte[] dirs = Utils.getOrthogonalDirection(npc.getLastWorldTile(), target.getLastWorldTile());
	
		if (dirs[0] == 1)	//To make it face the correct way
		    king.setNextFaceWorldTile( new WorldTile(king.getX() + 10, king.getY() + 2, king.getPlane()) );
		else if (dirs[0] == -1)	
		    king.setNextFaceWorldTile( new WorldTile(king.getX() - 10, king.getY() + 2, king.getPlane()) );
		else if (dirs[1] == 1)	
		    king.setNextFaceWorldTile( new WorldTile(king.getX() + 2, king.getY() + 10, king.getPlane()) );
		else if (dirs[1] == -1)	
		    king.setNextFaceWorldTile( new WorldTile(king.getX() + 2, king.getY() - 10, king.getPlane()) );
	
		WorldTile lastTile = null;
		int distance;
		for (distance = 1; distance < 10; distance++) {
		    WorldTile nextTile = new WorldTile(new WorldTile(king.getX() + (dirs[0] * distance), king.getY() + (dirs[1] * distance), king.getPlane()));
		    if (!World.isFloorFree(nextTile.getPlane(), nextTile.getX(), nextTile.getY(), king.getSize())) 
		    	break;
		    lastTile = nextTile;
		}
		if (lastTile == null || distance <= 2) { 
		    king.setNextAnimation(new Animation(19447));
		    king.setNextGraphics(new Graphics(3735));
		    for (Entity t : king.getPossibleTargets()) {
		    	if (!Utils.isOnRange(king, t, 1))
		    		continue;
		    	delayHit(npc, 0, t, getRegularHit(npc, Utils.random(260) + 260));
		    }
		} else {
		    king.setNextAnimation(new Animation(19457));
		    final int maxStep = distance / 2;
		    king.setCantInteract(true);
		    king.setNextAnimation(new Animation(maxStep + 19456));
		    int totalTime = distance/2;
		    final WorldTile firstTile = new WorldTile(king);
		    int dir = king.getDirection();
		    king.setNextForceMovement(new NewForceMovement(firstTile, 5, lastTile, totalTime + 5, dir));
		    WorldTile tpTile = lastTile;
		    final ArrayList<Entity> targets = king.getPossibleTargets();
		    
		    WorldTasksManager.schedule(new WorldTask() {
		    	
				int step = 0;
				
				@Override
				public void run()  {
				    if (step == maxStep - 1) {
				    	king.setCantInteract(false);
				    	king.setTarget(target);
				    	stop();
				    	return;
				    }
				    if (step == 1)
				    	king.setNextWorldTile(tpTile);
				    WorldTile kingTile = new WorldTile(firstTile.getX() + (dirs[0] * step * 2), firstTile.getY() + (dirs[1] * step * 2), king.getPlane());
				    int leftSteps = (maxStep - step) + 1;
				    for (Entity t : targets) {
						if (!(t instanceof Player))
						    continue;
						Player player = (Player) t;
						if (player.isLocked())
						    continue;
						if (Utils.colides(kingTile, t, king.getSize(), 1)) {
			
						    WorldTile lastTileForP = null;
						    int stepCount = 0;
						    for (int thisStep = 1; thisStep <= leftSteps; thisStep++) {
						    	WorldTile nextTile = new WorldTile(new WorldTile(player.getX() + (dirs[0] * thisStep * 2), player.getY() + (dirs[1] * thisStep * 2), player.getPlane()));
						    	if (!World.isFloorFree(nextTile.getPlane(), nextTile.getX(), nextTile.getY()))
						    		break;
						    	lastTileForP = nextTile;
						    	stepCount = thisStep;
						    }
						    if (lastTileForP == null)
						    	continue;
						    player.setNextForceMovement(new NewForceMovement(player, 0, lastTileForP, stepCount, Utils.getAngle(firstTile.getX() - player.getX(), firstTile.getY() - player.getY())));
						    player.setNextAnimation(new Animation(10070));
						    player.lock(stepCount + 1);
						    delayHit(npc, 0, t, getRegularHit(npc, Utils.random(180, 360)));
						    final WorldTile lastTileForP_T = lastTileForP;
			
						    WorldTasksManager.schedule(new WorldTask() {
						    	@Override
						    	public void run() {
						    		player.setNextWorldTile(lastTileForP_T);
						    		player.faceEntity(king);
						    	}
						    }, 0);
						}
				    }
				    step++;
				}
		    }, 3, 0);
		}*/
    }
    
    /**
     * Executes the Magic Ball attack.
     * @param npc KK.
     * @param target The target.
     * @param n NPC ID to transform into.
     * @param bleed if bleed.
     */
    private void mageBall(final NPC npc, Entity target, int n)	{
		npc.setNextAnimation(new Animation(19448));
		npc.setNextGraphics(new Graphics(3742));
	
		for (Entity t : npc.getPossibleTargets()) {
		    final WorldTile tile = new WorldTile(t).transform(n, 0, 0);
		    //if(twoOrbs)
		    //World.sendProjectileNew(npc, tile.transform(0, -1, 0), 3743, 100, 30, 80, 1, 16, 0);
		    WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
				    mageDoBallGraphics(npc, tile);
				    WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
						    World.sendGraphics(npc, new Graphics(3752), tile);
						    for (Entity t : npc.getPossibleTargets()) {
						    	if (t.withinDistance(tile, 3)) {
									Hit hit = getMagicHit(npc, getRandomMaxHit(npc, 600, NPCCombatDefinitions.MAGE, t));
						    		t.applyHit(hit);
						    	}
						    }
						}
				    }, 2);
				}
		    }, 1);
		}
    }

    /**
     * Executes double Magic Ball attacks.
     * @param npc KK.
     * @param target The target.
     */
    private void mageBallDouble(NPC npc, Entity target)	{
		mageBall(npc, target, 1);
		mageBall(npc, target, -1);
    }

    /**
     * Executes a Blue Magic Ball attack.
     * @param npc KK.
     * @param target The target.
     */
    private void mageBallBlue(NPC npc, Entity target) {
		npc.setNextAnimation(new Animation(19448));
		npc.setNextGraphics(new Graphics(3757));
		for (final Entity t : npc.getPossibleTargets()) {
		    Hit hit = getMagicHit(npc, getRandomMaxHit(npc, 600, NPCCombatDefinitions.MAGE, t));
			if (!(Utils.random(100) - hit.getDamage() > 1))
			    delayHit(npc, 1, t, hit);
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					t.setFreezeDelay(8000);
				}
			}, 1);
		}
    }

    /**
     * Executes Mage Ball attack with bleed effect.
     * @param npc KK.
     * @param target The target.
     */
    private void mageBallBleed(NPC npc, Entity target) {
    	mageBall(npc, target, 0); 
    }

    /**
     * Executes Mage Ball graphic.
     * @param npc KK.
     * @param tile The WorldTile.
     */
    private void mageDoBallGraphics(final NPC npc, final WorldTile tile) {
		//Projectile projectile = World.sendProjectileNew(tile, tile, 3758, 0, 100, 80, 2, 16, 0);
		World.sendGraphics(npc, new Graphics(3743), tile);
		WorldTasksManager.schedule(new WorldTask() {
	
		    @Override
		    public void run() {
		    	World.sendGraphics(npc, new Graphics(3743), tile);
		    }
		}, 1);
    }
    
    /**
     * Executes the Ranged Fragmentation attack.
     * @param npc KK.
     * @param target The target.
     */
    private void rangeFrag(final NPC npc, Entity target) {
		npc.setNextAnimation(new Animation(19450));
		List<Entity> list = npc.getPossibleTargets();
		Collections.shuffle(list);
		int c = 0;
		for (final Entity e : list) {
		    if (c < 3) {
		    	WorldTasksManager.schedule(new WorldTask() {
				    @Override
				    public void run() {
				    	delayHit(npc, 1, e, getRegularHit(npc, Utils.random(200) + 260));
				    	e.setNextGraphics(new Graphics(3574));
				    }
				}, 1);
				c++;
		    }
		}
    }
    
    /**
     * Executes the Ranged Stun attack.
     * @param npc KK.
     * @param target The target.
     */
    private void rangeStun(NPC npc, Entity target) {
		for (Entity t : npc.getPossibleTargets()) {
		    t.setFreezeDelay(16000);
	    	delayHit(npc, 1, t, getRegularHit(npc, Utils.random(260) + 260));
		}
    }
}