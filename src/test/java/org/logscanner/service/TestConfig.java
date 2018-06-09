package org.logscanner.service;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig extends DefaultBatchConfigurer
{
//    @Override
//    public void setDataSource(DataSource dataSource) {
//        //This BatchConfigurer ignores any DataSource
//    }
}
