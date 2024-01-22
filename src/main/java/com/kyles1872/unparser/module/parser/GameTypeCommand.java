package com.kyles1872.unparser.module.parser;

import com.kyles1872.unparser.annotation.Completer;
import com.kyles1872.unparser.module.command.CommandArgs;
import com.kyles1872.unparser.type.GameType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Kyle
 */
public class GameTypeCommand {

  @Completer(name = "gametype")
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
}
