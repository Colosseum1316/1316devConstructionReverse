package com.kyles1872.unparser.module.unparser;

import com.kyles1872.unparser.generator.VoidWorldGenerator;
import com.kyles1872.unparser.map.Map;
import com.kyles1872.unparser.module.Module;
import com.kyles1872.unparser.module.command.CommandManager;
import com.kyles1872.unparser.type.GameType;
import com.kyles1872.unparser.util.FileUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.plugin.Plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * @author Kyle
 */
public class UnparserManager extends Module {

  private final String mapsFolder = "map";
  private final HashMap<String, Integer> colorMap = new HashMap<>();

  public UnparserManager(Plugin plugin, CommandManager commandManager) {
    super(plugin);
    commandManager.registerCommands(new UnparseCommand(this));

    colorMap.put("White", 0);
    colorMap.put("Orange", 1);
    colorMap.put("Magenta", 2);
    colorMap.put("Sky", 3);
    colorMap.put("Yellow", 4);
    colorMap.put("Lime", 5);
    colorMap.put("Pink", 6);
    colorMap.put("Gray", 7);
    colorMap.put("LGray", 8);
    colorMap.put("Cyan", 9);
    colorMap.put("Purple", 10);
    colorMap.put("Blue", 11);
    colorMap.put("Brown", 12);
    colorMap.put("Green", 13);
    colorMap.put("Red", 14);
    colorMap.put("Black", 15);
  }

  public String unparseMap(File zipFile, GameType gameType, String admin) {

    String worldName = zipFile.getName().replaceAll(".zip", "").replaceAll(" ", "_");

    if (doesWorldExist(worldName, gameType))
      return ChatColor.translateAlternateColorCodes(
          '&', "&9Unparse> &cError that world already exists");

    if (!new FileUtil()
        .unzip(
            zipFile.getAbsolutePath(),
            new File(mapsFolder + File.separator + gameType.getName() + File.separator + worldName)
                .getPath()))
      return ChatColor.translateAlternateColorCodes(
          '&', "&9Unparse> &cAn error occurred unzipping " + zipFile.getAbsolutePath());

    WorldCreator worldCreator = new WorldCreator(getFullWorldName(worldName, gameType));
    worldCreator.generator(new VoidWorldGenerator());

    World world = Bukkit.createWorld(worldCreator);

    if (world == null)
      return ChatColor.translateAlternateColorCodes(
          '&', "&9Unparse> &cAn error occurred creating the world");

    Map map = getMapData(world);

    if (map == null) {
      // Unload and Delete World
      File worldFolder = world.getWorldFolder();
      Bukkit.unloadWorld(world, false);
      new FileUtil().deleteDirectory(worldFolder);
      return ChatColor.translateAlternateColorCodes(
          '&', "&9Unparse> &cAn error occurred generating world data");
    }

    // Delete world config as its no longer needed
    new File(world.getWorldFolder().getPath() + File.separator + "WorldConfig.dat").delete();

    if (!generateMapData(map, world, gameType, admin))
      return ChatColor.translateAlternateColorCodes(
          '&', "&9Unparse> &cAn error occurred generating map data");

    if (!generateParsedMap(map))
      return ChatColor.translateAlternateColorCodes(
          '&', "&9Unparse> &cAn error occurred trying to unparse the world");

    return ChatColor.translateAlternateColorCodes(
        '&', "&9Unparse> &7Successfully unparsed &e" + zipFile.getName());
  }

  private boolean doesWorldExist(String name, GameType gameType) {
    File file = new File(getFullWorldName(name, gameType));
    return file.exists() && file.isDirectory();
  }

  private String getFullWorldName(String name, GameType gameType) {
    return mapsFolder + File.separator + gameType.getName() + File.separator + name;
  }

