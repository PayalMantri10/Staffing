package com.envision.Staffing.services;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.envision.Staffing.model.Clinician;
import com.envision.Staffing.model.Day;
import com.envision.Staffing.model.DayShift;
import com.envision.Staffing.model.HourlyDetail;
import com.envision.Staffing.model.Input;
import com.envision.Staffing.model.JobDetails;
import com.envision.Staffing.model.Output;
import com.envision.Staffing.model.Shift;
import com.envision.Staffing.model.Workload;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ShiftPlanningService {
	Logger log = Logger.getLogger(ShiftPlanningService.class);
	private String[] days = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
			"Saturday" };

	public Input processFtpInput(InputStream ftpInputStream, JobDetails jobDetails) throws Exception {

		Input input = new Input();

		input.setClinician(jobDetails.getClinicians().stream().toArray(Clinician[]::new));
		input.setLowerLimitFactor(jobDetails.getLowerUtilizationFactor());
		input.setUpperLimitFactor((double) jobDetails.getUpperUtilizationFactor());
		input.setNotAllocatedStartTime(jobDetails.getNotAllocatedStartTime());
		input.setNotAllocatedEndTime(jobDetails.getNotAllocatedEndTime());
		input.setPatientHourWait(jobDetails.getPatientHourWait());
		input.setShiftLength(jobDetails.getShiftLengthPreferences());
		input.setDayWorkload(getDataFromExcelFile(ftpInputStream));
		return input;
	}

	// function to process form-data containing json object and the workload as an
	// .xlsx file
	// return the input object
	public Input processFileInput(MultipartFile excelFile, String inputData) throws Exception {

		Input input = new ObjectMapper().readValue(inputData, Input.class);
		InputStream excelInput = excelFile.getInputStream();
		input.setDayWorkload(getDataFromExcelFile(excelInput));
		return input;
	}

	public Day[] getDataFromExcelFile(InputStream excelInputStream) throws Exception {

		XSSFWorkbook myExcelBook;
		Day[] workload = new Day[7];
		try {
			myExcelBook = new XSSFWorkbook(excelInputStream);
			XSSFSheet myExcelSheet = myExcelBook.getSheetAt(0);

			for (int i = 0; i <= 6; i++) {
				workload[i] = new Day();
				// setting days name as saturday or monday etc
				workload[i].setName(days[i]);
				Double[] personPerHour = new Double[24];
				for (int j = 0; j <= 23; j++) {
					personPerHour[j] = Double.valueOf(myExcelSheet.getRow(i + 1).getCell(j + 1).getNumericCellValue());
				}
				workload[i].setExpectedPatientsPerHour(personPerHour);
			}
			myExcelBook.close();
		} catch (IOException e) {
			log.error("Error happened in getData from excel file method", e);
			e.printStackTrace();
		} catch (IllegalStateException illegalStateException) {
			throw new Exception(illegalStateException.toString());
		} catch (NumberFormatException numberFormatException) {
			throw new Exception(numberFormatException.toString());
		}

		return workload;
	}

	public Output getShiftPlan(Input input) throws IOException {

		log.info("*** ========================== ***");
		log.info("*** Scheduling Process Started ***");
		log.info("*** ========================== ***");

		Integer[] shiftPreferences = new Integer[] { 12, 10, 8, 4 };
		double lowerLimitFactor = 0.75;
		double upperLimitFactor = 1.1;
		Integer notAllocatedStartTime = 1;
		Integer notAllocatedEndTime = 6;
		Integer patientHourWait = 2;

		Clinician[] inputClinicians = input.getClinician();

		Clinician[] clinicians = Arrays.copyOf(inputClinicians, inputClinicians.length);
		Comparator<Clinician> comparator = Comparator.comparing(Clinician::getCost);
		Arrays.sort(clinicians, comparator);
		Arrays.sort(clinicians, Collections.reverseOrder());

		log.info("A.Inputs Description");
		log.info("---------------------------------");
		log.info("A.1.Default Values for Inputs ");
		log.info("---------------------------------");
		log.info("A.1.1 ShiftPreferences :" + Arrays.toString(shiftPreferences));
		log.info("A.1.2 lowerLimitFactor :" + lowerLimitFactor);
		log.info("A.1.3 upperLimitFactor :" + upperLimitFactor);
		log.info("A.1.4 restrictionStartTime :" + notAllocatedStartTime);
		log.info("A.1.5 restrictionEndTime :" + notAllocatedEndTime);
		log.info("A.1.6 numberOfPatientHourWait :" + patientHourWait);

		if (input.getShiftLength() != null) {
			shiftPreferences = input.getShiftLength();
		}

		if (input.getLowerLimitFactor() != null) {
			lowerLimitFactor = input.getLowerLimitFactor();
		}

		if (input.getUpperLimitFactor() != null) {
			upperLimitFactor = input.getUpperLimitFactor();
		}
		if (input.getNotAllocatedStartTime() != null) {
			notAllocatedStartTime = input.getNotAllocatedStartTime();
		}
		if (input.getNotAllocatedEndTime() != null) {
			notAllocatedEndTime = input.getNotAllocatedEndTime();
		}
		if (input.getPatientHourWait() != null) {
			patientHourWait = input.getPatientHourWait();
		}

		log.info("---------------------------------------");
		log.info("A.2.Getting Actual Values for Inputs ");
		log.info("---------------------------------------");
		log.info("A.2.1 shiftPreferences :" + Arrays.toString(shiftPreferences));
		log.info("A.2.2 lowerLimitFactor :" + lowerLimitFactor);
		log.info("A.2.3 upperLimitFactor :" + upperLimitFactor);
		log.info("A.2.4 restrictionStartTime :" + notAllocatedStartTime);
		log.info("A.2.5 restrictionEndTime :" + notAllocatedEndTime);
		log.info("A.2.6 numberOfPatientHourWait :" + patientHourWait);
		log.info("---------------------------------");
		log.info("B.Clinicians Details ");
		log.info("---------------------------------");
		for (int i = 0; i < clinicians.length; i++) {
			log.info("B." + (i + 1) + ".1 clinicianName :" + clinicians[i].getName() + ", B." + (i + 1)
					+ ".2 actualCapacity :" + clinicians[i].getPatientsPerHour() + ", B." + (i + 1) + ".3 Cost :"
					+ clinicians[i].getCost() + ", B." + (i + 1) + ".4 First/Mid/LastHourCapacity :"
					+ Arrays.toString(clinicians[i].getCapacity()) + ", B." + (i + 1) + ".5 Expression :"
					+ clinicians[i].getExpressions());
		}

		Workload work = new Workload();
		
		if (input.getDayWorkload() != null) {
			work = assignWorkload(input, work);
		}

		for (int i = 0; i < clinicians.length; i++) {
			clinicians[i].setClinicianCountPerHour(new int[168]);
		}

		ShiftCalculator shiftCalculator = new ShiftCalculator();
		shiftCalculator.setWorkloads(work);

		// checking which clinician is always true and store index in arrindex for this
		// clinician

		log.info(
				"Assigning Clinicians to Corresponding ShiftPreferences based upon the condition like Utilization,clinicianExpression");
		for (int i = 0; i < shiftPreferences.length; i++) {
			if (i != (shiftPreferences.length - 1)) {
				shiftCalculator.calculatePhysicianSlotsForAll(notAllocatedStartTime, notAllocatedEndTime,
						shiftPreferences[i], clinicians, lowerLimitFactor);
			} else
				shiftCalculator.calculateLastHourSlots(upperLimitFactor, notAllocatedStartTime, notAllocatedEndTime,
						clinicians, shiftPreferences[i]);
		}

		HourlyDetail[] hourlyDetailList = shiftCalculator.generateHourlyDetail(patientHourWait, clinicians,
				work.getDocEfficency(), lowerLimitFactor);

		// calculating the count of clinicians starting and ending at each hour

		ArrayList<Map<Integer, Map<String, Integer>>> clinicianStartEndCount = new ArrayList<>(168);

		String[] clincianCountKeys = new String[2 * clinicians.length];
		for (int i = 0; i < clinicians.length; i++) {
			clincianCountKeys[2 * i] = clinicians[i].getName() + "Start";
			clincianCountKeys[2 * i + 1] = clinicians[i].getName() + "End";
		}

		for (int i = 0; i < 168; i++) {
			Map<Integer, Map<String, Integer>> slotMap = new HashMap<>();
			for (int slot : shiftPreferences) {

				Map<String, Integer> clinicianMap = new HashMap<>();
				for (String clinicianKey : clincianCountKeys) {

					clinicianMap.put(clinicianKey, 0);

				}
				slotMap.put(slot, clinicianMap);

			}

			clinicianStartEndCount.add(slotMap);

		}

		List<List<Shift>> dayToshiftsmappingTemp = shiftCalculator.printSlots();
		for (int i = 0; i < 7; i++) {

			for (Shift s : dayToshiftsmappingTemp.get(i)) {
				Map<Integer, Map<String, Integer>> slotMapTemp = clinicianStartEndCount
						.get(s.getStartTime() + (i * 24));
				Map<String, Integer> clinicianMapTempStart = slotMapTemp.get(s.getNoOfHours());

				clinicianMapTempStart.put(s.getPhysicianType() + "Start",
						clinicianMapTempStart.get(s.getPhysicianType() + "Start") + 1);
				slotMapTemp.put(s.getNoOfHours(), clinicianMapTempStart);
				clinicianStartEndCount.set(s.getStartTime() + (i * 24), slotMapTemp);

				// updating end time
				if (((s.getStartTime() + (i * 24)) + (s.getNoOfHours())) < 168) {
					slotMapTemp = clinicianStartEndCount.get(s.getStartTime() + (i * 24) + s.getNoOfHours());
					clinicianMapTempStart = slotMapTemp.get(s.getNoOfHours());

					clinicianMapTempStart.put(s.getPhysicianType() + "End",
							clinicianMapTempStart.get(s.getPhysicianType() + "End") + 1);
					slotMapTemp.put(s.getNoOfHours(), clinicianMapTempStart);
					clinicianStartEndCount.set(s.getStartTime() + (i * 24) + s.getNoOfHours(), slotMapTemp);

				}

			}
		}

		Output out = new Output();
		out.setHourlyDetail(hourlyDetailList);
		out.setClinicianHourCount(clinicianStartEndCount);
		return out;
	}

	Workload assignWorkload(Input input, Workload work) {
		Day[] day;
		int k = 0, j = 1;
		log.info("---------------------------------");
		log.info("C.WorkLoad Details");
		log.info("---------------------------------");

		if (input.getDayWorkload() != null) {
			day = input.getDayWorkload();
			for (Day eachDay : day) {
				log.info("C." + j + ".1 Day :" + eachDay.getName());
				log.info("C." + j + ".2 ExpectedPatientPerHour :"
						+ Arrays.toString(eachDay.getExpectedPatientsPerHour()));
				j++;
				for (Double patientsPerHour : eachDay.getExpectedPatientsPerHour()) {
					work.getFixedworkloadArray()[k] = patientsPerHour;
					work.getWorkloadArray()[k] = work.getFixedworkloadArray()[k];
					k++;

				}
			}

		}
		return work;
	}

	public ByteArrayOutputStream excelWriter(Output output, JobDetails jobDetails) throws IOException {

		Workbook workbook = new XSSFWorkbook();
		Sheet coverageSummary = workbook.createSheet("Coverage Summary");
		Sheet shiftSummary = workbook.createSheet("Shift Summary");

		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 14);
		headerFont.setColor(IndexedColors.RED.getIndex());

		Font dayFont = workbook.createFont();
		dayFont.setBold(true);
		dayFont.setFontHeightInPoints((short) 14);
		dayFont.setColor(IndexedColors.BLUE.getIndex());

		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);

		CellStyle dayCellStyle = workbook.createCellStyle();
		dayCellStyle.setFont(dayFont);

		createCoverageSummarySheet(output, coverageSummary, headerCellStyle, dayCellStyle); // CoverageSummarySheet
		createShiftSummarySheet(output, jobDetails, shiftSummary, headerCellStyle, dayCellStyle); // ShiftSummarySheet

		FileOutputStream fos = new FileOutputStream("localOutput.xlsx");
		workbook.write(fos);
		fos.close();

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		workbook.write(bos); // write excel data to a byte array
		bos.close();
		workbook.close();
		return bos;
	}

	private void createCoverageSummarySheet(Output output, Sheet coverageSummary, CellStyle headerCellStyle,
			CellStyle dayCellStyle) {
		HourlyDetail[] hourlyDetailsList = output.getHourlyDetail();
		Row headerRow = coverageSummary.createRow(0);
		String[] coverageSummaryColumn = { "Day", "Hour", "Physician Coverage", "App Coverage", "Scribe Coverage",
				"Total Coverage", "Percent Physician", "Expected Patient Arriving", "Covered Patient Arriving",
				"Difference", "Expected Patient Per Provider", "Covered Patient Per Provider", "Cost", "Hour Wait ",
				"Patient Lost" };

		for (int i = 0; i < coverageSummaryColumn.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(coverageSummaryColumn[i]);
			cell.setCellStyle(headerCellStyle);
		}

		String day;
		int hour;
		int physician;
		int app;
		int scribe;
		double totalCoverage;
		double percentPhysician;
		double expectedPatientsPerProvider;
		double coveredPatientsPerProvider;
		double expectedWorkLoad;
		double capacityWorkLoad;
		double loss;
		double wait;
		double differnceBetweenCapacityAndWorkload;
		int cost;

		double totalCost = 0;
		double totalExpectedPatient = 0;
		double totalCapacityWorkload = 0;
		double totalPatientsWaiting = 0;
		double totalPatientsLoss = 0;
		int rowCount1 = 1;

		for (HourlyDetail detailedHour : hourlyDetailsList) {

			hour = (detailedHour.getHour()) % 24;
			physician = detailedHour.getNumberOfPhysicians();
			app = detailedHour.getNumberOfAPPs();
			scribe = detailedHour.getNumberOfScribes();
			totalCoverage = detailedHour.getNumberOfPhysicians() + detailedHour.getNumberOfAPPs()
					+ detailedHour.getNumberOfScribes();
			if (totalCoverage == 0) {
				percentPhysician = 0;
				expectedPatientsPerProvider = 0;
				coveredPatientsPerProvider = 0;
			} else {
				percentPhysician = detailedHour.getNumberOfPhysicians() / totalCoverage;
				expectedPatientsPerProvider = detailedHour.getExpectedWorkLoad() / totalCoverage * 100 / 100;
				coveredPatientsPerProvider = detailedHour.getCapacityWorkLoad() / totalCoverage * 100 / 100;
			}
			expectedWorkLoad = detailedHour.getExpectedWorkLoad();
			capacityWorkLoad = detailedHour.getCapacityWorkLoad();
			cost = detailedHour.getCostPerHour();
			loss = detailedHour.getLoss() * 100 / 100;
			wait = detailedHour.getWait() * 100 / 100;
			differnceBetweenCapacityAndWorkload = (detailedHour.getCapacityWorkLoad()
					- detailedHour.getExpectedWorkLoad()) * 100 / 100;

			Row row = coverageSummary.createRow(rowCount1++);
			day = days[(rowCount1 - 2) / 24];

			addCellCoverageSummary(row, dayCellStyle, day, hour, physician, app, scribe, totalCoverage,
					percentPhysician, expectedWorkLoad, capacityWorkLoad, differnceBetweenCapacityAndWorkload,
					expectedPatientsPerProvider, coveredPatientsPerProvider, cost, wait, loss);

			totalExpectedPatient = totalExpectedPatient + detailedHour.getExpectedWorkLoad();
			totalCapacityWorkload = totalCapacityWorkload + detailedHour.getCapacityWorkLoad();
			totalPatientsWaiting = totalPatientsWaiting + wait;
			totalPatientsLoss = totalPatientsLoss + loss;
			totalCost = totalCost + detailedHour.getCostPerHour();
		}
		for (int i = 0; i < 7; i++) {
			coverageSummary.addMergedRegion(new CellRangeAddress(24 * i + 1, 24 * i + 24, 0, 0));
		}

		Row rowName = coverageSummary.createRow(rowCount1 + 1);
		Cell cell = rowName.createCell(0);
		cell.setCellValue("Overall Summary");
		cell.setCellStyle(dayCellStyle);

		String[] summary = { "Total Expected Patient", "Total Capacity Workload", "Total Patients Waiting",
				"Total Patients Loss", "Total Cost" };
		Double[] summaryValue = { totalExpectedPatient, totalCapacityWorkload, Math.abs(totalPatientsWaiting),
				Math.abs(totalPatientsLoss), totalCost };

		addCellOverallSummary(coverageSummary, headerCellStyle, summary, summaryValue, rowCount1);

		for (int i = 0; i < coverageSummaryColumn.length; i++) {
			coverageSummary.autoSizeColumn(i);
		}
	}

	private void addCellOverallSummary(Sheet coverageSummary, CellStyle headerCellStyle, String[] summary,
			Double[] summaryValue, int rowCount1) {
		for (int i = 0; i < summary.length; i++) {
			Row row1 = coverageSummary.createRow(rowCount1 + 2 + i);
			Cell cell1 = row1.createCell(0);
			cell1.setCellValue(summary[i]);
			cell1.setCellStyle(headerCellStyle);
			Cell cell2 = row1.createCell(1);
			cell2.setCellValue(summaryValue[i]);
		}
	}

	private void addCellCoverageSummary(Row row, CellStyle dayCellStyle, String day, int hour, int physician, int app,
			int scribe, double totalCoverage, double percentPhysician, double expectedWorkLoad, double capacityWorkLoad,
			double differnceBetweenCapacityAndWorkload, double expectedPatientsPerProvider,
			double coveredPatientsPerProvider, int cost, double wait, double loss) {
		Cell cell = row.createCell(0);
		cell.setCellValue(day);
		cell.setCellStyle(dayCellStyle);
		row.createCell(1).setCellValue(hour);
		row.createCell(2).setCellValue(physician);
		row.createCell(3).setCellValue(app);
		row.createCell(4).setCellValue(scribe);
		row.createCell(5).setCellValue(totalCoverage);
		row.createCell(6).setCellValue(percentPhysician);
		row.createCell(7).setCellValue(expectedWorkLoad);
		row.createCell(8).setCellValue(capacityWorkLoad);
		row.createCell(9).setCellValue(differnceBetweenCapacityAndWorkload);
		row.createCell(10).setCellValue(expectedPatientsPerProvider);
		row.createCell(11).setCellValue(coveredPatientsPerProvider);
		row.createCell(12).setCellValue(cost);
		row.createCell(13).setCellValue(wait);
		row.createCell(14).setCellValue(loss);
	}

	private void createShiftSummarySheet(Output output, JobDetails jobDetails, Sheet shiftSummary,
			CellStyle headerCellStyle, CellStyle dayCellStyle) {

		ArrayList<DayShift> dayShiftList = new ArrayList<>();
		dayShiftList = getDayShiftList(output, jobDetails);

		Row headerRow2 = shiftSummary.createRow(0);
		String[] shiftSummaryColumn = { "Day        ", "Start Time", "End Time", "Shift Length", "Physician", "App",
				"Scribe" };

		for (int i = 0; i < shiftSummaryColumn.length; i++) {
			Cell cell2 = headerRow2.createCell(i);
			cell2.setCellValue(shiftSummaryColumn[i]);
			cell2.setCellStyle(headerCellStyle);
		}

		int rowCount2 = 1;
		for (DayShift dayShift : dayShiftList) {

			Row row = shiftSummary.createRow(rowCount2++);

			Cell dayCell = row.createCell(0);
			dayCell.setCellValue(dayShift.getDay());
			dayCell.setCellStyle(dayCellStyle);
			row.createCell(1).setCellValue(dayShift.getStartTime());
			row.createCell(2).setCellValue(dayShift.getEndTime());
			row.createCell(3).setCellValue(dayShift.getShiftLength());
			row.createCell(4).setCellValue(dayShift.getPhysician());
			row.createCell(5).setCellValue(dayShift.getApp());
			row.createCell(6).setCellValue(dayShift.getScribe());
		}

		int rowStart = 1;
		int rowEnd = 0;
		for (int i = 1; i <= shiftSummary.getPhysicalNumberOfRows() - 2; i++) {
			if (shiftSummary.getRow(i).getCell(0).getStringCellValue()
					.equals(shiftSummary.getRow(i + 1).getCell(0).getStringCellValue())) {
				rowEnd = i + 1;
			} else {
				CellRangeAddress cellRangeAddress = new CellRangeAddress(rowStart, rowEnd, 0, 0);
				shiftSummary.addMergedRegion(cellRangeAddress);
				rowStart = 1 + rowEnd;
			}
		}
		CellRangeAddress cellRangeAddress = new CellRangeAddress(rowStart, rowEnd, 0, 0);
		shiftSummary.addMergedRegion(cellRangeAddress);

		for (int i = 0; i < shiftSummaryColumn.length; i++) {
			shiftSummary.autoSizeColumn(i);
		}
	}

	private ArrayList<DayShift> getDayShiftList(Output output, JobDetails jobDetails) {

		List<Map<Integer, Map<String, Integer>>> shiftSlots = output.getClinicianHourCount();
		ArrayList<DayShift> dayShiftList = new ArrayList<>();
		Map<String, DayShift> map = new HashMap<>();

		int length = jobDetails.getClinicians().size();
		List<String> clinicianName = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			clinicianName.add(jobDetails.getClinicians().get(i).getName());
		}

		int index = 0;
		for (Map<Integer, Map<String, Integer>> shiftSlot : shiftSlots) {
			for (Integer key : shiftSlot.keySet()) {
				DayShift shift = new DayShift();
				for (String name : clinicianName) {
					if (shiftSlot.get(key).get(name + "Start") > 0) {
						if (map.containsKey(index + "to" + key)) {
							shift = map.get(index + "to" + key);
							shift.setName(name, shift.getName(name) + shiftSlot.get(key).get(name + "Start"));
						} else {
							shift = this.createNewShift(index, key);
							shift.setName(name, shiftSlot.get(key).get(name + "Start"));
						}
						dayShiftList.add(shift);
						map.put(index + "to" + key, shift);
					}
				}
			}
			index++;
		}
		for (int i = 1; i < dayShiftList.size(); i++) {
			if (dayShiftList.get(i).toString().equals(dayShiftList.get(i - 1).toString())) {
				dayShiftList.remove(i - 1);
				i--;
			}
		}
		return dayShiftList;
	}

	private DayShift createNewShift(int startTime, Integer shiftLength) {
		String[] daysOfWeek = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
		DayShift shift = new DayShift();
		shift.setStartTime(startTime % 24);
		shift.setEndTime((startTime + shiftLength) % 24);
		shift.setDay(daysOfWeek[(int) Math.floor(startTime / 24)]);
		shift.setShiftLength(shiftLength);
		return shift;
	}

}