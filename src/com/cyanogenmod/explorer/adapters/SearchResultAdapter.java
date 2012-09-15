/*
 * Copyright (C) 2012 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyanogenmod.explorer.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cyanogenmod.explorer.R;
import com.cyanogenmod.explorer.model.Query;
import com.cyanogenmod.explorer.model.SearchResult;
import com.cyanogenmod.explorer.ui.IconHolder;
import com.cyanogenmod.explorer.ui.widgets.RelevanceWidget;
import com.cyanogenmod.explorer.util.MimeTypeHelper;
import com.cyanogenmod.explorer.util.SearchHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of {@link ArrayAdapter} for display search results.
 */
public class SearchResultAdapter extends ArrayAdapter<SearchResult> implements OnClickListener {

    /**
     * A class that conforms with the ViewHolder pattern to performance
     * the list view rendering.
     */
    private static class ViewHolder {
        /**
         * @hide
         */
        public ViewHolder() {
            super();
        }
        ImageView mIvIcon;
        TextView mTvName;
        TextView mTvParentDir;
        ImageButton mBtMenu;
        RelevanceWidget mWgRelevance;
    }

    /**
     * A class that holds the full data information.
     */
    private static class DataHolder {
        /**
         * @hide
         */
        public DataHolder() {
            super();
        }
        Drawable mDwIcon;
        CharSequence mName;
        String mParentDir;
        float mRelevance;
    }




    private DataHolder[] mData;
    private IconHolder mIconHolder;
    private final int mItemViewResourceId;

    private final List<String> mQueries;

    //The resource of the item icon
    private static final int RESOURCE_ITEM_ICON = R.id.search_item_icon;
    //The resource of the item name
    private static final int RESOURCE_ITEM_NAME = R.id.search_item_name;
    //The resource of the item path
    private static final int RESOURCE_ITEM_PARENT_DIR = R.id.search_item_parent_dir;
    //The resource of the item check
    private static final int RESOURCE_ITEM_MENU = R.id.search_item_menu;
    //The resource of the item relevance
    private static final int RESOURCE_ITEM_RELEVANCE = R.id.search_item_relevance;

    /**
     * Constructor of <code>SearchResultAdapter</code>.
     *
     * @param context The current context
     * @param files The list of file system objects
     * @param itemViewResourceId The identifier of the layout that represents an item
     * of the list adapter
     * @param queries The query object used to make the result of this search
     */
    public SearchResultAdapter(
            Context context, List<SearchResult> files, int itemViewResourceId, Query queries) {
        super(context, RESOURCE_ITEM_NAME, files);
        this.mIconHolder = new IconHolder();
        this.mItemViewResourceId = itemViewResourceId;
        this.mQueries = queries.getQueries();

        //Do cache of the data for better performance
        loadDefaultIcons();
        processData();
    }

    /**
     * Method that loads the default icons (known icons and more common icons).
     */
    private void loadDefaultIcons() {
        this.mIconHolder.getDrawable(getContext(), R.drawable.ic_fso_default);
        this.mIconHolder.getDrawable(getContext(), R.drawable.ic_fso_folder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyDataSetChanged() {
        processData();
        super.notifyDataSetChanged();
    }

    /**
     * Method that dispose the elements of the adapter.
     */
    public void dispose() {
        clear();
        this.mData = null;
        this.mIconHolder = null;
    }

    /**
     * Method that process the data before use {@link #getView} method.
     */
    private void processData() {
        this.mData = new DataHolder[getCount()];
        for (int i = 0; i < getCount(); i++) {
            //File system object info
            SearchResult result = getItem(i);

            //Build the data holder
            this.mData[i] = new SearchResultAdapter.DataHolder();
            this.mData[i].mDwIcon =
                    this.mIconHolder.getDrawable(
                            getContext(), MimeTypeHelper.getIcon(getContext(), result.getFso()));
            this.mData[i].mName = SearchHelper.getHighlightedName(result, this.mQueries);
            this.mData[i].mParentDir = new File(result.getFso().getFullPath()).getParent();
            this.mData[i].mRelevance =
                    (float)((result.getRelevance() * 100) / SearchResult.MAX_RELEVANCE);
        }
    }

    /**
     * Method that returns the data of the adapter.
     *
     * @return List<SearchResult> The adapter data
     */
    public List<SearchResult> getData() {
        final List<SearchResult> data = new ArrayList<SearchResult>(getCount());
        for (int i = 0; i < getCount(); i++) {
            data.add(getItem(i));
        }
        return data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Check to reuse view
        View v = convertView;
        if (v == null) {
            //Create the view holder
            LayoutInflater li =
                    (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(this.mItemViewResourceId, parent, false);
            ViewHolder viewHolder = new SearchResultAdapter.ViewHolder();
            viewHolder.mIvIcon = (ImageView)v.findViewById(RESOURCE_ITEM_ICON);
            viewHolder.mTvName = (TextView)v.findViewById(RESOURCE_ITEM_NAME);
            viewHolder.mTvParentDir = (TextView)v.findViewById(RESOURCE_ITEM_PARENT_DIR);
            viewHolder.mBtMenu = (ImageButton)v.findViewById(RESOURCE_ITEM_MENU);
            viewHolder.mBtMenu.setOnClickListener(this);
            viewHolder.mWgRelevance = (RelevanceWidget)v.findViewById(RESOURCE_ITEM_RELEVANCE);
            v.setTag(viewHolder);
        }

        //Retrieve data holder
        final DataHolder dataHolder = this.mData[position];

        //Retrieve the view holder
        ViewHolder viewHolder = (ViewHolder)v.getTag();

        //Set the data
        viewHolder.mIvIcon.setImageDrawable(dataHolder.mDwIcon);
        viewHolder.mTvName.setText(dataHolder.mName, TextView.BufferType.SPANNABLE);
        viewHolder.mTvParentDir.setText(dataHolder.mParentDir);
        viewHolder.mWgRelevance.setRelevance(dataHolder.mRelevance);

        //Return the view
        return v;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case RESOURCE_ITEM_MENU:
                break;
            default:
                break;
        }
    }


}