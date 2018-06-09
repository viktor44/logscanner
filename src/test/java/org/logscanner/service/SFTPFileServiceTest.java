package org.logscanner.service;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.logscanner.ConsoleConfig;
import org.logscanner.data.FileInfo;
import org.logscanner.data.FilterParams;
import org.logscanner.data.Location;
import org.logscanner.data.LocationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={TestConfig.class, ConsoleConfig.class})
public class SFTPFileServiceTest
{
	private static final Logger log = LoggerFactory.getLogger(SFTPFileServiceTest.class);

	@Autowired
	private SFTPFileService service;
	
	@Test
	public void test1() throws Exception
	{
//		Location location = new Location("gera3", "gera3", "/gridgain/sas/instances/wildfly-10.1.0_5/standalone", "");
//		location.setHost("10.116.214.3");
//		location.setUser("hyperic");
//		location.setPassword("hyperic");

		Location location = new Location("local", "/Users/victor/tmp/Logs_Poletaev_2", "");
		location.setType(LocationType.SFTP);
		location.setHost("127.0.0.1");
		location.setUser("victor");
		location.setPassword("1qaz2wsx");
		
		FilterParams filterParams = new FilterParams();
		List<FileInfo> list = service.listFiles(location, filterParams);
		for (FileInfo f : list)
		{
			log.info("{}", f);
		}
		
	}

}
