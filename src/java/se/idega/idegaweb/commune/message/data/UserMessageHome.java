/*
 * $Id: UserMessageHome.java,v 1.5 2005/10/13 18:36:11 laddi Exp $
 * Created on 7.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package se.idega.idegaweb.commune.message.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.block.process.message.data.MessageHome;
import com.idega.data.IDOException;
import com.idega.user.data.Group;
import com.idega.user.data.User;

/**
 *
 *  Last modified: $Date: 2005/10/13 18:36:11 $ by $Author: laddi $
 *
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.5 $
 */
public interface UserMessageHome extends MessageHome {

    /**
     * @see se.idega.idegaweb.commune.message.data.UserMessageBMPBean#ejbFindMessages
     */
    @Override
	public Collection<UserMessage> findMessages(User user) throws FinderException;

    /**
     * @see se.idega.idegaweb.commune.message.data.UserMessageBMPBean#ejbFindMessagesByStatus
     */
    public Collection<UserMessage> findMessagesByStatus(User user, String[] status) throws FinderException;

    /**
     * @see se.idega.idegaweb.commune.message.data.UserMessageBMPBean#ejbFindMessagesByStatus
     */
    public Collection<UserMessage> findMessagesByStatus(User user, String[] status, int numberOfEntries, int startingEntry) throws FinderException;

    /**
     * @see se.idega.idegaweb.commune.message.data.UserMessageBMPBean#ejbFindMessagesByStatus
     */
    public Collection<UserMessage> findMessagesByStatus(Group group, String[] status) throws FinderException;

    /**
     * @see se.idega.idegaweb.commune.message.data.UserMessageBMPBean#ejbFindMessagesByStatus
     */
    public Collection<UserMessage> findMessagesByStatus(Group group, String[] status, int numberOfEntries, int startingEntry) throws FinderException;

    /**
     * @see se.idega.idegaweb.commune.message.data.UserMessageBMPBean#ejbFindMessagesByStatus
     */
    public Collection<UserMessage> findMessagesByStatus(User user, Collection<Group> groups, String[] status, int numberOfEntries, int startingEntry)
    	throws FinderException;

    /**
     * @see se.idega.idegaweb.commune.message.data.UserMessageBMPBean#ejbHomeGetNumberOfMessagesByStatus
     */
    public int getNumberOfMessagesByStatus(User user, String[] status) throws IDOException;

    /**
     * @see se.idega.idegaweb.commune.message.data.UserMessageBMPBean#ejbHomeGetNumberOfMessagesByStatus
     */
    public int getNumberOfMessagesByStatus(User user, Collection<Group> groups, String[] status) throws IDOException;

    /**
     * @see se.idega.idegaweb.commune.message.data.UserMessageBMPBean#ejbFindMessages
     */
    @Override
	public Collection<UserMessage> findMessages(User user, String[] status) throws FinderException;

    /**
     * @see se.idega.idegaweb.commune.message.data.UserMessageBMPBean#ejbFindMessages
     */
    @Override
	public Collection<UserMessage> findMessages(Group group, String[] status) throws FinderException;

    /**
     * @see se.idega.idegaweb.commune.message.data.UserMessageBMPBean#ejbFindMessages
     */
    @Override
	public Collection<UserMessage> findMessages(User user, String[] status, int numberOfEntries, int startingEntry) throws FinderException;

    /**
     * @see se.idega.idegaweb.commune.message.data.UserMessageBMPBean#ejbFindMessages
     */
    @Override
	public Collection<UserMessage> findMessages(Group group, String[] status, int numberOfEntries, int startingEntry) throws javax.ejb.FinderException;

    /**
     * @see se.idega.idegaweb.commune.message.data.UserMessageBMPBean#ejbFindMessages
     */
    @Override
	public Collection<UserMessage> findMessages(User user, Collection<Group> groups, String[] status, int numberOfEntries, int startingEntry) throws FinderException;

    /**
     * @see se.idega.idegaweb.commune.message.data.UserMessageBMPBean#ejbHomeGetNumberOfMessages
     */
    @Override
	public int getNumberOfMessages(User user, String[] status) throws IDOException;

    /**
     * @see se.idega.idegaweb.commune.message.data.UserMessageBMPBean#ejbHomeGetNumberOfMessages
     */
    @Override
	public int getNumberOfMessages(User user, Collection<Group> groups, String[] status) throws IDOException;

}