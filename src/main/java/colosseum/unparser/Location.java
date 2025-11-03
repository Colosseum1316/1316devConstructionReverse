package colosseum.unparser;

import lombok.AllArgsConstructor;
import lombok.Getter;

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

  private static int locToBlock(double loc) {
    final int floor = (int) loc;
    return floor == loc ? floor : floor - (int) (Double.doubleToRawLongBits(loc) >>> 63);
  }
}
