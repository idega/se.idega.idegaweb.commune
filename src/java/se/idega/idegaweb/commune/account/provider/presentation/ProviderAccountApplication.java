/*
 * $Id: ProviderAccountApplication.java,v 1.12 2004/02/03 09:55:37 staffan Exp $
 *
 * Copyright (C) 2002 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package se.idega.idegaweb.commune.account.provider.presentation;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import se.idega.idegaweb.commune.account.provider.business.ProviderAccountBusiness;
import se.idega.idegaweb.commune.presentation.CommuneBlock;

import com.idega.block.school.data.SchoolArea;
import com.idega.block.school.data.SchoolCategory;
import com.idega.block.school.data.SchoolType;
import com.idega.business.IBOLookup;
import com.idega.core.location.data.PostalCode;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.presentation.ExceptionWrapper;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SelectionBox;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextArea;
import com.idega.presentation.ui.TextInput;
import com.idega.util.ListUtil;
import com.idega.util.Validator;
/**
 * @author <a href="mail:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class ProviderAccountApplication extends CommuneBlock {
	protected final static int ACTION_VIEW_FORM = 0;
	protected final static int ACTION_SUBMIT_FORM = 1;
	private final static String PARAM_PROV_TYPE="paa_prov_type_id";
	private final static String PARAM_PROV_NAME = "paa_prov_name";
	private final static String PARAM_EMAIL = "paa_email";
	private final static String PARAM_PHONE = "paa_phone";
	private final static String PARAM_NUM_PLACES = "paa_num_places";
	private final static String PARAM_FORM_SUBMIT = "paa_submit";
	private final static String PARAM_ADDR = "paa_addr";
	private final static String PARAM_MAN_NAME = "paa_man_name";
	private final static String PARAM_ADD_INFO = "paa_add_info";
	private final static String PARAM_POSTAL_CODE = "paa_pst_code";
	private final static String PARAM_SCH_AREA = "paa_sch_area";

	//private final static String PARAM_PROV_TYPE = "paa_prov_type";
	private final static String ERROR_PROV_NAME = "paa_error_prov_name";
	private final static String ERROR_MAN_NAME = "paa_error_man_name";
	private final static String ERROR_ADDR = "paa_error_addr";
	private final static String ERROR_PHONE = "paa_error_phone_home";
	private final static String ERROR_NUM_PLACES = "paa_error_num_places";
	private final static String ERROR_NO_INSERT = "paa_no_insert";
	private final static String ERROR_NOT_EMAIL = "paa_err_email";
	private final static String ERROR_NO_POSTAL_CODE = "paa_err_no_postal_code";
	private final static String ERROR_NO_PROV_TYPE = "paa_err_no_prov_type";
	private final static String ERROR_NO_SCHOOL_AREA = "paa_err_no_school_type";

	private final static String TEXT_APPLICATION_SUBMITTED =
		"paa_app_submitted";
	private boolean _isSchoolAreaError = false;
	private boolean _isProvNameError = false;
	private boolean _isProvTypeError = false;
	private boolean _isManNameError = false;
	private boolean _isPostalCodeError = false;
	private boolean _isAddressError = false;
	private boolean _isPhoneError = false;
	private boolean _isEmailError = false;
	private boolean _isNumPlacesError = false;
	private boolean _isError = false;
	private Vector _errorMsg = null;
	private int applicationID = -1;
	protected String provNameString;
	protected String emailString;
	protected String phoneString;
	protected String numPlacesString;
	protected String addressString;
	protected String manNameString;
	protected String addInfoString;
	protected int postalCodeID = -1;
	protected int schoolAreaID = -1;
	protected int[] schoolTypeIDs;

	protected static final String PARAM_APPLICATION_ID = "paa_appl_id";
	int mainTableRows = 18;
	int mainTableColumns = 2;
	private Table inputTable = new Table(mainTableColumns, mainTableRows);

	private boolean bSubmissionSuccessful=true;


	@Override
	public void main(IWContext iwc) {
		setResourceBundle(getResourceBundle(iwc));
		try {
			int action = parseAction(iwc);
			initData(iwc);
			performAction(action, iwc);
		} catch (Exception e) {
			super.add(new ExceptionWrapper(e, this));
		}
	}
	@Override
	public Object clone() {
		ProviderAccountApplication p =
			(ProviderAccountApplication) super.clone();
		if (this.inputTable != null) {
			p.inputTable = (Table) this.inputTable.clone();
		}
		return p;
	}

	/**
	 * Can be ovverrided in subclasses
	 */
	protected void performAction(int action, IWContext iwc) throws Exception {
		switch (action) {
			case ACTION_VIEW_FORM :
				viewForm(iwc);
				break;
			case ACTION_SUBMIT_FORM :
				submitForm(iwc);
				break;
		}
	}
	/**
	 * Method initData.
	 * @param iwc
	 */
	private void initData(IWContext iwc) {
		String applicationIDString = iwc.getParameter(PARAM_APPLICATION_ID);
		if (getValidator().isInt(applicationIDString)) {
			setApplicationID(Integer.parseInt(applicationIDString));
		}
		if (getApplicationID() == -1) {
			provNameString = iwc.getParameter(PARAM_PROV_NAME);
			emailString = iwc.getParameter(PARAM_EMAIL);
			phoneString = iwc.getParameter(PARAM_PHONE);
			numPlacesString = iwc.getParameter(PARAM_NUM_PLACES);
			addressString = iwc.getParameter(PARAM_ADDR);
			manNameString = iwc.getParameter(PARAM_MAN_NAME);
			addInfoString = iwc.getParameter(PARAM_ADD_INFO);
		}
	}
	/**
	 * Method getApplicationID.
	 * @return int
	 */
	protected int getApplicationID() {
		return applicationID;
	}
	private void viewForm(IWContext iwc) {
		Form accountForm = new Form();
		inputTable.setCellspacing(2);
		inputTable.setCellpadding(4);
		inputTable.setAlignment(mainTableColumns, mainTableRows, "right");
		inputTable.setColor(getBackgroundColor());

		String provType = localize(PARAM_PROV_TYPE,"Type");
		String provName =
			localize(PARAM_PROV_NAME, "Shool/ChildcareCenter name");
		String schoolAreas = localize(PARAM_SCH_AREA,"School area:");
		String email = localize(PARAM_EMAIL, "E-Mail");
		String phone = localize(PARAM_PHONE, "Phone");
		String numPlaces = localize(PARAM_NUM_PLACES, "Number of places");
		String postalCode = localize(PARAM_POSTAL_CODE, "Postal code");
		String address = localize(PARAM_ADDR, "Address");
		String manName = localize(PARAM_MAN_NAME, "Name");
		String addInfo = localize(PARAM_ADD_INFO, "Additional info");
		String manager = localize("paa_manager","Manager:");
		String provider = localize("paa_provider","Provider:");

		//new row
		int currRow=1;
		int currCol=1;

		//Text and formelement
		Text textProvider = null;
		textProvider = this.getSmallText(provider);
		textProvider.setBold();
		add(textProvider,currCol,currRow);

		//new row
		currCol=1;
		currRow+=1;

		//Text and formelement
		Text schTypesText = null;
		if (!_isProvTypeError)
			schTypesText = getSmallText(provType);
		else
			schTypesText = getSmallErrorText(provType);
		//SelectionBox schTypesMenu = this.getSchoolTypesBox(iwc);
		PresentationObjectContainer schTypesMenu = this.getSchoolTypesCheckboxes(iwc);

		add(schTypesText,currCol,currRow);
		add(schTypesMenu,currCol,currRow+1);

		//Make room for selectionBox
		inputTable.mergeCells(currCol,currRow+1,currCol,currRow+3);

		//new column
		currCol+=1;

		//Text and formelement
		Text provNameText = null;
		if (!_isProvNameError)
			provNameText = getSmallText(provName);
		else
			provNameText = getSmallErrorText(provName);
		TextInput inputProvName = new TextInput(PARAM_PROV_NAME);
		inputProvName.setMaxlength(40);
		if (provNameString != null)
			inputProvName.setContent(provNameString);
		add(provNameText,currCol,currRow);
		add(inputProvName,currCol,currRow+1);

		//Text and formelement
		currRow+=2;

		//nothing in the second row



		//new row
		currCol=1;
		currRow+=2;


		//Text and formelement
		Text textAddress = null;
			if (!_isAddressError)
			textAddress = getSmallText(address);
		else
			textAddress = getSmallErrorText(address);
		TextInput inputAddress = new TextInput(PARAM_ADDR);
		inputAddress.setMaxlength(50);
		inputAddress.setStyleClass(getSmallTextFontStyle());
		if (addressString != null)
			inputAddress.setContent(addressString);
		add(textAddress,currCol,currRow);
		add(inputAddress,currCol,currRow+1);

		//new column
		currCol+=1;

		//Text and formelement
		Text textPostalCode = null;
			if (!_isPostalCodeError)
			textPostalCode = getSmallText(postalCode);
		else
			textPostalCode = getSmallErrorText(postalCode);
		DropdownMenu menuPostalCodes = this.getPostalCodesMenu(iwc);
		add(textPostalCode,currCol,currRow);
		add(menuPostalCodes,currCol,currRow+1);

		//new row
		currCol=1;
		currRow+=2;


		//Text and formelement
		Text textPhone = null;
			if (!_isPhoneError)
			textPhone = getSmallText(phone);
		else
			textPhone = getSmallErrorText(phone);
		TextInput inputPhone = new TextInput(PARAM_PHONE);
		inputPhone.setStyleClass(getSmallTextFontStyle());
		inputPhone.setMaxlength(20);
		if (phoneString != null)
			inputPhone.setContent(phoneString);
		add(textPhone,currCol,currRow);
		add(inputPhone,currCol,currRow+1);


		//new column
		currCol+=1;

		Text textShoolAreas = null;
			if (!_isSchoolAreaError)
			textShoolAreas = getSmallText(schoolAreas);
		else
			textShoolAreas = getSmallErrorText(schoolAreas);
		DropdownMenu menuShoolAreas = this.getSchoolAreasMenu(iwc);
		add(textShoolAreas,currCol,currRow);
		add(menuShoolAreas,currCol,currRow+1);

		//new row
		currCol=1;
		currRow+=2;

		//Nothing in this cell

		//new column
		currCol+=1;

		//Text and formelement
		Text textNumPlaces = null;
		if (!_isNumPlacesError)
			textNumPlaces = getSmallText(numPlaces);
		else
			textNumPlaces = getSmallErrorText(numPlaces);
		TextInput inputNumPlaces = new TextInput(PARAM_NUM_PLACES);
		inputNumPlaces.setStyleClass(getSmallTextFontStyle());
		inputNumPlaces.setMaxlength(3);
		inputNumPlaces.setLength(3);
		if (numPlacesString != null)
			inputNumPlaces.setContent(numPlacesString);
		add(textNumPlaces,currCol,currRow);
		add(inputNumPlaces,currCol,currRow+1);



		//new row
		currCol=1;
		currRow+=2;

		//Text and formelement
		Text textManager = null;
		textManager = this.getSmallText(manager);
		textManager.setBold();
		add(textManager,currCol,currRow);

		//new row
		currCol=1;
		currRow+=1;

		//Text and formelement
		Text textManName = null;
			if (!_isManNameError)
			textManName = getSmallText(manName);
		else
			textManName = getSmallErrorText(manName);
		TextInput inputManName = new TextInput(PARAM_MAN_NAME);
		inputManName.setMaxlength(40);
		inputManName.setLength(30);
		inputManName.setStyleClass(getSmallTextFontStyle());
		if (manNameString != null)
			inputManName.setContent(manNameString);
		add(textManName,currCol,currRow);
		add(inputManName,currCol,currRow+1);

		//new column
		currCol+=1;

		//Text and formelement
		Text textEmail = null;
		if (!_isEmailError)
			textEmail = getSmallText(email);
		else
			textEmail = getSmallErrorText(email);
		TextInput inputEmail = new TextInput(PARAM_EMAIL);
		inputEmail.setAsEmail(localize(ERROR_NOT_EMAIL, "Not a valid email"));
		inputEmail.setStyleClass(getSmallTextFontStyle());
		inputEmail.setMaxlength(40);
		if (emailString != null)
			inputEmail.setContent(emailString);

		add(textEmail,currCol,currRow);
		add(inputEmail,currCol,currRow+1);


		//new row
		currCol=1;
		currRow+=2;

		//Text and formelement
		Text textAddInfo = null;
		if (!_isEmailError)
			textAddInfo = getSmallText(addInfo);
		else
			textAddInfo = getSmallErrorText(addInfo);
		TextArea inputAddInfo = new TextArea(PARAM_ADD_INFO);
		inputAddInfo.setWidth("200");
		inputAddInfo.setHeight("100");
		if (addInfoString != null)
			inputAddInfo.setContent(addInfoString);

		add(textAddInfo, currCol, currRow);
		//Making room for textarea
		inputTable.mergeCells(1, currRow+1, 2, currRow+1);
		inputTable.add(inputAddInfo, currCol, currRow+1);

		addButtons(iwc);
		if (_isError) {
			if (_errorMsg != null) {
				Table errorTable = new Table(1, 1);
				errorTable.setCellspacing(2);
				errorTable.setCellpadding(4);
				Iterator it = _errorMsg.iterator();
				while (it.hasNext()) {
					String errorMsg = (String) it.next();
					errorTable.add(getErrorText(errorMsg), 1, 1);
					errorTable.add(Text.getBreak(), 1, 1);
				}
				accountForm.add(errorTable);
			}
		}

				//Text for additional info:
		/*inputTable.mergeCells(1, 7, 2, 7);
		inputTable.add(getSmallText(addInfo), 1, 7);
		inputTable.add(inputProvName, 1, 2);
		inputTable.add(inputEmail, 2, 6);
		inputTable.add(inputPhone, 1, 4);
		inputTable.add(inputNumPlaces, 2, 4);
		inputTable.add(inputManName, 1, 6);
		inputTable.add(inputAddress, 2, 2);*/

		accountForm.add(inputTable);
		add(accountForm);
	}

	/**
	 * Method addButtons.
	 * @param iwc
	 */
	protected void addButtons(IWContext iwc) {
		SubmitButton submitButton =
			new SubmitButton(
				getBundle(iwc).getImageButton(
					localize(PARAM_FORM_SUBMIT, "Submit application")),
				PARAM_FORM_SUBMIT);
		submitButton.setStyleClass(getLinkFontStyle());
		//inputTable.add(submitButton, 2, 10);
		addButton(submitButton);
	}

	private void submitForm(IWContext iwc) {
		String provNameString = iwc.getParameter(PARAM_PROV_NAME);
		String phoneString = iwc.getParameter(PARAM_PHONE);
		String emailString = iwc.getParameter(PARAM_EMAIL);
		String numPlacesString = iwc.getParameter(PARAM_NUM_PLACES);
		String addressString = iwc.getParameter(PARAM_ADDR);
		String manNameString = iwc.getParameter(PARAM_MAN_NAME);
		String addInfoString = iwc.getParameter(PARAM_ADD_INFO);
		String postalCodeString = iwc.getParameter(PARAM_POSTAL_CODE);
		//String providerTypeString = iwc.getParameter(PARAM_PROV_TYPE);
		String[] providerTypesString = iwc.getParameterValues(PARAM_PROV_TYPE);

		String schoolAreaString = iwc.getParameter(PARAM_SCH_AREA);


		String managerEmail = null;
		String address = null;
		String additionalInfo = null;
		String managerName = null;
		String provName = null;
		int numPlaces = -1;
		String telephone = null;
		_errorMsg = null;
		schoolTypeIDs = new int[providerTypesString.length];
		for (int i = 0; i < schoolTypeIDs.length; i++)
		{
			schoolTypeIDs[i]=Integer.parseInt(providerTypesString[i]);
		}
		try{
			schoolAreaID = Integer.parseInt(schoolAreaString);
		}
		catch(NumberFormatException nme){}

		if (provNameString == null || provNameString.equals("")) {
			_isProvNameError = true;
			_isError = true;
			addErrorString(localize(ERROR_PROV_NAME, "Provider name invalid"));
		}
		if (!getValidator().isEmail(emailString)) {
			_isEmailError = true;
			_isError = true;
			addErrorString(localize(ERROR_NOT_EMAIL, "Email invalid"));
		}
		if (!getValidator().isNumber(phoneString)) {
		//if (phoneString == null || phoneString.equals("")) {
			_isPhoneError = true;
			_isError = true;
			addErrorString(localize(ERROR_PHONE, "Phone invalid"));
		}
		if (addressString == null || addressString.equals("")) {
			//if(addressString.equals("")||addressString.indexOf(" ")==-1){
				_isAddressError = true;
				_isError = true;
				addErrorString(localize(ERROR_ADDR, "Address invalid"));
			//}
		}
		if (manNameString == null || manNameString.equals("")) {
			_isManNameError = true;
			_isError = true;
			addErrorString(localize(ERROR_MAN_NAME, "Manager name invalid"));
		}
		if (!getValidator().isInt(numPlacesString)) {
			_isNumPlacesError = true;
			_isError = true;
			addErrorString(
				localize(ERROR_NUM_PLACES, "Number of places invalid"));
		}
		if(providerTypesString==null){
				_isProvTypeError=true;
				_isError = true;
				addErrorString(localize(ERROR_NO_PROV_TYPE, "Please supply a provider type"));
		}
		if(!getValidator().isStringValid(schoolAreaString)){
				_isSchoolAreaError=true;
				_isError = true;
				addErrorString(localize(ERROR_NO_SCHOOL_AREA, "Please supply a school area"));
		}
		if(!getValidator().isStringValid(postalCodeString)){
				_isPostalCodeError=true;
				_isError = true;
				addErrorString(localize(ERROR_NO_POSTAL_CODE, "Please supply a postal code"));
		}
		if (_isError) {
			viewForm(iwc);
			return;
		}
		boolean insert = false;
		try {
			managerEmail = emailString;
			address = addressString;
			managerName = manNameString;
			provName = provNameString;
			telephone = phoneString;
			if (addInfoString != null) {
				additionalInfo = addInfoString;
			}
			postalCodeID = Integer.parseInt(postalCodeString);
			numPlaces = Integer.parseInt(numPlacesString);
			//CitizenAccountBusiness business = (CitizenAccountBusiness)IBOLookup.getServiceInstance(iwc,CitizenAccountBusiness.class);
			ProviderAccountBusiness business = this.getBusiness(iwc);
			//insert = business.insertApplication(business.getUser(pidString),pidString,emailString,phoneHomeString,phoneWorkString);
			business.createApplication(
				provName,
				address,
				telephone,
				numPlaces,
				managerName,
				managerEmail,
				additionalInfo,
				postalCodeID,
				schoolTypeIDs,
				schoolAreaID);
			insert = true;
		} catch (Exception e) {
			e.printStackTrace();
			insert = false;
		}
		if (!insert) {
			_isError = true;
			addErrorString(
				localize(ERROR_NO_INSERT, "Unable to insert application"));
			viewForm(iwc);
			return;
		}
		if (getResponsePage() != null)
			iwc.forwardToIBPage(getParentPage(), getResponsePage());
		else
			add(
				new Text(
					localize(
						TEXT_APPLICATION_SUBMITTED,
						"Application submitted")));
	}

	public void viewSubmission(){
		if(bSubmissionSuccessful){

		}
		else{

		}
	}

	protected void add(PresentationObject obj,int xpos,int ypos){
		inputTable.add(obj,xpos,ypos);
	}

	private void addErrorString(String errorString) {
		if (_errorMsg == null)
			_errorMsg = new Vector();
		_errorMsg.add(errorString);
	}
	/**
	 * This method can be overrided to add new actions
	 */
	protected int parseAction(IWContext iwc) {
		int action = ACTION_VIEW_FORM;
		if (iwc.isParameterSet(PARAM_FORM_SUBMIT)) {
			action = ACTION_SUBMIT_FORM;
		}
		return action;
	}
	protected ProviderAccountBusiness getBusiness(IWApplicationContext iwc)
		throws RemoteException {
		return (ProviderAccountBusiness) IBOLookup.getServiceInstance(
			iwc,
			ProviderAccountBusiness.class);
	}
	protected Validator getValidator() {
		return Validator.getInstance();
	}
	/**
	 * Returns the addInfoString.
	 * @return String
	 */
	public String getAdditionalInfo() {
		return addInfoString;
	}
	/**
	 * Returns the addressString.
	 * @return String
	 */
	public String getAddress() {
		return addressString;
	}
	/**
	 * Returns the emailString.
	 * @return String
	 */
	public String getEmail() {
		return emailString;
	}
	/**
	 * Returns the manNameString.
	 * @return String
	 */
	public String getManagerName() {
		return manNameString;
	}
	/**
	 * Returns the numPlacesString.
	 * @return String
	 */
	public int getNumPlaces() {
		try {
			return Integer.parseInt(numPlacesString);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	/**
	 * Returns the phoneString.
	 * @return String
	 */
	public String getPhone() {
		return phoneString;
	}
	/**
	 * Returns the provNameString.
	 * @return String
	 */
	public String getProviderName() {
		return provNameString;
	}
	/**
	 * Sets the addInfoString.
	 * @param addInfoString The addInfoString to set
	 */
	public void setAdditionalInfo(String addInfoString) {
		this.addInfoString = addInfoString;
	}
	/**
	 * Sets the addressString.
	 * @param addressString The addressString to set
	 */
	public void setAddress(String addressString) {
		this.addressString = addressString;
	}
	/**
	 * Sets the emailString.
	 * @param emailString The emailString to set
	 */
	public void setEmail(String emailString) {
		this.emailString = emailString;
	}
	/**
	 * Sets the manNameString.
	 * @param manNameString The manNameString to set
	 */
	public void setManagerName(String manNameString) {
		this.manNameString = manNameString;
	}
	/**
	 * Sets the numPlacesString.
	 * @param numPlacesString The numPlacesString to set
	 * @throws NumberFormatException if numPlacesString is not an integer
	 */
	public void setNumberofPlaces(String numPlacesString) {
		Integer.parseInt(numPlacesString);
		this.numPlacesString = numPlacesString;
	}
	/**
	 * Sets the numPlacesString.
	 * @param numPlacesString The numPlacesString to set
	 */
	public void setNumberOfPlaces(int numPlaces) {
		this.numPlacesString = Integer.toString(numPlaces);
	}
	/**
	 * Sets the phoneString.
	 * @param phoneString The phoneString to set
	 */
	public void setPhone(String phoneString) {
		this.phoneString = phoneString;
	}
	/**
	 * Sets the provNameString.
	 * @param provNameString The provNameString to set
	 */
	public void setProviderName(String provNameString) {
		this.provNameString = provNameString;
	}

	/**
	 * Sets the School Area.
	 * @param schAreaID The ID of the School Area to set
	 */
	public void setSchoolArea(int schAreaID) {
		this.schoolAreaID=schAreaID;
	}

	/**
	 * Sets the School Area.
	 * @param schAreaID The ID of the School Area to set
	 */
	public void setPostalCode(int postalCodeID) {
		this.postalCodeID=postalCodeID;
	}

	/**
	 * Sets the School Types.
	 * @param schTypesIDs The IDs of the SchoolTypes to set
	 */
	public void setSchoolTypes(int[] schTypesIDs) {
		this.schoolTypeIDs=schTypesIDs;
	}

	protected void setApplicationID(Integer applicationID) {
		setApplicationID(applicationID.intValue());
	}
	protected void setApplicationID(int applicationID) {
		this.applicationID = applicationID;
	}
	protected void addButton(PresentationObject po) {
		inputTable.add(po, mainTableColumns, mainTableRows);
	}

	protected DropdownMenu getPostalCodesMenu(IWContext iwc) {
		DropdownMenu drop = new DropdownMenu(PARAM_POSTAL_CODE);
		drop.keepStatusOnAction();
		drop.addMenuElement("",localize("paa_choose_pst_code","Choose postal code:"));
		Collection postalCodes = getAvailablePostalCodes(iwc);
		if (postalCodes != null) {
			try {
				Iterator iter = postalCodes.iterator();
				while (iter.hasNext()) {
					PostalCode pCode = (PostalCode) iter.next();
					int pCodeID = ((Integer) pCode.getPrimaryKey()).intValue();
					String pCodeName = pCode.getPostalAddress();
					drop.addMenuElement(pCodeID, pCodeName);
				}
			} catch (Exception e) {
				System.err.println("ProviderAccountApplication: Error getting postal codes:");
				e.printStackTrace();
			}
		}
		if(this.postalCodeID!=-1){
			drop.setSelectedElement(postalCodeID);
		}
		return drop;
	}

	protected Collection getAvailablePostalCodes(IWApplicationContext iwc) {
		try{
			return this.getBusiness(iwc).getAvailablePostalCodes();
		}
		catch(RemoteException e){
			return ListUtil.getEmptyList();
		}
	}

	protected Collection getAvailableSchoolTypes(IWApplicationContext iwc) {
		try{
			return this.getBusiness(iwc).getAvailableSchoolTypes();
		}
		catch(RemoteException e){
			return ListUtil.getEmptyList();
		}
	}

	protected Collection getAvailableSchoolAreas(IWApplicationContext iwc, SchoolCategory schoolCategory) {
		try{
			return this.getBusiness(iwc).getAvailableSchoolAreas(schoolCategory);
		}
		catch(RemoteException e){
			return ListUtil.getEmptyList();
		}
	}


	protected PresentationObjectContainer getSchoolTypesCheckboxes(IWContext iwc) {
		Table table = new Table(2,2);
		table.setResizable(true);

		Collection postalCodes = getAvailableSchoolTypes(iwc);
		if (postalCodes != null) {
			try {
				Iterator iter = postalCodes.iterator();
				int ypos=1;
				while (iter.hasNext()) {
					SchoolType schType = (SchoolType) iter.next();
					int schTypeID = ((Integer) schType.getPrimaryKey()).intValue();
					String nameSchType = schType.getName();
					CheckBox box = new CheckBox(PARAM_PROV_TYPE,Integer.toString(schTypeID));
					//System.out.println("ProvierAccountApplication: getSchoolTypesCheckboxes() : Testing school_type_id=\""+schTypeID+"\"");
					if(isValueInArray(schTypeID,schoolTypeIDs)){
						box.setChecked(true);
					}
					table.add(box,1,ypos);
					table.add(getText(nameSchType),2,ypos++);
				}
			} catch (Exception e) {
				System.err.println("ProviderAccountApplication: Error getting school types:");
				e.printStackTrace();
			}
		}
		return table;
	}

	private boolean isValueInArray(int value,int[] array){
		if(array!=null){
			//System.out.println("ProvierAccountApplication: isValueInArray() : array != null");
			//System.out.println("ProvierAccountApplication: isValueInArray() : array.length="+array.length);
			for (int i = 0; i < array.length; i++) {
				int integ = array[i];
				//System.out.println("ProvierAccountApplication: isValueInArray() : Testing school_type_id=\""+integ+"\" for value=\""+value+"\"");
				if(integ==value){
					return true;
				}
			}
		}
		else{
			System.out.println("ProvierAccountApplication: isValueInArray() : array == null");
		}
		return false;
	}



	protected SelectionBox getSchoolTypesBox(IWContext iwc) {
		SelectionBox drop = new SelectionBox(PARAM_PROV_TYPE);
		drop.keepStatusOnAction();
		//drop.addMenuElement("",localize("paa_choose_sch_type","Choose school type:"));
		Collection postalCodes = getAvailableSchoolTypes(iwc);
		if (postalCodes != null) {
			try {
				Iterator iter = postalCodes.iterator();
				while (iter.hasNext()) {
					SchoolType schType = (SchoolType) iter.next();
					int schTypeID = ((Integer) schType.getPrimaryKey()).intValue();
					String nameSchType = schType.getName();
					drop.addMenuElement(schTypeID, nameSchType);
				}
			} catch (Exception e) {
				System.err.println("ProviderAccountApplication: Error getting school types:");
				e.printStackTrace();
			}
		}
		if(this.schoolTypeIDs!=null){
			drop.setSelectedElements(schoolTypeIDs);
		}
		return drop;
	}

	protected DropdownMenu getSchoolTypesMenu(IWContext iwc) {
		DropdownMenu drop = new DropdownMenu(PARAM_PROV_TYPE);
		drop.addMenuElement("",localize("paa_choose_sch_type","Choose school type:"));
		Collection postalCodes = getAvailableSchoolTypes(iwc);
		if (postalCodes != null) {
			try {
				Iterator iter = postalCodes.iterator();
				while (iter.hasNext()) {
					SchoolType schType = (SchoolType) iter.next();
					int schTypeID = ((Integer) schType.getPrimaryKey()).intValue();
					String nameSchType = schType.getName();
					drop.addMenuElement(schTypeID, nameSchType);
				}
			} catch (Exception e) {
				System.err.println("ProviderAccountApplication: Error getting school types:");
				e.printStackTrace();
			}
		}
		return drop;
	}

	protected DropdownMenu getSchoolAreasMenu(IWContext iwc) {
		DropdownMenu drop = new DropdownMenu(PARAM_SCH_AREA);
		drop.keepStatusOnAction();
		drop.addMenuElement("",localize("paa_choose_sch_area","Choose school area:"));
		SchoolCategory schoolCategory = null;	//	FIXME: resolve the category
		Collection postalCodes = getAvailableSchoolAreas(iwc, schoolCategory);
		if (postalCodes != null) {
			try {
				Iterator iter = postalCodes.iterator();
				while (iter.hasNext()) {
					SchoolArea schArea = (SchoolArea) iter.next();
					int schAreaID = ((Integer) schArea.getPrimaryKey()).intValue();
					String schAreaName = schArea.getName();
					drop.addMenuElement(schAreaID, schAreaName);
				}
			} catch (Exception e) {
				System.err.println("ProviderAccountApplication: Error getting school areas:");
				e.printStackTrace();
			}
		}
		if(this.schoolAreaID!=-1){
			drop.setSelectedElement(this.schoolAreaID);
		}
		return drop;
	}

}
