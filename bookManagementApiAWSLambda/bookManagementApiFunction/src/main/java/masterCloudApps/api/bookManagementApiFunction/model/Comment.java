package masterCloudApps.api.bookManagementApiFunction.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Comment {
    private String id;
    private Userr author;
    private String comment;
    private int points;

    public Comment() {}

    public Comment(Userr author, String comment, int points) {
        this.author = author;
        this.comment = comment;
        this.points = points;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Userr getAuthor() {
        return author;
    }

    public void setAuthor(Userr author) {
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public static List<JSONObject> getCommentJsonList(Book book) throws JSONException {
        List<JSONObject> commentList = new ArrayList<>();
        for (Comment comment : book.getCommentList()) {
            JSONObject commentJson = new JSONObject();
            commentJson.put("id", UUID.randomUUID().toString());
            commentJson.put("author", Userr.getUserrJson(book));
            commentJson.put("comment", comment.getComment());
            commentJson.put("points", comment.getPoints());
            commentList.add(commentJson);
        }

        return commentList;
    }
}
