package com.rs.game.activites.duel;

import com.rs.game.item.Item;
import com.rs.game.item.ItemsContainer;
import com.rs.game.player.Player;

public class DuelRules {

    private Player player, target;
    public boolean[] duelRules = new boolean[26];
	private transient boolean hasRewardGiven;
    private ItemsContainer<Item> stake;

    public DuelRules(Player player, Player target) {
		this.player = player;
		this.target = target;
		this.stake = new ItemsContainer<Item>(28, false);
    }

    public boolean canAccept(ItemsContainer<Item> stake) {
    	if (getRule(0) && getRule(1) && getRule(2)) {
			player.sendMessage("You have to be able to use atleast one combat style in a duel.");
			getTarget().sendMessage("You have to be able to use atleast one combat style in a duel.");
			return false;
		}
		if (!getRule(24) && (player.getFamiliar() != null || getTarget().getFamiliar() != null)) {
			player.sendMessage("Summoning has been disabled during this duel!");
			getTarget().sendMessage("Summoning has been disabled during this duel!");
			return false;
		}
		if (player.getAuraManager().isActivated()) {
			player.sendMessage("You must remove your activated aura before starting this duel.");
			getTarget().sendMessage("Your opponent must remove their activated aura before starting this duel.");
			return false;
		}
		int count = 0;
		Item item;
		for (int i = 10; i < 24; i++) {
			int slot = i - 10;
			if (getRule(i) && (item = player.getEquipment().getItem(slot)) != null) {
				if (i == 23) {// arrows
					if (!(item.getDefinitions().isStackable() && player.getInventory().getItems().containsOne(item)))
						count++;
				} else
					count++;
			}
		}
		int freeSlots = player.getInventory().getItems().freeSlots() - count;
		if (freeSlots < 0) {
			player.sendMessage("You do not have enough inventory space to remove all the equipment.");
			getTarget().sendMessage("Your opponent does not have enough space to remove all the equipment.");
			return false;
		}
		for (int i = 0; i < stake.getSize(); i++) {
			if (stake.get(i) != null)
				freeSlots--;
		}
		if (freeSlots < 0) {
			player.sendMessage("You do not have enough room in your inventory for this stake.");
			getTarget().sendMessage("Your opponent does not have enough room in his inventory for this stake.");
			return false;
		}
		return true;
    }

    public boolean getRule(int ruleId) {
    	return duelRules[ruleId];
    }

    public ItemsContainer<Item> getStake() {
    	return stake;
    }

    public Player getTarget() {
    	return target;
    }

    public void resetStake() {
    	stake = null;
    }

    public void setConfigs() {
		int value = 0;
		int ruleId = 16;
		for (int i = 0; i < duelRules.length; i++) {
		    if (getRule(i)) {
				if (i == 7) // forfiet
				    value += 5;
				if (i == 25) // no movement
				    value += 6;
				value += ruleId;
		    }
		    ruleId += ruleId;
		}
		player.getPackets().sendConfig(286, value);
    }

    public boolean setRule(int ruleId, boolean value) {
    	return duelRules[ruleId] = value;
    }

    public void setRules(int ruleId) {
    	setRules(ruleId, true);
    }

    public void setRules(int ruleId, boolean updated) {
		if (!getRule(ruleId))
		    setRule(ruleId, true);
		else if (getRule(ruleId))
		    setRule(ruleId, false);
		if (updated) {
		    DuelRules rules = getTarget().getDuelRules();
		    if (rules == null)
		    	return;
		    rules.setRules(ruleId, false);
		}
		setConfigs();
    }

    public boolean hasRewardGiven() {
		return hasRewardGiven;
	}

	public void setRewardGiven(boolean hasGiven) {
		this.hasRewardGiven = hasGiven;
	}

	public static void sendRewardGivenUpdate(Player victor, Player loser, boolean hasGiven) {
		victor.getDuelRules().setRewardGiven(hasGiven);
		loser.getDuelRules().setRewardGiven(hasGiven);
	}
}