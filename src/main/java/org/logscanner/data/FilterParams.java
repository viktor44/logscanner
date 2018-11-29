package org.logscanner.data;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Victor Kadachigov
 */
@Getter
@Setter
@ToString
public class FilterParams
{
	private String[] includes;
	private Date dateFrom;
	private Date dateTo;
}
