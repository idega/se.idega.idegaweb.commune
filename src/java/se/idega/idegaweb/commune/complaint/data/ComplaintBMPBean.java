package se.idega.idegaweb.commune.complaint.data;

import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.FinderException;

import se.idega.idegaweb.commune.complaint.business.ComplaintBusinessBean;

import com.idega.block.process.data.AbstractCaseBMPBean;
import com.idega.block.process.data.Case;
import com.idega.block.process.data.CaseCode;
import com.idega.block.process.data.CaseStatus;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDORemoveRelationshipException;
import com.idega.user.data.User;

/**
 * @author laddi
 */

public class ComplaintBMPBean extends AbstractCaseBMPBean implements Complaint,Case {
	private static final String COLUMN_COMPLAINT = "COMPLAINT";
	private static final String COLUMN_DESCRIPTION = "DESCRIPTION";
	private static final String COLUMN_ANSWER = "ANSWER";
	private static final String COLUMN_MANAGER_ID = "MANAGER_ID";
	private static final String COLUMN_COMPLAINT_TYPE = "COMPLAINT_TYPE";
	private static final String[] caseStatusKeys = { ComplaintBusinessBean.CASE_STATUS_ANSWERED };
	private static final String[] caseStatusDesc = { "Complaint answered" };

	@Override
	public String[] getCaseStatusKeys()
	{
		return caseStatusKeys;
	}

	@Override
	public String[] getCaseStatusDescriptions()
	{
		return caseStatusDesc;
	}

	/**
	 * @see com.idega.block.process.data.AbstractCaseBMPBean#getCaseCodeKey()
	 */
	@Override
	public String getCaseCodeKey() {
		return "SYMESYN";
	}

	/**
	 * @see com.idega.block.process.data.AbstractCaseBMPBean#getCaseCodeDescription()
	 */
	@Override
	public String getCaseCodeDescription() {
		return "User complaint";
	}

	/**
	 * @see com.idega.data.IDOLegacyEntity#getEntityName()
	 */
	@Override
	public String getEntityName() {
		return "co_complaint";
	}

	/**
	 * @see com.idega.data.IDOLegacyEntity#initializeAttributes()
	 */
	@Override
	public void initializeAttributes() {
		addGeneralCaseRelation();

		this.addAttribute(COLUMN_COMPLAINT, "Complaint", String.class);
		this.addAttribute(COLUMN_DESCRIPTION, "Description", String.class,10000);
		this.addAttribute(COLUMN_ANSWER, "Answer", String.class,10000);
		this.addAttribute(COLUMN_MANAGER_ID, "The manager for the complaint", true, true, Integer.class, "many-to-one",User.class);
	    this.addAttribute(COLUMN_COMPLAINT_TYPE,"The complaint type",String.class,7);
	    this.addManyToOneRelationship(COLUMN_COMPLAINT_TYPE, CaseCode.class);
	}

	/**
	 * Returns the answer.
	 * @return String
	 */
	@Override
	public String getAnswer() {
		return getStringColumnValue(COLUMN_ANSWER);
	}

	/**
	 * Returns the complaint.
	 * @return String
	 */
	@Override
	public String getComplaint() {
		return getStringColumnValue(COLUMN_COMPLAINT);
	}

	/**
	 * Returns the description.
	 * @return String
	 */
	@Override
	public String getDescription() {
		return getStringColumnValue(COLUMN_DESCRIPTION);
	}

	/**
	 * Returns the managerID.
	 * @return int
	 */
	@Override
	public int getManagerID() {
		return getIntColumnValue(COLUMN_MANAGER_ID);
	}

	/**
	 * Returns the complaintType.
	 * @return String
	 */
	@Override
	public String getComplaintType() {
		return getStringColumnValue(COLUMN_COMPLAINT_TYPE);
	}

	/**
	 * Returns the complaintType.
	 * @return ComplaintType
	 */
	@Override
	public CaseCode getComplaintCaseType() {
		return (CaseCode) getColumnValue(COLUMN_COMPLAINT_TYPE);
	}

	/**
	 * Sets the answer.
	 * @param answer The answer to set
	 */
	@Override
	public void setAnswer(String answer) {
		setColumn(COLUMN_ANSWER,answer);
	}

	/**
	 * Sets the complaint.
	 * @param complaint The complaint to set
	 */
	@Override
	public void setComplaint(String complaint) {
		setColumn(COLUMN_COMPLAINT,complaint);
	}

	/**
	 * Sets the description.
	 * @param description The description to set
	 */
	@Override
	public void setDescription(String description) {
		setColumn(COLUMN_DESCRIPTION,description);
	}

	/**
	 * Sets the managerID.
	 * @param managerID The managerID to set
	 */
	@Override
	public void setManagerID(Integer managerID) {
		setColumn(COLUMN_MANAGER_ID,managerID);
	}

	/**
	 * Sets the managerID.
	 * @param managerID The managerID to set
	 */
	@Override
	public void setManagerID(int managerID) {
		setColumn(COLUMN_MANAGER_ID,managerID);
	}

	/**
	 * Sets the complaintID.
	 * @param complaintID The complaintID to set
	 */
	@Override
	public void setComplaintType(String complaintType) {
		setColumn(COLUMN_COMPLAINT_TYPE,complaintType);
	}

	/**
	 * Method setComplaintType.
	 * @param complaintType
	 * @throws RemoteException
	 */
	@Override
	public void setComplaintType(CaseCode complaintType) {
		setComplaintType((String)complaintType.getPrimaryKey());
	}

	/**
	 * Method ejbFindAllComplaints.
	 * @return Collection
	 * @throws FinderException
	 */
	public Collection ejbFindAllComplaints() throws FinderException {
		return this.idoFindPKsBySQL("select * from "+getEntityName());
	}

	/**
	 * Method ejbFindAllComplaintsByUser.
	 * @param User user
	 * @return Collection
	 * @throws FinderException
	 * @throws RemoteException
	 */
	public Collection ejbFindAllComplaintsByUser(User user) throws FinderException {
		return super.ejbFindAllCasesByUser(user);
	}

	/**
	 * Method ejbFindAllComplaintsByStatus.
	 * @param CaseStatus status
	 * @return Collection
	 * @throws FinderException
	 * @throws RemoteException
	 */
	public Collection ejbFindAllComplaintsByStatus(CaseStatus status) throws FinderException {
		return super.ejbFindAllCasesByStatus(status);
	}

	/**
	 * Method ejbFindAllComplaintsByManager.
	 * @param user
	 * @return Collection
	 * @throws FinderException
	 * @throws RemoteException
	 */
	public Collection ejbFindAllComplaintsByManager(User user) throws FinderException {
		return this.idoFindPKsBySQL("select * from "+getEntityName()+" where "+COLUMN_MANAGER_ID+" = "+(user.getPrimaryKey()).toString());
	}

	/**
	 * Method ejbFindAllComplaintsByType.
	 * @param type
	 * @return Collection
	 * @throws FinderException
	 * @throws RemoteException
	 */
	public Collection ejbFindAllComplaintsByType(CaseCode type) throws FinderException {
		return this.idoFindPKsBySQL("select * from "+getEntityName()+" where "+COLUMN_COMPLAINT_TYPE+" = "+(type.getPrimaryKey()).toString());
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
