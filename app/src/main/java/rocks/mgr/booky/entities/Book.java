package rocks.mgr.booky.entities;

import java.util.Arrays;
import java.util.List;

public class Book {

    private long id;
    private String title;
    private String isnb;
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
        List<BookDto.IndustryIdentifiers> industryIdentifiers = volumeInfo.getIndustryIdentifiers();
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
        if (industryIdentifiers.size() > 0) {
            if (industryIdentifiers.size() == 1) {
                this.isnb = industryIdentifiers.get(0).getIdentifier();
            } else {
                for (BookDto.IndustryIdentifiers industryIdentifier : industryIdentifiers) {
                    if (industryIdentifier.getType().equals("ISBN_13")) {
                        this.isnb = industryIdentifier.getIdentifier();
                        break;
                    }
                }
            }
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsnb() {
        return isnb;
    }

    public void setIsnb(String isnb) {
        this.isnb = isnb;
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

        if (authors == null) {
            return "";
        }

        for (int i = 0; i < authors.size(); i++) {
            authorsCombined.append(authors.get(i));
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
