package com.kyles1872.unparser.module.parser;

import com.kyles1872.unparser.module.Module;
import com.kyles1872.unparser.module.command.CommandManager;
import org.bukkit.plugin.Plugin;

/**
 * @author Kyle
 */
public class ParserManager extends Module {
  public ParserManager(Plugin plugin, CommandManager commandManager) {
    super(plugin);
    Plugin mapParser = plugin.getServer().getPluginManager().getPlugin("MapParser");

    if (mapParser == null || !mapParser.isEnabled()) return;

    commandManager.registerCommands(new MapCommand());
    commandManager.registerCommands(new GameTypeCommand());
  }
}
