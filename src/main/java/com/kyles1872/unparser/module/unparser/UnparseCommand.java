package com.kyles1872.unparser.module.unparser;

import com.kyles1872.unparser.annotation.Command;
import com.kyles1872.unparser.annotation.Completer;
import com.kyles1872.unparser.module.command.CommandArgs;
import com.kyles1872.unparser.type.GameType;
import org.bukkit.ChatColor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Kyle
 */
public class UnparseCommand {

  private final UnparserManager unparserManager;

  public UnparseCommand(UnparserManager unparserManager) {
    this.unparserManager = unparserManager;
  }

  @Command(
      name = "unparse",
      aliases = {"unmap"},
      description = "Converts parsed zip archives back into worlds",
      permission = "command.unparse",
      inGameOnly = true)
  public void command(CommandArgs args) {

    if (args.length() <= 1) {
      args.getSender()
          .sendMessage(
              ChatColor.translateAlternateColorCodes(
                  '&',
                  "&9Unparse> &7Usage: &e/" + args.getLabel() + " <gameType> <zip/directory>"));
      return;
    }

    GameType gameType =
        Arrays.stream(GameType.values())
            .filter(e -> e.name().equalsIgnoreCase(args.getArgs(0)))
            .findAny()
            .orElse(null);

    if (gameType == null) {
      args.getSender()
          .sendMessage(
              ChatColor.translateAlternateColorCodes(
                  '&', "&9Unparse> &7Could not find a GameType matching &e" + args.getArgs(0)));
      return;
    }

    findZipFiles(
            new File(
                String.join(" ", Arrays.stream(args.getArgs()).skip(1).toArray(String[]::new))))
        .forEach(
            path ->
                args.getSender()
                    .sendMessage(
                        unparserManager.unparseMap(
                            new File(path), gameType, args.getPlayer().getName())));
  }

  @Completer(
      name = "unparse",
      aliases = {"unmap"})
  public List<String> completer(CommandArgs args) {
    List<String> list = new ArrayList<>();

    if (args.length() == 1)
      Arrays.stream(GameType.values())
          .map(Enum::name)
          .filter(name -> name.toLowerCase().startsWith(args.getArgs(0).toLowerCase()))
          .sorted(String::compareToIgnoreCase)
          .forEach(list::add);
    else if (args.length() < 1)
      Arrays.stream(GameType.values())
          .map(Enum::name)
          .sorted(String::compareToIgnoreCase)
          .forEach(list::add);

    return list;
  }

  public ArrayList<String> findZipFiles(File file) {
    ArrayList<String> zipFiles = new ArrayList<>();
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      if (files != null) for (File f : files) zipFiles.addAll(findZipFiles(f));
    } else if (file.getName().endsWith(".zip")) zipFiles.add(file.getAbsolutePath());
    return zipFiles;
  }
}
