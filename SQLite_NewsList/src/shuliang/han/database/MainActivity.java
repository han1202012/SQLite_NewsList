package shuliang.han.database;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends Activity {

	private SQLiteDatabase db;	//数据库对象
	private ListView listView;	//列表
	private EditText et_tittle;	//输入的新闻标题
	private EditText et_content;	//输入的新闻内容
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//打开或者创建数据库, 这里是创建数据库
		db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString() + "/news.db", null);
		System.out.println(this.getFilesDir().toString() + "/news.db");

		//初始化组件
		listView = (ListView) findViewById(R.id.lv_news);
		et_tittle = (EditText) findViewById(R.id.et_news_tittle);
		et_content = (EditText) findViewById(R.id.et_news_content);
		
		
	}
	
	/*
	 * 插入数据到数据库中的触发点击事件
	 * 如果数据库存在就能正常访问数据库, 如果不存在访问数据库的时候就会出现 SQLiteException 异常
	 * 正常访问 : 获取输入的新闻标题 和 新闻内容, 将标题 和 内容插入到数据库, 重新获取Cursor, 使用Cursor刷新ListView内容
	 * 异常访问 : 如果访问出现了SQLiteException异常, 说明数据库不存在, 这时就需要先创建数据库
	 */
	public void insertNews(View view) {
		String tittle = et_tittle.getText().toString();
		String content = et_content.getText().toString();
		
		try{
			insertData(db, tittle, content);
			Cursor cursor = db.rawQuery("select * from news_table", null);
			inflateListView(cursor);
		}catch(SQLiteException exception){
			db.execSQL("create table news_table (" +
					"_id integer primary key autoincrement, " +
					"news_tittle varchar(50), " +
					"news_content varchar(5000))");
			insertData(db, tittle, content);
			Cursor cursor = db.rawQuery("select * from news_table", null);
			inflateListView(cursor);
		}
		
	}
	
	/*
	 * 向数据库中插入数据
	 * 参数介绍 : 
	 * -- 参数① : SQL语句, 在这个语句中使用 ? 作为占位符, 占位符中的内容在后面的字符串中按照顺序进行替换
	 * -- 参数② : 替换参数①中占位符中的内容
	 */
	private void insertData(SQLiteDatabase db, String tittle, String content) {
		db.execSQL("insert into news_table values(null, ?, ?)", new String[]{tittle, content});
	}
	
	/*
	 * 刷新数据库列表显示
	 * 1. 关联SimpleCursorAdapter与数据库表, 获取数据库表中的最新数据
	 * 2. 将最新的SimpleCursorAdapter设置给ListView
	 */
	private void inflateListView(Cursor cursor) {
		SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
				getApplicationContext(), 
				R.layout.item, 
				cursor, 
				new String[]{"news_tittle", "news_content"}, 
				new int[]{R.id.tittle, R.id.content});
		
		listView.setAdapter(cursorAdapter);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//在Activity销毁的时候, 如果没有
		if(db != null && db.isOpen())
			db.close();
	}
	
}
