package dev.lightdream.originalpanel.managers;

import com.mysql.cj.protocol.Resultset;
import dev.lightdream.databasehandler.OrderByType;
import dev.lightdream.databasehandler.database.HikariDatabaseManager;
import dev.lightdream.databasehandler.dto.LambdaExecutor;
import dev.lightdream.logger.Debugger;
import dev.lightdream.originalpanel.Main;
import dev.lightdream.originalpanel.dto.Staff;
import dev.lightdream.originalpanel.dto.data.BugsData;
import dev.lightdream.originalpanel.dto.data.ComplainData;
import dev.lightdream.originalpanel.dto.data.UnbanData;
import dev.lightdream.originalpanel.dto.data.frontend.Bug;
import dev.lightdream.originalpanel.dto.data.frontend.Complain;
import dev.lightdream.originalpanel.dto.data.frontend.UnbanRequest;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "ArraysAsListWithZeroOrOneArgument"})
public class DatabaseManager extends HikariDatabaseManager {

    @SuppressWarnings("FieldMayBeFinal")
    public DatabaseManager() {
        super(Main.instance);
        Debugger.info("Passed main as " + Main.instance);
        setup();
    }

    public void setup() {
        createTable(Complain.class);
        createTable(UnbanRequest.class);
        createTable(Bug.class);
    }

    @SneakyThrows
    public int getDonorsCount() {
        String sql = "SELECT COUNT(*) FROM `luckperms`.`luckperms_user_permissions` WHERE permission LIKE \"group.%\" AND permission!=\"group.default\" AND server=\"global\";";
        ResultSet r = executeQuery(sql, new ArrayList<>());
        if (r.next()) {
            return r.getInt(1);
        }
        return 0;
    }


    @SneakyThrows
    public int getRegisteredCount() {
        String sql = "SELECT COUNT(*) FROM `authme`.`authme` WHERE 1";
        ResultSet r = executeQuery(sql, new ArrayList<>());
        if (r.next()) {
            return r.getInt(1);
        }
        return 0;
    }

    @SneakyThrows
    public String getAuthMePassword(String username) {
        String sql = "SELECT password FROM `authme`.`authme` WHERE username=? OR realname=?";
        ResultSet r = executeQuery(sql, Arrays.asList(username, username));
        if (r.next()) {
            return r.getString(1);
        }
        return "";
    }

