/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 * 
 * Distributable under LGPL license. See terms of license at gnu.org.
 */

package org.atalk.android.gui.chat;

import net.java.sip.communicator.service.protocol.FileTransfer;
import net.java.sip.communicator.service.protocol.PresenceStatus;
import net.java.sip.communicator.service.protocol.ProtocolProviderService;
import net.java.sip.communicator.service.protocol.event.MessageListener;

import org.jivesoftware.smackx.chatstates.ChatState;
import org.jxmpp.jid.EntityBareJid;

import java.io.File;

/**
 * The <tt>ChatTransport</tt> is an abstraction of the transport method used when sending messages,
 * making calls, etc. through the chat fragment window.
 * A interface class to which the metaContactChat, conference chats are being implemented.
 *
 * @author Yana Stamcheva
 * @author Eng Chong Meng
 */
public interface ChatTransport
{
	/**
	 * Returns the descriptor object of this ChatTransport.
	 *
	 * @return the descriptor object of this ChatTransport
	 */
	public Object getDescriptor();

    /**
     * Returns <code>true</code> if this chat transport supports instant
     * messaging, otherwise returns <code>false</code>.
     *
     * @return <code>true</code> if this chat transport supports instant
     * messaging, otherwise returns <code>false</code>
     */
    public boolean allowsInstantMessage();

	/**
	 * Returns <tt>true</tt> if this chat transport supports message corrections and false otherwise.
	 *
	 * @return <code>true</code> if this chat transport supports message corrections and false otherwise.
	 */
	public boolean allowsMessageCorrections();

    /**
     * Returns <code>true</code> if this chat transport supports sms
     * messaging, otherwise returns <code>false</code>.
     *
     * @return <code>true</code> if this chat transport supports sms
     * messaging, otherwise returns <code>false</code>
     */
    public boolean allowsSmsMessage();

    /**
     * Returns <code>true</code> if this chat transport supports chat state
     * notifications, otherwise returns <code>false</code>.
     *
     * @return <code>true</code> if this chat transport supports chat state
     * notifications, otherwise returns <code>false</code>
     */
    public boolean allowsChatStateNotifications();

    /**
     * Returns the name of this chat transport. This is for example the name of
     * the contact in a single chat mode and the name of the chat room in the
     * multi-chat mode.
     *
     * @return The name of this chat transport.
     */
    public String getName();

    /**
     * Returns the display name of this chat transport. This is for example the
     * name of the contact in a single chat mode and the name of the chat room
     * in the multi-chat mode.
     *
     * @return The display name of this chat transport.
     */
    public String getDisplayName();

    /**
     * Returns the resource name of this chat transport. This is for example the
     * name of the user agent from which the contact is logged.
     *
     * @return The display name of this chat transport resource.
     */
    public String getResourceName();

    /**
     * Indicates if the display name should only show the resource.
     *
     * @return <tt>true</tt> if the display name shows only the resource,
     * <tt>false</tt> - otherwise
     */
    public boolean isDisplayResourceOnly();

	/**
	 * Returns the presence status of this transport.
	 *
	 * @return the presence status of this transport.
	 */
	public PresenceStatus getStatus();

	/**
	 * Returns the <tt>ProtocolProviderService</tt>, corresponding to this chat transport.
	 *
	 * @return the <tt>ProtocolProviderService</tt>, corresponding to this chat transport.
	 */
	public ProtocolProviderService getProtocolProvider();

	/**
     * Sends the given instant message trough this chat transport, by specifying
     * the mime type (html or plain text).
	 *
	 * @param message
	 *        The message to send.
	 * @param encType
	 *        The encType of the message to send: 1=text/html or 0=text/plain.
	 *        @see ChatMessage Encryption Type
	 * @throws Exception
	 *         if the send doesn't succeed
	 */
	public void sendInstantMessage(String message, int encType)
		throws Exception;

