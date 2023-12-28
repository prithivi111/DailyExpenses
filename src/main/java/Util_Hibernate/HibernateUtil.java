package Util_Hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import ExpensesModel.Expenses;


public class HibernateUtil {
	public static SessionFactory initializeSession() {
		try {
		Configuration con = new Configuration().configure().addAnnotatedClass(Expenses.class);
		SessionFactory sf = con.buildSessionFactory();
		return sf;
		} catch (Exception e) {
			throw new ExceptionInInitializerError();
		}
	}


}
