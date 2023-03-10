package masterCloudApps.api.bookManagementApiFunction.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import masterCloudApps.api.bookManagementApiFunction.model.Book;
import masterCloudApps.api.bookManagementApiFunction.model.Comment;
import masterCloudApps.api.bookManagementApiFunction.model.Userr;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

public class BookRepository {
    private static final String TABLE_NAME = "appbooks";
    private final Table table;

    public BookRepository() {
        DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.defaultClient());
        table = dynamoDB.getTable(TABLE_NAME);
    }

    public ScanResult getAllBooks() {
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(TABLE_NAME);
        return AmazonDynamoDBClientBuilder.defaultClient().scan(scanRequest);
    }

    public Item getBook(String id) {
        return table.getItem("id", id);
    }

    public PutItemOutcome addBook(Book book) throws JSONException {
        JSONObject authorJson = Userr.getUserrJson(book);
        List<JSONObject> commentList = Comment.getCommentJsonList(book);

        Item item = new Item()
                .withPrimaryKey("id", UUID.randomUUID().toString())
                .withString("title", book.getTitle())
                .withString("resume", book.getResume())
                .withJSON("author", authorJson.toString())
                .withString("editorial", book.getEditorial())
                .withString("publicationDate", book.getPublicationDate().toString())
                .withList("commentList", commentList);

        return table.putItem(item);
    }

    public UpdateItemOutcome updateBook(Book book) throws JSONException {
        JSONObject authorJson = Userr.getUserrJson(book);
        List<JSONObject> commentList = Comment.getCommentJsonList(book);

        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey("id", book.getId())
                .withUpdateExpression("set title = :title, resume = :resume, author = :author, " +
                        "editorial = :editorial, publicationDate = :publicationDate, " +
                        "commentList = :commentList")
                .withValueMap(new ValueMap()
                        .withString(":title", book.getTitle())
                        .withString(":resume", book.getResume())
                        .withJSON(":author", authorJson.toString())
                        .withString(":editorial", book.getEditorial())
                        .withString(":publicationDate", book.getPublicationDate().toString())
                        .withList(":commentList", commentList))
                .withReturnValues("ALL_OLD");
        return table.updateItem(updateItemSpec);
    }

    public DeleteItemOutcome deleteBook(String id) {
        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey("id", id)
                //.withConditionExpression("userid = :userid")
                //.withValueMap(new ValueMap().withString(":userid", userid))
                .withReturnValues(ReturnValue.ALL_OLD);
        return table.deleteItem(deleteItemSpec);
    }
}

