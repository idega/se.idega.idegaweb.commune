/*
 * $Id: UserMessageHomeImpl.java,v 1.9 2005/10/13 18:36:11 laddi Exp $
 * Created on 7.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package se.idega.idegaweb.commune.message.data;

import java.util.Collection;
import java.util.Collections;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.block.process.message.data.Message;
import com.idega.block.process.message.data.MessageHome;
import com.idega.data.IDOEntity;
import com.idega.data.IDOException;
import com.idega.data.IDOFactory;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.user.data.Group;
import com.idega.user.data.User;

/**
 *
 *  Last modified: $Date: 2005/10/13 18:36:11 $ by $Author: laddi $
 *
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.9 $
 */
public class UserMessageHomeImpl extends IDOFactory implements UserMessageHome {

	@Override
	protected Class<UserMessage> getEntityInterfaceClass() {
        return UserMessage.class;
    }

    @Override
	public Message create() throws CreateException {
        return (UserMessage) super.createIDO();
    }

    @Override
	public Message findByPrimaryKey(Object pk) throws FinderException {
        return (UserMessage) super.findByPrimaryKeyIDO(pk);
    }

    @Override
	public Collection<UserMessage> findMessages(User user) throws FinderException {
        IDOEntity entity = this.idoCheckOutPooledEntity();
        Collection<?> ids = ((UserMessageBMPBean) entity).ejbFindMessages(user);
        this.idoCheckInPooledEntity(entity);
        return this.getEntityCollectionForPrimaryKeys(ids);
    }

    @Override
	public Collection<UserMessage> findMessagesByStatus(User user, String[] status) throws FinderException {
        IDOEntity entity = this.idoCheckOutPooledEntity();
        Collection<?> ids = ((UserMessageBMPBean) entity).ejbFindMessagesByStatus(user, status);
        this.idoCheckInPooledEntity(entity);
        return this.getEntityCollectionForPrimaryKeys(ids);
    }

    @Override
	public Collection<UserMessage> findMessagesByStatus(User user, String[] status, int numberOfEntries, int startingEntry) throws FinderException {
        IDOEntity entity = this.idoCheckOutPooledEntity();
        Collection<?> ids = ((UserMessageBMPBean) entity).ejbFindMessagesByStatus(user, status, numberOfEntries, startingEntry);
        this.idoCheckInPooledEntity(entity);
        return this.getEntityCollectionForPrimaryKeys(ids);
    }

    @Override
	public Collection<UserMessage> findMessagesByStatus(Group group, String[] status) throws FinderException {
        IDOEntity entity = this.idoCheckOutPooledEntity();
        Collection<?> ids = ((UserMessageBMPBean) entity).ejbFindMessagesByStatus(group, status);
        this.idoCheckInPooledEntity(entity);
        return this.getEntityCollectionForPrimaryKeys(ids);
    }

    @Override
	public Collection<UserMessage> findMessagesByStatus(Group group, String[] status, int numberOfEntries, int startingEntry) throws FinderException {
        IDOEntity entity = this.idoCheckOutPooledEntity();
        Collection<?> ids = ((UserMessageBMPBean) entity).ejbFindMessagesByStatus(group, status, numberOfEntries, startingEntry);
        this.idoCheckInPooledEntity(entity);
        return this.getEntityCollectionForPrimaryKeys(ids);
    }

    @Override
	public Collection<UserMessage> findMessagesByStatus(User user, Collection<Group> groups, String[] status, int numberOfEntries, int startingEntry)
		throws FinderException {
        IDOEntity entity = this.idoCheckOutPooledEntity();
        Collection<?> ids = ((UserMessageBMPBean) entity).ejbFindMessagesByStatus(user, groups, status, numberOfEntries, startingEntry);
        this.idoCheckInPooledEntity(entity);
        return this.getEntityCollectionForPrimaryKeys(ids);
    }

    @Override
	public int getNumberOfMessagesByStatus(User user, String[] status) throws IDOException {
        IDOEntity entity = this.idoCheckOutPooledEntity();
        int theReturn = ((UserMessageBMPBean) entity).ejbHomeGetNumberOfMessagesByStatus(user, status);
        this.idoCheckInPooledEntity(entity);
        return theReturn;
    }

    @Override
	public int getNumberOfMessagesByStatus(User user, Collection<Group> groups, String[] status) throws IDOException {
        IDOEntity entity = this.idoCheckOutPooledEntity();
        int theReturn = ((UserMessageBMPBean) entity).ejbHomeGetNumberOfMessagesByStatus(user, groups, status);
        this.idoCheckInPooledEntity(entity);
        return theReturn;
    }

    @Override
	public Collection<UserMessage> findMessages(User user, String[] status) throws FinderException {
        IDOEntity entity = this.idoCheckOutPooledEntity();
        Collection<?> ids = ((UserMessageBMPBean) entity).ejbFindMessages(user, status);
        this.idoCheckInPooledEntity(entity);
        return this.getEntityCollectionForPrimaryKeys(ids);
    }

    @Override
	public Collection<UserMessage> findMessages(Group group, String[] status) throws FinderException {
        IDOEntity entity = this.idoCheckOutPooledEntity();
        Collection<?> ids = ((UserMessageBMPBean) entity).ejbFindMessages(group, status);
        this.idoCheckInPooledEntity(entity);
        return this.getEntityCollectionForPrimaryKeys(ids);
    }

    @Override
	public Collection<UserMessage> findMessages(User user, String[] status, int numberOfEntries, int startingEntry) throws FinderException {
        IDOEntity entity = this.idoCheckOutPooledEntity();
        Collection<?> ids = ((UserMessageBMPBean) entity).ejbFindMessages(user, status, numberOfEntries, startingEntry);
        this.idoCheckInPooledEntity(entity);
        return this.getEntityCollectionForPrimaryKeys(ids);
    }

    @Override
	public Collection<UserMessage> findMessages(Group group, String[] status, int numberOfEntries, int startingEntry) throws FinderException {
        IDOEntity entity = this.idoCheckOutPooledEntity();
        Collection<?> ids = ((UserMessageBMPBean) entity).ejbFindMessages(group, status, numberOfEntries, startingEntry);
        this.idoCheckInPooledEntity(entity);
        return this.getEntityCollectionForPrimaryKeys(ids);
    }

    @Override
	public Collection<UserMessage> findMessages(User user, Collection<Group> groups, String[] status, int numberOfEntries, int startingEntry) throws FinderException {
        IDOEntity entity = this.idoCheckOutPooledEntity();
        Collection<?> ids = ((UserMessageBMPBean) entity).ejbFindMessages(user, groups, status, numberOfEntries, startingEntry);
        this.idoCheckInPooledEntity(entity);
        return this.getEntityCollectionForPrimaryKeys(ids);
    }

    @Override
	public int getNumberOfMessages(User user, String[] status) throws IDOException {
        IDOEntity entity = this.idoCheckOutPooledEntity();
        int theReturn = ((UserMessageBMPBean) entity).ejbHomeGetNumberOfMessages(user, status);
        this.idoCheckInPooledEntity(entity);
        return theReturn;
    }

    @Override
	public int getNumberOfMessages(User user, Collection<Group> groups, String[] status) throws IDOException {
        IDOEntity entity = this.idoCheckOutPooledEntity();
        int theReturn = ((UserMessageBMPBean) entity).ejbHomeGetNumberOfMessages(user, groups, status);
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