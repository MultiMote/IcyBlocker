package com.multi.icyblocker;

import com.avaje.ebean.LogLevel;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by MultiMote on 24.11.2014.
 */
public class BlockedItems {
    private static final String FILENAME = "blocked.yml";

    public static BlockedItems instance = new BlockedItems();
    private List<ItemData> items;
    private File listFile;
    private Core coreInstance;
    private YamlConfiguration yamlList;

    private BlockedItems() {
        this.items = new ArrayList<ItemData>();
    }


    public boolean matches(ItemStack item, boolean using) { //спички
        return this.matches(item.getType().name(), Short.toString(item.getDurability()), using);
    }

    public boolean matches(Item item, boolean using) {
        return this.matches(item.getType().name(), "0", using);
    }

    public boolean matches(Block block, boolean using) {
        return this.matches(block.getType().name(), "0", using);
    }

    public boolean matches(String name, String meta, boolean using) {
        for (ItemData item : this.items) {
            if(name.equals(item.getName()) && (item.ignoresMeta() || meta.equals(item.getMeta()))){
                return !(!using && item.containsParam("CR"));
            }
        }
        return false;
    }

    public boolean matches(ItemData data) {
       return this.matches(data.getName(), data.getMeta(), true);
    }

    public boolean matches(ItemData first, ItemData second) {
        return first.getName().equals(second.getName()) && (first.ignoresMeta() || first.getMeta().equals(second.getMeta()));
    }

    public void add(ItemData item) {
        this.items.add(item);
        this.saveFile();
    }

    public boolean matchAndRemove(ItemData item) {
        int i = 0;
        for (ItemData id : this.items) {
            if (this.matches(id, item)) {
                this.items.remove(i);
                this.saveFile();
                return true;
            }
            i++;
        }
        return false;
    }
    public String generateList() {
        String s = "";
        for (int i = 0; i < this.items.size(); i++) {
            ItemData data = this.items.get(i);
            s += data.getName();
            s += data.ignoresMeta() ? "(любая мета)" : ":" + data.getMeta();
            s += data.containsParam("CR") ? "(крафт)" : "";
            if (i < this.items.size() - 1) s += ", ";
        }
        return s;
    }


    public void setCoreInstance(Core core) {
        this.coreInstance = core;
    }

    public void readFile() {
        this.listFile = new File(this.coreInstance.getDataFolder(), FILENAME);
        if (!this.listFile.exists()) {
            Core.getPluginLogger().info("Item list is not exists, creating...");
            this.coreInstance.saveResource(FILENAME, false);
        }
        this.yamlList = YamlConfiguration.loadConfiguration(this.listFile);

        if (this.yamlList.get("Items") instanceof List) {
            List strings = (List) this.yamlList.get("Items");
            for (Object obj : strings) {
                ItemData data = ItemData.parse(obj.toString());
                if (data != null) {
                    this.items.add(data);
                    //Core.getPluginLogger().info("Parsed item: " + data);
                } else Core.getPluginLogger().log(Level.WARNING ,"Can't parse " + obj + ", check your " + FILENAME);
            }
        }
    }

    public void saveFile() {
        ArrayList<String> list = new ArrayList<String>();
        for (ItemData data : this.items) list.add(data.toString());

        try {
            this.yamlList.set("Items", list);
            this.yamlList.save(this.listFile);
        } catch (IOException ex) {
            Core.getPluginLogger().info("Can't write " + FILENAME + ", because " + ex.getMessage());
        }
    }

}
