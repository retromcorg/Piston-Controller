package com.johnymuffin.beta.pistoncontroller;

import org.bukkit.util.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PCConfig extends Configuration {

    private PistonController plugin;

    public PCConfig(PistonController plugin, File file) {
        super(file);
        this.plugin = plugin;
        this.reload();
    }

    public void reload() {
        this.load();
        this.write();
        this.save();
    }

    public void write() {
        generateConfigOption("config-version", 1);
        generateConfigOption("settings.piston-block-ids.info", "List of block IDs that can be pushed by pistons");
        generateConfigOption("settings.piston-effect-type.info", "Whitelist or blacklist of blocks that can be pushed by pistons");
        generateConfigOption("settings.piston-effect-type.possible-settings", "whitelist, blacklist");
        generateConfigOption("settings.piston-effect-type.value", "blacklist");
    }

    public boolean isWhitelist() {
        return this.getConfigString("settings.piston-effect-type.value").equalsIgnoreCase("whitelist");
    }


    public List<String> getPistonBlockIDs() {
        String key = "settings.piston-block-ids.value";
        if (this.getList(key) == null) {
            List<String> defaultBlocks = new ArrayList<>();
            //Ores
            defaultBlocks.add("14"); //Gold Ore
            defaultBlocks.add("15"); //Iron Ore
            defaultBlocks.add("16"); //Coal Ore
            defaultBlocks.add("21"); //Lapis Ore
            defaultBlocks.add("56"); //Diamond Ore
            defaultBlocks.add("73"); //Redstone Ore
            defaultBlocks.add("74"); //Glowing Redstone Ore
            //Ingot Blocks
            defaultBlocks.add("41"); //Gold Block
            defaultBlocks.add("42"); //Iron Block
            defaultBlocks.add("57"); //Diamond Block
            defaultBlocks.add("22"); //Lapis Block

            //Misc
            defaultBlocks.add("26"); //Bed
            defaultBlocks.add("26:0:>="); //Bed and meta
            defaultBlocks.add("355"); //Chest
            defaultBlocks.add("54"); //Chest
            defaultBlocks.add("35"); //Wools
            defaultBlocks.add("44"); //Slabs
            defaultBlocks.add("62"); //Burning Furnace
            defaultBlocks.add("61"); //Furnace

            this.setProperty(key, defaultBlocks);

        }

        List<String> list = this.getStringList(key, new ArrayList<>());

        return new ArrayList<>(list);
    }


    //Getters Start
    public Object getConfigOption(String key) {
        return this.getProperty(key);
    }

    public String getConfigString(String key) {
        return String.valueOf(getConfigOption(key));
    }

    public Integer getConfigInteger(String key) {
        return Integer.valueOf(getConfigString(key));
    }

    public Long getConfigLong(String key) {
        return Long.valueOf(getConfigString(key));
    }

    public Double getConfigDouble(String key) {
        return Double.valueOf(getConfigString(key));
    }

    public Boolean getConfigBoolean(String key) {
        return Boolean.valueOf(getConfigString(key));
    }


    //Getters End

    public void generateConfigOption(String key, Object defaultValue) {
        if (this.getProperty(key) == null) {
            this.setProperty(key, defaultValue);
        }
        final Object value = this.getProperty(key);
        this.removeProperty(key);
        this.setProperty(key, value);
    }


    public Long getConfigLongOption(String key) {
        if (this.getConfigOption(key) == null) {
            return null;
        }
        return Long.valueOf(String.valueOf(this.getProperty(key)));
    }


    private boolean convertToNewAddress(String newKey, String oldKey) {
        if (this.getString(newKey) != null) {
            return false;
        }
        if (this.getString(oldKey) == null) {
            return false;
        }
        System.out.println("Converting Config: " + oldKey + " to " + newKey);
        Object value = this.getProperty(oldKey);
        this.setProperty(newKey, value);
        this.removeProperty(oldKey);
        return true;

    }
}
