package link.wizapp.booru4diffusion.model;


import java.sql.Timestamp;
import java.util.Date;

public class Image {

    private long id;
    private long userId;
    private String title;
    private String description;
    private String url;
    private Timestamp timestampCreated;
    private Timestamp timestampUpdated;
    private boolean published;

    public Image() {

    }

//    public Image(long id, String title, String description, boolean published) {
//        this.id = id;
//        this.title = title;
//        this.description = description;
//        this.published = published;
//    }

//    public Image(String title, String description, boolean published) {
//        this.title = title;
//        this.description = description;
//        this.published = published;
//    }

    public Image(long userId, String title, String description, String url, boolean published){
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.url = url;
        this.published = published;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean isPublished) {
        this.published = isPublished;
    }

    @Override
    public String toString() {
        return "Image [id=" + id + ", title=" + title + ", desc=" + description + ", published=" + published + "]";
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Timestamp getTimestampCreated() {
        return timestampCreated;
    }

    public void setTimestampCreated(Timestamp timestampCreated) {
        this.timestampCreated = timestampCreated;
    }

    public Timestamp getTimestampUpdated() {
        return timestampUpdated;
    }

    public void setTimestampUpdated(Timestamp timestampUpdated) {
        this.timestampUpdated = timestampUpdated;
    }
}