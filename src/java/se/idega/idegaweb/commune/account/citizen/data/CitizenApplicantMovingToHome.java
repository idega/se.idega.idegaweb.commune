package se.idega.idegaweb.commune.account.citizen.data;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOHome;

/**
 * Last modified: $Date: 2003/10/06 12:29:53 $ by $Author: laddi $
 *
 * @author <a href="http://www.staffannoteberg.com">Staffan N�teberg</a>
 * @version $Revision: 1.5 $
 */
public interface CitizenApplicantMovingToHome extends IDOHome {
    public CitizenApplicantMovingTo create() throws CreateException;
    public CitizenApplicantMovingTo findByApplicationId (int applicantId)
        throws FinderException;
}
