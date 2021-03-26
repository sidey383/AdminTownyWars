package ru.sidey383.townyWars.command;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import ru.sidey383.townyWars.TownyWars;
import ru.sidey383.townyWars.configuration.ConfigProperty;
import ru.sidey383.townyWars.objects.War;
import ru.sidey383.townyWars.objects.WarFactory;

public class WarBattleCommand implements CommandExecutor, TabExecutor {

	@ConfigProperty(Path = "command.wrong.id", Name = "lang")
	public static String wrongIDMessage;
	@ConfigProperty(Path = "command.dontHaveBattle", Name = "lang")
	public static String dontHaveBattle;
	@ConfigProperty(Path = "command.cantFindeWar", Name = "lang")
	public static String cantFindeWar;
	@ConfigProperty(Path = "command.dontEnoughPermissions", Name = "lang")
	public static String dontEnoughPermissions;
	
	private static final List<String> tabComplete = Arrays.asList("end", "start");
	
	public WarBattleCommand() {
		TownyWars.getLanguage().addClassToLoad(WarBattleCommand.class);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args) {
		if(args.length < 3) return false;
		if(!sender.hasPermission("townywars.command.war.battle"))
		{
			sender.sendMessage(dontEnoughPermissions);
			return true;
		}
		if(args[1].toLowerCase().equalsIgnoreCase("end")) 
		{
			try {
				int i = Integer.parseInt(args[2]);
				War w = WarFactory.getInstance().getWars().get(i);
				if(w == null)
				{
					sender.sendMessage(wrongIDMessage);
					return true;
				}
				if(!w.haveBattle())
				{
					sender.sendMessage(dontHaveBattle);
					return true;
				}
				w.stopBattle();
				return true;
			}catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		if(args.length < 4) return false;
		if(args[1].toLowerCase().equalsIgnoreCase("start")) 
		{
			String town1 = args[2];
			String town2 = args[3];
			for(War w: WarFactory.getInstance().getWars().values()) 
			{
				if((w.getTown1().getName().toLowerCase().equalsIgnoreCase(town1) && w.getTown2().getName().toLowerCase().equalsIgnoreCase(town2))) 
				{
					w.startBattle(true);
					return true;
				}
				if((w.getTown1().getName().toLowerCase().equalsIgnoreCase(town2) && w.getTown2().getName().toLowerCase().equalsIgnoreCase(town1)))
				{
					w.startBattle(false);
					return true;
				}
			}
			sender.sendMessage(cantFindeWar);
			return true;
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String name, String[] args) {
		if(args.length == 2)
			return tabComplete;
		if(args.length == 3) 
		{
			if(args[1].equals("end"))
				return Arrays.asList("id");
			if(args[1].equals("start"))
				return WarCommand.selectStrings(WarCommand.getTownNames(), args[2]);
		}
		if(args.length == 4)
			if(args[1].equals("start"))
				return WarCommand.selectStrings(WarCommand.getTownNames(), args[3]);
		return null;
	}

}
