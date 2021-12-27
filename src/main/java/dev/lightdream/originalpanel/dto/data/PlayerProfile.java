package dev.lightdream.originalpanel.dto.data;

import dev.lightdream.logger.Debugger;
import dev.lightdream.originalpanel.Main;
import dev.lightdream.originalpanel.dto.data.frontend.Complain;
import dev.lightdream.originalpanel.dto.data.frontend.UnbanRequest;
import dev.lightdream.originalpanel.utils.Utils;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class PlayerProfile {

    public String username;
    public String uuid;
    public String rank;
    public Long joinDateMilliseconds;
    public String joinDate;
    public int playTimeSeconds;
    public String playTime;
    public int originalCoins;
    public String discordName;
    public String avatarURL;
    public Long discordID;
    public List<Complain> complaints;
    public List<UnbanRequest> unbanRequests;

    public PlayerProfile(String username) {
        this.username = username;
        this.avatarURL = "https://cravatar.eu/helmavatar/" + username + "/190.png";
        this.uuid = Main.instance.databaseManager.getPlayerUUID(username);
        this.rank = Main.instance.databaseManager.getRank(uuid);
        this.joinDateMilliseconds = Main.instance.databaseManager.getJoinDate(username);
        if (joinDateMilliseconds == 0) {
            this.joinDate = "Unknown";
        } else {
            this.joinDate = Utils.millisecondsToDate(joinDateMilliseconds);
        }
        this.playTimeSeconds = Main.instance.databaseManager.getPlayTime(uuid);
        this.playTime = Utils.millisecondsToHours(playTimeSeconds);
        this.originalCoins = Main.instance.databaseManager.getOriginalCoins(uuid);
        this.discordID = Main.instance.databaseManager.getDiscordID(uuid);
        if (discordID == 0L) {
            this.discordName = "Not Linked";
        } else {
            Main.instance.bot.retrieveUserById(discordID).queue(user -> {
                if (user == null) {
                    this.discordName = "Not Linked";
                    return;
                }
                this.discordName = user.getAsTag();
            });
        }
        this.complaints = Main.instance.databaseManager.getComplains(username);
        this.unbanRequests = Main.instance.databaseManager.getUnbanRequests(username);

        int cycles = 0;

        //Awaiting the discord username to be retrieved from discord API
        while (this.discordName == null) {
            cycles++;
            if (cycles > 200000000) {
                Debugger.info("Break because of timeout");
                this.discordName = "Not Loaded";
                break;
            }
        }
    }

}


