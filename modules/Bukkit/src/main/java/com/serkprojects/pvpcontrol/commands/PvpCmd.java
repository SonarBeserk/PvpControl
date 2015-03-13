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

package com.serkprojects.pvpcontrol.commands;

import com.serkprojects.pvpcontrol.PVPControl;
import lombok.NonNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PvpCmd implements CommandExecutor {
    private PVPControl plugin;

    public PvpCmd(@NonNull PVPControl plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            helpSubCommand(sender);
            return true;
        }

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("help")) {
                helpSubCommand(sender);
                return true;
            }

            if (args[0].equalsIgnoreCase("status")) {
                statusSubCommand(sender, args);
                return true;
            }

            if (args[0].equalsIgnoreCase("enable")) {
                enableSubCommand(sender, args);
                return true;
            }

            if (args[0].equalsIgnoreCase("disable")) {
                disableSubCommand(sender, args);
                return true;
            }

            plugin.getMessaging().sendMessage(sender, true, plugin.getLanguage().getMessage("unknownSubcommand"));
            return true;
        }

        return true;
    }

    private void helpSubCommand(@NonNull CommandSender sender) {
        plugin.getMessaging().sendMessage(sender, true, plugin.getLanguage().getMessage("usagePvp").replace("{name}", plugin.getDescription().getName()));
    }

    private void statusSubCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission(plugin.getPermissionPrefix() + ".others.status")) {
            plugin.getMessaging().sendMessage(sender, true, plugin.getLanguage().getMessage("noPermission"));
            return;
        }

        if (args.length == 1) {
            plugin.getMessaging().sendMessage(sender, true, plugin.getLanguage().getMessage("mustUseExtended"));
            return;
        }

        if (args.length > 1) {
            if (args.length == 3) {
                plugin.getMessaging().sendMessage(sender, true, plugin.getLanguage().getMessage("pvpStatusSet").replace("{name}", args[1]).replace("{world}", args[2]).replace("{pvp}", String.valueOf(plugin.getTracker().pvpEnabled(UUID.fromString(args[1]), UUID.fromString(args[2])))));
            }
        }
    }

    private void enableSubCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission(plugin.getPermissionPrefix() + ".commands.enable")) {
            plugin.getMessaging().sendMessage(sender, true, plugin.getLanguage().getMessage("noPermission"));
            return;
        }

        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                plugin.getMessaging().sendMessage(sender, true, plugin.getLanguage().getMessage("mustUseExtended"));
                return;
            }

            Player senderPlayer = (Player) sender;

            if (!plugin.getTracker().pvpEnabled(senderPlayer.getUniqueId(), senderPlayer.getWorld().getUID())) {
                plugin.getTracker().enablePVP(senderPlayer.getUniqueId(), senderPlayer.getWorld().getUID());
                plugin.getMessaging().sendMessage(sender, true, plugin.getLanguage().getMessage("pvpStatus").replace("{world}", senderPlayer.getWorld().getName()).replace("{pvp}", String.valueOf(plugin.getTracker().pvpEnabled(senderPlayer.getUniqueId(), senderPlayer.getWorld().getUID()))));
                return;
            } else {
                plugin.getMessaging().sendMessage(senderPlayer, true, plugin.getLanguage().getMessage("alreadyEnabled").replace("{name}", sender.getName()).replace("{world}", senderPlayer.getWorld().getName()));
                return;
            }
        }

        if (args.length > 1) {
            if (!sender.hasPermission(plugin.getPermissionPrefix() + ".others.enable")) {
                plugin.getMessaging().sendMessage(sender, false, plugin.getLanguage().getMessage("noPermission"));
                return;
            }

            if (args.length == 3) {
                Player player = plugin.getServer().getPlayer(args[1]);

                if(player == null) {
                    plugin.getMessaging().sendMessage(sender, false, plugin.getLanguage().getMessage("playerNotFound").replace("{name}", args[1]));
                    return;
                }

                if (!plugin.getTracker().pvpEnabled(player.getUniqueId(), UUID.fromString(args[2]))) {
                    plugin.getTracker().enablePVP(player.getUniqueId(), UUID.fromString(args[2]));

                    plugin.getMessaging().sendMessage(sender, true, plugin.getLanguage().getMessage("pvpStatus").replace("{world}", player.getWorld().getName()).replace("{pvp}", String.valueOf(plugin.getTracker().pvpEnabled(player.getUniqueId(), UUID.fromString(args[2])))));
                } else {
                    plugin.getMessaging().sendMessage(sender, true, plugin.getLanguage().getMessage("alreadyEnabledPlayer").replace("{name}", args[1]).replace("{world}", args[2]));
                }
            }
        }
    }

    private void disableSubCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission(plugin.getPermissionPrefix() + ".commands.disable")) {
            plugin.getMessaging().sendMessage(sender, true, plugin.getLanguage().getMessage("noPermission"));
            return;
        }

        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                plugin.getMessaging().sendMessage(sender, true, plugin.getLanguage().getMessage("mustUseExtended"));
                return;
            }

            Player senderPlayer = (Player) sender;

            if (plugin.getTracker().pvpEnabled(senderPlayer.getUniqueId(), plugin.getServer().getPlayer(sender.getName()).getWorld().getUID())) {
                plugin.getTracker().disablePVP(senderPlayer.getUniqueId(), plugin.getServer().getPlayer(sender.getName()).getWorld().getUID());
                plugin.getMessaging().sendMessage(sender, true, plugin.getConfig().getString("pvpStatus").replace("{world}", senderPlayer.getWorld().getName()).replace("{pvp}", String.valueOf(plugin.getTracker().pvpEnabled(senderPlayer.getUniqueId(), senderPlayer.getWorld().getUID()))));
                return;
            } else {
                plugin.getMessaging().sendMessage(senderPlayer, true, plugin.getLanguage().getMessage("alreadyDisabled").replace("{name}", sender.getName()).replace("{world}", senderPlayer.getWorld().getName()));
                return;
            }
        }

        if (args.length > 1) {
            if (!sender.hasPermission(plugin.getPermissionPrefix() + ".others.disable")) {
                plugin.getMessaging().sendMessage(sender, true, plugin.getLanguage().getMessage("noPermission"));
                return;
            }

            if (args.length == 3) {
                Player player = plugin.getServer().getPlayer(args[1]);

                if(player == null) {
                    plugin.getMessaging().sendMessage(sender, false, plugin.getLanguage().getMessage("playerNotFound").replace("{name}", args[1]));
                    return;
                }

                if (plugin.getTracker().pvpEnabled(player.getUniqueId(), UUID.fromString(args[2]))) {
                    plugin.getTracker().disablePVP(player.getUniqueId(), UUID.fromString(args[2]));

                    plugin.getMessaging().sendMessage(sender, true, plugin.getLanguage().getMessage("pvpStatus").replace("{world}", player.getWorld().getName()).replace("{pvp}", String.valueOf(plugin.getTracker().pvpEnabled(player.getUniqueId(), UUID.fromString(args[2])))));
                } else {
                    plugin.getMessaging().sendMessage(sender, true, plugin.getLanguage().getMessage("alreadyDisabledPlayer").replace("{name}", args[1]).replace("{world}", args[2]));
                }
            }
        }
    }
}
