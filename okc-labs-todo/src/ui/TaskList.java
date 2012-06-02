package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import controller.TaskController;
import data.Task.Priority;

import com.michaelbaranov.microba.calendar.DatePicker;

public class TaskList extends JPanel implements ActionListener, MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3983364809253396861L;
	
	private static final int WIDTH = 300;
	private static final int HEIGHT = 600;
	
	private JFrame frame;
	private TaskController controller;
	
	private JButton addTask;
	private JButton deleteTask;
	private JButton clear;
	
	private TaskTableModel taskTableModel;
	private JTable taskTable;
	private JScrollPane taskPane;
	//private ArrayList<TaskPanel> tasks;
	
	public TaskList(JFrame frame, TaskController controller) {
		this.frame = frame;
		this.controller = controller;

		setLayout(new BorderLayout());
		
		createComponents();
		layoutComponents();
		setupEvents();
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
	}
	
	private void createComponents() {
		addTask = new JButton("+");
		deleteTask = new JButton("-");
		clear = new JButton("Clean up");
		
		taskTableModel = new TaskTableModel(controller.getTaskNames());
		taskTable = new JTable(taskTableModel);
		taskTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		taskTable.setAutoCreateRowSorter(true);
		taskPane = new JScrollPane(taskTable);
		taskPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		taskTable.setFillsViewportHeight(true);
		customizeTable();
	}
	
	private void customizeTable() {
		List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		sortKeys.add(new RowSorter.SortKey(TaskTableModel.PRIORITY, SortOrder.DESCENDING));
		//sortKeys.add(new RowSorter.SortKey(TaskTableModel.DUE, SortOrder.ASCENDING));
		taskTable.getRowSorter().setSortKeys(sortKeys);
		TableColumn isDoneCol = taskTable.getColumnModel().getColumn(TaskTableModel.IS_DONE);
		isDoneCol.setPreferredWidth(20);
		TableColumn priorityCol = taskTable.getColumnModel().getColumn(TaskTableModel.PRIORITY);
		priorityCol.setPreferredWidth(15);
		taskTable.setDefaultRenderer(Object.class, new TaskTableCellRenderer());
		taskTable.setDefaultRenderer(Date.class, new TaskTableCellRenderer());
	}
	
	private void layoutComponents() {
		setLayout(new BorderLayout());
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		JPanel taskPanel = new JPanel();
		taskPanel.add(addTask);
		taskPanel.add(deleteTask);
		JPanel clearPanel = new JPanel();
		clearPanel.add(clear);
		buttonPanel.add(taskPanel, BorderLayout.WEST);
		buttonPanel.add(clearPanel, BorderLayout.EAST);
		add(buttonPanel, BorderLayout.NORTH);
		add(taskPane, BorderLayout.CENTER);
	}
	
	private void setupEvents() {
		addTask.addActionListener(this);
		deleteTask.addActionListener(this);
		clear.addActionListener(this);
		taskTable.addMouseListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addTask) {
			NewTaskDialog dialog = new NewTaskDialog();
			dialog.setVisible(true);
		} else if (e.getSource() == deleteTask) {
			int row = taskTable.getSelectedRow();
			if (row >= 0) {
				String name = (String)taskTable.getValueAt(row, TaskTableModel.NAME);
				boolean success = controller.deleteTask(name);
				if (success) {
					taskTableModel.deleteTaskName(name);
					taskTableModel.fireTableDataChanged();
				}
			}
		} else if (e.getSource() == clear) {
			ArrayList<String> cleared = controller.clearCompleted();
			for (String name : cleared) {
				taskTableModel.deleteTaskName(name);
			}
			taskTableModel.fireTableDataChanged();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == taskTable && e.getClickCount() == 2) {
			int row = taskTable.getSelectedRow();
			String currentName = (String)taskTable.getValueAt(row, TaskTableModel.NAME);
			TaskEditor editor = new TaskEditor(controller, currentName, taskTableModel);
			editor.setVisible(true);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	protected class TaskTableModel extends AbstractTableModel {
		public static final int IS_DONE = 0;
		public static final int NAME = 1;
		public static final int PRIORITY = 2;
		public static final int DUE = 3;
		public static final int NUM_COLS = 4;
		
		ArrayList<String> taskNames;
		
		public TaskTableModel(Set<String> taskNames) {
			this.taskNames = new ArrayList<String>();
			Iterator<String> it = taskNames.iterator();
			while (it.hasNext()) {
				this.taskNames.add((String)it.next());
			}
		}
		
		public void addTaskName(String name) {
			taskNames.add(name);
		}
		
		public void deleteTaskName(String name) {
			taskNames.remove(name);
		}
		
		public void updateTaskName(String oldName, String newName) {
			taskNames.remove(oldName);
			taskNames.add(newName);
		}
	
		@Override
		public int getColumnCount() {
			return NUM_COLS;
		}
	
		@Override
		public int getRowCount() {
			return taskNames.size();
		}
	
		@Override
		public Object getValueAt(int row, int col) {
			if (col == PRIORITY) {
				return controller.getPriority(taskNames.get(row));
			} else if (col == IS_DONE) {
				return controller.isDone(taskNames.get(row));
			} else if (col == NAME) {
				return taskNames.get(row);
			} else if (col == DUE) {
				Calendar c = controller.getDue(taskNames.get(row));
				if (c == null)
					return null;
				else 
					return c.getTime();
			}
			return null;
		}
		
		public String getColumnName(int col)
		{
			if (col == PRIORITY) {
				return "Priority";
			} else if (col == IS_DONE) {
				return "Done?";
			} else if (col == NAME) {
				return "Task";
			} else if (col == DUE) {
				return "Due";
			}
			return null;
		}
		
		public Class<?> getColumnClass(int col)
		{
			if (col == PRIORITY) {
				return Priority.class;
			} else if (col == IS_DONE) {
				return Boolean.class;
			} else if (col == NAME) {
				return String.class;
			} else if (col == DUE) {
				return Date.class;
			}
			return null;
		}
		
		public boolean isCellEditable(int row, int col)
		{
			if (col == IS_DONE)
				return true;
			return false;
		}
	
		public void setValueAt(Object value, int row, int col)
		{
			if (col == IS_DONE) {
				controller.setDone(taskNames.get(row), (Boolean)value);
			}
		}
	}

	protected class NewTaskDialog extends JDialog implements ActionListener {
		JTextField name;
		JTextArea description;
		JComboBox priority;
		DatePicker start;
		DatePicker due;
		
		JButton createTask;
		
		public NewTaskDialog() 
		{
			super(TaskList.this.frame, "New Task");
			createComponents();
			layoutComponents();
			setupEvents();
			pack();
			setLocationRelativeTo(null);
		}
		
		private void createComponents()
		{
			name = new JTextField(20);
			description = new JTextArea(4, 20);
			priority = new JComboBox(Priority.values());
			start = new DatePicker(null);
			due = new DatePicker(null);
			createTask = new JButton("Create task");
		}
		
		private void layoutComponents()
		{
			getRootPane().setBorder(BorderFactory.createTitledBorder("New task"));
			GridBagLayout layout = new GridBagLayout();
			setLayout(layout);
			GridBagConstraints constraints = layout.getConstraints(this);		
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.anchor = GridBagConstraints.NORTHWEST;
			
			add(new JLabel("Name: "), constraints);
			constraints.gridx = 1;
			add(name, constraints);
			constraints.gridy++;
			
			constraints.gridx = 0;
			add(new JLabel("Description: "), constraints);
			constraints.gridx = 1;
			add(description, constraints);
			constraints.gridy++;
			
			constraints.gridx = 0;
			add(new JLabel("Priority: "), constraints);
			constraints.gridx = 1;
			add(priority, constraints);
			constraints.gridy++;
			
			constraints.gridx = 0;
			add(new JLabel("Start date: "), constraints);
			constraints.gridx = 1;
			add(start, constraints);
			constraints.gridy++;
			
			constraints.gridx = 0;
			add(new JLabel("Due date: "), constraints);
			constraints.gridx = 1;
			add(due, constraints);
			constraints.gridy++;
	
			constraints.gridx = 0;
			constraints.gridwidth = 2;
			add(Box.createVerticalStrut(5), constraints);
			constraints.gridy++;
	
			constraints.anchor = constraints.CENTER;
			add(createTask, constraints);
		}
		
		private void setupEvents()
		{
			createTask.addActionListener(this);
		}
	
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == createTask) {
				boolean success = controller.addTask(name.getText(), description.getText(), (Priority)priority.getSelectedItem(), start.getDate(), due.getDate());
				if (success) {
					taskTableModel.addTaskName(name.getText());
					taskTableModel.fireTableDataChanged();
					dispose();
				} else {
					JOptionPane.showMessageDialog(NewTaskDialog.this, "A task of that name already exists.  Please choose another name or edit the existing task.", "Task already exists", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	
	protected static class TaskTableCellRenderer extends DefaultTableCellRenderer {
		private static DateFormat formatter;
		
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			
			if (formatter == null) {
				formatter = DateFormat.getDateInstance();
			}
			if (value instanceof Date) {
				setText((value == null) ? "" : formatter.format(value));
			}
			Font font = c.getFont();
			Priority p = (Priority)table.getValueAt(row, TaskTableModel.PRIORITY);
			if (p.equals(Priority.HIGH)) {
				c.setFont(font.deriveFont(font.getSize2D()*1.25f));
			//} else if (p.equals(Priority.MEDIUM)) {
				// Do not change size for medium priority
			} else if (p.equals(Priority.LOW)) {
				c.setFont(font.deriveFont(font.getSize2D()*0.8f));
			}
			
			boolean isDone = (Boolean)table.getValueAt(row, TaskTableModel.IS_DONE);
			if (isDone) {
				c.setFont(font.deriveFont(Font.ITALIC));
				c.setForeground(Color.gray.darker());
			}
			
			Date due = (Date)table.getValueAt(row, TaskTableModel.DUE);
			if (!isDone && due != null) {
				Date now = Calendar.getInstance().getTime();
				Calendar soonCal = Calendar.getInstance();
				soonCal.add(Calendar.DATE, 7);
				Date soon = soonCal.getTime();
				if (due.before(now)) {
					c.setForeground(Color.red.darker());
				} else if (due.before(soon)) {
					c.setForeground(Color.orange.darker());
				}
			} else if (!isDone && due == null) {
				c.setForeground(Color.black);
			}
			
			return c;
		}
	}
}
