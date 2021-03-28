package ru.sidey383.townyWars.configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

@SuppressWarnings("rawtypes")
public class ConfigurationLoader {

	private Logger logger;
	private YamlConfiguration config;
	private File file;
	private ArrayList<Class> classToLoad = new ArrayList<Class>();
	private String name = "";
	
	
	public ConfigurationLoader(File file, String defaultFile, Logger logger) throws InvalidConfigurationException, IOException 
	{
		this.logger = logger;
		load(file, defaultFile);
		logger = Bukkit.getLogger();
	}
	
	public ConfigurationLoader(File file, String defaultFile, String name, Logger logger) throws InvalidConfigurationException, IOException 
	{
		this.logger = logger;
		this.name = name;
		load(file, defaultFile);
	}
	
	public ConfigurationLoader(File file, String defaultFile) throws InvalidConfigurationException, IOException 
	{
		logger = Bukkit.getLogger();
		load(file, defaultFile);
	}
	
	public ConfigurationLoader(File file, String defaultFile, String name) throws InvalidConfigurationException, IOException 
	{
		this.name = name;
		logger = Bukkit.getLogger();
		load(file, defaultFile);
	}
	
	private void load(File file, String defaultFile) throws IOException, InvalidConfigurationException 
	{
		File dir = file.getParentFile();
		if(!dir.exists())
			dir.mkdirs();
		if(!file.exists())
		{
			file.createNewFile();
			if(defaultFile != null)
			{
				OutputStream out = new FileOutputStream(file);
				URL u = getClass().getClassLoader().getResource(defaultFile);
				if(u!= null)
				{
					InputStream in = u.openStream();
					if(in != null)
					{
						byte[] buffer = new byte[1024];
				        int length;
				        while ((length = in.read(buffer)) > 0) {
				        	if(out != null)
				        		out.write(buffer, 0, length);
				        }
				        in.close();
					}else
						logger.log(Level.INFO, "cant create InputStrea for resource "+u.getPath());
					if(out != null)
						out.close();
				}else
					logger.log(Level.INFO, "cant create resource URL for file "+defaultFile);
			}
			logger.log(Level.INFO, "create configuration file "+ file.getName());
		}
		setFile(file);
	}
	
	public void setFile(File file) throws InvalidConfigurationException 
	{
		config = YamlConfiguration.loadConfiguration(file);
		if(config == null)
			throw new InvalidConfigurationException(file.getAbsolutePath());
		this.file = file;
		loadClass();
	}
	
	public void addClassToLoad(Class clazz) 
	{
		classToLoad.add(clazz);
		loadClass(clazz);
	}
	
	public void loadClass(Class clazz) 
	{
		if(clazz == null)
			logger.log(Level.INFO, "null clazz");
		if(clazz == null) return;
		for(Field f: clazz.getFields()) 
		{
			String path = null;
			try {
				f.setAccessible(true);
				boolean canNull = f.getAnnotationsByType(Nullable.class).length > 0;
				ConfigProperty cf = f.getAnnotation(ConfigProperty.class);
				if(cf == null)
				{
					continue;
				}
				path = cf.Path();
				String name = cf.Name();
				if(!name.equals(""))
					if(!name.equals(this.name)) 
					{
						continue;
					}
				Object obj = config.get(path);
				{
					List<String> list = null;
					if(f.getType().equals(String.class) && (list = config.getStringList(path)) != null)
					{
						if(list.size() != 0)
						{
							String result = "";
							for(String str: list)
								result+=str+"\n";
							obj = result;
						}
					}
				}
				if(obj == null)
					if(canNull)
						f.set(null, null);
					else
					{
						logger.warning("cant load field "+path+" from config "+config.getName()+" cant be null in class "+clazz.getName());
						continue;
					}
				if(obj instanceof String)
					obj = ((String) obj).replace('&', '§');
				f.set(null, obj);
			}catch (Exception e) {
				logger.warning("cant load field "+path+" from config "+config.getName()+" to class "+clazz.getName());
			}
		}
	}
	
	public YamlConfiguration getFileConfiguration() 
	{
		return config;
	}
	
	public void loadClass() 
	{
		for(Class clazz: classToLoad)
			loadClass(clazz);
	}
	
	public ArrayList<Class> getClassesToLoad() 
	{
		return classToLoad;
	}
	
	public void reload() 
	{
		try {
		config.load(file);
		}catch (Exception e) {
			logger.log(Level.SEVERE, " cant reload configuration ", e);
		}
	}

	
}
