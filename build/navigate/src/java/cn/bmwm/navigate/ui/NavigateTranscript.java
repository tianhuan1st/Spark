package cn.bmwm.navigate.ui;

import cn.bmwm.navigate.util.NavigateResources;
import org.jdesktop.swingx.calendar.DateUtils;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.BackgroundPanel;
import org.jivesoftware.spark.ui.VCardPanel;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.transcripts.ChatTranscript;
import org.jivesoftware.sparkimpl.plugin.transcripts.HistoryMessage;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

//import org.jivesoftware.resource.Res;

//import org.jivesoftware.resource.Res;

/**

 */
public class NavigateTranscript  {

	private Semaphore token = new Semaphore(1);
	private int pageIndex = 0;
	private int maxPages = 0;
	private final String mode_driving = "message.search.mode.driving";
	private final String mode_walking = "message.search.mode.walking";
	private final String mode_transit = "message.search.mode.transit";//公交
	private String searchPeriod = "";
	private List<String> modes = new ArrayList<String>();
	//private final timerTranscript transcriptTask = new timerTranscript();
//	private JLabel pageCounter = new JLabel("0 / 0");
/*	private JButton pageLeft = new JButton("<");
	private JButton pageRight = new JButton(">");*/
	private String jid = null;
	private SimpleDateFormat notificationDateFormatter = null;
	private SimpleDateFormat messageDateFormatter = null;
	private final AtomicBoolean isInitialized = new AtomicBoolean(false);

	//private LocalPreferences pref = SettingsManager.getLocalPreferences();
	private final JComboBox modeChooser= new JComboBox() ;
	private final JPanel filterPanel = new JPanel();
	private final JPanel mainPanel = new BackgroundPanel();
	private final JPanel searchPanel = new BackgroundPanel();
	//private final JPanel navigatorPanel = new JPanel();
	private final JPanel overTheTopPanel = new BackgroundPanel();
	private final JPanel controlPanel = new BackgroundPanel();
	private VCardPanel vacardPanel = null;
    private final JLabel startLabel = new JLabel(NavigateResources.getString("message.search.start"));
    private final JLabel endLabel = new JLabel(NavigateResources.getString("message.search.end"));
	private final JTextField searchField = new JTextField(25);
	private final JEditorPane window = new JEditorPane();
	private final JScrollPane pane = new JScrollPane(window);
	private final JFrame frame = new JFrame(NavigateResources.getString("navigate.title"));//
	private final StringBuilder builder = new StringBuilder();
	private List<ChatTranscript> searchFilteredList = new ArrayList<ChatTranscript>();
	private List<ChatTranscript> dateFilteredUnfilteredList = new ArrayList<ChatTranscript>();
    private AtomicBoolean isHistoryLoaded = new AtomicBoolean(false);
    private boolean sortDateAsc = false; 


	/**
	 * Open the Transcript with the given formatter.
	 * @param notificationDateFormatter the formatter for the notifications
	 * @param messageDateFormatter the formatter for dates
	 */


    public NavigateTranscript(SimpleDateFormat notificationDateFormatter, SimpleDateFormat messageDateFormatter) {
        this.notificationDateFormatter = notificationDateFormatter;
        this.messageDateFormatter = messageDateFormatter;
    }

    public NavigateTranscript() {

    }

	/**
	 * Show the History for the given Contact.
	 * @param jid the JID of the current transcript
	 */
	public void showHistory(String jid) {
		vacardPanel = new VCardPanel(jid);
		frame.setTitle("title.history.for");
		this.jid = jid;
	//	this.start();
	}





