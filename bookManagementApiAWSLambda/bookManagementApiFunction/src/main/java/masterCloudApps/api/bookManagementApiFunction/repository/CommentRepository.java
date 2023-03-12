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
            // TODO why when .add is returning null?
            for (Comment comment : comments) {
                commentList.add(comment);
            }
            // commentList.addAll(comments);
        }

        /*ScanResult scanResult = new ScanResult();
        List<Map<String, AttributeValue>> maps = new ArrayList<>();
        Map hashMap = new HashMap();
        hashMap.put("commentList", commentList);

        scanResult.setItems(maps);
        scanResult.setCount(commentList.size());*/
        return commentList;
    }

    public Item getComment(String id) {
        return table.getItem("id", id);
    }

    public PutItemOutcome addComment(Book book) throws JSONException {
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

    public UpdateItemOutcome updateComment(Book book) throws JSONException {
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

    public DeleteItemOutcome deleteComment(String id) {
        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey("id", id)
                //.withConditionExpression("userid = :userid")
                //.withValueMap(new ValueMap().withString(":userid", userid))
                .withReturnValues(ReturnValue.ALL_OLD);
        return table.deleteItem(deleteItemSpec);
    }
}