    @SneakyThrows
    public List<Staff> getStaff() {

        List<String> staffRanks = Arrays.asList("owner", "h-manager", "manager", "supervizor", "operator", "sradmin", "admin", "srmod", "mod", "jrmod", "helper", "trainee");

        String args1 = "";
        String args2 = "";

        for (String staffRank : staffRanks) {
            //noinspection StringConcatenationInLoop
            args1 += "permission=\"group." + staffRank + "\" OR ";
            //noinspection StringConcatenationInLoop
            args2 += "primary_group=\"" + staffRank + "\" OR ";
        }
        args1 += "OR";
        args1 = args1.replace("OR OR", "");
        args2 += "OR";
        args2 = args2.replace("OR OR", "");

        String sql1 = "SELECT * FROM `luckperms`.`luckperms_user_permissions` WHERE server=\"global\" AND ( " + args1 + " );";
        String sql2 = "SELECT * FROM `luckperms`.`luckperms_players` WHERE " + args2;

        ResultSet r1 = executeQuery(sql1, new ArrayList<>());
        ResultSet r2 = executeQuery(sql2, new ArrayList<>());

        HashSet<String> staffsUUID = new HashSet<>();

        while (r1.next()) {
            staffsUUID.add(r1.getString("uuid"));
        }

        while (r2.next()) {
            staffsUUID.add(r2.getString("uuid"));
        }

        List<Staff> staffs = new ArrayList<>();

        staffsUUID.forEach(uuid -> {
            Staff staff = new Staff();
            staff.uuid = uuid;
            staff.username = getPlayerName(uuid);
            staff.rank = getRank(uuid);
            if (!staff.username.equals("null")) {
                staffs.add(staff);
            }
        });

        //Sort staff

        List<Staff> owner = staffs.stream().filter(staff -> staff.rank.equals("owner")).collect(Collectors.toList());
        List<Staff> hManager = staffs.stream().filter(staff -> staff.rank.equals("h-manager")).collect(Collectors.toList());
        List<Staff> manager = staffs.stream().filter(staff -> staff.rank.equals("manager")).collect(Collectors.toList());
        List<Staff> operator = staffs.stream().filter(staff -> staff.rank.equals("operator")).collect(Collectors.toList());
        List<Staff> srAdmin = staffs.stream().filter(staff -> staff.rank.equals("sradmin")).collect(Collectors.toList());
        List<Staff> admin = staffs.stream().filter(staff -> staff.rank.equals("admin")).collect(Collectors.toList());
        List<Staff> srMod = staffs.stream().filter(staff -> staff.rank.equals("srmod")).collect(Collectors.toList());
        List<Staff> mod = staffs.stream().filter(staff -> staff.rank.equals("mod")).collect(Collectors.toList());
        List<Staff> jrMod = staffs.stream().filter(staff -> staff.rank.equals("jrmod")).collect(Collectors.toList());
        List<Staff> helper = staffs.stream().filter(staff -> staff.rank.equals("helper")).collect(Collectors.toList());
        List<Staff> trainee = staffs.stream().filter(staff -> staff.rank.equals("trainee")).collect(Collectors.toList());

        Collections.sort(owner);
        Collections.sort(hManager);
        Collections.sort(manager);
        Collections.sort(operator);
        Collections.sort(srAdmin);
        Collections.sort(admin);
        Collections.sort(srMod);
        Collections.sort(mod);
        Collections.sort(jrMod);
        Collections.sort(helper);
        Collections.sort(trainee);

        List<Staff> output = new ArrayList<>();

        output.addAll(owner);
        output.addAll(hManager);
        output.addAll(manager);
        output.addAll(operator);
        output.addAll(srAdmin);
        output.addAll(admin);
        output.addAll(srMod);
        output.addAll(mod);
        output.addAll(jrMod);
        output.addAll(helper);
        output.addAll(trainee);

        return output;
    }

    @SneakyThrows
    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    public String getPlayerName(String uuid) {
        String sql = "SELECT * FROM `luckperms`.`luckperms_players` WHERE uuid=?";
        ResultSet r = executeQuery(sql, Arrays.asList(uuid));

        if (r.next()) {
            return r.getString("username");
        }

        return "Undefined";
    }

    @SneakyThrows
    @SuppressWarnings({"ArraysAsListWithZeroOrOneArgument", "BooleanMethodIsAlwaysInverted"})
    public boolean validateUser(String username) {
        String sql = "SELECT COUNT(*) FROM `luckperms`.`luckperms_players` WHERE username=?";
        ResultSet r = executeQuery(sql, Arrays.asList(username));

        if (r.next()) {
            return r.getInt(1) >= 1;
        }

        return false;
    }

    @SneakyThrows
    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    public String getPlayerUUID(String username) {
        String sql = "SELECT uuid FROM `luckperms`.`luckperms_players` WHERE username=?";
        ResultSet r = executeQuery(sql, Arrays.asList(username.toLowerCase()));

        if (r.next()) {
            return r.getString(1);
        }

        return "";

    }

    @SneakyThrows
    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    public String getRank(String uuid) {
        String sql1 = "SELECT primary_group FROM `luckperms`.`luckperms_players` WHERE uuid=?";
        String sql2 = "SELECT permission FROM `luckperms`.`luckperms_user_permissions` WHERE permission LIKE \"group.%\" AND uuid=?";

        List<String> userRanks = new ArrayList<>();

        ResultSet r1 = executeQuery(sql1, Arrays.asList(uuid));
        ResultSet r2 = executeQuery(sql2, Arrays.asList(uuid));

        if (r1.next()) {
            userRanks.add(r1.getString(1).replace("group.", ""));
        }

        while (r2.next()) {
            userRanks.add(r2.getString(1).replace("group.", ""));
        }

        return selectHighestRank(userRanks);
    }

