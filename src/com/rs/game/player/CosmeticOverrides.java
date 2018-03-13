package com.rs.game.player;

import java.io.Serializable;

import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.item.Item;

/**
 * Handles everything related to Cosmetical Overrides.
 * @author Zeus.
 */
public class CosmeticOverrides implements Serializable {
	
	/**
	 * The generated serial UID.
	 */
	private static final long serialVersionUID = -2927386818603200182L;
	
	/**
	 * The player instance.
	 */
	private transient Player player;

	/**
	 * The player instance saving to.
	 * @param player The player.
	 */
	protected void setPlayer(Player player) {
		this.player = player;
	}
	
	/**
	 * An enum containing all Cosmetic Model ID's.
	 * @author Zeus
	 */
	public enum CosmeticItems {
		
		PALADIN("Paladin", 26464, 26466, 26468, 26470, 26472, 0),
		WARLORD("Warlord", 26182, 26184, 26186, 26190, 26188, 0),
		OBSIDIAN("Obsidian", 26128, 26140, 26136, 26126, 26124, 0),
		KALPHITE("Kalphite", 27075, 27077, 27079, 27080, 27078, 27076),
		DEMONFLESH("Demonflesh", 27120, 27122, 27124, 27128, 27126, 27130),
		REMOKEE("Remokee", 27148, 27149, 27150, 27151, 27152, 0),
		ASSASSIN("Assassin", 27181, 27182, 27183, 27184, 27185, 27186),
		SKELETON("Skeleton", 29782, 29784, 29786, 29790, 29788, 29792),
		GOTH("Goth", 29766, 29768, 29770, 29774, 29772, 0),
		MUMMY("Mummy", 29958, 29962, 29960, 29966, 29964, 0),
		REPLICA_DRAGON("Replica Dragon", 30191, 30193, 30194, 30195, 30196, 0),
		SENTINEL("Sentinel", 30597, 30598, 30599, 30600, 30601, 30602),
		REAVER("Reaver", 30606, 30607, 30608, 30609, 30610, 30611),
		HIKER("Hiker", 31296, 31297, 31298, 31299, 31301, 31300),
		SKYGUARD("Skyguard", 31536, 31537, 31538, 31539, 31540, 31543),
		VYREWATCH("Vyrewatch", 31546, 31547, 31548, 31550, 31549, 31551),
		SNOWMAN("Snowman", 33593, 33594, 33595, 0, 0, 0),
		SAMURAI("Samurai", 33637, 33638, 33639, 33640, 33641, 33642),
		WARM_WINTER("Warm Winter", 33755, 33756, 33757, 33758, 33759, 0),
		DARK_LORD("Dark Lord", 34222, 34223, 34224, 34225, 34226, 0);
		
		private String name;
		private int helm, chest, legs, boots, gloves, cape;
		
		CosmeticItems(String name, int helm, int chest, int legs, int boots, int gloves, int cape) {
			this.name = name;
			this.helm = helm;
			this.chest = chest;
			this.legs = legs;
			this.boots = boots;
			this.gloves = gloves;
			this.cape = cape;
		}
		
		public String getName() {
			return name;
		}
		
		public int getHelmId() {
			return helm;
		}
		
		public int getChestId() {
			return chest;
		}
		
		public int getLegsId() {
			return legs;
		}
		
		public int getBootsId() {
			return boots;
		}
		
		public int getGlovesId() {
			return gloves;
		}
		
		public int getCapeId() {
			return cape;
		}
	}
	
	/**
	 * Init the Cosmetic Override enum.
	 */
	public CosmeticItems outfit;

	/**
	 * Booleans handling of each override slot.
	 */
	public boolean showHelm = true, showBody = true, showLegs = true, 
			showBoots = true, showGloves = true, showCape = true, 
			showWeapon = true, showShield = true;
	
