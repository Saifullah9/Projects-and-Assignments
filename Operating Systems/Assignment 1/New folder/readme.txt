1- tiny_shell.c:
Contains the source code for implementing system commands with fork. This includes
chdir,history,limit,signal handling parts of the assignment.

2- tiny_shell.h:
Contains helper methods which are used in tiny_shell.c file such as get_a_line() function command
and tokenize() function.

3-my_system_write.c:
This is the first part of the fifo requirement of the assignment where after the user creates a 
fifo using the mkfifo command the execution command is as follows :
$gcc my_system_write -0 write
$./write user_fifo

4- my_system_read.c:
This is the second part of the fifo requirement of the assignment where after the user creates a 
fifo using the mkfifo command the execution command is as follows :
$gcc my_system_read -0 read
$./read user_fifo

5- trace.txt :
its the document that show the output of strace from the executable of tiny_shell.c.