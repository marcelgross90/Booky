package rocks.marcelgross.booky.entities;

import java.util.Arrays;
import java.util.List;

public class Book {

    private long id;
    private String title;
    private String isbn;
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
        if (industryIdentifiers != null && industryIdentifiers.size() > 0) {
            if (industryIdentifiers.size() == 1) {
                this.isbn = industryIdentifiers.get(0).getIdentifier();
            } else {
                for (BookDto.IndustryIdentifiers industryIdentifier : industryIdentifiers) {
                    if (industryIdentifier.getType().equals("ISBN_13")) {
                        this.isbn = industryIdentifier.getIdentifier();
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

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        if (id != book.id) return false;
        if (pageCount != book.pageCount) return false;
        if (!title.equals(book.title)) return false;
        if (isbn != null ? !isbn.equals(book.isbn) : book.isbn != null) return false;
        if (subtitle != null ? !subtitle.equals(book.subtitle) : book.subtitle != null)
            return false;
        if (authors != null ? !authors.equals(book.authors) : book.authors != null) return false;
        if (publishedDate != null ? !publishedDate.equals(book.publishedDate) : book.publishedDate != null)
            return false;
        return thumbnail != null ? thumbnail.equals(book.thumbnail) : book.thumbnail == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + title.hashCode();
        result = 31 * result + (isbn != null ? isbn.hashCode() : 0);
        result = 31 * result + (subtitle != null ? subtitle.hashCode() : 0);
        result = 31 * result + (authors != null ? authors.hashCode() : 0);
        result = 31 * result + (publishedDate != null ? publishedDate.hashCode() : 0);
        result = 31 * result + pageCount;
        result = 31 * result + (thumbnail != null ? thumbnail.hashCode() : 0);
        return result;
    }
}
