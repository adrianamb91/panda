package com.timesheetapplication.servlets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.timesheetapplication.enums.MonthlyTimesheetStatus;
import com.timesheetapplication.model.Activity;
import com.timesheetapplication.model.DailyTimeSheet;
import com.timesheetapplication.model.Department;
import com.timesheetapplication.model.Employee;
import com.timesheetapplication.model.MonthlyTimesheet;
import com.timesheetapplication.model.Project;
import com.timesheetapplication.service.ActivityService;
import com.timesheetapplication.service.DailyTimesheetService;
import com.timesheetapplication.service.DepartmentService;
import com.timesheetapplication.service.EmployeeService;
import com.timesheetapplication.service.MonthlyTimeSheetService;
import com.timesheetapplication.service.ProjectService;
import com.timesheetapplication.utils.TSMUtil;

/**
 * Servlet implementation class DepartmentManagerServlet
 */
@WebServlet("/DepartmentManagerServlet")
public class DepartmentManagerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	HttpSession session;
	DepartmentService departmentService = new DepartmentService();

	EmployeeService employeeService = new EmployeeService();

	MonthlyTimeSheetService mTimesheetService = new MonthlyTimeSheetService();

	DailyTimesheetService dTimesheetService = new DailyTimesheetService();

	ActivityService activityService = new ActivityService();

	ProjectService projectService = new ProjectService();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DepartmentManagerServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String phase = new String(request.getParameter("phase").toString());
		JSONObject responseMessage = new JSONObject();

		session = request.getSession();
		Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");

		switch (phase) {
		case "loadEmployees":
			processLoadManagersEmployees(loggedInUser, responseMessage, response);
			break;
		case "reviewLastMTS":
			processReviewLastMTS(request, responseMessage, response);
			break;
		case "reviewMTSforUserInInterval":
			processReviewMTSforUserInInterval(request, responseMessage, response);
			break;
		case "reviewSummaryWorkFromEmployeeInInterval":
			processReviewSummaryForUSerInInterval(request, responseMessage, response);
			break;
		case "reviewSummaryWorkForProjectInInterval":
			processReviewProjectWorkInInterval(request, responseMessage, response);
			break;
		case "reviewWorkInDepartmentInInterval":
			processReviewWordAtDepartment(request, responseMessage, response);
			break;
		case "exportProjectSummaryToPDF":
			processExportProjectSummaryToPDF(request, responseMessage, response);
			break;
		case "exportMTSforUserInIntervaltoPDF":
			processExportMTSforUserInIntervaltoPDF(request, responseMessage, response);
			break;
		case "exportWorkInDepartmentInInterval":
			processExportWorkInDepartmentInInterval(request, responseMessage, response);
			break;
		case "exportXLSwithReviewLastMTS":
			processExportXLSwithReviewLastMTS(request, responseMessage, response);
			break;
		case "exportWorkInDepartmentInIntervalXLS":
			processexportWorkInDepartmentInIntervalXLS(request, responseMessage, response);
			break;
		case "reviewLastSubmittedMTS":
			processReviewLastSubmittedMTS(request, responseMessage, response);
			break;
		case "changeMTSStatus":
			processChangeMTSStatus(request, responseMessage, response);
			break;
		default:
			break;
		}

	}

	private void processChangeMTSStatus(HttpServletRequest request,
			JSONObject responseMessage, HttpServletResponse response) {
		// TODO Auto-generated method stub
		String ename = request.getParameter("emp");
		String newStatus = request.getParameter("newstatus");

		Employee emp = employeeService.findEmployeeByFirstAndLastName(ename);

		Date maxDate = null;
		MonthlyTimesheet mts = null;

		List<MonthlyTimesheet> mtss = mTimesheetService.findAllMTSforUser(emp);
		if (mtss != null) {

			for (MonthlyTimesheet m: mtss) {
				if (m.getStatus().equals(MonthlyTimesheetStatus.APPROVED) || m.getStatus().equals(MonthlyTimesheetStatus.SUBMITTED)) {
					if (maxDate == null) {
						maxDate = m.getDate();
						mts = m;
					} else {
						if (maxDate.compareTo(m.getDate()) < 0) {
							maxDate = m.getDate();
							mts = m;
						}
					}
				}
			}
		}

		try {
			if (mts != null) {
				mts.setStatus(MonthlyTimesheetStatus.valueOf(newStatus));
				mTimesheetService.saveOrUpdate(mts);
				responseMessage.put("ok", true);
			} else {
				responseMessage.put("ok", false);
			}
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void processReviewLastSubmittedMTS(HttpServletRequest request,
			JSONObject responseMessage, HttpServletResponse response) {
		// TODO Auto-generated method stub
		String ename = request.getParameter("name");

		System.out.println("requested last submitted/approved mts for user:" + ename);

		Employee e = null;

		if (TSMUtil.isNotEmptyOrNull(ename)) {
			e = employeeService.findEmployeeByFirstAndLastName(ename);
			if (e != null) {
				List<MonthlyTimesheet> mts = mTimesheetService.findAllMTSforUser(e);
				if (mts != null) {
					Date maxDate = null;
					MonthlyTimesheet mtsTheOneIWant = null;
					for (MonthlyTimesheet m: mts) {
						if (m.getStatus().equals(MonthlyTimesheetStatus.APPROVED) || m.getStatus().equals(MonthlyTimesheetStatus.SUBMITTED)) {
							if (maxDate == null) {
								maxDate = m.getDate();
								mtsTheOneIWant = m;
							} else {
								if (maxDate.compareTo(m.getDate()) < 0) {
									maxDate = m.getDate();
									mtsTheOneIWant = m;
								}
							}
						}
					}
					if (maxDate == null) {
						//n-avem decat open
						System.out.println("n-avem monthlytimesheet");
					} else {
						List<DailyTimeSheet> dts = null;
						dts = dTimesheetService.findAllDTFromMTS(mtsTheOneIWant);
						if (dts == null) {
							System.out.println("n-avem niciun dailytimesheet");
						} else {
							List <Activity> acts = new ArrayList<Activity>();
							System.out.println("avem " + dts.size() + " pentru mts dorit");
							for (DailyTimeSheet d: dts) {
								List<Activity> auxAct = activityService.findActivitiesByDTS(d);
								System.out.println("avem " + auxAct.size() + " pentru dts " + d.getDate().toString());
								acts.addAll(auxAct);
							}
							if (acts.size() == 0) {
								System.out.println("n-avem nicio activitate");
								//degeaba
							} else {
								ArrayList<String> dateArray = new ArrayList<String>();
								ArrayList<String> durationArray = new ArrayList<String>();
								ArrayList<String> descriptionArray = new ArrayList<String>();
								ArrayList<String> projectArray = new ArrayList<String>();

								JSONArray array_du, array_de, array_p, array_da;

								for (Activity a : acts) {
									dateArray.add(TSMUtil.formatDate(a.getTimesheet().getDate()));
									durationArray.add(a.getDuration().toString());
									descriptionArray.add(a.getDescription());
									projectArray.add(a.getProject().getName());
								}

								array_da = new JSONArray(dateArray);
								array_du = new JSONArray(durationArray);
								array_de = new JSONArray(descriptionArray);
								array_p = new JSONArray(projectArray);

								try {
									responseMessage.put("date", array_da);
									responseMessage.put("description", array_de);
									responseMessage.put("duration", array_du);
									responseMessage.put("project", array_p);
									responseMessage.put("size", acts.size());
									responseMessage.put("ok", true);
									responseMessage.put("mtsDate", mtsTheOneIWant.getDate().toString());
									responseMessage.put("status", mtsTheOneIWant.getStatus().toString());

									response.setContentType("application/json; charset=UTF-8");
									response.getWriter().write(responseMessage.toString());

									return;
								} catch (JSONException | IOException e1) {
									e1.printStackTrace();
								}
							}
						}
					}
				}
			}
		}

		try {
			responseMessage.put("ok", false);
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());

		} catch (JSONException | IOException e1) {
			e1.printStackTrace();
		}
	}


	private void processexportWorkInDepartmentInIntervalXLS(HttpServletRequest request, JSONObject responseMessage, HttpServletResponse response) {

		String from = request.getParameter("from");
		String to = request.getParameter("to");

		Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");

		List<Project> projects = projectService.getProjectsForDepartment(loggedInUser.getDepartment());

		try {
			if (projects != null) {

				Map<String, Float> map = new HashMap<String, Float>();

				for (Project p : projects) {
					List<Activity> activities = activityService.findWorkPutIntoProject(p, TSMUtil.convertStringToDate(from),
							TSMUtil.convertStringToDate(to));
					for (Activity a : activities) {
						if (!map.containsKey(p.getName())) {
							map.put(p.getName(), a.getDuration());
						} else {
							Float oldDuration = map.get(p.getName());
							map.put(p.getName(), oldDuration + a.getDuration());
						}
					}
				}

				String formatted_from = from.replace("/", "-");
				String formatted_to = to.replace("/", "-");

				HSSFWorkbook workbook = new HSSFWorkbook();
				HSSFSheet sheet = workbook.createSheet("First sheet");
				int rownum = 0;
				Row row = sheet.createRow(rownum++);
				Cell cell1 = row.createCell(1);
				cell1.setCellValue("PROJECT");
				Cell cell2 = row.createCell(2);
				cell2.setCellValue("WORKING HOURS");

				for (String proj : map.keySet()) {
					row = sheet.createRow(rownum++);
					cell1 = row.createCell(1);
					cell1.setCellValue(proj);
					cell2 = row.createCell(2);
					cell2.setCellValue(map.get(proj).toString());
				}
				try {
					FileOutputStream out = new FileOutputStream(new File("C:\\temp\\" + loggedInUser.getDepartment().getName() + "_" + formatted_from + "_" + formatted_to + ".xls"));
					workbook.write(out);
					out.close();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				responseMessage.put("ok", true);
			} else {
				responseMessage.put("ok", false);
			}
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processExportXLSwithReviewLastMTS(HttpServletRequest request, JSONObject responseMessage, HttpServletResponse response) {

		/**
		 * Export XLS la nivel de EMPLOYEE
		 */

		String ename = request.getParameter("name");

		Employee e = null;
		List<DailyTimeSheet> dts = null;
		List<Activity> allActivities = new ArrayList<Activity>();
		List<Activity> crtActivities = new ArrayList<Activity>();

		if (TSMUtil.isNotEmptyOrNull(ename)) {
			e = employeeService.findEmployeeByFirstAndLastName(ename);

			if (e != null) {

				// aici se afiseaza numai pentru luna curenta pentru ca din alta
				// luna e prea greu... fac maine cu iubi:X:X
				Date date = TSMUtil.truncateDateToMonthsFirst(new Date());
				MonthlyTimesheet mts = mTimesheetService.findMTSByDateAndUser(date, e);

				if (mts != null && mts.getId() != null) {
					dts = dTimesheetService.findAllDTFromMTS(mts);
					if (dts != null) {
						for (DailyTimeSheet crtDTS : dts) {
							crtActivities = activityService.findActivitiesByDTS(crtDTS);
							if (crtActivities != null && crtActivities.size() > 0) {
								allActivities.addAll(crtActivities);
							}
						}

						HSSFWorkbook workbook = new HSSFWorkbook();
						HSSFSheet sheet = workbook.createSheet("First sheet");
						int rownum = 0;
						Row row = sheet.createRow(rownum++);
						Cell cell1 = row.createCell(1);
						cell1.setCellValue("DATA");
						Cell cell2 = row.createCell(2);
						cell2.setCellValue("WORKING HOURS");
						Cell cell3 = row.createCell(3);
						cell3.setCellValue("DESCRIPTION'");
						Cell cell4 = row.createCell(4);
						cell4.setCellValue("PROJECT");
						for (Activity a : allActivities) {
							row = sheet.createRow(rownum++);
							cell1 = row.createCell(1);
							cell1.setCellValue(TSMUtil.formatDate(a.getTimesheet().getDate()));
							cell2 = row.createCell(2);
							cell2.setCellValue(a.getDuration().toString());
							cell3 = row.createCell(3);
							cell3.setCellValue(a.getDescription());
							cell4 = row.createCell(4);
							cell4.setCellValue(a.getProject().getName());
						}
						try {
							String formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(date);
							FileOutputStream out = new FileOutputStream(new File("C:\\temp\\" + e.getUsername() + "_" + formattedDate + ".xls"));
							workbook.write(out);
							out.close();
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						} catch (IOException e2) {
							e2.printStackTrace();
						}
						try {
							responseMessage.put("ok", true);
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
					} else {
						try {
							responseMessage.put("ok", false);
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		} else {
			try {
				responseMessage.put("ok", false);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		try {
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void processExportWorkInDepartmentInInterval(HttpServletRequest request, JSONObject responseMessage, HttpServletResponse response) {

		/**
		 * exporta la nivel de toate departamentele
		 */

		String from = request.getParameter("from");
		String to = request.getParameter("to");

		Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");

		List<Project> projects = projectService.getProjectsForDepartment(loggedInUser.getDepartment());

		try {
			if (projects != null) {

				Map<String, Float> map = new HashMap<String, Float>();

				for (Project p : projects) {
					List<Activity> activities = activityService.findWorkPutIntoProject(p, TSMUtil.convertStringToDate(from),
							TSMUtil.convertStringToDate(to));
					for (Activity a : activities) {
						if (!map.containsKey(p.getName())) {
							map.put(p.getName(), a.getDuration());
						} else {
							Float oldDuration = map.get(p.getName());
							map.put(p.getName(), oldDuration + a.getDuration());
						}
					}
				}

				String formatted_from = from.replace("/", "-");
				String formatted_to = to.replace("/", "-");

				Document document = new Document();
				try {
					PdfWriter.getInstance(document, new FileOutputStream("c:/temp/" + loggedInUser.getDepartment().getName() + "_" + formatted_from
							+ "_" + formatted_to + ".pdf"));
				} catch (FileNotFoundException e2) {
					e2.printStackTrace();
				} catch (DocumentException e2) {
					e2.printStackTrace();
				}
				document.open();
				Paragraph preface = new Paragraph();
				TSMUtil.addEmptyLine(preface, 1);
				preface.add(new Paragraph("Report", TSMUtil.catFont));
				PdfPTable table = new PdfPTable(2);
				PdfPCell c1 = new PdfPCell(new Phrase("PROJECT"));
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(c1);
				c1 = new PdfPCell(new Phrase("WORKING HOURS"));
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(c1);

				for (String proj : map.keySet()) {
					table.addCell(proj);
					table.addCell(map.get(proj).toString());
				}
				responseMessage.put("ok", true);
				document.add(table);
				document.close();
			} else {
				responseMessage.put("ok", false);
			}
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}

	}

	private void processExportMTSforUserInIntervaltoPDF(HttpServletRequest request, JSONObject responseMessage, HttpServletResponse response) {

		/**
		 * exporta la nivel de user (detaliat)
		 */

		String ename = request.getParameter("name");
		String from = request.getParameter("from");
		String to = request.getParameter("to");

		System.out.println("from: " + from + "\n" + "to: " + to);

		if (TSMUtil.isNotEmptyOrNull(ename)) {
			Employee e = employeeService.findEmployeeByFirstAndLastName(ename);
			if (e != null) {
				List<DailyTimeSheet> dtsheets = dTimesheetService.loadDTSinInterval(e, TSMUtil.convertStringToDate(from),
						TSMUtil.convertStringToDate(to));
				if (dtsheets != null) {

					List<Activity> crtActivities = new ArrayList<Activity>();
					List<Activity> allActivities = new ArrayList<Activity>();

					for (DailyTimeSheet dts : dtsheets) {
						crtActivities = activityService.findActivitiesByDTS(dts);
						if (crtActivities != null && crtActivities.size() > 0) {
							allActivities.addAll(crtActivities);
						}
					}

					String formatted_from = from.replace("/", "-");
					String formatted_to = to.replace("/", "-");

					Document document = new Document();
					try {
						PdfWriter.getInstance(document, new FileOutputStream("c:/temp/" + e.getUsername() + "_" + formatted_from + "_" + formatted_to
								+ ".pdf"));
					} catch (FileNotFoundException e2) {
						e2.printStackTrace();
					} catch (DocumentException e2) {
						e2.printStackTrace();
					}
					document.open();
					Paragraph preface = new Paragraph();
					TSMUtil.addEmptyLine(preface, 1);
					preface.add(new Paragraph("Report", TSMUtil.catFont));
					PdfPTable table = new PdfPTable(4);
					PdfPCell c1 = new PdfPCell(new Phrase("DATA"));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(c1);
					c1 = new PdfPCell(new Phrase("WORKING HOURS"));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(c1);
					c1 = new PdfPCell(new Phrase("PROJECT"));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(c1);
					c1 = new PdfPCell(new Phrase("DESCRIPTION"));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(c1);

					for (Activity a : allActivities) {
						table.addCell(TSMUtil.formatDate(a.getTimesheet().getDate()));
						table.addCell(a.getDuration().toString());
						table.addCell(a.getDescription());
						table.addCell(a.getProject().getName());
					}
					try {
						responseMessage.put("ok", true);
						document.add(table);
						document.close();
					} catch (JSONException e1) {
						e1.printStackTrace();
					} catch (DocumentException e1) {
						e1.printStackTrace();
					}
				}
			} else {
				try {
					responseMessage.put("ok", false);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			}
		} else {
			try {
				responseMessage.put("ok", false);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		try {

			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	private void processExportProjectSummaryToPDF(HttpServletRequest request, JSONObject responseMessage, HttpServletResponse response) {

		/*
		 * Exporta la nivel de proiect
		 */

		String pname = request.getParameter("name");
		String from = request.getParameter("from");
		String to = request.getParameter("to");

		Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");

		try {
			if (TSMUtil.isNotEmptyOrNull(pname)) {

				Project p = projectService.findProjectByName(pname);
				if (p != null) {
					List<Activity> acts = activityService.findWorkPutIntoProject(p, TSMUtil.convertStringToDate(from),
							TSMUtil.convertStringToDate(to));

					Map<String, Float> projSumData = new HashMap<String, Float>();

					if (acts != null && acts.size() > 0) {
						for (Activity a : acts) {
							String firstAndLastName = a.getTimesheet().getOwner().getFirstName() + " " + a.getTimesheet().getOwner().getLastName();
							if (!projSumData.containsKey(firstAndLastName)) {
								projSumData.put(firstAndLastName, a.getDuration());
							} else {
								Float oldDur = projSumData.get(firstAndLastName);
								projSumData.put(firstAndLastName, oldDur + a.getDuration());
							}
						}

						/**
						 * EXPORT PDF SHIZZLE
						 */

						Document document = new Document();
						PdfWriter.getInstance(document, new FileOutputStream("c:/temp/" + loggedInUser.getUsername() + "_" + pname + ".pdf"));
						document.open();

						// adding page title;
						Paragraph preface = new Paragraph();
						TSMUtil.addEmptyLine(preface, 1);
						preface.add(new Paragraph("Report", TSMUtil.catFont));

						PdfPTable table = new PdfPTable(2);

						PdfPCell c1 = new PdfPCell(new Phrase("EMPLOYEE NAME"));
						c1.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(c1);

						c1 = new PdfPCell(new Phrase("WORKING HOURS"));
						c1.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(c1);

						for (String s : projSumData.keySet()) {
							// owners.add(s);
							// durations.add(projSumData.get(s).toString());
							//
							table.addCell(s);
							table.addCell(projSumData.get(s).toString());
						}

						document.add(table);
						document.close();

						responseMessage.put("ok", true);
					}
				} else {
					responseMessage.put("ok", false);
				}
			} else {
				responseMessage.put("ok", false);
			}
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}

	}

	private void processReviewWordAtDepartment(HttpServletRequest request, JSONObject responseMessage, HttpServletResponse response) {

		String from = request.getParameter("from");
		String to = request.getParameter("to");

		Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");

		List<Project> projects = projectService.getProjectsForDepartment(loggedInUser.getDepartment());

		try {
			if (projects != null) {

				Map<String, Float> map = new HashMap<String, Float>();

				for (Project p : projects) {
					List<Activity> activities = activityService.findWorkPutIntoProject(p, TSMUtil.convertStringToDate(from),
							TSMUtil.convertStringToDate(to));
					for (Activity a : activities) {
						if (!map.containsKey(p.getName())) {
							map.put(p.getName(), a.getDuration());
						} else {
							Float oldDuration = map.get(p.getName());
							map.put(p.getName(), oldDuration + a.getDuration());
						}
					}
				}

				StringBuilder sb = new StringBuilder();
				sb.append("[[");
				int iter = 0;
				for (String empl : map.keySet()) {
					sb.append("['");
					sb.append(empl);
					sb.append("', ");
					sb.append(map.get(empl));
					sb.append("]");
					if (iter < map.keySet().size() - 1) {
						sb.append(", ");
					}
					iter++;
				}
				sb.append("]]");
				JSONArray chartJSON = new JSONArray(sb.toString());
				responseMessage.put("chartDataJSON", chartJSON);

				List<String> projectNames = new ArrayList<String>();
				List<String> durations = new ArrayList<String>();
				for (String proj : map.keySet()) {
					projectNames.add(proj);
					durations.add(map.get(proj).toString());
				}

				JSONArray array_p, array_d;
				array_p = new JSONArray(projectNames);
				array_d = new JSONArray(durations);

				responseMessage.put("ok", true);
				responseMessage.put("size", projectNames.size());
				responseMessage.put("projects", array_p);
				responseMessage.put("durations", array_d);
			} else {
				responseMessage.put("ok", false);
			}
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processReviewProjectWorkInInterval(HttpServletRequest request, JSONObject responseMessage, HttpServletResponse response) {
		String pname = request.getParameter("name");
		String from = request.getParameter("from");
		String to = request.getParameter("to");

		try {
			if (TSMUtil.isNotEmptyOrNull(pname)) {
				Project p = projectService.findProjectByName(pname);
				if (p != null) {
					List<Activity> acts = activityService.findWorkPutIntoProject(p, TSMUtil.convertStringToDate(from),
							TSMUtil.convertStringToDate(to));

					List<String> owners = new ArrayList<String>();
					List<String> durations = new ArrayList<String>();

					Map<String, Float> projSumData = new HashMap<String, Float>();

					if (acts != null && acts.size() > 0) {
						for (Activity a : acts) {
							String firstAndLastName = a.getTimesheet().getOwner().getFirstName() + " " + a.getTimesheet().getOwner().getLastName();
							if (!projSumData.containsKey(firstAndLastName)) {
								projSumData.put(firstAndLastName, a.getDuration());
							} else {
								Float oldDur = projSumData.get(firstAndLastName);
								projSumData.put(firstAndLastName, oldDur + a.getDuration());
							}
						}

						StringBuilder sb = new StringBuilder();
						sb.append("[[");
						int iter = 0;
						for (String empl : projSumData.keySet()) {
							sb.append("['");
							sb.append(empl);
							sb.append("', ");
							sb.append(projSumData.get(empl));
							sb.append("]");
							if (iter < projSumData.keySet().size() - 1) {
								sb.append(", ");
							}
							iter++;
						}
						sb.append("]]");

						JSONArray chartJSON = new JSONArray(sb.toString());
						responseMessage.put("chartDataJSON", chartJSON);

						for (String s : projSumData.keySet()) {
							owners.add(s);
							durations.add(projSumData.get(s).toString());
						}

						JSONArray array_ow, array_du;
						array_ow = new JSONArray(owners);
						array_du = new JSONArray(durations);

						responseMessage.put("ok", true);
						responseMessage.put("size", owners.size());
						responseMessage.put("owners", array_ow);
						responseMessage.put("durations", array_du);
					}
				} else {
					responseMessage.put("ok", false);
				}
			} else {
				responseMessage.put("ok", false);
			}
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void processReviewSummaryForUSerInInterval(HttpServletRequest request, JSONObject responseMessage, HttpServletResponse response) {

		String ename = request.getParameter("name");
		String from = request.getParameter("from");
		String to = request.getParameter("to");

		System.out.println("from: " + from + "\n" + "to: " + to);

		if (TSMUtil.isNotEmptyOrNull(ename)) {
			Employee e = employeeService.findEmployeeByFirstAndLastName(ename);
			if (e != null) {
				List<DailyTimeSheet> dtsheets = dTimesheetService.loadDTSinInterval(e, TSMUtil.convertStringToDate(from),
						TSMUtil.convertStringToDate(to));
				if (dtsheets != null) {

					List<Activity> crtActivities = new ArrayList<Activity>();
					List<Activity> allActivities = new ArrayList<Activity>();
					Map<String, Float> chartData = new HashMap<String, Float>();

					for (DailyTimeSheet dts : dtsheets) {
						crtActivities = activityService.findActivitiesByDTS(dts);
						if (crtActivities != null && crtActivities.size() > 0) {
							allActivities.addAll(crtActivities);
						}
					}

					ArrayList<String> durationArray = new ArrayList<String>();
					ArrayList<String> projectArray = new ArrayList<String>();

					JSONArray array_du, array_de, array_p, array_da;

					for (Activity a : allActivities) {
						if (!chartData.containsKey(a.getProject().getName())) {
							chartData.put(a.getProject().getName(), a.getDuration());
						} else {
							Float oldValue = chartData.get(a.getProject().getName());
							chartData.put(a.getProject().getName(), oldValue + a.getDuration());
						}
					}

					for (String p : chartData.keySet()) {
						projectArray.add(p);
						durationArray.add(chartData.get(p).toString());
					}

					array_du = new JSONArray(durationArray);
					array_p = new JSONArray(projectArray);

					try {
						responseMessage.put("duration", array_du);
						responseMessage.put("project", array_p);
						responseMessage.put("size", chartData.size());
						responseMessage.put("ok", true);
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				}
			} else {
				try {
					responseMessage.put("ok", false);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			}
		} else {
			try {
				responseMessage.put("ok", false);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		try {

			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	private void processReviewMTSforUserInInterval(HttpServletRequest request, JSONObject responseMessage, HttpServletResponse response) {

		String ename = request.getParameter("name");
		String from = request.getParameter("from");
		String to = request.getParameter("to");

		System.out.println("from: " + from + "\n" + "to: " + to);

		if (TSMUtil.isNotEmptyOrNull(ename)) {
			Employee e = employeeService.findEmployeeByFirstAndLastName(ename);
			if (e != null) {
				List<DailyTimeSheet> dtsheets = dTimesheetService.loadDTSinInterval(e, TSMUtil.convertStringToDate(from),
						TSMUtil.convertStringToDate(to));
				if (dtsheets != null) {

					List<Activity> crtActivities = new ArrayList<Activity>();
					List<Activity> allActivities = new ArrayList<Activity>();
					Map<String, Float> chartData = new HashMap<String, Float>();

					for (DailyTimeSheet dts : dtsheets) {
						crtActivities = activityService.findActivitiesByDTS(dts);
						if (crtActivities != null && crtActivities.size() > 0) {
							allActivities.addAll(crtActivities);
						}
					}

					ArrayList<String> dateArray = new ArrayList<String>();
					ArrayList<String> durationArray = new ArrayList<String>();
					ArrayList<String> descriptionArray = new ArrayList<String>();
					ArrayList<String> projectArray = new ArrayList<String>();

					JSONArray array_du, array_de, array_p, array_da;

					for (Activity a : allActivities) {
						dateArray.add(TSMUtil.formatDate(a.getTimesheet().getDate()));
						durationArray.add(a.getDuration().toString());
						descriptionArray.add(a.getDescription());
						projectArray.add(a.getProject().getName());

						if (!chartData.containsKey(a.getProject().getName())) {
							chartData.put(a.getProject().getName(), a.getDuration());
						} else {
							Float oldValue = chartData.get(a.getProject().getName());
							chartData.put(a.getProject().getName(), oldValue + a.getDuration());
						}
					}

					array_da = new JSONArray(dateArray);
					array_du = new JSONArray(durationArray);
					array_de = new JSONArray(descriptionArray);
					array_p = new JSONArray(projectArray);

					System.out.println(array_da);
					System.out.println(chartData);

					JSONObject chartFormat = new JSONObject(chartData);
					System.out.println(chartFormat.toString());
					System.out.println(chartFormat);

					JSONArray chartJSON = null;
					StringBuilder sb = new StringBuilder();
					sb.append("[[");
					int iter = 0;
					for (String p : chartData.keySet()) {
						sb.append("['");
						sb.append(p);
						sb.append("', ");
						sb.append(chartData.get(p));
						sb.append("]");
						if (iter < chartData.keySet().size() - 1) {
							sb.append(", ");
						}
						iter++;
					}
					sb.append("]]");
					try {
						responseMessage.put("date", array_da);
						responseMessage.put("description", array_de);
						responseMessage.put("duration", array_du);
						responseMessage.put("project", array_p);
						responseMessage.put("size", allActivities.size());
						responseMessage.put("ok", true);
						chartJSON = new JSONArray(sb.toString());
						responseMessage.put("chartDataJSON", chartJSON);
						responseMessage.put("chartData", sb.toString());
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				}
			} else {
				try {
					responseMessage.put("ok", false);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			}
		} else {
			try {
				responseMessage.put("ok", false);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		try {

			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void processReviewLastMTS(HttpServletRequest request, JSONObject responseMessage, HttpServletResponse response) {
		String ename = request.getParameter("name");

		Employee e = null;
		List<DailyTimeSheet> dts = null;
		List<Activity> allActivities = new ArrayList<Activity>();
		List<Activity> crtActivities = new ArrayList<Activity>();

		if (TSMUtil.isNotEmptyOrNull(ename)) {
			e = employeeService.findEmployeeByFirstAndLastName(ename);

			if (e != null) {

				// aici se afiseaza numai pentru luna curenta pentru ca din alta
				// luna e prea greu... fac maine cu iubi:X:X
				Date date = TSMUtil.truncateDateToMonthsFirst(new Date());
				MonthlyTimesheet mts = mTimesheetService.findMTSByDateAndUser(date, e);

				if (mts != null && mts.getId() != null) {
					dts = dTimesheetService.findAllDTFromMTS(mts);
					if (dts != null) {
						for (DailyTimeSheet crtDTS : dts) {
							crtActivities = activityService.findActivitiesByDTS(crtDTS);
							if (crtActivities != null && crtActivities.size() > 0) {
								allActivities.addAll(crtActivities);
							}
						}

						ArrayList<String> dateArray = new ArrayList<String>();
						ArrayList<String> durationArray = new ArrayList<String>();
						ArrayList<String> descriptionArray = new ArrayList<String>();
						ArrayList<String> projectArray = new ArrayList<String>();

						JSONArray array_du, array_de, array_p, array_da;

						for (Activity a : allActivities) {
							dateArray.add(TSMUtil.formatDate(a.getTimesheet().getDate()));
							durationArray.add(a.getDuration().toString());
							descriptionArray.add(a.getDescription());
							projectArray.add(a.getProject().getName());
						}

						array_da = new JSONArray(dateArray);
						array_du = new JSONArray(durationArray);
						array_de = new JSONArray(descriptionArray);
						array_p = new JSONArray(projectArray);

						try {
							responseMessage.put("date", array_da);
							responseMessage.put("description", array_de);
							responseMessage.put("duration", array_du);
							responseMessage.put("project", array_p);
							responseMessage.put("size", allActivities.size());
							responseMessage.put("ok", true);
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		} else {
			try {
				responseMessage.put("ok", false);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		try {

			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void processLoadManagersEmployees(Employee loggedInUser, JSONObject responseMessage, HttpServletResponse response) {
		// TODO Auto-generated method stub
		Department d = departmentService.findDepartmentByManager(loggedInUser);
		List<Employee> employees = null;
		ArrayList<String> employeesFLN = null;
		if (d != null) {
			employees = employeeService.findAllEmployeesByDepartment(d);
			employeesFLN = new ArrayList<String>();
			for (Employee e : employees) {
				employeesFLN.add(e.getFirstName() + " " + e.getLastName());
			}
		}
		try {
			if (employeesFLN != null) {
				responseMessage.put("ok", true);
				JSONArray emps = new JSONArray(employeesFLN);
				// System.out.println(emps.toString());
				responseMessage.put("elements", emps);
				responseMessage.put("size", employeesFLN.size());
			} else {
				responseMessage.put("ok", false);
			}
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
