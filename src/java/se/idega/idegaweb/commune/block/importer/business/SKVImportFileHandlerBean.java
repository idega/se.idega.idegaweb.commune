package se.idega.idegaweb.commune.block.importer.business;

import is.idega.block.family.business.FamilyLogic;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import se.idega.idegaweb.commune.block.importer.business.SKVEntryHolder.SKVRelativeEntryHolder;
import se.idega.idegaweb.commune.block.importer.data.SKVUserCivilStatus;
import se.idega.idegaweb.commune.block.importer.data.SKVUserCivilStatusHome;
import se.idega.idegaweb.commune.block.importer.data.SKVUserExtraInfo;
import se.idega.idegaweb.commune.block.importer.data.SKVUserExtraInfoHome;
import se.idega.idegaweb.commune.business.CommuneUserBusiness;

import com.idega.block.importer.business.ImportFileHandler;
import com.idega.block.importer.data.ImportFile;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.business.IBOServiceBean;
import com.idega.core.location.business.AddressBusiness;
import com.idega.core.location.business.CommuneBusiness;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.AddressCoordinate;
import com.idega.core.location.data.AddressCoordinateHome;
import com.idega.core.location.data.AddressHome;
import com.idega.core.location.data.AddressType;
import com.idega.core.location.data.AddressTypeHome;
import com.idega.core.location.data.Commune;
import com.idega.core.location.data.Country;
import com.idega.core.location.data.CountryHome;
import com.idega.core.location.data.PostalCode;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDORemoveRelationshipException;
import com.idega.presentation.IWContext;
import com.idega.user.data.Gender;
import com.idega.user.data.GenderHome;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.Timer;

/**
 * SKVImportFileHandlerBean
 *
 * Last modified: $Date: 2006/12/14 12:09:32 $ by $Author: palli $
 *
 * @author <a href="mailto:palli@idega.com">palli</a>
 * @version $Revision: 1.1.2.10 $
 */
