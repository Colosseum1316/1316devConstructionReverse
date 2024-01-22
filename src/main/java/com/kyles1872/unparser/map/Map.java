package com.kyles1872.unparser.map;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Kyle
 */
public class Map {

  public String name;
  public String author;

  public Location[] corners;

  public HashMap<String, ArrayList<Location>> dataLocations;
  public HashMap<String, ArrayList<Location>> teamLocsLocations;
  public HashMap<String, ArrayList<Location>> customLocsLocations;

  public Map(
      String name,
      String author,
      Location[] corners,
      HashMap<String, ArrayList<Location>> dataLocations,
      HashMap<String, ArrayList<Location>> teamLocsLocations,
      HashMap<String, ArrayList<Location>> customLocsLocations) {
    this.name = name;
    this.author = author;
    this.corners = corners;
    this.dataLocations = dataLocations;
    this.teamLocsLocations = teamLocsLocations;
    this.customLocsLocations = customLocsLocations;
  }

  public List<Location> getIronLocations(String key) {
    return dataLocations.computeIfAbsent(key, k -> new ArrayList<>());
  }

  public List<Location> getGoldLocations(String key) {
    return teamLocsLocations.computeIfAbsent(key, k -> new ArrayList<>());
  }

  public List<Location> getSpongeLocations(String key) {
    return customLocsLocations.computeIfAbsent(key, k -> new ArrayList<>());
  }
}
