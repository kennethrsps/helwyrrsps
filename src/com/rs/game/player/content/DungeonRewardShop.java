package com.rs.game.player.content;

import java.util.HashMap;
import java.util.Map;

import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.TemporaryAtributtes.Key;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

public class DungeonRewardShop {

    public static final int REWARD_SHOP = 940;

    public static void openRewardShop(final Player player) {
		player.getInterfaceManager().sendInterface(REWARD_SHOP);
		player.getPackets().sendIComponentSettings(940, 2, 0, 205, 1278);
		refreshPoints(player);
		player.setCloseInterfacesEvent(new Runnable() {
	
		    @Override
		    public void run() {
		    	player.getTemporaryAttributtes().remove(Key.DUNGEON_REWARD_SLOT);
		    }
		});
    }

    public static void purchase(Player player) {
		if (!canPurchase(player))
		    return;
		removeConfirmationPurchase(player);
		int slot = (int) player.getTemporaryAttributtes().get(Key.DUNGEON_REWARD_SLOT);
		DungeonReward reward = DungeonReward.forId(slot);
		if (Settings.DEBUG)
			System.out.println(slot + " id;");
		if (reward != null) {
		    player.getInventory().addItemDrop(reward.getId(), 1);
		    player.setDungeoneeringTokens(player.getDungeoneeringTokens() - reward.getCost());
		}
		refreshPoints(player);
    }

    public static void sendConfirmationPurchase(Player player) {
		if (!canPurchase(player))
		    return;
		player.getPackets().sendHideIComponent(REWARD_SHOP, 42, false);
    }

    public static void removeConfirmationPurchase(Player player) {
    	player.getPackets().sendHideIComponent(REWARD_SHOP, 42, true);
    }

    public static void select(Player player, int slot) {
	if (slot == 0) {
	    player.getPackets().sendGameMessage("You already have boosted experience!");
	    return;
	}
	player.getTemporaryAttributtes().put(Key.DUNGEON_REWARD_SLOT, slot);
    }

    private static boolean canPurchase(Player player) {
		if (player.getTemporaryAttributtes().get(Key.DUNGEON_REWARD_SLOT) == null)
		    return false;
		int slot = (int) player.getTemporaryAttributtes().get(Key.DUNGEON_REWARD_SLOT);
		DungeonReward reward = DungeonReward.forId(slot);
		if (reward == null) {
		    player.getPackets().sendGameMessage("[Undefined slotId] Item not found. "+ (player.getRights() == 2 ? slot : ""));
		    return false;
		} else {
		    player.getTemporaryAttributtes().put("dungReward", reward);
		    player.getPackets().sendGameMessage(reward.getName()+" requires a level of atleast "+ reward.getRequirement() +" in Dungeoneering and costs "+Utils.getFormattedNumber(reward.getCost())+" Dungeoneering tokens.");
		}
		int dungeoneeringLevel = reward.getRequirement(), price = reward.getCost();
		if (!(player.getRights() == 2) && (player.getSkills().getLevel(Skills.DUNGEONEERING) < dungeoneeringLevel || player.getDungeoneeringTokens() < price)) {
		    player.getPackets().sendGameMessage("You do not meet the requirements to purchase this item.");
		    return false;
		} else {
		    player.getDialogueManager().startDialogue("DungRewardConfirm", reward);
		}
		return true;
    }

    public static void refreshPoints(Player player) {
		player.getPackets().sendIComponentText(940, 31, ""+player.getDungeoneeringTokens());
    }

