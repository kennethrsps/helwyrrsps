package com.rs.game.player.actions.fletching;

import java.util.HashMap;
import java.util.Map;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Action;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/**
 * Handles fletching and a little bit of crafting.
 * 
 * @author Zeus
 */
public class Fletching extends Action {

	public static final int KNIFE = 946, DUNGEONEERING_KNIFE = 17754, CHISLE = 1755;
	public static final int CROSSBOW_STRING = 9438;

	public static Fletch isFletching(Item first, Item second) {
		Fletch fletch = Fletch.forId(first.getId());
		int selected;
		if (fletch != null)
			selected = second.getId();
		else {
			fletch = Fletch.forId(second.getId());
			selected = first.getId();
		}
		return fletch != null && fletch.getSelected() == selected ? fletch : null;
	}

	public static boolean isFletching(Player player, int logId) {
		for (Fletch fletch : Fletch.values()) {
			if (fletch.getId() == logId) {
				player.getDialogueManager().startDialogue("FletchingD", fletch);
				return true;
			}
		}
		return false;
	}

	public static enum Fletch {

		/**
		 * (u)'s and completed bows
		 */
		REGULAR_BOW(1511, KNIFE, new int[] { 52, 50, 48, 9440 }, // product
				new int[] { 1, 5, 10, 9 }, // level req
				new double[] { 1.33, 5, 10, 6 }, // experience
				new Animation(24938)), // animation

		OAK_BOW(1521, KNIFE, new int[] { 54, 56, 9442 }, new int[] { 20, 25, 24 }, new double[] { 16.5, 25, 16 },
				new Animation(24940)),

		WILLOW_BOW(1519, KNIFE, new int[] { 60, 58, 9444 }, new int[] { 35, 40, 39 }, new double[] { 33.3, 41.5, 22 },
				new Animation(24943)),

		MAPLE_BOW(1517, KNIFE, new int[] { 64, 62, 9448 }, new int[] { 50, 55, 54 }, new double[] { 50, 58.3, 32 },
				new Animation(24943)),

		YEW_BOW(1515, KNIFE, new int[] { 68, 66, 9452 }, new int[] { 65, 70, 69 }, new double[] { 67.5, 75, 50 },
				new Animation(24943)),

		MAGIC_BOW(1513, KNIFE, new int[] { 72, 70, 25483 }, new int[] { 80, 85, 92 }, new double[] { 83.25, 91.5, 100 },
				new Animation(24939)),

		STRUNG_SHORT_BOW(50, 1777, new int[] { 841 }, new int[] { 5 }, new double[] { 5 }, new Animation(24945)),

		STRUNG_LONG_BOW(48, 1777, new int[] { 839 }, new int[] { 10 }, new double[] { 10 }, new Animation(24952)),

		STRUNG_OAK_SHORT_BOW(54, 1777, new int[] { 843 }, new int[] { 20 }, new double[] { 16.5 }, new Animation(24946)),

		STRUNG_OAK_LONG_BOW(56, 1777, new int[] { 845 }, new int[] { 25 }, new double[] { 25 }, new Animation(24953)),

		STRUNG_WILLOW_SHORT_BOW(60, 1777, new int[] { 849 }, new int[] { 35 }, new double[] { 33.3 },
				new Animation(24947)),

		STRUNG_WILLOW_LONG_BOW(58, 1777, new int[] { 847 }, new int[] { 40 }, new double[] { 41.5 },
				new Animation(24954)),

		STRUNG_MAPLE_SHORT_BOW(64, 1777, new int[] { 853 }, new int[] { 50 }, new double[] { 50 }, new Animation(24948)),

		STRUNG_MAPLE_LONG_BOW(62, 1777, new int[] { 851 }, new int[] { 55 }, new double[] { 58.3 },
				new Animation(24955)),

		STRUNG_YEW_SHORT_BOW(68, 1777, new int[] { 857 }, new int[] { 65 }, new double[] { 67.5 }, new Animation(24949)),

		STRUNG_YEW_LONG_BOW(66, 1777, new int[] { 855 }, new int[] { 70 }, new double[] { 75 }, new Animation(24956)),

		STRUNG_MAGIC_SHORT_BOW(72, 1777, new int[] { 861 }, new int[] { 80 }, new double[] { 83.25 },
				new Animation(24950)),

		STRUNG_MAGIC_LONG_BOW(70, 1777, new int[] { 859 }, new int[] { 85 }, new double[] { 91.5 },
				new Animation(24957)),

		/**
		 * Crossbows
		 */

		U_BRONZE_CBOW(9440, 9420, new int[] { 9454 }, new int[] { 9 }, new double[] { 6 }, new Animation(-1)),

		U_IRON_CBOW(9444, 9423, new int[] { 9457 }, new int[] { 39 }, new double[] { 22 }, new Animation(-1)),

