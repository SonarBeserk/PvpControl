/**
 * ********************************************************************************************************************
 * PVPControl - Provides a pvp tagging system
 * ====================================================================================================================
 * Copyright (C) 2015 by SonarBeserk, SerkProjects
 * https://gitlab.com/serkprojects/pvpcontrol
 * *********************************************************************************************************************
 * *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * *
 * *********************************************************************************************************************
 * Please refer to LICENSE for the full license. If it is not there, see <http://www.gnu.org/licenses/>.
 * *********************************************************************************************************************
 */

package com.serkprojects.pvpcontrol;

import com.serkprojects.pvpcontrol.commands.PvpCmd;
import com.serkprojects.pvpcontrol.listeners.ConsequencesListener;
import com.serkprojects.pvpcontrol.listeners.PlayerListener;
import com.serkprojects.pvpcontrol.tasks.UnTagTask;
import com.serkprojects.pvpcontrol.trackers.PlayerTracker;
import com.serkprojects.serkcore.plugin.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.UUID;

public class PVPControl extends JavaPlugin {
    private PlayerTracker tracker = null;

    private BukkitTask untag = null;

    @Override
    public boolean shouldSaveData() {
        return true;
    }

    @Override
    public boolean registerPremadeMainCMD() {
        return true;
    }

    @Override
    public String getPermissionPrefix() {
        return getConfig().getString("settings.permissionPrefix");
    }

    @Override
    public void onReload() {
    }

    public void onEnable() {
        super.onEnable();

        tracker = new PlayerTracker(this);

        untag = new UnTagTask(getTracker()).runTaskTimer(this, 1, 1);

        if (getConfig().getBoolean("settings.pvp.enabled")) {
            getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        }

        if (getConfig().getBoolean("settings.pvp.consequences.enabled")) {
            getServer().getPluginManager().registerEvents(new ConsequencesListener(this), this);
        }

        getCommand("pvp").setExecutor(new PvpCmd(this));

        if(getData().get("pvpEnabled") != null) {
            List<String> pvpEnabledPlayersList = (List<String>) getData().get("pvpEnabled");

            for(String pvpString: pvpEnabledPlayersList) {
                String[] splitString = pvpString.split(":");

                if(splitString.length < 2) {continue;}

                tracker.enablePVP(UUID.fromString(splitString[0]), UUID.fromString(splitString[1]));
            }
        }
    }

    /**
     * Returns the player tracker
     *
     * @return the player tracker
     */
    public PlayerTracker getTracker() {
       return tracker;
    }

    public void onDisable() {
        getData().set("pvpEnabled", getTracker().getPvpEnabled());

        tracker = null;
        untag.cancel();

        untag = null;

        super.onDisable();
    }
}
