/*
 * $Id: CommuneMessageBusinessBean.java,v 1.4.2.3 2006/02/14 17:50:54 palli Exp $
 *
 * Copyright (C) 2002 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package se.idega.idegaweb.commune.message.business;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import se.idega.idegaweb.commune.business.CommuneUserBusiness;
import se.idega.idegaweb.commune.message.data.MessageHandlerInfo;
import se.idega.idegaweb.commune.message.data.MessageHandlerInfoHome;
import se.idega.idegaweb.commune.message.data.PrintMessage;
import se.idega.idegaweb.commune.message.data.PrintedLetterMessage;
import se.idega.idegaweb.commune.message.data.PrintedLetterMessageHome;
import se.idega.idegaweb.commune.message.data.SystemArchivationMessage;
import se.idega.idegaweb.commune.message.data.SystemArchivationMessageHome;
import se.idega.idegaweb.commune.message.data.UserMessage;
import se.idega.idegaweb.commune.message.data.UserMessageHome;
import com.idega.block.process.business.CaseBusiness;
import com.idega.block.process.data.Case;
import com.idega.block.process.data.CaseCode;
import com.idega.block.process.message.business.MessageBusiness;
import com.idega.block.process.message.business.MessageBusinessBean;
import com.idega.block.process.message.business.MessageTypeManager;
import com.idega.block.process.message.data.Message;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.component.data.ICObject;
import com.idega.core.contact.data.Email;
import com.idega.core.file.data.ICFile;
import com.idega.data.IDOCreateException;
import com.idega.data.IDOException;
import com.idega.data.IDOStoreException;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.idegaweb.IWPropertyList;
import com.idega.idegaweb.IWUserContext;
import com.idega.user.business.UserBusiness;
import com.idega.user.business.UserProperties;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;

/**
 * @author Anders Lindman , <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class CommuneMessageBusinessBean extends MessageBusinessBean implements CommuneMessageBusiness, MessageBusiness {

   
  private final static String IW_BUNDLE_IDENTIFIER = "se.idega.idegaweb.commune";
	public static final String MESSAGE_PROPERTIES = "message_properties";
	public static final String MAIL_PROPERTIES = "mail_properties";

	private static String DEFAULT_SMTP_MAILSERVER="mail.agurait.com";
	private static String PROP_SYSTEM_SMTP_MAILSERVER="messagebox_smtp_mailserver";
	private static String PROP_MESSAGEBOX_FROM_ADDRESS="messagebox_from_mailaddress";
	private static String PROP_SYSTEM_FORCED_RECEIVER="messagebox_forced_receiver_address";
	private static String PROP_SYSTEM_BCC_RECEIVER="messagebox_bcc_receiver_address";
	private static String DEFAULT_MESSAGEBOX_FROM_ADDRESS="messagebox@idega.com";
	
	public static final String USER_PROP_SEND_TO_MESSAGE_BOX = "msg_send_box";
	public static final String USER_PROP_SEND_TO_EMAIL = "msg_send_email";

	public CommuneMessageBusinessBean() {
		MessageTypeManager typeManager = MessageTypeManager.getInstance();
		typeManager.addDataClassForType(MessageConstants.TYPE_USER_MESSAGE, UserMessage.class);
		//typeManager.addDataClassForType(MessageConstants.TYPE_SYSTEM_PRINT_MAIL_MESSAGE, PrintedLetterMessage.class);
	}
	
	private UserMessageHome getUserMessageHome()throws RemoteException{
	    return (UserMessageHome) this.getIDOHome(UserMessage.class);
	}
	
	protected PrintedLetterMessageHome getPrintedLetterMessageHome(){
		try{
			return (PrintedLetterMessageHome) this.getIDOHome(PrintedLetterMessage.class);
		}
		catch(RemoteException rme){
			throw new IBORuntimeException(rme);	
		}
	}
	
	protected SystemArchivationMessageHome getSystemArchivationMessageHome(){
		try{
			return (SystemArchivationMessageHome) this.getIDOHome(SystemArchivationMessage.class);
		}
		catch(RemoteException rme){
			throw new IBORuntimeException(rme);	
		}
	}
	
	public void deleteUserMessage(int messageID) {
		try {
			Message message = getUserMessage(messageID);
			changeCaseStatus(message, getCaseStatusInactive().getPrimaryKey().toString(), message.getOwner());	
		}
		catch (FinderException fe) {
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
	
	private String getTypeUserMessage() {
		return MessageConstants.TYPE_USER_MESSAGE;
	}
	
	private String getTypeMailMessage() {
		return MessageConstants.TYPE_SYSTEM_PRINT_MAIL_MESSAGE;
	}
	
	private String getTypeArchivationMessage() {
		return MessageConstants.TYPE_SYSTEM_PRINT_ARCHIVATION_MESSAGE;
	}
	
	public CaseCode getCaseCodeSystemArchivationMessage()throws RemoteException,FinderException{
		return getCaseBusiness().getCaseCode(getTypeArchivationMessage());	
	}
	
	public CaseCode getCaseCodeUserMessage()throws RemoteException,FinderException{
		return getCaseBusiness().getCaseCode(getTypeUserMessage());	
	}
	
	public CaseCode getCaseCodePrintedLetterMessage()throws RemoteException,FinderException{
		return getCaseBusiness().getCaseCode(getTypeMailMessage());	
	}

	protected CaseBusiness getCaseBusiness(){
		return this;	
	}

	public Message getUserMessage(int messageId) throws FinderException, RemoteException {
		return getMessage(getTypeUserMessage(), new Integer(messageId));
	}

	public int getNumberOfMessages(User user) throws Exception {
		String[] validStatuses = { getCaseStatusOpen().getStatus(), getCaseStatusGranted().getStatus() };
		return getUserMessageHome().getNumberOfMessages(user,validStatuses);
	}
	
	public int getNumberOfNewMessages(User user) throws IDOException {
		try {
			String[] validStatuses = { getCaseStatusOpen().getStatus() };
			return getUserMessageHome().getNumberOfMessages(user,validStatuses);
		}
		catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}
	}
	
	public int getNumberOfMessages(User user, Collection groups) throws Exception {
		String[] validStatuses = { getCaseStatusOpen().getStatus(), getCaseStatusGranted().getStatus() };
		return getUserMessageHome().getNumberOfMessages(user,groups,validStatuses);
	}
	

	public Collection findMessages(User user) throws Exception {
		String[] validStatuses = { getCaseStatusOpen().getStatus(), getCaseStatusGranted().getStatus() };
		return getUserMessageHome().findMessages(user,validStatuses);
	}
	
	public Collection findMessages(User user, int numberOfEntries, int startingEntry) throws Exception {
		String[] validStatuses = { getCaseStatusOpen().getStatus(), getCaseStatusGranted().getStatus() };
		return getUserMessageHome().findMessages(user,validStatuses,numberOfEntries,startingEntry);
	}
	
	public Collection findMessages(User user, Collection groups, int numberOfEntries, int startingEntry) throws Exception {
		String[] validStatuses = { getCaseStatusOpen().getStatus(), getCaseStatusGranted().getStatus() };
		return getUserMessageHome().findMessages(user,groups,validStatuses,numberOfEntries,startingEntry);
	}
	
	public Collection findMessages(Group group) throws Exception {
		String[] validStatuses = { getCaseStatusOpen().getStatus(), getCaseStatusGranted().getStatus() };
		return getUserMessageHome().findMessages(group, validStatuses);
	}	

	public Collection findMessages(Group group, int numberOfEntries, int startingEntry) throws Exception {
		String[] validStatuses = { getCaseStatusOpen().getStatus(), getCaseStatusGranted().getStatus() };
		return getUserMessageHome().findMessages(group,validStatuses,numberOfEntries,startingEntry);
	}
	
	public Message createUserMessage(User user, String subject, String body) {
		return createUserMessage(null, user, subject, body, true);
	}
	
	public Message createUserMessage(User user, String subject, String body, boolean sendLetter) {
		return createUserMessage(null, user, subject, body, sendLetter);
	}
	
	public Message createUserMessage(User user, String subject, Group handler, String body, boolean sendLetter) {
		return createUserMessage(null, user, null, handler, subject, body, sendLetter);
	}	
	
	public Message createUserMessage(User user, String subject, Group handler, String body, boolean sendLetter,String contentCode) {
		return createUserMessage(null, user, null, handler, subject, body, sendLetter,contentCode, false);
	}	
	
	public Message createUserMessage(User receiver, String subject, String body, User sender, boolean sendLetter) {
		return createUserMessage(null, receiver, sender, subject, body, sendLetter);
	}
	
	public Message createUserMessage(Case parentCase, User receiver, String subject, String body, boolean sendLetter) {
		return createUserMessage(parentCase, receiver, null, subject, body, sendLetter);
	}
	
	public Message createUserMessage(Case parentCase, User receiver, String subject, String body, boolean sendLetter, boolean alwaysSendLetter) {
		return createUserMessage(parentCase, receiver, null, null, subject, body, sendLetter, null, alwaysSendLetter);
	}
	
	public Message createUserMessage(Case parentCase, User receiver, String subject, String body, String letterBody, boolean sendLetter, boolean alwaysSendLetter) {
		return createUserMessage(parentCase, receiver, null, null, subject, body, letterBody, sendLetter, null, alwaysSendLetter);
	}
	
	public Message createUserMessage(Case parentCase, User receiver, String subject, String body, String letterBody, File attachment, boolean sendLetter, boolean alwaysSendLetter) {
		return createUserMessage(parentCase, receiver, null, null, subject, body, letterBody, attachment, sendLetter, null, alwaysSendLetter, true);
	}
	
	public Message createUserMessage(Case parentCase, User receiver, User sender, String subject, String body, boolean sendLetter) {
		return createUserMessage(parentCase, receiver, sender, null, subject, body, sendLetter);	
	}
	
	public Message createUserMessage(Case parentCase, User receiver, User sender, Group handler, String subject, String body, boolean sendLetter) {
		return createUserMessage(parentCase, receiver,sender,handler,subject,body,sendLetter,null, false);
	}
	public Message createUserMessage(Case parentCase, User receiver, User sender, Group handler, String subject, String body, boolean pSendLetterIfNoEmail,String contentCode) {
		return createUserMessage(parentCase, receiver,sender,handler,subject,body,pSendLetterIfNoEmail,contentCode, false);
	}

	public Message createUserMessage(Case parentCase, User receiver, User sender, Group handler, String subject, String body, boolean pSendLetterIfNoEmail,String contentCode, boolean alwaysSendLetter) {
		return createUserMessage(parentCase, receiver, sender, handler, subject, body, body, pSendLetterIfNoEmail, contentCode, alwaysSendLetter);
	}
	
	public Message createUserMessage(Case parentCase, User receiver, User sender, Group handler, String subject, String body, String letterBody, boolean pSendLetterIfNoEmail,String contentCode, boolean alwaysSendLetter) {
		return createUserMessage(parentCase, receiver, sender, handler, subject, body, letterBody, pSendLetterIfNoEmail, contentCode, alwaysSendLetter, true); 
	}

	public Message createUserMessage(Case parentCase, User receiver, User sender, Group handler, String subject, String body, String letterBody, boolean sendLetterIfNoEmail,String contentCode, boolean alwaysSendLetter, boolean sendMail) {
		return createUserMessage(parentCase, receiver, sender, handler, subject, body, letterBody, null, sendLetterIfNoEmail, contentCode, alwaysSendLetter, sendMail);
	}
	
	public Message createUserMessage(Case parentCase, User receiver, User sender, Group handler, String subject, String body, String letterBody, File attachment, boolean sendLetterIfNoEmail,String contentCode, boolean alwaysSendLetter, boolean sendMail) {
	    MessageValue value = new MessageValue();
	    value.setParentCase(parentCase);
	    value.setReceiver(receiver);
	    value.setSender(sender);
	    value.setHandler(handler);
	    value.setSubject(subject);
	    value.setBody(body);
	    value.setLetterBody(letterBody);
	    value.setSendLetterIfNoEmail(new Boolean(sendLetterIfNoEmail)); 
	    value.setContentCode(contentCode);
	    value.setAlwaysSendLetter(new Boolean(alwaysSendLetter));
	    value.setSendMail(new Boolean(sendMail));
	    value.setAttachment(attachment);
	    return createUserMessage(value);
	    
	}
	
	public Message createUserMessage(MessageValue msgValue){
		
		try {
			if (msgValue.getLetterBody() == null) {
			    msgValue.setLetterBody(msgValue.getBody());
			}
			Message message = null;
			boolean sendMail = getIfUserPreferesMessageByEmail(msgValue.getReceiver()) && msgValue.getSendMail().booleanValue();
			boolean sendToBox = getIfUserPreferesMessageInMessageBox(msgValue.getReceiver());
			boolean canSendEmail = getIfCanSendEmail();
			boolean sendLetterEvenWhenHavingEmail=getIfCreateLetterMessageHavingEmail();
			//By default: copies in-parameter value:
//			boolean doSendLetter=msgValue.sendLetterIfNoEmail.booleanValue();
			boolean doSendLetter=msgValue.getAlwaysSendLetter().booleanValue() | sendLetterEvenWhenHavingEmail;
			if (!canSendEmail) {
				doSendLetter |= msgValue.getSendLetterIfNoEmail().booleanValue();
			}
			
			if (sendToBox) {
			    msgValue.setMessageType(getTypeUserMessage());
				message = createMessage(msgValue);
			}

			if (sendMail) {
				boolean sendEmail = false;
				Email mail = ((UserBusiness)getServiceInstance(UserBusiness.class)).getUserMail(msgValue.getReceiver());	
				if (mail != null) {
					if (mail.getEmailAddress() != null)
						sendEmail = true;
				}
				
				if ( sendEmail ) {
					if (canSendEmail)
						try {
							sendMessage(mail.getEmailAddress(),msgValue.getSubject(),msgValue.getBody(),msgValue.getAttachment());
						}
						catch (Exception e) {
							doSendLetter |= msgValue.getSendLetterIfNoEmail().booleanValue();
							System.err.println("Couldn't send message to user via e-mail.");
						}
				} else {
					doSendLetter |= msgValue.getSendLetterIfNoEmail().booleanValue();
				}
				//else {
				//	if (pSendLetterIfNoEmail)
				//		createPrintedLetterMessage(parentCase, receiver, subject, body,null,contentCode);
				//}
			}
			//else {
			//	if (pSendLetterIfNoEmail)
			//		createPrintedLetterMessage(parentCase, receiver, subject, body,null,contentCode);
			//}

			if(doSendLetter){
				createPrintedLetterMessage(msgValue);
			}

			if (IWMainApplication.isDebugActive()) {
				System.out.println("[MessageBusiness] Creating user message with subject:" + msgValue.getSubject());
				System.out.println("[MessageBusiness] Body: " + msgValue.getBody());
				if (msgValue.getParentCase() != null)
					debug("[MessageBusiness] Parent case:" + msgValue.getParentCase().getClass().getName() + " (" + msgValue.getParentCase().getPrimaryKey().toString() + ")");
				System.out.println("[MessageBusiness] Receiver: " + msgValue.getReceiver().getName() + " (" + msgValue.getReceiver().getPrimaryKey().toString() + ")");
				if (msgValue.getSender() != null)
				    debug("[MessageBusiness] Sender: " + msgValue.getSender().getName() + " (" + msgValue.getSender().getPrimaryKey().toString() + ")");
				if (msgValue.getHandler() != null)
				    debug("[MessageBusiness] Handler: " + msgValue.getHandler().getName() + " (" + msgValue.getHandler().getPrimaryKey().toString() + ")");
			}

			//return message;
			return message;
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			return null;
		}
	}
	
	/**
	 * This property is for setting if to create letter messages even when the user has an email address.
	 * @return value of the set property. Default is false.
	 */
	protected boolean getIfCreateLetterMessageHavingEmail(){
		return getBundle().getBooleanProperty("create_letter_message_having_email",false);
	}

	public Message createUserMessage(int userID, String subject, String body) throws CreateException {
		User user;
		try {
			user = getUser(userID);
		}
		catch (FinderException fex) {
			throw new IDOCreateException(fex);
		}

		return createUserMessage(user, subject, body);
	}

	
	/**	 * @return Collection of PrintedLetterMessage that have already been printed	 */
	public Collection getPrintedLetterMessages()throws FinderException{
		return getPrintedLetterMessageHome().findAllPrintedLetters();	
	}
	
	/**
	 * @return Collection of PrintedLetterMessage that have already been printed
	 */
	public Collection getPrintedLetterMessagesByType(String type,int resultSize,int startingIndex)throws FinderException{
		return getPrintedLetterMessageHome().findPrintedLettersByType(type,resultSize,startingIndex);	
	}
	
	/**
	 * @return Collection of PrintedLetterMessage that have already been printed, created between dates
	 */
	public Collection getPrintedLetterMessagesByType(String type,IWTimestamp from,IWTimestamp to,int resultSize,int startingIndex)throws FinderException{
		return getPrintedLetterMessageHome().findPrintedLettersByType(type,from,to,resultSize,startingIndex);	
	}
	
	public Collection getSinglePrintedLetterMessagesByType(String type,IWTimestamp from,IWTimestamp to,int resultSize,int startingIndex)throws FinderException{
		return getPrintedLetterMessageHome().findSinglePrintedLettersByType(type,from,to,resultSize,startingIndex);	
	}
	/**
	 * @return Collection of PrintedLetterMessage that have not been printed
	 */	
	public Collection getUnPrintedLetterMessages()throws FinderException{
		return getPrintedLetterMessageHome().findAllUnPrintedLetters();	
	}
	
	/**
	 * @return Collection of PrintedLetterMessage that have not been printed
	 */	
	public Collection getUnPrintedLetterMessagesByType(String type,int resultSize,int startingIndex)throws FinderException{
		return getPrintedLetterMessageHome().findUnPrintedLettersByType(type,resultSize,startingIndex);	
	}
	
	/**
	 * @return Collection of PrintedLetterMessage that have not been printed
	 */	
	public Collection getUnPrintedLetterMessagesByType(String type,IWTimestamp from,IWTimestamp to,int resultSize,int startingIndex)throws FinderException{
		return getPrintedLetterMessageHome().findUnPrintedLettersByType(type,from,to,resultSize,startingIndex);	
	}
	
	public Collection getSingleUnPrintedLetterMessagesByType(String type,IWTimestamp from,IWTimestamp to,int resultSize,int startingIndex)throws FinderException{
		return getPrintedLetterMessageHome().findSingleUnPrintedLettersByType(type,from,to,resultSize,startingIndex);	
	}
		
	public Collection getSingleLettersByTypeAndStatus(String type,String status,IWTimestamp from,IWTimestamp to,int resultSize,int startingIndex)throws FinderException{
		return getPrintedLetterMessageHome().findSingleByTypeAndStatus(type,status,from,to,resultSize,startingIndex);	
	}
	
	public Collection getLettersByBulkFile(int file, String type , String status,int resultSize,int startingIndex)throws FinderException{
		return getPrintedLetterMessageHome().findByBulkFile(file,type,status,resultSize,startingIndex);	
	}
	
	/**
	 * Mark the status of the message so that it is printed.
	 * @param performer The User that makes the change
	 * @param message the message to be marked
	 */
	public void flagPrintedLetterAsPrinted(User performer,PrintedLetterMessage message) {
		String newCaseStatus=getCaseStatusReady().getStatus();
		super.changeCaseStatus(message,newCaseStatus,performer);
	}
	
	public void flagMessageAsPrinted(User performer,Message message) {
		String newCaseStatus=getCaseStatusReady().getStatus();
		super.changeCaseStatus(message,newCaseStatus,performer);
	}
	
	public void flagMessageAsUnPrinted(User performer,Message message) {
		String newCaseStatus=getCaseStatusOpen().getStatus();
		super.changeCaseStatus(message,newCaseStatus,performer);
	}
	
	public void  flagMessageWithStatus(User performer,Message message,String status) {
		super.changeCaseStatus(message,status,performer);
	}
	
	public void flagMessagesWithStatus(User performer, String[] msgKeys,String status)throws FinderException{
			for (int i = 0; i < msgKeys.length; i++) {
				super.changeCaseStatus( Integer.parseInt(msgKeys[i]),status,performer);
			}
		}
	

	public Message createPrintArchivationMessage(User user, String subject, String body) throws CreateException, RemoteException {
	    MessageValue msgValue = new MessageValue();
	    msgValue.setReceiver(user);
	    msgValue.setSubject(subject);
	    msgValue.setBody(body);
		Message message = createMessage(msgValue);
		return message;
	}

	public Message createPrintArchivationMessage(int userID, String subject, String body) throws CreateException, RemoteException {
		User user;
		try {
			user = getUser(userID);
		}
		catch (FinderException fex) {
			throw new IDOCreateException(fex);
		}

		return createPrintArchivationMessage(user, subject, body);
	}

	public SystemArchivationMessage createPrintArchivationMessage(User forUser, User creator, String subject, String body,ICFile attatchement) throws CreateException {
		int forUserID=-1;
		int creatorUserID=-1;
		int fileID=-1;
		try{
			forUserID=((Integer)forUser.getPrimaryKey()).intValue();
			creatorUserID=((Integer)creator.getPrimaryKey()).intValue();
			fileID=((Integer)attatchement.getPrimaryKey()).intValue();
		}
		catch(Exception e){
			throw new IDOCreateException(e);	
		}
		return createPrintArchivationMessage(forUserID,creatorUserID,subject,body,fileID);
	}

	public SystemArchivationMessage createPrintArchivationMessage(int forUserID, int creatorUserID, String subject, String body,int attatchementFileID) throws CreateException {

		/**
		 * @todo implement support for creator
		 */
		try{
		    MessageValue msgValue = new MessageValue();
		    msgValue.setMessageType(getTypeArchivationMessage());
		    msgValue.setReceiver(getUser(forUserID));
		    msgValue.setSubject(subject);
		    msgValue.setBody(body);
			Message message = createMessage(msgValue);
			SystemArchivationMessage saMessage = (SystemArchivationMessage)message;
			saMessage.setAttachedFile(attatchementFileID);
			message.store();
			return saMessage;
		}
		catch(Exception e){
			throw new IDOCreateException(e);	
		}

	}
	
	public PrintedLetterMessage createPrintedPasswordLetterMessage(User user, String subject, String body) throws CreateException {
		PrintedLetterMessageHome home = getPrintedLetterMessageHome();
		PrintedLetterMessage message = (PrintedLetterMessage) home.create();
		message.setOwner(user);
		message.setSubject(subject);
		message.setBody(body);
		message.setAsPasswordLetter();
		try {
			message.store();
		}
		catch (IDOStoreException idos) {
			throw new IDOCreateException(idos);
		}
		return message;
	}
	
	public PrintedLetterMessage createPasswordMessage(User user, String username,String password) throws CreateException {
		PrintedLetterMessageHome home = getPrintedLetterMessageHome();
		PrintedLetterMessage message = (PrintedLetterMessage) home.create();
		message.setOwner(user);
		message.setSubject("Username and Password");
		message.setBody(username+"|"+password);
		message.setAsPasswordLetter();
		try {
			message.store();
		}
		catch (IDOStoreException idos) {
			throw new IDOCreateException(idos);
		}
		return message;
	}

	private PrintedLetterMessage createPrintedLetterMessage(MessageValue msgValue) throws CreateException {
		PrintedLetterMessageHome home = getPrintedLetterMessageHome();
		PrintedLetterMessage message = (PrintedLetterMessage) home.create();
		message.setOwner(msgValue.getReceiver());
		message.setSubject(msgValue.getSubject());
		message.setBody(msgValue.getBody());
		if (msgValue.getParentCase() != null)
			message.setParentCase(msgValue.getParentCase());
		if(msgValue.getPrintedLetterType() != null){
			message.setLetterType(msgValue.getPrintedLetterType());
		}
		if(msgValue.getContentCode() != null){
			message.setContentCode(msgValue.getContentCode());
		}
		try {
			message.store();
		}
		catch (IDOStoreException idos) {
			throw new IDOCreateException(idos);
		}
		return message;
	}
	
	 public Message createPrintedLetterMessage(int userID, String subject,String body)throws CreateException {
	     try {
	         MessageValue msgValue = new MessageValue();
            msgValue.setReceiver(getUser(userID));
            msgValue.setSubject(subject);
            msgValue.setBody(body);
            return createPrintedLetterMessage(msgValue);
	    } catch (FinderException e) {
            throw new IDOCreateException(e);
        }
	 }

	public void sendMessage(String email, String subject, String body) {
		sendMessage(email, subject, body, null);
	}
	
	public void sendMessage(String email, String subject, String body, File attachment) {
	    	String receiver = email.trim();
		String mailServer = DEFAULT_SMTP_MAILSERVER;
		String fromAddress = DEFAULT_MESSAGEBOX_FROM_ADDRESS;
		String forcedToAddress = null;
		String bccReceiver = null;
		try{
			mailServer = getPropertyValue(PROP_SYSTEM_SMTP_MAILSERVER,DEFAULT_SMTP_MAILSERVER);
			fromAddress = getPropertyValue(PROP_MESSAGEBOX_FROM_ADDRESS,DEFAULT_MESSAGEBOX_FROM_ADDRESS);
			forcedToAddress = getPropertyValue(PROP_SYSTEM_FORCED_RECEIVER,"notset");
			bccReceiver = getPropertyValue(PROP_SYSTEM_BCC_RECEIVER,"notset");
		}
		catch(Exception e){
			System.err.println("MessageBusinessBean: Error getting mail property from bundle");
			e.printStackTrace();	
		}
		
		if(forcedToAddress!=null && !"notset".equals(forcedToAddress))
		    receiver = forcedToAddress;
		
		if("notset".equals(bccReceiver))
		    bccReceiver = null;
			

		try {
			if (attachment == null) {
				com.idega.util.SendMail.send(fromAddress, receiver, "", bccReceiver, mailServer, subject, body);
			} else {
				com.idega.util.SendMail.send(fromAddress, receiver, "", bccReceiver, mailServer, subject, body, attachment);
			}
		}
		catch (javax.mail.MessagingException me) {
			System.err.println("Error sending mail to address: " + email + " Message was: " + me.getMessage());
		}
	}


	protected UserProperties getUserPreferences(User user) throws Exception {
		UserProperties property = getCommuneUserBusiness().getUserProperties(user);	
		return property;
	}
	
	protected IWPropertyList getUserMessagePreferences(User user) {
		try{
			IWPropertyList messageProperties = getUserPreferences(user).getProperties(MESSAGE_PROPERTIES);
			return messageProperties;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public boolean getIfUserPreferesMessageByEmail(User user){
		IWPropertyList propertyList = getUserMessagePreferences(user);
		
		if (propertyList != null) {
			String property = propertyList.getProperty(USER_PROP_SEND_TO_EMAIL);
			if(property!=null)
				return Boolean.valueOf(property).booleanValue();
		}
		return true;
	}

	public boolean getIfUserPreferesMessageInMessageBox(User user){
		IWPropertyList propertyList = getUserMessagePreferences(user);
		if (propertyList != null) {
			String property = propertyList.getProperty(USER_PROP_SEND_TO_MESSAGE_BOX);
			if(property!=null)
				return Boolean.valueOf(property).booleanValue();
		}
		return true;
	}
	
	public boolean getIfCanSendEmail() {
		return Boolean.valueOf(getPropertyValue("can_send_email", Boolean.FALSE.toString())).booleanValue();
	}

	public void setIfUserPreferesMessageByEmail(User user,boolean preference){
		IWPropertyList propertyList = getUserMessagePreferences(user);
		propertyList.setProperty(USER_PROP_SEND_TO_EMAIL, new Boolean(preference));
	}

	public void setIfUserPreferesMessageInMessageBox(User user,boolean preference){
		IWPropertyList propertyList = getUserMessagePreferences(user);
		propertyList.setProperty(USER_PROP_SEND_TO_MESSAGE_BOX, new Boolean(preference));
	}
	
	public void sendMessageToCommuneAdministrators(String subject, String body) throws RemoteException {
		sendMessageToCommuneAdministrators(null, subject, body);
	}
	
	public void sendMessageToCommuneAdministrators(Case theCase, String subject, String body) throws RemoteException {
		try {
			Collection administrators = getCommuneUserBusiness().getAllCommuneAdministrators();
			if (!administrators.isEmpty()) {
				Iterator iterator = administrators.iterator();
				while (iterator.hasNext()) {
					User administrator = (User) iterator.next();
					createUserMessage(theCase,administrator,subject,body,false);
				}
			}
		}
		catch (FinderException fe) {
			fe.printStackTrace();		
		}
	}
	
	private CommuneUserBusiness getCommuneUserBusiness() throws RemoteException {
		return (CommuneUserBusiness) getServiceInstance(CommuneUserBusiness.class);
	}
	
	public MessageSession getMessageSession(IWUserContext iwuc) throws IBOLookupException {
	    return (MessageSession)getSessionInstance(iwuc,MessageSession.class);
	}
	
	public String getBundleIdentifier(){
		return IW_BUNDLE_IDENTIFIER;
	}
	
	public MessageHandlerInfo createMessageHandlerInfo(MessagePdfHandler handler,ICObject ico) throws CreateException, RemoteException{
		MessageHandlerInfoHome mhhome= (MessageHandlerInfoHome)getIDOHome(MessageHandlerInfo.class);
		MessageHandlerInfo info = mhhome.create();
		info.setHandlerCode(handler.getHandlerCode());
		info.setICObject(ico);
		info.store();
		return info;
	}
	
	public void setMessageFile(PrintMessage msg, boolean flagPrinted,User performer, ICFile file){
	    msg.setMessageData(file);
		
		if (flagPrinted) {
			flagMessageAsPrinted(performer, msg);
		}
		else {
			msg.store();
		}
		
	}

	/**
	 * Gets the value for a property name ... replaces the bundle properties that were used previously
	 * @param propertyName
	 * @return
	 */
	private String getPropertyValue(String propertyName, String defaultValue) {
		IWMainApplicationSettings settings = getIWMainApplication().getSettings();
		String value = settings.getProperty(propertyName);
		if (value != null) {
			return value;
		}
		IWBundle iwb = getIWApplicationContext().getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER);
		value = iwb.getProperty(propertyName);
		settings.setProperty(propertyName, value != null ? value : defaultValue);
		return defaultValue;
	}
}