	/**
	 * Sets a Cosmetical Override as an outfit.
	 * @param outfit The outfit to use.
	 */
	public void setOutfit(CosmeticItems outfit) {
		resetCosmetics();
		if(outfit == null)
			return;
		setCosmetic((outfit.getHelmId() == 0 || showHelm) ? null : new Item(outfit.getHelmId()), Equipment.SLOT_HAT);
		setCosmetic((outfit.getChestId() == 0 || showBody) ? null : new Item(outfit.getChestId()), Equipment.SLOT_CHEST);
		setCosmetic((outfit.getLegsId() == 0 || showLegs) ? null : new Item(outfit.getLegsId()), Equipment.SLOT_LEGS);
		setCosmetic((outfit.getBootsId() == 0 || showBoots) ? null : new Item(outfit.getBootsId()), Equipment.SLOT_FEET);
		setCosmetic((outfit.getGlovesId() == 0 || showGloves) ? null : new Item(outfit.getGlovesId()), Equipment.SLOT_HANDS);
		setCosmetic((outfit.getCapeId() == 0 || showCape) ? null : new Item(outfit.getCapeId()), Equipment.SLOT_CAPE);
		player.getGlobalPlayerUpdater().generateAppearenceData();
		this.outfit = outfit;
	}
	
	 /* Sets a specified slot as cosmetic
	 * 
	 * @param item
	 *            The cosmetic item
	 * @param slot
	 *            The slot to set
	 */
	private void setCosmetic(Item item, int slot) {
		player.getGlobalPlayerUpdater().cosmeticItems[slot] = item;
	}

	/**
	 * Clears the cosmetic data
	 */
	public void resetCosmetics() {
		player.getGlobalPlayerUpdater().cosmeticItems = new Item[14];
		this.outfit = null;
		player.getGlobalPlayerUpdater().generateAppearenceData();
		if (Settings.DEBUG)
			System.err.println("Reset cosmetic items for "+player.getUsername());
	}
	
	/**
	 * Represents all available overrides.
	 */
	public boolean retroCapes, paladin, warlord, obsidian, kalphite, demonflesh, remokee, assassin,
					skeleton, goth, mummy, replicaDragon, sentinel, reaver, hiker, skyguard,
					vyrewatch, snowman, samurai, warmWinter, darkLord;
	
