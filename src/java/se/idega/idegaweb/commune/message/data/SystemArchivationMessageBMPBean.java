package se.idega.idegaweb.commune.message.data;

import java.util.Collection;

import javax.ejb.EJBException;
import javax.ejb.FinderException;

import com.idega.block.process.data.AbstractCaseBMPBean;
import com.idega.block.process.data.Case;
import com.idega.core.file.data.ICFile;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOException;
import com.idega.data.IDORemoveRelationshipException;
import com.idega.data.query.SelectQuery;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class SystemArchivationMessageBMPBean extends AbstractCaseBMPBean implements SystemArchivationMessage, PrintMessage, Case {

	private static final long serialVersionUID = -1866397870125322003L;

	private static final String COLUMN_SUBJECT = "SUBJECT";

	private static final String COLUMN_BODY = "BODY";

	private static final String COLUMN_MESSAGE_TYPE = "MESSAGE_TYPE";

	private static final String COLUMN_MESSAGE_DATA = "MESSAGE_DATA";

	private static final String COLUMN_ATTATCHED_FILE_ID = "ATTATCHED_FILE_ID";

	private static final String COLUMN_BULK_DATA = "BULK_DATA";

	private static final String COLUMN_CONTENT_CODE = "CONTENT_CODE";

	private static final String CASE_CODE_KEY = "SYMEARK";

	private static final String CASE_CODE_DESCRIPTION = "System Archivation Message";

	public static final String PRINT_TYPE = "ARCH";

	@Override
	public String getEntityName() {
		return "MSG_SYSTEM_ARCH_MESSAGE";
	}

	@Override
	public boolean isPrinted() {
		return this.getCaseStatus().getStatus().equals(getCaseStatusReady());
	}

	@Override
	public void initializeAttributes() {
		addGeneralCaseRelation();
		this.addAttribute(COLUMN_SUBJECT, "Message subject", String.class);
		this.addAttribute(COLUMN_BODY, "Message body", String.class, 1000);
		this.addAttribute(COLUMN_MESSAGE_TYPE, "Message type", String.class, 20);
		this.addAttribute(COLUMN_CONTENT_CODE, "Message content code", String.class, 20);
		this.addManyToOneRelationship(COLUMN_MESSAGE_DATA, "Message data", ICFile.class);
		this.addManyToOneRelationship(COLUMN_ATTATCHED_FILE_ID, "Attatched file", ICFile.class);
		this.addManyToOneRelationship(COLUMN_BULK_DATA, "Message bulk data", ICFile.class);
	}

	@Override
	public String getCaseCodeKey() {
		return CASE_CODE_KEY;
	}

	@Override
	public String getCaseCodeDescription() {
		return CASE_CODE_DESCRIPTION;
	}

	@Override
	public void setSubject(String subject) {
		this.setColumn(COLUMN_SUBJECT, subject);
	}

	@Override
	public String getSubject() {
		return this.getStringColumnValue(COLUMN_SUBJECT);
	}

	@Override
	public void setBody(String body) {
		this.setColumn(COLUMN_BODY, body);
	}

	@Override
	public String getBody() {
		return this.getStringColumnValue(COLUMN_BODY);
	}

	@Override
	public String getMessageType() {
		return this.getStringColumnValue(COLUMN_MESSAGE_TYPE);
	}

	@Override
	public void setMessageType(String type) {
		this.setColumn(COLUMN_MESSAGE_TYPE, type);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see se.idega.idegaweb.commune.message.data.PrintMessage#getContentCode()
	 */
	@Override
	public String getContentCode() {
		return this.getStringColumnValue(COLUMN_CONTENT_CODE);
	}

	@Override
	public void setContentCode(String contentCode) {
		this.setColumn(COLUMN_CONTENT_CODE, contentCode);
	}

	@Override
	public ICFile getMessageData() {
		return (ICFile) this.getColumnValue(COLUMN_MESSAGE_DATA); // Replace this
																															// later
	}

	@Override
	public int getMessageDataFileID() {
		return this.getIntColumnValue(COLUMN_MESSAGE_DATA);
	}

	@Override
	public void setMessageData(ICFile file) { // Temp (test) method
		this.setColumn(COLUMN_MESSAGE_DATA, file);
	}

	@Override
	public void setMessageData(int fileID) { // Temp (test) method
		this.setColumn(COLUMN_MESSAGE_DATA, fileID);
	}

	@Override
	public void setAttachedFile(ICFile file) { // Temp (test) method
		this.setColumn(COLUMN_ATTATCHED_FILE_ID, file);
	}

	@Override
	public void setAttachedFile(int fileID) { // Temp (test) method
		this.setColumn(COLUMN_ATTATCHED_FILE_ID, fileID);
	}

	@Override
	public ICFile getAttachedFile() {
		return (ICFile) this.getColumnValue(COLUMN_ATTATCHED_FILE_ID); // Replace
																																		// this
																																		// later
	}

	@Override
	public int getAttachedFileID() {
		return this.getIntColumnValue(COLUMN_ATTATCHED_FILE_ID);
	}

	@Override
	public ICFile getMessageBulkData() {
		return (ICFile) this.getColumnValue(COLUMN_BULK_DATA); // Replace this
																														// later
	}

	@Override
	public int getMessageBulkDataFileID() {
		return this.getIntColumnValue(COLUMN_BULK_DATA);
	}

	@Override
	public void setMessageBulkData(ICFile file) { // Temp (test) method
		this.setColumn(COLUMN_BULK_DATA, file);
	}

	@Override
	public void setMessageBulkData(int fileID) { // Temp (test) method
		this.setColumn(COLUMN_BULK_DATA, fileID);
	}

	@Override
	public User getSender() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSender(User sender) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getSenderID() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSenderID(int senderID) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSenderName() {
		return getOwner().getName();
	}

	@Override
	public String getDateString() {
		/**
		 * @todo: implement
		 */
		return "";
	}

	@Override
	public String getPrintType() {
		return PRINT_TYPE;
	}

	public Collection ejbFindMessages(User user) throws FinderException {
		return super.ejbFindAllCasesByUser(user);
	}

	public Collection ejbFindMessagesByStatus(User user, String[] status) throws FinderException {
		return super.ejbFindAllCasesByUserAndStatusArray(user, status);
	}

	public Collection ejbFindPrintedMessages() throws FinderException {
		return super.idoFindPKsByQuery(super.idoSelectQueryGetAllCasesByStatusOrderedByCreation(getCaseStatusReady()));
	}

	public Collection ejbFindUnPrintedMessages() throws FinderException {
		return super.idoFindPKsByQuery(super.idoSelectQueryGetAllCasesByStatusOrderedByCreation(getCaseStatusOpen()));
	}

	public Collection ejbFindPrintedMessages(IWTimestamp from, IWTimestamp to) throws FinderException {
		SelectQuery query = idoSelectQueryGetAllCases();
		query.addCriteria(idoCriteriaForStatus(getCaseStatusReady()));
		query.addCriteria(idoCriteriaForCreatedWithinDates(from, to));
		query.addOrder(idoOrderByCreationDate(false));

		return super.idoFindPKsByQuery(query);
	}

	public Collection ejbFindUnPrintedMessages(IWTimestamp from, IWTimestamp to) throws FinderException {
		SelectQuery query = idoSelectQueryGetAllCases();
		query.addCriteria(idoCriteriaForStatus(getCaseStatusOpen()));
		query.addCriteria(idoCriteriaForCreatedWithinDates(from, to));
		query.addOrder(idoOrderByCreationDate(false));

		return super.idoFindPKsByQuery(query);
	}

	/**
	 * Counts the number of letters that are of type default and unprinted
	 */
	public int ejbHomeGetNumberOfUnPrintedMessages() {
		try {
			SelectQuery sql = super.idoSelectQueryGetCountCasesWithStatus(getCaseStatusOpen());
			return super.idoGetNumberOfRecords(sql);
		}
		catch (IDOException sqle) {
			throw new EJBException(sqle.getMessage());
		}
	}

	public String[] ejbHomeGetPrintMessageTypes() {
		String[] types = new String[1];
		types[0] = PRINT_TYPE;
		return types;
	}

	public java.util.Collection ejbFindMessages(com.idega.user.data.User user, String[] status)
			throws javax.ejb.FinderException {
		return super.ejbFindAllCasesByUserAndStatusArray(user, status);
	}

	public Collection ejbFindMessages(User user, String[] status, int numberOfEntries, int startingEntry)
			throws FinderException {
		return super.ejbFindAllCasesByUserAndStatusArray(user, status, numberOfEntries, startingEntry);
	}

	public Collection ejbFindMessages(Group group, String[] status) throws FinderException {
		return super.ejbFindAllCasesByGroupAndStatusArray(group, status);
	}

	public Collection ejbFindMessages(Group group, String[] status, int numberOfEntries, int startingEntry)
			throws FinderException {
		return super.ejbFindAllCasesByGroupAndStatusArray(group, status, numberOfEntries, startingEntry);
	}

	public Collection ejbFindMessages(User user, Collection groups, String[] status, int numberOfEntries,
			int startingEntry) throws FinderException {
		return super.ejbFindAllCasesByUserAndGroupsAndStatusArray(user, groups, status, numberOfEntries, startingEntry);
	}

	public int ejbHomeGetNumberOfMessages(User user, Collection groups, String[] status) throws IDOException {
		return super.ejbHomeGetCountCasesByUserAndGroupsAndStatusArray(user, groups, status);
	}

	public int ejbHomeGetNumberOfMessages(User user, String[] status) throws IDOException {
		return super.ejbHomeGetCountCasesByUserAndStatusArray(user, status);
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
}