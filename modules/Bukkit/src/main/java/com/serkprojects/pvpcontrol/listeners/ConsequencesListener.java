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

package com.serkprojects.pvpcontrol.listeners;

import com.serkprojects.pvpcontrol.PVPControl;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class ConsequencesListener implements Listener {
    private PVPControl plugin = null;

    public ConsequencesListener(@NonNull PVPControl plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void pvpLog(PlayerQuitEvent e) {
        if (!plugin.getConfig().getBoolean("settings.pvp.consequences.pvpLog") || !plugin.getTracker().isTagged(e.getPlayer().getUniqueId(), e.getPlayer().getWorld().getUID())) {
            return;
        }

        e.getPlayer().setHealth(0);

        boolean emptyInventory = true;

        for (ItemStack stack : e.getPlayer().getInventory().getArmorContents()) {
            if (stack == null || stack.getType() == null || stack.getType() == Material.AIR) {
                continue;
            }

            emptyInventory = false;
        }

        for (ItemStack stack : e.getPlayer().getInventory().getContents()) {
            if (stack == null || stack.getType() == null || stack.getType() == Material.AIR) {
                continue;
            }

            emptyInventory = false;
        }

        if (emptyInventory) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (plugin.getTracker().pvpEnabled(player.getUniqueId(), player.getWorld().getUID())) {
                    plugin.getMessaging().sendMessage(e.getPlayer(), true, plugin.getLanguage().getMessage("pvpLogEmpty").replace("{name}", e.getPlayer().getName()).replace("{dispname}", e.getPlayer().getDisplayName()));
                }
            }
        } else {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (plugin.getTracker().pvpEnabled(player.getUniqueId(), player.getWorld().getUID())) {
                    plugin.getMessaging().sendMessage(e.getPlayer(), true, plugin.getLanguage().getMessage("pvpLogItems").replace("{name}", e.getPlayer().getName()).replace("{dispname}", e.getPlayer().getDisplayName()));
                }
            }
        }
    }
}
