package ru.sidey383.townyWars;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;
import ru.sidey383.townyWars.command.WarBattleCommand;
import ru.sidey383.townyWars.command.WarCommand;
import ru.sidey383.townyWars.command.WarDeclareCommand;
import ru.sidey383.townyWars.command.WarEndCommand;
import ru.sidey383.townyWars.command.WarHelpCommand;
import ru.sidey383.townyWars.command.WarListCommand;
import ru.sidey383.townyWars.configuration.ConfigurationLoader;
import ru.sidey383.townyWars.db.WarsDataBase;
import ru.sidey383.townyWars.objects.Battle;
import ru.sidey383.townyWars.objects.TownBlockCapture;
import ru.sidey383.townyWars.objects.War;
import ru.sidey383.townyWars.objects.WarFactory;

public class TownyWars extends JavaPlugin {
	
	static TownyWars plugin;
	public static boolean hasVault = true;
    public static Economy econ = null;
    private static WarsDataBase warsDataBase;
    private static ConfigurationLoader conflodaer;
    private static ConfigurationLoader language;
	
	public void onEnable() {
		  plugin = this;
		  if (Bukkit.getPluginManager().getPlugin("Towny") == null) {
			  plugin.getLogger().log(Level.SEVERE, "Towny is not installed");
			  Bukkit.getPluginManager().disablePlugin(plugin);
			  return;
		  } 
		  if(!setupEconomy() ) {
			  plugin.getLogger().log(Level.WARNING, "Vault is not installed");
			  hasVault = false;
		  }
		  try {
			  warsDataBase = new WarsDataBase(new File(getDataFolder(),"data.db"));
		  }catch (Exception e) {
			  plugin.getLogger().log(Level.SEVERE, "Cant load database", e);
			  Bukkit.getPluginManager().disablePlugin(plugin);
			  return;
		  }
		if(!loadConfiguration())
			return;
		  try {
			  WarFactory.init();
		  }catch (Exception e) {
			  plugin.getLogger().log(Level.SEVERE, "Cant load WarFacotrt", e);
			  Bukkit.getPluginManager().disablePlugin(plugin);
			  return;
		  }
	  }
	
	private boolean loadConfiguration() 
	{
		  try {
			  conflodaer = new ConfigurationLoader(new File(plugin.getDataFolder(),"config.yml"), "config.yml", "default", getLogger());
		  }catch (Exception e) {
			  plugin.getLogger().log(Level.SEVERE, "Cant load Config Loader", e);
			  Bukkit.getPluginManager().disablePlugin(plugin);
			  return false;
		  }
		  String lang = conflodaer.getFileConfiguration().getString("lang");
		  if(lang == null) 
		  {
			  lang = "ru";
			  plugin.getLogger().log(Level.WARNING, "Cant find lang in config. Use default language.", getLogger());
		  }
		  try {
			  language = new ConfigurationLoader(new File(plugin.getDataFolder(), lang+".yml"), lang+".yml", "lang", getLogger());
		  }catch (Exception e) {
			  plugin.getLogger().log(Level.SEVERE, "Cant load Config Loader", e);
			  Bukkit.getPluginManager().disablePlugin(plugin);
			  return false;
		  }
		  WarCommand command = new WarCommand();
		  Bukkit.getPluginCommand("war").setExecutor(command);
		  Bukkit.getPluginCommand("war").setTabCompleter(command);
		  getLogger().log(Level.INFO, "plugin loaded");
		  language.addClassToLoad(Battle.class);
		  language.addClassToLoad(TownBlockCapture.class);
		  language.addClassToLoad(War.class);
		  language.addClassToLoad(WarBattleCommand.class);
		  language.addClassToLoad(WarDeclareCommand.class);
		  language.addClassToLoad(WarEndCommand.class);
		  language.addClassToLoad(WarListCommand.class);
		  language.addClassToLoad(WarHelpCommand.class);
		  conflodaer.addClassToLoad(Battle.class);
		  return true;
	}
	
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    
    public static TownyWars getInstance() 
    {
    	return plugin;
    }
    
    public static WarsDataBase getDataBase() 
    {
    	return warsDataBase;
    }
   
    public static Logger getLoggerStatic() 
    {
    	return plugin.getLogger();
    }
    
    public static ConfigurationLoader getConfigurationLoader() 
    {
    	return conflodaer;
    }
    
    public static ConfigurationLoader getLanguage() 
    {
    	return language;
    }

    public void reloadLanguage() 
    {
    		
		  String lang = conflodaer.getFileConfiguration().getString("lang");
		  if(lang == null) 
		  {
			  lang = "ru";
			  plugin.getLogger().log(Level.WARNING, "Cant find lang in config. Use default language.", getLogger());
		  }
		  try {
			  if(language != null)
			  {
				  @SuppressWarnings("rawtypes")
				  List<Class> classes = language.getClassesToLoad();
				  language = new ConfigurationLoader(new File(plugin.getDataFolder(), lang+".yml"), lang+".yml", "lang", getLogger());
				  for(@SuppressWarnings("rawtypes") Class cl: classes)
					  language.addClassToLoad(cl);
			  }
			  else
				  language = new ConfigurationLoader(new File(plugin.getDataFolder(), lang+".yml"), lang+".yml", "lang", getLogger());
		  }catch (Exception e) {
			  plugin.getLogger().log(Level.SEVERE, "Cant load Config Loader", e);
			  Bukkit.getPluginManager().disablePlugin(plugin);
		  }
    }
}
