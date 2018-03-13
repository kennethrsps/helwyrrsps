package com.rs.game.player.content;

import java.util.HashMap;
import java.util.Map;
import com.rs.game.player.Player;
import com.rs.utils.Utils;

/**
 * Used to handle repairing of broken/decharged items.
 * @author Zeus
 */
public class RepairItems {

	/**
	 * An enum containing all repairable data.
	 * @author Zeus
	 */
	public enum BrokenItems {

		TORVA_HELM(20138, 20135, 5000000),

		TORVA_PLATE(20142, 20139, 5000000),

		TORVA_LEGS(20146, 20143, 5000000),

		TORVA_GLOVES(24979, 24977, 5000000),

		TORVA_BOOTS(24985, 24983, 5000000),

		PERNIX_COWL(20150, 20147, 5000000),

		PERNIX_BODY(20154, 20151, 5000000),

		PERNIX_CHAPS(20158, 20155, 5000000),

		PERNIX_GLOVES(24976, 24974, 5000000),

		PERNIX_BOOTS(24991, 24989, 5000000),

		ZARYTE_BOW(20174, 20171, 7500000),

		VIRTUS_MASK(20162, 20159, 5000000),

		VIRTUS_TOP(20166, 20163, 5000000),

		VIRTUS_LEGS(20170, 20167, 5000000),

		VIRTUS_GLOVES(24982, 24980, 5000000),

		VIRTUS_BOOTS(24988, 24986, 5000000),

		TORVA_HELM_HALF(20137, 20135, 2000000),

		TORVA_PLATE_HALF(20141, 20139, 2000000),

		TORVA_LEGS_HALF(20145, 20143, 2000000),

		TORVA_GLOVES_HALF(24978, 24977, 2000000),

		TORVA_BOOTS_HALF(24984, 24983, 2000000),

		PERNIX_COWL_HALF(20149, 20147, 1500000),

		PERNIX_BODY_HALF(20153, 20151, 1500000),

		PERNIX_CHAPS_HALF(20157, 20155, 1500000),

		PERNIX_GLOVES_HALF(24975, 24974, 1500000),

		PERNIX_BOOTS_HALF(24990, 24989, 1500000),

		ZARYTE_BOW_HALF(20173, 20171, 2000000),

		VIRTUS_MASK_HALF(20161, 20159, 1000000),

		VIRTUS_TOP_HALF(20165, 20163, 1000000),

		VIRTUS_LEGS_HALF(20169, 20167, 1000000),

		VIRTUS_GLOVES_HALF(24981, 24980, 1000000),

		VIRTUS_BOOTS_HALF(24987, 24986, 1000000),
		
		AKRISAES_HOOD_100(21738, 21736, 100000),
		
		AKRISAES_MACE_100(21746, 21744, 100000),
		
		AKRISAES_TOP_100(21754, 21752, 100000),
		
		AKRISAES_SKIRT_100(21762, 21760, 100000),
				
		AHRIMS_HOOD_100 (4856, 4708 , 100000),
		
		AHRIMS_STAFF_100 (4862 , 4710 ,100000 ),
		
		AHRIMS_ROBE_TOP_100 (4868, 4712 ,100000 ),
		
		AHRIMS_ROBE_SKIRT_100 (4874, 4714 ,100000 ),
		
		DHAROKS_HELM_100 (4880,4716 ,100000 ),
		
		DHAROKS_GREATAXE_100 (4886,4718 ,100000 ),
		
		DHAROKS_PLATEBODY_100 (4892,4720 ,100000 ),
		
		DHAROKS_PLATELEGS_100 (4898,4722 ,100000 ),
		
		GUTHANS_HELM_100 (4904,4724 ,100000 ),
		
		GUTHANS_WARSPEAR_100 (4910,4726 ,100000 ),
		
		GUTHANS_PLATEBODY_100 (4916,4728 ,100000 ),
		
