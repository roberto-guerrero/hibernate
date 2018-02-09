package com.javacodegeeks.snippets.enterprise.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Filter;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class App {
	public static void main(String[] args) {
		Employee em1 = new Employee("Mary Smith", 25);
		Employee em2 = new Employee("John Aces", 32);
		Employee em3 = new Employee("Ian Young", 29);
		
		System.out.println(" =======CREATE =======");
		create(em1);
		create(em2);
		create(em3);
		
		System.out.println(" =======READ ALL =======");
        List<Employee> ems0 = read();
        for(Employee e: ems0) {
            System.out.println(e.toString());
        }
		
		System.out.println(" =======READ WITH PARAM FILTER =======");
        List<Employee> ems1 = readFilter();
        for(Employee e: ems1) {
            System.out.println(e.toString());
        }
        
        System.out.println(" =======UPDATE =======");
        em1.setAge(44);
        em1.setName("Mary Rose");
        update(em1);
        
        System.out.println(" =======READ =======");
        List<Employee> ems2 = read();
        for(Employee e: ems2) {
            System.out.println(e.toString());
        }
        
        System.out.println(" =======DELETE ======= ");
        delete(em2.getId());

        System.out.println(" =======READ =======");
        List<Employee> ems3 = read();
        for(Employee e: ems3) {
            System.out.println(e.toString());
        }
        
        System.out.println(" =======READ UNIQUE USER =======");
        Employee ems4 = findUser("Mary Rose");
        System.out.println(ems4.toString());
        
        System.out.println(" =======READ FROM CACHE =======");
        Employee ems5 = findUser("Mary Rose");
        System.out.println(ems5.toString());
        
        System.out.println(" =======DELETE ALL ======= ");
        deleteAll();
        
        System.out.println(" =======READ =======");
        List<Employee> ems6 = read();
        for(Employee e: ems6) {
            System.out.println(e.toString());
        }
        
        System.exit(0);
	}
	
	public static SessionFactory getSessionFactory() {
		Configuration configuration = new Configuration().configure();
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
		SessionFactory sessionFactory = configuration.buildSessionFactory(builder.build());
		return sessionFactory;
	}

	public static Integer create(Employee e) {
		Session session = getSessionFactory().openSession();
		session.beginTransaction();
		session.save(e);
		session.getTransaction().commit();
		session.close();
		System.out.println("Successfully created " + e.toString());
		return e.getId();

	}

	public static List<Employee> read() {
		Session session = getSessionFactory().openSession();
		@SuppressWarnings("unchecked")
		List<Employee> employees = session.createQuery("FROM Employee").list();
		session.close();
		System.out.println("Found " + employees.size() + " Employees");
		return employees;

	}
	
	public static List<Employee> readFilter() {
		Session session = getSessionFactory().openSession();
		Criteria c = session.createCriteria(Employee.class);
		Filter ageFilter = session.enableFilter("onlyOlderThan");
		ageFilter.setParameter("olderThan", new Integer(30));
		List<Employee> employees = (List<Employee>)c.list();
		return employees;
	}

	public static void update(Employee e) {
		Session session = getSessionFactory().openSession();
		session.beginTransaction();
		Employee em = (Employee) session.load(Employee.class, e.getId());
		em.setName(e.getName());
		em.setAge(e.getAge());
		session.getTransaction().commit();
		session.close();
		System.out.println("Successfully updated " + e.toString());

	}

	public static void delete(Integer id) {
		Session session = getSessionFactory().openSession();
		session.beginTransaction();
		Employee e = findByID(id);
		session.delete(e);
		session.getTransaction().commit();
		session.close();
		System.out.println("Successfully deleted " + e.toString());

	}

	public static Employee findByID(Integer id) {
		Session session = getSessionFactory().openSession();
		Employee e = (Employee) session.load(Employee.class, id);
		session.close();
		return e;
	}
	
	public static void deleteAll() {
		Session session = getSessionFactory().openSession();
		session.beginTransaction();
		Query query = session.createQuery("DELETE FROM Employee ");
		query.executeUpdate();
		session.getTransaction().commit();
		session.close();
		System.out.println("Successfully deleted all employees.");

	}
	
	public static Employee findUser(String name) {
		Session session = getSessionFactory().openSession();
		Query q = session.createQuery("FROM Employee where name = :name");
		q.setString("name", name);
		Employee employee = (Employee) q.uniqueResult();
		session.close();
		return employee;

	}
	
}
