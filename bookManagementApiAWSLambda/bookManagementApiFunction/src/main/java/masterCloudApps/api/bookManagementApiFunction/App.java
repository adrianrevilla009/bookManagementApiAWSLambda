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
import masterCloudApps.api.bookManagementApiFunction.service.BookService;
import masterCloudApps.api.bookManagementApiFunction.service.CommentService;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private BookService bookService = new BookService();

    private CommentService commentService = new CommentService();

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {

        if (event.getPath().contains("/books/")) {
            switch (event.getHttpMethod()) {
                case "GET":
                    if (event.getPathParameters() != null && event.getPathParameters().get("id") != null) {
                        return this.bookService.getBook(event.getPathParameters().get("id"));
                    }
                    return this.bookService.getAllBooks();
                case "POST":
                    return this.bookService.addBook(event.getBody());
                case "PUT":
                    return this.bookService.updateBook(event.getPathParameters().get("id"), event.getBody());
                case "DELETE":
                    return this.bookService.deleteBook(event.getPathParameters().get("id"));
                default:
                    return this.bookService.createResponse(400, "Unsupported method " + event.getHttpMethod());
            }
        }

        if (event.getPath().contains("/comments/")) {
            switch (event.getHttpMethod()) {
                case "GET":
                    if (event.getPathParameters() != null && event.getPathParameters().get("id") != null) {
                        return this.commentService.getComment(event.getPathParameters().get("id"));
                    }
                    return this.commentService.getAllComments();
                case "POST":
                    return this.commentService.addComment(event.getBody());
                case "PUT":
                    return this.commentService.updateComment(event.getPathParameters().get("id"), event.getBody());
                case "DELETE":
                    return this.commentService.deleteComment(event.getPathParameters().get("id"));
                default:
                    return this.commentService.createResponse(400, "Unsupported method " + event.getHttpMethod());
            }
        }

        return this.bookService.createResponse(400, "Not valid path " + event.getHttpMethod());
    }

}