		GUTHANS_CHAINSKIRT_100 (4922,4730 ,100000 ),
		
		KARILS_COIF_100 (4928,4732 ,100000 ),
		
		KARILS_CROSSBOW_100 (4934,4734 ,100000 ),
		
		KARILS_TOP_100 (4940,4736 ,100000 ),
		
		KARILS_SKIRT_100 (4946,4738 ,100000 ),
		
		TORAGS_HELM_100 (4952,4745 , 100000),
		
		TORAGS_HAMMERS_100 (4958,4747 ,100000 ),
		
		TORAGS_PLATEBODY_100 (4964,4749 , 100000),
		
		TORAGS_PLATELEGS_100 (4970,4751 , 100000),
		
		VERACS_HELM_100 (4976,4753 , 100000),
		
		VERACS_FLAIL_100 (4982,4755 , 100000),
		
		VERACS_BRASSARD_100 (4988, 4757, 100000),
		
		VERACS_PLATESKIRT_100 (4994,4759 , 100000),	
		
		AKRISAES_HOOD_75(21739, 21736, 175000),
		
		AKRISAES_MACE_75(21747, 21744, 175000),
		
		AKRISAES_TOP_75(21755, 21752, 175000),
		
		AKRISAES_SKIRT_75(21763, 21760, 175000),
				
		AHRIMS_HOOD_75 (4857, 4708, 175000),
		
		AHRIMS_STAFF_75 (4863,4710 , 175000),
		
		AHRIMS_ROBE_TOP_75 (4869,4712 , 175000),
		
		AHRIMS_ROBE_SKIRT_75 (4875,4714 , 175000),
		
		DHAROKS_HELM_75 (4881,4716 , 175000),
		
		DHAROKS_GREATAXE_75 (4887,4718 , 175000),
		
		DHAROKS_PLATEBODY_75 (4893,4720 , 175000),
		
		DHAROKS_PLATELEGS_75 (4899,4722 , 175000),
		
		GUTHANS_HELM_75 (4905,4724 , 175000),
		
		GUTHANS_WARSPEAR_75 (4911,4726 , 175000),
		
		GUTHANS_PLATEBODY_75 (4917,4728 , 175000),
		
		GUTHANS_CHAINSKIRT_75 (4923,4730 , 175000),
		
		KARILS_COIF_75 (4929,4732 , 175000),
		
		KARILS_CROSSBOW_75 (4935, 4734, 175000),
		
		KARILS_TOP_75 (4941, 4736, 175000),
		
		KARILS_SKIRT_75 (4947, 4738, 175000),
		
		TORAGS_HELM_75 (4953, 4745, 175000),
		
		TORAGS_HAMMERS_75 (4959, 4747, 175000),
		
		TORAGS_PLATEBODY_75 (4965, 4749, 175000),
		
		TORAGS_PLATELEGS_75 (4971, 4751, 175000),
		
		VERACS_HELM_75 (4977, 4753, 175000),
		
		VERACS_FLAIL_75 (4983, 4755, 175000),
		
		VERACS_BRASSARD_75 (4989, 4757, 175000),
		
		VERACS_PLATESKIRT_75 (4995, 4759, 175000),	
		
		AKRISAES_HOOD_50(21740, 21736, 250000),
		
		AKRISAES_MACE_50(21748, 21744, 250000),
		
		AKRISAES_TOP_50(21756, 21752, 250000),
		
		AKRISAES_SKIRT_50(21764, 21760, 250000),
				
		AHRIMS_HOOD_50(4858, 4708, 250000),
		
		AHRIMS_STAFF_50 (4864,4710 , 250000),
		
		AHRIMS_ROBE_TOP_50 (4870,4712 , 250000),
		
		AHRIMS_ROBE_SKIRT_50 (4876,4714 , 250000),
		
		DHAROKS_HELM_50 (4882,4716 , 250000),
		
