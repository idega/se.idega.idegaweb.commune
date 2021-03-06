/*
 * $Id: DocumentBusinessHome.java 1.1 7.10.2004 aron Exp $
 * Created on 7.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package se.idega.idegaweb.commune.printing.business;




import com.idega.business.IBOHome;

/**
 * 
 *  Last modified: $Date: 7.10.2004 00:06:46 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public interface DocumentBusinessHome extends IBOHome {
    public DocumentBusiness create() throws javax.ejb.CreateException,
            java.rmi.RemoteException;

}
