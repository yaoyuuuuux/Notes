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
 /*可以把这个 Notes.java 文件想象成一个表格的模板设计图：
Notes 类：就像是整个“便签管理系统”。
DataColumns 接口：就像是其中一张叫“细节表”的列名定义。
DATA1, DATA2, DATA3：你可以理解为桌子上的“抽屉 1”、“抽屉 2”、“抽屉 3”。
如果你往桌子上放一盏台灯（MIMETYPE = 台灯），那么“抽屉 1”里可能放的是“灯泡功率”。
如果你往桌子上放一个书架（MIMETYPE = 书架），那么“抽屉 1”里可能放的是“书籍数量”。
这种设计叫“通用表结构”，在 Android 系统（如联系人应用）中非常常见，初学者理解了这个，对以后看大型 Android 源码非常有帮助。
 */

package net.micode.notes.data;

import android.net.Uri;

//这是数据的“契约类”，定义了数据库长什么样

public class Notes {
    public static final String AUTHORITY = "micode_notes";//唯一的标识符，用于跨应用访问数据
    public static final String TAG = "Notes";
    public static final int TYPE_NOTE     = 0;
    public static final int TYPE_FOLDER   = 1;
    public static final int TYPE_SYSTEM   = 2;
//{@link #xxx}这类注释在java中表示“链接到名为xxx的变量”.
    /**
     * Following IDs are system folders' identifiers
     * {@link Notes#ID_ROOT_FOLDER } is default folder
     * {@link Notes#ID_TEMPARAY_FOLDER } is for notes belonging no folder
     * {@link Notes#ID_CALL_RECORD_FOLDER} is to store call records
     */
    public static final int ID_ROOT_FOLDER = 0;
    public static final int ID_TEMPARAY_FOLDER = -1;
    public static final int ID_CALL_RECORD_FOLDER = -2;
    public static final int ID_TRASH_FOLER = -3;

    public static final String INTENT_EXTRA_ALERT_DATE = "net.micode.notes.alert_date";
    public static final String INTENT_EXTRA_BACKGROUND_ID = "net.micode.notes.background_color_id";
    public static final String INTENT_EXTRA_WIDGET_ID = "net.micode.notes.widget_id";
    public static final String INTENT_EXTRA_WIDGET_TYPE = "net.micode.notes.widget_type";
    public static final String INTENT_EXTRA_FOLDER_ID = "net.micode.notes.folder_id";
    public static final String INTENT_EXTRA_CALL_DATE = "net.micode.notes.call_date";

    public static final int TYPE_WIDGET_INVALIDE      = -1;
    public static final int TYPE_WIDGET_2X            = 0;
    public static final int TYPE_WIDGET_4X            = 1;

    public static class DataConstants {
        public static final String NOTE = TextNote.CONTENT_ITEM_TYPE;
        public static final String CALL_NOTE = CallNote.CONTENT_ITEM_TYPE;
    }

    /**
     * Uri to query all notes and folders
     */
    public static final Uri CONTENT_NOTE_URI = Uri.parse("content://" + AUTHORITY + "/note");

    /**
     * Uri to query data
     */
    public static final Uri CONTENT_DATA_URI = Uri.parse("content://" + AUTHORITY + "/data");
//定义便签内容的列名
    public interface NoteColumns {

        //在接口中，字段、成员默认是public、static、final的，所以显式里不用再强调 public static final 了

        String ID = "_id";//每条便签的唯一ID
        String PARENT_ID = "parent_id";//所属文件夹的ID
        String CREATED_DATE = "created_date";//创建时间
        String MODIFIED_DATE = "modified_date";//最后修改时间
        String ALERTED_DATE = "alert_date";
        String SNIPPET = "snippet";
        String WIDGET_ID = "widget_id"; //桌面小部件的关联ID
        String WIDGET_TYPE = "widget_type"; //小部件类型
        String BG_COLOR_ID = "bg_color_id";
        String HAS_ATTACHMENT = "has_attachment";
        String NOTES_COUNT = "notes_count";
        String TYPE = "type"; //类型；是普通便签还是文件夹
        String SYNC_ID = "sync_id";
        String LOCAL_MODIFIED = "local_modified";
        String ORIGIN_PARENT_ID = "origin_parent_id";
        String GTASK_ID = "gtask_id";
        String VERSION = "version";
    }

    //这是Notes类内部的一个接口，专门定义“扩展数据表”的列名

    public interface DataColumns {

        /**
         * MIMETYPE: 数据的类型。
         * 比如：这条数据是一个“闹钟”还是一个“文本内容”。
         * 这个字段非常重要，决定了后面 DATA1, DATA2 存的是什么。
         */
        String MIME_TYPE = "mime_type";//定义数据类型
        /*
        IDE（编辑器）发现你的 DataColumns
        接口里没有定义 MIMETYPE 这个常量，所以它找不到目标，就标红报错了。
        */

        /**
         * ID:数据的唯一标识（通常是自增数字）
         */
        String ID = "_id";

        /**
         * NOTE_ID: 关联的主便签 ID。
         * 表示这条扩展数据是属于哪一页便签的。
         */
        String NOTE_ID = "note_id";

        // --- 下面是通用数据列 (Generic Data Columns) ---
        // 为什么要叫 DATA1, DATA2？
        // 因为这张表要存很多种数据，为了省事，定义了几个通用的格子，
        // 如果是闹钟，DATA1 可能存时间；如果是清单，DATA1 可能存文字。
        String CONTENT = "content";
        /**
         * DATA1: 通用数据列1
         * 在代码中通常用于存储：主要的文本内容或某种状态（如是否完成）。
         * 类型：通常是 TEXT（字符串）
         */

        String DATA1 = "data1";

        /**
         * DATA2: 通用数据列2
         * 类型：通常是 INTEGER（整数）
         * 比如：在闹钟类型中，这里可能存的是提醒的状态。
         */
        String DATA2 = "data2";

        /**
         * DATA3:通用数据列3
         * 类型：通常是字符串
         */
        String DATA3 = "data3";

        //DATA4,5依次类推，作为备用拓展位
        String DATA4 = "data4";

        String DATA5 = "data5";

    }

    public static final class TextNote implements DataColumns {
        /**
         * Mode to indicate the text in check list mode or not
         * <P> Type: Integer 1:check list mode 0: normal mode </P>
         */
        public static final String MODE = DATA1;

        public static final int MODE_CHECK_LIST = 1;

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/text_note";

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/text_note";

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/text_note");
    }

    public static final class CallNote implements DataColumns {
        /**
         * Call date for this record
         * <P> Type: INTEGER (long) </P>
         */
        public static final String CALL_DATE = DATA1;

        /**
         * Phone number for this record
         * <P> Type: TEXT </P>
         */
        public static final String PHONE_NUMBER = DATA3;

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/call_note";

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/call_note";

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/call_note");
    }
}
