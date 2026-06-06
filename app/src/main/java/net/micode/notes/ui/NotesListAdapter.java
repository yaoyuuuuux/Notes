///*
// * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package net.micode.notes.ui;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CursorAdapter;
//
//import net.micode.notes.data.Notes;
//
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//
//
//public class NotesListAdapter extends CursorAdapter {
//    private static final String TAG = "NotesListAdapter";
//    private Context mContext;
//    private HashMap<Integer, Boolean> mSelectedIndex;
//    private int mNotesCount;
//    private boolean mChoiceMode;
//
//    public static class AppWidgetAttribute {
//        public int widgetId;
//        public int widgetType;
//    };
//
//    public NotesListAdapter(Context context) {
//        super(context, null);
//        mSelectedIndex = new HashMap<Integer, Boolean>();
//        mContext = context;
//        mNotesCount = 0;
//    }
//
//    @Override
//    public View newView(Context context, Cursor cursor, ViewGroup parent) {
//        return new NotesListItem(context);
//    }
//
//    @Override
//    public void bindView(View view, Context context, Cursor cursor) {
//        if (view instanceof NotesListItem) {
//            NoteItemData itemData = new NoteItemData(context, cursor);
//            ((NotesListItem) view).bind(context, itemData, mChoiceMode,
//                    isSelectedItem(cursor.getPosition()));
//        }
//    }
//
//    public void setCheckedItem(final int position, final boolean checked) {
//        mSelectedIndex.put(position, checked);
//        notifyDataSetChanged();
//    }
//
//    public boolean isInChoiceMode() {
//        return mChoiceMode;
//    }
//
//    public void setChoiceMode(boolean mode) {
//        mSelectedIndex.clear();
//        mChoiceMode = mode;
//    }
//
//    public void selectAll(boolean checked) {
//        Cursor cursor = getCursor();
//        for (int i = 0; i < getCount(); i++) {
//            if (cursor.moveToPosition(i)) {
//                if (NoteItemData.getNoteType(cursor) == Notes.TYPE_NOTE) {
//                    setCheckedItem(i, checked);
//                }
//            }
//        }
//    }
//
//    public HashSet<Long> getSelectedItemIds() {
//        HashSet<Long> itemSet = new HashSet<Long>();
//        for (Integer position : mSelectedIndex.keySet()) {
//            if (mSelectedIndex.get(position) == true) {
//                Long id = getItemId(position);
//                if (id == Notes.ID_ROOT_FOLDER) {
//                    Log.d(TAG, "Wrong item id, should not happen");
//                } else {
//                    itemSet.add(id);
//                }
//            }
//        }
//
//        return itemSet;
//    }
//
//    public HashSet<AppWidgetAttribute> getSelectedWidget() {
//        HashSet<AppWidgetAttribute> itemSet = new HashSet<AppWidgetAttribute>();
//        for (Integer position : mSelectedIndex.keySet()) {
//            if (mSelectedIndex.get(position) == true) {
//                Cursor c = (Cursor) getItem(position);
//                if (c != null) {
//                    AppWidgetAttribute widget = new AppWidgetAttribute();
//                    NoteItemData item = new NoteItemData(mContext, c);
//                    widget.widgetId = item.getWidgetId();
//                    widget.widgetType = item.getWidgetType();
//                    itemSet.add(widget);
//                    /**
//                     * Don't close cursor here, only the adapter could close it
//                     */
//                } else {
//                    Log.e(TAG, "Invalid cursor");
//                    return null;
//                }
//            }
//        }
//        return itemSet;
//    }
//
//    public int getSelectedCount() {
//        Collection<Boolean> values = mSelectedIndex.values();
//        if (null == values) {
//            return 0;
//        }
//        Iterator<Boolean> iter = values.iterator();
//        int count = 0;
//        while (iter.hasNext()) {
//            if (true == iter.next()) {
//                count++;
//            }
//        }
//        return count;
//    }
//
//    public boolean isAllSelected() {
//        int checkedCount = getSelectedCount();
//        return (checkedCount != 0 && checkedCount == mNotesCount);
//    }
//
//    public boolean isSelectedItem(final int position) {
//        if (null == mSelectedIndex.get(position)) {
//            return false;
//        }
//        return mSelectedIndex.get(position);
//    }
//
//    @Override
//    protected void onContentChanged() {
//        super.onContentChanged();
//        calcNotesCount();
//    }
//
//    @Override
//    public void changeCursor(Cursor cursor) {
//        super.changeCursor(cursor);
//        calcNotesCount();
//    }
//
//    private void calcNotesCount() {
//        mNotesCount = 0;
//        for (int i = 0; i < getCount(); i++) {
//            Cursor c = (Cursor) getItem(i);
//            if (c != null) {
//                if (NoteItemData.getNoteType(c) == Notes.TYPE_NOTE) {
//                    mNotesCount++;
//                }
//            } else {
//                Log.e(TAG, "Invalid cursor");
//                return;
//            }
//        }
//    }
//}
/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.micode.notes.ui;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
// 【优化】添加SparseBooleanArray导入，替代HashMap<Integer, Boolean>节省内存
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import net.micode.notes.data.Notes;

