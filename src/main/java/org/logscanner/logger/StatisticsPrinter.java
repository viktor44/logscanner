package org.logscanner.logger;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.logscanner.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jamonapi.MonitorFactory;

/**
 * @author Victor Kadachigov
 */
public class StatisticsPrinter
{
	private static final Logger log = LoggerFactory.getLogger(StatisticsPrinter.class);
	
	public enum Format
	{
		TEXT,
		HTML
	}
	
	private static final String DEFAULT_EXCLUDE_FILTER[] = new String[] {
			"com.jamonapi.Exceptions"
	};
	
	private String includeFilter[];
	private String excludeFilter[];
	private Format format = Format.TEXT;
	private Map<String, String[]> totalGroups;
	
	public StatisticsPrinter setIncludeFilter(String... monitorNames)
	{
		includeFilter = monitorNames;
		return this;
	}

	public StatisticsPrinter setExcludeFilter(String... monitorNames)
	{
		excludeFilter = monitorNames;
		return this;
	}

	public StatisticsPrinter addTotalGroup(String groupName, String... monitorNames)
	{
		throw new NotImplementedException(Resources.getStr("error.not_implemented"));
		//return this;
	}
	

	public StatisticsPrinter setFormat(Format format)
	{
		this.format = format;
		return this;
	}

	private List<MonitorView> buildData()
	{
		List<MonitorView> monitorViewList = new ArrayList<>();
		Object data[][] = MonitorFactory.getData();
		long sum = 0;
		double sumTotalTime = 0;
		if (data != null) {
			int size = data.length;
			for (int i = 0; i < size; i++) {
				MonitorView monitorView = new MonitorView(data[i]);
				
				boolean skip = false;
				skip |= includeFilter != null && !StringUtils.startsWithAny(monitorView.mvLabel, includeFilter);
				skip |= excludeFilter != null && StringUtils.startsWithAny(monitorView.mvLabel, excludeFilter);
				skip |= StringUtils.startsWithAny(monitorView.mvLabel, DEFAULT_EXCLUDE_FILTER);
				if (skip) 
					continue;
				
				sum += monitorView.mvHits;
				sumTotalTime += monitorView.mvHits * monitorView.mvAvg;
				monitorViewList.add(monitorView);
			}
			for (MonitorView mv: monitorViewList) {
				mv.calcPercent(sum, sumTotalTime);
			}
		}
		
		Collections.sort(
				monitorViewList,
				new Comparator<MonitorView>() {
					@Override
					public int compare(MonitorView m1, MonitorView m2) {
						return new CompareToBuilder()
								.append(m1.mvLabel, m2.mvLabel)
								.toComparison();
					}
					
				}
		);
		
		return monitorViewList;
	}
	
	public String print()
	{
		StringWriter writer = new StringWriter();
		print(writer);
		return writer.toString();
	}

	public void print(OutputStream outputStream)
	{
		Writer writer = new OutputStreamWriter(outputStream);
		print(writer);
	}

	public void print(Writer writer)
	{
		List<MonitorView> monitorViewList = buildData();
		
		try
		{
			switch (format)
			{
				case TEXT:
					printText(monitorViewList, writer);
					break;
				case HTML:
					printHtml(monitorViewList, writer);
			}
		}
		catch (IOException ex)
		{
			//vk: не выпускаем ошибку наружу
			log.error(ex.getMessage(), ex);
		}
	}

	private void printHtml(List<MonitorView> data, Writer writer) throws IOException
	{
		writer.write("<html><body><pre>");
		printText(data, writer);
		writer.write("</pre></body></html>");
	}

	private void printText(List<MonitorView> data, Writer writer) throws IOException
	{
		DecimalFormat df = new DecimalFormat("#0.00");
		String line = "=================================================================================================================================================================================\n";
		StringBuffer sb = new StringBuffer("\n\n");
		
		sb.append(line);
		sb.append(line);
		sb.append(StringUtils.center("Label", 82))
			.append(StringUtils.center("Hits", 16))
			.append(StringUtils.center("Avg", 16))
			.append(StringUtils.center("Max", 16))
			.append(StringUtils.center("Hits %", 16))
			.append(StringUtils.center("Total Time", 16))
			.append(StringUtils.center("Total Time %", 16)).append('\n');
		
		for (MonitorView monitorView : data)
		{
			sb.append(StringUtils.rightPad(monitorView.mvLabel, 80)).append(' ').append('|') 
					.append(StringUtils.leftPad(String.valueOf(monitorView.mvHits), 14)).append(' ').append('|') 
					.append(StringUtils.leftPad(df.format(monitorView.mvAvg), 14)).append(' ').append('|') 
					.append(StringUtils.leftPad(df.format(monitorView.mvMax), 14)).append(' ').append('|') 
					.append(StringUtils.leftPad(df.format(monitorView.mvHitsP), 14)).append(' ').append('|') 
					.append(StringUtils.leftPad(df.format(monitorView.mvTotalTime), 14)).append(' ').append('|') 
					.append(StringUtils.leftPad(df.format(monitorView.mvTotalTimeP), 14)).append('\n');
		}
		sb.append(line);
		sb.append(line);
		
		writer.write(sb.toString());
	}

	private class MonitorView
	{

		double mvHitsP;
		double mvTotalTime;
		double mvTotalTimeP;
		
		String mvLabel;//0
		long mvHits;//1
		double mvAvg;//2
		//double mvMin;//6
		double mvMax;//7
		//double mvMaxActive;//10
		
		MonitorView(Object[] mon){
			//vk: почему-то иногда вначале вдруг появляется ещё один элемент 
			mvLabel = (String)mon[mon.length - 16];
			mvHits = ((Double)mon[mon.length - 15]).longValue();
			mvAvg = (Double)mon[mon.length - 14];
			mvMax = (Double)mon[mon.length - 9];
			//---
			//---
			mvHitsP = 0.0;
			mvTotalTime = 0.0;
			mvTotalTimeP = 0.0;
		}
		
		private void calcPercent(long sumHits, double sumTotalTime) {
			if (sumHits > 0){
				mvHitsP = mvHits * 100.0 /sumHits;
			}
			if (sumTotalTime > 0){
				mvTotalTime = mvHits * mvAvg;
				mvTotalTimeP = mvTotalTime * 100.0 / sumTotalTime;
			}
		}
	}
}
