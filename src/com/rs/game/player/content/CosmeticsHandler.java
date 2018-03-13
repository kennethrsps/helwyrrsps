package com.rs.game.player.content;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.item.Item;
import com.rs.game.player.Equipment;
import com.rs.game.player.Player;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.network.protocol.codec.decode.impl.ButtonHandler;

public class CosmeticsHandler {

	/**
	 * Every Item Id You Put will show in the dialogue when clicked at the slot
	 * no matter how many it will calculate the number of pages needed
	 * automaticly
	 */
	public static int[] HATS = { 24583, 24595, 24605, 24627, 24639, 24567, 24569, 24571, 24827, 24822, 25074, 25086,
			25098, 25136, 25148, 25160, 25170, 25174, 25273, 24806, 24819, 25374, 25386, 26043, 26158, 26170, 26182,
			26402, 26414, 26390, 26426, 26464, 27075, 27083, 27174, 27181, 27189, 27205, 27198, 27120, 27134, 27220,
			27419, 27431, 27443, 27455, 23664, 27557, 27565, 27572, 27580, 27602, 27604, 27606, 27610, 27608, 27612,
			27549, 28001, 28049, 28056, 28063, 28070, 28838, 28846, 28854, 28861, 28868, 28876, 28884, 28891, 28822,
			28823, 29009, 29015, 29021, 29027, 29033, 29039, 29045, 29051, 29057, 29063, 29069, 29075, 29081, 29087,
			29561, 29575, 29587, 29601, 28428, 28941, 29421, 29431, 29461, 29766, 29782, 29958, 29762, 29976, 29990,
			30147, 30159, 30167, 30175, 30183, 30191, 30199, 30205, 30359, 30361, 30417, 30433, 30461, 30469, 30476,
			30484, 30597, 30606, 30617, 30622, 30627, 29944, 30613, 30952, 30959, 30967, 30974, 30976, 30978, 31028,
			31039, 31040, 31128, 31211, 31219, 31225, 31232, 31296, 31536, 31546, 31706, 31707, 31708, 31833, 31975,
			31982, 32315, 32322, 32329, 32543, 32551, 32560, 32568, 33587, 33593, 33637, 33654, 33674, 33680, 33686,
			33692, 33698, 33705, 33711, 33755 };
	public static int[] CAPES = { 30000, 30044, 30046, 30048, 30050, 30602, 30611, 30893, 30895, 30897, 30899, 31543,
			31551, 31823, 31825, 32548, 32556, 32565, 32573, 33655, 33853, 24613, 24623, 24635, 24649, 25142, 25154,
			25166, 25172, 24817, 25396, 26071, 27076, 27084, 27178, 27186, 27210, 27130, 27554, 28843, 28851, 28873,
			28881, 29571, 29597, 29424, 29436, 29792, 30427, 30445, 30466, 30481, 29943, 31139, 31182, 31183, 31184,
			31300, 31827, 31829, 31603, 31618, 32549, 32557, 32566, 32574, 33642 };
	public static int[] AMULETS = { 25100, 24815, 31710, 31987 };
	public static int[] WEAPONS = { 24904, 25112, 26015, 28983, 31124, 32400, 24651, 24573, 25283, 25398, 26019, 26476,
			28979, 28429, 29945, 31122, 31363, 26011, 28958, 29794, 30956, 30963, 31115, 33701, 33708, 24900, 24886,
			28987, 27597, 29986, 30949, 31126, 31241, 31240, 31242, 31243, 33634, 33747, 24824, 24902, 25110, 30614,
			30603, 33689, 24898, 26007, 29466, 29425, 30337, 30339, 32396, 24892, 24894, 26005, 24577, 26053, 28737,
			28738, 30002, 31133, 31357, 31552, 33677, 33749, 24890, 30345, 30346, 31359, 24888, 26073, 27614, 28741,
			28742, 31140, 31541, 33751, 24575, 25285, 26001, 29776, 31369, 32392, 33695, 28011, 28739, 28740, 29946,
			30612, 31519, 31520, 33709, 33597, 33683, 29428, 30341, 30343, 31366 };
	public static int[] CHESTS = { 24585, 24597, 24607, 24617, 24629, 24641, 24828, 25076, 25088, 25102, 25138, 25150,
			25162, 25275, 24807, 25376, 25388, 26045, 26063, 26160, 26172, 26184, 26404, 26416, 26392, 26428, 26466,
			27077, 27085, 27175, 27182, 27190, 27206, 27199, 27122, 27136, 27222, 27421, 27433, 27445, 27457, 27558,
			27566, 27573, 27581, 27550, 28003, 28050, 28057, 28064, 28071, 28839, 28847, 28855, 28862, 28869, 28877,
			28885, 28892, 28950, 28971, 29010, 29016, 29022, 29028, 29034, 29040, 29046, 29052, 29058, 29064, 29070,
			29076, 29082, 29088, 29563, 29577, 29589, 29603, 28431, 28942, 29419, 29432, 29462, 29768, 29784, 29962,
			29978, 29992, 30149, 30161, 30169, 30177, 30185, 30193, 30635, 30201, 30207, 30419, 30437, 30462, 30470,
			30477, 30485, 30598, 30607, 30618, 30623, 30628, 30887, 30953, 30960, 30964, 30980, 30981, 30982, 30988,
			31129, 31135, 31111, 31118, 31212, 31220, 31226, 31233, 31237, 31297, 31537, 31547, 31698, 31702, 31834,
			31976, 31983, 32316, 32323, 32330, 32527, 32529, 32531, 32544, 32552, 32561, 32569, 33594, 33638, 33712,
			33675, 33681, 33687, 33693, 33699, 33706, 33756 };
	public static int[] SHIELDS = { 26037, 26031, 26017, 28985, 31125, 32402, 26029, 26025, 25997, 26023, 26021, 26478,
			28981, 28430, 31123, 31365, 26013, 28960, 29796, 31116, 33702, 26035, 26033, 26009, 29467, 29426, 30338,
			30340, 32398, 26027, 25999, 26003, 29778, 32394, 29429, 30342, 30344, 31368, 24896, 25287, 26474, 28989,
			29427, 30347, 30348, 30630, 31127, 31215, 31223, 31229, 31709, 27144, 31361, 33522 };
	public static int[] LEGS = { 24587, 24599, 24609, 24619, 24631, 24643, 24830, 25080, 25092, 25106, 25140, 25152,
			25164, 25277, 24809, 25378, 25390, 26047, 26065, 26162, 26174, 26186, 26406, 26418, 26394, 26430, 26468,
			27079, 27087, 27176, 27183, 27191, 27207, 27200, 27124, 27138, 27224, 27423, 27435, 27447, 27459, 27560,
			27568, 27575, 27583, 27552, 28007, 28052, 28059, 28066, 28073, 28840, 28848, 28856, 28863, 28870, 28878,
			28886, 28893, 28952, 28973, 29011, 29017, 29023, 29029, 29035, 29041, 29047, 29053, 29059, 29065, 29071,
			29077, 29083, 29090, 29565, 29579, 29591, 29605, 28432, 28943, 29420, 29433, 29463, 29770, 29786, 29960,
			29980, 29994, 30151, 30163, 30171, 30179, 30187, 30194, 30632, 30202, 30633, 30208, 30634, 30421, 30439,
			30463, 30471, 30478, 30487, 30599, 30608, 30619, 30624, 30629, 30954, 30961, 30965, 30983, 30984, 30985,
			30989, 31130, 31136, 31112, 31119, 31213, 31214, 31221, 31222, 31227, 31228, 31234, 31238, 31298, 31538,
			31548, 31699, 31703, 31835, 31977, 31984, 32317, 32324, 32331, 32545, 32553, 32562, 32570, 33595, 33639,
			33713, 33676, 33682, 33688, 33694, 33700, 33707, 33757 };
	public static int[] BOOTS = { 24589, 24601, 24611, 24621, 24633, 24645, 24832, 25082, 25094, 25108, 25144, 25156,
			25168, 25281, 24811, 25382, 25394, 26051, 26069, 26166, 26178, 26190, 26410, 26422, 26398, 26434, 26470,
			27080, 27088, 27177, 27184, 27193, 27209, 27201, 27128, 27142, 27228, 27427, 27439, 27451, 27463, 27561,
			27569, 27576, 27584, 27553, 28009, 28053, 28060, 28067, 28074, 28842, 28850, 28858, 28865, 28872, 28880,
			28888, 28895, 28956, 28977, 29012, 29018, 29024, 29030, 29036, 29042, 29048, 29054, 29060, 29066, 29072,
			29078, 29084, 29091, 29569, 29583, 29595, 29609, 28434, 28945, 29465, 29774, 29790, 29966, 29984, 29998,
			30155, 30196, 30425, 30443, 30465, 30473, 30479, 30488, 30600, 30609, 30955, 30962, 30966, 30986, 30987,
			31132, 31138, 31114, 31121, 31217, 31239, 31299, 31539, 31550, 31836, 31979, 31986, 32318, 32325, 32332,
			32546, 32554, 32563, 32571, 33640, 33714, 33758 };
	public static int[] GLOVES = { 24591, 24647, 25078, 25090, 25104, 25279, 24813, 25380, 25392, 26049, 26067, 26164,
			26176, 26188, 26408, 26420, 26396, 26432, 26472, 27078, 27086, 27185, 27192, 27208, 27202, 27126, 27140,
			27226, 27425, 27437, 27449, 27461, 27559, 27567, 27574, 27582, 27551, 28005, 28051, 28058, 28065, 28072,
			28841, 28849, 28857, 28864, 28871, 28879, 28887, 28894, 28954, 28975, 29089, 29567, 29581, 29593, 29607,
			28433, 28944, 29464, 29772, 29788, 29964, 29982, 29996, 30153, 30195, 30423, 30441, 30464, 30472, 30480,
			30486, 30601, 30610, 31131, 31137, 31113, 31120, 31216, 31301, 31540, 31549, 31711, 31837, 31978, 31985,
			32319, 32326, 32333, 32547, 32555, 32564, 32572, 33641, 33715, 33759 };
	public static int[] WINGS = { 29092, 29094, 29096, 29098, 29100, 29102, 29104, 29106, 33596 };

