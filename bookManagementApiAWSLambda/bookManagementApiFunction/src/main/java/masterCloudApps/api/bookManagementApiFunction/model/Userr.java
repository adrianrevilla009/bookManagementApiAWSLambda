package masterCloudApps.api.bookManagementApiFunction.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class Userr {
    private String id;
    private String nickname;
    private String email;

    public Userr() {}

    public Userr(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static JSONObject getUserrJson(Book book) throws JSONException {
        JSONObject userrJson = new JSONObject();
        userrJson.put("id", UUID.randomUUID().toString());
        userrJson.put("nickname", book.getAuthor().getNickname());
        userrJson.put("email", book.getAuthor().getEmail());

        return userrJson;
    }
}
