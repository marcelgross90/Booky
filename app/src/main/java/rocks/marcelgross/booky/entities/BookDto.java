package rocks.marcelgross.booky.entities;


import java.util.ArrayList;
import java.util.List;



public class BookDto {

    private List<Items> items;
    private int totalItems;

    public List<Items> getItems() {
        return items;
    }

    @SuppressWarnings("unused")
    public void setItems(List<Items> items) {
        this.items = items;
    }

    public int getTotalItems() {
        return totalItems;
    }

    @SuppressWarnings("unused")
    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public class Items {
        private VolumeInfo volumeInfo;

        public VolumeInfo getVolumeInfo() {
            return volumeInfo;
        }

        @SuppressWarnings("unused")
        public void setVolumeInfo(VolumeInfo volumeInfo) {
            this.volumeInfo = volumeInfo;
        }
    }

    public class VolumeInfo {
        private String title;
        private String subtitle;
        private final List<String> authors = new ArrayList<>();
        private String publishedDate;
        private int pageCount;
        private ImageLinks imageLinks;
        private final List<IndustryIdentifiers> industryIdentifiers = new ArrayList<>();

        public String getTitle() {
            return title;
        }

        @SuppressWarnings("unused")
        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubtitle() {
            return subtitle;
        }

        @SuppressWarnings("unused")
        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }

        public List<String> getAuthors() {
            return authors;
        }

        @SuppressWarnings("unused")
        public void setAuthors(List<String> authors) {
            this.authors.addAll(authors);
        }

        public String getPublishedDate() {
            return publishedDate;
        }

        @SuppressWarnings("unused")
        public void setPublishedDate(String publishedDate) {
            this.publishedDate = publishedDate;
        }

        public int getPageCount() {
            return pageCount;
        }

        @SuppressWarnings("unused")
        public void setPageCount(int pageCount) {
            this.pageCount = pageCount;
        }

        public ImageLinks getImageLinks() {
            return imageLinks;
        }

        @SuppressWarnings("unused")
        public void setImageLinks(ImageLinks imageLinks) {
            this.imageLinks = imageLinks;
        }

        public List<IndustryIdentifiers> getIndustryIdentifiers() {
            return industryIdentifiers;
        }

        public void setIndustryIdentifiers(List<IndustryIdentifiers> industryIdentifiers) {
            this.industryIdentifiers.addAll(industryIdentifiers);
        }
    }

    public class IndustryIdentifiers {
        private String type;
        private String identifier;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }
    }

    public class ImageLinks {
        private String smallThumbnail;
        private String thumbnail;

        public String getSmallThumbnail() {
            return smallThumbnail;
        }

        @SuppressWarnings("unused")
        public void setSmallThumbnail(String smallThumbnail) {
            this.smallThumbnail = smallThumbnail;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        @SuppressWarnings("unused")
        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }
    }
}