	public static final int[][] FULL_OUTFITS = { { 24583, 24585, 24587, 24591, 24589, },
			{ 24595, 24601, 24597, 24599, }, { 24605, 24613, 24607, 24609, 24611, }, { 24623, 24621, 24617, 24619, },
			{ 24627, 24635, 24629, 24631, 24633, }, { 24639, 24649, 24651, 24641, 26029, 24643, 24647, 24645, },
			{ 24827, 24832, 24828, 24830, }, { 25074, 25076, 25080, 25078, 25082, },
			{ 25086, 25088, 25092, 25090, 25094, }, { 25098, 25100, 25102, 25106, 25104, 25108, },
			{ 25136, 25142, 25138, 25140, 25144, }, { 25148, 25154, 25150, 25152, 25156, },
			{ 25160, 25166, 25162, 25164, 25168, }, { 25273, 25275, 25277, 25279, 25281, },
			{ 25374, 25376, 25378, 25380, 25382, }, { 25386, 25396, 25388, 25390, 25392, 25394, },
			{ 26043, 26045, 26047, 26049, 26051, }, { 26071, 26063, 26065, 26067, 26069, },
			{ 26158, 26160, 26162, 26164, 26166, }, { 26170, 26172, 26174, 26176, 26178, },
			{ 26182, 26184, 26186, 26188, 26190, }, { 26402, 26404, 26406, 26408, 26410, },
			{ 26414, 26416, 26418, 26420, 26422, }, { 26390, 26392, 26394, 26396, 26398, },
			{ 26426, 26428, 26430, 26432, 26434, }, { 26472, 26470, 26466, 26468, },
			{ 26464, 26466, 26468, 26472, 26470, }, { 27075, 27076, 27077, 27079, 27078, 27080, },
			{ 27083, 27084, 27085, 27087, 27086, 27088, }, { 27174, 27178, 27175, 27176, 27177, },
			{ 27181, 27186, 27182, 27183, 27185, 27184, }, { 27189, 27190, 27191, 27192, 27193, },
			{ 27205, 27210, 27206, 27207, 27208, 27209, }, { 27198, 27199, 27200, 27202, 27201, },
			{ 27120, 27130, 27122, 27124, 27126, 27128, }, { 27134, 27136, 27138, 27140, 27142, },
			{ 27220, 27222, 27224, 27226, 27228, }, { 27419, 27421, 27423, 27425, 27427, },
			{ 27431, 27433, 27435, 27437, 27439, }, { 27443, 27445, 27447, 27449, 27451, },
			{ 27455, 27457, 27459, 27461, 27463, }, { 27557, 27558, 27560, 27559, 27561, },
			{ 27565, 27566, 27568, 27567, 27569, }, { 27572, 27573, 27575, 27574, 27576, },
			{ 27580, 27581, 27583, 27582, 27584, }, { 27549, 27554, 27550, 27552, 27551, 27553, },
			{ 28001, 28003, 28007, 28005, 28009, }, { 28049, 28050, 28052, 28051, 28053, },
			{ 28056, 28057, 28059, 28058, 28060, }, { 28063, 28064, 28066, 28065, 28067, },
			{ 28070, 28071, 28073, 28072, 28074, }, { 28838, 28843, 28839, 28840, 28841, 28842, },
			{ 28846, 28851, 28847, 28848, 28849, 28850, }, { 28854, 28855, 28856, 28857, 28858, },
			{ 28861, 28862, 28863, 28864, 28865, }, { 28868, 28873, 28869, 28870, 28871, 28872, },
			{ 28876, 28881, 28877, 28878, 28879, 28880, }, { 28884, 28885, 28886, 28887, 28888, },
			{ 28891, 28892, 28893, 28894, 28895, }, { 28954, 28956, 28950, 28952, }, { 28975, 28977, 28971, 28973, },
			{ 29009, 29012, 29010, 29011, }, { 29015, 29018, 29016, 29017, }, { 29021, 29024, 29022, 29023, },
			{ 29027, 29030, 29028, 29029, }, { 29033, 29036, 29034, 29035, }, { 29039, 29042, 29040, 29041, },
			{ 29045, 29048, 29046, 29047, }, { 29051, 29054, 29052, 29053, }, { 29057, 29060, 29058, 29059, },
			{ 29063, 29066, 29064, 29065, }, { 29069, 29072, 29070, 29071, }, { 29075, 29078, 29076, 29077, },
			{ 29081, 29084, 29082, 29083, }, { 29087, 29088, 29090, 29089, 29091, },
			{ 29561, 29571, 29563, 29565, 29567, 29569, }, { 29575, 29577, 29579, 29581, 29583, },
			{ 29587, 29597, 29589, 29591, 29593, 29595, }, { 29601, 29603, 29605, 29607, 29609, },
			{ 28428, 28429, 28431, 28430, 28432, 28433, 28434, }, { 28941, 28942, 28943, 28944, 28945, },
			{ 29421, 29424, 29419, 29420, }, { 29431, 29436, 29432, 29433, }, { 29461, 29462, 29463, 29464, 29465, },
			{ 29766, 29776, 29768, 29778, 29770, 29772, 29774, },
			{ 29782, 29792, 29794, 29784, 29796, 29786, 29788, 29790, }, { 29958, 29962, 29960, 29964, 29966, },
			{ 29976, 29986, 29978, 29980, 29982, 29984, }, { 29990, 30000, 30002, 29992, 29994, 29996, 29998, },
			{ 30147, 30149, 30151, 30153, 30155, }, { 30159, 30161, 30163, 30153, 30155, },
			{ 30167, 30169, 30171, 30153, 30155, }, { 30175, 30177, 30179, 30153, 30155, },
			{ 30183, 30185, 30187, 30153, 30155, }, { 30191, 30193, 30194, 30195, 30196, },
			{ 30199, 30201, 30202, 30195, 30196, }, { 30205, 30207, 30208, 30195, 30196, },
			{ 30417, 30427, 30419, 30421, 30423, 30425, }, { 30433, 30445, 30437, 30439, 30441, 30443, },
			{ 30461, 30466, 30462, 30463, 30464, 30465, }, { 30469, 30470, 30471, 30472, 30473, },
			{ 30476, 30481, 30477, 30478, 30480, 30479, }, { 30484, 30485, 30487, 30486, 30488, },
			{ 30597, 30602, 30603, 30598, 30599, 30601, 30600, }, { 30606, 30611, 30612, 30607, 30608, 30610, 30609, },
			{ 30617, 30618, 30619, }, { 30622, 30623, 30624, }, { 30627, 30628, 30629, },
			{ 30952, 30956, 30953, 30954, 30955, }, { 30959, 30963, 30960, 30961, 30962, },
			{ 30967, 30966, 30964, 30965, }, { 30974, 30986, 30980, 30983, }, { 30976, 30987, 30981, 30984, },
			{ 30978, 30982, 30985, }, { 31128, 31129, 31130, 31131, 31132, }, { 31139, 31135, 31136, 31137, 31138, },
			{ 31113, 31114, 31111, 31112, }, { 31120, 31121, 31118, 31119, }, { 31211, 31212, 31213, 31216, 31217, },
			{ 31219, 31220, 31221, }, { 31225, 31226, 31227, }, { 31232, 31233, 31234, }, { 31237, 31239, 31238, },
			{ 31296, 31300, 31297, 31298, 31301, 31299, }, { 31536, 31543, 31541, 31537, 31538, 31540, 31539, },
			{ 31546, 31551, 31552, 31547, 31548, 31549, 31550, }, { 31698, 31699, }, { 31709, 31711, 31710, },
			{ 31833, 31834, 31835, 31837, 31836, }, { 31975, 31976, 31977, 31978, 31979, },
			{ 31982, 31983, 31984, 31985, 31986, }, { 32315, 32316, 32317, 32319, 32318, },
			{ 32322, 32323, 32324, 32326, 32325, }, { 32329, 32330, 32331, 32333, 32332, },
			{ 32543, 32549, 32548, 32544, 32545, 32547, 32546, }, { 32551, 32557, 32556, 32552, 32553, 32555, 32554, },
			{ 32560, 32566, 32565, 32561, 32562, 32564, 32563, }, { 32568, 32574, 32573, 32569, 32570, 32572, 32571, },
			{ 33593, 33633, 33597, 33594, 33596, 33595, }, { 33637, 33642, 33638, 33639, 33641, 33640, },
			{ 33711, 33709, 33712, 33713, 33715, 33714, }, { 33674, 33677, 33675, 33676, },
			{ 33680, 33683, 33681, 33682, }, { 33686, 33689, 33687, 33688, }, { 33692, 33695, 33693, 33694, },
			{ 33698, 33701, 33699, 33702, 33700, }, { 33705, 33708, 33706, 33707, },
			{ 33755, 33756, 33757, 33759, 33758, } };
	public static String[] OUTFIT_NAMES = { "Cabaret outfit", "Colosseum outfit", "Feline outfit", "Gothic outfit",
			"Swashbuckler outfit", "Assassin outfit", "Beachwear outfit", "Monarch outfit", "Noble outfit",
			"Servant outfit", "Fox outfit", "Wolf outfit", "Panda outfit", "Dwarven Warsuit outfit",
			"K'ril's Battlegear outfit", "K'ril's Godcrusher armour", "Ariane's outfit", "Ozan's outfit",
			"TokHaar Brute outfit", "TokHaar Veteran outfit", "TokHaar Warlord outfit", "Eastern Captain's outfit",
			"Eastern Crew's outfit", "Western Captain's outfit", "Western Crew's outfit", "Paladin  outfit",
			"Paladin Hero outfit", "Kalphite Sentinel outfit", "Kalphite Emissary outfit", "Shadow Cat outfit",
			"Shadow Hunter outfit", "Shadow Sentinel outfit", "Shadow Demon outfit", "Shadow Knight outfit",
			"Greater demonflesh armour", "Lesser demonflesh armour", "Dragon wolf outfit", "Guthixian war robes",
			"Saradominist war robes", "Zamorakian war robes", "Zarosian war robes", "Robes of Sorrow outfit",
			"Vestments of Sorrow outfit", "Robes of Remembrance outfit", "Vestments of Remembrance outfit",
			"Skypouncer outfit", "Executioner outfit", "Flameheart armour", "Stoneheart armour", "Stormheart armour",
			"Iceheart armour", "Colossus armour", "Veteran colossus armour", "Titan armour", "Veteran titan armour",
			"Behemoth armour", "Veteran behemoth armour", "Beast armour", "Veteran beast armour", "Linza's outfit",
			"Owen's armaments", "Dervish outfit", "Eastern outfit", "Tribal outfit", "Samba outfit",
			"Theatrical outfit", "Pharaoh's outfit", "Wushanko outfit", "Silken outfit", "Colonist's outfit",
			"Highland outfit", "Feathered serpent outfit", "Musketeer outfit", "Elven outfit", "Werewolf outfit",
			"Ambassador of Order outfit", "Envoy of Order outfit", "Ambassador of Chaos", "Envoy of Chaos outfit",
			"Aurora armour", "Templar armour", "Superhero outfit", "Superior hero outfit", "Dulcin armour",
			"Ravenskull outfit", "Deathless Regent outfit", "Ancient mummy outfit", "Ogre infiltrator outfit",
			"Drakewing outfit", "Replica infinity robes", "Replica infinity robes (air)",
			"Replica infinity robes (earth)", "Replica infinity robes (fire)", "Replica infinity robes (water)",
			"Replica dragon plate armour", "Replica dragon plate armour (sp)", "Replica dragon plate armour (or)",
			"Frostwalker outfit", "Glad tidings outfit", "Golem of Strength armour", "Construct of Strength armour",
			"Golem of Justice armour", "Construct of Justice armour", "Blessed Sentinel outfit", "Cursed Reaver outfit",
			"Replica Virtus outfit", "Replica Torva outfit", "Replica Pernix outfit", "Clown costume",
			"Ringmaster costume", "Acrobat costume", "Audience outfit", "Spectator's outfit", "Circus goer's outfit",
			"Shadow Ariane outfit", "Shadow Ozan outfit", "Shadow Linza outfit", "Shadow Owen outfit",
			"Replica metal plate armour", "Replica metal plate armour (t)", "Replica metal plate armour (g)",
			"Replica Armadyl armour", "Replica Bandos armour", "Hiker costume", "Aviansie Skyguard outfit",
			"Vyrewatch Skyshadow outfit", "Replica Void Knight armour", "Replica Void Knight equipment",
			"Vanquisher's gear", "Zarosian shadow outfit", "Zarosian praetor outfit", "Elven warrior outfit",
			"Elven ranger outfit", "Elven mage outfit", "Nex outfit", "Attuned Nex outfit", "King Black Dragon outfit",
			"Attuned King Black Dragon outfit", "Snowman costume", "Samurai outfit", "Outfit of Omens",
			"Replica Ahrim's outfit", "Replica Dharok's outfit", "Replica Guthan's outfit", "Replica Karil's outfit",
			"Replica Torag's outfit", "Replica Verac's outfit", "Warm winter outfit" };

