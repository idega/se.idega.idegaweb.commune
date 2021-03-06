/*
 * $Id: DocumentServiceHomeImpl.java,v 1.1 2004/11/04 20:34:48 aron Exp $
 * Created on 4.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package se.idega.idegaweb.commune.printing.business;




import com.idega.business.IBOHomeImpl;

/**
 * 
 *  Last modified: $Date: 2004/11/04 20:34:48 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public class DocumentServiceHomeImpl extends IBOHomeImpl implements
        DocumentServiceHome {
    protected Class getBeanInterfaceClass() {
        return DocumentService.class;
    }

    public DocumentService create() throws javax.ejb.CreateException {
        return (DocumentService) super.createIBO();
    }

}
