package com.example.friends;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.friends.ui.ItemView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MainActivity extends Activity {

    private ListView listView;
    private InputMethodManager imm;
    private EditText edit;
    private RelativeLayout comment_view;
    private Button submit;
    int changeHeight;
    int curPosition;
    HashMap<Integer,String> map = new HashMap<>();
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    int j =0;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listview);
        edit = (EditText) findViewById(R.id.edit);
        comment_view = (RelativeLayout) findViewById(R.id.comment_view);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        submit = (Button) findViewById(R.id.submit);
        listView.addHeaderView(getHeadView());
        listView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                changeHeight = oldBottom - bottom;
                //             Log.i("》》》》  ", "  left=" + left + "  top=" + top + "  right=" + right + "  bottom=" + bottom + "  oldLeft=" + oldLeft + "  oldTop=" + oldTop + "  oldRight=" + oldRight + "  oldBottom=" + oldBottom);
                //             Log.i("》》》》  ", "  changeHeight" + changeHeight);
            }
        });
        sp = getSharedPreferences("activity", MODE_PRIVATE);
        editor = sp.edit();

        int size = sp.getInt("size",0);
        Log.i("》》》  ", "  size=" + size);
        for(int i=0;i<size;i++){
            map.clear();
            String word = sp.getString("word"+i, "");
            int position = sp.getInt("position"+i,Integer.MAX_VALUE);

            Log.i("》》》----  ", "  word=" + word + "  position=" + position);
            map.put(position, word);
        }

        initData();
    }

    private void initData(){
        List<Item> list = new ArrayList<>();
        for(int i=0;i<5;i++){
            Item item = new Item();
            item.setPortraitId(R.drawable.default_qq_avatar);
            item.setNickName("test");
            item.setCreatedAt("昨天");
            item.setContent("床前明月光");
            List<UserImage> images = new ArrayList<>();
            UserImage image = new UserImage();
            image.setImage(R.drawable.ic_launcher);
            images.add(image);
            images.add(image);
            item.setImages(images);
            List<CommentItem> comments = new ArrayList<>();
            comments.add(new CommentItem("金蝉子",":hahahahh"));
            item.setComments(comments);
            list.add(item);
        }
        Item item = new Item();
        item.setPortraitId(R.drawable.default_qq_avatar);
        item.setNickName("test");
        item.setCreatedAt("昨天");
        item.setContent("床前明月光");
        list.add(item);

        Item item2 = new Item();
        item2.setPortraitId(R.drawable.default_qq_avatar);
        item2.setNickName("test");
        item2.setCreatedAt("昨天");
        List<UserImage> images = new ArrayList<>();
        UserImage image = new UserImage();
        image.setImage(R.drawable.ic_launcher);
        images.add(image);
        images.add(image);
        item2.setImages(images);
        list.add(item2);

        listView.setAdapter(new MyAdapter(list));
    }

    private View getHeadView(){
        View view = LayoutInflater.from(this).inflate(R.layout.friends_circle_head,null);
        return view;
    }

    private class MyAdapter extends BaseAdapter implements ItemView.ICommentListener,ItemView.IItemContentListener{
        private List<Item> lists;
        public MyAdapter(List<Item> lists) {
            this.lists = lists;
        }

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int position) {
            return lists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemView itemView = new ItemView(getBaseContext());
            itemView.setmData(lists.get(position));
            itemView.setPosition(position);
            itemView.setCommentListener(this);
            itemView.setItemContentListener(this);
            return itemView;
        }

        @Override
        public void comment(final int position){
            Log.i("》》》  ", "map.get(position)  " + (position + 1) + "  " + map.get(position + 1));
            if(map.get(position+1)!= null && !map.get(position+1).equals("") ){
                edit.setText(map.get(position+1));
            }else {
                edit.setText("");
            }
            curPosition = position+1;

            WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
            int windowHeight = wm.getDefaultDisplay().getHeight();
            comment_view.setVisibility(View.VISIBLE);
            edit.requestFocus();
            imm.showSoftInput(edit, InputMethodManager.RESULT_SHOWN);
            imm.toggleSoftInput(InputMethodManager.RESULT_SHOWN, InputMethodManager.HIDE_IMPLICIT_ONLY);

//            ItemView itemView = (ItemView) getView(position+1,null,listView);
//            float y = itemView.getFeed_more().getY();
//            int height = comment_view.getHeight() + changeHeight;
//            Log.i("》》》  ", "  height=" + height + "   y=" + y);
// //           listView.scrollTo(0, height);
////            listView.scrollListBy((int)height-(int)(windowHeight - y));
//            listView.scrollBy(0,(int)height-(int)(windowHeight - y));

//            if(word != null){
//                edit.setText(word);
//            }else {
//                edit.setText("");
//            }
 //           saveData(edit.getText().toString(),curPosition);
            if(curPosition != 0 && !edit.getText().toString().equals("")){
                map.put(curPosition, edit.getText().toString());

                j++;
                editor.putString("word"+j,edit.getText().toString());
                editor.putInt("position" + j, curPosition);
                editor.putInt("size", map.size());
                Log.i("》》》》 "," word="+"word"+j  +" , position="+"position" + j+"  size="+map.size());
                editor.apply();
            }

            submit.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    final String s = edit.getText().toString();

                    if (imm != null){
                        imm.hideSoftInputFromWindow(comment_view.getWindowToken(), 0);
                    }
                    lists.get(position).getComments().add(new CommentItem("苦行僧", ":" + s));
                    notifyDataSetChanged();
                    edit.setText("");
                    map.put(position,"");
                    comment_view.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public void addContent(final int position,final int num) {
            edit.requestFocus();
            comment_view.setVisibility(View.VISIBLE);
            imm.showSoftInput(edit, InputMethodManager.RESULT_SHOWN);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String s = edit.getText().toString();
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(comment_view.getWindowToken(), 0);
                    }
                    List<CommentItem> comments = lists.get(position).getComments();
                    comments.add(new CommentItem("金蝉子", comments.get(num).getName(), s));
                    notifyDataSetChanged();
                    edit.setText("");
                    map.clear();
                    comment_view.setVisibility(View.GONE);
                }
            });
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){

        if(ev.getAction() == MotionEvent.ACTION_DOWN){

            if(isShouldHideInput(comment_view, ev)){
                if(imm!= null){
                    if(curPosition != 0 && !edit.getText().toString().equals("")){
                        map.put(curPosition, edit.getText().toString());
                    }
                    imm.hideSoftInputFromWindow(comment_view.getWindowToken(),0);
                    comment_view.setVisibility(View.GONE);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        if(getWindow().superDispatchTouchEvent(ev)){
            return true;
        }
        return onTouchEvent(ev);
    }

    public void saveData(String word,int position) {
        editor.putString("word",word);
        editor.putInt("position", position);

        Log.i("》》》 ---- ", "  word=" + word + "  position=" + position);
        editor.apply();

    }


    public boolean isShouldHideInput(View v,MotionEvent event){
        if(v != null && v instanceof RelativeLayout){
            int[] leftTop = {0,0};
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top +v.getHeight();
            int right = left + v.getWidth();

            if(event.getX() > left && event.getX() < right && event.getY()>top &&event.getY()<bottom){
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            }else {
                return true;
            }
        }
        return false;
    }

}
