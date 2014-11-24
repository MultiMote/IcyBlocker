package com.multi.icyblocker;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * Created by MultiMote on 24.11.2014.
 */
public class Core extends JavaPlugin {

    private static Logger logger;

    public Core(){
        logger = getLogger();
    }

    public void onEnable() {
        this.getCommand("icy").setExecutor(new CommandHandler());
        Bukkit.getPluginManager().registerEvents(new PlayerActionsHandler(), this);
        logger.info("Reading item list...");
        BlockedItems.instance.setCoreInstance(this);
        BlockedItems.instance.readFile();
        logger.info("Loaded.");
        if(Math.random() < 0.3F)logger.info("You are awesome!");
    }

    public static Logger getPluginLogger() {
        return logger;
    }
}