import java.util.HashSet;


public class NotesListAdapter extends CursorAdapter {
    private static final String TAG = "NotesListAdapter";
    private Context mContext;
    // 【优化】使用SparseBooleanArray替代HashMap<Integer, Boolean>
    // 原代码：private HashMap<Integer, Boolean> mSelectedIndex;
    // 问题：HashMap使用Integer对象作为key，每个Integer对象占用16字节（对象头12字节+int值4字节）
    // 优化：SparseBooleanArray使用基本类型int数组，内存占用减少约50%
    private SparseBooleanArray mSelectedIndex;
    private int mNotesCount;
    private boolean mChoiceMode;

    public static class AppWidgetAttribute {
        public int widgetId;
        public int widgetType;
    };

    public NotesListAdapter(Context context) {
        super(context, null);
        // 【修改】初始化SparseBooleanArray
        mSelectedIndex = new SparseBooleanArray();
        mContext = context;
        mNotesCount = 0;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return new NotesListItem(context);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (view instanceof NotesListItem) {
            NoteItemData itemData = new NoteItemData(context, cursor);
            ((NotesListItem) view).bind(context, itemData, mChoiceMode,
                    isSelectedItem(cursor.getPosition()));
        }
    }

    public void setCheckedItem(final int position, final boolean checked) {
        mSelectedIndex.put(position, checked);
        notifyDataSetChanged();
    }

    public boolean isInChoiceMode() {
        return mChoiceMode;
    }

    public void setChoiceMode(boolean mode) {
        mSelectedIndex.clear();
        mChoiceMode = mode;
    }

    public void selectAll(boolean checked) {
        Cursor cursor = getCursor();
        for (int i = 0; i < getCount(); i++) {
            if (cursor.moveToPosition(i)) {
                if (NoteItemData.getNoteType(cursor) == Notes.TYPE_NOTE) {
                    setCheckedItem(i, checked);
                }
            }
        }
    }

    // 【优化】适配SparseBooleanArray的API
    // 原代码遍历HashMap.keySet()，现在遍历SparseBooleanArray
    public HashSet<Long> getSelectedItemIds() {
        HashSet<Long> itemSet = new HashSet<Long>();
        for (int i = 0; i < mSelectedIndex.size(); i++) {
            int position = mSelectedIndex.keyAt(i);
            if (mSelectedIndex.valueAt(i)) {
                Long id = getItemId(position);
                if (id == Notes.ID_ROOT_FOLDER) {
                    Log.d(TAG, "Wrong item id, should not happen");
                } else {
                    itemSet.add(id);
                }
            }
        }

        return itemSet;
    }

    // 【优化】适配SparseBooleanArray的API
    public HashSet<AppWidgetAttribute> getSelectedWidget() {
        HashSet<AppWidgetAttribute> itemSet = new HashSet<AppWidgetAttribute>();
        for (int i = 0; i < mSelectedIndex.size(); i++) {
            int position = mSelectedIndex.keyAt(i);
            if (mSelectedIndex.valueAt(i)) {
                Cursor c = (Cursor) getItem(position);
                if (c != null) {
                    AppWidgetAttribute widget = new AppWidgetAttribute();
                    NoteItemData item = new NoteItemData(mContext, c);
                    widget.widgetId = item.getWidgetId();
                    widget.widgetType = item.getWidgetType();
                    itemSet.add(widget);
                } else {
                    Log.e(TAG, "Invalid cursor");
                    return null;
                }
            }
        }
        return itemSet;
    }

    // 【优化】简化计数逻辑
    // 原代码使用Iterator遍历HashMap.values()，现在直接遍历数组
    public int getSelectedCount() {
        int count = 0;
        for (int i = 0; i < mSelectedIndex.size(); i++) {
            if (mSelectedIndex.valueAt(i)) {
                count++;
            }
        }
        return count;
    }

    public boolean isAllSelected() {
        int checkedCount = getSelectedCount();
        return (checkedCount != 0 && checkedCount == mNotesCount);
    }

    // 【优化】使用SparseBooleanArray的默认值功能
    // 原代码：if (null == mSelectedIndex.get(position)) return false;
    // 优化：直接返回，不存在时默认为false
    public boolean isSelectedItem(final int position) {
        return mSelectedIndex.get(position, false);
    }

    @Override
    protected void onContentChanged() {
        super.onContentChanged();
        calcNotesCount();
    }

    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
        calcNotesCount();
    }

    private void calcNotesCount() {
        mNotesCount = 0;
        for (int i = 0; i < getCount(); i++) {
            Cursor c = (Cursor) getItem(i);
            if (c != null) {
                if (NoteItemData.getNoteType(c) == Notes.TYPE_NOTE) {
                    mNotesCount++;
                }
            } else {
                Log.e(TAG, "Invalid cursor");
                return;
            }
        }
    }
}

