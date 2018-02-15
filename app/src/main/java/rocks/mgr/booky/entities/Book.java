package rocks.mgr.booky.entities;

import java.util.Arrays;
import java.util.List;

public class Book {

    private String title;
    private String ISNB;
    private String subtitle;
    private List<String> authors;
    private String publishedDate;
    private int pageCount;
    private String thumbnail;

    public Book() {
    }

    public Book(BookDto.Items item) {
        BookDto.VolumeInfo volumeInfo = item.getVolumeInfo();
        BookDto.ImageLinks imageLinks = volumeInfo.getImageLinks();
        if (volumeInfo != null) {
            this.title = volumeInfo.getTitle();
            this.subtitle = volumeInfo.getSubtitle();
            this.authors = volumeInfo.getAuthors();
            this.publishedDate = volumeInfo.getPublishedDate();
            this.pageCount = volumeInfo.getPageCount();
            if (imageLinks != null) {
                if (imageLinks.getThumbnail() == null)
                    this.thumbnail = imageLinks.getThumbnail();
                else
                    this.thumbnail = imageLinks.getSmallThumbnail();
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getISNB() {
        return ISNB;
    }

    public void setISNB(String ISNB) {
        this.ISNB = ISNB;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    private void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getConcatAuthors() {
        StringBuilder authorsCombined = new StringBuilder();

        for (int i = 0; i < authors.size(); i++) {
            authorsCombined.append(authors.get(0));
            if (i < authors.size() - 1)
                authorsCombined.append(", ");
        }

        return authorsCombined.toString();
    }

    public void extractAuthors(String authors) {
        String[] splitAuthors = authors.split(", ");
        setAuthors(Arrays.asList(splitAuthors));
    }
}
