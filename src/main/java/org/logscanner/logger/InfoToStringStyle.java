package org.logscanner.logger;

import org.apache.commons.lang3.builder.RecursiveToStringStyle;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Victor Kadachigov
 */
public class InfoToStringStyle extends RecursiveToStringStyle {

   private static final long serialVersionUID = 1L;

	@Override
    protected void appendDetail(StringBuffer buffer, String fieldName, Collection<?> coll) {
    	setUseShortClassName(true);
        if (coll.size() > 7) {
            buffer.append(coll.getClass().getSimpleName())
            			.append("[size=").append(coll.size()).append("]");
            return;
        }
        super.appendDetail(buffer, fieldName, coll);
    }
}
