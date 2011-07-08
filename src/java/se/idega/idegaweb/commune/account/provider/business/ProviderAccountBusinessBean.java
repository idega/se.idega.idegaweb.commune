package se.idega.idegaweb.commune.account.provider.business;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;

import se.idega.idegaweb.commune.account.business.AccountApplicationBusinessBean;
import se.idega.idegaweb.commune.account.business.AccountBusiness;
import se.idega.idegaweb.commune.account.business.IncompleteApplicationException;
import se.idega.idegaweb.commune.account.data.AccountApplication;
import se.idega.idegaweb.commune.account.provider.data.ProviderApplication;
import se.idega.idegaweb.commune.account.provider.data.ProviderApplicationHome;

import com.idega.block.school.business.SchoolBusiness;
import com.idega.block.school.data.School;
import com.idega.block.school.data.SchoolArea;
import com.idega.block.school.data.SchoolAreaHome;
import com.idega.block.school.data.SchoolCategory;
import com.idega.block.school.data.SchoolType;
import com.idega.core.location.data.PostalCode;
import com.idega.core.location.data.PostalCodeHome;
import com.idega.data.IDOCreateException;
import com.idega.data.IDOLookup;
import com.idega.user.data.User;
import com.idega.util.ListUtil;
import com.idega.util.Validator;
/**
 * Title:        idegaWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author tryggvil
 * @version 1.0
 */
public class ProviderAccountBusinessBean extends AccountApplicationBusinessBean implements ProviderAccountBusiness, AccountBusiness {

	private static final long serialVersionUID = 6494269110976268077L;

	@Override
	protected Class<ProviderApplication> getCaseEntityClass() {
		return ProviderApplication.class;
	}

	@Override
	public void acceptApplication(int applicationID, int performerUserID) throws CreateException, RemoteException {
		try	{
			User user = this.getUser(performerUserID);
			this.acceptApplication(applicationID, user);
		}
		catch (FinderException fe) {
			throw new IDOCreateException(fe);
		}
	}
	@Override
	public void acceptApplication(int applicationID, User performer)
		throws CreateException
	{
		super.acceptApplication(applicationID, performer);
	}
	@Override
	public void rejectApplication(int applicationID, User performer)
		throws RemoteException, CreateException, FinderException
	{
		rejectApplication(applicationID, performer, "");
	}
	@Override
	public void rejectApplication(int applicationID, int performerUserID) throws CreateException, RemoteException
	{
		try
		{
			User user = this.getUser(performerUserID);
			this.rejectApplication(applicationID, user);
		}
		catch (FinderException fe)
		{
			throw new IDOCreateException(fe);
		}
	}
	@Override
	public void rejectApplication(int applicationID, User performer, String reasonDescription)
		throws RemoteException, CreateException, FinderException
	{
		super.rejectApplication(applicationID, performer, reasonDescription);
	}
	protected ProviderApplicationHome getProviderApplicationHome()
	{
		try
		{
			return (ProviderApplicationHome) this.getIDOHome(ProviderApplication.class);
		}
		catch (RemoteException e)
		{
			throw new EJBException(e.getMessage());
		}
	}
	@Override
	public ProviderApplication getProviderApplication(int applicationID) throws FinderException
	{
		return this.getProviderApplicationHome().findByPrimaryKey(new Integer(applicationID));
	}

	@Override
	public ProviderApplication createApplication(
		String providerName,
		String address,
		String telephone,
		int numberOfPlaces,
		String managerName,
		String managerEmail,
		String additionalInfo,
		int postalCodeID,
		int schoolTypeID,
		int schoolAreaID)
		throws CreateException
	{

		int[] schoolTypeIDs = {schoolTypeID};
		return createApplication(providerName,address,telephone,numberOfPlaces,managerName,managerEmail,additionalInfo,
			postalCodeID,
			schoolTypeIDs,
			schoolAreaID);
	}


