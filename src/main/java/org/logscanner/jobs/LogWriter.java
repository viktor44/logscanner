package org.logscanner.jobs;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

/**
 * @author Victor Kadachigov
 */
public class LogWriter implements ItemWriter<Object>
{
	private static Logger log = LoggerFactory.getLogger(LogWriter.class);

	@Override
	public void write(List<? extends Object> items) throws Exception 
	{
//		if (items.size() > 0)
//			log.info("{} write: {}", hashCode(), items.get(0).getFiles().get(0).getAbsolutePath());
//		for (int i = 0; i < items.size(); i++)
//			log.info("{} write {}: {}", hashCode(), i, items.get(i));
//		items.forEach(item -> {
//			for (File f : item.getFiles())
//				log.info(f.getAbsolutePath());
//		});
	}
}
