/*
 * $Id: SystemArchivationMessageHomeImpl.java 1.1 Oct 12, 2005 laddi Exp $
 * Created on Oct 12, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package se.idega.idegaweb.commune.message.data;

import java.util.Collection;
import java.util.Collections;

import javax.ejb.FinderException;

import com.idega.block.process.message.data.Message;
import com.idega.block.process.message.data.MessageHome;
import com.idega.data.IDOException;
import com.idega.data.IDOFactory;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;


/**
 * Last modified: $Date: 2004/06/28 09:09:50 $ by $Author: laddi $
 *
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class SystemArchivationMessageHomeImpl extends IDOFactory implements SystemArchivationMessageHome {

	private static final long serialVersionUID = -8263470268713203326L;

	@Override
	protected Class<SystemArchivationMessage> getEntityInterfaceClass() {
		return SystemArchivationMessage.class;
	}

	@Override
	public Message create() throws javax.ejb.CreateException {
		return (SystemArchivationMessage) super.createIDO();
	}

	@Override
	public Message findByPrimaryKey(Object pk) throws javax.ejb.FinderException {
		return (SystemArchivationMessage) super.findByPrimaryKeyIDO(pk);
	}

	@Override
	public Collection findMessages(User user) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((SystemArchivationMessageBMPBean) entity).ejbFindMessages(user);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findMessagesByStatus(User user, String[] status) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((SystemArchivationMessageBMPBean) entity).ejbFindMessagesByStatus(user, status);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findPrintedMessages() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((SystemArchivationMessageBMPBean) entity).ejbFindPrintedMessages();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findUnPrintedMessages() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((SystemArchivationMessageBMPBean) entity).ejbFindUnPrintedMessages();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findPrintedMessages(IWTimestamp from, IWTimestamp to) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((SystemArchivationMessageBMPBean) entity).ejbFindPrintedMessages(from, to);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findUnPrintedMessages(IWTimestamp from, IWTimestamp to) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((SystemArchivationMessageBMPBean) entity).ejbFindUnPrintedMessages(from, to);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public int getNumberOfUnPrintedMessages() {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		int theReturn = ((SystemArchivationMessageBMPBean) entity).ejbHomeGetNumberOfUnPrintedMessages();
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	@Override
	public String[] getPrintMessageTypes() {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		String[] theReturn = ((SystemArchivationMessageBMPBean) entity).ejbHomeGetPrintMessageTypes();
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	@Override
	public java.util.Collection findMessages(com.idega.user.data.User user, String[] status)
			throws javax.ejb.FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((SystemArchivationMessageBMPBean) entity).ejbFindMessages(user, status);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findMessages(User user, String[] status, int numberOfEntries, int startingEntry)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((SystemArchivationMessageBMPBean) entity).ejbFindMessages(user, status,
				numberOfEntries, startingEntry);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findMessages(Group group, String[] status) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((SystemArchivationMessageBMPBean) entity).ejbFindMessages(group, status);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findMessages(Group group, String[] status, int numberOfEntries, int startingEntry)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((SystemArchivationMessageBMPBean) entity).ejbFindMessages(group, status,
				numberOfEntries, startingEntry);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findMessages(User user, Collection groups, String[] status, int numberOfEntries, int startingEntry)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((SystemArchivationMessageBMPBean) entity).ejbFindMessages(user, groups, status,
				numberOfEntries, startingEntry);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public int getNumberOfMessages(User user, Collection groups, String[] status) throws IDOException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		int theReturn = ((SystemArchivationMessageBMPBean) entity).ejbHomeGetNumberOfMessages(user, groups, status);
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	@Override
	public int getNumberOfMessages(User user, String[] status) throws IDOException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		int theReturn = ((SystemArchivationMessageBMPBean) entity).ejbHomeGetNumberOfMessages(user, status);
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	@Override
	public Collection<Message> findMessages(User user, String caseId) throws FinderException {
		MessageHome messageHome = null;
		try {
			messageHome = (MessageHome) IDOLookup.getHome(Message.class);
		} catch (IDOLookupException e) {
			e.printStackTrace();
		}
		if (messageHome == null)
			return Collections.emptyList();

		return messageHome.findMessages(user, caseId);
	}
}