  private Map getMapData(World world) {

    String name = "null";
    String author = "null";

    HashMap<String, ArrayList<Location>> dataLocations = new HashMap<>();
    HashMap<String, ArrayList<Location>> teamLocsLocations = new HashMap<>();
    HashMap<String, ArrayList<Location>> customLocsLocations = new HashMap<>();

    try {
      List<String> lines =
          Files.readAllLines(
              new File(world.getWorldFolder().getPath() + File.separator + "WorldConfig.dat")
                  .toPath());
      List<Location> current = null;
      int minX = -256, minY = 0, minZ = -256, maxX = 256, maxY = 256, maxZ = 256;

      for (String line : lines) {
        String[] tokens = line.split(":");

        if (tokens.length < 2 || tokens[0].isEmpty()) continue;

        String key = tokens[0], value = tokens[1];

        // Name & Author
        if (key.equalsIgnoreCase("MAP_NAME")) name = value;
        else if (key.equalsIgnoreCase("MAP_AUTHOR")) author = value;

        // Spawn Locations
        else if (key.equalsIgnoreCase("TEAM_NAME")) {
          current = teamLocsLocations.computeIfAbsent(value, k -> new ArrayList<>());
        } else if (key.equalsIgnoreCase("TEAM_SPAWNS")) {
          for (int i = 1; i < tokens.length; i++) {
            Location location = fromString(world, tokens[i]);

            if (location == null) continue;

            current.add(location);
          }
        }

        // Data Locations
        else if (key.equalsIgnoreCase("DATA_NAME"))
          current = dataLocations.computeIfAbsent(value, k -> new ArrayList<>());
        else if (key.equalsIgnoreCase("DATA_LOCS")) {
          for (int i = 1; i < tokens.length; i++) {
            Location location = fromString(world, tokens[i]);

            if (location == null) continue;

            current.add(location);
          }
        }

        // Custom Locations
        else if (key.equalsIgnoreCase("CUSTOM_NAME"))
          current = customLocsLocations.computeIfAbsent(value, k -> new ArrayList<>());
        else if (key.equalsIgnoreCase("CUSTOM_LOCS")) {
          for (int i = 1; i < tokens.length; i++) {
            Location location = fromString(world, tokens[i]);

            if (location == null) continue;

            current.add(location);
          }
        }

        // Map Bounds
        else if (key.equalsIgnoreCase("MIN_X")) minX = Integer.parseInt(value);
        else if (key.equalsIgnoreCase("MAX_X")) maxX = Integer.parseInt(value);
        else if (key.equalsIgnoreCase("MIN_Z")) minZ = Integer.parseInt(value);
        else if (key.equalsIgnoreCase("MAX_Z")) maxZ = Integer.parseInt(value);
        else if (key.equalsIgnoreCase("MIN_Y")) minY = Integer.parseInt(value);
        else if (key.equalsIgnoreCase("MAX_Y")) maxY = Integer.parseInt(value);
      }

      Location[] corners = new Location[2];
      corners[0] = new Location(world, minX, minY, minZ);
      corners[1] = new Location(world, maxX, maxY, maxZ);

      return new Map(name, author, corners, dataLocations, teamLocsLocations, customLocsLocations);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private boolean generateMapData(Map map, World world, GameType gameType, String admin) {
    try {
      FileWriter fileWriter =
          new FileWriter(world.getWorldFolder().getPath() + File.separator + "Map.dat");
      BufferedWriter out = new BufferedWriter(fileWriter);

      out.write("MAP_NAME:" + map.name);
      out.write("\n");
      out.write("MAP_AUTHOR:" + map.author);
      out.write("\n");
      out.write("GAME_TYPE:" + gameType.name());
      out.write("\n");
      out.write("ADMIN_LIST:" + admin + ",");
      out.write("\n");
      out.write("currentlyLive:" + false);
      out.write("\n");
      out.write("warps:");
      out.write("\n");
      out.write("LOCKED:" + false);

      out.close();
      return true;
    } catch (Exception e) {
      e.printStackTrace();

      File worldFolder = world.getWorldFolder();
      Bukkit.unloadWorld(world, false);
      new FileUtil().deleteDirectory(worldFolder);

      return false;
    }
  }

  private boolean generateParsedMap(Map map) {
    try {
      // Min/Max Border
      // If they are the default we don't need to set blocks
      if (map.corners[0].getBlock().getX() != -256
          && map.corners[0].getBlock().getZ() != -256
          && map.corners[1].getBlock().getX() != 256
          && map.corners[1].getBlock().getZ() != 256) {

        map.corners[0].getBlock().setType(Material.WOOL);
        map.corners[0].getBlock().setData((byte) 0);
        map.corners[0].add(0, 1, 0).getBlock().setType(Material.GOLD_PLATE);

        // If it's out of the world meaning it's the same as Y
        if (map.corners[1].getBlock().getY() == 256) {
          map.corners[1].getBlock().getLocation().setY(map.corners[0].getBlock().getY());
        }

        map.corners[1].getBlock().setType(Material.WOOL);
        map.corners[1].getBlock().setData((byte) 0);
        map.corners[1].add(0, 1, 0).getBlock().setType(Material.GOLD_PLATE);
      }

      // Data/Iron
      for (String woolCol : map.dataLocations.keySet()) {
        // Loop over each location
        for (Location loc : map.getIronLocations(woolCol)) {
          // Set to wool
          loc.getBlock().setType(Material.WOOL);
          // Add color/data
          loc.getBlock().setData(DyeColor.valueOf(woolCol.toUpperCase()).getWoolData());
          // Set iron plate
          loc.add(0, 1, 0).getBlock().setType(Material.IRON_PLATE);
        }
      }

      // Spawns/Gold
      for (String woolCol : map.teamLocsLocations.keySet()) {
        // Loop over each location
        for (Location loc : map.getGoldLocations(woolCol)) {
          // Set to wool
          loc.getBlock().setType(Material.WOOL);
          // Add color/data

          Optional<Integer> color =
              colorMap.entrySet().stream()
                  .filter(entry -> entry.getKey().equalsIgnoreCase(woolCol))
                  .map(java.util.Map.Entry::getValue)
                  .findFirst();

          loc.getBlock()
              .setData(
                  color
                      .map(integer -> (byte) (int) integer)
                      .orElseGet(() -> DyeColor.valueOf(woolCol.toUpperCase()).getWoolData()));
          // Set gold plate
          loc.add(0, 1, 0).getBlock().setType(Material.GOLD_PLATE);
        }
      }

      // Custom/Sponge - Inc Data
      for (String name : map.customLocsLocations.keySet()) {
        // Loop over each location
        for (Location loc : map.getSpongeLocations(name)) {

          try {
            int id = Integer.parseInt(name);

            Material mat = Material.getMaterial(id);
            if (mat == null) throw new Exception();

            // if is id block
            loc.getBlock().setType(mat);
          } catch (Exception e) {
            // Set to sponge
            loc.getBlock().setType(Material.SPONGE);

            // Set sign
            Block block = loc.add(0, 1, 0).getBlock();
            block.setType(Material.SIGN_POST);

            Sign signState = (Sign) block.getState();

            String[] lines = new String[4];

            for (String data : name.split(" ")) {
              int size = data.length();

              for (int i = 0; i < 4; i++) {
                if (lines[i] == null) {
                  lines[i] = "";
                }
                if (lines[i].length() + size + (lines[i].isEmpty() ? 0 : 1) <= 15) {
                  if (!lines[i].isEmpty()) {
                    lines[i] += " ";
                  }
                  lines[i] += data;
                  break;
                }
              }
            }

            for (int i = 0; i < 4; i++) {
              signState.setLine(i, lines[i]);
            }
            signState.update();
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  private Location fromString(World world, String location) {
    String[] cords = location.split(",");

    try {
      return new Location(
          world,
          Integer.parseInt(cords[0]) + 0.5,
          Integer.parseInt(cords[1]),
          Integer.parseInt(cords[2]) + 0.5);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }
}
