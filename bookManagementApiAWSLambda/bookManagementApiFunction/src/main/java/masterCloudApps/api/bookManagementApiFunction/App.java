package masterCloudApps.api.bookManagementApiFunction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import masterCloudApps.api.bookManagementApiFunction.model.Book;
import masterCloudApps.api.bookManagementApiFunction.model.Comment;
import masterCloudApps.api.bookManagementApiFunction.model.Userr;
import masterCloudApps.api.bookManagementApiFunction.repository.BookRepository;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final BookRepository bookRepository = new BookRepository();

    private final ObjectMapper mapper = new ObjectMapper();

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {

        switch (event.getHttpMethod()) {
            case "GET":
                if (event.getPathParameters().get("id") != null) {
                    return getBook(event.getPathParameters().get("id"));
                }
                return getAllBooks();
            case "POST":
                return addBook(event.getBody());
            case "PUT":
                return updateBook(event.getPathParameters().get("id"), event.getBody());
            case "DELETE":
                return deleteBook(event.getPathParameters().get("id"));
            default:
                return createResponse(400, "Unsupported method " + event.getHttpMethod());
        }
    }

    private APIGatewayProxyResponseEvent getAllBooks() {

        try {
            ScanResult res = bookRepository.getAllBooks();

            List<Item> itemList = ItemUtils.toItemList(res.getItems());

            List<Map<String,Object>> values = new ArrayList<>();
            for(Item item: itemList){

                var value = new HashMap<String,Object>();
                value.put("id", item.get("id"));
                value.put("title", item.get("title"));
                value.put("resume", item.get("resume"));
                value.put("author", item.get("author"));
                value.put("editorial", item.get("editorial"));
                value.put("publicationDate", item.get("publicationDate"));
                value.put("commentList", item.get("commentList"));
                values.add(value);
            }

            String responseBody = mapper.writeValueAsString(values);
            return createResponse(200, responseBody);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return createResponse(500, e.getMessage());
        }
    }

    private APIGatewayProxyResponseEvent getBook(String id) {

        try {
            Item res = bookRepository.getBook(id);

            String responseBody = mapper.writeValueAsString(res);

            return createResponse(200, responseBody);
        } catch (Exception e) {
            System.out.println(e);
            return createResponse(500, e.getMessage());
        }
    }

    private APIGatewayProxyResponseEvent addBook(String data) {

        try {

            Map<String,Object> book = mapper.readValue(data, new TypeReference<HashMap<String, Object>>() {});

            Book saveBook = new Book(
                    (String)book.get("title"),
                    (String)book.get("resume"),
                    (Userr)book.get("author"),
                    (String)book.get("editorial"),
                    LocalDate.parse((String)book.get("publicationDate"))
            );
            saveBook.setCommentList((List<Comment>) book.get("commentList"));

            PutItemOutcome res = bookRepository.addBook(saveBook);

            String id = (String) res.getItem().get("id");

            String responseBody = mapper.writeValueAsString(id);

            return createResponse(201, responseBody);
        } catch (Exception e) {
            System.out.println(e);
            return createResponse(500, e.getMessage());
        }
    }

    private APIGatewayProxyResponseEvent updateBook(String id, String data) {

        try {

            Map<String,Object> book = mapper.readValue(data, new TypeReference<HashMap<String, Object>>() {});

            Book updateBook = new Book(
                    (String)book.get("title"),
                    (String)book.get("resume"),
                    (Userr)book.get("author"),
                    (String)book.get("editorial"),
                    LocalDate.parse((String)book.get("publicationDate"))
            );
            updateBook.setId((String)book.get("id"));
            updateBook.setCommentList((List<Comment>) book.get("commentList"));

            UpdateItemOutcome res = bookRepository.updateBook(updateBook);

            String responseBody = mapper.writeValueAsString(res.getUpdateItemResult().getAttributes());
            return createResponse(200, responseBody);
        } catch (Exception e) {
            System.out.println(e);
            return createResponse(500, e.getMessage());
        }
    }

    private APIGatewayProxyResponseEvent deleteBook(String id) {

        try {
            DeleteItemOutcome res = bookRepository.deleteBook(id);
            String responseBody = mapper.writeValueAsString(res.getDeleteItemResult());
            return createResponse(200, responseBody);
        } catch (Exception e) {
            System.out.println(e);
            return createResponse(500, e.getMessage());
        }
    }

    private APIGatewayProxyResponseEvent createResponse(int statusCode, String responseBody) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(statusCode);
        response.setBody(responseBody);
        return response;
    }
}