		DHAROKS_GREATAXE_50 (4888,4718 , 250000),
		
		DHAROKS_PLATEBODY_50 (4894,4720 , 250000),
		
		DHAROKS_PLATELEGS_50 (4900,4722 , 250000),
		
		GUTHANS_HELM_50 (4906,4724 , 250000),
		
		GUTHANS_WARSPEAR_50 (4912,4726 , 250000),
		
		GUTHANS_PLATEBODY_50 (4918,4728 , 250000),
		
		GUTHANS_CHAINSKIRT_50 (4924,4730 , 250000),
		
		KARILS_COIF_50 (4930, 4732, 250000),
		
		KARILS_CROSSBOW_50 (4936, 4734, 250000),
		
		KARILS_TOP_50 (4942, 4736, 250000),
		
		KARILS_SKIRT_50 (4948, 4738, 250000),
		
		TORAGS_HELM_50 (4954, 4745, 250000),
		
		TORAGS_HAMMERS_50 (4960, 4747, 250000),
		
		TORAGS_PLATEBODY_50 (4966, 4749, 250000),
		
		TORAGS_PLATELEGS_50 (4972, 4751, 250000),
		
		VERACS_HELM_50 (4978, 4753, 250000),
		
		VERACS_FLAIL_50 (4984, 4755, 250000),
		
		VERACS_BRASSARD_50 (4990, 4757, 250000),
		
		VERACS_PLATESKIRT_50 (4996, 4759, 250000),
		
		AKRISAES_HOOD_25(21741, 21736, 325000),
		
		AKRISAES_MACE_25(21749, 21744, 325000),
		
		AKRISAES_TOP_25(21757, 21752, 325000),
		
		AKRISAES_SKIRT_25(21765, 21760, 325000),
				
		AHRIMS_HOOD_25(4859, 4708, 325000),
		
		AHRIMS_STAFF_25 (4865,4710 , 325000),
		
		AHRIMS_ROBE_TOP_25 (4871,4712 , 325000),
		
		AHRIMS_ROBE_SKIRT_25 (4877,4714 , 325000),
		
		DHAROKS_HELM_25 (4883,4716 , 325000),
		
		DHAROKS_GREATAXE_25 (4889,4718 , 325000),
		
		DHAROKS_PLATEBODY_25 (4895,4720 , 325000),
		
		DHAROKS_PLATELEGS_25 (4901,4722 , 325000),
		
		GUTHANS_HELM_25 (4907,4724 , 325000),
		
		GUTHANS_WARSPEAR_25 (4913,4726 , 325000),
		
		GUTHANS_PLATEBODY_25 (4919,4728 , 325000),
		
		GUTHANS_CHAINSKIRT_25 (4925,4730 , 325000),
		
		KARILS_COIF_25 (4931,4732 , 325000),
		
		KARILS_CROSSBOW_25 (4937, 4734, 325000),
		
		KARILS_TOP_25 (4943, 4736, 325000),
		
		KARILS_SKIRT_25 (4949, 4738, 325000),
		
		TORAGS_HELM_25 (4955, 4745, 325000),
		
		TORAGS_HAMMERS_25 (4961, 4747, 325000),
		
		TORAGS_PLATEBODY_25 (4967, 4749, 325000),
		
		TORAGS_PLATELEGS_25 (4973, 4751, 325000),
		
		VERACS_HELM_25 (4979, 4753, 325000),
		
		VERACS_FLAIL_25 (4985, 4755, 325000),
		
		VERACS_BRASSARD_25 (4991, 4757, 325000),
		
		VERACS_PLATESKIRT_25 (4997, 4759, 325000),
		
		AKRISAES_HOOD_0(21742, 21736, 425000),
		
		AKRISAES_MACE_0(21750, 21744, 425000),
		
		AKRISAES_TOP_0(21758, 21752, 425000),
		
		AKRISAES_SKIRT_0(21766, 21760, 425000),
		
