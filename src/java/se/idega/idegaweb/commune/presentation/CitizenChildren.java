package se.idega.idegaweb.commune.presentation;

import is.idega.block.family.business.FamilyLogic;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import se.idega.idegaweb.commune.business.CommuneUserBusiness;
import se.idega.util.PIDChecker;
import com.idega.business.IBOLookup;
import com.idega.business.IBORuntimeException;
import com.idega.core.location.data.Address;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.Parameter;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.user.data.User;
import com.idega.user.data.UserHome;
import com.idega.util.Age;
import com.idega.util.PersonalIDFormatter;

/**
 * @author <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.0
 */
public class CitizenChildren extends CommuneBlock {
	//private IWBundle iwb;
	private IWResourceBundle iwrb;
	private Text buttonLabel;
	//private Text ssnLabel;
	public static final String prmChildId = "comm_child_id";
	public static final String prmChildUniqueId = "comm_child_unique_id";
  private static final String prmParentId = "comm_parent_id";
	private static final String prmChildSSN = "comm_child_ssn";
	private String prmSubmitName = "submit_cits_child";
	private boolean showSSNSearchForm = false;
	private boolean showOutOfRangeChildren =true;
	private int fromAge = -1, toAge = 100;
	private boolean addLoggedInUser = false;
	
	private boolean useCommuneRestriction = false;
	private boolean iUseSessionUser = false;

	public CitizenChildren() {
		
		//ssnLabel = getText("");
	}

	public void main(IWContext iwc) throws java.rmi.RemoteException {
		//iwb = getBundle(iwc);
		iwrb = getResourceBundle(iwc);
		if (iwc.isLoggedOn()) {
			if (useCommuneRestriction && !belongsToCommune(iwc, getUser(iwc))) {
				add(getErrorText(localize("does_not_belong_to_commune", "You have to live in the commune to be able to use this function.")));
				return;
			}

			Table T = new Table();
			int row = 1;
			int col = 1;
			if (iwc.isParameterSet(prmSubmitName) && iwc.getParameter(prmSubmitName).equals("true")) {
				try {
					User child = processSSNRequest(iwc);
					T.add(getChildLink(child, child.getUniqueId() != null), col, row);
				}
				catch (javax.ejb.FinderException fix) {
					T.add(new Text(fix.getMessage()), col, row);
				}
				row++;
			}

				T.add(getChildrenForm(iwc));
			add(T);
		}
		else
			add(iwrb.getLocalizedString("citizen_children.no_citizen_logged_on", "You are not a citizen of Nacka Commune"));
	}
	
