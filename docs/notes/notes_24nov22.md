## SEM lab 24 Nov 2022 notes

Note taker: Raul

- agree on a goal in terms of grades (be direct and sincere)
- organize at least one internal meeting per week
- we are graded based on notes taken during the meetings
- Mattermost will be the main comms channel with the TA
- communicate problems as early as possible
- make agendas for future TA meetings (upload it by tuesday evening)
- labs have mandatory attendance until week 5, 6 is optional
	- 2 times late (>1-2 min) results in penalty
- the TA is NOT here for code review and debugging
- keep the Git repo clean (```dev -> main```, ```main``` must always work, meaningful commit messages)
	- merge weekly changes into the ```main``` branch by tuesday evening
	- put requirements in GitLab and reference them in commits
	- no strict need for milestones or weights
	- keep the CI/CD pipeline in check on ```main```
	- we need to set up a pipeline and checkstyle ourselves and run it locally before commiting
	- try to avoid very small/large commits (e.g: 300 lines are a bit much)
	- make proper use of merge requests
- everyone needs to work on everything (assignments and code)
- assignment deadlines:
	- week 3: requirements (by tuesday)(we'll get feedback)
	- week 3: architecture draft (asg 1)
	- week 5: final deadline for assignment 1
	- week 6: coding deadline (we need unit and integration test, 70-80% coverage, postman)
	- asg 2 and 3 after Christmas, not much coding
- tip: do the security and microservices setup first, then continue with the rest of the project
- tip: use H2 as the database, or set up some web hosting for some other db
