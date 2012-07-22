import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;

public class GMusic_Front extends JFrame {
	private static Connection conn;
	File dbfile;
	public File[] fileList;
	public Statement stat;
	public ResultSet rs;
	public ResultSet rs2;
	public String artwork;
	public String trackNum;
	public String artist;
	public String album;
	public String title;
	public String albumID;
	public String albumArtist;
	public String genre;
	public Path p1;
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
					File curTrackFile = fileList[i];
					String curTrackFileName = curTrackFile.getName();

					System.out.println(curTrackFileName);
					try {
						stat = conn.createStatement();
						String sqlMetaData = "select TrackNumber, AlbumArtist, Album, Genre, Artist, Title, AlbumId from music where localcopytype = 200 and LocalCopyPath = \""+curTrackFileName+"\";";
						System.out.println(sqlMetaData);
						rs = stat.executeQuery(sqlMetaData);
						while (rs.next()) {
							trackNum = rs.getString("TrackNumber");
							System.out.println("TrackNumber = " + trackNum);
							
							artist = rs.getString("Artist");
							System.out.println("Artist = " + artist);
							
							albumArtist = rs.getString("AlbumArtist");
							System.out.println("AlbumArtist = " + albumArtist);
							
							album = rs.getString("Album");
							System.out.println("Album = " + album);
							
							title = rs.getString("Title");
							System.out.println("Title = " + title);
							
							albumID = rs.getString("AlbumId");
							System.out.println("AlbumId = " + albumID);
							
							genre = rs.getString("Genre");
							System.out.println("Genre = "+genre);
							
							MP3File f = (MP3File) AudioFileIO.read(curTrackFile);
							Tag tag = f.getTagOrCreateAndSetDefault();
							
							f.commit();
							
							tag.setField(FieldKey.ARTIST,artist);
							tag.setField(FieldKey.ALBUM,album);
							tag.setField(FieldKey.ALBUM_ARTIST,albumArtist);
							tag.setField(FieldKey.TITLE,title);
							tag.setField(FieldKey.TRACK,trackNum);
							tag.setField(FieldKey.GENRE,genre);
							f.commit();
							
							
							
							String sqlArtWork = "select LocalLocation from artwork where AlbumId = "+rs.getString("AlbumId")+";";
							System.out.println(sqlArtWork);
							rs2 = stat.executeQuery(sqlArtWork);
							
							while(rs2.next()){
								artwork = rs2.getString("LocalLocation");
								System.out.println("Artwork = " + artwork);
								
								p1 = Paths.get(curTrackFile.getAbsolutePath());
								System.out.println("this is the current path: " + p1.toString());
								Path artworkPath = p1.getParent().getParent();
								artworkPath = Paths.get(artworkPath.toString(), "artwork",artwork);
								System.out.println("This is the artwork path: "+ artworkPath.toString());
								
								File artworkFile = new File(artworkPath.toString());
								
								Artwork artwork_to_write = ArtworkFactory.createArtworkFromFile(artworkFile);
								tag.setField(artwork_to_write);
								f.commit();
								
								
							}
							
							Path newPath = Paths.get(p1.getParent().toString(),trackNum+"."+title+".mp3");
							File newFileName = new File(newPath.toString());
							boolean success = curTrackFile.renameTo(newFileName);
						}

					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (CannotReadException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					} catch (IOException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					} catch (TagException e4) {
						// TODO Auto-generated catch block
						e4.printStackTrace();
					} catch (ReadOnlyFileException e5) {
						// TODO Auto-generated catch block
						e5.printStackTrace();
					} catch (InvalidAudioFrameException e6) {
						// TODO Auto-generated catch block
						e6.printStackTrace();
					} catch (CannotWriteException e7) {
						// TODO Auto-generated catch block
						e7.printStackTrace();
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