		U_BLURITE_CBOW(9442, 9422, new int[] { 9456 }, new int[] { 24 }, new double[] { 16 }, new Animation(-1)),

		U_STEEL_CBOW(9446, 9425, new int[] { 9459 }, new int[] { 46 }, new double[] { 27 }, new Animation(-1)),

		U_MITHRIL_CBOW(9448, 9427, new int[] { 9461 }, new int[] { 54 }, new double[] { 32 }, new Animation(-1)),

		U_ADAMANT_CBOW(9450, 9429, new int[] { 9463 }, new int[] { 61 }, new double[] { 41 }, new Animation(-1)),

		U_RUNITE_CBOW(9452, 9431, new int[] { 9465 }, new int[] { 69 }, new double[] { 50 }, new Animation(-1)),

		U_DRAGON_CBOW(25483, 25481, new int[] { 25478 }, new int[] { 94 }, new double[] { 200 }, new Animation(-1)),

		BRONZE_CBOW(9454, 9438, new int[] { 9174 }, new int[] { 9 }, new double[] { 6.0 }, new Animation(6671)),

		IRON_CBOW(9457, 9438, new int[] { 9177 }, new int[] { 39 }, new double[] { 22 }, new Animation(6673)),

		STEEL_CBOW(9459, 9438, new int[] { 9179 }, new int[] { 46 }, new double[] { 27 }, new Animation(6674)),

		BLURITE_CBOW(9456, 9438, new int[] { 9176 }, new int[] { 24 }, new double[] { 16 }, new Animation(6672)),

		MITHRIL_CBOW(9461, 9438, new int[] { 9181 }, new int[] { 52 }, new double[] { 32 }, new Animation(6675)),

		ADAMANT_CBOW(9463, 9438, new int[] { 9183 }, new int[] { 61 }, new double[] { 41 }, new Animation(6676)),

		RUNITE_CBOW(9465, 9438, new int[] { 9185 }, new int[] { 69 }, new double[] { 50 }, new Animation(6677)),

		DRAGON_CBOW(25478, 9438, new int[] { 25917 }, new int[] { 94 }, new double[] { 100 }, new Animation(6677)),

		/**
		 * Arrows
		 */
		HEADLESS_ARROWS(52, 314, new int[] { 53 }, new int[] { 1 }, new double[] { 1 }, new Animation(19516)),

		BRONZE_ARROWS(39, 53, new int[] { 882 }, new int[] { 1 }, new double[] { 0.4 }, new Animation(19516)),

		IRON_ARROWS(40, 53, new int[] { 884 }, new int[] { 15 }, new double[] { 3.8 }, new Animation(19516)),

		STEEL_ARROWS(41, 53, new int[] { 886 }, new int[] { 30 }, new double[] { 6.3 }, new Animation(19516)),

		MITHRIL_ARROWS(42, 53, new int[] { 888 }, new int[] { 45 }, new double[] { 8.8 }, new Animation(19516)),

		ADAMANT_ARROWS(43, 53, new int[] { 890 }, new int[] { 60 }, new double[] { 11.3 }, new Animation(19516)),

		FRAGMENT_ARROWS(28435, 882, new int[] { 28464 }, new int[] { 60 }, new double[] { 5 }, new Animation(19516)),

		RUNITE_ARROWS(44, 53, new int[] { 892 }, new int[] { 75 }, new double[] { 13.8 }, new Animation(19516)),

		DRAGON_ARROWS(11237, 53, new int[] { 11212 }, new int[] { 90 }, new double[] { 16.3 }, new Animation(19516)),

		/**
		 * Bolts
		 */
		BRONZE_BOLT(9375, 314, new int[] { 877 }, new int[] { 9 }, new double[] { 0.5 }, new Animation(19516)),

		IRON_BOLT(9377, 314, new int[] { 9140 }, new int[] { 39 }, new double[] { 1.5 }, new Animation(19516)),

		STEEL_BOLT(9378, 314, new int[] { 9141 }, new int[] { 46 }, new double[] { 3.5 }, new Animation(19516)),

		MITHRIL_BOLT(9379, 314, new int[] { 9142 }, new int[] { 54 }, new double[] { 5 }, new Animation(19516)),

		ADAMANT_BOLT(9380, 314, new int[] { 9143 }, new int[] { 61 }, new double[] { 7 }, new Animation(19516)),

		RUNITE_BOLT(9381, 314, new int[] { 9144 }, new int[] { 69 }, new double[] { 10 }, new Animation(19516)),

		OPAL_BOLTS(45, 877, new int[] { 879 }, new int[] { 11 }, new double[] { 1.6 }, new Animation(19516)),

		BLURITE_BOLTS(9376, 314, new int[] { 9139 }, new int[] { 24 }, new double[] { 1 }, new Animation(19516)),

