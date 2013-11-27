var knownProjects = new Array();

function createPullRequestDom(pullRequest) {
	var nPullRequest = document.createElement("a");
	nPullRequest.className = "pr";
	nPullRequest.target = "_blank"
	nPullRequest.href = pullRequest.url
	nPullRequest.appendChild(document.createTextNode(pullRequest.number))

	return nPullRequest;
}

function projectId(project) {
	return project.githubRepo.replace("/", "_").replace(".", "-");
}

function createProjectDom(project) {

	var nProject = document.createElement("div");
	nProject.id = projectId(project);
	nProject.className = "project project-cat-" + project.category

	var nName = document.createElement("div");
	nName.className = "project-name";
	nProject.appendChild(nName);

	var nNameA = document.createElement("a");
	nNameA.href = "https://github.com/" + project.githubRepo;
	nNameA.target = "_blank";
	nNameA.appendChild(document.createTextNode(project.name));
	nName.appendChild(nNameA);

	var nPR = document.createElement("div");
	nPR.className = "project-pr";
	nProject.appendChild(nPR);

	var nPRLabel = document.createElement("span");
	nPRLabel.className = "label";
	nPRLabel.appendChild(document.createTextNode("open PRs:"))
	nPR.appendChild(nPRLabel);

	var prs = project.pullRequests;

	if (prs.length == 0) {
		nPR.appendChild(document.createTextNode(" -"));
	} else {
		for ( var i = 0; i < prs.length; i++) {
			nPR.appendChild(createPullRequestDom(prs[i]));
		}
	}

	return nProject;
}

function updateStats() {
	var nbPRs = 0;

	for ( var i = 0; i < knownProjects.length; i++) {
		nbPRs += knownProjects[i].pullRequests.length;
	}

	document.getElementById("open-pr-nb-value").innerHTML = nbPRs;
	document.getElementById("project-nb-value").innerHTML = knownProjects.length;
}

function message(msgText) {
	var msg = JSON.parse(msgText);

	var projectIndex = -1;
	var pId = projectId(msg.project)
	for ( var i = 0; i < knownProjects.length; i++) {
		if (pId == projectId(knownProjects[i])) {
			projectIndex = i;
			break;
		}
	}

	var projects = document.getElementById("projects");

	if (projectIndex == -1) {
		knownProjects[knownProjects.length] = msg.project;
		projects.appendChild(createProjectDom(msg.project));
	} else {
		var oldNode = document.getElementById(pId);
		projects.replaceChild(createProjectDom(msg.project), oldNode);
		knownProjects[projectIndex] = msg.project;
	}

	updateStats();
}

function createWebSocket() {

	var socket = new WebSocket("ws://localhost:9000/ws");

	socket.onopen = function() {
		console.log("web socket open");
		var message = {
			"action" : "go"
		};
		socket.send(JSON.stringify(message));
	}

	socket.onmessage = function(event) {
		message(event.data);
	}

	socket.onclose = function() {
		console.log("web socket closed");
		setTimeout(createWebSocket, 1000);
	}

	socket.onError = function() {
		console.log("web socket error");
		setTimeout(createWebSocket, 1000);
	}
}

createWebSocket();