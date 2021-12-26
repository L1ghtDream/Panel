package dev.lightdream.originalpanel;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.google.gson.Gson;
import dev.lightdream.originalpanel.dto.Staff;
import dev.lightdream.originalpanel.dto.data.*;
import dev.lightdream.originalpanel.dto.data.frontend.Bug;
import dev.lightdream.originalpanel.dto.data.frontend.Complain;
import dev.lightdream.originalpanel.dto.data.frontend.UnbanRequest;
import dev.lightdream.originalpanel.utils.Debugger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@RestController()
public class RestEndPoints {

    public List<String> bugsStaff = Arrays.asList(
            "h-manager",
            "owner"
    );

    public List<String> complainStaff = Arrays.asList(
            "admin",
            "sradmin",
            "operator",
            "manager",
            "h-manager",
            "owner"
    );

    public List<String> unbanStaff = Arrays.asList(
            "srmod",
            "admin",
            "sradmin",
            "operator",
            "manager",
            "h-manager",
            "owner"
    );

    public RestEndPoints() {

    }

    @PostMapping("/api/login/v2")
    public @ResponseBody
    Response login(@RequestBody String dataStream) {
        LoginData data;

        try {
            data = new Gson().fromJson(dataStream, LoginData.class);
        } catch (Throwable t) {
            return Response.BAD_CREDENTIALS_401();
        }

        if (data == null || !checkPassword(data)) {
            return Response.BAD_CREDENTIALS_401();
        }

        return Response.OK_200(Main.instance.databaseManager.getAuthMePassword(data.username));
    }

    @PostMapping("/api/login/validate")
    public @ResponseBody
    Response validateCookie(@RequestBody String dataStream) {
        Debugger.info(dataStream);
        LoginData data;
        try {
            data = new Gson().fromJson(dataStream, LoginData.class);
        } catch (Throwable t) {
            return Response.BAD_CREDENTIALS_401();
        }


        if (data == null || !data.password.equals(Main.instance.databaseManager.getAuthMePassword(data.username))) {
            return Response.BAD_CREDENTIALS_401();
        }

        return Response.OK_200();
    }

    @PostMapping("/api/form/complain")
    public @ResponseBody
    Response complain(@RequestBody ComplainData.ComplainCreateData data) {

        Debugger.info("Received complain");

        if (!validateCookie(data.cookie).code.equals("200")) {
            return Response.BAD_CREDENTIALS_401();
        }

        if (!Main.instance.databaseManager.validateUser(data.target)) {
            return Response.INVALID_ENTRY_422();
        }

        data.status = ComplainData.ComplainStatus.OPEN_AWAITING_TARGET_RESPONSE;
        data.targetResponse = "";
        data.timestamp = System.currentTimeMillis();

        Main.instance.databaseManager.saveComplain(data);
        return Response.OK_200();
    }

    @PostMapping("/api/form/complain-target-responde")
    public @ResponseBody
    Response complainTargetRespond(@RequestBody ComplainData.ComplainTargetResponseData data) {

        if (!validateCookie(data.cookie).code.equals("200")) {
            return Response.BAD_CREDENTIALS_401();
        }

        LoginData loginData;
        try {
            loginData = new Gson().fromJson(data.cookie, LoginData.class);
        } catch (Throwable t) {
            return Response.BAD_CREDENTIALS_401();
        }

        Complain complain = Main.instance.databaseManager.getComplain(data.id);

        if (complain == null) {
            return Response.INVALID_ENTRY_422();
        }

        if (loginData == null || !loginData.username.equals(complain.target)) {
            return Response.BAD_CREDENTIALS_401();
        }

        Main.instance.databaseManager.setTargetMessageComplain(data);

        return Response.OK_200();
    }

    public boolean checkPassword(String username, String password) {
        return BCrypt.verifyer().verify(
                password.getBytes(StandardCharsets.UTF_8),
                Main.instance.databaseManager.getAuthMePassword(username).getBytes(StandardCharsets.UTF_8)
        ).verified;
    }

    public boolean checkPassword(LoginData data) {
        if (data == null) {
            return false;
        } else if (data.username == null || data.username.equals("") || data.password == null || data.password.equals("")) {
            return false;
        }
        return checkPassword(data.username, data.password);
    }

