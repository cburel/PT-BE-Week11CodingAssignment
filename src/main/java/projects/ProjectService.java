package projects;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import projects.entity.Project;

class ProjectService {

	private ProjectDao projectDao = new ProjectDao();
	
	// adds a new project to the DB
	public Project addProject(Project project) {
		return projectDao.insertProject(project);
	}

	// gets the list of all projects in the db
	public List<Project> fetchAllProjects() {
		return projectDao.fetchAllProjects();
	}

	// gets a single given project in the db
	public Project fetchProjectById(Integer projectId) {
		return projectDao.fetchProjectById(projectId).orElseThrow(
				() -> new NoSuchElementException(
						"Project with project ID=" + projectId + "does not exist."));
		
	}

}
