package nautiluscore.commands;

import nautiluscore.NautilusCore;
import org.bukkit.command.CommandSender;

import static org.bukkit.ChatColor.*;

public class ColorsCommand extends CommandStem {
    public ColorsCommand(NautilusCore plugin) {
        super(plugin, "colors", "Show color and formatting codes", "", "");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage(RED + "=============================\n" + WHITE +
                "&0 = " + BLACK + "Black        " + RESET + "&1 = " + DARK_BLUE + "Dark Blue\n" + RESET +
                "&2 = " + DARK_GREEN + "Dark Green " + RESET + "&3 = " + DARK_AQUA + "Dark Aqua\n" + RESET +
                "&4 = " + DARK_RED + "Dark Red    " + RESET + "&5 = " + DARK_PURPLE + "Dark Purple\n" + RESET +
                "&6 = " + GOLD + "Gold         " + RESET + "&7 = " + GRAY + "Gray\n" + RESET +
                "&8 = " + DARK_GRAY + "Dark Gray  " + RESET + "&9 = " + BLUE + "Blue\n" + RESET +
                "&a = " + GREEN + "Green       " + RESET + "&b = " + AQUA + "Aqua\n" + RESET +
                "&c = " + RED + "Red          " + RESET + "&d = " + LIGHT_PURPLE + "Light Purple\n" + RESET +
                "&e = " + YELLOW + "Yellow       " + RESET + "&f = " + WHITE + "White\n" + RESET +
                RED + "=============================\n" + WHITE +
                "&k = " + MAGIC + "Magic        " + RESET + "&l = " + BOLD + "Bold\n" + RESET +
                "&m = " + STRIKETHROUGH + "Strike" + RESET + "       &n = " + UNDERLINE + "Underline\n" + RESET +
                "&o = " + ITALIC + "Italic        " + RESET + "&r = " + RESET + "Reset\n" + RESET +
                RED + "=============================\n"
        );
        return true;
    }
}
