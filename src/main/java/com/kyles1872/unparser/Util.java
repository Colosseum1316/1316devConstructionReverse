package com.kyles1872.unparser;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Colosseum1316
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("deprecation")
final class Util {
  static final short MATERIAL_WOOL = (short) Material.WOOL.getId();
  static final short MATERIAL_SPONGE = (short) Material.SPONGE.getId();
  static final short MATERIAL_GOLD_PLATE = (short) Material.GOLD_PLATE.getId();
  static final short MATERIAL_IRON_PLATE = (short) Material.IRON_PLATE.getId();
  static final short MATERIAL_SIGN_POST = (short) Material.SIGN_POST.getId();
  static final Map<String, Integer> COLOR_MAP = new HashMap<>() {{
    put("White", 0);
    put("Orange", 1);
    put("Magenta", 2);
    put("Sky", 3);
    put("Yellow", 4);
    put("Lime", 5);
    put("Pink", 6);
    put("Gray", 7);
    put("LGray", 8);
    put("Cyan", 9);
    put("Purple", 10);
    put("Blue", 11);
    put("Brown", 12);
    put("Green", 13);
    put("Red", 14);
    put("Black", 15);
  }};
}