    public enum DungeonReward {
		//id, slotid, req, amount
    	BONECRUSHER(18337, 0, 21, 34000),
		HERBICIDE(19675, 5, 21, 34000),
		SCROLL_OF_LIFE(18336, 15, 25, 10000),
		SCROLL_OF_CLEANSING(19890, 40, 49, 20000),
		SCROLL_OF_EFFICIENCY(19670, 105, 55, 20000),
		SCROLL_OF_AUGURY(18344, 150, 77, 153000),
		SCROLL_OF_RIGOUR(18839, 145, 74, 140000),
		SCROLL_OF_RENEWAL(18343, 125, 65, 107000),
		MERCENARY_GLOVES(18347, 140, 73, 48500),
		TOME_OF_FROST(18346, 80, 48, 43000),
		ARCANE_PULSE_NECKLACE(18333, 20, 30, 6500),
		GRAVITE_SHORTBOW(18373, 70, 45, 40000),
		GRAVITE_LONGSWORD(18367, 55, 45, 40000),
		GRAVITE_RAPIER(18365, 50, 45, 40000),
		GRAVITE_STAFF(18371, 65, 45, 40000),
		GRAVITE_2H(18369, 60, 45, 40000),
		ARCANE_BLAST_NECKLACE(18334, 90, 50, 15500),
		RING_OF_VIGOUR(19669, 120, 62, 50000),
		ARCANE_STREAM_NECKLACE(18335, 130, 70, 30500),
		CHAOTIC_RAPIER(18349, 155, 80, 200000),
		CHAOTIC_LONGSWORD(18351, 160, 80, 200000),
		CHAOTIC_MAUL(18353, 165, 80, 200000),
		CHAOTIC_STAFF(18355, 170, 80, 200000),
		CHAOTIC_CROSSBOW(18357, 175, 80, 200000),
		CHAOTIC_KITESHIELD(18359, 180, 80, 200000),
		EAGLE_EYE_KITESHIELD(18361, 185, 80, 200000),
		FARSEER_KITESHIELD(18363, 190, 80, 200000),
		SNEAKERPEEPER(19894, 195, 80, 85000),
		TWISTEDNECKLACE(19886, 25, 30, 8500),
		DRAGONTOOTHNECKLACE(19887, 115, 60, 17000),
		DEMONHORNNECKLACE(19888, 200, 90, 35000);
	
		private static Map<Integer, DungeonReward> rewards;
	
		public static DungeonReward forId(int id) {
		    if (rewards == null)
			init();
		    return rewards.get(id);
		}
	
		static void init() {
		    rewards = new HashMap<Integer, DungeonReward>();
		    for (DungeonReward dr : DungeonReward.values())
			rewards.put(dr.slotId, dr);
		}
	
		private int id;
		private int req;
		private int cost;
		private int slotId;
		private String name;
	
		private DungeonReward(int id, int slotId, int req, int cost) {
		    this.id = id;
		    this.req = req;
		    this.cost = cost;
		    this.slotId = slotId;
		    this.name = ItemDefinitions.getItemDefinitions(id).getName();
		}
	
		public int getId() {
		    return id;
		}
	
		public String getName() {
		    return name;
		}
	
		public int getCost() {
		    return cost;
		}
	
		public int getSlotId() {
		    return slotId;
		}
	
		public int getRequirement() {
		    return req;
		}
    }
    
    public static void buyXP(Player player, int amount) {
    	int tokens = player.getDungeoneeringTokens();
    	if(player.getDungeoneeringTokens() >= amount) {
    		double xpFinal = player.getSkills().xpNoBonus(Skills.DUNGEONEERING, amount);
	    	player.setDungeoneeringTokens(tokens - amount);
	    	player.getSkills().addXp(Skills.DUNGEONEERING, xpFinal);
	    	player.sendMessage("You have bought "+amount+" dungeoneering xp!");
    	} else {
    		player.sendMessage(Colors.red+"How did you plan on buying more XP than you had tokens?");
    	}
    }
    
    public static void handleButtons(Player player, int componentId, int slotId, int packetId) {
		if (componentId == 64 && packetId == 14) {
			if (player.getTemporaryAttributtes().get("dungReward") != null) {
				DungeonReward reward = (DungeonReward) player.getTemporaryAttributtes().get("dungReward");
				if (reward != null) {
					if (player.getSkills().getLevelForXp(Skills.DUNGEONEERING) < reward.getRequirement()) {
						player.sendMessage("You need "+reward.getRequirement()+" dungeoneering to buy this reward.");
						return;
					}
					if (player.getDungeoneeringTokens() < reward.getCost()) {
						player.sendMessage("You need "+Utils.getFormattedNumber(reward.getCost())+" dungeoneering tokens to buy this reward.");
						return;
					}
					player.getDialogueManager().startDialogue("DungRewardConfirm", reward);
				} else
					player.sendMessage("You must choose a reward before trying to buy something.");
			}
			return;
		}
		if (componentId == 2) {
			DungeonReward reward = DungeonReward.forId(slotId);
			if(slotId == 205) {
				if(player.getSkills().getLevel(Skills.DUNGEONEERING) >= 77) {
					player.closeInterfaces();
			        player.getPackets().sendInputIntegerScript(true, "How much XP would you like to buy? Max amount : ("+ player.getDungeoneeringTokens() + ")");
			        player.getTemporaryAttributtes().put("dung_store_xp", Boolean.TRUE);
				} else
					player.sendMessage("You need level 77 dungeoneering to use this!");
				return;
			}
			if (reward == null && slotId != 205) {
				player.sendMessage(Colors.red+"This reward is not added! ["+slotId+"]");
				return;
			} else
				player.getTemporaryAttributtes().put("dungReward", reward);
		}
	}
}