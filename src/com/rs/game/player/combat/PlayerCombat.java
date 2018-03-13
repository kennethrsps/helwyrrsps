package com.rs.game.player.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.Region;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.npc.familiar.Steeltitan;
import com.rs.game.npc.fightkiln.HarAken;
import com.rs.game.npc.fightkiln.HarAkenTentacle;
import com.rs.game.npc.glacor.Glacyte;
import com.rs.game.npc.godwars.zaros.NexMinion;
import com.rs.game.npc.qbd.QueenBlackDragon;
import com.rs.game.player.CombatDefinitions;
import com.rs.game.player.Equipment;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Action;
import com.rs.game.player.actions.DummyEntry;
import com.rs.game.player.content.Combat;
import com.rs.game.player.content.Magic;
import com.rs.game.player.content.Slayer;
import com.rs.game.player.content.items.Defenders;
import com.rs.game.player.controllers.Wilderness;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.MapAreas;
import com.rs.utils.Utils;

public class PlayerCombat extends Action {

	public static final boolean LEGACY = true;
	private Entity target;
	private int max_hit;
	private double base_mage_xp;
	private int mage_hit_gfx;
	private int magic_sound;
	private int max_poison_hit;
	private int freeze_time;
	@SuppressWarnings("unused")
	private boolean reduceAttack;
	private boolean blood_spell;
	private boolean block_tele;
	private int spellcasterGloves;

	public boolean getRandomBoolean() {
		Random random = new Random();
		return random.nextBoolean();
	}

	public PlayerCombat(Entity target) {
		this.target = target;
	}

	@Override
	public boolean start(Player player) {
		player.setNextFaceEntity(target);

		if (checkAll(player))
			return true;

		player.setNextFaceEntity(null);
		return false;
	}

	@Override
	public boolean process(Player player) {
		return checkAll(player);
	}

	private boolean forceCheckClipAsRange(Entity target) {
		return target instanceof NexMinion || target instanceof HarAken || target instanceof HarAkenTentacle
				|| target instanceof QueenBlackDragon;
	}

	@Override
	public int processWithDelay(Player player) {
		if (target.isDead() || target.hasFinished() || target == null)
			return -1;
		int isRanging = isRanging(player);
		int spellId = player.getCombatDefinitions().getSpellId();
		if (spellId < 1 && hasPolyporeStaff(player))
			spellId = 65535;
		if (spellId < 1 && hasNoxiousStaff(player)) 
			spellId = 2472;
		if (spellId < 1 && hasSeismicWand(player))
			spellId = 2473;
		if (spellId < 1 && hasCywirWand(player))
			spellId = 2474;
		int maxDistance = isRanging != 0 || spellId > 0 ? 7 : 0;
		if (player.getEquipment().getWeaponId() == 33590)
			maxDistance = 10;
		int distanceX = player.getX() - target.getX();
		int distanceY = player.getY() - target.getY();
		double multiplier = 1.0;
		if (player.getTemporaryAttributtes().get("miasmic_effect") == Boolean.TRUE)
			multiplier = 1.5;
		if (player.getTemporaryAttributtes().remove("dfs_shield_active") == Boolean.TRUE) {

			// TODO DFS special attack
		}
		int size = target.getSize();
		if (!player.clipedProjectile(target, maxDistance == 0 && !forceCheckClipAsRange(target)))
			return 0;
		if (player.hasWalkSteps())
			maxDistance += 1;
		if (distanceX > size + maxDistance || distanceX < -1 - maxDistance || distanceY > size + maxDistance
				|| distanceY < -1 - maxDistance)
			return 0;
		if (!player.getControlerManager().keepCombating(target))
			return -1;
		addAttackedByDelay(player);
		if (spellId > 0) {
			boolean manualCast = spellId != 2472 && spellId != 2473 && spellId != 2474 && spellId != 65535 && spellId >= 256;
			Item gloves = player.getEquipment().getItem(Equipment.SLOT_HANDS);
			spellcasterGloves = gloves != null && gloves.getDefinitions().getName().contains("Spellcaster glove")
					&& player.getEquipment().getWeaponId() == -1 && Utils.random(10) == 0 ? spellId : -1;
			int delay = mageAttack(player, manualCast ? spellId - 256 : spellId, !manualCast);
			if (player.getNextAnimation() != null && spellcasterGloves > 0) {
				player.setNextAnimation(new Animation(14339));
				spellcasterGloves = -1;
			}
			return delay;
		} else {
			if (player.getEquipment().getWeaponId() == 33590)
				isRanging = 2;
			if (isRanging == 0 && !isRangeWeapon(player.getEquipment().getShieldId())
					&& !isRangeWeapon(player.getEquipment().getWeaponId())) {
				// System.out.println("d1");
				return (int) (meleeAttack(player) * multiplier);
			} else if (isRanging == 1) {
				player.sendMessage("You cannot use your ammunition with this weapon.");
				player.isMainhand = !player.isMainhand;
				player.faceEntity(target);
				return -1;
			} else if (isRanging == 3) {
				player.sendMessage("You don't have any ammunition equipped.");
				player.faceEntity(target);
				return -1;
			} else {
				if (player.isMainhand) {
					// System.out.println("isMainhand");
					if (isRangeWeapon(player.getEquipment().getWeaponId())) {
						player.addWalkSteps(target.getX() + size, target.getY());
						// System.out.println("rtotf");
						return (int) (rangeAttack(player) * multiplier);
					} else if (!isRangeWeapon(player.getEquipment().getWeaponId())) {
						player.addWalkSteps(target.getX() + size, target.getY());
						// System.out.println("cdfff");
						return (int) (meleeAttack(player) * multiplier);
					} else
						return (int) (meleeAttack(player) * multiplier);
				} else if (!player.isMainhand) {
					// System.out.println("isOffhand");
					if (isRangeWeapon(player.getEquipment().getShieldId())) {
						try {
							// System.out.println("cdf");
							return (int) (rangeAttack(player) * multiplier);
						} catch (NullPointerException e) {
							player.isMainhand = true;
							player.sendMessage("Please equip the appropriate ammo for your offhand.");
							return -1;
						}
					} else if (isRangeWeapon(player.getEquipment().getWeaponId())
							&& player.getEquipment().getShieldId() == -1) {
						// System.out.println("db2");
						return (int) (rangeAttack(player) * multiplier);
					} else if (!isRangeWeapon(player.getEquipment().getWeaponId())) {
						// System.out.println("db1");
						return (int) (meleeAttack(player) * multiplier);
					} else if (!isRangeWeapon(player.getEquipment().getShieldId())
							&& !isRangeWeapon(player.getEquipment().getWeaponId())) {
						if (!((player.getX() - target.getX()) >= 1) || !((player.getY() - target.getY()) >= 1))
							player.addWalkSteps(target.getX(), target.getY());
						// System.out.println("db5");
						return (int) (meleeAttack(player) * multiplier);
					} else {
						if (!isRangeWeapon(player.getEquipment().getShieldId())) {
							if (isRangeWeapon(player.getEquipment().getWeaponId()))
								return (int) (rangeAttack(player) * multiplier);
							// System.out.println("db545");
							return (int) (meleeAttack(player) * multiplier);

						} else
							return (int) (rangeAttack(player) * multiplier);
					}
				} else
					return (int) (rangeAttack(player) * multiplier);
			}
		}
	}

	private boolean isRangeWeapon(int weaponId) {
		Item wep = new Item(weaponId);
		if (wep.getName().contains("bow") || wep.getName().contains("knife") || wep.getName().contains("dart")
				|| wep.getName().contains("chin") || wep.getName().contains("javelin") || wep.getId() == 15241)
			return true;
		else
			return false;
	}

	private void addAttackedByDelay(Entity player) {
		target.setAttackedBy(player);
		target.setAttackedByDelay(Utils.currentTimeMillis() + 8000); // 8
																		// seconds
	}

	private int getRangeCombatDelay(int shieldId, int weaponId, int attackStyle) {
		int delay = 6;
		if (weaponId != -1) {
			String weaponName = ItemDefinitions.getItemDefinitions(weaponId).getName().toLowerCase();
			String shieldName = ItemDefinitions.getItemDefinitions(shieldId).getName().toLowerCase();
			if (weaponName.contains("shortbow") || weaponName.contains("karil") || weaponName.contains("zaryte")
					|| weaponName.contains("noxious") || weaponName.contains("crystal") || weaponName.contains("icyenic")
					|| weaponName.contains("godbow"))
				delay = 3;
			else if (weaponName.contains("crossbow") && !shieldName.contains("crossbow") || weaponName.contains("crossbow") && !shieldName.contains("repriser"))
				delay = 4;
			else if (weaponName.contains("crossbow") && shieldName.contains("crossbow") || weaponName.contains("crossbow") && shieldName.contains("repriser"))
				delay = 1;
			else if (weaponName.contains("dart") || weaponName.contains("knife") || weaponName.contains("chinchompa"))
				delay = 2;
			else {
				switch (weaponId) {
				case 15241:// hand cannon TODO
					delay = 7;
					break;
				case 16887:
				case 16337:
				case 11235: // dark bows
				case 15701:
				case 15702:
				case 15703:
				case 15704:
					// case 24457:
					delay = 9;
					break;
				case 24456:
					delay = 4;
					break;
				default:
					delay = 6;
					break;
				}
			}
		}
		if (attackStyle == 1)
			delay--;
		else if (attackStyle == 2)
			delay++;
		return delay;
	}

	public Entity[] getMultiAttackTargets(Player player, int maxDistance, int maxAmtTargets) {
		List<Entity> possibleTargets = new ArrayList<Entity>();
		possibleTargets.add(target);
		y: for (int regionId : target.getMapRegionsIds()) {
			Region region = World.getRegion(regionId);
			if (target instanceof Player) {
				List<Integer> playerIndexes = region.getPlayerIndexes();
				if (playerIndexes == null)
					continue;
				for (int playerIndex : playerIndexes) {
					Player p2 = World.getPlayers().get(playerIndex);
					if (p2 == null || p2 == player || p2 == target || p2.isDead() || p2.hasFinished() || !p2.isCanPvp()
							|| !p2.isAtMultiArea() || !p2.withinDistance(target, maxDistance))
						continue;
					possibleTargets.add(p2);
					if (possibleTargets.size() == maxAmtTargets)
						break y;
				}
			} else {
				List<Integer> npcIndexes = region.getNPCsIndexes();
				if (npcIndexes == null)
					continue;
				for (int npcIndex : npcIndexes) {
					NPC n = World.getNPCs().get(npcIndex);
					if (n == null || n == target || n == player.getFamiliar() || n.isDead() || n.hasFinished()
							|| !n.isAtMultiArea() || !n.withinDistance(target, maxDistance)
							|| !n.getDefinitions().hasAttackOption())
						continue;
					possibleTargets.add(n);
					if (possibleTargets.size() == maxAmtTargets)
						break y;
				}
			}
		}
		return possibleTargets.toArray(new Entity[possibleTargets.size()]);
	}