	/**
     * Sends <tt>message</tt> as a message correction through this transport,
     * specifying the mime type (html or plain text) and the id of the
	 *
	 * @param message
	 *        The message to send.
	 * @param encType
	 *        The encType of the message to send: 1=text/html or 0=text/plain.
	 *        @see ChatMessage Encryption Type
	 * @param correctedMessageUID
	 *        The ID of the message being corrected by this message.
	 */
	public void correctInstantMessage(String message, int encType, String correctedMessageUID);

	/**
	 * Determines whether this chat transport supports the supplied content type
	 *
	 * @param encType
	 *        the encryption type we want to check
	 * @return <tt>true</tt> if the chat transport supports it and <tt>false</tt> otherwise.
	 */
	public boolean isContentTypeSupported(int encType);

	/**
	 * Whether a dialog need to be opened so the user can enter the destination number.
	 * 
	 * @return <tt>true</tt> if dialog needs to be open.
	 */
	public boolean askForSMSNumber();

	/**
	 * Sends the given SMS message trough this chat transport.
	 *
	 * @param phoneNumber
	 *        the phone number to which to send the message
	 * @param message
	 *        The message to send.
	 * @throws Exception
	 *         if the send doesn't succeed
	 */
	public void sendSmsMessage(String phoneNumber, String message)
		throws Exception;

	/**
	 * Sends the given SMS message trough this chat transport, leaving the transport to choose the destination.
	 *
	 * @param message
	 *        The message to send.
	 * @throws Exception
	 *         if the send doesn't succeed
	 */
	public void sendSmsMessage(String message)
		throws Exception;

	/**
     * Sends the given SMS multimedia message trough this chat transport,
     * leaving the transport to choose the destination.
	 *
	 * @param file
	 *        the file to send
	 * @throws Exception
	 *         if the send doesn't succeed
	 */
	public FileTransfer sendMultimediaFile(File file)
		throws Exception;

	/**
	 * Sends a chat state notification.
	 *
	 * @param chatState
	 *        the chat state notification to send
	 *
	 */
	public void sendChatStateNotification(ChatState chatState);

	/**
	 * Sends the given file trough this chat transport.
	 *
	 * @param file
	 *        the file to send
	 * @return the <tt>FileTransfer</tt> charged to transfer the given <tt>file</tt>.
	 * @throws Exception
	 *         if the send doesn't succeed
	 */
	public FileTransfer sendFile(File file)
		throws Exception;

	/**
	 * Returns the maximum file length supported by the protocol in bytes.
	 * 
	 * @return the file length that is supported.
	 */
	public long getMaximumFileLength();

	/**
	 * Invites a contact to join this chat.
	 *
	 * @param contactAddress
	 *        the address of the contact we invite
	 * @param reason
	 *        the reason for the invite
	 */
	public void inviteChatContact(EntityBareJid contactAddress, String reason);

	/**
     * Returns the parent session of this chat transport. A <tt>ChatSession</tt>
     * could contain more than one transports.
	 *
	 * @return the parent session of this chat transport
	 */
	public ChatSession getParentChatSession();

	/**
	 * Adds an sms message listener to this chat transport.
	 *
	 * @param l
	 *        The message listener to add.
	 */
	public void addSmsMessageListener(MessageListener l);

	/**
	 * Adds an instant message listener to this chat transport.
	 *
	 * @param l
	 *        The message listener to add.
	 */
	public void addInstantMessageListener(MessageListener l);

	/**
	 * Removes the given sms message listener from this chat transport.
	 *
	 * @param l
	 *        The message listener to remove.
	 */
	public void removeSmsMessageListener(MessageListener l);

	/**
	 * Removes the instant message listener from this chat transport.
	 *
	 * @param l
	 *        The message listener to remove.
	 */
	public void removeInstantMessageListener(MessageListener l);

	/**
	 * Disposes this chat transport.
	 */
	public void dispose();
}