    public String selectHighestRank(List<String> ranks) {

        Debugger.info(ranks);

        List<String> definedRanks = new ArrayList<>(Arrays.asList("owner", "h-manager", "manager", "supervizor", "operator", "sradmin", "admin", "srmod", "mod", "jrmod", "helper", "trainee", "sponsor", "original", "eternal", "platinum", "legendary", "god", "xenon", "heda", "suprem", "alpha"));

        for (String rank : definedRanks) {
            if (ranks.contains(rank)) {
                return rank;
            }
        }

        return "default";
    }

    @SneakyThrows
    public int getPlayerBanCount(String uuid) {
        String sql = "SELECT COUNT(*) FROM 'litebans'.'bans' WHERE uuid=?";
        ResultSet r = executeQuery(sql, Arrays.asList(uuid));

        int output = 0;

        while (r.next()) {
            output = r.getInt(1);
        }

        return output;
    }

    @SneakyThrows
    public int getPlayerKickCount(String uuid) {
        String sql = "SELECT COUNT(*) FROM 'litebans'.'kicks' WHERE uuid=?";
        ResultSet r = executeQuery(sql, Arrays.asList(uuid));

        int output = 0;

        while (r.next()) {
            output = r.getInt(1);
        }

        return output;
    }

    @SneakyThrows
    public int getPlayerMuteCount(String uuid) {
        String sql = "SELECT COUNT(*) FROM 'litebans'.'mutes' WHERE uuid=?";
        ResultSet r = executeQuery(sql, Arrays.asList(uuid));

        int output = 0;

        while (r.next()) {
            output = r.getInt(1);
        }

        return output;
    }

    @SneakyThrows
    public int getPlayerWarningCount(String uuid) {
        String sql = "SELECT COUNT(*) FROM 'litebans'.'warnings' WHERE uuid=?";
        ResultSet r = executeQuery(sql, Arrays.asList(uuid));

        int output = 0;

        while (r.next()) {
            output = r.getInt(1);
        }

        return output;
    }

    @SneakyThrows
    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    public Long getJoinDate(String username) {
        String sql = "SELECT regdate FROM `authme`.`authme` WHERE  username=?";
        ResultSet r = executeQuery(sql, Arrays.asList(username.toLowerCase()));

        if (r.next()) {
            return r.getLong(1);
        }

        return 0L;
    }

    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    @SneakyThrows
    public int getPlayTime(String uuid) {
        String sql = "SELECT playedtime FROM `bungeecord`.`playtime` WHERE uuid=?";
        ResultSet r = executeQuery(sql, Arrays.asList(uuid));

        int output = 0;

        while (r.next()) {
            output += r.getInt(1);
        }

        return output;
    }

    @SneakyThrows
    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    public int getOriginalCoins(String uuid) {
        String sql = "SELECT points FROM `player_points`.`playerpoints_points` WHERE uuid=?";
        ResultSet r = executeQuery(sql, Arrays.asList(uuid));

        if (r.next()) {
            return r.getInt(1);
        }

        return 0;
    }

    @SneakyThrows
    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    public Long getDiscordID(String uuid) {
        String sql = "SELECT discord_id FROM `royal_security`.`users` WHERE uuid=?";
        ResultSet r = executeQuery(sql, Arrays.asList(uuid));

        if (r.next()) {
            return r.getLong(1);
        }

        return 0L;
    }


    @SneakyThrows
    public List<Complain> getComplains(String username) {
        return get(Complain.class, new HashMap<>() {{
            put("user", username);
            put("target", username);
        }}, "id", 10, OrderByType.DESCENDENT);
    }


    @SneakyThrows
    public List<UnbanRequest> getUnbanRequests(String username) {
        return get(UnbanRequest.class, new HashMap<>() {{
            put("user", username);
            put("staff", username);
        }}, "id", 10, OrderByType.DESCENDENT);
    }