    @PostMapping("/api/check/staff")
    public Response checkStaff(String user, String useCase) {

        @SuppressWarnings("unchecked") List<Staff> staffs = (List<Staff>) Main.instance.cacheManager.staffs.get();

        if (staffs.stream().anyMatch(staff -> {
            if (useCase.equals("complain")) {
                return staff.username.equalsIgnoreCase(user) && complainStaff.contains(staff.rank);
            }
            if (useCase.equals("unban")) {
                return staff.username.equalsIgnoreCase(user) && unbanStaff.contains(staff.rank);
            }
            if (useCase.equals("bug")) {
                return staff.username.equalsIgnoreCase(user) && bugsStaff.contains(staff.rank);
            }
            if (useCase.equals("any")) {
                return staff.username.equalsIgnoreCase(user);
            }
            return false;
        })) {
            return Response.OK_200();
        }

        return Response.BAD_CREDENTIALS_401();
    }

    @PostMapping("/api/update/form/complain")
    public Response changeComplainStatus(@RequestBody ComplainData.ComplainDecisionData data) {
        if (!validateCookie(data.cookie).code.equals("200")) {
            return Response.BAD_CREDENTIALS_401();
        }

        LoginData loginData;
        try {
            loginData = new Gson().fromJson(data.cookie, LoginData.class);
        } catch (Throwable t) {
            return Response.BAD_CREDENTIALS_401();
        }

        Complain complain = Main.instance.databaseManager.getComplain(data.id);

        if (complain == null) {
            return Response.INVALID_ENTRY_422();
        }

        if (loginData == null || !checkStaff(loginData.username, "complain").code.equals("200")) {
            return Response.BAD_CREDENTIALS_401();
        }

        Main.instance.databaseManager.setComplainDecision(data);

        return Response.OK_200();
    }

    @PostMapping("/api/form/unban")
    public @ResponseBody
    Response unban(@RequestBody UnbanData.UnbanCreateData data) {

        if (!validateCookie(data.cookie).code.equals("200")) {
            return Response.BAD_CREDENTIALS_401();
        }

        if (!Main.instance.databaseManager.validateUser(data.staff)) {
            return Response.INVALID_ENTRY_422();
        }

        data.status = UnbanData.UnbanStatus.OPEN;
        data.timestamp = System.currentTimeMillis();

        Main.instance.databaseManager.saveUnban(data);
        return Response.OK_200();
    }

    @PostMapping("/api/update/form/unban")
    public Response changeUnbanStatus(@RequestBody UnbanData.UnbanDecisionData data) {
        if (!validateCookie(data.cookie).code.equals("200")) {
            return Response.BAD_CREDENTIALS_401();
        }

        LoginData loginData;
        try {
            loginData = new Gson().fromJson(data.cookie, LoginData.class);
        } catch (Throwable t) {
            return Response.BAD_CREDENTIALS_401();
        }

        UnbanRequest complain = Main.instance.databaseManager.getUnbanRequest(data.id);

        if (complain == null) {
            return Response.INVALID_ENTRY_422();
        }

        if (loginData == null || !checkStaff(loginData.username, "unban").code.equals("200")) {
            return Response.BAD_CREDENTIALS_401();
        }

        Main.instance.databaseManager.setUnbanDecision(data);

        return Response.OK_200();
    }

    @PostMapping("/api/form/bugs")
    public @ResponseBody
    Response bugs(@RequestBody BugsData.BugCreateData data) {

        if (!validateCookie(data.cookie).code.equals("200")) {
            return Response.BAD_CREDENTIALS_401();
        }

        data.status = BugsData.BugStatus.OPEN;
        data.timestamp = System.currentTimeMillis();

        Main.instance.databaseManager.saveBug(data);
        return Response.OK_200();
    }

    @PostMapping("/api/update/form/bug")
    public Response closeBug(@RequestBody BugsData.BugCloseData data) {
        if (!validateCookie(data.cookie).code.equals("200")) {
            return Response.BAD_CREDENTIALS_401();
        }

        LoginData loginData;
        try {
            loginData = new Gson().fromJson(data.cookie, LoginData.class);
        } catch (Throwable t) {
            return Response.BAD_CREDENTIALS_401();
        }

        Bug complain = Main.instance.databaseManager.getBug(data.id);

        if (complain == null) {
            return Response.INVALID_ENTRY_422();
        }

        if (loginData == null || !checkStaff(loginData.username, "bug").code.equals("200")) {
            return Response.BAD_CREDENTIALS_401();
        }

        Main.instance.databaseManager.closeBug(data);

        return Response.OK_200();
    }


}
