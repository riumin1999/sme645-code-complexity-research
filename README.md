# Rule-based model

## build and run the program

Grab the code into a IDE and launch it. 
Remember to specify the source code as the first parameter and the target directory as the second parameter.

You may need to run the program twice, sometimes the program generate a blank file.
I believe this is due to some file system or handler issue.

## artificial justification of the output

For rule 1, four parts on dataset6 will require artificial justification after the model refactoring.
You need to move the declaration of variables before the whole if logic.

For rule 2, the original code to auto find and replace the code has been removed.
The code cannot auto-detect whether the assignment statement is a member declaration or a statement in method. 
Simply do search and replace would reach identical effect as described in report.
The code logic could be found in the commented lines in `Model.java`.

## post-refactor verification

Follow the original empirical research and run Checker's Framework on the new, generated code.