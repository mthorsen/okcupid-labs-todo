import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import controller.TaskController;

import ui.TaskList;


public class TodoApp extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2863158426006383835L;
	
	private TaskController controller;
	private TaskList listGUI;
	
	private static final String FILE_NAME = "tasks.todo";
	
	public TodoApp() {
		super("Todo List");
		controller = new TaskController();
		readTasks();

		createComponents();
		layoutComponents();
		setupEvents();
		
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
	
	private void createComponents()
	{
		listGUI = new TaskList(this, controller);
	}
	
	private void layoutComponents()
	{
		setLayout(new BorderLayout());
		setTitle("Todo List");
		add(listGUI);
		pack();
	}
	
	private void setupEvents()
	{
		addWindowListener(new WindowAdapter()
		{
			protected void processWindowEvent(WindowEvent e)
			{
				if (e.getNewState() == WindowEvent.WINDOW_CLOSING ||
						e.getNewState() == WindowEvent.WINDOW_CLOSED)
					dispose();
			}
		});
	}
	
	/**
	 * The main function merely creates the Application.
	 * @param args	any arguments to parse; none currently supported
	 */
	public static void main(String[] args)
	{
		TodoApp todo = new TodoApp();
		todo.setVisible(true);
	}
	
	public void readTasks() {
		File file = new File(FILE_NAME);
		if (file.exists()) {
			FileInputStream fin;
			try {
				fin = new FileInputStream(FILE_NAME);
				ObjectInputStream in = new ObjectInputStream(fin);
				controller.readTasks(in);
				in.close();
				fin.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Save all Entity information to a file.
	 */
	public void saveData()
	{	
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(FILE_NAME);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			controller.writeTasks(out);
			out.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Save the Entity data when disposing of this Application.
	 */
	public void dispose()
	{
		saveData();
		super.dispose();
	}
}