	@Override
	public ProviderApplication createApplication(
		String providerName,
		String address,
		String telephone,
		int numberOfPlaces,
		String managerName,
		String managerEmail,
		String additionalInfo,
		int postalCodeID,
		int[] schoolTypeIDs,
		int schoolAreaID)
		throws CreateException
	{
		try
		{
			//if((providerName==null)||(address==null)||(telephone==null)||(numberOfPlaces==null)||(managerName==null)||(managerEmail==null))
			if (!getValidator().isStringValid(providerName))
				throw new IncompleteApplicationException("Provider name is not set");
			if (!getValidator().isStringValid(address))
				throw new IncompleteApplicationException("Address is not set");
			if (!getValidator().isStringValid(telephone))
				throw new IncompleteApplicationException("Telephone is not set");
			if ((numberOfPlaces < 1))
				throw new IncompleteApplicationException("Number of Places is invalid");
			if (!getValidator().isStringValid(managerName))
				throw new IncompleteApplicationException("Manager Name is not set");
			if (!getValidator().isEmail(managerEmail))
				throw new IncompleteApplicationException("Manager Email is invalid");
			ProviderApplication appl = getProviderApplicationHome().create();
			appl.setAddress(address);
			appl.setName(providerName);
			appl.setManagerName(managerName);
			appl.setAdditionalInfo(additionalInfo);
			appl.setEmailAddress(managerEmail);
			appl.setNumberOfPlaces(numberOfPlaces);
			appl.setPhone(telephone);
			if(postalCodeID!=-1){
					appl.setPostalCode(postalCodeID);
			}
			if(schoolTypeIDs!=null){
					appl.setSchoolTypes(schoolTypeIDs);
			}
			if(schoolAreaID!=-1){
					appl.setSchoolArea(schoolAreaID);
			}
			appl.store();
			return appl;
		}
		catch (Exception e)
		{
			throw new IDOCreateException(e);
		}
	}
	protected Validator getValidator()
	{
		return Validator.getInstance();
	}
	/**
	 * @see se.idega.idegaweb.commune.account.business.AccountApplicationBusinessBean#getAllAcceptedApplications()
	 */
	@Override
	public Collection getAllAcceptedApplications() throws FinderException
	{
		return getProviderApplicationHome().findAllApprovedApplications();
	}
	/**
	 * @see se.idega.idegaweb.commune.account.business.AccountApplicationBusinessBean#getAllPendingApplications()
	 */
	@Override
	public Collection getAllPendingApplications() throws FinderException
	{
		return getProviderApplicationHome().findAllPendingApplications();
	}
	/**
	 * @see se.idega.idegaweb.commune.account.business.AccountApplicationBusinessBean#getAllRejectedApplications()
	 */
	@Override
	public Collection getAllRejectedApplications() throws FinderException
	{
		return getProviderApplicationHome().findAllRejectedApplications();
	}
	@Override
	public AccountApplication getApplication(int applicationID) throws FinderException
	{
		return getProviderApplication(applicationID);
	}
	/**
	 * Overrided from superclass
	 */
	@Override
	protected User createUserForApplication(AccountApplication theCase) throws CreateException, RemoteException
	{
		School school = createSchoolForApplication(theCase);
		User user = createProviderAdministratorForApplication(theCase,school);
		//createLoginAndSendMessage(theCase);
		return user;
	}

	/**
	 * Creates a School/provider administrator from the application in the Commune system
	 */
	protected User createProviderAdministratorForApplication(AccountApplication theCase,School school) throws CreateException, RemoteException
	{
		String firstName = null;
		String lastName = null;
		String applicantName = "";
		try{
			applicantName = theCase.getApplicantName();
			firstName = theCase.getApplicantName().substring(0,applicantName.indexOf(" "));
			lastName = applicantName.substring(
				applicantName.lastIndexOf(" ") + 1,
				applicantName.length());
		}
		catch(Exception e){
			//Catches the case when there is no last name (only one name specified)
			firstName = applicantName;
			lastName = "";
		}
		User user = null;
		try{
			user = getUserBusiness().createProviderAdministrator(firstName, null, lastName, school);
		}
		catch(FinderException fe){
			throw new IDOCreateException(fe);
		}
		theCase.setOwner(user);
		theCase.store();
		return user;
	}

