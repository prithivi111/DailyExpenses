package Service;

import java.io.FileNotFoundException;
import java.util.List;

import ExpensesDAO.ExpensesDAO;
import ExpensesModel.Expenses;

public class Main {
	public static void main(String[] args) {
		
		//Insert expenses into the DB
		ExpensesDAO.addExpenses();	
		
		//View all expenses from the database
		List<Expenses> expenses = ExpensesDAO.viewExpenses();
		
		//PDF generation for each expense record
		try {
		ExpensesDAO.pdfGenerateOfExpense(expenses);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
