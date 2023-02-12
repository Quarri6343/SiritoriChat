package quarri6343.siritorichat;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.argument.PlayerArgument;
import net.kunmc.lab.commandlib.argument.StringArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;

public class SwapCommand extends Command {
    public SwapCommand() {
        super("swap");
        
        argument(new StringArgument("character", StringArgument.Type.PHRASE), (string, ctx) -> {
            if(string.isEmpty())
                return;
            
            SiritoriChat.currentCharacter = string.charAt(0);
        });
    }
}
