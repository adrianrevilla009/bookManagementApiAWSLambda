package masterCloudApps.api.bookManagementApiFunction.service;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import masterCloudApps.api.bookManagementApiFunction.model.Book;
import masterCloudApps.api.bookManagementApiFunction.model.Comment;
import masterCloudApps.api.bookManagementApiFunction.model.Userr;
import masterCloudApps.api.bookManagementApiFunction.repository.BookRepository;
import masterCloudApps.api.bookManagementApiFunction.repository.CommentRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentService {
    private final CommentRepository commentRepository = new CommentRepository();

    private final ObjectMapper mapper = new ObjectMapper();

    public APIGatewayProxyResponseEvent getAllComments() {

        try {
            List<Comment> commentList = commentRepository.getAllComments();
            String responseBody = commentList.toString();

            return createResponse(200, responseBody);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return createResponse(500, e.getMessage());
        }
    }

    // TODO not developed
    public APIGatewayProxyResponseEvent getComment(String id) {

        try {
            String s = commentRepository.getComment(id);
            return createResponse(200, s);
        } catch (Exception e) {
            System.out.println(e);
            return createResponse(500, e.getMessage());
        }
    }

    // TODO not developed
    public APIGatewayProxyResponseEvent addComment(String data) {

        try {
            String s = commentRepository.addComment(new Book());
            return createResponse(201, s);
        } catch (Exception e) {
            System.out.println(e);
            return createResponse(500, e.getMessage());
        }
    }

    // TODO not developed
    public APIGatewayProxyResponseEvent updateComment(String id, String data) {

        try {
            String s = commentRepository.updateComment(new Book());
            return createResponse(200, s);
        } catch (Exception e) {
            System.out.println(e);
            return createResponse(500, e.getMessage());
        }
    }

    // TODO not developed
    public APIGatewayProxyResponseEvent deleteComment(String id) {

        try {
            String s = commentRepository.deleteComment(id);
            return createResponse(200, s);
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
