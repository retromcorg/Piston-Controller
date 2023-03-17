package com.johnymuffin.beta.pistoncontroller;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PistonController extends JavaPlugin {
    //Basic Plugin Info
    private static JavaPlugin plugin;
    private Logger log;
    private String pluginName;
    private PluginDescriptionFile pdf;

    private ArrayList<PCMaterial> materials;

    private PCConfig config;

    @Override
    public void onEnable() {
        plugin = this;
        log = this.getServer().getLogger();
        pdf = this.getDescription();
        pluginName = pdf.getName();
        log.info("[" + pluginName + "] Is Loading, Version: " + pdf.getVersion());

        PCListener listener = new PCListener(this);

        Bukkit.getPluginManager().registerEvents(listener, this);

        config = new PCConfig(this, new File(this.getDataFolder(), "config.yml"));

        materials = new ArrayList<PCMaterial>();

        for (String item : config.getPistonBlockIDs()) {
            if (!item.contains(":")) {
                if (!isInteger(item)) {
                    log.warning("[" + pdf.getName() + "] Invalid Input, Please Use Item ID: " + item);
                } else if (Material.getMaterial(Integer.valueOf(item)) == null) {
                    log.warning("[" + pdf.getName() + "] Invalid Item ID: " + item);
                } else {
                    log.info("[" + pdf.getName() + "] Looking for item: " + Material.getMaterial(Integer.valueOf(item)).name());
                    this.materials.add(new PCMaterial(Integer.valueOf(item), 0, false, false));
                }
            } else {
                String[] itemDetails = item.split(":");
                if (!isInteger(itemDetails[0]) || !isInteger(itemDetails[1])) {
                    log.warning("[" + pdf.getName() + "] Invalid Input, Please Use Item ID: " + item);
                } else if (Material.getMaterial(Integer.valueOf(itemDetails[0])) == null) {
                    log.warning("[" + pdf.getName() + "] Invalid Item ID: " + item);
                } else {
                    log.info("[" + pdf.getName() + "] Looking for item: " + Material.getMaterial(Integer.valueOf(itemDetails[0])).name());
                    boolean removeGreaterThen = false;
                    if (itemDetails.length > 2 && itemDetails[2].equalsIgnoreCase(">=")) {
                        removeGreaterThen = true;
                    }
                    this.materials.add(new PCMaterial(Integer.valueOf(itemDetails[0]), Integer.valueOf(itemDetails[1]), true, removeGreaterThen));
                }
            }
        }


    }

    public void logger(Level level, String message) {
        Bukkit.getLogger().log(level, "[" + pluginName + "] " + message);
    }


    @Override
    public void onDisable() {
        log.info("[" + pluginName + "] Is Unloading, Version: " + pdf.getVersion());
    }


    public PCConfig getPCConfig() {
        return config;
    }

    public ArrayList<PCMaterial> getMaterials() {
        return materials;
    }

    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }
}
