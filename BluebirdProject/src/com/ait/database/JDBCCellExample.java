package com.ait.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Calendar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.PrintWriter;

//added public /// extends JFrame

public class JDBCCellExample extends JFrame implements ActionListener {
	

	// Create some Buttons
	private JButton exportButton = new JButton("Export All Data");
	private JButton exportCusOrdButton = new JButton("Export Customer Order with Shipper");
	private JButton productsStockButton = new JButton ("Export Products Inventory");
	private JButton viewAllCustomerButton = new JButton ("View all Customers");
	private JButton searchForOrdButton = new JButton("Search for Order with Order No: ");
	private JButton totalSalesYearButton = new JButton("List Year Sales: ");
	
	
	
	

	// Create some Text Fields
	private JTextField cellIDTF = new JTextField(12);
	private JTextField timeTF = new JTextField(12);

	// Set up the JDBC requirements
	private Connection con = null;
	private Statement stmt = null;

	public JDBCCellExample(String str) {
		super(str);

		// Set the layout format ( 4 Rows * 2 Columns)
		getContentPane().setLayout(new GridLayout(4, 2));

		// Connect to the Database
		initDBConnection();

		// Add the GUI components
		getContentPane().add(exportButton);
		getContentPane().add(exportCusOrdButton);
		getContentPane().add(productsStockButton);
		getContentPane().add(viewAllCustomerButton);
		getContentPane().add(searchForOrdButton);
		getContentPane().add(cellIDTF);
		getContentPane().add(totalSalesYearButton);
		getContentPane().add(timeTF);
		
		

		// Add some listeners to monitor for actions (i.e. button presses)
		exportButton.addActionListener(this);
		exportCusOrdButton.addActionListener(this);
		productsStockButton.addActionListener(this);
		viewAllCustomerButton.addActionListener(this);
		searchForOrdButton.addActionListener(this);
		totalSalesYearButton.addActionListener(this);
		
		
		

		// Set the Window Size
		 setSize(500, 500);
		 
		// Show the Window
		 setVisible(true);
	}

	private void initDBConnection() {

		// Try connect to the database
		try {

			String url = "jdbc:mysql://127.0.0.1:3306/BLUEBIRD?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
			con = DriverManager.getConnection(url, "root", "admin");
			stmt = con.createStatement();

		}
		// If an error occurs anywhere in the previous code - Report the error and stop
		// the code
		catch (Exception e) {
			// Updated error
			System.out.println("Failed to initialise DB Connection");
			System.out.println("Error: " + e);
		}
	}

	// Write information to a file
	private void writeToFile(ResultSet rs) {
		try {

			// What does this do?
			FileWriter outputFile = new FileWriter("CellOutput.csv");
			PrintWriter printWriter = new PrintWriter(outputFile);
			ResultSetMetaData rsmd = rs.getMetaData();
			int numColumns = rsmd.getColumnCount();

			// What does this do?
			for (int i = 0; i < numColumns; i++) {
				printWriter.print(rsmd.getColumnLabel(i + 1) + ",");
			}
			printWriter.print("\n");

			// What does this do?
			while (rs.next()) {
				for (int i = 0; i < numColumns; i++) {
					printWriter.print(rs.getString(i + 1) + ",");
				}

				printWriter.print("\n");
				printWriter.flush();
			}

			// What does this do?
			printWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// When an action is performed (i.e user clicks a button) - Do Something.
	public void actionPerformed(ActionEvent e) {
		Object target = e.getSource();
		ResultSet rs = null;
		String cmd = null;

		// When the Export button is clicked construct the SQL command and store it in
		// CMD
		if (target.equals(exportButton)) {

			cmd = "select * from orders";

			// Print a line for example
			System.out.println("Export Button Clicked!\n\n");
		} else if (target.equals(searchForOrdButton)) {
			String orderNo = cellIDTF.getText();
			cmd = "SELECT order_ID, orderDate, requiredDate, shippedDate FROM orders ";
			if (!orderNo.isEmpty()) {
				cmd += " WHERE order_ID=" + orderNo;
			}
			cmd += " ORDER BY order_ID;";
		} else if (target.equals(productsStockButton)) {
			cmd = "SELECT products.*, categories.categoryName FROM categories " +
					" INNER JOIN products ON categories.category_ID = products.category_ID " +
					"WHERE products.unitsInStock > 0";
		} else if (target.equals(totalSalesYearButton)) {
			int currentYear = Calendar.getInstance().get(Calendar.YEAR);
			cmd = "SELECT customers.customer_ID, customers.companyName, customers.City, customers.Country " +
					" FROM customers JOIN orders ON customers.customer_ID = orders.customer_ID " +
					" WHERE year(orders.orderDate)=" + currentYear;
		}else if (target.equals(exportCusOrdButton)) {
			cmd = "SELECT orders.order_ID, customers.contactName, shippers.companyName" +
					"FROM orders INNER JOIN customers ON orders.customer_ID = customers.customer_ID" +
					"INNER JOIN shippers ON orders.shipper_ID = shippers.shipper_ID";
		}

		// Try call this query on the SQL database
		try {
			System.out.println("the cmd: " + cmd);
			rs = stmt.executeQuery(cmd);
			writeToFile(rs);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public static void main(String args[]) {

		// Set the window title
		new JDBCCellExample("BlueBird Data Export");
	}

}