		JADE_BOLTS(9187, 9376, new int[] { 9335 }, new int[] { 26 }, new double[] { 2.4 }, new Animation(19516)),

		PEARL_BOLTS(46, 9140, new int[] { 880 }, new int[] { 41 }, new double[] { 3.2 }, new Animation(19516)),

		SILVER_BOLTS(9382, 314, new int[] { 9145 }, new int[] { 43 }, new double[] { 2.5 }, new Animation(19516)),

		RED_TOPAZ_BOLTS(9188, 9141, new int[] { 9336 }, new int[] { 48 }, new double[] { 3.9 }, new Animation(19516)),

		BARBED_BOLTS(47, 877, new int[] { 881 }, new int[] { 51 }, new double[] { 9.5 }, new Animation(19516)),

		SAPPHIRE_BOLTS(9189, 9142, new int[] { 9337 }, new int[] { 56 }, new double[] { 2.4 }, new Animation(19516)),

		EMERALD_BOLTS(9187, 9376, new int[] { 9335 }, new int[] { 26 }, new double[] { 4.7 }, new Animation(19516)),

		FRAGMENT_BOLTS(28435, 877, new int[] { 28463 }, new int[] { 60 }, new double[] { 5 }, new Animation(19516)),

		RUBY_BOLTS(9191, 9143, new int[] { 9339 }, new int[] { 63 }, new double[] { 6.3 }, new Animation(19516)),

		DIAMOND_BOLTS(9192, 9143, new int[] { 9340 }, new int[] { 65 }, new double[] { 7 }, new Animation(19516)),

		DRAGON_BOLTS(9193, 9144, new int[] { 9341 }, new int[] { 71 }, new double[] { 8.2 }, new Animation(19516)),

		ONYX_BOLTS(9194, 9144, new int[] { 9342 }, new int[] { 73 }, new double[] { 9.4 }, new Animation(19516)),

		DRAGON_BANE_BOLTS(24127, 21843, new int[] { 21660 }, new int[] { 85 }, new double[] { 15.6 },
				new Animation(19516)),

		ASCENSION_BOLTS(28436, 946, new int[] { 28465 }, new int[] { 90 }, new double[] { 20 }, new Animation(19516)),

		ASCENDRI_BOLTS(31867, 28465, new int[] { 31868 }, new int[] { 80 }, new double[] { 20 }, new Animation(19516)),

		/**
		 * Darts
		 */
		BRONZE_DART(819, 314, new int[] { 806 }, new int[] { 1 }, new double[] { 0.8 }, new Animation(19516)),

		IRON_DART(820, 314, new int[] { 807 }, new int[] { 22 }, new double[] { 1 }, new Animation(19516)),

		STEEL_DART(821, 314, new int[] { 808 }, new int[] { 37 }, new double[] { 1.7 }, new Animation(19516)),

		MITHRIL_DART(822, 314, new int[] { 809 }, new int[] { 52 }, new double[] { 4 }, new Animation(19516)),

		ADAMANT_DART(823, 314, new int[] { 810 }, new int[] { 67 }, new double[] { 5.6 }, new Animation(19516)),

		RUNITE_DART(824, 314, new int[] { 811 }, new int[] { 81 }, new double[] { 8.2 }, new Animation(19516)),

		DRAGON_DART(11232, 314, new int[] { 11230 }, new int[] { 95 }, new double[] { 10.4 }, new Animation(19516)),

		SAGAIE_SHAFT(21351, KNIFE, new int[] { 21353 }, new int[] { 73 }, new double[] { 1 }, new Animation(19516)),

		SAGIE_SHAFTS(21351, KNIFE, new int[] { 21353 }, new int[] { 83 }, new double[] { 50 }, new Animation(19516)),

		BOLAS(21359, 21358, new int[] { 21365 }, new int[] { 87 }, new double[] { 4 }, new Animation(-1)),

		BROAD_BOLTS(13279, 314, new int[] { 13280 }, new int[] { 55 }, new double[] { 30 }, new Animation(19516)),

		BROAD_ARROWS(13278, 53, new int[] { 4160 }, new int[] { 52 }, new double[] { 25 }, new Animation(19516));

		private static Map<Integer, Fletch> fletching = new HashMap<Integer, Fletch>();

		public static Fletch forId(int id) {
			return fletching.get(id);
		}

		static {
			for (Fletch fletch : Fletch.values())
				fletching.put(fletch.id, fletch);
		}

		private int[] product, level;
		private int id, selected;
		private double[] xp;
		private Animation anim;

		private Fletch(int id, int selected, int[] product, int level[], double[] xp, Animation anim) {
			this.id = id;
			this.product = product;
			this.selected = selected;
			this.xp = xp;
			this.anim = anim;
			this.level = level;
		}

		public int getId() {
			return id;
		}

		public int getSelected() {
			return selected;
		}

