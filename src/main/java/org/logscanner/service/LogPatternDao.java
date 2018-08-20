package org.logscanner.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.logscanner.data.LogPattern;
import org.logscanner.exception.BusinessException;
import org.logscanner.util.LocationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DaoSupport;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

/**
 * @author Victor Kadachigov
 */
@Service
public class LogPatternDao extends DaoSupport
{
	@Autowired
	private AppProperties props;
	@Autowired
	private ObjectMapper mapper;
	
	private List<LogPattern> list = Collections.emptyList();

	@Override
	protected void checkDaoConfig() throws IllegalArgumentException 
	{
	}
	
	@Override
	protected void initDao() throws Exception 
	{
		ObjectReader reader = mapper.reader();
		List<LogPattern> l = reader.readValue(
						reader.getFactory().createParser(props.getPatternsFile().toFile()), 
						mapper.getTypeFactory().constructCollectionType(List.class, LogPattern.class)
				);
		LocationHelper.checkCode(l);
		list = l;
	}
	
	private void checkCode(List<LogPattern> l) throws BusinessException 
	{
		Set<LogPattern> set = new HashSet<>();
		for (LogPattern lp : l)
		{
			if (StringUtils.isBlank(lp.getCode()))
				throw new BusinessException("Empty pattern code");
			if (!set.add(lp))
				throw new BusinessException("Pattern code '" + lp.getCode() + "' already used");
		}
	}

	public LogPattern getByCode(String code)
	{
		Optional<LogPattern> result = list.stream()
					.filter(p -> Objects.equals(p.getCode(), code))
					.findFirst();
		return result.isPresent() ? result.get() : null;
	}
	
	public List<LogPattern> getAll()
	{
		return list;
	}
}
