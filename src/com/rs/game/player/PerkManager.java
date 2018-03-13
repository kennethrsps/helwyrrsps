package com.rs.game.player;

import java.io.Serializable;

import com.rs.Settings;
import com.rs.utils.Colors;

/**
 * Handles all Game Perks that have been donated for.
 * 
 * @author Zeus.
 */
public class PerkManager implements Serializable {

	/**
	 * Generated serial UID.
	 */
	private static final long serialVersionUID = -6454356751078830705L;

	/**
	 * The player instance.
	 */
	private transient Player player;

	/**
	 * The player instance saving to.
	 * 
	 * @param player
	 *            The player.
	 */
	protected void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * A list of available perks.
	 */
	public boolean bankCommand, staminaBoost, greenThumb, birdMan, unbreakableForge, sleightOfHand, familiarExpert,
			chargeBefriender, charmCollector, herbivore, masterFisherman, delicateCraftsman, coinCollector,
			prayerBetrayer, avasSecret, keyExpert, dragonTrainer, gwdSpecialist, dungeon, petChanter, perslaysion,
			overclocked, elfFiend, masterChef, masterDiviner, quarryMaster, miniGamer, masterFledger, thePiromaniac,
			huntsman, portsMaster, investigator, divineDoubler, imbuedFocus, alchemicSmith, petLoot, dungeoneeringPower,
			dungeonOrison, mastersorcerer;

	/**
	 * Lets shorten the line here. Have to love our eyes man :(.
	 * 
	 * @param line
	 *            The interID to print.
	 * @param message
	 *            The String to print.
	 */
	private void sendText(int line, String message) {
		player.getPackets().sendIComponentText(275, line, "<shad=000000>" + message);
	}

	/**
	 * Displays players activated game perks.
	 */
	public void displayAvailablePerks() {
		for (int i = 0; i < 309; i++)
			player.getPackets().sendIComponentText(275, i, "");
		sendText(1, Colors.red + Settings.SERVER_NAME + " Game Perks");
		sendText(10, Colors.cyan + "Game Perks can be purchased from our ;;store.");
		sendText(11, Colors.red + "Red - not active</col>  -  " + Colors.green + "Green - active</col>.");
		sendText(12, "</shad>----------------------");
		sendText(13, (bankCommand ? Colors.green : Colors.red) + "Bank Command");
		sendText(14, (staminaBoost ? Colors.green : Colors.red) + "Stamina Boost");
		sendText(15, (greenThumb ? Colors.green : Colors.red) + "Green Thumb");
		sendText(16, (birdMan ? Colors.green : Colors.red) + "Bird Man");
		sendText(17, (unbreakableForge ? Colors.green : Colors.red) + "Unbreakable Forge");
		sendText(18, (sleightOfHand ? Colors.green : Colors.red) + "Sleight of Hand");
		sendText(19, (familiarExpert ? Colors.green : Colors.red) + "Familiar Expert");
		sendText(20, (chargeBefriender ? Colors.green : Colors.red) + "Charge Befriender");
		sendText(21, (charmCollector ? Colors.green : Colors.red) + "Charm Collector");
		sendText(22, (herbivore ? Colors.green : Colors.red) + "Herbivore");
		sendText(23, (masterFisherman ? Colors.green : Colors.red) + "Master Fisherman");
		sendText(24, (delicateCraftsman ? Colors.green : Colors.red) + "Delicate Craftsman");
		sendText(25, (coinCollector ? Colors.green : Colors.red) + "Coin Collector");
		sendText(26, (prayerBetrayer ? Colors.green : Colors.red) + "Prayer Betrayer");
		sendText(27, (avasSecret ? Colors.green : Colors.red) + "Avas Secret");
		sendText(28, (keyExpert ? Colors.green : Colors.red) + "Key Expert");
		sendText(29, (dragonTrainer ? Colors.green : Colors.red) + "Dragon Trainer");
		sendText(30, (gwdSpecialist ? Colors.green : Colors.red) + "GWD Specialist");
		sendText(31, (dungeon ? Colors.green : Colors.red) + "Dungeons Master");
		sendText(32, (petChanter ? Colors.green : Colors.red) + "Pet'chanter");
		sendText(33, (perslaysion ? Colors.green : Colors.red) + "Per'slay'sion");
		sendText(34, (overclocked ? Colors.green : Colors.red) + "Overclocked");
		sendText(35, (elfFiend ? Colors.green : Colors.red) + "Elf Fiend");
		sendText(36, (masterChef ? Colors.green : Colors.red) + "Master Chefs Man");
		sendText(37, (masterDiviner ? Colors.green : Colors.red) + "Master Diviner");
		sendText(38, (quarryMaster ? Colors.green : Colors.red) + "Quarrymaster");
		sendText(39, (miniGamer ? Colors.green : Colors.red) + "The Mini-Gamer");
		sendText(40, (masterFledger ? Colors.green : Colors.red) + "Master Fledger");
		sendText(41, (thePiromaniac ? Colors.green : Colors.red) + "The Piromaniac");
		sendText(42, (huntsman ? Colors.green : Colors.red) + "Huntsman");
		sendText(43, (portsMaster ? Colors.green : Colors.red) + "Ports Master");
		sendText(44, (investigator ? Colors.green : Colors.red) + "Investigator");
		sendText(45, (divineDoubler ? Colors.green : Colors.red) + "Divine Doubler");
		sendText(46, (imbuedFocus ? Colors.green : Colors.red) + "Imbued Focus");
		sendText(47, (alchemicSmith ? Colors.green : Colors.red) + "Alchemic Smithing");
		sendText(48, (petLoot ? Colors.green : Colors.red) + "Pet Loot");
		sendText(49, (dungeoneeringPower ? Colors.green : Colors.red) + "Dungeoneering Power");
		sendText(50, (dungeonOrison ? Colors.green : Colors.red) + "Dungeon Orison");
		sendText(49, (mastersorcerer ? Colors.green : Colors.red) + "Master Sorcerer");
		player.getInterfaceManager().sendInterface(275);
	}
}