package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;

public class ProjectsApp {
	
	// setup for adding a project
	// @formatter:off
	private List<String> operations = List.of("1) Add a project", 
									"2) List projects", 
									"3) Select a project", 
									"4) Update project details",
									"5) Delete a project");
	// @formatter:on
	private Scanner scanner = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();
	Project curProject;
			
  public static void main(String[] args) {
    // connect to projects schema
    new ProjectsApp().processUserSelections();
  }

  	// allows the user to select a project transaction
	private void processUserSelections() {
		boolean done = false;
		while (!done) {
			try {
				int selection = getUserSelection();
				
				switch(selection) {
					case -1:
						done = exitMenu();
						break;
					case 1:
						createProject();
						break;
					case 2:
						listProjects();
						break;
					case 3:
						selectProject();
						break;
					case 4:
						updateProjectDetails();
						break;
					case 5:
						deleteProject();
						break;
					default:
						System.out.println("\n" + selection + " is not a valid selection. Try again.");
						break;
				}
			}
			catch(Exception e) {
				System.out.println("\nError: " + e + "Try Again.");
				e.printStackTrace();
			}
		}
	}
	
	// removes a project from the db
	private void deleteProject() {
		listProjects();
		Integer projectId = getIntInput("\nEnter the ID of the project to delete");
		projectService.deleteProject(projectId);
		System.out.println("\nProject " + projectId +  " was deleted.");
		if (Objects.nonNull(curProject) && curProject.getProjectId().equals(projectId)) {
			curProject = null;
		}
	}

	// updates a project's details
	private void updateProjectDetails() {
		if (Objects.isNull(curProject)) {
			System.out.println("\nPlease select a project");
			return;
		}
		
		// prompt user for updated details
		String projectName = getStringInput("Enter the project name [ " + curProject.getProjectName() + " ]");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours [ " + curProject.getEstimatedHours() + " ]");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours [ " + curProject.getActualHours() + " ]");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5) [ " + curProject.getDifficulty() + " ]");
		String notes = getStringInput("Enter the project notes [ " + curProject.getNotes() + " ]");
		
		Project project = new Project();
		
		project.setProjectId(curProject.getProjectId());
		project.setProjectName(Objects.isNull(projectName) ? curProject.getProjectName() : projectName);
		project.setEstimatedHours(Objects.isNull(estimatedHours) ? curProject.getEstimatedHours() : estimatedHours);
		project.setActualHours(Objects.isNull(actualHours) ? curProject.getActualHours() : actualHours);
		project.setDifficulty(Objects.isNull(difficulty) ? curProject.getDifficulty() : difficulty);
		project.setNotes(Objects.isNull(notes) ? curProject.getNotes() : notes);
		
		projectService.modifyProjectDetails(project);
		curProject = projectService.fetchProjectById(curProject.getProjectId());
	}

	// selects a given project from the db
	private void selectProject() {
		listProjects();
		Integer projectId = getIntInput("Enter a project ID to select a project");
		
		// remove selected project in case of thrown exception
		curProject = null;
		
		curProject = projectService.fetchProjectById(projectId);
		
		if (curProject == null) {
			System.out.println("Invalid project ID selected.");
		}
	}

	// lists all projects in the db
	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();
		System.out.println("\nProjects:");
		
		// prints out each project
		projects.forEach(project -> System.out.println(" " + project.getProjectId() + ": " + project.getProjectName()));
		
	}

	// collects the data for the new project
	private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		String notes = getStringInput("Enter the project notes");
		Project project = new Project();
		
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created project: " + dbProject);
	}

	// exits the menu
	private boolean exitMenu() {
		System.out.println("Exiting the menu.");
		return true;
	}

	// gets the user selection from the console
	private int getUserSelection() {
		printOperations();
		Integer input = getIntInput("Enter a menu selection");
		
		// ensure the given object is not null
		return Objects.isNull(input) ? -1 : input;
	}

	// tells the user what transactions are available. allows quit.
	private void printOperations() {
		System.out.println("\nThese are the available selections. Press the Enter key to quit:");
		operations.forEach(line -> System.out.println(" " + line));
		
		if (curProject == null) {
			System.out.println("\nYou are not working with a project.");
		}
		else {
			System.out.println("\nYou are working with the project: " + curProject);
		}
	}

	// gets an integer inputed by the user
	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);
		
		if (Objects.isNull(input)) {
			return null;
		}
		
		try {
			return Integer.valueOf(input);
		}
		catch(NumberFormatException e) {
			throw new DbException(input + "is not a valid number");
		}
	}
	
	// gets a big decimal inputed by the user
	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);
		
		if (Objects.isNull(input)) {
			return null;
		}
		
		try {
			return new BigDecimal(input).setScale(2);
		}
		catch(NumberFormatException e) {
			throw new DbException(input + "is not a valid decimal number");
		}
	}

	// get a string inputed by the user
	private String getStringInput(String prompt) {
		System.out.println(prompt + ": ");
		String input = scanner.nextLine();
		
		return input.isBlank() ? null : input.trim();
	}

}
