package masterCloudApps.api.bookManagementApiFunction.service;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import masterCloudApps.api.bookManagementApiFunction.model.Book;
import masterCloudApps.api.bookManagementApiFunction.model.Comment;
import masterCloudApps.api.bookManagementApiFunction.model.Userr;
import masterCloudApps.api.bookManagementApiFunction.repository.BookRepository;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookService {
    private final BookRepository bookRepository = new BookRepository();

    private final ObjectMapper mapper = new ObjectMapper();

    public APIGatewayProxyResponseEvent getAllBooks() {

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

    public APIGatewayProxyResponseEvent getBook(String id) {

        try {
            Item res = bookRepository.getBook(id);

            String responseBody = res.toString();

            return createResponse(200, responseBody);
        } catch (Exception e) {
            System.out.println(e);
            return createResponse(500, e.getMessage());
        }
    }

    public APIGatewayProxyResponseEvent addBook(String data) {

        try {

            Map<String,Object> book = mapper.readValue(data, new TypeReference<HashMap<String, Object>>() {});

            Gson g = new Gson();
            Userr author = g.fromJson(book.get("author").toString(), Userr.class);

            Book saveBook = new Book(
                    (String)book.get("title"),
                    (String)book.get("resume"),
                    author,
                    (String)book.get("editorial"),
                    LocalDate.parse((String)book.get("publicationDate"))
            );

            Type listType = new TypeToken<ArrayList<Comment>>() {}.getType();
            List<Comment> comments = new Gson().fromJson(book.get("commentList").toString(), listType);

            saveBook.setCommentList(comments);

            PutItemOutcome res = bookRepository.addBook(saveBook);

            // TODO uncomment String id = (String) res.getItem().get("id");
            String id = "This is not returning anything";

            String responseBody = mapper.writeValueAsString(id);

            return createResponse(201, responseBody);
        } catch (Exception e) {
            System.out.println(e);
            return createResponse(500, e.getMessage());
        }
    }

    public APIGatewayProxyResponseEvent updateBook(String id, String data) {

        try {

            Map<String,Object> book = mapper.readValue(data, new TypeReference<HashMap<String, Object>>() {});

            Gson g = new Gson();
            Userr author = g.fromJson(book.get("author").toString(), Userr.class);

            Book updateBook = new Book(
                    (String)book.get("title"),
                    (String)book.get("resume"),
                    author,
                    (String)book.get("editorial"),
                    LocalDate.parse((String)book.get("publicationDate"))
            );
            updateBook.setId(id);

            Type listType = new TypeToken<ArrayList<Comment>>() {}.getType();
            List<Comment> comments = new Gson().fromJson(book.get("commentList").toString(), listType);

            updateBook.setCommentList(comments);

            UpdateItemOutcome res = bookRepository.updateBook(updateBook);

            // TODO uncomment String responseBody = mapper.writeValueAsString(res.getUpdateItemResult().getAttributes());
            String responseBody = updateBook.toString();
            return createResponse(200, responseBody);
        } catch (Exception e) {
            System.out.println(e);
            return createResponse(500, e.getMessage());
        }
    }

    public APIGatewayProxyResponseEvent deleteBook(String id) {

        try {
            DeleteItemOutcome res = bookRepository.deleteBook(id);
            String responseBody = mapper.writeValueAsString(res.getDeleteItemResult());
            return createResponse(200, responseBody);
        } catch (Exception e) {
            System.out.println(e);
            return createResponse(500, e.getMessage());
        }
    }

    public APIGatewayProxyResponseEvent createResponse(int statusCode, String responseBody) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(statusCode);
        response.setBody(responseBody);
        return response;
    }
}
