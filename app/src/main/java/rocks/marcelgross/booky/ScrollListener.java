package rocks.marcelgross.booky;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class ScrollListener extends RecyclerView.OnScrollListener {


    public interface OnScrollListener {
        void loadNextBooks();
    }

    //offset when to trigger load
    @SuppressWarnings("FieldCanBeLocal")
    private final int offset = 1;

    private final LinearLayoutManager layoutManager;
    private final OnScrollListener listener;

    public ScrollListener(LinearLayoutManager layoutManager, OnScrollListener listener) {
        this.layoutManager = layoutManager;
        this.listener = listener;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int visibleItems = recyclerView.getChildCount();
        int totalItems = layoutManager.getChildCount();
        int firstVisible = layoutManager.findFirstVisibleItemPosition();

        if (totalItems - visibleItems <= firstVisible + offset) {
            listener.loadNextBooks();
        }
    }
}
