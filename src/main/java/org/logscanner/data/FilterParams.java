package org.logscanner.data;

import java.util.Date;

/**
 * @author Victor Kadachigov
 */
public class FilterParams
{
	private String[] includes;
	private Date dateFrom;
	private Date dateTo;
	
	public String[] getIncludes()
	{
		return includes;
	}
	public void setIncludes(String[] includes)
	{
		this.includes = includes;
	}
	public Date getDateFrom()
	{
		return dateFrom;
	}
	public void setDateFrom(Date dateFrom)
	{
		this.dateFrom = dateFrom;
	}
	public Date getDateTo()
	{
		return dateTo;
	}
	public void setDateTo(Date dateTo)
	{
		this.dateTo = dateTo;
	}
}