    @SneakyThrows
    public Complain getComplain(int id) {
        return get(Complain.class, new HashMap<>() {{
            put("id", id);
        }}).stream().findFirst().orElse(null);
    }

    @SneakyThrows
    public UnbanRequest getUnbanRequest(int id) {
        return get(UnbanRequest.class, new HashMap<>() {{
            put("id", id);
        }}).stream().findFirst().orElse(null);
    }

    @SneakyThrows
    public List<Complain> getComplains() {
        return get(Complain.class, new HashMap<>() {{
            put("status", ComplainData.ComplainStatus.OPEN_AWAITING_STAFF_APPROVAL.toString());
        }}, "id", 20, OrderByType.ASCENDANT);
    }

    @SneakyThrows
    public List<UnbanRequest> getUnbans() {
        return get(UnbanRequest.class, new HashMap<>() {{
            put("status", UnbanData.UnbanStatus.OPEN.toString());
        }}, "id", 20, OrderByType.ASCENDANT);
    }

    @SneakyThrows
    public List<Bug> getBugs() {
        return get(Bug.class, new HashMap<>() {{
            put("status", BugsData.BugStatus.OPEN.toString());
        }}, "id", 20, OrderByType.ASCENDANT);
    }

    @SneakyThrows
    public Bug getBug(int id) {
        return get(Bug.class, new HashMap<>() {{
            put("id", id);
        }}).stream().findFirst().orElse(null);
    }

    @Override
    public HashMap<Class<?>, String> getDataTypes() {
        return new HashMap<>() {{
            put(ComplainData.ComplainStatus.class, "TEXT");
            put(ComplainData.ComplainDecision.class, "TEXT");
            put(UnbanData.UnbanStatus.class, "TEXT");
            put(UnbanData.UnbanDecision.class, "TEXT");
            put(BugsData.BugStatus.class, "TEXT");
        }};
    }

    @Override
    public HashMap<Class<?>, LambdaExecutor> getSerializeMap() {
        return new HashMap<>() {{
            put(ComplainData.ComplainStatus.class, obj -> "\"" + obj.toString() + "\"");
            put(ComplainData.ComplainDecision.class, obj -> "\"" + obj.toString() + "\"");
            put(UnbanData.UnbanStatus.class, obj -> "\"" + obj.toString() + "\"");
            put(UnbanData.UnbanDecision.class, obj -> "\"" + obj.toString() + "\"");
            put(BugsData.BugStatus.class, obj -> "\"" + obj.toString() + "\"");
        }};
    }

    @Override
    public HashMap<Class<?>, LambdaExecutor> getDeserializeMap() {
        return new HashMap<>() {{
            put(ComplainData.ComplainStatus.class, obj -> ComplainData.ComplainStatus.valueOf(obj.toString()));
            put(ComplainData.ComplainDecision.class, obj -> ComplainData.ComplainDecision.valueOf(obj.toString()));
            put(UnbanData.UnbanStatus.class, obj -> UnbanData.UnbanStatus.valueOf(obj.toString()));
            put(UnbanData.UnbanDecision.class, obj -> UnbanData.UnbanDecision.valueOf(obj.toString()));
            put(BugsData.BugStatus.class, obj -> BugsData.BugStatus.valueOf(obj.toString()));
        }};
    }

    public List<Bug> getRecentBugs(String user) {
        return get(Bug.class, new HashMap<>() {{
            put("user", user);
            put(">timestamp", System.currentTimeMillis() - 30 * 60 * 1000L);
        }});
    }

    public List<UnbanRequest> getRecentUnbanRequests(String user) {
        return get(UnbanRequest.class, new HashMap<>() {{
            put("user", user);
            put(">timestamp", System.currentTimeMillis() - 7 * 25 * 60 * 60 * 1000L);
        }});
    }

    public List<Complain> getRecentComplaints(String user) {
        return get(Complain.class, new HashMap<>() {{
            put("user", user);
            put(">timestamp", System.currentTimeMillis() - 60 * 60 * 1000L);
        }});
    }


}
