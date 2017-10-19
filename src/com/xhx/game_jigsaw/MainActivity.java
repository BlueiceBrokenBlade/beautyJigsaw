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
	//���ö�ά���鴴�����ɸ�С����
	private ImageView[][] iv_game_arr=new ImageView[3][5];
	//��Ϸ������
	private GridLayout gl_main_game;
	//��ǰ�շ���
	private ImageView iv_null_image;
	//��ǰ����
	private GestureDetector mGesture;
	//���windowmanager���������Ļ���������ͼƬ������С
	WindowManager wm;
	private ImageView originalImage;
	//ͼƬ��Դ
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
	 * ��ʼ������
	 * @param img
	 */
	public void init(int imgId){
//		Bitmap originalBm = ((BitmapDrawable)getResources().getDrawable(R.drawable.xinyuanjieyi1)).getBitmap();//��ȡ������ͼ
//		Bitmap bigBm=ajustImageByScreen(originalBm);
		Bitmap bigBm = ((BitmapDrawable)getResources().getDrawable(imgId)).getBitmap();
		int tuWandH = bigBm.getWidth() / 5;//ÿ��С����Ŀ�͸�
		//��ʼ����ά����
		for(int i = 0 ; i < iv_game_arr.length ; i++){
			for(int j =0 ; j < iv_game_arr[0].length ; j++){
				Bitmap bm = bigBm.createBitmap(bigBm, tuWandH * j , (int) (tuWandH * i *1.2) , tuWandH, (int) (tuWandH*1.2));//�ü�ÿ��С���� 
				
				iv_game_arr[i][j] = new ImageView(this);
				iv_game_arr[i][j].setImageBitmap(bm);
				iv_game_arr[i][j].setPadding(2, 2, 2, 2);
				iv_game_arr[i][j].setTag(new GameData(i, j, bm));//imageView�ؼ�����GameDatas����
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
		
		//��ʼ��������
		
		for(int i = 0 ; i < iv_game_arr.length ; i++){
			for(int j =0 ; j < iv_game_arr[0].length ; j++){
				gl_main_game.addView(iv_game_arr[i][j]);
			}
		}
		
		//�������һ������Ϊ�շ���
		setNullImageView(iv_game_arr[2][4]);
		
		//�°��������ԭͼ
		originalImage.setImageDrawable(getResources().getDrawable(imgs[currentImag]));
		
		//�������ͼƬ
		randomMove();
		isGameStart=true;
	}
	
	/**
	 * ����ͼƬ��С��Ӧ��Ļ��С���Լ������������õ������ͼƬ����Ӧ��Ļ��û�ɹ����о�Ӧ�ã�
	 * @param originalBm �ֻ���Ļ1080 1920
	 * @return
	 */
	private Bitmap ajustImageByScreen(Bitmap originalBm) {
		// TODO Auto-generated method stub
		int width = originalBm.getWidth();
		int height = originalBm.getHeight();
		//��Ļ��С
		wm=(WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		int screenWidth = wm.getDefaultDisplay().getWidth();
		int screenHeight = wm.getDefaultDisplay().getHeight();
		//�������		
		float radioOfWidth = (float)screenWidth/width;
		float radioOfHeight = (float)screenHeight/height;
		Matrix matrix=new Matrix();
		matrix.postScale(radioOfWidth, radioOfHeight);
		//�õ����ź��ͼƬ
		Bitmap newBm=Bitmap.createBitmap(originalBm, 0, 0, width, height, matrix, true);	
		return newBm;
	}
	
	/**
	 * ����ĳ������Ϊ�շ���
	 * @param mImageView
	 */
	private void setNullImageView(ImageView mImageView) {
		mImageView.setImageBitmap(null);
		iv_null_image=mImageView;
	}
	
	/**
	 * �жϵ��������շ����Ƿ�Ϊ���ڹ�ϵ
	 * @param mImageView ����ķ���
	 * @return true ���ڣ�false ������
	 */
	public boolean isHasByNullImageView(ImageView mImageView){
		GameData mNullGameData = (GameData) iv_null_image.getTag();//�շ�������
		GameData mGameData = (GameData) mImageView.getTag();//�����������
		
		
		if(mGameData.y==mNullGameData.y && mGameData.x+1==mNullGameData.x){//��������ڿշ����ϱ�		
			return true;
		}else if(mGameData.y==mNullGameData.y && mGameData.x-1==mNullGameData.x){//��������ڿշ����±�
			return true;
		}else if(mGameData.y+1==mNullGameData.y && mGameData.x==mNullGameData.x){//��������ڿշ������
			return true;
		}else if(mGameData.y-1==mNullGameData.y && mGameData.x==mNullGameData.x){//��������ڿշ����ұ�
			return true;
		}
		return false;
	}
	
	
	
	/**
	 * �ж����Ʒ���
	 * @param start_x
	 * @param start_y
	 * @param end_x
	 * @param end_y
	 * @return
	 */
	public int getDirByGes(float start_x,float start_y,float end_x,float end_y){
		if(start_y-end_y>0&&(Math.abs(start_y-end_y)>Math.abs(start_x-end_x))){
			return 1;//���ϻ���
		}else if(start_y-end_y<0&&(Math.abs(start_y-end_y)>Math.abs(start_x-end_x))){
			return 2;//���»���
		}else if(start_x-end_x>0&&(Math.abs(start_x-end_x)>Math.abs(start_y-end_y))){
			return 3;//���󻬶�
		}else if(start_x-end_x<0&&(Math.abs(start_x-end_x)>Math.abs(start_y-end_y))){
			return 4;//���һ���
		}
		return 0;
	}
	
	public void changeDataByImageView(final ImageView mImageView){
		changeDataByImageView(mImageView,true);
	}
	
	/**
	 * ���ö��������󣬽��з���֮�����ݽ���
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
		//����һ��λ�ƶ���
		TranslateAnimation translateAnimation = null;
		//�ж϶����ƶ�����
		if(mImageView.getX() > iv_null_image.getX()){//��������ڿշ����ұߣ������ƶ�
			translateAnimation = new TranslateAnimation(0, -mImageView.getWidth(), 0, 0);
		}else if(mImageView.getX() < iv_null_image.getX()){//��������ڿշ�����ߣ������ƶ�
			translateAnimation = new TranslateAnimation(0, mImageView.getWidth(), 0, 0 );
		}else if(mImageView.getY() > iv_null_image.getY()){//��������ڿշ����±ߣ������ƶ�
			translateAnimation = new TranslateAnimation(0, 0, 0, -mImageView.getWidth());
		}else if(mImageView.getY() < iv_null_image.getY()){//��������ڿշ����ϱߣ������ƶ�
			translateAnimation = new TranslateAnimation(0, 0, 0, mImageView.getWidth());
		}
		//���ö���ʱ��
		translateAnimation.setDuration(70);
		//���ö�������ͣ��
		translateAnimation.setFillAfter(true);
		//�������������ݽ���
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
				mImageView.clearAnimation();//�������
				//��������
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
		
		//ִ�ж���
		mImageView.startAnimation(translateAnimation);
	}
	
	public void changeDataByGesture(int type){
		changeDataByGesture(type , true);
	}
	
	/**
	 * �������Ʒ����ƶ�����
	 * @param type 1:�ϣ�2���£�3����4����
	 */
	public void changeDataByGesture(int type,boolean isAnim){
		//��ȡ��ǰ�շ���λ��
		GameData mNullGameData = (GameData) iv_null_image.getTag();
		int nullImg_x=mNullGameData.x;
		int nullImg_y=mNullGameData.y;
		int new_x = nullImg_x;
		int new_y = nullImg_y;
		//�������Ʒ���������Ӧ�����ڵ�λ������
		if(type == 1){
			new_x=nullImg_x+1;
		}else if(type == 2){
			new_x=nullImg_x-1;
		}else if(type == 3){
			new_y=nullImg_y+1;
		}else if(type == 4){
			new_y=nullImg_y-1;
		}
		//�ж��������Ƿ����
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
	 * �������ͼƬ˳��
	 */
	public void randomMove(){
		for(int i = 0 ;i < 100 ; i++){
			int type=(int)(Math.random()*4)+1;
			changeDataByGesture(type,false);
		}
	}
	
	/**
	 * �ж���Ϸ�Ƿ����
	 */
	public void isGameOver(){
		boolean isGameOver = true;
		//����ÿ��С���飬Ϊ���������ж�
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
		//���ر���
		if(isGameOver){
			Toast.makeText(this, "Game Over!", Toast.LENGTH_SHORT).show();
		}
	}
	
	//������Ϣ��
	class GameData{
		public int x = 0;//����λ��
		public int y = 0;
		public Bitmap bm;
		public int p_x = 0;//С�����ͼƬλ��
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
		 * �ж�ÿ��С����λ���Ƿ�ƥ��ͼƬ true��ƥ��     false����ƥ��
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
