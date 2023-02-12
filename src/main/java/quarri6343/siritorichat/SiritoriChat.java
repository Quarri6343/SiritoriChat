package quarri6343.siritorichat;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kunmc.lab.commandlib.CommandLib;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SiritoriChat extends JavaPlugin implements Listener {

    public static Character currentCharacter = 'り';

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        CommandLib.register(this, new SwapCommand());
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> player.sendActionBar(Component.text("現在の文字は： " + currentCharacter)));
            }
        }.runTaskTimer(this, 0, 10);
    }

    @Override
    public void onDisable() {
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent e) {
        String message = new PlainComponentSerializer().serialize(e.message());

        if (message.isEmpty()) {
            return;
        }

        if (e.getPlayer().getGameMode() != GameMode.SURVIVAL) {
            e.message(Component.text(message).color(NamedTextColor.GRAY));
            return;
        }

        //最初の文字を判定する
        Character firstCharacter = analyzeFirstCharacter(message);
        if(firstCharacter.equals(Character.MIN_VALUE) || firstCharacter.equals('*') || firstCharacter.equals('ー')){
            e.setCancelled(true);
            e.getPlayer().sendMessage("最初の文字の認識に失敗しました。言葉を変えてもう一度お試しください");
            return;
        }
        else if(!firstCharacter.equals(currentCharacter)){
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.YELLOW + Character.toString(firstCharacter) + ChatColor.RESET + " は現在の文字ではありません");
            return;
        }
        
        //最後の文字を判定する
        Character finalCharacter = analyzeFinalCharacter(message);
        if (finalCharacter.equals(Character.MIN_VALUE) || finalCharacter.equals('*') || finalCharacter.equals('ー')) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("最後の文字の認識に失敗しました。言葉を変えてもう一度お試しください");
        } else if (finalCharacter.equals('ん')) {
            e.message(Component.text(message + ChatColor.RED + "(" + finalCharacter + ")"));
            Bukkit.getScheduler().runTaskLater(this, () -> {
                e.getPlayer().getLocation().createExplosion(4, false, false);
                e.getPlayer().setGameMode(GameMode.SPECTATOR);
                Bukkit.broadcast(Component.text(e.getPlayer().getName() + "が「ん」で終わる言葉を話しました！").color(NamedTextColor.RED));
            }, 0);
        } else {
            e.message(Component.text(message + ChatColor.YELLOW + "(" + finalCharacter + ")"));
            currentCharacter = finalCharacter;
        }
    }

    private Character analyzeFinalCharacter(String message) {
        Tokenizer.Builder builder = new Tokenizer.Builder();
        Tokenizer tokenizer = null;
        try {
            InputStream inputStream = new ByteArrayInputStream("".getBytes("utf-8"));
            tokenizer = builder.userDictionary(inputStream).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //チャットから記号や英語を取り除く
        message = message.replaceAll("([ -~])+|([･-ﾟ])+|([０-９Ａ-Ｚａ-ｚ])+|([！-／：-＠［-｀｛-～、-〜”’・ー])+", "");
        if (message.isEmpty())
            return Character.MIN_VALUE;

        //もし最後の文字がカタカナまたはひらがなだった場合、大文字のひらがなにして返す
        char lastCharacter = message.charAt(message.length() - 1);
        Pattern pattern = Pattern.compile("([ぁ-んー])+|([ァ-ヶー])+|([ｱ-ﾝﾞﾟ])+");
        Matcher matcher = pattern.matcher(Character.toString(lastCharacter));
        if (matcher.find()) {
            lastCharacter = KanaUtil.toUpperCase(Character.toString(lastCharacter)).charAt(0);
            lastCharacter = KanaUtil.kataToHira(Character.toString(lastCharacter)).charAt(0);
            return lastCharacter;
        }

        //チャットの最後がそれ以外だった場合、読み仮名を推測してひらがなにして返す
        List<Token> tokens = tokenizer.tokenize(message);
        String lastPronounciation = tokens.get(tokens.size() - 1).getPronunciation();
        lastPronounciation = KanaUtil.toUpperCase(lastPronounciation);
        Character lastCharacter2 = lastPronounciation.charAt(lastPronounciation.length() - 1);
        if (lastCharacter2.equals('ー') && lastPronounciation.length() > 1)
            lastCharacter2 = lastPronounciation.charAt(lastPronounciation.length() - 2);
        return KanaUtil.kataToHira(Character.toString(lastCharacter2)).charAt(0);
    }

    private boolean verify() {
        return false;
    }

    private Character analyzeFirstCharacter(String message){
        Tokenizer.Builder builder = new Tokenizer.Builder();
        Tokenizer tokenizer = null;
        try {
            InputStream inputStream = new ByteArrayInputStream("".getBytes("utf-8"));
            tokenizer = builder.userDictionary(inputStream).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //チャットから記号や英語を取り除く
        message = message.replaceAll("([ -~])+|([･-ﾟ])+|([０-９Ａ-Ｚａ-ｚ])+|([！-／：-＠［-｀｛-～、-〜”’・ー])+", "");
        if (message.isEmpty())
            return Character.MIN_VALUE;

        //もし最初の文字がカタカナまたはひらがなだった場合、大文字のひらがなにして返す
        char firstCharacter = message.charAt(0);
        Pattern pattern = Pattern.compile("([ぁ-んー])+|([ァ-ヶー])+|([ｱ-ﾝﾞﾟ])+");
        Matcher matcher = pattern.matcher(Character.toString(firstCharacter));
        if (matcher.find()) {
            firstCharacter = KanaUtil.toUpperCase(Character.toString(firstCharacter)).charAt(0);
            firstCharacter = KanaUtil.kataToHira(Character.toString(firstCharacter)).charAt(0);
            return firstCharacter;
        }

        //チャットの最初がそれ以外だった場合、読み仮名を推測してひらがなにして返す
        List<Token> tokens = tokenizer.tokenize(message);
        String firstPronounciation = tokens.get(0).getPronunciation();
        firstPronounciation = KanaUtil.toUpperCase(firstPronounciation);
        Character firstCharacter2 = firstPronounciation.charAt(0);
        return KanaUtil.kataToHira(Character.toString(firstCharacter2)).charAt(0);
    }
}
