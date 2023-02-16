# Software: ProcessMan

This software implements a **Process Manager**.

We chose the Faculty of IT (FIT) at Hanoi University as the case
for investigation. FIT is a relatively young faculty that has been undergoing significant organisational changes. One of these changes involves standardising the organisational processes in accordance with the ISO-9001:2008’s quality management standard7. We thus chose organisational process management as the particular subject of our investigation.

As an educational institution, the core processes that FIT performs
are teaching **subjects** (a.k.a course modules) to students every semester
and formally assessing the students’ performances. 
Conceptually, a **process** is a sequence of **tasks**, each of which is a sequence of **actions**. A process is created once, by an **organisation unit**, and is periodically
applied to the same unit and possibly to other organisational units that
have similar needs. For certain processes, task and action would need
to be specialised in order to specify more details. For example, in the
assessment process, a subject is created only once but is taught in the
same semester every year. This periodic delivery of a subject is called
**subject-by-semester**. The type of tasks that is applied specifically to
subject-by-semester is called **task-for-subject**. Each task-for-subject
consists of a sequence of **action-for-subject**, which is a specialisation
of action.

# Design

Use AGL to implement activity-based modules.

## None-AGL design
### Module: ProcessApplicationManager
This module aims to apply a given `Process` (e.g. Course Planning) to an `OrgUnit` (e.g. Department of IT) in a given semester of a year. The application involves the following steps:

1. [User] Selects the `Process` and `OrgUnit` and specifies the year and semester
   1. System: creates a `ProcessApplication` with the specified details
2. [System] Copies the specified Process's structure to be referenced by the new `ProcessApplication`
   - `vn.com.processman.modules.processmanager.processapplication.controller.command.CreateNewProcessApplicationCommand`

### Module: Process4SubjectApplicationManager
Works similarly to `ProcessApplicationManager` except that in **step 2**, it uses the following, more specialised, data command to copy the `Task4Subject` structure to the new `Process4SubjectApplication`:
- `vn.com.processman.modules.processmanager.processapplication.forsubject.controller.command.CreateNewProcess4SubjectCommand`

## AGL-based design

## Module: ProcessManager

**Type**: Sequential

1. Create Process
2. Create Tasks
	- allow multiple Tasks to have the same previous Task and thus they can be executed concurrently
	
3. Create Actions
	- allow multiple Actions to have the same previous Action and thus they can be executed concurrently

**Process Example: Teacher Recruitment**
1. Request for recruitment (what position, how many, description)
2. Advertise the position
3. Screen applications for the position
4. Schedule an applicant interview
5. Interview an applicant
6. Conduct demo-teaching for an applicant
7. Make hiring decision with the applicant
8. Sign contract with the qualified applicant

**Main flow: Decisional, Sequential, Merge**

```
START -> 1 -> 2 ---------------------------------------------------------------------------<M1>---> END
           -> 3 -> [found applicant?] -[N]> -> [more applicant?] -[Y]>  -> [ended?] -[Y]> (<M1>)
                                                                                    -[N]> (3)
                                                                 -[N]>  -> ([ended?])
                                      -[Y]> -> 4 -> 5 -> [interview ok?] -[Y]> 6 -> 7 -> [hired] -[Y]> 8 
                                                                                                 -[N]> (3)
                                                                         -[N]> (3)
```
**(4): Fork & Join**
```
START .-> Prepare interview -------------------------------------------------------------------------------------------|
      |                                                                                                                |-> Create interview event
      .-> Request available time slots -> (Interviewer) Review time slots -> Send available slots --|                  |
                                      |                                                            |-> Receive slots --|
                                      -> (Interviewee) Review time slots -> Send available slots --|
```
**(8): Fork & Join**
```
 START .-> Prepare contract ------------------------------------------------------------------------|
       |                                                                                            | -> Send contract -> Sign -> END
       .-> Request for supporting documents -> (Applicant) prepare documents -> Receive documents --|
```
## Module: ProcessApplicationManager
**Type**: Decisional & Sequential

1. Decisional: 
Allows user to choose whether to **apply** a normal process or a process for subject.
If user choose process-for-subject then there are 3 steps, in which the second step is to choose a subject. Otherwise, there are only 2 steps as described in the previous section.

2. Sequential: 
- The 2 or 3 steps of the main procedure are performed in sequence.

## Module: ProcessExecution
**Type**: Decisional, Sequential, Forked, Merged, Join


1. [Decisional] User choose to **execute** a normal process or process-for-subject

2. [Forked, Sequential, Join] Each task of the selected process is initiated
	- tasks and their actions are performed in sequence, as specified by precedence
	- tasks/actions that have the same previous task/action (resp.) are executed concurrently. There are two cases of the execution: merged and forked.
		- [Forked] All Tasks/Actions of a Join node must be completed (with an output) before the Join is completed
		- [Merged] Completion of Any Task/Action of a Merge node leads to completion of the Merge node

