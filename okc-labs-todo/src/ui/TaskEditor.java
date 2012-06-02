package ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import ui.TaskList.TaskTableModel;

import com.michaelbaranov.microba.calendar.DatePicker;

import controller.TaskController;

import data.Task.Priority;

public class TaskEditor extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2750831328109855934L;
	
	private TaskController controller;
	private TaskTableModel model;
	
	protected String taskName;
	
	protected JTextField name;
	protected JTextArea description;
	protected JComboBox priority;
	protected DatePicker start;
	protected DatePicker due;
	protected JSpinner percentComplete;
	protected JCheckBox isDone;
	
	protected JButton save;
	
	public TaskEditor(TaskController controller, String taskName, TaskTableModel model) {
		this.controller = controller;
		this.taskName = taskName;
		this.model = model;

		createComponents();
		layoutComponents();
		setupEvents();
		
		setTitle("Edit " + taskName);
		setLocationRelativeTo(null);
	}
	
	private void createComponents()
	{
		name = new JTextField(20);
		name.setText(taskName);
		description = new JTextArea(4, 20);
		description.setText(controller.getDescription(taskName));
		priority = new JComboBox(Priority.values());
		priority.setSelectedItem(controller.getPriority(taskName));
		start = new DatePicker(null);
		try {
			if (controller.getStart(taskName) != null)
				start.setDate(controller.getStart(taskName).getTime());
		} catch (PropertyVetoException e1) {
			e1.printStackTrace();
		}
		due = new DatePicker(null);
		try {
			if (controller.getDue(taskName) != null)
				due.setDate(controller.getDue(taskName).getTime());
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		percentComplete = new JSpinner(new SpinnerNumberModel(controller.getPercentComplete(taskName), 0, 100, 5));
		isDone = new JCheckBox();
		isDone.setSelected(controller.isDone(taskName));
		
		save = new JButton("Save");
	}
	
	private void layoutComponents()
	{
		getRootPane().setBorder(BorderFactory.createTitledBorder("Edit task"));
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
		add(new JLabel("Percent complete: "), constraints);
		constraints.gridx = 1;
		add(percentComplete, constraints);
		constraints.gridy++;
		
		constraints.gridx = 0;
		add(new JLabel("Done? "), constraints);
		constraints.gridx = 1;
		add(isDone, constraints);
		constraints.gridy++;

		constraints.gridx = 0;
		constraints.gridwidth = 2;
		add(Box.createVerticalStrut(5), constraints);
		constraints.gridy++;

		constraints.anchor = constraints.CENTER;
		add(save, constraints);
		pack();
	}
	
	private void setupEvents()
	{
		save.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == save) {
			if (name.getText().equals(taskName)) { // save changes
				controller.updateTask(name.getText(), description.getText(), (Priority)priority.getSelectedItem(), start.getDate(), due.getDate(), (Integer)percentComplete.getValue(), isDone.isSelected());
			} else { // replace old task
				controller.addTask(name.getText(), description.getText(), (Priority)priority.getSelectedItem(), start.getDate(), due.getDate(), (Integer)percentComplete.getValue(), isDone.isSelected());
				controller.deleteTask(taskName);
				model.updateTaskName(taskName, name.getText());
			}
			dispose();
		}
	}
	
	protected String getTaskName() {
		return name.getText();
	}
}