public class SKVImportFileHandlerBean extends IBOServiceBean implements
		SKVImportFileHandler, ImportFileHandler {

	private static Gender MALE = null;

	private static Gender FEMALE = null;

	private ImportFile iFile = null;

	private User performer = null;

	private List failedRecords = null;

	private Collection TFlist = null;

	private Map coordinateMap = null;

	public SKVImportFileHandlerBean() {
	}

	public static void main(String args[]) {
		/*
		 * File file = new File("/Users/bluebottle/kiw0182.stoffe6.txt");
		 * SKVImportFileHandlerBean bean = new SKVImportFileHandlerBean();
		 * ImportFile iFile = new SKVImportFile(file); String item; while
		 * (!(item = (String) iFile.getNextRecord()).equals("")) { try {
		 * bean.processRecord(item); } catch (IOException e) {
		 * e.printStackTrace(); } }
		 */

		for (int i = 0; i < 10000; i++) {
			System.out.println((int) Math.floor(Math.random() * 90.0d + 10.0d));
		}
	}

	@Override
	public synchronized boolean handleRecords() {
		failedRecords = new ArrayList();
		try {
			performer = IWContext.getInstance().getCurrentUser();
		} catch (Exception ex) {
			performer = null;
		}
		Timer clock = new Timer();
		clock.start();
		try {
			// Initialize the default/home commune
			// homeCommune = getCommuneHome().findDefaultCommune();
			// HOME_COMMUNE_CODE = homeCommune.getCommuneCode();
			// initialize business beans and data homes
			TFlist = new Vector();
			Collection row1 = new Vector();
			row1.add("Personal ID changes and secret persons");
			TFlist.add(row1);

			log("SKVImportFileHandler [STARTING] time: "
					+ IWTimestamp.getTimestampRightNow().toString());

			// iterate through the records and process them
			String item;
			int count = 0;
			while (!(item = (String) iFile.getNextRecord()).equals("")) {
				count++;
				if (!processRecord(item)) {
					failedRecords.add(item);
				}
				if ((count % 250) == 0) {
					log("SKVImportFileHandler processing RECORD [" + count
							+ "] time: "
							+ IWTimestamp.getTimestampRightNow().toString());
				}
				item = null;
			}
			clock.stop();
			log("Time to handleRecords: " + clock.getTime() + " ms  OR "
					+ ((int) (clock.getTime() / 1000)) + " s.");

			/*
			 * if (TFlist.size() > 1) { // Header is line 1... if nothing
			 * else... then nothing to report
			 * getImportBusiness().addExcelReport(file.getFile(), "report",
			 * TFlist, "\n"); }
			 */
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	private boolean processRecord(String record) throws IOException {

		LineNumberReader lnr = new LineNumberReader(new StringReader(record));
		String line = null;
		SKVEntryHolder entryHolder = new SKVEntryHolder();
		SKVEntryHolder.SKVRelativeEntryHolder relativeEntryHolder = null;
		String relPin = null;
		String relAltPin = null;
		String relType = null;
		String relFirstName = null;
		String relMiddleName = null;
		String relLastName = null;
		String relDeactivationCode = null;
		String relDeactivationDate = null;

		while ((line = lnr.readLine()) != null) {
			if (line.length() > 4) {
				if (line.substring(0, 4).equals(SKVConstants.ENTRY_START)) {
					String actionString = line.substring(4, 9);

					if (actionString
							.equals(SKVConstants.COLUMN_RELATIONAL_SECTION_STARTS)) {
						relativeEntryHolder = entryHolder
								.getNewRelativeEntryHolder();
						relPin = null;
						relAltPin = null;
						relType = null;
						relFirstName = null;
						relMiddleName = null;
						relLastName = null;
						relDeactivationDate = null;
						relDeactivationCode = null;
					} else if (actionString
							.equals(SKVConstants.COLUMN_RELATIONAL_SECTION_ENDS)) {
						relativeEntryHolder
								.setRelativeAlternativePin(relAltPin);
						relativeEntryHolder.setRelativeFirstName(relFirstName);
						relativeEntryHolder.setRelativeLastName(relLastName);
						relativeEntryHolder
								.setRelativeMiddleName(relMiddleName);
						relativeEntryHolder.setRelativePin(relPin);
						relativeEntryHolder.setRelativeType(relType);
						relativeEntryHolder
								.setRelativeDeactivationCode(relDeactivationCode);
						relativeEntryHolder
								.setRelativeDeactivationDate(relDeactivationDate);

						entryHolder.addRelative(relativeEntryHolder);
					} else if (actionString
							.equals(SKVConstants.COLUMN_RELATIVE_PIN)
							|| actionString
									.equals(SKVConstants.COLUMN_RELATIVE_ALTERNATIVE_PIN)
							|| actionString
									.equals(SKVConstants.COLUMN_RELATIVE_FIRST_NAME)
							|| actionString
									.equals(SKVConstants.COLUMN_RELATIVE_LAST_NAME)
							|| actionString
									.equals(SKVConstants.COLUMN_RELATIVE_MIDDLE_NAME)
							|| actionString
									.equals(SKVConstants.COLUMN_RELATIVE_TYPE)
							|| actionString
									.equals(SKVConstants.COLUMN_RELATIVE_DEACTIVATION_CODE)
							|| actionString
									.equals(SKVConstants.COLUMN_RELATIVE_DEACTIVATION_DATE)) {
						String value = line.substring(10);
						if (actionString
								.equals(SKVConstants.COLUMN_RELATIVE_PIN)) {
							relPin = value;
						} else if (actionString
								.equals(SKVConstants.COLUMN_RELATIVE_ALTERNATIVE_PIN)) {
							relAltPin = value;
						} else if (actionString
								.equals(SKVConstants.COLUMN_RELATIVE_FIRST_NAME)) {
							relFirstName = value;
						} else if (actionString
								.equals(SKVConstants.COLUMN_RELATIVE_LAST_NAME)) {
							relLastName = value;
						} else if (actionString
								.equals(SKVConstants.COLUMN_RELATIVE_MIDDLE_NAME)) {
							relMiddleName = value;
						} else if (actionString
								.equals(SKVConstants.COLUMN_RELATIVE_TYPE)) {
							relType = value;
						} else if (actionString
								.equals(SKVConstants.COLUMN_RELATIVE_DEACTIVATION_CODE)) {
							relDeactivationCode = value;
						} else if (actionString
								.equals(SKVConstants.COLUMN_RELATIVE_DEACTIVATION_DATE)) {
							relDeactivationDate = value;
						}
					} else if (!actionString
							.equals(SKVConstants.COLUMN_CITIZEN_INFO_SECTION_STARTS)
							&& !actionString
									.equals(SKVConstants.COLUMN_CITIZEN_INFO_SECTION_ENDS)) {
						if (line.length() > 9) {
							String value = line.substring(10);
							entryHolder.setAttribute(actionString, value);
						}
					}
				}
			}
		}

		if (!entryHolder.isEmpty()) {
			// System.out.println("entry = " + entryHolder.toString());
			processEntry(entryHolder);
		} else {
			System.out.println("Entry is empty");
		}

		return true;
	}

	private boolean processEntry(SKVEntryHolder entry) {
		boolean movingFromCommune = false;
		boolean movingFromCountry = false;
		boolean isDisabled = false;
		boolean deceased = false;
		boolean pinChanged = false;
		boolean newPerson = false;

		User user = null;

		String deactivationCode = entry.getDeactivationCode();
		if (deactivationCode != null) {
			if (deactivationCode.equals(SKVConstants.DEACTIVATION_CODE_DEATH)) {
				deceased = true;
			} else if (deactivationCode
					.equals(SKVConstants.DEACTIVATION_CODE_OLD_PIN)) {
				pinChanged = true;
			} else if (deactivationCode
					.equals(SKVConstants.DEACTIVATION_CODE_EMIGRATED)) {
				movingFromCountry = true;
				movingFromCommune = true;
			} else if (deactivationCode
					.equals(SKVConstants.DEACTIVATION_CODE_OTHER)
					|| deactivationCode
							.equals(SKVConstants.DEACTIVATION_CODE_OTHER2)
					|| deactivationCode
							.equals(SKVConstants.DEACTIVATION_CODE_OTHER3)
					|| deactivationCode
							.equals(SKVConstants.DEACTIVATION_CODE_OTHER4)) {
				movingFromCommune = true;
				isDisabled = true;
			}
		}

		if (!pinChanged) {
			if (entry.getReferencePin() != null
					&& !entry.getReferencePin().equals("")) {
				pinChanged = true;
			}
		}

		try {
			if (pinChanged) {
				user = handlePinChanged(entry);
			} else {
				user = getCommuneUserBusiness().getUser(entry.getPin());
			}
		} catch (RemoteException e) {
			e.printStackTrace();

			return false;
		} catch (FinderException e) {
			newPerson = true;
		}

		String secrecyCode = entry.getSecrecy();
		if (secrecyCode != null && "J".equals(secrecyCode)) {
			Collection coll = new Vector();
			coll.add(entry.getPin() + "\t(secret person)");
			TFlist.add(coll);

			if (newPerson) {
				return true;
			}

			handleCitizenGroup(user, movingFromCommune, entry);

			return handleSecretPerson(user, entry);
		}

		if (deceased) {
			if (newPerson) {
				return true;
			}

			return handleDeceased(user, entry.getDeactivationDate());
		}

		if (isDisabled) {
			if (newPerson) {
				return true;
			}

			handleCitizenGroup(user, movingFromCommune, entry);

			return handleDisabled(user);
		}

		Gender gender = getGenderFromPin(entry.getPin());
		IWTimestamp dateOfBirth = getBirthDateFromPin(entry.getPin());

		try {
			user = getCommuneUserBusiness().createOrUpdateCitizenByPersonalID(
					entry.getFirstName(), entry.getFirstPartOfLastName(),
					entry.getLastName(), entry.getPin(), gender, dateOfBirth);
			user = getImportBusiness().handleNames(user, entry.getFirstName(),
					entry.getFirstPartOfLastName(), entry.getLastName(),
					entry.getPreferredFirstNameIndex(), true);
			if (entry.getDisplayName() != null
					&& !"".equals(entry.getDisplayName().trim())) {
				user.setDisplayName(entry.getDisplayName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		if (!movingFromCommune) {
			movingFromCommune = !isHomeCommune(entry);
		}

		handleCitizenGroup(user, movingFromCommune, entry);

		if (!handleAddress(user, entry, movingFromCountry)) {
			return false;
		}

		if (!handleRelations(user, entry)) {
			return false;
		}

		if (!handleExtraInfo(user, entry)) {
			return false;
		}

		return true;
	}

	private User handlePinChanged(SKVEntryHolder entry) throws RemoteException,
			FinderException {
		User emptyUser = getEmptyNewUser(entry.getPin());
		boolean returnEmptyUser = false;

		if (emptyUser != null) {
			IWTimestamp creationDate = new IWTimestamp(emptyUser.getCreated());
			creationDate.setAsDate();
			IWTimestamp now = IWTimestamp.RightNow();
			now.setAsDate();
			if (now.equals(creationDate)) {
				try {
					getCommuneUserBusiness().getUser(entry.getReferencePin());
					deleteEmptyUser(emptyUser);
				} catch (FinderException e) {
					returnEmptyUser = true;
				}
			} else {
				returnEmptyUser = true;
				try {
					User newUser = getCommuneUserBusiness().getUser(entry.getReferencePin());
					deleteEmptyUser(newUser);
				} catch (FinderException e) {
				}
			}
		}

		User user = null;
		if (returnEmptyUser) {
			user = emptyUser;
		} else {
			user = getCommuneUserBusiness().getUser(entry.getReferencePin());

		}
		user.setPersonalID(entry.getPin());
		user.store();

		return user;
	}

	private void deleteEmptyUser(User user) {
		try {
			getFamilyLogic().removeAllFamilyRelationsForUser(user);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		user.setPersonalID(user.getPersonalID().substring(0, 8) + "REPL");
		user.setDeleted(true);
		user.setDeletedBy(((Integer) performer.getPrimaryKey()).intValue());
		user.setDeletedWhen(IWTimestamp.getTimestampRightNow());
		user.store();
	}

	private User getEmptyNewUser(String pin) {
		User user = null;
		try {
			user = getCommuneUserBusiness().getUser(pin);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (FinderException e) {
		}

		return user;
	}

	private void handleCitizenGroup(User user, boolean movingFromCommune,
			SKVEntryHolder entry) {
		try {
			if (movingFromCommune) {
				if (entry != null && entry.getDeactivationDate() != null) {
					getCommuneUserBusiness().moveCitizenFromCommune(
							user,
							getDateFromString(entry.getDeactivationDate())
									.getTimestamp(), performer);
				} else {
					getCommuneUserBusiness().moveCitizenFromCommune(user,
							IWTimestamp.getTimestampRightNow(), performer);
				}
			} else {
				if (entry != null && entry.getDeactivationDate() != null) {
					getCommuneUserBusiness().moveCitizenToCommune(
							user,
							getDateFromString(entry.getDeactivationDate())
									.getTimestamp(), performer);
				} else {
					getCommuneUserBusiness().moveCitizenToCommune(user,
							IWTimestamp.getTimestampRightNow(), performer);
				}
			}
		} catch (Exception e) {
		}
	}

	private boolean handleExtraInfo(User user, SKVEntryHolder entry) {
		try {
			SKVUserExtraInfoHome ueih = (SKVUserExtraInfoHome) IDOLookup
					.getHome(SKVUserExtraInfo.class);
			SKVUserExtraInfo info = null;
			try {
				info = ueih.findByUser(user);
			} catch (FinderException e) {
				try {
					info = ueih.create();
				} catch (CreateException e1) {
					return false;
				}
			}

			if (entry.getBirthParish() != null) {
				info.setBirthParish(entry.getBirthParish());
			}

			if (entry.getBirthCounty() != null) {
				info.setBirthCounty(Integer.parseInt(entry.getBirthCounty()));
			}

			if (entry.getCitizenshipCode() != null) {
				info.setCitizenshipCode(entry.getCitizenshipCode());
			}

			if (entry.getCitizenshipDate() != null) {
				info.setCitizenshipDate(getDateFromString(
						entry.getCitizenshipDate()).getDate());
			}

			if (entry.getCivilStatusDate() != null) {
				info.setCivilStatusDate(getDateFromString(
						entry.getCivilStatusDate()).getDate());
			}

			if (entry.getCivilStatusCode() != null) {
				try {
					SKVUserCivilStatusHome ucsh = (SKVUserCivilStatusHome) IDOLookup
							.getHome(SKVUserCivilStatus.class);
					SKVUserCivilStatus status = ucsh.findByStatusCode(entry
							.getCivilStatusCode());
					info.setUserCivilStatus(status);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (entry.getForeignBirthCity() != null) {
				info.setForeignBirthCity(entry.getForeignBirthCity());
			}

			if (entry.getForeignBirthCountry() != null) {
				info.setForeignBirthCountry(entry.getForeignBirthCountry());
			}

			if (entry.getImmigrationDate() != null) {
				info.setImigrationDate(getDateFromString(
						entry.getImmigrationDate()).getDate());
			}

			info.setUser(user);

			info.store();
		} catch (IDOLookupException e) {
			e.printStackTrace();
		}

		return true;
	}

	private boolean handleRelations(User user, SKVEntryHolder entry) {
		try {
			getFamilyLogic().removeAllFamilyRelationsForUser(user);
			if (entry.getRelatives() != null && !entry.getRelatives().isEmpty()) {
				Iterator it = entry.getRelatives().iterator();
				while (it.hasNext()) {
					SKVRelativeEntryHolder holder = (SKVRelativeEntryHolder) it
							.next();
					User relative = null;
					String pin = holder.getRelativePin();
					if (pin == null || "".equals(pin.trim())) {
						pin = holder.getRelativeAlternativePin();
					}

					boolean isDeceased = false;
					if (holder.getRelativeDeactivationDate() != null) {
						if (holder.getRelativeDeactivationCode().equals(
								SKVConstants.DEACTIVATION_CODE_DEATH)) {
							isDeceased = true;
						}
					}

					if (pin != null && !"".equals(pin.trim())) {
						try {
							relative = getCommuneUserBusiness().getUser(pin);
						} catch (Exception e) {
						}

						if (!isDeceased) {
							if (holder.getRelativeType().equals(
									SKVConstants.RELATION_TYPE_CHILD)) {
								if (relative != null) {
									getFamilyLogic().setAsParentFor(user,
											relative);
								}
							} else if (holder.getRelativeType().equals(
									SKVConstants.RELATION_TYPE_SPOUSE)) {
								if (relative != null) {
									getFamilyLogic().setAsSpouseFor(user,
											relative);
								}
							} else if (holder.getRelativeType().equals(
									SKVConstants.RELATION_TYPE_FATHER)) {
								if (relative != null) {
									getFamilyLogic().setAsChildFor(user,
											relative);
								}
							} else if (holder.getRelativeType().equals(
									SKVConstants.RELATION_TYPE_MOTHER)) {
								if (relative != null) {
									getFamilyLogic().setAsChildFor(user,
											relative);
								}
							} else if (holder.getRelativeType().equals(
									SKVConstants.RELATION_TYPE_PARTNER)) {
								// getFamilyLogic().setAs, child);
							} else if (holder.getRelativeType().equals(
									SKVConstants.RELATION_TYPE_CUSTODIAN1)) {
								if (relative == null) {
									relative = getCommuneUserBusiness()
											.createCitizen(
													holder
															.getRelativeFirstName(),
													holder
															.getRelativeMiddleName(),
													holder
															.getRelativeLastName(),
													pin);
									handleCitizenGroup(relative, true, null);
								}
								getFamilyLogic().setAsCustodianFor(relative,
										user);
							} else if (holder.getRelativeType().equals(
									SKVConstants.RELATION_TYPE_CUSTODIAN2)) {
								if (relative == null) {
									relative = getCommuneUserBusiness()
											.createCitizen(
													holder
															.getRelativeFirstName(),
													holder
															.getRelativeMiddleName(),
													holder
															.getRelativeLastName(),
													pin);
									handleCitizenGroup(relative, true, null);
								}
								getFamilyLogic().setAsCustodianFor(user,
										relative);
							}
						} else {
							if (relative != null) {
								handleDeceased(relative, holder
										.getRelativeDeactivationDate());
							}
						}
					}
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (CreateException e) {
			e.printStackTrace();
		}

		return true;
	}

	private boolean handleSecretPerson(User user, SKVEntryHolder entry) {
		try {
			user.removeAllAddresses();
			user.removeAllEmails();
			user.removeAllPhones();

			SKVUserExtraInfoHome ueih = (SKVUserExtraInfoHome) IDOLookup
					.getHome(SKVUserExtraInfo.class);
			try {
				SKVUserExtraInfo info = ueih.findByUser(user);
				info.remove();
			} catch (FinderException e1) {
				e1.printStackTrace();
			} catch (EJBException e) {
				e.printStackTrace();
			} catch (RemoveException e) {
				e.printStackTrace();
			}

			// getFamilyLogic().removeAllFamilyRelationsForUser(user);
			List parents = user.getParentGroups();
			Iterator it = parents.iterator();
			while (it.hasNext()) {
				Group parent = (Group) it.next();
				parent.removeGroup(((Integer) user.getPrimaryKey()).intValue(),
						performer, false);
			}

			getCommuneUserBusiness().moveCitizenToProtectedCitizenGroup(user,
					IWTimestamp.getTimestampRightNow(), performer);

			// boolean updated = false;

			handleRelations(user, entry);

			/*
			 * while (!updated) { StringBuffer pinString = new
			 * StringBuffer(user.getPersonalID() .substring(0, 8));
			 * pinString.append("SP"); pinString.append((int) Math
			 * .floor(Math.random() * 90.0d + 10.0d)); try { User tmpUser =
			 * getCommuneUserBusiness().getUser( pinString.toString()); if
			 * (tmpUser == null) { user.setDescription("Secret");
			 * user.setPersonalID(pinString.toString()); user.store(); updated =
			 * true; } } catch (FinderException e) {
			 * user.setDescription("Secret");
			 * user.setPersonalID(pinString.toString()); user.store(); updated =
			 * true; } catch (RemoteException e) { e.printStackTrace();
			 *
			 * return false; }
			 *  }
			 */
			user.setDescription("Secret");
			user.setFirstName("skyddad");
			user.setMiddleName("skyddad");
			user.setLastName("skyddad");
			user.store();
		} catch (IDORemoveRelationshipException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		return true;
	}

	private boolean handleDeceased(User user, String date) {
		try {
			/*
			 * user.removeAllAddresses(); user.removeAllEmails();
			 * user.removeAllPhones();
			 *
			 * getFamilyLogic().removeAllFamilyRelationsForUser(user); List
			 * parents = user.getParentGroups(); Iterator it =
			 * parents.iterator(); while (it.hasNext()) { Group parent = (Group)
			 * it.next(); parent.removeGroup(user); }
			 */

			if (date != null) {
				getCommuneUserBusiness().setUserAsDeceased(
						(Integer) user.getPrimaryKey(),
						getDateFromString(date).getDate());
			} else {
				getCommuneUserBusiness().setUserAsDeceased(
						(Integer) user.getPrimaryKey(),
						IWTimestamp.RightNow().getDate());
			}

		} catch (RemoteException e) {
			e.printStackTrace();
		}

		return true;
	}

	private boolean handleDisabled(User user) {
		try {
			user.removeAllAddresses();
			user.removeAllEmails();
			user.removeAllPhones();

			getFamilyLogic().removeAllFamilyRelationsForUser(user);
			List parents = user.getParentGroups();
			Iterator it = parents.iterator();
			while (it.hasNext()) {
				Group parent = (Group) it.next();
				parent.removeGroup(user);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (IDORemoveRelationshipException e) {
			e.printStackTrace();
		}

		return true;
	}

	private boolean isHomeCommune(SKVEntryHolder entry) {
		String communeCode = entry.getCountyCode() + entry.getCommuneCode();
		Commune commune = null;
		try {
			commune = getCommuneBusiness().getCommuneByCode(communeCode);
			Commune defaultCommune = getCommuneBusiness().getDefaultCommune();

			if (commune == null) {
				return false;
			}

			if (defaultCommune == null) {
				return false;
			}

			if (defaultCommune.getCommuneCode() == null
					|| commune.getCommuneCode() == null) {
				return false;
			}

			if (defaultCommune.getCommuneCode()
					.equals(commune.getCommuneCode())) {
				return true;
			}
		} catch (RemoteException e1) {
			logDebug("Commune with code:" + communeCode
					+ " (countyNumber+communeNumber) not found in database");
		}

		return false;
	}

	/**
	 * @param user
	 * @param countyNumber
	 * @param commune
	 * @return
	 */
	private boolean handleAddress(User user, SKVEntryHolder entry,
			boolean movingFromCountry) {
		String communeCode = entry.getCountyCode() + entry.getCommuneCode();
		Commune commune = null;
		try {
			commune = getCommuneBusiness().getCommuneByCode(communeCode);
		} catch (RemoteException e1) {
			logDebug("Commune with code:" + communeCode
					+ " (countyNumber+communeNumber) not found in database");
		}

		// country id 187 name Sweden isoabr: SE
		Country sweden = null;
		try {
			sweden = ((CountryHome) getIDOHome(Country.class))
					.findByIsoAbbreviation("SE");
		} catch (RemoteException e1) {
			e1.printStackTrace();
		} catch (FinderException e1) {
			e1.printStackTrace();
		}

		if (!movingFromCountry) {
			StringBuffer addressLine = new StringBuffer();
			boolean addressLineHasPreviousEntry = false;
			if (entry.getCoAddress() != null
					&& !"".equals(entry.getCoAddress())) {
				addressLine.append(entry.getCoAddress());
				addressLineHasPreviousEntry = true;
			}

			if (entry.getAddress1() != null && !"".equals(entry.getAddress1())) {
				if (addressLineHasPreviousEntry) {
					addressLine.append(" ");
				} else {
					addressLineHasPreviousEntry = true;
				}
				addressLine.append(entry.getAddress1());
			}

			if (entry.getAddress2() != null && !"".equals(entry.getAddress2())) {
				if (addressLineHasPreviousEntry) {
					addressLine.append(" ");
				}

				addressLine.append(entry.getAddress2());
			}

			// main address
			if (addressLine.length() != 0) {
				try {
					String streetName = getAddressBusiness()
							.getStreetNameFromAddressString(
									addressLine.toString());
					String streetNumber = getAddressBusiness()
							.getStreetNumberFromAddressString(
									addressLine.toString());
					Address address = getCommuneUserBusiness()
							.getUsersMainAddress(user);

					log("finding address for user = " + user.getName());
					if (address == null) {
						log("address is null");
					} else {
						log("address is = "
								+ address.getPrimaryKey().toString());
					}
					if (address != null) {
						Collection addresses = user.getAddresses();
						log("count = " + (addresses == null ? 0 : addresses.size()));
						if (!ListUtil.isEmpty(addresses)) {
							try {
								user.removeAllAddresses();
							} catch (IDORemoveRelationshipException e1) {
								e1.printStackTrace();
							}
							address = null;
						}

						/*
						 * if count == 0 then something strange is happening.
						 * Should never happen...
						 */
					}

					PostalCode code = null;
					if (entry.getPostalName() != null) {
						code = getAddressBusiness()
								.getPostalCodeAndCreateIfDoesNotExist(
										entry.getPostalCode(),
										entry.getPostalName(), sweden);
					}
					boolean addAddress = false;
					if (address == null) {
						AddressHome addressHome = getAddressBusiness()
								.getAddressHome();
						address = addressHome.create();
						AddressType mainAddressType = addressHome
								.getAddressType1();
						address.setAddressType(mainAddressType);
						addAddress = true;
					}
					address.setCountry(sweden);
					if (code != null) {
						address.setPostalCode(code);
					}
					address.setProvince(entry.getCountyCode());
					if (commune != null) {
						address.setCity(commune.getCommuneName());
						address.setCommune(commune);
					}
					address.setStreetName(streetName);
					address.setStreetNumber(streetNumber);
					AddressCoordinate ac = getAddressCoordinate(entry
							.getAddressCoordinate(), commune);
					if (ac != null) {
						address.setCoordinate(ac);
					}
					//	FIXME:	address.setCoordinateDate(entry.getRegistrationDate());
					address.store();
					if (addAddress) {
						user.addAddress(address);
					}
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}

			StringBuffer extraAddressLine = new StringBuffer();
			boolean extraAddressLineHasPreviousEntry = false;
			if (entry.getExtraCoAddress() != null
					&& !"".equals(entry.getExtraCoAddress())) {
				extraAddressLine.append(entry.getExtraCoAddress());
				extraAddressLineHasPreviousEntry = true;
			}

			if (entry.getExtraAddress1() != null
					&& !"".equals(entry.getExtraAddress1())) {
				if (extraAddressLineHasPreviousEntry) {
					extraAddressLine.append(" ");
				} else {
					extraAddressLineHasPreviousEntry = true;
				}
				extraAddressLine.append(entry.getExtraAddress1());
			}

			if (entry.getExtraAddress2() != null
					&& !"".equals(entry.getExtraAddress2())) {
				if (extraAddressLineHasPreviousEntry) {
					extraAddressLine.append(" ");
				}

				extraAddressLine.append(entry.getExtraAddress2());
			}

			if (extraAddressLine.length() != 0) {
				try {
					String streetName = getAddressBusiness()
							.getStreetNameFromAddressString(
									extraAddressLine.toString());
					String streetNumber = getAddressBusiness()
							.getStreetNumberFromAddressString(
									extraAddressLine.toString());

					AddressTypeHome ath = (AddressTypeHome) IDOLookup
							.getHome(AddressType.class);
					AddressType at = null;
					try {
						at = null;	//	FIXME: ath.findByUniqueName("ic_user_address_3");
					} catch (Exception e) {
						e.printStackTrace();
						at = ath.create();
						at.setDescription("Special");
						at.setName("Special");
						at.setUniqueName("ic_user_address_3");
						at.store();
					}

					Address address = getCommuneUserBusiness()
							.getUserAddressByAddressType(
									((Integer) user.getPrimaryKey()).intValue(),
									at);

					PostalCode code = getAddressBusiness()
							.getPostalCodeAndCreateIfDoesNotExist(
									entry.getExtraPostalCode(),
									entry.getExtraPostalName(), sweden);
					boolean addAddress = false;
					if (address == null) {
						AddressHome addressHome = getAddressBusiness()
								.getAddressHome();
						address = addressHome.create();
						address.setAddressType(at);
						addAddress = true;
					}
					address.setCountry(sweden);
					address.setPostalCode(code);
					address.setProvince(entry.getCountyCode());
					if (commune != null) {
						address.setCity(commune.getCommuneName());
						address.setCommune(commune);
					}
					address.setStreetName(streetName);
					address.setStreetNumber(streetNumber);
					address.store();
					if (addAddress) {
						user.addAddress(address);
					}
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		} else {
			try {
				user.removeAllAddresses();
			} catch (IDORemoveRelationshipException e1) {
				e1.printStackTrace();
			}
			StringBuffer foreignAddressLine = new StringBuffer();
			boolean foreignAddressLineHasPreviousEntry = false;
			if (entry.getForeignAddress1() != null
					&& !"".equals(entry.getForeignAddress1())) {
				foreignAddressLine.append(entry.getForeignAddress1());
				foreignAddressLineHasPreviousEntry = true;
			}

			if (entry.getForeignAddress2() != null
					&& !"".equals(entry.getForeignAddress2())) {
				if (foreignAddressLineHasPreviousEntry) {
					foreignAddressLine.append(" ");
				} else {
					foreignAddressLineHasPreviousEntry = true;
				}
				foreignAddressLine.append(entry.getForeignAddress2());
			}

			if (entry.getForeignAddress3() != null
					&& !"".equals(entry.getForeignAddress3())) {
				if (foreignAddressLineHasPreviousEntry) {
					foreignAddressLine.append(" ");
				} else {
					foreignAddressLineHasPreviousEntry = true;
				}

				foreignAddressLine.append(entry.getForeignAddress3());
			}

			if (entry.getForeignAddressCountry() != null
					&& !"".equals(entry.getForeignAddressCountry())) {
				if (foreignAddressLineHasPreviousEntry) {
					foreignAddressLine.append(" ");
				}

				foreignAddressLine.append(entry.getForeignAddressCountry());
			}

			if (foreignAddressLine.length() != 0) {
				try {
					String streetName = foreignAddressLine.toString();
					AddressTypeHome ath = (AddressTypeHome) IDOLookup
							.getHome(AddressType.class);
					AddressType at = null;
					try {
						at = null;	//	FIXME: ath.findByUniqueName("ic_user_address_4");
					} catch (Exception e) {
						at = ath.create();
						at.setDescription("Foreign");
						at.setName("Foreign");
						at.setUniqueName("ic_user_address_4");
						at.store();
					}

					Address address = getCommuneUserBusiness()
							.getUserAddressByAddressType(
									((Integer) user.getPrimaryKey()).intValue(),
									at);

					boolean addAddress = false;
					if (address == null) {
						AddressHome addressHome = getAddressBusiness()
								.getAddressHome();
						address = addressHome.create();
						address.setAddressType(at);
						addAddress = true;
					}
					address.setStreetName(streetName);
					address.setStreetNumber("");
					address.setCommune(null);
					address.store();
					if (addAddress) {
						user.addAddress(address);
					}
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		}

		return true;
	}

	private AddressCoordinate getAddressCoordinate(String addressKeyCode,
			Commune commune) throws IDOLookupException {
		if (coordinateMap == null) {
			coordinateMap = new HashMap();
		}

		if (addressKeyCode != null) {
			if (coordinateMap.containsKey(addressKeyCode)) {
				// Can return null
				return (AddressCoordinate) coordinateMap.get(addressKeyCode);
			} else {
				AddressCoordinateHome ach = (AddressCoordinateHome) IDOLookup
						.getHome(AddressCoordinate.class);
				AddressCoordinate ac = null;
				try {
					ac = ach.findByCoordinate(addressKeyCode);
				} catch (FinderException f) {
					try {
						ac = ach.create();
						ac.setCommune(commune);
						ac.setCoordinate(addressKeyCode);
						ac.store();
					} catch (CreateException e) {
						return null;
					}
				}
				coordinateMap.put(addressKeyCode, ac);
				return ac;
			}
		}
		return null;
	}

	private Gender getGenderFromPin(String pin) {
		// pin format = 190010221208 second last number is the gender
		// even number = female
		// odd number = male
		try {
			GenderHome home = (GenderHome) this.getIDOHome(Gender.class);
			if (Integer.parseInt(pin.substring(10, 11)) % 2 == 0) {
				if (FEMALE == null) {
					FEMALE = home.getFemaleGender();
				}
				return FEMALE;
			} else {
				if (MALE == null) {
					MALE = home.getMaleGender();
				}
				return MALE;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null; // if something happened
		}
	}

	private IWTimestamp getBirthDateFromPin(String pin) {
		int day = Integer.parseInt(pin.substring(6, 8));
		int month = Integer.parseInt(pin.substring(4, 6));
		int year = Integer.parseInt(pin.substring(0, 4));
		IWTimestamp dateOfBirth = new IWTimestamp(day, month, year);
		return dateOfBirth;
	}

	@Override
	public void setImportFile(ImportFile file) {
		iFile = file;
	}

	private SKVImportBusiness getImportBusiness() {
		try {
			return (SKVImportBusiness) IBOLookup.getServiceInstance(this
					.getIWApplicationContext(), SKVImportBusiness.class);
		} catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}

	private CommuneUserBusiness getCommuneUserBusiness() {
		try {
			return (CommuneUserBusiness) getServiceInstance(CommuneUserBusiness.class);
		} catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}

	private AddressBusiness getAddressBusiness() {
		try {
			return (AddressBusiness) getServiceInstance(AddressBusiness.class);
		} catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}

	private FamilyLogic getFamilyLogic() {
		try {
			return (FamilyLogic) getServiceInstance(FamilyLogic.class);
		} catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}

	private CommuneBusiness getCommuneBusiness() {
		try {
			return (CommuneBusiness) getServiceInstance(CommuneBusiness.class);
		} catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}

	@Override
	public List getFailedRecords() throws RemoteException {
		return failedRecords;
	}

	@Override
	public void setRootGroup(Group rootGroup) throws RemoteException {
	}

	private IWTimestamp getDateFromString(String dateOfRegistrationString) {
		int year = Integer.parseInt(dateOfRegistrationString.substring(0, 4));
		int month = Integer.parseInt(dateOfRegistrationString.substring(4, 6));
		int day = Integer.parseInt(dateOfRegistrationString.substring(6, 8));
		IWTimestamp date = new IWTimestamp(day, month, year);
		return date;
	}

	@Override
	public List getSuccessRecords() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
}