		GUTHANS_HELM_0(4908, 4724, 425000),

		GUTHANS_PLATEBODY_0(4920, 4728, 425000),

		GUTHANS_PLATESKIRT_0(4926, 4730, 425000),

		GUTHANS_WARSPEAR_0(4914, 4726, 425000),

		AHRIMS_HOOD_0(4860, 4708, 425000),

		AHRIMS_STAFF_0(4866, 4710, 425000),

		AHRIMS_ROBE_TOP_0(4872, 4712, 425000),

		AHRIMS_ROBE_SKIRT_0(4878, 4714, 425000),

		DHAROKS_HELM_0(4884, 4716, 425000),

		DHAROKS_GREATAXE_0(4890, 4718, 425000),

		DHAROKS_PLATEBODY_0(4896, 4720, 425000),

		DHAROKS_PLATELEGS_0(4902, 4722, 425000),

		KARILS_COIF_0(4932, 4732, 425000),

		KARILS_CROSSBOW_0(4938, 4734, 425000),

		KARILS_TOP_0(4944, 4736, 425000),

		KARILS_SKIRT_0(4950, 4738, 425000),

		TORAGS_HELM_0(4956, 4745, 425000),

		TORAGS_HAMMER_0(4962, 4747, 425000),

		TORAGS_PLATEBODY_0(4968, 4749, 425000),

		TORAGS_PLATELEGS_0(4974, 4751, 425000),

		VERACS_HELM_0(4980, 4753, 425000),

		VERACS_FLAIL_0(4986, 4755, 425000),

		VERACS_BRASSARD_0(4992, 4757, 425000),

		VERACS_PLATESKIRT_0(4998, 4759, 425000),

		CHAOTIC_RAPIER(18350, 18349, 500000),

		CHAOTIC_LONGSWORD(18352, 18351, 500000),

		CHAOTIC_MAUL(18354, 18353, 500000),

		CHAOTIC_STAFF(18356, 18355, 500000),

		CHAOTIC_CROSSBOW(18358, 18357, 500000),

		CHAOTIC_KITESHIELD(18360, 18359, 500000),

		EAGLE_EYE_KITESHIELD(18362, 18361, 500000),

		FARSEER_KITESHIELD(18364, 18363, 500000),
		
		FUNGAL_VISOR(22461, 22458, 10000),

		FUNGAL_PONCHO(22469, 22466, 50000),

		FUNGAL_LEGGINGS(22465, 22462, 40000),

		GRIFOLIC_VISOR(22473, 22470, 100000),

		GRIFOLIC_PONCHO(22481, 22478, 500000),

		GRIFOLIC_LEGGINGS(22477, 22474, 400000),

		GANODERMIC_VISOR(22484, 22482, 1000000),

		GANODERMIC_PONCHO(22492, 22490, 5000000),

		GANODERMIC_LEGGINGS(22488, 22486, 4000000),

		POLYPORE_STAFF(22497, 22494, 4000000),
		
		ASCENSION_CROSSBOW(28440, 28437, 4500000),
		
		ASCENSION_CROSSBOW_BARROWS(33320, 33318, 4500000),
		
		ASCENSION_CROSSBOW_SHADOW(33386, 33384, 4500000),
		
		ASCENSION_CROSSBOW_THIRD_AGE(33452, 33450, 4500000),
		
		ASCENSION_CROSSBOW_BLOOD(36323, 36321, 4500000),
		
		ASCENSION_CROSSBOW_HALF(28439, 28437, 2750000),
		
		ASCENSION_CROSSBOW_BARROWS_HALF(33319, 33318, 2750000),
		
		ASCENSION_CROSSBOW_SHADOW_HALF(33385, 33384, 2750000),
		
		ASCENSION_CROSSBOW_THIRD_AGE_HALF(33451, 33450, 2750000),
		
