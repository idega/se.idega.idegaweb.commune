package se.idega.idegaweb.commune.message.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.block.process.data.AbstractCaseBMPBean;
import com.idega.block.process.data.Case;
import com.idega.block.process.message.data.Message;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOException;
import com.idega.data.IDORemoveRelationshipException;
import com.idega.data.query.SelectQuery;
import com.idega.user.data.Group;
import com.idega.user.data.User;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author Anders Lindman
 * @version 1.0
 */

public class UserMessageBMPBean extends AbstractCaseBMPBean implements UserMessage,Message,Case{

  private static final String COLUMN_SUBJECT="SUBJECT";
  private static final String COLUMN_BODY="BODY";
  private static final String COLUMN_TEMP_SENDER="TEMP_SENDER";
  private static final String COLUMN_DATE="TEMP_DATE";
	private static final String COLUMN_SENDER="SENDER";

  private static final String CASE_CODE_KEY="SYMEDAN";
  private static final String CASE_CODE_DESCRIPTION="User Message";

  @Override
public String getEntityName() {
    return "MSG_USER_MESSAGE";
  }

  @Override
public void initializeAttributes(){
    addGeneralCaseRelation();
    this.addAttribute(COLUMN_SUBJECT,"Message subject",String.class);
    this.addAttribute(COLUMN_BODY,"Message body",String.class,4000);
    //this.addAttribute(COLUMN_SENDER,"Message sender",Integer.class);//temp
    this.addAttribute(COLUMN_DATE,"Message sender",String.class);//temp
    this.addAttribute(COLUMN_TEMP_SENDER,"Message sender",String.class);//temp
    this.addManyToOneRelationship(COLUMN_SENDER, User.class);
    //this.setNullable(COLUMN_SENDER, true);
  }

  @Override
public String getCaseCodeKey(){
    return CASE_CODE_KEY;
  }

  @Override
public String getCaseCodeDescription(){
    return CASE_CODE_DESCRIPTION;
  }

  @Override
public void setSubject(String subject){
    this.setColumn(COLUMN_SUBJECT,subject);
  }

  @Override
public String getSubject(){
    return this.getStringColumnValue(COLUMN_SUBJECT);
  }

  @Override
public void setBody(String body){
    this.setColumn(COLUMN_BODY,body);
  }

  @Override
public String getBody(){
    return this.getStringColumnValue(COLUMN_BODY);
  }

  @Override
public int getSenderID(){
    return this.getIntColumnValue(COLUMN_SENDER);
  }

  @Override
public void setSenderID(int userID){
    this.setColumn(COLUMN_SENDER,userID);
  }

	@Override
	public User getSender(){
		return (User) getColumnValue(COLUMN_SENDER);
	}

	@Override
	public void setSender(User user){
		this.setColumn(COLUMN_SENDER,user.getPrimaryKey());
	}

  public Collection ejbFindMessages(User user)throws FinderException{
    return super.ejbFindAllCasesByUser(user);
  }

	public Collection ejbFindMessagesByStatus(User user, String[] status)throws FinderException{
		return super.ejbFindAllCasesByUserAndStatusArray(user, status);
	}

	public Collection ejbFindMessagesByStatus(User user, String[] status, int numberOfEntries, int startingEntry)throws FinderException{
		return super.ejbFindAllCasesByUserAndStatusArray(user, status, numberOfEntries, startingEntry);
	}

	public Collection ejbFindMessagesByStatus(Group group, String[] status)throws FinderException{
		return super.ejbFindAllCasesByGroupAndStatusArray(group, status);
	}

	public Collection ejbFindMessagesByStatus(Group group, String[] status, int numberOfEntries, int startingEntry)throws FinderException{
		return super.ejbFindAllCasesByGroupAndStatusArray(group, status, numberOfEntries, startingEntry);
	}

	public Collection ejbFindMessagesByStatus(User user, Collection groups, String[] status, int numberOfEntries, int startingEntry)throws FinderException{
		return super.ejbFindAllCasesByUserAndGroupsAndStatusArray(user, groups, status, numberOfEntries, startingEntry);
	}

	public int ejbHomeGetNumberOfMessagesByStatus(User user, String[] status) throws IDOException {
		return super.ejbHomeGetCountCasesByUserAndStatusArray(user, status);
	}

	public int ejbHomeGetNumberOfMessagesByStatus(User user, Collection groups, String[] status) throws IDOException {
		return super.ejbHomeGetCountCasesByUserAndGroupsAndStatusArray(user, groups, status);
	}


	 public java.util.Collection ejbFindMessages(com.idega.user.data.User user, String[] status)throws javax.ejb.FinderException{
	     return super.ejbFindAllCasesByUserAndStatusArray(user,status);
	 }
	 public java.util.Collection ejbFindMessages(com.idega.user.data.Group group, String[] status)throws javax.ejb.FinderException{
	     return super.ejbFindAllCasesByGroupAndStatusArray(group,status);
	 }
	 public java.util.Collection ejbFindMessages(com.idega.user.data.User user, String[] status, int numberOfEntries, int startingEntry)throws javax.ejb.FinderException{
	     SelectQuery query = idoSelectQueryGetAllCases();
		 query.addCriteria(idoCriteriaForUser(user));
		 query.addCriteria(idoCriteriaForStatus(status));
		 query.addOrder(idoOrderByCreationDate(false));
		 return super.idoFindPKsByQuery(query, numberOfEntries, startingEntry);
	 }
	 public java.util.Collection ejbFindMessages(com.idega.user.data.Group group, String[] status, int numberOfEntries, int startingEntry)throws javax.ejb.FinderException{
	     return super.ejbFindAllCasesByGroupAndStatusArray(group,status,numberOfEntries,startingEntry);
	 }
	 public java.util.Collection ejbFindMessages(com.idega.user.data.User user, java.util.Collection groups, String[] status, int numberOfEntries, int startingEntry)throws javax.ejb.FinderException{
	     SelectQuery query = idoSelectQueryGetAllCases();
		 query.addCriteria(idoCriteriaForUser(user));
		 query.addCriteria(idoCriteriaForStatus(status));
		 query.addCriteria(idoCriteriaForGroup(groups));
		 query.addOrder(idoOrderByCreationDate(false));
	     return super.idoFindPKsByQuery(query, numberOfEntries, startingEntry);
	     //return super.ejbFindAllCasesByUserAndGroupsAndStatusArray(user,groups,status,numberOfEntries,startingEntry);
	 }
	 public int ejbHomeGetNumberOfMessages(com.idega.user.data.User user, String[] status) throws IDOException{
	     return super.ejbHomeGetCountCasesByUserAndStatusArray(user, status);
	 }
	 public int ejbHomeGetNumberOfMessages(com.idega.user.data.User user, java.util.Collection groups, String[] status) throws IDOException{
	     return super.ejbHomeGetCountCasesByUserAndGroupsAndStatusArray(user,groups,status);
	 }

	@Override
	public void addSubscriber(User arg0) throws IDOAddRelationshipException {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<User> getSubscribers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeSubscriber(User arg0)
			throws IDORemoveRelationshipException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getContentCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setContentCode(String arg0) {
		// TODO Auto-generated method stub

	}
}