	/**
     * Builds html string with the stored messages
     * @return String containing the messages as html 
     */
    public final String buildString(List<HistoryMessage> messages){
    	StringBuilder builder = new StringBuilder();
    	final String personalNickname = SparkManager.getUserManager().getNickname();
		Date lastPost = null;
		String lastPerson = null;
		boolean initialized = false;

		for (HistoryMessage message : messages) {
			String color = "blue";

			String from = message.getFrom();
			String nickname = SparkManager.getUserManager()
					.getUserNicknameFromJID(message.getFrom());
			String body = org.jivesoftware.spark.util.StringUtils
					.escapeHTMLTags(message.getBody());
			if (nickname.equals(message.getFrom())) {
				String otherJID = StringUtils.parseBareAddress(message
						.getFrom());
				String myJID = SparkManager.getSessionManager()
						.getBareAddress();

				if (otherJID.equals(myJID)) {
					nickname = personalNickname;
				} else {
					nickname = StringUtils.parseName(nickname);
				}
			}

			if (!StringUtils.parseBareAddress(from).equals(
					SparkManager.getSessionManager().getBareAddress())) {
				color = "red";
			}

			long lastPostTime = lastPost != null ? lastPost.getTime() : 0;

			int diff = 0;
			if (DateUtils.getDaysDiff(lastPostTime, message.getDate()
					.getTime()) != 0) {
				diff = DateUtils.getDaysDiff(lastPostTime, message
						.getDate().getTime());
			} else {
				diff = DateUtils.getDayOfWeek(lastPostTime)
						- DateUtils.getDayOfWeek(message.getDate()
								.getTime());
			}

			if (diff != 0) {
				if (initialized) {
					builder.append("<tr><td><br></td></tr>");
				}
				builder.append(
						"<tr><td colspan=2><font size=4 color=gray><b><u>")
						.append(notificationDateFormatter.format(message
								.getDate()))
						.append("</u></b></font></td></tr>");
				lastPerson = null;
				initialized = true;
			}

			String value = "["
					+ messageDateFormatter.format(message.getDate())
					+ "]&nbsp;&nbsp;  ";

			boolean newInsertions = lastPerson == null
					|| !lastPerson.equals(nickname);
			if (newInsertions) {
				builder.append("<tr valign=top><td colspan=2 nowrap>");
				builder.append("<font size=4 color='").append(color).append("'><b>");
				builder.append(nickname);
				builder.append("</b></font>");
				builder.append("</td></tr>");
			}

			builder.append("<tr valign=top><td align=left nowrap>");
			builder.append(value);
			builder.append("</td><td align=left>");
			builder.append(body);

			builder.append("</td></tr>");

			lastPost = message.getDate();
			lastPerson = nickname;
		}
		builder.append("</table></body></html>");

		return builder.toString();
	}

    /**
     * If a new page is loaded or the search is 
     * changed, displays the current page again.
     */
	private synchronized void display() {
		try {
			token.acquire();

		/*	if ((searchFilteredList.size() > 0) && (pageIndex <= searchFilteredList.size())) {
				builder.append(buildString(searchFilteredList.get(pageIndex-1).getMessages()));

			}else{
				// Handle no history
				builder.replace(0, builder.length(), "");
				builder.append("<b>")
						.append("message.no.history.found"))
						.append("</b>");
			}*/
			window.setText(builder.toString());
			builder.replace(0, builder.length(), "");
			if (window.getText().length() > 0) window.setCaretPosition(0);
			//pageCounter.setText(pageIndex + " / " + maxPages);
			token.release();
		} catch (InterruptedException e) {
			Log.error(e);
			e.printStackTrace();
		}
	}







	/**
	 * Check if the given String represents a valid mode
	 * @param p the mode, that have to be checked
	 * @return true if valid, false if invalid
	 */
	private int getPeriodIndex(String p){
		int result = 0;
		for (int i = 0; i < modes.size(); i++){
			if (p.equals(modes.get(i))) {
				result = i;
				break;
			}
		}
		return result;
	}

	/**
	 * Set the layout settings
	 */
	public void finished() {
/*		pageLeft.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//pageLeft();
			}
		});
		pageRight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//pageRight();
			}
		});*/
	    modeChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//handlePeriodChange (modes.get(modeChooser.getSelectedIndex()));
	        }
	      });

		// add search text input
		searchPanel.setLayout(new GridBagLayout());
		//navigatorPanel.setLayout(new GridBagLayout());
		controlPanel.setLayout(new BorderLayout());
		filterPanel.setLayout(new GridBagLayout());
		mainPanel.setLayout(new BorderLayout());

		// the list of modes

		modes.add(mode_driving);
		modes.add(mode_walking);
		modes.add(mode_transit);

		// get the default preferences for the search mode 
		//int index = getPeriodIndex(pref.getSearchPeriod(modes.get(0)));

		for (String mode : modes){//导航模式
			modeChooser.addItem(NavigateResources.getString(mode));

		}

		modeChooser.setToolTipText(NavigateResources.getString("message.search.modeChooser"));
