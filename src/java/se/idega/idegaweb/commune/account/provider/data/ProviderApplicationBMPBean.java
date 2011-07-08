package se.idega.idegaweb.commune.account.provider.data;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.FinderException;

import se.idega.idegaweb.commune.account.data.AccountApplication;

import com.idega.block.process.data.AbstractCaseBMPBean;
import com.idega.block.process.data.Case;
import com.idega.block.school.data.SchoolArea;
import com.idega.block.school.data.SchoolType;
import com.idega.block.school.data.SchoolTypeHome;
import com.idega.core.location.data.PostalCode;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOLookup;
import com.idega.data.IDORemoveRelationshipException;
import com.idega.user.data.User;
import com.idega.util.ListUtil;
/**
 * Title:        idegaWeb
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      idega software
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class ProviderApplicationBMPBean
	extends AbstractCaseBMPBean
	implements ProviderApplication, Case, AccountApplication
{
	private static final String CASE_CODE_KEY = "ANANSKO";
	private static final String TABLE_NAME = "COMM_ACC_PROV";
	private static final String COLUMN_EMAIL = "PROV_EMAIL";
	private static final String COLUMN_NAME = "PROV_NAME";
	private static final String COLUMN_ADDRESS = "PROV_ADDRESS";
	private static final String COLUMN_PLACES = "PROV_PLACES";
	private static final String COLUMN_PHONE = "PROV_PHONE";
	private static final String COLUMN_ADDITIONAL_INFO = "PROV_ADD_INFO";
	private static final String COLUMN_MANAGER_NAME = "PROV_MANAGER_NAME";
	//private static final String COLUMN_SCHOOL_TYPE = "SCH_TYPE";
	private static final String COLUMN_SCHOOL_AREA = "SCH_AREA";
	private static final String COLUMN_POSTAL_CODE = "POSTAL_CODE";

	@Override
	public void initializeAttributes()
	{
		addGeneralCaseRelation();
		this.addAttribute(COLUMN_NAME, "Provider name", String.class);
		this.addAttribute(COLUMN_ADDRESS, "Provider address", String.class);
		this.addAttribute(COLUMN_PHONE, "Telephone", String.class, 30);
		this.addAttribute(COLUMN_MANAGER_NAME, "Manager Name", String.class);
		this.addAttribute(COLUMN_EMAIL, "Manager email", String.class);
		this.addAttribute(COLUMN_PLACES, "Provider places", Integer.class);
		this.addAttribute(COLUMN_ADDITIONAL_INFO, "Additional info", String.class, 2000);

		//this.addManyToOneRelationship(COLUMN_SCHOOL_TYPE,SchoolType.class);
		this.addManyToManyRelationShip(SchoolType.class);

		this.addManyToOneRelationship(COLUMN_SCHOOL_AREA,SchoolArea.class);
		this.addManyToOneRelationship(COLUMN_POSTAL_CODE,PostalCode.class);

	}
	@Override
	public String getEntityName()
	{
		return ProviderApplicationBMPBean.TABLE_NAME;
	}
	@Override
	public String getCaseCodeKey()
	{
		return CASE_CODE_KEY;
	}
	@Override
	public String getCaseCodeDescription()
	{
		return "Application for School Administrators";
	}
	@Override
	public String getEmailAddress()
	{
		return this.getStringColumnValue(COLUMN_EMAIL);
	}
	@Override
	public void setEmailAddress(String emailAddress)
	{
		this.setColumn(COLUMN_EMAIL, emailAddress);
	}
	@Override
	public String getName()
	{
		return this.getStringColumnValue(COLUMN_NAME);
	}
	@Override
	public void setName(String providerName)
	{
		this.setColumn(COLUMN_NAME, providerName);
	}
	@Override
	public String getAddress()
	{
		return this.getStringColumnValue(COLUMN_ADDRESS);
	}
	@Override
	public void setAddress(String providerAddress)
	{
		this.setColumn(COLUMN_ADDRESS, providerAddress);
	}
	@Override
	public String getPhone()
	{
		return this.getStringColumnValue(COLUMN_PHONE);
	}
	@Override
	public void setPhone(String phoneString)
	{
		this.setColumn(COLUMN_PHONE, phoneString);
	}
	@Override
	public int getNumberOfPlaces()
	{
		return this.getIntColumnValue(COLUMN_PLACES);
	}
	@Override
	public void setNumberOfPlaces(int places)
	{
		this.setColumn(COLUMN_PLACES, places);
	}
	@Override
	public String getManagerName()
	{
		return this.getStringColumnValue(COLUMN_MANAGER_NAME);
	}
	@Override
	public void setManagerName(String name)
	{
		this.setColumn(COLUMN_MANAGER_NAME, name);
	}
	@Override
	public String getAdditionalInfo()
	{
		return this.getStringColumnValue(COLUMN_ADDITIONAL_INFO);
	}
	@Override
	public void setAdditionalInfo(String info)
	{
		this.setColumn(COLUMN_ADDITIONAL_INFO, info);
	}
	/**
	 * @see se.idega.idegaweb.commune.account.data.AccountApplication#getApplicantName()
	 */
	@Override
	public String getApplicantName()
	{
		return getManagerName();
	}
	/**
	 * @see se.idega.idegaweb.commune.account.data.AccountApplication#getEmail()
	 */
	@Override
	public String getEmail()
	{
		return this.getEmailAddress();
	}
	/**
	 * @see se.idega.idegaweb.commune.account.data.AccountApplication#setApplicantName(String)
	 */
	@Override
	public void setApplicantName(String p0)
	{
		setManagerName(p0);
	}
	/**
	 * @see se.idega.idegaweb.commune.account.data.AccountApplication#setEmail(String)
	 */
	@Override
	public void setEmail(String p0)
	{
		setEmailAddress(p0);
	}

