import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class GMusic_Front extends JFrame {
	private static Connection conn;
	File dbfile;
	public File[] fileList;
	public Statement stat;
	public ResultSet rs;
	/**
	 * @author vishnu
	 *
	 */
	private class ButtonListener implements ActionListener {

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override

		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == btnSelectDB){
				int returnVal = fc.showOpenDialog(GMusic_Front.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					dbfile = fc.getSelectedFile();
					lblDbFile.setText(dbfile.getAbsolutePath());
					try {
						conn = DriverManager.getConnection("jdbc:sqlite:"+dbfile.getAbsolutePath());
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//This is where a real application would open the file.
				} else {
				}
			}
			else if (e.getSource() == btnSelectMusicDirectory){
				fc2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fc2.showOpenDialog(GMusic_Front.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					cache_dir = fc2.getSelectedFile();
					fileList = cache_dir.listFiles();
					for(int i = 0; i<fileList.length;i++){
						String curFileName = fileList[i].getName();

						System.out.println(curFileName);

					}
					txtDirectory.setText(cache_dir.getAbsolutePath());
					//This is where a real application would open the file.
				} else {
				}
			}
			else{
				for(int i = 0; i<fileList.length;i++){
					File curFile = fileList[i];
					String curFileName = curFile.getName();

					System.out.println(curFileName);
					try {
						stat = conn.createStatement();
						String sql = "select TrackNumber, Album, Genre, Artist, Title from music where localcopytype = 200 and LocalCopyPath = \""+curFileName+"\";";
						System.out.println(sql);
						rs = stat.executeQuery(sql);
						while (rs.next()) {
							System.out.println("TrackNumber = " + rs.getString("TrackNumber"));
							System.out.println("Artist = " + rs.getString("Artist"));
							System.out.println("Album = " + rs.getString("Album"));
							System.out.println("Title = " + rs.getString("Title"));
						}

					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
			}
		}
	}

	final JFileChooser fc = new JFileChooser();
	final JFileChooser fc2 = new JFileChooser();

	private File cache_dir;

	private JPanel contentPane;

	private JButton btnSelectDB;

	private JLabel lblDbFile;

	private JButton btnSelectMusicDirectory;

	private JButton btnTag;

	private JTextArea txtDirectory;

	/**
	 * Launch the application.
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException {

		Class.forName("org.sqlite.JDBC");


		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GMusic_Front frame = new GMusic_Front();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GMusic_Front() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 291, 265);
		contentPane = new JPanel();
		System.out.println("Jpanel created");
		contentPane.setBackground(new Color(19,22,25));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		btnSelectDB = new JButton("Select .db File");
		btnSelectDB.addActionListener(new ButtonListener());
		btnSelectDB.setForeground(new Color(51,181,229));
		btnSelectDB.setBounds(45, 49, 199, 25);
		btnSelectDB.setBackground(new Color(73,73,74));
		contentPane.add(btnSelectDB);
		System.out.println("added button1");
		lblDbFile = new JLabel("");
		lblDbFile.setForeground(new Color(51,181,229));
		lblDbFile.setBounds(12, 91, 265, 15);
		contentPane.add(lblDbFile);

		btnSelectMusicDirectory = new JButton("Select Music Directory");
		btnSelectMusicDirectory.addActionListener(new ButtonListener());
		btnSelectMusicDirectory.setForeground(new Color(51,181,229));
		btnSelectMusicDirectory.setBounds(45, 123, 199, 25);
		btnSelectMusicDirectory.setBackground(new Color(73,73,74));
		contentPane.add(btnSelectMusicDirectory);

		JLabel lblTitle = new JLabel("Google Music Offline Tagger");
		lblTitle.setForeground(new Color(51,181,229));
		lblTitle.setBounds(45, 17, 199, 15);
		contentPane.add(lblTitle);

		btnTag = new JButton("Tag and Rename");
		btnTag.addActionListener(new ButtonListener());
		btnTag.setForeground(new Color(51,181,229));
		btnTag.setBackground(new Color(73,73,74));
		btnTag.setBounds(45, 197, 199, 25);
		contentPane.add(btnTag);

		txtDirectory = new JTextArea();
		txtDirectory.setEditable(false);
		txtDirectory.setLineWrap(true);
		txtDirectory.setForeground(new Color(51,181,229));
		txtDirectory.setBackground(new Color(19,22,25));
		txtDirectory.setBounds(12, 148, 265, 37);
		contentPane.add(txtDirectory);
	}
}
