package masterCloudApps.api.bookManagementApiFunction.repository;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookRepository {
    private static final String TABLE_NAME = "appbooks";
    private final Table table;

    private AmazonDynamoDB amazonDynamoDB;

    private DynamoDB dynamoDB;

    public BookRepository() {
        String dynamoDbEndpoint = System.getenv("DYNAMODB_ENDPOINT");
        if (dynamoDbEndpoint != null && !dynamoDbEndpoint.equals("")) {
            System.out.println("Local DynamoDB");
            System.out.println("DYNAMODB_ENDPOINT="+dynamoDbEndpoint);
            amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
                    .withEndpointConfiguration(
                            new AwsClientBuilder.EndpointConfiguration(
                                    dynamoDbEndpoint, "us-east-1"))
                    .build();
        } else {
            System.out.println("AWS DynamoDB");
            amazonDynamoDB = AmazonDynamoDBClientBuilder.defaultClient();
        }

        dynamoDB = new DynamoDB(amazonDynamoDB);
        table = dynamoDB.getTable(TABLE_NAME);
    }

    public ScanResult getAllBooks() {
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(TABLE_NAME);
        return amazonDynamoDB.scan(scanRequest);
    }

    public Item getBook(String id) {
        GetItemSpec gio = new GetItemSpec()
                .withPrimaryKey("id", id);

        Item item = table.getItem(gio);
        return item;
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
                .withList("commentList", new ArrayList<>());
        // TODO save comments .withList("commentList", commentList)

        PutItemSpec putItemSpec = new PutItemSpec()
                .withItem(item)
                .withReturnValues(ReturnValue.ALL_OLD);

        return table.putItem(putItemSpec);
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
                        .withList(":commentList", new ArrayList<>())
                ) // TODO update comments .withList(":commentList", commentList)
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