		public int[] getProduct() {
			return product;
		}

		public int[] getLevel() {
			return level;
		}

		public double[] getXp() {
			return xp;
		}

		public Animation getAnim() {
			return anim;
		}
	}

	private Fletch fletch;
	private int option;
	private int ticks;

	public Fletching(Fletch fletch, int option, int ticks) {
		this.fletch = fletch;
		this.option = option;
		this.ticks = ticks;
	}

	@Override
	public boolean start(Player player) {
		if (option >= fletch.getProduct().length)
			return false;
		if (!process(player))
			return false;
		return true;
	}

	@Override
	public boolean process(Player player) {
		if (ticks <= 0)
			return false;
		boolean stackable1 = ItemDefinitions.getItemDefinitions(fletch.getSelected()).isStackable();
		int amount = stackable1 ? 15 : 1;
		int amount2 = fletch.getSelected() != Fletching.DUNGEONEERING_KNIFE
				&& ItemDefinitions.getItemDefinitions(fletch.getId()).isStackable() ? 15 : 1;
		if (stackable1) {
			int amt1 = player.getInventory().getAmountOf(fletch.getSelected());
			int amt2 = player.getInventory().getAmountOf(fletch.getId());
			if (amt1 < amount) {
				amount = amt1;
				amount2 = amt1;
			}
			if (amt2 < amount2) {
				amount = amt2;
				amount2 = amt2;
			}
			if (amt1 == 0 || amt2 == 0)
				return false;
		}
		if (player.getSkills().getLevel(Skills.FLETCHING) < fletch.getLevel()[option]) {
			player.sendMessage("You need a fletching level of " + fletch.getLevel()[option] + " to fletch this.");
			return false;
		}
		if ((!player.getInventory().containsOneItem(fletch.getSelected(), amount)
				&& !((fletch.getSelected() == 1777 || fletch.getSelected() == 314)
						&& player.getPerkManager().masterFledger))
				|| !player.getInventory().containsOneItem(fletch.getId(), amount2))
			return false;
		if (fletch.getId() != 314 || fletch.getSelected() != 314) {
			if (!player.getInventory().containsOneItem(KNIFE)) {
				player.sendMessage("You will need a knife in order to fletch this.");
				return false;
			}
		}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		ticks--;
		if (player.getAnimations().hasKarateFletch && player.getAnimations().karateFletch) {
			player.setNextAnimation(new Animation(17299));
			player.setNextGraphics(new Graphics(3300));
		} else
			player.setNextAnimation(fletch.getAnim());
		boolean stackable1 = ItemDefinitions.getItemDefinitions(fletch.getSelected()).isStackable();
		int amount = stackable1 ? 15 : 1;
		int amount2 = fletch.getSelected() != Fletching.DUNGEONEERING_KNIFE
				&& ItemDefinitions.getItemDefinitions(fletch.getId()).isStackable() ? 15 : 1;
		int amount3 = ItemDefinitions
				.getItemDefinitions(fletch == Fletch.SAGIE_SHAFTS ? 5 : fletch.getProduct()[option]).isStackable() ? 15
						: 1;
		if (stackable1) {
			int amt1 = player.getInventory().getAmountOf(fletch.getSelected());
			int amt2 = player.getInventory().getAmountOf(fletch.getId());
			if (amt1 < amount) {
				amount = amt1;
				amount2 = amt1;
				amount3 = amt1;
			}
			if (amt2 < amount2) {
				amount = amt2;
				amount2 = amt2;
				amount3 = amt2;
			}
		}

		player.getInventory().deleteItem(fletch.getId(), amount2);

		if (fletch.getSelected() != KNIFE && fletch.getSelected() != DUNGEONEERING_KNIFE
				&& fletch.getSelected() != CHISLE && !((fletch.getSelected() == 1777 || fletch.getSelected() == 314)
						&& player.getPerkManager().masterFledger))
			player.getInventory().deleteItem(fletch.getSelected(), amount);

		player.getInventory().addItem(fletch.getProduct()[option], amount3);
		player.addItemsFletched();
		String name = new Item(fletch.getProduct()[option]).getDefinitions().getName().replace("(u)", "");
		player.sendMessage("You successfully create "
				+ (name.contains("bolts") || name.contains("arrows") || name.contains("tips") || name.contains("shaft")
						|| name.contains("dart") ? "some" : Utils.getAorAn(name))
				+ " " + name + "; " + "items fletched: " + Colors.red
				+ Utils.getFormattedNumber(player.getItemsFletched()) + "</col>.", true);
		player.getSkills().addXp(Skills.FLETCHING, fletch.getXp()[option] * amount);
		return player.getPerkManager().masterFledger ? 0 : 1;
	}

	@Override
	public void stop(final Player player) {
		player.setNextAnimation(new Animation(-1));
		setActionDelay(player, 3);
	}
}