package ExpensesDAO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import ExpensesModel.Expenses;
import Util_Hibernate.HibernateUtil;

public class ExpensesDAO {
	public static SessionFactory sf = HibernateUtil.initializeSession();
	
	public static void addExpenses() {
		Session session1 = null;
		Transaction tx1 = null;
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the no. of expenses you want to add..");
		int noOfInput = sc.nextInt();
		int count = 1;
		List<Expenses> expenses = new ArrayList<Expenses>();
			for(int i=0; i<noOfInput; i++) {
				
				Expenses expense = new Expenses(); 
				
				System.out.print("Enter " + count++ + " Expense Name: ");
		        String expenseName = sc.next();

				Date dateNTime = null;
				do {
					System.out.println("Enter date and time of expense (yyyy/MM/dd HH:mm:ss): For eg: 1988-06-29 14:30:30");			
					String inputDateAndTime = sc.nextLine();	
					dateNTime = parseDateAndTimeMethod(inputDateAndTime);   //this will call parseDateAndTimeMethod below
				} while (dateNTime == null);	

		        System.out.print("Enter Purpose: ");
		        String purpose = sc.nextLine();

		        System.out.print("Enter Amount: ");
		        double amount = sc.nextDouble();
		        
		        expense.setExpenseName(expenseName);
				expense.setDateTime(dateNTime);
				expense.setPurpose(purpose);
				expense.setAmount(amount);
				
				//adding in the list
		        expenses.add(expense);
		        System.out.println("**************");
			}
	
			try {
				session1 = sf.openSession();
				tx1 = session1.beginTransaction();
					for(Expenses ee : expenses) {
						session1.save(ee);
					}	
				System.out.println(noOfInput + " record(s) added into Database successfully!!");
				tx1.commit();
				} catch(Exception e) {
							if(tx1!= null) {
							tx1.rollback();
							}
					e.printStackTrace();
				}finally {
					if (session1 != null) {
					session1.close();
					}
				}
		sc.close();

	}
	
	
	private static Date parseDateAndTimeMethod(String inputDateAndTime) {
		try {
			SimpleDateFormat dd = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			return dd.parse(inputDateAndTime);
	
		}catch(ParseException e) {
			System.err.println("Invalid date-time Format. Please use yyyy/MM/dd HH:mm:ss");
			return null;
		}	
	}


	public static List<Expenses> viewExpenses() {
		Session session2 = null;
		Transaction tx2 = null;
		List<Expenses> expenses = null;
		try {
			session2 = sf.openSession();
			tx2 = session2.beginTransaction();
			
			Query query = session2.createQuery("from Expenses");
			expenses = query.list();
			
			System.out.println("The details of all the expenses record(s) is/are mentioned below: ");
			for(Expenses exp : expenses) {
				System.out.print(exp.getExpenseName() + " | " +exp.getId() + " | " + exp.getDateTime() + " | " + 
								 exp.getPurpose() + " | " + exp.getAmount());
				System.out.println();
			}
			
			tx2.commit();
			} catch(Exception e) {
						if(tx2!= null) {
						tx2.rollback();
						}
				e.printStackTrace();
			}finally {
				if (session2 != null) {
				session2.close();
				}
			}
		return expenses;
	}
	
	public static void pdfGenerateOfExpense(List<Expenses> expenses) throws FileNotFoundException {
		File file = new File("C:/Users/s011271sur/eclipse-workspace/DailyExpenses/src/main/resources/PDFs/expense.pdf");
		try (PDDocument pdDocument = new PDDocument()){
			PDPage pdPage = new PDPage();
			pdDocument.addPage(pdPage);
			try(PDPageContentStream pdPageContentStream = new PDPageContentStream(pdDocument, pdPage)){
		
				 pdPageContentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
				 pdPageContentStream.beginText();
				 pdPageContentStream.newLineAtOffset(20, pdPage.getTrimBox().getHeight() - 50);
				 pdPageContentStream.showText("Expense Report");
				 pdPageContentStream.endText();	
				
				DateFormat d_format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
				String formattedDate = d_format.format(date);
				
				pdPageContentStream.beginText();
				 pdPageContentStream.newLineAtOffset(20, pdPage.getTrimBox().getHeight() - 70);
				pdPageContentStream.showText("Generated on: "+ formattedDate);
				pdPageContentStream.endText();	
				
				int yPosition = 700;
				
					for(Expenses exp: expenses) {
					yPosition -=20;
					pdPageContentStream.beginText();
					pdPageContentStream.newLineAtOffset(20, yPosition);
					pdPageContentStream.showText("Expense_Id: " + exp.getId() + ", Expense Name: " + exp.getExpenseName() + ", Purpose: " +
					                             exp.getPurpose() + ", Amount: " + exp.getAmount() + ", Date&Time: " + exp.getDateTime());
					pdPageContentStream.endText();	
					}  
					
					pdPageContentStream.close();	
				}
				
				pdDocument.save(file);
				pdDocument.close();
				System.out.println("PDG generated successfully! @ " + file);
			}catch(IOException e) {
					e.printStackTrace();
			}
	}
}
