create table Gradebook_Assignment (
	uuid_ VARCHAR(75) null,
	assignmentId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	title STRING null,
	description STRING null,
	dueDate DATE null
);

create table Gradebook_Submission (
	submissionId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	studentId LONG,
	submitDate DATE null,
	submissionText VARCHAR(75) null,
	comment_ VARCHAR(75) null,
	grade INTEGER,
	assignmentId LONG
);