		ASCENSION_CROSSBOW_BLOOD_HALF(36322, 36321, 2750000),
		
		NOXIOUS_SCYTHE(31728, 31725, 4500000),
		
		NOXIOUS_SCYTHE_HALF(31727, 31725, 2750000),
		
		NOXIOUS_SCYTHE_BARROWS(33332, 33330, 4500000),
		
		NOXIOUS_SCYTHE_BARROWS_HALF(33331, 33330, 2750000),
		
		NOXIOUS_SCYTHE_SHADOW(33398, 33396, 4500000),
		
		NOXIOUS_SCYTHE_SHADOW_HALF(33397, 33396, 2750000),
		
		NOXIOUS_SCYTHE_THIRD_AGE(33464, 33462, 4500000),
		
		NOXIOUS_SCYTHE_THIRD_AGE_HALF(33463, 33462, 2750000),
		
		NOXIOUS_SCYTHE_BLOOD(36335, 36333, 4500000),
		
		NOXIOUS_SCYTHE_BLOOD_HALF(36334, 36333, 2750000),
		
		NOXIOUS_STAFF(31732, 31729, 4500000),
		
		NOXIOUS_STAFF_HALF(31731, 31729, 2750000),
		
		NOXIOUS_STAFF_BARROWS(33335, 33333, 4500000),
		
		NOXIOUS_STAFF_BARROWS_HALF(33334, 33333, 2750000),
		
		NOXIOUS_STAFF_SHADOW(33401, 33399, 4500000),
		
		NOXIOUS_STAFF_SHADOW_HALF(33400, 33399, 2750000),
		
		NOXIOUS_STAFF_THIRD_AGE(33467, 33465, 4500000),
		
		NOXIOUS_STAFF_THIRD_AGE_HALF(33466, 33465, 2750000),
		
		NOXIOUS_STAFF_BLOOD(36338, 36336, 4500000),
		
		NOXIOUS_STAFF_BLOOD_HALF(36337, 36336, 2750000),
		
		NOXIOUS_LONGBOW(31736, 31733, 4500000),
		
		NOXIOUS_LONGBOW_HALF(31735, 31733, 2750000),
		
		NOXIOUS_LONGBOW_BARROWS(33338, 33336, 4500000),
		
		NOXIOUS_LONGBOW_BARROWS_HALF(33337, 33336, 2750000),
		
		NOXIOUS_LONGBOW_SHADOW(33404, 33402, 4500000),
		
		NOXIOUS_LONGBOW_SHADOW_HALF(33403, 33402, 2750000),
		
		NOXIOUS_LONGBOW_THIRD_AGE(33470, 33468, 4500000),
		
		NOXIOUS_LONGBOW_THIRD_AGE_HALF(33469, 33468, 2750000),
		
		NOXIOUS_LONGBOW_BLOOD(36341, 36339, 4500000),
		
		NOXIOUS_LONGBOW_BLOOD_HALF(36340, 36339, 2750000),
		
		DRYGORE_RAPIER(26582, 26579, 3000000),
		
		DRYGORE_RAPIER_HALF(26581, 26579, 1500000),
		
		DRYGORE_RAPIER_BARROWS(33308, 33306, 3000000),
		
		DRYGORE_RAPIER_BARROWS_HALF(33307, 33306, 1500000),
		
		DRYGORE_RAPIER_SHADOW(33373, 33372, 1500000),
		
		DRYGORE_RAPIER_SHADOW_HALF(33374, 33372, 3000000),
		
		DRYGORE_RAPIER_THIRD_AGE(33440, 33438, 1500000),
		
		DRYGORE_RAPIER_THIRD_AGE_HALF(33439, 33438, 3000000),
		
		DRYGORE_RAPIER_BLOOD(36311, 36309, 1500000),
		
		DRYGORE_RAPIER_BLOOD_HALF(36310, 36309, 3000000),
		
		DRYGORE_LONGSWORD(26590, 26587, 3000000),
		
