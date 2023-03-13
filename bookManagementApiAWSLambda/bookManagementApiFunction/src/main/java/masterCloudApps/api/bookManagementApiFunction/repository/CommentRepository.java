package masterCloudApps.api.bookManagementApiFunction.repository;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import masterCloudApps.api.bookManagementApiFunction.model.Book;
import masterCloudApps.api.bookManagementApiFunction.model.Comment;
import masterCloudApps.api.bookManagementApiFunction.model.Userr;
import masterCloudApps.api.bookManagementApiFunction.service.BookService;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.*;

public class CommentRepository {
    private static final String TABLE_NAME = "appbooks";
    private final Table table;
    private AmazonDynamoDB amazonDynamoDB;

    private DynamoDB dynamoDB;

    private BookRepository bookRepository = new BookRepository();

    public CommentRepository() {
        // dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.defaultClient());

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

    public List<Comment> getAllComments() {
        ScanResult res = bookRepository.getAllBooks();

        List<Item> itemList = ItemUtils.toItemList(res.getItems());
        List<Comment> commentList = new ArrayList<>();

        for (Item item : itemList) {
            Type listType = new TypeToken<ArrayList<Comment>>() {}.getType();
            List<Comment> comments = new Gson().fromJson(item.get("commentList").toString(), listType);
            commentList.addAll(comments);
        }
        return commentList;
    }

    // TODO not developed
    public String getComment(String id) {
        return "";
    }

    // TODO not developed
    public String addComment(Book book) throws JSONException {
        return "";
    }

    // TODO not developed
    public String updateComment(Book book) {
        return "";
    }

    // TODO not developed
    public String deleteComment(String id) {
        return "";
    }
}