	public int mageAttack(final Player player, int spellId, boolean autocast) {
		if (!autocast) {
			player.getCombatDefinitions().resetSpells(false);
			player.getActionManager().forceStop();
		}
		if (!Magic.checkCombatSpell(player, spellId, -1, true)) {
			if (autocast)
				player.getCombatDefinitions().resetSpells(true);
			return -1; // stops
		}
		if (spellId == 65535) {
			player.setNextFaceEntity(target);
			player.setNextGraphics(new Graphics(2034));
			player.setNextAnimation(new Animation(15448));
			mage_hit_gfx = 2036;
			delayMagicHit(2, getMagicHit(player,
					getRandomMagicMaxHit(player, (5 * player.getSkills().getLevel(Skills.MAGIC)) - 180)));
			World.sendProjectile(player, target, 2035, 60, 32, 50, 50, 0, 0);
			return 4;
		}
		if (spellId == 2474) { //hellfire
			player.setNextFaceEntity(target);
			//player.setNextGraphics(new Graphics(1));
			player.setNextAnimation(new Animation(23477));
			mage_hit_gfx = 6649;
			int baseDamage = (12 * player.getSkills().getLevel(Skills.RANGE)) - 120;
			if (target instanceof NPC) {
				NPC n = (NPC) target;
				if (n.getId() == 16697) // kk
					baseDamage *= 2.5;
				if (n.getId() == 8133) // corp
					baseDamage *= 2.5;	
				if (n.getId() == 19463) // Arrax
					baseDamage *= 2.5;	
				if (n.getId() == 14301) // Glacors
					baseDamage *= 2;
				if (n.getId() == 51) // Frost drag
					baseDamage *= 2;
			}
			delayMagicHit(2, getRangeHit(player,
					getRandomMagicMaxHit(player, baseDamage)));
			World.sendProjectile(player, target, 6650, 60, 32, 50, 50, 0, 0);
			//World.sendProjectile(shooter, startTile, receiver, gfxId, startHeight, endHeight, speed, delay, curve, startDistanceOffset);
			return 3;
		}
		if (spellId == 2473) { //Seismic Spell
			player.setNextFaceEntity(target);
			player.setNextGraphics(new Graphics(6619));
			player.setNextAnimation(new Animation(18221));
			mage_hit_gfx = 6216;
			int baseDamage = (4 * player.getSkills().getLevel(Skills.MAGIC)) - 180;
			if (hasSeismicSing(player) && hasSeismicWand(player)) {
				baseDamage *= 2.5;
			}
			delayMagicHit(2, getMagicHit(player,
					getRandomMagicMaxHit(player, baseDamage)));
			World.sendProjectile(player, target, -1, 60, 32, 50, 50, 0, 0);
			if (hasSeismicSing(player) && hasSeismicWand(player)) {
				return 3;
			} else {
				return 4;
			}
			
		}
		if (spellId == 2472) { //Noxious Spell
			player.setNextFaceEntity(target);
			//player.setNextGraphics(new Graphics(1));
			player.setNextAnimation(new Animation(18333));
			mage_hit_gfx = 6649;
			int baseDamage = (8 * player.getSkills().getLevel(Skills.MAGIC)) - 180;
			if (target instanceof NPC) {
				NPC n = (NPC) target;
				if (n.getId() == 9463) // ice verm
					baseDamage *= 1.5;
				if (n.getId() == 14301) // Glacors
					baseDamage *= 2;
				if (n.getId() == 51) // Frost drag
					baseDamage *= 2;
			}
			delayMagicHit(2, getMagicHit(player,
					getRandomMagicMaxHit(player, baseDamage)));
			World.sendProjectile(player, target, 6650, 60, 32, 50, 50, 0, 0);
			//World.sendProjectile(shooter, startTile, receiver, gfxId, startHeight, endHeight, speed, delay, curve, startDistanceOffset);
			return 4;
		}

		if (player.getCombatDefinitions().getSpellBook() == 192) {
			int weaponId = player.getEquipment().getWeaponId();
			switch (spellId) {
			case 98: // wind rush
				player.setNextAnimation(new Animation(14221));
				playSound(220, player, target);
				mage_hit_gfx = 2700;
				base_mage_xp = 1.2;
				int baseDamage = 10;
				magic_sound = 217;
				delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, baseDamage)));
				World.sendProjectile(player, target, 2699, 18, 18, 50, 50, 0, 0);
				return 5;
			case 25: // air strike
				player.setNextAnimation(new Animation(14221));
				playSound(220, player, target);
				mage_hit_gfx = 2700;
				base_mage_xp = 5.5;
				baseDamage = 25;
				magic_sound = 221;
				delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, baseDamage)));
				World.sendProjectile(player, target, 2699, 18, 18, 50, 50, 0, 0);
				return getMagicAttackDelay(player, weaponId);
			case 28: // water strike
				player.setNextGraphics(new Graphics(2701));
				playSound(211, player, target);
				mage_hit_gfx = 2708;
				base_mage_xp = 7.5;
				baseDamage = 50;
				magic_sound = 212;
				delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, baseDamage)));
				World.sendProjectile(player, target, 2703, 18, 18, 50, 50, 0, 0);
				return getMagicAttackDelay(player, weaponId);
			case 36:// bind
				player.setNextGraphics(new Graphics(177));
				player.setNextAnimation(new Animation(710));
				playSound(101, player, target);
				mage_hit_gfx = 181;
				base_mage_xp = 60.5;
				magic_sound = 99;
				Hit magicHit = getMagicHit(player, getRandomMagicMaxHit(player, 20));
				delayMagicHit(2, magicHit);
				magic_sound = 152;
				World.sendProjectile(player, target, 178, 18, 18, 50, 50, 0, 0);
				long currentTime = Utils.currentTimeMillis();
				if (magicHit.getDamage() > 0 && target.getFrozenBlockedDelay() < currentTime)
					target.addFreezeDelay(5000, true);
				return getMagicAttackDelay(player, weaponId);
			case 55:// snare
				player.setNextGraphics(new Graphics(177));
				player.setNextAnimation(new Animation(710));
				mage_hit_gfx = 180;
				base_mage_xp = 91.1;
				Hit snareHit = getMagicHit(player, getRandomMagicMaxHit(player, 30));
				delayMagicHit(2, snareHit);
				magic_sound = 152;
				if (snareHit.getDamage() > 0 && target.getFrozenBlockedDelay() < Utils.currentTimeMillis())
					target.addFreezeDelay(10000, true);
				World.sendProjectile(player, target, 178, 18, 18, 50, 50, 0, 0);
				return getMagicAttackDelay(player, weaponId);
			case 54: // ibans blast
				Item weapon = player.getEquipment().getItem(Equipment.SLOT_WEAPON);
				int modifier = weapon.getName().contains("noxious staff") || weapon.getName().contains("seismic") ? 5
						: 3;
				player.setNextAnimation(new Animation(708));
				mage_hit_gfx = 89;
				//spellType = NPCWeaknesses.FIRE;
				base_mage_xp = 45;
				delayMagicHit(2, getMagicHit(player,
						getRandomMagicMaxHit(player, modifier * player.getSkills().getLevelForXp(Skills.MAGIC))));
				World.sendProjectile(player, target, 88, 40, 32, 20, 50, 0, 0);
				return 5;
			case 56: // slayer dart
				player.setNextAnimation(new Animation(1575));
				mage_hit_gfx = 329;
				base_mage_xp = 31.5;
				int[][] levels = { { 50, 60, 150 }, { 60, 70, 160 }, { 70, 80, 170 }, { 80, 90, 180 },
						{ 90, 99, 190 } };
				for (int i = 0; i < levels.length; i++) {
					if (player.getSkills().getLevel(Skills.MAGIC) >= levels[i][0]
							&& player.getSkills().getLevel(Skills.MAGIC) < levels[i][1])
						delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, levels[i][2])));
				}
				World.sendProjectile(player, target, 328, 56, 32, 50, 50, 0, 0);
				break;
			case 81:// entangle
				player.setNextGraphics(new Graphics(177));
				player.setNextAnimation(new Animation(710));
				player.setNextAnimation(new Animation(getMagicAttackEmote(player, weaponId)));
				mage_hit_gfx = 179;
				base_mage_xp = 91.1;
				Hit entangleHit = getMagicHit(player, getRandomMagicMaxHit(player, 50));
				delayMagicHit(2, entangleHit);
				magic_sound = 152;
				if (entangleHit.getDamage() > 0 && target.getFrozenBlockedDelay() < Utils.currentTimeMillis())
					target.addFreezeDelay(20000, true);
				World.sendProjectile(player, target, 178, 18, 18, 50, 50, 0, 0);
				return getMagicAttackDelay(player, weaponId);
			case 82: // stun
				player.setNextAnimation(new Animation(710));
				player.setNextGraphics(new Graphics(998, 45, 0));
				player.setNextAnimation(new Animation(getMagicAttackEmote(player, weaponId)));
				mage_hit_gfx = 174;
				base_mage_xp = 95;
				Hit stunHit = getMagicHit(player, getRandomMagicMaxHit(player, 150));
				magic_sound = 152;
				if (stunHit.getDamage() > 0 && !target.isStunned())
					target.addStunDelay(20000, true);
				World.sendProjectile(player, target, 178, 18, 18, 50, 50, 0, 0);
				return getMagicAttackDelay(player, weaponId);
			case 30: // earth strike
				player.setNextGraphics(new Graphics(2713));
				player.setNextAnimation(new Animation(14221));
				playSound(132, player, target);
				mage_hit_gfx = 2723;
				base_mage_xp = 9.5;
				magic_sound = 133;
				baseDamage = 75;
				delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, baseDamage)));
				World.sendProjectile(player, target, 2718, 18, 18, 50, 50, 0, 0);
				return getMagicAttackDelay(player, weaponId);
			case 32: // fire strike
				player.setNextGraphics(new Graphics(2728));
				player.setNextAnimation(new Animation(14221));
				playSound(160, player, target);
				mage_hit_gfx = 2737;
				base_mage_xp = 11.5;
				baseDamage = 100;
				magic_sound = 161;
				if (target instanceof NPC) {
					NPC n = (NPC) target;
					if (n.getId() == 9463) // ice verm
						baseDamage *= 1.5;
					if (n.getId() == 14301) // Glacors
						baseDamage *= 2;
					if (n.getId() == 51) // Frost drag
						baseDamage *= 2;
				}
				int damage = getRandomMagicMaxHit(player, baseDamage);
				delayMagicHit(2, getMagicHit(player, damage));
				World.sendProjectile(player, target, 2729, 18, 18, 50, 50, 0, 0);
				return getMagicAttackDelay(player, weaponId);
			case 34: // air bolt
				player.setNextAnimation(new Animation(14220));
				playSound(218, player, target);
				mage_hit_gfx = 2700;
				base_mage_xp = 13.5;
				baseDamage = 110;
				magic_sound = 219;
				delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, baseDamage)));
				World.sendProjectile(player, target, 2699, 18, 18, 50, 50, 0, 0);
				return getMagicAttackDelay(player, weaponId);
			case 39: // water bolt
				player.setNextGraphics(new Graphics(2707, 0, 100));
				player.setNextAnimation(new Animation(14220));
				playSound(209, player, target);
				mage_hit_gfx = 2709;
				base_mage_xp = 16.5;
				baseDamage = 120;
				magic_sound = 210;
				delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, baseDamage)));
				World.sendProjectile(player, target, 2704, 18, 18, 50, 50, 0, 0);
				return getMagicAttackDelay(player, weaponId);
			case 42: // earth bolt
				player.setNextGraphics(new Graphics(2714));
				player.setNextAnimation(new Animation(14222));
				playSound(130, player, target);
				mage_hit_gfx = 2724;
				base_mage_xp = 19.5;
				magic_sound = 131;
				baseDamage = 130;
				delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, baseDamage)));
				World.sendProjectile(player, target, 2719, 18, 18, 50, 50, 0, 0);
				return getMagicAttackDelay(player, weaponId);
			case 45: // fire bolt
				player.setNextGraphics(new Graphics(2728));
				player.setNextAnimation(new Animation(14223));
				playSound(157, player, target);
				mage_hit_gfx = 2738;
				base_mage_xp = 22.5;
				baseDamage = 140;
				magic_sound = 158;
				player.getPackets().sendSound(155, 2, 0);
				damage = getRandomMagicMaxHit(player, baseDamage);
				if (target instanceof NPC) {
					NPC n = (NPC) target;
					if (n.getId() == 9463) // ice verm
						damage *= 1.5;
					if (n.getId() == 14301) // Glacors
						baseDamage *= 2;
					if (n.getId() == 51) // Frost drag
						baseDamage *= 2;
				}
				delayMagicHit(2, getMagicHit(player, damage));
				World.sendProjectile(player, target, 2731, 18, 18, 50, 50, 0, 0);
				return getMagicAttackDelay(player, weaponId);
			case 47: // crumble undead
				player.setNextAnimation(new Animation(724));
				player.setNextGraphics(new Graphics(145));
				playSound(122, player, target);
				magic_sound = 124;
				mage_hit_gfx = 147;
				base_mage_xp = 24;
				baseDamage = 130;
				damage = getRandomMagicMaxHit(player, baseDamage);
				if (target instanceof NPC) {
					NPC n = (NPC) target;
					if (n.getDefinitions().getName().contains("zombie")
							|| n.getDefinitions().getName().contains("skelet")
							|| n.getDefinitions().getName().contains("ghost")
							|| n.getDefinitions().getName().contains("shade"))
						damage *= 2.5;
				}
				delayMagicHit(2, getMagicHit(player, damage));
				World.sendProjectile(player, target, 146, 18, 18, 50, 50, 0, 0);
				return getMagicAttackDelay(player, weaponId);
			case 49: // air blast
				player.setNextAnimation(new Animation(14221));
				player.setNextGraphics(new Graphics(2699));
				playSound(216, player, target);
				mage_hit_gfx = 2700;
				base_mage_xp = 25.5;
				magic_sound = 217;
				delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, 155)));
				World.sendProjectile(player, target, 2699, 18, 18, 50, 50, 0, 0);
				return getMagicAttackDelay(player, weaponId);
			case 52: // water blast
				player.setNextGraphics(new Graphics(2701));
				player.setNextAnimation(new Animation(14220));
				playSound(207, player, target);
				mage_hit_gfx = 2710;
				base_mage_xp = 31.5;
				magic_sound = 208;
				delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, 165)));
				World.sendProjectile(player, target, 2705, 18, 18, 50, 50, 0, 0);
				return getMagicAttackDelay(player, weaponId);
			case 58: // earth blast
				player.setNextGraphics(new Graphics(2715));
				player.setNextAnimation(new Animation(14222));
				playSound(128, player, target);
				mage_hit_gfx = 2725;
				base_mage_xp = 31.5;
				magic_sound = 129;
				delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, 175)));
				World.sendProjectile(player, target, 2720, 18, 18, 50, 50, 0, 0);
				return getMagicAttackDelay(player, weaponId);
			case 63: // fire blast
				player.setNextGraphics(new Graphics(2728));
				player.setNextAnimation(new Animation(14223));
				playSound(155, player, target);
				mage_hit_gfx = 2739;
				base_mage_xp = 34.5;
				magic_sound = 156;
				damage = getRandomMagicMaxHit(player, 185);
				if (target instanceof NPC) {
					NPC n = (NPC) target;
					if (n.getId() == 9463) // ice verm
						damage *= 1.5;
					if (n.getId() == 14301) // Glacors
						damage *= 2;
					if (n.getId() == 51) // Frost drag
						damage *= 2;
				}
				delayMagicHit(2, getMagicHit(player, damage));
				World.sendProjectile(player, target, 2733, 18, 18, 50, 50, 0, 0);
				return getMagicAttackDelay(player, weaponId);
			case 66:// Saradomin Strike
				player.setNextAnimation(new Animation(811));
				playSound(1656, player, target);
				mage_hit_gfx = 76;
				base_mage_xp = 34.5;
				delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, 300)));
				return getMagicAttackDelay(player, weaponId);
			case 67: // Claws of Guthix
				player.setNextAnimation(new Animation(811));
				playSound(1656, player, target);
				mage_hit_gfx = 77;
				base_mage_xp = 34.5;
				delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, 300)));
				return getMagicAttackDelay(player, weaponId);
			case 68: // Flames of Zamorak
				player.setNextAnimation(new Animation(811));
				playSound(1655, player, target);
				mage_hit_gfx = 78;
				base_mage_xp = 34.5;
				delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, 300)));
				return getMagicAttackDelay(player, weaponId);
			case 70: // air wave
				player.setNextAnimation(new Animation(14221));
				player.setNextGraphics(new Graphics(2699));
				playSound(222, player, target);
				mage_hit_gfx = 2700;
				base_mage_xp = 36;
				magic_sound = 223;
				delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, 200)));
				World.sendProjectile(player, target, 2699, 18, 18, 50, 50, 0, 0);
				return getMagicAttackDelay(player, weaponId);
			case 73: // water wave
				player.setNextGraphics(new Graphics(2702));
				player.setNextAnimation(new Animation(14220));
				playSound(213, player, target);
				mage_hit_gfx = 2710;
				base_mage_xp = 37.5;
				magic_sound = 214;
				delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, 225)));
				World.sendProjectile(player, target, 2706, 18, 18, 50, 50, 0, 0);
				return getMagicAttackDelay(player, weaponId);
			case 77: // earth wave
				player.setNextGraphics(new Graphics(2716));
				player.setNextAnimation(new Animation(14222));
				playSound(134, player, target);
				mage_hit_gfx = 2726;
				base_mage_xp = 42.5;
				magic_sound = 135;
				delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, 250)));
				World.sendProjectile(player, target, 2721, 18, 18, 50, 50, 0, 0);
				return getMagicAttackDelay(player, weaponId);
			case 80: // fire wave
				player.setNextGraphics(new Graphics(2728));
				player.setNextAnimation(new Animation(14223));
				playSound(162, player, target);
				mage_hit_gfx = 2740;
				base_mage_xp = 42.5;
				magic_sound = 163;
				damage = getRandomMagicMaxHit(player, 275);
				if (target instanceof NPC) {
					NPC n = (NPC) target;
					if (n.getId() == 9463) // ice verm
						damage *= 1.5;
					if (n.getId() == 14301) // Glacors
						damage *= 2;
					if (n.getId() == 51) // Frost drag
						damage *= 2;
				}
				delayMagicHit(2, getMagicHit(player, damage));
				World.sendProjectile(player, target, 2735, 18, 18, 50, 50, 0, 0);
				return getMagicAttackDelay(player, weaponId);
			case 86: // teleblock
				if (target instanceof Player && ((Player) target).getTeleBlockDelay() <= Utils.currentTimeMillis()) {
					player.setNextGraphics(new Graphics(1841));
					player.setNextAnimation(new Animation(getMagicAttackEmote(player, weaponId)));
					playSound(202, player, target);
					mage_hit_gfx = 1843;
					base_mage_xp = 80;
					block_tele = true;
					magic_sound = 203;
					Hit hit = getMagicHit(player, getRandomMagicMaxHit(player, 30));
					delayMagicHit(2, hit);
					World.sendProjectile(player, target, 1842, 18, 18, 50, 50, 0, 0);
				} else {
					player.sendMessage("This player is already effected by this spell.", true);
				}
				return getMagicAttackDelay(player, weaponId);
			case 84:// air surge
				player.setNextGraphics(new Graphics(457));
				player.setNextAnimation(new Animation(10546));
				playSound(222, player, target);
				mage_hit_gfx = 2700;
				base_mage_xp = 80;
				magic_sound = 223;
				delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, 290)));
				World.sendProjectile(player, target, 462, 18, 18, 50, 50, 0, 0);
				return getMagicAttackDelay(player, weaponId);
			case 87:// water surge
				player.setNextGraphics(new Graphics(2701));
				player.setNextAnimation(new Animation(10542));
				playSound(213, player, target);
				mage_hit_gfx = 2712;
				base_mage_xp = 80;
				magic_sound = 214;
				delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, 310)));
				World.sendProjectile(player, target, 2707, 18, 18, 50, 50, 3, 0);
				return getMagicAttackDelay(player, weaponId);
			case 89:// earth surge
				player.setNextGraphics(new Graphics(2717));
				player.setNextAnimation(new Animation(14209));
				playSound(134, player, target);
				mage_hit_gfx = 2727;
				base_mage_xp = 80;
				magic_sound = 135;
				delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, 330)));
				World.sendProjectile(player, target, 2722, 18, 18, 50, 50, 0, 0);
				return getMagicAttackDelay(player, weaponId);
			case 91:// fire surge
				player.setNextGraphics(new Graphics(2728));
				player.setNextAnimation(new Animation(2791));
				playSound(162, player, target);
				mage_hit_gfx = 2741;
				base_mage_xp = 80;
				magic_sound = 163;
				damage = getRandomMagicMaxHit(player, 350);
				if (damage > 0 && target instanceof NPC) {
					NPC n = (NPC) target;
					if (n.getId() == 9463) // ice verm
						damage *= 1.5;
					if (n.getId() == 14301) // Glacors
						damage *= 2;
					if (n.getId() == 51) // Frost drag
						damage *= 2;
				}
				delayMagicHit(2, getMagicHit(player, damage));
				World.sendProjectile(player, target, 2735, 18, 18, 50, 50, 3, 0);
				World.sendProjectile(player, target, 2736, 18, 18, 50, 50, 20, 0);
				World.sendProjectile(player, target, 2736, 18, 18, 50, 50, 110, 0);
				return getMagicAttackDelay(player, weaponId);
			case 99: // Storm of armadyl
				player.setNextGraphics(new Graphics(457));
				player.setNextAnimation(new Animation(10546));
				mage_hit_gfx = 1019;
				base_mage_xp = 70;
				int boost = (player.getSkills().getLevelForXp(Skills.MAGIC) - 77) * 5;
				int hit = getRandomMagicMaxHit(player, 160 + boost);
				if (hit > 0 && hit < boost)
					hit += boost;
				delayMagicHit(2, getMagicHit(player, hit));
				World.sendProjectile(player, target, 1019, 18, 18, 50, 30, 0, 0);
				return player.getEquipment().getWeaponId() == 21777 ? 4 : 5;
			}
		} else if (player.getCombatDefinitions().getSpellBook() == 193) {
			switch (spellId) {
			case 28:// Smoke Rush
				player.setNextAnimation(new Animation(1978));
				playSound(176, player, target);
				mage_hit_gfx = 385;
				base_mage_xp = 30;
				max_poison_hit = 20;
				magic_sound = 177;
				delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, 150)));
				World.sendProjectile(player, target, 386, 18, 18, 50, 50, 0, 0);
				return 4;
			case 32:// Shadow Rush
				player.setNextAnimation(new Animation(1978));
				playSound(175, player, target);
				mage_hit_gfx = 379;
				base_mage_xp = 31;
				magic_sound = 177;
				reduceAttack = true;
				delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, 160)));
				World.sendProjectile(player, target, 380, 18, 18, 50, 50, 0, 0);
				return 4;
			case 36: // Miasmic rush
				player.setNextAnimation(new Animation(10513));
				player.setNextGraphics(new Graphics(1845));
				mage_hit_gfx = 1847;
				base_mage_xp = 35;
				delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, 200)));
				World.sendProjectile(player, target, 1846, 43, 22, 51, 50, 16, 0);
				if (target.getTemporaryAttributtes().get("miasmic_immunity") == Boolean.TRUE) {
					return 4;
				}
				if (target instanceof Player) {
					((Player) target).getPackets().sendGameMessage("You feel slowed down.");
				}
				target.getTemporaryAttributtes().put("miasmic_immunity", Boolean.TRUE);
				target.getTemporaryAttributtes().put("miasmic_effect", Boolean.TRUE);
				final Entity t = target;
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						t.getTemporaryAttributtes().remove("miasmic_effect");
						WorldTasksManager.schedule(new WorldTask() {
							@Override
							public void run() {
								t.getTemporaryAttributtes().remove("miasmic_immunity");
								stop();
							}
						}, 15);
						stop();
					}
				}, 20);
				return 4;
			case 37: // Miasmic blitz
				player.setNextAnimation(new Animation(10524));
				player.setNextGraphics(new Graphics(1850));
				mage_hit_gfx = 1851;
				base_mage_xp = 48;
				delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, 280)));
				World.sendProjectile(player, target, 1852, 43, 22, 51, 50, 16, 0);
				if (target.getTemporaryAttributtes().get("miasmic_immunity") == Boolean.TRUE) {
					return 4;
				}
				if (target instanceof Player) {
					((Player) target).getPackets().sendGameMessage("You feel slowed down.");
				}
				target.getTemporaryAttributtes().put("miasmic_immunity", Boolean.TRUE);
				target.getTemporaryAttributtes().put("miasmic_effect", Boolean.TRUE);
				final Entity t0 = target;
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						t0.getTemporaryAttributtes().remove("miasmic_effect");
						WorldTasksManager.schedule(new WorldTask() {
							@Override
							public void run() {
								t0.getTemporaryAttributtes().remove("miasmic_immunity");
								stop();
							}
						}, 15);
						stop();
					}
				}, 60);
				return 4;
			case 38: // Miasmic burst
				player.setNextAnimation(new Animation(10516));
				player.setNextGraphics(new Graphics(1848));
				attackTarget(getMultiAttackTargets(player), new MultiAttack() {
					private boolean nextTarget;

					@Override
					public boolean attack() {
						mage_hit_gfx = 1849;
						base_mage_xp = 42;
						int damage = getRandomMagicMaxHit(player, 240);
						delayMagicHit(2, getMagicHit(player, damage));
						if (target.getTemporaryAttributtes().get("miasmic_immunity") != Boolean.TRUE) {
							if (target instanceof Player) {
								((Player) target).getPackets().sendGameMessage("You feel slowed down.");
							}
							target.getTemporaryAttributtes().put("miasmic_immunity", Boolean.TRUE);
							target.getTemporaryAttributtes().put("miasmic_effect", Boolean.TRUE);
							final Entity t = target;
							WorldTasksManager.schedule(new WorldTask() {
								@Override
								public void run() {
									t.getTemporaryAttributtes().remove("miasmic_effect");
									WorldTasksManager.schedule(new WorldTask() {
										@Override
										public void run() {
											t.getTemporaryAttributtes().remove("miasmic_immunity");
											stop();
										}
									}, 15);
									stop();
								}
							}, 40);
						}
						if (!nextTarget) {
							if (damage == -1) {
								return false;
							}
							nextTarget = true;
						}
						return nextTarget;

					}
				});
				return 4;
			case 39: // Miasmic barrage
				player.setNextAnimation(new Animation(10518));
				player.setNextGraphics(new Graphics(1853));
				attackTarget(getMultiAttackTargets(player), new MultiAttack() {
					private boolean nextTarget;

					@Override
					public boolean attack() {
						mage_hit_gfx = 1854;
						base_mage_xp = 54;
						int damage = getRandomMagicMaxHit(player, 320);
						delayMagicHit(2, getMagicHit(player, damage));
						if (target.getTemporaryAttributtes().get("miasmic_immunity") != Boolean.TRUE) {
							if (target instanceof Player) {
								((Player) target).getPackets().sendGameMessage("You feel slowed down.");
							}
							target.getTemporaryAttributtes().put("miasmic_immunity", Boolean.TRUE);
							target.getTemporaryAttributtes().put("miasmic_effect", Boolean.TRUE);
							final Entity t = target;
							WorldTasksManager.schedule(new WorldTask() {
								@Override
								public void run() {
									t.getTemporaryAttributtes().remove("miasmic_effect");
									WorldTasksManager.schedule(new WorldTask() {
										@Override
										public void run() {
											t.getTemporaryAttributtes().remove("miasmic_immunity");
											stop();
										}
									}, 15);
									stop();
								}
							}, 80);
						}
						if (!nextTarget) {
							if (damage == -1) {
								return false;
							}
							nextTarget = true;
						}
						return nextTarget;

					}
				});
				return 4;
			case 24:// Blood rush
				player.setNextAnimation(new Animation(1978));
				mage_hit_gfx = 373;
				base_mage_xp = 33;
				magic_sound = 174;
				blood_spell = true;
				delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, 170)));
				World.sendProjectile(player, target, 374, 18, 18, 50, 50, 0, 0);
				return 4;
			case 20:// Ice rush
				player.setNextAnimation(new Animation(1978));
				mage_hit_gfx = 361;
				base_mage_xp = 34;
				freeze_time = 5000;
				magic_sound = 173;
				delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, 180)));
				World.sendProjectile(player, target, 362, 18, 18, 50, 50, 0, 0);
				return 4;
			case 30:// Smoke burst
				player.setNextAnimation(new Animation(1979));
				playSound(179, player, target);
				attackTarget(getMultiAttackTargets(player), new MultiAttack() {

					private boolean nextTarget; // real target is first player
					// on array

					@Override
					public boolean attack() {
						mage_hit_gfx = 389;
						base_mage_xp = 36;
						max_poison_hit = 20;
						magic_sound = 180;
						int damage = getRandomMagicMaxHit(player, 190);
						delayMagicHit(2, getMagicHit(player, damage));
						World.sendProjectile(player, target, 388, 18, 18, 50, 50, 0, 0);
						if (!nextTarget) {
							if (damage == -1)
								return false;
							nextTarget = true;
						}
						return nextTarget;

					}
				});
				return 4;
			case 34:// Shadow burst
				player.setNextAnimation(new Animation(1979));
				playSound(178, player, target);
				attackTarget(getMultiAttackTargets(player), new MultiAttack() {

					private boolean nextTarget; // real target is first player
					// on array

					@Override
					public boolean attack() {
						mage_hit_gfx = 382;
						base_mage_xp = 37;
						reduceAttack = true;
						int damage = getRandomMagicMaxHit(player, 200);
						delayMagicHit(2, getMagicHit(player, damage));
						if (!nextTarget) {
							if (damage == -1)
								return false;
							nextTarget = true;
						}
						return nextTarget;

					}
				});
				return 4;
			case 26:// Blood burst
				player.setNextAnimation(new Animation(1979));
				playSound(103, player, target);
				attackTarget(getMultiAttackTargets(player), new MultiAttack() {

					private boolean nextTarget;

					@Override
					public boolean attack() {
						mage_hit_gfx = 376;
						base_mage_xp = 39;
						blood_spell = true;
						int damage = getRandomMagicMaxHit(player, 210);
						delayMagicHit(2, getMagicHit(player, damage));
						if (!nextTarget) {
							if (damage == -1)
								return false;
							nextTarget = true;
						}
						return nextTarget;

					}
				});
				return 4;
			case 22:// Ice burst
				player.setNextGraphics(new Graphics(366));
				player.setNextAnimation(new Animation(1979));
				playSound(171, player, target);
				attackTarget(getMultiAttackTargets(player), new MultiAttack() {

					private boolean nextTarget; // real target is first player
					// on array

					@Override
					public boolean attack() {
						mage_hit_gfx = 367;
						base_mage_xp = 46;
						freeze_time = 15000;
						magic_sound = 169;
						int damage = getRandomMagicMaxHit(player, 220);
						delayMagicHit(4, getMagicHit(player, damage));
						World.sendProjectile(player, target, 366, 43, 0, 120, 0, 50, 64);
						if (!nextTarget) {
							if (damage == -1)
								return false;
							nextTarget = true;
						}
						return nextTarget;

					}
				});
				return 4;
			case 29:// Smoke Blitz
				player.setNextAnimation(new Animation(1978));
				playSound(183, player, target);
				mage_hit_gfx = 387;
				base_mage_xp = 42;
				magic_sound = 184;
				max_poison_hit = 40;
				delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, 230)));
				World.sendProjectile(player, target, 386, 18, 18, 50, 50, 0, 0);
				return 4;
			case 33:// Shadow Blitz
				player.setNextAnimation(new Animation(1978));
				playSound(175, player, target);
				mage_hit_gfx = 381;
				base_mage_xp = 43;
				magic_sound = 180;
				reduceAttack = true;
				delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, 240)));
				World.sendProjectile(player, target, 380, 18, 18, 50, 50, 0, 0);
				return 4;
			case 25:// Blood Blitz
				player.setNextAnimation(new Animation(1978));
				playSound(108, player, target);
				mage_hit_gfx = 375;
				base_mage_xp = 45;
				magic_sound = 105;
				blood_spell = true;
				int damage = getRandomMagicMaxHit(player, 250);
				delayMagicHit(2, getMagicHit(player, damage));
				World.sendProjectile(player, target, 374, 18, 18, 50, 50, 0, 0);
				return 4;
			case 21:// Ice Blitz
				player.setNextGraphics(new Graphics(366));
				player.setNextAnimation(new Animation(1978));
				playSound(171, player, target);
				mage_hit_gfx = 366;
				base_mage_xp = 46;
				freeze_time = 15000;
				magic_sound = 169;
				delayMagicHit(2, getMagicHit(player, getRandomMagicMaxHit(player, 260)));
				World.sendProjectile(player, target, 368, 60, 32, 50, 50, 0, 0);
				return 4;
			case 31:// Smoke barrage
				player.setNextAnimation(new Animation(1979));
				playSound(183, player, target);
				attackTarget(getMultiAttackTargets(player), new MultiAttack() {

					private boolean nextTarget; // real target is first player
					// on array

					@Override
					public boolean attack() {
						mage_hit_gfx = 391;
						base_mage_xp = 48;
						max_poison_hit = 40;
						magic_sound = 184;
						int damage = getRandomMagicMaxHit(player, 270);
						delayMagicHit(2, getMagicHit(player, damage));
						World.sendProjectile(player, target, 390, 18, 18, 50, 50, 0, 0);
						if (!nextTarget) {
							if (damage == -1)
								return false;
							nextTarget = true;
						}
						return nextTarget;

					}
				});
				return 4;
			case 35:// shadow barrage
				player.setNextAnimation(new Animation(1979));
				playSound(175, player, target);
				attackTarget(getMultiAttackTargets(player), new MultiAttack() {

					private boolean nextTarget; // real target is first player
					// on array

					@Override
					public boolean attack() {
						mage_hit_gfx = 383;
						base_mage_xp = 49;
						magic_sound = 178;
						reduceAttack = true;
						int damage = getRandomMagicMaxHit(player, 280);
						delayMagicHit(2, getMagicHit(player, damage));
						if (!nextTarget) {
							if (damage == -1)
								return false;
							nextTarget = true;
						}
						return nextTarget;

					}
				});
				return 4;
			case 27:// blood barrage
				player.setNextAnimation(new Animation(1979));
				playSound(108, player, target);
				attackTarget(getMultiAttackTargets(player), new MultiAttack() {

					private boolean nextTarget;

					@Override
					public boolean attack() {
						mage_hit_gfx = 377;
						base_mage_xp = 51;
						magic_sound = 107;
						blood_spell = true;
						int damage = getRandomMagicMaxHit(player, 290);
						delayMagicHit(2, getMagicHit(player, damage));
						World.sendProjectile(player, target, 390, 18, 18, 50, 50, 0, 0);
						if (!nextTarget) {
							if (damage == -1)
								return false;
							nextTarget = true;
						}
						return nextTarget;

					}
				});
				return 4;
			case 23: // ice barrage
				player.setNextAnimation(new Animation(1979));
				playSound(171, player, target);
				attackTarget(getMultiAttackTargets(player), new MultiAttack() {

					private boolean nextTarget;

					@Override
					public boolean attack() {
						magic_sound = 168;
						long currentTime = Utils.currentTimeMillis();
						if (target.getSize() >= 2 || target.getFreezeDelay() >= currentTime
								|| target.getFrozenBlockedDelay() >= currentTime)
							mage_hit_gfx = 1677;
						else {
							mage_hit_gfx = 369;
							freeze_time = 20000;
						}
						base_mage_xp = 52;
						int damage = getRandomMagicMaxHit(player, 300);
						Hit hit = getMagicHit(player, damage);
						delayMagicHit(Utils.getDistance(player, target) > 3 ? 4 : 2, hit);
						World.sendProjectile(player, target, 368, 60, 32, 50, 50, 0, 0);
						if (!nextTarget) {
							if (damage == -1)
								return false;
							nextTarget = true;
						}
						return nextTarget;

					}
				});
				return 4;
			}

		}
		return -1; // stops atm
	}

	public interface MultiAttack {

		public boolean attack();

	}

	public void attackTarget(Entity[] targets, MultiAttack perform) {
		Entity realTarget = target;
		for (Entity t : targets) {
			target = t;
			if (!perform.attack())
				break;
		}
		target = realTarget;
	}

	public int getRandomMagicMaxHit(Player player, int baseDamage) {
		int current = getMagicMaxHit(player, baseDamage);
		if (current <= 0) // Splash.
			return 0;
		int hit = Utils.random(current + 1);
		if (hit > 0) {
			if (target instanceof NPC) {
				NPC n = (NPC) target;
				if (n.getId() == 9463 && hasFireCape(player)) // ice verm
					hit += 40;
			}
		}
		return hit;
	}

	private boolean hasSlayerHelm(Player player) {
		int helmId = player.getEquipment().getHatId();
		return helmId == 13263 || helmId == 14636 || helmId == 14637 || (helmId >= 15492 && helmId <= 15497)
				|| (helmId >= 22528 && helmId <= 22551);
	}

	private boolean hasReaperHood(Player player) {
		int helmId = player.getEquipment().getHatId();
		return helmId == 11789;
	}

	public static final boolean fullGuthanEquipped(Player player) {
		int helmId = player.getEquipment().getHatId();
		int chestId = player.getEquipment().getChestId();
		int legsId = player.getEquipment().getLegsId();
		int weaponId = player.getEquipment().getWeaponId();
		if (helmId == -1 || chestId == -1 || legsId == -1 || weaponId == -1)
			return false;
		return ItemDefinitions.getItemDefinitions(helmId).getName().contains("Guthan")
				&& ItemDefinitions.getItemDefinitions(chestId).getName().contains("Guthan")
				&& ItemDefinitions.getItemDefinitions(legsId).getName().contains("Guthan")
				&& ItemDefinitions.getItemDefinitions(weaponId).getName().contains("Guthan");
	}

	public static final boolean berserkerNecklace(Player player) {
		int amuletId = player.getEquipment().getAmuletId();
		int weaponId = player.getEquipment().getWeaponId();
		if (amuletId == 11128) {
			if (weaponId == 6527 || weaponId == 6539 || weaponId == 6523 || weaponId == 6526)
				return true;
		}
		return false;
	}

	private int getMagicMaxHit(Player player, int baseDamage) {
		double EA = (Math.round(player.getSkills().getLevel(Skills.MAGIC) * player.getPrayer().getMageMultiplier())
				+ 11);
		if (fullVoidEquipped(player, new int[] { 11663, 11674 }))
			EA *= 1.15;
		EA *= player.getAuraManager().getMagicAccurayMultiplier();
		double ED = 0, DB;
		if (target instanceof Player) {
			Player p2 = (Player) target;
			double EMD = ((Math.round(p2.getSkills().getLevel(Skills.MAGIC) * p2.getPrayer().getMageMultiplier()) + 8)
					+ (player.getCombatDefinitions().isDefensiveCasting() ? 3 : 0));
			if (p2.getFamiliar() != null && (p2.getFamiliar().getId() == 6870 || p2.getFamiliar().getId() == 6871))
				EMD *= 1.05;
			Math.round(EMD);
			double ERD = (Math.round(p2.getSkills().getLevel(Skills.DEFENCE) * p2.getPrayer().getDefenceMultiplier())
					+ 8);
			EMD *= 0.7;
			ERD *= 0.3;
			ED = (EMD + ERD);
			DB = p2.getCombatDefinitions().getBonuses()[CombatDefinitions.MAGIC_DEF];
		} else {
			NPC n = (NPC) target;
			DB = ED = n.getBonuses() != null ? n.getBonuses()[CombatDefinitions.MAGIC_DEF] / 1.6 : 0;
		}
		double A = (EA * (1 + player.getCombatDefinitions().getBonuses()[CombatDefinitions.MAGIC_ATTACK]) / 64);
		double D = (ED * (1 + DB)) / 64;
		double accuracy = 0.05; // so a level 3 can hit a level 138
		if (A < D)
			accuracy = (A - 1) / (2 * D);
		else if (A > D)
			accuracy = 1 - (D + 1) / (2 * A);
		if (accuracy < Math.random())
			return 0;
		if (player.getEquipment().getAmuletId() == 31872)
			accuracy += 0.03;
		max_hit = baseDamage;
		double boost = 1
				+ ((player.getSkills().getLevel(Skills.MAGIC) - player.getSkills().getLevelForXp(Skills.MAGIC)) * 0.03);
		if (boost > 1)
			max_hit *= boost;
		double magicPerc = player.getCombatDefinitions().getBonuses()[CombatDefinitions.MAGIC_DAMAGE];
		if (spellcasterGloves > 0) {
			if (baseDamage > 60 || spellcasterGloves == 28 || spellcasterGloves == 25) {
				magicPerc += 17;
				if (target instanceof Player) {
					Player p = (Player) target;
					p.getSkills().drainLevel(0, p.getSkills().getLevel(0) / 10);
					p.getSkills().drainLevel(1, p.getSkills().getLevel(1) / 10);
					p.getSkills().drainLevel(2, p.getSkills().getLevel(2) / 10);
					p.getPackets().sendGameMessage("Your melee skills have been drained.");
					player.sendMessage("Your spell weakened your enemy.");
				}
				player.sendMessage("Your magic surged with extra power.");
			}
		}
		boost = magicPerc / 100 + 1;
		max_hit *= boost;
		return (int) Math.floor(max_hit);
	}

	private int rangeAttack(final Player player) {
		final int weaponId = player.getEquipment().getWeaponId();
		final int shieldId = player.getEquipment().getShieldId();
		final int attackStyle = player.getCombatDefinitions().getAttackStyle();
		int combatDelay = getRangeCombatDelay(shieldId, weaponId, attackStyle);
		if (weaponId == 33590) {
			if (target instanceof NPC) {
				NPC n = (NPC) target;
				if (n.getId() == 10021) {

					if (player.getEquipment().getWeaponId() != 33590) {
						player.sendMessage("You need a snowball equipped or in your inventory to do this!", true);
						return 0;
					}

					if (player.getXmas().inPresentBox) {
						player.sendMessage("You can't attack while being trapped in the present box.");
						return 0;
					}

					if (player.getXmas().isSnowman) {
						player.sendMessage("You can't attack while being a snowman.");
						return 0;
					}

					if (player.withinDistance(n, 7)) { //
						player.getXmas().throwSnowball(n);
						n.setTarget(player);
					} else {
						player.addWalkSteps(n.getX(), n.getY());
						WorldTasksManager.schedule(new WorldTask() {
							@Override
							public void run() {
								player.getXmas().throwSnowball(n);
								n.setTarget(player);
							}
						}, 0);
					}
					return 2;
				}
			}
		}
		// Projectiles ID's now stored in the cache.
		ItemDefinitions defs = player.getEquipment().getItem(Equipment.SLOT_WEAPON).getDefinitions();
		int projectileGfx = defs.getCSOpcode(2940);
		if (projectileGfx == 0) { // uses ammo
			ItemDefinitions ammoDefs = player.getEquipment().getItem(Equipment.SLOT_ARROWS).getDefinitions();
			projectileGfx = ammoDefs.getCSOpcode(2940);
		}

		if (player.getCombatDefinitions().isUsingSpecialAttack()) {
			int specAmt = getSpecialAmmount(weaponId);
			if (specAmt == 0) {
				player.sendMessage("This weapon has no special attack; report to an administrator.");
				player.getCombatDefinitions().decreaseSpecialAttack(0);
				return combatDelay;
			}
			if (player.getCombatDefinitions().hasRingOfVigour())
				specAmt *= 0.9;
			if (player.getCombatDefinitions().getSpecialAttackPercentage() < specAmt) {
				player.sendMessage("You don't have enough power left.");
				player.getCombatDefinitions().decreaseSpecialAttack(0);
				return combatDelay;
			}
			player.getCombatDefinitions().decreaseSpecialAttack(specAmt);
			switch (weaponId) {
			case 19149:// zamorak bow
			case 19151:
				player.setNextAnimation(new Animation(426));
				player.setNextGraphics(new Graphics(97));
				World.sendProjectile(player, target, 100, 41, 16, 25, 35, 16, 0);
				delayHit(1, weaponId, attackStyle,
						getRangeHit(player, getRandomMaxHit(player, weaponId, attackStyle, true, true, 1.0, true)));
				dropAmmo(player, 1);
				break;
			case 19146:
			case 19148:// guthix bow
				player.setNextAnimation(new Animation(426));
				player.setNextGraphics(new Graphics(95));
				World.sendProjectile(player, target, 98, 41, 16, 25, 35, 16, 0);
				delayHit(1, weaponId, attackStyle,
						getRangeHit(player, getRandomMaxHit(player, weaponId, attackStyle, true, true, 1.0, true)));
				dropAmmo(player, 1);
				break;
			case 19143:// saradomin bow
			case 19145:
				player.setNextAnimation(new Animation(426));
				player.setNextGraphics(new Graphics(96));
				World.sendProjectile(player, target, 99, 41, 16, 25, 35, 16, 0);
				delayHit(1, weaponId, attackStyle,
						getRangeHit(player, getRandomMaxHit(player, weaponId, attackStyle, true, true, 1.0, true)));
				dropAmmo(player, 1);
				break;

			case 859: // magic longbow
			case 861: // magic shortbow
			case 10284: // Magic composite bow
			case 18332: // Magic longbow (sighted)
				player.setNextAnimation(new Animation(1074));
				World.sendProjectile(player, target, 249, 41, 16, 31, 35, 18, 0);
				World.sendProjectile(player, target, 249, 41, 16, 25, 35, 21, 0);
				delayHit(2, weaponId, attackStyle,
						getRangeHit(player, getRandomMaxHit(player, weaponId, attackStyle, true, true, 1.05, true)));
				delayHit(3, weaponId, attackStyle,
						getRangeHit(player, getRandomMaxHit(player, weaponId, attackStyle, true, true, 1.0, true)));
				dropAmmo(player, 2);
				break;
			case 15241: // Hand cannon
				WorldTasksManager.schedule(new WorldTask() {
					int loop = 0;

					@Override
					public void run() {
						if ((target.isDead() || player.isDead() || loop > 1) && !World.getNPCs().contains(target)) {
							stop();
							return;
						}
						if (loop == 0) {
							player.setNextAnimation(new Animation(12174));
							player.setNextGraphics(new Graphics(2138));
							World.sendProjectile(player, target, 2143, 18, 18, 50, 50, 0, 0);
							delayHit(1, weaponId, attackStyle, getRangeHit(player,
									getRandomMaxHit(player, weaponId, attackStyle, true, true, 1.0, true)));
						} else if (loop == 1) {
							player.setNextAnimation(new Animation(12174));
							player.setNextGraphics(new Graphics(2138));
							World.sendProjectile(player, target, 2143, 18, 18, 50, 50, 0, 0);
							delayHit(1, weaponId, attackStyle, getRangeHit(player,
									getRandomMaxHit(player, weaponId, attackStyle, true, true, 1.0, true)));
							stop();
						}
						loop++;
					}
				}, 0, (int) 0.25);
				combatDelay = 9;
				dropAmmo(player, -2);
				break;
			case 10033:
			case 10034:
				attackTarget(getMultiAttackTargets(player), new MultiAttack() {

					private boolean nextTarget;

					@Override
					public boolean attack() {
						int damage = getRandomMaxHit(player, weaponId, attackStyle, true, true,
								weaponId == 10034 ? 1.2 : 1.0, true);
						player.setNextAnimation(new Animation(2779));
						World.sendProjectile(player, target, weaponId == 10034 ? 909 : 908, 41, 16, 31, 35, 16, 0);
						delayHit(1, weaponId, attackStyle, getRangeHit(player, damage));
						WorldTasksManager.schedule(new WorldTask() {
							@Override
							public void run() {
								player.setNextGraphics(new Graphics(2739, 0, 96 << 16));
							}
						}, 3);
						if (!nextTarget) {
							if (damage == -1)
								return false;
							nextTarget = true;
						}
						return nextTarget;

					}
				});
				player.getEquipment().removeAmmo(weaponId, -1);
				break;
			case 11235:
			case 13405:
			case 15701:
			case 15702:
			case 15703:
			case 15704:
				int ammoId = player.getEquipment().getAmmoId();
				player.setNextAnimation(new Animation(getWeaponAttackEmote(weaponId, attackStyle, player)));
				player.setNextGraphics(new Graphics(projectileGfx, 0, 100));
				if (ammoId == 11212) {
					int damage = getRandomMaxHit(player, weaponId, attackStyle, true, true, 1.5, true);
					if (damage < 80)
						damage = 80;
					int damage2 = getRandomMaxHit(player, weaponId, attackStyle, true, true, 1.5, true);
					if (damage2 < 80)
						damage2 = 80;
					World.sendProjectile(player, target, 1099, 41, 16, 31, 35, 16, 0);
					World.sendProjectile(player, target, 1099, 41, 16, 25, 35, 21, 0);
					delayHit(2, weaponId, attackStyle, getRangeHit(player, damage));
					delayHit(3, weaponId, attackStyle, getRangeHit(player, damage2));
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							target.setNextGraphics(new Graphics(1100, 0, 100));
						}
					}, 2);
				} else {
					int damage = getRandomMaxHit(player, weaponId, attackStyle, true, true, 1.3, true);
					if (damage < 50)
						damage = 50;
					int damage2 = getRandomMaxHit(player, weaponId, attackStyle, true, true, 1.3, true);
					if (damage2 < 50)
						damage2 = 50;
					World.sendProjectile(player, target, 1101, 41, 16, 31, 35, 16, 0);
					World.sendProjectile(player, target, 1101, 41, 16, 25, 35, 21, 0);
					delayHit(2, weaponId, attackStyle, getRangeHit(player, damage));
					delayHit(3, weaponId, attackStyle, getRangeHit(player, damage2));
				}
				dropAmmo(player, 2);

				break;

			case 14684: // zanik cbow
				player.setNextAnimationForce(new Animation(getWeaponAttackEmote(weaponId, attackStyle, player)));
				player.setNextGraphics(new Graphics(1714));
				World.sendProjectile(player, target, 2001, 41, 41, 41, 35, 0, 0);
				delayHit(2, weaponId, attackStyle,
						getRangeHit(player, getRandomMaxHit(player, weaponId, attackStyle, true, true, 1.0, true) + 30
								+ Utils.getRandom(120)));
				dropAmmo(player);
				break;
			case 13954:// morrigan javelin
			case 12955:
			case 13956:
			case 13879:
			case 13880:
			case 13881:
			case 13882:
				player.setNextGraphics(new Graphics(1836));
				player.setNextAnimation(new Animation(10501));
				World.sendProjectile(player, target, 1837, 41, 41, 41, 35, 0, 0);
				final int hit = getRandomMaxHit(player, weaponId, attackStyle, true, true, 1.0, true);
				delayHit(2, weaponId, attackStyle, getRangeHit(player, hit));
				if (hit > 0) {
					final Entity finalTarget = target;
					WorldTasksManager.schedule(new WorldTask() {
						int damage = hit;

						@Override
						public void run() {
							if (finalTarget.isDead() || finalTarget.hasFinished()) {
								stop();
								return;
							}
							if (damage > 50) {
								damage -= 50;
								finalTarget.applyHit(new Hit(player, 50, HitLook.REGULAR_DAMAGE));
							} else {
								finalTarget.applyHit(new Hit(player, damage, HitLook.REGULAR_DAMAGE));
								stop();
							}
						}
					}, 4, 2);
				}
				dropAmmo(player, -1);
				break;
			case 13883:
			case 13957:// morigan thrown axe
				player.setNextGraphics(new Graphics(1838));
				player.setNextAnimation(new Animation(10504));
				World.sendProjectile(player, target, 1839, 41, 41, 41, 35, 0, 0);
				delayHit(2, weaponId, attackStyle,
						getRangeHit(player, getRandomMaxHit(player, weaponId, attackStyle, true, true, 1.0, true)));
				dropAmmo(player, -1);
				break;
			default:
				player.getPackets().sendGameMessage(
						"This weapon has no special Attack, if you still see special bar please relogin.");
				return combatDelay;
			}
		} else {
			if (weaponId != -1) {

				String weaponName = ItemDefinitions.getItemDefinitions(weaponId).getName().toLowerCase();
				if (weaponName.contains("throwing axe") || weaponName.contains("knife") || weaponName.contains("dart")
						|| weaponName.contains("javelin") || weaponName.contains("throwing")) {
					World.sendProjectile(player, target, projectileGfx, 41, 36, 50, 15, 0, 50);
					int hit = getRandomMaxHit(player, weaponId, attackStyle, true);
					delayHit(1, weaponId, attackStyle, getRangeHit(player, hit));
					checkSwiftGlovesEffect(player, 1, attackStyle, weaponId, hit, projectileGfx, 41, 36, 41, 32, 15, 0);
					dropAmmo(player, -1);
				} else if (weaponName.contains("crossbow")) {
					int damage = 0;
					int ammoId = player.getEquipment().getAmmoId();
					if (ammoId != -1 && Utils.getRandom(10) == 5) {
						switch (ammoId) {
						case 9237:
							damage = getRandomMaxHit(player, weaponId, attackStyle, true);
							target.setNextGraphics(new Graphics(755));
							if (target instanceof Player) {
								Player p2 = (Player) target;
								p2.stopAll();
							} else {
								NPC n = (NPC) target;
								n.setTarget(null);
							}
							break;
						case 9242:
							max_hit = Short.MAX_VALUE;
							damage = (int) (target.getHitpoints() * 0.2);
							target.setNextGraphics(new Graphics(754));
							player.applyHit(new Hit(target,
									player.getHitpoints() > 20 ? (int) (player.getHitpoints() * 0.1) : 1,
									HitLook.REFLECTED_DAMAGE));
							break;
						case 9243:
							damage = getRandomMaxHit(player, weaponId, attackStyle, true, false, 1.15, true);
							target.setNextGraphics(new Graphics(751));
							break;
						case 9244:
							damage = getRandomMaxHit(player, weaponId, attackStyle, true, false,
									!Combat.hasAntiDragProtection(target) ? 1.45 : 1.0, true);
							target.setNextGraphics(new Graphics(756));
							break;
						case 9245:
							damage = getRandomMaxHit(player, weaponId, attackStyle, true, false, 1.15, true);
							target.setNextGraphics(new Graphics(753));
							player.heal((int) (player.getMaxHitpoints() * 0.25));
							break;
						default:
							damage = getRandomMaxHit(player, weaponId, attackStyle, true);
						}
					} else {
						damage = getRandomMaxHit(player, weaponId, attackStyle, true);
						checkSwiftGlovesEffect(player, 2, attackStyle, weaponId, damage, projectileGfx, 38, 36, 41, 32,
								5, 0);
					}
					World.sendProjectile(player, target, projectileGfx, 38, 36, 41, 32, 5, 0);
					delayHit(2, weaponId, attackStyle, getRangeHit(player, damage));
					if (weaponId != 4740)
						dropAmmo(player);
					else
						player.getEquipment().removeAmmo(ammoId, 1);
				} else if (weaponId == 15241) {// handcannon
					if (Utils.getRandom(player.getSkills().getLevel(Skills.FIREMAKING) << 1) == 0
							&& Utils.getRandom(1) == 0) {
						// explode
						player.setNextGraphics(new Graphics(2140));
						player.getEquipment().getItems().set(3, null);
						player.getEquipment().refresh(3);
						player.applyHit(new Hit(player, Utils.getRandom(100) + 10, HitLook.REGULAR_DAMAGE));
						player.setNextAnimation(new Animation(12175));
						player.getGlobalPlayerUpdater().generateAppearenceData();
						return combatDelay;
					} else {
						player.setNextGraphics(new Graphics(2138, 0, 95));
						World.sendProjectile(player, target, projectileGfx, 50, 20, 60, 30, 0, 0);
						delayHit(1, weaponId, attackStyle,
								getRangeHit(player, getRandomMaxHit(player, weaponId, attackStyle, true)));
						dropAmmo(player, -2);
					}
				} else if (weaponName.contains("crystal bow")) {
					player.setNextAnimation(new Animation(getWeaponAttackEmote(weaponId, attackStyle, player)));
					World.sendProjectile(player, target, projectileGfx, 39, 36, 41, 50, 0, 100);
					int hit = getRandomMaxHit(player, weaponId, attackStyle, true);
					delayHit(2, weaponId, attackStyle, getRangeHit(player, hit));
					checkSwiftGlovesEffect(player, 2, attackStyle, weaponId, hit, projectileGfx, 39, 36, 41, 48, 0, 0);
				} else if (weaponName.contains("chinchompa")) {
					Item weapon = player.getEquipment().getItem(Equipment.SLOT_WEAPON);
					player.getEquipment().getItem(Equipment.SLOT_WEAPON)
							.setAmount(player.getEquipment().getItem(Equipment.SLOT_WEAPON).getAmount() - 1);
					World.sendProjectile(player, target, projectileGfx, 41, 16, 31, 25, 16, 0);
					attackTarget(getMultiAttackTargets(player), new MultiAttack() {

						private boolean nextTarget;

						@Override
						public boolean attack() {
							int damage = getRandomMaxHit(player, weaponId, attackStyle, true, true,
									weaponId == 10034 ? 1.2 : 1.0, true);
							player.setNextAnimation(new Animation(getWeaponAttackEmote(weaponId, attackStyle, player)));
							player.getEquipment().refresh(Equipment.SLOT_WEAPON);

							delayHit(1, weaponId, attackStyle, getRangeHit(player, damage));
							WorldTasksManager.schedule(new WorldTask() {
								@Override
								public void run() {
									target.setNextGraphics(new Graphics(2739, 0, 96 << 16));
								}
							}, 2);
							if (!nextTarget) {
								if (damage == -1)
									return false;
								nextTarget = true;
							}
							return nextTarget;
						}
					});
					if (player.getEquipment().getItem(Equipment.SLOT_WEAPON).getAmount() == 0) {
						player.sendMessage("You used up your last Chinchompa.");
						player.getEquipment().deleteItem(weaponId, weapon.getAmount());
						player.getGlobalPlayerUpdater().generateAppearenceData();
						return -1;
					}
					return combatDelay;
				} else if (weaponId == 21365) { // Bolas
					dropAmmo(player, -3);
					player.setNextAnimation(new Animation(getWeaponAttackEmote(weaponId, attackStyle, player)));
					World.sendProjectile(player, target, projectileGfx, 41, 41, 41, 35, 0, 0);
					int delay = 15000;
					if (target instanceof Player) {
						Player p = (Player) target;
						Item weapon = p.getEquipment().getItem(3);
						boolean slashBased = weapon != null;
						if (weapon != null) {
							int slash = p.getCombatDefinitions().getBonuses()[CombatDefinitions.SLASH_ATTACK];
							for (int i = 0; i < 5; i++) {
								if (p.getCombatDefinitions().getBonuses()[i] > slash) {
									slashBased = false;
									break;
								}
							}
						}
						if (p.getInventory().containsItem(946, 1) || p.getToolBelt().contains(946) || slashBased)
							delay /= 2;
						if (p.getPrayer().usingPrayer(0, 18) || p.getPrayer().usingPrayer(1, 8))
							delay /= 2;
						if (delay < 3000)
							delay = 3000;
					}
					long currentTime = Utils.currentTimeMillis();
					if (getRandomMaxHit(player, weaponId, attackStyle, true) > 0
							&& target.getFrozenBlockedDelay() < currentTime) {
						target.addFreezeDelay(delay, true);
						WorldTasksManager.schedule(new WorldTask() {
							@Override
							public void run() {
								target.setNextGraphics(new Graphics(469, 0, 96));
							}
						}, 2);
					}
					return combatDelay;
				} else { // bow/default
					World.sendProjectile(player, target, projectileGfx, 38, 35, 41, 50, 5, 100);
					int hit = getRandomMaxHit(player, weaponId, attackStyle, true);
					delayHit(2, weaponId, attackStyle, getRangeHit(player, hit));
					checkSwiftGlovesEffect(player, 2, attackStyle, weaponId, hit, projectileGfx, 38, 36, 41, 50, 10,
							100);
					if (weaponId == 11235 || weaponId == 15702 || weaponId == 15704) { // dbows
						World.sendProjectile(player, target, projectileGfx, 38, 35, 41, 65, 25, 100);

						delayHit(3, weaponId, attackStyle,
								getRangeHit(player, getRandomMaxHit(player, weaponId, attackStyle, true)));
						dropAmmo(player, 2);

					} else {
						if (weaponId != -1) {
							if (!weaponName.endsWith("bow full") && !weaponName.equals("zaryte bow"))
								dropAmmo(player);
						}
					}

				}
				player.setNextAnimation(new Animation(getWeaponAttackEmote(weaponId, attackStyle, player)));
			}
		}
		return combatDelay;
	}

	private void checkSwiftGlovesEffect(Player player, int hitDelay, int attackStyle, int weaponId, int hit, int gfxId,
			int startHeight, int endHeight, int speed, int delay, int curve, int startDistanceOffset) {
		if (hit > 0 && Utils.getRandom(30) < 29)
			return;
		Item gloves = player.getEquipment().getItem(Equipment.SLOT_HANDS);
		if (gloves == null || !gloves.getDefinitions().getName().contains("Swift glove"))
			return;
		World.sendProjectile(player, target, gfxId, startHeight - 5, endHeight - 5, speed, delay,
				curve - 5 < 0 ? 0 : curve - 5, startDistanceOffset);
		delayHit(hitDelay, weaponId, attackStyle,
				getRangeHit(player, getRandomMaxHit(player, weaponId, attackStyle, true)));
	}

	public void dropAmmo(Player player, int quantity) {
		if (quantity == -2) {
			final int ammoId = player.getEquipment().getAmmoId();
			player.getEquipment().removeAmmo(ammoId, 1);
		} else if (quantity == -1 || quantity == -3) {
			final int weaponId = player.getEquipment().getWeaponId();
			if (weaponId != -1) {
				if ((quantity == -3 && Utils.getRandom(10) < 2) || (quantity != -3 && Utils.getRandom(3) > 0)) {
					if (Combat.hasAvas(player) || player.getPerkManager().avasSecret) {
						if (player.getEquipment().getItem(Equipment.SLOT_CHEST) != null) {
							if (player.getEquipment().getItem(Equipment.SLOT_CHEST).getName().contains("platebody")) {
								player.sendMessage("Your armour interferes with Ava's device.");
								player.getEquipment().removeAmmo(weaponId, quantity);
								return;
							}
						}
						return;
					}
				} else {
					player.getEquipment().removeAmmo(weaponId, quantity);
					return;
				}
				player.getEquipment().removeAmmo(weaponId, quantity);
				World.updateGroundItem(new Item(weaponId, quantity),
						new WorldTile(target.getCoordFaceX(target.getSize()), target.getCoordFaceY(target.getSize()),
								target.getPlane()),
						player);
			}
		} else {
			final int ammoId = player.getEquipment().getAmmoId();
			if (Utils.getRandom(3) > 0) {
				if (Combat.hasAvas(player) || player.getPerkManager().avasSecret)
					return;
			} else {
				player.getEquipment().removeAmmo(ammoId, quantity);
				return;
			}
			if (ammoId != -1) {
				player.getEquipment().removeAmmo(ammoId, quantity);
				World.updateGroundItem(new Item(ammoId, quantity), new WorldTile(target.getCoordFaceX(target.getSize()),
						target.getCoordFaceY(target.getSize()), target.getPlane()), player);
			}
		}
	}

	public void dropAmmo(Player player) {
		dropAmmo(player, 1);
	}

	private int meleeAttack(final Player player) {
		int weaponId = player.getEquipment().getWeaponId();
		// int offhandId = new
		// Item(player.getEquipment().getShieldId()).getName().contains("off-hand")
		// ? player.getEquipment().getShieldId() : -1;
		int attackStyle = player.getCombatDefinitions().getAttackStyle();
		int combatDelay = getAttackSpeed(player);
		if (weaponId == -1) {
			Item gloves = player.getEquipment().getItem(Equipment.SLOT_HANDS);
			if (gloves != null && gloves.getDefinitions().getName().contains("Goliath gloves"))
				weaponId = -2;
		}
		if (player.getCombatDefinitions().isUsingSpecialAttack()) {
			if (!specialExecute(player))
				return combatDelay;
			switch (weaponId) {
			case 15442:// whip start
			case 15443:
			case 15444:
			case 15441:
			case 4151:
			case 23691:
				player.setNextAnimationForce(new Animation(11971));
				target.setNextGraphics(new Graphics(2108, 0, 100));
				if (target instanceof Player) {
					Player p2 = (Player) target;
					p2.setRunEnergy(p2.getRunEnergy() > 25 ? p2.getRunEnergy() - 25 : 0);
				}
				delayNormalHit(weaponId, attackStyle,
						getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.2, true)));
				break;
			case 11061:
				player.setNextAnimationForce(new Animation(10505));
				player.setNextGraphics(new Graphics(1052));
				if (target instanceof Player) {
					Player p2 = (Player) target;
					int hp = (int) (p2.getPrayer().getPrayerpoints() / 0.75);
					p2.getPrayer().drainPrayer(hp);
				}
				delayNormalHit(weaponId, attackStyle,
						getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.2, true)));
				break;
			case 11730: // sara sword
			case 23690:
				player.setNextAnimationForce(new Animation(11993));
				target.setNextGraphics(new Graphics(1194));
				delayNormalHit(weaponId, attackStyle, getMeleeHit(player, 50 + Utils.getRandom(100)),
						getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.1, true)));
				break;
			case 1249:// d spear
			case 1263:
			case 3176:
			case 5716:
			case 5730:
			case 13770:
			case 13772:
			case 13774:
			case 13776:
				player.setNextAnimationForce(new Animation(12017));
				player.stopAll();
				target.setNextGraphics(new Graphics(80, 5, 60));

				if (!target.addWalkSteps(target.getX() - player.getX() + target.getX(),
						target.getY() - player.getY() + target.getY(), 1))
					player.setNextFaceEntity(target);
				target.setNextFaceEntity(player);
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						target.setNextFaceEntity(null);
						player.setNextFaceEntity(null);
					}
				});
				if (target instanceof Player) {
					final Player other = (Player) target;
					other.lock();
					other.addFoodDelay(3000);
					other.setDisableEquip(true);
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							other.setDisableEquip(false);
							other.unlock();
						}
					}, 5);
				} else {
					NPC n = (NPC) target;
					n.setFreezeDelay(3000);
					n.resetCombat();
				}
				break;
			case 11698: // sgs
			case 23681:
				player.setNextAnimationForce(new Animation(7071));
				player.setNextGraphics(new Graphics(2109));
				int sgsdamage = getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.1, true);
				player.heal(sgsdamage / 2);
				player.getPrayer().restorePrayer((sgsdamage / 4) * 10);
				delayNormalHit(weaponId, attackStyle, getMeleeHit(player, sgsdamage));
				break;
			case 6739: // d hatchet
				player.setNextAnimationForce(new Animation(2876));
				player.setNextGraphics(new Graphics(479));
				int daxe = getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.1, true);
				delayNormalHit(weaponId, attackStyle, getMeleeHit(player, daxe));
				break;
			case 7158: // d2h
				player.setNextAnimationForce(new Animation(2876));
				player.setNextGraphics(new Graphics(479));
				int d2h = getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.1, true);
				delayNormalHit(weaponId, attackStyle, getMeleeHit(player, d2h));
				break;
			case 11696: // bgs
			case 23680:
				player.setNextAnimationForce(new Animation(11991));
				player.setNextGraphics(new Graphics(2114));
				int damage = getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.2, true);
				delayNormalHit(weaponId, attackStyle, getMeleeHit(player, damage));
				if (target instanceof Player) {
					Player targetPlayer = ((Player) target);
					int amountLeft;
					if ((amountLeft = targetPlayer.getSkills().drainLevel(Skills.DEFENCE, damage / 10)) > 0) {
						if ((amountLeft = targetPlayer.getSkills().drainLevel(Skills.STRENGTH, amountLeft)) > 0) {
							if ((amountLeft = targetPlayer.getSkills().drainLevel(Skills.PRAYER, amountLeft)) > 0) {
								if ((amountLeft = targetPlayer.getSkills().drainLevel(Skills.ATTACK, amountLeft)) > 0) {
									if ((amountLeft = targetPlayer.getSkills().drainLevel(Skills.MAGIC,
											amountLeft)) > 0) {
										if (targetPlayer.getSkills().drainLevel(Skills.RANGE, amountLeft) > 0) {
											break;
										}
									}
								}
							}
						}
					}
				}
				break;
			case 11694: // ags
			case 23679:
				player.setNextAnimationForce(new Animation(11989));
				player.setNextGraphics(new Graphics(2113));
				delayNormalHit(weaponId, attackStyle,
						getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.5, true)));
				break;

			case 13899: // vls
			case 13901:
				player.setNextAnimationForce(new Animation(10502));
				delayNormalHit(weaponId, attackStyle,
						getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.20, true)));
				break;
			case 13902: // statius hammer
			case 13904:
				player.setNextAnimationForce(new Animation(10505));
				player.setNextGraphics(new Graphics(1840));
				delayNormalHit(weaponId, attackStyle,
						getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.25, true)));
				break;
			case 13905: // vesta spear
			case 13907:
				player.setNextAnimationForce(new Animation(10499));
				player.setNextGraphics(new Graphics(1835));
				delayNormalHit(weaponId, attackStyle,
						getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.1, true)));
				break;
			case 19784: // korasi sword
			case 18786:
				player.setNextAnimationForce(new Animation(14788));
				player.setNextGraphics(new Graphics(1729));
				int korasiDamage = getMaxHit(player, weaponId, attackStyle, false, true, 1);
				double multiplier = 0.5 + Math.random();
				max_hit = (int) (korasiDamage * 1.5);
				korasiDamage *= multiplier;
				delayNormalHit(weaponId, attackStyle, getMagicHit(player, korasiDamage));
				break;
			case 11700:
				int zgsdamage = getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.0, true);
				player.setNextAnimationForce(new Animation(7070));
				player.setNextGraphics(new Graphics(1221));
				if (zgsdamage != 0 && target.getSize() <= 1) { // freezes small
					// npcs
					target.setNextGraphics(new Graphics(2104));
					target.addFreezeDelay(18000); // 18seconds
				}
				delayNormalHit(weaponId, attackStyle, getMeleeHit(player, zgsdamage));
				break;

			case 14484: // d claws
			case 23695:
				player.setNextAnimationForce(new Animation(24010));
				player.setNextGraphics(new Graphics(1950));
				int[] hits = new int[] { 0, 1 };
				int hit = getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.0, true);
				if (hit > 0)
					hits = new int[] { hit, hit / 2, (hit / 2) / 2, (hit / 2) - ((hit / 2) / 2) };
				else {
					hit = getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.0, true);
					if (hit > 0)
						hits = new int[] { 0, hit, hit / 2, hit - (hit / 2) };
					else {
						hit = getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.0, true);
						if (hit > 0)
							hits = new int[] { 0, 0, hit / 2, (hit / 2) + 10 };
						else {
							hit = getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.0, true);
							if (hit > 0)
								hits = new int[] { 0, 0, 0, (int) (hit * 1.5) };
							else
								hits = new int[] { 0, 0, 0, Utils.getRandom(7) };
						}
					}
				}
				for (int i = 0; i < hits.length; i++) {
					if (i > 1)
						delayHit(1, weaponId, attackStyle, getMeleeHit(player, hits[i]));
					else
						delayNormalHit(weaponId, attackStyle, getMeleeHit(player, hits[i]));
				}
				break;
			case 10887: // anchor
				player.setNextAnimationForce(new Animation(5870));
				player.setNextGraphics(new Graphics(1027));
				delayNormalHit(weaponId, attackStyle,
						getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, false, 1.0, true)));
				break;
			case 24694: // aurora sword
			case 1305: // dragon long
				player.setNextAnimationForce(new Animation(12033));
				player.setNextGraphics(new Graphics(2117, 0, 75));
				delayNormalHit(weaponId, attackStyle,
						getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.25, true)));
				break;
			case 3204: // d hally
				player.setNextAnimationForce(new Animation(1665));
				player.setNextGraphics(new Graphics(282));
				if (target.getSize() < 3) {
					target.setNextGraphics(new Graphics(254));
				}
				delayNormalHit(weaponId, attackStyle,
						getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.1, true)));
				if (target.getSize() < 3)
					delayHit(1, weaponId, attackStyle, getMeleeHit(player,
							getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.1, true)));
				break;

			case 4587: // dragon sci
			case 24882:
				player.setNextAnimationForce(new Animation(12031));
				player.setNextGraphics(new Graphics(2118));
				Hit hit1 = getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.0, true));
				if (target instanceof Player) {
					Player p2 = (Player) target;
					if (hit1.getDamage() > 0)
						p2.setPrayerDelay(5000);// 5 seconds
				}
				delayNormalHit(weaponId, attackStyle, hit1);
				break;
			case 15259: // dragon pickaxe
				player.setNextAnimationForce(new Animation(12031));
				player.setNextGraphics(new Graphics(2144));
				delayNormalHit(weaponId, attackStyle,
						getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.1, true)));
				break;
			case 1215: // dragon dagger
			case 5698: // dds
				player.setNextAnimationForce(new Animation(1062));
				player.setNextGraphics(new Graphics(252, 0, 100));
				delayNormalHit(weaponId, attackStyle,
						getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.15, true)),
						getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.15, true)));
				break;
			case 1434: // dragon mace
				player.setNextAnimationForce(new Animation(1060));
				player.setNextGraphics(new Graphics(251));
				delayNormalHit(weaponId, attackStyle,
						getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.45, true)));
				break;
			default:
				player.sendMessage("This weapon has no special Attack.");
				return combatDelay;
			}
		} else {
			if (weaponId == -2 && Utils.random(30) < 2) {
				player.setNextAnimationForce(new Animation(14417));
				final int attack = attackStyle;
				attackTarget(getMultiAttackTargets(player, 5, Integer.MAX_VALUE), new MultiAttack() {

					private boolean nextTarget;

					@Override
					public boolean attack() {
						target.addFreezeDelay(10000, true);
						target.setNextGraphics(new Graphics(181, 0, 96));
						final Entity t = target;
						WorldTasksManager.schedule(new WorldTask() {
							@Override
							public void run() {
								final int damage = getRandomMaxHit(player, -2, attack, false, false, 1.0, false);
								t.applyHit(new Hit(player, damage, HitLook.REGULAR_DAMAGE));

								stop();
							}
						}, 1);
						if (target instanceof Player) {
							Player p = (Player) target;
							for (int i = 0; i < 7; i++) {
								if (i != 3 && i != 5)
									p.getSkills().drainLevel(i, 7);
							}
							p.getPackets().sendGameMessage("Your stats have been drained!");
						}
						if (!nextTarget)
							nextTarget = true;
						return nextTarget;

					}

				});
				return combatDelay;
			}
			delayNormalHit(weaponId, attackStyle,
					getMeleeHit(player, getRandomMaxHit(player, weaponId, attackStyle, false)));
			player.setNextAnimationForce(new Animation(getWeaponAttackEmote(weaponId, attackStyle, player)));
		}
		return combatDelay;
	}

	public void playSound(int soundId, Player player, Entity target) {
		if (soundId == -1)
			return;
		player.getPackets().sendSound(soundId, 0, 1);
		if (target instanceof Player) {
			Player p2 = (Player) target;
			p2.getPackets().sendSound(soundId, 0, 1);
		}
	}

	public static int getSpecialAmmount(int weaponId) {
		switch (weaponId) {
		case 4587: // dragon sci
		case 859: // magic longbow
		case 861: // magic shortbow
		case 10284: // Magic composite bow
		case 18332: // Magic longbow (sighted)
		case 19149:// zamorak bow
		case 19151:
		case 19143:// saradomin bow
		case 19145:
		case 19146:
		case 19148:// guthix bow
			return 55;
		case 11235: // dark bows
		case 15702:
		case 15704:
		case 15259:
			return 65;
		case 13899: // vls
		case 13901:
		case 1305: // dragon long
		case 1215: // dragon dagger
		case 5698: // dds
		case 1434: // dragon mace
		case 1249:// d spear
		case 1263:
		case 3176:
		case 5716:
		case 5730:
		case 13770:
		case 13772:
		case 13774:
		case 13776:
		case 24882:
		case 24694:
			return 25;
		case 15442:// whip start
		case 15443:
		case 15444:
		case 15441:
		case 4151:
		case 23691:
		case 11698: // sgs
		case 23681:
		case 11694: // ags
		case 23679:
		case 13902: // statius hammer
		case 13904:
		case 13905: // vesta spear
		case 13907:
		case 14484: // d claws
		case 23695:
		case 10887: // anchor
		case 3204: // d hally
		case 4153: // granite maul
		case 14684: // zanik cbow
		case 15241: // hand cannon
		case 13908:
		case 13954:// morrigan javelin
		case 13955:
		case 13956:
		case 13879:
		case 13880:
		case 13881:
		case 13882:
		case 13883:// morigan thrown axe
		case 13957:
		case 6739:
			return 45;
		case 11730: // ss
		case 23690:
		case 11696: // bgs
		case 23680:
		case 11700: // zgs
		case 23682:
		case 35:// Excalibur
		case 8280:
		case 14632:
		case 1377:// dragon battle axe
		case 13472:
		case 15486:// staff of lights
		case 22207:
		case 22209:
		case 22211:
		case 22213:
		case 7158:
		case 15703: // white bow
			return 75;
		case 15701: // custom bow
		case 11061:
			return 100;
		case 19784: // korasi sword
			return 60;
		case 26595:
		case 29922:
		case 24455:
		case 24456: // decimation
			return 50;
		case 24457:
			return 100;
		case 20821: // thok's sword
			return 25;

		default:
			return 0;
		}
	}

	public int getRandomMaxHit(Player player, int weaponId, int attackStyle, boolean ranging) {
		return getRandomMaxHit(player, weaponId, attackStyle, ranging, true, 1.0D, false);
	}

	public int getRandomMaxHit(Player player, int weaponId, int attackStyle, boolean ranging, boolean defenceAffects,
			double specMultiplier, boolean usingSpec) {
		max_hit = getMaxHit(player, weaponId, attackStyle, ranging, usingSpec, specMultiplier);
		if (defenceAffects) {
			double att = player.getSkills().getLevel(ranging ? 4 : 0) + player.getCombatDefinitions()
					.getBonuses()[ranging ? 4 : CombatDefinitions.getMeleeBonusStyle(weaponId, attackStyle)];
			if (weaponId == -2) {
				att += 82;
			}
			att *= ranging ? player.getPrayer().getRangeMultiplier() : player.getPrayer().getAttackMultiplier();
			if (fullVoidEquipped(player, ranging ? (new int[] { 11664, 11675 }) : (new int[] { 11665, 11676 })))
				att *= 1.15;
			if (berserkerNecklace(player))
				att *= 1.3;
			if (player.getEquipment().getAmuletId() == 31872)
				att += Utils.random(1.01, 1.3);
			if (target instanceof NPC) {
				// CORP
				if (((NPC) target).getId() == 8133) {
					weaponId = player.getEquipment().getWeaponId();
					if (weaponId == 11716 || weaponId == 13454 || weaponId == 23683)
						att *= 1.25;
				}
			}
			if (player.getContract() != null) {
				if (player.getContract().hasCompleted() != true) {
					if (target instanceof NPC) {
						String npcName = NPCDefinitions.getNPCDefinitions(player.getContract().getNpcId()).getName()
								.toLowerCase();
						NPC n = (NPC) target;
						if (n.getDefinitions().name.toLowerCase().contains(npcName)) {
							if (hasReaperHood(player))
								att *= 1.05;
						}
					}
				}
			}
			if (player.getSlayerManager() != null) {
				if (target instanceof NPC) {
					if (player.getEquipment().getHatId() == 15488 || Slayer.hasFullSlayerHelmet(player)) {// TODO
						if (player.getSlayerManager().isValidTask(((NPC) target).getName()))
							att *= 1.15;
					}
				}
			}
			if (target instanceof NPC) {
				NPC n = (NPC) target;
				if (n.getDefinitions().name.toLowerCase().contains("dragon")) {
					Item ring = player.getEquipment().getItem(Equipment.SLOT_RING);
					if (ring != null && ring.getName().toLowerCase().contains("kethsi ring"))
						att *= 1.04;
				}
			}
			if (ranging)
				att *= player.getAuraManager().getRangeAccurayMultiplier();
			double def = 0;
			if (target instanceof Player) {
				Player p2 = (Player) target;
				def = (double) p2.getSkills().getLevel(Skills.DEFENCE)
						+ (2 * p2.getCombatDefinitions().getBonuses()[ranging ? 9
								: CombatDefinitions.getMeleeDefenceBonus(
										CombatDefinitions.getMeleeBonusStyle(weaponId, attackStyle))]);

				def *= p2.getPrayer().getDefenceMultiplier();
				if (!ranging) {
					if (p2.getFamiliar() instanceof Steeltitan)
						def *= 1.15;
				}
			} else {
				NPC n = (NPC) target;
				def = n.getBonuses() != null ? n.getBonuses()[ranging ? 9
						: CombatDefinitions
								.getMeleeDefenceBonus(CombatDefinitions.getMeleeBonusStyle(weaponId, attackStyle))]
						: 0;
				def *= 2;
				if (n.getId() == 1160 && fullVeracsEquipped(player))
					def *= 0.6;
			}
			if (usingSpec) {
				double multiplier = /* 0.25 + */getSpecialAccuracyModifier(player);
				att *= multiplier;
			}
			if (def < 0) {
				def = -def;
				att += def;
			}
			double prob = att / def;

			if (prob > 0.90) // max, 90% prob hit so even lvl 138 can miss at
				// lvl 3
				prob = 0.90;
			else if (prob < 0.05) // minimun 5% so even lvl 3 can hit lvl 138
				prob = 0.05;
			if ((Defenders.isRepriser(player.getEquipment().getShieldId())
					|| Defenders.isDefender(player.getEquipment().getShieldId())) && player.defenderPassive) {
				prob = 1.0;
				player.defenderPassive = false;
			}
			if (prob < Math.random())
				return 0;
		}
		int hit = Utils.random(max_hit + 1);
		if (target instanceof NPC) {
			NPC n = (NPC) target;
			if (n.getId() == 9463 && hasFireCape(player))
				hit += 40;
			if (n.getId() == 1958) {
				player.damagepoints++;
				if (!DummyEntry.reachedDamageCap(player.getSession().getIP())) {
					// if((player.isVeteran() && player.inVeteranGuild())) {

					int multiplier = player.TentMulti + 1;
					hit += (hit / 100) * multiplier;
					int damage = hit * player.damagepoints;
					;
					if (hit >= 10000)
						hit = 9999;
					if (player.getEquipment().getGlovesId() == 22358 && player.getEquipment().getWeaponId() == -1) {
						hit = 0;
						player.sendMessage("Your Goliath Gloves have no affect on the dummy!");
					}
					// player.checkNewAFKAction();

					player.getInventory().addItem(13652, hit * 3);

					player.applyHit(new Hit(player, Utils.getRandom(1) + hit / 10, HitLook.POISON_DAMAGE));
					if (player.isSendTentiDetails())
						player.sendMessage("Your " + player.TentMulti + "% Multiplier hits " + hit
								+ " on the mummy, receiving  damage tokens!");
					if (hit >= 2000) {
						n.setNextForceTalk(new ForceTalk(player.getDisplayName() + " hit a " + hit + " the bastard!"));

					}

					DummyEntry.addDamage(player.getUsername(), hit);

				}
			}

		}
		if (player.getAuraManager().usingEquilibrium()) {
			int perc25MaxHit = (int) (max_hit * 0.25);
			hit -= perc25MaxHit;
			max_hit -= perc25MaxHit;
			if (hit < 0)
				hit = 0;
			if (hit < perc25MaxHit)
				hit += perc25MaxHit;

		}
		if (fullGuthanEquipped(player)) {
			if (Utils.random(10) == 0) {
				if (hit > 0) {
					target.setNextGraphics(new Graphics(398));
					player.heal(hit / 2);
				}
			}
		}

		final boolean hasOffhand = new Item(player.getEquipment().getShieldId()).getName().contains("Off-hand");
		if (hasOffhand) {
			if (!player.isMainhand) {
				// System.out.println("secondary");
				hit = (int) (hit * 0.85);
			}
		}
		// System.out.println("Hit: " + hit + " : hasOffhand ? " + hasOffhand);
		return hit;
	}

	private final int getMaxHit(Player player, int weaponId, int attackStyle, boolean ranging, boolean usingSpec,
			double specMultiplier) {
		if (!ranging) {
			double strengthLvl = player.getSkills().getLevel(Skills.STRENGTH);
			int xpStyle = CombatDefinitions.getXpStyle(weaponId, attackStyle);
			double styleBonus = xpStyle == Skills.STRENGTH ? 3 : xpStyle == CombatDefinitions.SHARED ? 1 : 0;
			double otherBonus = 1;
			if (fullDharokEquipped(player)) {
				double hp = player.getHitpoints();
				double maxhp = player.getMaxHitpoints();
				double d = hp / maxhp;
				otherBonus = 2 - d;
			}
			double effectiveStrength = 8
					+ Math.floor((strengthLvl * player.getPrayer().getStrengthMultiplier()) + styleBonus);
			if (fullVoidEquipped(player, 11665, 11676))
				effectiveStrength = Math.floor(effectiveStrength * 1.15);
			if (berserkerNecklace(player))
				effectiveStrength = Math.floor(effectiveStrength * 1.3);
			if (target instanceof NPC) {
				if (((NPC) target).getId() == 8133) {
					weaponId = player.getEquipment().getWeaponId();
					if (weaponId == 11716 || weaponId == 13454 || weaponId == 23683) {
						effectiveStrength = Math.floor(effectiveStrength * 1.5);
					}
				}
			}
			if (player.getContract() != null) {
				if (player.getContract().hasCompleted() != true) {
					if (target instanceof NPC) {
						String npcName = NPCDefinitions.getNPCDefinitions(player.getContract().getNpcId()).getName()
								.toLowerCase();
						NPC n = (NPC) target;
						if (n.getDefinitions().name.toLowerCase().contains(npcName)) {
							if (hasReaperHood(player))
								effectiveStrength = Math.floor(effectiveStrength * 1.05);
						}

					}
				}
			}

			if (target instanceof NPC) {
				if (player.getEquipment().getHatId() == 15488 || Slayer.hasFullSlayerHelmet(player)) {// TODO
					if (player.getSlayerManager().isValidTask(((NPC) target).getName())) {
						effectiveStrength = Math.floor(effectiveStrength * 1.05);
					}
				}
			}

			double strengthBonus = player.getCombatDefinitions().getBonuses()[CombatDefinitions.STRENGTH_BONUS];
			if (weaponId == -2)
				strengthBonus += 82;
			double baseDamage = (5 + effectiveStrength * (1 + (strengthBonus / 64))) * specMultiplier * otherBonus;
			int damage = (int) Math
					.floor((player.defenderPassive && Defenders.isDefender(player.getEquipment().getShieldId())
							? Defenders.getModifier(player.getEquipment().getShieldId()) : 1) * baseDamage);
			if (player.defenderPassive && (Defenders.isDefender(player.getEquipment().getShieldId())
					|| Defenders.isRepriser(player.getEquipment().getShieldId()))) {
				player.sendMessage(Colors.cyan + "Your defender passive has dealt "
						+ (damage - ((int) baseDamage) + " bonus damage!"), true);
				player.defenderPassive = false;
			}
			return damage;
		} else {
			if (weaponId == 24338 && target instanceof Player)

			{
				player.sendMessage("The royal crossbow feels weak and unresponsive against other players.");
				return 60;
			}
			double rangedLvl = player.getSkills().getLevel(Skills.RANGE);
			double styleBonus = attackStyle == 0 ? 3 : attackStyle == 1 ? 0 : 1;
			double effectiveStrenght = Math.floor(rangedLvl * player.getPrayer().getRangeMultiplier()) + styleBonus;

			if (

			fullVoidEquipped(player, 11664, 11675))
				effectiveStrenght += Math.floor((player.getSkills().getLevelForXp(Skills.RANGE) / 5) + 1.3);
			if (player.getContract() != null) {
				if (player.getContract().hasCompleted() != true) {
					if (target instanceof NPC) {
						String npcName = NPCDefinitions.getNPCDefinitions(player.getContract().getNpcId()).getName()
								.toLowerCase();
						NPC n = (NPC) target;
						if (n.getDefinitions().name.toLowerCase().contains(npcName)) {
							if (hasReaperHood(player))
								effectiveStrenght = Math.floor(effectiveStrenght * 1.05);
						}

					}
				}
			}

			if (target instanceof NPC) {
				NPC n = (NPC) target;
				int hatId = player.getEquipment().getHatId();
				if ((hatId >= 8901 && hatId <= 8922) || Slayer.hasSlayerHelmet(player)) {// TODO
					if (player.getSlayerManager().isValidTask(((NPC) target).getName())) {
						effectiveStrenght = Math.floor(effectiveStrenght * 1.05);
					}
				}
			}

			double strengthBonus = player.getCombatDefinitions().getBonuses()[CombatDefinitions.RANGED_STR_BONUS];
			double baseDamage = 5 + (((effectiveStrenght + 8) * (strengthBonus + 64)) / 64);
			double damage = baseDamage * specMultiplier;
			if (player.defenderPassive && (Defenders.isDefender(player.getEquipment().getShieldId())
					|| Defenders.isRepriser(player.getEquipment().getShieldId()))) {
				player.sendMessage(Colors.cyan + "Your defender passive has dealt "
						+ (int) ((Defenders.getModifier(player.getEquipment().getShieldId()) * damage) - damage)
						+ " bonus damage!", true);
				player.defenderPassive = false;
			}
			return (int) Math.floor((player.defenderPassive && Defenders.isRepriser(player.getEquipment().getShieldId())
					? Defenders.getModifier(player.getEquipment().getShieldId()) : 1) * damage);
		}

	}

	private double getSpecialAccuracyModifier(Player player) {
		Item weapon = player.getEquipment().getItem(Equipment.SLOT_WEAPON);
		if (weapon == null)
			return 1;
		String name = weapon.getDefinitions().getName().toLowerCase();
		if (name.contains("whip") || name.contains("dragon longsword") || name.contains("dragon scimitar")
				|| name.contains("dragon dagger"))
			return 1.1;
		if (name.contains("anchor") || name.contains("magic longbow"))
			return 2;
		if (name.contains("dragon claw") || name.contains("armadyl godsword"))
			return 2;
		return 1;
	}

	public boolean hasFireCape(Player player) {
		int capeId = player.getEquipment().getCapeId();
		return capeId == 6570 || capeId == 20769 || capeId == 20771;
	}

	public static final boolean fullVanguardEquipped(Player player) {
		int helmId = player.getEquipment().getHatId();
		int chestId = player.getEquipment().getChestId();
		int legsId = player.getEquipment().getLegsId();
		int weaponId = player.getEquipment().getWeaponId();
		int bootsId = player.getEquipment().getBootsId();
		int glovesId = player.getEquipment().getGlovesId();
		if (helmId == -1 || chestId == -1 || legsId == -1 || weaponId == -1 || bootsId == -1 || glovesId == -1)
			return false;
		return ItemDefinitions.getItemDefinitions(helmId).getName().contains("Vanguard")
				&& ItemDefinitions.getItemDefinitions(chestId).getName().contains("Vanguard")
				&& ItemDefinitions.getItemDefinitions(legsId).getName().contains("Vanguard")
				&& ItemDefinitions.getItemDefinitions(weaponId).getName().contains("Vanguard")
				&& ItemDefinitions.getItemDefinitions(bootsId).getName().contains("Vanguard")
				&& ItemDefinitions.getItemDefinitions(glovesId).getName().contains("Vanguard");
	}

	public static final boolean usingGoliathGloves(Player player) {
		String name = player.getEquipment().getItem(Equipment.SLOT_SHIELD) != null
				? player.getEquipment().getItem(Equipment.SLOT_SHIELD).getDefinitions().getName().toLowerCase() : "";
		if (player.getEquipment().getItem((Equipment.SLOT_HANDS)) != null) {
			if (player.getEquipment().getItem(Equipment.SLOT_HANDS).getDefinitions().getName().toLowerCase()
					.contains("goliath") && player.getEquipment().getWeaponId() == -1) {
				if (name.contains("defender") && name.contains("dragonfire shield"))
					return true;
				return true;
			}
		}
		return false;
	}

	public static final boolean fullVeracsEquipped(Player player) {
		int helmId = player.getEquipment().getHatId();
		int chestId = player.getEquipment().getChestId();
		int legsId = player.getEquipment().getLegsId();
		int weaponId = player.getEquipment().getWeaponId();
		if (helmId == -1 || chestId == -1 || legsId == -1 || weaponId == -1)
			return false;
		return ItemDefinitions.getItemDefinitions(helmId).getName().contains("Verac's")
				&& ItemDefinitions.getItemDefinitions(chestId).getName().contains("Verac's")
				&& ItemDefinitions.getItemDefinitions(legsId).getName().contains("Verac's")
				&& ItemDefinitions.getItemDefinitions(weaponId).getName().contains("Verac's");
	}

	public static final boolean fullDharokEquipped(Player player) {
		int helmId = player.getEquipment().getHatId();
		int chestId = player.getEquipment().getChestId();
		int legsId = player.getEquipment().getLegsId();
		int weaponId = player.getEquipment().getWeaponId();
		if (helmId == -1 || chestId == -1 || legsId == -1 || weaponId == -1)
			return false;
		return ItemDefinitions.getItemDefinitions(helmId).getName().contains("Dharok's")
				&& ItemDefinitions.getItemDefinitions(chestId).getName().contains("Dharok's")
				&& ItemDefinitions.getItemDefinitions(legsId).getName().contains("Dharok's")
				&& ItemDefinitions.getItemDefinitions(weaponId).getName().contains("Dharok's");
	}

	public static final boolean fullVoidEquipped(Player player, int... helmid) {
		boolean hasDeflector = player.getEquipment().getShieldId() == 19712;
		if (player.getEquipment().getGlovesId() != 8842) {
			if (hasDeflector)
				hasDeflector = false;
			else
				return false;
		}
		int legsId = player.getEquipment().getLegsId();
		boolean hasLegs = legsId != -1 && (legsId == 8840 || legsId == 19786 || legsId == 19788 || legsId == 19790);
		if (!hasLegs) {
			if (hasDeflector)
				hasDeflector = false;
			else
				return false;
		}
		int torsoId = player.getEquipment().getChestId();
		boolean hasTorso = torsoId != -1
				&& (torsoId == 8839 || torsoId == 10611 || torsoId == 19785 || torsoId == 19787 || torsoId == 19789);
		if (!hasTorso) {
			if (hasDeflector)
				hasDeflector = false;
			else
				return false;
		}
		if (hasDeflector)
			return true;
		int helmId = player.getEquipment().getHatId();
		if (helmId == -1)
			return false;
		boolean hasHelm = false;
		for (int id : helmid) {
			if (helmId == id) {
				hasHelm = true;
				break;
			}
		}
		if (!hasHelm)
			return false;
		return true;
	}

	public void delayNormalHit(int weaponId, int attackStyle, Hit... hits) {
		delayHit(0, weaponId, attackStyle, hits);
	}

	public Hit getMeleeHit(Player player, int damage) {
		return new Hit(player, damage, HitLook.MELEE_DAMAGE);
	}

	public Hit getRangeHit(Player player, int damage) {
		return new Hit(player, damage, HitLook.RANGE_DAMAGE);
	}

	public Hit getMagicHit(Player player, int damage) {
		return new Hit(player, damage, HitLook.MAGIC_DAMAGE);
	}

	private void delayMagicHit(int delay, final Hit... hits) {
		delayHit(delay, -1, -1, hits);
	}

	public void resetVariables() {
		base_mage_xp = 0;
		mage_hit_gfx = 0;
		magic_sound = 0;
		max_poison_hit = 0;
		freeze_time = 0;
		reduceAttack = false;
		blood_spell = false;
		block_tele = false;
	}

	private void delayHit(int delay, final int weaponId, final int attackStyle, final Hit... hits) {
		addAttackedByDelay(hits[0].getSource(), target); // called separately
															// since some spells
															// dont do dmg

		final Entity target = this.target;
		final int max_hit = this.max_hit;
		final double base_mage_xp = this.base_mage_xp;
		final int mage_hit_gfx = this.mage_hit_gfx;
		final int magic_sound = this.magic_sound;
		final int max_poison_hit = this.max_poison_hit;
		final int freeze_time = this.freeze_time;
		final boolean blood_spell = this.blood_spell;
		final boolean block_tele = this.block_tele;
		// resetVariables();

		for (Hit hit : hits) {
			Player player = (Player) hit.getSource();
			if (target instanceof Player) {
				Player p2 = (Player) target;
				if (player.getPrayer().usingPrayer(1, 18))
					p2.sendSoulSplit(hit, player);
			}
			int damage = hit.getDamage() > target.getHitpoints() ? target.getHitpoints() : hit.getDamage();
			if (target instanceof NPC) {
				NPC tg = (NPC) target;
				if (tg.getCapDamage() > -1) {
					if (hit.getDamage() > tg.getCapDamage())
						hit.setDamage(tg.getCapDamage());
				}
			}
			damage = hit.getDamage() > target.getHitpoints() ? target.getHitpoints() : hit.getDamage();
			if (hit.getLook() == HitLook.RANGE_DAMAGE || hit.getLook() == HitLook.MELEE_DAMAGE) {
				double combatXp = damage / 2.5;
				if (combatXp > 0) {
					player.getAuraManager().checkSuccefulHits(hit.getDamage());
					if (hit.getLook() == HitLook.RANGE_DAMAGE) {
						if (attackStyle == 2) {
							player.getSkills().addXp(Skills.RANGE, combatXp / 2);
							player.getSkills().addXp(Skills.DEFENCE, combatXp / 2);
						} else
							player.getSkills().addXp(Skills.RANGE, combatXp);

					} else {
						int xpStyle = CombatDefinitions.getXpStyle(weaponId, attackStyle);
						if (xpStyle != CombatDefinitions.SHARED)
							player.getSkills().addXp(xpStyle, combatXp);
						else {
							player.getSkills().addXp(Skills.ATTACK, combatXp / 3);
							player.getSkills().addXp(Skills.STRENGTH, combatXp / 3);
							player.getSkills().addXp(Skills.DEFENCE, combatXp / 3);
						}
					}
					double hpXp = damage / 7.5;
					if (hpXp > 0)
						player.getSkills().addXp(Skills.HITPOINTS, hpXp);
				}
			} else if (hit.getLook() == HitLook.MAGIC_DAMAGE) {
				if (mage_hit_gfx != 0 && damage > 0) {
					if (freeze_time > 0) {
						target.addFreezeDelay(freeze_time, freeze_time == 0);
						if (target instanceof Player)
							((Player) target).stopAll(false);
						target.addFrozenBlockedDelay(freeze_time + (4 * 1000));
					}
				} else if (damage < 0)
					damage = 0;
				double combatXp = base_mage_xp * 1 + (damage / 5);

				if (combatXp > 0) {
					player.getAuraManager().checkSuccefulHits(hit.getDamage());
					if (player.getCombatDefinitions().isDefensiveCasting()
							|| (hasPolyporeStaff(player) && player.getCombatDefinitions().getAttackStyle() == 1)
							|| (hasNoxiousStaff(player) && player.getCombatDefinitions().getAttackStyle() == 1)
							|| (hasSeismicWand(player) && player.getCombatDefinitions().getAttackStyle() == 1)
							|| (hasCywirWand(player) && player.getCombatDefinitions().getAttackStyle() == 1)) {
						int defenceXp = (int) (damage / 7.5);
						if (defenceXp > 0) {
							combatXp -= defenceXp;
							player.getSkills().addXp(Skills.DEFENCE, defenceXp / 7.5);
						}
					}
					player.getSkills().addXp(Skills.MAGIC, combatXp);
					double hpXp = damage / 7.5;
					if (hpXp > 0)
						player.getSkills().addXp(Skills.HITPOINTS, hpXp);
				}
			}
		}
		// TODO Add off-hand combat here

		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				for (Hit hit : hits) {
					boolean splash = false;
					Player player = (Player) hit.getSource();
					if (player.isDead() || player.hasFinished() || target.isDead() || target.hasFinished())
						return;
					if (player.getEquipment().getWeaponId() != 33590) {
						if (hit.getDamage() > -1) {
							target.applyHit(hit);
						} else {
							splash = true;
							hit.setDamage(0);
						}
					}
					doDefenceEmotes();
					int damage = hit.getDamage() > target.getHitpoints() ? target.getHitpoints() : hit.getDamage();
					if ((damage >= max_hit * 0.90) && (hit.getLook() == HitLook.MAGIC_DAMAGE
							|| hit.getLook() == HitLook.RANGE_DAMAGE || hit.getLook() == HitLook.MELEE_DAMAGE))
						hit.setCriticalMark();
					if (hit.getLook() == HitLook.RANGE_DAMAGE || hit.getLook() == HitLook.MELEE_DAMAGE) {
						double combatXp = damage / 2.5;
						if (combatXp > 0) {
							if (hit.getLook() == HitLook.RANGE_DAMAGE) {
								if (weaponId != -1) {
									String name = ItemDefinitions.getItemDefinitions(weaponId).getName();
									if (name.contains("(p++)")) {
										if (Utils.getRandom(8) == 0)
											target.getPoison().makePoisoned(48);
									} else if (name.contains("(p+)")) {
										if (Utils.getRandom(8) == 0)
											target.getPoison().makePoisoned(38);
									} else if (name.contains("(p)")) {
										if (Utils.getRandom(8) == 0)
											target.getPoison().makePoisoned(28);
									}
								}
							} else {
								if (weaponId != -1) {
									String name = ItemDefinitions.getItemDefinitions(weaponId).getName();
									if (name.contains("(p++)")) {
										if (Utils.getRandom(8) == 0)
											target.getPoison().makePoisoned(68);
									} else if (name.contains("(p+)")) {
										if (Utils.getRandom(8) == 0)
											target.getPoison().makePoisoned(58);
									} else if (name.contains("(p)")) {
										if (Utils.getRandom(8) == 0)
											target.getPoison().makePoisoned(48);
									}
									if (target instanceof Player) {
										if (((Player) target).getPolDelay() >= Utils.currentTimeMillis())
											target.setNextGraphics(new Graphics(2320));
									}
								}
							}
						}
					} else if (hit.getLook() == HitLook.MAGIC_DAMAGE) {
						if (splash) {
							target.setNextGraphics(new Graphics(85, 0, 96));
							playSound(227, player, target);
						} else {
							if (mage_hit_gfx != 0) {
								target.setNextGraphics(
										new Graphics(mage_hit_gfx, 0, mage_hit_gfx == 369 || mage_hit_gfx == 1843
												|| (mage_hit_gfx > 1844 && mage_hit_gfx < 1855) ? 0 : 96));
								if (blood_spell)
									player.heal(damage / 4);
								if (block_tele) {
									if (target instanceof Player) {
										Player targetPlayer = (Player) target;
										targetPlayer.setTeleBlockDelay((targetPlayer.getPrayer().usingPrayer(0, 17)
												|| targetPlayer.getPrayer().usingPrayer(1, 7) ? 100000 : 300000));
										targetPlayer.sendMessage("You have been teleblocked.", true);
									}
								}
							}
							if (magic_sound > 0)
								playSound(magic_sound, player, target);
						}
					}
					if (max_poison_hit > 0 && Utils.getRandom(10) == 0) {
						if (!target.getPoison().isPoisoned())
							target.getPoison().makePoisoned(max_poison_hit);
					}
					if (target instanceof Player) {
						Player p2 = (Player) target;
						// p2.closeInterfaces();
						if (p2.getCombatDefinitions().isAutoRelatie() && !p2.getActionManager().hasSkillWorking()
								&& !p2.hasWalkSteps())
							p2.getActionManager().setAction(new PlayerCombat(player));
					} else {
						NPC n = (NPC) target;
						if (!n.isUnderCombat() || n.canBeAttackedByAutoRetaliate())
							n.setTarget(player);
					}
				}
			}
		}, delay);
	}

	public static int getWeaponAttackEmote(int weaponId, int attackStyle, Player player) {
		final ItemDefinitions weapon = (weaponId == -1 ? null : ItemDefinitions.getItemDefinitions(weaponId));
		final Item shield = player.getEquipment().getItem(Equipment.SLOT_SHIELD);
		String weaponName = ItemDefinitions.getItemDefinitions(weaponId).getName().toLowerCase();
		final boolean hasOffhand = new Item(player.getEquipment().getShieldId()).getName().toLowerCase().contains("off-hand")
				|| new Item(player.getEquipment().getShieldId()).getName().toLowerCase().contains("offhand")
				|| new Item(player.getEquipment().getShieldId()).getName().toLowerCase().contains("defender")
				|| new Item(player.getEquipment().getShieldId()).getName().toLowerCase().contains("repriser");
		
		if (weaponName.contains("rapier") || weaponName.contains("darklight") || weaponName.contains("longsword") ||weaponName.contains("Korasi's") ) {
			switch (attackStyle) {
			case 2:
				return 18241;
			default:
				return 18226;
			}
		}
		if (weaponName.contains("staff") || weaponName.contains("maul") || weaponName.contains("mace")) {
			switch (attackStyle) {
			case 2:
				return 18222;
			default:
				return 18222;
			}
		}
		if (weaponId == -2) {
			switch (attackStyle) {
			case 1:
				return 14307;
			default:
				return 14393;
			}
		}
		if (weaponName.contains("dagger")) {
			switch (attackStyle) {
			case 1:
				return 1060;
			default:
				return 1062;
			}
		}
		
		if (weaponName.contains("chinchompa"))
			return 2779;
		if (weaponName.contains("staff of light")) {
			switch (attackStyle) {
			case 0:
				return 15072;
			case 1:
				return 15071;
			case 2:
				return 414;
			}
		}
		if (weaponName.contains("2h sword") || weaponName.equals("dominion sword") || weaponName.equals("thok's sword")
				|| weaponName.equals("saradomin sword")) {
			switch (attackStyle) {
			case 2:
				return 7048;
			case 3:
				return 7049;
			default:
				return 7041;
			}
		}
		if (weaponName.contains("whip")) {
			switch (attackStyle) {
			case 1:
				return 23936;
			default:
				return 23936;
			}
		}
		if (weaponName.contains("scythe")) {
			switch (attackStyle) {
			case 0:
				return 18236;
			case 1:
				return 440;
			case 2:
				return 16284;
			}
		}
		if (weaponName.contains("godsword")) {
			switch (attackStyle) {
			case 0:
				return 23914;
			case 1:
				return 7048;
			case 2:
				return 7041;
			}
		}
		if (weaponId == 15241) {
			return 12174;
		}

		if (weapon != null) {
			if (player.isMainhand || !hasOffhand) {
				if (weapon.getMainhandEmote(LEGACY) != -1) {
					player.isMainhand = false;
					return weapon.getMainhandEmote(LEGACY);
				}
			} else if (!player.isMainhand) {
				if (shield != null && shield.getDefinitions() != null) {
					if (shield.getDefinitions().getOffhandEmote(LEGACY) != -1) {
						player.isMainhand = true;
						return shield.getDefinitions().getOffhandEmote(LEGACY);
					} else {
						if (weapon.getMainhandEmote(LEGACY) != -1) {
							player.isMainhand = true;
							return weapon.getMainhandEmote(LEGACY);
						}
						if (weapon.isShield()) {
							final ItemDefinitions d = ItemDefinitions
									.getItemDefinitions(player.getEquipment().getWeaponId());
							if (d != null && d.id != -1) {
								if (d.getMainhandEmote(LEGACY) != -1) {
									player.isMainhand = true;
									return d.getMainhandEmote(LEGACY);
								}
							}
						}

						// return (attackStyle == 1 ? 423 : 422);
					}
				} else {
					if (weapon.getMainhandEmote(LEGACY) != -1) {
						player.isMainhand = true;
						return weapon.getMainhandEmote(LEGACY);
					}
					// return (attackStyle == 1 ? 423 : 422);
				}
			}
		} else {
			if (player.isMainhand) {
				return (attackStyle == 1 ? 423 : 422);
			} else {
				if (shield != null && shield.getDefinitions() != null) {
					if (shield.getDefinitions().getOffhandEmote(LEGACY) != -1) {
						player.isMainhand = false;
						return shield.getDefinitions().getOffhandEmote(LEGACY);
					} else {
						return (attackStyle == 1 ? 423 : 422);
					}
				}
			}
		}
		return (attackStyle == 1 ? 423 : 422);
	}

	// public static int getWeaponAttackEmote(int weaponId, int attackStyle) {
	// if (weaponId != -1) {
	// if (weaponId == -2) {
	// switch (attackStyle) {
	// case 1:
	// return 14307;
	// default:
	// return 14393;
	// }
	// }
	// Item item = new Item(weaponId);
	// return item.getDefinitions().getCombatOpcode(2914);
	// }
	// return -1;
	// }

	private void doDefenceEmotes() {
		target.setNextAnimationNoPriority(new Animation(Combat.getDefenceEmote(target)));
	}

	private static Item getWeapon(Player player) {
		return player.getEquipment().getItem(Equipment.SLOT_WEAPON);
	}

	public static int getAttackSpeed(Player player) {
		Item item = getWeapon(player);
		if (item == null)
			return 4;
		String name = item.getName().toLowerCase();
		// exceptions since RS3 is slower attacks due to off-hands
		if ((name.contains("rapier") || name.contains("dagger") || name.contains(" sword") || name.contains("scimitar")
				|| name.contains("whip")) && (!name.contains("2h") && !name.contains("dominion")))
			return 2;
		if ((name.contains("scythe") || name.contains("lance")))
			return 3;
		final boolean hasOffhand = new Item(player.getEquipment().getShieldId()).getName().contains("Off-hand");
		final boolean hasDefender = new Item(player.getEquipment().getShieldId()).getName().contains("defender");
		final boolean hasOffCrossbow = new Item(player.getEquipment().getShieldId()).getName().contains("crossbow");
		final boolean hasRepriser = new Item(player.getEquipment().getShieldId()).getName().contains("repriser");
		if (hasOffhand && !new Item(player.getEquipment().getShieldId()).getName().contains("bow") ||
			hasDefender && !new Item(player.getEquipment().getShieldId()).getName().contains("bow")) {
			return item.getDefinitions().getAttackSpeed() / 2;
		}
		if (name.contains("crossbow") && hasOffCrossbow || name.contains("crossbow") && hasRepriser) {
			return 2;
		}
		if (item.getId() == 18357 && player.getEquipment().getShieldId() == 25995) {
				return 2;
		}
		return item.getDefinitions().getAttackSpeed();
	}

	@Override
	public void stop(final Player player) {
		player.setNextFaceEntity(null);
		player.resetCombat();
	}

	private boolean checkAll(Player player) {
		// checks if target is ingame and not ded
		if (player.isDead() || player.hasFinished() || player.isCantWalk())
			return false;
		if (target.isDead() || target.hasFinished()) {
			// fixes fact u had already started attacked and then target died
			player.setNextAnimation(new Animation(-1));
			return false;
		}
		// checks if player appears in viewport
		if (!player.withinDistance(target, 16))
			return false;
		if (player.getFreezeDelay() >= Utils.currentTimeMillis()) {
			if (player.withinDistance(target, 0))// done
				return false;
			return true;
		}
		if (target instanceof Player) {
			Player p2 = (Player) target;
			if (!player.isCanPvp() || !p2.isCanPvp())
				return false;
		} else {
			NPC n = (NPC) target;
			if (n.isCantInteract())
				return false;

			if (n instanceof Familiar) {
				Familiar familiar = (Familiar) n;
				if (!familiar.canAttack(target))
					return false;
			} else {
				if (!n.canBeAttackFromOutOfArea() && !MapAreas.isAtArea(n.getMapAreaNameHash(), player))
					return false;
				if (!canAttackNPC(player, n))
					return false;
			}
		}
		if (!(target instanceof NPC && ((NPC) target).isForceMultiAttacked())) {
			if (!target.isAtMultiArea() || !player.isAtMultiArea()) {
				if (player.getAttackedBy() != target && player.getAttackedByDelay() > Utils.currentTimeMillis())
					return false;
				if (target.getAttackedBy() != player && target.getAttackedByDelay() > Utils.currentTimeMillis())
					return false;
			}
		}
		int distanceX = player.getX() - target.getX();
		int distanceY = player.getY() - target.getY();
		int size = target.getSize();
		int maxDistance = 16;
		int isRanging = isRanging(player);
		if (distanceX < size && distanceX > -1 && distanceY < size && distanceY > -1 && !target.hasWalkSteps()) {
			player.resetWalkSteps();
			if (!player.addWalkSteps(target.getX() + size, target.getY())) {
				player.resetWalkSteps();
				if (!player.addWalkSteps(target.getX() - 1, target.getY())) {
					player.resetWalkSteps();
					if (!player.addWalkSteps(target.getX(), target.getY() + size)) {
						player.resetWalkSteps();
						if (!player.addWalkSteps(target.getX(), target.getY() - 1))
							return false;
					}
				}
			}
			return true;
		} else if (isRanging == 0 && target.getSize() == 1 && player.getCombatDefinitions().getSpellId() <= 0
				&& !hasPolyporeStaff(player) && !hasNoxiousStaff(player) && !hasCywirWand(player) && !hasSeismicWand(player) && Math.abs(player.getX() - target.getX()) == 1
				&& Math.abs(player.getY() - target.getY()) == 1 && !target.hasWalkSteps()
				&& player.getEquipment().getWeaponId() != 33590) {
			if (!player.addWalkSteps(target.getX(), player.getY(), 1))
				player.addWalkSteps(player.getX(), target.getY(), 1);
			return true;
		}
		// xmas shit fuck you hezoos christmas snowball
		maxDistance = player.getEquipment().getWeaponId() == 33590 || isRanging != 0
				|| player.getCombatDefinitions().getSpellId() > 0 || hasNoxiousStaff(player) || hasCywirWand(player) || hasSeismicWand(player) || hasPolyporeStaff(player) ? 7 : 0;
		if ((!player.clipedProjectile(target, maxDistance == 0 && !forceCheckClipAsRange(target)))
				|| distanceX > size + maxDistance || distanceX < -1 - maxDistance || distanceY > size + maxDistance
				|| distanceY < -1 - maxDistance) {
			if (!player.hasWalkSteps()) {
				player.resetWalkSteps();
				player.addWalkStepsInteract(target.getX(), target.getY(), player.getRun() ? 2 : 1, size, true);
			}
			return true;
		} else
			player.resetWalkSteps();
		if (player.getPolDelay() >= Utils.currentTimeMillis() && !(player.getEquipment().getWeaponId() == 15486
				|| player.getEquipment().getWeaponId() == 22207 || player.getEquipment().getWeaponId() == 22209
				|| player.getEquipment().getWeaponId() == 22211 || player.getEquipment().getWeaponId() == 22213))
			player.setPolDelay(0);
		player.getTemporaryAttributtes().put("last_target", target);
		if (target != null)
			target.getTemporaryAttributtes().put("last_attacker", player);
		if (player.getCombatDefinitions().isInstantAttack()) {
			player.getCombatDefinitions().setInstantAttack(false);
			if (player.getCombatDefinitions().getAutoCastSpell() > 0)
				return true;
			if (player.getCombatDefinitions().isUsingSpecialAttack()) {
				if (!specialExecute(player))
					return true;
				player.getActionManager().setActionDelay(0);
				int weaponId = player.getEquipment().getWeaponId();
				int attackStyle = player.getCombatDefinitions().getAttackStyle();
				switch (weaponId) {
				case 4153:
					player.setNextAnimation(new Animation(1667));
					player.setNextGraphics(new Graphics(340, 0, 96 << 16));
					delayNormalHit(weaponId, attackStyle, getMeleeHit(player,
							getRandomMaxHit(player, weaponId, attackStyle, false, true, 1.1, true)));
					break;
				}
				player.getActionManager().setActionDelay(4);
			}
			return true;
		}
		return true;
	}

	/**
	 * Checks if the player can attack an NPC.
	 * 
	 * @return if can attack.
	 */
	private boolean canAttackNPC(Player player, NPC n) {
		// int slayerLevel = Combat.getSlayerLevelForNPC(n.getId());
		String npcName = n.getDefinitions().name.toLowerCase();
		if (n.getId() == 10021 && player.getEquipment().getWeaponId() != 33590) {
			player.sendMessage("You need to equip a snowball to do this!", false);
			return false;
		}
		int slayerLevel = Slayer.getLevelRequirement(n.getName());// TODO
		if (slayerLevel > player.getSkills().getLevel(Skills.SLAYER)) {
			player.getPackets().sendGameMessage(
					"You need a slayer level of " + slayerLevel + " to know how to wound this monster.");
			return false;
		}
		/*if (World.isAtKuradalsDungeon(player)) {
		if (player.getSlayerManager().isValidTask(n.getName())) {
			player.sendMessage("You can only attack this monster when on a Slayer task.");
			return false;
		}
	}
	if (npcName.equalsIgnoreCase("edimmu")) {
		if (player.getSlayerManager().isValidTask(n.getName())) {
			player.sendMessage("You can only attack this monster when on a Slayer task.");
			return false;
		}
	}*/
		if (player.getEquipment().getWeaponId() == 4084) {
			player.sendMessage("How would I do this wearing a sled?");
			return false;
		}
		if (n.getId() == 14578) {
			if (player.getEquipment().getWeaponId() != 2402 && player.getCombatDefinitions().getAutoCastSpell() <= 0
					&& !hasPolyporeStaff(player)) {
				player.sendMessage("I'd better wield a Silverlight first.");
				return false;
			}
		}
		if (n.getId() == 14301 || n.getId() == 14302 || n.getId() == 14303 || n.getId() == 14304) {
			Glacyte glacyte = (Glacyte) n;
			if (glacyte.getGlacor().getTargetIndex() != -1
					&& player.getIndex() != glacyte.getGlacor().getTargetIndex()) {
				player.sendMessage("This isn't your target.");
				return false;
			}
		}
		if (n.getId() == 6222 || n.getId() == 6223 || n.getId() == 6225 || n.getId() == 6227) {
			if (isRanging(player) == 0) {
				player.sendMessage("You can't reach that - you'll need a ranged weapon.");
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if we can execute the special attack move.
	 * 
	 * @param player
	 *            The player executing.
	 * @return if can use special attack.
	 */
	public static boolean specialExecute(Player player) {
		int weaponId = player.getEquipment().getWeaponId();
		player.getCombatDefinitions().switchUsingSpecialAttack();
		int specAmt = getSpecialAmmount(weaponId);
		if (specAmt == 0) {
			player.sendMessage("This weapon has no special attack move.");
			player.getCombatDefinitions().decreaseSpecialAttack(0);
			return false;
		}
		if (player.getCombatDefinitions().hasRingOfVigour())
			specAmt *= 0.9;
		if (player.getCombatDefinitions().getSpecialAttackPercentage() < specAmt) {
			player.sendMessage("You don't have enough special attack energy.");
			player.getCombatDefinitions().decreaseSpecialAttack(0);
			return false;
		}
		if (player.getEquipment().getWeaponId() == 14484) {
			if (player.getEquipment().getShieldId() != 25555 && player.getEquipment().getShieldId() != 25952) {
				player.sendMessage("You will need the off-hand Dragon claw in order to use the special attack.");
				return false;
			}
		}
		player.getCombatDefinitions().decreaseSpecialAttack(specAmt);
		return true;
	}

	/*
	 * 0 not ranging, 1 invalid ammo so stops att, 2 can range, 3 no ammo
	 */
	public static final int isRanging(Player player) {
		int ammoId = player.getEquipment().getAmmoId();
		if (player.getEquipment().getWeaponId() == 15241) {
			switch (ammoId) {
			case -1:
				return 3;
			case 15243: // bronze arrow
				return 2;
			default:
				return 1;
			}
		}
		String name = ItemDefinitions.getItemDefinitions(player.getEquipment().getWeaponId()).getName().toLowerCase();
		if (name != null) { // those dont need arrows
			if (name.contains("knife") || name.contains("dart") || name.contains("javelin")
					|| name.contains("thrownaxe") || name.contains("throwing axe") || name.contains("crystal bow")
					|| name.equalsIgnoreCase("zaryte bow") || name.contains("chinchompa") || name.contains("Decimation")
					|| name.contains("bolas"))
				return 2;
		}
		int wep = new Item(player.getEquipment().getWeaponId()).getName().contains("bow")
				? player.getEquipment().getWeaponId() : player.getEquipment().getShieldId();

		int weaponId = wep;// player.getEquipment().getWeaponId();
		// int offhandId = player.getEquipment().getShieldId();
		// System.out.println(player.isMainhand ? "mainhand" : "offhand");
		if (weaponId == -1)
			return 0;

		switch (weaponId) {

		case 839: // longbow
		case 841: // shortbow
			switch (ammoId) {
			case -1:
				return 3;
			case 882: // bronze arrow
			case 883: // bronze arrow (p)
			case 5616: // bronze arrow (p+)
			case 5622: // bronze arrow (p++)
			case 884: // iron arrow
			case 885: // iron arrow (p)
			case 5617: // iron arrow (p+)
			case 5623: // iron arrow (p++)
				return 2;
			default:
				return 1;
			}
		case 843: // oak longbow
		case 845: // oak shortbow
			switch (ammoId) {
			case -1:
				return 3;
			case 882: // bronze arrow
			case 883: // bronze arrow (p)
			case 5616: // bronze arrow (p+)
			case 5622: // bronze arrow (p++)
			case 884: // iron arrow
			case 885: // iron arrow (p)
			case 5617: // iron arrow (p+)
			case 5623: // iron arrow (p++)
			case 886: // steel arrow
			case 887: // steel arrow (p)
			case 5618: // steel arrow (p+)
			case 5624: // steel arrow (p++)
				return 2;
			default:
				return 1;
			}
		case 847: // willow longbow
		case 849: // willow shortbow
		case 13541: // Willow composite bow
			switch (ammoId) {
			case -1:
				return 3;
			case 882: // bronze arrow
			case 883: // bronze arrow (p)
			case 5616: // bronze arrow (p+)
			case 5622: // bronze arrow (p++)
			case 884: // iron arrow
			case 885: // iron arrow (p)
			case 5617: // iron arrow (p+)
			case 5623: // iron arrow (p++)
			case 886: // steel arrow
			case 887: // steel arrow (p)
			case 5618: // steel arrow (p+)
			case 5624: // steel arrow (p++)
			case 888: // mithril arrow
			case 889: // mithril arrow (p)
			case 5619: // mithril arrow (p+)
			case 5625: // mithril arrow (p++)
				return 2;
			default:
				return 1;
			}
		case 851: // maple longbow
		case 853: // maple shortbow
		case 18331: // Maple longbow (sighted)
			switch (ammoId) {
			case -1:
				return 3;
			case 882: // bronze arrow
			case 883: // bronze arrow (p)
			case 5616: // bronze arrow (p+)
			case 5622: // bronze arrow (p++)
			case 884: // iron arrow
			case 885: // iron arrow (p)
			case 5617: // iron arrow (p+)
			case 5623: // iron arrow (p++)
			case 886: // steel arrow
			case 887: // steel arrow (p)
			case 5618: // steel arrow (p+)
			case 5624: // steel arrow (p++)
			case 888: // mithril arrow
			case 889: // mithril arrow (p)
			case 5619: // mithril arrow (p+)
			case 5625: // mithril arrow (p++)
			case 890: // adamant arrow
			case 891: // adamant arrow (p)
			case 5620: // adamant arrow (p+)
			case 5626: // adamant arrow (p++)
				return 2;
			default:
				return 1;
			}
		case 2883:// ogre bow
			switch (ammoId) {
			case -1:
				return 3;
			case 2866: // ogre arrow
				return 2;
			default:
				return 1;
			}
		case 4827:// Comp ogre bow
			switch (ammoId) {
			case -1:
				return 3;
			case 2866: // ogre arrow
			case 4773: // bronze brutal
			case 4778: // iron brutal
			case 4783: // steel brutal
			case 4788: // black brutal
			case 4793: // mithril brutal
			case 4798: // adamant brutal
			case 4803: // rune brutal
				return 2;
			default:
				return 1;
			}
		case 855: // yew longbow
		case 857: // yew shortbow
		case 10281: // Yew composite bow
		case 14121: // Sacred clay bow
		case 859: // magic longbow
		case 861: // magic shortbow
		case 10284: // Magic composite bow
		case 18332: // Magic longbow (sighted)
		case 6724: // seercull
			switch (ammoId) {
			case -1:
				return 3;
			case 882: // bronze arrow
			case 883: // bronze arrow (p)
			case 5616: // bronze arrow (p+)
			case 5622: // bronze arrow (p++)
			case 884: // iron arrow
			case 885: // iron arrow (p)
			case 5617: // iron arrow (p+)
			case 5623: // iron arrow (p++)
			case 886: // steel arrow
			case 887: // steel arrow (p)
			case 5618: // steel arrow (p+)
			case 5624: // steel arrow (p++)
			case 888: // mithril arrow
			case 889: // mithril arrow (p)
			case 5619: // mithril arrow (p+)
			case 5625: // mithril arrow (p++)
			case 890: // adamant arrow
			case 891: // adamant arrow (p)
			case 5620: // adamant arrow (p+)
			case 5626: // adamant arrow (p++)
			case 892: // rune arrow
			case 893: // rune arrow (p)
			case 5621: // rune arrow (p+)
			case 5627: // rune arrow (p++)
			case 28464: // fragment arrows
				return 2;
			default:
				return 1;
			}
		case 16887:
		case 16337:
		case 11235: // dark bows
		case 15701:
		case 15702:
		case 15703:
		case 15704:
			switch (ammoId) {
			case -1:
				return 3;
			case 882: // bronze arrow
			case 883: // bronze arrow (p)
			case 5616: // bronze arrow (p+)
			case 5622: // bronze arrow (p++)
			case 884: // iron arrow
			case 885: // iron arrow (p)
			case 5617: // iron arrow (p+)
			case 5623: // iron arrow (p++)
			case 886: // steel arrow
			case 887: // steel arrow (p)
			case 5618: // steel arrow (p+)
			case 5624: // steel arrow (p++)
			case 888: // mithril arrow
			case 889: // mithril arrow (p)
			case 5619: // mithril arrow (p+)
			case 5625: // mithril arrow (p++)
			case 890: // adamant arrow
			case 891: // adamant arrow (p)
			case 5620: // adamant arrow (p+)
			case 5626: // adamant arrow (p++)
			case 892: // rune arrow
			case 893: // rune arrow (p)
			case 5621: // rune arrow (p+)
			case 5627: // rune arrow (p++)
			case 11212: // dragon arrow
			case 11227: // dragon arrow (p)
			case 11228: // dragon arrow (p+)
			case 11229: // dragon arrow (p++)
				return 2;
			default:
				return 1;
			}
		case 31733: // noxious
		case 31735:
		case 31736:
		case 33336:
		case 33337:
		case 33402:
		case 33403:
		case 33468:
		case 33469:
		case 36339:
		case 36340:
			switch (ammoId) {
			case -1:
				return 3;
			case 882: // bronze arrow
			case 883: // bronze arrow (p)
			case 5616: // bronze arrow (p+)
			case 5622: // bronze arrow (p++)
			case 884: // iron arrow
			case 885: // iron arrow (p)
			case 5617: // iron arrow (p+)
			case 5623: // iron arrow (p++)
			case 886: // steel arrow
			case 887: // steel arrow (p)
			case 5618: // steel arrow (p+)
			case 5624: // steel arrow (p++)
			case 888: // mithril arrow
			case 889: // mithril arrow (p)
			case 5619: // mithril arrow (p+)
			case 5625: // mithril arrow (p++)
			case 890: // adamant arrow
			case 891: // adamant arrow (p)
			case 5620: // adamant arrow (p+)
			case 5626: // adamant arrow (p++)
			case 892: // rune arrow
			case 893: // rune arrow (p)
			case 5621: // rune arrow (p+)
			case 5627: // rune arrow (p++)
			case 11212: // dragon arrow
			case 11227: // dragon arrow (p)
			case 11228: // dragon arrow (p+)
			case 11229: // dragon arrow (p++)
			case 31737: // araxyte arrow
				return 2;
			default:
				return 1;
			}
		case 19143: // saradomin bow
			switch (ammoId) {
			case -1:
				return 3;
			case 19152: // saradomin arrow
				return 2;
			default:
				return 1;
			}
		case 19146: // guthix bow
			switch (ammoId) {
			case -1:
				return 3;
			case 19157: // guthix arrow
				return 2;
			default:
				return 1;
			}
		case 19149: // zamorak bow
			switch (ammoId) {
			case -1:
				return 3;
			case 19162: // zamorak arrow
				return 2;
			default:
				return 1;
			}
		case 24338: // Royal crossbow
			switch (ammoId) {
			case -1:
				return 3;
			case 24336: // Coral bolts
				return 2;
			default:
				return 1;
			}
		case 24303: // Coral crossbow
			switch (ammoId) {
			case -1:
				return 3;
			case 24304: // Coral bolts
				return 2;
			default:
				return 1;
			}
		case 4734: // karil crossbow
		case 4934:
		case 4935:
		case 4936:
		case 4937:
		case 4938:
			switch (ammoId) {
			case -1:
				return 3;
			case 4740: // bolt rack
				return 2;
			default:
				return 1;
			}
		case 10156: // hunters crossbow
			switch (ammoId) {
			case -1:
				return 3;
			case 10158: // Kebbit bolts
			case 10159: // Long kebbit bolts
				return 2;
			default:
				return 1;
			}
		case 8880: // Dorgeshuun c'bow
			switch (ammoId) {
			case -1:
				return 3;
			case 877: // bronze bolts
			case 9140: // iron bolts
			case 8882: // bone bolts
				return 2;
			default:
				return 1;
			}
		case 14684: // zanik crossbow
			switch (ammoId) {
			case -1:
				return 3;
			case 877: // bronze bolts
			case 9140: // iron bolts
			case 9141: // steel bolts
			case 13083: // black bolts
			case 9142:// mithril bolts
			case 9143: // adam bolts
			case 9145: // silver bolts
			case 8882: // bone bolts
				return 2;
			default:
				return 1;
			}
		case 767: // phoenix crossbow
		case 837: // crossbow
			switch (ammoId) {
			case -1:
				return 3;
			case 877: // bronze bolts
				return 2;
			default:
				return 1;
			}
		case 9174: // bronze crossbow
			switch (ammoId) {
			case -1:
				return 3;
			case 877: // bronze bolts
			case 9236: // Opal bolts (e)
				return 2;
			default:
				return 1;
			}
		case 9176: // blurite crossbow
			switch (ammoId) {
			case -1:
				return 3;
			case 877: // bronze bolts
			case 9140: // iron bolts
			case 9141: // steel bolts
			case 13083: // black bolts
			case 9236: // Opal bolts (e)
			case 9238: // Pearl bolts (e)
			case 9239: // Topaz bolts (e)
			case 9139: // Blurite bolts
			case 9237: // Jade bolts (e)
				return 2;
			default:
				return 1;
			}
		case 9177: // iron crossbow
		case 25883: // offhand
			switch (ammoId) {
			case -1:
				return 3;
			case 877: // bronze bolts
			case 9140: // iron bolts
			case 9236: // Opal bolts (e)
			case 9238: // Pearl bolts (e)
				return 2;
			default:
				return 1;
			}
		case 9179: // steel crossbow
		case 25885:
			switch (ammoId) {
			case -1:
				return 3;
			case 877: // bronze bolts
			case 9140: // iron bolts
			case 9141: // steel bolts
			case 9236: // Opal bolts (e)
			case 9238: // Pearl bolts (e)
			case 9239: // Topaz bolts (e)
				return 2;
			default:
				return 1;
			}
		case 13081: // black crossbow
		case 33498:
			switch (ammoId) {
			case -1:
				return 3;
			case 877: // bronze bolts
			case 9140: // iron bolts
			case 9141: // steel bolts
			case 13083: // black bolts
			case 9236: // Opal bolts (e)
			case 9238: // Pearl bolts (e)
			case 9239: // Topaz bolts (e)
				return 2;
			default:
				return 1;
			}
		case 9181: // Mith crossbow
		case 25887:
			switch (ammoId) {
			case -1:
				return 3;
			case 877: // bronze bolts
			case 9140: // iron bolts
			case 9141: // steel bolts
			case 13083: // black bolts
			case 9142:// mithril bolts
			case 9145: // silver bolts
			case 9236: // Opal bolts (e)
			case 9238: // Pearl bolts (e)
			case 9239: // Topaz bolts (e)
			case 9240: // Sapphire bolts (e)
			case 9241: // Emerald bolts (e)
				return 2;
			default:
				return 1;
			}
		case 9183: // adam c bow
		case 25889:
			switch (ammoId) {
			case -1:
				return 3;
			case 877: // bronze bolts
			case 9140: // iron bolts
			case 9141: // steel bolts
			case 13083: // black bolts
			case 9142:// mithril bolts
			case 9143: // adam bolts
			case 9145: // silver bolts wtf
			case 9236: // Opal bolts (e)
			case 9238: // Pearl bolts (e)
			case 9239: // Topaz bolts (e)
			case 9240: // Sapphire bolts (e)
			case 9241: // Emerald bolts (e)
			case 9242: // Ruby bolts (e)
			case 9243: // Diamond bolts (e)
				return 2;
			default:
				return 1;
			}
		case 9185: // rune c bow
		case 25891: // offhand
		case 18357: // chaotic crossbow
		case 25995: // offhand
		case 25917: // Dragon Crossbow
		case 25894:
		case 22348: // dominion cross
		case 25037:
		case 18358:
		case 36176://tainted repriser
		case 36179://ancient repriser
			switch (ammoId) {
			case -1:
				return 3;
			case 877: // bronze bolts
			case 9140: // iron bolts
			case 9141: // steel bolts
			case 13083: // black bolts
			case 9142:// mithril bolts
			case 9143: // adam bolts
			case 9144: // rune bolts
			case 9145: // silver bolts wtf
			case 9236: // Opal bolts (e)
			case 9238: // Pearl bolts (e)
			case 9239: // Topaz bolts (e)
			case 9240: // Sapphire bolts (e)
			case 9241: // Emerald bolts (e)
			case 9242: // Ruby bolts (e)
			case 9243: // Diamond bolts (e)
			case 9244: // Dragon bolts (e)
			case 9341: // Dragon bolts
			case 9245: // Onyx bolts (e)
			case 24116: // Bakriminel bolts
			case 28463: // Fragment bolts
			case 13280: // broadtipped
				return 2;
			default:
				return 1;
			}
		case 28437: // ascension c'bow
		case 28441: // ascension c'bow
		case 28439:
		case 33318:
		case 33319:
		case 33384:
		case 33385:
		case 33450:
		case 33451:
		case 36321:
		case 36322:
		case 36181://kalphite repriser
			switch (ammoId) {
			case -1:
				return 3;
			case 877: // bronze bolts
			case 9140: // iron bolts
			case 9141: // steel bolts
			case 13083: // black bolts
			case 9142:// mithril bolts
			case 9143: // adam bolts
			case 9144: // rune bolts
			case 9145: // silver bolts wtf
			case 9236: // Opal bolts (e)
			case 9238: // Pearl bolts (e)
			case 9239: // Topaz bolts (e)
			case 9240: // Sapphire bolts (e)
			case 9241: // Emerald bolts (e)
			case 9242: // Ruby bolts (e)
			case 9243: // Diamond bolts (e)
			case 9244: // Dragon bolts (e)
			case 9341: // Dragon bolts
			case 9245: // Onyx bolts (e)
			case 24116: // Bakriminel bolts
			case 28463: // Fragment bolts
			case 28465: // Ascension bolts
			case 31868: // Ascendri bolts
				return 2;
			default:
				return 1;
			}
		default:
			return 0;
		}

	}

	/**
	 * Checks if the player is wielding polypore staff.
	 * 
	 * @param player
	 *            The player.
	 * @return {@code True} if so.
	 */
	private static boolean hasPolyporeStaff(Player player) {
		int weaponId = player.getEquipment().getWeaponId();
		return weaponId == 22494 || weaponId == 22496;
	}
	
	private static boolean hasNoxiousStaff(Player player) {
		int weaponId = player.getEquipment().getWeaponId();
		return weaponId == 31729 || weaponId == 31731 ||
				weaponId == 33333 || weaponId == 33334 ||
				weaponId == 33399 || weaponId == 33400 ||
				weaponId == 33465 || weaponId == 33466 ||
				weaponId == 36336 || weaponId == 36337 ||
				weaponId == 36713 || weaponId == 38274 ||
				weaponId == 38300 || weaponId == 38326 ||
				weaponId == 38352;
	}
	
	private static boolean hasWandAndOrb(Player player) {
		int weaponId = player.getEquipment().getWeaponId();
		int shieldId = player.getEquipment().getShieldId();
		String weaponName = ItemDefinitions.getItemDefinitions(weaponId).getName().toLowerCase();
		String shieldName = ItemDefinitions.getItemDefinitions(shieldId).getName().toLowerCase();
		return weaponName.contains("wand") && shieldName.contains("orb")
			|| weaponName.contains("wand") && shieldName.contains("rebounder")
			|| weaponName.contains("wand") && shieldName.contains("ancient lantern")
			|| weaponName.contains("wand") && shieldName.contains("singularity");
	}
	
	private static int getMagicAttackDelay(Player player, int weaponId) {
		String weaponName = ItemDefinitions.getItemDefinitions(weaponId).getName().toLowerCase();
		if(weaponName.contains("wand")) {
			if(hasWandAndOrb(player)) {
				return 2;
			} else {
				return 4;
			}
		} else if(weaponName.contains("staff")) {
			return 3;
		} else {
			return 5;
		}
	}
	
	private static int getMagicAttackEmote(Player player, int weaponId) {
		String weaponName = ItemDefinitions.getItemDefinitions(weaponId).getName().toLowerCase();
		if(weaponName.contains("wand")) {
			return 18260;
		} else if(weaponName.contains("staff")) {
			return 18302;
		} else {
			return -1;
		}
	}
	private static int getMagicAttackGraphics(Player player, int weaponId, int spellId) {
		String weaponName = ItemDefinitions.getItemDefinitions(weaponId).getName().toLowerCase();
		if(weaponName.contains("staff")) {
			if(spellId == 25) {//air strike
				return 3601;
			} else if(spellId == 28) { //water strike
				return 3605;
			} else if(spellId == 30) {//earth strike
				return 3603;
			} else if(spellId == 32) {//fire strike
				return 3607;
			} else if(spellId == 34) {//air bolt
				return 3601;
			} else if(spellId == 39) {//water bolt
				return 3605;
			} else if(spellId == 42) {//earth bolt
				return 3603;
			} else if(spellId == 45) {//fire bolt
				return 3607;
			} else if(spellId == 49) {//air blast
				return 3601;
			} else if(spellId == 52) {//water blast
				return 3605;
			} else if(spellId == 58) {//earth blast
				return 3603;
			} else if(spellId == 63) {//fire blast
				return 3607;
			} else if(spellId == 70) {//air wave
				return 3601;
			} else if(spellId == 73) {//water wave
				return 3605;
			} else if(spellId == 77) {//earth wave
				return 3603;
			} else if(spellId == 80) {//fire wave
				return 3607;
			} else if(spellId == 84) {//air surge
				return 3601;
			} else if(spellId == 87) {//water surge
				return 3605;
			} else if(spellId == 89) {//earth surge
				return 3603;
			} else if(spellId == 91) {//fire surge
				return 3607;
			} else {
				return -1;
			}
		} else if(weaponName.contains("wand")) {
			if(spellId == 25) {//air strike
				return 3600;
			} else if(spellId == 28) { //water strike
				return 3604;
			} else if(spellId == 30) {//earth strike
				return 3602;
			} else if(spellId == 32) {//fire strike
				return 3606;
			} else if(spellId == 34) {//air bolt
				return 3600;
			} else if(spellId == 39) {//water bolt
				return 3604;
			} else if(spellId == 42) {//earth bolt
				return 3602;
			} else if(spellId == 45) {//fire bolt
				return 3606;
			} else if(spellId == 49) {//air blast
				return 3600;
			} else if(spellId == 52) {//water blast
				return 3604;
			} else if(spellId == 58) {//earth blast
				return 3602;
			} else if(spellId == 63) {//fire blast
				return 3606;
			} else if(spellId == 70) {//air wave
				return 3600;
			} else if(spellId == 73) {//water wave
				return 3604;
			} else if(spellId == 77) {//earth wave
				return 3602;
			} else if(spellId == 80) {//fire wave
				return 3606;
			} else if(spellId == 84) {//air surge
				return 3600;
			} else if(spellId == 87) {//water surge
				return 3604;
			} else if(spellId == 89) {//earth surge
				return 3602;
			} else if(spellId == 91) {//fire surge
				return 3606;
			} else {
				return -1;
			}
		}
		return -1;
	}
	
	private static boolean hasSeismicWand(Player player) {
		int weaponId = player.getEquipment().getWeaponId();
		return weaponId == 28617;
	}
	
	private static boolean hasSeismicSing(Player player) {
		int shieldId = player.getEquipment().getShieldId();
		return shieldId == 28621;
	}
	
	private static boolean hasCywirWand(Player player) {
		int weaponId = player.getEquipment().getWeaponId();
		return weaponId == 37912;
	}
	
	private static boolean hasCywirOrb(Player player) {
		int shieldId = player.getEquipment().getShieldId();
		return shieldId == 40600;
	}

	public Entity getTarget() {
		return target;
	}

	private static void addAttackedByDelay(Entity player, Entity target) {
		target.setAttackedBy(player);
		target.setAttackedByDelay(Utils.currentTimeMillis() + 6000); // 8seconds
		player.setAttackingDelay(Utils.currentTimeMillis() + 6000);
	}

	public static void addAttackingDelay(Entity player) {
		player.setAttackingDelay(Utils.currentTimeMillis() + 6000);
	}

	public Entity[] getMultiAttackTargets(Player player) {
		return getMultiAttackTargets(player, target, 1, 9, false);
	}

	public static Entity[] getMultiAttackTargets(Player player, Entity target) {
		return getMultiAttackTargets(player, target, 1, 9, false);
	}

	public static Entity[] getMultiAttackTargets(Player player, Entity target, int maxDistance, int maxAmtTargets) {
		return getMultiAttackTargets(player, target, maxDistance, maxAmtTargets, false);
	}

	public static Entity[] getMultiAttackTargets(Player player, Entity target, int maxDistance, int maxAmtTargets,
			boolean usePlayerLoc) {
		List<Entity> possibleTargets = new ArrayList<Entity>();
		possibleTargets.add(target);
		if (target.isAtMultiArea()) {
			y: for (int regionId : target.getMapRegionsIds()) {
				Region region = World.getRegion(regionId);
				if (target instanceof Player) {
					List<Integer> playerIndexes = region.getPlayerIndexes();
					if (playerIndexes == null)
						continue;
					for (int playerIndex : playerIndexes) {
						Player p2 = World.getPlayers().get(playerIndex);
						if (p2 == null || p2 == player || p2 == target || p2.isDead() || p2.hasFinished()
								|| !p2.isCanPvp() || !p2.withinDistance(usePlayerLoc ? player : target, maxDistance)
								|| !player.getControlerManager().canHit(p2) || !player.clipedProjectile(p2, false)
								|| (p2.getControlerManager().getControler() instanceof Wilderness
										&& !World.isMultiArea(p2)))
							continue;
						possibleTargets.add(p2);
						if (possibleTargets.size() == maxAmtTargets)
							break y;
					}
				} else {
					List<Integer> npcIndexes = region.getNPCsIndexes();
					if (npcIndexes == null)
						continue;
					for (int npcIndex : npcIndexes) {
						NPC n = World.getNPCs().get(npcIndex);
						if (n == null || n == target || n == player.getFamiliar() || n.isDead() || n.hasFinished()
								|| !n.withinDistance(usePlayerLoc ? player : target, maxDistance)
								|| !n.getDefinitions().hasAttackOption() || !player.getControlerManager().canHit(n)
								|| !player.clipedProjectile(n, false))
							continue;
						possibleTargets.add(n);
						if (possibleTargets.size() == maxAmtTargets)
							break y;
					}
				}
			}
		}
		return possibleTargets.toArray(new Entity[possibleTargets.size()]);
	}
}