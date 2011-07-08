/*
 * $Id: UserMessage.java,v 1.8 2005/10/13 18:36:11 laddi Exp $
 * Created on 7.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package se.idega.idegaweb.commune.message.data;



import com.idega.block.process.data.Case;
import com.idega.block.process.message.data.Message;
import com.idega.data.IDOEntity;
import com.idega.user.data.User;

/**
 * 
 *  Last modified: $Date: 2005/10/13 18:36:11 $ by $Author: laddi $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.8 $
 */
public interface UserMessage extends IDOEntity, Message, Case {
    /**
     * @see se.idega.idegaweb.commune.message.data.UserMessageBMPBean#getCaseCodeKey
     */
    public String getCaseCodeKey();

    /**
     * @see se.idega.idegaweb.commune.message.data.UserMessageBMPBean#getCaseCodeDescription
     */
    public String getCaseCodeDescription();

    /**
     * @see se.idega.idegaweb.commune.message.data.UserMessageBMPBean#setSubject
     */
    public void setSubject(String subject);

    /**
     * @see se.idega.idegaweb.commune.message.data.UserMessageBMPBean#getSubject
     */
    public String getSubject();

    /**
     * @see se.idega.idegaweb.commune.message.data.UserMessageBMPBean#setBody
     */
    public void setBody(String body);

    /**
     * @see se.idega.idegaweb.commune.message.data.UserMessageBMPBean#getBody
     */
    public String getBody();

    /**
     * @see se.idega.idegaweb.commune.message.data.UserMessageBMPBean#getSenderID
     */
    public int getSenderID();

    /**
     * @see se.idega.idegaweb.commune.message.data.UserMessageBMPBean#setSenderID
     */
    public void setSenderID(int userID);

    /**
     * @see se.idega.idegaweb.commune.message.data.UserMessageBMPBean#getSender
     */
    public User getSender();

    /**
     * @see se.idega.idegaweb.commune.message.data.UserMessageBMPBean#setSender
     */
    public void setSender(User user);

}
