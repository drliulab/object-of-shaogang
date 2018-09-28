package videoplayer.Window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.Spring;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import videoplayer.MAIN.PlayerMain;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class Window extends JFrame {

	private JPanel contentPane; // ������������������ҳ�������
	private JMenuBar menuBar; // �˵���
	private JMenuItem mnOpenVideo, mnExit; // �ļ��˵���Ŀ¼�����������˳�
	private JPanel panel; // ������������
	private JProgressBar progress; // ������
	private JPanel progressPanel; // ����������
	private JPanel controlPanel; // ���ư�ť����
	private JButton btnStop, btnPlay, btnPause; // ���ư�ť��ֹͣ�����š���ͣ
	private JSlider slider; // �������ƿ�
	JScrollPane jsp = null;
	JScrollPane jsp1 = null;
	JScrollPane jsp2 = null;
	JScrollPane jsp3 = null;
	PreparedStatement ps = null;
	Connection ct = null;
	ResultSet rs = null;
	EmbeddedMediaPlayerComponent playerComponent; // ý�岥�������
	String AbsolutePath;
	static Vector rowData, columnNames;
	static Integer a;
	static Integer b;
	static Integer c;
	static Integer d;
	static String year;
	static String month;
	static String day;
	static String date;

	public static void main(String[] args) {

	}

	public Window() {

		final JFrame jf = new JFrame("������Ƶ�������");
		jf.setSize(1500, 1000);
		jf.setResizable(false);
		jf.setLocationRelativeTo(null);
		jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		final DefaultTableModel model = new DefaultTableModel(); // �½�һ��Ĭ������ģ��
		JTable table = new JTable(model) {
		    public boolean isCellEditable(int rowIndex, int ColIndex){
	             return false;
	            }
		};
		
		rowData = new Vector();
		// ���������������
		JPanel panel = new JPanel(new BorderLayout());
		JPanel pl1 = new JPanel(new BorderLayout());
		JPanel pl2 = new JPanel(new BorderLayout());
		JPanel pl3 = new JPanel(new BorderLayout());
		JPanel pl4 = new JPanel(new BorderLayout());
		JPanel pl5 = new JPanel(new BorderLayout());
		JPanel pl6 = new JPanel(new GridLayout(0,11));
		JPanel pl7 = new JPanel(new BorderLayout());
		JPanel pl8 = new JPanel();
		JPanel pl9 = new JPanel();
		JPanel pl10 = new JPanel();
		JPanel pl11 = new JPanel();
		JPanel pl12 = new JPanel();
		JPanel pl13 = new JPanel();
		JPanel pl14 = new JPanel();
		JPanel pl15 = new JPanel();
		JPanel pl16 = new JPanel();
		JPanel pl17 = new JPanel();
		JPanel pl18 = new JPanel();
		JPanel pl19 = new JPanel(new BorderLayout());

		pl1.setBackground(Color.WHITE);
		pl1.setPreferredSize(new Dimension(1500, 50));

		pl2.setBackground(Color.RED);
		pl2.setPreferredSize(new Dimension(300, 950));

		pl3.setBackground(Color.GREEN);
		pl3.setPreferredSize(new Dimension(1200, 950));

		pl4.setBackground(Color.WHITE);
		pl4.setPreferredSize(new Dimension(300, 50));
		Border titleBorder1 = BorderFactory.createTitledBorder("�豸��");
		pl4.setBorder(titleBorder1);

		pl5.setBackground(Color.WHITE);
		pl5.setPreferredSize(new Dimension(300, 50));
		Border titleBorder3 = BorderFactory.createTitledBorder("�������");
		pl5.setBorder(titleBorder3);

		pl6.setBackground(Color.WHITE);
		pl6.setPreferredSize(new Dimension(900, 50));
		Border titleBorder2 = BorderFactory.createTitledBorder("����������");
		pl6.setBorder(titleBorder2);

		// pl7.setBackground(Color.WHITE);
		pl7.setPreferredSize(new Dimension(900, 950));

		pl8.setBackground(Color.WHITE);
		pl8.setPreferredSize(new Dimension(300, 950));
		pl8.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

		pl18.setBackground(Color.WHITE);
		pl18.setPreferredSize(new Dimension(900, 50));
		Border titleBorder4 = BorderFactory.createTitledBorder("��Ƶ����");
		pl18.setBorder(titleBorder4);

		// pl19.setBackground(Color.WHITE);
		pl19.setPreferredSize(new Dimension(900, 900));

		panel.add(pl1, BorderLayout.NORTH);
		panel.add(pl2, BorderLayout.WEST);
		panel.add(pl3, BorderLayout.CENTER);

		pl1.add(pl4, BorderLayout.WEST);
		pl1.add(pl5, BorderLayout.EAST);
		pl1.add(pl6, BorderLayout.CENTER);

		pl3.add(pl7, BorderLayout.CENTER);
		pl3.add(pl8, BorderLayout.EAST);

		pl7.add(pl18, BorderLayout.NORTH);
		pl7.add(pl19, BorderLayout.SOUTH);

		// ���ñ�ͷ
		columnNames = new Vector();
		columnNames.add("filename");
		columnNames.add("path");
		columnNames.add("date");
		columnNames.add("duration");
		columnNames.add("device");
		columnNames.add("category");
		// ��ѯģ��

		// Υ����������˵�
		JLabel label = new JLabel(" Υ�����");
		pl6.add(label);
		String[] listData = new String[] { "��", "1���ֻ�", "2˯��" };
		final JComboBox<String> comboBox = new JComboBox<String>(listData);
		comboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// ֻ����ѡ�е�״̬
				if (e.getStateChange() == ItemEvent.SELECTED) {
					a = comboBox.getSelectedIndex();
					System.out.println(a);
				}
			}
		});
		comboBox.setSelectedIndex(0);
		pl6.add(comboBox);

		// ʱ��ѡ�������˵�
		JLabel label1 = new JLabel("    ʱ        ����");
		pl6.add(label1);
		String[] listData1 = new String[] { "��", "<30s", "30~90s","90~150s","������"};
		final JComboBox<String> comboBox1 = new JComboBox<String>(listData1);
		comboBox1.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					b = comboBox1.getSelectedIndex();
					System.out.println(comboBox1.getSelectedIndex());
					switch (b) {
					case 0:
						c=0;
						d=0;
						break;
					case 1:
						c=0;
						d=30;
						break;
					case 2:
						c=30;
						d=90;
						break;
					case 3:
						c=90;
						d=150;
						break;
					case 4:
						c=0;
						d=500;
						break;
					default:
						break;
					}
					System.out.println(c);
					System.out.println(d);
				}
			}
		});
		comboBox1.setSelectedIndex(0);
		pl6.add(comboBox1);

		
		// ����ѡ��ģ��
		JLabel label3 = new JLabel("                 �꣺");
		pl6.add(label3);
		String[] listData2 = new String[] {"2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018" };
		final JComboBox<String> comboBox2 = new JComboBox<String>(listData2);
		comboBox2.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// ֻ����ѡ�е�״̬
				if (e.getStateChange() == ItemEvent.SELECTED) {
					year = (String) comboBox2.getSelectedItem();
					System.out.println(year);
				}
			}
		});
		comboBox2.setSelectedIndex(0);
		jsp1 = new JScrollPane(comboBox2);
		pl6.add(jsp1);
		
		JLabel label4 = new JLabel("                 �£�");
		pl6.add(label4);
		String[] listData3 = new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
		final JComboBox<String> comboBox3 = new JComboBox<String>(listData3);
		comboBox3.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// ֻ����ѡ�е�״̬
				if (e.getStateChange() == ItemEvent.SELECTED) {
					month = "" + ((int) comboBox3.getSelectedIndex()+1);
					System.out.println(month);
				}
			}
		});
		comboBox3.setSelectedIndex(0);
		jsp2 = new JScrollPane(comboBox3);
		pl6.add(jsp2);
		
		JLabel label5 = new JLabel("                 �գ�");
		pl6.add(label5);
		String[] listData4 = new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14" , "15" , "16" , "17" , "18" , "19" , "20" , "21" , "22" , "23" , "24" , "25" , "26" , "27" , "28" , "29" , "30" , "31" };
		final JComboBox<String> comboBox4 = new JComboBox<String>(listData4);
		comboBox4.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// ֻ����ѡ�е�״̬
				if (e.getStateChange() == ItemEvent.SELECTED) {
					day= "" + (1 + (int) comboBox4.getSelectedIndex());
					System.out.println(day);
				}
			}
		});
		comboBox4.setSelectedIndex(0);
		jsp3 = new JScrollPane(comboBox4);
		pl6.add(jsp3);
		
		
		// ��ѯ��ť
		JButton btn = new JButton("��ѯ");
		btn.setFont(new Font(null, Font.PLAIN, 16));
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("�����ѯ ");
				date = year + "." + month + "." + day;
				System.out.println(date);
				try {
					model.setRowCount(0);
					Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

					// 2.�õ�����(����)[ָ�����ӵ��Ǹ�����Դ"sa""sa"���û���������]
					// �����������Դʹ��windows nt�Ͳ���Ҫ�û���������
					String url = "jdbc:sqlserver://localhost:61647;DatabaseName=shaogang;";

					ct = DriverManager.getConnection(url, "sa", "1234qwer");
					ps = ct.prepareStatement("select * from chaxun1 where category = ? and date = ? and duration between ? and ? ");
					ps.setInt(1, a);
					ps.setString(2, date);
					ps.setInt(3, c);
					ps.setInt(4, d);
					System.out.println("���ӳɹ�");
					rs = ps.executeQuery();
					while (rs.next()) {
						// rowData���Դ�Ŷ���
						Vector hang = new Vector();
						hang.add(rs.getString(1));
						hang.add(rs.getString(2));
						hang.add(rs.getString(3));
						hang.add(rs.getInt(4));
						hang.add(rs.getString(5));
						hang.add(rs.getInt(6));
						// ���뵽rowData
						rowData.add(hang);
					}
					System.out.println(rowData);
					model.setDataVector(rowData, columnNames);

				} catch (Exception e1) {
					e1.printStackTrace();
				} finally {

					try {
						if (rs != null) {
							rs.close();
						}
						if (ps != null) {
							ps.close();
						}
						if (ct != null) {
							ct.close();
						}
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		pl6.add(btn);
		// �����и� 
		table.setRowHeight(30);
		table.setPreferredScrollableViewportSize(new Dimension(300, 250));
		//��������
		table.addMouseListener(new MouseAdapter(){
		    public void mouseClicked(MouseEvent event){
		    	if(event.getClickCount()==2) {
		    		int row=table.getSelectedRow();
		    		String filename= table.getValueAt(row, 0).toString();
		    		String path= table.getValueAt(row, 1).toString();
		    		//String	e="\"";
		    		//String		f=".";
		    		AbsolutePath =(String) (path) ;
		    		System.out.println("ѡ�б��");
		    		System.out.println(AbsolutePath);
		    		//ֱ�Ӳ���
		    		getMediaPlayer().playMedia(AbsolutePath);
		    		//�ȴ����ţ�������Ű�ť�󲥷�
		    		//getMediaPlayer().prepareMedia(AbsolutePath);
		    		
		    	}
		    }
		});
		jsp = new JScrollPane(table);
		pl8.add(jsp);

		// ����豸��ѡ��
		// �������ڵ�
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("�豸ѡ��");

		// ���������ڵ�
		DefaultMutableTreeNode rootNode1 = new DefaultMutableTreeNode("�п���");
		DefaultMutableTreeNode rootNode2 = new DefaultMutableTreeNode("������");
		DefaultMutableTreeNode rootNode3 = new DefaultMutableTreeNode("����");
		rootNode.add(rootNode1);
		rootNode.add(rootNode2);
		rootNode.add(rootNode3);

		// ���������ڵ�
		DefaultMutableTreeNode zks1Node = new DefaultMutableTreeNode("�п���1");
		DefaultMutableTreeNode zks2Node = new DefaultMutableTreeNode("�п���2");
		DefaultMutableTreeNode zks3Node = new DefaultMutableTreeNode("�п���3");
		DefaultMutableTreeNode zxq1Node = new DefaultMutableTreeNode("������1");
		DefaultMutableTreeNode zxq2Node = new DefaultMutableTreeNode("������2");
		DefaultMutableTreeNode gq1Node = new DefaultMutableTreeNode("����1");
		rootNode1.add(zks1Node);
		rootNode1.add(zks2Node);
		rootNode1.add(zks3Node);
		rootNode2.add(zxq1Node);
		rootNode2.add(zxq2Node);
		rootNode3.add(gq1Node);

		// �����ļ��ڵ�
		DefaultMutableTreeNode sb1Node = new DefaultMutableTreeNode("�豸1");
		DefaultMutableTreeNode sb2Node = new DefaultMutableTreeNode("�豸2");

		DefaultMutableTreeNode sb3Node = new DefaultMutableTreeNode("�豸1");
		DefaultMutableTreeNode sb4Node = new DefaultMutableTreeNode("�豸2");

		DefaultMutableTreeNode sb5Node = new DefaultMutableTreeNode("�豸1");
		DefaultMutableTreeNode sb6Node = new DefaultMutableTreeNode("�豸2");
		DefaultMutableTreeNode sb7Node = new DefaultMutableTreeNode("�豸3");

		DefaultMutableTreeNode sb8Node = new DefaultMutableTreeNode("�豸1");
		DefaultMutableTreeNode sb9Node = new DefaultMutableTreeNode("�豸2");
		DefaultMutableTreeNode sb10Node = new DefaultMutableTreeNode("�豸3");

		DefaultMutableTreeNode sb11Node = new DefaultMutableTreeNode("�豸1");
		DefaultMutableTreeNode sb12Node = new DefaultMutableTreeNode("�豸2");

		zks1Node.add(sb1Node);
		zks1Node.add(sb2Node);

		zks2Node.add(sb3Node);
		zks2Node.add(sb4Node);

		zks3Node.add(sb5Node);
		zks3Node.add(sb6Node);
		zks3Node.add(sb7Node);

		zxq1Node.add(sb8Node);
		zxq1Node.add(sb9Node);
		zxq2Node.add(sb10Node);

		gq1Node.add(sb11Node);
		gq1Node.add(sb12Node);

		// ʹ�ø��ڵ㴴�������
		JTree tree = new JTree(rootNode);
		tree.setShowsRootHandles(true);
		tree.setEditable(false);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				System.out.println("��ǰ��ѡ�еĽڵ�: " + e.getPath());
			}
		});
		JScrollPane scrollPane = new JScrollPane(tree);
		pl2.add(scrollPane);

		// �Ϸ��˵���
		JMenuBar menuBar = new JMenuBar();

		// ����һ���˵�
		JMenu UserMenu = new JMenu("�û�����");
		JMenu ModelMenu = new JMenu("ģ�͹���");
		JMenu VideoMenu = new JMenu("��Ƶ����");
		JMenu HelpMenu = new JMenu("����");
		menuBar.add(UserMenu);
		menuBar.add(ModelMenu);
		menuBar.add(VideoMenu);
		menuBar.add(HelpMenu);

		// ���� "UserMenu" һ���˵����Ӳ˵�
		JMenuItem newMenuItem = new JMenuItem("�½�");
		JMenuItem openMenuItem = new JMenuItem("��");
		JMenuItem exitMenuItem = new JMenuItem("�˳�");
		UserMenu.add(newMenuItem);
		UserMenu.add(openMenuItem);
		UserMenu.addSeparator();
		UserMenu.add(exitMenuItem);

		// ���� "ModelMenu" һ���˵����Ӳ˵�
		JMenuItem copyMenuItem = new JMenuItem("ɾ��");
		JMenuItem pasteMenuItem = new JMenuItem("����");
		ModelMenu.add(copyMenuItem);
		ModelMenu.add(pasteMenuItem);

		// ���� "VideoMenu" һ���˵����Ӳ˵�
		JMenuItem openflieMenuItem = new JMenuItem("���ļ�");
		VideoMenu.add(openflieMenuItem);

		// ���� "�û�����" �Ӳ˵�������ļ�����
		newMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(" �û��������");
			}
		});
		// ���� "ģ�͹���" �Ӳ˵�������ļ�����
		openMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("ģ�͹���  �����");
			}
		});
		// ���� "��Ƶ����" �Ӳ˵�������ļ�����
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("��Ƶ����  �����");
			}
		});

		// ��Ƶ�������ļ�
		openflieMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				PlayerMain.openVideo();
			}
		});

		// ���������в��Ž��沿��
		JPanel videoPane = new JPanel();
		videoPane.setPreferredSize(new Dimension(900, 900));
		pl19.add(videoPane, BorderLayout.CENTER);
		videoPane.setLayout(new BorderLayout(0, 0));
		playerComponent = new EmbeddedMediaPlayerComponent();
		videoPane.add(playerComponent);

		// ���������п��Ʋ���
		// ������������
		JPanel pl20 = new JPanel();
		videoPane.add(pl20, BorderLayout.SOUTH);

		progressPanel = new JPanel();
		pl20.add(progressPanel, BorderLayout.NORTH);

		// ��ӽ�����
		progress = new JProgressBar();
		progressPanel.add(progress);
		progress.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int x = e.getX();
				PlayerMain.jumpTo((float) x / progress.getWidth());
			}
		});
		progress.setStringPainted(true);

		JPanel pl21 = new JPanel(); // ʵ�������ư�ť����
		pl20.add(pl21, BorderLayout.SOUTH);

		// ֹͣ��ť
		btnStop = new JButton("ֹͣ");
		btnStop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				PlayerMain.stop();
			}
		});
		pl21.add(btnStop);

		// ���Ű�ť
		btnPlay = new JButton("����");
		btnPlay.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				PlayerMain.play();
			}
		});
		pl21.add(btnPlay);

		// ��ͣ��ť
		btnPause = new JButton("��ͣ");
		btnPause.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				PlayerMain.pause();
			}
		});
		pl21.add(btnPause);

		// �������ƿ�
		slider = new JSlider();
		slider.setValue(80);
		slider.setMaximum(100);
		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				PlayerMain.setVol(slider.getValue());
			}
		});
		pl21.add(slider);

		jf.setContentPane(panel);
		jf.setLocationRelativeTo(null);
		jf.setVisible(true);
		jf.setJMenuBar(menuBar);
		jf.setVisible(true);
	}

	// ��ȡ����ý��ʵ����ĳ����Ƶ��
	public EmbeddedMediaPlayer getMediaPlayer() {
		return playerComponent.getMediaPlayer();
	}

	// ��ȡ������ʵ��
	public JProgressBar getProgressBar() {
		return progress;
	}

}
