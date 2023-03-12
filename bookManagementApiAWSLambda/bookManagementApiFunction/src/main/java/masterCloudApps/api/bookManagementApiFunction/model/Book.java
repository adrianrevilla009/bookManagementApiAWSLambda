package masterCloudApps.api.bookManagementApiFunction.model;

import java.time.LocalDate;
import java.util.List;

public class Book {
    private String id;
    private String title;
    private String resume;
    private Userr author;
    private String editorial;
    private LocalDate publicationDate;
    private List<Comment> commentList;

    public Book() {}

    public Book(String title, String resume, Userr author, String editorial, LocalDate publicationDate) {
        this.title = title;
        this.resume = resume;
        this.author = author;
        this.editorial = editorial;
        this.publicationDate = publicationDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public Userr getAuthor() {
        return author;
    }

    public void setAuthor(Userr author) {
        this.author = author;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", resume='" + resume + '\'' +
                ", author=" + author +
                ", editorial='" + editorial + '\'' +
                ", publicationDate=" + publicationDate +
                '}';
    }
}
