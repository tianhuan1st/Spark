
package cn.bmwm.navigate;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListenerAdapter;
import org.jivesoftware.spark.ui.MessageEventListener;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**

 */
public class NavigatePlugin implements Plugin {

    /**
     * Called after Spark is loaded to initialize the new plugin.
     */
    public void initialize() {
        // Retrieve ChatManager from the SparkManager
        final ChatManager chatManager = SparkManager.getChatManager();

        // Add to a new ChatRoom when the ChatRoom opens.
        chatManager.addChatRoomListener(new ChatRoomListenerAdapter() {
            public void chatRoomOpened(ChatRoom room) {
                // only do the translation for single chat
                if (room instanceof ChatRoomImpl) {
                    final ChatRoomImpl roomImpl = (ChatRoomImpl) room;

                    // Create a new ChatRoomButton.
                    final JComboBox translatorBox = new JComboBox(TranslatorUtil.TranslationType.getTypes());

                    translatorBox.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            // Set the focus back to the message box.
                            roomImpl.getChatInputEditor().requestFocusInWindow();
                        }
                    });

                    roomImpl.addChatRoomComponent(translatorBox);

                    // do the translation for outgoing messages.
                    final MessageEventListener messageListener = new MessageEventListener() {
                        public void sendingMessage(Message message) {
                            String currentBody = message.getBody();
                            String oldBody = message.getBody();
                            TranslatorUtil.TranslationType type =
                                    (TranslatorUtil.TranslationType) translatorBox.getSelectedItem();
                            if (type != null && type != TranslatorUtil.TranslationType.None) {
                                message.setBody(null);
                                currentBody = TranslatorUtil.translate(currentBody, type);
                                if (oldBody.equals(currentBody.substring(0, currentBody.length() - 1))) {
                                    chatManager.getChatRoom(org.jivesoftware.smack.util.StringUtils.parseBareAddress(message.getTo())).getTranscriptWindow().insertNotificationMessage("Could not translate: " + currentBody, ChatManager.ERROR_COLOR);
                                } else {
                                    chatManager.getChatRoom(org.jivesoftware.smack.util.StringUtils.parseBareAddress(message.getTo())).getTranscriptWindow().insertNotificationMessage("-> " + currentBody, Color.gray);
                                    message.setBody(currentBody);
                                }
                            }
                        }


                        public void receivingMessage(Message message) {
                            // do nothing
                        }
                    };
                    roomImpl.addMessageEventListener(messageListener);
                }
            }
        });
    }

    /**
     * Called when Spark is shutting down to allow for persistence of information
     * or releasing of resources.
     */
    public void shutdown() {

    }

    /**
     * Return true if the Spark can shutdown on users request.
     *
     * @return true if Spark can shutdown on users request.
     */
    public boolean canShutDown() {
        return true;
    }

    /**
     * Is called when a user explicitly asked to uninstall this plugin.
     * The plugin owner is responsible to clean up any resources and
     * remove any components install in Spark.
     */
    public void uninstall() {
        // Remove all resources belonging to this plugin.
    }
}
