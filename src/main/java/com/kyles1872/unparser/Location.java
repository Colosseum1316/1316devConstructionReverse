package com.kyles1872.unparser;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static org.bukkit.Location.locToBlock;

/**
 * @author Colosseum1316
 */
@AllArgsConstructor
@Getter
public final class Location {
  private double x;
  private double y;
  private double z;

  public int getBlockX() {
    return locToBlock(this.x);
  }

  public int getBlockY() {
    return locToBlock(this.y);
  }

  public int getBlockZ() {
    return locToBlock(this.z);
  }
}