	protected School createSchoolForApplication(AccountApplication theCase) throws RemoteException
	{
		ProviderApplication appl = ((ProviderApplication)theCase);
		String providerName = appl.getName();
		String address = appl.getAddress();
		String phone = appl.getPhone();
		PostalCode pCode = appl.getPostalCode();
		String zipcode = pCode.getPostalCode();
		String ziparea = pCode.getName();
		/**
		 * @todo: Remove hardcoding
		 */
		//int school_type = 1;
		//int school_area = 1;
		int school_area = ((Integer)appl.getSchoolArea().getPrimaryKey()).intValue();
		//int[] school_types = {1};
		Collection schoolTypes = appl.getSchoolTypes();
		int[] school_types = new int[schoolTypes.size()];
		int i=0;
		for (Iterator iter = schoolTypes.iterator(); iter.hasNext();)
		{
			SchoolType element = (SchoolType) iter.next();
			int schTypeID = ((Integer)element.getPrimaryKey()).intValue();
			school_types[i++]=schTypeID;
		}
		School school = getSchoolBusiness().createSchool(providerName,address,zipcode,ziparea,phone,school_area,school_types);
		return school;
	}


	/**
	 * Returns a collection of com.idega.core.data.PostalCode
	 */
	@Override
	public Collection getAvailablePostalCodes() throws java.rmi.RemoteException{
		try {
			Collection coll = null;
			coll = getPostalCodeHome().findAllOrdererByCode();
			return coll;
		} catch (FinderException e) {
			return ListUtil.getEmptyList();
		}
	}

	/**
	 * Returns a collection of com.idega.block.school.data.SchoolType
	 * @return Only Childcare schoolTypes
	 */
	@Override
	public Collection getAvailableSchoolTypes() throws java.rmi.RemoteException{
		return getAvailableChildCareSchoolTypes();
	}


	/**
	 * Returns a collection of com.idega.block.school.data.SchoolType
	 */
	public Collection getAvailableChildCareSchoolTypes() throws java.rmi.RemoteException{
		return getSchoolBusiness().findAllSchoolTypesForChildCare();
	}

	/**
	 * Returns a collection of com.idega.block.school.data.SchoolArea
	 */
	@Override
	public Collection<SchoolArea> getAvailableSchoolAreas(SchoolCategory schoolCategory) throws java.rmi.RemoteException{
		if (schoolCategory == null) {
			try {
				SchoolAreaHome schoolAreaHome = (SchoolAreaHome) IDOLookup.getHome(SchoolArea.class);
				return schoolAreaHome.getAllScoolAreas();
			} catch (FinderException e) {
				e.printStackTrace();
				return null;
			}
		}

		return getSchoolBusiness().findAllSchoolAreas(schoolCategory);
	}


	protected SchoolBusiness getSchoolBusiness() throws RemoteException
	{
		SchoolBusiness bus = (SchoolBusiness)this.getServiceInstance(SchoolBusiness.class);
		return bus;
	}

	protected PostalCodeHome getPostalCodeHome() throws RemoteException
	{
		PostalCodeHome bus = (PostalCodeHome)this.getIDOHome(PostalCode.class);
		return bus;
	}
		@Override
		public String getAcceptMessageSubject()
	{
		return this.getLocalizedString("acc.app.provider.appr.subj", "Your provider account application has been approved");
	}

	@Override
	public String getRejectMessageSubject()
	{
		return this.getLocalizedString("acc.app.provider.rej.subj", "Your provider account application has been rejected");
	}
}
