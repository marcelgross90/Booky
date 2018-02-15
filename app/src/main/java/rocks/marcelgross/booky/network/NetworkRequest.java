package rocks.marcelgross.booky.network;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rocks.marcelgross.booky.entities.Book;
import rocks.marcelgross.booky.entities.BookDto;

public class NetworkRequest {
    public interface OnResultListener {
        void onResultListener(BookRequest.ResponseObject responseObject);
    }

    private static final String host = "https://www.googleapis.com/books/v1/volumes?maxResults=40";

    private static final int MAXIMUM_RESPONSE_SIZE = 1048576;

    public void searchISBNAsync(final String isbn, final int startIndex, final OnResultListener listener) {
        String isbnEndpoint = "&q=isbn:" + isbn;
        isbnEndpoint += "&startIndex=" + startIndex;
        new BookRequest(listener).execute(isbnEndpoint);
    }

    public void searchTitleAsync(final String title, final int startIndex, final OnResultListener listener) {
        String titleEndpoint = "&q=title:" + title.replaceAll(" ", "+");
        titleEndpoint += "&startIndex=" + startIndex;
        new BookRequest(listener).execute(titleEndpoint);

    }

    public static class BookRequest extends AsyncTask<String, Void, BookRequest.Response> {

        final OnResultListener listener;

        BookRequest(OnResultListener listener) {
            this.listener = listener;
        }

        @Override
        protected Response doInBackground(String... params) {
            HttpURLConnection connection = null;

            try {
                connection = openConnection(params[0]);

                return new Response(
                        connection.getResponseCode(),
                        readResponse(connection.getInputStream())
                );
            } catch (IOException e) {
                // fall through
            } finally {
                if (connection != null)
                    connection.disconnect();
            }

            return new Response(HttpURLConnection.HTTP_INTERNAL_ERROR, null);
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            listener.onResultListener(response.getResponseObject());
        }


        private HttpURLConnection openConnection(String endpoint) throws IOException {
            HttpURLConnection connection =
                    (HttpURLConnection) new URL(host + endpoint).openConnection();
            connection.setRequestMethod("GET");

            return connection;
        }

        private byte[] readResponse(InputStream in) throws IOException {
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];

            int length = 0;
            int bytes = in.read(buffer);

            while (bytes > -1) {
                if (bytes > 0) {
                    data.write(buffer, 0, bytes);
                    length += bytes;

                    if (length > MAXIMUM_RESPONSE_SIZE)
                        return null;
                }
                bytes = in.read(buffer);
            }

            return data.toByteArray();
        }


        public class Response {
            private final Gson gson = new Gson();
            private final int code;
            private final byte data[];

            Response(int code, byte data[]) {
                this.code = code;
                this.data = data;
            }

            ResponseObject getResponseObject() {
                if (successfulRequest(code)) {
                    List<Book> books = new ArrayList<>();
                    BookDto dto = gson.fromJson(getString(), BookDto.class);
                    for (BookDto.Items items : dto.getItems()) {
                        books.add(new Book(items));
                    }
                    return new ResponseObject(books, dto.getTotalItems());

                }
                return new ResponseObject(Collections.<Book>emptyList(), 0);
            }

            public String getString() {
                try {
                    return new String(data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
            }

            private boolean successfulRequest(int statusCode) {
                return statusCode >= HttpURLConnection.HTTP_OK && statusCode < HttpURLConnection.HTTP_MULT_CHOICE;
            }
        }

        public class ResponseObject {
            private final List<Book> books = new ArrayList<>();
            private int totalItems;

            public ResponseObject(List<Book> books, int totalItems) {
                this.books.addAll(books);
                this.totalItems = totalItems;
            }

            public List<Book> getBooks() {
                return books;
            }

            public int getTotalItems() {
                return totalItems;
            }
        }
    }
}