	/**
	 * Gets the Retro version of a skillcape as an override.
	 * @param cape The cape ID to get.
	 * @return the Cape item ID.
	 */
	public int getRetroCapeModelId1(int capeId) {
		switch (capeId) {
		case 9747: //attack
			return ItemDefinitions.getItemDefinitions(34540).getMaleWornModelId1();
		case 9748: //attack T
			return ItemDefinitions.getItemDefinitions(34542).getMaleWornModelId1();
		case 34246: //attack hooded
			return ItemDefinitions.getItemDefinitions(34541).getMaleWornModelId1();
		case 34247: //attack hooded T
			return ItemDefinitions.getItemDefinitions(34543).getMaleWornModelId1();
		case 9750: //strength
			return ItemDefinitions.getItemDefinitions(34545).getMaleWornModelId1();
		case 9751: //strength T
			return ItemDefinitions.getItemDefinitions(34547).getMaleWornModelId1();
		case 34248: //strength hooded
			return ItemDefinitions.getItemDefinitions(34546).getMaleWornModelId1();
		case 34249: //strength hooded T
			return ItemDefinitions.getItemDefinitions(34548).getMaleWornModelId1();
		case 9753: //defence
			return ItemDefinitions.getItemDefinitions(34550).getMaleWornModelId1();
		case 9754: //defence T
			return ItemDefinitions.getItemDefinitions(34552).getMaleWornModelId1();
		case 34250: //defence hooded
			return ItemDefinitions.getItemDefinitions(34551).getMaleWornModelId1();
		case 34251: //defence hooded T
			return ItemDefinitions.getItemDefinitions(34553).getMaleWornModelId1();
		case 9756: //ranging
			return ItemDefinitions.getItemDefinitions(34555).getMaleWornModelId1();
		case 9757: //ranging T
			return ItemDefinitions.getItemDefinitions(34557).getMaleWornModelId1();
		case 34556: //ranging hooded
			return ItemDefinitions.getItemDefinitions(34556).getMaleWornModelId1();
		case 34557: //ranging hooded T
			return ItemDefinitions.getItemDefinitions(34558).getMaleWornModelId1();
		case 9759: //prayer
			return ItemDefinitions.getItemDefinitions(34560).getMaleWornModelId1();
		case 9760: //prayer T
			return ItemDefinitions.getItemDefinitions(34562).getMaleWornModelId1();
		case 34254: //prayer hooded
			return ItemDefinitions.getItemDefinitions(34561).getMaleWornModelId1();
		case 34255: //prayer hooded T
			return ItemDefinitions.getItemDefinitions(34563).getMaleWornModelId1();
		case 9762: //magic
			return ItemDefinitions.getItemDefinitions(34565).getMaleWornModelId1();
		case 9763: //magic T
			return ItemDefinitions.getItemDefinitions(34567).getMaleWornModelId1();
		case 34256: //magic hooded
			return ItemDefinitions.getItemDefinitions(34566).getMaleWornModelId1();
		case 34257: //magic hooded T
			return ItemDefinitions.getItemDefinitions(34568).getMaleWornModelId1();
		case 9765: //runecrafting
			return ItemDefinitions.getItemDefinitions(34570).getMaleWornModelId1();
		case 9766: //runecrafting T
			return ItemDefinitions.getItemDefinitions(34572).getMaleWornModelId1();
		case 34258: //runecrafting hooded
			return ItemDefinitions.getItemDefinitions(34571).getMaleWornModelId1();
		case 34259: //runecrafting hooded T
			return ItemDefinitions.getItemDefinitions(34573).getMaleWornModelId1();
		case 9768: //constitution
			return ItemDefinitions.getItemDefinitions(34580).getMaleWornModelId1();
		case 9769: //constitution T
			return ItemDefinitions.getItemDefinitions(34582).getMaleWornModelId1();
		case 34262: //constitution hooded
			return ItemDefinitions.getItemDefinitions(34581).getMaleWornModelId1();
		case 34263: //constitution hooded T
			return ItemDefinitions.getItemDefinitions(34583).getMaleWornModelId1();
		case 9771: //agility
			return ItemDefinitions.getItemDefinitions(34585).getMaleWornModelId1();
		case 9772: //agility T
			return ItemDefinitions.getItemDefinitions(34587).getMaleWornModelId1();
		case 34264: //agility hooded
			return ItemDefinitions.getItemDefinitions(34586).getMaleWornModelId1();
		case 34265: //agility hooded T
			return ItemDefinitions.getItemDefinitions(34588).getMaleWornModelId1();
		case 9774: //herblore
			return ItemDefinitions.getItemDefinitions(34590).getMaleWornModelId1();
		case 9775: //herblore T
			return ItemDefinitions.getItemDefinitions(34592).getMaleWornModelId1();
		case 34266: //herblore hooded
			return ItemDefinitions.getItemDefinitions(34591).getMaleWornModelId1();
		case 34267: //herblore hooded T
			return ItemDefinitions.getItemDefinitions(34593).getMaleWornModelId1();
		case 9777: //thieving
			return ItemDefinitions.getItemDefinitions(34595).getMaleWornModelId1();
		case 9778: //thieving T
			return ItemDefinitions.getItemDefinitions(34597).getMaleWornModelId1();
		case 34268: //thieving hooded
			return ItemDefinitions.getItemDefinitions(34596).getMaleWornModelId1();
		case 34269: //thieving hooded T
			return ItemDefinitions.getItemDefinitions(34598).getMaleWornModelId1();
		case 9780: //crafting
			return ItemDefinitions.getItemDefinitions(34600).getMaleWornModelId1();
		case 9781: //crafting T
			return ItemDefinitions.getItemDefinitions(34602).getMaleWornModelId1();
		case 34270: //crafting hooded
			return ItemDefinitions.getItemDefinitions(34601).getMaleWornModelId1();
		case 34271: //crafting hooded T
			return ItemDefinitions.getItemDefinitions(34603).getMaleWornModelId1();
		case 9783: //fletching
			return ItemDefinitions.getItemDefinitions(34605).getMaleWornModelId1();
		case 9784: //fletching T
			return ItemDefinitions.getItemDefinitions(34607).getMaleWornModelId1();
		case 34272: //fletching hooded
			return ItemDefinitions.getItemDefinitions(34606).getMaleWornModelId1();
		case 34273: //fletching hooded T
			return ItemDefinitions.getItemDefinitions(34608).getMaleWornModelId1();
		case 9786: //slayer
			return ItemDefinitions.getItemDefinitions(34610).getMaleWornModelId1();
		case 9787: //slayer T
			return ItemDefinitions.getItemDefinitions(34612).getMaleWornModelId1();
		case 34274: //slayer hooded
			return ItemDefinitions.getItemDefinitions(34611).getMaleWornModelId1();
		case 34275: //slayer hooded T
			return ItemDefinitions.getItemDefinitions(34613).getMaleWornModelId1();
		case 9789: //construction
			return ItemDefinitions.getItemDefinitions(34615).getMaleWornModelId1();
		case 9790: //construction T
			return ItemDefinitions.getItemDefinitions(34617).getMaleWornModelId1();
		case 34276: //construction hooded
			return ItemDefinitions.getItemDefinitions(34616).getMaleWornModelId1();
		case 34277: //construction hooded T
			return ItemDefinitions.getItemDefinitions(34618).getMaleWornModelId1();
		case 9792: //mining
			return ItemDefinitions.getItemDefinitions(34620).getMaleWornModelId1();
		case 9793: //mining T
			return ItemDefinitions.getItemDefinitions(34622).getMaleWornModelId1();
		case 34278: //mining hooded
			return ItemDefinitions.getItemDefinitions(34621).getMaleWornModelId1();
		case 34279: //mining hooded T
			return ItemDefinitions.getItemDefinitions(34623).getMaleWornModelId1();
		case 9795: //smithing
			return ItemDefinitions.getItemDefinitions(34625).getMaleWornModelId1();
		case 9796: //smithing T
			return ItemDefinitions.getItemDefinitions(34627).getMaleWornModelId1();
		case 34280: //smithing hooded
			return ItemDefinitions.getItemDefinitions(34626).getMaleWornModelId1();
		case 34281: //smithing hooded T
			return ItemDefinitions.getItemDefinitions(34628).getMaleWornModelId1();
		case 9798: //fishing
			return ItemDefinitions.getItemDefinitions(34630).getMaleWornModelId1();
		case 9799: //fishing T
			return ItemDefinitions.getItemDefinitions(34632).getMaleWornModelId1();
		case 34282: //fishing hooded
			return ItemDefinitions.getItemDefinitions(34631).getMaleWornModelId1();
		case 34283: //fishing hooded T
			return ItemDefinitions.getItemDefinitions(34633).getMaleWornModelId1();
		case 9801: //cooking
			return ItemDefinitions.getItemDefinitions(34635).getMaleWornModelId1();
		case 9802: //cooking T
			return ItemDefinitions.getItemDefinitions(34637).getMaleWornModelId1();
		case 34284: //cooking hooded
			return ItemDefinitions.getItemDefinitions(34636).getMaleWornModelId1();
		case 34285: //cooking hooded T
			return ItemDefinitions.getItemDefinitions(34638).getMaleWornModelId1();
		case 9804: //firemaking
			return ItemDefinitions.getItemDefinitions(34640).getMaleWornModelId1();
		case 9805: //firemaking T
			return ItemDefinitions.getItemDefinitions(34642).getMaleWornModelId1();
		case 34286: //firemaking hooded
			return ItemDefinitions.getItemDefinitions(34641).getMaleWornModelId1();
		case 34287: //firemaking hooded T
			return ItemDefinitions.getItemDefinitions(34643).getMaleWornModelId1();
		case 9807: //woodcutting
			return ItemDefinitions.getItemDefinitions(34645).getMaleWornModelId1();
		case 9808: //woodcutting T
			return ItemDefinitions.getItemDefinitions(34647).getMaleWornModelId1();
		case 34288: //woodcutting hooded
			return ItemDefinitions.getItemDefinitions(34646).getMaleWornModelId1();
		case 34289: //woodcutting hooded T
			return ItemDefinitions.getItemDefinitions(34648).getMaleWornModelId1();
		case 9810: //farming
			return ItemDefinitions.getItemDefinitions(34650).getMaleWornModelId1();
		case 9811: //farming T
			return ItemDefinitions.getItemDefinitions(34652).getMaleWornModelId1();
		case 34290: //farming hooded
			return ItemDefinitions.getItemDefinitions(34651).getMaleWornModelId1();
		case 34291: //farming hooded T
			return ItemDefinitions.getItemDefinitions(34653).getMaleWornModelId1();
		case 9948: //hunter
			return ItemDefinitions.getItemDefinitions(34575).getMaleWornModelId1();
		case 9949: //hunter T
			return ItemDefinitions.getItemDefinitions(34577).getMaleWornModelId1();
		case 34260: //hunter hooded
			return ItemDefinitions.getItemDefinitions(34576).getMaleWornModelId1();
		case 34261: //hunter hooded T
			return ItemDefinitions.getItemDefinitions(34578).getMaleWornModelId1();
		case 12169: //summoning
			return ItemDefinitions.getItemDefinitions(34665).getMaleWornModelId1();
		case 12170: //summoning T
			return ItemDefinitions.getItemDefinitions(34667).getMaleWornModelId1();
		case 34296: //summoning hooded
			return ItemDefinitions.getItemDefinitions(34666).getMaleWornModelId1();
		case 34297: //summoning hooded T
			return ItemDefinitions.getItemDefinitions(34668).getMaleWornModelId1();
		case 18508: //dungeoneering
			return ItemDefinitions.getItemDefinitions(34660).getMaleWornModelId1();
		case 18509: //dungeoneering T
			return ItemDefinitions.getItemDefinitions(34662).getMaleWornModelId1();
		case 34294: //dungeoneering hooded
			return ItemDefinitions.getItemDefinitions(34661).getMaleWornModelId1();
		case 34295: //dungeoneering hooded T
			return ItemDefinitions.getItemDefinitions(34663).getMaleWornModelId1();
		case 29185: //divination
			return ItemDefinitions.getItemDefinitions(34655).getMaleWornModelId1();
		case 29186: //divination T
			return ItemDefinitions.getItemDefinitions(34657).getMaleWornModelId1();
		case 34292: //divination hooded
			return ItemDefinitions.getItemDefinitions(34656).getMaleWornModelId1();
		case 34293: //divination hooded T
			return ItemDefinitions.getItemDefinitions(34658).getMaleWornModelId1();
		case 36351: //invention
			return ItemDefinitions.getItemDefinitions(36789).getMaleWornModelId1();
		case 36352: //invention T
			return ItemDefinitions.getItemDefinitions(36791).getMaleWornModelId1();
		case 36353: //invention hooded
			return ItemDefinitions.getItemDefinitions(36790).getMaleWornModelId1();
		case 36354: //invention hooded T
			return ItemDefinitions.getItemDefinitions(36792).getMaleWornModelId1();
		}
		return -1;
	}
	