	public static final int[] LOCKED_COSTUMES_IDS = {};

	public static final int KEEP_SAKE_KEY = 25430;

	private final static HashMap<Integer, String> cosmeticsNames = new HashMap<Integer, String>();

	private final static String PACKED_PATH = "data/items/packedCosmeticsNames.cn";
	private final static String UNPACKED_PATH = "data/items/unpackedCosmeticsNames.txt";

	public static final void init() {
		if (new File(PACKED_PATH).exists())
			loadPackeddCosmeticsNames();
		else
			loadUnpackedCosmeticsNames();
	}

	private static void loadPackeddCosmeticsNames() {
		try {
			RandomAccessFile in = new RandomAccessFile(PACKED_PATH, "r");
			FileChannel channel = in.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0, channel.size());
			while (buffer.hasRemaining())
				cosmeticsNames.put(buffer.getShort() & 0xffff, readAlexString(buffer));
			channel.close();
			in.close();
		} catch (Throwable e) {

		}
	}

	private static void loadUnpackedCosmeticsNames() {
		try {
			BufferedReader in = new BufferedReader(new FileReader(UNPACKED_PATH));
			DataOutputStream out = new DataOutputStream(new FileOutputStream(PACKED_PATH));
			while (true) {
				String line = in.readLine();
				if (line == null)
					break;
				if (line.startsWith("//"))
					continue;
				line = line.replace("?", "");
				String[] splitedLine = line.split(" - ", 2);
				if (splitedLine.length < 2) {
					in.close();
					throw new RuntimeException("Invalid list for Cosmetic Names line: " + line);
				}
				int npcId = Integer.valueOf(splitedLine[0]);
				if (splitedLine[1].length() > 255)
					continue;
				out.writeShort(npcId);
				writeAlexString(out, splitedLine[1]);
				cosmeticsNames.put(npcId, splitedLine[1]);
			}

			in.close();
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String readAlexString(ByteBuffer buffer) {
		int count = buffer.get() & 0xff;
		byte[] bytes = new byte[count];
		buffer.get(bytes, 0, count);
		return new String(bytes);
	}

	public static void writeAlexString(DataOutputStream out, String string) throws IOException {
		byte[] bytes = string.getBytes();
		out.writeByte(bytes.length);
		out.write(bytes);
	}

	public static void openCosmeticsHandler(final Player player) {
		player.stopAll();
		player.getTemporaryAttributtes().put("Cosmetics", Boolean.TRUE);
		player.getInterfaceManager().sendInventoryInterface(670);
		player.getInterfaceManager().sendInterface(667);
		player.getPackets().sendConfigByFile(8348, 1);
		player.getPackets().sendConfigByFile(4894, 0);
		player.getPackets().sendItems(93, player.getInventory().getItems());
		player.getPackets().sendInterSetItemsOptionsScript(670, 0, 93, 4, 7, "Equip", "Compare", "Stats", "Examine");
		player.getPackets().sendUnlockIComponentOptionSlots(670, 0, 0, 27, 0, 1, 2, 3);
		Item[] cosmetics = player.getEquipment().getItems().getItemsCopy();
		for (int i = 0; i < cosmetics.length; i++) {
			Item item = cosmetics[i];
			if (item == null)
				cosmetics[i] = new Item(0);
		}
		player.getPackets().sendItems(94, cosmetics);
		player.getPackets().sendUnlockIComponentOptionSlots(667, 9, 0, 14, true, 0, 1, 2, 3);
		ButtonHandler.refreshEquipBonuses(player);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				player.getPackets().sendConfigByFile(8348, 1);
				player.getPackets().sendRunScriptBlank(2319);
			}
		});
		player.setCloseInterfacesEvent(() -> {
			player.getDialogueManager().finishDialogue();
			player.getTemporaryAttributtes().remove("Cosmetics");
			for (int i = 0; i < 15; i++) {
				player.getEquipment().refresh(i);
			}
		});
	}

	public static boolean keepSakeItem(Player player, Item itemUsed, Item itemUsedWith) {
		if (itemUsed.getId() != KEEP_SAKE_KEY && itemUsedWith.getId() != KEEP_SAKE_KEY)
			return false;
		if (itemUsed.getId() == KEEP_SAKE_KEY && itemUsedWith.getId() == KEEP_SAKE_KEY)
			return false;
		Item keepSakeKey = itemUsed.getId() == KEEP_SAKE_KEY ? itemUsed : itemUsedWith;
		Item keepSakeItem = itemUsed.getId() == KEEP_SAKE_KEY ? itemUsedWith : itemUsed;
		if (keepSakeItem == null || keepSakeKey == null)
			return false;
		if (player.getEquipment().getKeepSakeItems().size() >= 50) {
			player.getPackets().sendGameMessage("You can only keep sake 50 items.");
			return false;
		}
		int equipSlot = keepSakeItem.getDefinitions().getEquipSlot();
		if (equipSlot == Equipment.SLOT_ARROWS || equipSlot == Equipment.SLOT_AURA
				|| equipSlot == Equipment.SLOT_RING) {
			player.getPackets().sendGameMessage(
					"You can only keep sake items that goes into head, cape, neck, body, legs, gloves, main hand, off-hand, or boots slots.");
			return false;
		}
		if (equipSlot == -1) {
			player.getPackets().sendGameMessage("You can't keep sake this item as its not wearable.");
			return false;
		}
		if (!ItemConstants.canWear(keepSakeItem, player, true)) {
			player.getPackets().sendGameMessage("You don't have enough requirments to keep sake this item.");
			return false;
		}
		if (keepSakeItem.getDefinitions().isBindItem() || keepSakeItem.getDefinitions().isLended()
				|| keepSakeItem.getDefinitions().isStackable())
			return false;
		String name = keepSakeItem.getName().toLowerCase();
		if (name.contains("broken")) {
			player.getPackets().sendGameMessage("You can't keep sake broken items.");
			return false;
		}
		for (Item item : player.getEquipment().getKeepSakeItems()) {
			if (item == null)
				continue;
			if (item.getId() == keepSakeItem.getId()) {
				player.getPackets().sendGameMessage("You already have that item in your keepsake box.");
				return false;
			}
		}
		player.stopAll();
		player.getDialogueManager().startDialogue(new Dialogue() {

			@Override
			public void start() {
				sendOptionsDialogue("DO YOU WANT TO KEEP SAKE THIS ITEM?",
						"Yes, keep sake this item.(You won't be able to retrieve key)", "No, I would like to keep it.");
			}

			@Override
			public void run(int interfaceId, int componentId) {
				if (componentId == OPTION_1) {
					player.getEquipment().getKeepSakeItems().add(keepSakeItem);
					player.getPackets().sendGameMessage("You have added " + keepSakeItem.getName()
							+ " to keepsakes. It will appear along with other in cosmetic list. Talk to Solomon");
					player.getInventory().deleteItem(KEEP_SAKE_KEY, 1);
					player.getInventory().deleteItem(keepSakeItem);
				}
				end();
			}

			@Override
			public void finish() {

			}

		});
		return true;
	}

	/*
	 * Change the names of certain items of the costumes
	 */

	public static String getNameOnDialogue(int itemId) {
		String cosmeticName = cosmeticsNames.get(itemId);
		if (cosmeticName != null)
			return cosmeticName;
		return ItemDefinitions.getItemDefinitions(itemId).getName();
	}

	/*
	 * Restricts player from using certain items if they doesn't have the
	 * requirment needed, if the player doesn't have the requirment needed it
	 * wont show the item in the dialogue
	 */

	public static boolean isRestrictedItem(Player player, int itemId) {
		for (int LOCKED_COSTUMES_ID : LOCKED_COSTUMES_IDS) {
			if (itemId == LOCKED_COSTUMES_ID && player.isLockedCostume(itemId))
				return true;
		}
		switch (itemId) {
		case 995:
			player.getPackets().sendGameMessage("Error message here.");
			return true;
		}
		return false;
	}

	public static void UnlockCostumeId(Player player, int itemId) {
		if (player.getUnlockedCostumesIds().contains(itemId))
			return;
		player.getUnlockedCostumesIds().add(itemId);
		player.getPackets().sendGameMessage("<col=00ff00>You have unlocked " + getNameOnDialogue(itemId) + "!");
	}

	public static void UnlockOutfit(Player player, int index) {
		for (int itemId : FULL_OUTFITS[index]) {
			UnlockCostumeId(player, itemId);
		}
	}

	public static boolean isRestrictedOutfit(Player player, int index) {
		for (int itemId : FULL_OUTFITS[index]) {
			if (isRestrictedItem(player, itemId))
				return true;
		}
		return false;
	}

}