/*		pageCounter.setToolTipText("message.search.page.counter");
		pageRight.setToolTipText("message.search.page.right");
		pageLeft.setToolTipText("message.search.page.left");*/
		searchField.setText(NavigateResources.getString("message.search.start"));
		searchField.setToolTipText("message.search.for.history");
		searchField.setForeground((Color) UIManager
				.get("TextField.lightforeground"));

/*		searchPanel.add(vacardPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(1, 5, 1, 1), 0, 0));*/

		filterPanel.add(modeChooser,new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(1, 5, 1, 1), 0, 0));

        filterPanel.add(startLabel , new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(1, 5, 1, 1), 0, 0));

        filterPanel.add(endLabel, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(1, 5, 1, 1), 0, 0));

		filterPanel.add(searchField, new GridBagConstraints(2, 0,
				GridBagConstraints.REMAINDER, 1, 1.0, 1.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(1, 1, 6, 1), 0, 0));

	/*	navigatorPanel.add(pageLeft, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(1, 5, 1, 1), 0, 0));
		navigatorPanel.add(pageCounter, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(1, 5, 1, 1), 0, 0));
		navigatorPanel.add(pageRight, new GridBagConstraints(2, 0,
				GridBagConstraints.REMAINDER, 1, 1.0, 1.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(1, 1, 6, 1), 0, 0));*/

		controlPanel.add(filterPanel, BorderLayout.NORTH);
		//controlPanel.add(navigatorPanel, BorderLayout.SOUTH);

		overTheTopPanel.setLayout(new BorderLayout());
		overTheTopPanel.add(searchPanel,BorderLayout.NORTH);
		overTheTopPanel.add(controlPanel,BorderLayout.SOUTH);

		mainPanel.add(overTheTopPanel, BorderLayout.NORTH);

		window.setEditorKit(new HTMLEditorKit());
		window.setBackground(Color.white);
		pane.getVerticalScrollBar().setBlockIncrement(200);
		pane.getVerticalScrollBar().setUnitIncrement(20);

		mainPanel.add(pane, BorderLayout.CENTER);

/*		frame.setIconImage(SparkRes.getImageIcon(SparkRes.HISTORY_16x16)
				.getImage());*/
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		frame.pack();
		frame.setSize(600, 400);
		window.setCaretPosition(0);
		window.requestFocus();
		GraphicUtils.centerWindowOnScreen(frame);
		frame.setVisible(true);
		window.setEditable(false);

		builder.append("<html><body><table cellpadding=0 cellspacing=0>");

		searchField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				/*if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					TaskEngine.getInstance().schedule(transcriptTask, 10);
					searchField.requestFocus();
				}*/
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});
		searchField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				searchField.setText("");
				searchField.setForeground((Color) UIManager
						.get("TextField.foreground"));
			}

			public void focusLost(FocusEvent e) {
				searchField.setForeground((Color) UIManager
						.get("TextField.lightforeground"));
				searchField.setText("message.search.for.history");
			}
		});

		// after initializing the mode, we can load the history
		isInitialized.set(true);
	//	modeChooser.setSelectedIndex(index);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				window.setText("");
			}

			@Override
			public void windowClosed(WindowEvent e) {
				frame.removeWindowListener(this);
				frame.dispose();
			//	transcriptTask.cancel();
				searchPanel.remove(vacardPanel);
			}
		});
	}




    public static void main(String[] args) {

        NavigateTranscript transcript = new NavigateTranscript();
        transcript.finished();
        // transcript.showHistory("youshengrong@im.js.todaysoft.com.cn");
    }

}
