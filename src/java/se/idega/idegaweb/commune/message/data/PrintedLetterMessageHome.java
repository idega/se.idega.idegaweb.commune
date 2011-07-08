/*
 * $Id: PrintedLetterMessageHome.java 1.1 Oct 12, 2005 laddi Exp $
 * Created on Oct 12, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package se.idega.idegaweb.commune.message.data;

import java.util.Collection;
import javax.ejb.FinderException;
import com.idega.block.process.message.data.MessageHome;
import com.idega.block.school.data.School;
import com.idega.data.IDOException;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;


/**
 * Last modified: $Date: 2004/06/28 09:09:50 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public interface PrintedLetterMessageHome extends MessageHome {

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbFindMessages
	 */
	public Collection findMessages(User user) throws FinderException;

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbFindMessagesByStatus
	 */
	public Collection findMessagesByStatus(User user, String[] status) throws FinderException;

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbFindAllUnPrintedLetters
	 */
	public Collection findAllUnPrintedLetters() throws FinderException;

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbFindAllPrintedLetters
	 */
	public Collection findAllPrintedLetters() throws FinderException;

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbHomeGetNumberOfUnprintedLettersByType
	 */
	public int getNumberOfUnprintedLettersByType(String letterType);

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbHomeGetNumberOfPrintedLettersByType
	 */
	public int getNumberOfPrintedLettersByType(String letterType);

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbHomeGetNumberOfLettersByStatusAndType
	 */
	public int getNumberOfLettersByStatusAndType(String caseStatus, String letterType);

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbHomeGetNumberOfUnPrintedPasswordLetters
	 */
	public int getNumberOfUnPrintedPasswordLetters();

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbHomeGetNumberOfPrintedPasswordLetters
	 */
	public int getNumberOfPrintedPasswordLetters();

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbHomeGetNumberOfUnPrintedDefaultLetters
	 */
	public int getNumberOfUnPrintedDefaultLetters();

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbHomeGetNumberOfPrintedDefaultLetters
	 */
	public int getNumberOfPrintedDefaultLetters();

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbHomeGetLetterTypes
	 */
	public String[] getLetterTypes();

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbFindPrintedLettersByType
	 */
	public Collection findPrintedLettersByType(String letterType, int resultSize, int startingIndex)
			throws FinderException;

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbFindPrintedLettersByType
	 */
	public Collection findPrintedLettersByType(String letterType, IWTimestamp from, IWTimestamp to, int resultSize,
			int startingIndex) throws FinderException;

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbFindSinglePrintedLettersByType
	 */
	public Collection findSinglePrintedLettersByType(String letterType, IWTimestamp from, IWTimestamp to, int resultSize,
			int startingIndex) throws FinderException;

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbFindByBulkFile
	 */
	public Collection findByBulkFile(int file, String letterType, String status, int resultSize, int startingIndex)
			throws FinderException;

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbFindSingleByTypeAndStatus
	 */
	public Collection findSingleByTypeAndStatus(String letterType, String status, IWTimestamp from, IWTimestamp to,
			int resultSize, int startingIndex) throws FinderException;

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbFindUnPrintedLettersByType
	 */
	public Collection findUnPrintedLettersByType(String letterType, int resultSize, int startingIndex)
			throws FinderException;

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbFindUnPrintedLettersByType
	 */
	public Collection findUnPrintedLettersByType(String letterType, IWTimestamp from, IWTimestamp to, int resultSize,
			int startingIndex) throws FinderException;

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbFindSingleUnPrintedLettersByType
	 */
	public Collection findSingleUnPrintedLettersByType(String letterType, IWTimestamp from, IWTimestamp to,
			int resultSize, int startingIndex) throws FinderException;

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbFindUnPrintedPasswordLetters
	 */
	public Collection findUnPrintedPasswordLetters(int resultSize, int startingIndex) throws FinderException;

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbFindPrintedPasswordLetters
	 */
	public Collection findPrintedPasswordLetters(int resultSize, int startingIndex) throws FinderException;

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbFindUnPrintedDefaultLetters
	 */
	public Collection findUnPrintedDefaultLetters(int resultSize, int startingIndex) throws FinderException;

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbFindPrintedDefaultLetters
	 */
	public Collection findPrintedDefaultLetters(int resultSize, int startingIndex) throws FinderException;

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbHomeGetPrintMessageTypes
	 */
	public String[] getPrintMessageTypes();

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbFindLettersByChildcare
	 */
	public Collection findLettersByChildcare(int providerID, String ssn, String msgId, IWTimestamp from, IWTimestamp to)
			throws FinderException;

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbFindLettersByAdultEducation
	 */
	public Collection findLettersByAdultEducation(School school, String ssn, String msgId, IWTimestamp from,
			IWTimestamp to) throws FinderException;

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbFindAllLettersBySchool
	 */
	public Collection findAllLettersBySchool(int providerID, String ssn, String msgId, IWTimestamp from, IWTimestamp to)
			throws FinderException;

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbFindLetters
	 */
	public Collection findLetters(String[] msgId) throws FinderException;

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbFindMessages
	 */
	public java.util.Collection findMessages(com.idega.user.data.User user, String[] status)
			throws javax.ejb.FinderException;

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbFindMessages
	 */
	public Collection findMessages(User user, String[] status, int numberOfEntries, int startingEntry)
			throws FinderException;

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbFindMessages
	 */
	public Collection findMessages(Group group, String[] status) throws FinderException;

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbFindMessages
	 */
	public Collection findMessages(Group group, String[] status, int numberOfEntries, int startingEntry)
			throws FinderException;

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbFindMessages
	 */
	public Collection findMessages(User user, Collection groups, String[] status, int numberOfEntries, int startingEntry)
			throws FinderException;

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbHomeGetNumberOfMessages
	 */
	public int getNumberOfMessages(User user, Collection groups, String[] status) throws IDOException;

	/**
	 * @see se.idega.idegaweb.commune.message.data.PrintedLetterMessageBMPBean#ejbHomeGetNumberOfMessages
	 */
	public int getNumberOfMessages(User user, String[] status) throws IDOException;
}
