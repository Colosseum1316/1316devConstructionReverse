package com.kyles1872.unparser;

import com.kyles1872.unparser.generator.VoidWorldGenerator;
import com.kyles1872.unparser.module.command.CommandManager;
import com.kyles1872.unparser.module.unparser.UnparserManager;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public class Unparser extends JavaPlugin {

  private CommandManager commandManager;

  @Override
  public void onEnable() {
    commandManager = new CommandManager(this);
    new UnparserManager(this, commandManager);
    //    new ParserManager(this, commandManager);
    commandManager.registerHelp();
  }

  @Override
  public void onDisable() {}

  @Override
  public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
    return new VoidWorldGenerator();
  }
}