		DRYGORE_LONGSWORD_HALF(26589, 26587, 1500000),
		
		DRYGORE_LONGSWORD_BARROWS(33313, 33312, 3000000),
		
		DRYGORE_LONGSWORD_BARROWS_HALF(33314, 33312, 1500000),
		
		DRYGORE_LONGSWORD_SHADOW(33380, 33378, 3000000),
		
		DRYGORE_LONGSWORD_SHADOW_HALF(33379, 33378, 1500000),
		
		DRYGORE_LONGSWORD_THIRD_AGE(33446, 33444, 3000000),
		
		DRYGORE_LONGSWORD_THIRD_AGE_HALF(33445, 33444, 1500000),
		
		DRYGORE_LONGSWORD_BLOOD(36317, 36315, 3000000),
		
		DRYGORE_LONGSWORD_BLOOD_HALF(36316, 36315, 1500000),
		
		DRYGORE_MACE(26598, 26595, 3000000),
		
		DRYGORE_MACE_HALF(26597, 26595, 1500000),
		
		DRYGORE_MACE_BARROWS(33302, 33300, 3000000),
		
		DRYGORE_MACE_BARROWS_HALF(33301, 33300, 1500000),
		
		DRYGORE_MACE_SHADOW(33368, 33366, 3000000),
		
		DRYGORE_MACE_SHADOW_HALF(33367, 33366, 1500000),
		
		DRYGORE_MACE_THIRD_AGE(33434, 33432, 3000000),
		
		DRYGORE_MACE_THIRD_AGE_HAF(33433, 33432, 1500000),
		
		DRYGORE_MACE_BLOOD(36305, 36303, 3000000),
		
		DRYGORE_MACE_BLOOD_HALF(36304, 36303, 1500000),
		
		SUPERIOR_TETSU_HELM(26331, 26322, 600000),
		
		SUPERIOR_TETSU_BODY(26332, 26323, 2400000),
		
		SUPERIOR_TETSU_LEGS(26333, 26324, 1200000),
		
		SUPERIOR_TETSU_KATANA_HALF(33880, 33879, 1250000),
		
		SUPERIOR_TETSU_KATANA(33881, 33879, 2500000),
		
		SUPERIOR_DEATH_LOTUS_HELM(26355, 26352, 600000),
		
		SUPERIOR_DEATH_LOTUS_CHESTPLATE(26356, 26353, 2400000),
		
		SUPERIOR_DEATH_LOTUS_CHAPS(26357, 26354, 1200000),
		
		SUPERIOR_SEASINGERS_HOOD(26343, 26334, 600000),
		
		SUPERIOR_SEASINGERS_ROBE_TOP(26344, 26335, 2400000),
		
		SUPERIOR_SEASINGERS_ROBE_BOTTOM(26345, 26336, 1200000),
		
		SUPERIOR_SEASINGER_KIBA_HALF(33887, 33886, 1250000),
		
		SUPERIOR_SEASINGER_KIBA(33888, 33886, 2500000),
		
		SUPERIOR_SEASINGER_MAKIGAI_HALF(33890, 33889, 1250000),
		
		SUPERIOR_SEASINGER_MAKIGAI(33891, 33889, 2500000),
		
		ASCENSION_GRIPS(31203, 31205, 1000000),
		
		CELESTIAL_HANDWRAPS(31189, 31191, 1000000),
		
		RAZORBACK_GAUNTLETS(30213, 30215, 1000000),
		
		//(worn) items
		
		DEATH_LOTUS_HELM(26349, 26346, 600000),
		
		DEATH_LOTUS_CHESTPLATE(26350, 26347, 2400000),
		
		DEATH_LOTUS_CHAPS(26351, 26348, 1200000),
		
		SEASINGERS_HOOD(26340, 26337, 600000),
		
		SEASINGERS_ROBE_TOP(26341, 26338, 2400000),
		
