package videoplayer.MAIN;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import java.awt.EventQueue;
import java.io.File;
 
import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
 
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
 
import videoplayer.Window.Window;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
 
public class PlayerMain {
 
	static Window frame;
	//private static final String NATIVE_LIBRARY_SEARCH_PATH = "D:\\Program Files\\VideoLAN\\VLC\\sdk\\lib";
	
	public static void main(String[] args) {
		
		//�������ã���vlc sdk���뵽eclipse
		
		//if(RuntimeUtil.isWindows()){					}
		NativeLibrary.addSearchPath(
				RuntimeUtil.getLibVlcLibraryName(), "E:\\vlc\\a");	//�����·����vlc�İ�װ·��
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(),LibVlc.class);
        //System.out.println(LibVlc.INSTANCE.libvlc_get_version());
 
		//����������������д���
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try{
				
					frame=new Window();
					frame.setVisible(false);
					//ֱ�Ӳ��������������������ļ��ľ���·��
					//frame.getMediaPlayer().playMedia(frame.AbsolutePath);
					//���Ʋ�����Ƶ��Ĭ�ϲ��Ż���
					//frame.getMediaPlayer().prepareMedia("G:\\6ά������Ƶ\\[CMBT] Cooking Master Boy Complete\\[CMBT] 01 - Genius Cooking Boy, Mao!.MKV");
					new SwingWorker<String, Integer>() {
 
						@Override
						protected String doInBackground() throws Exception {
							// TODO Auto-generated method stub
							while (true) {	//��ȡ��Ƶ���Ž��Ȳ��Ұ��ٷֱ���ʾ
								long total=frame.getMediaPlayer().getLength();
								long curr=frame.getMediaPlayer().getTime();
								float percent=(float)curr/total;
								publish((int)(percent*100));
								Thread.sleep(100);
							}	
							
						}
						
						protected void process(java.util.List<Integer> chunks) {
							for(int v:chunks){
								frame.getProgressBar().setValue(v);
							} 
						}
					}.execute();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}
	
	//���ļ�
	public static void openVideo() {
		JFileChooser chooser=new JFileChooser();
		int v=chooser.showOpenDialog(null);
		if(v==JFileChooser.APPROVE_OPTION){
			File file=chooser.getSelectedFile();
			frame.getMediaPlayer().playMedia(file.getAbsolutePath());
		}
	}
	
	//�˳�����
	public static void exit() {
		frame.getMediaPlayer().release();
		System.exit(0);
	}
 
	//ʵ�ֲ��Ű�ť�ķ���
	public static void play() {
		frame.getMediaPlayer().play();
	}
	
	//ʵ����ͣ��ť�ķ���
	public static void pause() {
		frame.getMediaPlayer().pause();
	}
	
	//ʵ��ֹͣ��ť�ķ���
	public static void stop() {
		frame.getMediaPlayer().stop();
	}
	
	//ʵ�ֵ����������ת�ķ���
	public static void jumpTo(float to) {
		frame.getMediaPlayer().setTime((long)(to*frame.getMediaPlayer().getLength()));
	}
	
	//ʵ�ֿ��������ķ���
	public static void setVol(int v) {
		frame.getMediaPlayer().setVolume(v);
	}
	
}