	private boolean belongsToCommune(IWContext iwc, User user) {
		try {
			CommuneUserBusiness business = (CommuneUserBusiness) IBOLookup.getServiceInstance(iwc, CommuneUserBusiness.class);
			Address address = business.getUsersMainAddress(user);
			return address.getPostalCode().getCommune().equals(business.getDefaultCommune());
		}
		catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}
	}

	public static String getChildIDParameterName() {
		return prmChildId;
	}
	
	public static String getChildUniqueIDParameterName() {
		return prmChildUniqueId;
	}

  public static String getParentIDParameterName() {
		return prmParentId;
	}

	public static String getChildSSNParameterName() {
		return prmChildId;
	}

	public static Parameter getChildIDParameter(int child_id) {
		return new Parameter(prmChildId, String.valueOf(child_id));
	}
	
	public static Parameter getChildUniqueIDParameter(int child_unique_id) {
		return new Parameter(prmChildUniqueId, String.valueOf(child_unique_id));
	}

	public static Parameter getChildSSNParameter(int child_ssn) {
		return new Parameter(prmChildSSN, String.valueOf(child_ssn));
	}
	
	private Text getButtonLabel(){
	    if(buttonLabel==null)
	        buttonLabel = getText("");
	    return buttonLabel;
	}

	public void setLocalizedButtonLabel(String localeString, String text) {
		buttonLabel.setLocalizedText(localeString, text);
	}
	
	protected boolean addLoggedInUser() {
		return addLoggedInUser;
	}
	
	public void setToAddLoggedInUser(boolean addUser) {
		addLoggedInUser = addUser;
	}

	private Form getChildrenForm(IWContext iwc) throws RemoteException {
		Form f = new Form();
		Table T = new Table();
		T.setCellpadding(0);
		T.setCellspacing(0);
		
		int row = 1;
		Collection childs = getChilds(iwc, getUser(iwc));
		if (addLoggedInUser()) {
			childs.add(getUser(iwc));
		}
		if (childs != null && !childs.isEmpty()) {
			boolean useUniqueID = true;
			Iterator iter = childs.iterator();
			Iterator iterator = childs.iterator();
			while (iterator.hasNext()) {
				User child = (User) iterator.next();
				if (child.getUniqueId() == null) {
					useUniqueID = false;
					break;
				}
			}
			
			User child;
			ArrayList outOfRangeChilds = new ArrayList();
			while (iter.hasNext()) {
				child = (User) iter.next();
				Age age = null;
				if (child.getDateOfBirth() != null)
					age = new Age(child.getDateOfBirth());
				else if (child.getPersonalID() != null)
					age = new Age(PIDChecker.getInstance().getDateFromPersonalID(child.getPersonalID()));

				if(age != null && getShowChild(iwc, child) && age.getYears() <= toAge && age.getYears() >=fromAge){
					T.add(getChildLink(child, useUniqueID), 1, row++);
					if (iter.hasNext())
						T.setHeight(row++,2);
				}
				else{
					outOfRangeChilds.add(child);
				}
			}
			if(showOutOfRangeChildren && !outOfRangeChilds.isEmpty()){
				T.setHeight(row++,12);
				T.add(getHeader(iwrb.getLocalizedString("citizen_children.out_of_range_childs","Children out of range:")),1,row);
				Iterator iter2 = outOfRangeChilds.iterator();
				++row;
				while(iter2.hasNext()){
					child = (User) iter2.next();
					T.add(new Text(child.getName()),1,row++);
				}
			}

		}

		if(showSSNSearchForm){
			Table submitTable = new Table(3,1);
			submitTable.setCellpadding(0);
			submitTable.setCellspacing(0);
			submitTable.setWidth(2, "4");
			
			TextInput inputSSN = (TextInput) getStyledInterface(new TextInput(prmChildSSN));
			String label = getButtonLabel().getLocalizedText(iwc);

			SubmitButton submit = (SubmitButton) getButton(new SubmitButton(label, prmSubmitName, "true"));
			T.setHeight(row++,12);
			submitTable.add(inputSSN, 1, 1);
			submitTable.add(submit, 3, 1);
			T.add(submitTable, 1, row);
		}
		f.add(T);
		return f;
	}

	/**
	 * @param child
	 * @return
	 */
	protected boolean getShowChild(IWContext iwc, User child) {
		try {
			return getFamilyLogic(iwc).isChildInCustodyOf(child, getUser(iwc));
		}
		catch (RemoteException re) {
			return false;
		}
	}

	private Link getChildLink(User child, boolean useUniqueID) {
		Link L = new Link(child.getName());
		if (getResponsePage() != null)
			L.setPage(getResponsePage());
		if (useUniqueID)
			L.addParameter(prmChildUniqueId, child.getUniqueId());
		else
			L.addParameter(prmChildId, ((Integer) child.getPrimaryKey()).toString());
		if (getEventListener() != null) {
			L.setEventListener(getEventListener());
		}
		return L;
	}

	private User processSSNRequest(IWContext iwc) throws javax.ejb.FinderException, java.rmi.RemoteException {
		String ssn = iwc.getParameter(prmChildSSN);
		if (ssn != null && !ssn.equals("")) {
			ssn = handlePersonalID(ssn);
			UserHome userHome = (UserHome) IDOLookup.getHome(User.class);
			User child = userHome.findByPersonalID(ssn);
			if (child != null) {
				return child;
			}
		}
		throw new javax.ejb.FinderException("No user with that ssn");
	}
	
	private String handlePersonalID(String s){
		return PersonalIDFormatter.stripForDatabaseSearch(s);
	}

	private Collection getChilds(IWContext iwc, User user) {
		/** @todo familymethods from usersystem */
		try {
			return getUserBusiness(iwc).getChildrenForUser(user);
		}
		catch (RemoteException e) {
		}
		
		return new Vector();
	}
	
	private FamilyLogic getFamilyLogic(IWApplicationContext iwac) {
		try {
			return (FamilyLogic) IBOLookup.getServiceInstance(iwac, FamilyLogic.class);
		}
		catch (RemoteException e) {
			throw new IBORuntimeException(e);
		}
	}
	
	protected Class getEventListener() {
		return null;
	}
	
	protected User getUser(IWContext iwc) throws RemoteException {
		if (iUseSessionUser) {
			return getUserSession(iwc).getUser();
		}
		else {
			return iwc.getCurrentUser();
		}
	}

	/**
	 * Sets if the component is to show the search for to search by personal_id.
	 * <br>This defaults to false
	 **/
	public void setShowSSNSearchForm( boolean showForm){
		this.showSSNSearchForm = showForm;
	}
	
	/**
	 * Sets if the component is to set children age range.
	 * <br>This defaults to the range -1 to 1000
	 **/
	public void setAgeRange(int from,int to){
		this.fromAge = from;
		this.toAge = to;
	}
	
	/**
	 * Sets if the component is to show the children that dont fit specified range of age.
	 * <br>This defaults to true
	 **/
	public void setShowOutOfRangeChilds(boolean show){
		this.showOutOfRangeChildren = show;
	}
	
	public void setUseCommuneRestriction(boolean useRestriction) {
		useCommuneRestriction = useRestriction;
	}

	
	public void setUseSessionUser(boolean userSessionUser) {
		iUseSessionUser = userSessionUser;
	}
}