	/**
	 * Sets Paladin Outfit as the current Cosmetic Override.
	 */
	public void setPaladinOutfit() {
		if (!paladin && !Settings.DEBUG && !player.isHeadStaff()) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You have to unlock "
					+ "the '"+outfit.getName()+"'s' Outfit by purchasing it from our ;;store "
							+ "page - Cosmetic Overrides - section!");
			return;
		}
		setOutfit(CosmeticItems.PALADIN);
		player.getDialogueManager().startDialogue("SimpleMessage", 
				"You are now using '"+outfit.getName()+"'s' override!");
	}
	
	/**
	 * Sets Warlord Outfit as the current Cosmetic Override.
	 */
	public void setWarlordOutfit() {
		if (!warlord && !Settings.DEBUG && !player.isHeadStaff()) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You have to unlock "
					+ "the '"+outfit.getName()+"'s' Outfit by purchasing it from our ;;store "
							+ "page - Cosmetic Overrides - section!");
			return;
		}
		setOutfit(CosmeticItems.WARLORD);
		player.getDialogueManager().startDialogue("SimpleMessage", 
				"You are now using '"+outfit.getName()+"'s' override!");
	}
	
	/**
	 * Sets Obsidian Outfit as the current Cosmetic Override.
	 */
	public void setObsidianOutfit() {
		if (!obsidian && !Settings.DEBUG && !player.isHeadStaff()) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You have to unlock "
					+ "the '"+outfit.getName()+"'s' Outfit by purchasing it from our ;;store "
							+ "page - Cosmetic Overrides - section!");
			return;
		}
		setOutfit(CosmeticItems.OBSIDIAN);
		player.getDialogueManager().startDialogue("SimpleMessage", 
				"You are now using '"+outfit.getName()+"'s' override!");
	}
	
	/**
	 * Sets Kaklphite Outfit as the current Cosmetic Override.
	 */
	public void setKalphiteOutfit() {
		if (!kalphite && !Settings.DEBUG && !player.isHeadStaff()) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You have to unlock "
					+ "the '"+outfit.getName()+"'s' Outfit by purchasing it from our ;;store "
							+ "page - Cosmetic Overrides - section!");
			return;
		}
		setOutfit(CosmeticItems.KALPHITE);
		player.getDialogueManager().startDialogue("SimpleMessage", 
				"You are now using '"+outfit.getName()+"'s' override!");
	}
	
	/**
	 * Sets Demonflesh Outfit as the current Cosmetic Override.
	 */
	public void setDemonfleshOutfit() {
		if (!demonflesh && !Settings.DEBUG && !player.isHeadStaff()) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You have to unlock "
					+ "the '"+outfit.getName()+"'s' Outfit by purchasing it from our ;;store "
							+ "page - Cosmetic Overrides - section!");
			return;
		}
		setOutfit(CosmeticItems.DEMONFLESH);
		player.getDialogueManager().startDialogue("SimpleMessage", 
				"You are now using '"+outfit.getName()+"'s' override!");
	}
	
	/**
	 * Sets Remokee Outfit as the current Cosmetic Override.
	 */
	public void setRemokeeOutfit() {
		if (!remokee && !Settings.DEBUG && !player.isHeadStaff()) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You have to unlock "
					+ "the '"+outfit.getName()+"'s' Outfit by purchasing it from our ;;store "
							+ "page - Cosmetic Overrides - section!");
			return;
		}
		setOutfit(CosmeticItems.REMOKEE);
		player.getDialogueManager().startDialogue("SimpleMessage", 
				"You are now using '"+outfit.getName()+"'s' override!");
	}
	
	/**
	 * Sets Assassin Outfit as the current Cosmetic Override.
	 */
	public void setAssassinOutfit() {
		if (!assassin && !Settings.DEBUG && !player.isHeadStaff()) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You have to unlock "
					+ "the '"+outfit.getName()+"'s' Outfit by purchasing it from our ;;store "
							+ "page - Cosmetic Overrides - section!");
			return;
		}
		setOutfit(CosmeticItems.ASSASSIN);
		player.getDialogueManager().startDialogue("SimpleMessage", 
				"You are now using '"+outfit.getName()+"'s' override!");
	}
	
	/**
	 * Sets Skeleton Outfit as the current Cosmetic Override.
	 */
	public void setSkeletonOutfit() {
		if (!skeleton && !Settings.DEBUG && !player.isHeadStaff()) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You have to unlock "
					+ "the '"+outfit.getName()+"'s' Outfit by purchasing it from our ;;store "
							+ "page - Cosmetic Overrides - section!");
			return;
		}
		setOutfit(CosmeticItems.SKELETON);
		player.getDialogueManager().startDialogue("SimpleMessage", 
				"You are now using '"+outfit.getName()+"'s' override!");
	}
	
	/**
	 * Sets Goth Outfit as the current Cosmetic Override.
	 */
	public void setGothOutfit() {
		if (!goth && !Settings.DEBUG && !player.isHeadStaff()) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You have to unlock "
					+ "the '"+outfit.getName()+"'s' Outfit by purchasing it from our ;;store "
							+ "page - Cosmetic Overrides - section!");
			return;
		}
		setOutfit(CosmeticItems.GOTH);
		player.getDialogueManager().startDialogue("SimpleMessage", 
				"You are now using '"+outfit.getName()+"'s' override!");
	}
	
	/**
	 * Sets Mummy Outfit as the current Cosmetic Override.
	 */
	public void setMummyOutfit() {
		if (!mummy && !Settings.DEBUG && !player.isHeadStaff()) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You have to unlock "
					+ "the '"+outfit.getName()+"'s' Outfit by purchasing it from our ;;store "
							+ "page - Cosmetic Overrides - section!");
			return;
		}
		setOutfit(CosmeticItems.MUMMY);
		player.getDialogueManager().startDialogue("SimpleMessage", 
				"You are now using '"+outfit.getName()+"'s' override!");
	}
	
	/**
	 * Sets Replica Dragon Outfit as the current Cosmetic Override.
	 */
	public void setReplicaDragonOutfit() {
		if (!replicaDragon && !Settings.DEBUG && !player.isHeadStaff()) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You have to unlock "
					+ "the '"+outfit.getName()+"'s' Outfit by purchasing it from our ;;store "
							+ "page - Cosmetic Overrides - section!");
			return;
		}
		setOutfit(CosmeticItems.REPLICA_DRAGON);
		player.getDialogueManager().startDialogue("SimpleMessage", 
				"You are now using '"+outfit.getName()+"'s' override!");
	}
	
	/**
	 * Sets Sentinel Outfit as the current Cosmetic Override.
	 */
	public void setSentinelOutfit() {
		if (!sentinel && !Settings.DEBUG && !player.isHeadStaff()) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You have to unlock "
					+ "the '"+outfit.getName()+"'s' Outfit by purchasing it from our ;;store "
							+ "page - Cosmetic Overrides - section!");
			return;
		}
		setOutfit(CosmeticItems.SENTINEL);
		player.getDialogueManager().startDialogue("SimpleMessage", 
				"You are now using '"+outfit.getName()+"'s' override!");
	}
	
	/**
	 * Sets Reaver Outfit as the current Cosmetic Override.
	 */
	public void setReaverOutfit() {
		if (!reaver && !Settings.DEBUG && !player.isHeadStaff()) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You have to unlock "
					+ "the '"+outfit.getName()+"'s' Outfit by purchasing it from our ;;store "
							+ "page - Cosmetic Overrides - section!");
			return;
		}
		setOutfit(CosmeticItems.REAVER);
		player.getDialogueManager().startDialogue("SimpleMessage", 
				"You are now using '"+outfit.getName()+"'s' override!");
	}
	
	/**
	 * Sets Hiker Outfit as the current Cosmetic Override.
	 */
	public void setHikerOutfit() {
		if (!hiker && !Settings.DEBUG && !player.isHeadStaff()) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You have to unlock "
					+ "the '"+outfit.getName()+"'s' Outfit by purchasing it from our ;;store "
							+ "page - Cosmetic Overrides - section!");
			return;
		}
		setOutfit(CosmeticItems.HIKER);
		player.getDialogueManager().startDialogue("SimpleMessage", 
				"You are now using '"+outfit.getName()+"'s' override!");
	}
	
	/**
	 * Sets Skyguard Outfit as the current Cosmetic Override.
	 */
	public void setSkyguardOutfit() {
		if (!skyguard && !Settings.DEBUG && !player.isHeadStaff()) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You have to unlock "
					+ "the '"+outfit.getName()+"'s' Outfit by purchasing it from our ;;store "
							+ "page - Cosmetic Overrides - section!");
			return;
		}
		setOutfit(CosmeticItems.SKYGUARD);
		player.getDialogueManager().startDialogue("SimpleMessage", 
				"You are now using '"+outfit.getName()+"'s' override!");
	}
	
	/**
	 * Sets Vyrewatch Outfit as the current Cosmetic Override.
	 */
	public void setVyrewatchOutfit() {
		if (!vyrewatch && !Settings.DEBUG && !player.isHeadStaff()) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You have to unlock "
					+ "the '"+outfit.getName()+"'s' Outfit by purchasing it from our ;;store "
							+ "page - Cosmetic Overrides - section!");
			return;
		}
		setOutfit(CosmeticItems.VYREWATCH);
		player.getDialogueManager().startDialogue("SimpleMessage", 
				"You are now using '"+outfit.getName()+"'s' override!");
	}
	
	/**
	 * Sets Snowman Outfit as the current Cosmetic Override.
	 */
	public void setSnowmanOutfit() {
		if (!snowman && !Settings.DEBUG && !player.isHeadStaff()) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You have to unlock "
					+ "the '"+outfit.getName()+"'s' Outfit by purchasing it from our ;;store "
							+ "page - Cosmetic Overrides - section!");
			return;
		}
		setOutfit(CosmeticItems.SNOWMAN);
		player.getDialogueManager().startDialogue("SimpleMessage", 
				"You are now using '"+outfit.getName()+"'s' override!");
	}
	
	/**
	 * Sets Samurai Outfit as the current Cosmetic Override.
	 */
	public void setSamuraiOutfit() {
		if (!samurai && !Settings.DEBUG && !player.isHeadStaff()) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You have to unlock "
					+ "the '"+outfit.getName()+"'s' Outfit by purchasing it from our ;;store "
							+ "page - Cosmetic Overrides - section!");
			return;
		}
		setOutfit(CosmeticItems.SAMURAI);
		player.getDialogueManager().startDialogue("SimpleMessage", 
				"You are now using '"+outfit.getName()+"'s' override!");
	}
	
	/**
	 * Sets Warm Winter Outfit as the current Cosmetic Override.
	 */
	public void setWarmWinterOutfit() {
		if (!warmWinter && !Settings.DEBUG && !player.isHeadStaff()) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You have to unlock "
					+ "the '"+outfit.getName()+"'s' Outfit by purchasing it from our ;;store "
							+ "page - Cosmetic Overrides - section!");
			return;
		}
		setOutfit(CosmeticItems.WARM_WINTER);
		player.getDialogueManager().startDialogue("SimpleMessage", 
				"You are now using '"+outfit.getName()+"'s' override!");
	}
	
	/**
	 * Sets Dark Lord Outfit as the current Cosmetic Override.
	 */
	public void setDarkLordOutfit() {
		if (!darkLord && !Settings.DEBUG && !player.isHeadStaff()) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You have to unlock "
					+ "the '"+outfit.getName()+"'s' Outfit by purchasing it from our ;;store "
							+ "page - Cosmetic Overrides - section!");
			return;
		}
		setOutfit(CosmeticItems.DARK_LORD);
		player.getDialogueManager().startDialogue("SimpleMessage", 
				"You are now using '"+outfit.getName()+"'s' override!");
	}
}