		SEASINGERS_ROBE_BOTTOM(26342, 26339, 1200000),
		
		TETSU_HELM(26328, 26325, 600000),
		
		TETSU_BODY(26329, 26326, 2400000),
		
		TETSU_LEGS(26330, 26327, 1200000),
				
		AHRIMS_HOOD(35551, 4708 , 100000),
		
		AHRIMS_ROBE_TOP(35553, 4712 ,100000 ),
		
		AHRIMS_ROBE_SKIRT(35554, 4714 ,100000 ),
		
		DHAROKS_HELM(35555, 4716 ,100000 ),
		
		DHAROKS_PLATEBODY(35557, 4720 ,100000 ),
		
		DHAROKS_PLATELEGS(35558, 4722 ,100000 ),
		
		GUTHANS_HELM(35559, 4724 ,100000 ),
		
		GUTHANS_PLATEBODY(35561, 4728 ,100000 ),
		
		GUTHANS_CHAINSKIRT(35562, 4730 ,100000 ),
		
		KARILS_COIF(35563, 4732 ,100000 ),
		
		KARILS_TOP(35565, 4736 ,100000 ),
		
		KARILS_SKIRT(35566, 4738 ,100000 ),
		
		TORAGS_HELM(35567, 4745 , 100000),
		
		TORAGS_PLATEBODY(35569, 4749 , 100000),
		
		TORAGS_PLATELEGS(35570, 4751 , 100000),
		
		VERACS_HELM(35571, 4753 , 100000),
		
		VERACS_BRASSARD(35573, 4757, 100000),
		
		VERACS_PLATESKIRT(35579, 4759 , 100000),
		
		
		;
		
		/** Broken - Repaired - Cost **/;
		
		private int id;
		private int id2;
		private int Price;

		private static Map<Integer, BrokenItems> BROKENITEMS = new HashMap<Integer, BrokenItems>();

		static {
			for (BrokenItems brokenitems : BrokenItems.values()) {
				BROKENITEMS.put(brokenitems.getId(), brokenitems);
			}
		}

		public static BrokenItems forId(int id) {
			return BROKENITEMS.get(id);
		}

		private BrokenItems(int id, int id2, int Price) {
			this.id = id;
			this.id2 = id2;
			this.Price = Price;
		}

		public int getId() {
			return id;
		}

		public int getId2() {
			return id2;
		}

		public int getPrice() {
			return Price;
		}

	}

	/**
	 * Used to handle repairing items.
	 * @param player The player repairing.
	 * @param itemId The itemId to repair.
	 * @return if Repaired.
	 */
	public static boolean repair(Player player, int itemId) {
		final BrokenItems brokenitems = BrokenItems.forId(itemId);
		int price = brokenitems.getPrice();
		if (player.hasMoney(price)) {
			player.getInventory().deleteItem(itemId, 1);
			player.takeMoney(price);
			player.getInventory().addItem(brokenitems.getId2(), 1);
			player.getCharges().addCharges(itemId, ItemConstants.getItemDefaultCharges(itemId), -1);
			return true;
		} else {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You dont have enough coins to repair this; you need " + getPrice(itemId) + ".");
			return false;
		}
	}
	
	/**
	 * Checks broken item price to be repaired.
	 * @param player The player checking.
	 * @param itemId The itemId to check.
	 * @return The price as an int.
	 */
	public static int checkPrice(Player player, int itemId) {
		final BrokenItems brokenitems = BrokenItems.forId(itemId);
		int price = brokenitems.getPrice();
		return price;
	}

	/**
	 * Gets the price and formats it.
	 * @param itemId The itemId to get the price.
	 * @return the formatted String.
	 */
	public static String getPrice(int itemId) {
		final BrokenItems brokenitems = BrokenItems.forId(itemId);
		int price = brokenitems.getPrice();
		return Utils.getFormattedNumber(price);
	}
}