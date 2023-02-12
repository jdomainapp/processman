# Software: ProcessMan
***Version 1.0***: Original code (refactored to use JDA)

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

# Implementation
