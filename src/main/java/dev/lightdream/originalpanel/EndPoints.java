package dev.lightdream.originalpanel;

import dev.lightdream.originalpanel.dto.data.Complain;
import dev.lightdream.originalpanel.dto.data.PlayerProfile;
import dev.lightdream.originalpanel.dto.data.UnbanRequest;
import dev.lightdream.originalpanel.utils.Debugger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EndPoints {
    @GetMapping("/")
    public String indexWithMessage(Model model, String message) {

        model.addAttribute("donors_count", Main.instance.cacheManager.donorsCount.get());
        model.addAttribute("registered_count", Main.instance.cacheManager.registeredPlayersCount.get());
        model.addAttribute("online_players_count", Main.instance.cacheManager.onlinePlayers.get());
        model.addAttribute("message", message);

        return "index.html";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login.html";
    }

    @GetMapping("/staff")
    public String staff(Model model) {
        model.addAttribute("staffs", Main.instance.cacheManager.staffs.get());
        return "staff.html";
    }

    @GetMapping("/rules")
    public String rules(Model model) {
        return "server-rules.html";
    }

    @GetMapping("/complain")
    public String complain(Model model, Integer id) {
        if (id == null) {
            return "complaints.html";
        }

        Complain complain = Main.instance.databaseManager.getComplain(id);

        if (complain == null) {
            return "404.html";
        }

        model.addAttribute("complain", complain);

        return "complaints-details.html";
    }

    @GetMapping("/profile")
    public String profile(Model model, String user) {
        PlayerProfile profile = new PlayerProfile(user);
        model.addAttribute("profile", profile);
        Debugger.info(profile);
        return "user.html";
    }

    @GetMapping("/unauthorised")
    public String unauthorised(Model model) {
        return "401.html";
    }

    @GetMapping("/notfound")
    public String notFound(Model model) {
        return "404.html";
    }

    @GetMapping("/entries")
    public String entries(Model model) {

        model.addAttribute("complaints", Main.instance.databaseManager.getComplains());

        return "entries.html";
    }

    @GetMapping("/unban")
    public String unban(Model model, Integer id) {
        if (id == null) {
            return "unban.html";
        }

        UnbanRequest unbanRequest = Main.instance.databaseManager.getUnbanRequest(id);

        if (unbanRequest == null) {
            return "404.html";
        }

        model.addAttribute("unbanRequest", unbanRequest);

        return "unban-details.html";
    }
}


