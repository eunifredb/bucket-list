package com.example.bucketlist.util;

public interface OnItemSelectedListener {
    void onSignOutSelected();

    void onDetailSelected();

    void onSearchSelected();

    void hideBottomNavView();

    void unHideBottomNavView();
    void onCategorySelected(String category);


}
