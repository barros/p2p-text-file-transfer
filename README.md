The Peer-to-Peer Text File Transfer Program
==========================================
A peer-to-peer text file transfer program which uses UDP (user datagram protocol) and FTP (file transfer protocol) to allow users to register and receive files from other contributors.<br>

#### Start up

If you are running the project in the environment in which you code can compile and run Java programs,
open the included projects in the environment and head to the environment's
Run Configurations. You will only be running the Broker.java and Peer.java files.
For the Broker, pass in your devices IP address and desired port number as
arguments. If the port number is unavailable, you will be told so. Once you begin
running the Broker class, you can now go ahead set the Peer's input argument as the same
port number the Broker is currently on.<br>

If you are running the project on the command line (Terminal in the Mac environment), navigate to the respective project's
*src* folder. Once there, enter "java *class-name* *input-arguments*". For example,
if you would like to run the Broker on port 5511, you would enter "java Broker 5511".
To run the Peer on, say your local machine, you would enter "java Peer 127.0.0.1 5511".


#### Usage
The directions for the program are very straight forward. Once you run a peer, you will
be asked to enter your name. Then you will be asked what you would like to do. the options
include 'receive', 'register', 'unregister', and 'exit'. <br>

If you want to **receive a file**, you will enter the full name of the file (including
the *.txt* extension). If a contributor is sharing the file, you will be connect to them
and will receive the requested file. This file can now be found in the peer's *src* folder (the file will be saved in the *bin* folder if you followed the directions for running the program on the command line).
Depending on if the file is what you were looking for, if it is complete, or if you
even received it in the first place, you will be asked if you would recommend this contributor.
Each contributor has a rating that helps peers choose who they would like to receive
a file from.<br>

To **register a file**, you will be asked the name of said file (again, including the *.txt*
extension). This text file must be saved in the peer's *src* folder. You cannot register
two of the same files. If a text file that you are sharing is updated, you will need to
unregister the file in the look-up database first, then re-register it.<br>

To **unregister a file**, you will be asked to simply enter the name of the file (with the *.txt*,
as you know). If you're version of the file was the last version in the database, the
name of the text will be removed from the database as a whole. If a file is deleted or moved
from your system, please unregister the file.

#### Included files
- 3 Peer Java projects (all project *src* folders will have different text files included for testing purposes)
- 1 Broker Java project

###### Extras
- When a contributor's class is terminated, their "server" goes along with them.
  Any peer trying to receive a file on the contributor's system will not be accessible
- Sleep functions were used throughout the program in order to give the peer time to
  read certain instructions or notifications. The program itself is not slow. The only time
  there is an actual delay is during a file transfer


*Created by Jeffrey Barros Peña, Boston College (2017)*
