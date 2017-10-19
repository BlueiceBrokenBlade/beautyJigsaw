package com.xhx.game_jigsaw;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnGestureListener{
	boolean isGameStart=false;
	//利用二维数组创建若干个小方块
	private ImageView[][] iv_game_arr=new ImageView[3][5];
	//游戏主界面
	private GridLayout gl_main_game;
	//当前空方块
	private ImageView iv_null_image;
	//当前手势
	private GestureDetector mGesture;
	//获得windowmanager用来获得屏幕宽高来调整图片比例大小
	WindowManager wm;
	private ImageView originalImage;
	//图片资源
	private int[] imgs={R.drawable.pintu1,R.drawable.pintu2,R.drawable.pintu3};
	int currentImag=0;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return mGesture.onTouchEvent(event);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mGesture=new GestureDetector(this, this);		
		
		gl_main_game=(GridLayout) findViewById(R.id.gl_main_game);
		originalImage=(ImageView) findViewById(R.id.originalImage);
		init(imgs[currentImag]);
		
		
	}
	
	/**
	 * 初始化界面
	 * @param img
	 */
	public void init(int imgId){
//		Bitmap originalBm = ((BitmapDrawable)getResources().getDrawable(R.drawable.xinyuanjieyi1)).getBitmap();//获取完整大图
//		Bitmap bigBm=ajustImageByScreen(originalBm);
		Bitmap bigBm = ((BitmapDrawable)getResources().getDrawable(imgId)).getBitmap();
		int tuWandH = bigBm.getWidth() / 5;//每个小方块的宽和高
		//初始化二维数组
		for(int i = 0 ; i < iv_game_arr.length ; i++){
			for(int j =0 ; j < iv_game_arr[0].length ; j++){
				Bitmap bm = bigBm.createBitmap(bigBm, tuWandH * j , (int) (tuWandH * i *1.2) , tuWandH, (int) (tuWandH*1.2));//裁剪每个小方块 
				
				iv_game_arr[i][j] = new ImageView(this);
				iv_game_arr[i][j].setImageBitmap(bm);
				iv_game_arr[i][j].setPadding(2, 2, 2, 2);
				iv_game_arr[i][j].setTag(new GameData(i, j, bm));//imageView控件附加GameDatas数据
				iv_game_arr[i][j].setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						boolean flag = isHasByNullImageView((ImageView) v); 
						if(flag){
							changeDataByImageView((ImageView) v);
						}
					}
				});
			}
		}
		
		//初始化主界面
		
		for(int i = 0 ; i < iv_game_arr.length ; i++){
			for(int j =0 ; j < iv_game_arr[0].length ; j++){
				gl_main_game.addView(iv_game_arr[i][j]);
			}
		}
		
		//设置最后一个方块为空方块
		setNullImageView(iv_game_arr[2][4]);
		
		//下半界面设置原图
		originalImage.setImageDrawable(getResources().getDrawable(imgs[currentImag]));
		
		//随机打乱图片
		randomMove();
		isGameStart=true;
	}
	
	/**
	 * 调整图片大小适应屏幕大小（自己尝试做了做让导入随便图片自适应屏幕，没成功，感觉应该）
	 * @param originalBm 手机屏幕1080 1920
	 * @return
	 */
	private Bitmap ajustImageByScreen(Bitmap originalBm) {
		// TODO Auto-generated method stub
		int width = originalBm.getWidth();
		int height = originalBm.getHeight();
		//屏幕大小
		wm=(WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		int screenWidth = wm.getDefaultDisplay().getWidth();
		int screenHeight = wm.getDefaultDisplay().getHeight();
		//计算比例		
		float radioOfWidth = (float)screenWidth/width;
		float radioOfHeight = (float)screenHeight/height;
		Matrix matrix=new Matrix();
		matrix.postScale(radioOfWidth, radioOfHeight);
		//得到缩放后的图片
		Bitmap newBm=Bitmap.createBitmap(originalBm, 0, 0, width, height, matrix, true);	
		return newBm;
	}
	
	/**
	 * 设置某个方块为空方块
	 * @param mImageView
	 */
	private void setNullImageView(ImageView mImageView) {
		mImageView.setImageBitmap(null);
		iv_null_image=mImageView;
	}
	
	/**
	 * 判断点击方块与空方块是否为相邻关系
	 * @param mImageView 点击的方块
	 * @return true 相邻，false 不相邻
	 */
	public boolean isHasByNullImageView(ImageView mImageView){
		GameData mNullGameData = (GameData) iv_null_image.getTag();//空方块数据
		GameData mGameData = (GameData) mImageView.getTag();//点击方块数据
		
		
		if(mGameData.y==mNullGameData.y && mGameData.x+1==mNullGameData.x){//点击方块在空方块上边		
			return true;
		}else if(mGameData.y==mNullGameData.y && mGameData.x-1==mNullGameData.x){//点击方块在空方块下边
			return true;
		}else if(mGameData.y+1==mNullGameData.y && mGameData.x==mNullGameData.x){//点击方块在空方块左边
			return true;
		}else if(mGameData.y-1==mNullGameData.y && mGameData.x==mNullGameData.x){//点击方块在空方块右边
			return true;
		}
		return false;
	}
	
	
	
	/**
	 * 判断手势方向
	 * @param start_x
	 * @param start_y
	 * @param end_x
	 * @param end_y
	 * @return
	 */
	public int getDirByGes(float start_x,float start_y,float end_x,float end_y){
		if(start_y-end_y>0&&(Math.abs(start_y-end_y)>Math.abs(start_x-end_x))){
			return 1;//向上滑动
		}else if(start_y-end_y<0&&(Math.abs(start_y-end_y)>Math.abs(start_x-end_x))){
			return 2;//向下滑动
		}else if(start_x-end_x>0&&(Math.abs(start_x-end_x)>Math.abs(start_y-end_y))){
			return 3;//向左滑动
		}else if(start_x-end_x<0&&(Math.abs(start_x-end_x)>Math.abs(start_y-end_y))){
			return 4;//向右滑动
		}
		return 0;
	}
	
	public void changeDataByImageView(final ImageView mImageView){
		changeDataByImageView(mImageView,true);
	}
	
	/**
	 * 利用动画结束后，进行方块之间数据交换
	 * @param mImageView
	 */
	public void changeDataByImageView(final ImageView mImageView, boolean isAnim){
			
		if(!isAnim){
			GameData mGameData = (GameData) mImageView.getTag();
			iv_null_image.setImageBitmap(mGameData.bm);
			GameData mNullGameData = (GameData) iv_null_image.getTag();
			mNullGameData.bm = mGameData.bm;
			mNullGameData.p_x = mGameData.p_x;
			mNullGameData.p_y = mGameData.p_y;
			setNullImageView(mImageView);	
			if(isGameStart){
				isGameOver();
			}	
			return;
		}
		//创建一个位移动画
		TranslateAnimation translateAnimation = null;
		//判断动画移动方向
		if(mImageView.getX() > iv_null_image.getX()){//点击方块在空方块右边，往左移动
			translateAnimation = new TranslateAnimation(0, -mImageView.getWidth(), 0, 0);
		}else if(mImageView.getX() < iv_null_image.getX()){//点击方块在空方块左边，往右移动
			translateAnimation = new TranslateAnimation(0, mImageView.getWidth(), 0, 0 );
		}else if(mImageView.getY() > iv_null_image.getY()){//点击方块在空方块下边，往上移动
			translateAnimation = new TranslateAnimation(0, 0, 0, -mImageView.getWidth());
		}else if(mImageView.getY() < iv_null_image.getY()){//点击方块在空方块上边，往下移动
			translateAnimation = new TranslateAnimation(0, 0, 0, mImageView.getWidth());
		}
		//设置动画时长
		translateAnimation.setDuration(70);
		//设置动画结束停留
		translateAnimation.setFillAfter(true);
		//动画结束，数据交换
		translateAnimation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				mImageView.clearAnimation();//清除动画
				//交换数据
				GameData mGameData = (GameData) mImageView.getTag();
				iv_null_image.setImageBitmap(mGameData.bm);
				GameData mNullGameData = (GameData) iv_null_image.getTag();
				mNullGameData.bm = mGameData.bm;
				mNullGameData.p_x = mGameData.p_x;
				mNullGameData.p_y = mGameData.p_y;
				setNullImageView(mImageView);	
				if(isGameStart){
					isGameOver();
				}	
			}
		});
		
		//执行动画
		mImageView.startAnimation(translateAnimation);
	}
	
	public void changeDataByGesture(int type){
		changeDataByGesture(type , true);
	}
	
	/**
	 * 根据手势方向移动方块
	 * @param type 1:上，2：下，3：左，4：右
	 */
	public void changeDataByGesture(int type,boolean isAnim){
		//获取当前空方块位置
		GameData mNullGameData = (GameData) iv_null_image.getTag();
		int nullImg_x=mNullGameData.x;
		int nullImg_y=mNullGameData.y;
		int new_x = nullImg_x;
		int new_y = nullImg_y;
		//根据手势方向，设置相应的相邻的位置坐标
		if(type == 1){
			new_x=nullImg_x+1;
		}else if(type == 2){
			new_x=nullImg_x-1;
		}else if(type == 3){
			new_y=nullImg_y+1;
		}else if(type == 4){
			new_y=nullImg_y-1;
		}
		//判断新坐标是否存在
		if(new_x >= 0 && new_x < iv_game_arr.length && new_y >= 0 && new_y < iv_game_arr[0].length ){
			if(isAnim){
				changeDataByImageView(iv_game_arr[new_x][new_y]);
			}else{
				changeDataByImageView(iv_game_arr[new_x][new_y],isAnim);
			}
			
		}else{
			
		}
	}
	
	/**
	 * 随机打乱图片顺序
	 */
	public void randomMove(){
		for(int i = 0 ;i < 100 ; i++){
			int type=(int)(Math.random()*4)+1;
			changeDataByGesture(type,false);
		}
	}
	
	/**
	 * 判断游戏是否结束
	 */
	public void isGameOver(){
		boolean isGameOver = true;
		//遍历每个小方块，为空跳过不判断
		for(int i =0 ;i < iv_game_arr.length;i++ ){
			for(int j =0 ;j< iv_game_arr[0].length ;j++){
				if(iv_game_arr[i][j]==iv_null_image){
					continue;
				}
				GameData mGameData=(GameData) iv_game_arr[i][j].getTag();
				if(!mGameData.isTrue()){
					isGameOver=false;
					break;
				}
			}
		}
		//开关变量
		if(isGameOver){
			Toast.makeText(this, "Game Over!", Toast.LENGTH_SHORT).show();
		}
	}
	
	//方块信息类
	class GameData{
		public int x = 0;//方块位置
		public int y = 0;
		public Bitmap bm;
		public int p_x = 0;//小方块的图片位置
		public int p_y = 0;
		public GameData(int x, int y, Bitmap bm) {
			super();
			this.x = x;
			this.y = y;
			this.bm = bm;
			this.p_x = x;
			this.p_y = y;
		}
		/**
		 * 判断每个小方块位置是否匹配图片 true：匹配     false：不匹配
		 * @return
		 */
		public boolean isTrue() {
			// TODO Auto-generated method stub
			if(x == p_x && y == p_y){
				return true;
			}
			return false;
		}
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		int type=getDirByGes(e1.getX(), e1.getY(), e2.getX(), e2.getY());
		changeDataByGesture(type);
		return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id=item.getItemId();
		if(id==R.id.again){
			randomMove();
			return true;
		}
		if(id==R.id.changeImage){		
			gl_main_game.removeAllViews();

			if(currentImag < imgs.length-1){
				currentImag++;
			}else{
				currentImag=0;
			}
			isGameStart=false;
			init(imgs[currentImag]);
		}
		return true;
	}
}