/**

	//
	 //Gets the school type according to this application
	 //
	public SchoolType getSchoolType()
	{
		return (SchoolType)this.getColumnValue(COLUMN_SCHOOL_TYPE);
	}

	//
	 //Sets the school type according to this application
	 //
	public void setSchoolType(SchoolType type) throws RemoteException
	{
		setColumn(COLUMN_SCHOOL_TYPE,type);
	}

	//
	//Sets the school type according to this application
	//
	public void setSchoolType(int typeID) throws RemoteException
	{
		setColumn(COLUMN_SCHOOL_TYPE,typeID);
	}
**/

	//
	 //Gets the school type according to this application
	 //
	@Override
	public Collection getSchoolTypes()
	{
		//return (SchoolType)this.getColumnValue(COLUMN_SCHOOL_TYPE);
		try{
			return this.idoGetRelatedEntities(SchoolType.class);
		}
		catch(Exception e){
			e.printStackTrace();
			return ListUtil.getEmptyList();
		}
	}

	//
	 //Sets the school type according to this application
	 //
	@Override
	public void setSchoolTypes(Collection types)
	{
		//setColumn(COLUMN_SCHOOL_TYPE,type);
		for (Iterator iterator = types.iterator(); iterator.hasNext();)
		{
			try{
				SchoolType element = (SchoolType) iterator.next();
				addSchoolType(element);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	//
	//Sets the school type according to this application
	//
	@Override
	public void setSchoolTypes(int[] typeIDs)
	{
		//setColumn(COLUMN_SCHOOL_TYPE,typeID);
		for (int i = 0; i < typeIDs.length; i++)
		{
			int id = typeIDs[i];
			try{
				SchoolType type  = getSchoolTypeHome().findByPrimaryKey(new Integer(id));
				addSchoolType(type);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method getSchoolTypeHome.
	 */
	private SchoolTypeHome getSchoolTypeHome()throws RemoteException
	{
		return (SchoolTypeHome)IDOLookup.getHome(SchoolType.class);
	}


	/**
	 * Adds a schooltype to this application
	 **/
	@Override
	public void addSchoolType(SchoolType type){
			try{
				this.idoAddTo(type);
			}
			catch(Exception e){
				e.printStackTrace();
			}
	}

	/**
	 *Gets the school area according to this application
	 */
	@Override
	public SchoolArea getSchoolArea()
	{
		return (SchoolArea)this.getColumnValue(COLUMN_SCHOOL_AREA);
	}

	/**
	 *Sets the postal code according to this application
	 */
	@Override
	public void setPostalCode(PostalCode code)
	{
		setColumn(COLUMN_POSTAL_CODE,code);
	}

	/**
	 *Gets the postal code according to this application
	 */
	@Override
	public void setPostalCode(int postalCodeID)
	{
		setColumn(COLUMN_POSTAL_CODE,postalCodeID);
	}

	/**
	 *Gets the postal code according to this application
	 */
	@Override
	public PostalCode getPostalCode()
	{
		return (PostalCode)this.getColumnValue(COLUMN_POSTAL_CODE);
	}

	/**
	 *Sets the school area according to this application
	 */
	@Override
	public void setSchoolArea(SchoolArea area)
	{
		setColumn(COLUMN_SCHOOL_AREA,area);
	}

	/**
	 *Sets the school area according to this application
	 */
	@Override
	public void setSchoolArea(int areaID)
	{
		setColumn(COLUMN_SCHOOL_AREA,areaID);
	}

	/**
	 * Mandates EJB standard behaviour
	 **/
	@Override
	protected boolean doInsertInCreate(){
		return true;
	}


	public Collection ejbFindAllPendingApplications() throws FinderException
	{
		return super.ejbFindAllCasesByStatus(this.getCaseStatusOpen().toString());
		//return null;
	}
	public Collection ejbFindAllRejectedApplications() throws FinderException
	{
		return super.ejbFindAllCasesByStatus(this.getCaseStatusDenied().toString());
		//return null;
	}
	public Collection ejbFindAllApprovedApplications() throws FinderException
	{
		return super.ejbFindAllCasesByStatus(this.getCaseStatusGranted().toString());
		//return null;
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